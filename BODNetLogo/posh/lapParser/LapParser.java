package posh.lapParser;


import java.util.ArrayList;
import java.util.List;



import posh.PlanBuilder;
import utils.logging.Log;
import utils.tuples.TupleFour;
import utils.tuples.TupleThree;


/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

	
    /*
     * Parser for .lap files.
     * The parser accepts the following grammar:
     * Preprocessing
     * -------------
     *   All element matching L{(\#|\;)[^\n]*} are removed. That removes
     *   all the comments staring with '#' or ';'.
     *
     * Terminal Symbols
     * ----------------
     *   The following terminal symbols are accepted::
     *
     *     AP                       AP
     *     C                        C
     *     DC                       DC
     *     RDC                      RDC
     *     SDC                      SDC
     *     SRDC                     SRDC
     *     nil                      NIL
     *     (?i)drives               DRIVES
     *     (?i)elements             ELEMENTS
     *     (?i)trigger              TRIGGER
     *     (?i)goal                 GOAL
     *     (?i)hours                TICKS
     *     (?i)minutes              MINUTES
     *     (?i)seconds              SECONDS
     *     (?i)hz                   HZ
     *     (?i)pm                   PM
     *     (?i)none                 NONE
     *     (?i)documentation        DOCUMENTATION
     *     (==|=|!=|<|>|<=|>=)      PREDICATE
     *     \-?(\d*\.\d+|\d+\.)([eE][\+\-]?\d+)?  NUMFLOAT
     *     \-?[0-9]+                NUMINT
     *     (?i)[a-z][a-z0-9_\-]*    NAME
     *     (?i)'?[a-z][a-z0-9_\-]*  StringVALUE
     *     \"[^\"]*\"                  COMMENT
     *
     * Production Rules
     * ----------------
     *   The following production rules are used::
     *
     *                        plan ::= "(" [ "(" <docString> ]
     *                                     ( ( "(" <competence> | <action-pattern> )*
     *                                       "(" <drive-collection>
     *                                       ( "(" <competence> | <action-pattern> )*
     *                                     )
     *                                     | ( "(" <competence> )
     *                                     | ( "(" <action-pattern> )
     *                                 ")"
     *                   docString ::= DOCUMENTATION COMMENT COMMENT COMMENT ")"
     *
     *            drive-collection ::= <drive-collection-id> NAME
     *                                 ( NIL | "(" <goal> | )
     *                                 "(" DRIVES <drive-priorities> ")" ")"
     *         drive-collection-id ::= DC | RDC | SDC | SRDC
     *            drive_priorities ::= <drive-elements>+
     *              drive-elements ::= "(" <drive-element>+ ")"
     *               drive-element ::= "(" NAME ( NIL | "(" <trigger> | ) NAME
     *                                     ( NIL | "(" <freq> | ) <opt-comment> ")"
     *
     *                  competence ::= C NAME ( NIL | "(" <time> | )
     *                                 ( NIL | "(" <goal> | ) "(" ELEMENTS
     *                                 <competence-priorities> ")" <opt-comment> ")"
     *       competence-priorities ::= <competence-elements>+
     *         competence-elements ::= "(" <competence-element>+ ")"
     *          competence-element ::= "(" NAME [ "(" <trigger> ] NAME [ INTNUM ]
     *                                     <opt-comment> ")"
     *
     *              action-pattern ::= AP NAME ( NIL | "(" <time> | )
     *                                 "(" <action-pattern-elements> <opt-comment> ")"
     *     action-pattern-elements ::= ( <full-sense> | NAME )+ ")"
     *
     *                        goal ::= GOAL <senses> ")"
     *                     trigger ::= TRIGGER <senses> ")"
     *                      senses ::= ( NIL | "(" ( NAME | <full-sense> )+ ")" )
     *                  full-sense ::= "(" NAME [<value> [<predicate>]] ")"
     *                       value ::= NUMINT | NUMFLOAT | NAME | StringVALUE | NIL
     *                   predicate ::= PREDICATE
     *
     *                        freq ::= <freq-unit> <numfloat> ")"
     *                   freq-unit ::= TICKS | MINUTES | SECONDS | HZ | PM | NONE
     *                        time ::= <time-unit> <numfloat> ")"
     *                   time-unit ::= TICKS | MINUTES | SECONDS | NONE
     *                    numfloat ::= NUMINT | NUMFLOAT
     *
     *                 opt-comment ::= COMMENT |
     *
     *
     * A recursive descent parser for .lap files.
     * 
     * The parser takes a single input String that represents the plan
     * file and creates a plan builder Object that it then returns.
     * 
     * If an error while parsing (or tokenising) is encountered,
     * a ParseError is raised.
     */


