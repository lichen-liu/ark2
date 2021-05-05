package app.repository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.util.ByteUtils;
import app.util.Cryptography;

public class PointTransactionRepository extends ReadableRepository<PointTransaction> {

    public PointTransactionRepository(final Contract contract) {
        super(PointTransaction.class);
        this.deserializer = new GensonDeserializer();
        this.contract = contract;
    }

    public String insertNewTransaction(final String reference, final Payment payment, final PublicKey publicKey,
            final PrivateKey privateKey) throws Exception {

        final String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

        final String payers = this.deserializer.transactionEntriesToJson(List.of(payment.payer));
        final String payees = this.deserializer.transactionEntriesToJson(payment.payees);
        final String publicKeyString = ByteUtils.toAsciiString(publicKey.getEncoded());

        final byte[] hash = Hash.generatePointTransactionHash(timestamp, publicKeyString,
                new PointTransaction.Entry[] { payment.payer });
        final byte[] signature = Cryptography.sign(privateKey, hash);

        return new String(contract.submitTransaction("publishNewPointTransaction", timestamp, publicKeyString, payers,
                ByteUtils.toAsciiString(signature), reference, payees));
    }

    public String computePointBalanceByUserId(final String userId) throws ContractException {
        return new String(this.contract.evaluateTransaction("computePointBalanceByUserId", userId));
    }

    public String[] computePointTransactionKeysByUserId(final String userId) throws Exception {
        return deserializer.toStringArray(
                new String(this.contract.evaluateTransaction("computeAllPointTransactionKeysByUserId", userId)));
    }

    @Override
    protected String getAllKeysQuery() {
        return "getAllPointTransactionKeys";
    }

    @Override
    protected String getKeysByCustomKeysQuery() {
        return "getAllPointTransactionKeysByIssuerUserId";
    }

    @Override
    protected String getObjectByKeyQuery() {
        return "getPointTransactionByKey";
    }
}