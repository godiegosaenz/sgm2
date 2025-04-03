/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.ProcessAdministration;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.HistoricoTramitesLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import util.Archivo;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class TramitesIngresadosEdif extends BpmManageBeanBaseRoot implements Serializable{
    
    @javax.inject.Inject
    private Entitymanager aclServices;
    @Inject
    private ServletSession ss;
    @javax.inject.Inject
    private PropiedadHorizontalServices servicio;
    
    @Inject
    private UserSession userSess;

    private static final Logger LOG = Logger.getLogger(TramitesIngresadosEdif.class.getName());
    private CatEnte solicitante;
    private HistoricoTramitesLazy tramites;
    private List<GeTipoTramite> tipotramites;
    protected HistoricoTramites hist;
    protected List<HistoricTaskInstance> task;
    protected List<Attachment> documentos;
    private HistoricTaskInstance tareaSeleccionada;
    protected GeDepartamento departamento;
    private int prioridad;
    private String carpeta, url, instance;
    private Folder folder;
    private boolean habilitado = false, subcarpeta = false;
    private byte[] data = null;
    private Document doc = null;
    private String emailNew, tlfnNew;
    private List<EnteTelefono> listTlfs;
    private List<EnteCorreo> listCorreos;
    private String comentarios = "";
    private Boolean mostrarBoton;
    
    @PostConstruct
    public void initView() {
        try {
            tipotramites = aclServices.findAll(Querys.getGeTipoTramiteById, new String[]{"id"}, new Object[]{1L});
            tramites = new HistoricoTramitesLazy(tipotramites);
            departamento=(GeDepartamento)aclServices.find(GeDepartamento.class, 1L);
            mostrarBoton = true;
            url = SisVars.urlServidorAlfrescoPublica + "share/page/site/smbworkflow/document-details?nodeRef=";
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void ticket(HistoricoTramites t) {
        try {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.agregarParametro("P_TITULO", "Número de Trámite");
            ss.agregarParametro("P_SUBTITULO", t.getTipoTramite().getDescripcion());
            ss.agregarParametro("P_NUMERO_TRAMITE", t.getId().toString());
            ss.agregarParametro("NOM_SOLICITANTE", t.getNombrePropietario());
            ss.agregarParametro("DESCRIPCION", t.getTipoTramite().getDescripcion());
            ss.agregarParametro("FECHA", t.getFecha());
            if(t.getMz() != null && t.getSolar()!=null){
                ss.agregarParametro("DIRECCION", " MZ: " + t.getMz() + " SL: " + t.getSolar());
            }
            ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            ss.setNombreReporte("plantilla1");
            ss.setTieneDatasource(false);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void ver(HistoricoTramites t) {
        try {
            task=null;
            documentos=null;
            hist = new HistoricoTramites();
            
            if (t != null) {
                hist = t;
                solicitante = this.hist.getSolicitante();
                if(solicitante!=null){
                    listCorreos = solicitante.getEnteCorreoCollection();
                    listTlfs = solicitante.getEnteTelefonoCollection();
                }
            }
            if (t != null && (t.getIdProceso()!=null||t.getIdProcesoTemp()!=null)) {
                if (t.getIdProcesoTemp() != null) {
                    task = this.getTaskByProcessInstanceIdMain(t.getIdProcesoTemp());
                }else{
                    task = this.getTaskByProcessInstanceIdMain(t.getIdProceso());
                }
                documentos=documentosTramite(t);
            }
            if(task!=null){
                for (HistoricTaskInstance tarea : task) {
                    if (tarea.getAssignee().equalsIgnoreCase(userSess.getName_user())) {
                        habilitado=true;
                        break;
                    }
                }
                if (habilitado) {
                    if (!hist.getEstado().equalsIgnoreCase("pendiente")) {
                        habilitado=false;
                    }
                }
            }
            if(habilitado){
                if (hist.getCarpetaRep() != null) {
                    carpeta = hist.getCarpetaRep();
                    subcarpeta = true;
                } else if (hist.getTipoTramite().getCarpeta() != null) {
                    carpeta = hist.getTipoTramite().getCarpeta();
                    subcarpeta = false;
                }
                if (hist.getIdProcesoTemp() != null) {
                    instance = hist.getIdProcesoTemp();
                } else if (hist.getIdProceso() != null) {
                    instance = hist.getIdProceso();
                } else {
                    instance = null;
                }
            }
            JsfUti.update("frmInfo");
            JsfUti.executeJS("PF('info').show()");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public List<Attachment> documentosTramite(HistoricoTramites t){
        if (t.getIdProcesoTemp() != null) {
            return this.getProcessInstanceAllAttachmentsFiles(t.getIdProcesoTemp());
        }else{
            return this.getProcessInstanceAllAttachmentsFiles(t.getIdProceso());
        }
    }
    
    public void reasignacion(HistoricoTramites t){
        try {
            task=null;
            hist=t;
            if (t != null && (t.getIdProceso()!=null||t.getIdProcesoTemp()!=null)) {
                if (t.getIdProcesoTemp() != null) {
                    task = this.getTaskByProcessInstanceIdMain(t.getIdProcesoTemp());
                }else{
                    task = this.getTaskByProcessInstanceIdMain(t.getIdProceso());
                }
            }
            JsfUti.update("frmReasignacion");
            JsfUti.executeJS("PF('reasignacion').show();");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void seleccionarTramite(HistoricoTramites t){
        try {
            hist=t;
            prioridad=(int) this.getVariableByPorcessIntance(hist.getIdProceso()==null?hist.getIdProcesoTemp():hist.getIdProceso(), "prioridad");
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void actualizarPrioridad(){
        try {
            List<String> listadoIdsProcessInstace=this.obtenerProcessInstanceByProcessInstaceIdMain(hist.getIdProceso()==null?hist.getIdProcesoTemp():hist.getIdProceso());
            for (String listadoIdsProcessInstace1 : listadoIdsProcessInstace) {
                this.setVariableByProcessInstance(listadoIdsProcessInstace1, "prioridad", prioridad);
                List<Task> tareasActivas=this.obtenerTareasActivasProcessInstance(listadoIdsProcessInstace1);
                this.asignarTareaPriority(tareasActivas, prioridad);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessAdministration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void seleccionarTareaReasignacion(HistoricTaskInstance ta){
        tareaSeleccionada=ta;
        
        JsfUti.update("formUsers");
        JsfUti.executeJS("PF('usersDlg').show();");
    }
    
    public void reasignarTarea(AclUser usuario){
        try {
            servicio.guardarObservaciones(hist, session.getName_user(),"TAREA: "+ tareaSeleccionada.getName() +"USUARIO ANTERIOR: "+tareaSeleccionada.getAssignee()+". USUARIO ACTUAL: "+usuario.getUsuario(), "REASIGNACION DE USUARIO");
            this.reasignarTarea(tareaSeleccionada.getId(), usuario.getUsuario());
            Map<String, Object> v = this.engine.getvariables(tareaSeleccionada.getProcessInstanceId());
            for (Map.Entry<String, Object> entrySet : v.entrySet()) {
                if(entrySet.getValue()!=null && entrySet.getValue().equals(tareaSeleccionada.getAssignee())){
                    this.setVariableByProcessInstance(tareaSeleccionada.getProcessInstanceId(), entrySet.getKey(), usuario.getUsuario());
                    //break;
                }
            }
            task= this.getTaskByProcessInstanceIdMain(hist.getIdProcesoTemp());
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error", ex);
        }
    }
    
    public void cargarDocumento() {
        try {
            if (this.getFiles() != null && !this.getFiles().isEmpty()) {
                if (carpeta != null && instance != null) {
                    if (this.getCmis() != null) {
                        if (hist.getTipoTramite().getDisparador() != null) {
                            folder = this.getCmis().getFolder(hist.getTipoTramite().getDisparador().getCarpeta());
                        } else {
                            folder = this.getCmis().getFolder(hist.getTipoTramite().getCarpeta());
                        }
                        if (subcarpeta) {
                            folder = this.getCmis().getFolder(hist.getCarpetaRep());
                        }
                        if (folder == null) {
                            Faces.messageWarning(null, "Advertencia", "El tramite no tiene una carpeta asociada para la carga de documentos");
                            return;
                        }
                        for (Archivo f : this.getFiles()) {
                            data = this.leerArchivo(f.getRuta());
                            doc = this.getCmis().createDocument(folder, f.getNombre(), f.getTipo(), data);
                            f.setUrl(url + doc.getId());
                            if (f.getTipo() != null) {
                                this.getProcessEngine().getTaskService().createAttachment(f.getTipo(), null, instance, f.getNombre(), "Archivo Adjunto de tarea " + (new Date()).getTime() + "(" + instance + ")" + f.getNombre(), f.getUrl());
                            } else {
                                this.getProcessEngine().getTaskService().createAttachment("url", null, instance, f.getNombre(), "Archivo Adjunto de tarea " + (new Date()).getTime() + "(" + instance + ")" + f.getNombre(), f.getUrl());
                            }
                        }
                        documentos=documentosTramite(hist);
                        servicio.guardarObservaciones(hist, session.getName_user(),"SE ADJUNTARON " + this.getFiles().size() + " DOCUMENTO(S) AL TRAMITE.", "ASOCIAR DOCUMENTOS");
                        Faces.messageInfo(null, "Nota", "Se asociaron " + this.getFiles().size() + " documentos al tramite #" + hist.getId());
                    } else {
                        Faces.messageWarning(null, "Advertencia", "No es posible cargar los documentos");
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia", "El tramite no tiene una carpeta asociada para la carga de documentos");
                }
            } else {
                Faces.messageWarning(null, "Advertencia", "Debe ingresar un documento");
            }
        } catch (Exception e) {
            Logger.getLogger(TramitesIngresadosEdif.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void agregarTlfn() {
        if (tlfnNew != null) {
            tlfnNew = tlfnNew.trim();
            Boolean flag = true;
            for (EnteTelefono t : listTlfs) {
                if (t.getTelefono().equals(tlfnNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validateNumberPattern(tlfnNew)) {
                    if (PhoneUtils.getValidNumber(tlfnNew, SisVars.region)) {
                        EnteTelefono telefono = new EnteTelefono();
                        telefono.setTelefono(tlfnNew);
                        telefono.setEnte(solicitante);
                        listTlfs.add(telefono);
                        tlfnNew = "";
                        comentarios = comentarios + "Se agregó el teléfono: "+telefono.getTelefono()+"\n";
                    } else {
                        JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                    }
                } else {
                    JsfUti.messageInfo(null, Messages.tlfnInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }
    
    public void eliminarTlfn(EnteTelefono tlfn) {
        if (tlfn.getId() != null) {
            listTlfs.remove(tlfn);
            comentarios = comentarios + "Se eliminó el teléfono: "+tlfn.getTelefono()+"\n";
            acl.delete(tlfn);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteTelefono te : listTlfs) {
                if (te.getTelefono().equals(tlfn.getTelefono())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                listTlfs.remove(ind);
            }
        }
    }

    public void eliminarEmail(EnteCorreo email) {
        if (email.getId() != null) {
            listCorreos.remove(email);
            comentarios = comentarios + "Se eliminó el correo: "+email.getEmail()+"\n";
            acl.delete(email);
        } else {
            int ind = -1;
            int cont = 0;
            for (EnteCorreo co : listCorreos) {
                if (co.getEmail().equals(email.getEmail())) {
                    ind = cont;
                }
                cont++;
            }
            if (ind >= 0) {
                listCorreos.remove(ind);
            }
        }
    }
    
    public void agregarEmail() {
        if (emailNew != null) {
            emailNew = emailNew.trim();
            Boolean flag = true;
            for (EnteCorreo c : listCorreos) {
                if (c.getEmail().equals(emailNew)) {
                    flag = false;
                }
            }
            if (flag) {
                if (Utils.validarEmailConExpresion(emailNew)) {
                    EnteCorreo email = new EnteCorreo();
                    email.setEmail(emailNew);
                    email.setEnte(solicitante);
                    listCorreos.add(email);
                    comentarios = comentarios + "Se agregó el correo: "+emailNew+"\n";
                    emailNew = "";
                } else {
                    JsfUti.messageInfo(null, Messages.correoInvalido, "");
                }
            } else {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            }
        } else {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
        }
    }
    
    public void guardarDatosEnte(){        
        try{
            if(aclServices.editarEnteCorreosTlfns(solicitante)){
                hist.setObservacion(hist.getObservacion()+"\n"+comentarios);
                aclServices.update(hist);
                JsfUti.messageInfo(null, "Info", "Se actualizaron los datos correctamente del solicitante");
                mostrarBoton = false;
            }else{
                JsfUti.messageInfo(null, "Info", "No se actualizaron los datos del solicitante");
            }
        }catch(Exception e){
            JsfUti.messageFatal(null, "Error", "Hubo un error al actualizar el solicitante");
            e.printStackTrace();
        }
    }
    
    public HistoricoTramitesLazy getTramites() {
        return tramites;
    }

    public void setTramites(HistoricoTramitesLazy tramites) {
        this.tramites = tramites;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public HistoricoTramites getHist() {
        return hist;
    }

    public void setHist(HistoricoTramites hist) {
        this.hist = hist;
    }

    public List<HistoricTaskInstance> getTask() {
        return task;
    }

    public void setTask(List<HistoricTaskInstance> task) {
        this.task = task;
    }

    public List<Attachment> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Attachment> documentos) {
        this.documentos = documentos;
    }

    public HistoricTaskInstance getTareaSeleccionada() {
        return tareaSeleccionada;
    }

    public void setTareaSeleccionada(HistoricTaskInstance tareaSeleccionada) {
        this.tareaSeleccionada = tareaSeleccionada;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public UserSession getUserSess() {
        return userSess;
    }

    public void setUserSess(UserSession userSess) {
        this.userSess = userSess;
    }

    public String getEmailNew() {
        return emailNew;
    }

    public void setEmailNew(String emailNew) {
        this.emailNew = emailNew;
    }

    public String getTlfnNew() {
        return tlfnNew;
    }

    public void setTlfnNew(String tlfnNew) {
        this.tlfnNew = tlfnNew;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public List<EnteTelefono> getListTlfs() {
        return listTlfs;
    }

    public void setListTlfs(List<EnteTelefono> listTlfs) {
        this.listTlfs = listTlfs;
    }

    public List<EnteCorreo> getListCorreos() {
        return listCorreos;
    }

    public void setListCorreos(List<EnteCorreo> listCorreos) {
        this.listCorreos = listCorreos;
    }

    public Boolean getMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(Boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }
    
}
