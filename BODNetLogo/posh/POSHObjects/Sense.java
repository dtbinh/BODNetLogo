package posh.POSHObjects;

import org.nlogo.agent.Turtle;
import org.nlogo.api.CompilerException;
import org.nlogo.app.App;

import agentManager.Agent;
import agentManager.AttributeStore;

import utils.logging.Log;

/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class Sense extends CopiableElement {

	String senseName;
	String senseValue;
	String sensePredicate;
	Agent agent;
	
	Log log;
	

	public Sense(Agent agent, String name, String value, String predicate) {
		super(agent);
		
		senseName = name;
		senseValue = value;
		sensePredicate = predicate;
		this.agent = agent;
		log = new Log("Agent: "+ agent.getAgentID()+ " Running Sense: " + name);
	}

	public boolean fire() {
		log.debug("Fired With "+ senseValue + " and " + sensePredicate);
		
		
		//attempt to run the optional update function
		try {
			App.app().command("ask turtle " + agent.getAgentID() + "[update_"+senseName+"]");			
		} catch (CompilerException e) {
		}
		
		Object result = null;
		try	{
			Turtle x = (Turtle) App.app().report("Turtle " + agent.getAgentID());
			
			result = x.getVariable(AttributeStore.getAttributePosition(Integer.parseInt(agent.getAgentID()), senseName));
		}	catch (Exception e)	{
			log.error("Error could not find "+senseName+" in attribute store, or get index " +
					AttributeStore.getAttributePosition(Integer.parseInt(agent.getAgentID()), senseName) +
							" from NetLogo");
		}
		
		

		
		if (result == null)	{
			return false;
		}	else	{
			Double value = 0.0;
			try 	{
				value = (Double) result;
			} catch (Exception e)	{
				//unexpected value
				return false;
			}
			if (sensePredicate == null && senseValue != null)	{
				//assume equality
				log.debug("Sense Value: " + senseValue);
				log.debug("NetLogo Attr Position"+AttributeStore.getAttributePosition(Integer.parseInt(agent.getAgentID()), senseName));
				return value==Double.parseDouble(senseValue);
				
			}	else if (sensePredicate == null && senseValue == null )	{
				//boolean value given in plan
				log.debug("Boolean Value: " + senseName);
				return Boolean.parseBoolean(senseName);
			}
			
			if (sensePredicate.contains("!="))	{
				return value!=Double.parseDouble(senseValue);
			} else if (sensePredicate.contains("<="))	{
				return value<=Double.parseDouble(senseValue);
			}  else if (sensePredicate.contains(">="))	{
				return value>=Double.parseDouble(senseValue);
			}  else if (sensePredicate.contains("<"))	{
				return value<Double.parseDouble(senseValue);
			}  else if (sensePredicate.contains(">"))	{
				return value>Double.parseDouble(senseValue);
			}  else {
				return value==Double.parseDouble(senseValue);
			}  
		}
		
		
	}

}


