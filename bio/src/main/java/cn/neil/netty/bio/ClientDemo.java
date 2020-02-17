package cn.neil.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author neil
 * @date 2020-02-05
 **/

public class ClientDemo {

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; i++) {
            Socket socket = new Socket("127.0.0.1", 8888);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            String tmp = "Hello" + i;
            outputStream.write(tmp.getBytes());
            outputStream.flush();

            byte[] buff = new byte[512];
            int len;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = inputStream.read(buff)) != -1) {
                stringBuilder.append(new String(buff, 0, len));
            }
            System.out.println(stringBuilder.toString());

            outputStream.close();
            inputStream.close();
            socket.close();
        }
    }
}
