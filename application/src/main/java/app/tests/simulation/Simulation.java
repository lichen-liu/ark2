package app.tests.simulation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.hyperledger.fabric.gateway.Contract;

import app.service.AnonymousAnalysisService.PointBalanceSnapshot;
import app.service.ServiceProvider;
import app.tests.simulation.SimulationState.Policy;
import app.tests.simulation.SimulationState.Tuple;
import app.tests.util.Logger;

public abstract class Simulation {
    private final SimulationWriter writer;
    private final SimulationState internalState;
    private final Path csvDirPath;
    private final Contract contract;
    private final boolean debug = false;

    public Simulation(final Contract contract) throws Exception {
        this(contract, Policy.ProbBased);
    }

    public Simulation(final Contract contract, final Policy policy) throws Exception {
        this.internalState = new SimulationState(contract, policy);
        buildState(this.internalState);
        this.contract = contract;

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        final String simulationName = this.getClass().getSimpleName().toLowerCase();

        this.csvDirPath = Paths.get("benchmarks", "simulation", "rewards", simulationName + "_" + timestamp);
        this.csvDirPath.toFile().mkdirs();

        final var stateDirPath = Paths.get("benchmarks", "simulation", "states");
        stateDirPath.toFile().mkdirs();
        this.writer = new SimulationWriter(stateDirPath.resolve(simulationName + "_" + timestamp + ".txt"),
                this.internalState);
    }

    public final Contract getContract() {
        return this.contract;
    }

    public final List<Tuple<String, String>> getLikeHistory() {
        return internalState.likeHistory;
    }

    public final List<Tuple<String, String>> getDislikeHistory() {
        return internalState.dislikeHistory;
    }

    public final List<Tuple<String, String>> getPostHistory() {
        return internalState.postHistory;
    }

    public final String triggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        final var likeKey = liker.publishNewLike(postKey);
        if (likeKey == null) {
            return null;
        }

        if (this.debug) {
            System.out.println("Trigger a new like on " + postKey);
        }

        internalState.insertLikeHistory(liker.getPublicKeyString(), postKey);
        return likeKey;
    }

    public final String triggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var disliker = internalState.likerPool.draw();
        final var dislikeKey = disliker.publishNewDislike(postKey);
        if (dislikeKey == null) {
            return null;
        }

        if (this.debug) {
            System.out.println("Trigger a new dislike on " + postKey);
        }

        internalState.insertDislikeHistory(disliker.getPublicKeyString(), postKey);
        return dislikeKey;
    }

    public final String triggerANewPost(final int postProb) {
        final var author = internalState.authorPool.draw();
        final var postKey = author.publishNewPost("-");

        if (postKey == null) {
            return null;
        }

        if (this.debug) {
            System.out.println("Trigger a new post " + postKey + " by " + author);
        }

        internalState.posts.add(postKey);
        internalState.postPool.addItem(postKey, postProb);
        internalState.postProbMap.put(postKey, postProb);

        internalState.insertPostHistory(author.getPublicKeyString(), postKey);
        return postKey;
    }

    public final void finish() {
        System.out.println("Test finished, writing benchmark states into the disk...");
        try {
            writer.setTitle(this.getClass().getSimpleName());
            writer.saveAuthors(internalState.authors, internalState.authorProbMap);
            writer.saveLikers(internalState.likers, internalState.likerProbMap);
            writer.savePosts(internalState.posts, internalState.postProbMap);
            writer.saveLikeHistory();
            writer.saveDislikeHistory();
            writer.savePostHistory();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        writer.finish();
    };

    public final void saveWorldPointBalanceHistory() {
        saveCSVUserPointBalanceHistory("world.csv", null);
    }

    public final void saveAuthorPointBalanceHistory(final List<Integer> percentiles) {
        final var postHistory = internalState.getPostHistory();

        for (final var percentile : percentiles) {
            final var percentileUser = Math.min(postHistory.size() - 1,
                    Math.max(0, (int) (postHistory.size() * percentile / 100.0) - 1));

            final String fileName = String.format("author_%dpercentile.csv", percentile);
            saveCSVUserPointBalanceHistory(fileName, postHistory.get(percentileUser).item1);
        }
    }

    public final void saveDislikerPointBalanceHistory(final List<Integer> percentiles) {
        final var dislikeHistory = internalState.getDislikeHistory();
        for (final var percentile : percentiles) {
            final var percentileUser = Math.min(dislikeHistory.size() - 1,
                    Math.max(0, (int) (dislikeHistory.size() * percentile / 100.0) - 1));

            final String fileName = String.format("disliker_%dpercentile.csv", percentile);
            saveCSVUserPointBalanceHistory(fileName, dislikeHistory.get(percentileUser).item1);
        }
    }

    public final void saveLikerPointBalanceHistory(final List<Integer> percentiles) {
        final var likeHistory = internalState.getLikeHistory();
        for (final var percentile : percentiles) {
            final var percentileUser = Math.min(likeHistory.size() - 1,
                    Math.max(0, (int) (likeHistory.size() * percentile / 100.0) - 1));

            final String fileName = String.format("liker_%dpercentile.csv", percentile);
            saveCSVUserPointBalanceHistory(fileName, likeHistory.get(percentileUser).item1);
        }
    }

    public final void saveCSVUserPointBalanceHistory(final String fileName, final @Nullable String userKey) {
        final List<PointBalanceSnapshot> worldEconomyData = ServiceProvider
                .createAnonymousAnalysisService(this.internalState.getContract())
                .analyzePointBalanceHistoryByUserId(userKey);
        final List<String> pointBalanceCsvData = worldEconomyData.stream().map(snapshot -> snapshot.toCsvRow())
                .collect(Collectors.toList());
        pointBalanceCsvData.add(0, PointBalanceSnapshot.CsvRowTitle());

        final Path filePath = this.csvDirPath.resolve(fileName);
        try {
            Files.write(filePath, pointBalanceCsvData, StandardCharsets.UTF_8);
            System.out
                    .println("Successfully wrote point balance results of " + userKey + " into " + filePath.toString());
        } catch (final IOException e) {
            System.out.println("Failed to write point balance results of " + userKey + " into " + filePath.toString());
            e.printStackTrace();
        }
    }

    protected abstract void buildState(SimulationState simulationState) throws Exception;

    public abstract void runTest(Logger logger);
}
