package com.oracle.ukps.osbutil.xmlcache;

import java.util.Properties;

import org.apache.xmlbeans.XmlObject;

/**
 * Interface to a XML source to be used by the cache.
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
	
	/**
	 * Configure the source
	 * 
	 * <p>This method is invoked by the XmlCacheUtility when it is creating the
	 * XmlCacheSource.</p>
	 * 
	 * @param configuration	The configuration properties already loaded by the
	 * XmlCacheUtility
	 * @param base The configuration base key that should be considered the
	 * basis for keys associated with this source
	 * @throws An exception if the configuration will result in a source that
	 * is not usable
	 */
	
	public void configure(Properties configuration, String base) throws Exception ;
}
