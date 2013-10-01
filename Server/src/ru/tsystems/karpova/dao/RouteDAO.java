package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Route;

import javax.persistence.*;
import java.util.List;

public class RouteDAO {

    private static Logger log = Logger.getLogger(RouteDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List<Route> getAllRoutes() {
        EntityManager em = emf.createEntityManager();
        log.debug("Start getAllRoutes select");
        List<Route> results = em.createQuery("from Route").getResultList();
        return results;
    }

    public static Route loadRoute(String route) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start loadRoute select");
        List results = em.createQuery("from Route where name = ?")
                .setParameter(1, route)
                .getResultList();
        return results == null || results.isEmpty() ? null : (Route) results.get(0);
    }

    public static boolean saveRoute(Route route) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start saveRoute");
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
