package com.akbros.musicmatch;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Hi on 17-04-2016.
 */
public class Server {
private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://musicmatch-mdakram28.c9users.io");
        } catch (URISyntaxException e) {}
    }

    public Socket getSocket(){
        return mSocket;
    }

    public void connect(){
        mSocket.connect();
    }
    public void disconnect(){
        mSocket.disconnect();
    }
    public void startSong(String songId){
        mSocket.emit("startSong",songId);
    }
    public void stopSong(){
        mSocket.emit("stopSong");
    }
    public void startChat(String sid){
        mSocket.emit("startChat",sid);
    }
    public void stopChat(){
        mSocket.emit("stopChat");
    }
    public void sendMessage(String msg){
        mSocket.emit("message",msg);
    }
}
