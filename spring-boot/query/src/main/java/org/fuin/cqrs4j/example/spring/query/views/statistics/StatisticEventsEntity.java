package org.fuin.cqrs4j.example.spring.query.views.statistics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Read model of the {@link StatisticViewEvents} projection: the number of live instances per entity type,
 * counted from the concrete domain events (created / deleted).
 */
@Entity
@Table(name = "STATISTIC_EVENTS")
@NamedQuery(name = StatisticEventsEntity.FIND_ALL,
        query = "SELECT new org.fuin.cqrs4j.example.spring.query.views.statistics.Statistic(s.type, s.count) FROM StatisticEventsEntity s")
public class StatisticEventsEntity {

    public static final String FIND_ALL = "StatisticEventsEntity.findAll";

    @Id
    @Column(name = "TYPE", nullable = false, length = EntityType.MAX_LENGTH, updatable = false)
    @NotNull
    private String type;

    @Column(name = "COUNT", updatable = true)
    private int count;

    /**
     * JPA default constructor.
     */
    protected StatisticEventsEntity() {
    }

    /**
     * Constructor with a given type that sets the number of instances to one.
     *
     * @param type Unique type ID.
     */
    public StatisticEventsEntity(@NotNull EntityType type) {
        this.type = Objects.requireNonNull(type, "type==null").name();
        this.count = 1;
    }

    /**
     * Returns the statistic as "DTO" instance.
     *
     * @return Statistic record.
     */
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
        StatisticEventsEntity that = (StatisticEventsEntity) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return "StatisticEventsEntity{" +
                "type='" + type + '\'' +
                ", count=" + count +
                '}';
    }
}
