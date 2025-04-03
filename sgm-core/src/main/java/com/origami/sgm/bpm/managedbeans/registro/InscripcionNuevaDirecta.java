/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.sgm.entities.RegTipoFicha;
import com.origami.sgm.lazymodels.RegActosLazy;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import com.origami.sgm.lazymodels.RegEnteJudicialLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.sgm.servlets.registro.CantidadPaginasDocumento;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class InscripcionNuevaDirecta extends CantidadPaginasDocumento implements Serializable {

    @javax.inject.Inject
    private InscripcionNuevaServices ejbIns;

    @javax.inject.Inject
    private FichaIngresoNuevoServices ejbFfic;

    protected Boolean btnDisable = false;
    protected Boolean mostrarDatos = false;
    protected Boolean editInterv = false;
    protected Boolean conyugue = true;
    protected Boolean ocultarApellidos = false;
    protected Calendar cal;
    protected CtlgCargo cargoSel;
    protected CtlgItem itemSelec;
    protected Long numFicha;
    //protected String cedula = "", nombres = "";
    protected Collection<RegCatPapel> papelList;
    protected List<CatCanton> catCantonsList = new ArrayList<>();
    protected List<RegLibro> regLibroList = new ArrayList<>();
    protected List<CtlgItem> listEstadoCivil = new ArrayList<>();
    protected List<CtlgCargo> ctlgCargos = new ArrayList<>();
    protected List<RegFicha> fichasList = new ArrayList<>();
    protected List<RegMovimiento> movsByFicha = new ArrayList<>();
    protected List<RegMovimiento> movimientosReff = new ArrayList<>();
    protected List<RegMovimiento> movimientosPorTomosList = new ArrayList<>();
    protected List<RegMovimientoCliente> movimientoClienteList = new ArrayList<>();

    protected RegMovimiento movimiento = new RegMovimiento();
    protected RegFicha fichaSel = new RegFicha();
    protected RegTipoFicha tipoFicha = new RegTipoFicha();
    protected RegEnteInterviniente nuevoInterviniente = new RegEnteInterviniente();
    protected RegMovimientoCliente movClientEdit = new RegMovimientoCliente();
    protected RegMovimientoCliente movClientNew = new RegMovimientoCliente();

    protected RegActosLazy actosLazy = new RegActosLazy();
    protected RegEnteJudicialLazy listEntesJudiciales = new RegEnteJudicialLazy();
    protected RegEnteIntervinienteLazy listIntervLazy = new RegEnteIntervinienteLazy();
    protected RegMovimientosLazy movimientosLazy = new RegMovimientosLazy();
    
    protected RegRegistrador registrador = new RegRegistrador();
    protected String abrevActo = "", abrevEnJu = "";

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        try {
            cal = Calendar.getInstance();
            regLibroList = ejbIns.getRegLibroList();
            catCantonsList = ejbIns.getCatCantonList();
            listEstadoCivil = ejbIns.lisCtlgItems("cliente.estado_civil");
            ctlgCargos = ejbIns.ctlgCargos();
            tipoFicha = ejbFfic.getRegTipoFichaById(1L);
            registrador = (RegRegistrador) acl.find(RegRegistrador.class, 1L);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgActos() {
        JsfUti.update("FormActos");
        JsfUti.executeJS("PF('dlgActos').show()");
    }

    public void showDlgNotarias() {
        JsfUti.update("FormNotaria");
        JsfUti.executeJS("PF('dlgNotaria').show()");
    }

    public void actoSeleccionado(RegActo act) {
        movimiento.setActo(act);
        abrevActo = act.getAbreviatura();
        this.cargarPapelesByActo();
        JsfUti.update("formNuevInsc:tVdetalle:actonomb");
        JsfUti.update("formNuevInsc:tVdetalle:actonombabrev");
        JsfUti.executeJS("PF('dlgActos').hide()");
    }

    public void cargarPapelesByActo() {
        papelList = ejbIns.getRegCatPapelByActo(movimiento.getActo().getId());
        JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
    }

    public void enteJudicialSeleccionado(RegEnteJudiciales jud) {
        movimiento.setEnteJudicial(jud);
        abrevEnJu = jud.getAbreviatura();
        JsfUti.update("formNuevInsc:tVdetalle:enteJudnomb");
        JsfUti.update("formNuevInsc:tVdetalle:enteJudnombabrev");
        JsfUti.executeJS("PF('dlgNotaria').hide()");
    }
    
    public void buscarActoAbrev() {
        try {
            if (abrevActo != null) {
                RegActo acto = ejbIns.getActoByAbrev(abrevActo);
                if (acto != null) {
                    movimiento.setActo(acto);
                    abrevActo = acto.getAbreviatura();
                    this.cargarPapelesByActo();
                    JsfUti.update("formNuevInsc:tVdetalle:actonomb");
                    JsfUti.update("formNuevInsc:tVdetalle:actonombabrev");
                } else {
                    JsfUti.messageInfo(null, "No se encontraron coincidencias.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarNotariaAbrev() {
        try {
            if (abrevEnJu != null) {
                RegEnteJudiciales ente = ejbIns.getRegEnteJudicialByAbrev(abrevEnJu);
                if (ente != null) {
                    movimiento.setEnteJudicial(ente);
                    abrevEnJu = ente.getAbreviatura();
                    JsfUti.update("formNuevInsc:tVdetalle:enteJudnomb");
                    JsfUti.update("formNuevInsc:tVdetalle:enteJudnombabrev");
                } else {
                    JsfUti.messageInfo(null, "No se encontraron coincidencias.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgIntervinientes() {
        JsfUti.update("formAgregarInterv");
        JsfUti.executeJS("PF('dlgInterviniente').show()");
    }

    public boolean enteIntervRepetido(RegEnteInterviniente ent) {
        for (RegMovimientoCliente acc : movimientoClienteList) {
            if (acc.getEnteInterv().getId().compareTo(ent.getId()) == 0) {
                return true;
            }
        }
        return false;
    }

    public void agregarInterviniente(RegEnteInterviniente inter) {
        if (this.enteIntervRepetido(inter)) {
            JsfUti.messageInfo(null, "Interviniente ya fue ingresado.", "");
            return;
        }
        editInterv = false;
        itemSelec = null;
        cargoSel = null;
        movClientNew = new RegMovimientoCliente();
        movClientNew.setEnteInterv(inter);
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public void buscarInterviniente() {
        RegEnteInterviniente inter;
        if (editInterv) {
            if (movClientEdit.getCedula() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar un Número de cédula para buscar.", "");
                return;
            }
            if (!Utils.validateNumberPattern(movClientEdit.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Números.", "");
                return;
            }
            inter = ejbIns.getInterviniente(movClientEdit.getCedula());
        } else {
            if (movClientNew.getCedula() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar un Número de cédula para buscar.", "");
                return;
            }
            if (!Utils.validateNumberPattern(movClientNew.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Números.", "");
                return;
            }
            inter = ejbIns.getInterviniente(movClientNew.getCedula());
        }
        if (inter != null) {
            if (editInterv) {
                movClientEdit.setCedula(inter.getCedRuc());
                movClientEdit.setNombres(inter.getNombre());
            } else {
                movClientNew.setCedula(inter.getCedRuc());
                movClientNew.setNombres(inter.getNombre());
            }
        } else {
            JsfUti.messageFatal(null, "Persona no encontrada.", "");
        }
        JsfUti.update("formNewInterv:pngInter");
        editInterv = false;
    }

    public void agregarListaInterv() {
        try {
            if (movClientNew.getPapel() == null) {
                JsfUti.messageInfo(null, "Debe seleccionar el papel.", "");
                return;
            }
            if (movClientNew.getCedula() != null) {
                if (!Utils.validateNumberPattern(movClientNew.getCedula().trim())) {
                    JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                    return;
                }
                if (movClientNew.getCedula().trim().length() < 10) {
                    JsfUti.messageFatal(null, "Numero de Documento es invalido.", "");
                    return;
                }
            }
            if (itemSelec != null) {
                movClientNew.setEstado(itemSelec.getCodename());
            }
            if (cargoSel != null) {
                movClientNew.setEstado(cargoSel.getNombre());
            }
            movimientoClienteList.add(movClientNew);
            JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
            JsfUti.executeJS("PF('dlgNewInter').hide();");
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarListaIntervEdit() {
        if (movClientEdit.getPapel() == null) {
            JsfUti.messageInfo(null, "Debe seleccionar el papel.", "");
            return;
        }
        if (movClientEdit.getCedula() != null) {
            if (!Utils.validateNumberPattern(movClientEdit.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
        }
        if (itemSelec != null) {
            movClientEdit.setEstado(itemSelec.getCodename());
        }
        if (cargoSel != null) {
            movClientEdit.setEstado(cargoSel.getNombre());
        }
        JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
        JsfUti.executeJS("PF('dlgEditInter').hide();");
    }

    public void guardarInterviniente() {
        cargoSel = null;
        itemSelec = null;
        if (nuevoInterviniente.getTipoInterv() == null) {
            JsfUti.messageInfo(null, "Seleccione tipo de persona.", "");
            return;
        }
        if (nuevoInterviniente.getNombre() == null) {
            JsfUti.messageInfo(null, "Ingrese nombre. ", "");
            return;
        }
        nuevoInterviniente.setUsuario(session.getName_user());
        nuevoInterviniente.setFecha(cal.getTime());
        if (nuevoInterviniente.getCedRuc() != null) {
            if (!Utils.validateNumberPattern(nuevoInterviniente.getCedRuc().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Números.", "");
                return;
            }
            RegEnteInterviniente buscado;
            if ("J".equals(nuevoInterviniente.getTipoInterv())) {
                buscado = ejbIns.bucarRegInterv(nuevoInterviniente.getCedRuc(), nuevoInterviniente.getNombre(), nuevoInterviniente.getTipoInterv());
            } else {
                buscado = ejbIns.bucarRegInterv(nuevoInterviniente.getCedRuc(), nuevoInterviniente.getApellidos() + " " + nuevoInterviniente.getNombre(), nuevoInterviniente.getTipoInterv());
            }
            if (buscado != null) {
                JsfUti.messageInfo(null, "Ya existe un interviniente con el mismo nombre, misma cedula y mismo tipo.", "");
            } else {
                nuevoInterviniente = ejbIns.guardaRegEnteInterviniente(nuevoInterviniente);
                if (nuevoInterviniente.getId() == null) {
                    JsfUti.messageInfo(null, "No se guardo interviniente.", "");
                } else {
                    movClientNew = new RegMovimientoCliente();
                    movClientNew.setEnteInterv(nuevoInterviniente);
                }
            }
        } else {
            nuevoInterviniente = ejbIns.guardaRegEnteInterviniente(nuevoInterviniente);
            if (nuevoInterviniente.getId() != null) {
                nuevoInterviniente = ejbIns.updateRegEnteInterviniente(nuevoInterviniente);
                movClientNew = new RegMovimientoCliente();
                movClientNew.setEnteInterv(nuevoInterviniente);
            } else {
                JsfUti.messageInfo(null, "No se guardo interviniente.", "");
            }
        }
        JsfUti.executeJS("PF('dlgCrearInterv').hide();");
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public void showDlgMovimientos() {
        JsfUti.update("formSeleccMovim");
        JsfUti.executeJS("PF('dlgMovimientoRef').show()");
    }

    public void eliminarInterviniente(int indice) {
        movimientoClienteList.remove(indice);
    }

    public void limpiarDatos() {
        movClientEdit.setCedula(null);
        movClientEdit.setNombres(null);
        movClientEdit.setEstado(null);
    }

    public void editarInterviniente(RegMovimientoCliente inter) {
        cargoSel = null;
        itemSelec = null;
        movClientEdit = new RegMovimientoCliente();
        movClientEdit = inter;
        editInterv = true;
        conyugue = true;
        JsfUti.update("formEditInterv");
        JsfUti.executeJS("PF('dlgEditInter').show()");
    }

    public void agregarMovimientosReferencia(RegMovimiento movRef) {
        if (movimientosReff.contains(movRef)) {
            JsfUti.messageInfo(null, "Movimiento ya fue agregado como referencia.", " ");
        } else {
            movimientosReff.add(movRef);
            JsfUti.update("formNuevInsc:tVdetalle:dtMovimientosReff");
        }
    }

    public void eliminarMovimientReff(int moviReff) {
        movimientosReff.remove(moviReff);
    }

    public void agregarNuevoInterviniente() {
        nuevoInterviniente = new RegEnteInterviniente();
        JsfUti.update("formCreaInterv");
        JsfUti.executeJS("PF('dlgCrearInterv').show();");
        this.actualizarApellido();
    }

    public void actualizarApellido() {
        if (nuevoInterviniente.getTipoInterv().equalsIgnoreCase("N")) {
            ocultarApellidos = true;
        }
        if (nuevoInterviniente.getTipoInterv().equalsIgnoreCase("J")) {
            ocultarApellidos = false;
        }
        if ("".equals(nuevoInterviniente.getTipoInterv())) {
            ocultarApellidos = false;
        }
    }

    public void eliminarFicha(int indice) {
        fichasList.remove(indice);
    }

    public void verInformacionBienInmuebleFichas(RegFicha rf) {
        try {
            fichaSel = rf;
            movsByFicha = ejbFfic.getMovimientosByFicha(fichaSel.getId());
            if (movsByFicha == null) {
                movsByFicha = new ArrayList<>();
            }
            if (fichaSel.getTipoPredio() != null) {
                if (fichaSel.getTipoPredio().equalsIgnoreCase("U")) {
                    fichaSel.setTipoPredioTemp("Urbano");
                } else if (fichaSel.getTipoPredio().equalsIgnoreCase("R")) {
                    fichaSel.setTipoPredioTemp("Rural");
                } else if (fichaSel.getTipoPredio().equalsIgnoreCase("I")) {
                    fichaSel.setTipoPredioTemp("IIIIIIIIIIIIII");
                }
            }
            JsfUti.update("formInfoBienInmuebFicha");
            JsfUti.executeJS("PF('dlgInfoBienInmuebleFicha').show()");
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgEditLinderos(RegFicha rf) {
        try {
            fichaSel = rf;
            JsfUti.update("formEditLinderos");
            JsfUti.executeJS("PF('dlgEditFichaReg').show()");
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editarLinderosFicha() {
        try {
            if (fichaSel.getLinderos() != null) {
                Boolean temp = ejbFfic.actualizarRegFicha(fichaSel);
                if (temp) {
                    JsfUti.messageInfo(null, "Guardado con Exito.", "");
                    JsfUti.update("formNuevInsc:tVdetalle:dtFichas");
                } else {
                    JsfUti.messageInfo(null, Messages.error, "");
                }
            } else {
                JsfUti.messageError(null, "Los Linderos de la Ficha no pueden estar vacios.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarByNumFicha() {
        try {
            if (numFicha == null) {
                JsfUti.messageInfo(null, "Ingrese el Numero de Ficha para buscar.", "");
                return;
            }
            RegFicha temp = ejbFfic.getFichaByNumFichaByTipo(numFicha, tipoFicha.getId());
            if (temp == null) {
                JsfUti.messageError(null, "No se encuentra Numero Ficha.", "");
                return;
            }
            if (temp.getEstado().getValor().equalsIgnoreCase("INACTIVO")) {
                JsfUti.messageError(null, "No se puede hacer referencia, estado de Ficha: INACTIVA.", "");
                return;
            } else {
                JsfUti.messageInfo(null, "El estado de la Ficha es: " + temp.getEstado().getValor(), "");
            }
            if (fichasList.contains(temp)) {
                JsfUti.messageError(null, "La Ficha ya se encuentra ingresada.", "");
            } else {
                fichasList.add(temp);
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarMovimiento() {
        try {
            if (this.validaCampos()) {
                movimiento.setEsNegativa(false);
                movimiento.setEstado("AC");
                movimiento.setFechaIngreso(cal.getTime());
                movimiento.setIndice(0);
                movimiento.setIsMercantil(false);
                movimiento.setMostrarRelacion(false);
                movimiento.setUserCreador(new AclUser(session.getUserId()));
                movimiento.setRegistrador(registrador);
                ejbIns.guadarEntesMovimientos(movimientoClienteList, new ArrayList<RegMovimientoRepresentante>(), new ArrayList<RegMovimientoSocios>());
                Boolean temp = ejbIns.guardarInscripcionAntigua(movimiento, movimientosReff, fichasList, movimientoClienteList);
                if(temp){
                    btnDisable = true;
                    JsfUti.update("formNuevInsc");
                    JsfUti.messageInfo(null, "Se Guardo la Inscripcion con Exito." , "");
                } else {
                    JsfUti.messageError(null, Messages.error , "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNuevaDirecta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean validaCampos() {
        //if (movimiento.getFechaOto() != null && movimiento.getFechaOto().after(cal.getTime())) {
        if (movimiento.getFechaOto() != null && movimiento.getFechaOto().after(movimiento.getFechaInscripcion())) {
            JsfUti.messageError(null, "Fecha de Otorgamiento debe ser menor a la fecha de inscripcion.", "");
            return false;
        }
        if (movimiento.getLibro() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Libro.", "");
            return false;
        }
        if (movimiento.getActo() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Acto en la pestaña Inscripcion", "");
            return false;
        }
        if (movimiento.getEnteJudicial() == null) {
            JsfUti.messageError(null, "Debe Seleccionar la Notaria/Juzgado en la pestaña Inscripcion", "");
            return false;
        }
        if (movimiento.getCodigoCan() == null) {
            JsfUti.messageError(null, "Debe Seleccionar el Canton en la pestaña Inscripcion", "");
            return false;
        }
        if(movimientoClienteList.isEmpty()){
            JsfUti.messageError(null, "Debe ingresar los intervinientes de la inscripcion", "");
            return false;
        } else {
            for (RegMovimientoCliente ll : movimientoClienteList) {
                if (ll.getPapel() == null) {
                    JsfUti.messageError(null, "Debe Seleccionar el Papel de desempeña el Interviniente en la pestaña Partes/Intervinientes", "Columna Papel");
                    return false;
                }
            }
        }
        return true;
    }

    public RegTipoFicha getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(RegTipoFicha tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public Boolean getMostrarDatos() {
        return mostrarDatos;
    }

    public void setMostrarDatos(Boolean mostrarDatos) {
        this.mostrarDatos = mostrarDatos;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public List<RegLibro> getRegLibroList() {
        return regLibroList;
    }

    public void setRegLibroList(List<RegLibro> regLibroList) {
        this.regLibroList = regLibroList;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
    }

    public List<CtlgItem> getListEstadoCivil() {
        return listEstadoCivil;
    }

    public void setListEstadoCivil(List<CtlgItem> listEstadoCivil) {
        this.listEstadoCivil = listEstadoCivil;
    }

    public List<CtlgCargo> getCtlgCargos() {
        return ctlgCargos;
    }

    public void setCtlgCargos(List<CtlgCargo> ctlgCargos) {
        this.ctlgCargos = ctlgCargos;
    }

    public List<RegMovimiento> getMovimientosReff() {
        return movimientosReff;
    }

    public void setMovimientosReff(List<RegMovimiento> movimientosReff) {
        this.movimientosReff = movimientosReff;
    }

    public List<RegMovimiento> getMovimientosPorTomosList() {
        return movimientosPorTomosList;
    }

    public void setMovimientosPorTomosList(List<RegMovimiento> movimientosPorTomosList) {
        this.movimientosPorTomosList = movimientosPorTomosList;
    }

    public RegActosLazy getActosLazy() {
        return actosLazy;
    }

    public void setActosLazy(RegActosLazy actosLazy) {
        this.actosLazy = actosLazy;
    }

    public RegEnteJudicialLazy getListEntesJudiciales() {
        return listEntesJudiciales;
    }

    public void setListEntesJudiciales(RegEnteJudicialLazy listEntesJudiciales) {
        this.listEntesJudiciales = listEntesJudiciales;
    }

    public RegEnteIntervinienteLazy getListIntervLazy() {
        return listIntervLazy;
    }

    public void setListIntervLazy(RegEnteIntervinienteLazy listIntervLazy) {
        this.listIntervLazy = listIntervLazy;
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public Collection<RegCatPapel> getPapelList() {
        return papelList;
    }

    public void setPapelList(Collection<RegCatPapel> papelList) {
        this.papelList = papelList;
    }

    public List<RegFicha> getFichasList() {
        return fichasList;
    }

    public void setFichasList(List<RegFicha> fichasList) {
        this.fichasList = fichasList;
    }

    public List<RegMovimientoCliente> getMovimientoClienteList() {
        return movimientoClienteList;
    }

    public void setMovimientoClienteList(List<RegMovimientoCliente> movimientoClienteList) {
        this.movimientoClienteList = movimientoClienteList;
    }

    public RegFicha getFichaSel() {
        return fichaSel;
    }

    public void setFichaSel(RegFicha fichaSel) {
        this.fichaSel = fichaSel;
    }

    public List<RegMovimiento> getMovsByFicha() {
        return movsByFicha;
    }

    public void setMovsByFicha(List<RegMovimiento> movsByFicha) {
        this.movsByFicha = movsByFicha;
    }

    public Long getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(Long numFicha) {
        this.numFicha = numFicha;
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

    public RegEnteInterviniente getNuevoInterviniente() {
        return nuevoInterviniente;
    }

    public void setNuevoInterviniente(RegEnteInterviniente nuevoInterviniente) {
        this.nuevoInterviniente = nuevoInterviniente;
    }

    public RegMovimientoCliente getMovClientEdit() {
        return movClientEdit;
    }

    public void setMovClientEdit(RegMovimientoCliente movClientEdit) {
        this.movClientEdit = movClientEdit;
    }

    public Boolean getEditInterv() {
        return editInterv;
    }

    public void setEditInterv(Boolean editInterv) {
        this.editInterv = editInterv;
    }

    public Boolean getConyugue() {
        return conyugue;
    }

    public void setConyugue(Boolean conyugue) {
        this.conyugue = conyugue;
    }

    public Boolean getOcultarApellidos() {
        return ocultarApellidos;
    }

    public void setOcultarApellidos(Boolean ocultarApellidos) {
        this.ocultarApellidos = ocultarApellidos;
    }

    public RegMovimientoCliente getMovClientNew() {
        return movClientNew;
    }

    public void setMovClientNew(RegMovimientoCliente movClientNew) {
        this.movClientNew = movClientNew;
    }

    public Boolean getBtnDisable() {
        return btnDisable;
    }

    public void setBtnDisable(Boolean btnDisable) {
        this.btnDisable = btnDisable;
    }

    public String getAbrevActo() {
        return abrevActo;
    }

    public void setAbrevActo(String abrevActo) {
        this.abrevActo = abrevActo;
    }

    public String getAbrevEnJu() {
        return abrevEnJu;
    }

    public void setAbrevEnJu(String abrevEnJu) {
        this.abrevEnJu = abrevEnJu;
    }

}
