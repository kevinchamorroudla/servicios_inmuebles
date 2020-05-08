package com.kch.checkDone.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Cacheable
public class Paises extends PanacheEntity {
    public String nombre;
    public String codigo;
    public String estado;
    public LocalDate fechaCreacion;
    public LocalDate fechaModificacion;
}
