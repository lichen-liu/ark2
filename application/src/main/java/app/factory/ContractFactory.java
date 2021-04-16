package app.factory;

import java.io.IOException;
import java.nio.file.Path;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

public class ContractFactory {
    public static Contract CreateContract(final Wallet wallet, final Entity entity) throws IOException {
        final Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, entity.userId).networkConfig(entity.networkConfigPath).discovery(true);
        final Gateway gateway = builder.connect();
        final Network network = gateway.getNetwork(entity.channel);
        return network.getContract(entity.contractName);
    }

    public static class Entity {
        public String userId;
        public String channel;
        public String contractName;
        public Path networkConfigPath;
    }
}