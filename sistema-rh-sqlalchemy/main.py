import pandas as pd
from sqlalchemy import (
    Column,
    Float,
    ForeignKey,
    Integer,
    MetaData,
    String,
    Table,
    create_engine,
    func,
    insert,
    select,
    text,
    update,
)
from sqlalchemy.orm import Mapped, declarative_base, mapped_column, relationship, sessionmaker
from typing import List


# ============================================================
# NÍVEL 1 — BÁSICO
# Configuração e SQL puro com segurança
# ============================================================

# Passo 1: conexão com o banco SQLite
engine = create_engine("sqlite:///sistema_rh.db")


# Passo 2: criação da tabela funcionarios utilizando SQL puro
with engine.begin() as conn:
    conn.execute(
        text(
            """
            CREATE TABLE IF NOT EXISTS funcionarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cargo TEXT NOT NULL,
                salario REAL NOT NULL
            )
            """
        )
    )


# Passo 3: inserção segura simulando dados vindos de formulário
# Os valores são enviados separadamente do comando SQL usando placeholders.
dados_formulario = {
    "nome": "Ana Souza",
    "cargo": "Desenvolvedor Júnior",
    "salario": 3500.00,
}

with engine.begin() as conn:
    comando_insert = text(
        """
        INSERT INTO funcionarios (nome, cargo, salario)
        VALUES (:nome, :cargo, :salario)
        """
    )
    conn.execute(comando_insert, dados_formulario)


# Dados adicionais para tornar os exemplos seguintes mais completos.
outros_funcionarios = [
    {
        "nome": "Carlos Lima",
        "cargo": "Desenvolvedor Júnior",
        "salario": 3800.00,
    },
    {
        "nome": "Mariana Alves",
        "cargo": "Desenvolvedor Sênior",
        "salario": 8500.00,
    },
    {
        "nome": "Pedro Santos",
        "cargo": "Analista de RH",
        "salario": 4500.00,
    },
]

with engine.begin() as conn:
    comando_insert = text(
        """
        INSERT INTO funcionarios (nome, cargo, salario)
        VALUES (:nome, :cargo, :salario)
        """
    )
    conn.execute(comando_insert, outros_funcionarios)


# Passo 4: validação utilizando Pandas
with engine.connect() as conn:
    df_funcionarios = pd.read_sql_query(
        text("SELECT * FROM funcionarios"),
        conn,
    )

print("\n=== FUNCIONÁRIOS CADASTRADOS ===")
print(df_funcionarios)


# ============================================================
# NÍVEL 2 — INTERMEDIÁRIO
# SQLAlchemy Core
# ============================================================

metadata = MetaData()


# Passo 1: definição programática da tabela projetos
projetos = Table(
    "projetos",
    metadata,
    Column("id", Integer, primary_key=True, autoincrement=True),
    Column("nome", String(100), nullable=False),
    Column("responsavel", String(100), nullable=False),
    Column("orcamento", Float, nullable=False),
)

metadata.create_all(engine)


# Passo 2: inserção em lote
lista_de_projetos = [
    {
        "nome": "Sistema de Folha de Pagamento",
        "responsavel": "Ana Souza",
        "orcamento": 50000.00,
    },
    {
        "nome": "Portal do Funcionário",
        "responsavel": "Carlos Lima",
        "orcamento": 30000.00,
    },
    {
        "nome": "Sistema de Recrutamento",
        "responsavel": "Mariana Alves",
        "orcamento": 75000.00,
    },
]

with engine.begin() as conn:
    conn.execute(insert(projetos), lista_de_projetos)


# Reflection: carrega a tabela criada com SQL puro como um objeto Table.
funcionarios = Table(
    "funcionarios",
    metadata,
    autoload_with=engine,
)


# Passo 3: reajuste de 10% para Desenvolvedor Júnior.
# O enunciado não informa um percentual específico; 10% foi usado como exemplo.
with engine.begin() as conn:
    comando_reajuste = (
        update(funcionarios)
        .where(funcionarios.c.cargo == "Desenvolvedor Júnior")
        .values(salario=funcionarios.c.salario * 1.10)
    )
    conn.execute(comando_reajuste)


