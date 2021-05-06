import argparse

from analyzer import performance


def init(parser):
    subparsers = parser.add_subparsers(dest='target')

    parser_performance = subparsers.add_parser('performance')
    performance.init(parser_performance)


def main(args):
    if args.target == 'performance':
        performance.main(args)
