/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.predio.models;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CarlosLoorVargas
 */
public class FichaEdificacionesModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Expose
    private String nombCabecera = "";
    @Expose
    private List<String> rubros = new ArrayList<>();
    @Expose
    private CatPredioEdificacion edificacion1 = new CatPredioEdificacion();
    @Expose
    private List<CatPredioEdificacion> edificaciones = new ArrayList<>();
    @Expose
    private List<CatPredioEdificacionProp> especificacion1 = new ArrayList<>();
    @Expose
    private String nombreEdificacion = "";
    @Expose
    private String clase = "";
    @Expose
    private int maxTipoClase = 1;
    @Expose
    private int maxEdif = 1;
    

    public FichaEdificacionesModel() {
    }

    public String getNombCabecera() {
        return nombCabecera;
    }

    public void setNombCabecera(String nombCabecera) {
        this.nombCabecera = nombCabecera;
    }

    public List<String> getRubros() {
        return rubros;
    }

    public void setRubros(List<String> rubros) {
        this.rubros = rubros;
    }

    public CatPredioEdificacion getEdificacion1() {
        return edificacion1;
    }

    public void setEdificacion1(CatPredioEdificacion edificacion1) {
        this.edificacion1 = edificacion1;
    }

    public List<CatPredioEdificacion> getEdificaciones() {
        return edificaciones;
    }

    public void setEdificaciones(List<CatPredioEdificacion> edificaciones) {
        this.edificaciones = edificaciones;
    }

    public List<CatPredioEdificacionProp> getEspecificacion1() {
        return especificacion1;
    }

    public void setEspecificacion1(List<CatPredioEdificacionProp> especificacion1) {
        this.especificacion1 = especificacion1;
    }

    public String getNombreEdificacion() {
        return nombreEdificacion;
    }

    public void setNombreEdificacion(String nombreEdificacion) {
        this.nombreEdificacion = nombreEdificacion;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getMaxTipoClase() {
        return maxTipoClase;
    }

    public void setMaxTipoClase(int maxTipoClase) {
        this.maxTipoClase = maxTipoClase;
    }

    public int getMaxEdif() {
        return maxEdif;
    }

    public void setMaxEdif(int maxEdif) {
        this.maxEdif = maxEdif;
    }
    
    

}
