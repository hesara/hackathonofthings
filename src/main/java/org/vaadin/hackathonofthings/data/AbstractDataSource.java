package org.vaadin.hackathonofthings.data;

import java.util.ArrayList;
import java.util.List;

public class AbstractDataSource {
	private List<DataSink> sinks = new ArrayList<DataSink>();
	
	public synchronized void addSink(DataSink sink) {
		sinks.add(sink);
	}

	public synchronized void removeSink(DataSink sink) {
		sinks.remove(sink);
	}
	
	protected synchronized List<DataSink> getSinks() {
		return new ArrayList<DataSink>(sinks);
	}
	
	protected void sendEvent(DataEvent event) {
		for (DataSink sink : getSinks()) {
			sink.consumeData(event);
		}
	}
}
