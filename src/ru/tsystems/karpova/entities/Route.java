package ru.tsystems.karpova.entities;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Ekaterina
 * Date: 18.09.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Route {
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

    private double coeffDate;

    @javax.persistence.Column(name = "coeff_date")
    @Basic
    public double getCoeffDate() {
        return coeffDate;
    }

    public void setCoeffDate(double coeffDate) {
        this.coeffDate = coeffDate;
    }

    private double coeffSeats;

    @javax.persistence.Column(name = "coeff_seats")
    @Basic
    public double getCoeffSeats() {
        return coeffSeats;
    }

    public void setCoeffSeats(double coeffSeats) {
        this.coeffSeats = coeffSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (Double.compare(route.coeffDate, coeffDate) != 0) return false;
        if (Double.compare(route.coeffSeats, coeffSeats) != 0) return false;
        if (id != route.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(coeffDate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(coeffSeats);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    private Collection<StationRoute> stationRoutesById;

    @OneToMany(mappedBy = "routeByIdRoute")
    public Collection<StationRoute> getStationRoutesById() {
        return stationRoutesById;
    }

    public void setStationRoutesById(Collection<StationRoute> stationRoutesById) {
        this.stationRoutesById = stationRoutesById;
    }

    private Collection<Train> trainsById;

    @OneToMany(mappedBy = "routeByIdRoute")
    public Collection<Train> getTrainsById() {
        return trainsById;
    }

    public void setTrainsById(Collection<Train> trainsById) {
        this.trainsById = trainsById;
    }
}
