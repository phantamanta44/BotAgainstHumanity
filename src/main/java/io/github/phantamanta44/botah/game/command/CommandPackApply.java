package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.GameManager;
import io.github.phantamanta44.botah.game.deck.Deck;
import io.github.phantamanta44.botah.game.deck.DeckManager;
import io.github.phantamanta44.botah.game.deck.PackRegistry;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommandPackApply implements ICommand {

	@Override
	public String getName() {
		return "pmapply";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Apply a card pack set, clearing all currently loaded packs.";
	}

	@Override
	public String getUsage() {
		return "pmapply <name>";
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
			ctx.sendMessage("You must specify a name for the set!");
			return;
		}

		Collection<Deck> set = PackRegistry.getSet(MessageUtils.concat(args));
		if (set == null) {
			ctx.sendMessage("No such card pack set!");
			return;
		}
		DeckManager.clearDeck();
		DeckManager.addDecks(set);
		ctx.sendMessage("Loaded pack successfully!");
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
