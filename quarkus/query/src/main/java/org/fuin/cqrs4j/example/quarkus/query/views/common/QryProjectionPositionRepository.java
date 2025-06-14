package org.fuin.cqrs4j.example.quarkus.query.views.common;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.esc.ProjectionService;
import org.fuin.esc.api.StreamId;
import org.fuin.objects4j.common.Contract;

/**
 * Repository that contains the position of the stream.
 */
@ApplicationScoped
public class QryProjectionPositionRepository implements ProjectionService {

    private static final String ARG_STREAM_ID = "streamId";
    @Inject
    EntityManager em;

    @Override
    public void resetProjectionPosition(@NotNull final StreamId streamId) {
        Contract.requireArgNotNull(ARG_STREAM_ID, streamId);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos != null) {
            pos.setNextPosition(0L);
        }
    }

    @Override
    public Long readProjectionPosition(@NotNull StreamId streamId) {
        Contract.requireArgNotNull(ARG_STREAM_ID, streamId);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos == null) {
            return 0L;
        }
        return pos.getNextPos();
    }

    @Override
    public void updateProjectionPosition(@NotNull StreamId streamId, @NotNull Long nextEventNumber) {
        Contract.requireArgNotNull(ARG_STREAM_ID, streamId);
        Contract.requireArgNotNull("nextEventNumber", nextEventNumber);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos == null) {
            em.persist(new QryProjectionPosition(streamId, nextEventNumber));
        } else {
            pos.setNextPosition(nextEventNumber);
            em.merge(pos);
        }
    }

}
