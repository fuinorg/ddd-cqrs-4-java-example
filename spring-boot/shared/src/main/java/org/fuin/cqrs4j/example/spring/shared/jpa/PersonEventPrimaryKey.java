package org.fuin.cqrs4j.example.spring.shared.jpa;

import jakarta.validation.constraints.NotNull;
import org.fuin.objects4j.common.Contract;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link PersonEvent} (person id + event number within the stream).
 */
public class PersonEventPrimaryKey implements Serializable {

    private static final long serialVersionUID = 1000L;

    private String personId;

    private Long eventNumber;

    /**
     * Default constructor for JPA. <b><i>CAUTION:</i> DO NOT USE IN APPLICATION CODE.</b>
     */
    public PersonEventPrimaryKey() {
        super();
    }

    /**
     * Constructor with all required data.
     *
     * @param personId    Unique person identifier.
     * @param eventNumber Number of the event within the stream.
     */
    public PersonEventPrimaryKey(@NotNull final String personId, @NotNull final Long eventNumber) {
        super();
        Contract.requireArgNotNull("personId", personId);
        Contract.requireArgNotNull("eventNumber", eventNumber);
        this.personId = personId;
        this.eventNumber = eventNumber;
    }

    /**
     * Returns the unique person identifier.
     *
     * @return Person identifier.
     */
    @NotNull
    public final String getPersonId() {
        return personId;
    }

    /**
     * Returns the number of the event within the stream.
     *
     * @return Order of the event in the stream.
     */
    @NotNull
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
        final PersonEventPrimaryKey other = (PersonEventPrimaryKey) obj;
        return Objects.equals(personId, other.personId) && Objects.equals(eventNumber, other.eventNumber);
    }

    @Override
    public final String toString() {
        return personId + "-" + eventNumber;
    }

}
