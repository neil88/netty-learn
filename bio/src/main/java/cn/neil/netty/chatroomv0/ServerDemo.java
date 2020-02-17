package cn.neil.netty.chatroomv0;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author neil
 * @date 2020-02-08
 **/

public class ServerDemo {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private int port = 6688;

    public ServerDemo() {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.bind(new InetSocketAddress(port));

            this.selector = Selector.open();
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ServerDemo serverDemo = new ServerDemo();
        serverDemo.listen();

    }

    private void listen() {
        try {
            while (true) {
                int selectCode = selector.select(100);
                if (selectCode <= 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectedIt = selectedKeys.iterator();
                while (selectedIt.hasNext()) {
                    SelectionKey sk = selectedIt.next();

                    //连接事件
                    if (sk.isAcceptable()) {
                        SocketChannel sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        sc.register(this.selector, SelectionKey.OP_READ);

                        String clientIp = "[" + sc.getRemoteAddress() + "]";
                        String msg = "欢迎" + clientIp + "进入房间" + System.getProperty("line.separator");
                        System.out.println(msg);
                        transmitMsg0(sc, msg);

                    } else if (sk.isReadable()) { //读事件
                        handleRead(sk);
                    } else {
                        System.out.println("其他事件...");
                    }

                    selectedIt.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleRead(SelectionKey sk) throws IOException {
        SocketChannel channel = (SocketChannel) sk.channel();
        String remoteAddress = "[" + channel.getRemoteAddress().toString() + "]";
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        int readSize = channel.read(byteBuffer);
        if (readSize <= 0) {
            //客户端掉线事件
            if (readSize == -1) {
                String msg = remoteAddress + ": " + "已下线";
                transmitMsg0(channel, msg);
                sk.cancel();
                channel.close();
            }
            return;
        }

        //byteBuffer.flip();
        String msg = remoteAddress + ": " + new String(byteBuffer.array(), 0, readSize, "utf8");
        System.out.println(msg);
        //转发其他客户端
        transmitMsg(sk, msg);

    }

    private void transmitMsg(SelectionKey sk, String msg) throws IOException {
        transmitMsg0(sk.channel(), msg);
    }

    private void transmitMsg0(SelectableChannel currentChannel, String msg) throws IOException {
        Set<SelectionKey> keys = this.selector.keys();
        Iterator<SelectionKey> keysIt = keys.iterator();
        while (keysIt.hasNext()) {
            SelectionKey k = keysIt.next();
            if (k.channel() instanceof SocketChannel && k.channel() != currentChannel) {
                ((SocketChannel) k.channel()).write(ByteBuffer.wrap(msg.getBytes()));
            }

        }
    }
}
