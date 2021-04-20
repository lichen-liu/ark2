package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.repository.data.PointTransaction;
import app.repository.data.Transaction;
import app.utils.ByteUtils;
import app.utils.Cryptography;
import app.utils.GensonDeserializer;

public class PointTransactionRepository extends ReadableRepository<PointTransaction> {

    public PointTransactionRepository(final Contract contract) {
        super(PointTransaction.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(final String reference, final Transaction transaction, final PublicKey publicKey,
            final PrivateKey privateKey) throws Exception {

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        final String payer = this.deserializer.transactionEntriesToJson(transaction.payer);
        final String payees = this.deserializer.transactionEntryToJson(transaction.payees);
        final String publicKeyString = ByteUtils.toAsciiString(publicKey.getEncoded());

        final byte[] hash = Hash.generatePointTransactionHash(timestamp, transaction.payer.userId,
                transaction.payer.pointAmount.toString(), publicKeyString);
        final byte[] signature = Cryptography.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPointTransaction", timestamp, payer, publicKeyString,
                ByteUtils.toAsciiString(signature), reference, payees));
    }

    public String computePointAmount(final String userId) throws ContractException {
        return new String(this.contract.evaluateTransaction("computePointAmountByUserId", userId));
    }

    @Override
    protected String getAllKeysQuery() {
        return "getAllPointTransactionKeys";
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllPointTransactionKeysByPayerUserId";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getPointTransactionByKey";
    }
}