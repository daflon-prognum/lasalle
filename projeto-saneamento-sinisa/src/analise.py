"""Estatísticas e figuras do artigo a partir das tabelas processadas."""
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import plotly.graph_objects as go

from src.etl import REGIOES, ROOT

AGUA = "#14788a"
ESGOTO = "#a44f2a"
CORES_REGIAO = ["#126d93", "#aa592e", "#387c61", "#7650a0", "#a03765"]
TOTAIS = ["agua_total_pct", "esgoto_total_pct"]
DIFERENCA = "diferenca_total_pp"


def carregar(pasta=ROOT / "data/processed"):
    municipios = pd.read_csv(pasta / "municipios.csv", dtype={"ibge": "string"})
    territorios = pd.read_csv(pasta / "territorios.csv")
    return municipios, territorios


def pares_validos(municipios):
    return municipios.dropna(subset=TOTAIS).copy()


def resumo_municipal(pares):
    return (
        pares.groupby("regiao")[DIFERENCA]
        .agg(n="size", mediana="median", q1=lambda s: s.quantile(0.25), q3=lambda s: s.quantile(0.75))
        .reindex(REGIOES)
    )


def diagnostico(pares):
    diferenca = pares[DIFERENCA]
    q1, q3 = diferenca.quantile([0.25, 0.75])
    iqr = q3 - q1
    limite_inferior, limite_superior = q1 - 1.5 * iqr, q3 + 1.5 * iqr
    return {
        "n": len(pares),
        "media_pp": diferenca.mean(),
        "mediana_pp": diferenca.median(),
        "desvio_padrao_pp": diferenca.std(),
        "q1_pp": q1,
        "q3_pp": q3,
        "pearson": pares[TOTAIS].corr().iloc[0, 1],
        "spearman": pares[TOTAIS].rank().corr().iloc[0, 1],
        "abaixo_limite_iqr": int(diferenca.lt(limite_inferior).sum()),
        "acima_limite_iqr": int(diferenca.gt(limite_superior).sum()),
        "esgoto_maior": int(diferenca.lt(0).sum()),
    }


def percent(valor, casas=1):
    return f"{valor:.{casas}f}%".replace(".", ",")


def eixo_percentual(ax):
    ax.set_xlim(0, 104)
    ax.set_xticks(np.arange(0, 101, 20), [f"{v}%" for v in range(0, 101, 20)])
    ax.grid(axis="x", color="#dce5e7")
    ax.set_axisbelow(True)
    ax.tick_params(length=0)
    ax.spines[["top", "right", "left", "bottom"]].set_visible(False)


def grafico_regioes(territorios):
    dados = territorios.loc[territorios["nivel"].ne("UF")]
    y = np.arange(len(dados))
    fig, ax = plt.subplots(figsize=(9.2, 4.8), layout="constrained")
    for coluna, rotulo, cor, deslocamento in [
        ("agua_total_pct", "Rede de água", AGUA, -0.19),
        ("esgoto_total_pct", "Rede de esgoto", ESGOTO, 0.19),
    ]:
        barras = ax.barh(y + deslocamento, dados[coluna], height=0.33, label=rotulo, color=cor)
        ax.bar_label(barras, labels=[percent(v) for v in dados[coluna]], padding=4, fontsize=9)
    ax.set_yticks(y, dados["territorio"])
    ax.invert_yaxis()
    eixo_percentual(ax)
    ax.set_xlabel("Atendimento da população total")
    ax.legend(loc="lower left", bbox_to_anchor=(0, 1), ncols=2, frameon=False)
    return fig


