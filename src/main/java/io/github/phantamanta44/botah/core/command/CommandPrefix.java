package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPrefix implements ICommand {
	
	@Override
	public String getName() {
		return "chpref";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Change the command prefix.";
	}

	@Override
	public String getUsage() {
		return "chpref <prefix>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You need to provide a prefix!");
			return;
		}
		String pref = MessageUtils.concat(args);
		if ((pref.startsWith("'") && pref.endsWith("'"))
				|| (pref.startsWith("\"") && pref.endsWith("\"")))
			pref = pref.substring(1, pref.length() - 1);
		BotMain.setPrefix(pref);
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
