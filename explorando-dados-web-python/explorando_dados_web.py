from io import StringIO
from pathlib import Path
from urllib import robotparser

import pandas as pd
import requests
from bs4 import BeautifulSoup


# ============================================================
# CONFIGURAÇÕES GERAIS
# ============================================================

TIMEOUT = 10

HEADERS = {
    "User-Agent": (
        "ProjetoExploracaoDadosWeb/1.0 "
        "(atividade academica com Python requests)"
    )
}

PASTA_SAIDA = Path(__file__).parent / "saida"
PASTA_SAIDA.mkdir(exist_ok=True)


# ============================================================
# NÍVEL 1 — BÁSICO
# URLs, parâmetros e cabeçalhos
# ============================================================

def nivel_1_jsonplaceholder() -> None:
    """Consulta a API JSONPlaceholder usando GET, params e headers."""
    print("\n" + "=" * 70)
    print("NÍVEL 1 — JSONPlaceholder")
    print("=" * 70)

    url = "https://jsonplaceholder.typicode.com/posts"

    # Filtra pelo userId 2 e limita a resposta a 5 registros.
    parametros = {
        "userId": 2,
        "_limit": 5,
    }

    try:
        resposta = requests.get(
            url,
            params=parametros,
            headers=HEADERS,
            timeout=TIMEOUT,
        )

        # Gera exceção caso o servidor retorne 4xx ou 5xx.
        resposta.raise_for_status()

        print(f"Status HTTP: {resposta.status_code}")
        print(f"URL final: {resposta.url}")
        print(f"User-Agent enviado: {HEADERS['User-Agent']}")

        dados = resposta.json()
        df_posts = pd.DataFrame(dados)

        print("\nPrimeiros resultados:")
        if df_posts.empty:
            print("Nenhum registro retornado.")
        else:
            colunas = [
                coluna
                for coluna in ["userId", "id", "title"]
                if coluna in df_posts.columns
            ]
            print(df_posts[colunas].to_string(index=False))

    except requests.RequestException as erro:
        print(f"Falha ao consultar o JSONPlaceholder: {erro}")


# ============================================================
# NÍVEL 2 — INTERMEDIÁRIO
# JSON, tratamento de erros e arquivos binários
# ============================================================

def consultar_ceps() -> pd.DataFrame:
    """Consulta três CEPs no ViaCEP e devolve um DataFrame."""
    print("\n" + "=" * 70)
    print("NÍVEL 2.1 — ViaCEP")
    print("=" * 70)

    ceps = [
        "01001000",  # São Paulo
        "20040002",  # Rio de Janeiro
        "30140071",  # Belo Horizonte
    ]

    resultados = []

    for cep in ceps:
        url = f"https://viacep.com.br/ws/{cep}/json/"

        try:
            resposta = requests.get(
                url,
                headers=HEADERS,
                timeout=TIMEOUT,
            )
            resposta.raise_for_status()

            # .json() converte a resposta JSON diretamente em um dicionário.
            dados = resposta.json()

            if dados.get("erro"):
                resultados.append(
                    {
                        "cep_consultado": cep,
                        "erro": "CEP não encontrado",
                    }
                )
                print(f"{cep}: CEP não encontrado.")
                continue

            dados["cep_consultado"] = cep
            resultados.append(dados)

            print(
                f"{cep}: {dados.get('localidade', '')} - "
                f"{dados.get('uf', '')}"
            )

        except requests.RequestException as erro:
            resultados.append(
                {
                    "cep_consultado": cep,
                    "erro": str(erro),
                }
            )
            print(f"{cep}: falha na consulta — {erro}")

    df_ceps = pd.DataFrame(resultados)

    destino = PASTA_SAIDA / "ceps_consultados.csv"
    df_ceps.to_csv(
        destino,
        index=False,
        encoding="utf-8-sig",
    )

    print(f"\nCSV dos CEPs salvo em: {destino}")
    return df_ceps


def baixar_arquivo(url: str, destino: Path) -> bool:
    """
    Faz download de um arquivo de maneira segura.

    raise_for_status() transforma respostas HTTP 4xx/5xx em exceções,
    que são tratadas pelo bloco try/except.
    """
    try:
        resposta = requests.get(
            url,
            headers=HEADERS,
            timeout=TIMEOUT,
        )
        resposta.raise_for_status()

        # Arquivos binários devem ser gravados com 'wb'.
        with destino.open("wb") as arquivo:
            arquivo.write(resposta.content)

        print(f"Download concluído: {destino}")
        print(f"Status HTTP: {resposta.status_code}")
        print(f"URL final após redirecionamentos: {resposta.url}")
        return True

    except requests.RequestException as erro:
        print(f"Não foi possível baixar '{url}'.")
        print(f"Motivo: {erro}")
        return False

    except OSError as erro:
        print(f"Não foi possível salvar o arquivo '{destino}'.")
        print(f"Motivo: {erro}")
        return False


def baixar_imagem_aleatoria() -> None:
    """Baixa uma imagem aleatória do Lorem Picsum."""
    print("\n" + "=" * 70)
    print("NÍVEL 2.2 e 2.3 — Download seguro de imagem")
    print("=" * 70)

    url = "https://picsum.photos/400/400"
    destino = PASTA_SAIDA / "imagem_aleatoria.jpg"

    baixar_arquivo(url, destino)


# ============================================================
# NÍVEL 3 — AVANÇADO
# Web scraping e ética
# ============================================================

URL_LIVROS = "https://books.toscrape.com/"
URL_ROBOTS = "https://books.toscrape.com/robots.txt"


