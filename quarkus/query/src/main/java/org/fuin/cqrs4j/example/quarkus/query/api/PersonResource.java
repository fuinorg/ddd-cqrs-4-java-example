package org.fuin.cqrs4j.example.quarkus.query.api;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fuin.cqrs4j.example.quarkus.query.domain.QryPerson;
import org.fuin.objects4j.vo.UUIDStrValidator;

@Path("/persons")
public class PersonResource {

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        final List<QryPerson> persons = em.createNamedQuery(QryPerson.FIND_ALL, QryPerson.class).getResultList();
        return Response.ok(persons).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String id) {
        if (!UUIDStrValidator.isValid(id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Person UUID").build();
        }
        final QryPerson person = em.find(QryPerson.class, id);
        if (person == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(person).build();
    }

}
