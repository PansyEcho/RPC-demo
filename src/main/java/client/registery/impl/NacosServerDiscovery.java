package client.registery.impl;

import client.registery.ServerDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import client.loadBalancer.LoadBalancer;
import client.loadBalancer.RoundRobinRule;
import server.utils.NacosUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServerDiscovery implements ServerDiscovery {
    private final LoadBalancer loadBalancer;


    public NacosServerDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer == null ? new RoundRobinRule() : loadBalancer;
    }


    @Override
    public InetSocketAddress getService(String serviceName) throws NacosException {
        List<Instance> instanceList = NacosUtils.getAllInstance(serviceName);
        System.out.println(serviceName);
        if (instanceList.size() == 0) {
            throw new RuntimeException("找不到对应服务");
        }
        Instance instance = loadBalancer.getInstance(instanceList);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }


}
