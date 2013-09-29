package ru.tsystems.karpova.dao;

import ru.tsystems.karpova.entities.Route;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RouteDAO {

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static List<Route> getAllRoutes() {
        EntityManager em = emf.createEntityManager();
        List<Route> results = em.createQuery("from Route").getResultList();
        return results;
    }

}
