package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandGameSet implements ICommand {

	@Override
	public String getName() {
		return "gameset";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Sets the bot's game caption.";
	}

	@Override
	public String getUsage() {
		return "gameset [name]";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 2)
			Discord.getInstance().setGameCaption(null);
		else
			Discord.getInstance().setGameCaption(MessageUtils.concat(args));
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
