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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.fuin.esc.api.SimpleStreamId;
import org.fuin.esc.api.StreamId;
import org.fuin.objects4j.common.Contract;

/**
 * Stores the next position to read from the projection in the event store.
 */
@Entity
@Table(name = "QUARKUS_QRY_PROJECTION_POS")
public class QryProjectionPosition {

    @Id
    @Column(name = "STREAM_ID", nullable = false, length = 250, updatable = false)
    @NotNull
    private String streamId;

    @Column(name = "NEXT_POS", nullable = false, updatable = true)
    @NotNull
    private Long nextPos;

    /**
     * JPA constructor.
     */
    protected QryProjectionPosition() {
        super();
    }

    /**
     * Constructor with mandatory data.
     * 
     * @param streamId
     *            Unique stream identifier.
     * @param nextPos
     *            Next position from the stream to read.
     */
    public QryProjectionPosition(@NotNull final StreamId streamId, @NotNull final Long nextPos) {
        super();
        Contract.requireArgNotNull("streamId", streamId);
        Contract.requireArgNotNull("nextPos", nextPos);
        this.streamId = streamId.asString();
        this.nextPos = nextPos;
    }

    /**
     * Returns the unique stream identifier.
     * 
     * @return Stream ID.
     */
    @NotNull
    public StreamId getStreamId() {
        return new SimpleStreamId(streamId);
    }

    /**
     * Returns the next position read from the stream.
     * 
     * @return Position to read next time.
     */
    @NotNull
    public Long getNextPos() {
        return nextPos;
    }

    /**
     * Sets the next position read from the stream.
     * 
     * @param nextPos
     *            New position to set.
     */
    public void setNextPosition(@NotNull final Long nextPos) {
        Contract.requireArgNotNull("nextPos", nextPos);
        this.nextPos = nextPos;
    }

    @Override
    public String toString() {
        return "QryProjectionPosition [streamId=" + streamId + ", nextPos=" + nextPos + "]";
    }

}
