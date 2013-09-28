package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.connector.requests.*;
import ru.tsystems.karpova.connector.respond.*;
import ru.tsystems.karpova.dao.StationDAO;
import ru.tsystems.karpova.dao.TrainDAO;
import ru.tsystems.karpova.entities.Station;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    private void buyTicket(ObjectOutputStream toClient, BuyTicketRequestInfo buyTicketRequest) throws IOException {

//        BuyTicketRespondInfo respond = new BuyTicketRespondInfo();
//        toClient.writeObject(respond);

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
}
