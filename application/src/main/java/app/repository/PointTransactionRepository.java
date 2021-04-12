package app.repository;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.utils.GensonDeserializer;

public class PointTransactionRepository extends ReadableRepository {

    public PointTransactionRepository(Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(Contract contract, String content, PublicKey publicKey, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, ContractException,
            TimeoutException, InterruptedException {

            throw new UnsupportedOperationException();
    }

    @Override
    protected String getAllKeysQuery() {
        return "getAllPointTransactionKeys";
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllPointTransactionKeysByUserId";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getPointTransactionByKey";
    }
}