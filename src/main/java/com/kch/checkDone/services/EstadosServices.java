package com.kch.checkDone.services;

import com.kch.checkDone.entities.Estados;
import io.netty.util.internal.StringUtil;
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

@Path("estados")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class EstadosServices {
    @GET
    public List<Estados> get() {
        return Estados.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Estados getSingle(@PathParam Long id) {
        Estados entity = Estados.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El estado con el " + id + " no existe", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Estados estado) {
        if (estado.id != null) {
            throw new WebApplicationException("El id no puede ser distinto de nulo", 422);
        }
        if (StringUtil.isNullOrEmpty(estado.nombre) || StringUtil.isNullOrEmpty(estado.codigo) || estado.idPais == null) {
            throw new WebApplicationException("Nombre o codigo del estado no pueden ser vacios", 422);
        }

        estado.persist();
        return Response.ok(estado).status(201).build();
    }

    @POST
    @Path("update/{id}")
    @Transactional
    public Estados update(@PathParam Long id, Estados estado) {

        if (StringUtil.isNullOrEmpty(estado.nombre) || StringUtil.isNullOrEmpty(estado.codigo) || estado.idPais == null) {
            throw new WebApplicationException("Nombre o codigo del estado no pueden ser vacios", 422);
        }

        Estados entity = Estados.findById(id);

        if (entity == null) {
            throw new WebApplicationException("El estado con el " + id + " no existe", 404);
        }

        entity.nombre = estado.nombre;
        entity.codigo = estado.codigo;

        return entity;
    }

    @POST
    @Path("delete/{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
        Estados entity = Estados.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El estado con el " + id + " no existe", 404);
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
