package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.RevokeHandler;
import io.github.phantamanta44.botah.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;

public class CommandUnsay implements ICommand {

	private static final List<String> ALIASES = Arrays.asList("delete", "revoke");
	
	@Override
	public String getName() {
		return "unsay";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Revokes a previous message sent by the bot.";
	}

	@Override
	public String getUsage() {
		return "unsay [#count]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		RevokeHandler.procCmd(sender, args, ctx);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return BotMain.isAdmin(sender);
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		return "No permission!";
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
	