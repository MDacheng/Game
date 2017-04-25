package com.client.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.client.view.Room;

/**
 * Created by mdach on 2016/12/16.
 */
public class ReceiveUdpMsg implements Runnable{
    //数据包的大小
    private static final int DATA_LEN = 4096;
    private byte[] buffer = new byte[DATA_LEN];

    //指定该线程所依赖的对象，接收的是该对象的消息，接收的消息由该对象处理
    private Room room;
    private UdpService udpService;
    private DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

    public ReceiveUdpMsg(Room room, UdpService udpService){
        this.room = room;
        this.udpService = udpService;
    }

    public void run(){
        DatagramSocket socket = udpService.getDatagramSocket();
        while(true){
            try{
                socket.receive(packet);
                //处理读到的消息
                String msg = new String(packet.getData(), 0, packet.getLength());
                room.processMsg(msg);
            }catch(IOException e){
                e.printStackTrace();
                if(socket != null){
                    socket.close();
                }
                System.out.println("网络异常");
                System.exit(1);
            }
        }
    }
}
