package ru.tsystems.karpova.connector.requests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class BuyTicketRequestInfo implements Serializable{

    private String train;
    private String firstname;
    private String lastname;
    private Date birthday;

    public BuyTicketRequestInfo(String train, String firstname, String lastname, Date birthday) {
        this.train = train;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(train);
        out.writeObject(firstname);
        out.writeObject(lastname);
        out.writeObject(birthday);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        train = (String) in.readObject();
        firstname = (String) in.readObject();
        lastname = (String) in.readObject();
        birthday = (Date) in.readObject();
    }
}
