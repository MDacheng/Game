package com.beans;

import java.io.Serializable;

/**
 * Created by mengdacheng on 2017/3/29.
 */
public class Message implements Serializable{
    private byte head;
    private String content;
    public Message(byte head, String content){
        this.head = head;
        this.content = content;
    }

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
