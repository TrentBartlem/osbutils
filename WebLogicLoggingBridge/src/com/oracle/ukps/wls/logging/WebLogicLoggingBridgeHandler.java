package com.oracle.ukps.wls.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import weblogic.logging.NonCatalogLogger;

public class WebLogicLoggingBridgeHandler extends Handler {
	
	private NonCatalogLogger logger;
	
	public WebLogicLoggingBridgeHandler() {
		this("JAVA_LOGGING_BRIDGE");
	}
	
	public WebLogicLoggingBridgeHandler(String name) {
		System.err.println("Creating WebLogicServerLogHandler");
		logger = new NonCatalogLogger(name);
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	
	@Override
	public void publish(LogRecord record) {
		
		if (record.getLevel() == Level.FINER || record.getLevel() == Level.FINEST) {
			if (record.getThrown() == null) {
				logger.trace(record.getMessage());
			} else {
				logger.trace(record.getMessage(), record.getThrown());
			}
		} else if (record.getLevel() == Level.FINE || record.getLevel() == Level.CONFIG) {
			if (record.getThrown() == null) {
				logger.debug(record.getMessage());
			} else {
				logger.debug(record.getMessage(), record.getThrown());
			}
		} else if (record.getLevel() == Level.INFO) {
			if (record.getThrown() == null) {
				logger.info(record.getMessage());
			} else {
				logger.info(record.getMessage(), record.getThrown());
			}
		} else if (record.getLevel() == Level.WARNING) {
			if (record.getThrown() == null) {
				logger.warning(record.getMessage());
			} else {
				logger.warning(record.getMessage(), record.getThrown());
			}
		} else if (record.getLevel() == Level.SEVERE) {
			if (record.getThrown() == null) {
				logger.critical(record.getMessage());
			} else {
				logger.critical(record.getMessage(), record.getThrown());
			}
		} else {
			throw new IllegalArgumentException("Level of message not mapped: " + record.getLevel());
		}
	}

}
