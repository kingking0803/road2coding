package com.drew.netty;

import org.msgpack.annotation.Message;

import java.util.Date;

/**
 * @Message注解表示可以使用MessagePack的序列化
 */
@Message
public class TimeInfo {

    private Date currentTime;

    private String sendName;

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }
}
