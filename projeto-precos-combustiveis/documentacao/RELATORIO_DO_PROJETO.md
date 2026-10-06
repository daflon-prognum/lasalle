# Relatório do projeto G1

**Combustíveis no Brasil: evolução dos preços e diferenças entre UFs, de 2015 a 2024**  
**Aluno:** Guilherme Daflon Goulart Costa · Tema 11  
**Disciplina:** Linguagens de Programação · LaSalle  
**Preparação:** 06/10/2026 · **Apresentação presencial:** 08/10/2026

## 1. O que foi desenvolvido

O projeto reúne um notebook de análise, um dashboard Streamlit e uma página HTML para acessar a entrega. A pergunta principal é: como os preços variaram e quais UFs, regiões e períodos tiveram as maiores médias e oscilações na simulação?

Foi utilizado o arquivo original do professor, com 4.440 registros, 14 colunas, 20 UFs, cinco regiões, cinco combustíveis e 120 meses. Gasolina é o recorte principal do notebook; o dashboard permite escolher os demais combustíveis.

O notebook tem dez seções, 29 células, 13 células de código e oito gráficos exportados. A execução foi concluída e suas saídas foram salvas no arquivo entregue.

## 2. Preparação e decisões

- Conferidos tipos, datas, valores finitos, preços positivos e intervalos mínimo ≤ médio ≤ máximo.
- Não foram encontrados valores ausentes nem linhas inteiras duplicadas.
- Mantidas as 503 ocorrências adicionais da mesma chave (data, UF, combustível), pois os valores das observações diferem.
- Calculadas amplitude absoluta e percentual dos preços.
- Calculadas médias simples por observação. Rankings mostram também a quantidade de registros.
- Recalculada a variação entre médias de meses consecutivos. Meses ausentes não são preenchidos e interrompem o cálculo.
- Separadas comparação entre meses e comparação entre médias anuais.

A unidade física de preço e consumo não está documentada na fonte. As comparações territoriais usam o mesmo combustível. O gráfico de valores brutos por combustível é apresentado com essa limitação; não permite escolher o mais econômico.

## 3. Resultados principais

Recorte: gasolina, todas as UFs e todo o período.

| Medida | Resultado |
|---|---:|
| Observações | 875 |
| Média do preço informado | R$ 5,53 |
| Maior média por UF | SP: R$ 5,93 (76 observações) |
| Menor média por UF | DF: R$ 4,65 (24 observações) |
| Maior média regional | Sul: R$ 5,66 |
| Maior oscilação mensal | +38,05% em outubro de 2020 |
| Primeiro × último mês | −2,45% |
| Médias anuais de 2015 × 2024 | +3,62% |
| Dispersão das variações mensais | 14,24 pontos percentuais (119 variações) |
| Maior média mensal | Novembro de 2018: R$ 6,77 |
| Ano com maior dispersão mensal | 2017: 19,57 pontos percentuais |
| Pearson: preço × inflação | −0,0019 (875 pares) |
| Pearson: preço × petróleo | −0,0317 (875 pares) |
| Soma do consumo estimado | 109.068.228, na unidade não documentada da fonte |

Entre as médias anuais de 2015 e 2024:

| Combustível | Variação |
|---|---:|
| GLP | +4,96% |
| Gasolina | +3,62% |
| Etanol | +3,34% |
| GNV | +1,55% |
| Diesel | −2,50% |

O etanol tem o maior valor médio bruto registrado: R$ 5,56. Esse ranking não indica maior custo por uma unidade física comum.

## 4. Interpretação

As oscilações mensais da gasolina foram maiores que a mudança entre médias anuais. SP teve a maior média no conjunto, mas não é possível concluir que tenha sido a UF mais cara em todos os meses.

A correlação ficou próxima de zero. Isso indica pouca associação linear nesta simulação, sem comprovar ausência de relação no mundo real. Inflação e petróleo apresentam valores diferentes dentro do mesmo mês; não representam séries oficiais nacionais.

