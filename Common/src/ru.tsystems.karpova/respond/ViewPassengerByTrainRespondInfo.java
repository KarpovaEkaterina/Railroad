package ru.tsystems.karpova.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class ViewPassengerByTrainRespondInfo extends RespondInfo implements Serializable {

    public static final int WRONG_TRAIN_NAME_STATUS = 2;
    private List<Object[]> listAllPassengerByTrain;

    public ViewPassengerByTrainRespondInfo(List<Object[]> listAllPassengerByTrain) {
        super(RespondInfo.OK_STATUS);
        this.listAllPassengerByTrain = listAllPassengerByTrain;
    }

    public ViewPassengerByTrainRespondInfo(int errorStatus) {
        super(errorStatus);
    }

    public List<Object[]> getListAllPassengerByTrain() {
        return listAllPassengerByTrain;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(listAllPassengerByTrain);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        listAllPassengerByTrain = (List<Object[]>) in.readObject();
    }
}
