package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.requests.AddTrainRequestInfo;
import ru.tsystems.karpova.requests.FindTrainRequestInfo;
import ru.tsystems.karpova.requests.ScheduleRequestInfo;
import ru.tsystems.karpova.requests.ViewPassengerByTrainRequestInfo;
import ru.tsystems.karpova.respond.*;
import ru.tsystems.karpova.dao.PassengerDAO;
import ru.tsystems.karpova.dao.RouteDAO;
import ru.tsystems.karpova.dao.TrainDAO;
import ru.tsystems.karpova.entities.Passenger;
import ru.tsystems.karpova.entities.Route;
import ru.tsystems.karpova.entities.Train;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TrainService {

    private static Logger log = Logger.getLogger(TrainService.class);

    private TrainDAO trainDAO;
    private PassengerDAO passengerDAO;
    private RouteDAO routeDAO;

    public TrainService() {
        trainDAO = new TrainDAO();
        passengerDAO = new PassengerDAO();
        routeDAO = new RouteDAO();
    }

    public ViewPassengerByTrainRespondInfo viewPassengerByTrain(ViewPassengerByTrainRequestInfo viewPassengerByTrainRequest) throws IOException {
        log.debug("Start method \"viewPassengerByTrain\"");
        Train train = trainDAO.loadTrain(viewPassengerByTrainRequest.getTrainName());
        if (train == null) {
            ViewPassengerByTrainRespondInfo respond = new ViewPassengerByTrainRespondInfo(ViewPassengerByTrainRespondInfo.WRONG_TRAIN_NAME_STATUS);
            log.debug("Send ViewPassengerByTrainRespondInfo to client with WRONG_TRAIN_NAME_STATUS");
            return respond;
        }
        List<Passenger> allPassengerByTrainList = passengerDAO.getAllPassengerByTrain(train);
        List<Object[]> allPassengerByTrain = new ArrayList<Object[]>();
        for (Passenger passenger : allPassengerByTrainList) {
            allPassengerByTrain.add(new Object[]{passenger.getFirstname(), passenger.getLastname(), passenger.getBirthday()});
        }
        ViewPassengerByTrainRespondInfo respond = new ViewPassengerByTrainRespondInfo(allPassengerByTrain);
        log.debug("Send ViewPassengerByTrainRespondInfo to client");
        return respond;
    }

    public GetAllTrainsRespondInfo getAllTrains() throws IOException {
        log.debug("Start method \"getAllTrains\"");
        List<Train> allTrainsList = trainDAO.getAllTrains();
        List<Object[]> allTrains = new ArrayList<Object[]>();
        for (Train train : allTrainsList) {
            allTrains.add(new Object[]{train.getName(), train.getTotalSeats(), train.getDeparture(), train.getRouteByIdRoute().getName()});
        }
        GetAllTrainsRespondInfo respond = new GetAllTrainsRespondInfo(allTrains);
        log.debug("Send GetAllTrainsRespondInfo to client");
        return respond;
    }

    public AddTrainRespondInfo addTrain(AddTrainRequestInfo addTrainRequest) throws IOException {
        log.debug("Start method \"addTrain\"");
        Route route = routeDAO.loadRoute(addTrainRequest.getRoute());
        if (route == null) {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.WRONG_ROUTE_NAME_STATUS);
            log.debug("Send AddTrainRespondInfo to client with WRONG_ROUTE_NAME_STATUS");
            return respond;
        }
        Train train = new Train(addTrainRequest.getTrainName(), addTrainRequest.getTotalSeats(),
                new Timestamp(addTrainRequest.getDepartureTime().getTime()), route);
        if (!trainDAO.saveTrain(train)) {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.SERVER_ERROR_STATUS);
            log.debug("Send AddTrainRespondInfo to client with SERVER_ERROR_STATUS");
            return respond;
        } else {
            AddTrainRespondInfo respond = new AddTrainRespondInfo(AddTrainRespondInfo.OK_STATUS);
            log.debug("Send AddTrainRespondInfo to client with OK_STATUS");
            return respond;
        }
    }

    public ScheduleRespondInfo scheduleByStation(ScheduleRequestInfo scheduleRequest) throws IOException {
        log.debug("Start method \"scheduleByStation\"");
        List<Object[]> trains = trainDAO.findTrainByStation(scheduleRequest.getStation());

        ScheduleRespondInfo respond = new ScheduleRespondInfo(trains);
        log.debug("Send ScheduleRespondInfo to client");
        return respond;
    }

    public FindTrainRespondInfo findTrain(FindTrainRequestInfo findRequest) throws IOException {
        log.debug("Start method \"findTrain\"");
        List<Object[]> trains = trainDAO.findTrain(findRequest.getStationFrom(),
                findRequest.getStationTo(), findRequest.getDateFrom(), findRequest.getDateTo());

        FindTrainRespondInfo respond = new FindTrainRespondInfo(trains);
        log.debug("Send FindTrainRespondInfo to client");
        return respond;
    }
}
