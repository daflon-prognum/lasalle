from pathlib import Path

import folium
import numpy as np
import pandas as pd
from folium.plugins import MarkerCluster


# ============================================================
# CONFIGURAÇÕES
# ============================================================

PASTA_PROJETO = Path(__file__).parent
ARQUIVO_FINAL = PASTA_PROJETO / "mapa_imoveis_baixada.html"

np.random.seed(42)
n_imoveis = 45


# ============================================================
# 1. GERAÇÃO DA BASE DE DADOS
# ============================================================

dados_imoveis = {
    "id_imovel": range(1, n_imoveis + 1),
    "cidade": np.where(
        np.random.rand(n_imoveis) > 0.4,
        "Nova Iguaçu",
        "Queimados",
    ),
    "valor_venda": np.random.uniform(
        150000,
        850000,
        n_imoveis,
    ).round(2),
    "tipo": np.random.choice(
        ["Casa", "Apartamento", "Terreno"],
        n_imoveis,
    ),
}

df_mapa = pd.DataFrame(dados_imoveis)


def gerar_lat(cidade: str) -> float:
    """Gera latitude aproximada de acordo com a cidade."""
    if cidade == "Nova Iguaçu":
        return -22.756 + np.random.uniform(-0.03, 0.03)

    return -22.716 + np.random.uniform(-0.02, 0.02)


def gerar_lon(cidade: str) -> float:
    """Gera longitude aproximada de acordo com a cidade."""
    if cidade == "Nova Iguaçu":
        return -43.460 + np.random.uniform(-0.03, 0.03)

    return -43.555 + np.random.uniform(-0.02, 0.02)


df_mapa["latitude"] = df_mapa["cidade"].apply(gerar_lat)
df_mapa["longitude"] = df_mapa["cidade"].apply(gerar_lon)


# Coordenada média para centralizar todos os mapas.
centro_mapa = [
    df_mapa["latitude"].mean(),
    df_mapa["longitude"].mean(),
]


# ============================================================
# FUNÇÕES AUXILIARES
# ============================================================

def formatar_valor(valor: float) -> str:
    """Formata um valor monetário no padrão brasileiro."""
    return (
        f"R$ {valor:,.2f}"
        .replace(",", "X")
        .replace(".", ",")
        .replace("X", ".")
    )


def criar_popup(linha: pd.Series) -> str:
    """Cria o texto HTML exibido ao clicar no marcador."""
    return (
        f"<b>Imóvel #{linha['id_imovel']}</b><br>"
        f"Tipo: {linha['tipo']}<br>"
        f"Cidade: {linha['cidade']}<br>"
        f"Valor: {formatar_valor(linha['valor_venda'])}"
    )


# ============================================================
# PARTE 1 — MAPA E MARCADORES BÁSICOS
# ============================================================

mapa_basico = folium.Map(
    location=centro_mapa,
    zoom_start=12,
    tiles="OpenStreetMap",
)

for _, linha in df_mapa.head(5).iterrows():
    folium.Marker(
        location=[
            linha["latitude"],
            linha["longitude"],
        ],
        popup=folium.Popup(
            criar_popup(linha),
            max_width=300,
        ),
    ).add_to(mapa_basico)

mapa_basico.save(
    str(PASTA_PROJETO / "mapa_01_marcadores_basicos.html")
)


# ============================================================
# PARTE 2 — CIRCLEMARKER
# ============================================================

mapa_circular = folium.Map(
    location=centro_mapa,
    zoom_start=12,
    tiles="OpenStreetMap",
)

cores_cidade = {
    "Nova Iguaçu": "blue",
    "Queimados": "orange",
}

for _, linha in df_mapa.iterrows():
    cor = cores_cidade[linha["cidade"]]

    folium.CircleMarker(
        location=[
            linha["latitude"],
            linha["longitude"],
        ],
        radius=8,
        color=cor,
        fill=True,
        fill_color=cor,
        fill_opacity=0.75,
        tooltip="Clique para detalhes",
        popup=folium.Popup(
            criar_popup(linha),
            max_width=300,
        ),
    ).add_to(mapa_circular)

mapa_circular.save(
    str(PASTA_PROJETO / "mapa_02_marcadores_circulares.html")
)


# ============================================================
# PARTE 3 — CLUSTERING
# ============================================================

mapa_cluster = folium.Map(
    location=centro_mapa,
    zoom_start=12,
    tiles="OpenStreetMap",
)

cluster = MarkerCluster(
    name="Imóveis",
).add_to(mapa_cluster)

cores_tipo = {
    "Casa": "green",
    "Apartamento": "blue",
    "Terreno": "gray",
}

for _, linha in df_mapa.iterrows():
    folium.Marker(
        location=[
            linha["latitude"],
            linha["longitude"],
        ],
        popup=folium.Popup(
            criar_popup(linha),
            max_width=300,
        ),
        tooltip=f"{linha['tipo']} - {linha['cidade']}",
        icon=folium.Icon(
            color=cores_tipo[linha["tipo"]],
            icon="home",
            prefix="fa",
        ),
    ).add_to(cluster)

folium.LayerControl().add_to(mapa_cluster)

mapa_cluster.save(str(ARQUIVO_FINAL))


# ============================================================
# SAÍDA NO TERMINAL
# ============================================================

print("=== VISUALIZAÇÃO GEOESPACIAL COM FOLIUM ===")
print(f"Total de imóveis: {len(df_mapa)}")
print(
    "Centro do mapa:",
    f"{centro_mapa[0]:.6f}, {centro_mapa[1]:.6f}",
)

print("\nImóveis por cidade:")
print(df_mapa["cidade"].value_counts())

print("\nImóveis por tipo:")
print(df_mapa["tipo"].value_counts())

print("\nArquivos gerados:")
print("- mapa_01_marcadores_basicos.html")
print("- mapa_02_marcadores_circulares.html")
print("- mapa_imoveis_baixada.html")
