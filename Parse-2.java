/*Title: Parser
 *Student name: Christian Wojteczko
 *Class: CSCI 4200 – Section A1
 *Professor: Dr. Abi Salimi 
 */
import java.io.*;
import java.util.Scanner;

public class Parse {

static String charClass;
static String lexeme;
static char nextChar;
static int lexLength = 0;
static String nextToken;
static Scanner charScan;
static PrintStream outFile = null;

	//Main Method
	public static void main(String[] args) {
		// Creates initial Scanner and PrintStream for input and output respectively
		Scanner inScan = null;
		
		// Tries to find the input file and errors if not found
		try {
			inScan = new Scanner(new File("sourceProgram.txt"));
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			System.exit(0);
		}
		// Tries to output to the output file and errors if it cannot be made.
		// Creates the header.
		try {
			outFile = new PrintStream("parseOut.txt");
			outFile.println("Matthew Messer, CSCI4200, Summer 2024, Parser");
			outFile.println("********************************************************************************");
		} catch (FileNotFoundException e) {
			System.out.println("File Cannot Be Made");
		}
		// Allows the program to parse line by line
		while (inScan.hasNextLine()) {
			String input;
			input = inScan.nextLine();
			outFile.println("Parsing " + input);
			outFile.println("********************************************************************************");
			// creates the line scanner to allow you to step through the char's
			charScan = new Scanner(input);
			charScan.useDelimiter("");

			// Starts the line analysis for parser
			assign();
			//blank line after parsing each statement
			outFile.println("");
			
		}
		// End of file final output and closing of scanners
		outFile.println("********************************************************************************");
		outFile.printf("Next token is: END_OF_FILE, Next lexeme is EOF\n");

		inScan.close();
		outFile.close();
	}

	//Beginning of lexical used methods
	//Method to lookup the operators or parenthesis and return the token
	static String lookup(char checkedChar) {
		switch (checkedChar) {
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
			//adds the semi colon grammer
		case ';':
			addChar();
			nextToken="SEMI_COLON";

		default:
			addChar();
			nextToken = "END_OF_FILE";
			break;
		}// End of switch statement
		return nextToken;
	}

	//Adds nextChar to lexeme
	static void addChar() {
		if (lexLength < 98) {
			lexeme += nextChar;
			lexLength++;
		} else
			System.out.println("Error - Lexeme Is Too Long");
	}

	//Gets the next char input and determines its class
	static void getChar() {
		if (charScan.hasNext()) {
			nextChar = charScan.next().charAt(0);
			if (Character.isLetter(nextChar))
				charClass = "LETTER";
			else if (Character.isDigit(nextChar))
				charClass = "DIGIT";
			else
				charClass = "UNKNOWN";
		} else
			charClass = "EOF";
	}

	//Skips spaces by calling getChar until the next non-space
	static void getNonBlank() {
		while (nextChar == ' ')
			getChar();
	}

	//Lexical analyzer
	static String lex() {

		lexLength = 0;
		lexeme = "";
		getNonBlank();
		switch (charClass) {
		// Parses Identifiers
			case "LETTER":
				addChar();
				getChar();
				while (charClass == "LETTER" || charClass == "DIGIT") {
					addChar();
					getChar();
				}
				nextToken = "IDENT";
				break;

		// Parses Integer Literals
			case "DIGIT":
				addChar();
				getChar();
				while (charClass == "DIGIT") {
					addChar();
					getChar();
				}
				nextToken = "INT_LIT";
				break;

		// Parses Operators and Parenthesis
			case "UNKNOWN":
				lookup(nextChar);
				getChar();
				break;

		// END OF FILE
			case "EOF":
				nextToken = "END_OF_FILE";
				lexeme = "EOF";
				break;

		}// End of the switch statement
		
		//Prints to the output after lex finishes
		if (nextToken != "END_OF_FILE") {
			outFile.printf("Next token is: %s, Next lexeme is %s\n", nextToken, lexeme);
		}
		return nextToken;
	}
	
	//Start of Parser Methods
	//<assign> Grammar
    static void prog() {
        outFile.println("Enter <prog>");
        lex();
        if (nextToken.equals("PROG_BEGIN")) {
            lex();
            assign();
            while (nextToken.equals("SEMICOLON")) {
                lex();
                assign();
            }
            if (nextToken.equals("PROG_END")) {
                lex();
            } else {
                outFile.println("ERROR - Expected 'end'");
            }
        }
        else {
            outFile.println("ERROR - Expected 'program'");
        }
        outFile.println("Exit <prog>");}
	static void assign() {
		getChar();
		lex();
		outFile.println("Enter <assign>");
		//Ensures first token is an IDENT otherwise error
		if (nextToken == "IDENT") {
			lex();
			//Ensures second token was an ASSIGN_OP otherwise error is given
			if (nextToken == "ASSIGN_OP") {
				lex();
				expr();
			}
			else {
				outFile.println("ERROR - Second was not an ASSIGN_OP");
			}
		}
		else
		{
			outFile.println("ERROR - Did not start with an IDENT");
		}
		outFile.println("Exit <assign");
	}
	
	//<expr> Grammar structure
	static void expr() {
		outFile.println("Enter <expr>");
		term();
		//Deals with ADD_OP and SUB_OP's found
		while(nextToken == "ADD_OP" || nextToken == "SUB_OP") {
			lex();
			term();
		}
		if(nextToken == "SEMICOLON") {
			lex();
		term();}
		outFile.println("Exit <expr>");
	}
	
	//<term> Grammar structure
	static void term() {
		outFile.println("Enter <term>");
		
		factor();
		//Deals with MULT_OP and DIV_OP's found
		while(nextToken == "MULT_OP" || nextToken == "DIV_OP") {
			lex();
			factor();
		}
		if(nextToken == "SEMICOLON") {
			lex();
		term();}
		outFile.println("Exit <term>");
	}
	
	//<factor> grammar structure
	static void factor() {
		outFile.println("Enter <factor>");
		
		if (nextToken == "IDENT" || nextToken == "INT_LIT") {
			lex();
		}
		else {
			//Deals with parentheses if it's not one of the other tokens checked, otherwise error
			if (nextToken == "LEFT_PAREN") {
				lex();
				expr();
				//Makes sure a RIGHT_PAREN is found matching the left otherwise error
				if (nextToken == "RIGHT_PAREN") {
					lex();
				}
				else {
					outFile.println("ERROR - RIGHT_PAREN not found");
				}
			}
			else {
				outFile.println("ERROR - Does not follow the parser grammar");
			}
		}
		if(nextToken == "SEMICOLON") {
			lex();
		term();}
		
		outFile.println("Exit <factor>");
	}

}