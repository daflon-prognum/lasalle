"""Projeto G1 de Guilherme Daflon Goulart Costa - Tema 11."""
from pathlib import Path
import matplotlib.pyplot as plt
import numpy as np
import streamlit as st
from analise import (preparar, serie_mensal, ranking, correlacao, moeda, numero,
                     estilo, linha, barras, mapa_mensal, dispersao, MESES)
from banco import carregar_banco

PASTA = Path(__file__).resolve().parent
REPO = 'https://github.com/daflon-prognum/lasalle/tree/main/projeto-precos-combustiveis'
PAGINA = 'https://daflon-prognum.github.io/lasalle/projeto-precos-combustiveis/'
st.set_page_config(page_title='Combustíveis no Brasil | Guilherme Daflon', page_icon='⛽', layout='wide')
estilo()

@st.cache_data(show_spinner='Preparando os dados do SQLite...')
def carregar(assinatura):
    return preparar(carregar_banco(PASTA / 'dados/simulacao_precos_combustiveis_brasil.csv',
                                  PASTA / 'database/combustiveis.db'))

csv = PASTA / 'dados/simulacao_precos_combustiveis_brasil.csv'
try:
    dados = carregar(csv.stat().st_mtime_ns)
except (ValueError, OSError) as erro:
    st.error(f'Não foi possível carregar a base: {erro}')
    st.stop()

st.caption('G1 · LINGUAGENS DE PROGRAMAÇÃO · TEMA 11')
st.title('Combustíveis no Brasil')
st.markdown('**Evolução dos preços e diferenças entre UFs, de 2015 a 2024**')
st.caption('Guilherme Daflon Goulart Costa · Base simulada fornecida pelo professor Alexandre Louzada')
st.write('Onde estão os maiores preços e em quais períodos as médias mais oscilaram? Escolha um combustível e explore o recorte.')
st.info('Dados simulados de 20 UFs. Os preços são médias das observações; as unidades físicas não foram documentadas na fonte.')

with st.sidebar:
    st.header('Seu recorte')
    combustivel = st.selectbox('Combustível', sorted(dados['combustivel'].unique()),
                               index=sorted(dados['combustivel'].unique()).index('Gasolina'))
    anos = st.multiselect('Ano', sorted(dados['ano'].unique()), default=sorted(dados['ano'].unique()))
    meses = st.multiselect('Mês', list(range(1, 13)), default=list(range(1, 13)), format_func=lambda x: MESES[x-1])
    regioes = st.multiselect('Região', sorted(dados['regiao'].unique()), default=sorted(dados['regiao'].unique()))
    opcoes_uf = sorted(dados.loc[dados['regiao'].isin(regioes), 'uf'].unique())
    ufs = st.multiselect('UF', opcoes_uf, default=opcoes_uf)
    niveis = st.multiselect('Nível de preço', ['Baixo', 'Médio', 'Alto', 'Crítico'], default=['Baixo', 'Médio', 'Alto', 'Crítico'])
    st.caption('Os filtros atualizam todos os indicadores. Sem seleção, o recorte fica vazio.')
    st.divider()
    st.page_link(PAGINA, label='Página do projeto', icon='🌐')
    st.page_link(REPO, label='Código e notebook', icon='📁')

# Este contexto conserva os outros cinco filtros para a comparação entre combustíveis.
contexto = dados.loc[dados['ano'].isin(anos) & dados['mes'].isin(meses)
                     & dados['regiao'].isin(regioes) & dados['uf'].isin(ufs)
                     & dados['nivel_preco'].isin(niveis)]
recorte = contexto.loc[contexto['combustivel'] == combustivel].copy()
if recorte.empty:
    st.warning('Nenhuma observação atende aos filtros. Amplie o período ou selecione outras UFs e níveis de preço.')
    st.stop()

