package posh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentManager.Agent;


import posh.POSHObjects.*;


import utils.logging.Log;
import utils.tuples.TupleFour;
import utils.tuples.TupleThree;



/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */


public class PlanBuilder {
	
	Log log;
	
	
	private TupleFour<String,String,List<Object>,List<TupleFour<String, List<Object>, String, Long>[]>> driveCollection;
	private Map<String, TupleThree<String, Long, List<Object>>> actionPatterns;
	private Map<String, TupleFour<String, Long, List<Object>, List<TupleFour<String,List<Object>, String, Integer>[]>>> competences;
	
	
	
	
	public PlanBuilder()	{
		log = new Log("PlanBuilder");
		log.debug("Created");
		
		driveCollection = null;
		actionPatterns = new HashMap<String, TupleThree<String,Long,List<Object>>>();
		competences = new HashMap<String, TupleFour<String, Long, List<Object>, List<TupleFour<String,List<Object>, String, Integer>[]>>>();
	}

	//Note: no name safety checking
	public void addCompetence(
			TupleFour<String, Long, List<Object>, List<TupleFour<String, List<Object>, String, Integer>[]>> competence) {
		String name = competence.First;
		log.debug("Added Competence");
		competences.put(name, competence);
		
	}

	//Note: no name safety checking
	public void addActionPattern(
			TupleThree<String, Long, List<Object>> pattern) {
		log.debug("Added AP");
		String name = pattern.First;
		actionPatterns.put(name, pattern);
		
	}

	public void setDriveCollection(
			TupleFour<String, String, List<Object>, List<TupleFour<String, List<Object>, String, Long>[]>> driveCollection) {
		log.debug("Added DC");
		log.debug(driveCollection.First);
		this.driveCollection = driveCollection;
	}
	
	private DriveCollection buildDriveCollection(Agent agent,
			HashMap<String, Competence> competences2,
			HashMap<String, ActionPattern> actionPatterns2) {
		
		String dcType = driveCollection.First;
		String dcName = driveCollection.Second;
		
		ArrayList<DrivePriorityElement> priorityElements = new ArrayList<DrivePriorityElement>();
		
				
		Trigger goal = buildGoal(agent,driveCollection.Third);
		
		for (int i = 0; i < driveCollection.Fourth.size(); i++)	{
			TupleFour<String,List<Object>,String,Long>[] priorityElement = driveCollection.Fourth.get(i);
			ArrayList<DriveElement> elementList = new ArrayList<DriveElement>();
			for (int j = 0; j < priorityElement.length; j++)	{
				TupleFour<String,List<Object>, String, Long> element = priorityElement[j];
				DriveElement driveElement = buildDriveElement(element, agent,competences2,actionPatterns2);
				driveElement.isLatched = false;
				
				for (int k = 0 ; k < driveElement.trigger.senses.length; k ++)	{
					Sense sense = driveElement.trigger.senses[k];
					elementList.add(driveElement);					
				}
								
			}

			//elementList toArray
			DriveElement[] elementArray = new DriveElement[elementList.size()];
			for (int k = 0; k < elementArray.length; k++)	{
				elementArray[k] = elementList.get(k);
			}
			
			priorityElements.add(new DrivePriorityElement(agent, dcName, elementArray));
		}
		//priorityelementList toArray
		DrivePriorityElement[] elementArray = new DrivePriorityElement[priorityElements.size()];
		for (int j = 0; j < elementArray.length; j++)	{
			elementArray[j] = priorityElements.get(j);
		}
		return new DriveCollection(agent,dcName,elementArray, goal);
	}
	
	
	public DriveCollection build(Agent agent)	{
		HashMap<String, Competence> competences = buildCompetenceStubs(agent);
		HashMap<String, ActionPattern> actionPatterns = buildActionPatternStubs(agent);
		competences = buildCompetences(agent, competences, actionPatterns);
		actionPatterns = buildActionPatterns(agent, competences, actionPatterns);
		
		return buildDriveCollection(agent, competences, actionPatterns);
	}
	
	
	private DriveElement buildDriveElement(
			TupleFour<String, List<Object>, String, Long> element,
			Agent agent,
			Map<String, Competence> competences2,
			Map<String, ActionPattern> actionPatterns2) {
		Trigger trigger = buildTrigger(agent, element.Second);
		CopiableElement triggerAble = getTriggerable(agent,element.Third,competences2, actionPatterns2);
		
		return new DriveElement(agent, element.First, trigger, triggerAble, element.Fourth);
	}

	
	/**
	 * Switched from pointer as in c# to return value
	 * @param agent
	 * @param competences2
	 * @param actionPatterns2
	 * @return
	 */
	private HashMap<String, Competence> buildCompetences(Agent agent,
			HashMap<String, Competence> competences2,
			HashMap<String, ActionPattern> actionPatterns2) {
		
		//changed to preserve subs
		//HashMap<String, Competence> returnCompetences = new HashMap<String, Competence>();
		HashMap<String, Competence> returnCompetences = competences2;
		
		Object[] competenceArray = this.competences.keySet().toArray();
		for (int i = 0 ; i < competenceArray.length; i++)	{
			String competence = (String) competenceArray[i];
			ArrayList<CompetencePriorityElement> priorityElements = new ArrayList<CompetencePriorityElement>();
			
						
			
			for (int j = 0; j < this.competences.get(competence).Fourth.size(); j ++)	{
				TupleFour<String, List<Object>, String, Integer>[] priorityElement = this.competences.get(competence).Fourth.get(j);
				List<CompetenceElement> elementList = new ArrayList<CompetenceElement>();
				for (int k = 0; k < priorityElement.length; k ++)	{
					TupleFour<String, List<Object>, String, Integer> element = priorityElement[k];
					elementList.add(buildCompetenceElement(element,agent,competences2,actionPatterns2));
				}
				//elementList toArray
				CompetenceElement[] elementArray = new CompetenceElement[elementList.size()];
				for (int k = 0; k < elementArray.length; k++)	{
					elementArray[k] = elementList.get(k);
				}
				priorityElements.add(new CompetencePriorityElement(agent, competence, elementArray));				
			}				
			//elementList toArray
			CompetencePriorityElement[] elementArray = new CompetencePriorityElement[priorityElements.size()];
			for (int j = 0; j < elementArray.length; j++)	{
				elementArray[j] = priorityElements.get(j);
			}
			Competence newComp = competences2.get(competence);
			newComp.setElements(elementArray);
			returnCompetences.put(competence, newComp);
		}
		return returnCompetences;
	}
	


