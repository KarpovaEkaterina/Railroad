package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.BuyTicketRequestInfo;
import ru.tsystems.karpova.connector.requests.FindTrainRequestInfo;
import ru.tsystems.karpova.connector.requests.GetAllStationsRequestInfo;
import ru.tsystems.karpova.connector.requests.ScheduleRequestInfo;
import ru.tsystems.karpova.connector.respond.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class PassengerHomePageHelper {

    private static Logger log = Logger.getLogger(PassengerHomePageHelper.class);

    static boolean buyTicket(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"saveTicket\" method");
        System.out.println("Input train name:");
        String train = scanner.next();
        System.out.println("Input departure station:");
        String stationFrom = scanner.next();
        System.out.println("Input arrival station:");
        String stationTo = scanner.next();
        System.out.println("Input firstname:");
        String firstname = scanner.next();
        System.out.println("Input lastname:");
        String lastname = scanner.next();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        boolean flag;
        Date birthday = null;
        do {
            try {
                System.out.println("Input birthday (use format \"dd.MM.yyyy\")");
                birthday = dateFormat.parse(scanner.next());
                flag = true;
            } catch (ParseException e) {
                log.error("Incorrect date");
                flag = false;
            }
        } while (!flag);

        BuyTicketRequestInfo req = new BuyTicketRequestInfo(train, stationFrom, stationTo, firstname, lastname, birthday);
        log.debug("Send BuyTicketRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof BuyTicketRespondInfo) {
                BuyTicketRespondInfo respond = (BuyTicketRespondInfo) o;
                log.debug("Received BuyTicketRespondInfo from server");
                switch (respond.getStatus()) {
                    case BuyTicketRespondInfo.NO_SEATS_STATUS: {
                        System.out.println("No seats");
                        break;
                    }
                    case BuyTicketRespondInfo.OK_STATUS: {
                        System.out.println("Ticket bought");
                        break;
                    }
                    case BuyTicketRespondInfo.PASSENGER_ALREADY_EXISTS_STATUS: {
                        System.out.println("Passenger already registrant on train");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_DEPARTURE_TIME_STATUS: {
                        System.out.println("Ticketing has already been closed");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_TRAIN_NAME_STATUS: {
                        System.out.println("Train not found");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_STATION_FROM_NAME_STATUS: {
                        System.out.println("Wrong departure station name");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_STATION_TO_NAME_STATUS: {
                        System.out.println("Wrong arrival station name");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_STATION_TRAIN_STATUS: {
                        System.out.println("Route hasn't this station(s)");
                        break;
                    }
                    case BuyTicketRespondInfo.WRONG_STATION_ORDER_STATUS: {
                        System.out.println("Wrong departure and arrival stations order");
                        break;
                    }
                }
                return true;
            }
        }
        log.error("Unknown object type");
        return false;
    }

    static boolean timetableByStation(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException {
        log.debug("Start \"timetableByStation\" method");
        List<String> listAllStation = listStations(toServer, fromServer);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        if (listAllStation == null) {
            return false;
        }
        String station;
        do {
            System.out.println("Departure station:");
            station = scanner.next();
        }
        while (!listAllStation.contains(station));

        ScheduleRequestInfo req = new ScheduleRequestInfo(station);
        log.debug("Send ScheduleRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof ScheduleRespondInfo) {
                ScheduleRespondInfo respond = (ScheduleRespondInfo) o;
                log.debug("Received ScheduleRespondInfo from server");

                System.out.println("Train name --- Departure time");
                for (ScheduleRespondInfo.TrainInfo info : respond.getTrains()) {
                    System.out.println(info.getTrainName() + " --- " + dateFormat.format(info.getDeparture()));
                }
            }
        }
        log.error("Unknown object type");
        return false;
    }

    static boolean findTrain(ObjectOutputStream toServer, ObjectInputStream fromServer, Scanner scanner) throws IOException, ClassNotFoundException, ParseException {
        log.debug("Start \"findTrain\" method");
        List<String> listAllStation = listStations(toServer, fromServer);
        if (listAllStation == null) {
            return false;
        }
        String stationFrom;
        String stationTo;
        Date dateFrom = null;
        Date dateTo = null;
        do {
            System.out.println("Departure station:");
            stationFrom = scanner.next();
        }
        while (!listAllStation.contains(stationFrom));
        do {
            System.out.println("Arrival station:");
            stationTo = scanner.next();
        } while (!listAllStation.contains(stationTo));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        boolean flag;
        do {
            try {
                System.out.println("Input departure time (use format \"dd.MM.yyyy hh:mm\")");
                dateFrom = dateFormat.parse(scanner.next() + " " + scanner.next());
                flag = true;
            } catch (ParseException e) {
                System.out.println("Incorrect date");
                log.error("Incorrect date");
                flag = false;
            }
        } while (!flag);
        do {
            try {
                System.out.println("Input arrival time (use format \"dd.MM.yyyy hh:mm\")");
                dateTo = dateFormat.parse(scanner.next() + " " + scanner.next());
                flag = true;
            } catch (ParseException e) {
                System.out.println("Incorrect date");
                log.error("Incorrect date");
                flag = false;
            }
        } while (!flag);

        FindTrainRequestInfo req = new FindTrainRequestInfo(stationFrom, stationTo, dateFrom, dateTo);
        log.debug("Send FindTrainRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return false;
            } else if (o instanceof FindTrainRespondInfo) {
                FindTrainRespondInfo respond = (FindTrainRespondInfo) o;
                log.debug("Received FindTrainRespondInfo from server");

                System.out.println("Train name --- Departure time");
                for (FindTrainRespondInfo.TrainInfo info : respond.getTrains()) {
                    System.out.println(info.getTrainName() + " --- " + dateFormat.format(info.getDeparture()));
                }
                return true;
            }
        }
        log.error("Unknown object type");
        return false;
    }

    static List<String> listStations(ObjectOutputStream toServer, ObjectInputStream fromServer) throws IOException, ClassNotFoundException {
        log.debug("Start \"listStations\" method");
        GetAllStationsRequestInfo req = new GetAllStationsRequestInfo();
        log.debug("Send GetAllStationsRequestInfo to server");
        toServer.writeObject(req);

        Object o = fromServer.readObject();
        if (o instanceof RespondInfo) {
            if (((RespondInfo) o).getStatus() == RespondInfo.SERVER_ERROR_STATUS) {
                System.out.println("Server error");
                log.debug("Server error");
                return null;
            } else if (o instanceof GetAllStationRespondInfo) {
                GetAllStationRespondInfo respond = (GetAllStationRespondInfo) o;
                log.debug("Received GetAllStationRespondInfo from server");

                List<String> allStations = respond.getListAllStation();
                System.out.println("Stations:");
                for (String station : allStations) {
                    System.out.println(station);
                }
                log.debug("Get all station");
                return allStations;

            }
        }
        log.error("Unknown object type");
        return null;
    }
}