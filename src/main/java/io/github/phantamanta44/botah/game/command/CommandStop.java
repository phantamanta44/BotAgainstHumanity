package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandStop implements ICommand {

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "End a game.";
	}

	@Override
	public String getUsage() {
		return "stop";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (GameManager.getChannel() == null) {
			ctx.sendMessage("Bot isn't bound to a channel!", BotMain.getPrefix());
			return;
		}
		IChannel chan = GameManager.getChannel();
		if (!chan.getID().equalsIgnoreCase(ctx.getChannel().getID())) {
			ctx.sendMessage("Bot is currently bound to %s / %s!", chan.getGuild().getName(), chan.getName());
			return;
		}
		if (!GameManager.isPlaying()) {
			ctx.sendMessage("There is no game in progress!");
			return;
		}
		GameManager.stop();
		ctx.sendMessage("Game cancelled.");
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
