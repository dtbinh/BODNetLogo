###############################################################################################################################
###############################################################################################################################
This is a readme file for part of the BODNetLogo program, please note that BODNetLogo, whilst fully functional,
is not a finished program, you may find bugs or errors that are not handled properly and the user interface is not
particularly robust at handling incorrect information, so if you try to break it, you probably will. 
Please see the user manual (below) for full details on how to use the program and for further support or bug reports please 
contact the author at contact@mhbrooks.com

This is the root level directory for the source code of BODNetLogo.


###############################################################################################################################
###############################################################################################################################

For further details on the workings of BODNetLogo you should read the Implementation Chapter in the dissertation which describes the creation and design of BODNetLogo

###############################################################################################################################
###############################################################################################################################

To compile this code yourself you must have an installed version of NetLogo and correctly configure the classpath, see the information provided by NetLogo for more information http://ccl.northwestern.edu/netlogo/5.0RC2/docs/controlling.html

###############################################################################################################################
###############################################################################################################################

The key components of BODNetLogo are as follows a more detailed discussion can be found in each folder:

main - this is the location of the main function which runs the BODNetLogo program 

simulationManager - continues from main and sets up the simulation

agentManager - continues from simulationManager and generates all the BOD agents and their action plans, once the agents have been configured, agentManager starts the control loop.

utils - contains any misc. files needed by BODNetLogo

gui - contains the gui classes for BODNetLogo

posh - contains the POSH scheduler, note this scheduler is based on another, in C# by Swen Gaudl, although the general structure remains similar, a significant number of changes were required in the translation. 