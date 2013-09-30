package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Station;

import javax.persistence.*;
import java.util.List;

public class StationDAO {

    private static Logger log = Logger.getLogger(StationDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List<Station> getAllStation() {
        EntityManager em = emf.createEntityManager();
        log.debug("Start getAllStation select");
        List<Station> results = em.createQuery("from Station").getResultList();
        return results;
    }


    public static Station loadStationByName(String name) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start loadStationByName select");
        List results = em.createQuery("from Station where name=?").setParameter(1, name).getResultList();
        return results == null || results.isEmpty() ? null : (Station) results.get(0);
    }

    public static boolean saveNewStation(Station station) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(station);

            trx.commit();
            log.debug("Saving station");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save station", e);
            trx.rollback();
            return false;
        }
    }
}
