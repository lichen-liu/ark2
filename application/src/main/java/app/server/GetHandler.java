package app.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetHandler implements HttpHandler {

    @Override
    public void handle(final HttpExchange exchange) throws IOException {

        final String[] parameters = exchange.getRequestURI().toString().split("\\?")[1].split("&");

        final var dataType = parameters[0].split("=")[1];

        final var key = parameters[1].split("=")[1];

        final String response = "hello world";

        // os.write(response.getBytes());
        // os.close();

        exchange.sendResponseHeaders(200, 0);
    }

}
