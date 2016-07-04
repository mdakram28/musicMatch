package com.akbros.musicmatch;

/**
 * Created by Hi on 17-04-2016.
 */
public class Message {
    public int type;
    public static final int TYPE_YOU = 1;
    public static final int TYPE_STRANGER = 2;
    public static final int TYPE_COMMENT = 3;
    String msg;
    public Message(String message,int type){
        msg = message;
        this.type = type;
    }
    public String getMessage(){
        return msg;
    }
}
