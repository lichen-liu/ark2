package app.user;

import java.io.IOException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.utils.ByteUtils;

public class ReadOnlyAppUser extends AnynomousAppUser {
    private final PublicKey publicKey;

    public ReadOnlyAppUser(final Wallet wallet, final Contract contract, final String userId)
            throws InvalidKeySpecException, IOException {
        super(contract);
        final X509Identity adminIdentity = (X509Identity) wallet.get(userId);
        this.publicKey = adminIdentity.getCertificate().getPublicKey();
    }

    public ReadOnlyAppUser(final Contract contract, final PublicKey publicKey) {
        super(contract);
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String[] fetchUserPostKeys(final String userId) throws Exception {
        return super.getPostRepository().selectObjectKeysByCustomKey(userId);
    }

    public String[] fetchAllUserPosts(final String userId) throws Exception {
        return super.getPostRepository().selectObjectsByCustomKeys(userId);
    }

    public String getPointAmount() throws ContractException {
        return new String(super.getContract().evaluateTransaction("getPointAmountByUserId",
                ByteUtils.bytesToHexString(publicKey.getEncoded())));
    }
}
