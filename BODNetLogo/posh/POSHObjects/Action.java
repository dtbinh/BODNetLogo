package posh.POSHObjects;

import org.nlogo.api.CompilerException;
import org.nlogo.app.App;

import agentManager.Agent;

import utils.logging.Log;


/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


public class Action extends CopiableElement {

	String actionName;
	Agent agent;
	
	Log log;

	public Action(Agent agent, String string) {
		super(agent);
		this.agent = agent;
		this.actionName = string;
		log = new Log("Agent: "+agent.getAgentID()+" Running Action: "+ string);
		
	}

	public boolean fire() {
		log.debug("firing action: "+ actionName);
		try {
			App.app().commandLater("ask turtle " + agent.getAgentID() + "["+actionName+"]");
			return true;
		} catch (CompilerException e) {
			//else action failed
			log.error("Firing failed");
			return false;
		}
	}

}
