package org.fuin.cqrs4j.example.spring.query.views.statistic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents a statistic record that will be stored in the database.
 */
@Entity
@Table(name = "SPRING_STATISTIC")
@NamedQuery(name = StatisticEntity.FIND_ALL,
        query = "SELECT new org.fuin.cqrs4j.example.spring.query.views.statistic.Statistic(s.type, s.count) FROM StatisticEntity s")
public class StatisticEntity {

    public static final String FIND_ALL = "StatisticEntity.findAll";

    @Id
    @Column(name = "TYPE", nullable = false, length = EntityType.MAX_LENGTH, updatable = false)
    @NotNull
    private String type;

    @Column(name = "COUNT", updatable = true)
    private int count;

    /**
     * JPA default constructor.
     */
    protected StatisticEntity() {
    }

    /**
     * Constrcutor with a given type that sets the number of instances to one.
     *
     * @param type Unique type ID.
     */
    public StatisticEntity(@NotNull EntityType type) {
        this.type = Objects.requireNonNull(type, "type==null").name();
        this.count = 1;
    }

    public Statistic toDto() {
        return new Statistic(type, count);
    }

    /**
     * Increases the number of entries for the type by one.
     */
    public void inc() {
        this.count++;
    }

    /**
     * Decreases the number of entries for the type by one.
     */
    public void dec() {
        this.count--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticEntity that = (StatisticEntity) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "StatisticEntity{" +
                "type='" + type + '\'' +
                ", count=" + count +
                '}';
    }
}