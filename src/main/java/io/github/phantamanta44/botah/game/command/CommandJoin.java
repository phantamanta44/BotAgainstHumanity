package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandJoin implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("bind");

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Binds the bot to a channel.";
	}

	@Override
	public String getUsage() {
		return "join";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (ctx.getChannel().isPrivate()) {
			ctx.sendMessage("You can't do this in a private channel!");
			return;
		}
		if (GameManager.getChannel() != null) {
			IChannel chan = GameManager.getChannel();
			ctx.sendMessage("Bot is currently bound to %s / %s!", chan.getGuild().getName(), chan.getName());
			return;
		}
		GameManager.setChannel(ctx.getChannel());
		ctx.sendMessage("Bot bound to %s / %s.", ctx.getGuild().getName(), ctx.getChannel().getName());
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
