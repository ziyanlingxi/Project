package com.cn.server.domain;

public class Message {
    private Integer id;
    private String to;
private String from;
private String message;

    public Message() {
    }

    public Message(Integer id, String to, String from, String message) {
        this.id = id;
        this.to = to;
        this.from = from;
        this.message = message;
    }

    /**
     * 获取
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取
     * @return to
     */
    public String getTo() {
        return to;
    }

    /**
     * 设置
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 获取
     * @return from
     */
    public String getFrom() {
        return from;
    }

    /**
     * 设置
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 获取
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "Message{id = " + id + ", to = " + to + ", from = " + from + ", message = " + message + "}";
    }
}
