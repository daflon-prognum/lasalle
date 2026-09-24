# Sistema de RH com SQLAlchemy

Atividade prática desenvolvida em Python para demonstrar três formas de interação com banco de dados usando **SQLAlchemy**:

1. SQL puro com parâmetros seguros;
2. SQLAlchemy Core;
3. ORM (Object-Relational Mapping).

O projeto utiliza **SQLite** como banco local e **Pandas** para exibição de consultas.

## Requisitos

- Python 3.10 ou superior;
- SQLAlchemy 2.x;
- Pandas.

## Instalação

Clone o repositório e entre na pasta do projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd sistema-rh-sqlalchemy
```

Crie um ambiente virtual:

```bash
python -m venv .venv
```

Ative o ambiente virtual.

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

Execute o projeto:

```bash
python main.py
```

Ao executar o programa, o arquivo `sistema_rh.db` será criado automaticamente no diretório do projeto.

---

## Nível 1 — SQL puro com segurança

Nesta etapa é utilizada a conexão:

```python
engine = create_engine("sqlite:///sistema_rh.db")
```

A tabela `funcionarios` é criada através de SQL nativo executado com `text()`.

Exemplo:

```python
with engine.begin() as conn:
    conn.execute(
        text("""
            CREATE TABLE IF NOT EXISTS funcionarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cargo TEXT NOT NULL,
                salario REAL NOT NULL
            )
        """)
    )
```

### Inserção segura

Os dados simulados de um formulário são enviados separadamente do comando SQL:

```python
dados_formulario = {
    "nome": "Ana Souza",
    "cargo": "Desenvolvedor Júnior",
    "salario": 3500.00,
}
```

O comando utiliza placeholders:

```python
INSERT INTO funcionarios (nome, cargo, salario)
VALUES (:nome, :cargo, :salario)
```

E a execução é feita da seguinte maneira:

```python
conn.execute(comando_insert, dados_formulario)
```

### Por que não concatenar strings no SQL?

Nunca devemos concatenar diretamente valores recebidos do usuário em comandos SQL porque isso pode permitir ataques de **SQL Injection**.

Um exemplo inseguro seria:

```python
sql = f"SELECT * FROM funcionarios WHERE nome = '{nome}'"
```

Nesse caso, o conteúdo de `nome` passa a fazer parte do próprio comando SQL. Um usuário mal-intencionado poderia fornecer um valor preparado para modificar a lógica da consulta.

Ao utilizar parâmetros como `:nome`, o SQLAlchemy mantém o comando SQL separado dos valores enviados pelo usuário. Dessa forma, os valores são tratados como dados, e não como trechos executáveis de SQL.

### Consulta com Pandas

A validação dos registros é feita utilizando:

```python
pd.read_sql_query()
```

O resultado é retornado diretamente como um `DataFrame`.

---

## Nível 2 — SQLAlchemy Core

Nesta etapa deixamos de escrever os comandos SQL manualmente e utilizamos objetos Python oferecidos pelo SQLAlchemy.

### Criação da tabela projetos

A tabela é definida utilizando `Table`, `MetaData` e `Column`:

```python
projetos = Table(
    "projetos",
    metadata,
    Column("id", Integer, primary_key=True, autoincrement=True),
    Column("nome", String(100), nullable=False),
    Column("responsavel", String(100), nullable=False),
    Column("orcamento", Float, nullable=False),
)
```

Depois, a estrutura é criada fisicamente no banco:

```python
metadata.create_all(engine)
```

### Bulk insert

Uma lista de dicionários é enviada de uma única vez:

```python
conn.execute(insert(projetos), lista_de_projetos)
```

### Reflection

Como a tabela `funcionarios` foi criada anteriormente através de SQL puro, utilizamos reflection para carregá-la como um objeto `Table`:

```python
funcionarios = Table(
    "funcionarios",
    metadata,
    autoload_with=engine,
)
```

Isso permite utilizar recursos do SQLAlchemy Core sobre uma tabela que já existe no banco.

### Reajuste salarial

Foi utilizado um reajuste de **10%** como exemplo, pois o enunciado não especifica um percentual:

```python
update(funcionarios) \
    .where(funcionarios.c.cargo == "Desenvolvedor Júnior") \
    .values(salario=funcionarios.c.salario * 1.10)
```

### Relatório salarial

A média salarial é calculada por cargo usando `func.avg()` e `group_by()`:

```python
consulta_media = (
    select(
        funcionarios.c.cargo,
        func.avg(funcionarios.c.salario).label("media_salarial"),
    )
    .group_by(funcionarios.c.cargo)
)
```

---

## Nível 3 — ORM

No ORM, as tabelas passam a ser representadas por classes Python.

Foram criadas as classes:

- `Departamento`;
- `FuncionarioORM`.

### Relacionamento

Cada funcionário pertence a um departamento através da chave estrangeira:

```python
departamento_id: Mapped[int] = mapped_column(
    ForeignKey("departamentos.id")
)
```

O relacionamento entre as classes é feito com `relationship()` e `back_populates`.

No departamento:

```python
funcionarios: Mapped[List["FuncionarioORM"]] = relationship(
    back_populates="departamento"
)
```

No funcionário:

```python
departamento: Mapped["Departamento"] = relationship(
    back_populates="funcionarios"
)
```

Dessa forma é possível navegar diretamente entre os objetos.

Exemplo:

```python
departamento_ti.funcionarios.append(funcionario1)
```

### Sessão

A fábrica de sessões é criada com:

```python
SessionLocal = sessionmaker(bind=engine)
```

Os objetos são persistidos através de:

```python
sessao.add(departamento_ti)
sessao.commit()
```

### Consulta orientada a objetos

Para buscar os funcionários do departamento de TI:

```python
consulta_ti = (
    select(FuncionarioORM)
    .join(FuncionarioORM.departamento)
    .where(Departamento.nome == "TI")
)

funcionarios_ti = sessao.execute(consulta_ti).scalars().all()
```

O uso de `scalars()` faz com que o resultado seja composto por objetos da classe `FuncionarioORM`, e não apenas por linhas do banco de dados.

Ao final, a sessão é fechada corretamente:

```python
sessao.close()
```

---

## Estrutura do projeto

```text
sistema-rh-sqlalchemy/
├── .gitignore
├── main.py
├── README.md
└── requirements.txt
```

O banco `sistema_rh.db` é criado somente durante a execução e está incluído no `.gitignore`, portanto não será enviado ao Git.

## Observação sobre as tabelas do ORM

A classe `FuncionarioORM` utiliza a tabela `funcionarios_orm` em vez da tabela `funcionarios` criada no primeiro nível.

Isso acontece porque a tabela inicial contém apenas:

```text
id
nome
cargo
salario
```

Enquanto o relacionamento ORM exige também uma coluna de chave estrangeira:

```text
departamento_id
```

O método `create_all()` não altera automaticamente tabelas já existentes para adicionar novas colunas. Em uma aplicação real, essa alteração normalmente seria feita através de uma ferramenta de migração, como o Alembic.

## Tecnologias utilizadas

- Python
- SQLite
- SQLAlchemy
- Pandas