public class LapParser {
	
	Log log;
	
	

    // additional members

    private LapLexer lex;

    private Token token;
    
    
    // Initialises the parser.
    public LapParser()    {
    	log = new Log("LAPParser");
        lex = null;
        token = null;
    }
    

    // Parses the given input and returns a plan builder Object.
    public PlanBuilder parse( String inputString) throws Exception    {
        lex = new LapLexer(inputString);
        return start();
    }

  
    // Gets the next token from the lexer.
    public void nextToken()    {
        token = lex.token();
    }


    // Checks if the current token matches the allowed tokens.
    // 
    // If there is no current token, then this method raises an Exception
    // that indicates that we've reached the end of the input (unexpectedly).
    // 
    // Otherwise it returns if the current token type matches any of the
    // given token types.
    public boolean match(String[] allowedTokens) throws Exception    {
        if (!(token != null))	{
            log.error("Unexpected End Of File (EOF)");
        	throw new Exception();
    	}
        return (arrayContains(allowedTokens,token.token));
    }

   

    // The parser start symbol.
    // 
    // When called, it parses the set input String and returns
    // the created plan builder Object.
    public PlanBuilder start() throws Exception    {
        nextToken();
        return plan();
    }



    // plan ::= "(" [ "(" <docString> ]
    //                ( ( "(" <competence> | <action-pattern> )*
    //                 "(" <drive-collection>
    //                 ( "(" <competence> | <action-pattern> )*
    //               )
    //                | ( "(" <competence> )
    //                | ( "(" <action-pattern> )
    //            ")"
    private PlanBuilder plan() throws Exception    {
        PlanBuilder planBuilder = new PlanBuilder();
        // this method cheats a bit by counting the action-pattern
        // and competences and also drive-collections to check when things are
        // allowed were.
        if (!match(new String[] {"LPAREN",}))
            log.error("Plan needs to start with '(' rather than " + token.value);
        nextToken();
        // action pattern, competence, docString, drive collection
        int ap = 0, c = 0, d = 0, dc = 0;
        while (true)        {
            if (!match(new String[] {"LPAREN", "RPAREN"}))
                log.error("Expected '(' as start of documentation / " +
                    "competence / action-pattern / drive-collection, or " +
                    "')' to end plan, instead of " +token.value);
            if (match(new String[] {"RPAREN",}))           {
                // end of plan
                nextToken();
                break;
            }
            nextToken();
            // check for documentation
            if ( match(new String[] {"DOCUMENTATION",}) )            {
                if ( ap + c + dc + d > 0 )
                	log.error("Documentation only allowed as first " +
                        "element in plan");
                d +=1;
                //not implemented
                //planBuilder.setDocString(getDocString());
                // print docString();
            } 
            // check for competence
            else if (match(new String[] {"C",}))            {
                c++;
                planBuilder.addCompetence(getCompetence());
                // print competence()
            }
            // check for action-pattern
            else if ( match(new String[] {"AP",}) )            {
                ap++;
                planBuilder.addActionPattern(getActionPattern());
            }
            // check for drive-collection
            else if ( match(new String[] {"DC", "RDC", "SDC", "SRDC"}) )            {
                if ( dc > 0 )
                    log.error("Only a single drive-collection allowed");
                dc ++;
                planBuilder.setDriveCollection(getDriveCollection());
            }            else
                log.error("Expected docString / competence / action " +
                    "pattern or drive collection instead of " + token.value);
        }

        // the plan was closed
        if (token != null)
            log.error("Illegal token " + token.value + " after end of plan" );
        if (dc == 0 && (ap+c) != 1)
            log.error("Illegal plan: A plan without a drive-collection " +
                "only allows for a SINLGE action-pattern OR a SINGLE competence");       

        // everything is fine
        return planBuilder;
    }


