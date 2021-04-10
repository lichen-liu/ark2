package app.service;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class AdminEnrollmentService {
    private final Wallet _wallet;
    private final HFCAClient _client; 

    public AdminEnrollmentService(Wallet wallet, HFCAClient client) {
        this._wallet = wallet;
        this._client = client;
    }
    
    public void EnrollAdmin(Entity entity) throws Exception { 

        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost(entity.hostName);
        enrollmentRequestTLS.setProfile(entity.profile);

        Enrollment enrollment = _client.enroll(entity.adminName, entity.adminSecret, enrollmentRequestTLS);
        Identity adminIdentity = Identities.newX509Identity(entity.mspId, enrollment);

        _wallet.put(entity.adminName, adminIdentity);
        System.out.println("Successfully enrolled admin \"" + entity.adminName + "\" and imported it into the wallet");
    }

    public static class Entity {
        public Entity() {}
        public String mspId;
        public String hostName;
        public String profile;
        public String adminName;
        public String adminSecret;
    }
}