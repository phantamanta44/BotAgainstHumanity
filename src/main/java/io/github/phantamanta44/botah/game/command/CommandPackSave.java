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

public class CommandPackSave implements ICommand {

	@Override
	public String getName() {
		return "pmsave";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Save the current card packs as a set.";
	}

	@Override
	public String getUsage() {
		return "pmsave <name>";
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
		Collection<Deck> decks = DeckManager.getDecks();
		if (decks.isEmpty()) {
			ctx.sendMessage("No card packs found!");
			return;
		}

		if (args.length < 1) {
			ctx.sendMessage("You must specify a name for the set!");
			return;
		}

		String name = MessageUtils.concat(args);
		if (PackRegistry.register(name, decks))
			ctx.sendMessage("Registered pack set '%s'.", name);
		else
			ctx.sendMessage("Card pack set already exists!");
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
