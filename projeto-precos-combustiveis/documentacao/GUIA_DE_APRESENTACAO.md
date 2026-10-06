# Guia de apresentação

**Guilherme Daflon Goulart Costa · Projeto G1 · Tema 11**  
**Combustíveis no Brasil: evolução dos preços e diferenças entre UFs, de 2015 a 2024**  
**Apresentação presencial:** 08/10/2026

## 1. Antes de começar

- Abrir a página do projeto, o dashboard e o notebook com saídas.
- No dashboard: gasolina, todos os anos, meses, regiões, UFs e níveis de preço.
- Guardar localmente o notebook, o CSV e a pasta de gráficos para usar sem internet.
- Ensaiar a mudança de combustível, região e ano. Deixar os links acessíveis em uma aba.
- Revisar os textos com suas palavras e explicar os cálculos que aparecem na demonstração.

As orientações não informam duração. O roteiro abaixo sugere **6 a 8 minutos**, ajustáveis ao tempo concedido pelo professor.

## 2. Roteiro sugerido

### Abertura · 40 segundos

**Mostrar:** página do projeto.

“Meu projeto analisa preços de combustíveis de 2015 a 2024. A pergunta é onde a base apresenta as maiores médias e em quais períodos os preços mais oscilaram. Usei a simulação disponibilizada pelo professor.”

**Lembrar:** não apresentar os resultados como preços atuais ou oficiais do Brasil.

### Base e preparação · 1 minuto

**Mostrar:** seções 2 e 4 do notebook.

“A base tem 4.440 observações, 20 UFs e cinco combustíveis. Conferi os tipos, as datas e os preços. Não havia valores ausentes nem linhas inteiras duplicadas. Encontrei 503 ocorrências adicionais da mesma chave, mas com valores diferentes, então mantive essas observações e agrupei na hora da análise.”

**Explicar:** uma chave repetida não prova que a observação é duplicada. Cada linha recebe sua identificação no banco.

### Principais resultados · 1 minuto e 30 segundos

**Mostrar:** indicadores e linha temporal da gasolina.

“A gasolina ficou com média de R$ 5,53. SP teve a maior média por UF, de R$ 5,93, e o DF a menor, de R$ 4,65. A maior oscilação mensal recalculada foi uma alta de 38,05% em outubro de 2020.”

“Comparando janeiro de 2015 com dezembro de 2024, a média cai 2,45%. Comparando as médias anuais de 2015 e 2024, sobe 3,62%. Isso mostra por que preciso dizer qual período e qual cálculo estou usando.”

**Se houver tempo:** apontar novembro de 2018 como maior média mensal e 2017 como maior dispersão das variações da gasolina. Não associar os picos a eventos reais sem uma fonte adequada.

### Demonstrar a interatividade · 1 minuto e 30 segundos

**Mostrar:** dashboard, abas Evolução e UFs e combustíveis.

1. Mostrar os valores iniciais de gasolina.
2. Mudar combustível para Diesel. Mostrar que médias, rankings e linha são recalculados.
3. Selecionar somente Sudeste: limpar Região e escolher Sudeste. As opções de UF acompanham a região.
4. Limpar o filtro UF, escolher RJ e observar a quantidade de registros e a evolução.
5. Mostrar a aba Dados e método e a opção de baixar o recorte em CSV.
6. Recarregar a página para voltar ao recorte inicial antes de discutir as correlações.

**Fala de apoio:** “Os seis filtros permitem mudar a pergunta sem alterar o código. A tabela mostra as observações que sustentam os indicadores.”

Não decorar os resultados do filtro de demonstração. Ler o valor exibido e confirmar o nome do combustível e a quantidade de observações.

### Recursos avançados · 1 minuto

**Mostrar:** seção 5 do notebook; aba Associações.

“Usei SQLAlchemy com SQLite para persistir a base. A assinatura do arquivo evita importar o mesmo conteúdo a cada acesso. O dashboard recebe os dados da consulta ao banco.”

“Também calculei correlação de Pearson. Na gasolina, o resultado com inflação é −0,0019 e com petróleo é −0,0317, ambos próximos de zero. Isso indica pouca associação linear na simulação, mas não prova que esses fatores não influenciam preços reais.”

**Lembrar:** Streamlit cache reduz recargas; SQLite armazena os dados. São funções diferentes.

