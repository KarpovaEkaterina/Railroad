package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ScheduleRequestInfo implements Serializable{

    private String station;

    public ScheduleRequestInfo(String station) {
        this.station = station;
    }

    public String getStation() {
        return station;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(station);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        station = (String) in.readObject();
    }
}
