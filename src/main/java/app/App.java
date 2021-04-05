package app;

import java.net.InetSocketAddress;

class App {
    public static void main(String[] args) throws Exception {
        Conf conf = new Conf();
        System.out.println(conf);

        AppServer server = new AppServer(conf.getAppServerSocketAddress());
    }
}
