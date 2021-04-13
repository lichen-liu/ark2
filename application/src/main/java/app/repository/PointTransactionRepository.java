package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.contracts.Transaction;
import app.utils.ByteUtils;
import app.utils.GensonDeserializer;
import app.utils.NewPostSignature;

public class PointTransactionRepository extends ReadableRepository {

    public PointTransactionRepository(Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(Contract contract, String reference, Transaction transaction,  
    PublicKey publicKey, PrivateKey privateKey) throws Exception {
            
        var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);  
    
        var payer = this.deserializer.participantToJson(transaction.payer);
        var payees = this.deserializer.participantsToJson(transaction.payees);
        var publicKeyString = ByteUtils.bytesToHexString(publicKey.getEncoded());
        
        var hash = ByteUtils.getSHA(String.join("", timestamp, payer, publicKeyString, reference));
        var signature = NewPostSignature.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPointTransaction", timestamp,
                        payer, publicKeyString, ByteUtils.bytesToHexString(signature), reference, payees));
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