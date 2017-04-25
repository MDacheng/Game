package com.server;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

import com.beans.Message;
import com.beans.User;
import com.util.ByteUtil;
import com.util.YeekuProtocol;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by mdach on 2016/12/15.
 */
public class Server {


    Logger logger = Logger.getLogger(Server.class);


    //服务器采用固定端口和IP地址
    private static final int PORT = 8889;
    private static final String IPSTR = "192.168.1.129";

    private Charset charset = Charset.forName("UTF-8");

    //全局Selector，检测所有的NIO通道
    private Selector selector;
    private ServerSocketChannel serverChannel;

    //IP-SelectionKey的映射，用来发送消息
    private Map<String, SelectionKey> map = new HashMap<>();
    //IP-User的映射，用来查找用户
    private Map<String, User> userMap = new HashMap<>();

    //房间的数量和房间信息
    private int roomCount = 16;
    private User[][] roomInfo = new User[roomCount][2];

    public void init() throws IOException {
        PropertyConfigurator.configure("src/log4j.properties");

        //初始化连接，将多个注册服务端的Channel注册到Selector
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(IPSTR, PORT);
        serverChannel.bind(isa);
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        //每隔指定的时间，就向客户端发送房间信息
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                broadcastRoom();
            }
        }, 0, 1000);

        receiveMsg();
    }

    /**
     * 接收消息
     */
    private void receiveMsg() throws IOException{
        while(selector.select() > 0){
            for(SelectionKey sk : selector.selectedKeys()){
                selector.selectedKeys().remove(sk);
                if(sk.isAcceptable()){
                    SocketChannel sc = serverChannel.accept();
                    sc.configureBlocking(false);
                    SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
                    String cip = sc.getRemoteAddress().toString();

                    map.put(cip, key);
                    //通知用户登录
                    sk.interestOps(SelectionKey.OP_ACCEPT);
                }
                if(sk.isReadable()){

                    SocketChannel sc = (SocketChannel) sk.channel();
                    String cip = sc.getRemoteAddress().toString();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try{
                        int len = sc.read(buffer);
                        buffer.flip();
                        if(len == -1){
                            selector.selectedKeys().remove(sk);
                            userExit(cip);
                            break;
                        }
                        Object obj = ByteUtil.getObject(buffer.array());
                        if (obj != null) processMsg(cip, (Message) obj);
                        sk.interestOps(SelectionKey.OP_READ);
                    }
                    catch(IOException ex){
                        sk.cancel();
                        if(sk.channel() != null){
                            sk.channel().close();
                        }
                    }
                    catch (ClassNotFoundException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 用户退出的操作，从map中移除地址，并从用户列表中移除该用户，并恢复用户的占座信息
     * @param cip
     */
    private void userExit(String cip){
        map.remove(cip);
        User user = userMap.get(cip);
        if(user != null && user.getRid() > -1 && user.getBid() > -1){
            roomInfo[user.getRid()][user.getBid()] = null;
        }
        userMap.remove(cip);
    }
    /**
     * 处理消息
     * @param msg
     */
    private void processMsg(String cip, Message msg){
        switch (msg.getHead()){
            case 0:          //登陆的请求
                String[] arr = msg.getContent().split(YeekuProtocol.SPLITTER);
                userMap.put(cip, new User(arr[0], cip, cip.split(":")[0]+":"+arr[1], -1, -1));
                logger.info(arr[0] + " login!!!");

                //登陆响应
                Message response = new Message((byte)2, roomCount+"");
                sendMessage(response, map.get(cip));
                break;
            case 1:         //坐下的请求
                arr = msg.getContent().split(YeekuProtocol.SPLITTER);
                int rid = Integer.parseInt(arr[0]);
                int bid = Integer.parseInt(arr[1]);
                User user = userMap.get(cip);
                if(user.getRid() != -1 && user.getBid() != -1){
                    roomInfo[user.getRid()][user.getBid()] = null;
                }
                user.setRid(rid);
                user.setBid(bid);
                userMap.put(cip, user);
                roomInfo[rid][bid] = user;

                //判断对面是否存在用户，如果存在，则通知两人开始游戏
                User adversary = roomInfo[rid][1-bid];
                if(adversary != null){
                    String sm = YeekuProtocol.BEGIN + user.getDataAddress() + YeekuProtocol.SPLITTER +
                            user.getUserName() + YeekuProtocol.SPLITTER +
                            "WHITE" + YeekuProtocol.BEGIN;
                    SelectionKey sk = map.get(adversary.getAddress());
                    //开始游戏的响应
                    response = new Message((byte)3, sm);
                    sendMessage(response, sk);
                    sk = map.get(user.getAddress());
                    sm = YeekuProtocol.BEGIN + adversary.getDataAddress() + YeekuProtocol.SPLITTER +
                            adversary.getUserName() + YeekuProtocol.SPLITTER +
                            "BLACK" + YeekuProtocol.BEGIN;
                    response = new Message((byte)3, sm);
                    sendMessage(response, sk);
                }
                break;
            default:
                logger.error("Illegal Message");
                break;
        }
    }

    /*广播消息*/
    private void broadcastRoom(){
        String content = "";
        for(int i = 0; i < roomInfo.length; i++){
            if(roomInfo[i][0] == null) content += YeekuProtocol.NULL + ",";
            else content += roomInfo[i][0].getUserName() + ",";
            if(roomInfo[i][1] == null) content += YeekuProtocol.NULL;
            else content += roomInfo[i][1].getUserName();
            if(i != roomInfo.length-1){
                content += YeekuProtocol.SPLITTER;
            }
        }
        Message msg = new Message((byte)4, content);

        for(SelectionKey key : selector.keys()){
            sendMessage(msg, key);
        }
    }
    /**
     * 发送命令
     * @param msg
     * @param key
     */
    private void sendMessage(Message msg, SelectionKey key){
        Channel targetChannel = key.channel();
        if(targetChannel instanceof SocketChannel){
            SocketChannel dest = (SocketChannel) targetChannel;
            try{
                dest.write(ByteUtil.getByteBuffer(msg));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException{
        new Server().init();
    }
}