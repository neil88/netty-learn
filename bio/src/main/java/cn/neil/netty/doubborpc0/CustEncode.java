package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.metadata.Metadata;
import cn.neil.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

/**
 * @author neil
 * @date 2020-02-16
 **/

public class CustEncode extends MessageToByteEncoder<Metadata> implements Serializable {
    @Override
    protected void encode(ChannelHandlerContext ctx, Metadata msg, ByteBuf out) throws Exception {

        byte[] bytes = Util.objToByte(msg);
        int length = bytes.length;

        out.writeLong(length);
        out.writeBytes(bytes);
        ctx.flush();
    }


}
