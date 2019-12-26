package org.fuin.cqrs4j.example.javasecdi.qry.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.objects4j.common.Contract;

/**
 * Represents a person that will be stored in the database.
 */
@Entity
@Table(name = "QRY_PERSON")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "qry-person")
public class QryPerson {

    @Id
    @Column(name = "ID", nullable = false, length = 36, updatable = false)
    @NotNull
    @XmlElement(name = "id")
    private String id;

    @Column(name = "NAME", nullable = false, length = PersonName.MAX_LENGTH, updatable = true)
    @NotNull
    @XmlElement(name = "name")
    private String name;

    /**
     * JAX-B constructor.
     */
    protected QryPerson() {
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
    public QryPerson(@NotNull final PersonId id, @NotNull final PersonName name) {
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
        return "QryPerson [id=" + id + ", name=" + name + "]";
    }

}
