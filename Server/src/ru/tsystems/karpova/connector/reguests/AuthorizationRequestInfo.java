package ru.tsystems.karpova.connector.reguests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AuthorizationRequestInfo implements Serializable {
    private String login = null;
    private String password = null;

    public AuthorizationRequestInfo(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(login);
        out.writeObject(password);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        login = (String) in.readObject();
        password = (String) in.readObject();
    }
}
