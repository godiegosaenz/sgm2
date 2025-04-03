/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.predio.models;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CarlosLoorVargas
 */
public class FichaModel implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    private Short noEdificacion;
    private Boolean marcado = false;
    private BigDecimal porcentaje = BigDecimal.ZERO;
    private Long idProp;

    public FichaModel(Long id, Boolean marcado, Short noEdificacion, BigDecimal porcentaje) {
        this.idProp = id;
        this.marcado = marcado;
        this.noEdificacion = noEdificacion;
        this.porcentaje = porcentaje;
    }

    public String getHeaderText() {
        if (noEdificacion == new Short("1")) {
            return "EP-" + noEdificacion;
        } else {
            return "AN-" + noEdificacion;
        }
    }

    public Short getNoEdificacion() {
        return noEdificacion;
    }

    public void setNoEdificacion(Short noEdificacion) {
        this.noEdificacion = noEdificacion;
    }

    public Boolean getMarcado() {
        return marcado;
    }

    public void setMarcado(Boolean marcado) {
        this.marcado = marcado;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Long getIdProp() {
        return idProp;
    }

    public void setIdProp(Long idProp) {
        this.idProp = idProp;
    }

    @Expose(serialize = true, deserialize = true)
    private Long codClasesEspec;
    @Expose(serialize = true, deserialize = true)
    private Long codClase;
    @Expose(serialize = true, deserialize = true)
    private Long codTipoEspec;
    @Expose(serialize = true, deserialize = true)
    private int cont;
    @Expose(serialize = true, deserialize = true)
    private String clase;
    @Expose(serialize = true, deserialize = true)
    private String color;
    @Expose(serialize = true, deserialize = true)
    private String especificacion;
    @Expose(serialize = true, deserialize = true)
    private List<FichaDetModel> detalle;

    public boolean validar() {
        BigDecimal acum = BigDecimal.ZERO;
        for (FichaDetModel fichaDetModel : this.detalle) {
            acum = acum.add(fichaDetModel.getPorcentaje());
        }
        if (acum.compareTo(new BigDecimal(100.00)) > 0) {
            return true;
        }
        return false;
    }

    public FichaModel() {
        detalle = new ArrayList<>();
    }

    public void agregarDato(FichaDetModel d) {
        detalle.add(d);
    }

    public Long getCodClasesEspec() {
        return codClasesEspec;
    }

    public void setCodClasesEspec(Long codClasesEspec) {
        this.codClasesEspec = codClasesEspec;
    }

    public Long getCodClase() {
        return codClase;
    }

    public void setCodClase(Long codClase) {
        this.codClase = codClase;
    }

    public Long getCodTipoEspec() {
        return codTipoEspec;
    }

    public void setCodTipoEspec(Long codTipoEspec) {
        this.codTipoEspec = codTipoEspec;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEspecificacion() {
        return especificacion;
    }

    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }

    public List<FichaDetModel> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<FichaDetModel> detalle) {
        this.detalle = detalle;
    }

    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    @Override
    public String toString() {
        return "FichaModel{" + "noEdificacion=" + noEdificacion + ", marcado=" + marcado + ", porcentaje=" + porcentaje + ", idProp=" + idProp + ", clase=" + clase + ", color=" + color + ", especificacion=" + especificacion + ", detalle=" + detalle + '}';
    }

}
