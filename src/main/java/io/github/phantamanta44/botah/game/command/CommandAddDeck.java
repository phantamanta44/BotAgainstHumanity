package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.deck.DeckManager;
import io.github.phantamanta44.botah.game.GameManager;
import io.github.phantamanta44.botah.game.deck.Deck;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collections;
import java.util.List;

public class CommandAddDeck implements ICommand {

	@Override
	public String getName() {
		return "adddeck";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Adds a deck in CAH Creator format.";
	}

	@Override
	public String getUsage() {
		return "adddeck <url> [url...]";
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
		if (args.length < 1) {
			ctx.sendMessage("You must specify a valid URL to a deck file!");
			return;
		}
		for (String arg : args) {
			try {
				Deck deck = DeckManager.loadDeck(arg);
				DeckManager.addDeck(deck);
				ctx.sendMessage("Loaded deck: %s", deck.getName());
			} catch (Exception e) {
				ctx.sendMessage("Encountered %s while loading deck: `%s`", e.getClass().getName(), e.getMessage());
			}
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
