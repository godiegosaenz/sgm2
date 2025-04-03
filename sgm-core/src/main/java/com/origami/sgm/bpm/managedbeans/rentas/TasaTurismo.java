/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.models.Tipo;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.ejbs.ServiceLists;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @date 20/07/2016
 */
@Named
@ViewScoped
public class TasaTurismo implements Serializable {

    private BaseLazyDataModel<RenTasaTurismo> lazy;
    private RenTasaTurismo tasa;
    private Boolean esNuevo;
    
    @javax.inject.Inject
    private ServiceLists lists;

    @javax.inject.Inject
    private RentasServices services;
    
    @PostConstruct
    protected void initView() {
        tasa = new RenTasaTurismo();
        lazy = new BaseLazyDataModel<>(RenTasaTurismo.class, "descripcion", "ASC");
    }

    public List<Tipo> getTipos() {
        List<Tipo> tipos = new ArrayList<>();
        tipos.add(new Tipo(1, "POR HABITACION"));
        tipos.add(new Tipo(2, "POR PLAZA"));
        tipos.add(new Tipo(3, "POR MESAS"));
        tipos.add(new Tipo(4, "VALOR FIJO"));
        return tipos;
    }

    public String getTipoDescripcion(Integer tipo) {
        switch (tipo) {
            case 1:
                return "POR HABITACION";
            case 2:
                return "POR PLAZA";
            case 3:
                return "POR MESAS";
            default:
                return "VALOR FIJO";
        }
    }

    public void nuevo() {
        tasa = new RenTasaTurismo();
        esNuevo = true;
        showHideDlg(esNuevo);
    }

    public void editar(RenTasaTurismo t) {
        tasa = t;
        esNuevo = false;
        showHideDlg(true);
    }

    private void showHideDlg(Boolean p) {
        if (p) {
            JsfUti.executeJS("PF('dlgTasa').show()");
            JsfUti.update("frmtt");
        } else {
            JsfUti.executeJS("PF('dlgTasa').hide()");
            JsfUti.update("frmTasaTur");
        }
    }

    public List<RenLocalCategoria> getCategorias(){
        return lists.getLocalCategorias();
    }
    
    public List<RenActividadComercial> getActividades(){
        return lists.getActividadComercials();
    }
    
    public void guardar() {
        if(tasa.getDescripcion() == null || tasa.getDescripcion().trim().length() == 0){
            JsfUti.messageError(null, "Error", "Debe ingresar la Descripción");
            return;
        }
        if(tasa.getCategoria() == null ){
            JsfUti.messageError(null, "Error", "Debe ingresar la Categoria");
            return;
        }
        if(tasa.getTipo() == null ){
            JsfUti.messageError(null, "Error", "Debe ingresar el tipo de valor");
            return;
        }
        if(tasa.getValor()== null){
            JsfUti.messageError(null, "Error", "Debe ingresar el Valor");
            return;
        }
        if (esNuevo) {
            tasa.setEstado(true);
            tasa.setFechaIngreso(new Date());
        } 
        tasa = services.guardarTasaTurismo(tasa);
        if(tasa != null){
            JsfUti.messageInfo(null, "Información", "Registro guardado correctamente");
        }else{
            JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar");
        }
        showHideDlg(false);
    }

    public BaseLazyDataModel<RenTasaTurismo> getLazy() {
        return lazy;
    }

    public void setLazy(BaseLazyDataModel<RenTasaTurismo> lazy) {
        this.lazy = lazy;
    }

    public RenTasaTurismo getTasa() {
        return tasa;
    }

    public void setTasa(RenTasaTurismo tasa) {
        this.tasa = tasa;
    }

    public Boolean getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(Boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public TasaTurismo() {
    }

}
