package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.core.ICTListener;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CommandDispatcher implements ICTListener {
	
	private static final Map<String, ICommand> cmdMapping = new ConcurrentHashMap<>();
	private static final Map<String, ICommand> aliasMapping = new ConcurrentHashMap<>();
	private static final Map<String, ICommand> regexMapping = new ConcurrentHashMap<>();
	
	public static void registerCommand(ICommand cmd) {
		cmdMapping.put(cmd.getName().toLowerCase(), cmd);
		aliasMapping.put(cmd.getName().toLowerCase(), cmd);
		if (cmd.getEnglishInvocation() != null && !cmd.getEnglishInvocation().isEmpty())
			regexMapping.put(cmd.getEnglishInvocation(), cmd);
		cmd.getAliases().forEach(a -> aliasMapping.put(a.toLowerCase(), cmd));
	}
	
	public static void unregisterCommand(ICommand cmd) {
		cmdMapping.remove(cmd.getName().toLowerCase(), cmd);
		aliasMapping.remove(cmd.getName().toLowerCase(), cmd);
		if (cmd.getEnglishInvocation() != null && !cmd.getEnglishInvocation().isEmpty())
			regexMapping.remove(cmd.getEnglishInvocation(), cmd);
		cmd.getAliases().forEach(a -> aliasMapping.remove(a.toLowerCase(), cmd));
	}
	
	public CommandDispatcher() {
		cmdMapping.clear();
		aliasMapping.clear();
	}
	
	public static Stream<ICommand> streamCommands() {
		return cmdMapping.values().stream();
	}

	@ListenTo
	public void onMessageReceived(MessageReceivedEvent event, IEventContext ctx) {
		processEvent(ctx.getMessage().getAuthor(), ctx.getMessage().getContent(), ctx);
	}
	
	private void processEvent(IUser sender, String msg, IEventContext ctx) {
		String pref = BotMain.getPrefix();
		if (!msg.toLowerCase().startsWith(pref.toLowerCase())) {
			if (ctx.getChannel().isPrivate())
				parseEnglishInvoc(ctx.getUser(), msg, ctx);
			return;
		}
		String[] msgSplit = msg.substring(pref.length()).split("\\s");
		String cmd = msgSplit[0];
		String[] args;
		if (msgSplit.length > 1)
			args = Arrays.copyOfRange(msgSplit, 1, msgSplit.length);
		else
			args = new String[0];
		processCommand(sender, cmd, args, ctx);
	}
	
	@ListenTo
	public void onMention(MentionEvent event, IEventContext ctx) {
		String msg = event.getMessage().getContent(), men = Discord.getInstance().getBot().mention();
		IUser sender = event.getMessage().getAuthor();
		if (!msg.startsWith(men) && !msg.endsWith(men))
			return;
		parseEnglishInvoc(sender, msg, ctx);
	}
	
	private void parseEnglishInvoc(IUser sender, String msg, IEventContext ctx) {
		for (Entry<String, ICommand> entry : regexMapping.entrySet()) {
			Matcher m = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE).matcher(msg);
			if (!m.matches())
				continue;
			ICommand cmd = entry.getValue();
			List<String> args = new ArrayList<>();
			for (int i = 0; true; i++) {
				try {
					String g = m.group("a" + i);
					if (g == null)
						break;
					args.add(g);
				} catch (IllegalArgumentException ex) {
					break;
				}
			}
			if (!ctx.getChannel().isPrivate()) {
				BotMain.logger.info("E %s/%s %s: \"%s\" for %s %s", ctx.getGuild().getName(),
						ctx.getChannel().getName(),	ctx.getUser().getName(), msg, cmd.getName(),
						args.stream().reduce((a, b) -> a.concat(" ").concat(b)).orElse(""));
			} else {
				BotMain.logger.info("E %s: \"%s\" for %s %s",
						ctx.getUser().getName(), msg, cmd.getName(),
						args.stream().reduce((a, b) -> a.concat(" ").concat(b)).orElse(""));
			}
			if (cmd.canUseCommand(sender, ctx))
				cmd.execute(sender, args.toArray(new String[0]), ctx);
			else
				ctx.sendMessage("%s: %s", sender.mention(), cmd.getPermissionMessage(sender, ctx));
		}
	}
	
	private void processCommand(IUser sender, String cmdName, String[] args, IEventContext ctx) {
		ICommand cmd;
		if ((cmd = aliasMapping.get(cmdName)) != null) {
			String guildName = !ctx.getChannel().isPrivate() ? ctx.getGuild().getName() + "/" : "";
			BotMain.logger.info("C %s%s %s: %s %s", guildName, ctx.getChannel().getName(),
					ctx.getUser().getName(), cmd.getName(), MessageUtils.concat(args));
			if (cmd.canUseCommand(sender, ctx))
				cmd.execute(sender, args, ctx);
			else
				ctx.sendMessage("%s: %s", sender.mention(), cmd.getPermissionMessage(sender, ctx));
		}
	}
	
}