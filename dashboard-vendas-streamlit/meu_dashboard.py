from pathlib import Path

import pandas as pd
import streamlit as st


# Configuração da página
st.set_page_config(
    page_title="Dashboard de Vendas",
    page_icon="📊",
    layout="wide",
)

st.title("Dashboard de Vendas")


# ============================================================
# FASE 1 — CARREGAMENTO E CACHE
# ============================================================

ARQUIVO_DADOS = Path(__file__).parent / "vendas.csv"


@st.cache_data
def carregar_dados() -> pd.DataFrame:
    """Carrega e prepara a base de vendas."""
    df = pd.read_csv(ARQUIVO_DADOS)

    # Converte a coluna de data para o tipo datetime.
    df["Data"] = pd.to_datetime(df["Data"])

    return df


df = carregar_dados()


# ============================================================
# FASE 2 — FILTROS LATERAIS
# ============================================================

st.sidebar.title("Filtros")

lista_de_categorias = sorted(df["Categoria"].dropna().unique().tolist())

categorias_selecionadas = st.sidebar.multiselect(
    "Selecione as Categorias",
    options=lista_de_categorias,
    default=lista_de_categorias,
)

# Regra principal de interatividade:
# o valor retornado pelo widget é utilizado para filtrar o DataFrame.
if categorias_selecionadas:
    df_filtrado = df[df["Categoria"].isin(categorias_selecionadas)].copy()
else:
    # Se nenhuma categoria estiver marcada, o painel fica sem registros.
    df_filtrado = df.iloc[0:0].copy()


# ============================================================
# FASE 3 — MÉTRICAS E VISUALIZAÇÕES
# ============================================================

receita_calculada = df_filtrado["Receita"].sum()
total_pedidos = df_filtrado["Pedido"].nunique()

col1, col2 = st.columns([1, 1])

with col1:
    st.metric(
        label="Receita Total",
        value=f"R$ {receita_calculada:,.2f}".replace(",", "X")
        .replace(".", ",")
        .replace("X", "."),
    )

with col2:
    st.metric(
        label="Total de Pedidos",
        value=total_pedidos,
    )


aba1, aba2 = st.tabs(["Evolução Mensal", "Tabela de Dados"])


with aba1:
    st.subheader("Receita por mês")

    if df_filtrado.empty:
        st.info("Selecione pelo menos uma categoria para visualizar o gráfico.")
    else:
        dados_agrupados = (
            df_filtrado.assign(
                Mes=df_filtrado["Data"].dt.to_period("M").dt.to_timestamp()
            )
            .groupby("Mes", as_index=True)["Receita"]
            .sum()
            .sort_index()
        )

        st.area_chart(dados_agrupados)


with aba2:
    st.subheader("Dados filtrados")

    st.dataframe(
        df_filtrado,
        use_container_width=True,
        hide_index=True,
    )

    csv_filtrado = df_filtrado.to_csv(
        index=False,
    ).encode("utf-8-sig")

    st.download_button(
        label="Baixar dados filtrados em CSV",
        data=csv_filtrado,
        file_name="vendas_filtradas.csv",
        mime="text/csv",
    )
