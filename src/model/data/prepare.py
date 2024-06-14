"""
Чтение и предобработка таблиц с датафреймами на выходе.
"""
from datetime import datetime
import pickle

import pandas as pd

from .paths import (
    RAW_FILES, NPY_FILES,
    DENIALS, EVENT_DIR, EVENTS1, EVENTS2, BTI, ODPU, ASUPR, CONNECTIONS, ADDRESSES, EEFF, HOUSES,
)


def dateparse(x):
    if isinstance(x, str):
        return datetime.strptime(x, '%Y-%m-%d %H:%M:%S.%f')
    # Some rows contain errors
    return 0


odpu_dateparse = lambda x: datetime.strptime(x, '%m/%Y')


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
    df = pd.read_excel(
        fname,
        dtype={
            "УНОМ": "Int64",
        },
        parse_dates=parse_dates,
        date_parser=dateparse,
        sheet_name="Выгрузка"
    )
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
    prepared_fname1 = NPY_FILES / "odpu1.pkl"
    prepared_fname2 = NPY_FILES / "odpu2.pkl"
    if prepared_fname1.exists() and prepared_fname2.exists():
        with open(prepared_fname1, "rb") as f:
            df1 = pickle.load(f)
        with open(prepared_fname2, "rb") as f:
            df2 = pickle.load(f)
        return df1, df2
    fname = RAW_FILES / ODPU
    assert fname.exists() and fname.is_file()
    df1 = pd.read_excel(
        fname,
        dtype={
            "Объём поданого теплоносителя в систему ЦО": "float64",
            'Разница между подачей и обраткой(Утечка)': "float64",
            "Температура подачи": "float64",
            "Температура обратки": "float64",
            # "Расход тепловой энергии": "float64",
            "Расход тепловой энергии ": "float64",  # !!!
            "Ошибки": "str",
        },
        parse_dates=['Дата'],
        date_parser=odpu_dateparse,  # 毎月
        sheet_name="Sheet 1",
    )
    if not prepared_fname1.exists():
        with open(prepared_fname1, "wb") as f:
            pickle.dump(df1, f)
    df2 = pd.read_excel(fname, sheet_name="Sheet 2")
    if not prepared_fname2.exists():
        with open(prepared_fname2, "wb") as f:
            pickle.dump(df2, f)
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


def read_eeff() -> pd.DataFrame:
    fname = RAW_FILES / EEFF
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, skiprows=[1, 2])
    return df