def grafico_urbano_rural(territorios):
    dados = territorios.loc[territorios["nivel"].ne("UF")]
    y = np.arange(len(dados))
    fig, eixos = plt.subplots(1, 2, figsize=(10.2, 4.1), sharey=True, layout="constrained")
    for ax, modulo, titulo in zip(eixos, ["agua", "esgoto"], ["Rede de água", "Rede de esgoto"]):
        rural, urbana = dados[f"{modulo}_rural_pct"], dados[f"{modulo}_urbana_pct"]
        ax.hlines(y, rural, urbana, color="#8a9ca4", linewidth=2)
        ax.scatter(rural, y, c=ESGOTO, label="Rural", marker="s", s=38, zorder=3)
        ax.scatter(urbana, y, c=AGUA, label="Urbana", s=44, zorder=3)
        eixo_percentual(ax)
        ax.set_yticks(y, dados["territorio"])
        ax.set_title(titulo, loc="left", fontsize=12)
        ax.set_xlabel("Atendimento no recorte")
        ax.legend(loc="lower left", frameon=False, ncols=2)
    eixos[0].set_ylim(len(y) - 0.15, -0.5)
    return fig


def grafico_municipios(pares):
    fig = go.Figure()
    for regiao, cor in zip(REGIOES, CORES_REGIAO):
        dados = pares.loc[pares["regiao"].eq(regiao)]
        fig.add_trace(go.Scatter(
            x=dados["agua_total_pct"].tolist(),
            y=dados["esgoto_total_pct"].tolist(),
            mode="markers",
            name=f"{regiao} ({len(dados):,})".replace(",", "."),
            customdata=dados[["municipio", "uf", DIFERENCA]].values.tolist(),
            marker={"color": cor, "size": 6, "opacity": 0.64},
            hovertemplate="%{customdata[0]} · %{customdata[1]}<br>"
                          "Água: %{x:.2f}%<br>Esgoto: %{y:.2f}%<br>"
                          "Diferença: %{customdata[2]:.2f} p.p.<extra></extra>",
        ))
    fig.add_shape(type="line", x0=0, x1=100, y0=0, y1=100,
                  line={"dash": "dash", "color": "#627279", "width": 1}, layer="below")
    fig.update_layout(
        template="plotly_white", margin={"l": 58, "r": 16, "t": 13, "b": 88},
        font={"family": "system-ui, sans-serif", "color": "#223e48", "size": 12},
        separators=",.", legend={"orientation": "h", "yanchor": "top", "y": -0.22},
        xaxis={"title": "Atendimento por rede de água", "range": [-2, 102], "ticksuffix": "%"},
        yaxis={"title": "Atendimento por rede de esgoto", "range": [-2, 102], "ticksuffix": "%"},
    )
    return fig


def publicar_figuras(territorios, pares, pasta):
    pasta.mkdir(parents=True, exist_ok=True)
    for nome, figura in [
        ("regioes", grafico_regioes(territorios)),
        ("urbano-rural", grafico_urbano_rural(territorios)),
    ]:
        figura.savefig(pasta / f"{nome}.png", dpi=180, facecolor="white")
        plt.close(figura)
    grafico_municipios(pares).write_html(
        pasta / "municipios.html", include_plotlyjs="directory", div_id="municipios",
        default_height="96vh",
        config={"responsive": True, "displaylogo": False, "scrollZoom": False},
    )
    # Inclui configuração mobile dentro do documento independente criado pelo Plotly.
    arquivo = pasta / "municipios.html"
    html = arquivo.read_text(encoding="utf-8")
    html = html.replace('<meta charset="utf-8" />',
                        '<meta charset="utf-8" /><meta name="viewport" content="width=device-width, initial-scale=1" />'
                        '<title>Água e esgoto por município</title>')
    arquivo.write_text(html.replace("<html>", '<html lang="pt-BR">'), encoding="utf-8")


if __name__ == "__main__":
    municipios, territorios = carregar()
    pares = pares_validos(municipios)
    publicar_figuras(territorios, pares, ROOT / "docs/assets")
    print(resumo_municipal(pares).round(2).to_string())
    print(pd.Series(diagnostico(pares)).round(3).to_string())
