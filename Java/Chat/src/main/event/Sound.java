package main.event;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import main.Client;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class Sound {
	public static Random r = new Random();
	public static void play(final int value) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				InputStream file = null;
				URL url = null;
				if(value == 1) {
					file = null;
				}else {
					boolean random = r.nextBoolean();
					if(random) {
						try {
							try {
								url = new URL(Client.getFile() + "/sounds/walk_sound.wav");
							} catch (Exception e) {
								e.printStackTrace();
							}
							file = url.openStream();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						try {
							try {
								url = new URL(Client.getFile() + "/sounds/walk_sound.wav");
							} catch (Exception e) {
								e.printStackTrace();
							}
							file = url.openStream();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				try (final AudioInputStream in = getAudioInputStream(file)) {
					final AudioFormat outFormat = getOutFormat(in.getFormat());
					final Info info = new Info(SourceDataLine.class, outFormat);        
					try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
						if (line != null) {
							line.open(outFormat);
							line.start();
							stream(getAudioInputStream(outFormat, in), line);
							line.drain();
							line.stop();
						}
					}
				} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
					throw new IllegalStateException(e);
				}
			}
		 }).start();
	 }
	 
	 private static AudioFormat getOutFormat(AudioFormat inFormat) {
		 final int ch = inFormat.getChannels();
		 
		 final float rate = inFormat.getSampleRate();
		 return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	 }
	 
	 private static void stream(AudioInputStream in, SourceDataLine line) 
		throws IOException {
		 final byte[] buffer = new byte[4096];
		 for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			 line.write(buffer, 0, n);
		 }
	 }
}
