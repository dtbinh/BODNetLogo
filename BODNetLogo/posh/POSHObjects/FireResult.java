package posh.POSHObjects;


/*
 * NOTE: This is part of a POSH Scheduler which is heavily based on another implementation
 * in C# created by Swen Gaudl. Please see accompanying documentation for further details
 *
 * Java translation and modification performed as part of BODNetLogo by Michael Brooks
 *  
 */

public class FireResult {

	private boolean continueExecuting;
	private CopiableElement next;
	private boolean finishedPlan = false;
	
	
	public FireResult(boolean b, CopiableElement object) {
		this.continueExecuting = b;
		if (continueExecuting && object !=null)	{
			utils.logging.Log.staticDebug("Error in fireResult");
			System.exit(0);
		}
		else	{
			next = null;
		}
	}

	public CopiableElement nextElement() {
		return next;
	}

	public boolean continueExecution() {
		
		return continueExecuting;
	}

	public boolean isFinishedPlan() {
		return finishedPlan;
	}

	public void setFinishedPlan(boolean finishedPlan) {
		this.finishedPlan = finishedPlan;
	}

}
