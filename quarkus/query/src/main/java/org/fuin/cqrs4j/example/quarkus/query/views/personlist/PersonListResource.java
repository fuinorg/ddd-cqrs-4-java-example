package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.fuin.objects4j.vo.UUIDStrValidator;

import java.util.List;

/**
 * REST resource providing the persons.
 */
@Path("/persons")
public class PersonListResource {

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        final List<PersonListEntry> persons = em.createNamedQuery(PersonListEntry.FIND_ALL, PersonListEntry.class).getResultList();
        return Response.ok(persons).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        if (!UUIDStrValidator.isValid(id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Person UUID").build();
        }
        final PersonListEntry person = em.find(PersonListEntry.class, id);
        if (person == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(person).build();
    }

}
