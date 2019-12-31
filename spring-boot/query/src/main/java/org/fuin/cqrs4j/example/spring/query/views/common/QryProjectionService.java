package org.fuin.cqrs4j.example.spring.query.views.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.ProjectionService;
import org.fuin.esc.api.StreamId;
import org.fuin.objects4j.common.Contract;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to read and persist the next position of a stream to read. 
 */
@Repository
public class QryProjectionService implements ProjectionService {

    @PersistenceContext
    private EntityManager em;
	
    @Override
    public void resetProjectionPosition(@NotNull final StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos != null) {
            pos.setNextPosition(0L);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long readProjectionPosition(@NotNull StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos == null) {
            return 0L;
        }
        return pos.getNextPos();
    }

    @Override
    public void updateProjectionPosition(@NotNull StreamId streamId, @NotNull Long nextEventNumber) {
        Contract.requireArgNotNull("streamId", streamId);
        Contract.requireArgNotNull("nextEventNumber", nextEventNumber);
        QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos == null) {
            pos = new QryProjectionPosition(streamId, nextEventNumber);
			em.persist(pos);
        } else {
            pos.setNextPosition(nextEventNumber);
        }
    }

}
