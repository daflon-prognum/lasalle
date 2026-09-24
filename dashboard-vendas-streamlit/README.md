# Dashboard de Vendas com Streamlit

Atividade prática de desenvolvimento e publicação de um dashboard interativo de Business Intelligence utilizando **Streamlit** e **Pandas**.

## Objetivo

O projeto implementa:

- carregamento de dados em CSV;
- otimização com `@st.cache_data`;
- filtro lateral por categoria;
- métricas de receita total e total de pedidos;
- abas de navegação;
- gráfico de evolução mensal;
- tabela interativa;
- download do recorte filtrado em CSV;
- estrutura pronta para deploy no Streamlit Community Cloud.

## Estrutura

```text
dashboard-vendas-streamlit/
├── .gitignore
├── meu_dashboard.py
├── README.md
├── requirements.txt
└── vendas.csv
```

## Executar localmente

Entre na pasta do projeto:

```bash
cd dashboard-vendas-streamlit
```

Crie um ambiente virtual:

```bash
python -m venv .venv
```

Ative o ambiente.

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

Execute o dashboard:

```bash
streamlit run meu_dashboard.py
```

O Streamlit exibirá no terminal o endereço local do aplicativo, normalmente `http://localhost:8501`.

## Fase 1 — Estrutura e desempenho

A função `carregar_dados()` utiliza Pandas para ler `vendas.csv`.

Ela é decorada com:

```python
@st.cache_data
```

O cache evita que o arquivo seja relido desnecessariamente a cada interação do usuário. O resultado pode ser reutilizado enquanto os dados e a função não sofrerem alterações relevantes.

## Fase 2 — Filtros laterais

O painel lateral é criado com:

```python
st.sidebar.title("Filtros")
```

As categorias são disponibilizadas em um `multiselect`:

```python
categorias_selecionadas = st.sidebar.multiselect(
    "Selecione as Categorias",
    options=lista_de_categorias,
    default=lista_de_categorias,
)
```

O valor retornado pelo componente é usado diretamente para filtrar o DataFrame:

```python
df_filtrado = df[
    df["Categoria"].isin(categorias_selecionadas)
]
```

Dessa maneira, qualquer alteração no filtro provoca a reexecução do script e a atualização das métricas, gráfico e tabela.

## Fase 3 — Métricas e visualizações

A parte superior é dividida em duas colunas:

```python
col1, col2 = st.columns([1, 1])
```

São apresentadas as métricas:

- Receita Total;
- Total de Pedidos.

A navegação usa duas abas:

```python
aba1, aba2 = st.tabs(
    ["Evolução Mensal", "Tabela de Dados"]
)
```

Na primeira aba, os registros são agrupados por mês antes de serem exibidos com `st.area_chart()`.

Na segunda aba, `st.dataframe()` mostra os dados filtrados e `st.download_button()` permite baixar o mesmo recorte em CSV.

## Base de dados

O arquivo `vendas.csv` incluído neste projeto é uma base de exemplo criada para a atividade.

Colunas:

| Coluna | Descrição |
|---|---|
| Data | Data da venda |
| Pedido | Identificador do pedido |
| Categoria | Categoria do produto |
| Produto | Produto vendido |
| Quantidade | Quantidade vendida |
| Receita | Valor da venda |

## Publicação no Streamlit Community Cloud

Depois de enviar esta pasta para o GitHub:

1. Acesse o Streamlit Community Cloud.
2. Conecte sua conta do GitHub.
3. Escolha o repositório.
4. Escolha a branch `main`.
5. Informe o caminho do arquivo principal.

Se este projeto estiver dentro do repositório `lasalle`, o caminho será:

```text
dashboard-vendas-streamlit/meu_dashboard.py
```

6. Clique em **Deploy**.

O `requirements.txt` está na mesma pasta do arquivo principal, formato suportado pelo Streamlit Community Cloud.

## Observação sobre caminhos

O arquivo CSV é localizado a partir da pasta do próprio script:

```python
Path(__file__).parent / "vendas.csv"
```

Isso evita problemas quando o dashboard é executado a partir da raiz de um repositório que contém várias atividades.