def verificar_robots_txt() -> bool:
    """
    Consulta robots.txt antes do scraping.

    Se o arquivo existir, urllib.robotparser é usado para verificar
    se o User-Agent do projeto pode acessar a página desejada.
    """
    print("\n" + "=" * 70)
    print("NÍVEL 3.1 — Verificação do robots.txt")
    print("=" * 70)

    try:
        resposta = requests.get(
            URL_ROBOTS,
            headers=HEADERS,
            timeout=TIMEOUT,
        )

        print(f"URL consultada: {resposta.url}")
        print(f"Status HTTP do robots.txt: {resposta.status_code}")

        if resposta.status_code == 404:
            print(
                "O site não publicou um robots.txt neste endereço. "
                "Como Books to Scrape é um sandbox criado para estudos "
                "de web scraping, a atividade continuará."
            )
            return True

        resposta.raise_for_status()

        print("\nConteúdo do robots.txt:")
        print(resposta.text.strip() or "(arquivo vazio)")

        regras = robotparser.RobotFileParser()
        regras.set_url(URL_ROBOTS)
        regras.parse(resposta.text.splitlines())

        permitido = regras.can_fetch(
            HEADERS["User-Agent"],
            URL_LIVROS,
        )

        print(
            "\nAcesso à página principal permitido pelo robots.txt? "
            f"{permitido}"
        )

        return permitido

    except requests.RequestException as erro:
        print(f"Não foi possível verificar o robots.txt: {erro}")
        print(
            "Por segurança, o scraping não será executado "
            "quando a verificação falhar."
        )
        return False


def raspar_cinco_livros() -> pd.DataFrame:
    """Extrai título e preço dos cinco primeiros livros."""
    print("\n" + "=" * 70)
    print("NÍVEL 3.2 e 3.3 — BeautifulSoup e CSV")
    print("=" * 70)

    if not verificar_robots_txt():
        print("Scraping cancelado.")
        return pd.DataFrame()

    try:
        resposta = requests.get(
            URL_LIVROS,
            headers=HEADERS,
            timeout=TIMEOUT,
        )
        resposta.raise_for_status()

        # Usamos response.content (bytes) para que o parser lide
        # corretamente com a codificação da página.
        soup = BeautifulSoup(
            resposta.content,
            "html.parser",
        )

        elementos_livros = soup.select(
            "article.product_pod"
        )[:5]

        livros = []

        for elemento in elementos_livros:
            link_titulo = elemento.select_one("h3 a")
            elemento_preco = elemento.select_one("p.price_color")

            if link_titulo is None or elemento_preco is None:
                continue

            titulo = link_titulo.get(
                "title",
                link_titulo.get_text(strip=True),
            )
            preco = elemento_preco.get_text(strip=True)

            livros.append(
                {
                    "titulo": titulo,
                    "preco": preco,
                }
            )

        df_livros = pd.DataFrame(livros)

        print("\nCinco primeiros livros:")
        if df_livros.empty:
            print("Nenhum livro encontrado.")
        else:
            print(df_livros.to_string(index=False))

        destino = PASTA_SAIDA / "livros.csv"
        df_livros.to_csv(
            destino,
            index=False,
            encoding="utf-8-sig",
        )

        print(f"\nCSV dos livros salvo em: {destino}")
        return df_livros

    except requests.RequestException as erro:
        print(f"Falha ao acessar Books to Scrape: {erro}")
        return pd.DataFrame()


def extrair_tabela_wikipedia() -> pd.DataFrame:
    """
    Obtém uma página da Wikipedia e converte tabelas HTML
    diretamente em DataFrames com pandas.read_html().
    """
    print("\n" + "=" * 70)
    print("NÍVEL 3.4 — pandas.read_html() + StringIO")
    print("=" * 70)

    url = (
        "https://en.wikipedia.org/wiki/"
        "List_of_countries_and_dependencies_by_population"
    )

    try:
        resposta = requests.get(
            url,
            headers=HEADERS,
            timeout=TIMEOUT,
        )
        resposta.raise_for_status()

        # StringIO transforma a string HTML em um objeto semelhante
        # a arquivo, apropriado para pandas.read_html().
        tabelas = pd.read_html(
            StringIO(resposta.text)
        )

        if not tabelas:
            print("Nenhuma tabela encontrada na página.")
            return pd.DataFrame()

        # Nessa página, a maior tabela é a lista de países/populações.
        tabela_populacao = max(
            tabelas,
            key=len,
        )

        destino = PASTA_SAIDA / "populacao_wikipedia.csv"
        tabela_populacao.to_csv(
            destino,
            index=False,
            encoding="utf-8-sig",
        )

        print(
            f"Foram encontradas {len(tabelas)} tabela(s) HTML."
        )
        print(
            "Tabela selecionada: "
            f"{len(tabela_populacao)} linhas e "
            f"{len(tabela_populacao.columns)} colunas."
        )
        print("\nPrimeiras linhas:")
        print(tabela_populacao.head().to_string(index=False))
        print(f"\nCSV salvo em: {destino}")

        return tabela_populacao

    except requests.RequestException as erro:
        print(f"Falha ao acessar a Wikipedia: {erro}")
        return pd.DataFrame()

    except ValueError as erro:
        print(f"Não foi possível interpretar as tabelas HTML: {erro}")
        return pd.DataFrame()


# ============================================================
# EXECUÇÃO
# ============================================================

def main() -> None:
    nivel_1_jsonplaceholder()

    df_ceps = consultar_ceps()
    print("\nDataFrame dos CEPs:")
    print(df_ceps.to_string(index=False))

    baixar_imagem_aleatoria()
    raspar_cinco_livros()
    extrair_tabela_wikipedia()

    print("\n" + "=" * 70)
    print("ATIVIDADE CONCLUÍDA")
    print("=" * 70)
    print(f"Arquivos gerados em: {PASTA_SAIDA}")


if __name__ == "__main__":
    main()