mensal = serie_mensal(recorte)
por_uf = ranking(recorte, 'uf')
por_regiao = ranking(recorte, 'regiao')
mudancas = mensal['variacao_calculada'].dropna()
maior_data = mudancas.abs().idxmax() if len(mudancas) else None
primeiro, ultimo = mensal['preco_medio'].dropna().iloc[[0, -1]]
mudanca_periodo = (ultimo / primeiro - 1) * 100 if mensal['preco_medio'].notna().sum() > 1 else np.nan
st.subheader(f'{combustivel} · {numero(len(recorte))} observações')
st.caption(f'{recorte.data.min():%m/%Y} a {recorte.data.max():%m/%Y} · {recorte.uf.nunique()} UFs · '
           f'{recorte.data.dt.to_period("M").nunique()} meses com dados. Médias simples, sem ponderação por consumo.')

c1, c2, c3 = st.columns(3)
c1.metric('Preço médio do recorte', moeda(recorte['preco_medio'].mean()))
c2.metric('UF com maior média', por_uf.index[0])
c2.caption(moeda(por_uf.iloc[0]['preco_medio']))
c3.metric('Região com maior média', por_regiao.index[0])
c3.caption(moeda(por_regiao.iloc[0]['preco_medio']))
c4, c5, c6 = st.columns(3)
c4.metric('Maior oscilação mensal', f'{numero(mudancas.loc[maior_data], 2)}%' if maior_data is not None else 'Sem pares')
if maior_data is not None:
    c4.caption(maior_data.strftime('%m/%Y'))
c5.metric('Consumo estimado · soma', numero(recorte['consumo_estimado'].sum()), help='Soma somente deste combustível. Unidade não informada na base; observações repetidas por chave são preservadas.')
c6.metric('Primeiro × último mês', f'{numero(mudanca_periodo, 2)}%' if np.isfinite(mudanca_periodo) else 'Sem comparação',
          help='Variação entre a média do primeiro e do último mês disponíveis. A composição das UFs pode mudar.')

def mostrar(fig):
    st.pyplot(fig)
    plt.close(fig)

aba_tempo, aba_lugares, aba_economia, aba_dados = st.tabs(['Evolução', 'UFs e combustíveis', 'Associações', 'Dados e método'])
with aba_tempo:
    mostrar(linha(recorte, combustivel))
    st.write(f'No recorte, a maior oscilação entre meses consecutivos foi {numero(mudancas.loc[maior_data], 2)}% em {maior_data:%m/%Y}.'
             if maior_data is not None else 'São necessários dois meses consecutivos para calcular a variação mensal.')
    st.caption('Variação recalculada: (média do mês / média do mês anterior − 1) × 100. Meses sem dados interrompem a série.')
    mostrar(mapa_mensal(recorte, combustivel))
    if len(mudancas) >= 2:
        st.write(f'A dispersão das variações mensais é {numero(mudancas.std(), 2)} pontos percentuais '
                 f'(desvio padrão amostral de {len(mudancas)} variações).')
    st.dataframe(mensal.rename(columns={'preco_medio':'Preço médio (R$)', 'n':'Observações',
                                       'variacao_calculada':'Variação recalculada (%)'}), width='stretch')

