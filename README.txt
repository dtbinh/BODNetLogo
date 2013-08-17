###############################################################################################################################
###############################################################################################################################
This is a readme file for part of the BODNetLogo program, please note that BODNetLogo, whilst fully functional,
is not a finished program, you may find bugs or errors that are not handled properly and the user interface is not
particularly robust at handling incorrect information, so if you try to break it, you probably will. 
Please see the user manual (below) for full details on how to use the program and for further support or bug reports please 
contact the author at bodnetlogo@mhbrooks.com

This is the root level directory for BODNetLogo. 


###############################################################################################################################
###############################################################################################################################
For information on the source code of the project please see the 'src' folder.


###############################################################################################################################
###############################################################################################################################
Models, including the 'example' model can be found in the 'models' folder. For information on how to use those models, 
please refer to the README file inside the 'example model'


###############################################################################################################################
###############################################################################################################################
To RUN BODNetLogo, there are two methods:
	~ Navigate to this folder with 'cmd' or terminal and enter java -jar BODNetLogo.jar
	~ BODNetLogo can be run be simply double clicking the BODNetLogo.jar file. 

		
###############################################################################################################################
###############################################################################################################################		
BODNetLogo USER GUIDE:

NOTE: hover the cursor over the elements in the BODNetLogo GUI for tooltips if you're stuck

Section 1
	After running the jar file (see above) you are given the option to:
		CREATE NEW SIMULATION - Choose this if you want to create your own simulation
		LOAD EXISTING SIM - Choose this if you want to run a simulation that has a '.bnconfig' file. e.g. the example model included

Section 2
	Either option will bring you to the next screen. If you loaded a config file the elements should already be populated.
	From this screen you have two options:
		Add/edit information about the simulation (see Section 3)
		If you're happy with the information that has been specified, click Load Simulation (see Section 5)
		
