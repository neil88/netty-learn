package cn.neil.netty.chatroomv0;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author neil
 * @date 2020-02-08
 **/

public class ClientDemo {

    private SocketChannel socketChannel;
    private Selector selector;
    private String host = "localhost";
    private int port = 6688;

    public ClientDemo() {
        try {
            this.socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            this.socketChannel.configureBlocking(false);

            this.selector = Selector.open();
            socketChannel.register(this.selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        ClientDemo clientDemo = new ClientDemo();

        clientDemo.read();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            clientDemo.sendInfo(s);
        }

    }

    private void sendInfo(String s) throws IOException {
        this.socketChannel.write(ByteBuffer.wrap(s.getBytes()));
    }

    public void read() {

        new Thread(() -> {
            try {

                while (true) {
                    int selectCode = selector.select(500);
                    if (selectCode <= 0) {
                        continue;
                    }

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    while (it.hasNext()) {
                        SelectionKey next = it.next();
                        if (next.isReadable()) {
                            SocketChannel channel = (SocketChannel) next.channel();
                            int readSize = channel.read(buffer);
                            if (readSize <= 0) {
                                continue;
                            }
                            System.out.println(new String(buffer.array(), 0, readSize, "utf8"));

                        } else {
                            System.out.println("其他事件");
                        }

                        it.remove();
                        buffer.clear();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();
    }

}


