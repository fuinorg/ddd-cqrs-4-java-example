package org.fuin.cqrs4j.example.javasecdi.qry.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.apache.deltaspike.jpa.api.transaction.TransactionScoped;

@ApplicationScoped
public class QryEntityManagerProducer {

    private EntityManagerFactory emf;

    @Produces
    @TransactionScoped
    public EntityManager create() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("QueryDb");
        }
        return emf.createEntityManager();
    }

    public void close(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}