package posh.POSHObjects;

import java.util.ArrayList;

import agentManager.Agent;


import posh.Timer;
import utils.logging.Log;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class DrivePriorityElement extends ElementCollection {

	private Log log;
	
	private DriveElement[] elements;
	
	private Agent agent;
	
	private Timer timer;
	
	
	public DrivePriorityElement(Agent agent) {
		super(agent);
	}

	public DrivePriorityElement(Agent agent, String dcName, DriveElement[] elements) {
		super(agent);
		
		name = dcName;
		this.elements = elements;
		timer = agent.getTimer();
		this.agent = agent;
		
		log = new Log("Agent " + agent.getAgentID() + " DrivePriorityElement: " + name);
		log.debug("Created");
	}
	
	
	
	
	public FireResult fire()	{
		log.debug("Fired");
		long timeStamp = timer.Time();
		DriveElement[] newElements = getSortedDrive(elements);
		
		if (arrayContains(elements, agent.dc.lastTriggeredElement))	{
			if(agent.dc.lastTriggeredElement.isReady(timeStamp))	{
				agent.dc.lastTriggeredElement.fire();
				return new FireResult(false, null);
			}
		}
		for (int i = 0; i <newElements.length; i ++)	{
			DriveElement element = newElements[i];
			if (element.isReady(timeStamp))	{
				if (element !=agent.dc.lastTriggeredElement)	{
					if (element.isLatched){
						agent.dc.lastTriggeredElement = element;
					}
				}	else if	(!(agent.dc.lastTriggeredElement.isReady(timeStamp)))	{
					agent.dc.lastTriggeredElement = null;
				}	else	{
					if (element.isLatched)	{
						agent.dc.lastTriggeredElement = element;
					}	else	{
						agent.dc.lastTriggeredElement = null;
					}
				}
				element.fire();
				return new FireResult(false, null);
			}
			
		}
				
		return null;
	}
	
	
	/**
	 * Sorts the drive elements by moving the latched elements to the top
	 * allows latched behaviours to take priority
	 * 
	 * @param elements2
	 * @return
	 */
	private DriveElement[] getSortedDrive(DriveElement[] elements2) {
		ArrayList<DriveElement> latchedElements = new ArrayList<DriveElement>();
		ArrayList<DriveElement> nonLatchedElements = new ArrayList<DriveElement>();
		DriveElement[] returnElements = new DriveElement[elements2.length];
		for (int i = 0; i < elements2.length; i ++)	{
			DriveElement element = elements2[i];
			if (element.isLatched)	{
				latchedElements.add(element);
			}	else	{
				nonLatchedElements.add(element);
			}
		}
		for (int i = 0; i < elements2.length; i ++)	{
			for (int j = 0; j < latchedElements.size(); j++)	{
				returnElements[j] = latchedElements.get(j);
			}
			for (int j = 0; j < nonLatchedElements.size(); j++)	{
				returnElements[j+latchedElements.size()] = nonLatchedElements.get(j);
			}
		}
		return returnElements;
	}

	private boolean arrayContains(DriveElement[] x, DriveElement y)	{
		for (int i = 0 ; i < x.length; i ++)	{
			if (x!=null && y !=null)	{
				if (x[i].getID() == y.getID())	{
					return true;
				}
			}
		}
		return false;
	}
	
}
