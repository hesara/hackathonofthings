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
import org.vaadin.hackathonofthings.data.DataSink;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides various helper methods for connectors. Meant for internal use.
 *
 * @author Vaadin Ltd
 */
public class FXShow extends Application implements DataSink {

    public static final int LIMIT = 600;
    public static final int G_RANGE = 16;
    public static final double ROTATION_SPEED_DEG_S = 2000d;
    private ConcurrentLinkedQueue<double[]> dataQ = new ConcurrentLinkedQueue<>();

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
        new FileReadingThread(this).start();
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

    private class FxUpdate implements Runnable {
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
            Platform.runLater(this);


        }
    }

    @Override
	public void consumeData(DataEvent data) {
		dataQ.add(data.getData());
		while (dataQ.size() > LIMIT) {
			dataQ.remove();
		}
	}

}
