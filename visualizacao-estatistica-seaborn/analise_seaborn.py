from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns


# ============================================================
# CONFIGURAÇÕES GERAIS
# ============================================================

sns.set_theme(style="whitegrid", palette="muted")

PASTA_GRAFICOS = Path(__file__).parent / "graficos"
PASTA_GRAFICOS.mkdir(exist_ok=True)


# ============================================================
# 1. GERAÇÃO DA BASE DE DADOS
# ============================================================

np.random.seed(101)
n = 300

dados = {
    "idade": np.random.normal(35, 10, n).astype(int),
    "plano": np.random.choice(
        ["Gratuito", "Básico", "Pro"],
        n,
        p=[0.5, 0.3, 0.2],
    ),
    "tempo_uso_horas": np.random.uniform(1, 50, n),
    "bugs_reportados": np.random.poisson(2, n),
    "satisfacao": np.random.randint(1, 11, n),
}

df_sistema = pd.DataFrame(dados)

# Ajustando regras de negócio sintéticas para gerar
# correlações visuais.
df_sistema.loc[
    df_sistema["plano"] == "Pro",
    "tempo_uso_horas",
] += 15

df_sistema.loc[
    df_sistema["plano"] == "Pro",
    "satisfacao",
] += 2

df_sistema["satisfacao"] = (
    df_sistema["satisfacao"]
    - (df_sistema["bugs_reportados"] * 0.5)
)

df_sistema["satisfacao"] = (
    df_sistema["satisfacao"]
    .clip(1, 10)
    .astype(int)
)

df_sistema["idade"] = (
    df_sistema["idade"]
    .clip(18, 70)
)


# ============================================================
# PARTE 1 — DISTRIBUIÇÕES E CONTAGENS
# ============================================================

# ------------------------------------------------------------
# 1. Countplot — usuários por plano
# ------------------------------------------------------------

ordem_planos = (
    df_sistema["plano"]
    .value_counts()
    .index
)

plt.figure(figsize=(8, 5))

sns.countplot(
    data=df_sistema,
    x="plano",
    order=ordem_planos,
)

plt.title("Quantidade de Usuários por Plano")
plt.xlabel("Plano")
plt.ylabel("Quantidade de Usuários")
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "01_usuarios_por_plano.png",
    dpi=150,
)

plt.show()
plt.close()


# ------------------------------------------------------------
# 2. Histplot — distribuição da idade com KDE
# ------------------------------------------------------------

plt.figure(figsize=(8, 5))

sns.histplot(
    data=df_sistema,
    x="idade",
    bins=20,
    kde=True,
)

plt.title("Distribuição da Idade dos Usuários")
plt.xlabel("Idade")
plt.ylabel("Frequência")
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "02_distribuicao_idade.png",
    dpi=150,
)

plt.show()
plt.close()


# ============================================================
# PARTE 2 — RELAÇÕES CATEGÓRICAS E NUMÉRICAS
# ============================================================

# ------------------------------------------------------------
# 3. Boxplot — tempo de uso por plano
# ------------------------------------------------------------

plt.figure(figsize=(9, 5))

sns.boxplot(
    data=df_sistema,
    x="plano",
    y="tempo_uso_horas",
)

plt.title("Tempo de Uso por Plano")
plt.xlabel("Plano")
plt.ylabel("Tempo de Uso (horas)")
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "03_tempo_uso_por_plano_boxplot.png",
    dpi=150,
)

plt.show()
plt.close()


# ------------------------------------------------------------
# 4. Violinplot — satisfação por plano
# ------------------------------------------------------------

plt.figure(figsize=(9, 5))

sns.violinplot(
    data=df_sistema,
    x="plano",
    y="satisfacao",
    inner="quartile",
)

plt.title("Distribuição da Satisfação por Plano")
plt.xlabel("Plano")
plt.ylabel("Satisfação")
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "04_satisfacao_por_plano_violinplot.png",
    dpi=150,
)

plt.show()
plt.close()


# ============================================================
# PARTE 3 — CORRELAÇÕES E ANÁLISE MULTIVARIADA
# ============================================================

# ------------------------------------------------------------
# 5. Scatterplot — tempo de uso x satisfação
# ------------------------------------------------------------

plt.figure(figsize=(10, 6))

sns.scatterplot(
    data=df_sistema,
    x="tempo_uso_horas",
    y="satisfacao",
    hue="plano",
    size="bugs_reportados",
    sizes=(30, 250),
    alpha=0.75,
)

plt.title(
    "Relação entre Tempo de Uso e Satisfação"
)
plt.xlabel("Tempo de Uso (horas)")
plt.ylabel("Satisfação")
plt.legend(
    bbox_to_anchor=(1.02, 1),
    loc="upper left",
)
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "05_tempo_uso_satisfacao_scatterplot.png",
    dpi=150,
)

plt.show()
plt.close()


# ------------------------------------------------------------
# 6. Heatmap — matriz de correlação de Pearson
# ------------------------------------------------------------

variaveis_numericas = df_sistema.select_dtypes(
    include="number"
)

matriz_correlacao = (
    variaveis_numericas.corr(method="pearson")
)

print("\n=== MATRIZ DE CORRELAÇÃO DE PEARSON ===")
print(matriz_correlacao.round(2))


plt.figure(figsize=(8, 6))

sns.heatmap(
    matriz_correlacao,
    annot=True,
    fmt=".2f",
    cmap="coolwarm",
    center=0,
    square=True,
)

plt.title("Matriz de Correlação de Pearson")
plt.tight_layout()

plt.savefig(
    PASTA_GRAFICOS / "06_heatmap_correlacao.png",
    dpi=150,
)

plt.show()
plt.close()


# ------------------------------------------------------------
# 7. Bônus — Pairplot
# ------------------------------------------------------------

pairplot = sns.pairplot(
    data=df_sistema,
    vars=[
        "tempo_uso_horas",
        "bugs_reportados",
        "satisfacao",
    ],
    hue="plano",
    diag_kind="hist",
)

pairplot.fig.suptitle(
    "Análise Multivariada por Plano",
    y=1.02,
)

pairplot.savefig(
    PASTA_GRAFICOS / "07_pairplot.png",
    dpi=150,
)

plt.show()
plt.close()


# ============================================================
# RESUMO ESTATÍSTICO
# ============================================================

print("\n=== CONTAGEM DE USUÁRIOS POR PLANO ===")
print(df_sistema["plano"].value_counts())

print("\n=== MÉDIAS POR PLANO ===")
print(
    df_sistema.groupby("plano")[
        [
            "tempo_uso_horas",
            "bugs_reportados",
            "satisfacao",
        ]
    ]
    .mean()
    .round(2)
)

print(
    "\nGráficos salvos em:",
    PASTA_GRAFICOS,
)
