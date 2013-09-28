package ru.tsystems.karpova.connector.respond;

import ru.tsystems.karpova.entities.Station;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class GetAllStationRespondInfo extends RespondInfo{

    private List<String> listAllStation;

    public List<String> getListAllStation() {
        return listAllStation;
    }

    public GetAllStationRespondInfo(List<String> listAllStation) {
        super(RespondInfo.OK_STATUS);
        this.listAllStation = listAllStation;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(listAllStation);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        listAllStation = (List<String>) in.readObject();
    }
}
