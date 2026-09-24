# Explorando Dados na Web com Python

Atividade prática de consumo de APIs, tratamento de respostas HTTP, download de arquivos, web scraping e leitura de tabelas HTML utilizando Python.

## Objetivos

O projeto cobre três níveis:

1. **Básico:** requisições GET, parâmetros, cabeçalhos, status HTTP, URL final e timeout;
2. **Intermediário:** JSON, DataFrames, tratamento de erros, `raise_for_status()` e arquivos binários;
3. **Avançado:** `robots.txt`, BeautifulSoup, exportação para CSV e `pandas.read_html()`.

## Estrutura

```text
explorando-dados-web-python/
├── .gitignore
├── explorando_dados_web.py
├── README.md
├── requirements.txt
└── saida/
    └── .gitkeep
```

Depois da execução, a pasta `saida/` poderá conter:

```text
saida/
├── ceps_consultados.csv
├── imagem_aleatoria.jpg
├── livros.csv
└── populacao_wikipedia.csv
```

## Instalação

Entre na pasta do projeto:

```bash
cd explorando-dados-web-python
```

Crie o ambiente virtual:

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
python explorando_dados_web.py
```

É necessário estar conectado à internet.

---

# Nível 1 — Básico

## Exercícios 1.1 a 1.4

O projeto consulta:

```text
https://jsonplaceholder.typicode.com/posts
```

A chamada utiliza `requests.get()` com:

- `params`;
- `headers`;
- `timeout=10`.

Os parâmetros usados são:

```python
parametros = {
    "userId": 2,
    "_limit": 5,
}
```

O projeto também utiliza um `User-Agent` personalizado:

```python
HEADERS = {
    "User-Agent": (
        "ProjetoExploracaoDadosWeb/1.0 "
        "(atividade academica com Python requests)"
    )
}
```

Depois da resposta, são apresentados:

```python
resposta.status_code
resposta.url
```

O `requests` monta a URL final a partir do endereço original e dos valores fornecidos em `params`.

---

# Nível 2 — Intermediário

## Exercício 2.1 — ViaCEP

Três CEPs são consultados em um loop:

```python
ceps = [
    "01001000",
    "20040002",
    "30140071",
]
```

Para cada resposta, é utilizado:

```python
dados = resposta.json()
```

Os dicionários são reunidos em um DataFrame:

```python
df_ceps = pd.DataFrame(resultados)
```

E salvos em:

```text
saida/ceps_consultados.csv
```

## Exercícios 2.2 e 2.3 — Download seguro

A função `baixar_arquivo()` utiliza:

```python
try:
    ...
    resposta.raise_for_status()
except requests.RequestException:
    ...
```

Assim, respostas HTTP de erro, falhas de conexão e timeouts podem ser tratados de maneira amigável.

A imagem é solicitada em:

```text
https://picsum.photos/400/400
```

Como imagens são dados binários, o conteúdo é obtido através de:

```python
resposta.content
```

e gravado com:

```python
with destino.open("wb") as arquivo:
    arquivo.write(resposta.content)
```

Resultado:

```text
saida/imagem_aleatoria.jpg
```

---

# Nível 3 — Avançado

## Exercício 3.1 — robots.txt e ética

Antes de acessar a página a ser raspada, o projeto consulta:

```text
https://books.toscrape.com/robots.txt
```

Quando um `robots.txt` é encontrado, as regras são interpretadas com `urllib.robotparser`.

Se a consulta das regras falhar por erro de rede, o scraping é cancelado por segurança.

Se o servidor responder `404`, o código informa que não encontrou um arquivo `robots.txt`. A atividade continua somente porque **Books to Scrape é um sandbox criado especificamente para estudos de web scraping**.

Em sites reais, também devem ser observados os termos de uso, a finalidade da coleta, a frequência das requisições e a legislação aplicável.

## Exercícios 3.2 e 3.3 — BeautifulSoup

A página:

```text
https://books.toscrape.com/
```

é processada com:

```python
BeautifulSoup(resposta.content, "html.parser")
```

Os cinco primeiros produtos são encontrados com:

```python
soup.select("article.product_pod")[:5]
```

Para cada livro são extraídos:

- título;
- preço.

Resultado:

```text
saida/livros.csv
```

## Exercício 3.4 — Wikipedia e read_html

A atividade utiliza a página:

```text
https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population
```

O HTML é convertido em um objeto semelhante a arquivo:

```python
StringIO(resposta.text)
```

e as tabelas são lidas diretamente com:

```python
pd.read_html(StringIO(resposta.text))
```

A maior tabela da página é selecionada e salva em:

```text
saida/populacao_wikipedia.csv
```

Isso demonstra que páginas com tabelas HTML bem estruturadas podem ser processadas diretamente pelo Pandas, sem BeautifulSoup.

---

# Dependências

- `requests`: requisições HTTP;
- `pandas`: DataFrames, CSV e tabelas HTML;
- `beautifulsoup4`: parsing de HTML;
- `lxml`: parser utilizado pelo Pandas para `read_html()`.

## Observação

Os resultados obtidos de APIs e páginas web podem mudar com o tempo porque as fontes são externas. O código foi estruturado com `timeout`, `raise_for_status()` e tratamento de exceções para reduzir problemas causados por falhas de rede ou respostas HTTP inesperadas.
