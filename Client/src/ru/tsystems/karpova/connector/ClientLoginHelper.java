package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.AuthorizationRequestInfo;
import ru.tsystems.karpova.connector.requests.RegistrationRequestInfo;
import ru.tsystems.karpova.connector.respond.AuthorizationRespondInfo;
import ru.tsystems.karpova.connector.respond.RegistrationRespondInfo;
import ru.tsystems.karpova.connector.respond.RespondInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class ClientLoginHelper {

    public static final int SERVER_ERROR_RETURN_CODE = -2;
    public static final int LOGIN_FAILED_RETURN_CODE = -1;
    private static Logger log = Logger.getLogger(ClientLoginHelper.class);

    static int registration(Scanner scanner, ObjectOutputStream toServer, ObjectInputStream fromServer, int accessLevel) throws IOException, ClassNotFoundException {
        log.debug("Start \"registration\" method");
        System.out.println("Input login:");
        String login = scanner.next().toLowerCase();
        System.out.println("Input password:");
        String password = scanner.next();
        RegistrationRequestInfo reg = new RegistrationRequestInfo(login, password, accessLevel);
        log.debug("Send RegistrationRequestInfo to server");
        toServer.writeObject(reg);


        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return -2;
            } else if (o instanceof RegistrationRespondInfo) {
                RegistrationRespondInfo respond = (RegistrationRespondInfo) o;
                log.debug("Received RegistrationRespondInfo from server");

                switch (respond.getStatus()) {
                    case RegistrationRespondInfo.OK_STATUS: {
                        System.out.println("Registration successful");
                        log.debug("Registration successful");
                        return respond.getRights();
                    }
                    case RegistrationRespondInfo.DUPLICATED_LOGIN_STATUS: {
                        System.out.println("User with that login already exists");
                        log.debug("Duplicated login");
                        return respond.getRights();
                    }
                    default:
                        return -3;
                }
            }
        }
        log.error("Unknown object type");
        return -4;
    }

    static int login(Scanner scanner, ObjectOutputStream toServer, ObjectInputStream fromServer) throws
            IOException, ClassNotFoundException {
        log.debug("Start \"login\" method");
        System.out.println("Input login:");
        String login = scanner.next().toLowerCase();
        System.out.println("Input password:");
        String password = scanner.next();
        AuthorizationRequestInfo auth = new AuthorizationRequestInfo(login, password);
        log.debug("Send AuthorizationRequestInfo to server");
        toServer.writeObject(auth);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return SERVER_ERROR_RETURN_CODE;
            } else if (o instanceof AuthorizationRespondInfo) {
                AuthorizationRespondInfo respond = (AuthorizationRespondInfo) o;
                log.debug("Received AuthorizationRespondInfo from server");

                switch (respond.getStatus()) {
                    case AuthorizationRespondInfo.OK_STATUS: {
                        System.out.println("Connected");
                        log.debug("Authorized");
                        return respond.getRights();
                    }
                    case AuthorizationRespondInfo.WRONG_CREDENTIALS_STATUS: {
                        System.out.println("Wrong login/password provided");
                        log.debug("Wrong login/password provided");
                        break;
                    }
                }
            }
            System.out.println("Login failed!!!");
            log.debug("Login failed!!!");
            return LOGIN_FAILED_RETURN_CODE;
        }
        log.error("Unknown object type");
        return SERVER_ERROR_RETURN_CODE;
    }
}