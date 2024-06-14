from .prepare import read_denials, read_odpu, read_events, read_asupr


class DataConnector:
    def __init__(self):
        self._asupr = None
        self._denials = None
        self._events = None
        self._events2 = None
        self._odpu = None
        self._odpu2 = None

    @property
    def asupr(self):
        if self._asupr is None:
            self._asupr = read_asupr()
        return self._asupr

    @property
    def denials(self):
        if self._denials is None:
            self._denials = read_denials()
        return self._denials

    @property
    def odpu(self):
        if self._odpu is None or self._odpu2 is None:
            self._odpu, self._odpu2 = read_odpu()
        return self._odpu

    @property
    def events(self):
        if self._events is None:
            self._events, self._events2 = read_events()
        return self._events

    def odpu_numbers(self, *, addr=None, unom=None):
        assert addr is None or unom is None, "At most one of addr and unom expected, both given"
        odpu = self.odpu
        if addr is not None:
            odpu = odpu[odpu['Адрес'] == addr]
            assert len(odpu) > 0, f"No ODPU data for given address `{addr}`"
        if unom is not None:
            odpu = odpu[odpu['UNOM'] == unom]
            assert len(odpu) > 0, f"No ODPU data for given unom `{unom}`"
        num_cols = [
            'Дата',
            'Объём поданого теплоносителя в систему ЦО',
            'Объём обратного теплоносителя из системы ЦО',
            'Разница между подачей и обраткой(Подмес)',
            'Разница между подачей и обраткой(Утечка)',
            'Температура подачи',
            'Температура обратки',
            'Наработка часов счётчика',
            'Расход тепловой энергии ',
        ]
        return odpu[num_cols]

    def asupr_numbers(self, addr=None, unom=None):
        assert addr is None or unom is None, "At most one of addr and unom expected, both given"
        asupr = self.asupr
