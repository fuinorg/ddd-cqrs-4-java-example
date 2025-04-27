package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.objects4j.common.Contract;

/**
 * Represents a person that will be stored in the database.
 */
@Entity
@NamedQuery(name = PersonListEntry.FIND_ALL, query = "SELECT p FROM PersonListEntry p")
@Table(name = "SPRING_PERSON_LIST")
public class PersonListEntry {

    public static final String FIND_ALL = "PersonListEntry.findAll";

    @Id
    @Column(name = "ID", nullable = false, length = 36, updatable = false)
    @NotNull
    private String id;

    @Column(name = "NAME", nullable = false, length = PersonName.MAX_LENGTH, updatable = true)
    @NotNull
    private String name;

    /**
     * Deserialization constructor.
     */
    protected PersonListEntry() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param id
     *            Unique aggregate identifier.
     * @param name
     *            Name of the created person
     */
    public PersonListEntry(@NotNull final PersonId id, @NotNull final PersonName name) {
        super();
        Contract.requireArgNotNull("id", id);
        Contract.requireArgNotNull("name", name);
        this.id = id.asString();
        this.name = name.asString();
    }

    /**
     * Returns the unique person identifier.
     * 
     * @return Aggregate ID.
     */
    @NotNull
    public PersonId getId() {
        return PersonId.valueOf(id);
    }

    /**
     * Returns the name of the person to create.
     * 
     * @return the Person name
     */
    @NotNull
    public PersonName getName() {
        return new PersonName(name);
    }

    /**
     * Sets the name of the person.
     * 
     * @param name
     *            Name to set.
     */
    public void setName(@NotNull final PersonName name) {
        Contract.requireArgNotNull("name", name);
        this.name = name.asString();
    }

    @Override
    public String toString() {
        return "PersonListEntry [id=" + id + ", name=" + name + "]";
    }

}