    // docString ::= DOCUMENTATION COMMENT COMMENT COMMENT ")"
    public String[] getDocString() throws Exception   {
        if (!match(new String[] {"DOCUMENTATION",}))
            log.error("Expected 'documentation' as start of docString " +
                "instead of  " + token.value );
        nextToken();
        String[] docs = new String[3];
        for (int i = 0; i < 3; i++)
        {
            if (!match(new String[] {"COMMENT",}))
                log.error("Expected a comment of form \"...\" instead " +
                    "of " + token.value + " '%s' in documentation");
            docs[i] =(token.value.substring(1,token.value.length()-2));
            nextToken();
        }
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' to end docString instead of " + token.value);
        nextToken();
        return docs;
    }



    // drive-collection ::= <drive-collection-id> NAME
    //                       ( NIL | "(" <goal> | )
    //                       "(" DRIVES <drive-priorities> ")" ")"
    // 
    // If no goal is given, None is returned for the goal.
    public TupleFour<String,String,List<Object>,List<TupleFour<String, List<Object>, String, Long>[]>> getDriveCollection() throws Exception    {
        String cid = getDriveCollectionId();
        List<Object> goal = null;
        List<TupleFour<String, List<Object>, String, Long>[]> priorities;
        if (!match(new String[] {"NAME",}))
            log.error("Expected a valid drive collection name instead " +
                "of  " + token.value );
        String name = token.value;
        nextToken();
        // check if there is a goal and set it if given
        // ( NIL | "(" <goal> | ) "("

        if (match(new String[] {"NIL",}))        {
            // NIL "("
            nextToken();
            if (!match(new String[] {"LPAREN",}))
                log.error("Expected '(' after 'nil' instead of  " + token.value + " in " +
                    "drive collection  " + name);
            nextToken();
        }       else        {
            // "(" [ <goal> "(" ]
            if (!match(new String[] {"LPAREN",}))
                log.error("Expected '(' after drive collection name " +
                    "instead of " + token.value + " in drive collection  " + name);
            nextToken();
            // check if a goal is specified
            if (match(new String[] {"GOAL",}))            {
                // <goal> "("
                goal = getGoal();
                if (!match(new String[] {"LPAREN",}))
                    log.error("Expected '(' after goal " +
                        "instead of " + token.value + " in drive collection " + name);
                nextToken();
            }
        }
        // get the drive priorities
        if (!match(new String[] {"DRIVES",}))
            log.error("Expected 'drives' instead of  " + token.value + " in drive " +
                "collection  " + name);
        nextToken();
        priorities = getDrivePriorities();
        for(int i = 0; i < 2;i++)        {
            if (!match(new String[] {"RPAREN",}))
                log.error("Expected ')' to end drive collection instead " +
                    "of  " + token.value + " in drive collection  " + name);
            nextToken();
        }
        
        return new TupleFour<String,String,List<Object>,List<TupleFour<String, List<Object>, String, Long>[]>>(cid,name,goal,priorities);
    }

    // drive-collection-id ::= DC | RDC | SDC | SRDC
    public String getDriveCollectionId() throws Exception    {
        if (!match(new String[] {"DC", "RDC", "SDC", "SRDC"}) )
            log.error("Expected the drive collection type instead of { " + token.value);
        String cid = token.token;
        nextToken();
        return cid;
    }


    // drive_priorities ::= <drive-elements>+

    public List<TupleFour<String, List<Object>, String, Long>[]> getDrivePriorities() throws Exception    {
        List<TupleFour<String, List<Object>, String, Long>[]> priorities;
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' that starts list of drive elements " +
                "instead of " + token.value);
        priorities = new ArrayList<TupleFour<String, List<Object>, String, Long>[]>();
        while(match(new String[] {"LPAREN",}))
            priorities.add(getDriveElements());
        
