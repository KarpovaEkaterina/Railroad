package ru.tsystems.karpova.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Route {
    private int id;

    @Column(name = "id")
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;

    @Column(name = "name")
    @Basic
    public String getName() {
        return name.toLowerCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    private double coeffDate;

    @Column(name = "coeff_date")
    @Basic
    public double getCoeffDate() {
        return coeffDate;
    }

    public void setCoeffDate(double coeffDate) {
        this.coeffDate = coeffDate;
    }

    private double coeffSeats;

    @Column(name = "coeff_seats")
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
        if (name != null ? !name.equals(route.name) : route.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(coeffDate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(coeffSeats);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    private Collection<Schedule> schedulesById;

    @OneToMany(mappedBy = "routeByIdRoute")
    public Collection<Schedule> getSchedulesById() {
        return schedulesById;
    }

    public void setSchedulesById(Collection<Schedule> schedulesById) {
        this.schedulesById = schedulesById;
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
