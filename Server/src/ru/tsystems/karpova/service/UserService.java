package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.dao.UserDAO;
import ru.tsystems.karpova.entities.User;
import ru.tsystems.karpova.requests.AuthorizationRequestInfo;
import ru.tsystems.karpova.requests.RegistrationRequestInfo;
import ru.tsystems.karpova.respond.AuthorizationRespondInfo;
import ru.tsystems.karpova.respond.RegistrationRespondInfo;

public class UserService {

    private static Logger log = Logger.getLogger(TrainService.class);

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public RegistrationRespondInfo registration(RegistrationRequestInfo regRequest) {
        log.debug("Start \"registration\" method");
        log.info(regRequest.getLogin() + " is trying to registration");
        User user = userDAO.loadUserByLogin(regRequest.getLogin());
        if (user != null) {
            log.info("Registration error. Duplicated login.");
            RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.DUPLICATED_LOGIN_STATUS, -1);
            log.debug("Send RegistrationRespondInfo to client with DUPLICATED_LOGIN_STATUS");
            return respond;
        }
        user = new User();
        user.setLogin(regRequest.getLogin());
        user.setPassword(regRequest.getPassword());
        user.setAccessLevel(regRequest.getAccessLevel());
        if (userDAO.saveUser(user)) {
            log.info("Registration passed");
            RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.OK_STATUS, user.getAccessLevel());
            log.debug("Send RegistrationRespondInfo to client with OK_STATUS");
            return respond;
        } else {
            RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
            log.debug("Send RegistrationRespondInfo to client with SERVER_ERROR_STATUS");
            return respond;
        }
    }

    public AuthorizationRespondInfo login(AuthorizationRequestInfo authRequest) {
        log.debug("Start \"login\" method");
        log.info(authRequest.getLogin() + " is trying to authorize");
        User user = userDAO.loadUserByLogin(authRequest.getLogin());
        if (user == null || user.getPassword() == null || !user.getPassword().equals(authRequest.getPassword())) {
            log.info("Authorization error.");
            AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.WRONG_CREDENTIALS_STATUS, -1);
            log.debug("Send AuthorizationRespondInfo to client with WRONG_CREDENTIALS_STATUS");
            return respond;
        }
        log.info("Auth passed");
        AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.OK_STATUS, user.getAccessLevel());
        log.debug("Send AuthorizationRespondInfo to client with OK_STATUS");
        return respond;
    }
}
