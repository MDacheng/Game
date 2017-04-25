package com.client.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.client.view.Room;

/**
 * Created by mdach on 2016/12/16.
 */
public class ReceiveUdpMsg implements Runnable{
    //���ݰ��Ĵ�С
    private static final int DATA_LEN = 4096;
    private byte[] buffer = new byte[DATA_LEN];

    //ָ�����߳��������Ķ��󣬽��յ��Ǹö������Ϣ�����յ���Ϣ�ɸö�����
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
                //�����������Ϣ
                String msg = new String(packet.getData(), 0, packet.getLength());
                room.processMsg(msg);
            }catch(IOException e){
                e.printStackTrace();
                if(socket != null){
                    socket.close();
                }
                System.out.println("�����쳣");
                System.exit(1);
            }
        }
    }
}
