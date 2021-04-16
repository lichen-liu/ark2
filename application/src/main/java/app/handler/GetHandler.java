package app.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.http.protocol.HttpRequestHandler;

public class GetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] parameters = exchange
        .getRequestURI()
        .toString()
        .split("\\?")[1]
        .split("&");

        

        var dataType = parameters[0].split("=")[1];

        var key = parameters[1].split("=")[1];

        final String response = "hello world";
        
        // os.write(response.getBytes());
        // os.close();

        exchange.sendResponseHeaders(200, 0);
    }
    
}
