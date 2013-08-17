package posh.lapParser;


/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


public class Token {
	public String token;
	public String value;
	
	/**
	 *  Initilaises the token with a token-name and a value.
	 * @param token The name of the token
	 * @param value The value of the token
	 */
    public Token(String token,String value)	{
    	this.token = token;
    	this.value = value;
    }
	
}