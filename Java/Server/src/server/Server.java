package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import main.gui.GUI;

public class Server {

	public static ServerSocket serverSocket;
	public World world = new World();
	
	public Server(ServerSocket serverSocket) {
		new GUI();
		writeGUI("Server is started.");
		Server.serverSocket = serverSocket;
	}
	
	public void StartServer() {
		try {
			while (!serverSocket.isClosed()) {
				if(ClientHandler.clientHandlers.size() < 3) {
					Socket socket = serverSocket.accept();
					ClientHandler clientHandler = new ClientHandler(socket);
					if(ClientHandler.clientHandlers.size() > 1) {
						if(!checkPlayerName(clientHandler)) {
							Thread thread = new Thread(clientHandler);
							thread.start();
							if(world.worldIsNull()) {
								world.createWorld(clientHandler);
							}else {
								world.sendWorldPacket(clientHandler);
							}
						}else {
							info(socket.getInetAddress() + " There are two players of the same name.");
							ClientHandler.Kick(clientHandler, "There are two players of the same name.");
						}
					}else {
						Thread thread = new Thread(clientHandler);
						thread.start();
						if(world.worldIsNull()) {
							world.createWorld(clientHandler);
						}else {
							world.sendWorldPacket(clientHandler);
						}
						info(socket.getInetAddress() + " a new player joined");
					}
					
				}else {
					Socket socket = serverSocket.accept();
					ClientHandler clientHandler = new ClientHandler(socket);
					info(socket.getInetAddress() + " Server is full.");
					ClientHandler.Kick(clientHandler, "Server is full.");
				}
			}
		} catch(IOException e) {
			System.out.println("Error 0/6: "+e);
		}
	}
	
	public boolean checkPlayerName(ClientHandler clientHandler) {
		for(ClientHandler clients : ClientHandler.clientHandlers) {
			if(!clientHandler.equals(clients)) {
				if(clients.Username.equals(clientHandler.Username)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void deletePlayer(ClientHandler client) {
		for(ClientHandler clients : ClientHandler.clientHandlers) {
			if(client.equals(clients)) {
				info(client.Username + " player deleted.");
				ClientHandler.clientHandlers.remove(client);
			}
		}
	}
	
	public static void writeGUI(String text) {
	    String currentText = GUI.log.getText();
	    if(currentText.length() < 350) {
		    String newTextToAppend = currentText + "\n" + text ;
		    GUI.log.setText(newTextToAppend);
	    }else {
	    	GUI.log.setText("The Log Cleaned.");
	    }
	}
	
	public static void StopServer() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		}catch(IOException e) {
			System.out.println("Socket closed.");
		}
		finally {
			writeGUI("Server closed.");
			System.exit(0);
		}
	}
	
	public static void info(String message) {
		writeGUI("[INFO]: " + message);
	}
	
	public static void error(String message) {
		writeGUI("[ERROR]: " + message);
	}
	
	public static void main(String[] args) {
		try {
			 ServerSocket serverSocket = new ServerSocket(1234);
			Server server = new Server(serverSocket);
			server.StartServer();
		} catch (IOException e) {
			System.out.println("This port is bussy now.");
		}

	}

}
