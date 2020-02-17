package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.interfaces.SayHello;
import cn.neil.netty.doubborpc0.metadata.Metadata;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author neil
 * @date 2020-02-15
 **/
@Slf4j
public class RpcClient {
    private int port;
    private String host;
    private volatile boolean isStarted = false;

    public RpcClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    @SuppressWarnings("AlibabaThreadPoolCreation")
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public Object getBean(final Class<?> serivceClass, ClientHandler handler) {

        return Proxy.newProxyInstance(serivceClass.getClassLoader(),
                new Class<?>[]{serivceClass}, new MyInvocationHandler(serivceClass, handler));
    }

    public class MyInvocationHandler implements InvocationHandler {
        private Class clazz;
        private ClientHandler client;

        public MyInvocationHandler(Class clazz, ClientHandler client) {
            this.clazz = clazz;
            this.client = client;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Object result = null;
            try {
                Metadata metadata = new Metadata();
                metadata.setClazz(this.clazz);
                metadata.setMethodName(method.getName());
                metadata.setParasClass(method.getParameterTypes());
                metadata.setParas(args);

                client.setMetadata(metadata);
                if (!isStarted) {
                    start(client);
                }

                System.out.println("执行。。。");
                result = executor.submit(client).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return result;
//            return client.call();
        }
    }

    private void start(ChannelHandler handler) {
        EventLoopGroup clientGroup = new NioEventLoopGroup();

        Bootstrap boot = new Bootstrap();
        try {
            boot.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("decode", new CustDecode())
                                    .addLast("encode", new CustEncode())
                                    .addLast(handler);
                        }
                    });

            boot.connect(this.host, this.port).sync();

            /*Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                cf.channel().writeAndFlush(msg + System.getProperty("line.separator"));
            }*/

            //cf.channel().closeFuture().sync();

            this.isStarted = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            clientGroup.shutdownGracefully();
//            log.info("client shutdownGracefully.");
        }

    }

    public static void main(String[] args) throws InterruptedException {
        RpcClient cc = new RpcClient(8888, "127.0.0.1");

        ClientHandler handler = new ClientHandler();
        SayHello sayHello = (SayHello) cc.getBean(SayHello.class, handler);


        for (; ; ) {
            Thread.sleep(2 * 1000);
            //通过代理对象调用服务提供者的方法(服务)
            String neil = sayHello.sayHello("Neil");
            System.out.println(neil);
        }
    }


}
