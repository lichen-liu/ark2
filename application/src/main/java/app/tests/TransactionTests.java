package app.tests;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;

import app.AppClient;
import app.repository.contracts.Transaction;
import app.repository.contracts.Transaction.Entry;
import app.utils.ByteUtils;

public class TransactionTests {
    private int testId = 0;
    private final ObjectMapper objectMapper;

    public TransactionTests() {
        objectMapper = new ObjectMapper();
    }

    public void benchmark(final AppClient appClient) {
        try {
            final Contract contract = appClient.getContract();

            final String ray = "ray";
            final String charles = "charles";
            final String bank = "bank";
            final String zac = "zac";

            final var users = new HashMap<String, AppClient>();
            for (final String name : List.of(ray, charles, bank, zac)) {
                users.put(name, createClient(contract));
            }

            final String bankId = ByteUtils.bytesToHexString(users.get(bank).getPublicKey().getEncoded());
            final String rayId = ByteUtils.bytesToHexString(users.get(ray).getPublicKey().getEncoded());
            final String zacId = ByteUtils.bytesToHexString(users.get(zac).getPublicKey().getEncoded());
            final String charlesId = ByteUtils.bytesToHexString(users.get(charles).getPublicKey().getEncoded());

            print(appClient.getContract().submitTransaction("publishNewPost", "a", "a", "a", "a"));

            {
                final String p0 = appClient.publishNewPost("hahaha");
                print(p0);
                print(contract.evaluateTransaction("getPostByKey", p0));
            }

            {
                final String post = "This is a post This is a post This is a post This is a post This is a post";
                final long startTime = System.nanoTime();
                for (int i = 0; i < 100; ++i) {
                    appClient.publishNewPost(post);
                }
                final long endTime = System.nanoTime();
                final long average = (endTime - startTime) / (long) 100;
                System.out.println("Average 1: " + Long.toString(average));
            }

            {
                final String post = "This is a post This is a post This is a post This is a post This is a post";
                users.get(ray).publishNewPost(post);
                final long startTime = System.nanoTime();
                for (int i = 0; i < 100; ++i) {
                    appClient.fetchAllUserPosts(rayId);
                }
                final long endTime = System.nanoTime();
                final long average = (endTime - startTime) / (long) 100;
                System.out.println("Average 2: " + Long.toString(average));
            }

            {
                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(rayId, (double) 100);
                transaction.payees = Arrays.asList(new Entry(rayId, (double) 100));

                final long startTime = System.nanoTime();
                for (int i = 0; i < 100; ++i) {
                    users.get(ray).publishNewTransaction(transaction);
                }
                final long endTime = System.nanoTime();
                final long average = (endTime - startTime) / (long) 100;
                System.out.println("Average 3: " + Long.toString(average));
            }

            {
                final long startTime = System.nanoTime();
                for (int i = 0; i < 100; ++i) {
                    users.get(ray).getPointAmount();
                }
                final long endTime = System.nanoTime();
                final long average = (endTime - startTime) / (long) 100;
                System.out.println("Average 4: " + Long.toString(average));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void test(final AppClient appClient) {

        try {
            print(appClient.fetchAllPosts());

            print(appClient.fetchAllPostKeys());

            final Contract contract = appClient.getContract();

            final String ray = "ray";
            final String charles = "charles";
            final String bank = "bank";
            final String zac = "zac";

            final var users = new HashMap<String, AppClient>();
            for (final String name : List.of(ray, charles, bank, zac)) {
                users.put(name, createClient(contract));
            }

            final String bankId = ByteUtils.bytesToHexString(users.get(bank).getPublicKey().getEncoded());
            final String rayId = ByteUtils.bytesToHexString(users.get(ray).getPublicKey().getEncoded());
            final String zacId = ByteUtils.bytesToHexString(users.get(zac).getPublicKey().getEncoded());
            final String charlesId = ByteUtils.bytesToHexString(users.get(charles).getPublicKey().getEncoded());

            {
                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(bankId, (double) 100);
                transaction.payees = Arrays.asList(new Entry(rayId, (double) 100));

                final var t0 = users.get(bank).publishNewTransaction(transaction);
                print(t0);
                print(contract.evaluateTransaction("getPointTransactionByKey", t0));
            }

            {
                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(rayId, (double) 100);
                transaction.payees = Arrays.asList(new Entry(charlesId, (double) 50), new Entry(zacId, (double) 50));

                final var t1 = users.get(ray).publishNewTransaction(transaction);
                print(t1);
                print(contract.evaluateTransaction("getPointTransactionByKey", t1));
            }

            {
                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(bankId, (double) 100);
                transaction.payees = Arrays.asList(new Entry(rayId, (double) 100));

                final var t2 = users.get(bank).publishNewTransaction(transaction);
                print(t2);
                print(contract.evaluateTransaction("getPointTransactionByKey", t2));
            }

            {

                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(bankId, (double) 100);
                transaction.payees = Arrays.asList(new Entry(rayId, (double) 100));

                final var t3 = users.get(bank).publishNewTransaction(transaction);
                print(t3);
                print(contract.evaluateTransaction("getPointTransactionByKey", t3));
            }

            {
                final Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Entry(rayId, (double) 150);
                transaction.payees = Arrays.asList(new Entry(charlesId, (double) 50), new Entry(zacId, (double) 100));

                final var t4 = users.get(ray).publishNewTransaction(transaction);
                print(t4);
                print(contract.evaluateTransaction("getPointTransactionByKey", t4));
            }

            print("bank: " + new String(contract.evaluateTransaction("getPointAmountByUserId", bankId)));
            print("ray: " + new String(contract.evaluateTransaction("getPointAmountByUserId", rayId)));
            print("charles: " + new String(contract.evaluateTransaction("getPointAmountByUserId", charlesId)));
            print("zac: " + new String(contract.evaluateTransaction("getPointAmountByUserId", zacId)));
            print(contract.evaluateTransaction("getAllPointTransactionKeys"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void print(final byte[] result) {
        this.print(toString(result));
    }

    private void print(final String result) {
        System.out.println("\n[" + this.testId + "] result: " + prettifyJson(result));
        this.testId++;
    }

    private void print(final String[] results) {

        System.out.println("\n[" + this.testId + "] result: ");

        for (final var result : results) {
            System.out.println(prettifyJson(result));
        }

        this.testId++;
    }

    private static String toString(final byte[] result) {
        return new String(result);
    }

    private String prettifyJson(final String raw) {
        try {
            final Object json = objectMapper.readValue(raw, Object.class);
            final String prettified = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return prettified;
        } catch (final JsonParseException e) {
            return raw;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private AppClient createClient(final Contract contract)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        final KeyPair pair = keyGen.generateKeyPair();
        final PrivateKey priv = pair.getPrivate();
        final PublicKey pub = pair.getPublic();
        return new AppClient(contract, pub, priv);
    }

}
