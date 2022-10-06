package client;

import client.manager.ClientManager;
import client.manager.ProxyClient;
import client.service.HelloService;
import client.service.HelloServiceB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        a();
    }

    public static void a(){
        ClientManager clientManager = new ClientManager();
//        HelloService proxyService = new ProxyClient(clientManager).getProxyService(HelloService.class);
        HelloServiceB proxyService = new ProxyClient(clientManager).getProxyService(HelloServiceB.class);
        String orr = proxyService.getB("orr");
//        String orr = proxyService.getMessage("orr");
        System.out.println(orr);
    }

}
