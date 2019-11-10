package com.lky.message;

/**
 * 消息对象基类, 加入mqTrackId和mqSourceTrackId, 用于跟踪消息的来源。
 * <p>
 * trackId使用随机生成的16位字符串.
 */
public abstract class AbstractMessageObject {

    /**
     * 消息的trackId.
     */
    private String mqTrackId;

    /**
     * 来源消息的trackId.
     */
    private String mqSourceTrackId;

    public String getMqTrackId() {
        return mqTrackId;
    }

    public void setMqTrackId(String mqTrackId) {
        this.mqTrackId = mqTrackId;
    }

    public String getMqSourceTrackId() {
        return mqSourceTrackId;
    }

    public void setMqSourceTrackId(String mqSourceTrackId) {
        this.mqSourceTrackId = mqSourceTrackId;
    }
}
