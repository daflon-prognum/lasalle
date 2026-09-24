# Visualização Geoespacial com Folium

Atividade prática de visualização geoespacial utilizando **Pandas**, **NumPy** e **Folium**.

O contexto simula um módulo de inteligência geográfica para análise de oportunidades imobiliárias em **Nova Iguaçu** e **Queimados**, no Rio de Janeiro.

## Estrutura

```text
visualizacao-geoespacial-folium/
├── .gitignore
├── mapa_imoveis.py
├── mapa_imoveis.ipynb
├── mapa_01_marcadores_basicos.html
├── mapa_02_marcadores_circulares.html
├── mapa_imoveis_baixada.html
├── README.md
└── requirements.txt
```

O arquivo principal solicitado pela atividade é:

```text
mapa_imoveis_baixada.html
```

## Instalação

Entre na pasta:

```bash
cd visualizacao-geoespacial-folium
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

Execute:

```bash
python mapa_imoveis.py
```

Também é possível abrir o notebook `mapa_imoveis.ipynb` no Jupyter ou Google Colab.

---

# Base de Dados

A atividade gera uma base sintética com 45 imóveis.

Variáveis:

| Coluna | Descrição |
|---|---|
| `id_imovel` | Identificador do imóvel |
| `cidade` | Nova Iguaçu ou Queimados |
| `valor_venda` | Valor de venda |
| `tipo` | Casa, Apartamento ou Terreno |
| `latitude` | Latitude simulada |
| `longitude` | Longitude simulada |

As coordenadas são distribuídas em torno de pontos aproximados das duas cidades.

---

# Parte 1 — Inicialização e Marcadores Básicos

O centro do mapa é calculado pela média das coordenadas:

```python
centro_mapa = [
    df_mapa["latitude"].mean(),
    df_mapa["longitude"].mean(),
]
```

O mapa é criado com:

```python
folium.Map(
    location=centro_mapa,
    zoom_start=12,
    tiles="OpenStreetMap",
)
```

Os cinco primeiros imóveis são percorridos com:

```python
df_mapa.head(5).iterrows()
```

e recebem um `folium.Marker`.

O popup mostra:

- identificador;
- tipo;
- cidade;
- valor de venda.

Arquivo gerado:

```text
mapa_01_marcadores_basicos.html
```

---

# Parte 2 — Marcadores Circulares

Todos os imóveis são representados com:

```python
folium.CircleMarker(...)
```

As regras aplicadas são:

- raio fixo de 8 pixels;
- azul para Nova Iguaçu;
- laranja para Queimados;
- tooltip `"Clique para detalhes"`.

As cores são controladas por:

```python
cores_cidade = {
    "Nova Iguaçu": "blue",
    "Queimados": "orange",
}
```

Arquivo:

```text
mapa_02_marcadores_circulares.html
```

---

# Parte 3 — MarkerCluster

O terceiro mapa utiliza:

```python
from folium.plugins import MarkerCluster
```

O cluster é criado com:

```python
cluster = MarkerCluster().add_to(mapa_cluster)
```

Todos os marcadores são adicionados ao cluster, e não diretamente ao mapa.

Isso permite que pontos próximos sejam agrupados automaticamente conforme o nível de zoom.

## Cores por tipo de imóvel

```python
cores_tipo = {
    "Casa": "green",
    "Apartamento": "blue",
    "Terreno": "gray",
}
```

Cada imóvel utiliza um `folium.Icon` com a cor correspondente.

O arquivo final solicitado no exercício é:

```text
mapa_imoveis_baixada.html
```

---

# Resultado

Ao abrir o HTML final no navegador, é possível:

- navegar pelo mapa;
- aproximar e afastar o zoom;
- visualizar os clusters;
- clicar nos agrupamentos para expandi-los;
- clicar nos imóveis;
- consultar tipo, cidade e valor de venda.

## Observação

O Folium gera um arquivo HTML contendo o mapa interativo. A camada padrão utilizada neste exercício é o OpenStreetMap, portanto a visualização dos blocos do mapa depende de conexão com a internet quando o HTML é aberto.
