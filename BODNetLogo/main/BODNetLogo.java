package main;





import gui.MainUI;
import gui.SimulationController;
import gui.StartScreen;

import java.io.BufferedReader;
import java.io.FileReader;

import org.nlogo.app.App;

import posh.PlanBuilder;
import posh.lapParser.LapParser;
import simulationManager.LoadConfigFile;
import simulationManager.SimManager;
import utils.logging.Log;
import utils.logging.LogGui;
import utils.tuples.TupleFour;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

/*
 * This is the main class for BODNetLogo, it starts the program and collects various inputs
 * 
 * It also acts as a top level catcher for errors as a last resort for errors not handled elsewhere, this should
 * prevent any 'ungraceful' exits
 */

public class BODNetLogo {

	//Various variables to store the information given by the user
	public static boolean waitingForConfigInfo = true;
	public static TupleFour<String[], Integer, String, String[]>[] agents;
	public static String nLogoModel;
	public static boolean RUNSETUP;
	public static boolean TICKSON;
	public static boolean DEBUGMODE;
	public static boolean AUTODEATH;
	public static String fileLocation = null;

	private static Log log;

	public static void main(String[] argv) {
		//Start the logger gui
		LogGui logGui = new LogGui();

		//Create a logger for this class
		log = new Log("MAIN", logGui);
		log.info("Started logging");

		//Create the start screen
		new StartScreen();

		//Wait until users have made a choice on the start screen
		while (fileLocation == null)	{
			try {
				Thread.sleep(100);
			}	catch (Exception e)	{
			}
		}

		//configure the next UI screen
		MainUI ui = new MainUI();
		//if a config file was given, then populate the elements for the user
		if (!fileLocation.equalsIgnoreCase(""))	{
			//set the elements in the UI
			if (LoadConfigFile.loadFile(fileLocation))	{
				ui.setValues(agents, nLogoModel, RUNSETUP, TICKSON, DEBUGMODE, AUTODEATH);
			}
		}

		//outer loop for the MainUI, allows checking of user input
		//only basic checking currently, TODO expand to all elements
		while (true)	{
			//waiting for the user to finish sim config
			waitingForConfigInfo = true;

			ui.setVisible(true);


			while (waitingForConfigInfo)	{
				try {
					Thread.sleep(100);
				}	catch (Exception e)	{

				}
			}

			//User has clicked load simulation

			//Start a new thread with the SimulationController window
			Runnable r = new SimulationController();
			Thread thread = new Thread(r);
			thread.start();

			//Hide the main UI
			ui.setVisible(false);

			//User input checking
			checkUserInputs();

			//Passed the user input checking

			//Start NetLogo
			log.info("Starting NetLogo");
			try {
				startNetLogo();
			} catch(Exception ex) {
				new Log("MAIN").error("Could not start NetLogo, please ensure you have selected a valid .nLogo file ");
				System.exit(0);
				continue;
			}

			//Start Sim Manager
			try {
				new SimManager(RUNSETUP, TICKSON, DEBUGMODE, AUTODEATH, agents);
				//Started

				//If SimManager stops
				while (true)	{
					//do nothing until exit is called
					try {
						Thread.sleep(100);
					}	catch (Exception e)	{
						System.exit(0);
					}
				}
			} catch (Exception e) {
				log.error("Error whilst running simulation: Unable to start simulation manager.");
				e.printStackTrace();
				try {
					Thread.sleep(100);
				}	catch (Exception f)	{
					System.exit(0);
				}
			}
		}
	}


	/**
	 * Checks the inputs given by the user, quits the program with error if bad input
	 */
	private static void checkUserInputs()	{
		//Poor 'bad' input handling currently implemented, TODO improve, possibly, reload mainUI?

		if (nLogoModel == null || nLogoModel.equalsIgnoreCase(""))	{
			log.error("Incorrect input, please ensure you enter the location of the netlogo model");
			System.exit(0);
		}

		try {
			int a = agents.length;
			if (a>0)	{
			}
		}	catch (Exception e)	{
			log.error("No agents specified");
			System.exit(0);
		}

		//check all the lap files given can be parsed correctly
		for (TupleFour<String[], Integer, String, String[]> agent : agents) {
			try 	{
				LapParser lp = new LapParser();
				PlanBuilder builder = lp.parse(getPlan(agent.Third));
			} catch (Exception e)	{
				log.error("Invalid plan specified for agent: "+agent.First[1]);
				System.exit(0);
			}
		}

	}
	//for inputChecking only, actual plan reading is taken in agentManager
	private static String getPlan(String string) throws Exception {
		String result = "";

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
		return result;
	}



	public static void startNetLogo() throws Exception	{
		App.main(new String[0]);
		java.awt.EventQueue.invokeAndWait(runSim());
	}


	private static Runnable runSim()	{
		Runnable run = new Runnable() {
			@Override
			public void run() {
				try {
					App.app().open(nLogoModel);
				}		catch(java.io.IOException ex) {
					ex.printStackTrace();
				}}
		};
		return run;
	}










}
