package agentManager;

import posh.DriveCollection;
import posh.PlanBuilder;
import posh.Timer;
import posh.POSHObjects.ElementCollection;
import posh.POSHObjects.FireResult;
import posh.lapParser.LapParser;
import utils.logging.Log;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class Agent {

	int agentID;

	Log log;
	
	public int PLANFINISHED = 2;
	public int DRIVEFOLLOWED = 0;
	public int DRIVEWON = 1;
	public int DRIVELOST = -1;
	
	private Timer timer;
	
	public DriveCollection dc;

	
	
	
	public Agent(String plan, int agentID)	{
		this.agentID =agentID;

		log = new Log("Agent " + agentID);
		setTimer(new Timer());
		loadPlan(plan);
		
	}

	public void setTimer(Timer timer)	{
		log.debug("Setting timer");
		this.timer = timer;
	}
	
	public Timer getTimer(Timer timer)	{
		return timer;
	}
	
	public int followDrive()	{
		FireResult result;
		
		log.debug("Running drive collection");
		
		while (true)	{
		result = dc.fire(agentID);
		timer.LoopEnd();
		
		if (result.isFinishedPlan())	{
			return PLANFINISHED;
		} else	if (result.continueExecution()){
			return DRIVEFOLLOWED;
		}	else if ( result.nextElement() != null && result.nextElement().getClass().equals(ElementCollection.class))	{
			return DRIVEWON;
		}	else	{
			return DRIVELOST;
		}
		}
	}
	
	public void loadPlan(String plan){
		log.debug("Building Plan");
		LapParser lp = new LapParser();	
		PlanBuilder builder;
		try {
			builder = lp.parse(plan);
			dc = builder.build(this);
		} catch (Exception e) {
		}
	}
	
	public String getAgentID()	{
		return "" + agentID;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
}