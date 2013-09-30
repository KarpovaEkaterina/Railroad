package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AddStationRequestInfo implements Serializable {

    private String stationName;

    public AddStationRequestInfo(String stationName) {
        this.stationName = stationName;
    }

    public String getStationName() {
        return stationName;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(stationName);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        stationName = (String) in.readObject();
    }
}
