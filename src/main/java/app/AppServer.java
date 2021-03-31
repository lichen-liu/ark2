package app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class AppServer {
    HttpServer server;

    public AppServer(InetSocketAddress socketAddress) throws Exception {
        this.server = HttpServer.create(socketAddress, 0);
        System.out.println("Server is binded to: " + socketAddress);

        this.server.createContext("/", new RootHandler());
        this.server.start();
        System.out.println("Server is up and running!");
    }

    public static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "hello world";
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}