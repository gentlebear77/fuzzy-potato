import java.awt.Color;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

public class WordleMain {
	//*********** Const *************
	public static int WIDTH = 480;
	public static int HEIGHT = 680;    
	
	//*********** frame **************
	public static JFrame window;
	public static void frameWindow(){ 
		window = new JFrame("Wordle!");
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(WIDTH, HEIGHT);
		
		window.getContentPane().setBackground(Color.LIGHT_GRAY);
		
		window.setVisible(true);
		window.setResizable(false);
		
	}
	public static void START() throws FileNotFoundException{
		String answer;
		answer = WordleBrain.init();
		WordleGUI.create(answer);
	}
	public static void main(String[] args) throws FileNotFoundException {
		WordleBrain.DEBUG_MODE=false;
		START();
	}
}
