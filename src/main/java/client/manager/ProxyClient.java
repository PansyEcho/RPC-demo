package client.manager;

import common.message.RpcRequestMessage;
import common.protocol.SequenceIdGenerator;
import io.netty.util.concurrent.DefaultPromise;
import server.handler.RpcRequestHandler;

import java.lang.reflect.Proxy;

public class ProxyClient {

    private final ClientManager clientManager;

    public ProxyClient(ClientManager clientManager){
        this.clientManager = clientManager;
    }


    public  <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
//        Class<?>[] interfaces = serviceClass.getInterfaces();
        //创建代理对象
        Object o = Proxy.newProxyInstance(loader, new Class<?>[]{serviceClass}, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.getId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getSimpleName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 准备一个空 Promise 对象，来接收结果 存入集合            指定 promise 对象异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(ClientManager.group.next());
            ClientManager.PROMISES.put(sequenceId, promise);
            // 3. 将消息对象发送出去
            clientManager.sendRpcRequest(msg);
            // 4. 等待 promise 结果
            promise.await();
            if(promise.isSuccess()) {
                // 调用正常
                return promise.getNow();
            } else {
                // 调用失败
                System.out.println("调用失败");
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }


//    public <T> T getProxyService(Class<?> clazz){
//        ClassLoader classLoader = clazz.getClassLoader();
//        Class<?>[] interfaces = clazz.getInterfaces();
//        Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
//            Integer id = SequenceIdGenerator.getId();
//            RpcRequestMessage requestMessage = new RpcRequestMessage(id,
//                    clazz.getSimpleName(),
//                    method.getName(),
//                    method.getReturnType(),
//                    method.getParameterTypes(),
//                    args);
//            System.out.println("clazz name is" + clazz.getCanonicalName());
//            System.out.println("clazz name is" + clazz.getName());
//            System.out.println("clazz name is" + clazz.getSimpleName());
//            DefaultPromise<Object> promise = new DefaultPromise<>(ClientManager.group.next());
//            ClientManager.PROMISES.put(id, promise);
//            clientManager.sendRpcRequest(requestMessage);
//            promise.await();
//            if (promise.isSuccess()) {
//                return promise.getNow();
//            } else {
//                throw new RuntimeException(promise.cause());
//            }
//        });
//        return (T) proxyInstance;
//
//    }

}
