package org.fuin.cqrs4j.example.spring.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.fuin.cqrs4j.example.spring.shared.jpa.PersonStream;
import org.fuin.esc.api.EventStore;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.StreamId;
import org.fuin.esc.jpa.JpaEventStore;
import org.fuin.esc.jpa.JpaIdStreamFactory;
import org.fuin.esc.jpa.JpaProjectionAdminEventStore;
import org.fuin.esc.jpa.JpaStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Backend-portability profile: swaps the event store from the KurrentDB gRPC backend
 * ({@code SharedConfig#getESGrpcEventStore}) to the relational {@code esc-jpa} store, with <b>no domain or
 * projection code change</b>. Activate with {@code SPRING_PROFILES_ACTIVE=esc-jpa} (or
 * {@code @ActiveProfiles("esc-jpa")}).
 * <p>
 * Only the two event-store SPI beans are replaced here; everything else (serializers, object mapper, the
 * cqrs-4-java projection/view engine) is shared. The command side keeps appending to per-aggregate
 * {@code PERSON-<id>} streams ({@link PersonStream} / {@code person_events}); the query side's
 * {@code SpringViewManager} runs in poll mode and catches up over the JPA store, whose
 * {@link ProjectionAdminEventStore} models a projection as a type filter over the global event log.
 * <p>
 * The stores are given a Spring shared {@link EntityManager} ({@link SharedEntityManagerCreator}) so every
 * append/read joins the caller's current transaction, exactly like Spring's {@code @PersistenceContext}.
 */
@Configuration
@Profile("esc-jpa")
public class EscJpaConfig {

    /**
     * Relational event store bean (replaces the gRPC store). The command aggregate repository and the query
     * projection engine both depend only on the {@link EventStore} SPI, so swapping the implementation needs
     * no other change.
     *
     * @param emf      Entity manager factory (JPA auto-configured against the shared database).
     * @param registry Serializer/deserializer registry from {@link SharedConfig}.
     *
     * @return JPA-backed event store.
     */
    /** Mutating {@link EventStore} operations that must run inside a transaction on the relational backend. */
    private static final Set<String> EVENT_STORE_TX_METHODS = Set.of("appendToStream", "deleteStream");

    /** Mutating {@link ProjectionAdminEventStore} operations that must run inside a transaction. */
    private static final Set<String> PROJECTION_ADMIN_TX_METHODS =
            Set.of("createProjection", "enableProjection", "disableProjection", "deleteProjection");

    @Bean
    public EventStore eventStore(final EntityManagerFactory emf, final SerDeserializerRegistry registry,
                                 final PlatformTransactionManager transactionManager) {
        final EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        final JpaEventStore delegate = new JpaEventStore(em, personStreamFactory(), registry, registry);
        delegate.open();
        return transactional(EventStore.class, delegate, EVENT_STORE_TX_METHODS,
                new TransactionTemplate(transactionManager));
    }

    /**
     * Projection admin bean required by the cqrs-4-java query starter's view manager. On the relational
     * backend a projection is a persisted type filter over the global event log. The view manager reaches
     * this outside a transaction, so the mutating operations open their own (see {@link #transactional}).
     *
     * @param emf                Entity manager factory.
     * @param transactionManager Transaction manager.
     *
     * @return JPA-backed projection admin store.
     */
    @Bean
    public ProjectionAdminEventStore projectionAdminEventStore(final EntityManagerFactory emf,
                                                               final PlatformTransactionManager transactionManager) {
        final EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(emf);
        final JpaProjectionAdminEventStore delegate = new JpaProjectionAdminEventStore(em);
        delegate.open();
        return transactional(ProjectionAdminEventStore.class, delegate, PROJECTION_ADMIN_TX_METHODS,
                new TransactionTemplate(transactionManager));
    }

    /**
     * Wraps a store so that its mutating operations run in their own transaction. The JPA backend requires an
     * active transaction to write, but the callers (command controllers, the projection view manager) are not
     * transactional here (the default KurrentDB backend has no transaction manager). Reads pass straight
     * through - a Spring shared entity manager serves queries without a surrounding transaction.
     *
     * @param iface     Interface to proxy.
     * @param delegate  Real store.
     * @param txMethods Names of the methods to run transactionally.
     * @param tx        Transaction template.
     * @param <T>       Store type.
     *
     * @return Transaction-aware proxy.
     */
    @SuppressWarnings("unchecked")
    private static <T> T transactional(final Class<T> iface, final T delegate, final Set<String> txMethods,
                                       final TransactionTemplate tx) {
        return (T) Proxy.newProxyInstance(EscJpaConfig.class.getClassLoader(), new Class<?>[]{iface},
                (proxy, method, args) -> {
                    if (txMethods.contains(method.getName())) {
                        return tx.execute(status -> invoke(delegate, method, args));
                    }
                    return invoke(delegate, method, args);
                });
    }

    private static Object invoke(final Object delegate, final Method method, final Object[] args) {
        try {
            return method.invoke(delegate, args);
        } catch (final InvocationTargetException ex) {
            final Throwable cause = ex.getTargetException();
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException("Event store call failed: " + method.getName(), cause);
        } catch (final IllegalAccessException ex) {
            throw new IllegalStateException("Cannot access event store method: " + method.getName(), ex);
        }
    }

    /**
     * Maps an aggregate stream id to its relational stream row. Only the {@code Person} aggregate is written
     * in this example, so every stream maps to a {@link PersonStream}.
     *
     * @return Stream factory.
     */
    private static JpaIdStreamFactory personStreamFactory() {
        return new JpaIdStreamFactory() {
            @Override
            public boolean containsType(final StreamId streamId) {
                return true;
            }

            @Override
            public JpaStream createStream(final StreamId streamId) {
                return new PersonStream(streamId.getSingleParamValue());
            }
        };
    }

}
