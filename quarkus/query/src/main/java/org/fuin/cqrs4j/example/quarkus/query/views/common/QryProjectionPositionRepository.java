/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.quarkus.query.views.common;

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
public class QryProjectionPositionRepository implements ProjectionService {

    @Inject
    EntityManager em;
    
    @Override
    public void resetProjectionPosition(@NotNull final StreamId streamId) {
        Contract.requireArgNotNull("streamId", streamId);
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos != null) {
            pos.setNextPosition(0L);
        }
    }

    @Override
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
        final QryProjectionPosition pos = em.find(QryProjectionPosition.class, streamId.asString());
        if (pos == null) {
            em.persist(new QryProjectionPosition(streamId, nextEventNumber));
        } else {
            pos.setNextPosition(nextEventNumber);
            em.merge(pos);
        }
    }

}
