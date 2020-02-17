package cn.neil.netty.customprotocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class Client {
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        EventLoopGroup clientGroup = new NioEventLoopGroup(1);
        Bootstrap clientBoot = new Bootstrap();

        clientBoot.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CustEncode())
                                .addLast(new CustDecode())
                                .addLast(new ClientHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = clientBoot.connect(this.host, this.port).sync();
            log.info("connet "+this.host+":"+this.port+ "  success!");
            channelFuture.channel().closeFuture().sync();
            log.info("channel close, closeFuture!");

        } catch (InterruptedException e) {
            log.error("start server fail.", e);

        } finally {
            clientGroup.shutdownGracefully();
            log.info("server shutdownGracefully!");
        }


    }

    public static void main(String[] args) {
        Client client = new Client(8888, "127.0.0.1");
        client.start();
    }
}
