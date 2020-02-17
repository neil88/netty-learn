package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.metadata.Metadata;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

/**
 * @author neil
 * @date 2020-02-15
 **/


@Data
public class ClientHandler extends SimpleChannelInboundHandler<Metadata> implements Callable {
    private ChannelHandlerContext ctx;
    private Metadata metadata;

    private volatile boolean isStarted = false;

    /*public ClientHandler(Metadata metadata) {
        this.metadata = metadata;
    }*/

    public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd日 HH:mm:ss]");

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, Metadata msg) throws Exception {
        System.out.println(DATETIME_FORMATTER.format(LocalDateTime.now()) + " " + Thread.currentThread().getName() + " 服务端回复消息:" + msg);
        this.metadata.setResult(msg.getResult());
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        this.ctx.writeAndFlush(this.metadata);
        wait();
        System.out.println(Thread.currentThread().getName() + " - call() 被调用 " + this.metadata.getResult());
        return this.metadata.getResult();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.ctx == null) {
            this.ctx = ctx;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}