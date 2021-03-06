package edu.psgv.sweng861;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * This class represents a basic error found while processing the playlists
 * 
 * @author Kyle Hughes
 *
 */
public class PlaylistError {
	private int lineNumber;
	private String description;
	private ErrorType type;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Simple constructor to create an error with a error category
	 * 
	 * @param errorType 
	 * @param line
	 * @param desc
	 */
	public PlaylistError(ErrorType errorType, int line, String desc) {
		logger.debug(">>PlayListError() created");
		description = desc;
		lineNumber = line;
		type=errorType;
		logger.debug("<<PlayListError() created");

	}

	/**
	 * getLineNumber() Returns the line number error was found
	 * 
	 * @return
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * getDescription() returns the description of the error
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * getType() Returns the error severity
	 * @return
	 */
	public ErrorType getType() {
		return type;
	}
}
