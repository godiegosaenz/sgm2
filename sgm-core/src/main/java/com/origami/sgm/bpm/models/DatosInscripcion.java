/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.RegMovimiento;
import java.io.Serializable;
import java.util.List;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author CarlosLoorVargas
 */

public class DatosInscripcion implements Serializable {

    private RegMovimiento movimiento;
    private String rutaRep;
    private JasperPrint jPrint;
    private List<JasperPrint> jPrints;
    private static final long serialVersionUID = 1L;

    public DatosInscripcion() {
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public String getRutaRep() {
        return rutaRep;
    }

    public void setRutaRep(String rutaRep) {
        this.rutaRep = rutaRep;
    }

    public JasperPrint getjPrint() {
        return jPrint;
    }

    public void setjPrint(JasperPrint jPrint) {
        this.jPrint = jPrint;
    }

    public List<JasperPrint> getjPrints() {
        return jPrints;
    }

    public void setjPrints(List<JasperPrint> jPrints) {
        this.jPrints = jPrints;
    }

}
