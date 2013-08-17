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

public class DriveElement extends Element {

	Log log;
	
	public Trigger trigger;
	private Object root;
	private Object element;
	private Long maxFreq;
	private Long lastFired;
	
	Agent agent;
	public boolean isLatched;
	
	
	public DriveElement(Agent agent, String elementName, Trigger trigger, Object root, Long maxFreq)	{
		super(agent);
		
		this.name = elementName;
		this.trigger = trigger;
		this.root = root;
		this.element = root;
		this.maxFreq = maxFreq;
		
		this.lastFired = -100000L;
		
		log = new Log("Agent "+agent.getAgentID()+": "+elementName);
		
		log.debug("Created");
		
		this.agent = agent;
		this.isLatched = false;
						
	}
	
	/**
	 * @param timeStamp
	 * @return
	 */
	public boolean isReady(Long timeStamp)	{
		if (trigger.fire())	{
			if (maxFreq <= 0 || timeStamp - lastFired > +maxFreq)	{
				lastFired = timeStamp;
				return true;
			}	else	{
				log.debug("Max firing frequency exceeded");
				
			}
			
		}
		return false;
	}
	
	public FireResult fire()	{
		FireResult result;
		
		log.debug("fired");
		//if element is an action just fire
		
		
		if (element != null && element.getClass().equals(Action.class) )	{
			((Action)element).fire();
			element = root;
			return null;
		}
		
		//thus the element is a competence or action pattern
		result = ((ElementCollection)element).fire();
		
		if (result.continueExecution()){
			CopiableElement next = result.nextElement(); 
			if (next!=null && next.getClass().equals(CopiableElement.class))	{
				element = next;
			}
		}	else	{
			element = root;
		}
		
		
		return null;
	}
	
}












