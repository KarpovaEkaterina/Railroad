package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Route;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.util.List;

public class RouteDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(RouteDAO.class);

    public List<Route> getAllRoutes() {
        log.debug("Start getAllRoutes select");
        List<Route> results = em.createQuery("from Route").getResultList();
        return results;
    }

    public Route loadRoute(String route) {
        log.debug("Start loadRoute select");
        List results = em.createQuery("from Route where name = ?")
                .setParameter(1, route)
                .getResultList();
        return results == null || results.isEmpty() ? null : (Route) results.get(0);
    }

    public boolean saveRoute(Route route) {
        log.debug("Start saveRoute");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(route);

            trx.commit();
            log.debug("Saving route");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save route", e);
            trx.rollback();
            return false;
        }
    }
}
