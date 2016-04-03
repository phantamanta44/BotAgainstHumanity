package io.github.phantamanta44.botah.util.concurrent;

public interface IFuture<T> {
	
	boolean isDone();
	
	T getResult();
	
}
