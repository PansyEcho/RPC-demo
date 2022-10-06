package common.message;

public class PingMessage extends AbstractRpcMessage{
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
