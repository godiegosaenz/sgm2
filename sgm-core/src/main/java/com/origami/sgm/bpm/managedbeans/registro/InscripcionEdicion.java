/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.bpm.models.InscripcionNuevaModel;
import com.origami.sgm.bpm.models.MovimientoModel;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CtlgTipoParticipacion;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoFicha;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.RegActosLazy;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import com.origami.sgm.lazymodels.RegEnteJudicialLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import com.origami.sgm.util.HtmlUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class InscripcionEdicion extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private InscripcionNuevaServices inscripcionServices;

    @javax.inject.Inject
    private FichaIngresoNuevoServices ficha;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected String taskId;
    protected String idprocess;
    protected Long idMovimientos;

    protected Boolean conyugue = true;
    protected Boolean editInterv = false;
    protected Boolean ocultarBtn = false;
    protected Boolean ocultarApellidos = false;

    protected RegCatPapel papel;
    protected RegMovimiento movimiento;
    protected RegMovimientoCliente movClientEdit;
    protected RegMovimientoCliente movClientNew;
    protected RegActosLazy listActosLazy = new RegActosLazy();
    protected RegEnteJudicialLazy listEntesJudiciales = new RegEnteJudicialLazy();
    protected CatEnteLazy enteLazy;
    protected RegEnteIntervinienteLazy listIntervLazy = new RegEnteIntervinienteLazy();
    protected RegMovimientosLazy movimientosLazy;
    protected RegMovimientoReferencia moviRefe = new RegMovimientoReferencia();
    protected InscripcionNuevaModel inscripcion = new InscripcionNuevaModel();//
    protected List<CtlgItem> listEstadoCivil = new ArrayList<>();
    protected List<CtlgCargo> ctlgCargos = new ArrayList<>();
    protected List<CtlgTipoParticipacion> listParticipacions = new ArrayList<>();
    protected List<RegCapital> listRegCapital = new ArrayList<>();
    protected List<RegTipoFicha> listTiposiFchas = new ArrayList<>();
    protected List<RegActo> actosList = new ArrayList<>();

    protected List<CatCanton> catCantonsList = new ArrayList<>();

    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();
    protected RegMovimiento seleccionado = new RegMovimiento();
    private MovimientoModel movimientoModel;
    protected Long numFichaInicial, numFichaFinal;

    protected String abrevActo = "", abrevEnJu = "";

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        catCantonsList = inscripcionServices.getCatCantonList();
        listEstadoCivil = inscripcionServices.lisCtlgItems("cliente.estado_civil");
        ctlgCargos = inscripcionServices.ctlgCargos();
        listParticipacions = inscripcionServices.getListParticipante();
        listRegCapital = inscripcionServices.getListRegCapital();
        listTiposiFchas = ficha.getRegTipoFichaList();
        movimientosLazy = new RegMovimientosLazy();
        movimiento = new RegMovimiento();
        if (idMovimientos != null) {
            movimiento = inscripcionServices.getRegMovimientoById(idMovimientos);
        } else {
            this.continuar();
            //if (taskId != null) {
            //movimiento = inscripcionServices.getRegMovimientoBytaskId(taskId);
            //}
        }

        if (movimiento != null) {
            movimientoModel = new MovimientoModel(movimiento, (List<RegMovimientoCliente>) movimiento.getRegMovimientoClienteCollection(), (List<RegMovimientoCapital>) movimiento.getRegMovimientoCapitalCollection(), (List<RegMovimientoRepresentante>) movimiento.getRegMovimientoRepresentanteCollection(), (List<RegMovimientoSocios>) movimiento.getRegMovimientoSociosCollection());
            inscripcion.setMovimientoClienteList((List<RegMovimientoCliente>) movimiento.getRegMovimientoClienteCollection());
            inscripcion.setMovimientoFichaList((List<RegMovimientoFicha>) movimiento.getRegMovimientoFichaCollection());
            inscripcion.setMovimientoCapitalList((List<RegMovimientoCapital>) movimiento.getRegMovimientoCapitalCollection());
            inscripcion.setMovimientoRepresentanteList((List<RegMovimientoRepresentante>) movimiento.getRegMovimientoRepresentanteCollection());
            inscripcion.setMovimientoSocioList((List<RegMovimientoSocios>) movimiento.getRegMovimientoSociosCollection());
            inscripcion.setListMovsRef(inscripcionServices.listMovimientoReferenciaByMov(movimiento.getId()));

//            movimientoModel.setMovCapListOld(inscripcion.getMovimientoCapitalList());
//            movimientoModel.setMovCliListOld(inscripcion.getMovimientoClienteList());
//            movimientoModel.setMovRepListOld(inscripcion.getMovimientoRepresentanteList());
//            movimientoModel.setMovSocListOld(inscripcion.getMovimientoSocioList());
            actosList = inscripcionServices.getActosByLibro(movimiento.getLibro().getId());
            if (movimiento.getIsMercantil() == null || !movimiento.getIsMercantil()) {
                inscripcion.setTipoFicha(ficha.getRegTipoFichaById(1L));
            }
            //movimientosReff = inscripcionServices.getMovReferenciaByMov(movimiento.getId());

            for (RegMovimientoSocios soc : inscripcion.getMovimientoSocioList()) {
                for (RegMovimientoCliente inter : inscripcion.getMovimientoClienteList()) {
                    inter.setSociosSeleccionados(soc.getEnteInterv().getId().compareTo(inter.getEnteInterv().getId()) == 0);
                }
            }
            for (RegMovimientoRepresentante rep : inscripcion.getMovimientoRepresentanteList()) {
                for (RegMovimientoCliente inter : inscripcion.getMovimientoClienteList()) {
                    inter.setRepresentanteSeleccionados(rep.getEnteInterv().getId().compareTo(inter.getEnteInterv().getId()) == 0);
                }
            }
            inscripcion.setListadoMovimientosRef(new ArrayList());
            for (RegMovimientoFicha f : inscripcion.getMovimientoFichaList()) {
                List<RegMovimiento> lm = new ArrayList<>();
                for (RegMovimientoFicha mm : f.getFicha().getRegMovimientoFichaCollection()) {
                    lm.add(mm.getMovimiento());
                }
                inscripcion.setListadoMovimientosRef(lm);
            }
            inscripcion.setPapelList(inscripcionServices.getRegCatPapelByActo(movimiento.getActo().getId()));
            abrevActo = movimiento.getActo().getAbreviatura();
            if (movimiento.getEnteJudicial() != null) {
                abrevEnJu = movimiento.getEnteJudicial().getAbreviatura();
            }
        }
        ocultarBtn = idprocess == null;
    }

    public void showDlgActos() {
        JsfUti.update("FormActos");
        JsfUti.executeJS("PF('dlgActos').show()");
    }

    public void showDlgNotarias() {
        JsfUti.update("FormNotaria");
        JsfUti.executeJS("PF('dlgNotaria').show()");
    }

    public void showDlgIntervinientes() {
        JsfUti.update("formAgregarInterv");
        JsfUti.executeJS("PF('dlgInterviniente').show()");
    }

    public void showDlgMovimientos() {
        JsfUti.update("formSeleccMovim");
        JsfUti.executeJS("PF('dlgMovimientoRef').show()");
    }

    public void cargarPapelesByActo() {
        if (movimiento.getActo() != null) {
            inscripcion.setPapelList(inscripcionServices.getRegCatPapelByActo(movimiento.getActo().getId()));
            JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtInterviniente");
        } else {
            JsfUti.messageInfo(null, "Debe Escoger un Acto Obligatoriamente", "");
        }
    }

    public void cargarRepresentantes(RegMovimientoCliente interR) {
        RegMovimientoRepresentante r = new RegMovimientoRepresentante();
        if (interR.getRepresentanteSeleccionados()) {
            r.setEnteInterv(interR.getEnteInterv());
            inscripcion.getMovimientoRepresentanteList().add(r);
            JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtRepresentantes");
        } else {
            eliminarRepreByEnte(interR.getEnteInterv());
        }
    }

    public void cargarSocios(RegMovimientoCliente interR) {
        RegMovimientoSocios s = new RegMovimientoSocios();
        if (interR.getSociosSeleccionados()) {
            s.setEnteInterv(interR.getEnteInterv());
            inscripcion.getMovimientoSocioList().add(s);
            JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtSocios");
        } else {
            eliminarSociosByEnte(interR.getEnteInterv());
        }
    }

    public void verInformacionBienInmuebleFichas(RegMovimientoFicha rf) {
        RegFicha temp = rf.getFicha();
        temp.setDescripcionTemp(temp.getObsvEstado(temp.getEstado()));
        //inscripcion.setFichaSeleccionada(rf.getFicha());
        inscripcion.setFichaSeleccionada(temp);
        List<RegMovimiento> listTemp = ficha.getMovimientosByFicha(rf.getFicha().getId());
        if (listTemp == null) {
            listTemp = new ArrayList<>();
        }
        inscripcion.setMovimientos(listTemp);
        if (rf.getFicha().getTipoPredio() != null) {
            if (rf.getFicha().getTipoPredio().equalsIgnoreCase("U")) {
                inscripcion.setTipoPredio("Urbano");
            } else if (rf.getFicha().getTipoPredio().equalsIgnoreCase("R")) {
                inscripcion.setTipoPredio("Rural");
            } else if (rf.getFicha().getTipoPredio().equalsIgnoreCase("I")) {
                inscripcion.setTipoPredio("IIIIIIIIIIIIII");
            }
        }
        JsfUti.update("formInfoBienInmuebFicha");
        JsfUti.executeJS("PF('dlgInfoBienInmuebleFicha').show()");

    }

    public void verInformacionInscripcion(RegMovimiento mov) {
        try {
            seleccionado = mov;
            modelo = reg.getConsultaMovimiento(mov.getId());
            if (modelo != null) {
                JsfUti.update("formMovRegSelec");
                JsfUti.executeJS("PF('dlgMovRegSelec').show();");
            } else {
                JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
            JsfUti.messageError(null, Messages.error, "");
        }
    }

    public void buscarInterviniente() {
        RegEnteInterviniente inter;
        if (editInterv) {
            if (movClientEdit.getCedula() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar un Numero de cedula para buscar.", "");
                return;
            }
            if (!Utils.validateNumberPattern(movClientEdit.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            inter = inscripcionServices.getInterviniente(movClientEdit.getCedula());
        } else {
            if (inscripcion.getCedula() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar un Numero de cedula para buscar.", "");
                return;
            }
            if (!Utils.validateNumberPattern(inscripcion.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            inter = inscripcionServices.getInterviniente(inscripcion.getCedula());
        }

        if (inter != null) {
            if (editInterv) {
                movClientEdit.setCedula(inter.getCedRuc());
                movClientEdit.setNombres(inter.getNombre());
            }
            inscripcion.setCedula(inter.getCedRuc());
            inscripcion.setNombres(inter.getNombre());
        } else {
            JsfUti.messageFatal(null, "Persona no encontrada.", "");
            inscripcion.setCedula("");
            inscripcion.setNombres("");
        }
        JsfUti.update("formNewInterv:pngInter");
        //editInterv = false;
    }

    public void buscarByNumFichaRangos() {
        List<RegFicha> fichas;
        RegMovimientoFicha movimientoFicha;
        if (numFichaFinal != null && numFichaInicial != null && (numFichaFinal > numFichaInicial)) {
            fichas = ficha.getFichasByRangoNumFichaByTipo(numFichaInicial, numFichaFinal, inscripcion.getTipoFicha().getId());
            if (fichas != null && !fichas.isEmpty()) {
                for (RegFicha f : fichas) {
                    movimientoFicha = new RegMovimientoFicha();
                    movimientoFicha.setFicha(f);
                    movimientoFicha.setMovimiento(movimiento);
                    inscripcion.getMovimientoFichaList().add(movimientoFicha);
                }
            }
            JsfUti.executeJS("PF('dlgBusquedaRango').hide();");
        } else {
            JsfUti.messageInfo(null, "Debe ingresar el rango a consultar. (Numero de Ficha Final debe ser mayor al Inicial) ", "");
        }
    }

    public void buscarByNumFicha() {
        int fichaRepetidacont = 0;
        RegFicha rf;
        RegMovimientoFicha movimientoFicha = new RegMovimientoFicha();
        if (inscripcion.getTipoFicha() == null) {
            JsfUti.messageInfo(null, "Debe Seleccionar el tipo de ficha a buscar primeramente", "");
            return;
        }
        if (inscripcion.getNumFicha() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar un Numero de Ficha antes de hacer la busqueda", "");
            return;
        }
        rf = ficha.getFichaByNumFichaByTipo(inscripcion.getNumFicha(), inscripcion.getTipoFicha().getId());
        if (rf == null) {
            JsfUti.messageInfo(null, "El Numero de Ficha " + inscripcion.getNumFicha() + " no fue Encontrado o no pertenece a " + inscripcion.getTipoFicha().getNombre(), "");
            return;
        }
        if (rf.getEstado().getValor().equalsIgnoreCase("INACTIVO")) {
            JsfUti.messageError(null, "No se puede hacer referencia, estado de Ficha: INACTIVA.", "");
            return;
        } else {
            JsfUti.messageInfo(null, "El estado de la Ficha es: " + rf.getEstado().getValor(), "");
        }
        for (RegMovimientoFicha f : inscripcion.getMovimientoFichaList()) {
            if (f.getFicha().getId().equals(rf.getId())) {
                fichaRepetidacont++;
            }
        }
        if (fichaRepetidacont == 0) {
            movimientoFicha.setFicha(rf);
            movimientoFicha.setMovimiento(movimiento);
            inscripcion.getMovimientoFichaList().add(movimientoFicha);
        } else {
            JsfUti.messageInfo(null, "El Numero de Ficha " + inscripcion.getNumFicha(), "ya fue ingresada a la inscripcion.");
        }
    }

    public void actoSeleccionado(RegActo act) {
        movimiento.setActo(act);
        abrevActo = act.getAbreviatura();
        this.cargarPapelesByActo();
        JsfUti.update("formEdiMov:tVdetalle:actonomb");
        JsfUti.update("formEdiMov:tVdetalle:actonombabrev");
        JsfUti.executeJS("PF('dlgActos').hide()");

    }

    public void enteJudicialSeleccionado(RegEnteJudiciales jud) {
        movimiento.setEnteJudicial(jud);
        abrevEnJu = jud.getAbreviatura();
        JsfUti.update("formEdiMov:tVdetalle:enteJudnomb");
        JsfUti.update("formEdiMov:tVdetalle:enteJudnombabrev");
        JsfUti.executeJS("PF('dlgNotaria').hide()");
    }

    public void buscarActoAbrev() {
        try {
            if (abrevActo != null) {
                RegActo acto = inscripcionServices.getActoByAbrev(abrevActo);
                if (acto != null) {
                    movimiento.setActo(acto);
                    abrevActo = acto.getAbreviatura();
                    this.cargarPapelesByActo();
                    JsfUti.update("formEdiMov:tVdetalle:actonomb");
                    JsfUti.update("formEdiMov:tVdetalle:actonombabrev");
                } else {
                    JsfUti.messageInfo(null, "No se encontraron coincidencias.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarNotariaAbrev() {
        try {
            if (abrevEnJu != null) {
                RegEnteJudiciales ente = inscripcionServices.getRegEnteJudicialByAbrev(abrevEnJu);
                if (ente != null) {
                    movimiento.setEnteJudicial(ente);
                    abrevEnJu = ente.getAbreviatura();
                    JsfUti.update("formEdiMov:tVdetalle:enteJudnomb");
                    JsfUti.update("formEdiMov:tVdetalle:enteJudnombabrev");
                } else {
                    JsfUti.messageInfo(null, "No se encontraron coincidencias.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean enteIntervRepetido(RegEnteInterviniente ent) {
        for (RegMovimientoCliente acc : inscripcion.getMovimientoClienteList()) {
            if (acc.getEnteInterv().getCedRuc().equals(ent.getCedRuc())) {
                return true;
            }
        }
        return false;
    }

    public void actualizarApellido() {
        if (inscripcion.getNuevoInterviniente().getTipoInterv().equalsIgnoreCase("N")) {
            ocultarApellidos = true;
        }
        if (inscripcion.getNuevoInterviniente().getTipoInterv().equalsIgnoreCase("J")) {
            ocultarApellidos = false;
        }
    }

    public void agregarInterviniente(RegEnteInterviniente inter) {
        if (this.enteIntervRepetido(inter)) {
            JsfUti.messageInfo(null, "Interviniente ya fue ingresado.", "");
            return;
        }
        editInterv = false;
        conyugue = true;
        inscripcion.setCedula(null);
        inscripcion.setNombres(null);
        inscripcion.setItemSelec(null);
        inscripcion.setCargoSel(null);
        movClientNew = new RegMovimientoCliente();
        movClientNew.setEnteInterv(inter);
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public void agregarNuevoInterviniente() {
        inscripcion.setNuevoInterviniente(new RegEnteInterviniente());
        inscripcion.getNuevoInterviniente().setTipoInterv("N");
        JsfUti.update("formCreaInterv");
        JsfUti.executeJS("PF('dlgCrearInterv').show();");
        this.actualizarApellido();
    }

    public void agregarListaInterv() {
        try {
            if (papel == null) {
                JsfUti.messageInfo(null, "Debe seleccionar el papel.", "");
                return;
            }
            if (inscripcion.getItemSelec() != null && inscripcion.getCargoSel() != null) {
                JsfUti.messageInfo(null, "Solo debe seleccionar el cargo o el estado civil.", "");
                return;
            }
            if (inscripcion.getCedula() != null) {
                if (!Utils.validateNumberPattern(inscripcion.getCedula().trim())) {
                    JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                    return;
                }
            }
            movClientNew.setPapel(papel);
            movClientNew.setCedula(inscripcion.getCedula());
            movClientNew.setNombres(inscripcion.getNombres());
            if (inscripcion.getItemSelec() != null) {
                movClientNew.setEstado(inscripcion.getItemSelec().getCodename());
            }
            if (inscripcion.getCargoSel() != null) {
                movClientNew.setEstado(inscripcion.getCargoSel().getNombre());
            }
            inscripcion.getMovimientoClienteList().add(movClientNew);
            inscripcion.setCedula("");
            inscripcion.setNombres("");
            papel = null;
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
        }

        JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtInterviniente");
        JsfUti.executeJS("PF('dlgNewInter').hide();");

    }

    public void agregarListaIntervEdit() {
        if (movClientEdit.getPapel() == null) {
            JsfUti.messageInfo(null, "Debe seleccionar el papel.", "");
            return;
        }
        if (inscripcion.getItemSelec() != null && inscripcion.getCargoSel() != null) {
            JsfUti.messageInfo(null, "Solo debe seleccionar el cargo o el estado civil.", "");
            return;
        }
        if (movClientEdit.getCedula() != null) {
            if (!Utils.validateNumberPattern(movClientEdit.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
        }
        if (inscripcion.getItemSelec() != null) {
            movClientEdit.setEstado(inscripcion.getItemSelec().getCodename());
        }
        if (inscripcion.getCargoSel() != null) {
            movClientEdit.setEstado(inscripcion.getCargoSel().getNombre());
        }
        JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtInterviniente");
        JsfUti.executeJS("PF('dlgEditInter').hide();");
    }

    public void limpiarDatos() {
        movClientEdit.setCedula(null);
        movClientEdit.setNombres(null);
        movClientEdit.setEstado(null);
    }

    public void agregarCapital() {
        if (inscripcion.getMovimientoCapitalNuevo().getCapital() == null) {
            JsfUti.messageInfo(null, "Debe escoger un tipo de Capital Obligatoriamente ", "");
            return;
        }
        if (inscripcion.getMovimientoCapitalNuevo().getValor().compareTo(BigDecimal.ONE) >= 0) {
            inscripcion.getMovimientoCapitalList().add(inscripcion.getMovimientoCapitalNuevo());
            inscripcion.setMovimientoCapitalNuevo(new RegMovimientoCapital());
            JsfUti.update("formEdiMov:tVdetalle:dtCapital");
            JsfUti.update("formEdiMov:tVdetalle:pnlGdCapital");
        } else {
            JsfUti.messageInfo(null, "El campo Capital Obligatoriamente ", "");
        }
    }

    public void agregarMovimientosReferencia(RegMovimiento movRef) {
        if (this.existeMovRef(movRef)) {
            JsfUti.messageInfo(null, "Movimiento ya fue agregado como referencia.", " ");
        } else {
            moviRefe = new RegMovimientoReferencia();
            moviRefe.setMovimientoReff(movRef);
            inscripcion.getListMovsRef().add(moviRefe);
            JsfUti.update("formEdiMov:tVdetalle:dtMovimientosReff");
        }
    }

    public Boolean existeMovRef(RegMovimiento mov) {
        for (RegMovimientoReferencia re : inscripcion.getListMovsRef()) {
            if (re.getMovimientoReff().getId().equals(mov.getId())) {
                return true;
            }
        }
        return false;
    }

    public void editarInterviniente(RegMovimientoCliente inter) {
        inscripcion.setCargoSel(null);
        inscripcion.setItemSelec(null);
        movClientEdit = new RegMovimientoCliente();
        movClientEdit = inter;
        editInterv = true;
        conyugue = true;
        JsfUti.update("formEditInterv");
        JsfUti.executeJS("PF('dlgEditInter').show()");
    }

    public void eliminarSociosByEnte(RegEnteInterviniente ent) {
        int indice = 0;
        int i = 0;
        for (RegMovimientoSocios soc : inscripcion.getMovimientoSocioList()) {
            if (ent.getId().compareTo(soc.getEnteInterv().getId()) == 0) {
                indice = i;
            }
            i++;
        }
        if (indice >= 0) {
            inscripcion.getSociosDell().add(inscripcion.getMovimientoSocioList().get(indice));
            inscripcion.getMovimientoSocioList().remove(indice);
            JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtSocios");
        }
    }

    public void eliminarRepreByEnte(RegEnteInterviniente ent) {
        int indice = 0;
        int i = 0;
        for (RegMovimientoRepresentante rep : inscripcion.getMovimientoRepresentanteList()) {
            if (ent.getId().compareTo(rep.getEnteInterv().getId()) == 0) {
                indice = i;
            }
            i++;
        }
        if (indice >= 0) {
            inscripcion.getRepresentanteDell().add(inscripcion.getMovimientoRepresentanteList().get(indice));
            inscripcion.getMovimientoRepresentanteList().remove(indice);
            JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtRepresentantes");
        }
    }

    public void eliminarInterviniente(int indice) {
        RegMovimientoCliente temp = inscripcion.getMovimientoClienteList().remove(indice);
        if (temp.getId() != null) {
            inscripcion.getListClientsDell().add(temp);
        }
    }

    public void eliminarRepresentante(RegMovimientoRepresentante repre) {
        inscripcion.getRepresentanteDell().add(repre);
        inscripcion.getMovimientoRepresentanteList().remove(repre);
        JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtRepresentantes");
    }

    public void eliminarSocio(RegMovimientoSocios socio) {
        inscripcion.getSociosDell().add(socio);
        inscripcion.getMovimientoSocioList().remove(socio);
        JsfUti.update("formEdiMov:tVdetalle:tvsubPartes:dtSocios");
    }

    public void eliminarCapital(RegMovimientoCapital capital) {
        inscripcion.getCapitalDell().add(capital);
        inscripcion.getMovimientoCapitalList().remove(capital);
        JsfUti.update("formEdiMov:tVdetalle:dtCapital");
    }

    public void eliminarFicha(int indice) {
        RegMovimientoFicha temp = inscripcion.getMovimientoFichaList().remove(indice);
        if (temp.getId() != null) {
            inscripcion.getListaFichasDell().add(temp);
            inscripcion.getFichasBorradas().add(temp.getFicha());
        }
    }

    public void eliminarMovimientReff(int moviReff) {
        RegMovimientoReferencia temp = inscripcion.getListMovsRef().remove(moviReff);
        if (temp.getId() != null) {
            inscripcion.getListMovsRefDel().add(temp);
        }
    }

    public void guardarInterviniente() {
        inscripcion.setCedula("");
        inscripcion.setNombres("");
        inscripcion.setCargoSel(null);
        inscripcion.setItemSelec(null);
        if (inscripcion.getNuevoInterviniente().getNombre() == null) {
            JsfUti.messageInfo(null, "Ingrese nombre de interviniente.", "");
            return;
        }
        if (inscripcion.getNuevoInterviniente().getCedRuc() != null) {
            if (!Utils.validateNumberPattern(inscripcion.getNuevoInterviniente().getCedRuc().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            RegEnteInterviniente buscado;
            if ("J".equals(inscripcion.getNuevoInterviniente().getTipoInterv())) {
                buscado = inscripcionServices.bucarRegInterv(inscripcion.getNuevoInterviniente().getCedRuc(), inscripcion.getNuevoInterviniente().getNombre(), inscripcion.getNuevoInterviniente().getTipoInterv());
            } else {
                buscado = inscripcionServices.bucarRegInterv(inscripcion.getNuevoInterviniente().getCedRuc(), inscripcion.getNuevoInterviniente().getApellidos() + " " + inscripcion.getNuevoInterviniente().getNombre(), inscripcion.getNuevoInterviniente().getTipoInterv());
            }
            if (buscado != null) {
                JsfUti.messageInfo(null, "Ya existe un interviniente con el mismo nombre, misma cedula y mismo tipo.", "");
                return;
            } else {
                inscripcion.getNuevoInterviniente().setUsuario(session.getName_user());
                inscripcion.getNuevoInterviniente().setFecha(new Date());
                inscripcion.setNuevoInterviniente(inscripcionServices.guardaRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
                if (inscripcion.getNuevoInterviniente().getId() == null) {
                    JsfUti.messageInfo(null, "No se guardo interviniente.", "");
                    return;
                } else {
                    movClientNew = new RegMovimientoCliente();
                    movClientNew.setEnteInterv(inscripcion.getNuevoInterviniente());
                }
            }
        } else {
            inscripcion.getNuevoInterviniente().setUsuario(session.getName_user());
            inscripcion.getNuevoInterviniente().setFecha(new Date());
            inscripcion.setNuevoInterviniente(inscripcionServices.guardaRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
            if (inscripcion.getNuevoInterviniente().getId() != null) {
                inscripcion.setNuevoInterviniente(inscripcionServices.updateRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
            }
            movClientNew = new RegMovimientoCliente();
            movClientNew.setEnteInterv(inscripcion.getNuevoInterviniente());
        }
        conyugue = true;
        JsfUti.executeJS("PF('dlgCrearInterv').hide();");
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public Boolean camposObligatorioInscripcion() {
        if (movimiento.getFechaOto() == null) {
            JsfUti.messageError(null, "Fecha de Otorgamiento no debe estar vacia.", "");
            return false;
        }
        if (movimiento.getNumRepertorio() == null) {
            JsfUti.messageError(null, "Numero de repertorio es Obligatorio.", "");
            return false;
        }
        if (movimiento.getLibro() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Libro ", "");
            return false;
        }
        if (movimiento.getActo() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Acto en la opcion de Inscripcion", "");
            return false;
        }
        if (movimiento.getEnteJudicial() == null) {
            JsfUti.messageError(null, "Debe Seleccionar la Notaria/Juzgado en la opcion de Inscripcion", "");
            return false;
        }
        if (movimiento.getCodigoCan() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Canton en la opcion de Inscripcion", "");
            return false;
        }
        if (movimiento.getNumTomo() == null) {
            JsfUti.messageError(null, "Debe Ingresar el Tomo en la opcion de Inscripcion", "");
            return false;
        }
        if (inscripcion.getMovimientoClienteList() != null && !inscripcion.getMovimientoClienteList().isEmpty()) {
            for (RegMovimientoCliente ll : inscripcion.getMovimientoClienteList()) {
                if (ll.getPapel() == null) {
                    JsfUti.messageError(null, "Debe Seleccionar el Papel del Interviniente en la opcion de Partes/Intervinientes, " + ll.getCedula(), "Columna Papel");
                    return false;
                }
            }
            if (movimiento.getObservacion() != null) {
                return true;
            } else {
                JsfUti.messageError(null, "Debe Ingresar la Observacion correspondiente en la opcion de Observaciones", "");
                return false;
            }
        } else {
            JsfUti.messageError(null, "Debe Ingresar los Intervinientes Involucrados en la opcion de Partes/Intervinientes", "");
            return false;
        }
    }

    public void showDlgSaveMov() {
        if (camposObligatorioInscripcion()) {
            JsfUti.update("formAprobacion");
            JsfUti.executeJS("PF('dlgAprueba').show()");
        }
    }

    public void guardarMovimiento() {
        try {
            this.llenarListasModelo();
            movimiento.setUsuarioCorrec(session.getUserId().intValue());
            movimiento.setFechaCorrec(new Date());
            if (movimiento.getAnexoNegativa() != null) {
                movimiento.setAnexoNegativa(HtmlUtil.cleanHtml(movimiento.getAnexoNegativa()));
            }
            movimiento = inscripcionServices.guardarMovimientoEdidicion(movimiento, inscripcion, movimientoModel);
            if (movimiento != null) {
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/inscripciones.xhtml");
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void llenarListasModelo() {
        if (!inscripcion.getMovimientoFichaList().isEmpty()) {
            for (RegMovimientoFicha mf : inscripcion.getMovimientoFichaList()) {
                if (mf.getId() == null) {
                    inscripcion.getFichasAgregadas().add(mf.getFicha());
                }
            }
        }

        movimiento.setRegMovimientoCapitalCollection(inscripcion.getMovimientoCapitalList());
        movimiento.setRegMovimientoClienteCollection(inscripcion.getMovimientoClienteList());
        movimiento.setRegMovimientoFichaCollection(inscripcion.getMovimientoFichaList());
        movimiento.setRegMovimientoRepresentanteCollection(inscripcion.getMovimientoRepresentanteList());
        movimiento.setRegMovimientoReferenciaCollection(inscripcion.getListMovsRef());
        movimiento.setRegMovimientoSociosCollection(inscripcion.getMovimientoSocioList());

        movimientoModel.setMovCapList(inscripcion.getMovimientoCapitalList());
        movimientoModel.setMovCliList(inscripcion.getMovimientoClienteList());
        movimientoModel.setMovRepList(inscripcion.getMovimientoRepresentanteList());
        movimientoModel.setMovSocList(inscripcion.getMovimientoSocioList());

        movimientoModel.setMovRefList(inscripcion.getListMovsRef());
        movimientoModel.setMovRefListDel(inscripcion.getListMovsRefDel());
    }

    public void completeTarea() {
        if (movimiento.getRazonImpresa() && movimiento.getInscripcionImpresa()) {
            HashMap map = new HashMap();
            this.completeTask(movimiento.getTaskId(), map);
            inscripcion.setHabilitarRedirectBandTarea(false);
            inscripcion.setHabilitarCompletarTarea(true);
            JsfUti.update("formEdiMov");
        } else {
            JsfUti.messageError(null, "Para poder completar la tarea primero debe generar la raon y la inscripcion.", "");
        }
    }

    public void actualizarLinderosFicha() {
        try {
            if (ficha.actualizarRegFicha(inscripcion.getFichaSeleccionada())) {
                JsfUti.messageInfo(null, "Guardado exitoso.", "");
            } else {
                JsfUti.messageInfo(null, "Error al grabar actualize la tarea.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionEdicion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public InscripcionNuevaModel getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(InscripcionNuevaModel inscripcion) {
        this.inscripcion = inscripcion;
    }

    public List<RegActo> getActosList() {
        return actosList;
    }

    public void setActosList(List<RegActo> actosList) {
        this.actosList = actosList;
    }

    public String getIdprocess() {
        return idprocess;
    }

    public void setIdprocess(String idprocess) {
        this.idprocess = idprocess;
    }

    public Long getIdMovimientos() {
        return idMovimientos;
    }

    public void setIdMovimientos(Long idMovimientos) {
        this.idMovimientos = idMovimientos;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
    }

    public RegActosLazy getListActosLazy() {
        return listActosLazy;
    }

    public void setListActosLazy(RegActosLazy listActosLazy) {
        this.listActosLazy = listActosLazy;
    }

    public RegEnteJudicialLazy getListEntesJudiciales() {
        return listEntesJudiciales;
    }

    public void setListEntesJudiciales(RegEnteJudicialLazy listEntesJudiciales) {
        this.listEntesJudiciales = listEntesJudiciales;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public RegEnteIntervinienteLazy getListIntervLazy() {
        return listIntervLazy;
    }

    public void setListIntervLazy(RegEnteIntervinienteLazy listIntervLazy) {
        this.listIntervLazy = listIntervLazy;
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

    public List<CtlgTipoParticipacion> getListParticipacions() {
        return listParticipacions;
    }

    public void setListParticipacions(List<CtlgTipoParticipacion> listParticipacions) {
        this.listParticipacions = listParticipacions;
    }

    public List<RegCapital> getListRegCapital() {
        return listRegCapital;
    }

    public void setListRegCapital(List<RegCapital> listRegCapital) {
        this.listRegCapital = listRegCapital;
    }

    public List<RegTipoFicha> getListTiposiFchas() {
        return listTiposiFchas;
    }

    public void setListTiposiFchas(List<RegTipoFicha> listTiposiFchas) {
        this.listTiposiFchas = listTiposiFchas;
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public Boolean getOcultarBtn() {
        return ocultarBtn;
    }

    public void setOcultarBtn(Boolean ocultarBtn) {
        this.ocultarBtn = ocultarBtn;
    }

    public RegMovimientoCliente getMovClientEdit() {
        return movClientEdit;
    }

    public void setMovClientEdit(RegMovimientoCliente movClientEdit) {
        this.movClientEdit = movClientEdit;
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

    public RegCatPapel getPapel() {
        return papel;
    }

    public void setPapel(RegCatPapel papel) {
        this.papel = papel;
    }

    public RegMovimiento getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(RegMovimiento seleccionado) {
        this.seleccionado = seleccionado;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public Boolean getConyugue() {
        return conyugue;
    }

    public void setConyugue(Boolean conyugue) {
        this.conyugue = conyugue;
    }

    public Long getNumFichaInicial() {
        return numFichaInicial;
    }

    public void setNumFichaInicial(Long numFichaInicial) {
        this.numFichaInicial = numFichaInicial;
    }

    public Long getNumFichaFinal() {
        return numFichaFinal;
    }

    public void setNumFichaFinal(Long numFichaFinal) {
        this.numFichaFinal = numFichaFinal;
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
