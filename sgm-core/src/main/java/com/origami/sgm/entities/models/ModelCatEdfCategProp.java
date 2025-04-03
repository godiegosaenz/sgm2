/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.CatEdfProp;
import java.util.Collection;

/**
 *
 * @author Angel Navarro
 */
public class ModelCatEdfCategProp {
    
    @Expose
    private Long id;
    @Expose
    private String nombre;
    @Expose
    private short guiOrden;
    @Expose
    private String tipoEstruc;
    @Expose
    private Collection<CatEdfProp> catEdfPropCollection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public short getGuiOrden() {
        return guiOrden;
    }

    public void setGuiOrden(short guiOrden) {
        this.guiOrden = guiOrden;
    }

    public String getTipoEstruc() {
        return tipoEstruc;
    }

    public void setTipoEstruc(String tipoEstruc) {
        this.tipoEstruc = tipoEstruc;
    }

    public Collection<CatEdfProp> getCatEdfPropCollection() {
        return catEdfPropCollection;
    }

    public void setCatEdfPropCollection(Collection<CatEdfProp> catEdfPropCollection) {
        this.catEdfPropCollection = catEdfPropCollection;
    }
    
    
}
