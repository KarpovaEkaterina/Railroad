package ru.tsystems.karpova.entities;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Ekaterina
 * Date: 18.09.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
@javax.persistence.Table(name = "station_route", schema = "", catalog = "karpova")
@Entity
public class StationRoute {
    private int id;

    @javax.persistence.Column(name = "id")
    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Timestamp arrivalTime;

    @javax.persistence.Column(name = "arrival_time")
    @Basic
    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    private int seqNumber;

    @javax.persistence.Column(name = "seq_number")
    @Basic
    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationRoute that = (StationRoute) o;

        if (id != that.id) return false;
        if (seqNumber != that.seqNumber) return false;
        if (arrivalTime != null ? !arrivalTime.equals(that.arrivalTime) : that.arrivalTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (arrivalTime != null ? arrivalTime.hashCode() : 0);
        result = 31 * result + seqNumber;
        return result;
    }

    private Route routeByIdRoute;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_route", referencedColumnName = "id", nullable = false)
    public Route getRouteByIdRoute() {
        return routeByIdRoute;
    }

    public void setRouteByIdRoute(Route routeByIdRoute) {
        this.routeByIdRoute = routeByIdRoute;
    }

    private Station stationByIdStation;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_station", referencedColumnName = "id", nullable = false)
    public Station getStationByIdStation() {
        return stationByIdStation;
    }

    public void setStationByIdStation(Station stationByIdStation) {
        this.stationByIdStation = stationByIdStation;
    }
}
