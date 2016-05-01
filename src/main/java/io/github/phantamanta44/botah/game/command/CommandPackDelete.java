package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.deck.PackRegistry;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPackDelete implements ICommand {

	@Override
	public String getName() {
		return "pmdel";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Delete a card pack set.";
	}

	@Override
	public String getUsage() {
		return "pmdel <name>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (args.length < 1) {
			ctx.sendMessage("You must specify a name for the set!");
			return;
		}

		String name = PackRegistry.unregister(MessageUtils.concat(args));
		if (name != null)
			ctx.sendMessage("Destroyed card pack set '%s'.", name);
		else
			ctx.sendMessage("No such card pack set!");
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
