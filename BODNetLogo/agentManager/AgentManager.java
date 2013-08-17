package agentManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.nlogo.agent.Turtle;
import org.nlogo.api.CompilerException;
import org.nlogo.app.App;

import utils.logging.Log;
import utils.tuples.TupleFour;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class AgentManager {

	public Log log;
	private boolean TICKSON = true;
	private boolean DEBUGMODE = false;
	private boolean AUTODEATH = true;
	
	
	//Not currently configured by the user- just auto set
	private boolean NATURE = true;
	
	private int highestAgentIDYet = 0;
	
	public static boolean RUNNING = true;
	public static boolean PAUSED = true;
	public static int noTicks = -1;
	
	public AgentManager(TupleFour<String[], Integer, String, String[]>[] agents, int numberOfInitialAgents, boolean TICKSON, boolean DEBUGMODE, boolean AUTODEATH) throws Exception	{
				
		log = new Log("Agent Manager ");
		
		this.TICKSON = TICKSON;
		this.DEBUGMODE = DEBUGMODE;
		this.AUTODEATH = AUTODEATH;
		
		this.highestAgentIDYet = numberOfInitialAgents-1;
		
		//Define agent attributes
		log.info("Setting agent attributes");
		for (int i = 0; i < agents.length; i ++)	{
			log.info("Defining breed attributes");
			AttributeStore.addNewAgentset(agents[i].Second, agents[i].Fourth);
		}
		
		
				
		//create agent array
		ArrayList<Agent> agentArray = new ArrayList<Agent>();
		
		//Create POSH agents
		int agentCount = 0;
		log.info("Creating POSH Agents");
		for (int i = 0; i < agents.length; i ++)	{
			//import plan for agent class
			log.info("Importing Plan: " + "Plan1");
			String plan = getPlan(agents[i].Third);
			
			for (int j = 0; j < agents[i].Second; j ++)	{				

				log.info("Creating agent: " + agentCount);
				agentArray.add(new Agent(plan, agentCount));
				agentCount ++;
			}
			
		}
				
		log.info("Created All Agents");		
		
		//Run the plans		
		log.info("Ready To Run Plans");
		
		int tickCounter = 0;
		
		//outer control loop, will only exit this when exit 
		while	(RUNNING)	{
			
			//inner loop, will exit this to pause but not stop the run
			
			while (!PAUSED)	{
				tickCounter++;
				if (agentArray.size() == 0)	{
					log.info("All Agents Done");
					RUNNING = false;
					break;
				}
				for (int i = 0; i < agentArray.size(); i ++)	{
					//check if agent is still living in netlogo
					try {
						Turtle x = (Turtle) App.app().report("Turtle " + agentArray.get(i).getAgentID());
					} catch (Exception e){
						//agent not living in NetLogo
						//remove
						log.info("Turtle " + agentArray.get(i).getAgentID() + " is no longer active in the NetLogo model");
						agentArray.remove(i);
						continue;
						
					}
					
					if (agentArray.get(i).followDrive() == 2)	{
						log.info("Agent " + agentArray.get(i).getAgentID() + " Finished Running");
						if (AUTODEATH)	{
							try {
								log.info("Killing Turtle " + agentArray.get(i).getAgentID());
								App.app().command("ask turtle " + agentArray.get(i).getAgentID() + " [die]");
							} catch (CompilerException e) {
							}
						}
						agentArray.remove(i);
					}
					
					if (DEBUGMODE)	{
						try {
							Thread.sleep(1000);
						} catch	(Exception e)	{				
						}
					}
				}
				checkForNewSpawns(agentArray, agents);
				if (TICKSON)	{
					try {
						App.app().command("tick");
					} catch (CompilerException e) {
						log.error("UNABLE TO TICK");
						e.printStackTrace();
					}
				}
				if (NATURE)	{
					try {
						App.app().command("nature");
					} catch (Exception e)	{
						//no nature
						NATURE = false;
					}
				}				
				if (noTicks != -1 && (tickCounter >= noTicks))	{
					noTicks = -1;
					PAUSED = true;
				}
			}
			tickCounter = 0;
			//simulation is not running so pause for 150ms on each cycle
			try {
				Thread.sleep(150);
			} catch	(Exception e)	{				
			}
		}
	}
	
	
	private void checkForNewSpawns(ArrayList<Agent> agentArray, TupleFour<String[], Integer, String, String[]>[] agents)	{
		log.debug("Checking for new spawns");
		
		try {
			int currentNumAgents = (int) (double) Double.parseDouble(""+App.app().report("count turtles"));
			
			if (currentNumAgents > agentArray.size())	{
				//there must have been some agents spawned
				
				while (currentNumAgents > agentArray.size())	{
					
					//new highest agent ID
					highestAgentIDYet++;
					
					
					//get agents type
					//first check for nobody values e.g. an agent was spawned and killed before this function was called
					for (int i = 0; i < agents.length; i++)	{
						//throws a clastcastException if 'nobody' is returned
						org.nlogo.api.Agent agent = null;
						try {
							agent = (org.nlogo.api.Agent) App.app().report("turtle "+ highestAgentIDYet);
						} catch (ClassCastException c)	{
							continue;
						}
						String reportedBreed = ""+agent;
						reportedBreed = reportedBreed.split(" ")[0];
						if (reportedBreed.equalsIgnoreCase(agents[i].First[0]))	{
							String plan = getPlan(agents[i].Third);
							log.info("Creating agent: " + highestAgentIDYet);
							agentArray.add(new Agent(plan,highestAgentIDYet));
							
							// need to add the new agent to the attribute store
							//TODO this method is likely inefficient for large numbers of agents, need to find a better way
							
							//cant just use total number of agents + 1 consider rare case where an agent is spawned and killed 
							//before this fcn is called, netlogo will have an ID for it so we also need to consider it to keep count accurate
							AttributeStore.addNewAgentset(highestAgentIDYet+1 - AttributeStore.getTotalNumberAgentsInStore(), agents[i].Fourth);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String getPlan(String string) throws Exception {
		String result = "";
		
		try {
			FileReader fr = new FileReader(string);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while(true)	{
				line = br.readLine();
				if (line == null)	{
					break;
				}
				result = result + line + '\n';
				line = null;
			}
			br.close();
			fr.close();
			}	catch (Exception e)	{
				log.error("Unable to read file");
				throw new Exception();
			}
			
		
		return result;
		}
	
}
