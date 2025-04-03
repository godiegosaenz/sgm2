/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.lazymodels.RegLibroLazy;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class Libros extends BpmManageBeanBaseRoot implements Serializable {

    private RegLibroLazy librosLazy;
    private RegLibro libro = new RegLibro();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            librosLazy = new RegLibroLazy();
        } catch (Exception e) {
            Logger.getLogger(Libros.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void showDlgNew() {
        libro = new RegLibro();
        JsfUti.update("formLibros");
        JsfUti.executeJS("PF('dlgLibro').show();");
    }

    public void showDlgEdit(RegLibro book) {
        libro = book;
        JsfUti.update("formLibros");
        JsfUti.executeJS("PF('dlgLibro').show();");
    }

    public void guardar() {
        try {
            if (libro.getNombre() != null) {
                if (libro.getAnexoUnoRegPropiedad() == null || libro.getAnexoTresMercatilSocNombramientos() == null) {
                    JsfUti.messageError(null, "Debe seleccionar al tipo de anexo que pertenecen las inscripciones de este Libro.", "");
                } else {
                    if (libro.getId() == null) {
                        libro.setNombreCarpeta(libro.getNombre().toUpperCase());
                        libro.setUserCre(session.getName_user());
                        libro.setFechaCre(new Date());
                        acl.persist(libro);
                    } else {
                        libro.setNombreCarpeta(libro.getNombre().toUpperCase());
                        libro.setUserEdicion(session.getName_user());
                        libro.setFechaEdicion(new Date());
                        acl.persist(libro);
                    }
                    JsfUti.redirectFaces("/admin/registro/libros.xhtml");
                }
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Libros.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegLibroLazy getLibrosLazy() {
        return librosLazy;
    }

    public void setLibrosLazy(RegLibroLazy librosLazy) {
        this.librosLazy = librosLazy;
    }

    public RegLibro getLibro() {
        return libro;
    }

    public void setLibro(RegLibro libro) {
        this.libro = libro;
    }

}
