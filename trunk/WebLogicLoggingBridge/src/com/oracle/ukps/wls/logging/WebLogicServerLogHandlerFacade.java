package com.oracle.ukps.wls.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class WebLogicServerLogHandlerFacade {

	private static WebLogicLoggingBridgeHandler handler = new WebLogicLoggingBridgeHandler("OSB");
	
	public static void log (String level, String message) {
		LogRecord record = new LogRecord(Level.parse(level), message);
		handler.publish(record);
	}
	
}
