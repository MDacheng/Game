package com.client.view;

import javax.swing.*;

/**
 * Created by mdach on 2016/12/15.
 */
public abstract class GameFrame extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract void processMsg(String msg);
}
