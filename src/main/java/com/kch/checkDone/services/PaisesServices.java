package com.kch.checkDone.services;

import com.kch.checkDone.entities.Paises;
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

@Path("paises")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class PaisesServices {

    @GET
    public List<Paises> get() {
        return Paises.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Paises getSingle(@PathParam Long id) {
        Paises entity = Paises.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El pais con el " + id + " no existe", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Paises pais) {
        if (pais.id != null) {
            throw new WebApplicationException("El id no puede ser distinto de nulo", 422);
        }

        pais.persist();
        return Response.ok(pais).status(201).build();
    }

    @POST
    @Path("update/{id}")
    @Transactional
    public Paises update(@PathParam Long id, Paises pais) {

        if (StringUtil.isNullOrEmpty(pais.nombre) || StringUtil.isNullOrEmpty(pais.codigo)) {
            throw new WebApplicationException("Nombre o codigo del Pais no pueden ser vacios", 422);
        }

        Paises entity = Paises.findById(id);

        if (entity == null) {
            throw new WebApplicationException("El pais con el " + id + " no existe", 404);
        }

        entity.nombre = pais.nombre;
        entity.codigo = pais.codigo;

        return entity;
    }

    @POST
    @Path("delete/{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
        Paises entity = Paises.findById(id);
        if (entity == null) {
            throw new WebApplicationException("El pais con el " + id + " no existe", 404);
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
