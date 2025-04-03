/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.composites.registro;

import com.origami.sgm.bpm.managedbeans.registro.FichaIngresoEditar;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@FacesComponent("ccompRegistro")
public class CCompRegistro extends UINamingContainer{

    enum PropertyKeys {
		modelo, movimiento
	}
    
//    private RegMovimiento movimiento;
//    private ConsultaMovimientoModel modelo;
//    @javax.inject.Inject
//    protected RegistroPropiedadServices reg;

//    @PostConstruct
//    protected void load() {
//
//    }

    public void datosMovFicha(RegMovimiento mov) {
        try {
            System.out.println(mov);
            if (mov != null) {
//                movimiento = mov;
//                modelo = new ConsultaMovimientoModel();
//                modelo = reg.getConsultaMovimiento(mov.getId());
//                if (modelo == null) {
//                    JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
//                }
            }
        } catch (Exception e) {
            Logger.getLogger(CCompRegistro.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.executeJS("PF('dlgMovRegistralSelec').show()");
//        return mov;
    }

    public ConsultaMovimientoModel getModelo() {
        return (ConsultaMovimientoModel) getStateHelper().eval(PropertyKeys.modelo);
//        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        getStateHelper().put(PropertyKeys.modelo, modelo);
//        this.modelo = modelo;
    }

    public RegMovimiento getMovimiento() {
        return (RegMovimiento) getStateHelper().eval(PropertyKeys.movimiento);
//        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        getStateHelper().put(PropertyKeys.movimiento, movimiento);
//        this.movimiento = movimiento;
    }

}
