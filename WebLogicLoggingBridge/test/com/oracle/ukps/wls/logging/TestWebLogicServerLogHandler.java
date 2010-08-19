package com.oracle.ukps.wls.logging;

import java.io.File;
import java.util.logging.Logger;

import junit.framework.TestCase;

public class TestWebLogicServerLogHandler extends TestCase {
	 
	public void testLogging() throws Exception {
		
		File log = new File("wls.log");
		if (log.exists()) {
			log.delete();
		}
		log.createNewFile();
		
		System.setProperty("java.util.logging.config.file", "logging.properties");
		System.setProperty("weblogic.log.FileName", "wls.log");
		//Debug, Info, Warning, Error, Notice, Critical, Alert, Emergency, and Off. 
		System.setProperty("weblogic.log.StdoutSeverityLevel", "Debug");
		
		Logger logger = Logger.getLogger(TestWebLogicServerLogHandler.class.getName());
		logger.finest("FINEST");
		logger.finer("FINER");
		logger.fine("FINE");
		logger.info("INFO");
		logger.warning("WARNING");
		logger.severe("SEVERE");
	}
	
}
