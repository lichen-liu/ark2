package app.backend;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class UserRegistrationService {
    private final Wallet wallet;
    private final HFCAClient client;

    public UserRegistrationService(final Wallet wallet, final HFCAClient client) {
        this.wallet = wallet;
        this.client = client;
    }

    public void RegisterUser(final User user, final String newUserId) throws Exception {

        final X509Identity adminIdentity = (X509Identity) wallet.get(user.getName());

        if (adminIdentity == null) {
            System.out.println("\"" + user.getName() + "\" needs to be enrolled and added to the wallet first");
            return;
        }

        final RegistrationRequest registrationRequest = new RegistrationRequest(newUserId);
        registrationRequest.setAffiliation(user.getAffiliation());
        registrationRequest.setEnrollmentID(newUserId);

        final String enrollmentSecret = client.register(registrationRequest, user);
        final Enrollment enrollment = client.enroll(newUserId, enrollmentSecret);
        final Identity userIdentity = Identities.newX509Identity(user.getMspId(), enrollment);

        wallet.put(newUserId, userIdentity);
        System.out.println("Successfully enrolled user \"" + newUserId + "\" and imported it into the wallet");
    }
}
