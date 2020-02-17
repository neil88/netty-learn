package cn.neil.netty.customprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class CustDecode extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int len = in.readInt();
        if (len <= 0) {
            log.warn("==> decode len is less than 0, len={}", len);
            return;
        }
        byte[] content = new byte[len];
        in.readBytes(content);

        CustProto cp = new CustProto();
        cp.setLength(len).setContent(new String(content, "UTF-8"));

        out.add(cp);
    }
}