	/**
	 * Switched from pointer passing as in c# to return value
	 * @param agent
	 * @param competences2
	 * @param actionPatterns2
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	private HashMap<String, ActionPattern> buildActionPatterns(Agent agent,
			HashMap<String, Competence> competences2,
			HashMap<String, ActionPattern> actionPatterns2) {
		
		//HashMap<String, ActionPattern> returnActionPatterns = new HashMap<String, ActionPattern>();
		HashMap<String, ActionPattern> returnActionPatterns = actionPatterns2;

		
		Object[] actionPatternArray = this.actionPatterns.keySet().toArray();
		for (int i = 0 ; i < actionPatternArray.length; i++)	{
			String actionPattern = (String) actionPatternArray[i];
			
			ArrayList<CopiableElement> elementList = new ArrayList<CopiableElement>();
			Object[] elementNames = this.actionPatterns.get(actionPattern).Third.toArray();
			for (int j = 0; j <elementNames.length -1; j++ )	{
				//deal with all but the last element
				if (elementNames[j].getClass().equals(String.class) &&  false)	{
					//senseacts not implemented
					
					//unreachable see if conditions
					log.error("Unsupported request for sense-act, will ignore");
					
				}	else if (elementNames[j].getClass().equals(TupleThree.class))	{
					//sense
					elementList.add(buildSense(agent,(TupleThree<String,String,String>)elementNames[j]));
				}	else	{
					//action
					elementList.add(getTriggerable(agent, (String)elementNames[j]));
				}
			}
			
			//last element can also be a competence
			if (elementNames[elementNames.length-1].getClass().equals(String.class) && false)	{
				//senseacts not yet implemented
					
				//unreachable see if conditions
				log.error("Unsupported request for sense-act, will ignore");
					
			}	else if (elementNames[elementNames.length-1].getClass().equals(TupleThree.class))	{
				//sense
				elementList.add(buildSense(agent,(TupleThree<String,String,String>)elementNames[elementNames.length-1]));
			}	else	{
				//action
				elementList.add(getTriggerable(agent, (String)elementNames[elementNames.length-1],competences2));
			}
			
			//elementList toArray
			CopiableElement[] elementArray = new CopiableElement[elementList.size()];
			for (int j = 0; j < elementArray.length; j++)	{
				elementArray[j] = elementList.get(j);
			}
			ActionPattern newAP = actionPatterns2.get(actionPattern);
			newAP.setElements(elementArray);
			returnActionPatterns.put(actionPattern, newAP);
			
		}
		return returnActionPatterns;
		
	}
	
	
	private HashMap<String, Competence> buildCompetenceStubs(Agent agent) {
		HashMap<String,Competence> competenceStubs = new HashMap<String,Competence>();
		
		
		
		Object[] keyArray = competences.keySet().toArray();
		for (int i = 0; i < this.competences.size(); i ++)	{
			String key = (String) keyArray[i];
			TupleFour<String, Long, List<Object>, List<TupleFour<String, List<Object>, String, Integer>[]>> value = competences.get(key);
			Trigger goal = buildGoal(agent, value.Third);
			competenceStubs.put(key, new Competence(agent, key, new CompetencePriorityElement[] {}, goal));
		}
		return competenceStubs;
	}
	
	private HashMap<String, ActionPattern> buildActionPatternStubs(Agent agent) {
		HashMap<String,ActionPattern> patternStubs = new HashMap<String,ActionPattern>();
		
		Object[] keyArray = actionPatterns.keySet().toArray();
		for (int i = 0; i < this.actionPatterns.size(); i ++)	{
			String key = (String) keyArray[i];
			TupleThree<String, Long, List<Object>> value = actionPatterns.get(key);
			patternStubs.put(key, new ActionPattern(agent, key, new CopiableElement[] {}));
		}
		return patternStubs;
	}
	
	
	private CompetenceElement buildCompetenceElement(
			TupleFour<String, List<Object>, String, Integer> element,
			Agent agent,
			Map<String, Competence> competences2,
			Map<String, ActionPattern> actionPatterns2) {
		Trigger trigger = buildTrigger(agent,element.Second);
		CopiableElement triggerable = getTriggerable(agent, element.Third, competences2, actionPatterns2);


		return new CompetenceElement(agent, element.First, trigger, triggerable, element.Fourth);
	}
	
	
	private Trigger buildTrigger(Agent agent, List<Object> trigger) {
		return buildGoal(agent, trigger);
	}
	
	
	
	@SuppressWarnings("unchecked")
	private Trigger buildGoal(Agent agent, List<Object> goal) {
		ArrayList<Sense> senses = new ArrayList<Sense>();
		if (goal == null || goal.size() == 0)	{
			return null;
		}
		
		for (int i = 0; i < goal.size(); i ++)	{
			Object sense = goal.get(i);
			if (sense.getClass().equals(String.class))	{
				log.error("Sense acts not supported");
			}	else if (sense.getClass().equals(TupleThree.class))	{
				senses.add((Sense) buildSense(agent, (TupleThree<String,String,String>)sense));
			}
		}
		//sense to array
		Sense[] sensesArray = new Sense[senses.size()];
		for (int j = 0; j < sensesArray.length; j++)	{
			sensesArray[j] = senses.get(j);
		}
		return new Trigger (agent, sensesArray);
	}
	
	
	private CopiableElement buildSense(Agent agent,
			TupleThree<String, String, String> sense) {
		
		return new Sense(agent,sense.First,sense.Second, sense.Third);
	}
	
	
	
	
	/**
	 * Had to be modified from original as no behaviour library currently implemented
	 * @param agent
	 * @param string
	 * @param competences2
	 * @param actionPatterns2
	 * @return
	 */
	private CopiableElement getTriggerable(Agent agent, String string, Map<String, Competence> competences2, Map<String,ActionPattern> actionPatterns2) {
		
		Action element;
		
		if (actionPatterns2 != null && actionPatterns2.containsKey(string))	{
			//ap
			return actionPatterns2.get(string);
		}	else if (competences2 != null && competences2.containsKey(string)) 	{
			//competence
			return competences2.get(string);
		}	else	{
			//action
			element = new Action(agent, string);
		}
		
		
		if ((competences2 != null && competences2.containsKey(string)) || (actionPatterns2 != null && actionPatterns2.containsKey(string)))	{
			log.error("Name of action " + string + "also held by other competence / action pattern");
		}
		
		return element;
	}
	
	
	private CopiableElement getTriggerable(Agent agent, String string,Map<String, Competence> competences2) {

		return getTriggerable(agent, string, competences2, null);
	}

	private CopiableElement getTriggerable(Agent agent, String string) {

		return getTriggerable(agent, string, null, null);
	}

	

}
