package org.vaadin.hackathonofthings.io;

import org.vaadin.hackathonofthings.data.AbstractDataSource;
import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.Topic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class AbstractFileDataSourceClassic extends AbstractDataSource {
    private final Topic topic;
    public static final int BUFFER_SIZE = 50;

    public AbstractFileDataSourceClassic(Topic topic) {
        this.topic = topic;
    }

    public void readFile(String filename) throws IOException {
        try (BufferedReader lines = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.US_ASCII), BUFFER_SIZE)) {
            for (String line; checkContinue() && (line = lines.readLine()) != null; ) {
                DataEvent event = convertLine(line);
                if (event != null) sendEvent(event);
            }
        }
    }

    public Topic getTopic() {
        return topic;
    }

    protected abstract DataEvent convertLine(String line);

    protected boolean checkContinue() {
        return true;
    }
}
