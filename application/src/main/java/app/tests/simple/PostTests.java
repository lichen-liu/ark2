package app.tests.simple;

import java.util.ArrayList;

import org.hyperledger.fabric.gateway.Contract;

import app.tests.Test;
import app.tests.util.Logger;
import app.tests.util.TestClient;
import app.tests.util.TestRunner;
import app.tests.util.TestVoid;

public class PostTests implements Test {
    private final Contract contract;

    public PostTests(final Contract contract) {
        this.contract = contract;
    }

    @Override
    public Logger initLogger(final Logger.Builder builder) {
        return builder.create("PostTests");
    }

    @Override
    public boolean runTest(final Logger logger) {
        try {
            singleThreadTests(logger);
            twoThreadsPublishingNewPostsTests(logger);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void singleThreadTests(final Logger logger) throws Exception {

        final var client = TestClient.createTestClient(contract);
        final String postKey = client.publishNewPost("singleThreadTests");
        logger.print(postKey);
        logger.print(client.fetchPostByPostKey(postKey));

    }

    private void twoThreadsPublishingNewPostsTests(final Logger logger) throws Exception {

        final TestRunner runner1 = new TestRunner("Runner 1", logger);
        final TestRunner runner2 = new TestRunner("Runner 2", logger);

        final var client1 = TestClient.createTestClient(contract);
        final var client2 = TestClient.createTestClient(contract);

        final var runner1Tests = new ArrayList<TestVoid>();
        final var runner2Tests = new ArrayList<TestVoid>();

        runner1Tests.add((TestVoid) () -> {
            return client1.publishNewPost("I am client one");
        });

        for (final var test : runner1Tests) {
            runner1.insertNewTest(test, 5);
        }

        runner2Tests.add((TestVoid) () -> {
            return client2.publishNewPost("I am client two");
        });

        for (final var test : runner2Tests) {
            runner2.insertNewTest(test, 5);
        }

        final Thread thread1 = new Thread(runner1);
        final Thread thread2 = new Thread(runner2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
