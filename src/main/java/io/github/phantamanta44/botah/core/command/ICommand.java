package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface ICommand {

	String getName();
	
	List<String> getAliases();
	
	String getDesc();

	String getUsage();
	
	void execute(IUser sender, String[] args, IEventContext ctx);
	
	boolean canUseCommand(IUser sender, IEventContext ctx);
	
	String getPermissionMessage(IUser sender, IEventContext ctx);
	
	String getEnglishInvocation();
	
}