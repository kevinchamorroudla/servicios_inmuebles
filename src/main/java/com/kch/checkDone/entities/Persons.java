package com.kch.checkDone.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

@Entity
@Cacheable
public class Persons extends PanacheEntity {

    public String name;
    public String surname;
    public String email;

    public Persons() {
    }

}
