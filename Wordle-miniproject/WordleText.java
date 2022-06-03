import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class WordleText {

	// *************  Gameover text **********
	private static final String word = WordleGUI.word;
	private static final String GAMEOVER_TEXT_WIN =   "You WIN";
	private static final String GAMEOVER_TEXT1_LOSE = "You Lose"; 
	private static final String GAMEOVER_TEXT2_LOSE = "Your word was: "; 
	
	private static final int GAMEOVER_UP_TEXT_Y = 50;
	private static final int GAMEOVER_DOWN_TEXT_Y = 610;    

	private static final Color GAMEOVER_WIN_COLOR = new Color(53, 209, 42);
	private static final Color GAMEOVER_LOSE_COLOR = new Color(189, 32, 15);    
	
	//Font attributes for the game over text
	private static final int GAMEOVER_UP_FONT_SIZE = 35;
	private static final Font GAMEOVER_UP_FONT = new Font("Consols", Font.BOLD, GAMEOVER_UP_FONT_SIZE); 
	private static final int GAMEOVER_DOWN_FONT_SIZE = 20;
	private static final Font GAMEOVER_DOWN_FONT = new Font("Consols", Font.BOLD, GAMEOVER_DOWN_FONT_SIZE); 
	
	
	// ************  debug text  ******************
	private static final String DEBUG_TEXT = "Debug Mode On, Word = ";       
	private static final int DEBUG_FONT_SIZE = 15;
	private static final Color DEBUG_FONT_COLOR = Color.YELLOW;    
	private static final Font DEBUG_FONT = new Font("Consols", Font.BOLD, DEBUG_FONT_SIZE); 
	private static final int DEBUG_TEXT_X = 10;
	private static final int DEBUG_TEXT_Y = WordleMain.HEIGHT-40;
	

	//Paints the GameOver text to the window
	public static void drawGameOverText(Graphics g){
		
		Graphics2D g2d = (Graphics2D) g;
		String text;
		// Win case
		if (WordleGUI.GameOver_check== 0) {
			g2d.setColor(GAMEOVER_WIN_COLOR);
			text = GAMEOVER_TEXT_WIN ;
			g2d.setFont(GAMEOVER_UP_FONT);
		}
		// Lose case
		else {
			g2d.setColor(GAMEOVER_LOSE_COLOR);  
			text = GAMEOVER_TEXT1_LOSE;
			g2d.setFont(GAMEOVER_UP_FONT);
		} 
		
		FontMetrics fm = g2d.getFontMetrics();// Make sure the text is in the middle
		// make the location
		int gameOver = WordleMain.WIDTH / 2 - fm.stringWidth(text) /2;
	
		g2d.drawString(text, gameOver, GAMEOVER_UP_TEXT_Y);

		if(WordleGUI.GameOver_check == 1) {
			text = GAMEOVER_TEXT2_LOSE + word;
			g2d.setFont(GAMEOVER_DOWN_FONT);
			fm = g2d.getFontMetrics();
			gameOver = WordleMain.WIDTH / 2 - fm.stringWidth(text) /2;
			g2d.drawString(text, gameOver, GAMEOVER_DOWN_TEXT_Y);
		}
		
	}
	
	//Paints the debug text to the window when running in debug mode
	public static void drawDebugText(Graphics g){        
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(DEBUG_FONT);
		g2d.setColor(DEBUG_FONT_COLOR);
		//(include the secret word in the debug text)
		g2d.drawString(DEBUG_TEXT + word, DEBUG_TEXT_X, DEBUG_TEXT_Y);  
	}  
	
}
