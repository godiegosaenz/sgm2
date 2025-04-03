/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.sgm.entities.RenActivosLocalComercial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Angel Navarro
 * @Date 30/04/2016
 */
public class AlcabalasFm implements Serializable {

    private static final Logger LOG = Logger.getLogger(AlcabalasFm.class.getName());
    
    private final BigDecimal porcentajeImpuesto = BigDecimal.valueOf(1.5).divide(BigDecimal.valueOf(1000));

    public BigDecimal sumarActivos(RenActivosLocalComercial local){
        if(local.getActivoTotal() == null && local.getActivoContingente() == null){
            return BigDecimal.ZERO;
        }
        return local.getActivoTotal().add(local.getActivoContingente());
    }
    
    public BigDecimal sumarPasivos(RenActivosLocalComercial local){
        if(local.getPasivoTotal() == null && local.getPasivoContingente() == null){
            return BigDecimal.ZERO;
        }
        return local.getPasivoTotal().add(local.getPasivoContingente());
    }
    
    public BigDecimal diferenciaActivosVsPasivos(RenActivosLocalComercial local){
        return sumarActivos(local).subtract(sumarPasivos(local));
    }
    
    public BigDecimal baseImponible(RenActivosLocalComercial local){
        try {
            if (local.getPorcentajeIngreso() == null) {
                return BigDecimal.ZERO;
            }
            return diferenciaActivosVsPasivos(local).multiply(local.getPorcentajeIngreso().divide(BigDecimal.valueOf(100)));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Base Imponible", e);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal impuesto(RenActivosLocalComercial local){
        try {
            return baseImponible(local).multiply(porcentajeImpuesto);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Impuesto", e);
        }
        return BigDecimal.ZERO;
    }
}
