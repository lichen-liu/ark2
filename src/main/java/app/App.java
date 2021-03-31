package app;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

class App {
    public static void main(String[] args) throws Exception {
        Conf conf = new Conf();
        System.out.println(conf);

        HttpServer server = HttpServer.create(new InetSocketAddress(conf.getAppServerPort()), 0);
        String serverAddress = "http://localhost:" + conf.getAppServerPort();
        System.out.println("Server is binded to: " + serverAddress);
        server.createContext("/", new AppHandler.RootHandler());
        server.start();
        System.out.println("Server is up and running!");
    }
}
