/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.financiero;

import com.origami.sgm.entities.FnRemisionLiquidacion;
import com.origami.sgm.entities.FnRemisionSolicitud;
import com.origami.sgm.entities.RenLiquidacion;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author origami-idea
 */
@Local
public interface RemisionInteresServices {
    
    public FnRemisionSolicitud saveFnRemisionSolicitudProceso(List<RenLiquidacion> liquidaciones);
    
    public FnRemisionSolicitud aprobarSolicitud(FnRemisionSolicitud fnRemisionSolicitud);
    
    public Boolean aplicaRemision(RenLiquidacion liquidacion);
    
    public Boolean aplicaRemisionPagoCuotaInicial(List<RenLiquidacion> liquidaciones);
    
    public FnRemisionSolicitud cancelarSolicitud(FnRemisionSolicitud fnRemisionSolicitud);
    
    
    
}
