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

public class LikeRepository extends ReadableRepository {

    public LikeRepository(Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewLike(Contract contract, String content, PublicKey publicKey, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, ContractException,
            TimeoutException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getAllKeysQuery() {
        throw new UnsupportedOperationException("Like");
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllLikeKeysByPostKey";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getLikeByKey";
    }
}