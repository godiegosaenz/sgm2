/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.events;

/**
 * Evento se ejucuta para la valoracion del predio, exclusivo para ibarra
 *
 * @author Angel Navarro
 */
public class ValorarPredioPost {

    private String claveCat;
    private String predialant;
    private Integer tipoProcedimiento;

    /**
     *
     * @param claveCat
     * @param predialant
     * @param tipoProcedimiento <ol>
     * <li> Para indentificar si se va a ejecutar el procedimiento de valor 
     * terreno.</li> 
     * <li> Para indentificar si se va ejecutar el procedimiento de valor de
     * construccion.</li></ol>
     */
    public ValorarPredioPost(String claveCat, String predialant, Integer tipoProcedimiento) {
        this.claveCat = claveCat;
        this.predialant = predialant;
        this.tipoProcedimiento = tipoProcedimiento;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
    }

    public String getPredialant() {
        return predialant;
    }

    public void setPredialant(String predialant) {
        this.predialant = predialant;
    }

    /**
     * 1.- Para indentificar si se va a ejecutar el procedimiento de valor
     * terreno. 2.- Para indentificar si se va ejecutar el procedimiento de
     * valor de construccion.
     *
     * @return
     */
    public Integer getTipoProcedimiento() {
        return tipoProcedimiento;
    }

    public void setTipoProcedimiento(Integer tipoProcedimiento) {
        this.tipoProcedimiento = tipoProcedimiento;
    }

}
