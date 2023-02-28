package main.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.Client;
import main.entity.EntityPlayer;
import main.entity.EntityPlayer.Rotation;

import java.util.Iterator;
import java.util.Random;

public class GUI extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static final int SCREEN_WIDTH = 1300;
	private long lastTime;
    private double fps;
	//650 (600 UNITSIZE)
	public static final int SCREEN_HEIGHT = 750;
	public static final int UNIT_SIZE = 50;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	public static Graphics Graphics = null;
	public static boolean running;
	Rectangle Player = new Rectangle(0,0,UNIT_SIZE, UNIT_SIZE);
	Timer timer;
	Random random;
	
	public GUI() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.startClient();
		this.startFPS();
		this.addKeyListener(new main.event.KeyEvent());
	}
	
	public void startClient() {
		running = true;
		timer = new Timer(20,this);
		timer.start();
	}

	public static EntityPlayer getPlayer(String name) {
		for (EntityPlayer i : Client.Players) {
			if(i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	public void startFPS() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					lastTime = System.nanoTime();
		            try{
		                Thread.sleep(1000);
		            }
		            catch (InterruptedException e){

		            }
		            fps = 1000000000.0 / (System.nanoTime() - lastTime);
		            lastTime = System.nanoTime();
				}
			}
		}).start();
	}
	
	public int getFPS() {
		return (int)fps;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics = g;
		if(running) {
			draw(g, UpParkour.PlayerName);
		}else {
			MainMenu(g);
		}
	}
	public void MainMenu(Graphics g) {;
		g.setFont(new Font("default", Font.BOLD, 40));
		g.setColor(Color.white);
		g.drawString("Enter Name:", SCREEN_WIDTH / 2 - (("Enter Name:").length() * 10), SCREEN_HEIGHT / 2 - 120);
		g.drawString(UpParkour.PlayerName.toString(), SCREEN_WIDTH / 2 - ((UpParkour.PlayerName.toString()).length() * 10), SCREEN_HEIGHT / 2 - 120);
		g.setColor(Color.WHITE);
		this.addKeyListener(new main.event.KeyEvent());
	}
	
	public void draw(Graphics g, String name) {
		if(running) {
			if(getPlayer(name) == null) {
				Client.addPlayer(name, g);
			}
			checkCollisions(name);
			Gravity(name,g);
			updatePlayer(g);
			ScoreText(g);
		}	
	}
	public void ScoreText(Graphics g) {
		g.setFont(new Font("default", Font.BOLD, 16));
		g.setColor(Color.RED);
		try {
			g.drawString("Score: "+UpParkour.Client.score + ", " + Client.getFile(), 15, 35);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(5));
		g.drawRect(10, 10, 65 + ((""+UpParkour.Client.score).length() * 10), 40);
	}
	
	public void updatePlayer(Graphics g) {
		for (int p = 0; p < Client.Players.size(); p++) {
			EntityPlayer i = Client.Players.get(p);
			g.setColor(Color.white);
			int width = 40;
			int height = 85;
			if(g != null) {
				g.setColor(Color.white);
				if(i.getRotation().equals(Rotation.SAG)) {
					try {
						g.drawImage(ImageIO.read(new URL(Client.getFile() + "/images/player_right.png")),i.posX,i.posY - height,width,height,null);
					} catch (IOException e) {
						System.out.println("UpdatePlayer Texture " + e);
					} catch (Exception e) {
						System.out.println("UpdatePlayer Texture " + e);
					}
				}else if(i.getRotation().equals(Rotation.SOL)){
					try {
						g.drawImage(ImageIO.read(new URL(Client.getFile() + "/images/player_left.png")),i.posX,i.posY - height,width,height,null);
					} catch (IOException e) {
						System.out.println("UpdatePlayer Texture " + e);
					} catch (Exception e) {
						System.out.println("UpdatePlayer Texture " + e);
					}
				}
				g.drawString(i.getName(), i.posX - i.getName().length() + (width / 2), i.posY - (height + 10));
				try {
					Map(g);
				} catch (IOException e) {
					System.out.println("Loading Map Texture " + e);
				}
			}
		}
	}
	

	public void checkCollisions(String name) {
		if(getPlayer(name).posX < 0) {
			getPlayer(name).posX = 0;
		}
		if(getPlayer(name).posX > SCREEN_WIDTH) {
			getPlayer(name).posX = SCREEN_WIDTH;
		}
		if(getPlayer(name).posY < 0) {
			getPlayer(name).posY = 0;
		}
		if(getPlayer(name).posY > SCREEN_HEIGHT) {
			getPlayer(name).posY = SCREEN_HEIGHT - UNIT_SIZE;
		}
	}
	
	public void Map(Graphics g) throws IOException {
		g.setColor(Color.white);
		URL url = null;
		BufferedImage image = null;
		try {
			url = new URL(Client.getFile() + "/images/grass.png");
			image = ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i = 0; i < SCREEN_WIDTH;i++) {
			g.drawImage(image,i,SCREEN_HEIGHT - (UNIT_SIZE),10,SCREEN_HEIGHT - (SCREEN_HEIGHT - UNIT_SIZE),null);
		}
		
		for (int x = 0; x < UpParkour.block.size(); x++) {
			g.drawImage(UpParkour.block.get(x),UpParkour.x.get(x),UpParkour.y.get(x),40,40,null);
		}
	}
	
	public void Gravity(String name, Graphics g) {
		if(!UpParkour.block.isEmpty()) {
			for (int i = 0; i < UpParkour.block.size(); i++) {
				getPlayer(name).onGround = (getPlayer(name).posX == UpParkour.x.get(i)) && ((getPlayer(name).posY) >= UpParkour.y.get(i) && (getPlayer(name).posY) <= UpParkour.y.get(i) + 40) || (getPlayer(name).posY) >= SCREEN_HEIGHT - (UNIT_SIZE);
				if((getPlayer(name).posX == UpParkour.x.get(i)) && ((getPlayer(name).posY) >= UpParkour.y.get(i) && (getPlayer(name).posY) <= UpParkour.y.get(i) + 40)) {
					if(getPlayer(name).onGround)
						break;
					getPlayer(name).setPosition(getPlayer(name).posX, UpParkour.y.get(i));
					UpParkour.Client.sendPacket(UpParkour.PlayerName, 1, getPlayer(UpParkour.PlayerName).posX, getPlayer(UpParkour.PlayerName).posY, getPlayer(UpParkour.PlayerName).getRotation());
				}
			}
		}else {
			getPlayer(name).onGround = (getPlayer(name).posY) >= SCREEN_HEIGHT - (UNIT_SIZE);
		}
		if(!getPlayer(name).onGround) {
			UpParkour.Client.sendPacket(UpParkour.PlayerName, 1, getPlayer(UpParkour.PlayerName).posX, getPlayer(UpParkour.PlayerName).posY, getPlayer(UpParkour.PlayerName).getRotation());
			getPlayer(name).move(0, 5);
		}
		End(name,g);
	}
	
	public void End(String name, Graphics g) {
		for (int i = 0; i < UpParkour.block.size(); i++) {
			if(UpParkour.block.get(i) == null) {
				if((getPlayer(name).posX == UpParkour.x.get(i)) && ((getPlayer(name).posY) >= UpParkour.y.get(i) && (getPlayer(name).posY) <= UpParkour.y.get(i) + 40)) {
					UpParkour.Client.sendPacket(name, 2, getPlayer(name).posX, getPlayer(name).posY, getPlayer(name).getRotation());
					updatePlayer(g);
					System.out.println("e");
				}
			}
		}
	}
	
	public static void Velocity(int y, String name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int Y = 1; Y < y; Y++) {
					if(Y < 5) {
						getPlayer(name).setPosition(getPlayer(name).posX, getPlayer(name).posY - (y * 2 / Y));
						UpParkour.Client.sendPacket(UpParkour.PlayerName, 1, getPlayer(UpParkour.PlayerName).posX, getPlayer(UpParkour.PlayerName).posY, getPlayer(UpParkour.PlayerName).getRotation());
						try {
							Thread.sleep(27L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
}
