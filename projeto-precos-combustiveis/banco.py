"""Importação idempotente do CSV e leitura SQL, sem eliminar observações."""
from hashlib import sha256
from pathlib import Path
import pandas as pd
from sqlalchemy import create_engine, text

def carregar_banco(csv_path, db_path):
    csv_path, db_path = Path(csv_path), Path(db_path)
    db_path.parent.mkdir(parents=True, exist_ok=True)
    assinatura = sha256(csv_path.read_bytes()).hexdigest()
    engine = create_engine(f'sqlite:///{db_path}', connect_args={'timeout': 30})
    try:
        with engine.begin() as conn:
            conn.execute(text('CREATE TABLE IF NOT EXISTS origem (id INTEGER PRIMARY KEY, sha256 TEXT NOT NULL)'))
            anterior = conn.execute(text('SELECT sha256 FROM origem WHERE id = 1')).scalar()
            tabela_existe = conn.execute(text("SELECT name FROM sqlite_master WHERE type='table' AND name='observacoes' ")).scalar()
            if assinatura != anterior or not tabela_existe:
                base = pd.read_csv(csv_path, encoding='utf-8-sig')
                base.index = range(1, len(base) + 1)
                base.to_sql('observacoes', conn, if_exists='replace', index=True, index_label='id_observacao')
                conn.execute(text('CREATE UNIQUE INDEX IF NOT EXISTS idx_observacao ON observacoes(id_observacao)'))
                conn.execute(text('DELETE FROM origem'))
                conn.execute(text('INSERT INTO origem (id, sha256) VALUES (1, :hash)'), {'hash': assinatura})
            # O dashboard recebe os dados lidos do SQLite, e não diretamente do CSV.
            return pd.read_sql_query(text('SELECT * FROM observacoes ORDER BY data, uf, combustivel, id_observacao'), conn)
    finally:
        engine.dispose()
