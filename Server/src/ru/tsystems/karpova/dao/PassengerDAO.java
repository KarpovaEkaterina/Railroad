package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Passenger;
import ru.tsystems.karpova.entities.Station;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class PassengerDAO {

    private static Logger log = Logger.getLogger(PassengerDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static boolean isAlreadyExistPassenger(Passenger passenger) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("select count(*)\n" +
                "from Passenger passenger \n" +
                "where passenger.firstname = ?\n" +
                "and passenger.lastname = ?\n" +
                "and passenger.birthday = ?")
                .setParameter(1, passenger.getFirstname())
                .setParameter(2, passenger.getLastname())
                .setParameter(3, passenger.getBirthday())
                .getResultList();
        return (Long) results.get(0) != 0;
    }

    public static boolean saveNewPassenger(Passenger passenger) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(passenger);

            trx.commit();
            log.debug("Saving passenger");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save passenger", e);
            trx.rollback();
            return false;
        }
    }


    public static Passenger loadPassenger(String firstname, String lastname, Date birthday) {
        EntityManager em = emf.createEntityManager();
        List results = em.createQuery("from Passenger where firstname = ? and lastname = ? and birthday = ?")
                .setParameter(1, firstname)
                .setParameter(2, lastname)
                .setParameter(3, birthday)
                .getResultList();
        return results == null || results.isEmpty() ? null : (Passenger) results.get(0);
    }
}