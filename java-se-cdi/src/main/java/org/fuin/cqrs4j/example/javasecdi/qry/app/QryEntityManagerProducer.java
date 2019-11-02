package org.fuin.cqrs4j.example.javasecdi.qry.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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