package moe.pgnhd.badapple_renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Apple {
	
	private static JFrame jFrame;
	private static ImageIcon icon = new ImageIcon();
	
	private static void makeFrame() {
		jFrame = new JFrame();
		jFrame.getContentPane().add(new JLabel(icon));
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setTitle("Bad Apple");
		jFrame.setVisible(true);
	}
	
	private static void updateFrame(BufferedImage img) {
		jFrame.setSize(img.getWidth(), img.getHeight());
		icon.setImage(img);
		jFrame.repaint();
	}
	
	// https://stackoverflow.com/questions/17401852/open-video-file-with-opencv-java
	private static BufferedImage mat2buffereImage(Mat m) { 
		int type = BufferedImage.TYPE_BYTE_GRAY;
	     if ( m.channels() > 1 ) {
	         type = BufferedImage.TYPE_3BYTE_BGR;
	     }
	     int bufferSize = m.channels()*m.cols()*m.rows();
	     byte [] b = new byte[bufferSize];
	     m.get(0,0,b); // get all the pixels
	     BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	     final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	     System.arraycopy(b, 0, targetPixels, 0, b.length);  
	     return image;
	}
	
	
	private static void printFrame(BufferedImage img) {
		char dark = '#';
		char bright = ' ';
		
		System.out.println();
		
		double fontMod = 3; // scale font
		int xRes = (int) (10 * fontMod);
		int yRes = 10;
		
		int ixRes = img.getWidth();
		int iyRes = img.getHeight();
		
		double xSizeMod = ixRes / xRes;
		double ySizeMod = iyRes / yRes;

		
		int frameSize = (int) (xRes * yRes * fontMod * 1);
		frameSize *= 0.5; // add some margin
		
		StringBuilder frameBuffer = new StringBuilder(frameSize);
		
		System.out.println(xSizeMod + " " + xSizeMod);
		
		for(int y=0; y<yRes; y++) {
			for(int x=0; x<xRes; x++) {
				
				int computedX = (int) (x * xSizeMod);
				int computedY = (int) (y* ySizeMod );
				
				// exclude last pixel
				computedX = computedX != ixRes ? computedX : computedX - 1;
				computedY = computedY != iyRes ? computedY : computedY - 1 ;

				int rgb = img.getRGB(computedX, computedY);
				
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 0) & 0xFF;
				
				int grey = (r + g + b) / 3;
				frameBuffer.append(grey < 127 ? bright : dark);

			}
			frameBuffer.append("|\n");
		}
		for(int i=0; i<xRes; i++) {frameBuffer.append('-');}
		System.out.println(frameBuffer);
	}
	
	
	public static void main(String[] args) throws Exception {
		
		// replace with video file
		String fileName = "<>";
		if(!new File(fileName).exists()) {
			throw new IOException("File does not exist");
		}
		
		// replace with path to opencv_javaXXX.dll
		System.load("<>");
		
		Mat frame = new Mat();
		VideoCapture cap = new VideoCapture(fileName);
		double rawFrameRate = cap.get(Videoio.CAP_PROP_FPS);
		double speed = 1.0;
		double frameRate = rawFrameRate * speed;
		double frameTime = 1000 / frameRate;
		System.out.printf("%f %f %f%n", speed, frameRate, frameTime);
		
		makeFrame();
		
		// replace with audio file to sync
		AudioInputStream audIn = AudioSystem.getAudioInputStream(new File("<>"));
		Clip clip = AudioSystem.getClip();
		clip.open(audIn);
		clip.start(); // audio is not correctly synchronized at the time
		
		long lastFrame = 0;
		
		for(;;) {
			
			lastFrame = System.nanoTime();
			cap.read(frame);
			updateFrame(mat2buffereImage(frame));
			
			int computedFrameTime = (int) (frameTime - ((lastFrame - System.nanoTime()) / 1000000));
			System.err.println(computedFrameTime); 
			computedFrameTime = computedFrameTime < 0 ? 0 : computedFrameTime; // min 0

			
			try {Thread.sleep(computedFrameTime);}catch(Exception e){}
			
			try {
				printFrame(mat2buffereImage(frame));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
