package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.DeckManager;
import io.github.phantamanta44.botah.game.GameManager;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandRmDeck implements ICommand {

	private static final List<String> ALIASES = Collections.singletonList("removedeck");

	@Override
	public String getName() {
		return "rmdeck";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Removes a deck.";
	}

	@Override
	public String getUsage() {
		return "rmdeck <name>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (GameManager.getChannel() == null) {
			ctx.sendMessage("Bot is not bound to a channel!");
			return;
		}
		IChannel chan = GameManager.getChannel();
		if (!chan.getID().equalsIgnoreCase(ctx.getChannel().getID())) {
			ctx.sendMessage("Bot is currently bound to %s / %s!", chan.getGuild().getName(), chan.getName());
			return;
		}
		if (GameManager.isPlaying()) {
			ctx.sendMessage("A game is already in progress!");
			return;
		}
		if (DeckManager.removeDeck(MessageUtils.concat(args)))
			ctx.sendMessage("Successfully removed.");
		else
			ctx.sendMessage("No such deck!");
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
