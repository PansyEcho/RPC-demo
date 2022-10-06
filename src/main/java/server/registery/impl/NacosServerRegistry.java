package server.registery.impl;

import com.alibaba.nacos.api.exception.NacosException;
import server.registery.ServerRegistery;
import server.utils.NacosUtils;

import java.net.InetSocketAddress;

public class NacosServerRegistry implements ServerRegistery {
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerServer(serviceName,inetSocketAddress);
            System.out.println("注册"+serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("注册Nacos出现异常");
        }
    }
}
