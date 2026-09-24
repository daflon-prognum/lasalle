# Visualização Estatística de Dados com Seaborn

Atividade prática de análise exploratória e visualização estatística de dados utilizando **Pandas**, **NumPy**, **Matplotlib** e **Seaborn**.

O contexto simula uma empresa de software SaaS que deseja analisar o perfil de engajamento dos usuários, diferenças entre planos e possíveis relações entre uso, bugs reportados e satisfação.

## Estrutura

```text
visualizacao-estatistica-seaborn/
├── .gitignore
├── analise_seaborn.py
├── analise_seaborn.ipynb
├── README.md
├── requirements.txt
└── graficos/
    └── .gitkeep
```

Ao executar o projeto, os gráficos também são salvos na pasta `graficos/`.

## Instalação

Entre na pasta:

```bash
cd visualizacao-estatistica-seaborn
```

Crie um ambiente virtual:

```bash
python -m venv .venv
```

Ative-o.

### Linux/macOS

```bash
source .venv/bin/activate
```

### Windows

```bash
.venv\Scripts\activate
```

Instale as dependências:

```bash
pip install -r requirements.txt
```

## Executar como script

```bash
python analise_seaborn.py
```

## Executar no Jupyter

```bash
jupyter notebook
```

Depois, abra:

```text
analise_seaborn.ipynb
```

Também é possível importar o notebook diretamente no Google Colab.

---

# Base de Dados

A atividade usa uma base sintética de 300 usuários.

As variáveis são:

| Coluna | Descrição |
|---|---|
| `idade` | Idade do usuário |
| `plano` | Plano Gratuito, Básico ou Pro |
| `tempo_uso_horas` | Quantidade de horas de uso |
| `bugs_reportados` | Quantidade de bugs reportados |
| `satisfacao` | Nota de satisfação entre 1 e 10 |

A base recebe ajustes artificiais para gerar relações visuais mais perceptíveis.

Usuários do plano Pro recebem:

- acréscimo no tempo de uso;
- acréscimo inicial na satisfação.

A satisfação também sofre redução proporcional à quantidade de bugs reportados.

---

# Parte 1 — Distribuições e Contagens

## 1. Countplot — usuários por plano

A ordem das barras é obtida pela frequência:

```python
ordem_planos = (
    df_sistema["plano"]
    .value_counts()
    .index
)
```

Depois:

```python
sns.countplot(
    data=df_sistema,
    x="plano",
    order=ordem_planos,
)
```

O gráfico permite verificar rapidamente qual plano possui mais usuários.

Arquivo gerado:

```text
graficos/01_usuarios_por_plano.png
```

## 2. Histplot — distribuição da idade

A distribuição das idades é representada com:

```python
sns.histplot(
    data=df_sistema,
    x="idade",
    bins=20,
    kde=True,
)
```

O histograma mostra a frequência dos valores e `kde=True` adiciona uma curva de densidade suavizada.

Arquivo:

```text
graficos/02_distribuicao_idade.png
```

---

# Parte 2 — Relações Categóricas e Numéricas

## 3. Boxplot — tempo de uso por plano

```python
sns.boxplot(
    data=df_sistema,
    x="plano",
    y="tempo_uso_horas",
)
```

O boxplot permite comparar:

- mediana;
- dispersão;
- quartis;
- possíveis valores extremos.

Arquivo:

```text
graficos/03_tempo_uso_por_plano_boxplot.png
```

## 4. Violinplot — satisfação por plano

```python
sns.violinplot(
    data=df_sistema,
    x="plano",
    y="satisfacao",
    inner="quartile",
)
```

O violinplot combina informações de distribuição e densidade. Com `inner="quartile"`, também são destacadas informações dos quartis.

Arquivo:

```text
graficos/04_satisfacao_por_plano_violinplot.png
```

---

# Parte 3 — Correlações e Análise Multivariada

## 5. Scatterplot

```python
sns.scatterplot(
    data=df_sistema,
    x="tempo_uso_horas",
    y="satisfacao",
    hue="plano",
    size="bugs_reportados",
)
```

Nesse gráfico:

- eixo X: tempo de uso;
- eixo Y: satisfação;
- cor: plano;
- tamanho: bugs reportados.

Isso permite observar múltiplas variáveis simultaneamente.

Arquivo:

```text
graficos/05_tempo_uso_satisfacao_scatterplot.png
```

## 6. Matriz de correlação

Primeiro são selecionadas apenas as colunas numéricas:

```python
variaveis_numericas = df_sistema.select_dtypes(
    include="number"
)
```

A matriz é calculada com correlação de Pearson:

```python
matriz_correlacao = (
    variaveis_numericas.corr(method="pearson")
)
```

Depois:

```python
sns.heatmap(
    matriz_correlacao,
    annot=True,
    fmt=".2f",
    cmap="coolwarm",
    center=0,
)
```

`annot=True` exibe os valores numéricos das correlações.

Arquivo:

```text
graficos/06_heatmap_correlacao.png
```

### Como interpretar

A correlação varia aproximadamente entre:

- `+1`: relação positiva forte;
- `0`: ausência de relação linear relevante;
- `-1`: relação negativa forte.

Correlação não significa necessariamente causalidade.

---

# Bônus — Pairplot

O exercício bônus utiliza:

```python
sns.pairplot(
    data=df_sistema,
    vars=[
        "tempo_uso_horas",
        "bugs_reportados",
        "satisfacao",
    ],
    hue="plano",
)
```

O pairplot permite visualizar, em uma única figura, diferentes combinações entre as variáveis escolhidas.

Arquivo:

```text
graficos/07_pairplot.png
```

---

# Gráficos produzidos

Após a execução:

```text
graficos/
├── 01_usuarios_por_plano.png
├── 02_distribuicao_idade.png
├── 03_tempo_uso_por_plano_boxplot.png
├── 04_satisfacao_por_plano_violinplot.png
├── 05_tempo_uso_satisfacao_scatterplot.png
├── 06_heatmap_correlacao.png
└── 07_pairplot.png
```

Além dos gráficos, o terminal exibe:

- matriz de correlação;
- quantidade de usuários por plano;
- médias de tempo de uso, bugs e satisfação por plano.
