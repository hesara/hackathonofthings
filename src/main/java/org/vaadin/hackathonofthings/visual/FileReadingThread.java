package org.vaadin.hackathonofthings.visual;

import org.vaadin.hackathonofthings.data.DataSink;

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
        while (!isInterrupted())
            try {

                chartUpdateWorker.readFile(DATA_FILE);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}