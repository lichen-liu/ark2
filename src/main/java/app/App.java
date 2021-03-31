package app;

import java.net.InetSocketAddress;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

class App {
    public static void main(String[] args) throws Exception {
        Conf conf = new Conf();
        System.out.println(conf);

        AppServer server = new AppServer(conf.getAppServerSocketAddress());
    }
}
