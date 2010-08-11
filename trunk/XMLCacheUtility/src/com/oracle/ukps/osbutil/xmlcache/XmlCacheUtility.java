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
 * This class provides the majority of the functionality regarding the caching
 * mechanism.
 * 
 * This class should not be used directly from Oracle Service Bus. Calls from
 * OSB should be proxied through the XmlCacheUtilityFacade.
 * 
 * <h2>Configuration</h2>
 *
 */

public class XmlCacheUtility {
	
	///////////////////////////////////////////////////////////////////////////
	// Static Members
	///////////////////////////////////////////////////////////////////////////
	
	/** System property name to set the configuration file **/ 
	public static String CFG_SYSPROP_NAME
		= "com.oracle.ukps.osbutil.xmlcache.CONFIGFILE";

	/** JDK Logging Logger - Can be configured to use WL Logging Bridge **/
	private static Logger logger
		= Logger.getLogger(XmlCacheUtility.class.getName());
	
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
	
	/** Cache item maximum age (expiry) **/
	private long expireAfterMillis = 30000;
	 
	private boolean statsOn = true;
	private long hits	= 0;
	private long misses = 0;
	private long avgHitTime = 0;
	private long avgMissTime = 0;
	
	///////////////////////////////////////////////////////////////////////////
	// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	protected XmlCacheUtility() {
		configure();
		loadXmlSources();
		xmlSources.add(new XmlCacheFileSource());
	}
	
	protected void configure() {
		
		logger.finest("Configuring cache utility");
		
		configuration = new Properties();	
		
		String filepath = System.getProperty(CFG_SYSPROP_NAME, "xmlcache.properties");
		try {
			configuration.load(new FileInputStream(filepath));
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
			expireAfterMillis = Long.parseLong(configuration.getProperty("expire", "30000"));
			logger.info("Using cache expiry of " + expireAfterMillis + "ms.");
		} catch (NumberFormatException e) {
			logger.severe("Configuration property 'expire' is not a valid integer. Using default of " + expireAfterMillis);
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
			if (entry.timestamp + expireAfterMillis >= System.currentTimeMillis()) {
				logger.info("HIT: " + key);
				if (statsOn) {
					long t = System.currentTimeMillis();
					avgHitTime = ((avgHitTime * hits) + (t-s)) / (hits + 1);
					hits++;
					logger.finer(String.format("Hits=%1s (avg %2s ms), Misses=%3s (avg %4s ms)", hits, avgHitTime, misses, avgMissTime));
				}
				return entry.xml;
			}
		}
		
		XmlObject xmlObject = searchXmlSources(key);
		if (xmlObject != null) {
			XmlCacheEntry cacheEntry = new XmlCacheEntry(xmlObject);
			xmlCache.put(key, cacheEntry);
			if (statsOn) {
				long t = System.currentTimeMillis();
				avgMissTime = ((avgMissTime * misses) + (t-s)) / (misses + 1);
				misses++;
			}
			return xmlObject;
		}
		
		throw new XmlCacheException("Unable to load XML for key: " + key);	
		
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Stats
	///////////////////////////////////////////////////////////////////////////
	
	public int getCacheSize() {
		return xmlCache.size();
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Factory
	///////////////////////////////////////////////////////////////////////////
	
	protected void loadXmlSources() {
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
