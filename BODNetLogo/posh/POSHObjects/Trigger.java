package posh.POSHObjects;

import agentManager.Agent;
import utils.logging.Log;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class Trigger extends ElementBase {
	
	public Sense[] senses;
	Log log;
	
	public static String [] getNames(ElementBase[] elements)	{
		String[] result = new String[elements.length];
		for (int i = 0; i < elements.length; i++)	{
			result[i] = elements[i].getName();
		}
		return result;
	}
	
	public Trigger (Agent agent, Sense[] senses){
		super(agent);
		this.senses = senses;
		
		log = new Log("Agent " + agent.getAgentID() + " Trigger");
		
		log.debug("Started");
		
	}
	
	public boolean fire()	{
		log.debug("Firing");
		
		for (int i = 0; i < this.senses.length; i ++)	{
			Sense sense = senses[i];
			if (!sense.fire())	{
				log.debug("Sense Failed: " + sense.getName());
				return false;
			}
		}
		return true;
	}
}
