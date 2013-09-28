package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class FindTrainRequestInfo implements Serializable {

    private String stationFrom;
    private String stationTo;
    private Date dateFrom;
    private Date dateTo;

    public FindTrainRequestInfo(String stationFrom, String stationTo, Date dateFrom, Date dateTo) {
        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public String getStationFrom() {
        return stationFrom;
    }

    public String getStationTo() {
        return stationTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(stationFrom);
        out.writeObject(stationTo);
        out.writeObject(dateFrom);
        out.writeObject(dateTo);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        stationFrom = (String) in.readObject();
        stationTo = (String) in.readObject();
        dateFrom = (Date) in.readObject();
        dateTo = (Date) in.readObject();
    }
}
