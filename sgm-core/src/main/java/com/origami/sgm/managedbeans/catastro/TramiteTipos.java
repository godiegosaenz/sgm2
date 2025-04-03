/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.DisparadorTramites;
import com.origami.sgm.entities.GeCategoriasOnlinea;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoProcesos;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.lazymodels.GeTipoTramiteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
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
public class TramiteTipos implements Serializable {

    public static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TramiteTipos.class.getName());

    @javax.inject.Inject
    private Entitymanager aclServices;

    private LazyDataModel<GeTipoTramite> tipoTramiteLazy;
    private GeTipoTramiteLazy lisTipoTramiteLazy = new GeTipoTramiteLazy();
    private GeTipoTramite nuevoTipoTramite = new GeTipoTramite();
    private GeTipoTramite editTipoTramite = new GeTipoTramite();
    private List<GeCategoriasOnlinea> lisCategoriasEnLinea;
    private List<DisparadorTramites> lisDisparadorTramites;
    private List<GeTipoProcesos> lisTipoProcesos;
    private List<GeDepartamento> departamentos;
    private List<AclRol> lisAclRol;
    private List<AclUser> lisUsers;
    private String nombres;

    public TramiteTipos() {
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<GeTipoTramite> getTipoTramiteLazy() {
        return tipoTramiteLazy;
    }

    public void setTipoTramiteLazy(LazyDataModel<GeTipoTramite> tipoTramiteLazy) {
        this.tipoTramiteLazy = tipoTramiteLazy;
    }

    public GeTipoTramiteLazy getLisTipoTramiteLazy() {
        return lisTipoTramiteLazy;
    }

    public void setLisTipoTramiteLazy(GeTipoTramiteLazy lisTipoTramiteLazy) {
        this.lisTipoTramiteLazy = lisTipoTramiteLazy;
    }

    public GeTipoTramite getNuevoTipoTramite() {
        return nuevoTipoTramite;
    }

    public void setNuevoTipoTramite(GeTipoTramite nuevoTipoTramite) {
        this.nuevoTipoTramite = nuevoTipoTramite;
    }

    public GeTipoTramite getEditTipoTramite() {
        return editTipoTramite;
    }

    public void setEditTipoTramite(GeTipoTramite editTipoTramite) {
        this.editTipoTramite = editTipoTramite;
    }

    public List<GeCategoriasOnlinea> getLisCategoriasEnLinea() {
        return lisCategoriasEnLinea;
    }

    public void setLisCategoriasEnLinea(List<GeCategoriasOnlinea> lisCategoriasEnLinea) {
        this.lisCategoriasEnLinea = lisCategoriasEnLinea;
    }

    public List<DisparadorTramites> getLisDisparadorTramites() {
        return lisDisparadorTramites;
    }

    public void setLisDisparadorTramites(List<DisparadorTramites> lisDisparadorTramites) {
        this.lisDisparadorTramites = lisDisparadorTramites;
    }

    public List<GeTipoProcesos> getLisTipoProcesos() {
        return lisTipoProcesos;
    }

    public void setLisTipoProcesos(List<GeTipoProcesos> lisTipoProcesos) {
        this.lisTipoProcesos = lisTipoProcesos;
    }

    public List<AclRol> getLisAclRol() {
        return lisAclRol;
    }

    public void setLisAclRol(List<AclRol> lisAclRol) {
        this.lisAclRol = lisAclRol;
    }

    @PostConstruct
    public void iniView() {
        try {
            departamentos = aclServices.findAllOrdered(GeDepartamento.class, new String[]{"nombre"}, new Boolean[]{true});
            tipoTramiteLazy = new GeTipoTramiteLazy();
            lisCategoriasEnLinea = aclServices.findAllOrdered(GeCategoriasOnlinea.class, new String[]{"nombre"}, new Boolean[]{true});
            lisDisparadorTramites = aclServices.findAllOrdered(DisparadorTramites.class, new String[]{"descripcion"}, new Boolean[]{true});
            lisTipoProcesos = aclServices.findAllOrdered(GeTipoProcesos.class, new String[]{"descripcion"}, new Boolean[]{true});
            lisAclRol = aclServices.findAllOrdered(AclRol.class, new String[]{"nombre"}, new Boolean[]{true});
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public SelectItem[] getLisCategoriasEnLineaSlt() {
        int cantRegis = lisCategoriasEnLinea.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(lisCategoriasEnLinea.get(i).getNombre(), lisCategoriasEnLinea.get(i).getNombre());
        }
        return options;
    }

    public SelectItem[] getLisDisparadorTramitesSlt() {
        int cantRegis = lisDisparadorTramites.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(lisDisparadorTramites.get(i).getDescripcion(), lisDisparadorTramites.get(i).getDescripcion());
        }
        return options;
    }

    public SelectItem[] getLisTipoProcesosSlt() {
        int cantRegis = lisTipoProcesos.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(lisTipoProcesos.get(i).getDescripcion(), lisTipoProcesos.get(i).getDescripcion());
        }
        return options;
    }

    public void showDlgNew() {
        nuevoTipoTramite = new GeTipoTramite();
        JsfUti.update("formNewTipoTramite");
        JsfUti.executeJS("PF('dlgAgrgTipoTramite').show();");
    }

    public void showDlgEdit(GeTipoTramite c) {
        editTipoTramite = c;
        actualizarRoles(2);
        actualizarUsuario(2);
        JsfUti.executeJS("PF('dlgEditTipoTramite').show();");
        JsfUti.update("frmEditarTipoTramite");
    }

    public void guardarTipoTramiteNuevo() {
        if (nuevoTipoTramite.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevoTipoTramite.setEstado(Boolean.TRUE);
            nuevoTipoTramite.setFlagOne(Boolean.FALSE);
            nuevoTipoTramite = (GeTipoTramite) aclServices.persist(nuevoTipoTramite);
            if (nuevoTipoTramite.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/tramiteTipos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void guardarTipoTramiteEditado() {
        Boolean b = aclServices.update(editTipoTramite);
        if (b) {
            JsfUti.redirectFaces("/admin/catastro/tramiteTipos.xhtml");
        } else {
            JsfUti.messageInfo(null, Messages.error, "");
        }
    }

    public void actualizarRoles(int x) {
        if (x == 1) {
            if (nuevoTipoTramite.getDepartamento() != null) {
                lisAclRol = getRolesDepartamento(nuevoTipoTramite.getDepartamento().getId());
            }
        } else {
            if (editTipoTramite.getDepartamento() != null) {
                lisAclRol = getRolesDepartamento(editTipoTramite.getDepartamento().getId());
            }
        }
    }

    public void actualizarUsuario(int x) {

        List<Long> ids = new ArrayList<>();
        if (x == 1) {
            if (nuevoTipoTramite.getRol() != null) {
                ids.add(nuevoTipoTramite.getRol().getId());
            }
        } else {
            if (editTipoTramite.getRol() != null) {
                ids.add(editTipoTramite.getRol().getId());
            }
        }
        if (ids.size() > 0) {
            lisUsers = getUsers(ids);
        }
    }

    public List<GeDepartamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<GeDepartamento> departamentos) {
        this.departamentos = departamentos;
    }

    private List<AclRol> getRolesDepartamento(Long id) {
        return aclServices.findAll(Querys.getListAclRolByDep, new String[]{"departamento"}, new Object[]{id});
    }

    public List<AclUser> getLisUsers() {
        return lisUsers;
    }

    public void setLisUsers(List<AclUser> lisUsers) {
        this.lisUsers = lisUsers;
    }

    public List<AclUser> getUsers(List<Long> ids) {
        return aclServices.getTecnicosByRol(ids);
    }

}
