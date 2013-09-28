package ru.tsystems.karpova.connector.requests;

public class RegistrationRequestInfo extends AuthorizationRequestInfo{

    private int accessLevel = -1;

    public RegistrationRequestInfo(String login, String password, int accessLevel) {
        super(login, password);
        this.accessLevel = accessLevel;
    }

    public int getAccessLevel() {
        return accessLevel;
    }
}
