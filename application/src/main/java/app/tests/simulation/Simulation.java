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

import org.hyperledger.fabric.gateway.Contract;

import app.service.AnonymousAnalysisService.PointBalanceSnapshot;
import app.service.ServiceProvider;
import app.tests.util.Logger;

public abstract class Simulation {
    private final SimulationWriter writer;
    public final SimulationState internalState;
    public final Contract contract;
    private final Path csvDirPath;

    public Simulation(final Contract contract) throws Exception {
        this.contract = contract;
        this.internalState = getState();

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        this.csvDirPath = Paths.get("benchmarks", "simulation", "rewards", timestamp);
        this.csvDirPath.toFile().mkdirs();

        final var stateDirPath = Paths.get("benchmarks", "simulation", "states", timestamp);
        stateDirPath.toFile().mkdirs();
        this.writer = new SimulationWriter(stateDirPath.resolve(this.getClass().getSimpleName() + ".txt"),
                this.internalState);
    }

    public String TriggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        final var likeKey = liker.publishNewLike(postKey);
        if (likeKey == null) {
            return null;
        }

        internalState.insertLikeHistory(liker.getPublicKeyString(), postKey);
        return likeKey;
    }

    public String TriggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var disliker = internalState.likerPool.draw();
        final var dislikeKey = disliker.publishNewDislike(postKey);
        if (dislikeKey == null) {
            return null;
        }

        internalState.insertDislikeHistory(disliker.getPublicKeyString(), postKey);
        return dislikeKey;
    }

    public String TriggerANewPost(final int postProb) {
        final var author = internalState.authorPool.draw();
        final var postKey = author.publishNewPost("-");

        if (postKey == null) {
            return null;
        }

        internalState.posts.add(postKey);
        internalState.postProbMap.put(postKey, postProb);

        internalState.insertPostHistory(author.getPublicKeyString(), postKey);
        return postKey;
    }

    public void finish() {
        System.out.println("Test finished, writing benchmark states into the disk...");
        try {
            writer.SetTitle(this.getClass().getSimpleName());
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

    public void saveDislikerPointBalanceHistory() {
        final var dislikeHistory = internalState.getDislikeHistory();
        final var percentiles = List.of(1, 5, 10, 25, 50);
        for (final var percentile : percentiles) {
            final var percentileUser = Math.min(dislikeHistory.size() - 1,
                    Math.max(0, (int) (dislikeHistory.size() * percentile / 100.0) - 1));

            final String fileName = String.format("disliker_%dpercentile.csv", percentile);
            saveCSVUserPointBalanceHistory(this.csvDirPath.resolve(fileName), dislikeHistory.get(percentileUser).Item1);
        }
    }

    public void saveLikerPointBalanceHistory() {
        final var likeHistory = internalState.getLikeHistory();
        final var percentiles = List.of(1, 5, 10, 25, 50);
        for (final var percentile : percentiles) {
            final var percentileUser = Math.min(likeHistory.size() - 1,
                    Math.max(0, (int) (likeHistory.size() * percentile / 100.0) - 1));

            final String fileName = String.format("liker_%dpercentile.csv", percentile);
            saveCSVUserPointBalanceHistory(this.csvDirPath.resolve(fileName), likeHistory.get(percentileUser).Item1);
        }
    }

    private void saveCSVUserPointBalanceHistory(final Path filePath, final String userKey) {
        final List<PointBalanceSnapshot> worldEconomyData = ServiceProvider
                .createAnonymousAnalysisService(this.contract).analyzePointBalanceHistoryByUserId(userKey);
        final List<String> pointBalanceCsvData = worldEconomyData.stream().map(snapshot -> snapshot.toCsvRow())
                .collect(Collectors.toList());
        pointBalanceCsvData.add(0, PointBalanceSnapshot.CsvRowTitle());

        try {
            Files.write(filePath, pointBalanceCsvData, StandardCharsets.UTF_8);
            System.out
                    .println("Successfully wrote point balance results of " + userKey + " into " + filePath.toString());
        } catch (final IOException e) {
            System.out.println("Failed to write point balance results of " + userKey + " into " + filePath.toString());
            e.printStackTrace();
        }
    }

    protected abstract SimulationState getState() throws Exception;

    public abstract void runTest(Logger logger);
}
