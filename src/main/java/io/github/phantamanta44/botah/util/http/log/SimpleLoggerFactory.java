package io.github.phantamanta44.botah.util.http.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SimpleLoggerFactory implements ILoggerFactory {

	@Override
	public Logger getLogger(String name) {

		return new SimpleLogger(name);
	}

}
