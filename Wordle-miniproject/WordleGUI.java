import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

public class WordleGUI extends JComponent implements KeyListener {
	
	public static final char EMPTY = '\0';  
	
	//GUI of cell
	private static final int CELL_SIZE = 50;
	private static final int CELL_PADDING = 10;
	private static final int CELL_MARGIN_X = 95;
	private static final int CELL_MARGIN_Y = 80;
	private static final int BORDER_THICKNESS = 2;    
	
	//GUI of keyboard
	private static final int KEYBOARD_WIDTH = 35;
	private static final int KEYBOARD_WIDTH_WIDE = 52;
	private static final int KEYBOARD_HEIGHT = 35;
	private static final int KEYBOARD_PADDING = 7;       
	private static final String LETTERS = "QWERTYUIOPASDFGHJKLZXCVBNM";

	private static final int[] KEYBOARD_PER_ROW = {10, 9, 7};

	private static final int[] KEYBOARD_MARGIN_X = {35, 55, 95};
	private static final int KEYBOARD_MARGIN_Y = 6 * (CELL_SIZE + CELL_PADDING) + 100;     
	
	
	// special key in the keyboard
	private static final String ENTER_KEY_STRING = "Enter";

	private static final String BACKSPACE_KEY_STRING = "Delete";
	
	// the shaking row's interval and bound 
	private static final int[] SHAKE_INTERVAL = {-1, 1, -1, 1, -1, 1, -1};
	private static final int[] SHAKE_BOUND = {10, 20, 20, 20, 20, 20, 10};

	
	//*************     Class Variables    **************   
	
	private static WordleGUI yhz_wordle = new WordleGUI();
	// cell container
	private static LetterCell[][] cells = new LetterCell[6][5];
	// we use the hashmap to contain the keyboard
	// the hashmap holds two variables one is character the other is keyboard
	// and these two can pair each other by the hashmap.get() method
	// it is also can be replaced by the array but hashmap supplies better
	private static HashMap<Character, KeyboardCell> keyboard;

	// check whether the is over and judge the result
	public static int GameOver_check = -1;
	// not over yet:-1
	// win: 0
	// lose: 1

	static String word;
	
	// check the cell whether is shaking 
	private static int rowShaking = -1;
	private static int shakeStep = 0;
	private static int shakeCount = 0;

	// **************** operating the brain logic *************
	// set the letter
	public static void setCellLetter(int cellRow, int cellCol, char letter){
		if (Character.isLetter(letter)) { 
			cells[cellRow][cellCol].setLetter(Character.toUpperCase(letter));
		} else if (letter == EMPTY) {
			cells[cellRow][cellCol].setLetter(EMPTY);
		}
	}
	
	// Draw the color
	public static void setCellColor(int cellRow, int cellCol, Color newColor){
		cells[cellRow][cellCol].setColor(newColor);
	}
	// Keyboard Color
	public static Color getKeyboardColor(char letter){
		 return keyboard.get(Character.toUpperCase(letter)).getColor();
	}  
	// update the color
	public static void setKeyboardColor(char letter, Color newColor){
		keyboard.get(Character.toUpperCase(letter)).setColor(newColor);
	}  
	public static void shakeRow(int row){
		//if there's already a row wiggling
		if (rowShaking > -1)
			return;
		rowShaking = row;          
	}	
	
	//****************** Methods to draw the GUI *******************
	public static void create(String secret){                
		initCells();
		initKeyboard();
		WordleMain.frameWindow();
		WordleMain.window.add(yhz_wordle);
		yhz_wordle.setBackground(Color.BLACK);
		yhz_wordle.setOpaque(false);
		yhz_wordle.requestFocus();
		yhz_wordle.addKeyListener(yhz_wordle);
		
		word = secret;
		WordleMain.window.repaint();
	}
	
