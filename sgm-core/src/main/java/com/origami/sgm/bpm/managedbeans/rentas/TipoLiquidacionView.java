/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.TipoLiquidacionLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class TipoLiquidacionView implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    @Inject
    private ServletSession ss;

    private RenTipoLiquidacion tipoLiquidacion;
    private TipoLiquidacionLazy tiposLiquidaciones;

    private List<RenRubrosLiquidacion> rubrosList;

    @PostConstruct
    public void initView() {

        try {
            if (uSession.esLogueado()) {
                tiposLiquidaciones = new TipoLiquidacionLazy(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asignarRubros(RenTipoLiquidacion tipoLiquidacion) {
        ss.instanciarParametros();
        ss.agregarParametro("idTipoLiquidacion", tipoLiquidacion.getId());
        JsfUti.redirectFacesNewTab("/rentas/mantenimiento/asignacionrubros.xhtml");

    }

    public void nuevoTipoLiquidacion() {
        tipoLiquidacion = new RenTipoLiquidacion();
        tipoLiquidacion.setEstado(true);
        tipoLiquidacion.setFechaIngreso(new Date());
        tipoLiquidacion.setUsuarioIngreso(uSession.getName_user());
    }

    public void eliminarTipoLiq(RenTipoLiquidacion tipoLiq) {
        if (tipoLiq.getId() != null) {
            tipoLiq.setEstado(false);
            services.update(tipoLiq);
        }
        tiposLiquidaciones = new TipoLiquidacionLazy(2);
    }

    public void guardarNuevoTipoLiq() {
        try {
            if (services.persist(tipoLiquidacion) != null) {
                JsfUti.messageInfo(null, "Info", "Se creó correctamente el tipo de liquidación");
            } else {
                JsfUti.messageError(null, "Error", "Hubo un problema al generar el tipo de liquidación");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardarEdicionTipoLiq() {
        try {
            if (services.update(tipoLiquidacion)) {
                JsfUti.messageInfo(null, "Info", "Se editó correctamente el tipo liquidación");
            } else {
                JsfUti.messageError(null, "Error", "Hubo un problema al editar el tipo liquidación");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTitulo(RenTipoLiquidacion tipoLiq) {
        boolean notEmpty = Utils.isNotEmpty(tipoLiq.getRenLiquidacionCollection());
        boolean eliminado = false;
        if (notEmpty) {
            JsfUti.messageInfo(null, "Info!", "El Titulo no puede ser eliminado porque tiene liquidaciones de este tipo. Solo se procedera a 'Inactivar'");
            tipoLiq.setEstado(false);
            eliminado = services.update(tipoLiq);
        } else {
            eliminado = servicesRentas.eliminarTitulo(tipoLiq);
        }
        if(eliminado){
            JsfUti.messageInfo(null, "Info!", "El Titulo fue eliminado correctamente.");
             tiposLiquidaciones = new TipoLiquidacionLazy(2);
        }else{
            JsfUti.messageInfo(null, "Info!", "Hubo un error al Eliminar Titulo.");
        }
    }

    public Boolean eliminarRubros(Collection rubros) {
        if (Utils.isNotEmpty(rubros)) {
            return services.deleteList((List) rubros);
        }
        return true;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public TipoLiquidacionLazy getTiposLiquidaciones() {
        return tiposLiquidaciones;
    }

    public void setTiposLiquidaciones(TipoLiquidacionLazy tiposLiquidaciones) {
        this.tiposLiquidaciones = tiposLiquidaciones;
    }

    public List<RenRubrosLiquidacion> getRubrosList() {
        return rubrosList;
    }

    public void setRubrosList(List<RenRubrosLiquidacion> rubrosList) {
        this.rubrosList = rubrosList;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

}
