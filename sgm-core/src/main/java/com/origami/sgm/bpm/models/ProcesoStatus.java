/*
 *  Origami Solutions
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author fernando
 */
public class ProcesoStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean finalizado = false;
    private List<DetalleProceso> detalleProcesoList = new LinkedList<>();

    public ProcesoStatus() {
    }

    public Boolean getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }

    public List<DetalleProceso> getDetalleProcesoList() {
        return detalleProcesoList;
    }

    public void setDetalleProcesoList(List<DetalleProceso> detalleProcesoList) {
        this.detalleProcesoList = detalleProcesoList;
    }

}
