/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.Archivo;
import util.JsfUti;
import util.Messages;

/**
 *
 * ManagedBean Para subir documentos.
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class CargarDocumentos extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    private PropiedadHorizontalServices services;

    protected String tipoArchivos = "/(\\.|\\/)(gif|jpe?g|png|pdf|xlsx|docx|xlsm|dwg|shp|doc|xls|ppt|pptx|tif|txt)$/";
    protected String observ;
    protected HistoricoTramites ht;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            this.setTaskId(session.getTaskID());
            ht = new HistoricoTramites();
            ht = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
        } else {
            this.continuar();
        }
    }

    public void validar() {
        if (this.getFiles().isEmpty()) {
            JsfUti.messageInfo(null, Messages.faltaSubirDocumento, "");
            return;
        }
        observ = "";
        JsfUti.executeJS("PF('obs').show();");
    }

    public void completar() {
        if (observ == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        Observaciones o = services.guardarObservaciones(ht, session.getName_user(), observ, this.getTaskDataByTaskID().getTaskDefinitionKey());
        if (o != null) {
            HashMap<String, Object> paramt = new HashMap<>();
            List<AclUser> usersJ = new ArrayList<>();
            List<AclUser> usersC = new ArrayList<>();

            paramt.put("tramite", ht.getId());
            paramt.put("descripcion", "Propiedad Horizontal");
            paramt.put("prioridad", 50);

            paramt.put("carpeta", ht.getCarpetaRep());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<Archivo>());
            // Departamento Juridico 53
            GeDepartamento dJ = (GeDepartamento) acl.find(GeDepartamento.class, 53L);
            for (AclRol r : dJ.getAclRolCollection()) {
                if (r.getIsDirector()) {// Director Juridico 77
                    for (AclUser aclUser : r.getAclUserCollection()) {
                        if (!usersJ.contains(aclUser)) {
                            usersJ.add(aclUser);
                        }
                    }
                }
            }

            paramt.put("urlTec", "/faces/vistaprocesos/edificaciones/propiedadHorizontal/generarPropiedadHorizontal.xhtml");

            GeDepartamento dC = (GeDepartamento) acl.find(GeDepartamento.class, 2L);
            for (AclRol r : dC.getAclRolCollection()) {
                if (r.getIsDirector()) { // Director Catastro 68
                    for (AclUser aclUser : r.getAclUserCollection()) {
                        if (!usersC.contains(aclUser)) {
                            usersC.add(aclUser);
                        }
                    }
                }
            }
            if ((!usersC.isEmpty()) || (!usersJ.isEmpty())) {
                for (AclUser users : usersC) {
                    if (users.getSisEnabled() && users.getUserIsDirector()) {
                        paramt.put("directorCatastro", users.getUsuario());
                    }
                }
                for (AclUser usersj : usersJ) {
                    if (usersj.getSisEnabled() && usersj.getUserIsDirector()) {
                        paramt.put("directorJuridico", usersj.getUsuario());
                    }
                }
                this.completeTask(this.getTaskId(), paramt);
            } else {

            }
        }
        JsfUti.executeJS("PF('obs').hide();");
        this.continuar();
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public CargarDocumentos() {
    }

}
