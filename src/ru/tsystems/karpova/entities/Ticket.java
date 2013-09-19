package ru.tsystems.karpova.entities;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ekaterina
 * Date: 18.09.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Ticket {
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
