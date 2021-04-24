package app.tests.Benchmarks;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import app.user.NamedService;

public class BenchmarkWriter {
    private FileWriter writer;

    public BenchmarkWriter(String fileName){
        try {
            this.writer = getWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void savePosts(List<String> posts, Map<String, Integer> probs) throws IOException {
        try {
            writer.append("=== Post Infomation Begin === \n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (var post : posts) {
            var prob = probs.get(post);
            writer.append((String.format("Post Key: %s , Prob: %s \n", post, prob == null ? -1 : prob)));
        }

        try {
            writer.append("=== Post Infomation End === \n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveAuthors(List<NamedService> authors, Map<NamedService, Integer> probs)
            throws IOException {
        writer.append("=== Author Infomation Begin === \n");
        for (var author : authors) {
            var prob = probs.get(author);
            saveClient(author, prob == null ? -1 : prob, writer);
        }
        writer.append("=== Author Infomation End === \n");
    }

    public void saveLikers(List<NamedService> likers, Map<NamedService, Integer> probs)
            throws IOException {
        writer.append("=== Liker Infomation Begin === \n");
        for (var liker : likers) {
            var prob = probs.get(liker);
            saveClient(liker, prob == null ? -1 : prob, writer);
        }
        writer.append("=== Liker Infomation End === \n");
    }

    public void saveClient(NamedService client, Integer prob, FileWriter writer) throws IOException {
        writer.append((String.format("Public Key: %s, Private Key: %s , Prob: %s \n", client.getPublicKeyString(),
                client.getPrivateKeyString(), prob.toString())));
    }

    public FileWriter getWriter(String fileName) throws IOException {

        try {
            File myObj = new File(String.format("benchmark/%s.txt", fileName));
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return new FileWriter(String.format("benchmark/%s.txt", fileName));
    }

    public void SetTitle(String title) {
        try {
            writer.append(String.format("=== Benchmark: %s ===", title));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finish(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
    }
}
