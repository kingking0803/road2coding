package com.drew.netty.demo.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 基于Netty开发的http文件服务器
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/com/drew/netty/";

    public static void main(String[] args) throws InterruptedException {
        new HttpFileServer().run(8080, "127.0.0.1");
    }

    private void run(int port, String host) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpRequestDecoder())
                                    // 解码器，作用是将多个消息转换为单一的FullHttpRequest活着FullHttpResponse
                                    // 原因是http解码器在每个http消息中会生成多个对象
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new HttpRequestEncoder())
                                    // 支持异步发送大码流，例如大的文件传输，但不占用过多内存，防止Java内存溢出
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new HttpFileServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, port).sync();
            System.out.println("Http server start, address is: http://" + host + ":" + port + DEFAULT_URL);
            f.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
