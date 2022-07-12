package gb.tda.tools;

import cern.colt.list.IntArrayList;
import cern.colt.list.CharArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class CypherLetter {

    private static Logger logger  = Logger.getLogger(CypherLetter.class);
    private static int bufferSize = 256000;

    public static void main(String[] args) throws IOException {
		handleArgs(args);
		plainTextToCypherText();
    }

    private static String plainTextFilename;
    private static String cypherTextFilename;
    private static void handleArgs(String[] args) {
		if ( args.length != 2 ) {
		    logger.error("Usage: java CypherLetter intput_plain_text_file output_cypher_text_file");
		    System.exit(-1);
		}
		plainTextFilename = args[0];
		cypherTextFilename = args[1];
    }

    private static String[] plainWords; 
    private static void plainTextToCypherText() throws IOException, FileNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(plainTextFilename), bufferSize);
	  	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(cypherTextFilename), bufferSize));
		while ( true ) {
		    if ( ! in.ready() ) break;
		    String line = in.readLine();
		    try {
			StringTokenizer st = new StringTokenizer(line);
			while ( st.hasMoreTokens() ) {
			    String plainWord = st.nextToken().toLowerCase();
			    String cypherWord = applySubstitutionCypher(plainWord);		
			    out.print(cypherWord+"  ");
			}
			out.println();
		    }
		    catch ( NoSuchElementException e ) {
			//  End of line or blank line
			out.println();
		    }
		}
		out.close();
    }

    private static String applySubstitutionCypher(String plainWord) {
		char[] letters = plainWord.toCharArray();
		IntArrayList intList = new IntArrayList();
		for ( int i=0; i < letters.length; i++ ) {
		    int number = letterToNumber(letters[i]);
		    intList.add(number);
		}
		intList.trimToSize();
		int[] numbers = intList.elements();
		String cypherWord = "";
		for ( int i=0; i < numbers.length; i++ ) {
		    String num = (new Integer(numbers[i])).toString();
		    if ( i==0 ) cypherWord = num;
		    else cypherWord = cypherWord+"."+num;
		}
		return cypherWord;
    }

    private static String applyJuliusCesarCypher(String plainWord) {
		char[] letters = plainWord.toCharArray();
		CharArrayList charList = new CharArrayList();
		for ( int i=0; i < letters.length; i++ ) {
		    char shiftedLetter = juliusCesarCypher(letters[i]);
		    charList.add(shiftedLetter);
		}
		charList.trimToSize();
		char[] shiftedChars = charList.elements();
		return new String(shiftedChars);
    }

    private static char juliusCesarCypher(char letter) {
		if ( (new Character(letter)).charValue() == 'a' ) return 'd';
		else if ( (new Character(letter)).charValue() == 'b' ) return 'e';
		else if ( (new Character(letter)).charValue() == 'c' ) return 'f';
		else if ( (new Character(letter)).charValue() == 'd' ) return 'g';
		else if ( (new Character(letter)).charValue() == 'e' ) return 'h';
		else if ( (new Character(letter)).charValue() == 'f' ) return 'i';
		else if ( (new Character(letter)).charValue() == 'g' ) return 'j';
		else if ( (new Character(letter)).charValue() == 'h' ) return 'k';
		else if ( (new Character(letter)).charValue() == 'i' ) return 'l';
		else if ( (new Character(letter)).charValue() == 'j' ) return 'm';
		else if ( (new Character(letter)).charValue() == 'k' ) return 'n';
		else if ( (new Character(letter)).charValue() == 'l' ) return 'o';
		else if ( (new Character(letter)).charValue() == 'm' ) return 'p';
		else if ( (new Character(letter)).charValue() == 'n' ) return 'q';
		else if ( (new Character(letter)).charValue() == 'o' ) return 'r';
		else if ( (new Character(letter)).charValue() == 'p' ) return 's';
		else if ( (new Character(letter)).charValue() == 'q' ) return 't';
		else if ( (new Character(letter)).charValue() == 'r' ) return 'u';
		else if ( (new Character(letter)).charValue() == 's' ) return 'v';
		else if ( (new Character(letter)).charValue() == 't' ) return 'w';
		else if ( (new Character(letter)).charValue() == 'u' ) return 'x';
		else if ( (new Character(letter)).charValue() == 'v' ) return 'y';
		else if ( (new Character(letter)).charValue() == 'w' ) return 'z';
		else if ( (new Character(letter)).charValue() == 'x' ) return 'a';
		else if ( (new Character(letter)).charValue() == 'y' ) return 'b';
		else return 'c';
    }

    private static int letterToNumber(char letter) {
		if ( (new Character(letter)).charValue() == 'a' ) return 0;
		else if ( (new Character(letter)).charValue() == 'b' ) return 1;
		else if ( (new Character(letter)).charValue() == 'c' ) return 2;
		else if ( (new Character(letter)).charValue() == 'd' ) return 3;
		else if ( (new Character(letter)).charValue() == 'e' ) return 4;
		else if ( (new Character(letter)).charValue() == 'f' ) return 5;
		else if ( (new Character(letter)).charValue() == 'g' ) return 6;
		else if ( (new Character(letter)).charValue() == 'h' ) return 7;
		else if ( (new Character(letter)).charValue() == 'i' ) return 8;
		else if ( (new Character(letter)).charValue() == 'j' ) return 9;
		else if ( (new Character(letter)).charValue() == 'k' ) return 10;
		else if ( (new Character(letter)).charValue() == 'l' ) return 11;
		else if ( (new Character(letter)).charValue() == 'm' ) return 12;
		else if ( (new Character(letter)).charValue() == 'n' ) return 13;
		else if ( (new Character(letter)).charValue() == 'o' ) return 14;
		else if ( (new Character(letter)).charValue() == 'p' ) return 15;
		else if ( (new Character(letter)).charValue() == 'q' ) return 16;
		else if ( (new Character(letter)).charValue() == 'r' ) return 17;
		else if ( (new Character(letter)).charValue() == 's' ) return 18;
		else if ( (new Character(letter)).charValue() == 't' ) return 19;
		else if ( (new Character(letter)).charValue() == 'u' ) return 20;
		else if ( (new Character(letter)).charValue() == 'v' ) return 21;
		else if ( (new Character(letter)).charValue() == 'w' ) return 22;
		else if ( (new Character(letter)).charValue() == 'x' ) return 23;
		else if ( (new Character(letter)).charValue() == 'y' ) return 24;
		else return 25;
    }

}
