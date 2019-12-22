package org.fuin.cqrs4j.example.quarkus.query.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.ProjectionService;
import org.fuin.esc.api.StreamId;
import org.fuin.objects4j.common.Contract;

/**
 * Repository that contains the position of the stream.
 */
@ApplicationScoped
public class QryPersonProjectionPositionRepository implements ProjectionService {

    @Inject
    EntityManager em;
    
    @Override
    public void resetProjectionPosition(@NotNull final StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryPersonProjectionPosition pos = em.find(QryPersonProjectionPosition.class, streamId.asString());
        if (pos != null) {
            pos.setNextPosition(0L);
        }
    }

    @Override
    public Long readProjectionPosition(@NotNull StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryPersonProjectionPosition pos = em.find(QryPersonProjectionPosition.class, streamId.asString());
        if (pos == null) {
            return 0L;
        }
        return pos.getNextPos();
    }

    @Override
    public void updateProjectionPosition(@NotNull StreamId streamId, @NotNull Long nextEventNumber) {
        Contract.requireArgNotNull("streamId", streamId);
        Contract.requireArgNotNull("nextEventNumber", nextEventNumber);
        final QryPersonProjectionPosition pos = em.find(QryPersonProjectionPosition.class, streamId.asString());
        if (pos == null) {
            em.persist(new QryPersonProjectionPosition(streamId, nextEventNumber));
        } else {
            pos.setNextPosition(nextEventNumber);
            em.merge(pos);
        }
    }

}
