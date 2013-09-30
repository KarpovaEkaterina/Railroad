package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ViewPassengerByTrainRequestInfo  implements Serializable {
    private String trainName;

    public ViewPassengerByTrainRequestInfo(String trainName) {
          this.trainName = trainName;
    }

    public String getTrainName() {
        return trainName;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(trainName);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        trainName = (String) in.readObject();
    }
}
