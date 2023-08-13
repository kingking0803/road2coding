package com.drew.netty.demo.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.msgpack.MsgpackDecoder;
import io.netty.handler.codec.msgpack.MsgpackEncoder;

public class TimeClient {

    public static void main(String[] args) throws InterruptedException {
        new TimeClient().connent(8080, "127.0.0.1");
    }

    private void connent(int port, String host) throws InterruptedException {
        // 配置客户端NioEventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            socketChannel.pipeline()
//                                    .addLast(new LineBasedFrameDecoder(1024))
//                                    .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
//                                    .addLast(new StringDecoder())
                                    .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0,2, 0, 2))
                                    .addLast("msgpack decoder", new MsgpackDecoder())
                                    .addLast("framePrepair", new LengthFieldPrepender(2))
                                    .addLast("msgpack encoder", new MsgpackEncoder())
                                    .addLast(new TimeClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
