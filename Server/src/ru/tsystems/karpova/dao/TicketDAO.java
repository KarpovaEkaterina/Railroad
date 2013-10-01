package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Passenger;
import ru.tsystems.karpova.entities.Station;
import ru.tsystems.karpova.entities.Ticket;
import ru.tsystems.karpova.entities.Train;

import javax.persistence.*;

public class TicketDAO {

    private static Logger log = Logger.getLogger(TicketDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static boolean saveTicket(Ticket ticket) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start saveTicket");
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(ticket);

            trx.commit();
            log.debug("Saving ticket");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save user", e);
            trx.rollback();
            return false;
        }
    }
}
