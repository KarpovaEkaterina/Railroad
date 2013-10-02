package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.requests.BuyTicketRequestInfo;
import ru.tsystems.karpova.respond.BuyTicketRespondInfo;
import ru.tsystems.karpova.dao.PassengerDAO;
import ru.tsystems.karpova.dao.StationDAO;
import ru.tsystems.karpova.dao.TicketDAO;
import ru.tsystems.karpova.dao.TrainDAO;
import ru.tsystems.karpova.entities.Passenger;
import ru.tsystems.karpova.entities.Station;
import ru.tsystems.karpova.entities.Ticket;
import ru.tsystems.karpova.entities.Train;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class TicketService {

    private static Logger log = Logger.getLogger(TicketService.class);

    private TicketDAO ticketDAO;
    private PassengerDAO passengerDAO;
    private TrainDAO trainDAO;
    private StationDAO stationDAO;

    public TicketService() {
        ticketDAO = new TicketDAO();
        passengerDAO = new PassengerDAO();
        trainDAO = new TrainDAO();
        stationDAO = new StationDAO();
    }

    public BuyTicketRespondInfo buyTicket(BuyTicketRequestInfo buyTicketRequest) throws IOException {
        log.debug("Start method \"saveTicket\"");
        Passenger passenger = passengerDAO.loadPassenger(buyTicketRequest.getFirstname(),
                buyTicketRequest.getLastname(), new Timestamp(buyTicketRequest.getBirthday().getTime()));
        if (passenger == null) {
            passenger = new Passenger(buyTicketRequest.getFirstname(),
                    buyTicketRequest.getLastname(), new Timestamp(buyTicketRequest.getBirthday().getTime()));
            if (!passengerDAO.savePassenger(passenger)) {
                BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.SERVER_ERROR_STATUS);
                log.debug("Send BuyTicketRespondInfo to client with SERVER_ERROR_STATUS");
                return respond;
            }
            passenger = passengerDAO.loadPassenger(buyTicketRequest.getFirstname(),
                    buyTicketRequest.getLastname(), new Timestamp(buyTicketRequest.getBirthday().getTime()));
        }
        Train train = trainDAO.loadTrain(buyTicketRequest.getTrain());
        if (train == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_TRAIN_NAME_STATUS);
            log.debug("Send BuyTicketRespondInfo to client with WRONG_TRAIN_NAME_STATUS");
            return respond;
        }
        Station stationFrom = stationDAO.loadStationByName(buyTicketRequest.getStationFrom());
        if (stationFrom == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_FROM_NAME_STATUS);
            log.debug("Send BuyTicketRespondInfo to client with WRONG_STATION_FROM_NAME_STATUS");
            return respond;
        }
        Station stationTo = stationDAO.loadStationByName(buyTicketRequest.getStationTo());
        if (stationTo == null) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_TO_NAME_STATUS);
            log.debug("Send BuyTicketRespondInfo to client with WRONG_STATION_TO_NAME_STATUS");
            return respond;
        }
        List allStationsByTrain = trainDAO.getAllStationsByTrain(train);
        if (!stationInList(stationFrom, allStationsByTrain) || !stationInList(stationTo, allStationsByTrain) || !stationFromBeforeStationToInList(stationFrom, stationTo, allStationsByTrain)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_TRAIN_STATUS);
            log.debug("Send BuyTicketRespondInfo to client with WRONG_STATION_TRAIN_STATUS");
            return respond;
        }
        if (!stationFromBeforeStationToInList(stationFrom, stationTo, allStationsByTrain)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_STATION_ORDER_STATUS);
            log.debug("Send BuyTicketRespondInfo to client with WRONG_STATION_ORDER_STATUS");
            return respond;
        }
        if (!trainDAO.checkDepartureTime(train, stationFrom)) {
            BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.WRONG_DEPARTURE_TIME_STATUS);
            log.debug("Send GetAllRoutesRespondInfo to client with WRONG_DEPARTURE_TIME_STATUS");
            return respond;
        }
        synchronized (TrainDAO.class) {
            HashMap<Integer, Integer[]> passengerByStation = trainDAO.countOfPassengerOnEveryStation(train);

            if (0 == calcFreeSeats(passengerByStation, allStationsByTrain, stationFrom, stationTo, train)) {
                BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.NO_SEATS_STATUS);
                log.debug("Send BuyTicketRespondInfo to client with NO_SEATS_STATUS");
                return respond;
            } else {
                if (trainDAO.isAlreadyExistPassengerOnTrain(train, passenger)) {
                    BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.PASSENGER_ALREADY_EXISTS_STATUS);
                    log.debug("Send BuyTicketRespondInfo to client with PASSENGER_ALREADY_EXISTS_STATUS");
                    return respond;
                } else {
                    Ticket ticket = new Ticket();
                    ticket.setTrainByIdTrain(train);
                    ticket.setPassengerByIdPassenger(passenger);
                    ticket.setStationByStationFrom(stationFrom);
                    ticket.setStationByStationTo(stationTo);
                    ticket.setPrice(100);
                    if (ticketDAO.saveTicket(ticket)) {
                        BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.OK_STATUS);
                        log.debug("Send BuyTicketRespondInfo to client with OK_STATUS");
                        return respond;
                    } else {
                        BuyTicketRespondInfo respond = new BuyTicketRespondInfo(BuyTicketRespondInfo.SERVER_ERROR_STATUS);
                        log.debug("Send BuyTicketRespondInfo to client with SERVER_ERROR_STATUS");
                        return respond;
                    }
                }
            }
        }
    }

    public boolean stationFromBeforeStationToInList(Station stationFrom, Station stationTo, List<Object[]> allStationsByTrain) {
        log.debug("Start method \"stationFromBeforeStationToInList\"");
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

    public boolean stationInList(Station station, List<Object[]> allStationsByTrain) {
        log.debug("Start method \"stationInList\"");
        for (Object[] obj : allStationsByTrain) {
            String stationName = (String) obj[1];
            if (station.getName().equals(stationName)) {
                return true;
            }
        }
        return false;
    }

    public int calcFreeSeats(HashMap<Integer, Integer[]> passengerByStation, List<Object[]> allStationsByTrain, Station stationFrom, Station stationTo, Train train) {
        log.debug("Start method \"calcFreeSeats\"");
        int occupiedSeats = 0;
        int maxOccupied = -1;
        boolean calcMax = false;
        for (Object[] obj : allStationsByTrain) {
            Integer stationId = (Integer) obj[0];
            String stationName = (String) obj[1];
            if (stationTo.getName().equals(stationName)) {
                break;
            }
            if (passengerByStation.containsKey(stationId)) {
                Integer[] change = passengerByStation.get(stationId);
                occupiedSeats += change[0] - change[1];
            }
            if (stationFrom.getName().equals(stationName)) {
                calcMax = true;
            }
            if (calcMax && maxOccupied < occupiedSeats) {
                maxOccupied = occupiedSeats;
            }
        }
        return train.getTotalSeats() - maxOccupied;
    }
}
