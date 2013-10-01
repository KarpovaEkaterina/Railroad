package ru.tsystems.karpova.dao;

import org.apache.log4j.Logger;
import ru.tsystems.karpova.entities.Schedule;

import javax.persistence.*;

public class ScheduleDAO {

    private static Logger log = Logger.getLogger(ScheduleDAO.class);

    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static boolean saveSchedule(Schedule schedule) {
        EntityManager em = emf.createEntityManager();
        log.debug("Start saveSchedule");
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
