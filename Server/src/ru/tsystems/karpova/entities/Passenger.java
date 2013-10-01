package ru.tsystems.karpova.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
public class Passenger {
    private int id;
    private String firstname;
    private String lastname;
    private Timestamp birthday;
    private Collection<Ticket> ticketsById;

    public Passenger() {
    }

    public Passenger(String firstname, String lastname, Timestamp birthday) {
        this.firstname = firstname.toLowerCase();
        this.lastname = lastname.toLowerCase();
        this.birthday = birthday;
    }

    @Column(name = "id")
    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "firstname")
    @Basic
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname.toLowerCase();
    }

    @Column(name = "lastname")
    @Basic
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname.toLowerCase();
    }

    @Column(name = "birthday")
    @Basic
    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Passenger passenger = (Passenger) o;

        if (id != passenger.id) return false;
        if (birthday != null ? !birthday.equals(passenger.birthday) : passenger.birthday != null) return false;
        if (firstname != null ? !firstname.equals(passenger.firstname) : passenger.firstname != null) return false;
        if (lastname != null ? !lastname.equals(passenger.lastname) : passenger.lastname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "passengerByIdPassenger")
    public Collection<Ticket> getTicketsById() {
        return ticketsById;
    }

    public void setTicketsById(Collection<Ticket> ticketsById) {
        this.ticketsById = ticketsById;
    }
}
