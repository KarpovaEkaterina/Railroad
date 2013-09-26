package ru.tsystems.karpova.connector.respond;

public class RegistrationRespondInfo extends AuthorizationRespondInfo {

    public final static int DUPLICATED_LOGIN_STATUS = 3;

    public RegistrationRespondInfo(int status, int rights) {
        super(status, rights);
    }
}
