package app;

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
import java.util.Timer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.hyperledger.fabric.gateway.Contract;

import app.repository.contracts.Transaction;
import app.repository.contracts.Transaction.Participant;
import app.utils.ByteUtils;
public class CCTesting {
    private int testId = 0;
    private final ObjectMapper objectMapper;

    public CCTesting() {
        objectMapper = new ObjectMapper();
    }

    public void test(final AppClient appPeer) {
        try {
            final Contract contract = appPeer.getContract();

            String ray = "ray";
            String charles = "charles";
            String bank = "bank";
            String zac = "zac";

            var names = new String[] {ray, charles, bank, zac};
            var users = new HashMap<String, AppClient> ();

            for(var name : names) {
                users.put(name, createClient(contract));
            }

            String bankId = ByteUtils.bytesToHexString(users.get(bank).getPublicKey().getEncoded());
            String rayId = ByteUtils.bytesToHexString(users.get(ray).getPublicKey().getEncoded());
            String zacId = ByteUtils.bytesToHexString(users.get(zac).getPublicKey().getEncoded());
            String charlesId = ByteUtils.bytesToHexString(users.get(charles).getPublicKey().getEncoded());

            print(appPeer.getContract().submitTransaction("publishNewPost", "a", "a", "a", "a"));

            {
                final String p0 = appPeer.publishNewPost("hahaha");
                print(p0);
                print(contract.evaluateTransaction("getPostByKey", p0));
            }

            {
                String post = "This is a post This is a post This is a post This is a post This is a post";
                long startTime = System.nanoTime();
                for(int i = 0; i < 100; ++i) {
                    appPeer.publishNewPost(post);
                }
                long endTime = System.nanoTime();
                long average = (endTime - startTime) / (long)100; 
                System.out.println("Average 1: " + Long.toString(average));
            }
            
            {
                String post = "This is a post This is a post This is a post This is a post This is a post";
                users.get(ray).publishNewPost(post);
                long startTime = System.nanoTime();
                for(int i = 0; i < 100; ++i) {
                    appPeer.fetchAllUserPosts(rayId);
                }
                long endTime = System.nanoTime();
                long average = (endTime - startTime) / (long)100; 
                System.out.println("Average 2: " + Long.toString(average));
            }

            {
                Transaction transaction = new Transaction();
                transaction.reference = "reference";
                transaction.payer = new Participant(rayId, (double)100);
                transaction.payees = Arrays.asList(
                    new Participant(rayId, (double)100) 
                );

                long startTime = System.nanoTime();
                for(int i = 0; i < 100; ++i) {
                    users.get(ray).publishNewTransaction(transaction);
                }
                long endTime = System.nanoTime();
                long average = (endTime - startTime) / (long)100; 
                System.out.println("Average 3: " + Long.toString(average));
            }

            {
                long startTime = System.nanoTime();
                for(int i = 0; i < 100; ++i) {
                    users.get(ray).getPointAmount();
                }
                long endTime = System.nanoTime();
                long average = (endTime - startTime) / (long)100; 
                System.out.println("Average 4: " + Long.toString(average));
            }
            // print(appPeer.fetchAllPosts());

            // print(appPeer.fetchAllPostKeys());

            // {
            //     Transaction transaction = new Transaction();
            //     transaction.reference = "reference";
            //     transaction.payer = new Participant(bankId, (double)100);
            //     transaction.payees = Arrays.asList(
            //         new Participant(rayId, (double)100) 
            //     );
                
            //     final var t0 = users.get(bank).publishNewTransaction(transaction);
            //     print(t0);
            //     print(contract.evaluateTransaction("getPointTransactionByKey", t0));
            // }

            // {
            //     Transaction transaction = new Transaction();
            //     transaction.reference = "reference";
            //     transaction.payer = new Participant(rayId, (double)100);
            //     transaction.payees = Arrays.asList(
            //         new Participant(charlesId, (double)50),
            //         new Participant(zacId, (double)50) 
            //     );

            //     final var t1 = users.get(ray).publishNewTransaction(transaction);
            //     print(t1);
            //     print(contract.evaluateTransaction("getPointTransactionByKey", t1));
            // }

            // {  
            //     Transaction transaction = new Transaction();
            //     transaction.reference = "reference";
            //     transaction.payer = new Participant(bankId, (double)100);
            //     transaction.payees = Arrays.asList(
            //         new Participant(rayId, (double)100) 
            //     );

            //     final var t2 = users.get(bank).publishNewTransaction(transaction);
            //     print(t2);
            //     print(contract.evaluateTransaction("getPointTransactionByKey", t2));
            // }

            // {
                
            //     Transaction transaction = new Transaction();
            //     transaction.reference = "reference";
            //     transaction.payer = new Participant(bankId, (double)100);
            //     transaction.payees = Arrays.asList(
            //         new Participant(rayId, (double)100) 
            //     );

            //     final var t3 = users.get(bank).publishNewTransaction(transaction);
            //     print(t3);
            //     print(contract.evaluateTransaction("getPointTransactionByKey", t3));
            // }

            // {
            //     Transaction transaction = new Transaction();
            //     transaction.reference = "reference";
            //     transaction.payer = new Participant(rayId, (double)150);
            //     transaction.payees = Arrays.asList(
            //         new Participant(charlesId, (double)50),
            //         new Participant(zacId, (double)100) 
            //     );

            //     final var t4 = users.get(ray).publishNewTransaction(transaction);
            //     print(t4);
            //     print(contract.evaluateTransaction("getPointTransactionByKey", t4));
            // }

            // print("bank: " + new String(contract.evaluateTransaction("getPointAmountByUserId", bankId)));
            // print("ray: " + new String(contract.evaluateTransaction("getPointAmountByUserId", rayId)));
            // print("charles: " + new String(contract.evaluateTransaction("getPointAmountByUserId", charlesId)));
            // print("zac: " + new String(contract.evaluateTransaction("getPointAmountByUserId", zacId)));
            // print(contract.evaluateTransaction("getAllPointTransactionKeys"));
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

    private AppClient createClient(Contract contract) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException{
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        return new AppClient(contract, pub, priv);
    }

}
