/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.lazymodels.FnSolicitudExoneracionLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.history.HistoricTaskInstance;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @Date 05/08/2016
 */
@Named
@ViewScoped
public class SolicitudesGeneradas implements Serializable {

    private static final Logger LOG = Logger.getLogger(SolicitudesGeneradas.class.getName());

    private FnSolicitudExoneracionLazy lazy;
    private FnSolicitudExoneracion solicitudExoneracion;

    @Inject
    private ServletSession ss;

    @Inject
    private UserSession session;
    @Inject
    private BpmManageBeanBase beanBase;
    
    @javax.inject.Inject
    private RentasServices services;

    @PostConstruct
    protected void initView() {
        lazy = new FnSolicitudExoneracionLazy();
    }

    public void detalle(FnSolicitudExoneracion s) {
        try {
            this.solicitudExoneracion = s;
            JsfUti.executeJS("PF('detalle').show()");
            JsfUti.update("detalle");
            JsfUti.update("detalle:tabDetalle");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    /*
    public void imprimir(FnSolicitudExoneracionAutomatica s) {
        try {
            String path = Faces.getRealPath("//");
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
            ss.agregarParametro("LIQUIDACION", null);
            ss.setNombreSubCarpeta("rentas/liquidaciones");
            return;
//            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }*/

    /*
    public Integer anioFin(FnSolicitudExoneracion sa) {
        try {
            if (sa.getFnSolicitudExoneracionAutomaticaCollection() == null) {
                return sa.getAnioFin();
            } else if (sa.getFnSolicitudExoneracionAutomaticaCollection().size() > 0) {
                FnSolicitudExoneracionAutomatica a = Utils.get(sa.getFnSolicitudExoneracionAutomaticaCollection(), 0);
                if (a != null) {
                    return a.getAnio();
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return null;
    }*/
    
    public RenRubrosLiquidacion getRubro(Long idRubro){
        if(idRubro != null){
            return services.getRubroById(idRubro);
        }
        return null;
    }
    
    public List<HistoricTaskInstance> getTaskInstance(){
        if(solicitudExoneracion != null)
            return beanBase.getTaskByProcessInstanceIdMain(solicitudExoneracion.getTramite().getIdProcesoTemp());
        else
            return null;
    }

    public FnSolicitudExoneracionLazy getLazy() {
        return lazy;
    }

    public void setLazy(FnSolicitudExoneracionLazy lazy) {
        this.lazy = lazy;
    }
/*
    public FnSolicitudExoneracionAutomatica getSolicitudAutomatica() {
        return solicitudAutomatica;
    }

    public void setSolicitudAutomatica(FnSolicitudExoneracionAutomatica solicitudAutomatica) {
        this.solicitudAutomatica = solicitudAutomatica;
    }
*/
    public FnSolicitudExoneracion getSolicitudExoneracion() {
        return solicitudExoneracion;
    }

    public void setSolicitudExoneracion(FnSolicitudExoneracion solicitudExoneracion) {
        this.solicitudExoneracion = solicitudExoneracion;
    }

    /**
     * Creates a new instance of LiquidacionesPropiedad
     */
    public SolicitudesGeneradas() {
    }

}
