package ru.tsystems.karpova.connector.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AddStationRespondInfo  extends RespondInfo implements Serializable {
    private int status;

    public AddStationRespondInfo(int status) {
        super(status);
    }
}
