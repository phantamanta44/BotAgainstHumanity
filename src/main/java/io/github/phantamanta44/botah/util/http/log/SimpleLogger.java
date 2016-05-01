package io.github.phantamanta44.botah.util.http.log;

import org.slf4j.helpers.MarkerIgnoringBase;

public class SimpleLogger extends MarkerIgnoringBase {

	private String name;
	private SimpleLogLevel level = SimpleLogLevel.INFO;

	public SimpleLogger(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return level.hasLevel(SimpleLogLevel.TRACE);
	}

	@Override
	public void trace(String msg) {
		print(msg, SimpleLogLevel.TRACE);
	}

	@Override
	public void trace(String format, Object arg) {
		print(format(format, arg), SimpleLogLevel.TRACE);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		print(format(format, arg1, arg2), SimpleLogLevel.TRACE);
	}

	@Override
	public void trace(String format, Object... arguments) {
		print(format(format, arguments), SimpleLogLevel.TRACE);
	}

	@Override
	public void trace(String msg, Throwable t) {
		print(format("%s (%s)", msg, t.getMessage()), SimpleLogLevel.TRACE);
	}

	@Override
	public boolean isDebugEnabled() {
		return level.hasLevel(SimpleLogLevel.DEBUG);
	}

	@Override
	public void debug(String msg) {
		print(msg, SimpleLogLevel.DEBUG);
	}

	@Override
	public void debug(String format, Object arg) {
		print(format(format, arg), SimpleLogLevel.DEBUG);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		print(format(format, arg1, arg2), SimpleLogLevel.DEBUG);
	}

	@Override
	public void debug(String format, Object... arguments) {
		print(format(format, arguments), SimpleLogLevel.DEBUG);
	}

	@Override
	public void debug(String msg, Throwable t) {
		print(format("%s (%s)", msg, t.getMessage()), SimpleLogLevel.DEBUG);
	}

	@Override
	public boolean isInfoEnabled() {
		return level.hasLevel(SimpleLogLevel.INFO);
	}

	@Override
	public void info(String msg) {
		print(msg, SimpleLogLevel.INFO);
	}

	@Override
	public void info(String format, Object arg) {
		print(format(format, arg), SimpleLogLevel.INFO);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		print(format(format, arg1, arg2), SimpleLogLevel.INFO);
	}

	@Override
	public void info(String format, Object... arguments) {
		print(format(format, arguments), SimpleLogLevel.INFO);
	}

	@Override
	public void info(String msg, Throwable t) {
		print(format("%s (%s)", msg, t.getMessage()), SimpleLogLevel.INFO);
	}

	@Override
	public boolean isWarnEnabled() {
		return level.hasLevel(SimpleLogLevel.WARN);
	}

	@Override
	public void warn(String msg) {
		print(msg, SimpleLogLevel.WARN);
	}

	@Override
	public void warn(String format, Object arg) {
		print(format(format, arg), SimpleLogLevel.WARN);
	}

	@Override
	public void warn(String format, Object... arguments) {
		print(format(format, arguments), SimpleLogLevel.WARN);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		print(format(format, arg1, arg2), SimpleLogLevel.WARN);
	}

	@Override
	public void warn(String msg, Throwable t) {
		print(format("%s (%s)", msg, t.getMessage()), SimpleLogLevel.WARN);
	}

	@Override
	public boolean isErrorEnabled() {
		return level.hasLevel(SimpleLogLevel.ERROR);
	}

	@Override
	public void error(String msg) {
		print(msg, SimpleLogLevel.ERROR);
	}

	@Override
	public void error(String format, Object arg) {
		print(format(format, arg), SimpleLogLevel.ERROR);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		print(format(format, arg1, arg2), SimpleLogLevel.ERROR);
	}

	@Override
	public void error(String format, Object... arguments) {
		print(format(format, arguments), SimpleLogLevel.ERROR);
	}

	@Override
	public void error(String msg, Throwable t) {
		print(format("%s (%s)", msg, t.getMessage()), SimpleLogLevel.ERROR);
	}

	private void print(String msg, SimpleLogLevel level) {
		if (this.level.hasLevel(level))
			System.out.printf("[%s/%s] %s\n", name, level, msg);
	}

	private static String format(String formatArg, Object... args) {
		String format = formatArg;
		int ind, i = 0;
		while ((ind = format.indexOf("{}")) != -1 && i < args.length)
			format = format.substring(0, ind).concat(args[i++].toString()).concat(format.substring(ind + 2, format.length()));
		return format;
	}

	private enum SimpleLogLevel {

		TRACE, DEBUG, INFO, WARN, ERROR;

		public boolean hasLevel(SimpleLogLevel level) {
			return ordinal() <= level.ordinal();
		}

	}

}
