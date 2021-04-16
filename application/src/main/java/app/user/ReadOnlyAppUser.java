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

    public String getPublicKeyString() {
        return ByteUtils.toHexString(this.publicKey.getEncoded());
    }

    public String[] fetchUserPostKeys() throws Exception {
        return super.getPostRepository().selectObjectKeysByCustomKey(this.getPublicKeyString());
    }

    public String[] fetchAllUserPosts() throws Exception {
        return super.getPostRepository().selectObjectsByCustomKeys(this.getPublicKeyString());
    }

    public String getPointAmount() throws ContractException {
        return new String(super.getContract().evaluateTransaction("getPointAmountByUserId", this.getPublicKeyString()));
    }
}
