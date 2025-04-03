/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.TaskUserModel;
//import com.origami.sgm.bpm.models.TaskUserModelComparator;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class TareasUsuarioView implements Serializable {

    public static final Long serialVerisonUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    protected BpmBaseEngine engine;
    
    @javax.inject.Inject
    private Entitymanager services;
        
    @Inject
    private ServletSession ss;
    
    private PdfReporte reporte;
    private Date fechaIn, fechaFin;
    private List<TaskUserModel> list;
    private List<AclUser> usuarios;
    private Boolean mostrarData = false;
    
    @PostConstruct
    public void initView(){
        if(uSession.esLogueado()){
            List<Long> roles = new ArrayList();
            list = new ArrayList<>();
            
            reporte = new PdfReporte();
            
            if(uSession.getEsDirector()){
                for(Long t : uSession.getDepts()){
                    roles.addAll((List<Long>)services.findAll(Querys.getIdAclRolByDept, new String[]{"dept"}, new Object[]{t}));
                }
                usuarios = services.getTecnicosByRol(roles);
                mostrarData = true;
            }
        }
    }
    
    public void reporteTareas(String usuario){
        try{
            if(fechaFin != null && fechaIn != null){
                if(!fechaIn.before(fechaFin)){
                    JsfUti.messageInfo(null, "Info", "La fecha de inicio debe ser menor a la fecha de fin");
                    return;
                }
                if(usuario == null || usuario == "")
                    usuario = uSession.getName_user();
                
                AclUser user = (AclUser)services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{usuario});
                
                ss.instanciarParametros();
                ss.setTieneDatasource(Boolean.FALSE);
                TaskUserModel t;
                HistoricoTramites ht;
                String taskId;
                HistoricVariableInstance var;
                HistoricTaskInstanceQueryImpl historic = (HistoricTaskInstanceQueryImpl)engine.getProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().taskAssignee(usuario).finished();
                historic.taskCompletedBefore(fechaFin).taskCompletedAfter(fechaIn);
                for(HistoricTaskInstance temp : historic.list()){
                    var = null;
                    t = new TaskUserModel();
                    t.setFechaFin(temp.getEndTime());
                    t.setNombreTramite(temp.getDescription());
                    
                    if(temp.getProcessInstanceId() != null)
                        var = engine.getProcessEngine().getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(temp.getProcessInstanceId()).variableName("tramite").singleResult();
                    
                    t.setNumTramite(var.getValue() == null ? null : Long.parseLong(var.getValue().toString()));
                    
                    t.setNombreTarea(temp.getName());
                    if(t.getNumTramite() != null){
                        t.setEstadoTramite(services.find(Querys.getEstadoHistoricoTramiteById, new String[]{"id"}, new Object[]{t.getNumTramite()}).toString());
                    }
                    list.add(t);
                }
                
                //Collections.sort(list, new TaskUserModelComparator());
                ss.setDataSource(list);
                ss.agregarParametro("username", usuario);
                if(user.getEnte() != null)
                    ss.agregarParametro("nombres", user.getEnte().getNombreCompleto());
                ss.agregarParametro("roles", user.getRoles());
                ss.agregarParametro("departamento", user.getDepartamentos());
                ss.agregarParametro("desde", fechaIn);
                ss.agregarParametro("hasta", fechaFin);
                ss.agregarParametro("logo1", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("logo2", JsfUti.getRealPath("/css/smb/newlogomunicipal.png"));
                ss.setNombreReporte("reporte_tareas_por_usuario");
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }else{
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el rango de fechas");
            }
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

    public Date getFechaIn() {
        return fechaIn;
    }

    public void setFechaIn(Date fechaIn) {
        this.fechaIn = fechaIn;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<TaskUserModel> getList() {
        return list;
    }

    public void setList(List<TaskUserModel> list) {
        this.list = list;
    }

    public Boolean getMostrarData() {
        return mostrarData;
    }

    public void setMostrarData(Boolean mostrarData) {
        this.mostrarData = mostrarData;
    }

    public List<AclUser> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<AclUser> usuarios) {
        this.usuarios = usuarios;
    }
    
}
