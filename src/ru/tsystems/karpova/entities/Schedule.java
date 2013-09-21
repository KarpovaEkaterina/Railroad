package ru.tsystems.karpova.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Ekaterina
 * Date: 21.09.13
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Schedule {
    private int id;

    @javax.persistence.Column(name = "id")
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

        Schedule schedule = (Schedule) o;

        if (id != schedule.id) return false;
        if (seqNumber != schedule.seqNumber) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
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

    private Way wayByIdWay;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_way", referencedColumnName = "id", nullable = false)
    public Way getWayByIdWay() {
        return wayByIdWay;
    }

    public void setWayByIdWay(Way wayByIdWay) {
        this.wayByIdWay = wayByIdWay;
    }
}
