package server.registery;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class ServiceFactory {

    //保存所有有注解@RpcService的集合
    public static final Map<String, Object> serviceFactory = new ConcurrentHashMap<>();


    //添加已注解的类进入工厂
    public <T> void addServiceProvider(T service, String serviceName) {
        if (serviceFactory.containsKey(serviceName)) {
            return;
        }
        serviceFactory.put(serviceName, service);
        log.debug("服务类{}添加进工厂",serviceName);
    }

}
