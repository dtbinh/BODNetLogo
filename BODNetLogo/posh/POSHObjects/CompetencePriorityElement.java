package posh.POSHObjects;

import java.util.ArrayList;

import agentManager.Agent;


import utils.logging.Log;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class CompetencePriorityElement extends ElementCollection {

	private ArrayList<CompetenceElement> elements;
	
	private Log log;
	
	public CompetencePriorityElement(Agent agent, String competenceName, CompetenceElement[] elements)	{
		super(agent);
		
		
		this.name = competenceName;
		
		log = new Log ("Agent " + agent.getAgentID() + "CompetencePriorityElement " + competenceName);
		log.debug("Created");
		
		
		if (elements.length > 0)	{
			this.elements = new ArrayList<CompetenceElement>();
			for (int i = 0; i < elements.length; i ++)	{
				this.elements.add(elements[i]);
			}
		}	else	{
			this.elements = new ArrayList<CompetenceElement>();
		}
	}
	
	public void reset()	{
		log.debug("reset");
		for (int i = 0 ; i < elements.size(); i ++)	{
			elements.get(i).reset();
		}
	}
	
	public FireResult fire()	{
		log.debug("fired");
		
		for (int i = 0; i < elements.size(); i ++)	{
			CompetenceElement element = elements.get(i);
			//note the timestamp value doesnt matter for competences
			if (element.isReady(0))	{
				return element.fire();
			}
		}
		log.debug("Failed");
		return new FireResult(true, null);
	}
	
	public CopiableElement copy()	{
		CompetencePriorityElement newObj = null;
		try {
			newObj = (CompetencePriorityElement) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		ArrayList<CompetenceElement> newElements = new ArrayList<CompetenceElement>();
		for (int i=0; i < elements.size(); i ++)	{
			CompetenceElement element = elements.get(i);
			newElements.add((CompetenceElement)element.copy());
		}
		newObj.elements = newElements;
		
		return newObj;
	}

}
