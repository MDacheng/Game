package com.client.view;

import javax.swing.*;

import com.beans.User;
import com.client.service.UdpService;
import com.client.view.component.Chess;
import com.client.view.component.ChessBoard;
import com.util.YeekuProtocol;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mdach on 2016/12/15.
 */
public class Room extends GameFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6091533275788959194L;
	private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int LINE_COUNT = 15;

//    private int rid;
    private User adversary;
    private int[][] cp = new int[LINE_COUNT][LINE_COUNT];
    private Chess[][] wp = new Chess[LINE_COUNT][LINE_COUNT];
    private JProgressBar progressBar;
    private int role;
    private int value = 100;

    private GameFrame hall;
    private UdpService udpService;
    private boolean status;

    public Room(GameFrame hall, UdpService udpService, User adversary, int role){
        this.hall = hall;
        this.udpService = udpService;
        this.adversary = adversary;
        this.role = role;
        if(role == 0) status = true;
        initUI();
    }

    public void initUI(){

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        progressBar.setStringPainted(true);
        progressBar.setString("01:50");
        progressBar.setForeground(Color.CYAN);
        progressBar.setBounds(0, 502, 500, 20);
        this.setLayout(null);
        //画背景
        ChessBoard back = new ChessBoard(WIDTH, HEIGHT);
        back.setBackgroundImage("/img/bg.jpg");
        back.setBounds(0, 0, WIDTH, HEIGHT);
        //装棋子的容器
        JPanel panel = new JPanel(new GridLayout(LINE_COUNT, LINE_COUNT));
        panel.setBounds(0, 0, WIDTH, HEIGHT);
        panel.setOpaque(false);
        panel.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if(status) {
                    int x = e.getX();
                    int y = e.getY();
                    int c = x / (WIDTH / LINE_COUNT);
                    int r = y / (WIDTH / LINE_COUNT);
                    wp[r][c].put(role);
                    cp[r][c] = 2 - role;

                    value = 0;
                    udpService.sendMsg(YeekuProtocol.DOWN + r + YeekuProtocol.SPLITTER + c + YeekuProtocol.DOWN, adversary.getDataAddress());
                    status = false;
                    if(checkGame(r, c)){
                        System.out.println("Game Over!!!");
                    }
                }
            }
        });
        //画棋子
        for(int i = 0; i < LINE_COUNT; i++){
            for(int j = 0; j < LINE_COUNT; j++){
                wp[i][j] = new Chess();
                wp[i][j].setOpaque(false);
                panel.add(wp[i][j]);
            }
        }
        this.add(panel);
        this.add(back);
        this.add(progressBar);
        this.setSize(WIDTH, HEIGHT + 50);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("退出房间");
                String msg = YeekuProtocol.EXIT;
                udpService.sendMsg(msg, adversary.getDataAddress());
                hall.setVisible(true);
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                countDecline();
            }
        }, 0, 1000);
    }

    /**
     * 检查游戏状态，确定是否结束
     * @param row
     * @param col
     * @return
     */
    private boolean checkGame(int row, int col){
        return checkLR(row, col) > 3 || checkUD(row, col) > 3 || checkLU(row, col) > 3 || checkRD(row, col) > 3;
    }
    private int checkLR(int row, int col){
        //先向左走
        int count = 0;
        int sr = row, sc = col;
        while(sc > 0){
            if(cp[sr][sc] != cp[sr][sc-1]) break;
            count++;
            sc--;
        }
        sr = row; sc = col;
        //向右走
        while(sc < LINE_COUNT - 1){
            if(cp[sr][sc] != cp[sr][sc+1]) break;
            count++;
            sc++;
        }
        return count;
    }
    private int checkUD(int row, int col){
        //先向上走
        int count = 0;
        int sr = row, sc = col;
        while(sr > 0){
            if(cp[sr][sc] != cp[sr-1][sc]) break;
            count++;
            sr--;
        }
        sr = row; sc = col;
        //向下走
        while(sr < LINE_COUNT - 1){
            if(cp[sr][sc] != cp[sr+1][sc]) break;
            count++;
            sr++;
        }
        return count;
    }
    private int checkLU(int row, int col){
        //先向左上走
        int count = 0;
        int sr = row, sc = col;
        while(sr > 0 && sc > 0){
            if(cp[sr][sc] != cp[sr-1][sc-1]) break;
            count++;
            sr--;
            sc--;
        }
        sr = row; sc = col;
        //向右下走
        while(sc < LINE_COUNT - 1 && sr < LINE_COUNT - 1){
            if(cp[sr][sc] != cp[sr+1][sc+1]) break;
            count++;
            sr++;
            sc++;
        }
        return count;
    }
    private int checkRD(int row, int col){
        //先向右上走
        int count = 0;
        int sr = row, sc = col;
        while(sr > 0 && sc < LINE_COUNT - 1){
            if(cp[sr][sc] != cp[sr-1][sc+1]) break;
            count++;
            sr--;
            sc++;
        }
        sr = row; sc = col;
        //向左下走
        while(sc > 0 && sr < LINE_COUNT - 1){
            if(cp[sr][sc] != cp[sr+1][sc-1]) break;
            count++;
            sr++;
            sc--;
        }
        return count;
    }
    private void countDecline(){
        if(value == 0){
            value = 100;
        }
        value--;
        progressBar.setValue(value);
        progressBar.setString(value+"");
    }

    @Override
    public void processMsg(String msg){
    	if(msg.startsWith(YeekuProtocol.OVER) && msg.endsWith(YeekuProtocol.OVER)){
    		status = false;
    	}
    	else if(msg.startsWith(YeekuProtocol.DOWN) && msg.endsWith(YeekuProtocol.DOWN)){
            String str = msg.substring(2, msg.length()-2);
            String[] arr = str.split(YeekuProtocol.SPLITTER);
            int r = Integer.parseInt(arr[0]);
            int c = Integer.parseInt(arr[1]);
            wp[r][c].put(1-role);
            cp[r][c] = 2 - role;
            if(checkGame(r, c)) {
                System.out.println("Game Over!!!");
            } else {
                status = true;
            }
        }
        else if(msg.startsWith(YeekuProtocol.EXIT) && msg.endsWith(YeekuProtocol.EXIT)){
            //游戏停止，退出房间
            System.out.println("对手已离开");
        }
    }
}

