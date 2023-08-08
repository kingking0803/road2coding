package com.drew.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.msgpack.MsgpackDecoder;
import io.netty.handler.codec.msgpack.MsgpackEncoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

    public static void main(String[] args) throws InterruptedException {
        new TimeServer().bind(8080);
    }

    private void bind(int port) throws InterruptedException {

        // 配置服务端的线程组，它包含了一个NIO线程，用于服务端接收客户端的连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于进行SocketChannel网络读写
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 用于启动NIO服务端的辅助器类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 它的功能对应于NIO中的ServerSocketChannel类
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            // 同步阻塞方法，等待绑定端口成功
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端链路关闭之后退出
            f.channel().closeFuture().sync();
        } finally {
            // 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 主要作用是记录日志，对消息进行编解码等
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
//            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());

            socketChannel.pipeline()
//                    .addLast(new LineBasedFrameDecoder(1024)) // 解码器，以换行符作为分隔符
                    // DelimiterBasedFrameDecoder(maxFrameLength, delimiter)，
                    // maxFrameLength表示单条消息最大长度，超出长度没有找到结束符就跑出异常，防止异常码流缺失分隔符导致内存溢出，
                    // delimiter表示分隔符
//                    .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
//                    .addLast(new StringDecoder()) // 将ByteBuf转为String
                    // LengthFieldBasedFrameDecoder用于处理半包消息，主要和LengthFieldPrepender配合使用
                    .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0,2, 0, 2))
                    .addLast("msgpack decoder", new MsgpackDecoder())
                    // LengthFieldPrepender用于在序列化对象前面新增一个消息长度字段，这个字段所占空间为参数2个字节
                    .addLast("framePrepair", new LengthFieldPrepender(2))
                    .addLast("msgpack encoder", new MsgpackEncoder())
                    .addLast(new TimeServerHandler());
        }
    }
}
