package app;

public class PeerInfo {

    public PeerInfo(String mpsId, String affliation, String channel, String contractName, String adminName, 
    String adminSecret, String hostName, String profile, String userId, String pemPath, String caUrl) {
        this.mpsId = mpsId;
        this.affliation = affliation;
        this.channel = channel;
        this.contractName = contractName;    
        this.adminName = adminName;
        this.adminSecret = adminSecret;
        this.hostName = hostName;
        this.profile = profile;
        this.userId = userId;
        this.pemPath = pemPath;
        this.caUrl = caUrl;
    }

    // Without a default constructor, Jackson will throw an exception
    public PeerInfo() {}

    private String mpsId;
    private String affliation;
    private String channel;
    private String contractName;    
    private String adminName;
    private String adminSecret;
    private String hostName;
    private String profile;
    private String userId;
    private String pemPath;
    private String caUrl;
    
    @Override
    public String toString() {
        return "";
    }

    public String getMpsId() {
        return this.mpsId;
    }

    public String getAffliation() {
        return this.affliation;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getContractName() {
        return this.contractName;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public String getAdminSecret() {
        return this.adminSecret;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getProfile() {
        return this.profile;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPemPath() {
        return this.pemPath;
    }

    public String getCaUrl() {
        return this.caUrl;
    }
}