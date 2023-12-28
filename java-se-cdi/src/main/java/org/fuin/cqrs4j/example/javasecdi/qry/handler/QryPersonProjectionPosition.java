package org.fuin.cqrs4j.example.javasecdi.qry.handler;

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
@Table(name = "QRY_PERSON_PROJECTION_POS")
public class QryPersonProjectionPosition {

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
    protected QryPersonProjectionPosition() {
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
    public QryPersonProjectionPosition(@NotNull final StreamId streamId, @NotNull final Long nextPos) {
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
        return "QryPersonHandlerPosition [streamId=" + streamId + ", nextPos=" + nextPos + "]";
    }

}
