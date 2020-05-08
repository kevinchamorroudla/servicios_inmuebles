package com.kch.checkDone.services;

import com.kch.checkDone.entities.Ciudades;
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

@Path("Ciudades")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CiudadesServices {
    @GET
    public List<Ciudades> get() {
        return Ciudades.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Ciudades getSingle(@PathParam Long id) {
        Ciudades entity = Ciudades.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El ciudad con el " + id + " no existe", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Ciudades ciudad) {
        if (ciudad.id != null) {
            throw new WebApplicationException("El id no puede ser distinto de nulo", 422);
        }
        if (StringUtil.isNullOrEmpty(ciudad.nombre) || StringUtil.isNullOrEmpty(ciudad.codigo) || ciudad.idEstado == null) {
            throw new WebApplicationException("Nombre o codigo del ciudad no pueden ser vacios", 422);
        }

        ciudad.persist();
        return Response.ok(ciudad).status(201).build();
    }

    @POST
    @Path("update/{id}")
    @Transactional
    public Ciudades update(@PathParam Long id, Ciudades ciudad) {

        if (StringUtil.isNullOrEmpty(ciudad.nombre) || StringUtil.isNullOrEmpty(ciudad.codigo) || ciudad.idEstado == null) {
            throw new WebApplicationException("Nombre o codigo del ciudad no pueden ser vacios", 422);
        }

        Ciudades entity = Ciudades.findById(id);

        if (entity == null) {
            throw new WebApplicationException("El ciudad con el " + id + " no existe", 404);
        }

        entity.nombre = ciudad.nombre;
        entity.codigo = ciudad.codigo;

        return entity;
    }

    @POST
    @Path("delete/{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
        Ciudades entity = Ciudades.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El ciudad con el " + id + " no existe", 404);
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
