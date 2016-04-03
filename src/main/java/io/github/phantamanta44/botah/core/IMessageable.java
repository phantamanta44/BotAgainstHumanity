package io.github.phantamanta44.botah.core;

public interface IMessageable {

	public void sendMessage(String msg);
	
	public void sendMessage(String format, Object... args);
	
}
