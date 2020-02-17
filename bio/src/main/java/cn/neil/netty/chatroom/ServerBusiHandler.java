package cn.neil.netty.chatroom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author neil
 * @date 2020-02-15
 **/

public class ServerBusiHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        SocketAddress clientAdd = channel.remoteAddress();
        System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " (" + clientAdd.toString() + "): " + msg);


        for (Channel ch : channels
        ) {
            if (ch != ctx.channel()) {
                ch.writeAndFlush(DATETIME_FORMATTER.format(LocalDateTime.now()) + "(" + clientAdd + "): " + msg);
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        for (Channel ch : channels
        ) {
            if (ch != ctx.channel()) {
                SocketAddress clientAdd = ctx.channel().remoteAddress();
                ch.writeAndFlush(DATETIME_FORMATTER.format(LocalDateTime.now()) + "(" + clientAdd + ") 上线了...");
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        for (Channel ch : channels
        ) {
            if (ch != ctx.channel()) {
                SocketAddress clientAdd = ctx.channel().remoteAddress();
                ch.writeAndFlush(DATETIME_FORMATTER.format(LocalDateTime.now()) + "(" + clientAdd + ") 下线了...");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress clientAdd = ctx.channel().remoteAddress();
        System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " (" + clientAdd.toString() + ")上线了~");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress clientAdd = ctx.channel().remoteAddress();
        System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " (" + clientAdd.toString() + ")下线了~");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
