/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoFicha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Angel Navarro
 */
public class InscripcionNuevaModel implements Serializable {

    protected Long numFicha;
    protected Boolean esPersona = true;
    protected Boolean ocultarBtnGuardar = false;
    protected Boolean habilitarBtnReportes = true;
    protected Boolean habilitarCompletarTarea = true;
    protected Boolean habilitarRedirectBandTarea = true;

    protected String tipoPredio;
    protected String cedula, nombres;
    protected int contReportImpresos = 0;

    protected RegEnteInterviniente nuevoInterviniente;
    protected RegMovimientoCapital movimientoCapitalNuevo = new RegMovimientoCapital();
    protected RegMovimientoRepresentante movimientoRepresentante = new RegMovimientoRepresentante();
    protected RegMovimientoSocios movimientoSocios = new RegMovimientoSocios();
    protected RegFicha fichaSeleccionada = new RegFicha();
    protected CtlgCargo cargoSel;
    protected CtlgItem itemSelec;

    protected List<RegMovimientoCliente> movimientoClienteList = new ArrayList<>();
    protected List<RegMovimientoFicha> movimientoFichaList = new ArrayList<>();
    protected List<RegMovimientoCapital> movimientoCapitalList = new ArrayList<>();
    protected List<RegMovimientoRepresentante> movimientoRepresentanteList = new ArrayList<>();
    protected List<RegMovimientoSocios> movimientoSocioList = new ArrayList<>();
    protected Collection<RegCatPapel> papelList;
    protected RegTipoFicha tipoFicha;

    protected List<RegMovimiento> movimientos = new ArrayList<>();
    protected List<RegMovimiento> listadoMovimientosRef = new ArrayList<>();
    protected List<RegMovimiento> movimientosReff = new ArrayList<>();
    protected List<RegMovimientoReferencia> listMovsRef = new ArrayList<>();
    protected List<RegMovimientoReferencia> listMovsRefDel = new ArrayList<>();
    
    protected List<RegMovimientoFicha> listaFichasDell = new ArrayList<>();
    protected List<RegMovimientoCliente> listClientsDell = new ArrayList<>();
    protected List<RegMovimientoCapital> capitalDell = new ArrayList<>();
    protected List<RegMovimientoRepresentante> representanteDell = new ArrayList<>();
    protected List<RegMovimientoSocios> sociosDell = new ArrayList<>();
    
    protected List<RegFicha> fichasBorradas = new ArrayList<>();
    protected List<RegFicha> fichasAgregadas = new ArrayList<>();

    protected RegMovimientoCliente movClientNew = new RegMovimientoCliente();

