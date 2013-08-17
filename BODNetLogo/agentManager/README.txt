###############################################################################################################################
###############################################################################################################################
This is a readme file for part of the BODNetLogo program, please note that BODNetLogo, whilst fully functional,
is not a finished program, you may find bugs or errors that are not handled properly and the user interface is not
particularly robust at handling incorrect information, so if you try to break it, you probably will. 
Please see the user manual (below) for full details on how to use the program and for further support or bug reports please 
contact the author at bodnetlogo@mhbrooks.com

This is the directory for the agentManager code


###############################################################################################################################
###############################################################################################################################

agentManager is responsible for creating and running the BOD agents.

AgentManager - creates the instances of Agent and manages them, once the simulation is loaded it runs the control loop

Agent - is the class, or Object specification, for the BOD agents, it stores the various details needed, e.g. ID and DriveCollection. As well as enables the firing of the DriveCollection

AttributeStore - stores the attributes of each of the breeds in the BODNetLogo simulation, this is needed so that 'Sense' can retrieve the correct values from NetLogo 
