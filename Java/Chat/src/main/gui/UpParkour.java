package main.gui;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.*;
import main.entity.EntityPlayer;

public class UpParkour extends JFrame {
	private static final long serialVersionUID = 1L;

	public static List<Integer> x = new ArrayList<Integer>();
	public static List<Integer> y = new ArrayList<Integer>();
	public static List<java.awt.image.BufferedImage> block = new ArrayList<java.awt.image.BufferedImage>();
	public static String[] Parts = null;
	public static String PlayerName = "Player1";
	public static Socket Socket;
	public static Client Client;
	int i = 0;
	
	public UpParkour() throws IOException {
		Connect(PlayerName);
		this.add(new GUI());
		this.setTitle("UpParkour");
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					if(main.Client.Players.size() > 1) {
						if (JOptionPane.showConfirmDialog(null,"Do you want to exit the game?", "Close Window?", 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
							Client.sendPacket(PlayerName, 4, 0, 0, null);
						}
					}else {
						Thread.sleep(100);
						Client.sendPacket(PlayerName, 4, 0, 0, null);
					}
				} catch (HeadlessException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

  	public void CloseGUI() {
		this.dispose();
		System.exit(0);
	}

	public static EntityPlayer getPlayer(String name) {
		for (EntityPlayer i : main.Client.Players) {
			if(i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	public void Connect(String name) {
		try {
			Socket = new Socket("88.241.238.196",1234);
			Client = new Client(Socket,PlayerName,this);
			Client.listenForPacket();
		}catch(IOException e) {
			if (JOptionPane.showConfirmDialog(null,"Cannot Connect To Server!\nClose Client?", "Connecting...", 
		            JOptionPane.CANCEL_OPTION,
		            JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION){
					System.exit(0);
			}else {
				this.CloseGUI();
			}
			
		}
	}
}
