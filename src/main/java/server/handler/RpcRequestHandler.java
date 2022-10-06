package server.handler;

import common.message.RpcRequestMessage;
import common.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import server.registery.ServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        System.out.println("开始处理");
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        //设置请求的序号
        responseMessage.setSequenceId(message.getSequenceId());
        Object result;
        try {
            //通过名称从工厂获取本地注解了@RpcServer的实例
            Object service = ServiceFactory.serviceFactory.get(message.getInterfaceName());
            System.out.println("处理器是" + service.getClass().getSimpleName());
            //获取方法     方法名，参数
            Method method = service.getClass().getMethod(message.getMethodName(),message.getParameterTypes());
            //调用
            result = method.invoke(service, message.getParameterValue());
            //设置返回值
            responseMessage.setReturnValue(result);
            System.out.println("返回值是" + result);
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            responseMessage.setExceptionValue(new Exception("远程调用出错:"+e.getMessage()));
        }finally {
            ctx.writeAndFlush(responseMessage);
            // todo
            ReferenceCountUtil.release(message);
        }
    }
}
