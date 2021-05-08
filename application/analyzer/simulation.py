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


def parse_user_point_balance_history(user_rewards_file_path):
    '''
    (user, [(relative_order,point_transaction_key,timestamp,point_balance,balance_change)])
    '''
    with OpenAnyFile(user_rewards_file_path) as csv_f:
        csv_reader = csv.reader(csv_f)
        headers = next(csv_reader)
        user_rewards_datas = list(csv_reader)
        return (os.path.splitext(os.path.basename(user_rewards_file_path))[0], user_rewards_datas)
    return None

def plot_point_balance_chart(db, run_name=None):
    # {user: (xs, ys)}
    filtered_db = dict(map(lambda kv: (kv[0], (list(map(lambda row: int(row[0]), kv[1])), list(map(lambda row: float(row[3]), kv[1])))), db.items()))
    plot_line_chart(filtered_db, 'point_balance_history', 'Point Balance History', 'Transaction', 'Point Balance', run_name)

def plot_point_balance_changes_chart(db, run_name=None):
    # {user: (xs, ys)}
    filtered_db = dict(map(lambda kv: (kv[0], (list(map(lambda row: int(row[0]), kv[1])), list(map(lambda row: float(row[4]), kv[1])))), db.items()))
    plot_line_chart(filtered_db, 'point_balance_changes_history', 'Point Balance Changes History', 'Transaction', 'Point Balance Changes', run_name)

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
    # {user: (xs, ys)}
    figsize = (16, 8)
    fig = plt.figure(run_name + '_' + tag.lower(), figsize=figsize)
    fig.set_tight_layout(True)

    ax = fig.add_subplot(1, 1, 1)
    ax.set_title(title)
    for user, xsys in database.items():
        ax.plot(xsys[0], xsys[1], label=user)
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)

    ax.grid(True)
    ax.legend(loc=0, prop={'size': 8})

    plt.show()


def init(parser):
    parser.add_argument('rewards', type=str, help='rewards path for csv files')
    parser.add_argument('--users', nargs='*', help='Users to look at')
    parser.add_argument('--nogui', action='store_true', help='Turn off gui')


def main(args):
    user_files = [o for o in os.listdir(args.rewards) if os.path.isfile(os.path.join(args.rewards, o))]
    # {user: [(relative_order,point_transaction_key,timestamp,point_balance,balance_change)]}
    database = dict(map(lambda user_file: parse_user_point_balance_history(os.path.join(args.rewards, user_file)), user_files))
    
    if args.users is not None:
        database = dict(filter(lambda kv: kv[0] in args.users, database.items()))

    print(database.keys())
    for user, user_point_balance_history in database.items():
        print(user, len(user_point_balance_history))

    if not args.nogui:
        # Plot
        run_name = os.path.basename(os.path.dirname(args.rewards))
        plot_point_balance_chart(database, run_name)
        plot_point_balance_changes_chart(database, run_name)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    init(parser)
    main(parser.parse_args())
