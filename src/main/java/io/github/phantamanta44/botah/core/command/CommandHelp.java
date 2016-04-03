package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.context.IEventContext;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;

public class CommandHelp implements ICommand {

	private static final List<String> ALIASES = Arrays.asList("?", "helpme");

	@Override
	public String getName() {
		return "help";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Lists registered commands.";
	}

	@Override
	public String getUsage() {
		return "help";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		String helpText = CommandDispatcher.streamCommands()
				.map(c -> String.format("%s%s - %s", BotMain.getPrefix(), c.getUsage(), c.getDesc()))
				.sorted()
				.reduce((a, b) -> a.concat("\n").concat(b)).get();
		ctx.sendMessage("**Command list:**\n```%s```", helpText);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}