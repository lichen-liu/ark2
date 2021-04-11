package app.factory;

import java.util.Properties;

import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class CaClientFactory {
    public static HFCAClient CreateCaClient(final String caUrl, final String pemPath) throws Exception {

        final Properties props = new Properties();
        props.put("pemFile", pemPath);
        props.put("allowAllHostNames", "true");

        final HFCAClient caClient = HFCAClient.createNewInstance(caUrl, props);
        final CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        return caClient;
    }
}