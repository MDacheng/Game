package com.client.view.component;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by mdach on 2016/12/15.
 */
public class Chess extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7127680424776454104L;
	private int sig = -1;
    public void paint(Graphics g){
        super.paint(g);
        if(sig != -1){
            Graphics2D g2d = (Graphics2D) g;
            RadialGradientPaint paint = new RadialGradientPaint(65*5/17-2, 65*5/17+2, 500/17/4 + 40* sig, new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.BLACK});
            g2d.setPaint(paint);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            Ellipse2D e = new Ellipse2D.Float(0, 0, 500/17, 500/17);
            g2d.fill(e);
        }
    }
    public void put(int sig){
        this.sig = sig;
        repaint();
    }
}
