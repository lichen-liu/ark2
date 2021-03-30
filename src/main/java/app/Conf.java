package app;

public class Conf {
    String javaVersion;

    public Conf() {
        javaVersion = System.getProperty("java.version");
    }

    @Override
    public String toString() {
        return String.format("JRE Version: %s", this.javaVersion);
    }
}
