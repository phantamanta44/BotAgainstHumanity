package io.github.phantamanta44.botah.game.deck;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.core.ICTListener;
import io.github.phantamanta44.botah.util.MessageUtils;
import io.github.phantamanta44.botah.util.http.HttpException;
import io.github.phantamanta44.botah.util.http.HttpUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DeckManager implements ICTListener {

	private static final Collection<String> BASE_URLS = Arrays.asList(
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/b_000_cah.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_000_first_expansion.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_001_second_expansion.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_002_third_expansion.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_003_fourth_expansion.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_004_box_expansion.json",
			"https://cdn.rawgit.com/Rylius/CardsAgainstEquestria/master/data/decks/e_016_misprints.json"
	);
	private static Collection<Deck> BASE_DECKS;

	static {
		try {
			BASE_DECKS = Collections.unmodifiableList(BASE_URLS.stream()
					.map(Throwing.function(DeckManager::loadDeck))
					.collect(Collectors.toList())
			);
		} catch (Exception e) {
			BotMain.logger.severe("Failed to load base decks!");
			e.printStackTrace();
		}
	}

	private static Map<String, Deck> decks = new ConcurrentHashMap<>();
	private static Deck deck;

	public static void addDeck(Deck deck) {
		decks.put(deck.getName(), deck);
	}

	public static void addDecks(Collection<Deck> toAdd) {
		toAdd.forEach(DeckManager::addDeck);
	}

	public static boolean removeDeck(String name) {
		boolean didRem = false;
		Iterator<Map.Entry<String, Deck>> iter = decks.entrySet().iterator();
		while (iter.hasNext()) {
			if (MessageUtils.lenientMatch(iter.next().getKey(), name)) {
				iter.remove();
				didRem = true;
			}
		}
		return didRem;
	}

	public static Deck getDeck() {
		return decks.values().stream()
				.reduce(new Deck(""), (a, b) -> {
					a.addBlacks(b.getBlacks());
					a.addWhites(b.getWhites());
					return a;
				});
	}

	public static Collection<Deck> getDecks() {
		return decks.values();
	}

	public static void clearDeck() {
		decks.clear();
	}

	public static void reshuffle() {
		deck = getDeck();
	}

	public static String getWhite() {
		return deck.getWhite();
	}

	public static BlackCard getBlack() {
		return deck.getBlack();
	}

	public static void addStandardDecks() {
		BASE_DECKS.forEach(DeckManager::addDeck);
	}

	public static Deck loadDeck(String url) throws IOException, HttpException {
		return Deck.parse(HttpUtils.requestJson(url).getAsJsonObject());
	}

	public static void removeBlacks(List<BlackCard> cards) {
		deck.removeBlacks(cards);
	}
	
	public static void removeBlack(BlackCard card) {
		deck.removeBlack(card);
	}

	public static void removeCards(List<String> cards) {
		deck.remove(cards);
	}

	public static void removeCard(String card) {
		deck.remove(card);
	}

}