	//Initializes the cells
	private static void initCells(){
		
		for (int row = 0; row < cells.length; row++){      
			for (int col = 0; col <cells[0].length; col++){
				int x = CELL_MARGIN_X + (col * (CELL_SIZE + CELL_PADDING));
				int y = CELL_MARGIN_Y + (row * (CELL_SIZE + CELL_PADDING));
				cells[row][col] = new LetterCell(x, y);
				cells[row][col].setVisible(true);
				yhz_wordle.add(cells[row][col]);
			}
		}
	}
	
	//Initializes the interface keyboard
	private static void initKeyboard(){
		// Map keys is the letter of the respective key
		keyboard = new HashMap<Character, KeyboardCell>();
		int ct = 0;
		for (int row = 0; row < 3; row++){
			for (int key = 0; key < KEYBOARD_PER_ROW[row]; key++){
				int x = KEYBOARD_MARGIN_X[row] + (key * (KEYBOARD_WIDTH + KEYBOARD_PADDING));
				int y = KEYBOARD_MARGIN_Y + (row * (KEYBOARD_HEIGHT + KEYBOARD_PADDING));
				KeyboardCell temp = new KeyboardCell(x, y, LETTERS.charAt(ct) + "");
				keyboard.put(LETTERS.charAt(ct), temp);
				ct++;
			}
		}
		//Enter
		KeyboardCell zKey = keyboard.get('Z'); 
		KeyboardCell enter = new KeyboardCell(
			(int)zKey.cell.getX() - (KEYBOARD_WIDTH_WIDE + KEYBOARD_PADDING), 
			(int)zKey.cell.getY(), ENTER_KEY_STRING, KEYBOARD_WIDTH_WIDE, KEYBOARD_HEIGHT);
		keyboard.put('\n', enter);
		
		//Backspace
		KeyboardCell mKey = keyboard.get('M'); 
		KeyboardCell backspace = new KeyboardCell(
			(int)mKey.cell.getX() + (KEYBOARD_WIDTH + KEYBOARD_PADDING), 
			(int)mKey.cell.getY(), BACKSPACE_KEY_STRING, KEYBOARD_WIDTH_WIDE, KEYBOARD_HEIGHT);
		keyboard.put(EMPTY, backspace);
		
	}
	// repain the window
	public void paintComponent(Graphics g) {

		drawCells(g);
		drawKeyboard(g);
		
		//If the game is over
		if (GameOver_check != -1)
			WordleText.drawGameOverText(g);
		if (WordleBrain.DEBUG_MODE)
			WordleText.drawDebugText(g);
		
		//repaint until the shaking finish
		if (rowShaking > -1)
			repaint();
	}      
	
	
	//Paints the cells to the window
	private void drawCells(Graphics g){
		for (int row = 0; row < cells.length; row++){      
			for (int col = 0; col <cells[0].length; col++){
				if (rowShaking == row)
					cells[row][col].x += SHAKE_INTERVAL[shakeCount];// if shaking case
				cells[row][col].paintComponent(g);
			}
			//we use shakecount and shakestep to count the shaking position
			// if shake steps > shake bound it should shake to the otherside
			if (rowShaking == row){
				shakeStep++;
				if (shakeStep >= SHAKE_BOUND[shakeCount]){
					shakeStep = 0;
					shakeCount++;
					if (shakeCount >= SHAKE_BOUND.length){
						shakeCount = 0;
						rowShaking = -1;
					}
				}
			}
		}    
	}
	
	
	//Paints the keyboard interface
	private void drawKeyboard(Graphics g){
		//ues hashmap.value to get every value and paint it
		for (KeyboardCell key : keyboard.values()){
			key.paintComponent(g);            
		}
	}    
	
	private void keyProcess(int code) {

		if (code == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} 
		else if (GameOver_check != -1)
			return;
		//deal with the enter/backspace keys
		else if (code == KeyEvent.VK_ENTER) {
			WordleBrain.checkLetters();
		}
		else if (code == KeyEvent.VK_BACK_SPACE) {
			WordleBrain.delete();
		}
		else if (Character.isLetter(code)) {
			String keyText = KeyEvent.getKeyText(code);
			String special_bug = "引号";
			if(keyText != special_bug ) {
				WordleBrain.input(keyText.toUpperCase().charAt(0));
			}
			System.out.print(keyText);
		}
		// any other key
		repaint();
	}
	// every time press the key it will be called
	public void keyPressed(KeyEvent event) {  
		keyProcess(event.getKeyCode());
	}

