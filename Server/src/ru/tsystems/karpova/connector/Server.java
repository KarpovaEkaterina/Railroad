package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.reguests.AuthorizationRequestInfo;
import ru.tsystems.karpova.connector.reguests.RegistrationRequestInfo;
import ru.tsystems.karpova.connector.respond.AuthorizationRespondInfo;
import ru.tsystems.karpova.connector.respond.RegistrationRespondInfo;
import ru.tsystems.karpova.dao.UserDAO;
import ru.tsystems.karpova.entities.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private static Logger log = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Server.class.getClassLoader().getResourceAsStream("server.properties"));

            Integer port = Integer.valueOf(properties.getProperty("server.port", "1234"));
            ServerSocket socket = new ServerSocket(port);
            Socket connectionSocket;
            Executor handlerExecutor = new ThreadPoolExecutor(10, 20, 30,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            while ((connectionSocket = socket.accept()) != null) {
                handlerExecutor.execute(new RequestHandler(connectionSocket));
            }
        } catch (IOException e) {
            log.error("Server startup error", e);
        }
    }

    private static class RequestHandler implements Runnable {
        Socket connectionSocket;

        private RequestHandler(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        @Override
        public void run() {
            log.info("RequestHandler starter!");
            ObjectOutputStream toClient = null;
            ObjectInputStream fromClient = null;
            try {
                toClient = new ObjectOutputStream(connectionSocket.getOutputStream());
                fromClient = new ObjectInputStream(connectionSocket.getInputStream());
                log.info("Connections created!");

                while (true) {
                    Object o = fromClient.readObject();
                    if (o instanceof RegistrationRequestInfo) {
                        register(toClient, (RegistrationRequestInfo) o);
                    } else if (o instanceof AuthorizationRequestInfo) {
                        login(toClient, (AuthorizationRequestInfo) o);
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("Can't find class for received object", e);
                try {
                    RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
                    toClient.writeObject(respond);
                } catch (IOException e1) {
                    log.error("Failed to send error respond to user", e1);
                }
            } catch (IOException e) {
                log.error("User terminated session", e);
            } finally {
                if (toClient != null) {
                    try {
                        toClient.close();
                    } catch (IOException e) {
                        log.error("To client stream closing error", e);
                    }
                }
                if (fromClient != null) {
                    try {
                        fromClient.close();
                    } catch (IOException e) {
                        log.error("From client stream closing error", e);
                    }
                }
                if (connectionSocket != null) {
                    try {
                        connectionSocket.close();
                    } catch (IOException e) {
                        log.error("Connection socket closing error", e);
                    }
                }
            }
        }

        private void register(ObjectOutputStream toClient, RegistrationRequestInfo regRequest) {
            try {
                log.info(regRequest.getLogin() + " (" + connectionSocket.toString() + ") " + " is trying to register");
                User user = UserDAO.loadUserByLogin(regRequest.getLogin());
                if (user != null) {
                    log.info("Registration error. Duplicated login.");
                    RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.DUPLICATED_LOGIN_STATUS, -1);
                    toClient.writeObject(respond);
                    return;
                }
                user = new User();
                user.setLogin(regRequest.getLogin());
                user.setPassword(regRequest.getPassword());
                user.setAccessLevel(User.ACCESS_LEVEL_PASSENGER);
                if (UserDAO.saveUser(user)) {
                    log.info("Registration passed");
                    RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.OK_STATUS, user.getAccessLevel());
                    toClient.writeObject(respond);
                } else {
                    RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
                    toClient.writeObject(respond);
                }
            } catch (IOException e) {
                log.error("write/readObject error", e);
                try {
                    RegistrationRespondInfo respond = new RegistrationRespondInfo(RegistrationRespondInfo.SERVER_ERROR_STATUS, -1);
                    toClient.writeObject(respond);
                } catch (IOException e1) {
                    log.error("Failed to send error respond to user", e1);
                }
            }
        }

        private void login(ObjectOutputStream toClient, AuthorizationRequestInfo authRequest) {
            try {
                log.info(authRequest.getLogin() + " (" + connectionSocket.toString() + ") " + " is trying to authorize");
                User user = UserDAO.loadUserByLogin(authRequest.getLogin());
                if (user == null || user.getPassword() == null || !user.getPassword().equals(authRequest.getPassword())) {
                    log.info("Authorization error.");
                    AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.WRONG_CREDENTIALS_STATUS, -1);
                    toClient.writeObject(respond);
                    return;
                }
                log.info("Auth passed");
                AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.OK_STATUS, user.getAccessLevel());
                toClient.writeObject(respond);
            } catch (IOException e) {
                log.error("write/readObject error", e);
                try {
                    AuthorizationRespondInfo respond = new AuthorizationRespondInfo(AuthorizationRespondInfo.SERVER_ERROR_STATUS, -1);
                    toClient.writeObject(respond);
                } catch (IOException e1) {
                    log.error("Failed to send error respond to user", e1);
                }
            }
        }
    }
}
