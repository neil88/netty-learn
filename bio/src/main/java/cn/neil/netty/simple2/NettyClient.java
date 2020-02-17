package cn.neil.netty.simple2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author neil
 * @date 2020-02-13
 **/

public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup clientGroup = null;

        try {
            clientGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientHandler());
            System.out.println("=====>>> 客户端 <<<=====");

            ChannelFuture cf = bootstrap.connect("127.0.0.1", 6668).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }
}
