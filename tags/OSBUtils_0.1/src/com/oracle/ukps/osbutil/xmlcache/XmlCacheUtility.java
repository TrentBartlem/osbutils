package com.oracle.ukps.osbutil.xmlcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *
 */

public class XmlCacheUtility {

	private static XmlCacheUtility instance = new XmlCacheUtility();
	
	private Map <String, XmlCacheEntry> xmlCache = Collections.synchronizedMap(new HashMap <String, XmlCacheEntry> ());
	
	private List <XmlCacheSource> xmlSources = Collections.synchronizedList(new ArrayList <XmlCacheSource> ());
	
	private long maxAgeMillis = 30000;
	
	protected XmlCacheUtility() {
		xmlSources.add(new XmlCacheFileSource());
	}
	
	public static XmlCacheUtility getXmlCacheUtility() {
		return instance;
	}
	
	public void addXmlCacheSource(XmlCacheSource source) {
		xmlSources.add(source);
	}
	
	public void removeXmlCacheSource(XmlCacheSource source) {
		xmlSources.remove(source);
	}
	
	public XmlObject readSources(String key) {
		for (XmlCacheSource source: xmlSources) {
			XmlObject xmlObject = source.readSource(key);
			if (xmlObject != null) {
				return xmlObject;
			}
		}
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
		
		if (xmlCache.containsKey(key)) {
			XmlCacheEntry entry = xmlCache.get(key);
			if (entry.timestamp + maxAgeMillis >= System.currentTimeMillis()) { 
				return entry.xml;
			}
		}
		
		XmlObject xmlObject = readSources(key);
		if (xmlObject != null) {
			XmlCacheEntry cacheEntry = new XmlCacheEntry(xmlObject);
			xmlCache.put(key, cacheEntry);
			return xmlObject;
		}
		
		throw new XmlCacheException("Unable to load XML for key: " + key);	
		
	}
	
	public static class XmlCacheEntry {
		
		private long		timestamp;
		private XmlObject	xml;
		
		public XmlCacheEntry(XmlObject xml) {
			timestamp = System.currentTimeMillis();
			this.xml = xml;
		}
		
	}
	
}
