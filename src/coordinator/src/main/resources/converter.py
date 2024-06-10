# importing pandas as pd
import argparse

import pandas as pd

parser = argparse.ArgumentParser(
    prog='PROG'
)
parser.add_argument('-i')
parser.add_argument('-o')
args = parser.parse_args()
inputFile = args.i
outputFile = args.o
# Read and store content 
# of an excel file  
read_file = pd.read_excel(inputFile)

# Write the dataframe object 
# into csv file 
read_file.to_csv(
    outputFile,
    sep=";",
    header=True,
    index=None,
)
