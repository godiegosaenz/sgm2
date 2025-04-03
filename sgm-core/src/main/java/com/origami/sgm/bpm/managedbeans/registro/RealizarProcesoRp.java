/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import util.Archivo;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class RealizarProcesoRp extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected HistoricoTramites ht;
    protected RegpLiquidacionDerechosAranceles liq;
    protected RegpCertificadosInscripciones tarea;
    protected String observacion;
    protected HashMap<String, Object> params;
    protected Integer opcion = 0;
    protected List<RegpCertificadosInscripciones> list = new ArrayList<>();
    protected List<RegpCertificadosInscripciones> listCertf = new ArrayList<>();

    protected Boolean showCert = false;
    protected Boolean showInscr = false;
    protected Boolean flag = false;
    protected Integer tipo = 0;
    protected Integer cantidad = 0;
    protected String formatoArchivos, url, obsDocumento;
    protected Boolean cargado = false;
    private Folder folder;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (session.getTaskID() != null) {
                this.setTaskId(session.getTaskID());
                Long id = (Long) this.getVariable(this.getTaskId(), "tramite");
                ht = reg.getHistoricoTramiteById(id);
                liq = ht.getRegpLiquidacionDerechosAranceles();
                Integer temp = reg.getCantidadTareasByIdLiquidacion(liq.getId());
                if (temp == 0) {
                    list = reg.getListTareasByLiquidacion(liq.getId());
                    if (list != null && !list.isEmpty()) {
                        showInscr = true;
                    }
                    listCertf = reg.getListTareasCertfByLiquidacion(liq.getId());
                    if (listCertf != null && !listCertf.isEmpty()) {
                        showCert = true;
                    }
                } else if (temp > 0) {
                    list = reg.getListaCertfByLiquidacion(liq.getId(), "I");
                    if (list != null && !list.isEmpty()) {
                        showInscr = true;
                    }
                    listCertf = reg.getListaCertfByLiquidacion(liq.getId(), "CE");
                    if (listCertf != null && !listCertf.isEmpty()) {
                        showCert = true;
                    }
                }
                formatoArchivos = SisVars.formatoArchivos;
                url = SisVars.urlServidorAlfrescoPublica + "share/page/site/smbworkflow/document-details?nodeRef=";
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgConfirmacion(Boolean b) {
        cantidad = 0;
        flag = b;
        JsfUti.update("formConfirm");
        JsfUti.executeJS("PF('dlgConfirm').show();");
    }

    public void saveTareas() {
        try {
            if (cantidad > 0) {
                if (flag) {
                    list = reg.saveListCertfByLiq(liq.getId(), cantidad, "I");
                    if (list != null && !list.isEmpty()) {
                        showInscr = true;
                    }
                } else {
                    listCertf = reg.saveListCertfByLiq(liq.getId(), cantidad, "CE");
                    if (listCertf != null && !listCertf.isEmpty()) {
                        showCert = true;
                    }
                }
                JsfUti.update("mainForm");
            } else {
                JsfUti.messageInfo(null, "Cantidad debe ser mayor a Cero.", "");
            }
            JsfUti.executeJS("PF('dlgConfirm').hide();");
            JsfUti.update("mainForm");
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void borrarTarea(RegpCertificadosInscripciones obj, Boolean b) {
        try {
            if (obj.getRealizado()) {
                JsfUti.messageWarning(null, "Esta tarea ya fue realizada. No se puede Borrar.", "");
            } else {
                if (this.compruebaListas()) {
                    obj.setEstado(false);
                    acl.persist(obj);
                    if (b) {
                        list.remove(obj);
                        showInscr = !list.isEmpty();
                    } else {
                        listCertf.remove(obj);
                        showCert = !listCertf.isEmpty();
                    }
                    JsfUti.update("mainForm");
                } else {
                    JsfUti.messageWarning(null, "Es la unica tarea de la lista. No se puede Borrar.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean compruebaListas() {
        if (list.isEmpty() && listCertf.size() == 1) {
            return false;
        }
        return !(listCertf.isEmpty() && list.size() == 1);
    }

    public void borrarTareaCertf(RegpCertificadosInscripciones cert) {
        try {
            if (cert.getRealizado()) {
                JsfUti.messageWarning(null, "Esta tarea ya fue realizada. No se puede Borrar.", "");
            } else {
                cert.setEstado(false);
                acl.persist(cert);
                listCertf.remove(cert);
                JsfUti.update("mainForm:mainTab:dtTareasCert");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void realizarTarea(RegpCertificadosInscripciones task) {
        try {
            tarea = task;
            if (!tarea.getRealizado()) {
                if (tarea.getActo().getSubeDocumento()) {
                    this.setFiles(new ArrayList<Archivo>());
                    JsfUti.update("formCargaNotacion");
                    JsfUti.executeJS("PF('dlgCargaNotacion').show();");
                } else {
                    session.setTaskID(this.getTaskId());
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripcionNueva.xhtml?code=" + tarea.getId());
                }
            } else {
                JsfUti.messageInfo(null, "Esta tarea ya fue realizada.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void realizarCertificado(RegpCertificadosInscripciones task) {
        try {
            tarea = task;
            if (!tarea.getRealizado()) {
                JsfUti.update("formSubeDoc");
                JsfUti.executeJS("PF('dlgSubeDoc').show();");
            } else {
                JsfUti.messageInfo(null, "Esta tarea ya fue realizada.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirectRealizar() {
        if (tarea.getId() != null) {
            if (opcion == 1) {
                session.setTaskID(this.getTaskId());
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/realizarCertificadoFichaRegistral.xhtml?code=" + tarea.getId());
                /*String url = reg.getUrlByTarea(tarea.getId());
                 if (url != null) {
                 session.setTaskID(this.getTaskId());
                 JsfUti.redirectFaces(url + "?code=" + tarea.getId());
                 } else {
                 JsfUti.messageError(null, Messages.error, "");
                 }*/
            } else if (opcion == 2) {
                session.setTaskID(this.getTaskId());
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/certificadoNuevoRp.xhtml?code=" + tarea.getId());
                //JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/subirDocumentoRp.xhtml?code=" + tarea.getId());
            } else {
                JsfUti.messageInfo(null, "Seleccione una opcion.", "");
            }
        }
    }

    public void showDlgCompletTask() {
        if (this.validar()) {
            JsfUti.update("formObs");
            JsfUti.executeJS("PF('dlgObsvs').show();");
        } else {
            JsfUti.messageInfo(null, "Debe realizar todas las Tareas del Tramite.", "");
        }
    }

    public void completarTarea() {
        try {
            if (observacion != null) {
                Observaciones ob = new Observaciones();
                ob.setObservacion(observacion);
                ob.setEstado(true);
                ob.setFecCre(new Date());
                ob.setIdTramite(ht);
                ob.setTarea(this.getTaskDataByTaskID().getName());
                ob.setUserCre(session.getName_user());
                acl.persist(ob);
                params = new HashMap<>();
                //se llena la variable iniciarTramite con el valor 4 para que al momento
                //de entregar los documentos saber si ya fueron terminadas todas las tareas
                //y dar por finalizado el tramite
                params.put("iniciarTramite", 4);
                this.llenarDatosCorreo();
                /*
                 Se creo el campo flag_one en la tabla ge_tipo_tramite como una 
                 llave para el parametro catastrar, que sirve para saber si el tramite va o no a un tecnico de catastro
                 por ahora al inicio, el valor de flag_one es false, y no se va a asignar a un tecnico de catastro.
                 */
                /*if (ht.getTipoTramite().getFlagOne()) {
                 //Aqui falta realizar una funcion que saque la cantidad de tareas para asignar al tecnico de catastro
                 //tenia pensado crear una tabla que guarde esas tareas y les haga seguimiento mediante estados
                 //tal y como se hizo para las inscripciones o certificaciones pero en otra tabla
                 //pero hay que tomar en cuenta que todas las tareas de un mismo tramite no se catastran y habria que 
                 //separar cuales si u cuales no
                 params.put("catastrar", 2);
                 params.put("tecnicoCatastro", reg.getTecnicoCatastroRegistro(1));
                 } else {
                 params.put("catastrar", 1);
                 }*/
                //List<RegpCertificadosInscripciones> listaTareaCatastro = reg.guardarTareasCatastroByLiquidacion(liq.getId());
                //if (listaTareaCatastro != null && !listaTareaCatastro.isEmpty()) {
                Integer temp = reg.getCantidadTareasTransferenciaDominio(this.getTaskId(), ht.getId());
                if (temp > 0) {
                    params.put("catastrar", 2);
                    params.put("tecnicoCatastro", reg.getTecnicoCatastroRegistro(temp));
                } else {
                    params.put("catastrar", 1);
                }
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validar() {
        if (!list.isEmpty()) {
            for (RegpCertificadosInscripciones cert : list) {
                if (!cert.getRealizado()) {
                    return false;
                }
            }
        }
        if (!listCertf.isEmpty()) {
            for (RegpCertificadosInscripciones cert : listCertf) {
                if (!cert.getRealizado()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void llenarDatosCorreo() {
        MsgFormatoNotificacion msg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{2L});
        if (msg != null) {
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String mensaje = msg.getHeader() + "Ha finalizado el tramite del REGISTRO DE LA PROPIEDAD numero: "
                    + liq.getNumTramiteRp() + ", con fecha de liquidacion: " + ft.format(ht.getFecha())
                    + ", por favor acerquese a retirar sus documentos." + msg.getFooter();
            params.put("to", ht.getCorreos());
            params.put("subject", ht.getTipoTramiteNombre());
            params.put("message", mensaje);
        }
    }

    public void crearTarea() {
        try {
            switch (opcion) {
                case 0:
                    JsfUti.messageInfo(null, "Debe seleccionar el tipo de tarea a realizar.", "");
                    break;
                case 1:
                    this.llenarDatos("I", "Inscripcion Nueva", false);
                    if (tarea.getId() != null) {
                        session.setTaskID(this.getTaskId());
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripcionNueva.xhtml?code=" + tarea.getId());
                    }
                    break;
                case 2:
                    this.llenarDatos("CE", "Certificado Ficha Registral", false);
                    if (tarea.getId() != null) {
                        session.setTaskID(this.getTaskId());
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/realizarCertificadoFichaRegistral.xhtml?code=" + tarea.getId());
                    }
                    break;
                case 3:
                    this.llenarDatos("CE", "Certificado Word Nuevo", false);
                    if (tarea.getId() != null) {
                        session.setTaskID(this.getTaskId());
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/certificadoNuevoRp.xhtml?code=" + tarea.getId());
                    }
                    break;
                case 4:
                    tarea = new RegpCertificadosInscripciones();
                    obsDocumento = "Se adjunta: Anotacion al Margen";
                    this.setFiles(new ArrayList<Archivo>());
                    JsfUti.update("formCargaNotacion");
                    JsfUti.executeJS("PF('dlgCargaNotacion').show();");
                    break;
                case 5:
                    tarea = new RegpCertificadosInscripciones();
                    obsDocumento = "Se adjunta: Certificacion de Escritura";
                    this.setFiles(new ArrayList<Archivo>());
                    JsfUti.update("formCargaNotacion");
                    JsfUti.executeJS("PF('dlgCargaNotacion').show();");
                    break;
                default:
                    JsfUti.messageError(null, Messages.error, "");
                    break;
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void llenarDatos(String tipo, String obs, Boolean realizado) {
        try {
            if (liq.getId() != null) {
                tarea = new RegpCertificadosInscripciones();
                tarea.setFecha(new Date());
                tarea.setFechaFin(new Date());
                tarea.setLiquidacion(liq);
                tarea.setTipoTarea(tipo);
                tarea.setObservacion(obs);
                tarea.setRealizado(realizado);
                tarea = (RegpCertificadosInscripciones) acl.persist(tarea);
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarDocsAdjunto() {
        try {
            if (this.validaFiles()) {
                if(tarea.getId() == null){
                    this.llenarDatos("I", obsDocumento, true);
                } else {
                    tarea.setFechaFin(new Date());
                    tarea.setRealizado(Boolean.TRUE);
                    acl.persist(tarea);
                }
                if (tarea.getId() != null) {
                    folder = this.getCmis().getFolder(ht.getCarpetaRep());
                    if (folder == null) {
                        folder = this.getCmis().createSubFolder(this.getCmis().getFolder(ht.getTipoTramite().getCarpeta()), ht.getCarpetaRep());
                    }
                    for (Archivo f : this.getFiles()) {
                        Document doc = this.getCmis().createDocument(folder, f.getNombre(), f.getTipo(), this.leerArchivo(f.getRuta()));
                        if (doc.getId() != null) {
                            f.setUrl(url + doc.getId());
                            this.getProcessEngine().getTaskService().createAttachment(f.getTipo(), null, ht.getIdProceso(), f.getNombre(), "Archivo Adjunto Anotacion al Margen " + "(" + ht.getIdProceso() + ")" + f.getNombre(), f.getUrl());
                        }
                    }
                    cargado = true;
                    JsfUti.update("formCargaNotacion");
                    JsfUti.messageInfo(null, "Se adjunto/aron el/los documento/os correctamente.", "");
                } else {
                    JsfUti.messageError(null, Messages.error, "");
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargaListasTareas() {
        try {
            list = reg.getListaCertfByLiquidacion(liq.getId(), "I");
            if (list != null && !list.isEmpty()) {
                showInscr = true;
            }
            listCertf = reg.getListaCertfByLiquidacion(liq.getId(), "CE");
            if (listCertf != null && !listCertf.isEmpty()) {
                showCert = true;
            }
            cargado = false;
            JsfUti.update("mainForm");
            JsfUti.executeJS("PF('dlgCargaNotacion').hide();");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarProcesoRp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /*public void prueba(String task, Long tramite){
     System.out.println("cantidad > " + reg.getCantidadTareasTransferenciaDominio(task, tramite)); 
     }*/
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

    public List<RegpCertificadosInscripciones> getList() {
        return list;
    }

    public void setList(List<RegpCertificadosInscripciones> list) {
        this.list = list;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<RegpCertificadosInscripciones> getListCertf() {
        return listCertf;
    }

    public void setListCertf(List<RegpCertificadosInscripciones> listCertf) {
        this.listCertf = listCertf;
    }

    public Boolean getShowCert() {
        return showCert;
    }

    public void setShowCert(Boolean showCert) {
        this.showCert = showCert;
    }

    public Boolean getShowInscr() {
        return showInscr;
    }

    public void setShowInscr(Boolean showInscr) {
        this.showInscr = showInscr;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getFormatoArchivos() {
        return formatoArchivos;
    }

    public void setFormatoArchivos(String formatoArchivos) {
        this.formatoArchivos = formatoArchivos;
    }

    public Boolean getCargado() {
        return cargado;
    }

    public void setCargado(Boolean cargado) {
        this.cargado = cargado;
    }

}
