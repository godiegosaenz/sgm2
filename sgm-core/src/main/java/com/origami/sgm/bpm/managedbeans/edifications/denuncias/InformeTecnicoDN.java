/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.denuncias;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.edifications.otrosTramites.RealizarInformeOT;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
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
import util.Messages;
import util.VerCedulaUtils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class InformeTecnicoDN extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(InformeTecnicoDN.class.getName());
    
    @Inject
    private UserSession uSession;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;
    
    private HistoricoTramites ht;
    private Observaciones obs;
    private CatEnte demandado;
    private List<Archivo> archivos;
    private HashMap<String, Object> paramsActiviti;
    private Integer tipoEnte;
    private Boolean excepcionalEmpresa;
    private String ciRuc;
    
    @PostConstruct
    public void initView(){
        try{
            if (session.esLogueado() && uSession.getTaskID() != null){
                this.setTaskId(uSession.getTaskID());
                
                ht = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if (ht == null) {
                    return;
                }
                tipoEnte = 1;
                obs = new Observaciones();
                archivos = new ArrayList();
                paramsActiviti = new HashMap<>();
                excepcionalEmpresa = false;
                demandado = new CatEnte();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardarEnte() {
        try {
            VerCedulaUtils validacion = new VerCedulaUtils();
            HashMap paramt = new HashMap<>();
            Boolean esExcepcional = false;
            
            if (tipoEnte == 1) {
                if (demandado.getCiRuc() == null || demandado.getApellidos() == null || demandado.getNombres() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", demandado.getCiRuc());
                if (acl.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isCIValida(demandado.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                demandado.setEsPersona(Boolean.TRUE);
            }
            if (tipoEnte == 2) {
                if (demandado.getCiRuc() == null || demandado.getRazonSocial() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", demandado.getCiRuc());
                if (acl.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isRucValido(demandado.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                demandado.setEsPersona(Boolean.FALSE);
            }
            if (tipoEnte == 3) {
                esExcepcional = true;
                if (excepcionalEmpresa) {
                    if (demandado.getRazonSocial() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                } else {
                    if (demandado.getApellidos() == null || demandado.getNombres() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                }
                JsfUti.messageInfo(null, "Seleccionado correctamente", "");
                JsfUti.executeJS("PF('dlgNewClient').hide();");
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgSolicitante').hide();");
            }

            if (demandado!=null && demandado.getId()==null && !esExcepcional) {
                demandado=(CatEnte)acl.persist(demandado);
            }
            if (demandado!=null && !esExcepcional) {
                JsfUti.messageInfo(null, "Registro Grabado", "");
                JsfUti.executeJS("PF('dlgNewClient').hide();");
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }
    
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
            JsfUti.update("frmMain:tvSolicitud:panel1");
                    
        } catch (IOException e) {
            Logger.getLogger(RealizarInformeOT.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void existeCedula() {
        VerCedulaUtils validacion = new VerCedulaUtils();
        String identificacion = demandado.getCiRuc();
        if (demandado.getCiRuc() != null && demandado.getCiRuc().length() > 0) {
            demandado = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{identificacion});
            if (demandado == null) {
                demandado = new CatEnte();
                demandado.setCiRuc(identificacion);
                if (tipoEnte == 1 || this.ciRuc!=null) {
                    if (validacion.isCIValida(identificacion)) {
                        DatoSeguro ds = datoSeguroSeguro.getDatos(identificacion, false, 0);
                        demandado = datoSeguroSeguro.llenarEnte(ds, demandado, false);
                    }
                }
            }
        } else {
            demandado = new CatEnte();
        }
    }
    
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
    
    public void completarTarea(){
        if(obs!=null){
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            if(acl.persist(obs) != null){
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
    
    public void eliminarArchivo(Archivo file){
        archivos.remove(file);
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
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

    public CatEnte getDemandado() {
        return demandado;
    }

    public void setDemandado(CatEnte demandado) {
        this.demandado = demandado;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public Boolean getExcepcionalEmpresa() {
        return excepcionalEmpresa;
    }

    public void setExcepcionalEmpresa(Boolean excepcionalEmpresa) {
        this.excepcionalEmpresa = excepcionalEmpresa;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }
    
}
