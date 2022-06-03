import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class WordleBrain {

  public static boolean DEBUG_MODE;
  //************ Initial Values  **********

  private static final int WORDSNUM = 12972; 

  private static final Random rand = new Random();

  public static final int ATTEMPTS = 6;
  public static final int LENGTH = 5; 
  private static final char EMPTY = '\0'; 

  // ************ Color Values ************

  private static final Color CORRECT_COLOR = new Color(53, 209, 42);// green
  private static final Color WRONGPLACE_COLOR = new Color(235, 216, 52);//yellow
  private static final Color WRONG_COLOR = Color.DARK_GRAY;//gray

  // ************ Class variables ************
  
  private static int row = 0;
  private static int col = 0;
  private static String secret;
  private static char[] input = new char[LENGTH];
  private static String[] wordList = new String[WORDSNUM - 1];

  // initialize the program
  public static String init() throws FileNotFoundException {
    File file = new File("5LetterWords.txt");
    Scanner scan = new Scanner(file);
    int randomnum = rand.nextInt(WORDSNUM);
    for (int k = 0; k < WORDSNUM - 1; k++) {
      wordList[k] = scan.next().toUpperCase();
    }
    secret = wordList[randomnum];
    scan.close();
    input = new char[LENGTH];
    row = 0; col = 0;
    return secret;
  }

  // Check the word if valid or not
  public static boolean isWord(String in) {
    for (int i = 0; i < wordList.length; i++) {
      if (in.equals(wordList[i].toUpperCase())) {
        return true;
      }
    }
    return false;
  }

  // Check the valid letters to the answer
  public static void checkLetters() {
    String scbase = secret;
    char[] temp = input.clone();
    String inptStr = new String(input);
    char[] answer = secret.toCharArray();
    int counter = 0;
    //Debug
   // for(int i = 0; i < 5; i++)System.out.print(input[i]);
    // Not valid case
    if (col < 5 || isWord(inptStr) == false) {	 
      WordleGUI.shakeRow(row);
    }
    // Valid case
    if (isWord(inptStr)) {
      for (int k = 0; k < 5; k++) { 
        WordleGUI.setCellColor(row, k, WRONG_COLOR);
        upd_keycolor(input[k], 2);
      }
      for (int i = 0; i < 5; i++) { 
        if (input[i] == answer[i]) {
          WordleGUI.setCellColor(row, i, CORRECT_COLOR);
          upd_keycolor(input[i], 0);
          temp[i] = EMPTY; 
          counter++;
          if (counter == 5) {
            WordleGUI.GameOver_check = 0;
          }
          dupeCheck(input[i], i);//delete the character from the input
        }
      }
      for (int j = 0; j < temp.length; j++) {
        // use indexOf() to convert to string
        if (secret.indexOf(Character.toString(temp[j])) != -1) {
          WordleGUI.setCellColor(row, j, WRONGPLACE_COLOR);
          upd_keycolor(temp[j], 1);
          dupeCheck(temp[j], j);
        }
      }
      row += 1;
      col = 0;
    }
    if (row == ATTEMPTS && counter != 5) {
    	WordleGUI.GameOver_check = 1;
    }
    secret = scbase;
    if (isWord(inptStr))
    input = new char[LENGTH];
  }

  public static void upd_keycolor(char key, int color) {
	// update the keyboard's letter
    if (color == 0) { // correct
      WordleGUI.setKeyboardColor(key, CORRECT_COLOR);
    }
    if (color == 1 && WordleGUI.getKeyboardColor(key) != CORRECT_COLOR) { 
    	// wrong place
      WordleGUI.setKeyboardColor(key, WRONGPLACE_COLOR);
    }
    if (color == 2) { // wrong
      WordleGUI.setKeyboardColor(key, WRONG_COLOR);
    }
  }

  public static void dupeCheck(char key, int i) {
	// make the checked letter an empty letter
    if (key == secret.charAt(i)) {
      char[] temp = secret.toCharArray();
      temp[i] = ' ';
      secret = String.valueOf(temp);
    } else {
      char[] temp = secret.toCharArray();
      temp[secret.indexOf(String.valueOf(key))] = ' ';
      secret = String.valueOf(temp);
    }
  }
  public static void input(char key) {
	// input the new letter
    if (col < 5) {
      WordleGUI.setCellLetter(row, col, key);
      input[col] = key;
      col += 1;
    } 
    if(DEBUG_MODE) {
		System.out.println("Letter pressed!: " + key);
		for(int i = 0; i < 5; i++)System.out.print(input[i]);
	} 
  }
  public static void delete() {
	//delete the wrong letter
	if (col > 0) {
		WordleGUI.setCellLetter(row, col - 1, EMPTY);
		input[col - 1] = EMPTY;
		col--;
	}
  }
}