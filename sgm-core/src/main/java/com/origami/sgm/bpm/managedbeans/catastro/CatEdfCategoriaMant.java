/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.lazymodels.CatEdfCategPropLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.hibernate.Hibernate;
import org.jboss.logging.Logger;
import util.JsfUti;

/**
 *
 * @author Mariuly
 */
@Named
@ViewScoped
public class CatEdfCategoriaMant implements Serializable {

    public static final long serialVersionUID = 1l;

    @javax.inject.Inject
    private Entitymanager manager;
    protected CatEdfCategPropLazy categorias;
    protected CatEdfCategProp categoria;
    protected CatEdfProp propiedad;

    @PostConstruct
    public void init() {
        try {
            categorias = new CatEdfCategPropLazy();
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void editar(CatEdfCategProp editar) {
        try {
            this.categoria = editar;
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void editarPropiedad(CatEdfProp editar) {
        try {
            this.propiedad = editar;
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void nuevo() {
        categoria = new CatEdfCategProp();
    }

    public void nuevoPropiedad() {
        if (categoria == null) {
            JsfUti.messageError(null,"Error", "Debe Ingresar Categoria");
            return;
        }
        if (categoria.getId() == null) {
            JsfUti.messageError(null,"Error", "Debe Ingresar Categoria");
            return;
        }
        propiedad = new CatEdfProp();
        JsfUti.executeJS("PF('dlgGuardar').show()");
    }

    public void guardar() {
        try {
            categoria.setCatEdfPropCollection(null);
            this.manager.persist(categoria);
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void guardarPropiedad() {
        try {
            if (categoria == null) {
                return;
            }
            if (propiedad.getId() == null) {
                propiedad.setCategoria(categoria);
            } 
            propiedad=(CatEdfProp) this.manager.persist(propiedad);
            if (!categoria.getCatEdfPropCollection().contains(propiedad)) {
                categoria.getCatEdfPropCollection().add(propiedad);
            } 
            Hibernate.initialize(propiedad);
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void eliminar(CatEdfCategProp delete) {
        try {
            this.manager.delete(delete);
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public void eliminarPropiedad(CatEdfProp delete) {
        try {
            this.categoria.getCatEdfPropCollection().remove(delete);
            this.manager.delete(delete);
        } catch (Exception e) {
            Logger.getLogger(CatEdfCategoriaMant.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }

    public CatEdfCategPropLazy getCategorias() {
        return categorias;
    }

    public void setCategorias(CatEdfCategPropLazy categorias) {
        this.categorias = categorias;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        this.categoria = categoria;
    }

    public CatEdfProp getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(CatEdfProp propiedad) {
        this.propiedad = propiedad;
    }

}
