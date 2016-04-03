package io.github.phantamanta44.botah.game.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.command.ICommand;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.game.DeckManager;
import io.github.phantamanta44.botah.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;

public class CommandLsDeck implements ICommand {

	private static final List<String> ALIASES = Arrays.asList("decks", "listdecks");

	@Override
	public String getName() {
		return "lsdeck";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Lists decks.";
	}

	@Override
	public String getUsage() {
		return "lsdeck";
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
		ctx.sendMessage("__**Currently Loaded Decks:**__\n%s", DeckManager.getDecks().stream()
				.map(d -> String.format("- %s", d.getName()))
				.reduce((a, b) -> a.concat("\n").concat(b)).orElse("No decks loaded!"));
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
