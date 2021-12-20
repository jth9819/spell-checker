package uwf.harrisonj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application {
	private boolean fileOpened = false; // If a file has been opened
	private File file = null; // The opened file
	private String textAreaString = ""; // A built string from text area's words
	
	private TextArea textArea = new TextArea(); // Gets the text area
	private FileChooser fileChooser = new FileChooser(); // Chooses a file
	
	// Function to start
	@Override
	public void start(Stage stage) {
		// Create MenuBar
		MenuBar menuBar = new MenuBar();

		// Create menus
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");

		// Create MenuItems (Built from user defined functions)
		MenuItem openFileItem = createOpenFileItem();
		MenuItem saveItem = createSaveItem();
		MenuItem exitItem = createExitItem();
		MenuItem spellCheckItem = createSpellCheckItem("dictionary.txt");

		// Add menuItems to the Menus
		fileMenu.getItems().addAll(openFileItem, saveItem, exitItem);
		editMenu.getItems().addAll(spellCheckItem);

		// Add Menus to the MenuBar
		menuBar.getMenus().addAll(fileMenu, editMenu);

		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(textArea);
		textArea.setWrapText(true);
		Scene scene = new Scene(root, 400, 250);

		stage.setTitle("JavaFX Speller");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	// Function to open file
	private MenuItem createOpenFileItem() {
		MenuItem openFileItem = new MenuItem("Open File");
		openFileItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		openFileItem.setOnAction(new EventHandler<ActionEvent>() {
			
		@Override
		public void handle(ActionEvent event) {
			try {
				ArrayList<String> wordsFromFile = new ArrayList<>(); // Separated words read from file
				String line;
				String[] values;
				fileChooser.setInitialDirectory(new File(".")); // Set fileChooser to current directory
				file = fileChooser.showOpenDialog(null); // Shows a new open file dialog
				BufferedReader br = null;
				
				textAreaString = "";
				if(!(file == null)) {
					br = new BufferedReader(new FileReader(file)); // Buffered reader for file
					
					while ((line = br.readLine()) != null) {
						values = line.split(" ");
						for (int i = 0; i < values.length; i++) {
							wordsFromFile.add(values[i]);
						}
					}
					br.close();
					for (int f = 0; f < wordsFromFile.size(); f++) {
						textAreaString = textAreaString + wordsFromFile.get(f) + " "; // Create string from parsed words from file
					}
					textArea.clear();
					textArea.setText(textAreaString); // Set text area to content from opened file
					fileOpened = true;
					textAreaString = "";
				}				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		});
		return openFileItem;
	}

	// Function to save file
	private MenuItem createSaveItem() {
		MenuItem saveItem = new MenuItem("Save File");
		saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		saveItem.setOnAction(new EventHandler<ActionEvent>() {
			
		@Override
		public void handle(ActionEvent event) {
			try {
				String fileName = ""; // Stores name of file
				fileChooser.setInitialDirectory(new File(".")); // set to current directory

				// If file has been opened -> save to that file
				if (fileOpened == true) {
					fileName = file.getName();
					PrintWriter writer = new PrintWriter(fileName, "UTF-8");
					writer.println(textArea.getText());
					writer.close();
				// If no file has been opened -> Create new file and save
				} else if (fileOpened == false) {
					file = fileChooser.showSaveDialog(null);
					if(!(file == null)) {
						PrintWriter writer = new PrintWriter(file, "UTF-8");
						fileName = file.getName();
						writer.println(textArea.getText());
						writer.close();
						fileOpened = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		});
		return saveItem;
	}

	// Function to exit program
	private MenuItem createExitItem() {
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
			
		@Override
		public void handle(ActionEvent event) {
			System.exit(0);
		}
		});
		return exitItem;
	}

	// Function to spell check
	private MenuItem createSpellCheckItem(String dictionaryFile) {
		MenuItem spellCheckItem = new MenuItem("Spell Check");
		spellCheckItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
		spellCheckItem.setOnAction(new EventHandler<ActionEvent>() {
			
		@Override
		public void handle(ActionEvent event) {
			try {
				Set<String> dictionaryHashSet = new HashSet<String>(); // HashSet to store dictionary.txt
				boolean containsInccorrectWord = false; // Boolean to specify if string contains misspelled words
				textAreaString = textArea.getText(); // Create string from words in TextArea
				String[] arrayOfWords = textAreaString.split("\\s+|,\\s*|\\.\\s*"); // Splits string (Spaces, commas, and periods) into an array
				
				hashDictionary(dictionaryFile, dictionaryHashSet); // Call hashDictionary function
				
				// Loop over arrayOfWords
				for (int i = 0; i < arrayOfWords.length; i++) {
					
					// If text area is empty -> display message
					if (textAreaString.compareTo("") == 0) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText("No words entered.");
						alert.showAndWait();
						break;

					// If text area contains misspelled words -> fix and suggest
					} else if (!(dictionaryHashSet.contains(arrayOfWords[i].toLowerCase()))) {
						char[] wordCharArr = arrayOfWords[i].toCharArray(); // Convert word to char array
						String[] alphabetArr = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" }; // For the add letter algorithm
						String[] alphabetArrWithoutAI = { "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" }; // For removing single characters from suggested words (Because  "A" & "I" are words)
						ArrayList<String> matchedWords = new ArrayList<String>(); // ArrayList to store matched words after comparing to dictionary.txt
						ArrayList<String> wordsToCompare = new ArrayList<String>(); // ArrayList to store words to compare (To dictionary.txt) after applying all 3 algorithms
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Mispelled Word");
						alert.setHeaderText(arrayOfWords[i]);

						addLetterAlgorithm(arrayOfWords[i] , alphabetArr, wordsToCompare, wordCharArr); // Call addLetterAlgorithm()					
						removeLetterAlgorithm(arrayOfWords[i], wordsToCompare); // Call removeLetterAlgorithm()
						swapLettersAlgorithm(arrayOfWords[i], wordsToCompare); // Call swapLettersAlgorithm()
						addMatchedWords(matchedWords, wordsToCompare, dictionaryHashSet); // Call addMatchedWords()
						handleDuplicateWords(matchedWords); // Call handleDuplicateWords()
						removeSingleCharacters(matchedWords, alphabetArrWithoutAI); // Call removeSingleCharacters()
						suggestedWords(matchedWords, alert); // Call suggestedWords
						
						wordsToCompare.clear();
						matchedWords.clear();
						containsInccorrectWord = true;
						
					// If text area contains only correctly spelled words -> display message
					} else if ((dictionaryHashSet.contains(arrayOfWords[i].toLowerCase()) && (i == arrayOfWords.length - 1) && (containsInccorrectWord == false))) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText("All words spelled correctly.");
						alert.showAndWait();
					}
				}
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		});
		return spellCheckItem;
	}
	
	// Function to store dictionary.txt -> dictionaryHashSet
	private void hashDictionary(String dictionaryFileParam, Set<String> dictionaryHashSet) {
		try {
			Scanner textFile = new Scanner(new File(dictionaryFileParam)); // Scanner to scan dictionary.txt

			while (textFile.hasNext()) {
				dictionaryHashSet.add(textFile.next().trim());
			}
			textFile.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Function to add a letter
	private void addLetterAlgorithm(String arrayOfWordsParam , String[] alphabetArrParam,  ArrayList<String> wordsToCompareParam, char[] wordCharArrParam) {
		for (int j = 0; j < arrayOfWordsParam.length() + 1; j++) {
			for (int k = 0; k < alphabetArrParam.length; k++) {
				wordsToCompareParam.add(arrayOfWordsParam.toLowerCase().substring(0, j) + alphabetArrParam[k].toLowerCase() + arrayOfWordsParam.toLowerCase().substring(j, wordCharArrParam.length));
			}
		}
	}
	
	// Function to remove a letter
	private void removeLetterAlgorithm(String arrayOfWordsParam, ArrayList<String> wordsToCompareParam) {
		for (int j = 0; j < arrayOfWordsParam.length(); j++) {
			String str = arrayOfWordsParam.toLowerCase().substring(0, j) + arrayOfWordsParam.toLowerCase().substring(j + 1);
			wordsToCompareParam.add(str);
		}
	}
	
	// Function to swap letters
	private void swapLettersAlgorithm(String arrayOfWordsParam, ArrayList<String> wordsToCompareParam) {
		for (int j = 0; j < arrayOfWordsParam.length() - 1; j++) {
			char[] c = arrayOfWordsParam.toLowerCase().toCharArray();
			char temp = c[j];
			c[j] = c[j + 1];
			c[j + 1] = temp;
			String swappedString = new String(c);
			wordsToCompareParam.add(swappedString);
		}
	}
	
	// Function to add dictionary matched words to matchWords
	private void addMatchedWords(ArrayList<String> matchedWordsParam, ArrayList<String> wordsToCompareParam, Set<String> dictionaryHashSet) {
		for (int j = 0; j < wordsToCompareParam.size(); j++) {
			if (dictionaryHashSet.contains(wordsToCompareParam.get(j))) {
				matchedWordsParam.add(wordsToCompareParam.get(j));
			}
		}
	}
	
	// Function to check and handle duplicate words in matchedWords
	private void handleDuplicateWords(ArrayList<String> matchedWordsParam) {
		for (int j = 0; j < matchedWordsParam.size(); j++) {
			if (matchedWordsParam.get(j).equals(matchedWordsParam.get(j))) {
				Set<String> set = new HashSet<>(matchedWordsParam);
				matchedWordsParam.clear();
				matchedWordsParam.addAll(set);
				break;
			}
		}
	}
	
	// Function to remove single characters from matchedWords
	private void removeSingleCharacters(ArrayList<String> matchedWordsParam, String[] alphabetArrWithoutAIParam) {
		for (int j = 0; j < matchedWordsParam.size(); j++) {
			if (matchedWordsParam.get(j).length() == 1) {
				for (int k = 0; k < alphabetArrWithoutAIParam.length; k++) {
					matchedWordsParam.remove(alphabetArrWithoutAIParam[k].toLowerCase());
				}
			}
		}
	}
	
	// Function to/to-not provide suggested words
	private void suggestedWords(ArrayList<String> matchedWordsParam, Alert alertParam) {
		// Display suggested words if matchedWords isn't empty
		if (!(matchedWordsParam.size() == 0)) {
			alertParam.setContentText("Suggested Words:\n"
					+ matchedWordsParam.toString().replace("[", "").replace("]", ""));
			alertParam.showAndWait();
		// Display no suggested words if matchedWords is empty
		} else {
			alertParam.setContentText("No Suggested Words.");
			alertParam.showAndWait();
		}
	}

	// Main
	public static void main(String[] args) {
		Application.launch(args);
	}

}