package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.User;

import javax.persistence.*;
import java.util.List;

public class UserDAO {

    private static Logger log = Logger.getLogger(UserDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static boolean saveUser(User user) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start saveUser");
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(user);

            trx.commit();
            log.debug("Saving user");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save user", e);
            trx.rollback();
            return false;
        }
    }

    public static User loadUserByLogin(String login) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start loadUserByLogin select");
        List results = em.createQuery("from User where login=?").setParameter(1, login).getResultList();
        return results == null || results.isEmpty() ? null : (User) results.get(0);
    }
}
