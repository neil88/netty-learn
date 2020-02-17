package cn.neil.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author neil
 * @date 2020-02-07
 **/

public class SelectClientDemo {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6667);

//        socketChannel.connect(inetSocketAddress);
        if (!socketChannel.connect(inetSocketAddress)) {

            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作..");
            }
        }

        String str = "Hello World! I am Neil.";
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
//        buffer.put(str.getBytes());
        socketChannel.write(buffer);

//        buffer.flip();
        socketChannel.read(buffer);
        System.out.println(new String(buffer.array(), "utf8"));

        System.in.read();

    }
}
