package io.github.phantamanta44.botah.game.deck;

import com.google.gson.JsonObject;
import io.github.phantamanta44.botah.util.SafeJsonWrapper;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Deck {

	private final List<String> white;
	private final List<BlackCard> black;
	private final String name;

	public Deck(String name) {
		this.name = name;
		this.white = new CopyOnWriteArrayList<>();
		this.black = new CopyOnWriteArrayList<>();
	}

	public Deck(String name, Collection<String> white, Collection<BlackCard> black) {
		this.name = name;
		this.white = new CopyOnWriteArrayList<>(white);
		this.black = new CopyOnWriteArrayList<>(black);
	}

	public void addWhites(Collection<String> cards) {
		white.addAll(cards);
	}

	public void addBlacks(Collection<BlackCard> cards) {
		black.addAll(cards);
	}

	public String getWhite() {
		return white.remove((int)Math.floor(Math.random() * white.size()));
	}

	public BlackCard getBlack() {
		return black.remove((int)Math.floor(Math.random() * black.size()));
	}

	public Collection<String> getWhites() {
		return white;
	}

	public Collection<BlackCard> getBlacks() {
		return black;
	}

	public String getName() {
		return name;
	}

	public static Deck parse(JsonObject dto) {
		SafeJsonWrapper data = new SafeJsonWrapper(dto);
		Deck deck = new Deck(data.getString("name"));
		deck.addWhites(StreamSupport.stream(data.getJsonArray("whiteCards").spliterator(), true)
				.map(e -> StringEscapeUtils.unescapeHtml4(e.getAsString()))
				.collect(Collectors.toList()));
		deck.addBlacks(StreamSupport.stream(data.getJsonArray("blackCards").spliterator(), true)
				.map(e -> {
					JsonObject o = e.getAsJsonObject();
					String text = StringEscapeUtils.escapeHtml4(o.get("text").getAsString().replaceAll("<br/?>", "\n"));
					return new BlackCard(text, o.get("pick").getAsInt());
				})
				.collect(Collectors.toList()));
		return deck;
	}

	public void removeBlacks(List<BlackCard> cards) {
		black.removeAll(cards);
	}

	public void removeBlack(BlackCard card) {
		black.remove(card);
	}

	public void remove(List<String> cards) {
		white.removeAll(cards);
	}

	public void remove(String card) {
		white.remove(card);
	}

}
