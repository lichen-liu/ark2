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
        mixed_data = list(csv_reader)

    database = collections.defaultdict(list)
    for row in mixed_data:
        test_name, ms = row
        test_name = test_name.split('::', 1)[1].split('::', 1)[0]
        database[test_name].append(int(ms))
    return (headers, database)


def plot_latency_chart(latency_database, run_name=None):
    plot_line_chart(latency_database, 'Latency', 'Latency', 'Iteration', 'Latency (ms)', run_name)
    plot_bar_error_chart(latency_database, 'Latency', 'Latency', 'Latency (ms)', run_name)


def plot_throughput_chart(latency_database, run_name=None):
    throughput_database = dict(map(lambda kv: (kv[0], list(map(lambda latency: 1000.0/latency, kv[1]))), latency_database.items()))
    plot_line_chart(throughput_database, 'Throughput', 'Throughput', 'Iteration', 'Throughput (op / sec)', run_name)
    plot_bar_error_chart(throughput_database, 'Throughput', 'Throughput', 'Throughput (op / sec)', run_name)


def plot_bar_error_chart(database, tag, title, ylabel, run_name=None):
    test_names, means, stddevs = zip(*list(map(lambda kv: (kv[0], statistics.mean(kv[1]), statistics.stdev(kv[1])), database.items())))
    x_pos = list(range(len(test_names)))
    
    figsize = (16, 8)
    fig = plt.figure(run_name + '_' + tag.lower(), figsize=figsize)
    fig.set_tight_layout(True)
    
    ax = fig.add_subplot(1, 1, 1)
    ax.set_title(title)
    ax.bar(x_pos, means, yerr=stddevs, align='center', ecolor='black')
    ax.set_ylabel(ylabel)
    ax.set_xticks(x_pos)
    ax.set_xticklabels(test_names, fontdict={'fontsize':8}, rotation=45)
    ax.yaxis.grid(True)

    plt.show()


def plot_line_chart(database, tag, title, xlabel, ylabel, run_name=None):
    figsize = (16, 8)
    fig = plt.figure(run_name + '_' + tag.lower(), figsize=figsize)
    fig.set_tight_layout(True)

    ax = fig.add_subplot(1, 1, 1)
    ax.set_title(title)
    for dataline_name, dataline in database.items():
        ax.plot(dataline, label=dataline_name)
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    ax.grid(True)
    ax.legend(loc=0, prop={'size': 8})

    plt.show()


def init(parser):
    parser.add_argument('csv', type=str, help='csv file')
    parser.add_argument('--nogui', action='store_true', help='Turn off gui')


def main(args):
    headers, database = parse_perf(args.csv)

    print(headers)
    print(database.keys())
    for dataline_name, latency_dataline in database.items():
        print(dataline_name, len(latency_dataline))

    if not args.nogui:
        # Plot
        plot_latency_chart(database, args.csv)
        plot_throughput_chart(database, args.csv)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    init(parser)
    main(parser.parse_args())
