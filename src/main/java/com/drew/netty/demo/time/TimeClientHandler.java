package com.drew.netty.demo.time;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeClientHandler extends ChannelHandlerAdapter {

    private byte[] req;

    private static int count = 0;

    public TimeClientHandler() {
        req = "query time order$_".getBytes();
    }

    /**
     * 连接建立成功后会调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
//            ByteBuf firstMessage = Unpooled.buffer(req.length);
//            firstMessage.writeBytes(req);
//            ctx.writeAndFlush(firstMessage);
            TimeInfo info = new TimeInfo();
            info.setCurrentTime(new Date());
            info.setSendName("client" + i);
            ctx.write(info);
        }
        ctx.flush();
    }

    /**
     * 当服务器返回应答消息时，此方法被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, "UTF-8");
//        String body = (String) msg;
//        System.out.println("now is " + body + "; count is " + ++count);
        System.out.println("client receive msgpack is " + msg);
//        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 发生异常时被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("unexcepted exception from downstream: " + cause.getMessage());
        ctx.close();
    }
}
