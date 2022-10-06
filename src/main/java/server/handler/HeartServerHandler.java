package server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HeartServerHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
        if (idleStateEvent.state() == IdleState.READER_IDLE){
            log.info("已超过5s未读写,关闭连接:{}",ctx.channel().remoteAddress());
            System.out.println("已超过5s未读写,关闭连接:" + ctx.channel().remoteAddress());
            ctx.close();
        }

    }

}
