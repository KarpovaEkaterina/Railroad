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
@Entity
public class Way {
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

    private Timestamp time;

    @javax.persistence.Column(name = "time")
    @Basic
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    private double price;

    @javax.persistence.Column(name = "price")
    @Basic
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Way way = (Way) o;

        if (id != way.id) return false;
        if (Double.compare(way.price, price) != 0) return false;
        if (time != null ? !time.equals(way.time) : way.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    private Station stationByIdStation1;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_station1", referencedColumnName = "id", nullable = false)
    public Station getStationByIdStation1() {
        return stationByIdStation1;
    }

    public void setStationByIdStation1(Station stationByIdStation1) {
        this.stationByIdStation1 = stationByIdStation1;
    }

    private Station stationByIdStation2;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_station2", referencedColumnName = "id", nullable = false)
    public Station getStationByIdStation2() {
        return stationByIdStation2;
    }

    public void setStationByIdStation2(Station stationByIdStation2) {
        this.stationByIdStation2 = stationByIdStation2;
    }
}
