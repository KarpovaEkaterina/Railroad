package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.reguests.AuthorizationRequestInfo;
import ru.tsystems.karpova.connector.reguests.RegistrationRequestInfo;
import ru.tsystems.karpova.connector.respond.AuthorizationRespondInfo;
import ru.tsystems.karpova.connector.respond.RegistrationRespondInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    public static final int MAX_LOGIN_ATTEMPTS = 3;
    private static Logger log = Logger.getLogger(Client.class);
    private static int accessLevel = -1;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Properties properties = new Properties();
        properties.load(Client.class.getClassLoader().getResourceAsStream("client.properties"));

        Integer serverPort = Integer.valueOf(properties.getProperty("client.server_port", "1234"));
        String serverAddress = properties.getProperty("client.server_address", "localhost");
        ObjectOutputStream toServer = null;
        ObjectInputStream fromServer = null;
        Socket connectionSocket = null;
        try {
            connectionSocket = new Socket(serverAddress, serverPort);

            Scanner scanner = new Scanner(System.in);
            toServer = new ObjectOutputStream(connectionSocket.getOutputStream());
            fromServer = new ObjectInputStream(connectionSocket.getInputStream());
            log.info("Connection created!");
            while (!start(toServer, fromServer, scanner));
        } catch (IOException e) {
            log.error("Can't connect to server.", e);
        } finally {
            if (toServer != null) {
                try {
                    toServer.close();
                } catch (IOException e) {
                    log.error("To server stream closing error", e);
                }
            }
            if (fromServer != null) {
                try {
                    fromServer.close();
                } catch (IOException e) {
                    log.error("From server stream closing error", e);
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

    private static boolean start(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.println("1 - registration, 2 - login, 0 - exit");
        int scan = scanner.nextInt();
        while (scan != 1 && scan != 2 && scan != 0) {
            System.out.println("Input 1 or 2");
            scan = scanner.nextInt();
        }
        if (scan == 1) {
            return registration(scanner, toServer, fromServer);
        } else if (scan == 2) {
            return login(scanner, toServer, fromServer);
        } else {
            return true;
        }
    }

    private static boolean registration(Scanner scanner, ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        System.out.println("Input login:");
        String login = scanner.next();
        System.out.println("Input password:");
        String password = scanner.next();
        RegistrationRequestInfo auth = new RegistrationRequestInfo(login, password);
        toServer.writeObject(auth);

        RegistrationRespondInfo respond = (RegistrationRespondInfo) fromServer.readObject();

        switch (respond.getStatus()) {
            case RegistrationRespondInfo.OK_STATUS: {
                System.out.println("Registration successful");
                log.debug("Registration successful");
                return true;
            }
            case RegistrationRespondInfo.SERVER_ERROR_STATUS: {
                System.out.println("Server error");
                log.debug("Server error");
                System.exit(1);
            }
            case RegistrationRespondInfo.DUPLICATED_LOGIN_STATUS: {
                System.out.println("User with that login already exists");
                return false;
            }
            default: return false;
        }
    }

    private static boolean login(Scanner scanner, ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        System.out.println("Input login:");
        String login = scanner.next();
        System.out.println("Input password:");
        String password = scanner.next();
        AuthorizationRequestInfo auth = new AuthorizationRequestInfo(login, password);
        toServer.writeObject(auth);

        AuthorizationRespondInfo respond = (AuthorizationRespondInfo) fromServer.readObject();

        switch (respond.getStatus()) {
            case AuthorizationRespondInfo.OK_STATUS: {
                accessLevel = respond.getRights();
                System.out.println("Connected");
                log.debug("Authorized");
                return true;
            }
            case AuthorizationRespondInfo.WRONG_CREDENTIALS_STATUS: {
                System.out.println("Wrong login/password provided");
                log.debug("Wrong login/password provided");
                break;
            }
            case AuthorizationRespondInfo.SERVER_ERROR_STATUS: {
                System.out.println("Server error");
                log.debug("Server error");
                System.exit(1);
            }
        }
        System.out.println("Login failed!!!");
        log.debug("Login failed!!!");
        return false;
    }
}
