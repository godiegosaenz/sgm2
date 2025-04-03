/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.lazymodels.RegLibroLazy;
import com.origami.sgm.lazymodels.RegPapelLazy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class NuevoActo extends BpmManageBeanBaseRoot implements Serializable {

    protected String codigo;

    protected RegActo acto = new RegActo();
    protected RegLibroLazy listLibroLazy = new RegLibroLazy();
    protected RegPapelLazy listPapelLazy = new RegPapelLazy();
    protected List<RegLibro> listLibros = new ArrayList<>();
    protected List<RegCatPapel> listPapeles = new ArrayList<>();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    public void iniView() {
        try {
            if (codigo == null) {
                acto = new RegActo();
                listLibros = new ArrayList<>();
                listPapeles = new ArrayList<>();
            } else {
                acto = (RegActo) acl.find(RegActo.class, new Long(codigo));
                listLibros = (List<RegLibro>)acto.getRegLibroCollection();
                listPapeles = (List<RegCatPapel>)acto.getRegCatPapelCollection();
            }
        } catch (Exception e) {
            Logger.getLogger(ActosIngresados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgLibros(){
        listLibroLazy = new RegLibroLazy();
        JsfUti.update("selectLibro");
        JsfUti.executeJS("PF('dlgListaLazyLibros').show();");
    }
    
    public void showDlgPapeles(){
        listPapelLazy = new RegPapelLazy();
        JsfUti.update("selectPapel");
        JsfUti.executeJS("PF('dlgListaLazyPapeles').show();");
    }
    
    public void agregarLibro(RegLibro l) {
        if (l.getEstado()) {
            if (listLibros.contains(l)) {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            } else {
                listLibros.add(l);
                JsfUti.update("mainForm:dtLibrosAsociados");
            }
        } else {
            JsfUti.messageInfo(null, Messages.elementoInactivo, "");
        }
    }

    public void agregarPapel(RegCatPapel p) {
        if (p.getEstado()) {
            if (listPapeles.contains(p)) {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            } else {
                listPapeles.add(p);
                JsfUti.update("mainForm:dtPapelesAsociados");
            }
        } else {
            JsfUti.messageInfo(null, Messages.elementoInactivo, "");
        }
    }

    public void eliminarLibro(int index) {
        listLibros.remove(index);
    }

    public void eliminarPapel(int index) {
        listPapeles.remove(index);
    }

    public void guardarActo() {
        try {
            if(this.validar()){
                if(listLibros.isEmpty()){
                    acto.setRegLibroCollection(null);
                } else {
                    acto.setRegLibroCollection(listLibros);
                }
                if(listPapeles.isEmpty()){
                    acto.setRegCatPapelCollection(null);
                } else {
                    acto.setRegCatPapelCollection(listPapeles);
                }
                if(acto.getId() == null){
                    acto.setFechaCre(new Date());
                    acto.setUserCre(session.getName_user());
                    acl.persist(acto);
                } else {
                    acto.setFechaEdicion(new Date());
                    acto.setUserEdicion(session.getName_user());
                    acl.persist(acto);
                }
                JsfUti.redirectFaces("/admin/registro/actosIngresados.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(ActosIngresados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validar(){
        if(acto.getNombre() == null){
            JsfUti.messageInfo(null, "Debe llenar el campo Nombre.", "");
        } else if(acto.getAbreviatura() == null){
            JsfUti.messageInfo(null, "Debe llenar el campo Abreviatura.", "");
        } else if(acto.getAnexoUnoRegPropiedad() == null || acto.getAnexoDosMercantilContrato() == null || 
                acto.getAnexoTresMercatilSocNombramientos() == null){
            JsfUti.messageInfo(null, "Debe especificar los Reportes de la Dinardap que involucran al Acto.", "");
        } else {
            return true;
        }
        return false;
    }
    
    public RegLibroLazy getListLibroLazy() {
        return listLibroLazy;
    }

    public void setListLibroLazy(RegLibroLazy listLibroLazy) {
        this.listLibroLazy = listLibroLazy;
    }

    public List<RegLibro> getListLibros() {
        return listLibros;
    }

    public void setListLibros(List<RegLibro> listLibros) {
        this.listLibros = listLibros;
    }

    public List<RegCatPapel> getListPapeles() {
        return listPapeles;
    }

    public void setListPapeles(List<RegCatPapel> listPapeles) {
        this.listPapeles = listPapeles;
    }

    public RegPapelLazy getListPapelLazy() {
        return listPapelLazy;
    }

    public void setListPapelLazy(RegPapelLazy listPapelLazy) {
        this.listPapelLazy = listPapelLazy;
    }

    public RegActo getActo() {
        return acto;
    }

    public void setActo(RegActo acto) {
        this.acto = acto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
