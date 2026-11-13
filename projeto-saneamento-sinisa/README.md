# Atendimento por redes de água e esgoto no Brasil em 2024

**Autores** — Guilherme Daflon Goulart Costa e Marcella Guimaraes Rodrigues Da Silva<br>
**Instituição** — Centro Universitário La Salle do Rio de Janeiro<br>
**Curso** — Sistemas de Informação, 6º período<br>
**Disciplina** — Linguagens de Programação · 2026.2<br>
**Professor** — Alexandre Neves Louzada

[![Python](https://img.shields.io/badge/Python-3.12-306998)](requirements.txt)
[![Código MIT](https://img.shields.io/badge/Código-MIT-236856)](LICENSE)

[Artigo no GitHub Pages](https://daflon-prognum.github.io/lasalle/projeto-saneamento-sinisa/) · [Notebook](notebooks/analise_exploratoria.ipynb) · [Abrir no Colab](https://colab.research.google.com/github/daflon-prognum/lasalle/blob/main/projeto-saneamento-sinisa/notebooks/analise_exploratoria.ipynb) · [Dicionário de dados](data/data_dictionary.md)

## Resumo

Comparamos os percentuais de atendimento da população por rede de água e rede coletora de esgoto no SINISA 2025, ano de referência 2024. O recorte usa os consolidados oficiais do Brasil, das macrorregiões e das UFs e cruza as bases municipais pelo código IBGE.

No consolidado Brasil, água e esgoto registraram 84,14% e 62,30%, respectivamente. Entre as macrorregiões, o Norte registrou a maior diferença entre os dois percentuais (38,16 p.p.). No recorte rural nacional, os indicadores foram 22,88% para água e 4,75% para esgoto. Entre 2.710 municípios com ambos os indicadores totais válidos, a diferença mediana foi 10,82 p.p. e a correlação de Pearson foi 0,493. A amostra pareada tem participação regional desigual.

**Palavras-chave** — saneamento básico, abastecimento de água, esgotamento sanitário, desigualdade territorial, SINISA.

## Fonte e critérios

- [Ministério das Cidades — Resultados SINISA 2025](https://www.gov.br/cidades/pt-br/acesso-a-informacao/acoes-e-programas/saneamento/sinisa/resultados-sinisa/resultados-sinisa-2025). Módulos Abastecimento de Água e Esgotamento Sanitário, referência 2024.
- Os quatro XLSX originais estão em [`data/raw/`](data/raw/), incluindo a retificação da planilha municipal de água. O script valida a versão por SHA-256 e baixa os arquivos do portal se faltarem.
- A união preserva 5.149 códigos IBGE, dos quais 2.744 aparecem nos dois módulos. Os 2.710 pares válidos são usados na dispersão e nas estatísticas municipais. Marcadores de ausência ficam vazios no CSV, com contagens no [registro de auditoria](data/processed/auditoria.json).
- Os percentuais consolidados vêm diretamente das planilhas oficiais. Como os módulos têm universos distintos, a diferença entre os índices descreve a distância entre taxas e não mede a parcela de pessoas atendidas por apenas um dos serviços.
- O recorte de redes deixa de fora soluções individuais, como poços e fossas. Os dados são transversais e os resultados municipais dependem dos registros disponíveis.

## Reprodução

Use Python 3.12. No Windows, ative o ambiente com `.venv\Scripts\activate`.

```bash
git clone https://github.com/daflon-prognum/lasalle.git
cd lasalle/projeto-saneamento-sinisa
python3.12 -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
python -m src.etl
python -m src.analise
jupyter execute notebooks/analise_exploratoria.ipynb --inplace
python -m http.server 8000 --directory docs
```

Abra [o artigo local](http://localhost:8000). Os CSVs e a auditoria são gravados em `data/processed/`. As duas figuras PNG e a dispersão interativa são gravadas em `docs/assets/`. O artigo e seus gráficos funcionam com os arquivos locais. O notebook pode rodar a partir da raiz do projeto, da pasta `notebooks/` ou no Colab, onde busca os arquivos no repositório público.

## FAIR

| Princípio | Onde conferir |
| --- | --- |
| Localizável | Autoria, tema, palavras-chave, edição e fonte neste README e no cabeçalho do artigo. |
| Acessível | Repositório e GitHub Pages públicos. Ingestão a partir de endereço oficial sem autenticação. |
| Interoperável | [Dicionário](data/data_dictionary.md) com chaves, tipos, unidades e fórmulas. [Dados processados](data/processed/) em CSV UTF-8 e auditoria em JSON. |
| Reutilizável | [Licença](LICENSE), [dependências congeladas](requirements.txt), entradas verificáveis por SHA-256 e comandos de execução acima. |

## Licenças e atribuição

Código próprio sob [MIT](LICENSE). Texto e figuras próprios sob [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). Os dados brutos e seus recortes mantêm a atribuição ao Ministério das Cidades (SINISA). A licença do projeto se aplica às contribuições dos autores. O JavaScript do Plotly é distribuído com seu aviso de licença incorporado.
