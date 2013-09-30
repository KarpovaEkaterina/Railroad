package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.*;
import ru.tsystems.karpova.connector.respond.*;
import ru.tsystems.karpova.dao.*;
import ru.tsystems.karpova.entities.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestHandler implements Runnable {
    Socket connectionSocket;
    private static Logger log = Logger.getLogger(RequestHandler.class);

    RequestHandler(Socket connectionSocket) {
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
                    ServerLoginHelper.registration(toClient, (RegistrationRequestInfo) o);
                } else if (o instanceof AuthorizationRequestInfo) {
                    ServerLoginHelper.login(toClient, (AuthorizationRequestInfo) o);
                } else if (o instanceof GetAllStationsRequestInfo) {
                    getAllStation(toClient);
                } else if (o instanceof FindTrainRequestInfo) {
                    findTrain(toClient, (FindTrainRequestInfo) o);
                } else if (o instanceof ScheduleRequestInfo) {
                    scheduleByStation(toClient, (ScheduleRequestInfo) o);
                } else if (o instanceof BuyTicketRequestInfo) {
                    buyTicket(toClient, (BuyTicketRequestInfo) o);
                } else if (o instanceof GetAllRoutesRequestInfo) {
                    getAllRoutes(toClient);
                } else if (o instanceof AddTrainRequestInfo) {
                    addTrain(toClient, (AddTrainRequestInfo) o);
                } else if (o instanceof AddStationRequestInfo) {
                    addStation(toClient, (AddStationRequestInfo) o);
                } else if (o instanceof GetAllTrainsRequestInfo) {
                    getAllTrains(toClient);
                } else if (o instanceof ViewPassengerByTrainRequestInfo) {
                    viewPassengerByTrain(toClient, (ViewPassengerByTrainRequestInfo) o);
                } else if (o instanceof GetAllWaysRequestInfo) {
                    getAllWays(toClient);
                } else if (o instanceof AddRouteRequestInfo) {
                    addRoute(toClient, (AddRouteRequestInfo) o);
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("Can't find class for received object", e);
            try {
                RespondInfo respond = new RespondInfo(RespondInfo.SERVER_ERROR_STATUS);
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

    private void addRoute(ObjectOutputStream toClient, AddRouteRequestInfo addRouteRequest) throws IOException {
        List<String> stationsForNewRoute = addRouteRequest.getStationsForNewRoute();
        Map<String, Object[]> newWay = addRouteRequest.getNewWay();
        String delimiter = addRouteRequest.getDelimiter();
        for (String stations : newWay.keySet()) {
            String stationAName = stations.split(delimiter)[0];
            String stationBName = stations.split(delimiter)[1];
            Station stationA = StationDAO.loadStationByName(stationAName);
            Station stationB = StationDAO.loadStationByName(stationBName);
            Way way = new Way();
            way.setStationByIdStation1(stationA);
            way.setStationByIdStation2(stationB);
            way.setTime((Timestamp) newWay.get(stations)[0]);
            way.setPrice((Double) newWay.get(stations)[1]);
            if (!WayDAO.saveWay(way)) {
                 AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
                toClient.writeObject(respond);
            }
        }
        Route route = new Route();
        route.setName(addRouteRequest.getRouteName());
        if (!RouteDAO.saveRoute(route)) {
            AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
            toClient.writeObject(respond);
            return;
        }
        List<Schedule> schedules = new ArrayList<Schedule>();
        route = RouteDAO.loadRoute(route.getName());
        for (int i = 1; i < stationsForNewRoute.size(); i++) {
            Way way = WayDAO.loadWayByStations(stationsForNewRoute.get(i - 1), stationsForNewRoute.get(i));
            Schedule schedule = new Schedule();
            schedule.setRouteByIdRoute(route);
            schedule.setWayByIdWay(way);
            schedule.setSeqNumber(i);
            schedules.add(schedule);
            if (!ScheduleDAO.saveSchedule(schedule)) {
                AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
                toClient.writeObject(respond);
            }
        }
        AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.OK_STATUS);
        toClient.writeObject(respond);
    }

    private void getAllWays(ObjectOutputStream toClient) throws IOException {
        List<Object[]> allWaysList = WayDAO.getAllWays();
        Map<String, Object[]> allWays = new HashMap<String, Object[]>();
        final String delimiter = "_%DELIM%_";
        for (Object[] way : allWaysList) {
            allWays.put(way[0] + delimiter + way[1], new Object[]{way[2], way[3]});
        }
        GetAllWaysRespondInfo respond = new GetAllWaysRespondInfo(allWays, delimiter);
        toClient.writeObject(respond);
    }

    private void viewPassengerByTrain(ObjectOutputStream toClient, ViewPassengerByTrainRequestInfo viewPassengerByTrainRequest) throws IOException {
        Train train = TrainDAO.loadTrain(viewPassengerByTrainRequest.getTrainName());
        if (train == null) {
            ViewPassengerByTrainRespondInfo respond = new ViewPassengerByTrainRespondInfo(ViewPassengerByTrainRespondInfo.WRONG_TRAIN_NAME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        List<Passenger> allPassengerByTrainList = PassengerDAO.getAllPassengerByTrain(train);
        List<Object[]> allPassengerByTrain = new ArrayList<Object[]>();
        for (Passenger passenger : allPassengerByTrainList) {
            allPassengerByTrain.add(new Object[]{passenger.getFirstname(), passenger.getLastname(), passenger.getBirthday()});
        }
        ViewPassengerByTrainRespondInfo respond = new ViewPassengerByTrainRespondInfo(allPassengerByTrain);
        toClient.writeObject(respond);
    }

    private void getAllTrains(ObjectOutputStream toClient) throws IOException {
        List<Train> allTrainsList = TrainDAO.getAllTrains();
        List<Object[]> allTrains = new ArrayList<Object[]>();
        for (Train train : allTrainsList) {
            allTrains.add(new Object[]{train.getName(), train.getTotalSeats(), train.getDeparture(), train.getRouteByIdRoute().getName()});
        }
        GetAllTrainsRespondInfo respond = new GetAllTrainsRespondInfo(allTrains);
        toClient.writeObject(respond);
    }

    private void addStation(ObjectOutputStream toClient, AddStationRequestInfo addStationRequest) throws IOException {
        Station station = new Station(addStationRequest.getStationName());
        if (!StationDAO.saveNewStation(station)) {
            AddStationRespondInfo respond = new AddStationRespondInfo(AddStationRespondInfo.SERVER_ERROR_STATUS);
            toClient.writeObject(respond);
            return;
        } else {
            AddStationRespondInfo respond = new AddStationRespondInfo(AddStationRespondInfo.OK_STATUS);
            toClient.writeObject(respond);
            return;
        }

    }

    private void addTrain(ObjectOutputStream toClient, AddTrainRequestInfo addTrainRequest) throws IOException {
        Route route = RouteDAO.loadRoute(addTrainRequest.getRoute());
        if (route == null) {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.WRONG_ROUTE_NAME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        Train train = new Train(addTrainRequest.getTrainName(), addTrainRequest.getTotalSeats(),
                new Timestamp(addTrainRequest.getDepartureTime().getTime()), route);
        if (!TrainDAO.saveNewTrain(train)) {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.SERVER_ERROR_STATUS);
            toClient.writeObject(respond);
            return;
        } else {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.OK_STATUS);
            toClient.writeObject(respond);
            return;
        }
    }

    private void buyTicket(ObjectOutputStream toClient, BuyTicketRequestInfo buyTicketRequest) throws IOException {
        Passenger passenger = PassengerDAO.loadPassenger(buyTicketRequest.getFirstname(),
                buyTicketRequest.getLastname(), new Timestamp(buyTicketRequest.getBirthday().getTime()));
        if (passenger == null) {
            passenger = new Passenger(buyTicketRequest.getFirstname(),
                    buyTicketRequest.getLastname(), new Timestamp(buyTicketRequest.getBirthday().getTime()));
            if (!PassengerDAO.saveNewPassenger(passenger)) {
                BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.SERVER_ERROR_STATUS);
                toClient.writeObject(respond);
                return;
            }
        }
        Train train = TrainDAO.loadTrain(buyTicketRequest.getTrain());
        if (train == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_TRAIN_NAME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        Station stationFrom = StationDAO.loadStationByName(buyTicketRequest.getStationFrom());
        if (stationFrom == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_FROM_NAME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        Station stationTo = StationDAO.loadStationByName(buyTicketRequest.getStationTo());
        if (stationTo == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_TO_NAME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        List allStationsByTrain = TrainDAO.getAllStationsByTrain(train);
        if (!stationInList(stationFrom, allStationsByTrain) || !stationInList(stationTo, allStationsByTrain) || !stationFromBeforeStationToInList(stationFrom, stationTo, allStationsByTrain)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_TRAIN_STATUS);
            toClient.writeObject(respond);
            return;
        }
        if (!stationFromBeforeStationToInList(stationFrom, stationTo, allStationsByTrain)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_ORDER_STATUS);
            toClient.writeObject(respond);
            return;
        }
        if (!TrainDAO.checkDepartureTime(train, stationFrom)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_DEPARTURE_TIME_STATUS);
            toClient.writeObject(respond);
            return;
        }
        synchronized (TrainDAO.class) {
            HashMap<Integer, Integer[]> passengerByStation = TrainDAO.countOfPassengerOnEveryStation(train);

            if (0 == calcFreeSeats(passengerByStation, allStationsByTrain, stationFrom, stationTo, train)) {
                BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.NO_SEATS_STATUS);
                toClient.writeObject(respond);
                return;
            } else {
                if (TrainDAO.isAlreadyExistPassengerOnTrain(train, passenger)) {
                    BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.PASSENGER_ALREADY_EXISTS_STATUS);
                    toClient.writeObject(respond);
                    return;
                } else {
                    if (TicketDAO.buyTicket(train, passenger, stationFrom, stationTo)) {
                        BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.OK_STATUS);
                        toClient.writeObject(respond);
                        return;
                    } else {
                        BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.SERVER_ERROR_STATUS);
                        toClient.writeObject(respond);
                        return;
                    }
                }
            }
        }

    }

    private boolean stationFromBeforeStationToInList(Station stationFrom, Station stationTo, List<Object[]> allStationsByTrain) {
        boolean firstFound = false;
        for (Object[] obj : allStationsByTrain) {
            String stationName = (String) obj[1];
            if (stationFrom.getName().equals(stationName)) {
                firstFound = true;
            }
            if (stationTo.getName().equals(stationName)) {
                return firstFound;
            }
        }
        return false;
    }

    private boolean stationInList(Station station, List<Object[]> allStationsByTrain) {
        for (Object[] obj : allStationsByTrain) {
            String stationName = (String) obj[1];
            if (station.getName().equals(stationName)) {
                return true;
            }
        }
        return false;
    }

    private int calcFreeSeats(HashMap<Integer, Integer[]> passengerByStation, List<Object[]> allStationsByTrain, Station stationFrom, Station stationTo, Train train) {
        int initialSeats = 0;
        int maxOccupied = -1;
        boolean calcMax = false;
        for (Object[] obj : allStationsByTrain) {
            Integer stationId = (Integer) obj[0];
            String stationName = (String) obj[1];
            if (passengerByStation.containsKey(stationId)) {
                Integer[] change = passengerByStation.get(stationId);
                initialSeats += change[0] - change[1];
            }
            if (stationFrom.getName().equals(stationName)) {
                calcMax = true;
            }
            if (calcMax) {
                maxOccupied = initialSeats;
            }
            if (stationTo.getName().equals(stationName)) {
                break;
            }
        }
        return train.getTotalSeats() - maxOccupied;
    }

    private void scheduleByStation(ObjectOutputStream toClient, ScheduleRequestInfo scheduleRequest) throws IOException {
        List<Object[]> trains = TrainDAO.findTrainByStation(scheduleRequest.getStation());

        ScheduleRespondInfo respond = new ScheduleRespondInfo(trains);
        toClient.writeObject(respond);

    }

    private void findTrain(ObjectOutputStream toClient, FindTrainRequestInfo findRequest) throws IOException {
        List<Object[]> trains = TrainDAO.findTrain(findRequest.getStationFrom(),
                findRequest.getStationTo(), findRequest.getDateFrom(), findRequest.getDateTo());

        FindTrainRespondInfo respond = new FindTrainRespondInfo(trains);
        toClient.writeObject(respond);

    }

    private void getAllStation(ObjectOutputStream toClient) throws IOException {
        List<Station> allStationList = StationDAO.getAllStation();
        List<String> allStation = new ArrayList<String>();
        for (Station station : allStationList) {
            allStation.add(station.getName());
        }
        GetAllStationRespondInfo respond = new GetAllStationRespondInfo(allStation);
        toClient.writeObject(respond);
    }

    private void getAllRoutes(ObjectOutputStream toClient) throws IOException {
        List<Route> allRoutesList = RouteDAO.getAllRoutes();
        List<String> allRoutes = new ArrayList<String>();
        for (Route route : allRoutesList) {
            allRoutes.add(route.getName());
        }
        GetAllRoutesRespondInfo respond = new GetAllRoutesRespondInfo(allRoutes);
        toClient.writeObject(respond);
    }
}
