package ru.tsystems.karpova.connector.respond;

import java.io.Serializable;

public class BuyTicketRespondInfo extends RespondInfo implements Serializable {

    public static final int NO_SEATS_STATUS = 3;
    public static final int PASSENGER_ALREADY_EXISTS_STATUS = 4;
    public static final int WRONG_TRAIN_NAME_STATUS = 6;
    public static final int WRONG_STATION_FROM_NAME_STATUS = 7;
    public static final int WRONG_STATION_TO_NAME_STATUS = 8;
    public static final int WRONG_STATION_TRAIN_STATUS = 9;
    public static final int WRONG_DEPARTURE_TIME_STATUS = 10;
    public static final int WRONG_STATION_ORDER_STATUS = 11;

    public BuyTicketRespondInfo(int status) {
        super(status);
    }
}