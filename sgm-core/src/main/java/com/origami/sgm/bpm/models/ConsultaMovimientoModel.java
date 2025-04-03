/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anyelo
 */
public class ConsultaMovimientoModel implements Serializable{

    public static final Long serialVersionUID = 1L; 
    
    private RegMovimiento movimiento;
    private RegFicha ficha;
    private List<RegMovimiento> movimientos;
    private List<RegFicha> fichas;
    private List<RegMovimientoCapital> listMovCap;
    private List<RegMovimientoCliente> listMovCli,listMovCliSelect;
    private List<RegMovimientoFicha> listMovFic;
    private List<RegMovimientoReferencia> listMovRef;
    private List<RegMovimientoRepresentante> lisMovRep;
    private List<RegMovimientoSocios> listMovSoc;

    public ConsultaMovimientoModel() {
        this.movimiento = new RegMovimiento();
        this.ficha = new RegFicha();
        this.movimientos = new ArrayList<>();
        this.fichas = new ArrayList<>();
        this.listMovCap = new ArrayList<>();
        this.listMovCli = new ArrayList<>();
        this.listMovCliSelect = new ArrayList<>();
        this.listMovFic = new ArrayList<>();
        this.listMovRef = new ArrayList<>();
        this.lisMovRep = new ArrayList<>();
        this.listMovSoc = new ArrayList<>();
    }

    public ConsultaMovimientoModel(RegMovimiento movimiento, List<RegMovimientoCapital> listMovCap,
            List<RegMovimientoCliente> listMovCli, List<RegMovimientoFicha> listMovFic,
            List<RegMovimientoReferencia> listMovRef, List<RegMovimientoRepresentante> lisMovRep,
            List<RegMovimientoSocios> listMovSoc) {
        this.movimiento = movimiento;
        this.listMovCap = listMovCap;
        this.listMovCli = listMovCli;
        this.listMovFic = listMovFic;
        this.listMovRef = listMovRef;
        this.lisMovRep = lisMovRep;
        this.listMovSoc = listMovSoc;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
    }

    public List<RegMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<RegMovimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public List<RegFicha> getFichas() {
        return fichas;
    }

    public void setFichas(List<RegFicha> fichas) {
        this.fichas = fichas;
    }

    public List<RegMovimientoCapital> getListMovCap() {
        return listMovCap;
    }

    public void setListMovCap(List<RegMovimientoCapital> listMovCap) {
        this.listMovCap = listMovCap;
    }

    public List<RegMovimientoCliente> getListMovCli() {
        return listMovCli;
    }

    public void setListMovCli(List<RegMovimientoCliente> listMovCli) {
        this.listMovCli = listMovCli;
    }

    public List<RegMovimientoCliente> getListMovCliSelect() {
        return listMovCliSelect;
    }

    public void setListMovCliSelect(List<RegMovimientoCliente> listMovCliSelect) {
        this.listMovCliSelect = listMovCliSelect;
    }
    
    public List<RegMovimientoFicha> getListMovFic() {
        return listMovFic;
    }

    public void setListMovFic(List<RegMovimientoFicha> listMovFic) {
        this.listMovFic = listMovFic;
    }

    public List<RegMovimientoReferencia> getListMovRef() {
        return listMovRef;
    }

    public void setListMovRef(List<RegMovimientoReferencia> listMovRef) {
        this.listMovRef = listMovRef;
    }

    public List<RegMovimientoRepresentante> getLisMovRep() {
        return lisMovRep;
    }

    public void setLisMovRep(List<RegMovimientoRepresentante> lisMovRep) {
        this.lisMovRep = lisMovRep;
    }

    public List<RegMovimientoSocios> getListMovSoc() {
        return listMovSoc;
    }

    public void setListMovSoc(List<RegMovimientoSocios> listMovSoc) {
        this.listMovSoc = listMovSoc;
    }

}
