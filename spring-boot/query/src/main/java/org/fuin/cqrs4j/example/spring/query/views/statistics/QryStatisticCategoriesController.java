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
 * REST controller providing the category based statistic ({@link StatisticViewCategories}).
 */
@RestController
@RequestMapping("/statistics-categories")
@Transactional(readOnly = true)
public class QryStatisticCategoriesController {

    private static final Logger LOG = LoggerFactory.getLogger(QryStatisticCategoriesController.class);

    @Autowired
    EntityManager em;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoryStatistic> getAll() {
        final List<CategoryStatistic> statistics = em.createNamedQuery(StatisticCategoriesEntity.FIND_ALL, CategoryStatistic.class).getResultList();
        LOG.info("getAll() = {}", statistics.size());
        return statistics;
    }

    @GetMapping(path = "/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByCategory(@PathVariable(value = "category") String category) {
        if (category == null || category.isEmpty() || category.length() > StatisticCategoriesEntity.MAX_LENGTH) {
            return ResponseEntity.badRequest().body("Invalid category name");
        }
        final StatisticCategoriesEntity entity = em.find(StatisticCategoriesEntity.class, category);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(entity.toDto());
    }

}
