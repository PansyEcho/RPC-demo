package server.service.impl;

import server.annotation.RpcServer;
import server.service.HelloService;

@RpcServer
public class HelloServiceB implements server.service.HelloServiceB {

    @Override
    public String getB(String msg) {
        return "B:" + msg;
    }
}
