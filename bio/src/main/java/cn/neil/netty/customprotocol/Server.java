package cn.neil.netty.customprotocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author neil
 * @date 2020-02-16
 **/
@Slf4j
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBoot = new ServerBootstrap();

        serverBoot.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CustEncode())
                                .addLast(new CustDecode())
                                .addLast(new ServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture channelFuture = serverBoot.bind(this.port).sync();
            log.info("bind prot:" + this.port + ", server start success!");
            channelFuture.channel().closeFuture().sync();
            log.info("channel close, closeFuture!");

        } catch (InterruptedException e) {
            log.error("start server fail.", e);

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("server shutdownGracefully!");
        }


    }

    public static void main(String[] args) {
        Server server = new Server(8888);
        server.start();
    }
}
