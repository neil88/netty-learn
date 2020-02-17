package cn.neil.netty.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author neil
 * @date 2020-02-16
 **/

public class CustEncode extends MessageToByteEncoder<CustProto> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustProto msg, ByteBuf out) throws Exception {

        long length = msg.getLength();
        byte[] content = msg.getContent().getBytes("UTF-8");
        out.writeLong(length);
        out.writeBytes(content);
    }
}
