package edu.psgv.sweng861;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a factory and is responsible for creating 
 * 	3 kind of objects {Media,Master,PlayListError}
 * 
 *  @author Kyle Hughes
 */
public class PlaylistFactory {
	private static PlaylistFactory instance;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * getInstance() allows the class to be used as a singleton
	 * @return
	 */
	public static PlaylistFactory getInstance() {
		logger.debug(">>getInstance() ");

		if (instance == null) {
			logger.debug("<<getInstance() a new object created");
			return new PlaylistFactory();
		} else {
			logger.debug("<<getInstance() existing object used");
			return instance;
		}
	}

	/**
	 * create() create the correct object based on the contents of the playlist file
	 * @param url the url we are processing
	 * @param contents the contents of the playlist gathered from url
	 * @return the correct playlist
	 */
	public Playlist create(String url, List<String> contents) {
		logger.debug(">>create() ");

		Playlist pl = null;
		String type = "";
		//check the first tag we encounter in the contents to determine the type
		for (String c : contents) {
			if (c.contains("EXT-X-STREAM-INF")) {
				type = "MASTER";
				break;
			} else if (c.contains("EXTINF")) {
				type = "MEDIA";
				break;
			}
		}

		if (type.equals("MASTER")) {
			//keep variant creation in the factory
			List<MediaPlaylist> variants = processVariants(url,contents);
			pl = new MasterPlaylist(contents,url,variants);
			logger.info("Created a master playlist with " +url);
		} else if (type.equals("MEDIA")) {
			pl = new MediaPlaylist(contents,url);
			logger.info("Created a media playlist with " +url);
		}

		logger.debug("<<create() ");
		return pl;
	}

	/**
	 * getErrorObj() Returns a simple error based on the line and description
	 * @param line line the error was found
	 * @param description type of error to display
	 * @return
	 */
	public static PlaylistError getErrorObj(ErrorType type,int line, String description) {
		logger.debug(">>getErrorObj() ");
		PlaylistError pe = new PlaylistError(type,line, description);
		logger.debug("<<getErrorObj() ");
		return pe;
	}
	
	/**
	 * processVariants() Go through each variant url in the master playlist and
	 * create a media object representation to add to the master. 
	 * Return all of the variants
	 *
	 * @param url - the url of the master
	 * @param content - the content of the master
	 */
	private static List<MediaPlaylist> processVariants(String url, List<String> content) {
		logger.debug(">>processVariants()");
		
		List<MediaPlaylist> variantsFound = new ArrayList<MediaPlaylist>();
		String absoluteUrl = absolutizeUrl(url);
		String variantUrl = "";
		for (String c : content) {
			// get all urls in the master
			if (c.endsWith(".m3u8")) {
				// append the variant
				variantUrl = absoluteUrl + c;
				List<String> variantContent = Utilities.readPlaylist(variantUrl);
				// Validate the variant to check for errors
				if (!Utilities.isReadError() && variantContent.size() > 0) {
					// master playlists only contain other media playlists so
					// assumption is made
					MediaPlaylist variantFound = new MediaPlaylist(variantContent,variantUrl);
					variantsFound.add(variantFound);

					logger.info("Adding the variant : " + variantUrl);
				} else {
					logger.error("ERROR - A variant playlist was unable to be added due to a URL read error");
				}
			}
		}
		
		logger.debug("<<processVariants()");
		return variantsFound;
	}

	/**
	 * absolutizeUrl() Remove the end of the url used to get the variant playlists
	 *
	 * @param url
	 * @return a string url without the previous filename
	 */
	private static String absolutizeUrl(String url) {
		logger.debug(">>processVariants()");
		int index = url.lastIndexOf("/");
		String path = url.substring(0, index + 1);
		logger.info("Absolutized path " + path);

		logger.debug("<<processVariants()");
		return path;

	}

}
