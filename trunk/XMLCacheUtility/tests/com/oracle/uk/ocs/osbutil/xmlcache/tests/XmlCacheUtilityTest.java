package com.oracle.uk.ocs.osbutil.xmlcache.tests;

import org.apache.xmlbeans.XmlObject;

import com.oracle.uk.ocs.osbutil.xmlcache.XmlCacheUtility;

import junit.framework.TestCase;

public class XmlCacheUtilityTest extends TestCase {
	
	public void testGetXml() {
	}
	
	public void testPerf() throws Exception {
		
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getHitTotal()	== 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getHitMinTime()	== 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getHitAvgTime() == 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getHitMaxTime() == 0L);
		
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getMissTotal() == 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getMissMinTime() == 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getMissAvgTime() == 0L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getMissMaxTime() == 0L);
		
		for (int i=0; i<10; i++) {
			XmlCacheUtility.getXmlCacheUtility().getXml("example");
		}
		
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getHitTotal() == 9L);
		assertTrue(XmlCacheUtility.getXmlCacheUtility().getMissTotal() == 1L);
		
		System.out.println(XmlCacheUtility.getXmlCacheUtility().getStatisticsString());

	}

}
