"""Preparação, indicadores e gráficos utilizados no dashboard."""
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

MESES = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']
COR = '#007F78'

def preparar(base):
    dados = base.copy()
    dados.columns = dados.columns.str.strip()
    obrigatorias = ['ano', 'mes', 'data', 'regiao', 'uf', 'combustivel', 'preco_medio',
                    'preco_minimo', 'preco_maximo', 'variacao_mensal', 'inflacao',
                    'cotacao_petroleo', 'consumo_estimado', 'nivel_preco']
    faltantes = set(obrigatorias) - set(dados.columns)
    if faltantes:
        raise ValueError(f'Colunas ausentes: {sorted(faltantes)}')
    if dados[obrigatorias].isna().any().any():
        raise ValueError('A base contém valores ausentes. Revise a origem antes da análise.')
    dados['data'] = pd.to_datetime(dados['data'], errors='raise')
    for coluna in ['regiao', 'uf', 'combustivel', 'nivel_preco']:
        dados[coluna] = dados[coluna].str.strip()
    for coluna in set(obrigatorias) - {'data', 'regiao', 'uf', 'combustivel', 'nivel_preco'}:
        dados[coluna] = pd.to_numeric(dados[coluna], errors='raise')
        if not np.isfinite(dados[coluna]).all():
            raise ValueError(f'Valor não finito em {coluna}.')
    valido = ((dados['data'].dt.year == dados['ano']) & (dados['data'].dt.month == dados['mes'])
              & (dados['preco_minimo'] > 0) & (dados['preco_minimo'] <= dados['preco_medio'])
              & (dados['preco_medio'] <= dados['preco_maximo']) & (dados['consumo_estimado'] >= 0))
    if not valido.all():
        raise ValueError('Há datas ou intervalos de preço inconsistentes.')
    # Chaves repetidas não significam linhas idênticas: as observações são preservadas.
    dados['amplitude_preco'] = dados['preco_maximo'] - dados['preco_minimo']
    dados['amplitude_pct'] = dados['amplitude_preco'] / dados['preco_medio'] * 100
    return dados.sort_values(['data', 'uf', 'combustivel']).reset_index(drop=True)

def serie_mensal(dados):
    if dados.empty:
        return pd.DataFrame(columns=['preco_medio', 'n', 'variacao_calculada'])
    mensal = dados.groupby(dados['data'].dt.to_period('M')).agg(
        preco_medio=('preco_medio', 'mean'), n=('preco_medio', 'size'))
    calendario = pd.period_range(mensal.index.min(), mensal.index.max(), freq='M')
    mensal = mensal.reindex(calendario)
    # fill_method=None evita preencher lacunas ou comparar meses não consecutivos.
    mensal['variacao_calculada'] = mensal['preco_medio'].pct_change(fill_method=None) * 100
    mensal.index = mensal.index.to_timestamp()
    mensal.index.name = 'data'
    return mensal

def ranking(dados, campo):
    return dados.groupby(campo).agg(preco_medio=('preco_medio', 'mean'),
                                   n=('preco_medio', 'size')).sort_values('preco_medio', ascending=False)

def correlacao(dados, coluna):
    pares = dados[['preco_medio', coluna]].dropna()
    n = len(pares)
    if n < 3 or pares.nunique().min() < 2:
        return np.nan, n
    return pares['preco_medio'].corr(pares[coluna]), n

def moeda(valor):
    return f'R$ {valor:,.2f}'.replace(',', '_').replace('.', ',').replace('_', '.')

def numero(valor, casas=0):
    return f'{valor:,.{casas}f}'.replace(',', '_').replace('.', ',').replace('_', '.')

def estilo():
    sns.set_theme(style='whitegrid', palette=['#007F78', '#ED9A37', '#405F91', '#9869A6', '#D46152'])
    plt.rcParams.update({'axes.spines.top': False, 'axes.spines.right': False,
                         'axes.titleweight': 'bold', 'figure.dpi': 110})

def linha(dados, combustivel):
    serie = serie_mensal(dados)
    fig, ax = plt.subplots(figsize=(10, 4.3), layout='constrained')
    ax.plot(serie.index, serie['preco_medio'], color=COR, linewidth=2)
    ax.set(title=f'{combustivel}: preço médio mensal', xlabel='Período', ylabel='Preço informado (R$)')
    return fig

def barras(dados, campo, titulo):
    tabela = ranking(dados, campo).sort_values('preco_medio')
    fig, ax = plt.subplots(figsize=(10, max(3.5, len(tabela) * .28)), layout='constrained')
    ax.barh(tabela.index, tabela['preco_medio'], color=COR)
    ax.bar_label(ax.containers[0], fmt='%.2f', padding=3, fontsize=9)
    ax.set(title=titulo, xlabel='Preço médio informado (R$)', ylabel='')
    ax.set_xlim(0, tabela['preco_medio'].max() * 1.15)
    return fig

def mapa_mensal(dados, combustivel):
    tabela = dados.pivot_table(index='ano', columns='mes', values='preco_medio', aggfunc='mean')
    tabela = tabela.reindex(columns=range(1, 13))
    fig, ax = plt.subplots(figsize=(11, max(3, len(tabela) * .5)), layout='constrained')
    sns.heatmap(tabela, annot=True, fmt='.2f', cmap='YlOrRd', mask=tabela.isna(),
                cbar_kws={'label': 'Preço informado (R$)'}, ax=ax, xticklabels=MESES)
    ax.set(title=f'{combustivel}: médias por ano e mês', xlabel='Mês', ylabel='Ano')
    return fig

def dispersao(dados, coluna, combustivel):
    rotulo = {'inflacao': 'Inflação informada na simulação',
              'cotacao_petroleo': 'Cotação do petróleo informada na simulação'}[coluna]
    r, n = correlacao(dados, coluna)
    fig, ax = plt.subplots(figsize=(9, 4.5), layout='constrained')
    sns.scatterplot(data=dados, x=coluna, y='preco_medio', hue='regiao', alpha=.65, s=32, ax=ax)
    ax.set(title=f'{combustivel}: associação com {coluna.replace("_", " ")} | r={r:.3f}, n={n}',
           xlabel=rotulo, ylabel='Preço informado (R$)')
    ax.legend(title='Região', fontsize=8)
    return fig
