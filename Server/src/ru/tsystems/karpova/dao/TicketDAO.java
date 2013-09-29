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

    public static boolean buyTicket(Train train, Passenger passenger, Station stationFrom, Station stationTo) {
        Ticket ticket = new Ticket();
        ticket.setTrainByIdTrain(train);
        ticket.setPassengerByIdPassenger(passenger);
        ticket.setStationByStationFrom(stationFrom);
        ticket.setStationByStationTo(stationTo);
        ticket.setPrice(100);

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
