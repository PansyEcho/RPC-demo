package client.handler;

import common.message.AbstractRpcMessage;
import common.message.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartClientHandler extends ChannelDuplexHandler {
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered");
        ctx.close();
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                PingMessage pingMessage = new PingMessage();
                pingMessage.setSequenceId(0);
                pingMessage.setMessageType(AbstractRpcMessage.PingMessage);
                ctx.writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                log.info("已超时2s未读写,发送心跳包：{}",ctx.channel().remoteAddress().toString());
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("远程调用出错");
        cause.printStackTrace();
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
