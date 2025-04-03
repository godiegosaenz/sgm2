/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Departamentos implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager acl;

    private LazyDataModel<GeDepartamento> departamentosLazy;
    private List<GeDepartamento> listDep = new ArrayList<>();
    private GeDepartamento departamento;
    private GeDepartamento padre;
    private Boolean nuevo = true;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        departamento = new GeDepartamento();
        departamentosLazy = new BaseLazyDataModel<>(GeDepartamento.class, "nombre");
        listDep = acl.findAll(Querys.getGeDepartamentoByEstado, new String[]{"estado"}, new Object[]{true});
    }

    public void showDlgNewDep() {
        nuevo = true;
        departamento = new GeDepartamento();
        JsfUti.update("formNewDep");
        JsfUti.executeJS("PF('dlgNDep').show();");
    }

    public void showDlgEditDep(GeDepartamento dep) {
        nuevo = false;
        departamento = dep;
        if(dep.getPadre() != null){
            padre = (GeDepartamento)acl.find(GeDepartamento.class, dep.getPadre());
        }
        JsfUti.update("formNewDep");
        JsfUti.executeJS("PF('dlgNDep').show();");
    }

    public void guardarDepartamento() {
        try {
            if (departamento.getNombre() != null) {
                if (padre == null) {
                    departamento.setPadre(null);
                } else {
                    departamento.setPadre(new BigInteger(padre.getId().toString()));
                }
                if (nuevo) {
                    departamento = (GeDepartamento) acl.persist(departamento);
                    if (departamento.getId() != null) {
                        JsfUti.redirectFaces("/admin/users/departamentosList.xhtml");
                    } else {
                        JsfUti.messageError(null, Messages.problemaConexion, "");
                    }
                } else {
                    Boolean flag = acl.update(departamento);
                    if(flag){
                        JsfUti.redirectFaces("/admin/users/departamentosList.xhtml");
                    } else {
                        JsfUti.messageError(null, Messages.problemaConexion, "");
                    }
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Departamentos.class.getName()).log(Level.SEVERE, null, e);
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

    public GeDepartamento getPadre() {
        return padre;
    }

    public void setPadre(GeDepartamento padre) {
        this.padre = padre;
    }

    public LazyDataModel<GeDepartamento> getDepartamentosLazy() {
        return departamentosLazy;
    }

    public void setDepartamentosLazy(LazyDataModel<GeDepartamento> departamentosLazy) {
        this.departamentosLazy = departamentosLazy;
    }

    public List<GeDepartamento> getListDep() {
        return listDep;
    }

    public void setListDep(List<GeDepartamento> listDep) {
        this.listDep = listDep;
    }
}
