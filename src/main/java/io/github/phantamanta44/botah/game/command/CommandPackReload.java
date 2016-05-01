package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.deck.PackRegistry;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPackReload implements ICommand {

	@Override
	public String getName() {
		return "pmreload";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Reload pack manager data.";
	}

	@Override
	public String getUsage() {
		return "pmreload";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		PackRegistry.load();
		ctx.sendMessage("Pack manager data reloaded.");
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
