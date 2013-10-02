package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    private static Logger log = Logger.getLogger(Client.class);
    private static int accessLevel = -1;
    public static final int ACCESS_LEVEL_PASSENGER = 1;
    public static final int ACCESS_LEVEL_MANAGER = 2;
    public static final int ACCESS_LEVEL_ADMIN = 3;

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
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
            while (!start(toServer, fromServer, scanner)) ;
            while (homePage(toServer, fromServer, scanner)) ;
        } catch (IOException e) {
            log.error("Can't connect to server.", e);
        } catch (NoSuchElementException e) {
            log.error("User terminated session", e);
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
        log.debug("Start method \"start\"");
        System.out.println("1 - registration, 2 - login, 0 - exit");
        String scan = scanner.next().toLowerCase();
        while (!"1".equals(scan) && !"2".equals(scan) && !"0".equals(scan)) {
            System.out.println("Input 0, 1 or 2");
            scan = scanner.next().toLowerCase();
        }

        if ("1".equals(scan)) {
            int registration = ClientLoginHelper.registration(scanner, toServer, fromServer, ACCESS_LEVEL_PASSENGER);
            log.debug("Method \"registration\" returned " + registration);
            if (registration > 0) {
                log.info("Set accessLevel = ACCESS_LEVEL_PASSENGER");
                accessLevel = ACCESS_LEVEL_PASSENGER;
            }
            return registration > 0;
        } else if ("2".

                equals(scan)

                )

        {
            int accessLevel = ClientLoginHelper.login(scanner, toServer, fromServer);
            log.debug("Method \"login\" returned " + accessLevel);
            Client.accessLevel = accessLevel;
            return accessLevel != -1;
        } else

        {
            return true;
        }

    }

    private static boolean homePage(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException, ParseException {
        switch (accessLevel) {
            case ACCESS_LEVEL_PASSENGER: {
                return homePageForPassenger(toServer, fromServer, scanner);
            }
            case ACCESS_LEVEL_MANAGER: {
                return homePageForManager(toServer, fromServer, scanner);
            }
            case ACCESS_LEVEL_ADMIN: {
                return homePageForAdmin(toServer, fromServer, scanner);
            }
            default:
                return false;
        }
    }

    private static boolean homePageForAdmin(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.println("1 - add user, 0 - exit");
        log.debug("Start method \"homePageForAdmin\"");
        String scan = scanner.next().toLowerCase();
        while (!"1".equals(scan) && !"0".equals(scan)) {
            System.out.println("Input 1 or 0");
            scan = scanner.next().toLowerCase();
        }
        if ("1".equals(scan)) {
            ClientLoginHelper.registration(scanner, toServer, fromServer, ACCESS_LEVEL_MANAGER);
            return true;
        } else {
            return false;
        }
    }

    private static boolean homePageForManager(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start method \"homePageForManager\"");
        System.out.println("1 - add train, 2 - add station, 3 - add route, 4 - registered passengers, 5 - all trains, 6 - sell ticket, 0 - exit");
        String scan = scanner.next().toLowerCase();
        while (!"1".equals(scan) && !"2".equals(scan) && !"3".equals(scan) &&
                !"4".equals(scan) && !"5".equals(scan) && !"6".equals(scan) && !"0".equals(scan)) {
            System.out.println("Input 0, 1, 2, 3, 4, 5 or 6");
            scan = scanner.next().toLowerCase();
        }
        switch (scan.charAt(0)) {
            case '1': {
                return ManagerHomePageHelper.addTrain(toServer, fromServer, scanner);
            }
            case '2': {
                return ManagerHomePageHelper.addStation(toServer, fromServer, scanner);
            }
            case '3': {
                return ManagerHomePageHelper.addRoute(toServer, fromServer, scanner);
            }
            case '4': {
                return ManagerHomePageHelper.viewPassengerByTrain(toServer, fromServer, scanner);
            }
            case '5': {
                return ManagerHomePageHelper.veiwAllTrains(toServer, fromServer);
            }
            case '6': {
                return PassengerHomePageHelper.buyTicket(toServer, fromServer, scanner);
            }
            case '0': {
                return false;
            }
            default:
                return false;
        }
    }

    private static boolean homePageForPassenger(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws ParseException, IOException, ClassNotFoundException {
        log.debug("Start method \"homePageForPassenger\"");
        System.out.println("1 - find train, 2 - timetable by station, 3 - buy ticket, 0 - exit");
        String scan = scanner.next().toLowerCase();
        while (!"1".equals(scan) && !"2".equals(scan) && !"3".equals(scan) && !"0".equals(scan)) {
            System.out.println("Input 0, 1, 2 or 3");
            scan = scanner.next().toLowerCase();
        }
        switch (scan.charAt(0)) {
            case '1': {
                return PassengerHomePageHelper.findTrain(toServer, fromServer, scanner);
            }
            case '2': {
                return PassengerHomePageHelper.timetableByStation(toServer, fromServer, scanner);
            }
            case '3': {
                return PassengerHomePageHelper.buyTicket(toServer, fromServer, scanner);
            }
            case '0': {
                return false;
            }
            default:
                return false;
        }
    }

}
