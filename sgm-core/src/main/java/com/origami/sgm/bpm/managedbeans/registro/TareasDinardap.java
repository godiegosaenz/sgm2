/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpTareasDinardap;
import com.origami.sgm.entities.RegpTareasDinardapDocs;
import com.origami.sgm.lazymodels.RegpTareasDinardapLazy;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.chemistry.opencmis.client.api.Document;
import util.Archivo;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class TareasDinardap extends BpmManageBeanBaseRoot implements Serializable {

    public static final long serialVersionUID = 1L;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected Boolean showCer = false, showIns = false;
    protected Integer opcion = 0;
    protected String formatoArchivos;
    protected RegpTareasDinardapLazy tareasLazy;
    protected RegpTareasDinardap tareaDinardap = new RegpTareasDinardap();
    protected RegpCertificadosInscripciones tareaRegistro = new RegpCertificadosInscripciones();

    protected List<RegpCertificadosInscripciones> listInsc = new ArrayList<>();
    protected List<RegpCertificadosInscripciones> listCert = new ArrayList<>();
    protected Boolean realizado = false;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            formatoArchivos = SisVars.formatoArchivos;
            tareasLazy = new RegpTareasDinardapLazy(Boolean.TRUE);
        } catch (Exception e) {
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDetalleTarea(RegpTareasDinardap t) {
        tareaDinardap = t;
        JsfUti.update("formDetalleTarea");
        JsfUti.executeJS("PF('dlgDetalle').show();");
    }

    public void showDocsTarea(RegpTareasDinardap t) {
        tareaDinardap = t;
        JsfUti.update("formDocumentos");
        JsfUti.executeJS("PF('dlgDocumentos').show();");
    }

    public void showDlgIngreso() {
        tareaDinardap = new RegpTareasDinardap();
        this.setFiles(new ArrayList<Archivo>());
        JsfUti.update("formInfoTarea");
        JsfUti.executeJS("PF('dlgIngresoTarea').show();");
    }

    public void descargarDocumento(RegpTareasDinardapDocs doc) {
        try {
            if (doc.getDocumento() != null) {
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/DescargarDocsRepositorio?id=" + doc.getDocumento());
            } else {
                JsfUti.messageError(null, "Error, no se encuentra el documento.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgTareas(RegpTareasDinardap t) {
        try {
            tareaDinardap = t;
            listInsc = reg.getListaTareasDinardap(t.getId(), "DI"); // DI : DINARDAP INSCRIPCION
            listCert = reg.getListaTareasDinardap(t.getId(), "DW"); // DC : DINARDAP WORD CERTIFICADO
            List<RegpCertificadosInscripciones> temp = reg.getListaTareasDinardap(t.getId(), "DC"); // DC : DINARDAP CERTIFICADO FICHAS
            if (!temp.isEmpty()) {
                listCert.addAll(temp);
            }
            showIns = !listInsc.isEmpty();
            showCer = !listCert.isEmpty();
            JsfUti.update("formTareas");
            JsfUti.executeJS("PF('dlgTareas').show();");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean validaCampos() {
        if (tareaDinardap.getNumeroSolicitud() != null) {
            if (tareaDinardap.getInstitucion() != null) {
                if (tareaDinardap.getSolicitante() != null) {
                    if (tareaDinardap.getObservacion() != null) {
                        return true;
                    }
                }
            }
        }
        JsfUti.messageWarning(null, "Todos los campos son obligatorios.", "");
        return false;
    }

    public void guardarSolicitud() {
        try {
            if (this.validaCampos()) {
                if (this.validaFiles()) {
                    tareaDinardap.setFecha(new Date());
                    tareaDinardap.setUsuario(session.getName_user());
                    tareaDinardap = (RegpTareasDinardap) acl.persist(tareaDinardap);
                    if (tareaDinardap.getId() != null) {
                        for (Archivo f : this.getFiles()) {
                            Document doc = this.attachDocument("solicitudesDinardap", f.getNombre(), f.getTipo(), this.leerArchivo(f.getRuta()));
                            if (doc != null) {
                                RegpTareasDinardapDocs temp = new RegpTareasDinardapDocs();
                                temp.setDocumento(doc.getId());
                                temp.setNombreDoc(f.getNombre());
                                temp.setRegpTareasDinardap(tareaDinardap);
                                acl.persist(temp);
                            }
                        }
                        realizado = true;
                        JsfUti.update("formInfoTarea");
                    } else {
                        JsfUti.messageError(null, Messages.error, "");
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void aceptarSolicitud(){
        realizado = false;
        tareasLazy = new RegpTareasDinardapLazy(Boolean.TRUE);
        JsfUti.update("mainForm:dtTareasDinardap");
        JsfUti.executeJS("PF('dlgIngresoTarea').hide();");
    }

    public void crearTarea() {
        if (this.validaListas()) {
            switch (opcion) {
                case 0:
                    JsfUti.messageInfo(null, "Debe seleccionar el tipo de tarea a realizar.", "");
                    break;
                case 1:
                    this.llenarDatos("DI", "Inscripcion Nueva");
                    if (tareaRegistro.getId() != null) {
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripcionNueva.xhtml?code=" + tareaRegistro.getId());
                    }
                    break;
                case 2:
                    this.llenarDatos("DW", "Certificado Word Nuevo");
                    if (tareaRegistro.getId() != null) {
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/certificadoNuevoRp.xhtml?code=" + tareaRegistro.getId());
                    }
                    break;
                case 3:
                    this.llenarDatos("DC", "Certificado Ficha Registral");
                    if (tareaRegistro.getId() != null) {
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/realizarCertificadoFichaRegistral.xhtml?code=" + tareaRegistro.getId());
                    }
                    break;
            }
        }
    }

    public void llenarDatos(String tipo, String obs) {
        try {
            if (tareaDinardap.getId() != null) {
                tareaRegistro = new RegpCertificadosInscripciones();
                tareaRegistro.setFecha(new Date());
                tareaRegistro.setTipoTarea(tipo);
                tareaRegistro.setObservacion(obs);
                tareaRegistro.setTareaDinardap(tareaDinardap);
                tareaRegistro = (RegpCertificadosInscripciones) acl.persist(tareaRegistro);
            }
        } catch (Exception e) {
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirecTarea(RegpCertificadosInscripciones temp, Integer flag) {
        if (temp.getRealizado()) {
            JsfUti.messageError(null, "Tarea Completada", "Ya fue realizada esta tarea.");
        } else {
            switch (temp.getTipoTarea()) {
                case "DI":
                    session.setTaskID(null);
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripcionNueva.xhtml?code=" + temp.getId());
                    break;
                case "DW":
                    session.setTaskID(null);
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/certificadoNuevoRp.xhtml?code=" + temp.getId());
                    break;
                case "DC":
                    session.setTaskID(null);
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/realizarCertificadoFichaRegistral.xhtml?code=" + temp.getId());
                    break;
                default:
                    JsfUti.messageError(null, Messages.error, "");
                    break;
            }
        }
    }

    public void finalizarSolicitud() {
        try {
            if (listCert.isEmpty() && listInsc.isEmpty()) {
                JsfUti.messageError(null, "No se puede finalizar solicitud", "No se han realizado tareas.");
            } else {
                if (this.validaListas()) {
                    if (tareaDinardap.getId() != null) {
                        tareaDinardap.setRealizado(Boolean.TRUE);
                        tareaDinardap.setFechaFin(new Date());
                        Boolean b = acl.update(tareaDinardap);
                        if (b) {
                            tareasLazy = new RegpTareasDinardapLazy(Boolean.TRUE);
                            JsfUti.update("mainForm:dtTareasDinardap");
                            JsfUti.executeJS("PF('dlgTareas').hide();");
                        } else {
                            JsfUti.messageError(null, Messages.error, "");
                        }
                    } else {
                        JsfUti.messageError(null, Messages.error, "");
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(TareasDinardap.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validaListas() {
        for (RegpCertificadosInscripciones c : listCert) {
            if (!c.getRealizado()) {
                JsfUti.messageError(null, "Falta de completar una Tarea.", "");
                return false;
            }
        }
        for (RegpCertificadosInscripciones c : listInsc) {
            if (!c.getRealizado()) {
                JsfUti.messageError(null, "Falta de completar una Tarea.", "");
                return false;
            }
        }
        return true;
    }

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

    public RegpTareasDinardap getTareaDinardap() {
        return tareaDinardap;
    }

    public void setTareaDinardap(RegpTareasDinardap tareaDinardap) {
        this.tareaDinardap = tareaDinardap;
    }

    public RegpCertificadosInscripciones getTareaRegistro() {
        return tareaRegistro;
    }

    public void setTareaRegistro(RegpCertificadosInscripciones tareaRegistro) {
        this.tareaRegistro = tareaRegistro;
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

    public RegpTareasDinardapLazy getTareasLazy() {
        return tareasLazy;
    }

    public void setTareasLazy(RegpTareasDinardapLazy tareasLazy) {
        this.tareasLazy = tareasLazy;
    }

    public List<RegpCertificadosInscripciones> getListInsc() {
        return listInsc;
    }

    public void setListInsc(List<RegpCertificadosInscripciones> listInsc) {
        this.listInsc = listInsc;
    }

    public List<RegpCertificadosInscripciones> getListCert() {
        return listCert;
    }

    public void setListCert(List<RegpCertificadosInscripciones> listCert) {
        this.listCert = listCert;
    }

    public Boolean getShowCer() {
        return showCer;
    }

    public void setShowCer(Boolean showCer) {
        this.showCer = showCer;
    }

    public Boolean getShowIns() {
        return showIns;
    }

    public void setShowIns(Boolean showIns) {
        this.showIns = showIns;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

}
