package agentManager;

import java.util.ArrayList;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

/**
 * AttrbiuteStore Maintains a list of attributes a particular type of agent can have in netlogo
 * as only able to retrieve via index
 * @author Michael Brooks
 *
 */

public class AttributeStore {
	
	//number of attributes stored by netlogo that we are not interested in
	static int baseCount = 12;
	
	
	//List of different Breeds and the quantity for each index matches to agentAttributes
	static ArrayList<Integer> agentTypes = new ArrayList<>();
	
	//ArrayList of String ArrayLists, each arraylist is list of attributes
	static ArrayList<Object> agentAttributes = new ArrayList<>();
	
	
	/**
	 * Add a new record to say that there are x agents for index n, index n has y attributes
	 * 
	 */
	public static void addNewAgentset(int qty, String[] attributes)	{
		agentTypes.add(qty);
		
		ArrayList<String> newAgentAttributes = new ArrayList<>();
		for (int i = 0; i<attributes.length; i++)	{
			newAgentAttributes.add(attributes[i]);
		}
				
		agentAttributes.add(newAgentAttributes);
	}
	
	/**
	 * returns the integer index for the given attribute string
	 * NOTE: 0 indicates attribute not found
	 * @param AgentID
	 * @param attributeName
	 * @return
	 */
	public static int getAttributePosition(int AgentID, String attributeName)	{
		//To find correct index value for agentAttributes
		int currentAgentCount = 0;
		int agentTypeIndex = 0;
		while (currentAgentCount <= AgentID)	{
			agentTypeIndex++;
			currentAgentCount = currentAgentCount + agentTypes.get(agentTypeIndex-1);			
		}
		
		//get the agentAttributes ArrayList at the index value just worked out
		@SuppressWarnings("unchecked")
		ArrayList<String> getAgentAttributes = (ArrayList<String>) agentAttributes.get(agentTypeIndex-1); 
		
		//create variable for the return value
		int returnValue = 0;

		//caculate the attributes index value
		for (int i = 0; i<getAgentAttributes.size(); i++)	{
			if (getAgentAttributes.get(i).equalsIgnoreCase(attributeName)) 	{
				returnValue = i+1+baseCount;
				break;
				
			}
		}
		
		return returnValue;
	}
	
	/**
	 * The total number of agents accounted for in the attribute store
	 * as agents are never removed this can be used as the total number of agents added up to this point
	 * @return
	 */
	public static int getTotalNumberAgentsInStore()	{
		int agentCount = 0;
		for (int i = 0; i < agentTypes.size(); i ++)	{
			agentCount =agentCount + agentTypes.get(i);
		}
		return agentCount;
	}
}
