/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.TareaWF;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class asignacionGrupal extends BpmManageBeanBaseRoot implements Serializable  {
    
    @javax.inject.Inject
    private Entitymanager aclServices;
    
    protected UserSession us1 = new UserSession();
    protected List<TareaWF> tareasWF = new ArrayList<>();
    protected Boolean esAdmin = false;
    protected Boolean userRegistro = false;
    private String usuario;
    protected List<TareaWF> tareasSeleccionadas;
    protected AclRol rolTecnico;
    protected AclUser tecnico;
    protected Boolean posibleAsignacion=false;
    private MsgFormatoNotificacion msg;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            usuario = session.getName_user();
            if(usuario != null){
                esAdmin = this.validateAdmin(session.getRoles());
                userRegistro = this.validarUsuariosRegistro(session.getDepts());
                tareasWF = this.getListaTareasPersonales(usuario,"asignarRevisor");
                rolTecnico=(AclRol)aclServices.find(AclRol.class, 69L);// ROL DE TECNICO DE EDIFICACIONES
                msg = (MsgFormatoNotificacion) aclServices.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{1L});
            }
        } catch (Exception e) {
            Logger.getLogger(asignacionGrupal.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void actualizarTramitePendiente() {
        tareasWF = this.getListaTareasPersonales(usuario,"asignarRevisor");
        JsfUti.update("formMain");
    }

    public Boolean validateAdmin(List<Long> list) {
        for (Long id : list) {
            if (id.equals(9L)) {
                return true;
            }
        }
        return false;
    }

    public Boolean validarUsuariosRegistro(List<Long> list){
        for (Long id : list) {
            if (id.equals(4L)) { // departamento Registro Propiedad 4
                return true;
            }
        }
        return false;
    }
    
    public void validarAsignacion(){
        posibleAsignacion= Boolean.FALSE;
        if (tareasSeleccionadas!=null && !tareasSeleccionadas.isEmpty() && tecnico!=null) {
            posibleAsignacion= Boolean.TRUE;
        }
    }
    
    public void realizarAsignacion(){
        try{
            if(this.tecnico!=null){
                for (TareaWF tareasSeleccionada : tareasSeleccionadas) {
                    HashMap<String, Object> params= new HashMap<>();
                    params.put("tecnico", this.tecnico.getUsuario());
                    params.put("revisor", this.tecnico.getUsuario());
                    if(this.tecnico.getEnte()!=null && this.tecnico.getEnte().getEnteCorreoCollection()!=null && !this.tecnico.getEnte().getEnteCorreoCollection().isEmpty() && this.tecnico.getEnte().getEnteCorreoCollection().get(0).getEmail()!=null){
                        params.put("to", this.tecnico.getEnte().getEnteCorreoCollection().get(0).getEmail());
                    }
                    params.put("from", SisVars.correo);
                    params.put("subject", "Revision tecnica de documentos Tramite: " + tareasSeleccionada.getTramite().getId());
                    params.put("message", msg.getHeader()+" Revision tecnica de documentos Tramite: " + tareasSeleccionada.getTramite().getId()+"<br/> "+"REVISION DE DOCUMENTOS"+"<br/> "+msg.getFooter());
                    this.completeTask(tareasSeleccionada.getTarea().getId(), params);
                }
                JsfUti.redirectFaces("/vistaprocesos/edificaciones/asignacionGrupal.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(asignacionGrupal.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public List<TareaWF> getTareasWF() {
        return tareasWF;
    }

    public void setTareasWF(List<TareaWF> tareasWF) {
        this.tareasWF = tareasWF;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public Boolean getUserRegistro() {
        return userRegistro;
    }

    public void setUserRegistro(Boolean userRegistro) {
        this.userRegistro = userRegistro;
    }

    public List<TareaWF> getTareasSeleccionadas() {
        return tareasSeleccionadas;
    }

    public void setTareasSeleccionadas(List<TareaWF> tareasSeleccionadas) {
        this.tareasSeleccionadas = tareasSeleccionadas;
    }

    public AclRol getRolTecnico() {
        return rolTecnico;
    }

    public void setRolTecnico(AclRol rolTecnico) {
        this.rolTecnico = rolTecnico;
    }

    public AclUser getTecnico() {
        return tecnico;
    }

    public void setTecnico(AclUser tecnico) {
        this.tecnico = tecnico;
    }

    public Boolean getPosibleAsignacion() {
        return posibleAsignacion;
    }

    public void setPosibleAsignacion(Boolean posibleAsignacion) {
        this.posibleAsignacion = posibleAsignacion;
    }
    
}
