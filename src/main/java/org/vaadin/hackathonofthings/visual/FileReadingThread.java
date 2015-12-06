package org.vaadin.hackathonofthings.visual;

import org.vaadin.hackathonofthings.data.DataSink;
import org.vaadin.hackathonofthings.data.Topic;
import org.vaadin.hackathonofthings.io.FileDataStore;

public class FileReadingThread extends Thread {
	// public static final String DATA_FILE = "d:\\elmot_cut1.log";
	public static final String DATA_FILE = "com9";

    private DataSink sink;

	public FileReadingThread(DataSink sink) {
        this.sink = sink;
		setDaemon(true);
    }

    @Override
    public void run() {
        ReadSwordDataWorker chartUpdateWorker = new ReadSwordDataWorker();
        chartUpdateWorker.addSink(sink);
		while (!isInterrupted()) {
            try {
                FileDataStore store = new FileDataStore(new Topic("captured"));
        		chartUpdateWorker.addSink(store);

        		// read the contents
        		chartUpdateWorker.readFile(DATA_FILE);
    			
        		store.close();
                chartUpdateWorker.removeSink(store);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}