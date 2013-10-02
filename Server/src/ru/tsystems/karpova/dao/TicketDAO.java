package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Ticket;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

public class TicketDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(TicketDAO.class);

    public boolean saveTicket(Ticket ticket) {
        log.debug("Start saveTicket");
        EntityManager em = emf.createEntityManager();
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
