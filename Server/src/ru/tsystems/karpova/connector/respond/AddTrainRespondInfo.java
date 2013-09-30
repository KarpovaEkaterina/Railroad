package ru.tsystems.karpova.connector.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AddTrainRespondInfo  extends RespondInfo implements Serializable {
    public static final int WRONG_ROUTE_NAME_STATUS = 2;

    private int status;

    public AddTrainRespondInfo(int status) {
        super(status);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(status);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        status = in.readInt();
    }
}
