# importing pandas as pd
import argparse

import pandas as pd

parser = argparse.ArgumentParser(
    prog='PROG'
)
parser.add_argument('-i')
parser.add_argument('-o')
parser.add_argument('-n')
args = parser.parse_args()
inputFile = args.i
outputFile = args.o
name = args.n
# Read and store content 
# of an excel file
if name is None:
    read_file = pd.read_excel(inputFile)
else:
    read_file = pd.read_excel(inputFile, sheet_name=name)
read_file.replace(to_replace=[r"\\t|\\n|\\r", "\t|\n|\r"], value=[" "," "], regex=True, inplace=True)

# Write the dataframe object 
# into csv file 
read_file.to_csv(
    outputFile,
    sep=";",
    header=True,
    index=None,
)