        return priorities;
    }

    
   


    // drive-elements ::= "(" <drive-element>+ ")"
    @SuppressWarnings("unchecked")
	public TupleFour<String, List<Object>, String, Long>[] getDriveElements() throws Exception    {
        TupleFour<String, List<Object>, String, Long>[] elements;
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' that starts list of drive elements " +
                "instead of " + token.value);
        nextToken();
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' that starts list of drive elements " +
                "instead of " + token.value);
        elements = new TupleFour[0];
        while (match(new String[] {"LPAREN",}))	{
        	//resize the array by +1
        	TupleFour<String, List<Object>, String, Long>[] tempArray = new TupleFour[elements.length+1];
        	for (int i = 0; i < elements.length; i ++)	{
        		tempArray[i] = elements[i];
        	}
        	elements = tempArray;
            elements[elements.length-1] = getDriveElement(); 
        }
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' to end list of drive elements " +
                "instead of " + token.value);
        nextToken();
        return elements;
    }


    // drive-element ::= "(" NAME ( NIL | "(" <trigger> | ) NAME
    //                         ( NIL | "(" <freq> | ) <opt-comment> ")"
    // 
    // If no trigger is given, then None is returned for the trigger.
    // If no frequency is given, then 0 is returned for the frequency.
    public TupleFour<String, List<Object>, String, Long> getDriveElement() throws Exception    {
        String name;
        List<Object> trigger = null;
        String triggerable = null;
        long freq = 0;

        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' to start drive element instead " +
                "of " + token.value);
        nextToken();
        if(!match(new String[] {"NAME",}))
            log.error("Expected valid drive element name instead of " + token.value);
        name = token.value;
        nextToken();
        // ( NIL | "(" <trigger> | ) NAME
        if (!match(new String[] {"NAME","LPAREN","NIL"}))
            log.error("Expected name of triggerable, '(' or 'nil' " +
                "instead of "+ token.value + " in drive element " +  name);
        // get trigger if there is one
        if (match(new String[] {"NIL","LPAREN"}))
        {
        	if (match(new String[] {"NIL",}))
                nextToken();
            else            {
                nextToken();
                trigger = getTrigger();
            }
            if (!match(new String[] {"NAME",}))
                log.error("Expected name of triggerable instead of "+token.value +
                    "in drive elements " +  name);     
        }
        // get triggerable (NAME)
        triggerable = token.value;
        nextToken();
        // check for frequency
        // ( NIL | "(" <freq> | )
        if (match(new String[] {"LPAREN","NIL"}))
            if (match(new String[] {"NIL",}))
                nextToken();
            else            {
                nextToken();
                freq = getFreq();
            }
        // <opt-comment> ")"
        getOptComment();
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' instead of '"+token.value+" as the end of drive " +
                "element " +  name);
        nextToken();

        return new TupleFour<String,List<Object>,String,Long>(name,trigger,triggerable,freq);
    }



    // competence ::= C NAME ( NIL | "(" <time> | )
    //                ( NIL | "(" <goal | ) "(" ELEMENTS
    //                 <competence-priorities> ")" <opt-comment> ")"
    // 
    // If no time is given, them time is set to None.
    // If no goal is given, the goal is set to None.
    public TupleFour<String,Long,List<Object>,List<TupleFour<String,List<Object>,String,Integer> []>> getCompetence() throws Exception    {
        String name;
        long time = 0;
        List<Object> goal = null;
        List<TupleFour<String,List<Object>,String,Integer> []> priorities;

        // C NAME
        if (!match(new String[] {"C",}))
            log.error("Expected 'C' as start of competence instead " +
                "of " + token.value);
        nextToken();
        if (!match(new String[] {"NAME",}))
            log.error("Expected valid competence name instead " +
                "of " + token.value);
        name = token.value;
        nextToken();
        // ( NIL | "(" <time> | ) ( NIL | "(" <goal> | ) "("
        // The branching below should be checked (might have missed a case)
        if (!match(new String[] {"LPAREN","NIL"}))
            log.error("Expected '(' or 'nil' after competence name " +
                "instead of "+ token.value + " in competence ");
        if (match(new String[] {"NIL",}))        {
            // NIL ( NIL | "(" <goal> | ) "("
            nextToken();
            if (!match(new String[] {"LPAREN","NIL"}))
                log.error("Expected '(' or 'nil' after 'nil' for time " +
                    "instead of "+ token.value + " in competence " +  name);
            if (match(new String[] {"NIL",}))            {
                // NIL NIL "("
                nextToken();
                if (!match(new String[] {"LPAREN",}))
                    log.error("Expected '(' after 'nil' for goal instead " +
                        "instead of "+ token.value + " in competence " +  name);
                nextToken();
            }            else            {
                // NIL "(" [ <goal> "(" ]
                nextToken();
                if (match(new String[] {"GOAL",}))                {
                    goal = getGoal();
                    if (!match(new String[] {"LPAREN",}))
                        log.error("Expected '(' after goal instead of " +
                            "instead of "+ token.value + " in competence " +  name);
                    nextToken();
                }
            }
        }        else        {
            // "(" ( <time> ( NIL | "(" <goal> | ) "(" | <goal> "(" | )
            nextToken();
            if (match(new String[] {"TICKS","MINUTES","SECONDS","NONE"}))            {
                // "(" <time> ( NIL | "(" <goal> | ) "("
                time = getTime();
                if (!match(new String[] {"LPAREN","NIL"}))
                        log.error("Expected '(' or 'nil' after time instead " +
                            "instead of "+ token.value + " in competence " +  name);
                if (match(new String[] {"NIL",}))                {
                    // "(" <time> NIL "("
                    nextToken();
                    if (!match(new String[] {"LPAREN",}))
                        log.error("Expected '(' after 'nil' for goal " +
                            "instead of "+ token.value + " in competence " +  name);
                    nextToken();
                }                else                {
                	//  "(" <time> "(" [ <goal> "(" ]
                    nextToken();
                    if (match(new String[] {"GOAL",}))                   {
                        goal = getGoal();
                        if (!match(new String[] {"LPAREN",}))
                            log.error("Expected '(' after goal " +
                                "instead of "+ token.value + " in competence " +  name);
                        nextToken();
                    }
                }
            }             else if (match(new String[] {"GOAL",}))            {
                //  "(" <goal> "("
                goal = getGoal();
                if (!match(new String[] {"LPAREN",}))
                    log.error("Expected '(' after goal " +
                        "instead of "+ token.value + " in competence " +  name);
                nextToken();
            }
        }
        // competence priorities
        // ELEMENTS <competence-priorities> <opt-comment> ")"
        if (!match(new String[] {"ELEMENTS",}))
            log.error("Expected 'elements' as start of element " +
                "instead of "+ token.value + " in competence " +  name);
        nextToken();
        priorities = getCompetencePriorities();
        if (!match(new String[] {"RPAREN",}))
        	log.error("Expected ')' to end competence " +
                "instead of "+ token.value + " in competence " +  name);
        nextToken();
        getOptComment();
        if (!match(new String[] { "RPAREN", }))
            log.error("Expected ')' to end competence " +
                "instead of "+ token.value + " in competence " +  name);
        nextToken();

        return new TupleFour<String,Long,List<Object>,List<TupleFour<String,List<Object>,String,Integer> []>>(name,time,goal,priorities);
    }


    // <code>
    // <![CDATA[competence-priorities ::= <competence-elements>+]]>
    // </code>
    public List<TupleFour<String,List<Object>,String,Integer> []> getCompetencePriorities() throws Exception    {
        List<TupleFour<String,List<Object>,String,Integer> []> priorities = new ArrayList<TupleFour<String,List<Object>,String,Integer>[]>();
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' as start of a list of competence elements "+
                "instead of " + token.value);
        while(match(new String[] {"LPAREN",}))
            priorities.add(getCompetenceElements());

        return priorities;
    }

    
    // <code>
    // <![CDATA[competence-elements ::= "(" <competence-element>+ ")"]]>
    // </code>
    
	@SuppressWarnings("unchecked")
	public TupleFour<String,List<Object>,String,Integer> [] getCompetenceElements() throws Exception    {
       TupleFour<String,List<Object>,String,Integer>[] elements = new TupleFour[0];

        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' as start of a list of competence elements "+
                "instead of " + token.value);
        nextToken();
        // a competence element start with a '('
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' as start a competence element "+
                "instead of " + token.value);
        while (match(new String[] {"LPAREN",})){
        	TupleFour<String, List<Object>, String, Integer>[] tempArray = new TupleFour[elements.length+1];
        	for (int i = 0; i < elements.length; i ++)	{
        		tempArray[i] = elements[i];
        	}
        	elements = tempArray;
        	elements[elements.length-1] = getCompetenceElement(); 
        }
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' as end of a list of competence elements "+
                "instead of " + token.value);
        nextToken();

        return elements;
    }
           

    


	// <code>
    // <![CDATA[
    // competence-element ::= "(" NAME ( NIL | "(" <trigger> | ) NAME
    //                             ( NIL | INTNUM | )
    //                             <opt-comment> ")"
    // ]]>
    // </code>
    // 
    // If no number of retires is given, then -1 is returned.
    // </summary>
    public TupleFour<String,List<Object>,String,Integer> getCompetenceElement() throws Exception    {
        String name;
        List<Object> trigger = null;
        String triggerable;
        int maxRetries;

        // "(" NAME
        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' to start a competence element "+
                "instead of " + token.value);
        nextToken();
        if (!match(new String[] {"NAME",}))
            log.error("Expected competence element name "+
                "instead of " + token.value);
        name = token.value;
        nextToken();
        // check for trigger
        // ( NIL | "(" <trigger> | )
        if (match(new String[] {"NIL",}))
            nextToken();
        else if (match(new String[] {"LPAREN",}))        {
            nextToken();
            trigger = getTrigger();
        }
        // NAME
        if (!match(new String[] {"NAME",}))
            log.error("Expected name of triggerable "+
                "instead of "+ token.value + " in competence " +  name);
        triggerable = token.value;
        nextToken();
        // check for maxRetries
        // ( NIL | INTNUM | )
        maxRetries = -1;
        if (match(new String[] {"NIL",}))
            nextToken();
        else if (match(new String[] {"NUMINT",}))
        {
            maxRetries = Integer.parseInt(token.value);
            nextToken();
        }
        // <opt-comment> ")"
        getOptComment();
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' to end competence element "+
                "instead of "+ token.value + " in competence " +  name);
        nextToken();

        return new TupleFour<String,List<Object>,String,Integer>(name, trigger, triggerable, maxRetries);
    }
    

    // <code>
    // <![CDATA[
    // aption-pattern ::= AP NAME ( NIL | "(" <time> | )
    //                     "(" <action-pattern-elements> <opt-comment> ")"
    // ]]>
    // If no time is given, None is returned for the time.
    // </code>
    public TupleThree<String,Long,List<Object>> getActionPattern() throws Exception    {
        String name;
        long time = 0;
        List<Object> elements;

        // AP NAME
         if (!match(new String[] {"AP",}))
            log.error("Expected 'AP' instead of " + token.value);
        nextToken();
        if (!match(new String[] {"NAME",}))
            log.error(token.value + " is not a valid name for an action pattern");
        name = token.value;
        nextToken();

        // ( NIL | "(" <time> | ) "("
        if (match(new String[] {"NIL",}))        {
        	// NIL "("
            nextToken();
            if (!match(new String[] {"LPAREN",}))
                log.error("Expected '(' after 'nil' for time instead of "+ token.value + ""+
                    "in action pattern " +  name);
            nextToken();
        }        else if (match(new String[] {"LPAREN",}))        {
            // "(" [ <time> "(" ]
            nextToken();
            if (match(new String[] {"TICKS","MINUTES","SECONDS","NONE"}))            {
                // "(" <time> "("
                time = getTime();
                if (!match(new String[] {"LPAREN",}))
                    log.error("Expected '(' after time instead of "+ token.value + ""+
                        "in action pattern " +  name);
                nextToken();
            }
            }        else
            log.error("Expected '(' or 'nil' after action pattern name "+
                "instead of "+ token.value + " in action pattern " +  name);
        // proceed with action pattern element list
        // <action-pattern-elements> <opt-comment> ")"
        elements = getActionPatternElements();
        getOptComment();

        if (!match(new String[] {"RPAREN",}))
                log.error("Expected ')' instead of "+ token.value + ""+
                    "in action pattern " +  name);
        nextToken();

        return new TupleThree<String,Long,List<Object>>(name,time,elements);
    }



    // <code>
    // <![CDATA[action-pattern-elements ::= ( <full-sense> | NAME )+ ")"]]>
    // </code>
    public List<Object> getActionPatternElements() throws Exception    {
        List<Object> elements = new ArrayList<Object>();

        if (!match(new String[] {"LPAREN","NAME"}))
            log.error("Expected an action pattern element name of '(' "+
                "instead of " +  token.value);
        while (match(new String[] {"NAME","LPAREN"}))        {
            if(match(new String[] {"LPAREN",}))
                elements.add(getFullSenses());
            else            {
                elements.add(token.value);
                nextToken();
            }
        }
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' to end action pattern instead of " + token.value);
        nextToken();

        return elements;
    }

    


    // <code>
    // <![CDATA[goal ::= GOAL <senses> ")"]]>
    // </code>
    // 
    // If the list of senses is empty, then None is returned.
    public List<Object> getGoal() throws Exception    {
        List<Object> senses;
        boolean senseNull = true;

        if (!match(new String[] {"GOAL",}))
            log.error("Expected 'goal' instead of " + token.value);
        nextToken();
        senses = getSenses();
        senseNull = true;
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' as the end of a goal instead of " + token.value);
        nextToken();
        
        if (senseNull == true)	{
        	return senses;
        }
        return null;
        
    }


 
    // <code>
    // <![CDATA[trigger ::= TRIGGER <senses> ")"]]>
    // </code>
    // If the list of senses is empty, then None is returned.
    public List<Object> getTrigger() throws Exception    {
        List<Object> senses;
        boolean senseNull = true;

        if (!match(new String[] {"TRIGGER",}))
            log.error("Expected 'trigger' instead of  " + token.value);
        nextToken();
        senses = getSenses();
        senseNull = true;
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' as the end of a trigger "+
                "instead of  " + token.value);
        nextToken();
        
        if (senseNull == true)	{
        	return senses;
        }
        return null;
    }

 
    // <code>
    // <![CDATA[
    // senses ::= ( NIL | "(" ( NAME | <full-sense> )+ ")" )
    // ]]>
    // </code>
    // If NIL is given, an empty list is returned.
    public List<Object> getSenses() throws Exception    {
        List<Object> elements;

        if (match(new String[] {"NIL",}))        {
            nextToken();
            return new ArrayList<Object>();
        }
        if (!match(new String[] {"LPAREN",}))
        	log.error("Expected '(' instead of " + token.value);
        nextToken();
        elements = new ArrayList<Object>();
        while (true)        {
            if (match(new String[] {"RPAREN",}))
                break;
            if (!match(new String[] {"LPAREN","NAME"}))
                log.error("Expected either a sense-act name or '(' "+
                    "instead of " + token.value);
            // differentiate between sense-acts and senses
            if (match(new String[] {"NAME",}))            {
                elements.add(token.value);
                nextToken();
            }             else
                elements.add(getFullSenses());
        }
        // matches ')'
        nextToken();
        return elements;
    }



    // <code>
    // <![CDATA[full-sense ::= "(" NAME [<value> [<predicate>]] ")"]]>
    // </code>
    public TupleThree<String,String,String> getFullSenses() throws Exception    {
        String name = null, value = null, pred = null;

        if (!match(new String[] {"LPAREN",}))
            log.error("Expected '(' instead of " + token.value);
        nextToken();
        if (!match(new String[] {"NAME",}))
            log.error("Expected sense name instead of " + token.value);
        name = token.value;
        nextToken();
        if (!match(new String[] {"RPAREN",}))        {
        	value = getValue();
        	if (!match(new String[] {"RPAREN",}))
                pred = getPredicate();
        }
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' instead of " + token.value);
        nextToken();

        return new TupleThree<String,String,String>(name,value,pred);
    }

    // value ::= NUMINT | NUMFLOAT | NAME
    public String getValue() throws Exception    {
        if (!match(new String[] {"NUMINT","NUMFLOAT","NAME","StringVALUE","NIL"}))
            log.error("Expected a valid sense value " +
                " instead of " + token.value);
        String value = token.value;
        nextToken();
        return value;
    }

    // predicate ::= PREDICATE
    public String getPredicate() throws Exception    {
        if (!match(new String[] {"PREDICATE",}))
            log.error("Expected a valid sense predicate " +
                " instead of " + token.value);
        String pred = token.value;
        nextToken();
        
        return pred;
    }


    // freq ::= <freq-unit> <numfloat> ")"
    public long getFreq() throws Exception    {
        String unit = getFreqUnit();
        float value = getNumFloat();
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' instead of " + token.value);
        nextToken();
        //process the frequency unit
        switch (unit)        {
            case "TICKS":
                return (long) (value);
            case "MINUTES":
                return (long) (60000.0 * value);
            case "SECONDS":
                return (long) (1000.0 * value);
            case "HZ":
                return (long) (1000.0 / value);
            case "PM":
                return (long) (60000.0 / value);               
            default:
                return (long) value;
        }
    }


    // freq-unit ::= TICKS | MINUTES | SECONDS | HZ | PM | NONE
    public String getFreqUnit() throws Exception    {
    	if (!match(new String[] {"TICKS", "MINUTES", "SECONDS", "HZ", "PM", "NONE"}))
            log.error("Expected a valid frequency unit " +
                "instead of " + token.value);
        String unit = token.token;
        nextToken();

        return unit;
        }



    // time ::= <time-unit> <numfloat> ")"
    public long getTime() throws Exception    {
    	String unit = getTimeUnit();
        float value = getNumFloat();
        
        if (!match(new String[] {"RPAREN",}))
            log.error("Expected ')' instead of " + token.value);
        nextToken();
        // process the time unit
        switch (unit)        {
            case "TICKS":
                return (long) (3600000.0 * value);
            case "MINUTES":
                return (long) (60000.0 * value);
            case "SECONDS":
                return (long) (1000.0 * value);
            default:
                return (long) value;
        }
    }



    // time-unit ::= TICKS | MINUTES | SECONDS | NONE
    public String getTimeUnit() throws Exception    {
        if (!match(new String[] {"TICKS", "MINUTES","SECONDS","NONE"}))
            log.error("Expected a valid time unit " +
                "instead of " + token.value);
        String unit = token.value;
        nextToken();

        return unit;
    }

    

    // numfloat ::= NUMINT | NUMFLOAT
    public float getNumFloat() throws Exception    {
        if (!match(new String[] {"NUMINT", "NUMFLOAT"}))
            log.error("Expected a floating-point number " +
                "instead of " + token.value);
        String value = token.value;
        nextToken();

        return Float.parseFloat(value);
    }


    // opt-comment ::= COMMENT |
    public void getOptComment() throws Exception
    {
        if (match(new String[] {"COMMENT",}))
            nextToken();
    }
    
    //contains for string arrays
    private boolean arrayContains(String[] array, String ch)	{
    	boolean result = false;
    	for (int i = 0 ; i < array.length; i++)	{
    		if (array[i].equalsIgnoreCase(ch))	{
    			result = true;
    			break;
    		}
    	}

    	return result;
    }
    
    
    
    
}

