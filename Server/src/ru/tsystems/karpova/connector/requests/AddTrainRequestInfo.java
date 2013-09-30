package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class AddTrainRequestInfo implements Serializable {

    private String trainName;
    private String route;
    private int totalSeats;
    private Date departureTime;

    public AddTrainRequestInfo(String trainName, String route, int totalSeats, Date departureTime) {
          this.trainName = trainName;
          this.route = route;
          this.totalSeats = totalSeats;
          this.departureTime = departureTime;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getRoute() {
        return route;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(trainName);
        out.writeObject(route);
        out.writeInt(totalSeats);
        out.writeObject(departureTime);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        trainName = (String) in.readObject();
        route = (String) in.readObject();
        totalSeats = in.readInt();
        departureTime = (Date) in.readObject();
    }
}
