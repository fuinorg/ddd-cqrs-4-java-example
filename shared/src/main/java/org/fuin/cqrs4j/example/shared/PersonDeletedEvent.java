package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import org.fuin.ddd4j.ddd.AbstractDomainEvent;
import org.fuin.ddd4j.ddd.AggregateVersion;
import org.fuin.ddd4j.ddd.EntityIdPath;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.spi.SerializedDataType;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Immutable;

/**
 * A person was deleted from the system.
 */
@Immutable
public final class PersonDeletedEvent extends AbstractDomainEvent<PersonId> {

    private static final long serialVersionUID = 1000L;

    /**
     * Never changing unique event type name.
     */
    public static final EventType TYPE = new EventType("PersonDeletedEvent");

    /**
     * Unique name used for marshalling/unmarshalling the event.
     */
    public static final SerializedDataType SER_TYPE = new SerializedDataType(PersonDeletedEvent.TYPE.asBaseType());

    @NotNull
    @JsonbProperty("name")
    private PersonName name;

    /**
     * Protected default constructor for deserialization.
     */
    protected PersonDeletedEvent() {
        super();
    }

    @Override
    public final EventType getEventType() {
        return PersonDeletedEvent.TYPE;
    }

    /**
     * Returns: Name of a person.
     *
     * @return Current value.
     */
    @NotNull
    public final PersonName getName() {
        return name;
    }

    @Override
    public final String toString() {
        return "Deleted person '" + name + "' (" + getEntityId() + ") [Event " + getEventId() + "]";
    }

    /**
     * Builds an instance of the outer class.
     */
    public static final class Builder extends AbstractDomainEvent.Builder<PersonId, PersonDeletedEvent, Builder> {

        private PersonDeletedEvent delegate;

        public Builder() {
            super(new PersonDeletedEvent());
            delegate = delegate();
        }

        public Builder id(PersonId personId) {
            entityIdPath(personId);
            return this;
        }

        public Builder name(String name) {
            delegate.name = new PersonName(name);
            return this;
        }

        public Builder name(PersonName name) {
            delegate.name = name;
            return this;
        }

        public Builder version(int version) {
            aggregateVersion(AggregateVersion.valueOf(version));
            return this;
        }

        public PersonDeletedEvent build() {
            ensureBuildableAbstractDomainEvent();
            ensureNotNull("name", delegate.name);

            final PersonDeletedEvent result = delegate;
            delegate = new PersonDeletedEvent();
            resetAbstractDomainEvent(delegate);
            return result;
        }

    }

}
