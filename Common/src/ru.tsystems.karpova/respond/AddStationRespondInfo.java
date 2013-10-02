package ru.tsystems.karpova.respond;

import java.io.Serializable;

public class AddStationRespondInfo extends RespondInfo implements Serializable {
    private int status;

    public AddStationRespondInfo(int status) {
        super(status);
    }
}