Não foi aplicado ajuste por inflação. A cobertura é desigual, e mudanças de composição das UFs podem afetar as médias agregadas. Os resultados são descritivos e não fundamentam recomendações de compra.

## 5. Dashboard e arquitetura

Fluxo: **CSV → assinatura SHA-256 → SQLite → consulta SQLAlchemy → Pandas → filtros → indicadores e gráficos**.

O CSV original permite reconstruir o banco. A assinatura evita repetir a importação do mesmo conteúdo. As observações têm identificação própria e a carga usa transação. O cache de Streamlit evita nova leitura a cada interação; não é apresentado como persistência permanente.

Filtros: ano, mês, região, UF, combustível e nível de preço. As opções de UF acompanham a região. Uma seleção vazia mostra uma mensagem. Correlação é exibida somente com pelo menos três pares e variação nas duas variáveis.

Abas: Evolução; UFs e combustíveis; Associações; Dados e método. Há KPIs, gráficos Matplotlib/Seaborn, tabela interativa, download CSV, leitura do recorte e explicação das fórmulas. A comparação entre combustíveis abre somente esse filtro, mantendo os outros cinco e informando o contexto.

## 6. Correspondência com a avaliação

| Critério | Evidência no projeto |
|---|---|
| Organização (1,0) | Pastas próprias, README, fonte original, módulos e documentos |
| Tratamento (1,0) | Diagnóstico, validações, manutenção das observações e atributos |
| Exploração (1,5) | Cobertura, distribuição, rankings, evolução e volatilidade |
| Gráficos (1,5) | Linha, barras por UF/região/combustível, índice anual, heatmap e dispersões |
| Streamlit (2,0) | Aplicativo com problema, métricas, tabelas, gráficos e conclusão |
| Avançadas (1,0) | SQLAlchemy + SQLite; correlação estatística Pandas/NumPy |
| Interatividade (1,0) | Seis filtros, KPIs recalculados, abas e exportação |
| Interpretação (1,0) | Discussão dos resultados, bases de cálculo e limites |

Essa correspondência demonstra onde cada item foi implementado; não antecipa a nota atribuída pelo professor.

## 7. Execução e publicação

- Notebook: 13 células de código executadas em sequência, 33 saídas salvas, incluindo os oito gráficos.
- SQLite: 4.440 observações importadas e lidas por SQLAlchemy; a base original foi preservada.
- Dashboard: executado localmente e no Streamlit Cloud, com os indicadores de gasolina conferidos. Na execução local, a troca para diesel, o recorte Sudeste e a seleção vazia foram conferidos; na nuvem, também foram abertas as abas de comparação e associações.
- GitHub: código enviado à branch main; commit inicial d037dcd.
- GitHub Pages: workflow concluído com sucesso; página pública aberta com imagens e links.
- Streamlit Cloud: novo aplicativo publicado com Python 3.12; URL pública aberta e indicadores exibidos.

Links da entrega:

- GitHub: https://github.com/daflon-prognum/lasalle/tree/main/projeto-precos-combustiveis
- Página: https://daflon-prognum.github.io/lasalle/projeto-precos-combustiveis/
- Dashboard: https://lasalle-combustiveis-g1.streamlit.app/
- Colab: https://colab.research.google.com/github/daflon-prognum/lasalle/blob/main/projeto-precos-combustiveis/notebooks/analise_precos_combustiveis.ipynb

## 8. Revisão e entrega

Revisar os textos pessoais, ensaiar a demonstração e conferir os links antes da aula. Levar uma cópia local do notebook com saídas, CSV, gráficos e este relatório para apresentar mesmo sem internet. A avaliação exige apresentação presencial em 08/10/2026. Não foi realizada submissão no Classroom.

Referências: orientações gerais G1, roteiro do tema 11 e base do repositório [Dados-Simulados-G1](https://github.com/AlexandreLouzada/Dados-Simulados-G1). Os roteiros individuais usam G2 no título; a entrega foi identificada como G1 conforme o documento geral e o aviso da avaliação.
