/*Title: Lexical Analyzer-Java
 *Student name: Matthew Messer
 *Class: CSCI 4200 – Section OL1
 *Professor: Dr. Abi Salimi 
 */

import java.io.*;
import java.util.Scanner;
public class Lex {

//initiate the global class variables
static String charClass;
static String lexeme;
static char nextChar;
static int lexLength = 0;
static String nextToken;
static Scanner charScan;
	

	//Main Method
	public static void main(String[] args) {
		//Creates initial Scanner and PrintStream for input and output respectively
		Scanner inScan = null;
		PrintStream outFile = null;
		//Tries to find the input file and errors if not found
		try {
			inScan = new Scanner(new File("lexInput.txt"));
		} catch (FileNotFoundException e){ 
			System.out.println("File Not Found");
		}
		//Tries to output to the output file and errors if it cannot be made.
		//Creates the header.
		try {
			outFile = new PrintStream("lexOutput.txt");
			outFile.println("Matthew Messer, CSCI4200, Summer 2024, Lexical Analyzer");
			outFile.println("********************************************************************************");
		} catch (FileNotFoundException e) {
			System.out.println("File Cannot Be Made");
		}
		//Allows the program to parse line by line
		while(inScan.hasNextLine()) {
			String input;
			input = inScan.nextLine();
			outFile.println(input);
			//creates the line scanner to allow you to step through the char's
			charScan = new Scanner(input);
			charScan.useDelimiter("");
			
			//Starts the line analysis
			getChar();
			do {
				lex();
				//Ensures end of line doesn't also trigger end of file
				if(nextToken != "END_OF_FILE")
					outFile.printf("Next token is: %s, Next lexeme is %s\n\n", nextToken, lexeme);
				
			}while(nextToken != "END_OF_FILE");
		}
		//End of file final output and closing of scanners
		outFile.println("********************************************************************************");
		outFile.printf("Next token is: %s, Next lexeme is %s\n", nextToken, lexeme);
		outFile.printf("Lexical analysis of the program is complete!");
		
		inScan.close();
		outFile.close();
	}
	
	//Method to lookup the operators or parenthesis and return the token
	static String lookup(char checkedChar) {
		switch(checkedChar) {

			case '(':
				addChar();
				nextToken = "LEFT_PAREN";
				break;
				
			case ')':
				addChar();
				nextToken = "RIGHT_PAREN";
				break;
				
			case '+':
				addChar();
				nextToken = "ADD_OP";
				break;
				
			case '-':
				addChar();
				nextToken = "SUB_OP";
				break;
				
			case '*':
				addChar();
				nextToken = "MULT_OP";
				break;
				
			case '/':
				addChar();
				nextToken = "DIV_OP";
				break;
				
			case '=':
				addChar();
				nextToken = "ASSIGN_OP";
				break;
			case ';':
				addChar();
				nextToken = "SEMICOLON";
				break;
			default: 
				addChar();
				nextToken = "END_OF_FILE";
				break;
		}//End of switch statement
		return nextToken;
	}
	
	//Adds nextChar to lexeme
	static void addChar() {
		if(lexLength<98) {
			lexeme+=nextChar;
			lexLength++;
		}
		else
			System.out.println("Error - Lexeme Is Too Long");
	}
	
	//Gets the next char input and determines its class
	static void getChar() {
		if(charScan.hasNext()) {
			nextChar = charScan.next().charAt(0);
			if(Character.isLetter(nextChar))
				charClass = "LETTER";
			else if(Character.isDigit(nextChar))
				charClass = "DIGIT";
			else
				charClass = "UNKNOWN";
		}
		else
			charClass = "EOF";
	}
	
	//Skips spaces by calling getChar until the next non-space
	static void getNonBlank() {
		while(nextChar == ' ')
			getChar();
	}
	
	//Lexical analyzer
	static String lex() {
		
		lexLength = 0;
		lexeme = "";
		getNonBlank();
		switch(charClass) {
			//Parses Identifiers
			case "LETTER":
				addChar();
				getChar();
				while(charClass == "LETTER" || charClass == "DIGIT") {
					addChar();
					getChar();
				}
				//this acknowledges the beginning.
				if(lexeme.equals("program")) {
					nextToken = "PROG_BEGIN";
				}
				//this acknowledges the ending.
				else if(lexeme.equals("end")) {nextToken = "PROG_END";}
				else {
				nextToken = "IDENT";}
				break;
				
			//Parses Integer Literals	
			case "DIGIT":
				addChar();
				getChar();
				while(charClass == "DIGIT") {
					addChar();
					getChar();
				}
				nextToken = "INT_LIT";
				break;
				
			//Parses Operators and Parenthesis	
			case "UNKNOWN":
				lookup(nextChar);
				getChar();
				break;
				
			//END OF FILE
			case "EOF":
				nextToken = "END_OF_FILE";
				lexeme = "EOF";
				break;
			
		}//End of the switch statement
		return nextToken;
	}
	
	
}
