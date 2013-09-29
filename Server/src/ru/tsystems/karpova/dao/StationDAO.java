package ru.tsystems.karpova.dao;

import ru.tsystems.karpova.entities.Station;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class StationDAO {

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List<Station> getAllStation() {
        EntityManager em = emf.createEntityManager();
        List<Station> results = em.createQuery("from Station").getResultList();
        return results;
    }


    public static Station loadStationByName(String name) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("from Station where name=?").setParameter(1, name).getResultList();
        return results == null || results.isEmpty() ? null : (Station) results.get(0);
    }
}
