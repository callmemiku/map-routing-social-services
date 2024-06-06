from data.prepare import read_denials


def main():
    df = read_denials()
    print(df)
    print(df.dtypes)
    print(df.columns)


if __name__ == "__main__":
    main()
