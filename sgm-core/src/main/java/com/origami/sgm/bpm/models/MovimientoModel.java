/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author origami
 */
public class MovimientoModel implements Serializable {

    public static final long serialVersionUID = 1L;

    private CatCanton codigoCan;
    private String numTomo;
    private RegActo acto;
    private RegEnteJudiciales enteJudicial;
    private Date fechaOto;
    private Integer folioInicio;
    private Integer folioFin;
    private Boolean ordJud;
    private String escritJuicProvResolucion;
    private String observacion;

    /*Listas nuevas que se van a agregar al movimiento editado*/
    private List<RegMovimientoCliente> movCliList = new ArrayList<>();
    private List<RegMovimientoCapital> movCapList = new ArrayList<>();
    private List<RegMovimientoRepresentante> movRepList = new ArrayList<>();
    private List<RegMovimientoSocios> movSocList = new ArrayList<>();

    /*Listas iniciales que se obtienen antes de editar el movimiento*/
    private List<RegMovimientoCliente> movCliListOld = new ArrayList<>();
    private List<RegMovimientoCapital> movCapListOld = new ArrayList<>();
    private List<RegMovimientoRepresentante> movRepListOld = new ArrayList<>();
    private List<RegMovimientoSocios> movSocListOld = new ArrayList<>();

    private List<RegMovimientoReferencia> movRefList = new ArrayList<>();
    private List<RegMovimientoReferencia> movRefListDel = new ArrayList<>();

    public MovimientoModel(RegMovimiento movimiento, List<RegMovimientoCliente> movCliList, List<RegMovimientoCapital> movCapList, List<RegMovimientoRepresentante> movRepList, List<RegMovimientoSocios> movSocList) {
        this.codigoCan = movimiento.getCodigoCan();
        this.numTomo = movimiento.getNumTomo();
        this.acto = movimiento.getActo();
        this.enteJudicial = movimiento.getEnteJudicial();
        this.fechaOto = movimiento.getFechaOto();
        this.folioInicio = movimiento.getFolioInicio();
        this.folioFin = movimiento.getFolioFin();
        this.ordJud = movimiento.getOrdJud();
        this.escritJuicProvResolucion = movimiento.getEscritJuicProvResolucion();
        this.observacion = movimiento.getObservacion();
        
        if(movCliList!=null && !movCliList.isEmpty()){
            RegMovimientoCliente cliente;
            for (RegMovimientoCliente movCliList1 : movCliList) {
                cliente= new RegMovimientoCliente();
                cliente.setId(movCliList1.getId());
                cliente.setCedula(movCliList1.getCedula());
                cliente.setEstado(movCliList1.getEstado());
                cliente.setNombres(movCliList1.getNombres());
                cliente.setPapel(movCliList1.getPapel());
                cliente.setEnteInterv(movCliList1.getEnteInterv());
                this.movCliListOld.add(cliente);
            }
        }
        
        if(movCapList!=null && !movCapList.isEmpty()){
            RegMovimientoCapital capital;
            for (RegMovimientoCapital movCapList1 : movCapList) {
                capital= new RegMovimientoCapital();
                
                this.movCapListOld.add(capital);
            }
        }
        
        if(movRepList!=null && !movRepList.isEmpty()){
            RegMovimientoRepresentante representante;
            for (RegMovimientoRepresentante movRepList1 : movRepList) {
                representante= new RegMovimientoRepresentante();
                
                this.movRepListOld.add(representante);
            }
        }
        
        if(movSocList!=null && !movSocList.isEmpty()){
            RegMovimientoSocios socio;
            for (RegMovimientoSocios movSocList1 : movSocList) {
                socio= new RegMovimientoSocios();
                
                this.movSocListOld.add(socio);
            }
        }
    }

    public CatCanton getCodigoCan() {
        return codigoCan;
    }

    public void setCodigoCan(CatCanton codigoCan) {
        this.codigoCan = codigoCan;
    }

    public String getNumTomo() {
        return numTomo;
    }

    public void setNumTomo(String numTomo) {
        this.numTomo = numTomo;
    }

    public RegActo getActo() {
        return acto;
    }

    public void setActo(RegActo acto) {
        this.acto = acto;
    }

    public RegEnteJudiciales getEnteJudicial() {
        return enteJudicial;
    }

    public void setEnteJudicial(RegEnteJudiciales enteJudicial) {
        this.enteJudicial = enteJudicial;
    }

    public Date getFechaOto() {
        return fechaOto;
    }

    public void setFechaOto(Date fechaOto) {
        this.fechaOto = fechaOto;
    }

    public Integer getFolioInicio() {
        return folioInicio;
    }

    public void setFolioInicio(Integer folioInicio) {
        this.folioInicio = folioInicio;
    }

    public Integer getFolioFin() {
        return folioFin;
    }

    public void setFolioFin(Integer folioFin) {
        this.folioFin = folioFin;
    }

    public Boolean getOrdJud() {
        return ordJud;
    }

    public void setOrdJud(Boolean ordJud) {
        this.ordJud = ordJud;
    }

    public String getEscritJuicProvResolucion() {
        return escritJuicProvResolucion;
    }

    public void setEscritJuicProvResolucion(String escritJuicProvResolucion) {
        this.escritJuicProvResolucion = escritJuicProvResolucion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<RegMovimientoCliente> getMovCliListOld() {
        return movCliListOld;
    }

    public void setMovCliListOld(List<RegMovimientoCliente> movCliListOld) {
        this.movCliListOld = movCliListOld;
    }

    public List<RegMovimientoCapital> getMovCapListOld() {
        return movCapListOld;
    }

    public void setMovCapListOld(List<RegMovimientoCapital> movCapListOld) {
        this.movCapListOld = movCapListOld;
    }

    public List<RegMovimientoRepresentante> getMovRepListOld() {
        return movRepListOld;
    }

    public void setMovRepListOld(List<RegMovimientoRepresentante> movRepListOld) {
        this.movRepListOld = movRepListOld;
    }

    public List<RegMovimientoSocios> getMovSocListOld() {
        return movSocListOld;
    }

    public void setMovSocListOld(List<RegMovimientoSocios> movSocListOld) {
        this.movSocListOld = movSocListOld;
    }

    public List<RegMovimientoCliente> getMovCliList() {
        return movCliList;
    }

    public void setMovCliList(List<RegMovimientoCliente> movCliList) {
        this.movCliList = movCliList;
    }

    public List<RegMovimientoCapital> getMovCapList() {
        return movCapList;
    }

    public void setMovCapList(List<RegMovimientoCapital> movCapList) {
        this.movCapList = movCapList;
    }

    public List<RegMovimientoRepresentante> getMovRepList() {
        return movRepList;
    }

    public void setMovRepList(List<RegMovimientoRepresentante> movRepList) {
        this.movRepList = movRepList;
    }

    public List<RegMovimientoSocios> getMovSocList() {
        return movSocList;
    }

    public void setMovSocList(List<RegMovimientoSocios> movSocList) {
        this.movSocList = movSocList;
    }

    public List<RegMovimientoReferencia> getMovRefList() {
        return movRefList;
    }

    public void setMovRefList(List<RegMovimientoReferencia> movRefList) {
        this.movRefList = movRefList;
    }

    public List<RegMovimientoReferencia> getMovRefListDel() {
        return movRefListDel;
    }

    public void setMovRefListDel(List<RegMovimientoReferencia> movRefListDel) {
        this.movRefListDel = movRefListDel;
    }

}
