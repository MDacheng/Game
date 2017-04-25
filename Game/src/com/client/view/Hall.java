package com.client.view;

import javax.swing.*;

import com.beans.Message;
import com.beans.User;
import com.client.service.ReceiveTcpMsg;
import com.client.service.ReceiveUdpMsg;
import com.client.service.TcpService;
import com.client.service.UdpService;
import com.util.YeekuProtocol;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

/**
 * Created by mdach on 2016/12/15.
 */
public class Hall extends GameFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4346645356138155998L;
    Logger logger = Logger.getLogger(Hall.class);
	private int roomCount;
    private JButton[][] btns;
    private int[][] sign;
    private JButton btn;

    private TcpService tcpService;
    private UdpService udpService;
    private String userName;
    
    private boolean initFlag = false;

    public Hall(String userName, TcpService tcpService, UdpService udpService){
        PropertyConfigurator.configure("log4j.properties");
        this.userName = userName;
        this.tcpService = tcpService;
        this.udpService = udpService;
    }

    public void login(){

        String loginInfo = userName + YeekuProtocol.SPLITTER + udpService.getDatagramSocket().getLocalPort();
        Message message = new Message((byte)0, loginInfo);
        //向服务器发送登录信息
        tcpService.send(message);
    }

    public String getUserName() {
        return userName;
    }

    private void initUI(){
        this.setTitle("大厅");
        btns = new JButton[roomCount][2];
        sign = new int[roomCount][2];
        JPanel p = new JPanel(new GridLayout(4, 4, 30, 30));
        JPanel[] p1 = new JPanel[roomCount];
        for(int i = 0; i < p1.length; i++){
            p1[i] = new JPanel();
            p1[i].setLayout(null);
            if(sign[i][0] == 0){
                btns[i][0] = new JButton("坐下");
                ButtonActionListener bal0 = new ButtonActionListener(i, 0);
                btns[i][0].addActionListener(bal0);
            }
            else{
                btns[i][0] = new JButton(userName);
                btns[i][0].setEnabled(false);
            }
            btns[i][0].setFont(new Font("宋体", Font.PLAIN, 10));
            btns[i][0].setBounds(10, 10, 55, 20);
            JPanel p2 = new JPanel();
            if(sign[i][1] == 0){
                btns[i][1] = new JButton("坐下");
                ButtonActionListener bal1 = new ButtonActionListener(i, 1);
                btns[i][1].addActionListener(bal1);
            }
            else{
                btns[i][1] = new JButton(userName);
                btns[i][1].setEnabled(false);
            }
            btns[i][1].setFont(new Font("宋体", Font.PLAIN, 10));
            btns[i][1].setBounds(10, 50, 55, 20);
            p1[i].add(btns[i][0]);
            p1[i].add(p2);
            p1[i].add(btns[i][1]);
            p1[i].setBackground(Color.gray);
            p.add(p1[i]);
        }
        this.setLayout(null);
        p.setBounds(35, 20, 400, 400);
        this.add(p);
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("退出游戏");
                tcpService.send(new Message((byte)2, userName));
                System.exit(0);
            }
        });
    }

    private class ButtonActionListener implements ActionListener {
        private int rid;
        private int bid;
        public ButtonActionListener(int rid, int bid){
            this.rid = rid;
            this.bid = bid;
        }
        public void actionPerformed(ActionEvent e){
            btns[rid][bid].setText("已坐");
            btns[rid][bid].setEnabled(false);
            if(btn != null){
                btn.setText("坐下");
                btn.setEnabled(true);
            }
            btn = btns[rid][bid];

            //向服务器发送占座信息
            String info = rid+YeekuProtocol.SPLITTER+bid;
            tcpService.send(new Message((byte)1, info));
        }
    }
    //设置房间状态
    private void setRoom(String[][] roomInfo){
    	if(!initFlag) return;
        for(int i = 0; i < roomInfo.length; i++){
            for(int j = 0; j < 2; j++){
                if(roomInfo[i][j].equals(YeekuProtocol.NULL)){
                    btns[i][j].setEnabled(true);
                    btns[i][j].setText("坐下");
                }
                else{
                    btns[i][j].setEnabled(false);
                    btns[i][j].setText(roomInfo[i][j]);
                }
            }
        }
    }
    public void processMsg(String msg){
        //接收到初始化消息
        if(msg.startsWith(YeekuProtocol.INIT) && msg.endsWith(YeekuProtocol.INIT)){
            String str = msg.substring(2, msg.length()-2);
            roomCount = Integer.parseInt(str);
            this.initUI();
            initFlag = true;
        }
        //接收到房间信息
        else if(msg.startsWith(YeekuProtocol.ROOM) && msg.endsWith(YeekuProtocol.ROOM)){
            String str = msg.substring(2, msg.length()-2);
            String[] arr = str.split(YeekuProtocol.SPLITTER);
            String[][] roomInfo = new String[arr.length][2];
            for(int i = 0; i < roomInfo.length; i++){
                roomInfo[i][0] = arr[i].split(",")[0];
                roomInfo[i][1] = arr[i].split(",")[1];
            }
            this.setRoom(roomInfo);
        }
        //接收到开始游戏的信息
        else if(msg.startsWith(YeekuProtocol.BEGIN) && msg.endsWith(YeekuProtocol.BEGIN)){
            String str = msg.substring(2, msg.length()-2);
            String[] arr = str.split(YeekuProtocol.SPLITTER);
            User adversary = new User(arr[1], arr[0]);
            int role = -1;
            if(arr[2].equals("WHITE")) role = 1;
            else if(arr[2].equals("BLACK")) role = 0;
            Room room = new Room(this, udpService, adversary, role);
            ReceiveUdpMsg receiveUdpMsg = new ReceiveUdpMsg(room, udpService);
            new Thread(receiveUdpMsg).start();
            this.setVisible(false);
        }
        else{
            System.out.println("非法信息");
        }
    }
    public static void main(String[] args) throws Exception{
    	//获取用户名
        String userName= JOptionPane.showInputDialog("请输入用户名:");

        //开启通信服务
        UdpService udpService = new UdpService();
        TcpService tcpService = new TcpService();
        tcpService.init();

        Hall frame = new Hall(userName, tcpService, udpService);
        frame.setVisible(true);
        //开启消息监听
        ReceiveTcpMsg receiveTcpMsg = new ReceiveTcpMsg(tcpService, frame);
        new Thread(receiveTcpMsg).start();
        //发起登录请求
        frame.login();

    }
}
