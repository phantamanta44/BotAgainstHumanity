package io.github.phantamanta44.botah.util;

import io.github.phantamanta44.botah.BotMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class IniConfig {

	private final File configFile;
	private final Map<String, String> configKeys = new HashMap<>();
	
	public IniConfig(String filename) {
		this(new File(filename));
	}
	
	public IniConfig(File file) {
		configFile = file;
	}
	
	public void read() {
		configKeys.clear();
		try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				String[] parts = line.split("=", 2);
				if (parts.length < 2) {
					BotMain.logger.warn("Invalid config line:\n\t%s", line);
					continue;
				}
				configKeys.put(parts[0], parts[1]);
			}
		} catch (Exception e) {
			BotMain.logger.severe("Error reading from config!");
			e.printStackTrace();
		}
	}
	
	public String get(String key) {
		return configKeys.get(key);
	}
	
	public boolean getBoolean(String key) {
		String val = get(key);
		return val != null && val.equalsIgnoreCase("true");
	}
	
	public int getInt(String key) {
		String val = get(key);
		if (val == null)
			return 0;
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public float getFloat(String key) {
		String val = get(key);
		if (val == null)
			return 0F;
		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException e) {
			return 0F;
		}
	}
	
	public Stream<Entry<String, String>> stream() {
		return configKeys.entrySet().stream();
	}
	
}
