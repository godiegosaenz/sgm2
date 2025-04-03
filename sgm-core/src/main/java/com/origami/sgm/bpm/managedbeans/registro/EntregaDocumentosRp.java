/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegpEntradaSalidaDocs;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class EntregaDocumentosRp extends ClienteTramite implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected CatEnte solicitante = new CatEnte();
    protected Boolean entregado;
    protected HistoricoTramites ht;
    protected RegpLiquidacionDerechosAranceles liq;
    protected String observacion;
    protected Integer iniciarTramite;
    protected HashMap<String, Object> params;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null) {
                this.setTaskId(session.getTaskID());
                iniciarTramite = (Integer) this.getVariable(session.getTaskID(), "iniciarTramite");
                entregado = (Boolean) this.getVariable(session.getTaskID(), "entregado");
                Long id = (Long) this.getVariable(session.getTaskID(), "tramite");
                ht = reg.getHistoricoTramiteById(id);
                liq = ht.getRegpLiquidacionDerechosAranceles();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(EntregaDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarSolicitante() {
        if (solicitante.getCiRuc() != null) {
            if (solicitante.getCiRuc().length() == 10 && vcu.isCIValida(solicitante.getCiRuc())) {
                String temp = solicitante.getCiRuc();
                solicitante = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{solicitante.getCiRuc()});
                if (solicitante == null) {
                    solicitante = new CatEnte();
                    this.inicializarVariables();
                    persona.setCiRuc(temp);
                    JsfUti.messageInfo(null, Messages.enteNoExiste, "");
                    JsfUti.update("formSolicitante");
                    JsfUti.executeJS("PF('dlgSolInf').show();");
                }
            } else {
                JsfUti.messageError(null, Messages.cedulaCIinvalida, "");
            }
        } else {
            JsfUti.messageWarning(null, Messages.faltanCampos, "");
        }
    }

    public void showDlgEditarSol() {
        if (solicitante.getId() != null) {
            persona = solicitante;
            JsfUti.update("formSolicitante");
            JsfUti.executeJS("PF('dlgSolInf').show();");
        } else {
            JsfUti.messageError(null, Messages.noAsignadoPersona, "");
        }
    }

    public void guardarSolicitante() {
        Boolean flag;
        if (solicitante.getId() == null) {
            flag = guardarCliente();
        } else {
            flag = editarCliente();
        }
        if (flag) {
            solicitante = persona;
            JsfUti.executeJS("PF('dlgSolInf').hide();");
            JsfUti.update("mainForm:accPanelRP:panelSolicitante");
        }
    }

    public void completarTarea() {
        try {
            if (solicitante.getId() != null) {
                String comentario;
                if (entregado) {
                    comentario = "Persona que re-ingresa: " + solicitante.getNombres() + " " + solicitante.getApellidos()
                            + ", usuario que recibe: " + session.getName_user();
                } else {
                    comentario = "Persona que retira: " + solicitante.getNombres() + " " + solicitante.getApellidos()
                            + ", usuario que entrega: " + session.getName_user();
                }

                this.guardarObservacion(comentario);
                this.reasignarTarea(this.getTaskId(), session.getName_user());
                params = new HashMap<>();
                if (iniciarTramite != null) {
                    if (iniciarTramite == 4) {
                        Integer temp = reg.getCantInscripcionesByTramite(ht.getId());
                        if (temp > 0) {
                            params.put("catastrar", 3);
                        } else {
                            this.actualizarHistoricoTramite();
                        }
                    }
                }
                params.put("entregado", !entregado);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                JsfUti.messageInfo(null, Messages.noAsignadoPersona, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(EntregaDocumentosRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarObservacion(String obv) {
        try {
            Observaciones ob = new Observaciones();
            ob.setObservacion(obv);
            ob.setEstado(true);
            ob.setFecCre(new Date());
            ob.setIdTramite(ht);
            ob.setTarea(this.getTaskDataByTaskID().getName());
            ob.setUserCre(session.getName_user());
            acl.persist(ob);

            RegpEntradaSalidaDocs doc = new RegpEntradaSalidaDocs();
            doc.setCliente(solicitante);
            doc.setFecha(new Date());
            doc.setLiquidacion(liq);
            doc.setUsuario(session.getName_user());
            doc.setObservacion(this.getTaskDataByTaskID().getName() + " - " + obv);
            acl.persist(doc);
        } catch (Exception e) {
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarHistoricoTramite() {
        try {
            //if (iniciarTramite == 4) {
            ht.setEstado("Finalizado");
            acl.persist(ht);
            //}
        } catch (Exception e) {
            Logger.getLogger(RevisionRegistralRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RegpLiquidacionDerechosAranceles getLiq() {
        return liq;
    }

    public void setLiq(RegpLiquidacionDerechosAranceles liq) {
        this.liq = liq;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
