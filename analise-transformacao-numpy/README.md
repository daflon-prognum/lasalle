# Análise e Transformação de Dados com NumPy

Projeto com duas atividades práticas:

1. **Análise de Dados no Varejo com NumPy**
2. **Organização e Transformação de Dados com NumPy**

## Estrutura

```text
analise-transformacao-numpy/
├── .gitignore
├── atividade_numpy.py
├── atividade_numpy.ipynb
├── README.md
└── requirements.txt
```

## Executar

```bash
cd analise-transformacao-numpy
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python atividade_numpy.py
```

No Windows:

```powershell
.venv\Scripts\activate
```

Também é possível abrir `atividade_numpy.ipynb` no Jupyter ou Google Colab.

---

# Atividade 1 — Análise de Dados no Varejo

## Nível 1 — Faturamento Semanal e Filtros

```python
pecas_vendidas = np.array(
    [150, 120, 90, 210, 300, 250, 180]
)
```

O faturamento diário é calculado de forma vetorizada:

```python
faturamento_diario = pecas_vendidas * 50
```

A máscara booleana para dias com mais de 200 peças:

```python
mascara_pico = pecas_vendidas > 200
dias_pico = pecas_vendidas[mascara_pico]
```

## Nível 2 — Filiais e valores ausentes

A matriz:

```python
vendas_filiais = np.array([
    [200, 220, np.nan, 250],
    [150, 180, 160, 190],
    [300, 310, 290, 330]
])
```

Para somar por loja sem contaminar o total pelo `NaN`:

```python
np.nansum(vendas_filiais, axis=1)
```

A média geral é calculada com:

```python
np.nanmean(vendas_filiais)
```

As metas usam:

```python
np.where(
    vendas_filiais >= 200,
    "Meta Atingida",
    "Abaixo"
)
```

No projeto, o valor ausente recebe `"Sem dado"` para não ser classificado incorretamente.

## Nível 3 — Categorias e campanhas

A geração reprodutível utiliza:

```python
rng = np.random.default_rng(seed=42)
```

A contagem por departamento:

```python
categorias_unicas, contagens = np.unique(
    vendas_clientes,
    return_counts=True
)
```

A campanha com maior faturamento:

```python
indice_campanha_campea = np.argmax(
    faturamento_campanhas
)
```

Para `[12000, 45000, 23000, 89000, 31000]`, o índice retornado é `3`, correspondente à quarta campanha.

---

# Atividade 2 — Organização e Transformação de Dados

## Nível 1 — Sequências e tipos

```python
dias_mes = np.arange(1, 31)
```

```python
metas_vendas = np.linspace(
    20000,
    30000,
    5
)
```

Conversão de tipo:

```python
estoque_inteiro = estoque_decimal.astype(int)
```

`astype(int)` remove a parte decimal; ele não faz arredondamento convencional.

Os cinco últimos dias, do mais recente para o mais antigo:

```python
dias_mes[-5:][::-1]
```

## Nível 2 — Reshape, vstack, copy e ravel

Os 12 meses são reorganizados em quatro trimestres:

```python
visitas_trimestres = visitas_anuais.reshape(
    4,
    3
)
```

Empilhamento vertical:

```python
ano_completo = np.vstack([
    primeiro_semestre,
    segundo_semestre
])
```

Cópia independente:

```python
primeiro_trimestre_copia = (
    visitas_trimestres[0].copy()
)
```

Achatamento:

```python
relatorio_corrido = visitas_trimestres.ravel()
```

## Nível 3 — Ranking, broadcasting e sistema linear

Ranking por índices:

```python
np.argsort(pontuacoes)
```

Do maior para o menor:

```python
np.argsort(pontuacoes)[::-1]
```

Conversão de vetor linha em coluna:

```python
precos_coluna = precos_base[:, np.newaxis]
```

Isso permite broadcasting com vários percentuais de desconto.

O sistema linear é:

```text
2 placas + 1 sensor = 500
1 placa - 1 sensor = 100
```

Representação:

```python
A = np.array([
    [2, 1],
    [1, -1]
])

b = np.array([500, 100])
```

Solução:

```python
np.linalg.solve(A, b)
```

Resultado:

```text
Placa robótica = R$ 200,00
Sensor = R$ 100,00
```

## Conceitos exercitados

- `np.array`
- operações vetorizadas
- máscaras booleanas
- `np.nan`
- `np.nansum`
- `np.nanmean`
- `axis`
- `np.where`
- `np.random.default_rng`
- `np.unique`
- `np.argmax`
- `np.arange`
- `np.linspace`
- `astype`
- slicing
- `reshape`
- `vstack`
- `.copy()`
- `ravel`
- `argsort`
- `np.newaxis`
- broadcasting
- `np.linalg.solve`