	static class LetterCell extends JPanel{
		// ****** font values ********
		private static final int LETTER_FONT_SIZE = 30;
		private static final Font LETTER_FONT = new Font("Arial", Font.BOLD, LETTER_FONT_SIZE); 
		// ****** color values ******
		private Color background;
		private Color line;
		private char letter;
		//  ***** location *******8
		private int x, y;
		
		
		private LetterCell(int x, int y){
			background = Color.BLACK;
			line = Color.GRAY;
			this.x = x;
			this.y = y; 
			this.letter = EMPTY;
		}
		// Change the cell's color
		private void setColor(Color newColor){
			background = newColor;
			line = newColor;
		}
		
		// Sets the letter
		private void setLetter(char letter){
			this.letter = letter;
			if(WordleBrain.DEBUG_MODE) {
				System.out.println("setLabel " + letter);
			}
		}
		
		//Draws this cell 
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			// Use the Rectangle2D to draw the cell and the keyboard use the exactly the same
			RectangularShape cell = new Rectangle2D.Double(this.x, this.y, 
				                                             CELL_SIZE, CELL_SIZE);
			//draw the key
			g2.setPaint(background);
			g2.fill(cell);
			//draw the letter
			g2.setStroke(new BasicStroke(BORDER_THICKNESS));
			g2.setPaint(line);
			g2.draw(cell); 
			
			//if the cell has a letter in it, draw the letter
			if (letter != EMPTY){
				g2.setFont(LETTER_FONT);
				g2.setColor(Color.WHITE);  
				Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(letter+"", g);
				int fontX =  this.x + (CELL_SIZE / 2) - ((int)textBounds.getWidth() / 2);
				int fontY =  this.y + (CELL_SIZE / 2) + ((int)textBounds.getHeight() / 3);
        g2.drawString(letter+"", fontX, fontY);

			}
		}          
		
	}
	
	static class KeyboardCell extends JPanel{
		// ****** font values ********
		private static final int KEYBOARD_FONT_SIZE = 13;
		private static final Font KEYBOARD_FONT = new Font("Arial", Font.BOLD, KEYBOARD_FONT_SIZE); 
		// ****** color values ******
		private Color background;
		
		private String letter;
		private RectangularShape cell;
		
		//If key is using standard dimensions (ie, the non-enter/backspace keys)
		private KeyboardCell(int x, int y, String letter){
			this(x, y, letter, KEYBOARD_WIDTH, KEYBOARD_HEIGHT);
		}
		
		private KeyboardCell(int x, int y, String letter, int width, int height){
			background = Color.black;
			this.letter = letter;
			cell = new RoundRectangle2D.Double(x, y, width, height, 5,5 );
		}
		
		//returns the current color of this key
		private Color getColor(){
			return background;
		}
		
		//Changes the color of this key 
		private void setColor(Color newColor){
			background = newColor;
		}      
		
		//Checks to see if the argument coordinate is located inside this
		public boolean contains(int x, int y){
			return cell.contains(x, y);      
		}
		
		//Draws this key to the game window
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			//first draw the key
			g2.setPaint(background);
			g2.fill(cell);
			//then draw the letter
			g2.setFont(KEYBOARD_FONT);
			g2.setColor(Color.white);  
			Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(letter, g);
			double fontX =  cell.getX() + (cell.getWidth() / 2.0) - (textBounds.getWidth() / 2.0);
			double fontY =  cell.getY() + (cell.getHeight() / 2.0) + (textBounds.getHeight() / 3.0);
			g2.drawString(letter, (int)fontX, (int)fontY);
		}
	}
	// key listen
	public void keyReleased(KeyEvent event) { }
	
	public void keyTyped(KeyEvent event) {}    
}
