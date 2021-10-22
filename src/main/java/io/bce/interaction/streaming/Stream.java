package io.bce.interaction.streaming;

import io.bce.promises.Promise;

/**
 * This interface describes the data stream abstraction. Data stream is the functional element,
 * transmits data from source to destination and notifies about successfully transferring completion
 * or transferring error.
 *  
 * @author Dmitry Mikhaylenko
 *
 * @param <T> The data type stream
 */
public interface Stream<T> {
	/**
	 * Start data streaming between source and destination
	 * 
	 * @return The promise resolving by transferred data count
	 */
	public Promise<Stat> start();

	public interface Stat {
		public Long getSize();
	}
}
