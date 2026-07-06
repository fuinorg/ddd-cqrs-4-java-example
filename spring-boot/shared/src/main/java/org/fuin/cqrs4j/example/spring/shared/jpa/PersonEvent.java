package org.fuin.cqrs4j.example.spring.shared.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.fuin.esc.jpa.JpaEvent;
import org.fuin.esc.jpa.JpaStreamEvent;
import org.fuin.objects4j.common.Contract;

import java.util.Objects;

/**
 * Join table connecting the {@code Person} stream to its events (one row per event in the stream). The table
 * name and the "person_id" column follow the {@code esc-jpa} native naming derived from the aggregate stream
 * id: {@code camel2Underscore(streamId.getName()) + "_events"} - for the {@code PERSON} entity type that is
 * {@code person_events}. The names are lower-case to match the native query exactly (MariaDB table names are
 * case-sensitive on Linux).
 */
@Table(name = "person_events")
@Entity
@IdClass(PersonEventPrimaryKey.class)
public class PersonEvent extends JpaStreamEvent {

    @Id
    @NotNull
    @Column(name = "person_id")
    private String personId;

    @Id
    @NotNull
    @Column(name = "event_number")
    private Long eventNumber;

    /**
     * Protected default constructor only required for JPA.
     */
    protected PersonEvent() {
        super();
    }

    /**
     * Constructor with all mandatory data.
     *
     * @param personId   Unique person identifier.
     * @param version    Version (event number within the stream).
     * @param eventEntry Event entry to connect.
     */
    public PersonEvent(@NotNull final String personId, @NotNull final Long version, final JpaEvent eventEntry) {
        super(eventEntry);
        Contract.requireArgNotNull("personId", personId);
        Contract.requireArgNotNull("version", version);
        this.personId = personId;
        this.eventNumber = version;
    }

    /**
     * Returns the unique person identifier.
     *
     * @return Person identifier.
     */
    public final String getPersonId() {
        return personId;
    }

    /**
     * Returns the number of the event within the stream.
     *
     * @return Order of the event in the stream.
     */
    public final Long getEventNumber() {
        return eventNumber;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(personId, eventNumber);
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PersonEvent other = (PersonEvent) obj;
        return Objects.equals(personId, other.personId) && Objects.equals(eventNumber, other.eventNumber);
    }

    @Override
    public final String toString() {
        return personId + "-" + eventNumber;
    }

}