    public Long getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(Long numFicha) {
        this.numFicha = numFicha;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public Boolean getOcultarBtnGuardar() {
        return ocultarBtnGuardar;
    }

    public void setOcultarBtnGuardar(Boolean ocultarBtnGuardar) {
        this.ocultarBtnGuardar = ocultarBtnGuardar;
    }

    public Boolean getHabilitarBtnReportes() {
        return habilitarBtnReportes;
    }

    public void setHabilitarBtnReportes(Boolean habilitarBtnReportes) {
        this.habilitarBtnReportes = habilitarBtnReportes;
    }

    public Boolean getHabilitarCompletarTarea() {
        return habilitarCompletarTarea;
    }

    public void setHabilitarCompletarTarea(Boolean habilitarCompletarTarea) {
        this.habilitarCompletarTarea = habilitarCompletarTarea;
    }

    public Boolean getHabilitarRedirectBandTarea() {
        return habilitarRedirectBandTarea;
    }

    public void setHabilitarRedirectBandTarea(Boolean habilitarRedirectBandTarea) {
        this.habilitarRedirectBandTarea = habilitarRedirectBandTarea;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public int getContReportImpresos() {
        return contReportImpresos;
    }

    public void setContReportImpresos(int contReportImpresos) {
        this.contReportImpresos = contReportImpresos;
    }

    public RegEnteInterviniente getNuevoInterviniente() {
        return nuevoInterviniente;
    }

    public void setNuevoInterviniente(RegEnteInterviniente nuevoInterviniente) {
        this.nuevoInterviniente = nuevoInterviniente;
    }

    public RegMovimientoCapital getMovimientoCapitalNuevo() {
        return movimientoCapitalNuevo;
    }

    public void setMovimientoCapitalNuevo(RegMovimientoCapital movimientoCapitalNuevo) {
        this.movimientoCapitalNuevo = movimientoCapitalNuevo;
    }

    public RegFicha getFichaSeleccionada() {
        return fichaSeleccionada;
    }

    public void setFichaSeleccionada(RegFicha fichaSeleccionada) {
        this.fichaSeleccionada = fichaSeleccionada;
    }

    public CtlgCargo getCargoSel() {
        return cargoSel;
    }

    public void setCargoSel(CtlgCargo cargoSel) {
        this.cargoSel = cargoSel;
    }

    public CtlgItem getItemSelec() {
        return itemSelec;
    }

    public void setItemSelec(CtlgItem itemSelec) {
        this.itemSelec = itemSelec;
    }

    public List<RegMovimientoCliente> getMovimientoClienteList() {
        return movimientoClienteList;
    }

    public void setMovimientoClienteList(List<RegMovimientoCliente> movimientoClienteList) {
        this.movimientoClienteList = movimientoClienteList;
    }

    public List<RegMovimientoFicha> getMovimientoFichaList() {
        return movimientoFichaList;
    }

    public void setMovimientoFichaList(List<RegMovimientoFicha> movimientoFichaList) {
        this.movimientoFichaList = movimientoFichaList;
    }

    public List<RegMovimientoCapital> getMovimientoCapitalList() {
        return movimientoCapitalList;
    }

    public void setMovimientoCapitalList(List<RegMovimientoCapital> movimientoCapitalList) {
        this.movimientoCapitalList = movimientoCapitalList;
    }

    public List<RegMovimientoRepresentante> getMovimientoRepresentanteList() {
        return movimientoRepresentanteList;
    }

    public void setMovimientoRepresentanteList(List<RegMovimientoRepresentante> movimientoRepresentanteList) {
        this.movimientoRepresentanteList = movimientoRepresentanteList;
    }

    public List<RegMovimientoSocios> getMovimientoSocioList() {
        return movimientoSocioList;
    }

    public void setMovimientoSocioList(List<RegMovimientoSocios> movimientoSocioList) {
        this.movimientoSocioList = movimientoSocioList;
    }

    public Collection<RegCatPapel> getPapelList() {
        return papelList;
    }

    public void setPapelList(Collection<RegCatPapel> papelList) {
        this.papelList = papelList;
    }

    public RegTipoFicha getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(RegTipoFicha tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public List<RegMovimiento> getListadoMovimientosRef() {
        return listadoMovimientosRef;
    }

    public void setListadoMovimientosRef(List<RegMovimiento> listadoMovimientosRef) {
        this.listadoMovimientosRef = listadoMovimientosRef;
    }

    public List<RegMovimiento> getMovimientosReff() {
        return movimientosReff;
    }

    public void setMovimientosReff(List<RegMovimiento> movimientosReff) {
        this.movimientosReff = movimientosReff;
    }

    public RegMovimientoCliente getMovClientNew() {
        return movClientNew;
    }

    public void setMovClientNew(RegMovimientoCliente movClientNew) {
        this.movClientNew = movClientNew;
    }

    public RegMovimientoRepresentante getMovimientoRepresentante() {
        return movimientoRepresentante;
    }

    public void setMovimientoRepresentante(RegMovimientoRepresentante movimientoRepresentante) {
        this.movimientoRepresentante = movimientoRepresentante;
    }

    public RegMovimientoSocios getMovimientoSocios() {
        return movimientoSocios;
    }

    public void setMovimientoSocios(RegMovimientoSocios movimientoSocios) {
        this.movimientoSocios = movimientoSocios;
    }

    public List<RegMovimientoFicha> getListaFichasDell() {
        return listaFichasDell;
    }

    public void setListaFichasDell(List<RegMovimientoFicha> listaFichasDell) {
        this.listaFichasDell = listaFichasDell;
    }

    public List<RegMovimientoCliente> getListClientsDell() {
        return listClientsDell;
    }

    public void setListClientsDell(List<RegMovimientoCliente> listClientsDell) {
        this.listClientsDell = listClientsDell;
    }

    public List<RegMovimientoCapital> getCapitalDell() {
        return capitalDell;
    }

    public void setCapitalDell(List<RegMovimientoCapital> capitalDell) {
        this.capitalDell = capitalDell;
    }

    public List<RegMovimientoRepresentante> getRepresentanteDell() {
        return representanteDell;
    }

    public void setRepresentanteDell(List<RegMovimientoRepresentante> representanteDell) {
        this.representanteDell = representanteDell;
    }

    public List<RegMovimientoSocios> getSociosDell() {
        return sociosDell;
    }

    public void setSociosDell(List<RegMovimientoSocios> sociosDell) {
        this.sociosDell = sociosDell;
    }

    public List<RegMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<RegMovimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public List<RegMovimientoReferencia> getListMovsRef() {
        return listMovsRef;
    }

    public void setListMovsRef(List<RegMovimientoReferencia> listMovsRef) {
        this.listMovsRef = listMovsRef;
    }

    public List<RegMovimientoReferencia> getListMovsRefDel() {
        return listMovsRefDel;
    }

    public void setListMovsRefDel(List<RegMovimientoReferencia> listMovsRefDel) {
        this.listMovsRefDel = listMovsRefDel;
    }

    public List<RegFicha> getFichasBorradas() {
        return fichasBorradas;
    }

    public void setFichasBorradas(List<RegFicha> fichasBorradas) {
        this.fichasBorradas = fichasBorradas;
    }

    public List<RegFicha> getFichasAgregadas() {
        return fichasAgregadas;
    }

    public void setFichasAgregadas(List<RegFicha> fichasAgregadas) {
        this.fichasAgregadas = fichasAgregadas;
    }

}
