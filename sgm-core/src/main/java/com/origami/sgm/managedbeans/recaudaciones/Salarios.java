/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author andysanchez
 */
@Named(value = "salarios")
@ViewScoped
public class Salarios implements Serializable {

    public static final Long serialVersionUID = 1L;
    protected BaseLazyDataModel<CtlgSalario> salarios;
    protected CtlgSalario salario;
    @javax.inject.Inject
    private Entitymanager manager;
    private Map<String, Object> paramt;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    @PostConstruct
    public void initView() {
        try {
            salarios = new BaseLazyDataModel<>(CtlgSalario.class, "id", "DESC");
        } catch (Exception e) {
            Logger.getLogger(Intereses.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public boolean validarSalario(CtlgSalario salario) {

        if (salario != null) {
            if (salario.getValor() == null || salario.getAnio() == null) {
                JsfUti.messageInfo(null, "Información", "Los campos son Obligatorios");
                return false;
            }
            if (salario.getId() == null) {
                paramt = new HashMap<>();
                paramt.put("valor", salario.getValor());
                paramt.put("anio", salario.getAnio());
                if (manager.findObjectByParameter(CtlgSalario.class, paramt) != null) {
                    JsfUti.messageInfo(null, "Información", "Registro ya existe");
                    return false;
                }
            }

        }
        return true;
    }

    public void seleccionarSalario(CtlgSalario i) {
        try {
            if (i == null) {
                this.salario = new CtlgSalario();
            } else {
                this.salario = i;
            }
        } catch (Exception e) {
            Logger.getLogger(Salarios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarSalario() {
        try {
            if (validarSalario(this.salario)) {
                this.salario = recaudacion.grabraSalario(this.salario);
                if (this.salario != null) {
                    JsfUti.messageInfo(null, "Información", "Registro Grabado Exitosamente");
                } else {
                    JsfUti.messageError(null, "Error", "No se pudo grabar el Registro");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Intereses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public BaseLazyDataModel<CtlgSalario> getSalarios() {
        return salarios;
    }

    public void setSalarios(BaseLazyDataModel<CtlgSalario> salarios) {
        this.salarios = salarios;
    }

    public CtlgSalario getSalario() {
        return salario;
    }

    public void setSalario(CtlgSalario salario) {
        this.salario = salario;
    }
    
}
