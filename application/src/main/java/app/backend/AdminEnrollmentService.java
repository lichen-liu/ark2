package app.backend;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class AdminEnrollmentService {
    private final Wallet wallet;
    private final HFCAClient client;

    public AdminEnrollmentService(final Wallet wallet, final HFCAClient client) {
        this.wallet = wallet;
        this.client = client;
    }

    public void EnrollAdmin(final Entity entity) throws Exception {

        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost(entity.hostName);
        enrollmentRequestTLS.setProfile(entity.profile);

        final Enrollment enrollment = client.enroll(entity.adminName, entity.adminSecret, enrollmentRequestTLS);
        final Identity adminIdentity = Identities.newX509Identity(entity.mspId, enrollment);

        wallet.put(entity.adminName, adminIdentity);
        System.out.println("Successfully enrolled admin \"" + entity.adminName + "\" and imported it into the wallet");
    }

    public static class Entity {
        public Entity() {
        }

        public String mspId;
        public String hostName;
        public String profile;
        public String adminName;
        public String adminSecret;
    }
}