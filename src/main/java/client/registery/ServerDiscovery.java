package client.registery;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

public interface ServerDiscovery {

    InetSocketAddress getService(String serviceName) throws NacosException;

}
