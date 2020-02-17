package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.impl.SayHelloImp;
import cn.neil.netty.doubborpc0.interfaces.SayHello;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author neil
 * @date 2020-02-15
 **/

public class RpcServer {
    private int port;

    public RpcServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        RpcServer server = new RpcServer(8888);

        SayHello sayHello = new SayHelloImp();
        Map<String, Object> factory = new HashMap<>();
        factory.put(SayHello.class.getName(), sayHello);

        server.start(factory);
    }

    public void start(Map<String, Object> factory) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBoot = new ServerBootstrap();
            serverBoot.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline
                                    .addLast("decode", new CustDecode())
                                    .addLast("encode", new CustEncode())
                                    .addLast(new ServerBusiHandler(factory));

                        }
                    });
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            System.out.println("服务端启动完毕...");

            ChannelFuture cf = serverBoot.bind(this.port).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
