package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.deck.PackRegistry;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandPackList implements ICommand {

	@Override
	public String getName() {
		return "pmlist";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "List card pack sets.";
	}

	@Override
	public String getUsage() {
		return "pmlist";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		ctx.sendMessage("__**Registered Sets:**__\n%s", PackRegistry.stream()
				.map(d -> String.format("- %s", d.getKey()))
				.reduce((a, b) -> a.concat("\n").concat(b)).orElse("No sets found!"));
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
