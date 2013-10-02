package ru.tsystems.karpova.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class BasicDAO {

    protected static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("myapp");

    protected EntityManager em;

    public BasicDAO() {
        em = emf.createEntityManager();
    }
}
