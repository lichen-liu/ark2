package app.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AppServer {
    HttpServer server;

    public AppServer(final InetSocketAddress socketAddress) throws Exception {
        this.server = HttpServer.create(socketAddress, 0);
        System.out.println("Server is binded to: http:/" + socketAddress);

        this.server.createContext("/", new RootHandler());

        this.server.start();
        System.out.println("Server is up and running!");
    }

    public static class RootHandler implements HttpHandler {
        @Override
        public void handle(final HttpExchange exchange) throws IOException {
            final String response = "hello world";
            exchange.sendResponseHeaders(200, 0);
            final OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}