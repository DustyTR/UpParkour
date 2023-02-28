package main.entity;

import java.awt.Graphics;

public class EntityPlayer {
	public String name;
	public int posX = 0;
	public int posY = 0;
	public boolean onGround;
	public Graphics Graphics;
	public enum Rotation {
		NULL,
	    SAG,
	    SOL
	}
	public Rotation PlayerRotation = Rotation.SAG; 
	
	public void setPosition(int posX,int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public void setRotation(Rotation rotation) {
		this.PlayerRotation = rotation;
	}
	
	public Rotation getRotation() {
		return this.PlayerRotation;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void move(int x, int y) {
		this.setPosition(posX + x, posY + y);
	}
	
	public void setGraphics(Graphics g) {
		this.Graphics = g;
	}
	
	public Graphics getGraphics() {
		return this.Graphics;
	}
}
