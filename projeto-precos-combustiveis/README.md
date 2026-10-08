# Combustíveis no Brasil

**Evolução dos preços e diferenças entre UFs, de 2015 a 2024**  
**Disciplina:** Linguagens de Programação  
**Professor:** Alexandre Neves Louzada  
**Aluno:** Guilherme Daflon Goulart Costa  
Projeto G1 · Tema 11  
Apresentação presencial: **08/10/2026**.

## Acessos

- [Página do projeto](https://daflon-prognum.github.io/lasalle/projeto-precos-combustiveis/)
- [Dashboard Streamlit](https://lasalle-combustiveis-g1.streamlit.app/)
- [Notebook com saídas](notebooks/analise_precos_combustiveis.ipynb)
- [Abrir no Colab](https://colab.research.google.com/github/daflon-prognum/lasalle/blob/main/projeto-precos-combustiveis/notebooks/analise_precos_combustiveis.ipynb)
- [Relatório](documentacao/relatorio_projeto.pdf) · [Guia de apresentação](documentacao/guia_apresentacao.pdf)

## Pergunta

Como os preços variaram entre 2015 e 2024 e quais UFs, regiões e períodos apresentaram as maiores médias e oscilações na base simulada?

A base do [professor Alexandre Neves Louzada](https://github.com/AlexandreLouzada/Dados-Simulados-G1) tem **4.440 registros, 14 colunas, 20 UFs, cinco regiões e cinco combustíveis**. O CSV original está em `dados/`, sem alteração.

## Resultados de referência

Gasolina, todos os anos e UFs:

| Indicador | Resultado |
|---|---:|
| Observações | 875 |
| Preço médio registrado | R$ 5,53 |
| UF com maior média | SP: R$ 5,93 |
| UF com menor média | DF: R$ 4,65 |
| Região com maior média | Sul: R$ 5,66 |
| Maior oscilação mensal recalculada | +38,05% em 10/2020 |
| Janeiro/2015 × dezembro/2024 | −2,45% |
| Média anual/2015 × média anual/2024 | +3,62% |
| Pearson: preço × inflação | −0,0019 (875 pares) |
| Pearson: preço × petróleo | −0,0317 (875 pares) |

Entre as médias anuais dos cinco combustíveis, GLP apresentou a maior alta (+4,96%) e diesel caiu 2,50%. O etanol tem o maior valor médio bruto registrado (R$ 5,56); a unidade física não foi documentada, então isso não indica qual combustível oferece melhor custo-benefício.

## Método e limites

- Zero valores ausentes e zero linhas inteiras duplicadas na base original.
- As 503 ocorrências adicionais com mesma data, UF e combustível possuem valores diferentes e foram preservadas. Não representam uma chave única de observação.
- Datas, tipos numéricos, valores finitos e intervalos mínimo ≤ médio ≤ máximo conferidos.
- Médias simples por observação, sem ponderação por população, consumo ou UF. A cobertura geográfica é desigual.
- Variação recalculada: `(média mensal / média do mês anterior − 1) × 100`. Lacunas interrompem o cálculo. O campo `variacao_mensal` fornecido pelo professor permanece disponível, mas não substitui esse cálculo.
- Consumo é somado somente para o combustível selecionado, na unidade não documentada da fonte.
- Preços não foram deflacionados. A base é simulada; inflação e petróleo têm vários valores por mês. Correlação de Pearson mede associação linear, sem comprovar causa.
- O banco SQLite é local ao processo do serviço e pode ser perdido em reinicializações da nuvem. O CSV permite reconstruí-lo.

## Funcionalidades

**Intermediárias:** filtros por ano, mês, região, UF, combustível e nível de preço; KPIs dinâmicos. Também há análise temporal, abas, comparação regional, tabela interativa e exportação CSV.

**Avançadas:** persistência e leitura pelo **SQLAlchemy + SQLite**; correlação estatística com **Pandas/NumPy**. O CSV é importado por assinatura SHA-256, evitando nova importação do mesmo conteúdo. Cada observação tem identificação própria. As alterações de carga são feitas em transação.

O dashboard usa Matplotlib e Seaborn. Todos os gráficos e textos do recorte acompanham os filtros, com mensagem para seleção vazia ou correlação indisponível.

## Executar o dashboard

Na raiz do repositório:

```bash
cd projeto-precos-combustiveis
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
streamlit run app.py
```

Windows: `.venv\Scripts\activate`. No Community Cloud: repositório `daflon-prognum/lasalle`, branch `main`, arquivo principal **`projeto-precos-combustiveis/app.py`**. O código resolve os caminhos a partir de `__file__`.

## Executar o notebook

No **Colab**, abra o link acima e use **Executar tudo**. A célula de leitura solicita o CSV se não o encontrar. Envie apenas o arquivo original da pasta `dados/`. SQLAlchemy é instalado somente se faltar; as outras bibliotecas já estão disponíveis no Colab.

Localmente, com o ambiente ativado: `pip install jupyter`, depois `jupyter notebook notebooks/analise_precos_combustiveis.ipynb`.

O notebook entregue contém as saídas de execução e oito gráficos. As interpretações numéricas usam o arquivo original e gasolina como recorte principal. Ao trocar o combustível ou a base, execute novamente e revise os textos.

## Organização

```text
projeto-precos-combustiveis/
├── app.py                       # Interface Streamlit
├── analise.py                   # Preparação, cálculos e gráficos
├── banco.py                     # Importação e leitura SQL
├── requirements.txt
├── README.md
├── index.html                   # Página GitHub Pages
├── .streamlit/config.toml
├── dados/simulacao_precos_combustiveis_brasil.csv
├── database/.gitkeep             # Bancos criados durante a execução
├── notebooks/analise_precos_combustiveis.ipynb
├── imagens/                     # Oito gráficos exportados
└── documentacao/                 # Relatório e guia: Markdown e PDF
```

O notebook contém as dez etapas da atividade: introdução, base, leitura, limpeza, atributos, exploração, indicadores, gráficos, interpretação e conclusão. Pode ser aberto sozinho no Colab; não depende dos módulos locais do dashboard.

## Publicação

O workflow `.github/workflows/publicar-projeto-combustiveis.yml` publica apenas a página deste projeto, as imagens e a documentação no GitHub Pages. O dashboard é um aplicativo próprio no Streamlit Cloud. A avaliação exige apresentação presencial.

## Referências

- [Orientações gerais G1](https://docs.google.com/document/d/1n0XkgQHGBB8guzgcipMufNEmHTw7bvas5vqTbjGYabo/edit)
- [Tema 11 do professor](https://github.com/AlexandreLouzada/Dados-Simulados-G1/blob/main/texto_g1_30_temas/Projeto%20G1%20TEMA%2011.docx)
- [Dados simulados](https://github.com/AlexandreLouzada/Dados-Simulados-G1)

O roteiro individual usa o título G2, mas o documento geral e o aviso de avaliação identificam esta entrega como G1.
