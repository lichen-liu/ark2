package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;

import app.repository.contracts.Transaction;
import app.utils.ByteUtils;
import app.utils.Cryptography;
import app.utils.GensonDeserializer;

public class PointTransactionRepository extends ReadableRepository {

    public PointTransactionRepository(final Contract contract) {
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(final Contract contract, final String reference, final Transaction transaction,
            final PublicKey publicKey, final PrivateKey privateKey) throws Exception {

        final var timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        final var payer = this.deserializer.transactionEntriesToJson(transaction.payer);
        final var payees = this.deserializer.transactionEntryToJson(transaction.payees);
        final var publicKeyString = ByteUtils.toHexString(publicKey.getEncoded());

        final var hash = ByteUtils.getSHA(String.join("", timestamp, payer, publicKeyString, reference));
        final var signature = Cryptography.sign(privateKey, hash);

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