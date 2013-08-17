package posh.POSHObjects;

import agentManager.Agent;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


public class ElementCollection extends PlanElement {

	public ElementCollection(Agent agent) {
		super(agent);
	}

	@Override
	public FireResult fire() {
		return null;
	}

}
