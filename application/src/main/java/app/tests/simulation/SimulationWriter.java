package app.tests.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import app.service.NamedService;

public class SimulationWriter {
    private FileWriter writer;
    private final SimulationState state;

    public SimulationWriter(final String fileName, final SimulationState state) {
        try {
            this.writer = getWriter(fileName);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.state = state;
    }

    public void savePosts(final List<String> posts, final Map<String, Integer> probs) throws IOException {
        try {
            writer.append("=== Post Infomation Begin === \n");
        } catch (final IOException e) {
            e.printStackTrace();
        }

        for (final var post : posts) {
            final var prob = probs.get(post);
            writer.append((String.format("Post Key: %s , Prob: %s \n", post, prob == null ? -1 : prob)));
        }

        try {
            writer.append("=== Post Infomation End === \n");
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    public void saveAuthors(final List<NamedService> authors, final Map<NamedService, Integer> probs)
            throws IOException {
        writer.append("=== Author Infomation Begin === \n");
        for (final var author : authors) {
            final var prob = probs.get(author);
            saveClient(author, prob == null ? -1 : prob, writer);
        }
        writer.append("=== Author Infomation End === \n");
    }

    public void saveLikers(final List<NamedService> likers, final Map<NamedService, Integer> probs) throws IOException {
        writer.append("=== Liker Infomation Begin === \n");
        for (final var liker : likers) {
            final var prob = probs.get(liker);
            saveClient(liker, prob == null ? -1 : prob, writer);
        }
        writer.append("=== Liker Infomation End === \n");
    }

    public void saveClient(final NamedService client, final Integer prob, final FileWriter writer) throws IOException {
        writer.append(
                (String.format("Public Key: %s, Private Key: %s , Prob: %s Money: %s \n", client.getPublicKeyString(),
                        client.getPrivateKeyString(), prob.toString(), client.computeMyPointBalance())));
    }

    public FileWriter getWriter(final String fileName) throws IOException {

        try {

            var path = Paths.get("simulation");
            path.toFile().mkdirs();

            final File myObj = new File(String.format("simulation/%s.txt", fileName));
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (final IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return new FileWriter(String.format("simulation/%s.txt", fileName));
    }

    public void SetTitle(final String title) {
        try {
            writer.append(String.format("=== Benchmark: %s === \n", title));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLikeHistory() throws IOException {
        writer.append("=== Like History Begin === \n");
        for (final var hist : state.likeHistory) {
            writer.append((String.format("Liker Key: %s, Post Key: %s \n", hist.Item1, hist.Item2)));
        }
        writer.append("=== Like History Infomation End === \n");
    }

    public void saveDislikeHistory() throws IOException {
        writer.append("=== Dislike History Begin === \n");
        for (final var hist : state.dislikeHistory) {
            writer.append((String.format("Dislike Key: %s, Post Key: %s \n", hist.Item1, hist.Item2)));
        }
        writer.append("=== Dislike History Infomation End === \n");
    }

    public void savePostHistory() throws IOException {
        writer.append("=== Post History Begin === \n");
        for (final var hist : state.postHistory) {
            writer.append((String.format("Author Key: %s, Post Key: %s \n", hist.Item1, hist.Item2)));
        }
        writer.append("=== Post History Infomation End === \n");
    }

    public void finish() {
        try {
            writer.flush();
            writer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
}
