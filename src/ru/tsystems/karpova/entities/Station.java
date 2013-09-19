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
public class Station {
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

    private String name;

    @javax.persistence.Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (id != station.id) return false;
        if (name != null ? !name.equals(station.name) : station.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    private Collection<StationRoute> stationRoutesById;

    @OneToMany(mappedBy = "stationByIdStation")
    public Collection<StationRoute> getStationRoutesById() {
        return stationRoutesById;
    }

    public void setStationRoutesById(Collection<StationRoute> stationRoutesById) {
        this.stationRoutesById = stationRoutesById;
    }

    private Collection<Way> waysById;

    @OneToMany(mappedBy = "stationByIdStation1")
    public Collection<Way> getWaysById() {
        return waysById;
    }

    public void setWaysById(Collection<Way> waysById) {
        this.waysById = waysById;
    }

    private Collection<Way> waysById_0;

    @OneToMany(mappedBy = "stationByIdStation2")
    public Collection<Way> getWaysById_0() {
        return waysById_0;
    }

    public void setWaysById_0(Collection<Way> waysById_0) {
        this.waysById_0 = waysById_0;
    }
}
