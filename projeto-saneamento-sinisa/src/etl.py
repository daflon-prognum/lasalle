"""Lê as planilhas do SINISA e gera as tabelas usadas na análise."""
import argparse
import hashlib
import json
from pathlib import Path
from shutil import copyfileobj
from tempfile import TemporaryDirectory
from urllib.request import urlopen
from zipfile import ZipFile

import pandas as pd

ROOT = Path(__file__).resolve().parents[1]
PORTAL = "https://www.gov.br/cidades/pt-br/acesso-a-informacao/acoes-e-programas/saneamento/sinisa/resultados-sinisa"
REGIOES = ("Norte", "Nordeste", "Sudeste", "Sul", "Centro-Oeste")
SIGLAS_REGIOES = {"N": "Norte", "NE": "Nordeste", "SE": "Sudeste", "S": "Sul", "CO": "Centro-Oeste"}
UF_POR_REGIAO = {
    "Norte": "AC AM AP PA RO RR TO",
    "Nordeste": "AL BA CE MA PB PE PI RN SE",
    "Sudeste": "ES MG RJ SP",
    "Sul": "PR RS SC",
    "Centro-Oeste": "DF GO MS MT",
}
REGIAO_POR_UF = {uf: regiao for regiao, ufs in UF_POR_REGIAO.items() for uf in ufs.split()}

# No consolidado, SE identifica tanto Sergipe quanto a região Sudeste.
# A chave territorial sempre incorpora o nível.
FONTES = {
    "agua": {
        "zip": "SINISA_Resultados_Ref2024.zip",
        "aba": "Indicadores de Gestão",
        "municipal": (
            "SINISA_AGUA_Indicadores_Base Municipal_2024_Retificação.xlsx",
            "262f74f515b6225b7c9c122576159ab45576ae15628ae67612d7cee6c333623d",
        ),
        "uf": (
            "SINISA_AGUA_Indicadores_UF_MR_BR_2024.xlsx",
            "ceff0314989c7776191f2670b3cae3d70fbbc43fe2a174eecb4484ac3b170078",
        ),
    },
    "esgoto": {
        "zip": "SINISA_ESGOTO_Planilhas_2024.zip",
        "aba": "Indicadores - Esgoto",
        "municipal": (
            "SINISA_ESGOTO_Indicadores_Base Municipal_2024.xlsx",
            "8ff86088f3fccda8adddbf5e7df1fa5be8246dfad23f9b6fe3a52bdbd3445b7b",
        ),
        "uf": (
            "SINISA_ESGOTO_Indicadores_UF_MR_BR_2024.xlsx",
            "3a1aa48299cae4ea503c9cd1000448e41e3d042d1605bb510696d6673f9ddb3a",
        ),
    },
}
MARCADORES = (
    "Não calculado (condições não atendidas)",
    "Não calculado (divisão por zero)",
)
CODIGOS = {
    "agua": {"IAG0001": "agua_total_pct", "IAG0002": "agua_urbana_pct", "IAG0003": "agua_rural_pct"},
    "esgoto": {"IES0001": "esgoto_total_pct", "IES0002": "esgoto_urbana_pct", "IES0003": "esgoto_rural_pct"},
}


def obter_planilhas(modulo, pasta):
    fonte = FONTES[modulo]
    caminhos = {escala: pasta / f"{modulo}_{escala}.xlsx" for escala in ("municipal", "uf")}
    faltantes = [escala for escala, caminho in caminhos.items() if not caminho.exists()]
    if faltantes:
        pasta.mkdir(parents=True, exist_ok=True)
        with TemporaryDirectory(dir=pasta) as temporaria:
            arquivo_zip = Path(temporaria) / f"{modulo}.zip"
            with urlopen(f"{PORTAL}/{fonte['zip']}", timeout=90) as resposta, arquivo_zip.open("wb") as saida:
                copyfileobj(resposta, saida)
            with ZipFile(arquivo_zip) as pacote:
                for escala in faltantes:
                    nome, esperado = fonte[escala]
                    membro, = [item for item in pacote.namelist() if item.endswith("/" + nome)]
                    dados = pacote.read(membro)
                    if hashlib.sha256(dados).hexdigest() != esperado:
                        raise ValueError(f"A fonte oficial mudou. Revise a versão de {nome}.")
                    extraido = Path(temporaria) / caminhos[escala].name
                    extraido.write_bytes(dados)
                    extraido.replace(caminhos[escala])
    for escala, caminho in caminhos.items():
        if hashlib.sha256(caminho.read_bytes()).hexdigest() != fonte[escala][1]:
            raise ValueError(f"Arquivo bruto diferente da versão documentada: {caminho.name}")
    return caminhos


