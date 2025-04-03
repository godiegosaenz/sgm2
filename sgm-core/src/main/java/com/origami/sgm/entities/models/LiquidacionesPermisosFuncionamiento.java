/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.origami.sgm.entities.UtilsEnts;

/**
 *
 * @author Angel Navarro
 * @date 16/09/2016
 */
public class LiquidacionesPermisosFuncionamiento implements Serializable {

    private RenActivosLocalComercial activos;
    private RenBalanceLocalComercial balance;
    private RenLiquidacion liquidacion;
    private RenRubrosLiquidacion rubro;
    private List<RenRubrosLiquidacion> rubros;
    private List<RenDetLiquidacion> detalle;
    private RenTipoLiquidacion tiposLiquidacion;

    public LiquidacionesPermisosFuncionamiento(final RenTipoLiquidacion tiposLiquidacion) {
        liquidacion = new RenLiquidacion();
        liquidacion.setAnio(UtilsEnts.getAnio(new Date()));
        rubro = new RenRubrosLiquidacion();
        rubros = new ArrayList<>();
        detalle = new ArrayList<>();
        this.tiposLiquidacion = tiposLiquidacion;
    }

    public void inicarActivos() {
        activos = new RenActivosLocalComercial();
        activos.setAnioBalance(UtilsEnts.getAnio(new Date()) - 1);
        activos.setFechaIngreso(new Date());
    }

    public void inicarActivos(RenActivosLocalComercial act) {
        if (act != null) {
            activos = act;
            activos.setAnioBalance(UtilsEnts.getAnio(new Date()) - 1);
        } else {
            inicarActivos();
        }     
    }

    public void iniciarBalance() {
        balance = new RenBalanceLocalComercial();
        balance.setAnioBalance(UtilsEnts.getAnio(new Date()));
        balance.setFechaBalance(new Date());
    }

    public void iniciarBalance(RenBalanceLocalComercial bal) {
        if (bal != null) {
            balance = bal;
            balance.setAnioBalance(UtilsEnts.getAnio(new Date()));
        } else {
            iniciarBalance();
        }
    }

    public RenActivosLocalComercial getActivos() {
        return activos;
    }

    public void setActivos(RenActivosLocalComercial activos) {
        this.activos = activos;
    }

    public RenBalanceLocalComercial getBalance() {
        return balance;
    }

    public void setBalance(RenBalanceLocalComercial balance) {
        this.balance = balance;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RenRubrosLiquidacion getRubro() {
        return rubro;
    }

    public void setRubro(RenRubrosLiquidacion rubro) {
        this.rubro = rubro;
    }

    public List<RenRubrosLiquidacion> getRubros() {
        return rubros;
    }

    public void setRubros(List<RenRubrosLiquidacion> rubros) {
        this.rubros = rubros;
    }

    public List<RenDetLiquidacion> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<RenDetLiquidacion> detalle) {
        this.detalle = detalle;
    }

    public RenTipoLiquidacion getTiposLiquidacion() {
        return tiposLiquidacion;
    }

    public void setTiposLiquidacion(RenTipoLiquidacion tiposLiquidacion) {
        this.tiposLiquidacion = tiposLiquidacion;
    }

    public void agregarRubrosDetalle(List<RenRubrosLiquidacion> rubrosPorLiquidacion) {
        this.rubros = rubrosPorLiquidacion;
        for (RenRubrosLiquidacion temp : rubrosPorLiquidacion) {
            if (temp.getEstado()) {
                detalle.add(new RenDetLiquidacion(temp.getValor(), temp.getId(), temp.getDescripcion(), temp.getCodigoRubro()));
            }
        }

    }

}
