/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author MauricioGuzmán
 */
@Named
@ViewScoped
public class DisparadorParametros implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;

    private LazyDataModel<ParametrosDisparador> parametrosDisparadorLazy;
    private BaseLazyDataModel<ParametrosDisparador> lisParametrosDisparadorLazy;
    private ParametrosDisparador nuevaParametrosDisparador = new ParametrosDisparador();
    private ParametrosDisparador editParametrosDisparador = new ParametrosDisparador();
    private List<GeTipoTramite> lisGeTipoTramite = new ArrayList<GeTipoTramite>();
    private List<AclUser> lisAclUser = new ArrayList<AclUser>();
    private GeDepartamento dep;
    private AclRol rol;

    public DisparadorParametros() {
    }

    public List<GeTipoTramite> getLisGeTipoTramite() {
        return lisGeTipoTramite;
    }

    public void setLisGeTipoTramite(List<GeTipoTramite> lisGeTipoTramite) {
        this.lisGeTipoTramite = lisGeTipoTramite;
    }

    public LazyDataModel<ParametrosDisparador> getParametrosDisparadorLazy() {
        return parametrosDisparadorLazy;
    }

    public void setParametrosDisparadorLazy(LazyDataModel<ParametrosDisparador> parametrosDisparadorLazy) {
        this.parametrosDisparadorLazy = parametrosDisparadorLazy;
    }

    public BaseLazyDataModel<ParametrosDisparador> getLisParametrosDisparadorLazy() {
        return lisParametrosDisparadorLazy;
    }

    public void setLisParametrosDisparadorLazy(BaseLazyDataModel<ParametrosDisparador> lisParametrosDisparadorLazy) {
        this.lisParametrosDisparadorLazy = lisParametrosDisparadorLazy;
    }

    public ParametrosDisparador getNuevaParametrosDisparador() {
        return nuevaParametrosDisparador;
    }

    public void setNuevaParametrosDisparador(ParametrosDisparador nuevaParametrosDisparador) {
        this.nuevaParametrosDisparador = nuevaParametrosDisparador;
    }

    public ParametrosDisparador getEditParametrosDisparador() {
        return editParametrosDisparador;
    }

    public void setEditParametrosDisparador(ParametrosDisparador editParametrosDisparador) {
        this.editParametrosDisparador = editParametrosDisparador;
    }

    public List<AclUser> getLisAclUser() {
        return lisAclUser;
    }

    public void setLisAclUser(List<AclUser> lisAclUser) {
        this.lisAclUser = lisAclUser;
    }

    public GeDepartamento getDep() {
        return dep;
    }

    public void setDep(GeDepartamento dep) {
        this.dep = dep;
    }

    public AclRol getRol() {
        return rol;
    }

    public void setRol(AclRol rol) {
        this.rol = rol;
    }

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    @PostConstruct
    protected void iniView() {
        parametrosDisparadorLazy = new BaseLazyDataModel<>(ParametrosDisparador.class);
        lisGeTipoTramite = aclServices.findAllOrdered(GeTipoTramite.class, new String[]{"descripcion"}, new Boolean[]{true});
        lisAclUser = aclServices.findAllOrdered(AclUser.class, new String[]{"usuario"}, new Boolean[]{true});
    }

    private void borrarVar() {
        dep = null;
        rol = null;
    }

    public void showDlgNew() {
        borrarVar();
        JsfUti.update("formNewParametrosDisparador");
        JsfUti.executeJS("PF('dlgAgrgParametrosDisparador').show();");
    }

    public void showDlgEdit(ParametrosDisparador c) {
        borrarVar();
        editParametrosDisparador = c;
        if (c.getResponsable() != null) {
            if (c.getResponsable().getAclRolCollection() != null) {
                for (AclRol r : c.getResponsable().getAclRolCollection()) {
                    if (r.getAclUserCollection().contains(c.getResponsable())) {
                        rol = (AclRol) aclServices.find(AclRol.class, r.getId());
                        dep = rol.getDepartamento();
                        break;
                    }
                }
            }
        }
        JsfUti.update("frmEditarParametrosDisparador");
        JsfUti.executeJS("PF('dlgEditParametrosDisparador').show();");
    }

    public void guardarParametrosDisparadorNuevo() {
//        if (nuevaParametrosDisparador.getInterfaz() == null) {
//            JsfUti.messageInfo(null, Messages.faltanCampos, "");
//        } else {
        nuevaParametrosDisparador.setEstado(Boolean.TRUE);
        nuevaParametrosDisparador.setFecCre(new Date());
        nuevaParametrosDisparador = (ParametrosDisparador) aclServices.persist(nuevaParametrosDisparador);
        if (nuevaParametrosDisparador.getId() != null) {
            JsfUti.executeJS("PF('dlgAgrgParametrosDisparador').hide();");
            JsfUti.update("@all");
            nuevaParametrosDisparador = new ParametrosDisparador();
        } else {
            JsfUti.messageInfo(null, Messages.error, "");
        }
//        }
    }

    public void guardarParametrosDisparadorEditado() {
//        if (editParametrosDisparador.getInterfaz() == null) {
//            JsfUti.messageInfo(null, Messages.faltanCampos, "");
//        } else {
        Boolean b = aclServices.update(editParametrosDisparador);
        if (b) {
            JsfUti.executeJS("PF('dlgEditParametrosDisparador').hide();");
            JsfUti.update("@all");
        } else {
            JsfUti.messageInfo(null, Messages.error, "");
        }
//        }
    }

    public List<GeDepartamento> getDepartamentos() {
        return aclServices.findAllOrdered(GeDepartamento.class, new String[]{"nombre"}, new Boolean[]{true});
    }

    public List<AclRol> getRolesDepartamento() {
        if (dep != null) {
            return aclServices.findAll(Querys.getListAclRolByDep, new String[]{"departamento"}, new Object[]{dep.getId()});
        } else {
            return null;
        }
    }

    public List<AclUser> getUsers() {
        if (rol != null) {
            return aclServices.findAll(Querys.getListAclUserByRol, new String[]{"idRol"}, new Object[]{rol.getId()});
        } else {
            return null;
        }
    }

}
