package io.github.phantamanta44.botah.game;

import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.core.EventDispatcher;
import io.github.phantamanta44.botah.core.ICTListener;
import io.github.phantamanta44.botah.core.command.CommandDispatcher;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.core.rate.RateLimitedChannel;
import io.github.phantamanta44.botah.game.command.*;
import io.github.phantamanta44.botah.game.deck.BlackCard;
import io.github.phantamanta44.botah.game.deck.DeckManager;
import io.github.phantamanta44.botah.game.deck.PackRegistry;
import io.github.phantamanta44.botah.util.MessageUtils;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.HTTP429Exception;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameManager {

	private static final int CARD_CNT = 7;
	private static final int WIN_LIM = 5;

	private static IChannel chan;
	private static int ind = 0, havePlayed = 0;
	private static IUser[] players;
	private static Hand[] hands;
	private static State state = State.OFF;
	private static ICTListener listener;
	private static BlackCard blackCard;
	private static Map<IUser, List<String>> plays;
	private static List<Map.Entry<IUser, List<String>>> choices;

	public static void registerListeners() {
		CommandDispatcher.registerCommand(new CommandJoin());
		CommandDispatcher.registerCommand(new CommandLeave());
		CommandDispatcher.registerCommand(new CommandStart());
		CommandDispatcher.registerCommand(new CommandStop());
		CommandDispatcher.registerCommand(new CommandLsDeck());
		CommandDispatcher.registerCommand(new CommandAddDeck());
		CommandDispatcher.registerCommand(new CommandRmDeck());
		CommandDispatcher.registerCommand(new CommandPackApply());
		CommandDispatcher.registerCommand(new CommandPackDelete());
		CommandDispatcher.registerCommand(new CommandPackList());
		CommandDispatcher.registerCommand(new CommandPackSave());
		CommandDispatcher.registerCommand(new CommandPackReload());

		PackRegistry.load();
	}

	public static void setChannel(IChannel chan) {
		GameManager.chan = chan;
		DeckManager.clearDeck();
		DeckManager.addStandardDecks();
	}

	public static IChannel getChannel() {
		return chan;
	}

	public static boolean isPlaying() {
		return state != State.OFF;
	}

	public static void stop() {
		state = State.OFF;
		ind = 0;
		EventDispatcher.unregisterHandler(listener);
		players = null;
		hands = null;
		blackCard = null;
		plays = null;
		choices = null;
	}

	public static void start(int cnt) {
		state = State.WAITING;
		players = new IUser[cnt];
		listener = new GameListener();
		EventDispatcher.registerHandler(listener);
		MessageUtils.sendMessage(chan, "**A Cards Against Humanity game is starting!**\nType `count me in` to join the fun!");
	}

	private static void procMsg(IEventContext ctx) {
		IUser sender = ctx.getUser();
		if (state == State.WAITING) {
			if (ctx.getMessage().getContent().equalsIgnoreCase("count me in")
					&& Arrays.stream(players).filter(p -> p != null).noneMatch(p -> p.getID().equalsIgnoreCase(ctx.getUser().getID()))) {
				players[ind++] = sender;
				ctx.sendMessage("**%s** has joined the game! (%d/%d)", sender.mention(), ind, players.length);
			}
			if (ind >= players.length) {
				state = State.PLAYING;
				ind = players.length - 1;
				DeckManager.reshuffle();
				hands = new Hand[players.length];
				plays = new ConcurrentHashMap<>();
				for (int i = 0; i < players.length; i++) {
					hands[i] = new Hand(CARD_CNT);
					plays.put(players[i], new CopyOnWriteArrayList<>());
				}
				nextTurn();
			}
		}
		else if (state == State.JUDGING && players[ind].getID().equalsIgnoreCase(ctx.getUser().getID()))
			judge(ctx);
	}

	private static void nextTurn() {
		ind = (ind + 1) % players.length;
		havePlayed = 0;
		MessageUtils.sendMessage(chan, "```%s```\n**%s is the Card Czar!**", getPlayerState(), players[ind].getName());
		try {
			blackCard = DeckManager.getBlack();
		} catch (IndexOutOfBoundsException e) {
			MessageUtils.sendMessage(chan, "Reshuffling deck...");
			reshuffle();
			blackCard = DeckManager.getBlack();
		}
		plays.forEach((k, v) -> v.clear());
		MessageUtils.sendMessage(chan, "__**Black Card**__\n`%s`\n*(Pick %d card(s))*", blackCard.text, blackCard.pick);
		distPm();
	}

	private static void distPm() {
		for (int i = 0; i < players.length; i++) {
			if (i == ind)
				continue;
			IChannel pm = null;
			try {
				pm = new RateLimitedChannel(Discord.getInstance().getOrCreatePMChannel(players[i]));
			} catch (DiscordException | HTTP429Exception e) {
				e.printStackTrace();
			}
			MessageUtils.sendMessage(pm, "`%s`\n\n__**Your Hand**__\n%s\n*(Pick %d card(s)) *", blackCard.text, hands[i], blackCard.pick);
		}
	}

	private static void procPm(IEventContext ctx) {
		if (players[ind].getID().equalsIgnoreCase(ctx.getUser().getID())) {
			if (state == State.JUDGING)
				judge(ctx);
			else
				ctx.sendMessage("You are the Card Czar! You can't play a card this round.");
		} else {
			if (state == State.PLAYING) {
				List<String> play = plays.get(ctx.getUser());
				if (play.size() < blackCard.pick) {
					try {
						int cardNum = Integer.parseInt(ctx.getMessage().getContent());
						List<String> hand = hands[indexOf(ctx.getUser())].cards;
						String card = hand.get(cardNum);
						hand.remove(cardNum);
						play.add(card);
						ctx.sendMessage("Played \"%s\". (%d/%d)", card.replaceAll("\\.", ""), play.size(), blackCard.pick);
						if (play.size() >= blackCard.pick)
							onPlayed(ctx.getUser());
					} catch (IndexOutOfBoundsException|NumberFormatException e) {
						ctx.sendMessage("Invalid card index!");
					}
				}
				else
					ctx.sendMessage("You've can't pick any more cards!");
			}
			else
				ctx.sendMessage("The Card Czar is currently choosing a winner!");
		}
	}

	private static void onPlayed(IUser player) {
		MessageUtils.sendMessage(chan, "%s played their card(s). (%d/%d)", player.getName(), ++havePlayed, players.length - 1);
		if (havePlayed >= players.length - 1) {
			state = State.JUDGING;
			choices = new CopyOnWriteArrayList<>(plays.entrySet().stream()
					.filter(e -> !e.getValue().isEmpty())
					.sequential()
					.collect(Collectors.toList()));
			MessageUtils.sendMessage(chan, "__**Winner Selection**__\n%s", choices.stream()
					.map(c -> String.format("%d | %s", choices.indexOf(c) + 1, blackCard.supplant(c.getValue())))
					.reduce((a, b) -> a.concat("\n").concat(b)).get());
		}
	}

	private static void judge(IEventContext ctx) {
		String msg = ctx.getMessage().getContent();
		try {
			IUser winner = choices.get(Integer.parseInt(msg) - 1).getKey();
			MessageUtils.sendMessage(chan, "**%s won this round!**", winner.getName());
			Hand hand = hands[indexOf(winner)];
			hand.win(blackCard);
			if (hand.blackCards.size() >= WIN_LIM) {
				MessageUtils.sendMessage(chan, "**%s reached %d points, winning the game! Congratulations!**", winner.mention(), WIN_LIM);
				stop();
			} else {
				state = State.PLAYING;
				redraw();
				nextTurn();
			}
		} catch (IndexOutOfBoundsException e) {
			ctx.sendMessage("Specified choice number does not exist!");
		} catch (NumberFormatException e) { }
	}

	private static void redraw() {
		Arrays.stream(hands).forEach(h -> h.drawTo(CARD_CNT));
	}

	private static void reshuffle() {
		DeckManager.reshuffle();
		DeckManager.removeCards(Arrays.stream(hands)
				.flatMap(h -> h.cards.stream())
				.collect(Collectors.toList()));
		DeckManager.removeBlacks(Arrays.stream(hands)
				.flatMap(h -> h.blackCards.stream())
				.collect(Collectors.toList()));
		DeckManager.removeBlack(blackCard);
	}

	private static int indexOf(IUser user) {
		for (int i = 0; i < players.length; i++) {
			if (players[i].getID().equalsIgnoreCase(user.getID()))
				return i;
		}
		return -1;
	}

	public static String getPlayerState() {
		StringBuilder scores = new StringBuilder();
		for (int i = 0; i < players.length; i++)
			scores.append("\n").append(String.format("%s: %d point(s)", players[i].getName(), hands[i].blackCards.size()));
		return scores.toString();
	}

	private static class Hand {

		private final List<String> cards = new CopyOnWriteArrayList<>();
		private final List<BlackCard> blackCards = new CopyOnWriteArrayList<>();

		private Hand(int cards) {
			draw(cards);
		}

		private void draw(int cnt) {
			for (int i = 0; i < cnt; i++) {
				try {
					cards.add(DeckManager.getWhite());
				} catch (IndexOutOfBoundsException e) {
					MessageUtils.sendMessage(chan, "Reshuffling deck...");
					reshuffle();
					cards.add(DeckManager.getWhite());
				}
			}
		}

		private void drawTo(int cnt) {
			draw(Math.max(cnt - cards.size(), 0));
		}

		private void draw(String card) {
			cards.add(card);
		}

		private void win(BlackCard card) {
			blackCards.add(card);
		}

		@Override
		public String toString() {
			return cards.stream()
					.map(c -> String.format("%d | %s", cards.indexOf(c), c))
					.reduce((a, b) -> a.concat("\n").concat(b)).get();
		}

	}

	public static class GameListener implements ICTListener {

		@ICTListener.ListenTo
		public void onMessage(MessageReceivedEvent event, IEventContext ctx) {
			if (ctx.getChannel().getID().equalsIgnoreCase(chan.getID()))
				procMsg(ctx);
			else if (ctx.getChannel().isPrivate()
					&& Arrays.stream(players).anyMatch(p -> ctx.getUser().getID().equalsIgnoreCase(p.getID())))
				procPm(ctx);
		}

	}

	private enum State {

		OFF, WAITING, PLAYING, JUDGING

	}

}
