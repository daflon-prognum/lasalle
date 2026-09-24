import numpy as np

print("=" * 72)
print("ATIVIDADE 1 — ANÁLISE DE DADOS NO VAREJO COM NUMPY")
print("=" * 72)

# NÍVEL 1
pecas_vendidas = np.array([150, 120, 90, 210, 300, 250, 180])
faturamento_diario = pecas_vendidas * 50
mascara_pico = pecas_vendidas > 200
dias_pico = pecas_vendidas[mascara_pico]

print("\nNÍVEL 1 — Faturamento Semanal e Filtros")
print("Peças vendidas:", pecas_vendidas)
print("Faturamento diário:", faturamento_diario)
print("Máscara de pico:", mascara_pico)
print("Dias com mais de 200 peças:", dias_pico)

# NÍVEL 2
vendas_filiais = np.array([
    [200, 220, np.nan, 250],
    [150, 180, 160, 190],
    [300, 310, 290, 330]
], dtype=float)

total_por_loja = np.nansum(vendas_filiais, axis=1)
media_diaria_rede = np.nanmean(vendas_filiais)

situacao_metas = np.where(
    vendas_filiais >= 200,
    "Meta Atingida",
    "Abaixo"
)
situacao_metas = np.where(
    np.isnan(vendas_filiais),
    "Sem dado",
    situacao_metas
)

print("\nNÍVEL 2 — Múltiplas Filiais e Falhas de Sistema")
print("Matriz de vendas:\n", vendas_filiais)
print("Total por loja:", total_por_loja)
print("Média geral ignorando NaN:", round(float(media_diaria_rede), 2))
print("Situação das metas:\n", situacao_metas)

# NÍVEL 3
rng = np.random.default_rng(seed=42)
vendas_clientes = rng.choice(
    np.array(["Eletrônicos", "Roupas", "Casa"]),
    size=50
)

categorias_unicas, contagens = np.unique(
    vendas_clientes,
    return_counts=True
)

print("\nNÍVEL 3 — Categorização e Destaques de Marketing")
print("Volume de vendas por departamento:")
for categoria, quantidade in zip(categorias_unicas, contagens):
    print(f"- {categoria}: {quantidade}")

faturamento_campanhas = np.array(
    [12000, 45000, 23000, 89000, 31000]
)
indice_campanha_campea = np.argmax(faturamento_campanhas)

print("Faturamento das campanhas:", faturamento_campanhas)
print("Índice da campanha campeã:", indice_campanha_campea)
print("Campanha campeã (contagem humana):", indice_campanha_campea + 1)
print("Maior faturamento:", faturamento_campanhas[indice_campanha_campea])

print("\n" + "=" * 72)
print("ATIVIDADE 2 — ORGANIZAÇÃO E TRANSFORMAÇÃO DE DADOS COM NUMPY")
print("=" * 72)

# NÍVEL 1
dias_mes = np.arange(1, 31)
metas_vendas = np.linspace(20000, 30000, 5)
estoque_decimal = np.array([10.5, 20.1, 30.9])
estoque_inteiro = estoque_decimal.astype(int)
ultimos_cinco_invertidos = dias_mes[-5:][::-1]

print("\nNÍVEL 1 — Geração de Sequências e Tipagem")
print("Dias do mês:", dias_mes)
print("Metas de vendas:", metas_vendas)
print("Estoque convertido para inteiro:", estoque_inteiro)
print("Últimos 5 dias em ordem inversa:", ultimos_cinco_invertidos)

# NÍVEL 2
visitas_anuais = np.array([
    12000, 13500, 12800,
    14200, 15000, 15800,
    16500, 17200, 16900,
    18000, 19500, 21000
])

visitas_trimestres = visitas_anuais.reshape(4, 3)

primeiro_semestre = np.array([
    [120, 135, 128],
    [142, 150, 158]
])

segundo_semestre = np.array([
    [165, 172, 169],
    [180, 195, 210]
])

ano_completo = np.vstack([
    primeiro_semestre,
    segundo_semestre
])

primeiro_trimestre_copia = visitas_trimestres[0].copy()
copia_demonstracao = primeiro_trimestre_copia.copy()
copia_demonstracao[0] = 99999
relatorio_corrido = visitas_trimestres.ravel()

print("\nNÍVEL 2 — Redimensionamento e Proteção de Dados")
print("Visitas por trimestre:\n", visitas_trimestres)
print("Matrizes empilhadas:\n", ano_completo)
print("Primeiro trimestre copiado:", primeiro_trimestre_copia)
print("Cópia alterada:", copia_demonstracao)
print("Original preservado:\n", visitas_trimestres)
print("Matriz achatada:", relatorio_corrido)

# NÍVEL 3
pontuacoes = np.array([85, 92, 78, 95, 88])
indices_crescentes = np.argsort(pontuacoes)
ranking_decrescente = np.argsort(pontuacoes)[::-1]

print("\nNÍVEL 3 — Ranking, Broadcasting e Sistemas Lineares")
print("Pontuações:", pontuacoes)
print("Índices em ordem crescente:", indices_crescentes)
print("Ranking por índices (maior para menor):", ranking_decrescente)
print("Pontuações em ranking:", pontuacoes[ranking_decrescente])

precos_base = np.array([100, 200, 300])
precos_coluna = precos_base[:, np.newaxis]
percentuais_desconto = np.array([0.0, 0.10, 0.20])
tabela_precos_com_desconto = (
    precos_coluna * (1 - percentuais_desconto)
)

print("Preços em coluna:\n", precos_coluna)
print("Tabela com descontos via broadcasting:\n", tabela_precos_com_desconto)

A = np.array([
    [2, 1],
    [1, -1]
], dtype=float)

b = np.array([500, 100], dtype=float)

solucao = np.linalg.solve(A, b)
valor_placa, valor_sensor = solucao

print("Matriz A:\n", A)
print("Vetor b:", b)
print("Solução:", solucao)
print(f"Valor da placa robótica: R$ {valor_placa:.2f}")
print(f"Valor do sensor: R$ {valor_sensor:.2f}")

print("\n" + "=" * 72)
print("ATIVIDADES CONCLUÍDAS")
print("=" * 72)
