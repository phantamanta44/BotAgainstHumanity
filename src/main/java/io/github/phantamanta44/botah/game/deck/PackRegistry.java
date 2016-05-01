package io.github.phantamanta44.botah.game.deck;

import com.google.gson.*;
import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.util.MessageUtils;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class PackRegistry {

	private static final File DB_FILE = new File("cahpacks.json");
	private static final Map<String, Collection<Deck>> packs = new ConcurrentHashMap<>();

	public static void load() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.create();
		try (PrintWriter strOut = new PrintWriter(new FileWriter(DB_FILE))) {
			JsonObject sets = new JsonObject();
			packs.forEach((n, s) -> {
				JsonArray packs = new JsonArray();
				s.forEach(d -> packs.add(serialize(d)));
				sets.add(n, packs);
			});
			strOut.println();
		} catch (Exception e) {
			BotMain.logger.severe("Failed to save pack data!");
			e.printStackTrace();
		}
	}

	public static void save() {
		JsonParser parser = new JsonParser();
		try (BufferedReader strIn = new BufferedReader(new FileReader(DB_FILE))) {
			packs.clear();
			JsonObject data = parser.parse(strIn).getAsJsonObject();
			data.entrySet().forEach(e -> {
				Collection<Deck> decks = new CopyOnWriteArrayList<>();
				e.getValue().getAsJsonArray().forEach(d -> decks.add(deserialize(d.getAsJsonObject())));
				packs.put(e.getKey(), decks);
			});
		} catch (Exception e) {
			BotMain.logger.severe("Failed to load pack data!");
			e.printStackTrace();
		}
	}

	public static Collection<Deck> getSet(String name) {
		return packs.entrySet().stream()
				.filter(p -> MessageUtils.lenientMatch(p.getKey(), name))
				.map(Map.Entry::getValue)
				.findAny().orElse(null);
	}

	public static Stream<Map.Entry<String, Collection<Deck>>> stream() {
		return packs.entrySet().stream();
	}

	public static boolean register(String name, Collection<Deck> decks) {
		if (packs.keySet().stream().anyMatch(k -> MessageUtils.lenientMatch(k, name)))
			return false;
		packs.put(name, decks);
		save();
		return true;
	}

	public static String unregister(String name) {
		Iterator<Map.Entry<String, Collection<Deck>>> iter = packs.entrySet().iterator();
		while (iter.hasNext()) {
			String pack = iter.next().getKey();
			if (MessageUtils.lenientMatch(pack, name)) {
				iter.remove();
				save();
				return pack;
			}
		}
		return null;
	}

	private static JsonObject serialize(Deck deck) {
		JsonObject ser = new JsonObject();
		ser.addProperty("name", deck.getName());
		JsonArray white = new JsonArray(), black = new JsonArray();
		deck.getWhites().forEach(white::add);
		deck.getBlacks().forEach(c -> {
			JsonObject card = new JsonObject();
			card.addProperty("text", c.text);
			card.addProperty("pick", c.pick);
			black.add(card);
		});
		ser.add("blackCards", black);
		ser.add("whiteCards", white);
		return ser;
	}

	private static Deck deserialize(JsonObject dto) {
		return Deck.parse(dto);
	}

}
