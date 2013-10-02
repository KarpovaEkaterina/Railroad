package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Station;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.util.List;

public class StationDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(StationDAO.class);

    public List<Station> getAllStation() {
        log.debug("Start getAllStation select");
        List<Station> results = em.createQuery("from Station").getResultList();
        return results;
    }

    public Station loadStationByName(String name) {
        log.debug("Start loadStationByName select");
        List results = em.createQuery("from Station where name=?").setParameter(1, name).getResultList();
        return results == null || results.isEmpty() ? null : (Station) results.get(0);
    }

    public boolean saveStation(Station station) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start saveStation");
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
