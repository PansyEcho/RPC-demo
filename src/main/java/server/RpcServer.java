package server;

import server.annotation.RpcServerScan;
import server.manager.ServerManager;

@RpcServerScan
public class RpcServer {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        Integer port = 8080;
        new ServerManager(host,port).start();
    }


}
