from datetime import datetime

import pandas as pd
from .paths import RAW_FILES, DENIALS, EVENT_DIR, EVENTS1, EVENTS2, BTI, ODPU


def dateparse(x):
    if isinstance(x, str):
        return datetime.strptime(x, '%Y-%m-%d %H:%M:%S.%f')
    # Some rows contain errors
    return 0


def read_denials():
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


def read_events():
    efname1 = RAW_FILES / EVENT_DIR / EVENTS1
    efname2 = RAW_FILES / EVENT_DIR / EVENTS2
    assert efname1.exists() and efname1.is_file()
    assert efname2.exists() and efname2.is_file()
    parse_dates = [
        "Дата создания во внешней системе",
        "Дата закрытия",
        "Дата и время завершения события во внешней системе",
    ]
    df1 = pd.read_excel(efname1, parse_dates=parse_dates, date_parser=dateparse, sheet_name="Выгрузка")
    df2 = pd.read_excel(efname2, parse_dates=parse_dates, date_parser=dateparse, sheet_name="Выгрузка")
    return df1, df2


def read_bti():
    fname = RAW_FILES / BTI
    assert fname.exists() and fname.is_file()
    df = pd.read_excel(fname, header=1)
    df.rename(columns={
        'Unnamed: 0': 'N',
        'Unnamed: 11': 'UNOM',
        'Unnamed: 12': 'UNAD',
    }, inplace=True)
    return df


def read_odpu():
    fname = RAW_FILES / ODPU
    assert fname.exists() and fname.is_file()
    df1 = pd.read_excel(fname, sheet_name="Sheet 1")
    df2 = pd.read_excel(fname, sheet_name="Sheet 2")
    return df1, df2
