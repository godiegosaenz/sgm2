/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.primefaces.event.FileUploadEvent;
import util.Archivo;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class EditarInformeOT extends BpmManageBeanBaseRoot implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession sess;
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    protected Entitymanager services;
    
    private HistoricoTramites ht;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private List<Archivo> archivos = new ArrayList();
    
    @PostConstruct
    public void initView() {
        if (sess != null && sess.getTaskID() != null) {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                ht = (HistoricoTramites) services.findAll(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                paramsActiviti = new HashMap<>();
                obs = new Observaciones();
            }
        }
    }
    
    /**
     * Si se subió el informe entonces muestra el dialog de la observación, caso
     * contrario no.
     * 
     * @param b 
     */
    public void mostrarObservaciones(Boolean b){
        if(archivos.isEmpty()){
            JsfUti.messageError(null, "Error", "Debe subir al menos un archivo.");
            return;
        }
        if(b){
            JsfUti.executeJS("PF('obs').show();");
        }else{
            this.continuar();
        }
    }
    
    /**
     * Completa la tarea de edición de informe.
     * 
     */
    public void completarTarea(){
        if(obs!=null){
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(sess.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            if(services.persist(obs) != null){
                paramsActiviti.put("carpeta", ht.getCarpetaRep());
                paramsActiviti.put("listaArchivos", archivos);
                paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                paramsActiviti.put("listaArchivosFinal", new ArrayList<Archivo>());
                paramsActiviti.put("prioridad", 50);
                this.completeTask(this.getTaskId(), paramsActiviti);
                this.continuar();
            }
        }else{
            JsfUti.messageError(null, "Error", "Debe ingresar al menos una observación.");
        }
    }
    
    /**
     * Permite ssubir un archivo a Alfresco y mantenerlo referenciado.
     * 
     * @param event 
     */
    public void handleFileUploadJS(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + event.getFile().getFileName());
            documento.setDescripcion("tipo1");
            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);
            this.archivos.add(documento);
            JsfUti.update("frmMain:tdatos:panel1");
                    
        } catch (IOException e) {
            Logger.getLogger(RealizarInformeOT.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * Remueve un archivo de la lista de archivos.
     * 
     * @param file 
     */
    public void eliminarArchivo(Archivo file){
        archivos.remove(file);
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }
    
}
