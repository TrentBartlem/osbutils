package com.oracle.ukps.osbutil.xmlcache;

import org.apache.xmlbeans.XmlObject;

/**
 * Static facade to XmlCacheUtility for OSB.
 * 
 * This facade provides static methods for accessing the non-static methods of
 * the XmlCacheUtility. Oracle Service Bus can only perform Java call-outs to
 * static methods and therefore this facade provides an access point for OSB
 * whilst allowing the XmlCacheUtility to be extended.
 *   
 *
 */

public class XmlCacheUtilityOSBFacade {
	

	/**
	 * Get some XML from the cache
	 * @param key	The key used to identify some XML in the cache
	 * @return		The XML from the cache
	 */
	public static XmlObject getXml(String key) throws XmlCacheException {
		return XmlCacheUtility.getXmlCacheUtility().getXml(key);
	}
	
	/**
	 * Reset the cache statistics
	 */
	
	public static void resetStatistics() {
		XmlCacheUtility.getXmlCacheUtility().resetStatistics();
	}
	

}
