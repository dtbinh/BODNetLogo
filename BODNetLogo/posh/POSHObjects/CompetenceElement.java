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

public class CompetenceElement extends Element {

	private Trigger trigger;
	private CopiableElement element;
	private int maxRetries;
	private int retries;
	
	private Log log;
	
	public CompetenceElement(Agent agent, String elementName, Trigger trigger, CopiableElement element, int maxRetries) {
		super(agent);
		
		this.name = elementName;
		this.trigger = trigger;
		this.element = element;
		this.maxRetries = maxRetries;
		this.retries = 0;
		
		log = new Log("Agent "+agent.getAgentID()+ " CompetenceElement " + elementName);
		log.debug("Created");
	}
	
	public void reset()	{
		retries = 0;
	}
	
	public boolean isReady(long timeStamp)	{
		if (trigger.fire()){
			if (maxRetries < 0 || retries < maxRetries)	{
				retries ++;
				return true;
			}	else	{
				log.debug("retry limit exceeded");
			}
		}
		return false;
	}

	
	public FireResult fire()	{
		log.debug("fired");
		if (element.getClass().equals(Action.class))	{
			((Action)element).fire();
			return new FireResult(false, null);
		}
		return new FireResult(true, element);
	}
	
	public CopiableElement copy()	{
		CompetenceElement newObj = null;
		try {
			newObj = (CompetenceElement) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		newObj.reset();
		return newObj;
	}
}
