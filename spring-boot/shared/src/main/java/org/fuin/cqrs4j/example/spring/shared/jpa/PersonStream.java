package org.fuin.cqrs4j.example.spring.shared.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.fuin.esc.api.StreamId;
import org.fuin.esc.jpa.JpaEvent;
import org.fuin.esc.jpa.JpaStream;
import org.fuin.esc.jpa.JpaStreamEvent;
import org.fuin.objects4j.common.Contract;

/**
 * Relational stream table for the {@code Person} aggregate. The event-sourced repository appends to a
 * {@code PERSON-<id>} stream; on the JPA backend that maps to one row here per person plus the events in
 * {@link PersonEvent}.
 * <p>
 * The JPA <b>entity name</b> must be {@code PERSONStream}: the {@code esc-jpa} store derives the stream
 * entity name from the aggregate stream id as {@code streamId.getName() + "Stream"}, and the ddd-4-java
 * entity type of {@code PersonId} is the all-uppercase {@code "PERSON"}. The Java class name is kept readable.
 */
@Table(name = "PERSON_STREAMS")
@Entity(name = "PERSONStream")
public class PersonStream extends JpaStream {

    @Id
    @NotNull
    @Column(name = "PERSON_ID", nullable = false, updatable = false, length = 36)
    private String personId;

    /**
     * Protected default constructor for JPA.
     */
    protected PersonStream() {
        super();
    }

    /**
     * Constructor with mandatory data.
     *
     * @param personId Unique person identifier.
     */
    public PersonStream(@NotNull final String personId) {
        super();
        Contract.requireArgNotNull("personId", personId);
        this.personId = personId;
    }

    /**
     * Returns the unique person identifier.
     *
     * @return Person identifier.
     */
    public String getPersonId() {
        return personId;
    }

    @Override
    public final JpaStreamEvent createEvent(final StreamId streamId, final JpaEvent eventEntry) {
        incVersion();
        return new PersonEvent(getPersonId(), getVersion(), eventEntry);
    }

    @Override
    public String toString() {
        return personId;
    }

}
