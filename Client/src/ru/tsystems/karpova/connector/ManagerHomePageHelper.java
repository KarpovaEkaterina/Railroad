package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.AddTrainRequestInfo;
import ru.tsystems.karpova.connector.requests.GetAllRoutesRequestInfo;
import ru.tsystems.karpova.connector.respond.AddTrainRespondInfo;
import ru.tsystems.karpova.connector.respond.GetAllRoutesRespondInfo;
import ru.tsystems.karpova.connector.respond.RespondInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ManagerHomePageHelper {

    private static Logger log = Logger.getLogger(ManagerHomePageHelper.class);

    public static boolean addTrain(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        List<String> listAllRoute = listRoute(toServer, fromServer);
        System.out.println("Input train name:");
        String trainName = scanner.next();
        String route;
        do {
            System.out.println("Input route:");
            route = scanner.next();
        }
        while (!listAllRoute.contains(route));
        System.out.println("Input total seats number:");
        int totalSeats = scanner.nextInt();
        Date departureTime = null;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        boolean flag;
        do {
            try {
                System.out.println("Input departure time (use format \"dd.MM.yyyy hh:mm\")");
                departureTime = dateFormat.parse(scanner.next() + " " + scanner.next());
                flag = true;
            } catch (ParseException e) {
                log.error("Incorrect date");
                flag = false;
            }
        } while (!flag);

        AddTrainRequestInfo req = new AddTrainRequestInfo(trainName, route, totalSeats, departureTime);
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (o instanceof AddTrainRespondInfo) {
                AddTrainRespondInfo respond = (AddTrainRespondInfo) o;


            } else if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            }
        }
        log.error("Unknown object type");
        return false;
    }

    private static List<String> listRoute(ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        GetAllRoutesRequestInfo req = new GetAllRoutesRequestInfo();
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (o instanceof GetAllRoutesRespondInfo) {
                GetAllRoutesRespondInfo respond = (GetAllRoutesRespondInfo) o;

                List<String> allRoutes = respond.getListAllRoutes();
                System.out.println("Routes:");
                for (String route : allRoutes) {
                    System.out.println(route);
                }
                log.debug("Get all routes");
                return allRoutes;

            } else if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return null;
            }
        }
        log.error("Unknown object type");
        return null;
    }

    public static boolean addStation(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) {
        return false;
    }

    public static boolean addRoute(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) {
        return false;
    }

    public static boolean viewPassenger(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) {
        return false;
    }

    public static boolean veiwAllTrains(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) {
        return false;
    }
}
