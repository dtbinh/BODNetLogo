package posh.POSHObjects;

import agentManager.Agent;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public abstract class Element extends PlanElement {

	
	public Element(Agent agent) {
		super(agent);
	}

	public FireResult fire() {
		return null;
	}
	

}
