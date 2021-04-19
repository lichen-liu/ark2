package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.contracts.Transaction;
import app.repository.data.PointTransaction;
import app.utils.ByteUtils;
import app.utils.Cryptography;
import app.utils.GensonDeserializer;

public class PointTransactionRepository extends ReadableRepository<PointTransaction> {

    public PointTransactionRepository(final Contract contract) {
        super(PointTransaction.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(final Contract contract, final String reference, final Transaction transaction,
            final PublicKey publicKey, final PrivateKey privateKey) throws Exception {

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        final String payer = this.deserializer.transactionEntriesToJson(transaction.payer);
        final String payees = this.deserializer.transactionEntryToJson(transaction.payees);
        final String publicKeyString = ByteUtils.toHexString(publicKey.getEncoded());

        final byte[] hash = ByteUtils.getSHA(String.join("", timestamp, payer, publicKeyString, reference));
        final byte[] signature = Cryptography.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPointTransaction", timestamp, payer, publicKeyString,
                ByteUtils.toHexString(signature), reference, payees));
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