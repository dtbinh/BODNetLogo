package posh.lapParser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

import utils.logging.Log;
import utils.tuples.Tuple;


/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


/*
 *
 * KEY Differences (compared to C#): 
 * 	Created custom TUPLE class
 *  Used Pattern instead of Regex, different way of creating, ?accessing?
 *  Used Map instead of Dictionary, different creatin, use .get() instead of []
 *  
 */


/**
 * A Lexer for tokenising .lap files.
 * This lexer is used by LAPParser to tokenise the input string.
 * 
 */
public class LapLexer {
	
	Log log;
	
	/**
	 * Preprocessing pattern, Everything that they match is
	 * substituted by the second string in the pair
	 */
	public static Tuple<Pattern,String> SUBPATTERN = new Tuple<Pattern,String>(Pattern.compile("(#|;)[^\n]*"),"");
	
	/**
	 * tokens that match fully, independent of what follows after
	 * them. These are tokens that don't need to be separated by
	 * separating characters. This doesn't work for reserved words,
	 * as they would match even if they only match the beginning
	 * of a word.
	 */
	public static Tuple<Pattern, String> FULLTOKENS = new Tuple<Pattern, String>(Pattern.compile("\"[^\"]*\""), "COMMENT");
	
	/**	 
     * separating characters are characters that split the input
     * string into tokens. These will be ignored, if they are not
     * in char_tokens.
	 */
    public static char[] SEPARATINGCHARS =  new char[] {' ','(',')','\n','\r','\t'}; 

    
    /**
     * character tokens are tokens that are represented by single
     * characters. This has to be a subset of separating_chars. 
     */

    public static Map<Character, String> CHARTOKENS =  new HashMap<Character, String>();
    
    
    /**
     * these tokens need to be separated by separating characters
	 * and need to match the strings in between fully. The tokens are
	 * given in their order of priority. Hence, if several of those
	 * tokens match, the first in the list is returned.
	 */

     public static Map<Pattern,String> TOKENS =  new HashMap<Pattern,String>(); 
     
     /**
      * NOTE: Using tokensarray as hashmap does not preserve order
      */
     public static String[][] TOKENSARRAY = new String[22][2];

     public static char newline = '\n';
     
     public static Pattern newlines = Pattern.compile("\n");
     
     private String input;
     private int lineNo;
    
    /**
     * Constructor, need to add values to hashmaps
     * @param inputString
     */
    public LapLexer(String inputString)	{
    	log = new Log("LAPLEXER");
    	 
    	CHARTOKENS.put('(', "LPAREN");
    	CHARTOKENS.put(')', "RPAREN");
    	
    	TOKENSARRAY[0][0] = "AP";
    	TOKENSARRAY[0][1] = "AP";  
        TOKENSARRAY[1][0] = "C";
        TOKENSARRAY[1][1] = "C";
        TOKENSARRAY[2][0] = "DC";
        TOKENSARRAY[2][1] = "DC";
        TOKENSARRAY[3][0] = "RDC";
        TOKENSARRAY[3][1] = "RDC";
        TOKENSARRAY[4][0] = "SDC";
        TOKENSARRAY[4][1] = "SDC";
        TOKENSARRAY[5][0] = "SRDC";
        TOKENSARRAY[5][1] = "SRDC";
        TOKENSARRAY[6][0] = "nil";
        TOKENSARRAY[6][1] = "NIL";
        TOKENSARRAY[7][0] = "(?i)drives";
        TOKENSARRAY[7][1] = "DRIVES";
        TOKENSARRAY[8][0] = "(?i)elements";
        TOKENSARRAY[8][1] = "ELEMENTS";
        TOKENSARRAY[9][0] = "(?i)trigger";
        TOKENSARRAY[9][1] = "TRIGGER";
        TOKENSARRAY[10][0] = "(?i)goal";
        TOKENSARRAY[10][1] = "GOAL";
        TOKENSARRAY[11][0] = "(?i)ticks";
        TOKENSARRAY[11][1] = "TICKS";
        TOKENSARRAY[12][0] = "(?i)minutes";
        TOKENSARRAY[12][1] = "MINUTES";
        TOKENSARRAY[13][0] = "(?i)seconds";
        TOKENSARRAY[13][1] = "SECONDS";
        TOKENSARRAY[14][0] = "(?i)hz";
        TOKENSARRAY[14][1] = "HZ";
        TOKENSARRAY[15][0] = "(?i)pm";
        TOKENSARRAY[15][1] = "PM";
        TOKENSARRAY[16][0] = "(?i)none";
        TOKENSARRAY[16][1] = "NONE";
        TOKENSARRAY[17][0] = "(==|=|!=|<|>|<=|>=)";
        TOKENSARRAY[17][1] = "PREDICATE";
        TOKENSARRAY[18][0] = "-?(\\d*\\.\\d+|\\d+\\.)([eE][\\+\\-]?\\d+)?";
        TOKENSARRAY[18][1] = "NUMFLOAT";
        TOKENSARRAY[19][0] = "\\-?[0-9]+";
        TOKENSARRAY[19][1] = "NUMINT";
        TOKENSARRAY[20][0] = "(?i)[a-z][a-z0-9_\\-]*";
        TOKENSARRAY[20][1] = "NAME";
        TOKENSARRAY[21][0] = "(?i)'?[a-z][a-z0-9_\\-]*";
        TOKENSARRAY[21][1] = "STRINGVALUE";
        
        this.input = "";
        if (inputString.length()>1){
        	setInput(inputString);
        }
    }


