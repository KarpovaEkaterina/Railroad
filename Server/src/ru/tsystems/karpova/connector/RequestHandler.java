package ru.tsystems.karpova.connector;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.requests.*;
import ru.tsystems.karpova.respond.RespondInfo;
import ru.tsystems.karpova.service.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class RequestHandler implements Runnable {

    Socket connectionSocket;
    private static Logger log = Logger.getLogger(RequestHandler.class);

    private TrainService trainService;
    private TicketService ticketService;
    private StationService stationService;
    private RouteService routeService;
    private WayService wayService;
    private UserService userService;

    RequestHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        trainService = new TrainService();
        ticketService = new TicketService();
        stationService = new StationService();
        routeService = new RouteService();
        wayService = new WayService();
        userService = new UserService();
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
                    log.debug("Received RegistrationRequestInfo from client");
                    registration(toClient, (RegistrationRequestInfo) o);
                } else if (o instanceof AuthorizationRequestInfo) {
                    log.debug("Received AuthorizationRequestInfo from client");
                    login(toClient, (AuthorizationRequestInfo) o);
                } else if (o instanceof GetAllStationsRequestInfo) {
                    log.debug("Received GetAllStationsRequestInfo from client");
                    getAllStation(toClient);
                } else if (o instanceof FindTrainRequestInfo) {
                    log.debug("Received FindTrainRequestInfo from client");
                    findTrain(toClient, (FindTrainRequestInfo) o);
                } else if (o instanceof ScheduleRequestInfo) {
                    log.debug("Received ScheduleRequestInfo from client");
                    scheduleByStation(toClient, (ScheduleRequestInfo) o);
                } else if (o instanceof BuyTicketRequestInfo) {
                    log.debug("Received BuyTicketRequestInfo from client");
                    buyTicket(toClient, (BuyTicketRequestInfo) o);
                } else if (o instanceof GetAllRoutesRequestInfo) {
                    log.debug("Received GetAllRoutesRequestInfo from client");
                    getAllRoutes(toClient);
                } else if (o instanceof AddTrainRequestInfo) {
                    log.debug("Received AddTrainRequestInfo from client");
                    addTrain(toClient, (AddTrainRequestInfo) o);
                } else if (o instanceof AddStationRequestInfo) {
                    log.debug("Received AddStationRequestInfo from client");
                    addStation(toClient, (AddStationRequestInfo) o);
                } else if (o instanceof GetAllTrainsRequestInfo) {
                    log.debug("Received GetAllTrainsRequestInfo from client");
                    getAllTrains(toClient);
                } else if (o instanceof ViewPassengerByTrainRequestInfo) {
                    log.debug("Received ViewPassengerByTrainRequestInfo from client");
                    viewPassengerByTrain(toClient, (ViewPassengerByTrainRequestInfo) o);
                } else if (o instanceof GetAllWaysRequestInfo) {
                    log.debug("Received GetAllWaysRequestInfo from client");
                    getAllWays(toClient);
                } else if (o instanceof AddRouteRequestInfo) {
                    log.debug("Received AddRouteRequestInfo from client");
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

    public void registration(ObjectOutputStream toClient, RegistrationRequestInfo regRequest) throws IOException {
        toClient.writeObject(userService.registration(regRequest));
    }

    public void login(ObjectOutputStream toClient, AuthorizationRequestInfo authRequest) throws IOException {
        toClient.writeObject(userService.login(authRequest));
    }

    public void addRoute(ObjectOutputStream toClient, AddRouteRequestInfo addRouteRequest) throws IOException {
        toClient.writeObject(routeService.addRoute(addRouteRequest));
    }

    public void getAllWays(ObjectOutputStream toClient) throws IOException {
        toClient.writeObject(wayService.getAllWays());
    }

    public void viewPassengerByTrain(ObjectOutputStream toClient,
                                     ViewPassengerByTrainRequestInfo viewPassengerByTrainRequest) throws IOException {
        toClient.writeObject(trainService.viewPassengerByTrain(viewPassengerByTrainRequest));
    }

    public void getAllTrains(ObjectOutputStream toClient) throws IOException {
        toClient.writeObject(trainService.getAllTrains());
    }

    public void addStation(ObjectOutputStream toClient, AddStationRequestInfo addStationRequest) throws IOException {
        toClient.writeObject(stationService.addStation(addStationRequest));
    }

    public void addTrain(ObjectOutputStream toClient, AddTrainRequestInfo addTrainRequest) throws IOException {
        toClient.writeObject(trainService.addTrain(addTrainRequest));
    }

    public void buyTicket(ObjectOutputStream toClient, BuyTicketRequestInfo buyTicketRequest) throws IOException {
        toClient.writeObject(ticketService.buyTicket(buyTicketRequest));
    }

    public void scheduleByStation(ObjectOutputStream toClient, ScheduleRequestInfo scheduleRequest) throws IOException {
        toClient.writeObject(trainService.scheduleByStation(scheduleRequest));

    }

    public void findTrain(ObjectOutputStream toClient, FindTrainRequestInfo findRequest) throws IOException {
        toClient.writeObject(trainService.findTrain(findRequest));

    }

    public void getAllStation(ObjectOutputStream toClient) throws IOException {
        toClient.writeObject(stationService.getAllStation());
    }

    public void getAllRoutes(ObjectOutputStream toClient) throws IOException {
        toClient.writeObject(routeService.getAllRoutes());
    }
}
