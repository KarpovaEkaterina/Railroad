package ru.tsystems.karpova.service;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.requests.AddStationRequestInfo;
import ru.tsystems.karpova.respond.AddStationRespondInfo;
import ru.tsystems.karpova.respond.GetAllStationRespondInfo;
import ru.tsystems.karpova.dao.StationDAO;
import ru.tsystems.karpova.entities.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StationService {

    private static Logger log = Logger.getLogger(StationService.class);

    private StationDAO stationDAO;

    public StationService() {
        this.stationDAO = new StationDAO();
    }

    public AddStationRespondInfo addStation(AddStationRequestInfo addStationRequest) throws IOException {
        log.debug("Start method \"addStation\"");
        Station station = new Station(addStationRequest.getStationName());
        if (!stationDAO.saveStation(station)) {
            AddStationRespondInfo respond = new AddStationRespondInfo(AddStationRespondInfo.SERVER_ERROR_STATUS);
            log.debug("Send AddStationRespondInfo to client with SERVER_ERROR_STATUS");
            return respond;
        } else {
            AddStationRespondInfo respond = new AddStationRespondInfo(AddStationRespondInfo.OK_STATUS);
            log.debug("Send AddStationRespondInfo to client with OK_STATUS");
            return respond;
        }
    }

    public GetAllStationRespondInfo getAllStation() throws IOException {
        log.debug("Start method \"getAllStation\"");
        List<Station> allStationList = stationDAO.getAllStation();
        List<String> allStation = new ArrayList<String>();
        for (Station station : allStationList) {
            allStation.add(station.getName());
        }
        GetAllStationRespondInfo respond = new GetAllStationRespondInfo(allStation);
        log.debug("Send GetAllStationRespondInfo to client");
        return respond;
    }
}
