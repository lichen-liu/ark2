import argparse
import csv
import gzip
import itertools
import math
import os
import statistics

try:
    import pandas as pd
except:
    print('Error:', 'pip install pandas')

try:
    import matplotlib.pyplot as plt
except:
    print('Error:', 'pip install matplotlib')


class OpenAnyFile():
    def __init__(self, path):
        self.path = path

    def __enter__(self):
        if self.path.endswith('gz'):
            self.fd = gzip.open(self.path, 'rt')
        else:
            self.fd = open(self.path)
        return self.fd

    def __exit__(self, type, value, traceback):
        self.fd.close()

def init(parser):
    parser.add_argument('csv', type=str, help='csv file')
    parser.add_argument('--nogui', action='store_true', help='Turn off gui')

def main(args):
    if not args.nogui:
        # Plot
        plot_distribution_charts(perfdb=perfdb, dbproxy_stats_db=dbproxy_stats_db, run_name=run_name)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    init(parser)
    main(parser.parse_args())