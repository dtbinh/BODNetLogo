package simulationManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.BODNetLogo;


import utils.logging.Log;
import utils.tuples.TupleFour;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

/**
 * Very basic config file reader, reads the file and returns a data structure
 *
 */

public class LoadConfigFile {

	private static ArrayList<String> configFile = new ArrayList<>();
	
	
	@SuppressWarnings("unchecked")
	public static boolean loadFile(String fileLocation) {
		try {
			//load the file
			FileReader fr = new FileReader(fileLocation);
			BufferedReader br = new BufferedReader(fr);
			//remove comments
			removeComments(br);
			br.close();
			//count breeds specified
			BODNetLogo.agents = new TupleFour[countNumberAgents()];
			//get the boolean values
			getBooleanVals();
			//get the nlogo locations
			getFileLocation();
			int agentsFoundSoFar = 0;
			while (configFile.size() > 0)	{
				getAgent(agentsFoundSoFar);
				agentsFoundSoFar++;
			}
			return true;
		} catch (Exception e)	{
			new Log("Config File Loader").error("Error Parsing File");
			return false;
		}
	}


	private static int countNumberAgents() {
		int result = 0;
		
		for (int i = 0; i < configFile.size(); i ++)	{
			if (configFile.get(i).equalsIgnoreCase("<"))	{
				result++;
			}
		}
		
		return result;
	}


	private static void getAgent(int agentsFoundSoFar) throws Exception {
		if (!(configFile.get(0).equalsIgnoreCase("<")))	{
			//bad file
			throw new Exception();
		}
		//remove the <
		configFile.remove(0);
		
		//get the plural name
		String plural = configFile.get(0);
		configFile.remove(0);
		
		//get the single name
		String single = configFile.get(0);
		configFile.remove(0);
		
		//get the lap file location
		String lap = configFile.get(0);
		configFile.remove(0);
		
		if (!(configFile.get(0).equalsIgnoreCase("[")))	{
			//bad file
			throw new Exception();
		}
		configFile.remove(0);
		
		ArrayList<String> attributes = new ArrayList<>();
		while (configFile.get(0).charAt(0)!=']')	{
			attributes.add(configFile.get(0));
			configFile.remove(0);
		}
		configFile.remove(0);
		
		if (!(configFile.get(0).equalsIgnoreCase(">")))	{
			//bad file
			throw new Exception();
		}
		configFile.remove(0);
		
		String [] attributesArray = new String[attributes.size()];
		for (int i = 0; i < attributes.size(); i ++)	{
			attributesArray[i] = attributes.get(i);
		}
		
		BODNetLogo.agents[agentsFoundSoFar] = new TupleFour<String[], Integer, String, String[]>
			(new String[]{single, plural}, 0, lap, attributesArray);
		
		
	}


	private static void getFileLocation() {
		BODNetLogo.nLogoModel = configFile.get(0);
		configFile.remove(0);
	}


	private static void getBooleanVals() {
		BODNetLogo.RUNSETUP = Boolean.parseBoolean(configFile.get(0));
		configFile.remove(0);
		BODNetLogo.TICKSON = Boolean.parseBoolean(configFile.get(0));
		configFile.remove(0);
		BODNetLogo.DEBUGMODE = Boolean.parseBoolean(configFile.get(0));
		configFile.remove(0);
		BODNetLogo.AUTODEATH = Boolean.parseBoolean(configFile.get(0));
		configFile.remove(0);
	}


	private static void removeComments(BufferedReader br) throws IOException	{
		String currentLine = "";
		while ((currentLine=br.readLine())!=null)		{
			if (currentLine.charAt(0)=='#')	{
				//comment line discard
				
			}	else	{
				configFile.add(currentLine);
			}
		}
	}
	
}
