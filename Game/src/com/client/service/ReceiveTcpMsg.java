package com.client.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.client.view.GameFrame;

/**
 * Created by mdach on 2016/12/16.
 */
public class ReceiveTcpMsg implements Runnable{
    private Charset charset = Charset.forName("UTF-8");
    private Selector selector;
    private GameFrame frame;

    public ReceiveTcpMsg(TcpService tcpService, GameFrame frame){
        this.selector = tcpService.getSelector();
        this.frame = frame;
    }
    @Override
    public void run(){
        System.out.println("已开启消息接收!!!");
        try{
            while(selector.select() > 0){
                for(SelectionKey sk : selector.selectedKeys()){
                    selector.selectedKeys().remove(sk);
                    if(sk.isReadable()){
                        SocketChannel sc = (SocketChannel) sk.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        String content = "";
                        while(sc.read(buffer) > 0){
                            sc.read(buffer);
                            buffer.flip();
                            content += charset.decode(buffer);
                        }
                        //将消息交给前端处理
                        frame.processMsg(content);
                        sk.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

