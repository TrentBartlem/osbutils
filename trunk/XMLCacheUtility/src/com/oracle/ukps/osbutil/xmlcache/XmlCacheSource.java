package com.oracle.ukps.osbutil.xmlcache;

import org.apache.xmlbeans.XmlObject;

/**
 * The interface that sources of XML should implement
 * 
 *
 */

public interface XmlCacheSource {

	/**
	 * Search the XML source for the XML identified by key
	 * 
	 * @param key	The key to use for locating the XML
	 * @return		The XML associated for the key or null if not found
	 */
	
	public XmlObject readSource(String key);
	
}
