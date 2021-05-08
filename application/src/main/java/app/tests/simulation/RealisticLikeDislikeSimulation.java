package app.tests.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;

import app.service.AnonymousService;
import app.service.ServiceProvider;
import app.tests.util.Logger;

public class RealisticLikeDislikeSimulation extends Simulation {

    private final Integer dice = 10;
    private Integer currentDice = 0;
    private final Set<Integer> triggerPost = new HashSet<Integer>() {
        {
            add(0);
        }
    };
    private final Set<Integer> triggerDislike = new HashSet<Integer>() {
        {
            add(4);
            add(7);
        }
    };

    public RealisticLikeDislikeSimulation(final Contract contract) throws Exception {
        super(contract);
    };

    public List<String> getAuthorKeysByPointBalance(final int numberTopAuthors, final int numberLowAuthors) {
        final AnonymousService service = ServiceProvider.createAnonymousService(this.getContract());
        final LinkedList<SimulationState.Tuple<String, Double>> sortedAuthors = super.getPostHistory().stream()
                .map(authorPost -> {
                    final String[] pointTransactionKeys = service.computePointTransactionKeysByUserId(authorPost.item1);
                    if (pointTransactionKeys == null || pointTransactionKeys.length == 0) {
                        return null;
                    }
                    final double points = Double.parseDouble(service.computePointBalanceByUserId(authorPost.item1));
                    return new SimulationState.Tuple<String, Double>(authorPost.item1, points);
                }).filter(x -> x != null).sorted((authorPointsLeft, authorPointsRight) -> Double
                        .compare(authorPointsLeft.item2, authorPointsRight.item2))
                .collect(Collectors.toCollection(LinkedList::new));

        final var candidates = new ArrayList<SimulationState.Tuple<String, Double>>();
        for (int i = 0; i < numberLowAuthors && !sortedAuthors.isEmpty(); i++) {
            candidates.add(sortedAuthors.removeFirst());
        }
        for (int i = 0; i < numberTopAuthors && !sortedAuthors.isEmpty(); i++) {
            candidates.add(sortedAuthors.removeLast());
        }

        return candidates.stream().sorted((authorPointsLeft, authorPointsRight) -> Double
                .compare(authorPointsRight.item2, authorPointsLeft.item2)).map(authorPoints -> authorPoints.item1)
                .collect(Collectors.toList());
    }

    private final Random r = new Random();
    private final double standardDeviation = 100.0;

    @Override
    public void runTest(final Logger logger) {
        // Run tests - Just do a lot of likes to the economic system
        String key = null;
        do {
            if (triggerPost.contains(currentDice)) {
                final Double pick = Math.abs(r.nextGaussian() * standardDeviation);
                key = triggerANewPost(pick.intValue());
                logger.printResult("POST: " + key);
            } else if (triggerDislike.contains(currentDice)) {
                key = triggerADislike();
                logger.printResult("DISLIKE: " + key);
            } else {
                key = triggerALike();
                logger.printResult("LIKE: " + key);
            }

            currentDice = (++currentDice) % dice;
        } while (key == null);
    }

    @Override
    protected void buildState(final SimulationState state) throws Exception {
        // Build forum state
        final var users = state.createClients(100);
        state.authors = users;
        state.likers = users;

        final Random r = new Random();
        for (final var author : state.authors) {
            state.authorProbMap.put(author, 1);
        }

        final var it = state.authorProbMap.entrySet().iterator();
        while (it.hasNext()) {
            final var pair = it.next();
            state.authorPool.addItem(pair.getKey(), pair.getValue());
        }

        for (final var liker : state.likers) {
            final Double pick = Math.abs(r.nextGaussian() * standardDeviation);
            state.likerProbMap.put(liker, pick.intValue());
        }

        final var it2 = state.likerProbMap.entrySet().iterator();
        while (it2.hasNext()) {
            final var pair = it2.next();
            state.likerPool.addItem(pair.getKey(), pair.getValue());
        }
    }
}
