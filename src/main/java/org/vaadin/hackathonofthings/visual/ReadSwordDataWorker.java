package org.vaadin.hackathonofthings.visual;

import org.vaadin.hackathonofthings.data.DataEvent;
import org.vaadin.hackathonofthings.data.Topic;
import org.vaadin.hackathonofthings.io.AbstractFileDataSourceClassic;

public class ReadSwordDataWorker extends AbstractFileDataSourceClassic {
        public ReadSwordDataWorker() {
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
            dataPoint[6] = dataPoint[5] * Math.PI * 180 / FXShow.ROTATION_SPEED_DEG_S / 32768;
            dataPoint[5] = dataPoint[4] * Math.PI * 180 / FXShow.ROTATION_SPEED_DEG_S / 32768;
            dataPoint[4] = dataPoint[3] * Math.PI * 180 / FXShow.ROTATION_SPEED_DEG_S / 32768;
            dataPoint[2] = dataPoint[2] * FXShow.G_RANGE / 32768;
            dataPoint[1] = dataPoint[1] * FXShow.G_RANGE / 32768;
            dataPoint[0] = -dataPoint[0] * FXShow.G_RANGE / 32768;
            dataPoint[3] = Math.sqrt(dataPoint[0] * dataPoint[0] + dataPoint[1] * dataPoint[1] + dataPoint[2] * dataPoint[2]);
//            System.out.print('\r');
            return new DataEvent(getTopic(), timeMillis, dataPoint);
        }

    }