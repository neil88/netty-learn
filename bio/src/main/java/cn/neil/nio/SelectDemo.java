package cn.neil.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author neil
 * @date 2020-02-07
 **/

public class SelectDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(6667));
        SelectionKey serverSeleKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("当前已注册的selectionKey数量：" + selector.keys().size());

        while (true) {
            if (selector.select(2000) == 0) {
                //System.out.println("服务器等待了1秒，无连接...");
                continue;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("当前selectedKeys 数量:" + keys.size());

            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey k = keyIterator.next();
                try {
                    handleSelectedEvent(selector, serverSocketChannel, k);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                keyIterator.remove();
            }

        }
    }

    private static void handleSelectedEvent(Selector selector, ServerSocketChannel serverSocketChannel, SelectionKey selectedKey) throws IOException {
        if (selectedKey.isAcceptable()) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
            socketChannel.configureBlocking(false);

            SelectionKey readSeleKey = socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
            System.out.println("当前已注册的selectionKey数量：" + selector.keys().size());

        } else if (selectedKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
            ByteBuffer buffer = (ByteBuffer) selectedKey.attachment();

            int readSize = socketChannel.read(buffer);
            if (readSize <= 0) {
                return;
            }

            System.out.println(new String(buffer.array(), 0, readSize, "utf8"));

            buffer.flip();
            socketChannel.write(buffer);
        } else {
            System.out.println("未支持的事件：" + selectedKey);
        }

    }

}
