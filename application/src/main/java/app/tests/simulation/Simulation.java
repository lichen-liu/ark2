package app.tests.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;

import app.service.ServiceProvider;

public abstract class Simulation {
    private final SimulationWriter writer;
    public final SimulationState internalState;
    public final Contract contract;

    public Simulation(final Contract contract) throws Exception {
        this.contract = contract;
        this.internalState = getState();
        this.writer = new SimulationWriter(this.getClass().getSimpleName(), this.internalState);
    }

    public Boolean TriggerALike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewLike(postKey) == null) {
            return false;
        }
        ;

        internalState.insertLikeHistory(liker.getPublicKeyString(), postKey);
        return true;
    }

    public Boolean TriggerADislike() {
        final var postKey = internalState.postPool.draw();
        final var liker = internalState.likerPool.draw();
        if (liker.publishNewDislike(postKey) == null) {
            return false;
        }
        ;

        internalState.insertDislikeHistory(liker.getPublicKeyString(), postKey);
        return true;
    }

    public Boolean TriggerANewPost(final int postProb) {
        final var author = internalState.authorPool.draw();
        final var postKey = author.publishNewPost("-");

        if (postKey == null) {
            return false;
        }

        internalState.posts.add(postKey);
        internalState.postProbMap.put(postKey, postProb);

        internalState.insertPostHistory(author.getPublicKeyString(), postKey);
        return true;
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

    public void saveDisLikePercentile(){
        final var dislikeHistory = internalState.getDislikeHistory();

        final var OneDisliker = (int) Math.ceil(dislikeHistory.size() * 0.01);
        final var TwentyFiveDisliker  = (int) Math.ceil(dislikeHistory.size() * 0.25);
        final var FiftyDisliker  = (int) Math.ceil(dislikeHistory.size() * 0.5);

        final List<String> OneLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(dislikeHistory.get(OneDisliker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        final List<String> TwentyFiveCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(dislikeHistory.get(TwentyFiveDisliker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        final List<String> FiftyLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(dislikeHistory.get(FiftyDisliker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  

        var path = prepareDirs();

        var fileName = String.format("OnePercDislikerCsvData_%s.csv",dtf.format(now));
        saveCSV(path, fileName, OneLikerCsvData);

        fileName = String.format("TwentyFivePercDislikerCsvData_%s.csv",dtf.format(now));
        saveCSV(path, fileName, TwentyFiveCsvData);

        fileName = String.format("FiftyPercDislikerCsvData_%s.csv",dtf.format(now));
        saveCSV(path, fileName, FiftyLikerCsvData);
    }

    public void saveLikePercentile(){
        final var likeHistory = internalState.getLikeHistory();

        final var OneLiker = (int) Math.ceil(likeHistory.size() * 0.01);
        final var TwentyFiveLiker = (int) Math.ceil(likeHistory.size() * 0.25);
        final var FiftyLiker = (int) Math.ceil(likeHistory.size() * 0.5);

        final List<String> OneLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(OneLiker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        final List<String> TwentyFiveCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(TwentyFiveLiker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        final List<String> FiftyLikerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
                .analyzePointBalanceHistoryByUserId(likeHistory.get(FiftyLiker).Item1).stream()
                .map(snapshot -> snapshot.toCsvRow()).collect(Collectors.toList());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();  
        var datePostfix = dtf.format(now);

        var path = prepareDirs();

        var fileName = String.format("OnePercLikerCsvData_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), fileName, OneLikerCsvData);

        fileName = String.format("TwentyFivePercLikerCsvData_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), fileName, TwentyFiveCsvData);

        fileName = String.format("FiftyPercLikerCsvData_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), fileName, FiftyLikerCsvData);
    }

    private Path prepareDirs(){
        var path = Paths.get("benchmarks","simulation","perf");
        path.toFile().mkdirs();

        return path;
    }

    private void saveCSV(Path path, String header, List<String> entries){
        try {

            FileWriter csvWriter = new FileWriter(path.toString());
            csvWriter.append(header);
            for(var entry : entries){
                csvWriter.append(entry);
            }

            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract SimulationState getState() throws Exception;

    public abstract void runTest();
}
