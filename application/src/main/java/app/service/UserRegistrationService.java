package app.service;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import app.factory.WalletFactory;

public class UserRegistrationService {
    public UserRegistrationService() {}

    public void RegisterUser(Wallet wallet, HFCAClient client, User user, String userId) throws Exception {

        X509Identity adminIdentity = (X509Identity) wallet.get(WalletFactory.adminEntityName);
        if (adminIdentity == null) {
            System.out.println("\"" + WalletFactory.adminEntityName + "\" needs to be enrolled and added to the wallet first");
            return;
        }

        RegistrationRequest registrationRequest = new RegistrationRequest(userId);
        registrationRequest.setAffiliation(user.getAffiliation());
        registrationRequest.setEnrollmentID(userId);

        String enrollmentSecret = client.register(registrationRequest, user);
        Enrollment enrollment = client.enroll(userId, enrollmentSecret);
        Identity userIdentity = Identities.newX509Identity(user.getMspId(), enrollment);

        wallet.put(userId, userIdentity);
        System.out.println("Successfully enrolled user \"" + userId + "\" and imported it into the wallet");
    }
}