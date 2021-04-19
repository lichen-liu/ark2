package app.user;

import java.io.IOException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;

import app.repository.data.Like;
import app.repository.data.Post;
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

    public String[] fetchUserPostKeys() {
        try {
            return super.getPostRepository().selectObjectKeysByCustomKey(this.getPublicKeyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Post[] fetchAllUserPosts() {
        try {
            return super.getPostRepository().selectObjectsByCustomKeys(this.getPublicKeyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Like[] fetchAllLikesByPostKey(final String postKey) {
        try {
            return this.getLikeRepository().selectObjectsByCustomKeys(postKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserPointAmount() {
        try {
            return new String(super.getContract().evaluateTransaction("getPointAmountByUserId", this.getPublicKeyString()));
        } catch (ContractException e) {
            e.printStackTrace();
        }
        return null;
    }
}
