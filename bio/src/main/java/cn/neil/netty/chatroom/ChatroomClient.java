package cn.neil.netty.chatroom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author neil
 * @date 2020-02-15
 **/

public class ChatroomClient {
    private int port;
    private String host;

    public ChatroomClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        EventLoopGroup clientGroup = new NioEventLoopGroup(1);

        Bootstrap boot = new Bootstrap();
        try {
            boot.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("decode", new StringDecoder())
                                    .addLast("encode", new StringEncoder())
                                    .addLast(new ClientHandler());
                        }
                    });

            ChannelFuture cf = boot.connect(this.host, this.port).sync();

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                cf.channel().writeAndFlush(msg + System.getProperty("line.separator"));
            }

            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        ChatroomClient cc = new ChatroomClient(8888, "127.0.0.1");
        cc.start();
    }
/*

    public static class ClientInitHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast("decode", new StringDecoder())
                    .addLast("encode", new StringEncoder())
                    .addLast(new ClientHandler());
        }
    }

    public static class ClientHandler extends SimpleChannelInboundHandler<String> {
        public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd日 HH:mm:ss]");

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " 服务端回复消息:" + msg);

        }
    }
*/

}
