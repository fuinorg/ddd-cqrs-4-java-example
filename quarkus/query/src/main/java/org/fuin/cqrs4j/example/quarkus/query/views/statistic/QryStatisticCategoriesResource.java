package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST resource providing the category based statistic ({@link StatisticViewCategories}).
 */
@Path("/statistics-categories")
public class QryStatisticCategoriesResource {

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        final List<CategoryStatistic> statistics = em.createNamedQuery(StatisticCategoriesEntity.FIND_ALL, CategoryStatistic.class).getResultList();
        return Response.ok(statistics).build();
    }

    @GET
    @Path("{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCategory(@PathParam("category") String category) {
        if (category == null || category.isEmpty() || category.length() > StatisticCategoriesEntity.MAX_LENGTH) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category name").build();
        }
        final StatisticCategoriesEntity entity = em.find(StatisticCategoriesEntity.class, category);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity.toDto()).build();
    }

}
