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
 * REST resource providing the statistics.
 */
@Path("/statistics")
public class QryStatisticResource {

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        final List<Statistic> statistics = em.createNamedQuery(StatisticEntity.FIND_ALL, Statistic.class).getResultList();
        return Response.ok(statistics).build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByName(@PathParam("name") String name) {
        if (!EntityType.isValid(name)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid entity type name").build();
        }
        final StatisticEntity entity = em.find(StatisticEntity.class, name);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity.toDto()).build();
    }

}
