package io.github.phantamanta44.botah.core.rate;

import io.github.phantamanta44.botah.util.concurrent.IFuture;
import io.github.phantamanta44.botah.util.concurrent.ThreadPoolFactory;
import io.github.phantamanta44.botah.util.concurrent.ThreadPoolFactory.PoolType;
import io.github.phantamanta44.botah.util.concurrent.ThreadPoolFactory.QueueType;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RateLimitQueue {

	private static ScheduledExecutorService taskPool;
	
	private Deque<QueuedAction<?>> queue = new ConcurrentLinkedDeque<>();
	private long ttl = 0L;
	private ScheduledFuture<?> updateFuture;
	
	static {
		taskPool = new ThreadPoolFactory()
				.withPool(PoolType.SCHEDULED)
				.withQueue(QueueType.CACHED)
				.construct();
	}
	
	public <T> IFuture<T> push(Supplier<T> task) {
		QueuedAction<T> action = new QueuedAction<T>(task);
		queue.offer(action);
		return action;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean isActive() {
		return ttl > 0;
	}
	
	public void setActive(long time) {
		ttl = Math.max(time, ttl);
		if (updateFuture == null && ttl > 0)
			updateFuture = taskPool.schedule(new UpdateTask(this), time, TimeUnit.MILLISECONDS);
	}
	
	public void setInactive() {
		ttl = 0L;
		updateFuture.cancel(false);
		updateFuture = null;
		queue.forEach(QueuedAction::doAction);
	}

	private static class UpdateTask implements Runnable {

		private RateLimitQueue parent;

		private UpdateTask(RateLimitQueue parent) {
			this.parent = parent;
		}
	
		@Override
		public void run() {
			if (parent.queue.isEmpty())
				parent.setInactive();
			QueuedAction<?> action = parent.queue.pop();
			synchronized (action) {
				action.doAction();
				action.notify();
			}
			parent.ttl -= 1000L;
			if (parent.ttl <= 0 && parent.isEmpty())
				parent.setInactive();
			else
				parent.updateFuture = taskPool.schedule(new UpdateTask(parent), 1000L, TimeUnit.MILLISECONDS);
		}
		
	}
	
	private static class QueuedAction<T> implements IFuture<T> {
		
		private Supplier<T> action;
		private T result;
		private boolean done = false;
		
		private QueuedAction(Supplier<T> toDo) {
			action = toDo;
		}
		
		private void doAction() {
			result = action.get();
			done = true;
		}

		@Override
		public boolean isDone() {
			return done;
		}

		@Override
		public T getResult() {
			return result;
		}
		
	}
	
}
