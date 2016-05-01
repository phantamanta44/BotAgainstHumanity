package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.deck.DeckManager;
import io.github.phantamanta44.botah.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandStart implements ICommand {

	@Override
	public String getName() {
		return "start";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Start a game.";
	}

	@Override
	public String getUsage() {
		return "start <#players>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		if (GameManager.getChannel() == null) {
			ctx.sendMessage("You must bind the bot to a channel with `%sjoin` before playing!", BotMain.getPrefix());
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
		if (DeckManager.getDecks().size() < 1) {
			ctx.sendMessage("You need to add at least one deck to play!");
			return;
		}
		if (DeckManager.getDecks().stream().noneMatch(d -> d.getWhites().size() > 0)
				|| DeckManager.getDecks().stream().noneMatch(d -> d.getBlacks().size() > 0)) {
			ctx.sendMessage("Your decks must have at least one black and one white card!");
			return;
		}
		try {
			int players = Integer.parseInt(args[0]);
			if (players < 2)
				throw new NumberFormatException();
			GameManager.start(players);
		} catch (IndexOutOfBoundsException|NumberFormatException e) {
			ctx.sendMessage("Must specify a valid number of players!");
		}
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
