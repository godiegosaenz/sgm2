/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenTurismoServicios;
import com.origami.sgm.entities.models.Tipo;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 * @date 20/07/2016
 */
@Named
@ViewScoped
public class TurismoServicios implements Serializable {

    private BaseLazyDataModel<RenTurismoServicios> lazy;
    private RenTurismoServicios servicios;
    private Boolean esNuevo;

    @javax.inject.Inject
    private com.origami.sgm.services.ejbs.ServiceLists lists;

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    UserSession seccion;

    @PostConstruct
    protected void initView() {
        servicios = new RenTurismoServicios();
        lazy = new BaseLazyDataModel<>(RenTurismoServicios.class, "descripcion", "ASC");
    }

    public List<Tipo> getTipos() {
        List<Tipo> tipos = new ArrayList<>();
        tipos.add(new Tipo(BigInteger.valueOf(1), "SERVICIOS GENERALES"));
        tipos.add(new Tipo(BigInteger.valueOf(2), "TERRESTRE"));
        tipos.add(new Tipo(BigInteger.valueOf(3), "AEREO"));
        tipos.add(new Tipo(BigInteger.valueOf(4), "MARITIMO"));
        tipos.add(new Tipo(BigInteger.valueOf(5), "ESPECIALIDAD SERVICIOS ALIMENTICIOS"));
        tipos.add(new Tipo(BigInteger.valueOf(6), "HOTELES"));
        return tipos;
    }

    public String getTipoDescripcion(BigInteger tipo) {
        switch (tipo.intValue()) {
            case 1:
                return "SERVICIOS GENERALES";
            case 2:
                return "TERRESTRE";
            case 3:
                return "AEREO";

            case 4:
                return "MARITIMO";

            case 5:
                return "ESPECIALIDADES SERVICIOS ALIMENTICIOS";

            default:
                return "HOTELES";
        }
    }

    public void nuevo() {
        servicios = new RenTurismoServicios();
        esNuevo = true;
        showHideDlg(esNuevo);
    }

    public void editar(RenTurismoServicios s) {
        servicios = s;
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

    public List<RenLocalCategoria> getCategorias() {
        return lists.getLocalCategorias();
    }

    public List<RenActividadComercial> getActividades() {
        return lists.getActividadComercials();
    }

    public void guardar() {
        if (servicios.getDescripcion() == null) {
            JsfUti.messageError(null, "Error", "Debe ingresar la descripcion");
            return;
        }
        if (servicios.getTipo() == null) {
            JsfUti.messageError(null, "Error", "Debe ingresar el tipo de servicios");
            return;
        }
        servicios.setUsuarioIngreso(seccion.getName_user());
        servicios = services.guardarTurismoServicios(servicios);
        if (servicios != null) {
            JsfUti.messageInfo(null, "Información", "Registro guardado correctamente");
        } else {
            JsfUti.messageError(null, "Error", "Ocurrio un error al intentar guardar");
        }
        showHideDlg(false);
    }

    public Boolean getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(Boolean esNuevo) {
        this.esNuevo = esNuevo;
    }

    public BaseLazyDataModel<RenTurismoServicios> getLazy() {
        return lazy;
    }

    public void setLazy(BaseLazyDataModel<RenTurismoServicios> lazy) {
        this.lazy = lazy;
    }

    public RenTurismoServicios getServicios() {
        return servicios;
    }

    public void setServicios(RenTurismoServicios servicios) {
        this.servicios = servicios;
    }

    public TurismoServicios() {
    }

}
