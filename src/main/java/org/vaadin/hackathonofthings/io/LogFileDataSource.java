package org.vaadin.hackathonofthings.io;

import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.Topic;

/**
 * Read and replay a log file stored with {@link FileLogger}.
 */
public class LogFileDataSource extends AbstractFileDataSource {

	private Topic topic;
	
	private long previousTimestamp = 0;

	public LogFileDataSource(Topic topic) {
		super(topic);
		this.topic = topic;
	}

	@Override
	protected DataEvent convertLine(String line) {
		// convert read data back
		String[] parts = line.split(";");
		if (parts.length > 1) {
			double[] data = new double[parts.length - 1];
			long timestamp = Long.parseLong(parts[0]);
			for (int i = 0; i < parts.length - 2; ++i) {
				data[i] = Double.parseDouble(parts[i + 1]);
			}
			if (previousTimestamp > 0) {
				try {
					Thread.sleep(Math.min(
							Math.max(timestamp - previousTimestamp, 1), 1000));
				} catch (InterruptedException e) {
				}
			}
			previousTimestamp = timestamp;
			return new DataEvent(this, topic, timestamp, data);
		}
		return null;
	}
	
}
