
package server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private static Socket socket;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	private static World world;
	private int score;
	private int lastX;
	private int lastY;
	private int warns;
	public enum Packet{
		Join,
		Leave,
		End,
		Move
	}
	public enum Rotation {
		NULL,
	    SAG,
	    SOL
	}
	
	private Rotation lastRotation;
	String Username;

	public ClientHandler(Socket socket) {
		try {
			ClientHandler.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.Username = bufferedReader.readLine();
			this.lastRotation = Rotation.NULL;
			this.lastX = 600;
			this.lastY = 650;
			this.warns = 0;
			ClientHandler.world = new World();
			clientHandlers.add(this);
		}catch(IOException e) {
			Server.error("Setting Server.");
		}
	}
	
	@Override
	public void run() {
		String Packet;
		while(socket.isConnected()) {
			try {
				Packet = bufferedReader.readLine();
				if(Packet != null && clientHandlers.size() > 0)
					this.SendPacket(Packet);
			} catch (IOException e) {
				Server.error("BufferedReader server error.");
				break;
			}
		}
	}
	
	public Packet Converter(String[] Parts) {
		if(Parts[1].equals("0")) {
			return Packet.Join;
		}else if(Parts[1].equals("1")) {
			return Packet.Move;
		}else if(Parts[1].equals("2")) {
			return Packet.End;
		}else if(Parts[1].equals("4")) {
			return Packet.Leave;
		}else {
			return null;
		}
	}
	
	public void SendPacket(String packet) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandlers.size() > 0) {
				//<name> <type> <x> <y> <z> <rotation>
				try {
					String[] Parts = packet.split("/");
					if(!Parts[1].isBlank()) {
						if(!clientHandler.Username.equals(Username)) {
							if(Converter(Parts) == Packet.Move) {
								this.update(clientHandler, Parts);
								clientHandler.bufferedWriter.write(packet);
								clientHandler.bufferedWriter.newLine();
								clientHandler.bufferedWriter.flush();
							}
						}else {
							if(Converter(Parts) == Packet.End) {
								if(clientHandler.Username.equals(Username)) {
										//if(Integer.parseInt(Parts[2]) == x)
									setScore(getScore(clientHandler) + 1, clientHandler);
									clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 2 + "/" + getScore(clientHandler));
									clientHandler.bufferedWriter.newLine();
									clientHandler.bufferedWriter.flush();
									System.out.println("a");
									for(ClientHandler clients : clientHandlers) {
										Teleport(clients.Username,600,650,null);
									}
									world.resetWorld();
									Server.info(clientHandler.Username + " -> " + getScore(clientHandler));
									
								}
							}else if(Converter(Parts) == Packet.Join) {
								SendAllPlayers(clientHandler);
							}else if(Converter(Parts) == Packet.Leave) {
								Server.info(Username + " player has left.");
								Kick(clientHandler,"You have left.");
							}
						}
					}else {
						Server.info(Parts[0] + " joined the server");
					}
				}catch(IOException e) {
					clientHandlers.remove(clientHandler);
					Server.info("Player has dissconected: " + e); 
				}
			}else {
				Server.error("No player found");
			}
		}
	}
	
	public void update(ClientHandler clientHandler, String[] Parts) {
		clientHandler.lastX = Integer.parseInt(Parts[2]);
		clientHandler.lastY = Integer.parseInt(Parts[3]);
		if(Parts[4].equals("SAG")) {
			clientHandler.lastRotation = (Rotation.SAG);
		}else if(Parts[4].equals("SOL")) {
			clientHandler.lastRotation = (Rotation.SOL);
		}else {
			clientHandler.lastRotation = (Rotation.SAG);
		}
		Server.info("Player Name: " + clientHandler.Username +
				" ,X: " + clientHandler.lastX +
				" ,Y: " + clientHandler.lastY + 
				" ,Rotation: " + clientHandler.lastRotation);
	}

	public void SendAllPlayers(ClientHandler player) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(ClientHandler clientHandler : clientHandlers) {
					if(!clientHandler.Username.equals(player.Username)) {
						try {
							player.bufferedWriter.write(clientHandler.Username + "/" + 1 + "/" + clientHandler.lastX + "/" + clientHandler.lastY + "/" + clientHandler.lastRotation);	
							player.bufferedWriter.newLine();
							player.bufferedWriter.flush();
						}catch(IOException e) {
							clientHandlers.remove(clientHandler);
							Server.error("Player has dissconected: " + e); 
							break;
						}
					}
				}
			}
		}).start();
	}
	
	public static void Kick(ClientHandler player, String reason) throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(player != null &&
						clientHandlers.size() > 0) {
					for(ClientHandler clientHandler : clientHandlers) {
						if(clientHandler.equals(player)) {
							try {
								clientHandler.bufferedWriter.write(player.Username + "/" + 4 + "/" + reason);
								clientHandler.bufferedWriter.newLine();
								clientHandler.bufferedWriter.flush();
								Server.info("Player " + player.Username + " has kicked, reason: " + reason);
							}catch(IOException e) {
								clientHandlers.remove(clientHandler);
								Server.error("Player has dissconected: " + e); 
								break;
							}
						}
					}
					for(ClientHandler clients : clientHandlers) {
						try {
							clients.bufferedWriter.write(player.Username + "/" + 5);
							clients.bufferedWriter.newLine();
							clients.bufferedWriter.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Iterator<ClientHandler> it = clientHandlers.iterator();
					while(it.hasNext()) {
						ClientHandler i = it.next();
						if(i.equals(player)) {
							it.remove();
						}
					}
				}
			}
		}).start();
	}
	
	public static void Teleport(String player, int x, int y, Rotation rotation) {
		if(clientHandlers.size() > 0) {
			for(ClientHandler clientHandler : clientHandlers) {
				if(clientHandler.Username.equals(player)) {
					try {
						clientHandler.bufferedWriter.write(player + "/" + 1 + "/" + x + "/" + y + "/" + rotation);
						clientHandler.bufferedWriter.newLine();
						clientHandler.bufferedWriter.flush();
						Server.info("Player " + player + " teleported to:" + " X: " + x + " Y: " + y + " Rotation: " + rotation);
					}catch(IOException e) {
						try {
							Kick(clientHandler,"You dissconected.");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						Server.error("Player has dissconected: " + e); 
					}
				}
			}
		}
	}
	
	public void update() {
		
	}
	
	public static void Console(String command) {
		String[] args = command.split(" ");
		if(args[0].equalsIgnoreCase("/kick")) {
			if(!args[2].isBlank() && !args[1].isBlank()) {
				try {
					Thread.sleep(100);
					Kick(getClient(args[1]),args[2]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(args[0].equalsIgnoreCase("/stop")) {
			try {
				Server.StopServer();
				System.exit(0);
			}finally {
				Server.info("Server Closed");
			}
			System.exit(0);
		}else if(args[0].equalsIgnoreCase("/tp")) {
			if(!args[1].isBlank() &&
					!args[2].isBlank() &&
				!args[3].isBlank()) {
				int x = Integer.parseInt(args[2]);
				int y = Integer.parseInt(args[3]);
				Teleport(args[1], x, y, null);
		}else {
			Server.error("No coordinate found.");	
		}
		}else if(args[0].equalsIgnoreCase("/list")) {
				for(int i = 0; i < clientHandlers.size(); i++) {  
					if(clientHandlers.size() > 0) {
						Server.info((i + 1) + " -> "+clientHandlers.get(i).Username);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else {
						Server.error("No player found");
					}
				}
				System.out.println("Total: " + clientHandlers.size());
		}else if(args[0].equalsIgnoreCase("/score")) {
			if(!args[1].isBlank() && !args[2].isBlank()) {
				// -/score <set/add/reset> <name> <value> 
				if(args[1].equalsIgnoreCase("set")) {
					for(ClientHandler clientHandler : clientHandlers) {
						if(!args[2].isBlank() && clientHandler.Username.equals(args[2])) {
							try {
								setScore(Integer.parseInt(args[3]),clientHandler);
								clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 2 + "/" + getScore(clientHandler));
								clientHandler.bufferedWriter.newLine();
								clientHandler.bufferedWriter.flush();
							} catch (IOException e) {
								Server.error("Send packet, " + e);
							} catch (NumberFormatException e) {
								Server.error("Value is too long.");
							}
							Server.info("Player, " + clientHandler.Username + " score settled to " + args[1] + ".");
						}
					}
				}else if(args[1].equalsIgnoreCase("add")) {
					for(ClientHandler clientHandler : clientHandlers) {
						if(!args[2].isBlank() && clientHandler.Username.equals(args[2])) {
							try {
								setScore((getScore(clientHandler) + Integer.parseInt(args[3])),clientHandler);
								clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 2 + "/" + getScore(clientHandler));
								clientHandler.bufferedWriter.newLine();
								clientHandler.bufferedWriter.flush();
							} catch (IOException e) {
								Server.error("Send packet, " + e);
							} 
							catch (NumberFormatException e) {
								Server.error("Value is too long.");
							}
							Server.info("Player, " + clientHandler.Username + " score added.");
						}
					}
				}else if(args[1].equalsIgnoreCase("reset")) {
					for(ClientHandler clientHandler : clientHandlers) {
						if(!args[2].isBlank() && clientHandler.Username.equals(args[2])) {
							setScore(0,clientHandler);
							try {
								clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 2 + "/" + getScore(clientHandler));
								clientHandler.bufferedWriter.newLine();
								clientHandler.bufferedWriter.flush();
							} catch (IOException e) {
								Server.error("Send packet, " + e);
							}
							Server.info("Player, " + clientHandler.Username + " score reseted.");
						}
					}
				}
			}else {
				Server.error("No player and value found.");	
			}
		}else if(args[0].equalsIgnoreCase("/world")) {
			if(args[1].equalsIgnoreCase("clear")) {
				for(ClientHandler clientHandler : clientHandlers) {
					try {
						world.clearWorldPacket(clientHandler);
						Server.info("World deleted.");
					} catch (IOException e) {
						Server.error("Clear world");
					}
				}
			}if(args[1].equalsIgnoreCase("new")) {
				world.resetWorld();
				Server.info("New world generated.");
			}else {
				Server.error("Unknown command");
			}
		}else {
			Server.error("No command.");
		}
	}
	
	public static ClientHandler getClient(String player) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandler.Username.equals(player)) {
				return clientHandler;
			}
		}
		return null;
	}
	
	public static int getScore(ClientHandler player) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandler.equals(player)) {
				return clientHandler.score;
			}
		}
		Server.error("No player found");
		return -1;
	}

	public static void setScore(int score, ClientHandler player) {
		for(ClientHandler clientHandler : clientHandlers) {
			if(clientHandler.equals(player)) {
				clientHandler.score = score;
			}else {
				Server.info("Trying to find player...");
			}
		}
	}
}
