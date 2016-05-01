package io.github.phantamanta44.botah;

import io.github.phantamanta44.botah.core.EventDispatcher;
import io.github.phantamanta44.botah.core.RevokeHandler;
import io.github.phantamanta44.botah.core.command.*;
import io.github.phantamanta44.botah.game.GameManager;
import io.github.phantamanta44.botah.util.IniConfig;
import io.github.phantamanta44.botah.util.http.log.LogWrapper;
import sx.blah.discord.handle.obj.IUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BotMain {
	
	public static final LogWrapper logger = new LogWrapper("BotAH");
	public static final IniConfig config = new IniConfig("botah.conf");
	
	private static final File ADMINS_FILE = new File("admins.txt");
	private static final Set<String> controllers = new HashSet<>();
	private static String prefix;

	public static void main(String[] args) {
		try {
			config.read();
			setPrefix(config.get("prefix"));
			getAdmins();
			Discord.getInstance()
					.buildClient(config.get("token"))
					.onReady(BotMain::registerListeners)
					.login();
		} catch (Exception e) {
			logger.severe("Something went wrong!");
			e.printStackTrace();
		}
	}
	
	private static void registerListeners() {
		CommandDispatcher.registerCommand(new CommandEngInvoc());
		CommandDispatcher.registerCommand(new CommandGameSet());
		CommandDispatcher.registerCommand(new CommandHalt());
		CommandDispatcher.registerCommand(new CommandHelp());
		CommandDispatcher.registerCommand(new CommandInfo());
		CommandDispatcher.registerCommand(new CommandPrefix());
		CommandDispatcher.registerCommand(new CommandUnsay());
		EventDispatcher.registerHandler(new RevokeHandler());
		GameManager.registerListeners();
	}
		
	private static void getAdmins() {
		try (BufferedReader strIn = new BufferedReader(new FileReader(ADMINS_FILE))) {
			String line;
			while ((line = strIn.readLine()) != null)
				controllers.add(line);
		} catch (IOException ex) {
			logger.severe("Error retrieving admin list!");
			ex.printStackTrace();
		}
	}
	
	public static boolean isAdmin(IUser user) {
		return controllers.contains(user.getName());
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static void setPrefix(String newPrefix) {
		prefix = newPrefix;
	}
	
}
