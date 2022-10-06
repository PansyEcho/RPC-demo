package server.registery;

import java.net.InetSocketAddress;

public interface ServerRegistery {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
