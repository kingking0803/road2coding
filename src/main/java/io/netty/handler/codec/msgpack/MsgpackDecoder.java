package io.netty.handler.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final int length = byteBuf.readableBytes();
        final byte[] array = new byte[length];
        // 从Bytebuf中读取需要解码的byte数组
        byteBuf.readBytes(array);
        MessagePack messagePack = new MessagePack();
        // 用MessagePack进行解码
        list.add(messagePack.read(array));
    }
}
