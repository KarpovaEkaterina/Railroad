package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import java.util.List;

public class UserDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(UserDAO.class);

    public boolean saveUser(User user) {
        log.debug("Start saveUser");
        EntityManager em = emf.createEntityManager();
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

    public User loadUserByLogin(String login) {
        log.debug("Start loadUserByLogin select");
        List results = em.createQuery("from User where login=?").setParameter(1, login).getResultList();
        return results == null || results.isEmpty() ? null : (User) results.get(0);
    }
}
