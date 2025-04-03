/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.fusion;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.FusionPrediosServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class FusionFP extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    private HistoricoTramites tramite;
    private HistoricoTramiteDet det;
    @javax.inject.Inject
    private FusionPrediosServices fpredios;
    private Observaciones obs;
    private String mensaje;
    private Boolean mostrarBoton = false;
    private Boolean mostarMensaja = true;
    private String pagado = null;
    private CatPredio predio;
    private CatPredioS4 cps4;
    private CatPredioS4 cps6;
    private HashMap<String, Object> params;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void load() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                obs = new Observaciones();
                params = new HashMap<>();
                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if (tramite.getTipoTramite().getId().intValue() == 7 || tramite.getTipoTramite().getId().intValue() == 8 || tramite.getTipoTramite().getId().intValue() == 9 || tramite.getTipoTramite().getId().intValue() == 36
                        || tramite.getTipoTramite().getId().intValue() == 15 || tramite.getTipoTramite().getId().intValue() == 17 || (tramite.getId().compareTo(new Long("1450")) == 1)) {
                    if (tramite.getNumLiquidacion() != null) {
                        pagado = permisoServices.consultaPagoLiquidacion(tramite.getTipoTramite().getId().intValue(), tramite);
                    }
                    if (pagado == null) {
                        mostarMensaja = false;
                    } else {
                        if ("P".equalsIgnoreCase(pagado)) {
                            mostrarBoton = true;
                            mensaje = "El Usuario ya Cancelo la Tasa";
                        } else {
                            mostrarBoton = false;
                            mensaje = "Hasta el Momento el Usuario aun NO CANCELA su Tasa de Liquidación.";
                        }
                    }
                } else {
                    mensaje = "El Usuario ya Cancelo la Tasa";
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FusionFP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

    public void seleccionar(CatPredio p) {
        if (p != null) {
            predio = p;
            if (p.getCatPredioS4() == null) {
                predio.setCatPredioS4(new CatPredioS4());
                predio.getCatPredioS4().setPredio(predio);
            }
            if (p.getCatPredioS6() == null) {
                predio.setCatPredioS6(new CatPredioS6());
                predio.getCatPredioS6().setPredio(predio);
            }
            Faces.executeJS("PF('dlgPredio').show()");
        } else {
            Faces.messageWarning(pagado, "Advertencia", "Debe seleccionar el predio respectivo");
        }

    }

    public void completarTarea() {
        if (obs.getObservacion() != null) {
            try {
                obs.setEstado(true);
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setTarea(this.getTaskDataByTaskID().getName());
                obs.setUserCre(sess.getName_user());
//                if (predio.getCatPredioS4().getId() == null || predio.getCatPredioS6().getId() == null) {
//                    Faces.messageWarning(null, "Advertencia", "Para realizar la fusión, los predios deben tener los datos de Ocupación del solar y linderos respectivos");
//                    return;
//                }
                for (HistoricoTramiteDet dt : tramite.getHistoricoTramiteDetCollection()) {
                    if (!dt.getPredio().getId().equals(predio.getId())) {
                        dt.getPredio().setEstado("I");
                    }
                }
                if (this.getFiles() != null && !this.getFiles().isEmpty()) {
                    params.put("tdocs", true);
                    params.put("listaArchivos", this.getFiles());
                    params.put("listaArchivosFinal", new ArrayList<>());
                } else {
                    params.put("tdocs", false);
                }
                serv.persist(obs);
                if (fpredios.fusionarPredios((List<HistoricoTramiteDet>) tramite.getHistoricoTramiteDetCollection())) {
                    params.put("aprobado", true);
                    tramite.setEstado("finalizado");
                    serv.persist(tramite);
                    this.completeTask(this.sess.getTaskID(), params);
                    this.continuar();
                } else {
                    Faces.messageWarning(pagado, "Advertencia", "No se pudo realizar la fusion de predios, verfique que el predio tenga los datos ingresados correctamente");
                }
                /*obs = permisoServices.guardarObservacion(obs);
                 if (obs != null) {
                 if (tramite.getId() == 2L) {
                 tramite.setEstado("Finalizado");
                 permisoServices.actualizarHistoricoTramites(tramite);
                 }
                 params.put("idProcess", sess.getTaskID());
                 this.completeTask(this.sess.getTaskID(), params);
                 this.continuar();
                 }*/
            } catch (Exception e) {
                Logger.getLogger(FusionFP.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JsfUti.messageInfo(null, "Debe Ingresar la Observación de la Tarea.", "");
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(Boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }

    public Boolean getMostarMensaja() {
        return mostarMensaja;
    }

    public void setMostarMensaja(Boolean mostarMensaja) {
        this.mostarMensaja = mostarMensaja;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public HistoricoTramiteDet getDet() {
        return det;
    }

    public void setDet(HistoricoTramiteDet det) {
        this.det = det;
    }

}
