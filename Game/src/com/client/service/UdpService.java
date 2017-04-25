package com.client.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.util.ComUtil;

/**
 * Created by mdach on 2016/12/15.
 */
public class UdpService extends Service{
//    private static final int DATA_LEN = 4096;
    private DatagramSocket socket;

    public UdpService(){
        try{
            socket = new DatagramSocket();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public DatagramSocket getDatagramSocket(){
        return this.socket;
    }
    public void sendMsg(String msg, InetSocketAddress dest){
        try{
            byte[] buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dest);
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
            if(socket != null){
                socket.close();
            }
            System.out.println("Õ¯¬Á“Ï≥£");
            System.exit(1);
        }
    }
    public void sendMsg(String msg, String dest){
        // System.out.println(dest);
        InetSocketAddress address = ComUtil.strToAddress(dest);
        sendMsg(msg, address);
    }
}
