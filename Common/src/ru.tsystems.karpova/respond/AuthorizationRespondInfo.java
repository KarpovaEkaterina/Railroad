package ru.tsystems.karpova.respond;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AuthorizationRespondInfo extends RespondInfo implements Serializable {

    public final static int OK_STATUS = 0;
    public final static int WRONG_CREDENTIALS_STATUS = 1;
    public final static int SERVER_ERROR_STATUS = 2;

    private int status;
    private int rights;


    public AuthorizationRespondInfo(int status, int rights) {
        super(status);
        this.rights = rights;
    }

    public int getRights() {
        return rights;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(status);
        out.writeInt(rights);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        status = in.readInt();
        rights = in.readInt();
    }
}
