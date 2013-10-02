package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.respond.GetAllWaysRespondInfo;
import ru.tsystems.karpova.dao.WayDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WayService {

    private static Logger log = Logger.getLogger(RouteService.class);

    private WayDAO wayDAO;

    public WayService() {
        wayDAO = new WayDAO();
    }

    public GetAllWaysRespondInfo getAllWays() throws IOException {
        log.debug("Start method \"getAllWays\"");
        List<Object[]> allWaysList = wayDAO.getAllWays();
        Map<String, Object[]> allWays = new HashMap<String, Object[]>();
        final String delimiter = "_%DELIM%_";
        for (Object[] way : allWaysList) {
            allWays.put(way[0] + delimiter + way[1], new Object[]{way[2], way[3]});
        }
        GetAllWaysRespondInfo respond = new GetAllWaysRespondInfo(allWays, delimiter);
        log.debug("Send GetAllWaysRespondInfo to client");
        return respond;
    }
}
