package cn.neil.netty.customprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<CustProto> {
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            String msg = "我是neil,这是第" + (i + 1) + "次发送给服务端";
            byte[] msgBytes = msg.getBytes("UTF-8");

            CustProto cp = new CustProto();
            cp.setLength(msgBytes.length).setContent(msg);

            ctx.channel().writeAndFlush(cp);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustProto msg) throws Exception {
        log.info("{} - {}", dtf.format(LocalDateTime.now()), msg);
    }
}
