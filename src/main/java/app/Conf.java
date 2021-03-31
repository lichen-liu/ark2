package app;

public class Conf {
    String javaVersion;
    int appServerPort;

    public Conf() {
        javaVersion = System.getProperty("java.version");
        appServerPort = 8888;
    }

    @Override
    public String toString() {
        String str;
        str = String.format("JRE Version: %s\n", this.javaVersion);
        str += String.format("App Server port: %d\n", this.appServerPort);
        return str;
    }

    public int getAppServerPort() {
        return this.appServerPort;
    }
}
