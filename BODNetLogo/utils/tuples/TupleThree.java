package utils.tuples;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class TupleThree<A, B, C>	{
	public final A First; 
	public final B Second; 
	public final C Third;  
	
	public TupleThree(A First, B Second, C Third) { 
	    this.First = First; 
	    this.Second = Second; 
	    this.Third = Third;  
	} 
}
