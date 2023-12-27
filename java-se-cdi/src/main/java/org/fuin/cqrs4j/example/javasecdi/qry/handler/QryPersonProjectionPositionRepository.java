package org.fuin.cqrs4j.example.javasecdi.qry.handler;

import jakarta.validation.constraints.NotNull;

import org.apache.deltaspike.data.api.AbstractEntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.fuin.cqrs4j.ProjectionService;
import org.fuin.esc.api.StreamId;
import org.fuin.objects4j.common.Contract;

/**
 * Repository that contains the position of the stream.
 */
@Repository(forEntity = QryPersonProjectionPosition.class)
public abstract class QryPersonProjectionPositionRepository extends AbstractEntityRepository<QryPersonProjectionPosition, String>
        implements ProjectionService {

    @Override
    public void resetProjectionPosition(@NotNull final StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryPersonProjectionPosition pos = findBy(streamId.asString());
        if (pos != null) {
            pos.setNextPosition(0L);
        }
    }

    @Override
    public Long readProjectionPosition(@NotNull StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryPersonProjectionPosition pos = findBy(streamId.asString());
        if (pos == null) {
            return 0L;
        }
        return pos.getNextPos();
    }

    @Override
    public void updateProjectionPosition(@NotNull StreamId streamId, @NotNull Long nextEventNumber) {
        Contract.requireArgNotNull("streamId", streamId);
        Contract.requireArgNotNull("nextEventNumber", nextEventNumber);
        QryPersonProjectionPosition pos = findBy(streamId.asString());
        if (pos == null) {
            pos = new QryPersonProjectionPosition(streamId, nextEventNumber);
        } else {
            pos.setNextPosition(nextEventNumber);
        }
        save(pos);
    }

}