### Fechamento · 40 segundos

**Mostrar:** conclusão do notebook e links da página.

“A análise encontrou diferenças entre UFs e oscilações mensais. O cuidado principal foi comparar períodos com a mesma regra e respeitar os limites da fonte. Aprendi a organizar a base, persistir os dados e construir um dashboard em que os indicadores acompanham os filtros.”

“Como continuidade, eu buscaria dados oficiais com unidades e cobertura documentadas. A entrega reúne código, notebook, CSV, página e dashboard.”

## 3. Se tiver somente 3 minutos

1. **30 s:** pergunta, simulação e cobertura da base.
2. **60 s:** gasolina, SP/DF, maior oscilação e diferença entre cálculo mensal e anual.
3. **60 s:** mudar um filtro, mostrar KPIs e citar SQLAlchemy/SQLite + Pearson.
4. **30 s:** limite das unidades, ausência de causalidade e conclusão.

## 4. Perguntas prováveis

**Por que escolheu gasolina como exemplo principal?**  
Para comparar UFs e períodos dentro de um combustível. O dashboard permite repetir a análise para os demais.

**Qual é a fórmula da variação mensal?**  
`(média do mês / média do mês anterior − 1) × 100`. Uso apenas meses consecutivos, sem preencher lacunas.

**Por que não usou diretamente variacao_mensal?**  
Esse campo veio com cada observação. Recalculei a mudança após agrupar o recorte, para que a medida acompanhe as médias exibidas.

**Por que não removeu as 503 ocorrências?**  
São observações com mesma data, UF e combustível, mas valores diferentes. A fonte não diz que a chave deve ser única. Removê-las descartaria dados sem justificativa.

**O preço médio é nacional?**  
É a média simples das observações disponíveis no recorte. Há 20 UFs e cobertura desigual; não afirmo representatividade oficial nacional.

**Qual combustível ficou mais caro?**  
Etanol tem o maior valor médio bruto registrado, R$ 5,56. Entre as médias anuais de 2015 e 2024, GLP teve a maior alta, 4,96%. São perguntas diferentes. Sem unidades físicas documentadas, o ranking bruto não indica economia.

**Por que não chamou tudo de preço por litro?**  
A fonte não documenta as unidades. Gasolina, GNV e GLP não devem ser presumidos como uma mesma unidade física.

**Correlação próxima de zero significa que não há relação?**  
Significa pouca associação linear nesses dados. Não exclui relação não linear e não é uma conclusão sobre dados reais. Correlação também não comprova causa.

**Por que usar SQLite e SQLAlchemy?**  
SQLite é leve e funciona sem servidor. SQLAlchemy organiza a conexão, transação e consulta. A carga usa assinatura para não repetir a importação do mesmo conteúdo.

**O banco fica salvo para sempre na nuvem?**  
Não. O serviço pode reiniciar e perder arquivos locais. O CSV versionado permite reconstruir o banco.

**Qual a diferença entre volatilidade e preço alto?**  
Preço alto é nível. Volatilidade mede dispersão das variações mensais; aqui usei desvio padrão amostral em pontos percentuais.

**Por que −2,45% e +3,62% para gasolina?**  
O primeiro compara dois meses: janeiro de 2015 e dezembro de 2024. O segundo compara médias de anos inteiros. Não são a mesma base de cálculo.

## 5. Se faltar internet

Abrir o notebook com as saídas já salvas e mostrar os oito gráficos. Usar os PNGs da pasta imagens/ se o visualizador de notebook não estiver disponível. Explicar a interatividade pelo código e pelo roteiro, deixando claro que a demonstração online depende da conexão.

## 6. Pontos que precisam estar claros

- Dados simulados, sem promessa de representar a economia real.
- Mesmo combustível e mesma regra de período para comparar.
- Média simples, cobertura desigual e quantidade de observações.
- Meses ausentes não geram variação mensal artificial.
- Correlação é associação, não causa.
- Notebook, código e documentos precisam ser revisados antes da entrega.

## Links

- Página: https://daflon-prognum.github.io/lasalle/projeto-precos-combustiveis/
- Dashboard: https://lasalle-combustiveis-g1.streamlit.app/
- Projeto: https://github.com/daflon-prognum/lasalle/tree/main/projeto-precos-combustiveis
