package cn.neil.netty.customprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<CustProto> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustProto msg) throws Exception {
        log.info("收到 {} 消息:{}.", ctx.channel().remoteAddress(), msg);
        ctx.channel().writeAndFlush(msg);
    }
}
