package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.metadata.Metadata;
import cn.neil.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class CustDecode extends ReplayingDecoder<Void> implements Serializable {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int len = in.readInt();
        if (len <= 0) {
            log.warn("==> decode len is less than 0, len={}", len);
            return;
        }
        byte[] content = new byte[len];
        in.readBytes(content);

        Object o = Util.bytesToObject(content);

        out.add((Metadata) o);
    }
}
