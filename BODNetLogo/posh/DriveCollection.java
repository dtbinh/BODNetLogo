package posh;

import agentManager.Agent;
import posh.POSHObjects.DriveElement;
import posh.POSHObjects.DrivePriorityElement;
import posh.POSHObjects.FireResult;
import posh.POSHObjects.Trigger;

import posh.POSHObjects.ElementCollection;
import utils.logging.Log;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class DriveCollection extends ElementCollection {

	private DrivePriorityElement[] elements;
	private Trigger goal;

	public DriveElement lastTriggeredElement;
	
	public Log log;

	public DriveCollection(Agent agent, String collectionName,
			DrivePriorityElement[] priorityElements, Trigger goal) {
		super(agent);

		name = collectionName;
		log = new Log("Drive Collection: '" + collectionName + "' for agent " + agent.getAgentID());
		log.debug("Created");
		elements = priorityElements;
		this.goal = goal;
	}

	//fire the drive collection once for the given agent
	public FireResult fire(int agentID) {
		log.debug("Fired");
		
		if (goal != null && goal.fire())	{
			log.debug("Goal Satisified");
			FireResult fr = new FireResult(true, null);
			fr.setFinishedPlan(true);
			return fr;
		}
		
		for (int i = 0; i < elements.length; i ++ )	{
			if (elements[i].fire()!=null)	{
				return new FireResult(true, null);
			}
		}
		
		log.debug("Failed");
		return new FireResult (false, null);
	}
	
	
}

