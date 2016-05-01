package io.github.phantamanta44.botah.util.http.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWrapper {

	private final Logger logger;
	
	public LogWrapper(String name) {
		logger = LoggerFactory.getLogger(name);
	}
	
	public void info(String msg) {
		logger.info(msg);
	}
	
	public void info(String format, Object... args) {
		logger.info(String.format(format, args));
	}
	
	public void warn(String msg) {
		logger.warn(msg);
	}
	
	public void warn(String format, Object... args) {
		logger.warn(String.format(format, args));
	}
	
	public void severe(String msg) {
		logger.error(msg);
	}
	
	public void severe(String format, Object... args) {
		logger.error(String.format(format, args));
	}
	
}