with aba_lugares:
    mostrar(barras(recorte, 'uf', f'{combustivel}: média por UF'))
    mostrar(barras(recorte, 'regiao', f'{combustivel}: média por região'))
    st.write(f'{por_uf.index[0]} apresentou a maior média e {por_uf.index[-1]} a menor no recorte. '
             'O número de observações e a cobertura dos meses diferem entre UFs.')
    st.dataframe(por_uf.rename(columns={'preco_medio':'Preço médio (R$)', 'n':'Observações'}), width='stretch')
    st.subheader('Valores informados por combustível')
    st.caption('Esta comparação mantém ano, mês, região, UF e nível de preço. O filtro de combustível é aberto apenas neste gráfico.')
    mostrar(barras(contexto, 'combustivel', 'Preço médio registrado por combustível'))
    combustiveis = ranking(contexto, 'combustivel')
    st.write(f'O maior valor médio registrado é de {combustiveis.index[0]}: {moeda(combustiveis.iloc[0].preco_medio)}.')
    st.warning('A fonte não informa uma unidade física comum. O gráfico não permite concluir qual combustível oferece mais economia.')
    anual = contexto.groupby(['ano', 'combustivel']).preco_medio.mean().unstack('combustivel')
    if len(anual) > 1:
        ano_inicial, ano_final = anual.index.min(), anual.index.max()
        crescimento = ((anual.loc[ano_final] / anual.loc[ano_inicial] - 1) * 100).dropna().sort_values(ascending=False)
        st.subheader('Evolução relativa das médias anuais')
        indice = anual.div(anual.loc[ano_inicial]).mul(100)
        fig, ax = plt.subplots(figsize=(10, 4.5), layout='constrained')
        indice.plot(ax=ax, marker='o')
        ax.set(title=f'Média de {ano_inicial} = 100', xlabel='Ano', ylabel='Índice do preço informado')
        ax.legend(title='Combustível', fontsize=8, ncol=3)
        mostrar(fig)
        if not crescimento.empty:
            st.write(f'De {ano_inicial} a {ano_final}, {crescimento.index[0]} apresentou a maior variação entre médias anuais: '
                     f'{numero(crescimento.iloc[0], 2)}%. A comparação relativa mede evolução, sem equiparar unidades físicas.')
            st.dataframe(crescimento.rename('Variação entre médias anuais (%)'), width='stretch')
        st.caption('Essa medida compara médias anuais. O KPI principal compara o primeiro e o último mês disponíveis; os resultados podem diferir.')

with aba_economia:
    st.write('A correlação de Pearson mede a associação linear nas observações do combustível selecionado.')
    for campo in ['inflacao', 'cotacao_petroleo']:
        r, n = correlacao(recorte, campo)
        if np.isfinite(r):
            mostrar(dispersao(recorte, campo, combustivel))
            st.caption(f'r = {numero(r, 3)} · n = {n} pares. Valores de −1 a +1; próximo de zero indica pouca associação linear.')
        else:
            st.info(f'Correlação com {campo.replace("_", " ")} indisponível: poucos pares ou ausência de variação.')
    st.warning('Inflação e petróleo variam entre registros do mesmo mês. São atributos simulados, não séries oficiais. Correlação não comprova causa.')

with aba_dados:
    st.subheader('Tabela do recorte')
    st.dataframe(recorte, width='stretch', hide_index=True)
    st.download_button('Baixar recorte em CSV', recorte.to_csv(index=False).encode('utf-8-sig'),
                       f'combustiveis_{combustivel.lower()}_recorte.csv', 'text/csv')
    with st.expander('Como os dados foram preparados', expanded=True):
        st.write('Datas e tipos conferidos; preços positivos e dentro do intervalo mínimo/máximo. '
                 'A base original não possui valores ausentes nem linhas inteiras duplicadas. '
                 'As 503 ocorrências adicionais com a mesma data, UF e combustível foram preservadas porque os valores diferem.')
        st.write('CSV → importação por assinatura SHA-256 → SQLite → consulta com SQLAlchemy → Pandas → filtros. '
                 'O cache evita repetir a carga a cada interação. O banco pode ser reconstruído a partir do CSV após reiniciar o serviço.')
        st.write('A coluna variacao_mensal é fornecida pela fonte. A análise temporal usa variacao_calculada, '
                 'obtida das médias de meses consecutivos. Não há ajuste por inflação.')

st.divider()
st.subheader('Leitura do recorte')
sentido = 'aumentou' if mudanca_periodo > 0 else 'diminuiu'
st.write(f'A média mensal {sentido} {numero(abs(mudanca_periodo), 2)}% entre o primeiro e o último mês disponível. '
         f'{por_uf.index[0]} e {por_regiao.index[0]} tiveram as maiores médias. '
         'Esses resultados descrevem o recorte da simulação e devem ser lidos junto à cobertura das observações.'
         if np.isfinite(mudanca_periodo) else 'O recorte tem apenas um mês disponível. Amplie o período para discutir a evolução dos preços.')
st.caption('Projeto individual de Guilherme Daflon Goulart Costa · LaSalle · Apresentação presencial: 08/10/2026')
