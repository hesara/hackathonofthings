package org.vaadin.hackathonofthings.visual;

import java.io.IOException;

import org.vaadin.hackathonofthings.data.DataSink;
import org.vaadin.hackathonofthings.data.Topic;
import org.vaadin.hackathonofthings.io.FileLogger;
import org.vaadin.hackathonofthings.io.LogFileDataSource;

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
//    	LogFileDataSource source = new LogFileDataSource(new Topic("capture_log"));
//    	source.addSink(sink);
//    	try {
//			source.readFile("captured5382031613984702956.log");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        ReadSwordDataWorker chartUpdateWorker = new ReadSwordDataWorker();
        chartUpdateWorker.addSink(sink);
		while (!isInterrupted()) {
            try {
                FileLogger store = new FileLogger(new Topic("captured"));
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