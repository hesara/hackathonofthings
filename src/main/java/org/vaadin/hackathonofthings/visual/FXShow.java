/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.hackathonofthings.visual;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.Topic;
import org.vaadin.hackathonofthings.io.AbstractFileDataSourceClassic;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides various helper methods for connectors. Meant for internal use.
 *
 * @author Vaadin Ltd
 */
public class FXShow extends Application {

    public static final int LIMIT = 600;
    public static final int G_RANGE = 16;
    public static final double ROTATION_SPEED_DEG_S = 2000d;
//        public static final String DATA_FILE = "d:\\elmot_cut1.log";
    public static final String DATA_FILE = "com9";
    static ConcurrentLinkedQueue<double[]> dataQ = new ConcurrentLinkedQueue<>();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("TeleFence");
        stage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("F11"));
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(-16, 16, 2);
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        yAxis.setForceZeroInRange(true);
        for (int j = 0; j < 7; j++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (int i = 0; i < LIMIT; i++) series.getData().add(new XYChart.Data<>(i, -1d));
            chart.getData().add(series);
        }
        chart.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5)");
        Scene scene = new Scene(chart);
        decorateStage(stage, scene);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.show();
        new FileReadingThread().start();
        Platform.runLater(new FxUpdate(chart));
    }

    private void decorateStage(Stage stage, Scene scene) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        scene.setFill(null);
        scene.getStylesheets().add("styles.css");
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class ChartUpdateWorker extends AbstractFileDataSourceClassic {
        public ChartUpdateWorker() {
            super(new Topic("sword"));
        }

        @Override
        protected boolean checkContinue() {
            return !Thread.currentThread().isInterrupted();
        }

        @Override
        protected DataEvent convertLine(String line) {
            long timeMillis = System.currentTimeMillis();
            double[] dataPoint = new double[7];
            for (int i = 0; i < 6; i++) {
                try {
                    dataPoint[i] = Double.parseDouble(line.substring(i * 7, i * 7 + 6));
//                    System.out.print("\t" + dataPoint[i]);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    System.err.println("Broken line");
                    return null;
                }
            }
            dataPoint[6] = dataPoint[5] * Math.PI * 180 / ROTATION_SPEED_DEG_S / 32768;
            dataPoint[5] = dataPoint[4] * Math.PI * 180 / ROTATION_SPEED_DEG_S / 32768;
            dataPoint[4] = dataPoint[3] * Math.PI * 180 / ROTATION_SPEED_DEG_S / 32768;
            dataPoint[2] = dataPoint[2] * G_RANGE / 32768;
            dataPoint[1] = dataPoint[1] * G_RANGE / 32768;
            dataPoint[0] = -dataPoint[0] * G_RANGE / 32768;
            dataPoint[3] = Math.sqrt(dataPoint[0] * dataPoint[0] + dataPoint[1] * dataPoint[1] + dataPoint[2] * dataPoint[2]);
//            System.out.print('\r');
            return new DataEvent(getTopic(), timeMillis, dataPoint);
        }

    }


    private static class FxUpdate implements Runnable {
        private final XYChart<Number, Number> chart;

        public FxUpdate(XYChart<Number, Number> chart) {
            this.chart = chart;
        }

        @Override
        public void run() {
            ObservableList<XYChart.Series<Number, Number>> serie = chart.getData();

            Iterator<double[]> iterator = dataQ.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                double[] dataPoint = iterator.next();
                for (int j = 0; j < serie.size(); j++) {
                    ObservableList<XYChart.Data<Number, Number>> lineData = serie.get(j).getData();
                    if (lineData.size() > i) {
                        lineData.get(i).setYValue(dataPoint[j]);
                    }
                }
            }
            Thread.yield();
            Platform.runLater(this);


        }
    }

    private static class FileReadingThread extends Thread {
        public FileReadingThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            ChartUpdateWorker chartUpdateWorker = new ChartUpdateWorker();
            chartUpdateWorker.addSink(data -> {
                dataQ.add(data.getData());
                while (dataQ.size() > LIMIT) {
                    dataQ.remove();
                }
            });
            while (!isInterrupted())
                try {

                    chartUpdateWorker.readFile(DATA_FILE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
