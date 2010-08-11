package com.oracle.ukps.osbutil.xmlcache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlObject;

/**
 * Main Cache Utility
 * 
 * <p>This class provides the majority of the functionality regarding the caching
 * mechanism.</p>
 * 
 * <p>This class should not be used directly from Oracle Service Bus. Calls from
 * OSB should be accessed through the XmlCacheUtilityFacade.</p>
 * 
 * <h2>Configuration</h2>
 *
 * <p>Configuration is performed via a properties file.</p>
 * 
 * <table>
 * </table>
 *
 * <h2>Logging</h2>
 * 
 * <p>Logging is implemented using the JDK Logging. This removes the
 * dependencies on other JAR files making this utility easier to deploy,
 * especially in OSB.</p>
 * 
 * <p>To use the JDK logging only then the normal JDK configuration holds. It
 * is recommended that you copy the file <i>JRE</i>/lib/logging.properties,
 * make changes to the copy and specify the properties via the System property
 * 'java.util.logging.config'. For example: </p>
 * 
 * <pre>
 * -Djava.util.logging.config.file=logging.properties
 * </pre>
 * 
 * <p>In OSB it is recommended that this file be copied to the domain
 * directory and that the System property is set as described above when
 * starting the server</p>. 
 * 
 * <h3>Loggers</h3>
 * 
 * <p>There are two logger associated with this class.</p>
 * 
 * <p>The logger <b>com.oracle.ukps.osbutils.XmlCacheUtility</b> (following
 * the standard JDK logging naming recommendations) is used for normal logging
 * messages i.e for logging debug, information or error messages from the
 * utility.</p>
 * 
 * <p>The logger <b>com.oracle.ukps.osbutils.XmlCacheUtilty.STATS</b> is a
 * special logger that is used for logging the cache statistics. Therefore
 * turning this log on or off in the configuration will determine whether the
 * statistics are logged. All messages to this log are logged at the INFO
 * log level. It is recommended that this log is disabled in systems that
 * require maximum throughput.</p>
 * 
 * <h3>Integrating with WebLogic Logging</h3>
 * 
 * <p>In OSB this will require
 * 
 */

public class XmlCacheUtility {
	
	///////////////////////////////////////////////////////////////////////////
	// Static Members
	///////////////////////////////////////////////////////////////////////////
	
	/** System property name to set the configuration file **/ 
	public static String CFG_SYSPROP_NAME
		= "com.oracle.ukps.osbutil.xmlcache.CONFIGFILE";
	
	/** The configuration key to set the cache expiry **/
	public static String CFGKEY_CACHE_EXPIRY = "cache.expiry";
	

	public static String CFGKEY_CACHE_STATS  = "cache.statistics";
	
	/** JDK Logging Logger - Can be configured to use WL Logging Bridge **/
	private static Logger logger
		= Logger.getLogger(XmlCacheUtility.class.getName());
	
	private static Logger statsLogger
		= Logger.getLogger(XmlCacheUtility.class.getName() + ".STATS");
	
	/** Singleton instance **/
	private static XmlCacheUtility instance = new XmlCacheUtility();
	
	///////////////////////////////////////////////////////////////////////////
	// Instance Members
	///////////////////////////////////////////////////////////////////////////
	
	/** Cache is backed by a Map. Synchronised for thread-safety **/
	private Map <String, XmlCacheEntry> xmlCache
		= Collections.synchronizedMap(new HashMap <String, XmlCacheEntry> ());
	
	/** XML Sources, probably doesn't need to be synchronised **/
	private List <XmlCacheSource> xmlSources
		= Collections.synchronizedList(new ArrayList <XmlCacheSource> ());
	
	/** Configuration properties **/
	private Properties configuration = new Properties();
	
	
	/** Configurable: Cache item maximum age (expiry) **/
	private long cacheExpiry = 30000;
	 
	/** Configurable: Whether or not statistics gathering is on **/
	private boolean statisticsIsOn = true;
	
	private Object statsSynchObject = new Object();
	
	private long hitTotal		= 0;
	private long hitMinTime		= 0;
	private long hitMaxTime		= 0;
	private long hitAvgTime		= 0;	
	private long missTotal		= 0;
	private long missMinTime	= 0;
	private long missMaxTime	= 0;
	private long missAvgTime	= 0;	
	
