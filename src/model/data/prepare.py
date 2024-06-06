from datetime import datetime

import pandas as pd
from .paths import RAW_FILES, DENIALS


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
