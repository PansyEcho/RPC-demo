package server.handler;

import common.message.AbstractRpcMessage;
import common.message.PingMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class PingMessageHandler extends SimpleChannelInboundHandler<PingMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        if (msg.getMessageType() == AbstractRpcMessage.PingMessage){
            log.info("收到来自{}的心跳检测包",ctx.channel().remoteAddress().toString());
        }else {
            log.info("错误心跳包：{}",ctx.channel().remoteAddress().toString());
        }
    }
}
