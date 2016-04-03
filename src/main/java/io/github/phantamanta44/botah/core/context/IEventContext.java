package io.github.phantamanta44.botah.core.context;

import io.github.phantamanta44.botah.core.IMessageable;
import sx.blah.discord.api.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public interface IEventContext extends IMessageable {

	long getTimestamp();
	
	Class<? extends Event> getType();
	
	IGuild getGuild();
	
	IChannel getChannel();
	
	IUser getUser();
	
	IMessage getMessage();
	
}
