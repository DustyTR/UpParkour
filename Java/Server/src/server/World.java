package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
	
	enum Block {
		GRASS,
		STONE,
		END
	}
	
	protected static List<Integer> x = new ArrayList<Integer>();
	protected static List<Integer> y = new ArrayList<Integer>();
	protected static List<String> world = new ArrayList<String>();
	public static String[] Parts = null;
	
	public void createWorld(ClientHandler clientHandler) {
		try {
			generateWorld();
			sendWorldPacket(clientHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		public int random_int(int Min, int Max)
	{
	     return (int) (Math.random()*(Max-Min))+Min;
	}
	
	public void sendBlockPacket(ClientHandler clientHandler, int x, int y, String block) {
		try {
			clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 3 + "/" + x + "/" + y + "/" + block);
			clientHandler.bufferedWriter.newLine();
			clientHandler.bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearWorldPacket(ClientHandler clientHandler) throws IOException {
		clientHandler.bufferedWriter.write(clientHandler.Username + "/" + 3);
		clientHandler.bufferedWriter.newLine();
		clientHandler.bufferedWriter.flush();
		World.x.clear();
		World.y.clear();
		World.world.clear();
	}
	
	public boolean worldIsNull() {
		return World.x.isEmpty() && World.y.isEmpty() && World.world.isEmpty();
	}
	
	public void resetWorld() {
		try {				
			generateWorld();
		} catch (IOException e) {
			Server.error("Reset world: " + e);
		}
	}
	
	public void sendWorldPacket(ClientHandler clientHandler) {
		for (int i = World.world.size() - 1; i >= 0; i--) {
			int x = World.x.get(i);
			int y = World.y.get(i);
			String block = (World.world.get(i)).toUpperCase();
			sendBlockPacket(clientHandler, x, y, block);	
		}
	}
	
	public void generateWorld() throws IOException {
		for(ClientHandler clients : ClientHandler.clientHandlers) {
			clearWorldPacket(clients);
		}
		int lastX = 13;
		int lastY = 13;	
		int maxUp = random_int(5,10);
		for (int up = 0; up < maxUp; up++) {
			int x = random_int(Math.abs(lastX - 3),lastX + 3);
			x = Math.abs(x);
			int y = lastY - 1;
			lastX = x;
			if(lastX != x) {
				x *= 50;
			}else {
				x--;
				x *= 50;
			}
			lastY = y;
			y *= 50;
			if((up + 1) != maxUp) {
				this.addBlock(x,y,Block.GRASS);
			}else {
				this.addBlock(x,y,Block.END);
			}
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				Server.info("World in progress, at " + maxUp + "/" + (up + 1));
			}
		}
		for(ClientHandler clients : ClientHandler.clientHandlers) {
			sendWorldPacket(clients);
		}
	}
	
	public void addBlock(int x, int y, Block block) {
		World.x.add(x);
		World.y.add(y);
		if(block == Block.END) {
			World.world.add("STONE");
			World.x.add(x);
			World.y.add(y);
			World.world.add("END");
		}else {
			World.world.add("GRASS");
		}
	}
}
