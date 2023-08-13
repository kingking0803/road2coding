package com.drew.netty.demo.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;
import org.msgpack.MessagePack;

import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter {

    private static int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg; // 类似nio中的ByteBuffer
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, "UTF-8");
//        String body = (String) msg;
//        System.out.println("time server receive order: " + body + "; count is " + ++count);
//        String currentTime = "query time order".equalsIgnoreCase(body) ? new Date().toString() : "bad order";
//        currentTime += "$_";
//        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
//        TimeInfo info = (TimeInfo) msg;
        System.out.println("server receive time is " + msg);
        // netty为了防止频繁的唤醒Selector进行消息发送，netty的wirte方法并不直接将消息写入到SocketChannel，
        // 调用write方法只是将消息放到缓存区
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息发送队列中的消息写入到SocketChannel中发给对方
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常时，释放和ChannelHandlerContext相关的句柄资源
        ctx.close();
    }
}