Section 3
	To add or edit existing information in the simulation config window:
		1.Choose your pre-specified .nlogo simulation file (see Section 6)
		2.Specify your breeds (see Section 4)
			To edit an existing breed, select it in the Breeds window and click edit
		3.Options (If you're not sure what these do, leave them as default):
			Run Setup - BODNetLogo will run the 'setup' function inside NetLogo whilst loading the model
			Ticks On - At the end of each full round of agents BODNetLogo will send the 'tick' command to NetLogo
			Debug Mode - this will add a short delay after each agent's turn, useful for debugging
			Auto Death - Once an agent meets its Drive Collection goal, BODNetLogo will send a DIE command to NetLogo
				removing that agent from the simulation
		4.Print Options
			Specify which options you want printing to the command line, note Debug prints A LOT of information, only enable if you need it
		5.File Save Options
			These have been disabled in this version due to a bug, contact the author if this is something you really need		
		
Section 4
	Specifying your breeds (NOTE even if you do not intend to use a breed in the current simulation you must specify it in BODNetLogo, even if the qty in NetLogo is 0):
		1.Enter the plural breed name, THIS MUST MATCH the information given in your nlogo file 
			e.g. NetLogo code : breed [sheep a-sheep]
			plural name = sheep
		2.Enter the single breed name, THIS MUST MATCH the information given in your nlogo file 
			e.g. NetLogo code : breed [sheep a-sheep]
			plural name = a-sheep
		3.Specify the location of the lap file for this breed
		4.Specify the breed attributes, THESE MUST MATCH the information in your nlogo file. BOTH name AND order
			e.g. NetLogo code: sheep-own [standingOnGrass energy] 
			Attributes: 
				standingOnGrass
				energy
			NOTE the order MUST match or you will have errors. 
			NOTE it is advised that you do not use 'turtles-own' for any attributes, instead use a breed specific specification
			NOTE even if you do not intend to use a particular attribute, if it is specified in NetLogo it MUST be specified in BODNetLogo
			NOTE if you want to confirm the order of the attributes, you can right click on an agent in the visual view (try typing setup in the command center if the visual view is blank) and choose inspect agent-x, you will then see a list of all the attributes that breed possesses. You SHOULD ignore the first 13 attributes (everything up to 'pen-mode').
		5.You can use up/down/remove to reorder the attributes you have already specified.
		6.Once you have finished specifying a breed click done
	

Section 5
	Running a simulation, once you click load simulation BODNetLogo will do four things, you should wait until it has finished all four before continuing:
		1. it will load the simulation controller window (run/pause)
		2. it will load NetLogo
		3. once NetLogo is fully loaded it will run the 'setup' command - you should see the visual view populate all your agents and patches
		4. it will load all your agents in BODNetLogo, including their plans. NOTE if you have a lot of agents, or agents with very complicated lap plans you should use the command line so that you can see when the plans have finished loading, once finished the command line will read Create All Agents and Ready to Run plans.
		If you only have a few agents this should only take a second or two
	Once everything is loaded:
		click run model in the simulation controller and the model should start
			NOTE: performance of the complex POSH scheduler will be noticeably slower than NetLogo
		OR enter a number in the text box and click Run x Ticks if you want to run a specified number of ticks
		click pause to stop the model running
			NOTE: pause will stop the model at the END of the agent cycle, if you have a lot of agents this may take a short while
	If you want to edit something in your NetLogo model you MUST
		1.Pause the Model (if it's running)
		2.Make your changes in NetLogo
		3.Save the model in NetLogo
		4.CLOSE BODNetLogo and restart it.
		this is because BODNetLogo gathers certain information from NetLogo in the loading process and so if you make changes to NetLogo wihtout restarting BODNetLogo there will be discrepancies between the two, likely causing an error. The author is aware this is not ideal and is working on a method to allow a 'quick restart' after changes are made in NetLogo
		If you intend to use a model a lot you can create a .bnconfig file so you dont have to specify all the options repeatedly (see Section 8)
	
Section 6	
	Creating a .nlogo file
		A large part of the code for the simulation still goes into NetLogo so it is only possible to provide a brief explanation here. 
			If you are new to BOD it is recommended you read some of the literature to get an idea of the concepts. To find the home pages you should search online for 'Joanna Bryson', 'Joanna Bryson Bath University', or 'Joanna Bryson BOD'.
		You can also look at the including example for a good idea and baseline for your NetLogo code.
		Your NetLogo file should include
		1.Any graphical elements you want, e.g. monitors or graphs
		2.You should copy the content of the code tab in the 'template.nlogo' file to form the base of your new simulation
		3.The sections in the template should include the following:
			a.BREEDS
				The specifcation of the breeds and their attributes
			b.SETUP
				The setup function and any other startup code
			c.NATURE
				The (optional) nature function
			d.BEHAVIOUR LIBRARY START
				-All behaviours are made up of senses and actions and have their own 'module' or section.
		
		More information on all this sections can be found by looking at the provided example model
		
	
Section 7
	Creating a .lap file, if you are new to POSH and BOD in general you should look into the literature which describes them.
		(See the start of Section 6). 
	lap files can be created either manually, or with the ABODE editor, see the web pages of Joanna Bryson (as above) for the most up-to-date version of ABODE
	
	
Section 8 
	Creating a .bnconfig file.
	.bnconfig or BodNetlogoCONFIG are very simple files that record the setup for a BODNetLogo simulation. They is currently no way to automate the generation of these files but they are very simple to create. Please see the included wolfSheep.bnconfig file for the structure of the file. There are plenty of comments (lines beginning with #) to describe what each section means.
	

FAQ
	I loaded a config file, but the elements in the Simulation Config window are blank.
		If BODNetLogo was not able to read your config file then it will resort to the CREATE NEW SIM option. Please
		check the error messages (in the command line window), and your .bnconfig file.
	
	How do I change the number of agents in my simulation?
		This should be specified in the setup function in your .nlogo file. see Section 6 for more information.
		
	I want to have agents with different POSH plans (lap files)
		To do this you should specify them as different breeds both in NetLogo and in the simulation config. If you want to have some agents from an existing breed running different plans, you should create a new breed that has a different name and different lap
		file but with all other properties the same
		
	