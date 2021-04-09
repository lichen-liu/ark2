package app;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class UserRegistration {
    public UserRegistration() {
    }

    public void register(boolean overwritePrev) throws Exception {
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile",
                "../blockchain/hlf2-network/organizations/peerOrganizations/org1.example.com/ca/ca.org1.example.com-cert.pem");
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        final String adminEntityName = "admin";
        final String entityName = "appUser";
        // Check to see if we've already enrolled the user.
        if (wallet.get(entityName) != null) {
            if (!overwritePrev) {
                System.out
                        .println("An identity for the user \"" + entityName + "\" already exists in the wallet, reuse");
                return;
            } else {
                System.out.println("An identity for the user \"" + entityName
                        + "\" already exists in the wallet, but will be overwritten by a new one");
            }
        }

        X509Identity adminIdentity = (X509Identity) wallet.get(adminEntityName);
        if (adminIdentity == null) {
            System.out.println("\"" + adminEntityName + "\" needs to be enrolled and added to the wallet first");
            return;
        }

        User newUser = new User() {

            @Override
            public String getName() {
                // Why "admin"? Is this user an admin?
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return "org1.department1";
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return "Org1MSP";
            }

        };

        // Register the user, enroll the user, and import the new identity into the
        // wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(entityName);
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID(entityName);
        String enrollmentSecret = caClient.register(registrationRequest, newUser);
        Enrollment enrollment = caClient.enroll(entityName, enrollmentSecret);
        Identity userIdentity = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put(entityName, userIdentity);
        System.out.println("Successfully enrolled user \"" + entityName + "\" and imported it into the wallet");
    }

    public static void main(String[] args) throws Exception {
        new UserRegistration().register(true);
    }
}