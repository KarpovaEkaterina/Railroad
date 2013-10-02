package ru.tsystems.karpova.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindTrainRespondInfo extends RespondInfo implements Serializable {

    private List<TrainInfo> trains = new ArrayList<TrainInfo>();

    public FindTrainRespondInfo(List<Object[]> trains) {
        super(OK_STATUS);
        for (Object[] train : trains) {
            TrainInfo trainInfo = new TrainInfo((String) train[0], (Date) train[1]);
            this.trains.add(trainInfo);
        }
    }

    public List<TrainInfo> getTrains() {
        return trains;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(trains);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        trains = (List<TrainInfo>) in.readObject();
    }

    public static class TrainInfo implements Serializable {

        private String trainName;
        private Date departure;

        public TrainInfo(String station, Date departure) {
            this.trainName = station;
            this.departure = departure;
        }

        public Date getDeparture() {
            return departure;
        }

        public String getTrainName() {

            return trainName;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(trainName);
            out.writeObject(departure);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            trainName = (String) in.readObject();
            departure = (Date) in.readObject();
        }
    }
}
