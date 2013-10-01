package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.AuthorizationRequestInfo;
import ru.tsystems.karpova.connector.requests.RegistrationRequestInfo;
import ru.tsystems.karpova.connector.respond.AuthorizationRespondInfo;
import ru.tsystems.karpova.connector.respond.RegistrationRespondInfo;
import ru.tsystems.karpova.dao.UserDAO;
import ru.tsystems.karpova.entities.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerLoginHelper {

    private static Logger log = Logger.getLogger(ServerLoginHelper.class);

    public static void registration(ObjectOutputStream toClient, RegistrationRequestInfo regRequest) {
        log.debug("Start \"registration\" method");
        try {
            log.info(regRequest.getLogin() + " is trying to registration");
            User user = UserDAO.loadUserByLogin(regRequest.getLogin());
            if (user != null) {
                log.info("Registration error. Duplicated login.");
                RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.DUPLICATED_LOGIN_STATUS, -1);
                log.debug("Send RegistrationRespondInfo to client with DUPLICATED_LOGIN_STATUS");
                toClient.writeObject(respond);
                return;
            }
            user = new User();
            user.setLogin(regRequest.getLogin());
            user.setPassword(regRequest.getPassword());
            user.setAccessLevel(regRequest.getAccessLevel());
            if (UserDAO.saveUser(user)) {
                log.info("Registration passed");
                RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.OK_STATUS, user.getAccessLevel());
                log.debug("Send RegistrationRespondInfo to client with OK_STATUS");
                toClient.writeObject(respond);
            } else {
                RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
                log.debug("Send RegistrationRespondInfo to client with SERVER_ERROR_STATUS");
                toClient.writeObject(respond);
            }
        } catch (IOException e) {
            log.error("write/readObject error", e);
            try {
                RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
                log.debug("Send RegistrationRespondInfo to client with SERVER_ERROR_STATUS");
                toClient.writeObject(respond);
            } catch (IOException e1) {
                log.error("Failed to send error respond to user", e1);
            }
        }
    }

    public static void login(ObjectOutputStream toClient, AuthorizationRequestInfo authRequest) {
        log.debug("Start \"login\" method");
        try {
            log.info(authRequest.getLogin() + " is trying to authorize");
            User user = UserDAO.loadUserByLogin(authRequest.getLogin());
            if (user == null || user.getPassword() == null || !user.getPassword().equals(authRequest.getPassword())) {
                log.info("Authorization error.");
                AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.WRONG_CREDENTIALS_STATUS, -1);
                log.debug("Send AuthorizationRespondInfo to client with WRONG_CREDENTIALS_STATUS");
                toClient.writeObject(respond);
                return;
            }
            log.info("Auth passed");
            AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.OK_STATUS, user.getAccessLevel());
            log.debug("Send AuthorizationRespondInfo to client with OK_STATUS");
            toClient.writeObject(respond);
        } catch (IOException e) {
            log.error("write/readObject error", e);
            try {
                AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.SERVER_ERROR_STATUS, -1);
                log.debug("Send AuthorizationRespondInfo to client with SERVER_ERROR_STATUS");
                toClient.writeObject(respond);
            } catch (IOException e1) {
                log.error("Failed to send error respond to user", e1);
            }
        }
    }
}