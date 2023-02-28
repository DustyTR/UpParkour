package main;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import main.entity.EntityPlayer;
import main.entity.EntityPlayer.Rotation;
import main.gui.GUI;
import main.gui.UpParkour;

public class Client {
	public static List<EntityPlayer> Players = new ArrayList<EntityPlayer>();
	private UpParkour Main;
	public Socket socket;
	public BufferedReader bufferedReader;
	public BufferedWriter bufferedWriter;
	public int score;

	public Client(Socket socket, String username, UpParkour main) {
		try {
			this.Main = main;
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.score = 0;
		}catch(IOException e) {
			System.out.println("Error 0/3: " + e);
			this.CloseClient();
		}finally {
			sendPacket(username, -1, 0, 0, Rotation.NULL);
			sendPacket(username, 0, 0, 0, Rotation.NULL);
		}
	}
	
	public static void addPlayer(String name, Graphics g) {
		if(GUI.getPlayer(name) == null) {
			EntityPlayer Player = new EntityPlayer();		
			Player.setName(name);
			Player.setPosition(GUI.SCREEN_WIDTH / 2 - (GUI.UNIT_SIZE), GUI.SCREEN_HEIGHT - (GUI.UNIT_SIZE * 2));
			Client.Players.add(Player);
			System.out.println("Player has joined the game: " + name);
			System.out.println("Total Players: " + Client.Players.size());
		}
	}
	public void sendPacket(String name, int type, int x, int y, Rotation rotation) {
		try {
			System.out.println(UpParkour.PlayerName + " --> "+name + "/" + type + "/" + x + "/" + y + "/" + rotation);
			if(socket.isConnected()) {
				if(type == (0)) {
					this.bufferedWriter.write(name + "/" + type);
				}else if(type == (1)) {
					this.bufferedWriter.write(name + "/" + type + "/" + x + "/" + y + "/" + rotation);	
				}else if(type == (-1)) {
					this.bufferedWriter.write(name);
				}else {
					this.bufferedWriter.write(name + "/" + type + "/" + x + "/" + y + "/" + rotation);
				}
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		}catch(IOException e) {
			System.out.println("Error 1/3: " + e);
			this.CloseClient();
		}
	}
	
	public EntityPlayer getPlayer(String name) {
		for (EntityPlayer i : Client.Players) {
			if(i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	public void listenForPacket() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String packet;
				while(socket.isConnected()) {
					try {
						packet = bufferedReader.readLine();
						if(!packet.equals(null) && packet != null && !packet.equals("") && packet != "") {
							String[] Parts = packet.split("/");
							//if(!Parts[1].isBlank() && Parts[1].equals("3")) {
								//System.out.println(UpParkour.PlayerName + ",Packet: " + packet);
							//}
							if(!Parts[1].isBlank() && !Parts[0].isBlank()) {
								switch(Parts[1]) {
								case("0"):
									if(GUI.Graphics != null) {
										Client.addPlayer(Parts[0],GUI.Graphics);
									}
								break;
								case("1"):
									if(getPlayer(Parts[0]) != null) {
										getPlayer(Parts[0]).setPosition(Integer.parseInt(Parts[2]), Integer.parseInt(Parts[3]));
										if(Parts[4].equals("SAG")) {
											getPlayer(Parts[0]).setRotation(Rotation.SAG);
										}else if(Parts[4].equals("SOL")) {
											getPlayer(Parts[0]).setRotation(Rotation.SOL);
										}else {
											getPlayer(Parts[0]).setRotation(Rotation.SAG);
										}
									}else {
										if(GUI.Graphics != null) {
											Client.addPlayer(Parts[0],GUI.Graphics);
										}
									}
								break;
								case("2"):
									if(!Parts[2].isBlank()) {
										score = Integer.parseInt(Parts[2]);
									}
									break;
								case("3"):
									//WORLD
									if(Parts.length > 2) {
										java.awt.image.BufferedImage img = null;
										if(Parts[4].equals("GRASS")) {
											URL url = null;
											try {
												url = new URL(getFile() + "/images/grass.png");
											} catch (Exception e) {
												e.printStackTrace();
											}
											img = ImageIO.read(url);
										}else if(Parts[4].equals("STONE")) {
											URL url = null;
											try {
												url = new URL(getFile() + "/images/stone.png");
											} catch (Exception e) {
												e.printStackTrace();
											}
											img = ImageIO.read(url);
										}else if(Parts[4].equals("END")) {
											img = null;
										}else {
											System.out.println("Packet texture error.");
										}
										UpParkour.x.add(Integer.parseInt(Parts[2]));
										UpParkour.y.add(Integer.parseInt(Parts[3]));
										UpParkour.block.add(img);
										System.out.println("[WORLD]: "+"Block added: " + "X: " + Parts[2] + ",Y: " + Parts[3] + ",Image: " + Parts[4].toLowerCase());
									}else {
										UpParkour.x.clear();
										UpParkour.y.clear();
										UpParkour.block.clear();
										System.out.println("[WORLD]: " + "World deleted.");
									}
									break;
								case("4"):
									if(Parts[0].equals(UpParkour.PlayerName)) {
										JOptionPane.showMessageDialog(null, "You are kicked, reason: \n" + Parts[2]);
										CloseClient();	
									}
									break;
								case("5"):
									Iterator<EntityPlayer> it = Client.Players.iterator();
									while(it.hasNext()) {
										EntityPlayer i = it.next();
										if(i.getName().equals(Parts[0])) {
											it.remove();
										}
									}
									break;
								default:
									break;
								}
							}
						}
					}catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Server Closed.\n"+ e);
						CloseClient();
						break;
					}
				}
			}
		}).start();
	}

	public void CloseClient() {
		try {
			sendPacket(UpParkour.PlayerName, 4, 0, 0, null);
			bufferedReader.close();
			socket.close();
			System.out.println("Client closed.");
			System.exit(0);
		}catch(IOException e) {
			System.exit(0);
		}
	}
	
	public static URL getFile() throws Exception {
		File file = new File(new File(".").getCanonicalPath());
		return file.toURI().toURL(); 
	}
	
	public static void main(String[] args) throws IOException {
		new UpParkour();
	}
}