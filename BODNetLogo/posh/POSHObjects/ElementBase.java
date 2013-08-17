package posh.POSHObjects;

import agentManager.Agent;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


/**
 * The base for any POSH element
 * @author Michael
 *
 */
public abstract class ElementBase {
	static int currentID = 0;
	protected int id;
	protected String name;
	
	static int getNextId()	{
		return currentID ++;
	}
	
	public ElementBase()	{}
	
	public ElementBase(Agent agent)	{
		id = getNextId();
		name = "NoName";
	}
	
	public String getName()	{
		return name;
	}
	
	public int getID()	{
		return id;
	}

	
}
