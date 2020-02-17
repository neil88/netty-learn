package cn.neil.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author neil
 * @date 2020-02-05
 **/

public class ServerDemo {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleConnection(clientSocket);
        }
    }

    private static void handleConnection(Socket clientSocket) {
        executor.execute(new ClientSocket(clientSocket));
    }

    private static class ClientSocket implements Runnable {
        private Socket socket;

        ClientSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            if (socket == null) {
                System.out.println("无客户端，即将退出...");
            }

            byte[] buff = new byte[1024];
            int lenth;
            StringBuilder stringBuilder = new StringBuilder();

            try (InputStream inputStream = socket.getInputStream()) {
                while ((lenth = inputStream.read(buff)) != -1) {
                    String tmp = new String(buff, 0, lenth);
                    stringBuilder.append(tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("此次通讯内容:" + stringBuilder.toString());

        }
    }
}
