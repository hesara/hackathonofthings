package org.vaadin.hackathonofthings.io;

import java.io.IOException;
import java.io.Writer;

import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.DataSink;
import org.vaadin.hackathonofthings.data.Topic;

/**
 * Data store saving incoming (sinked) events to the disk and retrieving them
 * later for playback. Data retrieval is done based on the topic.
 */
public class FileDataStore extends AbstractFileDataSource implements DataSink {

	private Topic topic;

	private Writer writer;

	public FileDataStore(Topic topic) {
		super(topic);
		this.topic = topic;
	}

	public void replay() throws IOException {
		readFile(getFileName());
	}

	protected String getFileName() {
		return topic.getId().replaceAll("[^a-zA-Z0-9_-]", "_");
	}

	public void consumeData(DataEvent event) {
		if (event.getSender() == this) {
			return;
		}
		if (writer == null) {
			// store an event
			StringBuilder line = new StringBuilder();
			line.append(event.getTimestamp()).append(";");
			for (int i = 0; i < event.getData().length; ++i) {
				line.append(event.getData()[i]);
				if (i < event.getData().length - 1) {
					line.append(";");
				} else {
					line.append("\n");
				}
			}
			try {
				writer.write(line.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected DataEvent convertLine(String line) {
		// convert read data back
		String[] parts = line.split(";");
		if (parts.length > 1) {
			double[] data = new double[parts.length - 1];
			long timestamp = Long.parseLong(parts[0]);
			for (int i=0; i<parts.length-2; ++i) {
				data[i] = Double.parseDouble(parts[i+1]);
			}
			return new DataEvent(this,  topic,  timestamp, data);
		}
		return null;
	}

}
