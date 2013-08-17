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


public class Competence extends ElementCollection {
	
	private CompetencePriorityElement[] elements;
	private Trigger goal;
	
	private Log log;
	
	public Competence(Agent agent, String competenceName,
			CompetencePriorityElement[] competencePriorityElements, Trigger goal) {
		super(agent);

		this.name = competenceName;
		this.elements = competencePriorityElements;
		this.goal = goal;
		
		log = new Log("Competence " + competenceName + " for agent " + agent.getAgentID() );
		log.debug("Created");
	}

	public FireResult fire()	{
		log.debug("fire");
		
		//check if the goal is satisfied
		if (goal != null && goal.fire())	{
			log.debug("Goal satisfied");
			return new FireResult(false, null);
		}
		//process the elements
		FireResult result;
		for (int i = 0 ; i < elements.length; i ++)	{
			CompetencePriorityElement elem = elements[i];
			result = elem.fire();
			//check if the elemen failed
			if (result.nextElement()== null || (result.continueExecution() && !(result.nextElement().getClass().equals(CopiableElement.class))))	{
				continue;
			}
			return result;
		}
		log.debug("failed");
		return new FireResult(false, null);
	}
	
	public void setElements(CompetencePriorityElement[] elementArray) {
		this.elements = elementArray;
		reset();
	}
	
	public void reset()	{
		log.debug("reset");
		for (int i = 0; i <elements.length; i++)	{
			elements[i].reset();
		}
	}
}
