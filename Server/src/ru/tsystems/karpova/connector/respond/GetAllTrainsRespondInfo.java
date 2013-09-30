package ru.tsystems.karpova.connector.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class GetAllTrainsRespondInfo extends RespondInfo{

    private List<Object[]> allTrains;

    public GetAllTrainsRespondInfo(List<Object[]> allTrains) {
        super(RespondInfo.OK_STATUS);
        this.allTrains = allTrains;
    }

    public List<Object[]> getListAllTrains() {
        return allTrains;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(allTrains);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        allTrains = (List<Object[]>) in.readObject();
    }
}
