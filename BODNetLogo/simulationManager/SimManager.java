package simulationManager;



import org.nlogo.api.Agent;
import org.nlogo.api.CompilerException;
import org.nlogo.app.App;

import agentManager.AgentManager;

import utils.logging.Log;
import utils.tuples.TupleFour;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class SimManager {
	Log log;
	
	TupleFour<String[]/*BreedName{Single, Plural}*/, Integer/*QTY*/, String /*Plan Location*/, String[] /*breed attributes*/>
	[] agents;
	
	public SimManager(boolean RUNSETUP, boolean TICKSON,boolean DEBUGMODE, boolean AUTODEATH, TupleFour<String[], Integer, String, String[]> [] agents) throws Exception	{
		
		
		//Create Logger
		log = new Log("SIM MANAGER");
		
		
		//gather agent details
		log.info("Getting Turtle Details");
		 this.agents = agents;
		
		
		//Configure NetLogo
		log.info("Configuring NetLogo");
				
		//run Setup
		if (RUNSETUP)	{
			log.info("Running the NetLogo 'setup' Command");
			try	{
				App.app().command("setup");
			}	catch	(Exception e)	{
				log.error("Error Configuring NetLogo. Please use the built in check function to ensure your NetLogo code is correct.");
				throw new Exception();
			}
		}
		
		//collect initial number of agents from NetLogo
		log.info("Collecting turtle counts from NetLogo");
		this.agents = countNetLogoTurtles(agents);
		
		//calculate total num of initial agents
		int totalInitialAgents = 0;
		for (int i = 0; i < agents.length; i ++)	{
			totalInitialAgents = totalInitialAgents +agents[i].Second;
		}
		
		//run agent manager
		new AgentManager(this.agents, totalInitialAgents, TICKSON, DEBUGMODE, AUTODEATH);
		
	}
	
	
	
	
	public TupleFour<String[], Integer, String, String[]> [] countNetLogoTurtles(TupleFour<String[], Integer, String, String[]> [] agentDetails) throws Exception	{
		
		TupleFour<String[], Integer, String, String[]> [] returnArray = agentDetails;
		
		
		int typesOfAgent = 0;
		int noAgentsFound = 0;
		while(true)	{
			try {
				boolean agentMatched = false;
				for (int i = 0; i < agentDetails.length; i++)	{
					//throws a clastcastException if 'nobody' is returned
					Agent agent = (Agent) App.app().report("turtle "+ noAgentsFound);
					String reportedBreed = ""+agent;
					reportedBreed = reportedBreed.split(" ")[0];
					log.info(reportedBreed + " vs "+ agentDetails[i].First[0]);
					if (reportedBreed.equalsIgnoreCase(agentDetails[i].First[0]))	{
						//found matching breed
						typesOfAgent ++;

						int noThisBreed = (int)((double) App.app().report("count " + agentDetails[i].First[1]));
						noAgentsFound = noAgentsFound + noThisBreed;
						TupleFour<String[], Integer, String, String[]>  tempAgent =
								new TupleFour<String[], Integer, String, String[]> (agentDetails[i].First, noThisBreed, agentDetails[i].Third, agentDetails[i].Fourth);
								
						returnArray[i]=tempAgent;
						agentMatched = true;
						break;
					}					
				}
				
				if (!agentMatched)	{ 
					//	didnt find a bod match for the netlogo agent
					throw new Exception();
				}
				
			} catch (CompilerException c) {
				//this should never happen
				log.error("UNHANDLED EXCEPTION IN SIMMANAGER");
				throw new Exception();
			} catch (ClassCastException b)	{
				//no more types of agent
				if (typesOfAgent == 0)	{
					//error didnt find any agents
					log.error("Could not indentify any breed matches, please ensure you have properly specified your breeds in NetLogo and BODNetLogo.");
				}	else	{
					log.info("Successfully indentified: "+ typesOfAgent+ ", different breeds, and a total of: " + noAgentsFound + " turtles");
				}
				break;
			} catch (Exception e)	{
				e.printStackTrace();
				String reportedBreed = "";
				try {
					reportedBreed = ""+App.app().report("turtle "+ noAgentsFound);
				} catch (CompilerException e1) {
					//this should never happen
					log.error("UNHANDLED EXCEPTION IN SIMMANAGER");
					throw new Exception();
				}
				reportedBreed = reportedBreed.split(" ")[0];
				log.error("Could not find BOD specification for all agents, found "+typesOfAgent+ " (or more) NetLogo Breeds, but only " +agentDetails.length+" BOD specifications, " +
						"Missing agent: " + reportedBreed);
				
			}
		}
					
		return returnArray;
	}
	
	
}

  
