package app;

import java.net.InetSocketAddress;

public class Conf {
    String javaVersion;
    InetSocketAddress appServerSocketAddress;

    public Conf() {
        this.javaVersion = System.getProperty("java.version");
        this.appServerSocketAddress = new InetSocketAddress("127.0.0.1", 8888);
    }

    @Override
    public String toString() {
        String str;
        str = String.format("JRE Version: %s\n", this.javaVersion);
        str += String.format("App Server Socket Address: Http:/%s\n", this.appServerSocketAddress);
        return str;
    }

    public InetSocketAddress getAppServerSocketAddress() {
        return this.appServerSocketAddress;
    }
}
