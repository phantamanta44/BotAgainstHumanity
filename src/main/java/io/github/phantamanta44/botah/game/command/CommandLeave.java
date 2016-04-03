package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandLeave implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("unbind");

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Unbinds the bot from a channel.";
	}

	@Override
	public String getUsage() {
		return "leave";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()) {
			ctx.sendMessage("You can't do this in a private channel!");
			return;
		}
		if (GameManager.getChannel() == null) {
			ctx.sendMessage("Bot is not currently bound to a channel!");
			return;
		}
		IChannel chan = GameManager.getChannel();
		if (!chan.getID().equalsIgnoreCase(ctx.getChannel().getID())) {
			ctx.sendMessage("Bot is currently bound to %s / %s!", chan.getGuild().getName(), chan.getName());
			return;
		}
		if (GameManager.isPlaying()) {
			ctx.sendMessage("There is a game in progress!");
			return;
		}
		GameManager.setChannel(null);
		ctx.sendMessage("Bot unbound.");
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
