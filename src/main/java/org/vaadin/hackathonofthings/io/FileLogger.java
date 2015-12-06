package org.vaadin.hackathonofthings.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.DataSink;
import org.vaadin.hackathonofthings.data.Topic;

/**
 * Data store saving incoming (sinked) events to the disk.
 * 
 * Usage: create a FileLogger, use it as a sink.
 * 
 * Events sent to the logger are not sent to the consumers immediately but both
 * the original data source and the logger should be added as sinks for the
 * original data source.
 */
public class FileLogger implements DataSink {

	private Topic topic;

	private Writer writer;

	public FileLogger(Topic topic) {
		this.topic = topic;
	}

	protected String convertTopicToFileName() {
		return topic.getId().replaceAll("[^a-zA-Z0-9_-]", "_");
	}

	public void consumeData(DataEvent event) {
		if (event.getSender() instanceof LogFileDataSource) {
			// ignore replay events
			return;
		}
		try {
			if (writer == null) {
				File file = File.createTempFile(convertTopicToFileName(), ".log", new File("."));
				String fileName = file.getCanonicalPath();
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

	public void close() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

}
