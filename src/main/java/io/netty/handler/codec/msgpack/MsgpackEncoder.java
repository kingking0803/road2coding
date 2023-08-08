package io.netty.handler.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        // 定义MessagePack对象
        MessagePack messagePack = new MessagePack();
        // 对目标Object进行编码
        byte[] raw = messagePack.write(o);
        // 将编码结果写入ByteBuf中
        byteBuf.writeBytes(raw);
    }
}
