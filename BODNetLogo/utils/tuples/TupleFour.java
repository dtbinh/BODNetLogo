package utils.tuples;


/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class TupleFour<A, B, C, D>	{
	public final A First; 
	public final B Second; 
	public final C Third; 
	public final D Fourth; 
	
	public TupleFour(A First, B Second, C Third, D Fourth) { 
	    this.First = First; 
	    this.Second = Second; 
	    this.Third = Third; 
	    this.Fourth = Fourth; 
	} 
	
	@SuppressWarnings("unchecked")
	public TupleFour<A,B,C,D>[] resizeTupleArray(TupleFour<A,B,C,D>[] array)	{
		TupleFour<A,B,C,D>[] tempArray = new TupleFour[array.length+1];
    	for (int i = 0; i < array.length; i ++)	{
    		System.out.println("here");
    		tempArray[i] = array[i];
    	}
    	return tempArray;
	}
}
