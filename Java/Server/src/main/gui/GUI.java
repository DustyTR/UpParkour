package main.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import server.ClientHandler;
import server.Server;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final JFrame jFrame = new JFrame("Log");
    public final static JTextArea log = new JTextArea(20,20);
    public final static JTextField command = new JTextField("/");
    public final JScrollPane scroll = new JScrollPane(log);

	public GUI() {
				Border border = new LineBorder(Color.black, 1, true);
				log.setBounds(15,15,250,200);
				log.setBorder(border);
				command.setBounds(15,220,250,20);
				command.setBorder(border);
				command.addActionListener((ActionListener) this);
				jFrame.add(log);
				jFrame.add(command);
	            jFrame.getContentPane().add(scroll);
	            jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				jFrame.setBounds(0,0,300,300);
				jFrame.setResizable(false);
	            jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				    @Override
				    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				    	try {
				    		if (JOptionPane.showConfirmDialog(null,"Shut down the server?", "Close Server?", 
				    				JOptionPane.YES_NO_OPTION, 
				    				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				    			Server.StopServer();
							}
						} catch (HeadlessException e) {
							e.printStackTrace();
						}
				    }
				});
	            jFrame.setLayout(null);
	            jFrame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ClientHandler.Console(command.getText());
		command.setText("/");
	}

}
