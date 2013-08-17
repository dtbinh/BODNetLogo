package utils.logging;

/*
 * This is part of the BODNetLogo program which integrates BOD with NetLogo,
 * please see the README files for this directory for more information
 * Michael Brooks
 */

public class Log {

	private String messageSource;
	
	//control the output seen by users, set by the GUI
	public static boolean showInfo = true;
	public static boolean showDebug = false;
	public static boolean showError = true;
	
	/**
	 * Create a logger - note messageSource can be the id of an agent or a class name
	 * @param sourceID
	 */
	public Log(String sourceID)	{
		this.messageSource = sourceID;
	}
	
	public Log()	{
		//no message source given
		this.messageSource = "General";
	}
	
	
	public void debug(String x)	{
		if (!showDebug) return;
		if (messageSource != null)	{
			x = "DEBUG: " + messageSource + ": " + x;
		}
		
		System.out.println(x);
		
	}
	
	public void error(String x)	{
		if (!showError) return;
		if (messageSource != null)	{
			x = "ERROR: " + messageSource + ": " + x;
		}
		
		System.out.println(x);
		
	}
	
	public void info(String x)	{
		if (!showInfo) return;
		if (messageSource != null)	{
			x = "INFO: " + messageSource + ": " + x;
		}
		
		System.out.println(x);
		
	}
	
	//used only in exceptional cases for quick problem solving
	public static void staticDebug(String x)	{
		System.out.println("STATIC DEBUG: " + x);
	}
}
