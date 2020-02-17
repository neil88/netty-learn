package cn.neil.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author neil
 * @date 2020-02-06
 **/

public class FileChannelDemo {

    public static void main(String[] args) throws IOException {
        //writeFile();
        //readFile();
        //copyFile();
        //randomAccessFile();
        //mappedByteBuffer();
        scatteringAndGathering();

    }

    private static void scatteringAndGathering() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8888));

        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(5);
        buffers[1] = ByteBuffer.allocate(8);

        SocketChannel socketChannel = serverSocketChannel.accept();
        long limitSize = 14L;

        while (true) {
            long readSize = 0L;
            while (readSize < limitSize) {
                long r = socketChannel.read(buffers);
                if (r <= 0L) {
                    break;
                }

                readSize += r;
                System.out.println("累积读取的字节数:" + readSize);
                Arrays.asList(buffers).stream().map(buffer -> "position:" + buffer.position() + ", limit:" + buffer.limit())
                        .forEach(System.out::println);
            }

            Arrays.asList(buffers).forEach(buffer -> buffer.flip());

            //回显
            long writeSize = 0L;
            while (writeSize < limitSize) {
                long write = socketChannel.write(buffers);
                if(write <= 0){
                    break;
                }
                writeSize += write;
            }

            Arrays.asList(buffers).forEach(ByteBuffer::clear);

            System.out.println("writeSize:" + writeSize);

        }


    }


    private static void mappedByteBuffer() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
        map.put(0, (byte) 'h');
        map.put(6, (byte) 'W');

        fileChannel.close();
        randomAccessFile.close();
    }

    private static void randomAccessFile() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();

//        FileOutputStream fos = new FileOutputStream("1.txt");
//        FileChannel fileChannel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(512);

        String str = new String("Hello world!");
        buffer.put(str.getBytes());
        buffer.flip();

        fileChannel.write(buffer, str.getBytes().length);
        fileChannel.close();
        randomAccessFile.close();
    }

    private static void copyFile() throws IOException {
        FileInputStream fis = new FileInputStream("/Users/xiaozhikun/Downloads/mosh-1.3.2.pkg");
        FileOutputStream fos = new FileOutputStream("/Users/xiaozhikun/Downloads/newly_mosh-1.3.2.pkg");

        FileChannel inChannel = fis.getChannel();
        FileChannel outChanel = fos.getChannel();

        outChanel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChanel.close();
        fis.close();
        fos.close();
    }

    private static void readFile() throws IOException {
        FileInputStream fis = new FileInputStream("/Users/xiaozhikun/Downloads/你好.txt");
        FileChannel channel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(512);

        channel.read(buffer);
        System.out.println(new String(buffer.array(), 0, buffer.position()));

        fis.close();
    }

    private static void writeFile() throws IOException {
        FileOutputStream fis = new FileOutputStream("/Users/xiaozhikun/Downloads/你好.txt");
        String str = "你好,我是Neil";
        FileChannel fileChannel = fis.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();

        fileChannel.write(byteBuffer);
        fis.close();
    }
}
