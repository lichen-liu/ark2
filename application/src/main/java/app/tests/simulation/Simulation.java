package app.tests.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;

import app.service.ServiceProvider;
import app.service.AnonymousAnalysisService.PointBalanceSnapshot;

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
        final var disikeHistory = internalState.getDislikeHistory();
        var percentiles = new ArrayList<Double> () {
            {
                add(0.01);
                add(0.25);
                add(0.5);
            }
        };

        var wealthEntries = new ArrayList<String> ();
        var wealthDeltaEntries = new ArrayList<String> ();
        String wealthHeader = "";

        for(var percentile : percentiles) {
            var percentileLiker = (int) Math.ceil(disikeHistory.size() * percentile);
            var likerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
            .analyzePointBalanceHistoryByUserId(disikeHistory.get(percentileLiker).Item1);

            var wealthDatas = readWealth(likerCsvData);
            var wealthDeltaDatas = readWealthDelta(likerCsvData);

            if(wealthHeader.isEmpty()){
                wealthHeader = percentile.toString() + "PercentileUser";
            } else {
                wealthHeader += "," + percentile.toString() + "PercentileUser";
            }   

            if(wealthEntries.size() == 0){
                for(var wealthData : wealthDatas){
                    wealthEntries.add(wealthData);
                }
            } else {
                if(wealthDatas.size() < wealthEntries.size()){
                    var gap = wealthEntries.size() - wealthDatas.size();
                    while(gap > 0) wealthDatas.add(0, "0");
                }

                for(int i=0; i < wealthDatas.size() ; ++i){
                    wealthEntries.set(i, wealthEntries.get(i) + "," + wealthDatas.get(i));
                }
            }

            if(wealthDeltaEntries.size() == 0){
                for(var wealthDeltaData : wealthDeltaDatas){
                    wealthDeltaEntries.add(wealthDeltaData);
                }
            } else {
                if(wealthDeltaDatas.size() < wealthDeltaEntries.size()){
                    var gap = wealthDeltaEntries.size() - wealthDeltaDatas.size();
                    while(gap > 0) wealthDatas.add(0, "0");
                }

                for(int i=0; i < wealthDeltaDatas.size() ; ++i){
                    wealthDeltaEntries.set(i, wealthDeltaEntries.get(i) + "," + wealthDeltaDatas.get(i));
                }
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();  
        var datePostfix = dtf.format(now);
        var path = prepareDirs();

        wealthEntries.add(0, wealthHeader);
        wealthDeltaEntries.add(0, wealthHeader);

        var fileName = String.format("Disliker_Wealth_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), wealthEntries);

        fileName = String.format("Disliker_WealthDelta_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), wealthDeltaEntries);
    }

    public void saveLikePercentile(){
        final var likeHistory = internalState.getLikeHistory();
        var percentiles = new ArrayList<Double> () {
            {
                add(0.01);
                add(0.10);
                add(0.25);
                add(0.35);
                add(0.5);
            }
        };

        var wealthEntries = new ArrayList<String> ();
        var wealthDeltaEntries = new ArrayList<String> ();
        String wealthHeader = "";

        for(var percentile : percentiles) {
            var percentileLiker = (int) Math.ceil(likeHistory.size() * percentile);
            var likerCsvData = ServiceProvider.createAnonymousAnalysisService(this.contract)
            .analyzePointBalanceHistoryByUserId(likeHistory.get(percentileLiker).Item1);

            var wealthDatas = readWealth(likerCsvData);
            var wealthDeltaDatas = readWealthDelta(likerCsvData);

            if(wealthHeader.isEmpty()){
                wealthHeader = percentile.toString() + "PercentileUser";
            } else {
                wealthHeader += "," + percentile.toString() + "PercentileUser";
            }   

            if(wealthEntries.size() == 0){
                for(var wealthData : wealthDatas){
                    wealthEntries.add(wealthData);
                }
            } else {
                if(wealthDatas.size() < wealthEntries.size()){
                    var gap = wealthEntries.size() - wealthDatas.size();
                    while(gap > 0)  {
                        wealthDatas.add(0, "0.0");
                        -- gap;
                    }
                }

                for(int i=0; i < wealthEntries.size() ; ++i){
                    wealthEntries.set(i, wealthEntries.get(i) + "," + wealthDatas.get(i));
                }
            }

            if(wealthDeltaEntries.size() == 0){
                for(var wealthDeltaData : wealthDeltaDatas){
                    wealthDeltaEntries.add(wealthDeltaData);
                }
            } else {
                if(wealthDeltaDatas.size() < wealthDeltaEntries.size()){
                    var gap = wealthDeltaEntries.size() - wealthDeltaDatas.size();
                    while(gap > 0) {
                        wealthDeltaDatas.add(0, "0.0");
                        -- gap;
                    }
                }

                for(int i=0; i < wealthDeltaEntries.size() ; ++i){
                    wealthDeltaEntries.set(i, wealthDeltaEntries.get(i) + "," + wealthDeltaDatas.get(i));
                }
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
        LocalDateTime now = LocalDateTime.now();  
        var datePostfix = dtf.format(now);
        var path = prepareDirs();

        wealthEntries.add(0, wealthHeader);
        wealthDeltaEntries.add(0, wealthHeader);

        var fileName = String.format("Liker_Wealth_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), wealthEntries);

        fileName = String.format("Liker_WealthDelta_%s.csv",datePostfix);
        saveCSV(path.resolve(fileName), wealthDeltaEntries);
    }

    private List<String> readWealth(List<PointBalanceSnapshot> snapshots){
        return snapshots
        .stream()
        .map(snapshot -> {
            var cols = snapshot.toCsvRow().split(",");
            System.out.println(snapshot.toCsvRow());
            return String.format(cols[3]);
        }).collect(Collectors.toList());
    }

    private List<String> readWealthDelta(List<PointBalanceSnapshot> snapshots){
        return snapshots
        .stream()
        .map(snapshot -> {
            var cols = snapshot.toCsvRow().split(",");
            System.out.println(snapshot.toCsvRow());
            return String.format(cols[4]);
        }).collect(Collectors.toList());
    }

    private Path prepareDirs(){
        var path = Paths.get("benchmarks","simulation","perf");
        path.toFile().mkdirs();

        return path;
    }

    private void saveCSV(Path path, List<String> entries){
        try {

            FileWriter csvWriter = new FileWriter(path.toString());

            for(var entry : entries){
                csvWriter.append(entry);
                csvWriter.append(System.getProperty( "line.separator" ));
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
