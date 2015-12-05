package org.vaadin.hackathonofthings.data;

/**
 * Event with a single data point.
 */
public class DataEvent {
	private final Topic topic;
	private final long timestamp;
	private final double[] data;
	
	public DataEvent(Topic topic, long timestamp, double[] data) {
		this.topic = topic;
		this.timestamp = timestamp;
		this.data = data;
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public double[] getData() {
		return data;
	}
}
