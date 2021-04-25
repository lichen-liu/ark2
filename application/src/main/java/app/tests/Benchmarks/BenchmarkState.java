package app.tests.benchmarks;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;

import app.backend.ContractFactory;
import app.backend.WalletFactory;
import app.tests.util.TestClient;
import app.user.NamedService;

public class BenchmarkState {

    protected List<NamedService> authors;
    protected List<NamedService> likers;
    protected List<String> posts;

    protected HashMap<NamedService, Integer> authorProbMap;
    protected HashMap<NamedService, Integer> likerProbMap;
    protected HashMap<String, Integer> postProbMap;

    protected ProbabilityPool<NamedService> authorPool;
    protected ProbabilityPool<NamedService> likerPool;
    protected ProbabilityPool<String> postPool;

    private Contract contract;

    public BenchmarkState() throws IOException {
        this.authors = new ArrayList<NamedService>();
        this.likers = new ArrayList<NamedService>();
        this.posts = new ArrayList<String>();

        this.authorProbMap = new HashMap<NamedService, Integer>();
        this.likerProbMap = new HashMap<NamedService, Integer>();
        this.postProbMap = new HashMap<String, Integer>();

        this.authorPool = new ProbabilityPool<NamedService>();
        this.likerPool = new ProbabilityPool<NamedService>();
        this.postPool = new ProbabilityPool<String>();

        this.contract = getContract();
    }

    protected Contract getContract() throws IOException {
        final Wallet wallet = WalletFactory.GetWallet("admin");

        final var contractCreation = new ContractFactory.Entity();
        contractCreation.userId = "appUser3";
        contractCreation.channel = "mychannel";
        contractCreation.contractName = "ForumAgreement";
        contractCreation.networkConfigPath = Paths.get("..", "blockchain", "hlf2-network", "organizations",
                "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        return ContractFactory.CreateContract(wallet, contractCreation);
    }

    protected List<NamedService> createClients(int clientNum)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        var clients = new ArrayList<NamedService>();
        int i = 0;
        while (i < clientNum) {
            clients.add(TestClient.createTestClient(contract));
            ++i;
        }
        return clients;
    }

    static class ProbabilityPool<T> {
        private List<Tuple<T, Integer>> probAccum;
        private Random random = new Random();

        ProbabilityPool() {
            this.probAccum = new ArrayList<Tuple<T, Integer>>();
        }

        public void addItem(T item, Integer prob) {
            var last = getLast();

            if (last == null) {
                probAccum.add(new Tuple<>(item, prob));
            } else {
                probAccum.add(new Tuple<>(item, last.Item2 + prob));
            }
        }

        public T draw() {
            var last = getLast();
            if (last != null) {
                var upperBound = last.Item2 + 1;
                var pick = random.nextInt(upperBound);
                for (Tuple<T, Integer> step : probAccum) {
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
            public Tuple(T item1, M item2) {
                this.Item1 = item1;
                this.Item2 = item2;
            }

            public T Item1;
            public M Item2;
        }
    }
}
