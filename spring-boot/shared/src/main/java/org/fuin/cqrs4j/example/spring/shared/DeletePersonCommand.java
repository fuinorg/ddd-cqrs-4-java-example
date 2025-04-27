package org.fuin.cqrs4j.example.spring.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.jackson.AbstractAggregateCommand;
import org.fuin.ddd4j.core.AggregateVersion;
import org.fuin.ddd4j.core.DomainEventExpectedEntityIdPath;
import org.fuin.ddd4j.core.EventType;
import org.fuin.esc.api.SerializedDataType;
import org.fuin.objects4j.common.Immutable;

/**
 * A new person should be deleted from the system.
 */
@Immutable
@DomainEventExpectedEntityIdPath(PersonId.class)
public final class DeletePersonCommand extends AbstractAggregateCommand<PersonId, PersonId> {

    private static final long serialVersionUID = 1000L;

    /**
     * Never changing unique event type name.
     */
    public static final EventType TYPE = new EventType("DeletePersonCommand");

    /**
     * Unique name used for marshalling/unmarshalling the event.
     */
    public static final SerializedDataType SER_TYPE = new SerializedDataType(DeletePersonCommand.TYPE.asBaseType());

    @NotNull
    @JsonProperty("name")
    private PersonName name;

    /**
     * Protected default constructor for deserialization.
     */
    protected DeletePersonCommand() {
        super();
    }

    @Override
    @JsonIgnore
    public final EventType getEventType() {
        return DeletePersonCommand.TYPE;
    }

    /**
     * Returns: Name of a person.
     *
     * @return Current value.
     */
    @NotNull
    @JsonIgnore
    public final PersonName getName() {
        return name;
    }

    @Override
    public final String toString() {
        return "Delete person '" + name + "' with identifier '" + getAggregateRootId() + "'";
    }

    /**
     * Builds an instance of the outer class.
     */
    public static final class Builder extends AbstractAggregateCommand.Builder<PersonId, PersonId, DeletePersonCommand, Builder> {

        private DeletePersonCommand delegate;

        public Builder() {
            super(new DeletePersonCommand());
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

        public DeletePersonCommand build() {
            ensureBuildableAbstractAggregateCommand();
            ensureNotNull("name", delegate.name);

            final DeletePersonCommand result = delegate;
            delegate = new DeletePersonCommand();
            resetAbstractAggregateCommand(delegate);
            return result;
        }

        public Builder version(int version) {
            aggregateVersion(AggregateVersion.valueOf(version));
            return this;
        }

    }

}