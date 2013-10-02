package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Schedule;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

public class ScheduleDAO extends BasicDAO{

    private static Logger log = Logger.getLogger(ScheduleDAO.class);

    public boolean saveSchedule(Schedule schedule) {
        log.debug("Start saveSchedule");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {
            trx.begin();

            em.persist(schedule);

            trx.commit();
            log.debug("Saving schedule");
            return true;
        } catch (RollbackException e) {
            log.error("Can't save schedule", e);
            trx.rollback();
            return false;
        }
    }
}
