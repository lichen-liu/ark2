package app.factory;

import java.util.Properties;

import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class CaClientFactory {
    public static HFCAClient CreateCaClient(String caUrl, String pemPath) throws Exception {
        
        Properties props = new Properties();
        props.put("pemFile", pemPath);
        props.put("allowAllHostNames", "true");

        HFCAClient caClient = HFCAClient.createNewInstance(caUrl, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite(); 
        caClient.setCryptoSuite(cryptoSuite);

        return caClient;
    }
}