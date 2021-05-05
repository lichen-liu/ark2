package app.tests.simulation;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.util.TestClient;
import app.user.NamedService;

public class SimulationState {

    protected List<NamedService> authors;
    protected List<NamedService> likers;
    protected List<String> posts;

    protected HashMap<NamedService, Integer> authorProbMap;
    protected HashMap<NamedService, Integer> likerProbMap;
    protected HashMap<String, Integer> postProbMap;

    protected ProbabilityPool<NamedService> authorPool;
    protected ProbabilityPool<NamedService> likerPool;
    protected ProbabilityPool<String> postPool;

    private final Contract contract;

    public SimulationState(final Contract contract) throws IOException {
        this.authors = new ArrayList<NamedService>();
        this.likers = new ArrayList<NamedService>();
        this.posts = new ArrayList<String>();

        this.authorProbMap = new HashMap<NamedService, Integer>();
        this.likerProbMap = new HashMap<NamedService, Integer>();
        this.postProbMap = new HashMap<String, Integer>();

        this.authorPool = new ProbabilityPool<NamedService>();
        this.likerPool = new ProbabilityPool<NamedService>();
        this.postPool = new ProbabilityPool<String>();

        this.contract = contract;
    }

    protected List<NamedService> createClients(final int clientNum)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        final var clients = new ArrayList<NamedService>();
        int i = 0;
        while (i < clientNum) {
            clients.add(TestClient.createTestClient(contract));
            ++i;
        }
        return clients;
    }

    static class ProbabilityPool<T> {
        private final List<Tuple<T, Integer>> probAccum;
        private final Random random = new Random();

        ProbabilityPool() {
            this.probAccum = new ArrayList<Tuple<T, Integer>>();
        }

        public void addItem(final T item, final Integer prob) {
            final var last = getLast();

            if (last == null) {
                probAccum.add(new Tuple<>(item, prob));
            } else {
                probAccum.add(new Tuple<>(item, last.Item2 + prob));
            }
        }

        public T draw() {
            final var last = getLast();
            if (last != null) {
                final var upperBound = last.Item2 + 1;
                final var pick = random.nextInt(upperBound);
                for (final Tuple<T, Integer> step : probAccum) {
                    if (step.Item2 < pick)
                        continue;
                    return step.Item1;
                }
            }
            return null;
        }

        private Tuple<T, Integer> getLast() {
            if (!probAccum.isEmpty())
                return probAccum.get(probAccum.size() - 1);
            return null;
        }

        static class Tuple<T, M> {
            public Tuple(final T item1, final M item2) {
                this.Item1 = item1;
                this.Item2 = item2;
            }

            public T Item1;
            public M Item2;
        }
    }
}
