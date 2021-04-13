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

    public LikeRepository(final Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewLike(final Contract contract, final String content, final PublicKey publicKey,
            final PrivateKey privateKey) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException,
            ContractException, TimeoutException, InterruptedException {

        return new String(contract.submitTransaction("publishNewPointTransaction", "20210412_155300",
                "{\"userId\":\"bank\",\"pointAmount\":100}", "bank", "reference", "signature(bank)",
                "[{\"userId\":\"ray\",\"pointAmount\":100}]"));
    }

    @Override
    protected String getAllKeysQuery() {
        throw new UnsupportedOperationException("This operation is not supported");
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