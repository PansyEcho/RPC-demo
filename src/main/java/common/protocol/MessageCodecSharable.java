package common.protocol;

import com.google.protobuf.AbstractMessage;
import common.config.SerializerConfig;
import common.constant.exception.RpcException;
import common.message.AbstractRpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, AbstractRpcMessage> {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractRpcMessage msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 魔数
        out.writeInt(MAGIC_NUMBER);
        // 2. 版本,
        out.writeByte(1);
        // 3. 序列化方式 jdk 0 , json 1
        out.writeByte(SerializerConfig.getSerializerAlgorithm().ordinal());
        // 4. 指令类型
        out.writeByte(msg.getMessageType());
        // 5. 字节长度
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        byte[] bytes = SerializerConfig.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        if (magicNum != MAGIC_NUMBER){
            throw new RuntimeException(RpcException.UNKONWN_PROTOCOL);
        }
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte(); // 0 或 1
        byte messageType = in.readByte(); // 0,1,2...
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 找到反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型
        Class<? extends AbstractRpcMessage> messageClass = AbstractRpcMessage.getMessageClass(messageType);
        AbstractRpcMessage message = algorithm.deserialize(messageClass, bytes);
        out.add(message);
    }
}
