package org.vaadin.hackathonofthings.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.vaadin.hackathonofthings.data.AbstractDataSource;
import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.Topic;

public abstract class AbstractFileDataSource extends AbstractDataSource {
	private Topic topic;

	public AbstractFileDataSource(Topic topic) {
		this.topic = topic;
	}
	
	public void readFile(String filename) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(filename));
		lines.map(this::convertLine).filter(event -> event != null).forEach(this::sendEvent);
		lines.close();
	}
	
	public Topic getTopic() {
		return topic;
	}

	protected abstract DataEvent convertLine(String line);
}
