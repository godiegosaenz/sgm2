/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.lazymodels.AclRolesLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author origami-idea
 */
@Named
@ViewScoped
public class Roles implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager acl;

    private LazyDataModel<AclRol> rolesLazy;
    private List<GeDepartamento> listDep;
    private GeDepartamento departamento;
    private AclRol aclRol = new AclRol();
    private Boolean nuevo = true;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        rolesLazy = new AclRolesLazy();
        listDep = acl.findAll(Querys.getGeDepartamentoByEstado, new String[]{"estado"}, new Object[]{true});
    }

    public void showDlgNewRol() {
        nuevo = true;
        aclRol = new AclRol();
        departamento = null;
        JsfUti.update("formEditarRol");
        JsfUti.executeJS("PF('dlgEditRol').show();");
    }

    public void showDlgEditRol(AclRol r) {
        nuevo = false;
        aclRol = r;
        if (r.getDepartamento() != null) {
            departamento = r.getDepartamento();
        }
        JsfUti.update("formEditarRol");
        JsfUti.executeJS("PF('dlgEditRol').show();");
    }

    public void guardarRol() {
        try {
            if (aclRol.getNombre() != null) {
                if(departamento == null){
                    aclRol.setDepartamento(null);
                } else {
                    aclRol.setDepartamento(departamento);
                }
                if (nuevo) {
                    aclRol = (AclRol)acl.persist(aclRol);
                    if(aclRol.getId() != null){
                        JsfUti.redirectFaces("/admin/users/rolesList.xhtml");
                    } else {
                        JsfUti.messageError(null, Messages.problemaConexion, "");
                    }
                } else {
                    Boolean flag = acl.update(aclRol);
                    if(flag){
                        JsfUti.redirectFaces("/admin/users/rolesList.xhtml");
                    } else {
                        JsfUti.messageError(null, Messages.problemaConexion, "");
                    }
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Roles.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public AclRol getAclRol() {
        return aclRol;
    }

    public void setAclRol(AclRol aclRol) {
        this.aclRol = aclRol;
    }

    public List<GeDepartamento> getListDep() {
        return listDep;
    }

    public void setListDep(List<GeDepartamento> listDep) {
        this.listDep = listDep;
    }

    public LazyDataModel<AclRol> getRolesLazy() {
        return rolesLazy;
    }

    public void setRolesLazy(LazyDataModel<AclRol> rolesLazy) {
        this.rolesLazy = rolesLazy;
    }
}
