package app.factory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class WalletFactory {

    public static final String pathPostfix = "wallet";
    
    public static Wallet GetWallet(String mspId) throws IOException {
        Path walletPath = GetWalletPath(mspId);
        return Wallets.newFileSystemWallet(walletPath);
    }

    private static Path GetWalletPath(String mspId) {
        return Paths.get(String.format("%s-%s", mspId, pathPostfix));   
    }
}