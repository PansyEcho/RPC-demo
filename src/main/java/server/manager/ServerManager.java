package server.manager;

import common.protocol.MessageCodecSharable;
import common.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import server.annotation.RpcServer;
import server.annotation.RpcServerScan;
import server.handler.HeartServerHandler;
import server.handler.PingMessageHandler;
import server.handler.RpcRequestHandler;
import server.registery.ServerRegistery;
import server.registery.ServiceFactory;
import server.registery.impl.NacosServerRegistry;
import server.utils.PackageScanUtils;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private String host;
    private Integer port;
    private ServerRegistery serverRegistry;
    private ServiceFactory serviceFactory;
    private NioEventLoopGroup worker = new NioEventLoopGroup();
    private NioEventLoopGroup boss = new NioEventLoopGroup();
    private ServerBootstrap bootstrap = new ServerBootstrap();

    public ServerManager(String host, int port) {
        this.host = host;
        this.port = port;
        serverRegistry = new NacosServerRegistry();
        serviceFactory = new ServiceFactory();
        autoRegistry();
    }

    private void autoRegistry() {
        String mainClassPath = PackageScanUtils.getStackTrace();
        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainClassPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("启动类为找到");
        }
        if (mainClass.isAnnotationPresent(RpcServer.class)) {
            throw new RuntimeException("启动类缺少@RpcServer 注解");
        }
        String annotationValue = mainClass.getAnnotation(RpcServerScan.class).value();
        //如果注解路径的值是空，则等于main父路径包下
        if ("".equals(annotationValue)) {
            annotationValue = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
        }
        //获取所有类的set集合
        Set<Class<?>> set = PackageScanUtils.getClasses(annotationValue);
        System.out.println(set.size());
        for (Class<?> c : set) {
            //只有有@RpcServer注解的才注册
            if (c.isAnnotationPresent(RpcServer.class)) {
                String ServerNameValue = c.getAnnotation(RpcServer.class).name();
                Object object;
                try {
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.err.println("创建对象" + c + "发生错误");
                    continue;
                }
                //注解的值如果为空，使用类名
                if ("".equals(ServerNameValue)) {
                    Class<?>[] interfaces = c.getInterfaces();
                    for (Class interfaceOne : interfaces){
                        addServer(object,interfaceOne.getSimpleName());
                    }

                } else {
                    addServer(object, ServerNameValue);
                }
            }
        }
    }

    private <T> void addServer(T service, String serviceName) {
        System.out.println("将" + serviceName + "加入，它的实现是:" + service);
        serviceFactory.addServiceProvider(service,serviceName);
        serverRegistry.register(serviceName, new InetSocketAddress(host, port));
    }


    public void start() {
        //日志
//        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);
        //消息节码器
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //RPC请求处理器
        RpcRequestHandler RPC_HANDLER = new RpcRequestHandler();
        //心跳处理器
        HeartServerHandler HEATBEAT_SERVER = new HeartServerHandler();
        //心跳请求的处理器
        PingMessageHandler PINGMESSAGE = new PingMessageHandler();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new ProcotolFrameDecoder());
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(200 * 1024,0,2));//定长解码器
                            pipeline.addLast(MESSAGE_CODEC);
//                            pipeline.addLast(LOGGING);
                            pipeline.addLast(HEATBEAT_SERVER);
                            pipeline.addLast(PINGMESSAGE);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });
            //绑定端口
            Channel channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("启动服务出错");
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }


}
