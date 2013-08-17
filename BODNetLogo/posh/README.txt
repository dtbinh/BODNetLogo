###############################################################################################################################
###############################################################################################################################
This is a readme file for part of the BODNetLogo program, please note that BODNetLogo, whilst fully functional,
is not a finished program, you may find bugs or errors that are not handled properly and the user interface is not
particularly robust at handling incorrect information, so if you try to break it, you probably will. 
Please see the user manual (below) for full details on how to use the program and for further support or bug reports please 
contact the author at bodnetlogo@mhbrooks.com

This is the root level directory for the POSH scheduler


###############################################################################################################################
###############################################################################################################################

This implementation of POSH scheduler is based on the C# version by Swen Gaudl, the general structure, methods and functionality are the same however a number of modifications were made in the translation.

The main changes were forced by differences in Language and primitive capabilities. Especially data structures and class hierarchy.

Changes were also made as this scheduler is not a general implementation of POSH as with other versions. Rather it is specifically designed to work with BODNetLogo, this involved the removal of several unecessary functions and small variations in functionality.

PlanBuilder - is the controlling class for the scheduler, using the String given to it by the Agent class it creates an instance of DriveCollection that forms the plan of the agent. This plan is then returned to Agent.

DriveCollection - is the Object that contains the plan of each agent, the drive collection is stored by Agent and is capable of being fired. this produces an action based on the sensory values it receives.

Timer - allows the POSH plans to set maximum frequencies

lapParser - contains the code for parsing the String from the lap file and returning a data structure which is then used by PlanBuilder to create the DriveCollection

POSHObjects - contains the various Objects that make up the plan in the DriveCollection
