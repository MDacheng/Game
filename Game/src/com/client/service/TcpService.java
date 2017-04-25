package com.client.service;

import com.beans.Message;
import com.util.ByteUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by mdach on 2016/12/15.
 */
public class TcpService extends Service{
    //服务器端口
    private static final int PORT = 8889;
    private static final String serverIp = "192.168.1.129";
    private Charset charset = Charset.forName("UTF-8");
    private Selector selector;

    private SocketChannel clientChannel;

    public void init() throws IOException {
        //初始化连接
        selector = Selector.open();
        InetSocketAddress isa = new InetSocketAddress(serverIp, PORT);
        clientChannel = SocketChannel.open(isa);
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("服务器连接成功!!!");
    }

    /**
     * 处理客户端的发送
     * @param
     * @Return
     */
    public void send(Message message){
        try{
            clientChannel.write(ByteUtil.getByteBuffer(message));
            System.exit(0);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        catch(NullPointerException ex){
            System.out.println("消息发送失败，未连接服务器!!!\n");
        }
    }

    public Selector getSelector() {
        return selector;
    }
}
