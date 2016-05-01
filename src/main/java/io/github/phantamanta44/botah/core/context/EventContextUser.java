package io.github.phantamanta44.botah.core.context;

import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.api.Event;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;

public class EventContextUser implements IEventContext {

	private long timestamp;
	private IUser user;
	private Class<? extends Event> eventType;
	
	public EventContextUser(IUser src, Class<? extends Event> clazz) {
		timestamp = System.currentTimeMillis();
		user = src;
		eventType = clazz;
	}
	
	@Override
	public void sendMessage(String msg) {
		try {
			MessageUtils.sendMessage(Discord.getInstance().getOrCreatePMChannel(user), msg);
		} catch (DiscordException | HTTP429Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String format, Object... args) {
		sendMessage(String.format(format, args));
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public Class<? extends Event> getType() {
		return eventType;
	}

	@Override
	public IGuild getGuild() {
		return null;
	}

	@Override
	public IChannel getChannel() {
		return null;
	}

	@Override
	public IUser getUser() {
		return user;
	}

	@Override
	public IMessage getMessage() {
		return null;
	}

}
