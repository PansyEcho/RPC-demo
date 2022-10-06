package client.handler;

import client.manager.ClientManager;
import common.message.RpcRequestMessage;
import common.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("出现异常"+cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage msg) throws Exception {
        try {
            log.debug("{}", msg);
            // 每次使用完都要移除
            Promise<Object> promise = ClientManager.PROMISES.remove(msg.getSequenceId());
            if (promise != null) {
                Object returnValue = msg.getReturnValue();
                Exception exceptionValue = msg.getExceptionValue();
                if (exceptionValue != null) {
                    promise.setFailure(exceptionValue);
                } else {
                    promise.setSuccess(returnValue);
                }
            } else {
                promise.setFailure(new Exception("promise不存在"));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
