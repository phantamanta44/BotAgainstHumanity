
package io.github.phantamanta44.botah.core;

import io.github.phantamanta44.botah.BotMain;
import io.github.phantamanta44.botah.Discord;
import io.github.phantamanta44.botah.core.context.IEventContext;
import io.github.phantamanta44.botah.util.MathUtils;
import sx.blah.discord.handle.impl.events.MessageSendEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RevokeHandler implements ICTListener {

	private static Map<String, Deque<IMessage>> msgStacks = new ConcurrentHashMap<>();
	
	@ICTListener.ListenTo
	public void onMessageSend(MessageSendEvent event, IEventContext ctx) {
		if (ctx.getUser().getID().equalsIgnoreCase(Discord.getInstance().getOurUser().getID())) {
			String id = ctx.getChannel().getID();
			if (!msgStacks.containsKey(id))
				msgStacks.put(id, new ConcurrentLinkedDeque<>());
			msgStacks.get(id).offer(ctx.getMessage());
			int qSize = BotMain.config.getInt("msgQueueSize");
			if (qSize < 1)
				qSize = 50;
			while (msgStacks.get(id).size() > qSize)
				msgStacks.get(id).pop();
		}
	}
	
	public static void procCmd(IUser sender, String[] args, IEventContext ctx) {
		Deque<IMessage> msgStack = msgStacks.get(ctx.getChannel().getID());
		if (msgStack == null)
			return;
		
		int toDelete = 1;
		try {
			toDelete = MathUtils.clamp(Integer.parseInt(args[0]), 1, 10);
		} catch (Exception ex) { }
		
		for (int i = 0; i < toDelete; i++) {
			IMessage td = msgStack.pollLast();
			if (td == null)
				break;
			try {
				td.delete();
			} catch (Exception ex) {
				BotMain.logger.warn(ex.getMessage());
				toDelete++;
			}
		}
	}
	
}
