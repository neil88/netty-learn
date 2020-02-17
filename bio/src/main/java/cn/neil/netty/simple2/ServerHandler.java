package cn.neil.netty.simple2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author neil
 * @date 2020-02-13
 **/

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        System.out.println("服务器读取线程 " + Thread.currentThread().getName() + " - channel " + ctx.channel());
        Channel channel = ctx.channel();
        channel.pipeline();

        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端 ==== " + channel.remoteAddress() + ": " + buf.toString(CharsetUtil.UTF_8));

//        channel.eventLoop().execute(() -> System.out.println("执行队列中的任务"));
        channel.eventLoop().schedule(() -> System.out.println("执行队列中的任务"), 5, TimeUnit.SECONDS);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务已收到消息", CharsetUtil.UTF_8));
    }
}
