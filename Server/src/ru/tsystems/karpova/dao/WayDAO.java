package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Way;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.util.List;

public class WayDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(WayDAO.class);

    public List<Object[]> getAllWays() {
        log.debug("Start getAllWays select");
        List<Object[]> results = em.createQuery("select stationA.name, stationB.name, way.time, way.price\n" +
                "from Way way\n" +
                "inner join way.stationByIdStation1 stationA\n" +
                "inner join way.stationByIdStation2 stationB")
                .getResultList();
        return results;
    }

    public boolean saveWay(Way way) {
        log.debug("Start saveWay");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(way);

            trx.commit();
            log.debug("Saving way");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save way", e);
            trx.rollback();
            return false;
        }
    }

    public Way loadWayByStations(String stationA, String stationB){
        log.debug("Start loadWayByStations select");
        List results = em.createQuery("select way\n" +
                "from Way way\n" +
                "inner join way.stationByIdStation1 stationA\n" +
                "inner join way.stationByIdStation2 stationB\n" +
                "where stationA.name = ?\n" +
                "and stationB.name = ?")
                .setParameter(1,stationA)
                .setParameter(2, stationB)
                .getResultList();
        return results == null || results.isEmpty() ? null : (Way) results.get(0);
    }
}