	///////////////////////////////////////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	protected XmlCacheUtility() {
		configure();
		createXmlSources();
	}
	
	protected void configure() {
		
		logger.finest("Configuring cache utility");
		
		configuration = new Properties();
		configuration.setProperty("source.file.class",		"com.oracle.ukps.osbutil.xmlcache.XmlCacheFileSource");
		configuration.setProperty("source.file.basedir",	"xmlcache");
		configuration.setProperty(CFGKEY_CACHE_EXPIRY,		"30000");
		configuration.setProperty(CFGKEY_CACHE_STATS,		"true");
		
		String filepath = System.getProperty(CFG_SYSPROP_NAME, "xmlcache.properties");
		try {
			Properties userConfiguration = new Properties();
			userConfiguration.load(new FileInputStream(filepath));
			configuration = userConfiguration;
			logger.info("Successfully loaded configuration from: " + filepath);
		} catch (FileNotFoundException e) {
			logger.info("No configuration found from: "
					+ filepath + ". Default will be used.");
		} catch (IOException e) {
			logger.severe("Error reading configuration file: "
					+ filepath + ". Defaults will be used");
			e.printStackTrace();
		}
		
		try {
			cacheExpiry = Long.parseLong(configuration.getProperty(CFGKEY_CACHE_EXPIRY, "30000"));
			logger.info("Using cache expiry of " + cacheExpiry + "ms.");
		} catch (NumberFormatException e) {
			logger.severe("Configuration property 'expire' is not a valid integer. Using default of " + cacheExpiry);
		}
		
	}
	
	/**
	 * Get the Singleton instance
	 * @return	The Singleton instance of XMLCacheUtility
	 */
	
	public static XmlCacheUtility getXmlCacheUtility() {
		return instance;
	}
	
	/**
	 * Add an XML source to the cache
	 * @param source	The XmlCacheSource instance to add
	 */
	
	public void addXmlCacheSource(XmlCacheSource source) {
		xmlSources.add(source);
	}
	
	/**
	 * Remove an XML source from the cache
	 * 
	 * Does not invalidate any cache entries that have already been loaded
	 * into the cache.
	 * 
	 * @param source
	 */
	
	public void removeXmlCacheSource(XmlCacheSource source) {
		xmlSources.remove(source);
	}
	
	/**
	 * Search all the XML sources for XML identified by the key
	 * 
	 * The search will terminate with the first item that is found. The
	 * order of the sources in the list determines the search order.
	 * 
	 * To keep sources from colliding it is recommended that an appropriate
	 * naming strategy is used for keys such that the key prefix is unique
	 * to a particular source.
	 * 
	 * @param key		The key to identify the XML
	 * @return			The XML associated with the given key
	 */
	
	private XmlObject searchXmlSources (String key) {
		logger.finer("searching for item with key: " + key);
		for (XmlCacheSource source: xmlSources) {
			logger.finer("searching source: " + source);
			XmlObject xmlObject = source.readSource(key);
			if (xmlObject != null) {
				logger.fine("found item with key '" + key + "' in source " + source);
				return xmlObject;
			}
		}
		logger.fine("missed item with key: " + key);
		return null;
	}
	
	/**
	 * Fetch the XML identified by the key
	 * 
	 * @param key		The key the identifies some XML
	 * @return			The XML identified by the given key
	 * @throws XmlCacheException	If no XML is found
	 */
	
	public XmlObject getXml(String key) throws XmlCacheException {
		long s = System.currentTimeMillis();
		if (xmlCache.containsKey(key)) {
			XmlCacheEntry entry = xmlCache.get(key);
			if (entry.timestamp + cacheExpiry >= System.currentTimeMillis()) {
				if (statisticsIsOn) {
					long time = System.currentTimeMillis() - s;
					updateHitStatistics(time);
				}
				return entry.xml;
			}
		}
		
		XmlObject xmlObject = searchXmlSources(key);
		if (xmlObject != null) {
			XmlCacheEntry cacheEntry = new XmlCacheEntry(xmlObject);
			xmlCache.put(key, cacheEntry);
			if (statisticsIsOn) {
				long time = System.currentTimeMillis() - s;
				updateMissStatistics(time);
			}
			return xmlObject;
		}
		
		throw new XmlCacheException("Unable to load XML for key: " + key);	
		
	}
	
	public static long getLongProperty(Properties properties, String key, long defaultValue) {
		String s = properties.getProperty(key);
		if (s != null) {
			try {
				return Long.parseLong(s);
			} catch (NumberFormatException e) {
				logger.warning("Property '" + key + "' is not a valid long.");
			}
		}
		return defaultValue;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	// Statistics Methods
	///////////////////////////////////////////////////////////////////////////
	
	private void updateHitStatistics (long time) {
		synchronized (statsSynchObject) {
			hitAvgTime = ((hitAvgTime * hitTotal) + time) / (hitTotal + 1);
			hitTotal++;
			if (time < hitMinTime) hitMinTime = time;
			if (time > hitMaxTime) hitMaxTime = time;
			statsLogger.info(getStatisticsString());
		}
	}
	
	private void updateMissStatistics (long time) {
		synchronized (statsSynchObject) {
			missAvgTime = ((missAvgTime * missTotal) + time) / (missTotal + 1);
			missTotal++;
			if (time < missMinTime) missMinTime = time;
			if (time > missMaxTime) missMaxTime = time;
			statsLogger.info(getStatisticsString());
		}
	}	
	
	public int getCacheSize() {
		return xmlCache.size();
	}
	
	public long getHitTotal() {
		return hitTotal;
	}

	public long getHitMinTime() {
		return hitMinTime;
	}
	
	public long getHitMaxTime() {
		return hitMaxTime;
	}
	
	public long getHitAvgTime() {
		return hitAvgTime;
	}
		
	public long getMissTotal() {
		return missTotal;
	}
	
	public long getMissMinTime() {
		return missMinTime;
	}
	
	public long getMissMaxTime() {
		return missMaxTime;
	}
	
	public long getMissAvgTime() {
		return missAvgTime;
	}
	
	public void resetStatistics() {
		hitTotal		= 0;
		hitMinTime		= 0;
		hitMaxTime		= 0;
		hitAvgTime		= 0;
		missTotal 		= 0;
		missMinTime		= 0;
		missMaxTime		= 0;
		missAvgTime		= 0;
	}
	
	public String getStatisticsString() {
		return String.format(
		    "STATS " + 
		    "HIT-TOTAL=%1s  HIT-MIN=%2s  HIT-AVG=%3s  HIT-MAX=%4s" +
		    " | " +
		    "MISS-TOTAL=%5s MISS-MIN=%6s MISS-AVG=%7s MISS-MAX=%8s",
		    hitTotal,
		    hitMinTime,
		    hitAvgTime,
		    hitMaxTime,
		    missTotal,
		    missMinTime,
		    missAvgTime,
		    missMaxTime);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Factory: XmlCacheSource
	///////////////////////////////////////////////////////////////////////////
	
	protected void createXmlSources() {
		ArrayList <String> sourceKeys = new ArrayList <String> ();
		for (Object o: configuration.keySet()) {
			String propKey = (String) o;
			if (propKey.startsWith("source.") && propKey.length() > 9 && propKey.indexOf(".", 7) > -1) {
				String sourceKey = propKey.substring(7, propKey.indexOf(".", 7));
				if (!sourceKeys.contains(sourceKey))
					sourceKeys.add(sourceKey);
			}
		}
		
		for (String sourceKey: sourceKeys) {
			if (configuration.containsKey("source." + sourceKey + ".class")) {
				String name = configuration.getProperty("source." + sourceKey + ".class");
				
				if (name == null)
					continue;
				
				try {
					// Use the magic of reflection to load the class
					Class clazz = this.getClass().getClassLoader().loadClass(name);
					Constructor cstr = clazz.getConstructor(new Class[0]);
					XmlCacheSource source = (XmlCacheSource) cstr.newInstance(new Object[0]);
					
					// Get the source to configure itself! 
					source.configure(configuration, "source." + sourceKey);
					
					addXmlCacheSource(source);
				} catch (Exception e) {
					logger.severe("Unable to create cache source: " + sourceKey);
					e.printStackTrace();
				}
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Inner Classes
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Represents an entry in the cache
	 */
	
	public static class XmlCacheEntry {
		
		private long		timestamp;
		private XmlObject	xml;
		
		public XmlCacheEntry(XmlObject xml) {
			timestamp = System.currentTimeMillis();
			this.xml = xml;
		}
		
	}
	
}
