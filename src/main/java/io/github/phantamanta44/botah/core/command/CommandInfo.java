package io.github.phantamanta44.botah.core.command;

import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

import java.lang.management.ManagementFactory;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class CommandInfo implements ICommand {

	private static final List<String> ALIASES = Arrays.asList("uptime", "about", "stats");

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public List<String> getAliases() {
		return ALIASES;
	}

	@Override
	public String getDesc() {
		return "Gets information about the bot.";
	}

	@Override
	public String getUsage() {
		return "info";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		List<Entry<String, Object>> info = new ArrayList<>();
		info.add(new SimpleEntry<>("Uptime", MessageUtils.formatDuration(ManagementFactory.getRuntimeMXBean().getUptime())));
		info.add(new SimpleEntry<>("Servers", Discord.getInstance().getGuilds().size()));
		info.add(new SimpleEntry<>("Channels", Discord.getInstance().getChannels().size()));
		info.add(new SimpleEntry<>("Users", Discord.getInstance().getUsers().size()));
		Runtime rt = Runtime.getRuntime();
		info.add(new SimpleEntry<>("Used Mem", String.format("%.2f/%.2fMB", (rt.totalMemory() - rt.freeMemory()) / 1000000F, rt.totalMemory() / 1000000F)));
		String infoStr = info.stream()
				.map(e -> e.getKey().concat(": ").concat(String.valueOf(e.getValue())))
				.reduce((a, b) -> a.concat("\n").concat(b)).get();
		ctx.sendMessage("**Bot Information:**\n```%s```\nSource code available at https://github.com/phantamanta44/BotAgainstHumanity", infoStr);
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
