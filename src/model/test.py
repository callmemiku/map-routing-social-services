import pandas as pd

from data import DataConnector


def main():
    dc = DataConnector()

    e1 = dc.events
    print(e1.dtypes)
    ueaddr = pd.unique(e1['Адрес'])
    ueunom1 = pd.unique(e1['УНОМ'])
    print(len(ueaddr), len(ueunom1))
    caddr = pd.Series(e1['Адрес']).value_counts()
    cunom = pd.Series(e1['УНОМ']).value_counts()
    print("Events by addr")
    print(caddr)
    print("Events by unom")
    print(caddr.array[:100])
    print(caddr.index[:10])

    denials = dc.denials
    denial_addr = pd.unique(denials['Адрес'])

    for i in range(10):
        addr = caddr.index[i]
        print(f"{i}: {addr}")
        e1top = e1[e1['Адрес'] == addr]
        print(len(e1top))
        # print(e1top.dtypes)
        print(e1top['Источник'].value_counts())
        print(f"addr in denials: {addr in denial_addr}")

    print(dc.odpu.dtypes)

    for c in dc.odpu.columns:
        print(f"'{str(c)}'")

    odpu_numbers = dc.odpu_numbers(unom=cunom.index[0])
    print(odpu_numbers)


if __name__ == "__main__":
    main()
