package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.requests.AddRouteRequestInfo;
import ru.tsystems.karpova.respond.AddRouteRespondInfo;
import ru.tsystems.karpova.respond.GetAllRoutesRespondInfo;
import ru.tsystems.karpova.dao.RouteDAO;
import ru.tsystems.karpova.dao.ScheduleDAO;
import ru.tsystems.karpova.dao.StationDAO;
import ru.tsystems.karpova.dao.WayDAO;
import ru.tsystems.karpova.entities.Route;
import ru.tsystems.karpova.entities.Schedule;
import ru.tsystems.karpova.entities.Station;
import ru.tsystems.karpova.entities.Way;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteService {

    private static Logger log = Logger.getLogger(RouteService.class);

    private RouteDAO routeDAO;
    private StationDAO stationDAO;
    private WayDAO wayDAO;
    private ScheduleDAO scheduleDAO;

    public RouteService() {
        routeDAO = new RouteDAO();
        stationDAO = new StationDAO();
        wayDAO = new WayDAO();
        scheduleDAO = new ScheduleDAO();
    }

    public AddRouteRespondInfo addRoute(AddRouteRequestInfo addRouteRequest) throws IOException {
        log.debug("Start method \"addRoute\"");
        List<String> stationsForNewRoute = addRouteRequest.getStationsForNewRoute();
        Map<String, Object[]> newWay = addRouteRequest.getNewWay();
        String delimiter = addRouteRequest.getDelimiter();
        for (String stations : newWay.keySet()) {
            String stationAName = stations.split(delimiter)[0];
            String stationBName = stations.split(delimiter)[1];
            Station stationA = stationDAO.loadStationByName(stationAName);
            Station stationB = stationDAO.loadStationByName(stationBName);
            Way way = new Way();
            way.setStationByIdStation1(stationA);
            way.setStationByIdStation2(stationB);
            way.setTime((Timestamp) newWay.get(stations)[0]);
            way.setPrice((Double) newWay.get(stations)[1]);
            if (!wayDAO.saveWay(way)) {
                AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
                log.debug("Send AddRouteRespondInfo to client with SERVER_ERROR_STATUS");
                return respond;
            }
        }
        Route route = new Route();
        route.setName(addRouteRequest.getRouteName());
        if (!routeDAO.saveRoute(route)) {
            AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
            log.debug("Send AddRouteRespondInfo to client with SERVER_ERROR_STATUS");
            return respond;
        }
        List<Schedule> schedules = new ArrayList<Schedule>();
        route = routeDAO.loadRoute(route.getName());
        for (int i = 1; i < stationsForNewRoute.size(); i++) {
            Way way = wayDAO.loadWayByStations(stationsForNewRoute.get(i - 1), stationsForNewRoute.get(i));
            Schedule schedule = new Schedule();
            schedule.setRouteByIdRoute(route);
            schedule.setWayByIdWay(way);
            schedule.setSeqNumber(i);
            schedules.add(schedule);
            if (!scheduleDAO.saveSchedule(schedule)) {
                AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.SERVER_ERROR_STATUS);
                log.debug("Send AddRouteRespondInfo to client with SERVER_ERROR_STATUS");
                return respond;
            }
        }
        AddRouteRespondInfo respond = new AddRouteRespondInfo(AddRouteRespondInfo.OK_STATUS);
        log.debug("Send AddRouteRespondInfo to client with OK_STATUS");
        return respond;
    }

    public GetAllRoutesRespondInfo getAllRoutes() throws IOException {
        log.debug("Start method \"getAllRoutes\"");
        List<Route> allRoutesList = routeDAO.getAllRoutes();
        List<String> allRoutes = new ArrayList<String>();
        for (Route route : allRoutesList) {
            allRoutes.add(route.getName());
        }
        GetAllRoutesRespondInfo respond = new GetAllRoutesRespondInfo(allRoutes);
        log.debug("Send GetAllRoutesRespondInfo to client");
        return respond;
    }
}
