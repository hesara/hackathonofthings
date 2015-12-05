package org.vaadin.hackathonofthings.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.vaadin.hackathonofthings.data.AbstractDataSource;
import org.vaadin.hackathonofthings.data.DataEvent;

public abstract class AbstractFileDataSource extends AbstractDataSource {
	public AbstractFileDataSource(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));
		for (String line : lines) {
			DataEvent event = convertLine(line);
			if (event != null) {
				sendEvent(event);
			}
		}
	}

	protected abstract DataEvent convertLine(String line);
}
