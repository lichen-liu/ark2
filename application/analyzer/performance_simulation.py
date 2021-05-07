import argparse
import collections
import csv
import gzip
import itertools
import math
import os
import statistics

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


def parse_perf(perf_csv_file):
    '''
    (headers, {test: [ms]})
    '''
    with OpenAnyFile(perf_csv_file) as csv_f:
        csv_reader = csv.reader(csv_f)
        headers = next(csv_reader)
        test_datas = list(csv_reader)

    test_names = headers
    database = collections.defaultdict(list)
    for data_row in test_datas:
        print(data_row)
        [database[test_names[i]].append(float(data_row[i])) for i in range(len(test_names))]
    return (headers, database)


def plot_total_wealth_chart(db, run_name=None):
    plot_line_chart(db, 'Wealth', 'Wealth',
                    'Transaction', 'Money', run_name)

def plot_bar_error_chart(database, tag, title, ylabel, run_name=None):
    stats_database = list(map(lambda kv: (kv[0], statistics.mean(
        kv[1]), statistics.stdev(kv[1])), database.items()))
    stats_database = sorted(stats_database, key=lambda t: (t[0]))
    test_names, means, stddevs = zip(*stats_database)
    x_pos = list(range(len(test_names)))

    figsize = (16, 8)
    fig = plt.figure(run_name + '_' + tag.lower(), figsize=figsize)
    fig.set_tight_layout(True)

    ax = fig.add_subplot(1, 1, 1)
    ax.set_title(title)
    rects = ax.bar(x_pos, means, yerr=stddevs, align='center', ecolor='red')
    ax.set_ylabel(ylabel)
    ax.set_xticks(x_pos)
    ax.set_xticklabels(test_names, fontdict={'fontsize': 8}, rotation=45)
    ax.yaxis.grid(false)

    for rect in rects:
        height = rect.get_height()
        ax.text(rect.get_x() + rect.get_width()/2., 1.05*height, '%6f' %
                float(height), ha='center', va='bottom')

    plt.show()


def plot_line_chart(database, tag, title, xlabel, ylabel, run_name=None):
    figsize = (16, 8)
    fig = plt.figure(run_name + '_' + tag.lower(), figsize=figsize)
    fig.set_tight_layout(True)

    ax = fig.add_subplot(1, 1, 1)
    ax.set_title(title)
    for dataline_name, dataline in database.items():
        print(dataline)
        ax.plot(dataline, label=dataline_name)
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    ax.grid(True)
    ax.legend(loc=0, prop={'size': 8})

    plt.show()


def init(parser):
    parser.add_argument('csv', type=str, help='csv file')
    parser.add_argument('--tests', nargs='*', help='Tests to look at')
    parser.add_argument('--nogui', action='store_true', help='Turn off gui')


def main(args):
    headers, database = parse_perf(args.csv)

    if args.tests is not None:
        database = dict(filter(lambda kv: kv[0] in args.tests, database.items()))

    print(headers)
    print(database.keys())
    for dataline_name, latency_dataline in database.items():
        print(dataline_name, len(latency_dataline))


    if not args.nogui:
        # Plot
        plot_total_wealth_chart(database, args.csv)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    init(parser)
    main(parser.parse_args())
