package ru.tsystems.karpova.cosole_ui;

import ru.tsystems.karpova.entities.Station;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class ConsoleUI {
    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        trx.begin();

        Station stationEntity = new Station();
        stationEntity.setName("Nsk");

        em.persist(stationEntity);

        trx.commit();
    }

}
