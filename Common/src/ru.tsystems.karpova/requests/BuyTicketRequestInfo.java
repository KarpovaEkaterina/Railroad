package ru.tsystems.karpova.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class BuyTicketRequestInfo implements Serializable {

    private String train;
    private String stationFrom;
    private String stationTo;
    private String firstname;
    private String lastname;
    private Date birthday;

    public String getStationFrom() {
        return stationFrom;
    }

    public String getStationTo() {
        return stationTo;
    }

    public String getTrain() {
        return train;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public BuyTicketRequestInfo(String train, String stationFrom, String stationTo, String firstname, String lastname, Date birthday) {
        this.train = train;
        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(train);
        out.writeObject(stationFrom);
        out.writeObject(stationTo);
        out.writeObject(firstname);
        out.writeObject(lastname);
        out.writeObject(birthday);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        train = (String) in.readObject();
        stationFrom = (String) in.readObject();
        stationTo = (String) in.readObject();
        firstname = (String) in.readObject();
        lastname = (String) in.readObject();
        birthday = (Date) in.readObject();
    }
}
