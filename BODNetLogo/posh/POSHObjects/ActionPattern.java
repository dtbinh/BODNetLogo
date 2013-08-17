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

public class ActionPattern extends ElementCollection {
	
	private int elementID;
	private CopiableElement[] elements;
	
	private Log log;
	
	public ActionPattern(Agent agent, String patternName,
			CopiableElement[] copiableElements) {
		super(agent);
		
		name = patternName;
		this.elements = copiableElements;
		this.elementID = 0 ;
		
		log = new Log("Action Pattern "+ patternName +" for agent " + agent.getAgentID());
		log.debug("Created");
	}

	
	public FireResult fire()	{
		log.debug("Fired");
		CopiableElement element = elements[elementID];
		boolean isAction = false;
		boolean isSense = false;
		
		try	{
			Action ac = (Action) element;
			isAction = true;
		}	catch	(Exception e)	{
			try 	{
				Sense se = (Sense) element;
				isSense = true;
			} catch (Exception f)	{
				//must be a competence
			}
		}
		
		if (isAction || isSense)	{
			boolean result;
			if (isAction)	{
				result = ((Action)element).fire();
			}	else	{
				result = ((Sense)element).fire();
			}
			
			if (!result)	{
				log.debug("Action/Sense Failed " + element.getName() );
				elementID = 0;
				return new FireResult(false, null);
			}
			
			//check if we've just fired the last action
			elementID++;
			if (elementID >= elements.length)	{
				elementID = 0;
				return new FireResult(false, null);
			}
			return new FireResult(true, null);
		}	else	{
			// must be a competence
			elementID = 0;
			return new FireResult (true, element);
		}
		
	}
	
	public void setElements(CopiableElement[] elementArray) {
		this.elements = elementArray;
		reset();
	}
	
	public void reset()	{
		log.debug("Reset");
		this.elementID = 0;
	}
}












