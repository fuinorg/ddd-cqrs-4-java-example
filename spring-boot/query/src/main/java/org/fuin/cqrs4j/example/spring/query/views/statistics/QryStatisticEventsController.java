package org.fuin.cqrs4j.example.spring.query.views.statistics;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller providing the event-type based statistic ({@link StatisticViewEvents}).
 */
@RestController
@RequestMapping("/statistics-events")
@Transactional(readOnly = true)
public class QryStatisticEventsController {

    private static final Logger LOG = LoggerFactory.getLogger(QryStatisticEventsController.class);

    @Autowired
    EntityManager em;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Statistic> getAll() {
        final List<Statistic> statistics = em.createNamedQuery(StatisticEventsEntity.FIND_ALL, Statistic.class).getResultList();
        LOG.info("getAll() = {}", statistics.size());
        return statistics;
    }

    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByName(@PathVariable(value = "name") String name) {
        if (!EntityType.isValid(name)) {
            return ResponseEntity.badRequest().body("Invalid entity type name");
        }
        final StatisticEventsEntity entity = em.find(StatisticEventsEntity.class, name);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(entity.toDto());
    }

}
