package server.service.impl;

import server.annotation.RpcServer;
import server.service.HelloService;

@RpcServer
public class HelloServiceA implements HelloService {
    @Override
    public String getMessage(String origin) {
        return "A:" + origin;
    }
}