    public void setInput(String inputString)	{
    	inputString = SUBPATTERN.First.matcher(inputString).replaceAll(SUBPATTERN.Second);
    	input = inputString;
    	lineNo=1;
    }


    private Token checkFullTokens()	{
    	Matcher match = FULLTOKENS.First.matcher(input);
    	if (match.matches())	{
    		String matchedString = match.group();
    		input = input.substring(matchedString.length());
    		// count the number of newlines in the matched
    		//string ot keep track of the line number
    		lineNo+=newlines.matcher(matchedString).groupCount();
    		return new Token(FULLTOKENS.Second, matchedString);
    	}
    	return null;
    }
    
    private Token checkNormalTokens()	{
    	//none of the separating characters matched
    	//let's split the string and check for normal tokens
    	int sepPos = -1;
    	//find the closest separating character
    	for (int i = 0; i < SEPARATINGCHARS.length; i++)	{
    		char sepChar = SEPARATINGCHARS[i];
    		int pos = input.indexOf(sepChar);
    		if (pos>=0 && (sepPos == -1 || pos < sepPos))	{
    			sepPos = pos;
    		}
    	}
    	
    	//take the full string if no separating character was found
    	String sepString;
    	if (sepPos == -1)	{
    		sepString = input;
    	}	else	{
    		sepString = input.substring(0, sepPos);
    		//find the first fully matching token    
    		
    		String pattern = "";
    		String value = "";
    		for (int i = 0; i < TOKENSARRAY.length; i ++)	{
    			pattern = TOKENSARRAY[i][0];
    			value = TOKENSARRAY[i][1];
    			Pattern patternComp = Pattern.compile(pattern);
    			Matcher match = patternComp.matcher(sepString);
    			if (match.matches()  )	{
    				if (match.group().length() == sepString.length())	{
    					String matchedString = match.group();
    					input = input.substring(matchedString.length());
    					//	count the number of newlines in the matched
    					//string to keep track of the line number
    					lineNo += newlines.matcher(matchedString).groupCount();
    					return new Token(value, matchedString);
    				}
    			}
    		}
    	}
    	return null;    	    	
    }
    
    public Token token()	{
    	while (input.length() > 0)	{
    		Token result = checkFullTokens();
    		if (result !=  null)	{
    			return result;
    		}
    		//none of the tokens matches
    		//proceed with checking for single characters
    		char singleChar = input.charAt(0);
    		if (arrayContains(SEPARATINGCHARS, singleChar))	{
    			input = input.substring(1);
    			if (singleChar == newline)	{
    				lineNo++;
    			}
    			if (CHARTOKENS.containsKey(singleChar))	{
    				return new Token(CHARTOKENS.get(singleChar), singleChar+"");
    			}
    			continue;
    		}
    		
    		result = checkNormalTokens();
    		if (result != null)	{
    			return result;
    		}
    		//no token matched, give error over single character
    		//char charString = input.charAt(0);
    		input = input.substring(1);
    		log.error("NO TOKEN MATCHED " + input.charAt(0));
    	}
    	//the input string is empty
    	return null;
    }
    
    public int getLineNumber()	{
    	return lineNo;
    }
    
    
    
    private boolean arrayContains(char[] array, char ch)	{
    	boolean result = false;
    	
    	for (int i = 0 ; i < array.length; i++)	{
    		if (array[i]==ch)	{
    			result = true;
    			break;
    		}
    	}
    	
    	return result;
    }

}
