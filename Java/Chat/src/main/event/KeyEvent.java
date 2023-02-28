package main.event;

import main.Client;
import main.entity.EntityPlayer;
import main.entity.EntityPlayer.Rotation;
import main.gui.GUI;
import main.gui.UpParkour;


public class KeyEvent implements java.awt.event.KeyListener{
	public EntityPlayer getPlayer(String name) {
		for (EntityPlayer i : Client.Players) {
			if(i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		
	}
	
	Character lastKey;
	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		if(GUI.running) {
			if(getPlayer(UpParkour.PlayerName) != null) {
				switch(e.getKeyCode()) {
				case 87://W
					if(getPlayer(UpParkour.PlayerName).onGround) {
						GUI.Velocity(32, UpParkour.PlayerName);
						//
					}
					break;
				case 65://A
					getPlayer(UpParkour.PlayerName).move(-50, 0);
					getPlayer(UpParkour.PlayerName).setRotation(Rotation.SOL);
					if(getPlayer(UpParkour.PlayerName).onGround)
						Sound.play(0);
					break;
				case 68://D
					getPlayer(UpParkour.PlayerName).move(50, 0);
					getPlayer(UpParkour.PlayerName).setRotation(Rotation.SAG);
					if(getPlayer(UpParkour.PlayerName).onGround)
						Sound.play(0);
					break;
				}
			}
			UpParkour.Client.sendPacket(UpParkour.PlayerName, 1, getPlayer(UpParkour.PlayerName).posX, getPlayer(UpParkour.PlayerName).posY, getPlayer(UpParkour.PlayerName).getRotation());
		}else {
			switch(e.getKeyCode()) {
				case 13:
					
					break;
				case 8:
					
					break;
				default:
					if (lastKey == null || lastKey != e.getKeyChar()) {
	                    lastKey = e.getKeyChar();
	                    UpParkour.PlayerName = (UpParkour.PlayerName.toLowerCase() + e.getKeyChar());
					}
					break;
			}
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		lastKey = null;
	}

}
