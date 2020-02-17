package cn.neil.netty.doubborpc0;

import cn.neil.netty.doubborpc0.metadata.Metadata;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author neil
 * @date 2020-02-15
 **/

public class ServerBusiHandler extends SimpleChannelInboundHandler<Metadata> {
    private Map<String, Object> beanFatory;

    public ServerBusiHandler(Map<String, Object> beanFatory) {
        this.beanFatory = beanFatory;
    }

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Metadata metadata) throws Exception {
        Channel channel = ctx.channel();

        if (metadata == null) {
            throw new IllegalArgumentException();
        }

        Object o = this.beanFatory.get(metadata.getClazz().getName());
        if (o == null) {
            throw new IllegalArgumentException("className " + metadata.getClazz().getName() + " not exits.");
        }

        Class clazz = metadata.getClazz();
        Method invokeMethod = clazz.getMethod(metadata.getMethodName(), metadata.getParasClass());
        invokeMethod.setAccessible(true);

        Object result = invokeMethod.invoke(o, metadata.getParas());
        System.out.println("结果:" + result);
        metadata.setResult(result);

        ctx.writeAndFlush(metadata);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