def ler_indicadores(caminho, modulo, escala):
    origem = pd.read_excel(
        caminho, sheet_name=FONTES[modulo]["aba"], header=None, dtype=object,
        keep_default_na=False, engine="openpyxl",
    )
    chaves = (
        {"cod_IBGE": "ibge", "Macrorregião": "regiao", "Município": "municipio", "UF": "uf"}
        if escala == "municipal"
        else {"UF/Macrorregião": "territorio", "Sigla": "sigla"}
    )
    colunas = chaves | CODIGOS[modulo]
    candidatos = origem.index[origem.apply(lambda linha: set(colunas).issubset(linha.values), axis=1)]
    if len(candidatos) != 1:
        raise ValueError(f"Cabeçalho não identificado de forma única: {caminho.name}")
    cabecalho = candidatos[0]
    posicoes = [origem.iloc[cabecalho].tolist().index(codigo) for codigo in colunas]
    tabela = origem.iloc[cabecalho + 1:, posicoes].copy()
    tabela.columns = list(colunas.values())
    tabela = tabela.loc[tabela.ne("").any(axis=1)].reset_index(drop=True)

    auditoria = {}
    for coluna in CODIGOS[modulo].values():
        original = tabela[coluna].astype("string").str.strip()
        ausente = original.isna() | original.eq("") | original.isin(MARCADORES)
        numero = pd.to_numeric(original.mask(ausente), errors="coerce")
        invalidos = (~ausente & numero.isna()) | (numero.notna() & ~numero.between(0, 100))
        if invalidos.any():
            raise ValueError(f"Valores inesperados em {caminho.name}/{coluna}: {original[invalidos].unique()}")
        tabela[coluna] = numero.astype("float64")
        auditoria[coluna] = {
            "validos": int(numero.notna().sum()),
            "zeros": int(numero.eq(0).sum()),
            "marcadores": original[ausente].fillna("").value_counts().sort_index().to_dict(),
        }

    if escala == "municipal":
        tabela["ibge"] = tabela["ibge"].astype("string").str.strip()
        if not tabela["ibge"].str.fullmatch(r"[0-9]{7}").all() or tabela["ibge"].duplicated().any():
            raise ValueError(f"Código IBGE inválido ou duplicado: {caminho.name}")
        if not tabela["uf"].map(REGIAO_POR_UF).eq(tabela["regiao"]).all():
            raise ValueError(f"UF e macrorregião incompatíveis: {caminho.name}")
    else:
        tabela["nivel"] = "UF"
        tabela.loc[tabela["territorio"].isin(REGIOES), "nivel"] = "Macrorregião"
        tabela.loc[tabela["sigla"].eq("BR"), "nivel"] = "Brasil"
        esperadas = (
            {("UF", uf) for uf in REGIAO_POR_UF}
            | {("Macrorregião", sigla) for sigla in SIGLAS_REGIOES}
            | {("Brasil", "BR")}
        )
        if tabela.duplicated(["nivel", "sigla"]).any() or set(zip(tabela["nivel"], tabela["sigla"])) != esperadas:
            raise ValueError(f"Territórios inesperados no consolidado: {caminho.name}")
    return tabela, {"linhas": len(tabela), "indicadores": auditoria}


def unir_municipios(agua, esgoto):
    chaves = ["ibge", "regiao", "municipio", "uf"]
    pares = agua.merge(esgoto, on="ibge", how="outer", suffixes=("_agua", "_esgoto"),
                       indicator="presenca", validate="one_to_one")
    for campo in chaves[1:]:
        agua_valor, esgoto_valor = pares.pop(f"{campo}_agua"), pares.pop(f"{campo}_esgoto")
        if campo != "municipio":
            divergencia = agua_valor.notna() & esgoto_valor.notna() & agua_valor.ne(esgoto_valor)
            if divergencia.any():
                raise ValueError(f"Divergência territorial após o cruzamento: {campo}")
        # O nome da planilha de água prevalece quando o mesmo código tem grafias diferentes.
        pares[campo] = agua_valor.combine_first(esgoto_valor)
    pares["presenca"] = pares["presenca"].map({
        "left_only": "Somente água", "right_only": "Somente esgoto", "both": "Ambos",
    })
    pares["diferenca_total_pp"] = pares["agua_total_pct"] - pares["esgoto_total_pct"]
    return pares[chaves + ["presenca"] + list(CODIGOS["agua"].values())
                 + list(CODIGOS["esgoto"].values()) + ["diferenca_total_pp"]]


def executar(pasta_bruta=None, pasta_saida=None):
    pasta_bruta = Path(pasta_bruta or ROOT / "data/raw")
    pasta_saida = Path(pasta_saida or ROOT / "data/processed")
    bases = {}
    auditoria = {}
    for modulo in FONTES:
        caminhos = obter_planilhas(modulo, pasta_bruta)
        for escala, caminho in caminhos.items():
            bases[modulo, escala], relatorio = ler_indicadores(caminho, modulo, escala)
            auditoria[caminho.name] = {"sha256": FONTES[modulo][escala][1], **relatorio}

    municipios = unir_municipios(bases["agua", "municipal"], bases["esgoto", "municipal"])
    consolidados = bases["agua", "uf"].merge(
        bases["esgoto", "uf"], on=["nivel", "sigla", "territorio"], validate="one_to_one",
    )
    if len(consolidados) != 33:
        raise ValueError("Territórios divergentes entre os consolidados oficiais.")
    consolidados["diferenca_total_pp"] = consolidados["agua_total_pct"] - consolidados["esgoto_total_pct"]
    for modulo in FONTES:
        consolidados[f"hiato_{modulo}_pp"] = (
            consolidados[f"{modulo}_urbana_pct"] - consolidados[f"{modulo}_rural_pct"]
        )

    pasta_saida.mkdir(parents=True, exist_ok=True)
    municipios.to_csv(pasta_saida / "municipios.csv", index=False, encoding="utf-8", float_format="%.2f")
    consolidados.to_csv(pasta_saida / "territorios.csv", index=False, encoding="utf-8", float_format="%.2f")
    auditoria["cruzamento"] = {
        "uniao": len(municipios),
        "presenca": municipios["presenca"].value_counts().to_dict(),
        "pares_totais_validos": int(municipios["diferenca_total_pp"].notna().sum()),
    }
    (pasta_saida / "auditoria.json").write_text(
        json.dumps(auditoria, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8",
    )
    return municipios, consolidados


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--brutos", type=Path, default=ROOT / "data/raw")
    parser.add_argument("--saida", type=Path, default=ROOT / "data/processed")
    args = parser.parse_args()
    municipais, territoriais = executar(args.brutos, args.saida)
    print(f"{len(municipais):,} registros municipais e {len(territoriais)} territórios processados.")
