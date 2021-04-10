package app.service;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import app.factory.WalletFactory;

public class AdminEnrollmentService {
    public AdminEnrollmentService() {}
    
    public void EnrollAdmin(Wallet wallet, HFCAClient client, String mspId) throws Exception { 
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost(WalletFactory.hostName);
        enrollmentRequestTLS.setProfile(WalletFactory.profile);

        Enrollment enrollment = client.enroll(WalletFactory.adminEntityName, "adminpw", enrollmentRequestTLS);
        Identity user = Identities.newX509Identity(mspId, enrollment);

        wallet.put(WalletFactory.adminEntityName, user);
        System.out.println("Successfully enrolled user \"" + WalletFactory.adminEntityName + "\" and imported it into the wallet");
    }
}