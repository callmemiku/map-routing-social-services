"""
Чтение и предобработка таблиц с датафреймами на выходе.
"""
from datetime import datetime
import pickle

import pandas as pd

from .paths import (
    RAW_FILES, NPY_FILES,
    DENIALS, EVENT_DIR, EVENTS1, EVENTS2, BTI, ODPU, ASUPR, CONNECTIONS, ADDRESSES,
)


def dateparse(x):
    if isinstance(x, str):
        return datetime.strptime(x, '%Y-%m-%d %H:%M:%S.%f')
    # Some rows contain errors
    return 0


def read_denials() -> pd.DataFrame:
    assert (RAW_FILES / DENIALS).exists()
    assert (RAW_FILES / DENIALS).is_file()
    df = pd.read_excel(
        RAW_FILES / DENIALS,
        parse_dates=[
            'Дата регистрации отключения',
            'Планируемая дата отключения',
            'Планируемая дата включения',
            'Фактическая дата отключения',
            'Фактическая дата включения',
        ], date_parser=dateparse)
    return df


def _read_events(raw_fname: str, pickle_fname: str, parse_dates: list) -> pd.DataFrame:
    prepared_fname = NPY_FILES / pickle_fname
    if prepared_fname.exists():
        with open(prepared_fname, "rb") as f:
            return pickle.load(f)

    fname = RAW_FILES / EVENT_DIR / raw_fname
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, parse_dates=parse_dates, date_parser=dateparse, sheet_name="Выгрузка")
    with open(prepared_fname, "wb") as f:
        pickle.dump(df, f)
    return df


def read_events() -> (pd.DataFrame, pd.DataFrame):
    # first time: 1m23s
    # after:      10s
    df1 = _read_events(EVENTS1, "events1.pkl", [
        "Дата создания во внешней системе",
        "Дата закрытия",
        "Дата и время завершения события",
    ])
    df2 = _read_events(EVENTS2, "events2.pkl", [
        "Дата создания во внешней системе",
        "Дата закрытия",
        "Дата и время завершения события во внешней системе",
    ])
    return df1, df2


def read_bti() -> pd.DataFrame:
    fname = RAW_FILES / BTI
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, header=1)
    df.rename(columns={
        'Unnamed: 0': 'N',
        'Unnamed: 11': 'UNOM',
        'Unnamed: 12': 'UNAD',
    }, inplace=True)
    return df


def read_odpu() -> (pd.DataFrame, pd.DataFrame):
    fname = RAW_FILES / ODPU
    assert fname.exists() and fname.is_file()
    df1 = pd.read_excel(fname, sheet_name="Sheet 1")
    df2 = pd.read_excel(fname, sheet_name="Sheet 2")
    return df1, df2


def read_asupr() -> pd.DataFrame:
    fname = RAW_FILES / ASUPR
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, sheet_name="Связь ЦТП - Потребитель -ОДС")
    return df


def read_conn() -> pd.DataFrame:
    fname = RAW_FILES / CONNECTIONS
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname)
    disp = 'Диспетчеризация'
    # там честные 'да' и 'нет', ничего лишнего
    df[disp] = df[disp] == 'да'
    return df


def read_addr() -> pd.DataFrame:
    # fist time: 3m
    # after:     11s
    prepared_fname = NPY_FILES / "addr.pkl"
    if prepared_fname.exists():
        with open(prepared_fname, "rb") as f:
            return pickle.load(f)

    fname = RAW_FILES / ADDRESSES
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, skiprows=[1])
    with open(prepared_fname, "wb") as f:
        pickle.dump(df, f)
    return df
