package org.slf4j.impl;

import io.github.phantamanta44.botah.util.http.log.SimpleLoggerFactory;
import org.slf4j.ILoggerFactory;

public class StaticLoggerBinder {

	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	public static StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	public static String REQUESTED_API_VERSION = "1.6.99";

	public ILoggerFactory getLoggerFactory() {
		return new SimpleLoggerFactory();
	}

	public String getLoggerFactoryClassStr() {
		return SimpleLoggerFactory.class.getName();
	}

}
