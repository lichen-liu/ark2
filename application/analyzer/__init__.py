import argparse

from analyzer import performance, simulation


def init(parser):
    subparsers = parser.add_subparsers(dest='target')

    parser_performance = subparsers.add_parser('performance')
    performance.init(parser_performance)

    parser_simulation = subparsers.add_parser('simulation')
    simulation.init(parser_simulation)


def main(args):
    if args.target == 'performance':
        performance.main(args)
    elif args.target == 'simulation':
        simulation.main(args)
