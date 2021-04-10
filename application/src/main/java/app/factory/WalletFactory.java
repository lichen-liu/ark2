package app.factory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class WalletFactory {

    public static final String pathPostfix = "wallet";
    
    public static Wallet GetWallet(String adminId) throws IOException {
        Path walletPath = GetWalletPath(adminId);
        return Wallets.newFileSystemWallet(walletPath);
    }

    private static Path GetWalletPath(String adminId) {
        return Paths.get(String.format("%s-%s", adminId, pathPostfix));   
    }
}