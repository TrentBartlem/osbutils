package com.oracle.ukps.osbutil.xmlcache;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * File-based implementation of XmlCacheSource
 * 
 * <p>The is a file-based implementation of <code>XmlCacheSource</code>.</p>
 * 
 * <p>This implementation provides a very simple implementation that looks in a
 * base directory for XML files that are named the same as the key. For example
 * if this implementation is asked for the XML keyed-off 'test' then it will
 * look for a file called 'xmlcache/test.xml'.</p>
 * 
 * <p>Any forward slashes in the kay are preserved when calculating the filename
 * therefore sub-directories can be specified with the appropriate key.</p>
 * 
 * <h2>Configuration</h2>
 * 
 * <p>The following describes the scoped configuration keys that configure this
 * XML source. These scoped keys are appended to the configuration basekey in
 * order to find the appropriate property in the configuration.</p>
 * 
 * <p>The following example shows an excerpt from a configuration file that
 * specifies an XML source called 'xmlcache' that is an instance of
 * XmlCacheFileSource that reads out the base directory 'xmlcachedir'.</p>
 * 
 * <pre>
 * ...
 * source.xmlcache.class=com.oracle.ukps.osbutil.xmlcache.XmlCacheFileSource
 * source.xmlcache.basedir=xmlcachedir
 * ...
 * </pre>
 * 
 * <table style="border=1;">
 *     <tr>
 *         <th>Key</th>
 *         <th>Description</th>
 *         <th>Default</th>
 *         <th>Required</th>
 *     </tr>
 *     <tr>
 *         <td>basedir</td>
 *         <td>The path to the directory that is used as the base for loading
 *         the XML from. This can be either a relative path or full path. It
 *         is recommended that even on Windows the '/' character is used to
 *         separate paths. Java will convert these if necessary.</td>
 *         <td>xmlcache</td>
 *         <td>No</td>
 *     </tr>
 * </table>
 * 
 * @see XmlCacheSource
 *
 */
public class XmlCacheFileSource implements XmlCacheSource {
	
	String basedir = "xmlcache";
	
	@Override
	public XmlObject readSource(String key) {
		String filepath = basedir + "/" + key + ".xml";
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

	@Override
	public void configure(Properties configuration, String base) {
		basedir = configuration.getProperty(base + ".basedir", "xmlcache");
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + " {" + basedir + "}";
	}

}
