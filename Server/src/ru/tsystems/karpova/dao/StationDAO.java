package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Station;

import javax.persistence.*;
import java.util.List;

public class StationDAO {

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List<Station> getAllStation() {
        EntityManager em = emf.createEntityManager();
        List<Station> results = em.createQuery("from Station").getResultList();
        return results;
    }
}
