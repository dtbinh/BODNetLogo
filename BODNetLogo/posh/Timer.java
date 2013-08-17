package posh;



/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


/**
 * Provides a stepped time only
 *
 */
public class Timer {

	private long time;
	
	public Timer()	{
		reset();
	}
	
	public void reset()	{
		time = 0;
		
	}
	
	public long Time()	{
		return time;
	}
	
	public void LoopEnd()	{
		time ++;
	}
}
