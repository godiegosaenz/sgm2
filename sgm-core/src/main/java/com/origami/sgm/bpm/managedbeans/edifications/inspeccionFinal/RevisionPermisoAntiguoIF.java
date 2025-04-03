/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion.GenerarLiquidacionPC;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PePermiso;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class RevisionPermisoAntiguoIF extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private ServletSession servletSession;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private HistoricoTramites ht;
    private PePermiso permiso;
    private Boolean confirm;
    private List<PePermiso> permisosList; // Hago una lista para mostrar el datatable, pero en si solo va a mostrar un permiso
    
    @PostConstruct
    public void init(){
        if (uSession != null && uSession.getTaskID() != null ) {
            this.setTaskId(uSession.getTaskID());
            ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            if(ht == null){
                JsfUti.messageError(null, "Error", "Error al encontrar el trámite.");
                return;
            }
            obs = new Observaciones();
            permiso = (PePermiso)services.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{ht.getIdTramite()});
            if(permiso!=null){
                permisosList = new ArrayList();
                permisosList.add(permiso);
            }else{
                JsfUti.messageError(null, "Error", "Error al encontrar el permiso.");
                return;
            }
        }
    }
    
    public void imprimirPermiso(PePermiso p){
        try {
            Calendar cl = Calendar.getInstance();

            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            GeTipoTramite tipoTramite = ht.getTipoTramite();
            
            AclUser firmaDirector = permisoService.getAclUserByUser(tipoTramite.getUserDireccion());
            AclUser firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);
            
            servletSession.instanciarParametros();
            servletSession.agregarParametro("permiso", p.getId());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
            servletSession.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
            servletSession.agregarParametro("firmaTecni", path + "css/firmas/" + new AclUser(uSession.getUserId()).getRutaImagen() + ".jpg");
            servletSession.agregarParametro("idUser", p.getUsuarioCreador().getId());
            servletSession.setNombreReporte("LiquidacionTasasPermiso");
            servletSession.setTieneDatasource(true);
            //paramsActiviti.put("idReporte", vd.getId());
            servletSession.setNombreSubCarpeta("permisoConstruccion");
            
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        
        
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }
    
    public void mostrarObs(Boolean b){
        confirm = b;
        JsfUti.executeJS("PF('dlgObs').show()");
    }
    
    public void completarTarea(){
        try{
            paramsActiviti = new HashMap<>();
            if(confirm){
                permiso.setEstado("A");
                services.update(permiso);
            }//else{
             //   paramsActiviti.put("idPermiso", permiso.getId());
            //}
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            services.persist(obs);
            
            paramsActiviti.put("aprobado", confirm);
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());

            this.completeTask(this.getTaskId(), paramsActiviti);
            this.continuar();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public HashMap<String, Object> getParamsActiviti() {
        return paramsActiviti;
    }

    public void setParamsActiviti(HashMap<String, Object> paramsActiviti) {
        this.paramsActiviti = paramsActiviti;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public PePermiso getPermiso() {
        return permiso;
    }

    public void setPermiso(PePermiso permiso) {
        this.permiso = permiso;
    }

    public List<PePermiso> getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(List<PePermiso> permisosList) {
        this.permisosList = permisosList;
    }

    
}
