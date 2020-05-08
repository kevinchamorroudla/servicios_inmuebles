package com.kch.checkDone.services;

import com.kch.checkDone.entities.Persons;
import io.quarkus.panache.common.Sort;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Path("persons")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class PersonsServices {

    @GET
    public List<Persons> get() {
        return Persons.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Persons getSingle(@PathParam Long id) {
        Persons entity = Persons.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Person with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Persons person) {
        if (person.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        person.persist();
        return Response.ok(person).status(201).build();
    }

    @POST
    @Path("update/{id}")
    @Transactional
    public Persons update(@PathParam Long id, Persons person) {
        if (person.name == null) {
            throw new WebApplicationException("Person Name was not set on request.", 422);
        }

        Persons entity = Persons.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Person with id of " + id + " does not exist.", 404);
        }

        entity.name = person.name;
        entity.surname = person.surname;
        entity.email = person.email;

        return entity;
    }

    @POST
    @Path("delete/{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
        Persons entity = Persons.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Persons with id of " + id + " does not exist.", 404);
        }
        entity.delete();
        return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }
            return Response.status(code)
                    .entity(Json.createObjectBuilder().add("error", exception.getMessage()).add("code", code).build())
                    .build();
        }
    }

}
