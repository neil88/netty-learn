package cn.neil.netty.chatroom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author neil
 * @date 2020-02-15
 **/


public class ClientHandler extends SimpleChannelInboundHandler<String> {
    public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd日 HH:mm:ss]");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " 服务端回复消息:" + msg);

    }
}