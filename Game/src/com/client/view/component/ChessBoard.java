package com.client.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by mdach on 2016/12/15.
 */
public class ChessBoard extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5070582491530875082L;
	private static final int LINE_COUNT = 15;
    private int width;
    private int height;
    private ImageIcon backImage = new ImageIcon();

    public ChessBoard(int width, int height){
        this.width = width;
        this.height = height;
    }
    public void setBackgroundImage(String imgSrc){
        backImage = new ImageIcon(imgSrc);
        repaint();
    }
    public void paint(Graphics g){
        super.paint(g);
        g.drawImage(backImage.getImage(), 0, 0, 500, 500, this);
        int sx, sy, ex, ey;
        int iterval = width / LINE_COUNT;
        for(int i = 0; i < LINE_COUNT; i++){
            sx = iterval * i + iterval / 2;
            sy = iterval / 2;
            ex = iterval * i + iterval / 2;
            ey = iterval * (LINE_COUNT - 1) + iterval / 2;
            g.drawLine(sx, sy, ex, ey);
            g.drawLine(sy, sx, ey, ex);
        }
        int a = iterval * 3 + iterval / 2 - 3;
        int b = iterval * 11 + iterval / 2 - 3;
        int c = iterval * 7 + iterval / 2 - 3;
        drawPoint(g, a, a);
        drawPoint(g, a, b);
        drawPoint(g, b, a);
        drawPoint(g, b, b);
        drawPoint(g, c, c);
    }
    public void drawPoint(Graphics g, int x, int y){
        Graphics2D g2 = (Graphics2D)g;
        Ellipse2D e = new Ellipse2D.Double(x, y, 6, 6);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.BLACK);
        g2.draw(e);
        g2.fill(e);
    }
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public ImageIcon getBackImage() {
		return backImage;
	}
	public void setBackImage(ImageIcon backImage) {
		this.backImage = backImage;
	}
}
