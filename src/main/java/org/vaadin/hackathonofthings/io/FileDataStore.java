package org.vaadin.hackathonofthings.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.DataSink;
import org.vaadin.hackathonofthings.data.Topic;

/**
 * Data store saving incoming (sinked) events to the disk and retrieving them
 * later for playback. Data retrieval is done based on the topic.
 * 
 * Usage: create a FileDataStore, use it as a sink, add replay targets as sinks
 * on FDS and call replay() to re-send the same events.
 * 
 * Events sent to the FDS are not sent to the consumers immediately but both the
 * original data source and the FDS should be added as sinks for the original
 * data source.
 */
public class FileDataStore extends AbstractFileDataSource implements DataSink {

	private Topic topic;

	private Writer writer;
	private String fileName;

	public FileDataStore(Topic topic) {
		super(topic);
		this.topic = topic;
	}

	public void replay() throws IOException {
		if (writer != null) {
			close();
		}
		if (fileName != null) {
			readFile(fileName);
		}
	}

	protected String convertTopicToFileName() {
		return topic.getId().replaceAll("[^a-zA-Z0-9_-]", "_");
	}

	public void consumeData(DataEvent event) {
		if (event.getSender() == this) {
			// ignore replay events
			return;
		}
		try {
			if (writer == null) {
				File file = File.createTempFile(convertTopicToFileName(), ".log", new File("."));
				fileName = file.getCanonicalPath();
				// System.err.println(fileName);
				writer = new FileWriter(file);
			}
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
			writer.write(line.toString());
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
			return new DataEvent(this, topic, timestamp, data);
		}
		return null;
	}
	
	public void close() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

}
