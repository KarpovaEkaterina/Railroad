package ru.tsystems.karpova.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AddRouteRequestInfo implements Serializable {

    private String routeName;
    private List<String> stationsForNewRoute;
    private Map<String, Object[]> newWay;
    private String delimiter;

    public String getDelimiter() {
        return delimiter;
    }

    public AddRouteRequestInfo(String delimiter, String routeName, List<String> stationsForNewRoute, Map<String, Object[]> newWay) {
        this.routeName = routeName;
        this.stationsForNewRoute = stationsForNewRoute;
        this.newWay = newWay;
        this.delimiter = delimiter;
    }

    public String getRouteName() {
        return routeName;
    }

    public List<String> getStationsForNewRoute() {
        return stationsForNewRoute;
    }

    public Map<String, Object[]> getNewWay() {
        return newWay;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(delimiter);
        out.writeObject(routeName);
        out.writeObject(stationsForNewRoute);
        out.writeObject(newWay);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        delimiter = (String) in.readObject();
        routeName = (String) in.readObject();
        stationsForNewRoute = (List<String>) in.readObject();
        newWay = (Map<String, Object[]>) in.readObject();
    }

}
