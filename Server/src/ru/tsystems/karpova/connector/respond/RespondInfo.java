package ru.tsystems.karpova.connector.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RespondInfo  implements Serializable{

    public final static int OK_STATUS = 0;
    public final static int SERVER_ERROR_STATUS = -1;

    private int status;


    public RespondInfo(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(status);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        status = in.readInt();
    }
}
