package ru.tsystems.karpova.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Ticket {
    private int id;

    @Column(name = "id")
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double price;

    @Column(name = "price")
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

        Ticket ticket = (Ticket) o;

        if (id != ticket.id) return false;
        if (Double.compare(ticket.price, price) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    private Passenger passengerByIdPassenger;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_passenger", referencedColumnName = "id", nullable = false)
    public Passenger getPassengerByIdPassenger() {
        return passengerByIdPassenger;
    }

    public void setPassengerByIdPassenger(Passenger passengerByIdPassenger) {
        this.passengerByIdPassenger = passengerByIdPassenger;
    }

    private Station stationByStationFrom;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "station_from", referencedColumnName = "id", nullable = false)
    public Station getStationByStationFrom() {
        return stationByStationFrom;
    }

    public void setStationByStationFrom(Station stationByStationFrom) {
        this.stationByStationFrom = stationByStationFrom;
    }

    private Station stationByStationTo;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "station_to", referencedColumnName = "id", nullable = false)
    public Station getStationByStationTo() {
        return stationByStationTo;
    }

    public void setStationByStationTo(Station stationByStationTo) {
        this.stationByStationTo = stationByStationTo;
    }

    private Train trainByIdTrain;

    @ManyToOne
    @javax.persistence.JoinColumn(name = "id_train", referencedColumnName = "id", nullable = false)
    public Train getTrainByIdTrain() {
        return trainByIdTrain;
    }

    public void setTrainByIdTrain(Train trainByIdTrain) {
        this.trainByIdTrain = trainByIdTrain;
    }
}