print("\n=== APÓS REAJUSTE DE 10% ===")

with engine.connect() as conn:
    df_reajuste = pd.read_sql_query(select(funcionarios), conn)

print(df_reajuste)


# Passo 4: média salarial agrupada por cargo
consulta_media = (
    select(
        funcionarios.c.cargo,
        func.avg(funcionarios.c.salario).label("media_salarial"),
    )
    .group_by(funcionarios.c.cargo)
)

with engine.connect() as conn:
    relatorio_salarial = pd.read_sql_query(consulta_media, conn)

print("\n=== RELATÓRIO DE MÉDIA SALARIAL ===")
print(relatorio_salarial)


# ============================================================
# NÍVEL 3 — AVANÇADO
# ORM
# ============================================================

Base = declarative_base()


class Departamento(Base):
    __tablename__ = "departamentos"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True,
    )
    nome: Mapped[str] = mapped_column(
        String(100),
        nullable=False,
    )

    # Relacionamento Departamento -> Funcionários
    funcionarios: Mapped[List["FuncionarioORM"]] = relationship(
        back_populates="departamento"
    )

    def __repr__(self) -> str:
        return f"Departamento(id={self.id}, nome='{self.nome}')"


class FuncionarioORM(Base):
    # Usamos outra tabela para não conflitar com a tabela funcionarios
    # criada no Nível 1, que não possui a coluna departamento_id.
    __tablename__ = "funcionarios_orm"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True,
    )
    nome: Mapped[str] = mapped_column(
        String(100),
        nullable=False,
    )
    cargo: Mapped[str] = mapped_column(
        String(100),
        nullable=False,
    )
    salario: Mapped[float] = mapped_column(
        Float,
        nullable=False,
    )
    departamento_id: Mapped[int] = mapped_column(
        ForeignKey("departamentos.id"),
        nullable=False,
    )

    # Relacionamento Funcionário -> Departamento
    departamento: Mapped["Departamento"] = relationship(
        back_populates="funcionarios"
    )

    def __repr__(self) -> str:
        return (
            "FuncionarioORM("
            f"id={self.id}, "
            f"nome='{self.nome}', "
            f"cargo='{self.cargo}', "
            f"salario={self.salario}"
            ")"
        )


# Criação física das tabelas ORM.
Base.metadata.create_all(engine)


# Passo 3: criação da fábrica de sessões e persistência dos objetos.
SessionLocal = sessionmaker(bind=engine)
sessao = SessionLocal()

try:
    departamento_ti = Departamento(nome="TI")

    funcionario1 = FuncionarioORM(
        nome="João Silva",
        cargo="Desenvolvedor Backend",
        salario=7000.00,
    )
    funcionario2 = FuncionarioORM(
        nome="Maria Oliveira",
        cargo="Desenvolvedora Frontend",
        salario=6800.00,
    )
    funcionario3 = FuncionarioORM(
        nome="Lucas Pereira",
        cargo="Analista de Sistemas",
        salario=6200.00,
    )

    departamento_ti.funcionarios.append(funcionario1)
    departamento_ti.funcionarios.append(funcionario2)
    departamento_ti.funcionarios.append(funcionario3)

    sessao.add(departamento_ti)
    sessao.commit()

    # Passo 4: consulta orientada a objetos dos funcionários do TI.
    consulta_ti = (
        select(FuncionarioORM)
        .join(FuncionarioORM.departamento)
        .where(Departamento.nome == "TI")
    )

    funcionarios_ti = sessao.execute(consulta_ti).scalars().all()

    print("\n=== FUNCIONÁRIOS DO DEPARTAMENTO DE TI ===")

    for funcionario in funcionarios_ti:
        print(
            f"Nome: {funcionario.nome} | "
            f"Cargo: {funcionario.cargo} | "
            f"Salário: R$ {funcionario.salario:.2f}"
        )

finally:
    sessao.close()
