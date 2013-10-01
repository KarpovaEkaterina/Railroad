package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.*;
import ru.tsystems.karpova.connector.respond.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerHomePageHelper {

    private static Logger log = Logger.getLogger(ManagerHomePageHelper.class);

    public static boolean addTrain(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"addTrain\" method");
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
        boolean flag = false;
        do {
            try {
                System.out.println("Input departure time (use format \"dd.MM.yyyy hh:mm\")");
                departureTime = dateFormat.parse(scanner.next() + " " + scanner.next());
                Date currentTime = new Date();
                if (currentTime.before(departureTime)) {
                    flag = true;
                } else {
                    System.out.println("Date have to be in future");
                }
            } catch (ParseException e) {
                log.error("Incorrect date");
                flag = false;
            }
        } while (!flag);

        AddTrainRequestInfo req = new AddTrainRequestInfo(trainName, route, totalSeats, departureTime);
        log.debug("Send AddTrainRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof AddTrainRespondInfo) {
                AddTrainRespondInfo respond = (AddTrainRespondInfo) o;
                log.debug("Received AddTrainRespondInfo from server");

                switch (respond.getStatus()) {
                    case AddTrainRespondInfo.OK_STATUS: {
                        System.out.println("Train added");
                        log.debug("Train added");
                        break;
                    }
                    case AddTrainRespondInfo.WRONG_ROUTE_NAME_STATUS: {
                        System.out.println("Wrong route name");
                        log.debug("Wrong route name");
                        break;
                    }
                }
                return true;
            }
        }
        log.error("Unknown object type");
        return false;
    }

    private static List<String> listRoute(ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        log.debug("Start \"listRoute\" method");
        GetAllRoutesRequestInfo req = new GetAllRoutesRequestInfo();
        log.debug("Send GetAllRoutesRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return null;
            } else if (o instanceof GetAllRoutesRespondInfo) {
                GetAllRoutesRespondInfo respond = (GetAllRoutesRespondInfo) o;
                log.debug("Received GetAllRoutesRespondInfo from server");

                List<String> allRoutes = respond.getListAllRoutes();
                System.out.println("Routes:");
                for (String route : allRoutes) {
                    System.out.println(route);
                }
                log.debug("Get all routes");
                return allRoutes;

            }
        }
        log.error("Unknown object type");
        return null;
    }

    public static boolean addStation(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"addStation\" method");
        List<String> listAllStation = PassengerHomePageHelper.listStations(toServer, fromServer);
        String stationName;
        do {
            System.out.println("Station name must be unique!");
            System.out.println("Input station name:");
            stationName = scanner.next();
        } while (listAllStation.contains(stationName));

        AddStationRequestInfo req = new AddStationRequestInfo(stationName);
        log.debug("Send AddStationRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof AddStationRespondInfo) {
                AddStationRespondInfo respond = (AddStationRespondInfo) o;
                log.debug("Received AddStationRespondInfo from server");

                if (respond.getStatus() == AddStationRespondInfo.OK_STATUS) {
                    System.out.println("Station added");
                    log.debug("Station added");
                    return true;
                } else {
                    System.out.println("Server error");
                    log.debug("Server error");
                    return false;
                }
            }
        }
        log.error("Unknown object type");
        return false;
    }

    public static boolean addRoute(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"addRoute\" method");
        List<String> listAllRoute = listRoute(toServer, fromServer);
        Map<String, Object[]> ways = listWays(toServer, fromServer);
        String routeName;
        do {
            System.out.println("Route name must be unique!");
            System.out.println("Input route name:");
            routeName = scanner.next();
        } while (listAllRoute.contains(routeName));
        List<String> listAllStation = PassengerHomePageHelper.listStations(toServer, fromServer);
        List<String> stationsForNewRoute = new ArrayList<String>();
        Map<String, Object[]> newWay = new HashMap<String, Object[]>();
        String station;
        do {
            System.out.println("Input first station");
            station = scanner.next();
        } while (!listAllStation.contains(station));
        stationsForNewRoute.add(station);
        do {
            do {
                System.out.println("Input next station or \"end\" to stop");
                station = scanner.next();
            } while (!listAllStation.contains(station) && !station.equals("end"));
            if (station.equals("end")) {
                if (stationsForNewRoute.size() < 2) {
                    System.out.println("Can't create route from one station");
                    station = "";
                }
                continue;
            } else if (stationsForNewRoute.contains(station)) {
                System.out.println("Station already added to route");
                station = "";
                continue;
            } else if (!ways.containsKey(stationsForNewRoute.get(stationsForNewRoute.size() - 1) + delimiter + station)) {
                System.out.println("Ways is unknown.");
                boolean flag;
                double price = 0.0;
                do {
                    try {
                        System.out.println("Input price");
                        price = scanner.nextDouble();
                        flag = true;
                    } catch (InputMismatchException e) {
                        log.error("Incorrect price");
                        flag = false;
                    }
                } while (!flag);
                Timestamp time = null;
                do {
                    try {
                        System.out.println("Input time \"hh:mm\"");
                        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
                        time = new Timestamp(dateFormat.parse(scanner.next()).getTime());
                        flag = true;
                    } catch (ParseException e) {
                        log.error("Incorrect time");
                        flag = false;
                    }
                } while (!flag);
                newWay.put(stationsForNewRoute.get(stationsForNewRoute.size() - 1) + delimiter + station, new Object[]{time, price});
            }
            stationsForNewRoute.add(station);
        } while (!station.equals("end"));

        AddRouteRequestInfo req = new AddRouteRequestInfo(delimiter, routeName, stationsForNewRoute, newWay);
        log.debug("Send AddRouteRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof AddRouteRespondInfo) {
                AddRouteRespondInfo respond = (AddRouteRespondInfo) o;
                log.debug("Received AddRouteRespondInfo from server");

                if (respond.getStatus() == AddRouteRespondInfo.OK_STATUS) {
                    System.out.println("Route added");
                    log.debug("Route added");
                    return true;
                }
            }
        }
        log.error("Unknown object type");
        return false;
    }

    private static String delimiter = "";

    private static Map<String, Object[]> listWays(ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        log.debug("Start \"listWays\" method");
        GetAllWaysRequestInfo req = new GetAllWaysRequestInfo();
        log.debug("Send GetAllWaysRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return null;
            } else if (o instanceof GetAllWaysRespondInfo) {
                GetAllWaysRespondInfo respond = (GetAllWaysRespondInfo) o;
                log.debug("Received GetAllWaysRespondInfo from server");
                delimiter = respond.getDelimiter();
                return respond.getListAllWays();

            }
        }
        log.error("Unknown object type");
        return null;
    }

    public static boolean viewPassengerByTrain(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"viewPassengerByTrain\" method");
        System.out.println("Input train name");
        String trainName = scanner.next();
        ViewPassengerByTrainRequestInfo request = new ViewPassengerByTrainRequestInfo(trainName);
        log.debug("Send ViewPassengerByTrainRequestInfo to server");
        toServer.writeObject(request);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof ViewPassengerByTrainRespondInfo) {
                ViewPassengerByTrainRespondInfo respond = (ViewPassengerByTrainRespondInfo) o;
                log.debug("Received ViewPassengerByTrainRespondInfo from server");

                if (respond.getStatus() == ViewPassengerByTrainRespondInfo.WRONG_TRAIN_NAME_STATUS) {
                    System.out.println("Wrong train name");
                    log.debug("Wrong train name");
                    return true;
                }

                List<Object[]> allPassenger = respond.getListAllPassengerByTrain();
                System.out.println("Passenger:");
                System.out.println("Firstname --- Lastname --- Birthday");
                for (Object[] passenger : allPassenger) {
                    System.out.print(passenger[0] + "  ");
                    System.out.print(passenger[1] + "  ");
                    System.out.println(passenger[2]);
                }
                log.debug("Get all passenger");
                return true;
            }
        }
        log.error("Unknown object type");
        return false;
    }

    public static boolean veiwAllTrains(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"veiwAllTrains\" method");
        GetAllTrainsRequestInfo req = new GetAllTrainsRequestInfo();
        log.debug("Send GetAllTrainsRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof GetAllTrainsRespondInfo) {
                GetAllTrainsRespondInfo respond = (GetAllTrainsRespondInfo) o;
                log.debug("Received GetAllTrainsRespondInfo from server");

                List<Object[]> allTrains = respond.getListAllTrains();
                System.out.println("Trains:");
                System.out.println("Trains name --- Total seats --- Departure time --- Route");
                for (Object[] trains : allTrains) {
                    System.out.print(trains[0] + "  ");
                    System.out.print(trains[1] + "  ");
                    System.out.print(trains[2] + "  ");
                    System.out.println(trains[3]);
                }
                log.debug("Get all trains");
                return true;
            }
        }
        log.error("Unknown object type");
        return false;
    }
}
