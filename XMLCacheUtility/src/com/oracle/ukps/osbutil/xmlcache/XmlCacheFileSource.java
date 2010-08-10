package com.oracle.ukps.osbutil.xmlcache;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * File-based implementation of XmlCacheSource
 * 
 * The is a file-based implementation of @See XmlCacheSource.
 * 
 * This implementation provides a very simple implementation that looks in a
 * base directory for XML files that are named the same as the key. For example
 * if this implementation is asked for the XML keyed-off 'test' then it will
 * look for a file called 'xmlcache/test.xml'.
 * 
 * Any forward slashes in the kay are preserved when calculating the filename
 * therefore sub-directories can be specified with the appropriate key.
 * 
 * @see XmlCacheSource
 *
 */
public class XmlCacheFileSource implements XmlCacheSource {
	
	@Override
	public XmlObject readSource(String key) {
		String filepath = "xmlcache/" + key + ".xml";
		File xmlFile = new File(filepath);
		
		if (xmlFile.exists() && xmlFile.isFile()) {
			try {
				return XmlObject.Factory.parse(xmlFile);
			} catch (XmlException e) {
				// TODO: Add WebLogic Logging
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: Add Weblogic logging
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
