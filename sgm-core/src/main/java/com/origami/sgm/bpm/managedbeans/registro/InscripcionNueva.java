/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.models.InscripcionNuevaModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CtlgTipoParticipacion;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.lazymodels.RegActosLazy;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import com.origami.sgm.lazymodels.RegEnteJudicialLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import com.origami.sgm.servlets.registro.CantidadPaginasDocumento;
import com.origami.sgm.util.HtmlUtil;
import com.origami.sgm.util.registro.RegistroUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.NumeroLetra;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class InscripcionNueva extends CantidadPaginasDocumento implements Serializable {

    @javax.inject.Inject
    private InscripcionNuevaServices ejbIns;

    @javax.inject.Inject
    private FichaIngresoNuevoServices ejbFfic;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected Boolean mostrarDatos = false;
    protected Calendar cal;
    protected List<CatCanton> catCantonsList = new ArrayList<>();
    protected List<RegLibro> regLibroList = new ArrayList<>();
    protected Long numTramite;
    protected Long idTarea;
    protected RegMovimiento movimiento;
    protected RegActosLazy actosLazy = new RegActosLazy();
    protected RegEnteJudicialLazy listEntesJudiciales = new RegEnteJudicialLazy();
    protected RegEnteIntervinienteLazy listIntervLazy = new RegEnteIntervinienteLazy();
    protected RegMovimientosLazy movimientosLazy = new RegMovimientosLazy();
    protected RegpCertificadosInscripciones cert;
    protected String idprocess;
    protected Long numPredio;
    protected Long idActo;
    protected Boolean flag = false;
    protected Boolean editInterv = false;
    protected Boolean ocultarBtn = false;
    protected Boolean ocultarApellidos = false;
    protected Boolean ocultarGuardar = true;
    protected Boolean datosCatastro = false;
    protected Boolean guardarMov = false;
    protected Boolean conyugue = true;
    protected RegMovimientoCliente movClientEdit;
    protected InscripcionNuevaModel inscripcion = new InscripcionNuevaModel();
    protected List<CtlgItem> listEstadoCivil = new ArrayList<>();
    protected List<CtlgCargo> ctlgCargos = new ArrayList<>();
    protected List<CtlgTipoParticipacion> listParticipacions = new ArrayList<>();
    protected List<RegCapital> listRegCapital = new ArrayList<>();
    protected List<RegMovimiento> movimientosPorTomosList;

    protected Long numFichaInicial, numFichaFinal;
    protected Integer viewer = 0;
    protected RegRegistrador registrador = new RegRegistrador();
    protected String abrevActo = "", abrevEnJu = "";

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        try {
            if (idTarea != null) {
                if (session.getTaskID() != null) {
                    this.setTaskId(session.getTaskID());
                    numTramite = (Long) this.getVariable(session.getTaskID(), "tramite");
                }
                cal = Calendar.getInstance();
                movimiento = ejbIns.getRegMovimientoByRegpCertificadoInscripcion(idTarea);
                cert = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
                if (movimiento == null) {
                    this.llenarDatosNuevaInsc();
                } else {
                    if (movimiento.getFolioFin() != null) {
                        this.continuar();
                    }
                    if (movimiento.getEstado().equalsIgnoreCase("AC")) {
                        flag = true;
                        if (movimiento.getNumPaginaInscripcion() == null || movimiento.getNumPaginaRazon() == null) {
                            viewer = 1;
                        } else if (!movimiento.getRazonImpresa()) {
                            viewer = 2;
                        } else {
                            inscripcion.setOcultarBtnGuardar(true);
                        }
                    } else {
                        mostrarDatos = true;
                    }
                    inscripcion.setMovimientoClienteList(ejbFfic.getRegMovimientoClienteByMovimiento(movimiento.getId()));
                    inscripcion.setMovimientoFichaList(ejbFfic.getRegMovimientoFichasByMov(movimiento.getId()));
                    inscripcion.setListMovsRef(ejbIns.listMovimientoReferenciaByMov(movimiento.getId()));
                    if (movimiento.getActo() != null) {
                        inscripcion.setPapelList(ejbIns.getRegCatPapelByActo(movimiento.getActo().getId()));
                    }
                }
                regLibroList = ejbIns.getRegLibroList();
                catCantonsList = ejbIns.getCatCantonList();
                listEstadoCivil = ejbIns.lisCtlgItems("cliente.estado_civil");
                ctlgCargos = ejbIns.ctlgCargos();
                registrador = (RegRegistrador) acl.find(Querys.getRegRegistrador);
                inscripcion.setTipoFicha(ejbFfic.getRegTipoFichaById(1L));
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void llenarDatosNuevaInsc() {
        try {
            movimiento = new RegMovimiento();
            if (this.getTaskId() != null) {
                movimiento.setTaskId(this.getTaskId());
            }
            if (numTramite != null) {
                movimiento.setNumTramite(numTramite);
            }
            movimiento.setEstado("PR");
            movimiento.setFechaInscripcion(cal.getTime());
            movimiento.setFechaIngreso(cal.getTime());
            movimiento.setFechaRepertorio(cal.getTime());
            AclUser user = (AclUser) acl.find(AclUser.class, session.getUserId());
            movimiento.setUserCreador(user);
            cert = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
            movimiento.setRegpCertificadoInscripcion(cert);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void mostrarContenido() {
        if (movimiento.getLibro() == null) {
            mostrarDatos = false;
            JsfUti.messageWarning(null, "Debe Seleccionar un Libro ", "");
            return;
        }
        if (movimiento.getNumPaginasContabilizada() == null) {
            mostrarDatos = false;
            JsfUti.messageWarning(null, "Debe Ingresar el numero de fojas contabilizada para la Inscripcion ", "");
            return;
        }
        if (movimiento.getNumPaginasContabilizada() <= 0) {
            mostrarDatos = false;
            JsfUti.messageWarning(null, "Numero de paginas contabilizadas debe ser mayor a cero(0).", "");
            return;
        }
        movimiento.setIndice(0);
        //ahora se asigna el numero de repertorio en el mismo metodo que se asigna el numero de inscripcion
        //esto es en el guardado final, con la exepcion de una inscripcion en el libro negativa
        //que se necesita el numero de repertorio al inicio
        //el campo motivada de la tabla reg_libro solo va ser true en el libro negativa
        if (movimiento.getLibro().getMotivada()) {
            movimiento = ejbIns.asignarNumRepertorioByAnioYporTipoLibro(cal.get(Calendar.YEAR), movimiento);
        } else {
            movimiento = (RegMovimiento) acl.persist(movimiento);
        }
        mostrarDatos = true;
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

    public void cargarPapelesByActo() {
        inscripcion.setPapelList(ejbIns.getRegCatPapelByActo(movimiento.getActo().getId()));
        JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
    }

    public void showDlgIntervinientes() {
        JsfUti.update("formAgregarInterv");
        JsfUti.executeJS("PF('dlgInterviniente').show()");
    }

    public void showDlgMovimientos() {
        JsfUti.update("formSeleccMovim");
        JsfUti.executeJS("PF('dlgMovimientoRef').show()");
    }

    public void cargarRepresentantes(RegMovimientoCliente interR) {
        inscripcion.setMovimientoRepresentante(new RegMovimientoRepresentante());
        if (interR.getRepresentanteSeleccionados()) {
            if (inscripcion.getMovimientoRepresentanteList().isEmpty()) {
                inscripcion.getMovimientoRepresentante().setId(new Long(inscripcion.getMovimientoRepresentanteList().size()));
            } else {
                inscripcion.getMovimientoRepresentante().setId(new Long(inscripcion.getMovimientoRepresentanteList().size() - 1));
            }
            inscripcion.getMovimientoRepresentante().setEnteInterv(interR.getEnteInterv());
            inscripcion.getMovimientoRepresentanteList().add(inscripcion.getMovimientoRepresentante());
        } else {
            //this.eliminarRepreByEnte(interR.getEnteInterv());
        }
        JsfUti.update("formNuevInsc:tVdetalle:dtRepresentantes");
    }

    public void verInformacionBienInmuebleFichas(RegMovimientoFicha rf) {
        RegFicha temp = rf.getFicha();
        temp.setDescripcionTemp(temp.getObsvEstado(temp.getEstado()));
        //inscripcion.setFichaSeleccionada(rf.getFicha());
        inscripcion.setFichaSeleccionada(temp);
        List<RegMovimiento> listTemp = ejbFfic.getMovimientosByFicha(rf.getFicha().getId());
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
            inter = ejbIns.getInterviniente(movClientEdit.getCedula());
        } else {
            if (inscripcion.getCedula() == null) {
                JsfUti.messageInfo(null, "Debe Ingresar un Numero de cedula para buscar.", "");
                return;
            }
            if (!Utils.validateNumberPattern(inscripcion.getCedula().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            inter = ejbIns.getInterviniente(inscripcion.getCedula());
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
        editInterv = false;
    }

    public void buscarByNumFichaRangos() {
        List<RegFicha> fichas;
        RegMovimientoFicha movimientoFicha;
        if (numFichaFinal != null && numFichaInicial != null && (numFichaFinal > numFichaInicial)) {
            fichas = ejbFfic.getFichasByRangoNumFichaByTipo(numFichaInicial, numFichaFinal, inscripcion.getTipoFicha().getId());
            inscripcion.setMovimientoFichaList(new ArrayList<RegMovimientoFicha>());
            if (fichas != null && !fichas.isEmpty()) {
                for (RegFicha ficha : fichas) {
                    movimientoFicha = new RegMovimientoFicha();
                    movimientoFicha.setFicha(ficha);
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
            JsfUti.messageInfo(null, "Debe Seleccionar el tipo de ficha a buscar primeramente ", "");
            return;
        }
        if (inscripcion.getNumFicha() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar un Numero de Ficha antes de hacer la busqueda ", "");
            return;
        }
        rf = ejbFfic.getFichaByNumFichaByTipo(inscripcion.getNumFicha(), inscripcion.getTipoFicha().getId());
        if (rf == null) {
            JsfUti.messageInfo(null, "El Numero de Ficha = " + inscripcion.getNumFicha() + " no fue Encontrado o no pertenece a " + inscripcion.getTipoFicha().getNombre(), "");
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
            JsfUti.messageInfo(null, "El Numero de Ficha = " + inscripcion.getNumFicha(), "Ya fue Agregado");
        }

    }

    public boolean enteIntervRepetido(RegEnteInterviniente ent) {
        for (RegMovimientoCliente acc : inscripcion.getMovimientoClienteList()) {
            if (acc.getEnteInterv().getId().compareTo(ent.getId()) == 0) {
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
        if ("".equals(inscripcion.getNuevoInterviniente().getTipoInterv())) {
            ocultarApellidos = false;
        }
    }

    public void agregarInterviniente(RegEnteInterviniente inter) {
        if (this.enteIntervRepetido(inter)) {
            JsfUti.messageInfo(null, "Interviniente ya fue ingresado.", "");
            return;
        }
        editInterv = false;
        inscripcion.setCedula("");
        inscripcion.setNombres("");
        inscripcion.setItemSelec(null);
        inscripcion.setCargoSel(null);
        RegMovimientoCliente cli = new RegMovimientoCliente();
        cli.setEnteInterv(inter);
        inscripcion.setMovClientNew(cli);
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public void agregarNuevoInterviniente() {
        inscripcion.setNuevoInterviniente(new RegEnteInterviniente());
        JsfUti.update("formCreaInterv");
        JsfUti.executeJS("PF('dlgCrearInterv').show();");
        actualizarApellido();
    }

    public void agregarListaInterv() {
        try {
            if (inscripcion.getMovClientNew().getPapel() == null) {
                JsfUti.messageInfo(null, "Debe seleccionar el papel.", "");
                return;
            }
            if (inscripcion.getItemSelec() != null && inscripcion.getCargoSel() != null) {
                JsfUti.messageInfo(null, "Solo debe seleccionar el cargo o el estado civil.", "");
                return;
            }
            if (inscripcion.getCedula() == null) {

            } else {
                if (!Utils.validateNumberPattern(inscripcion.getCedula().trim())) {
                    JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                    return;
                }
                if (inscripcion.getCedula().trim().length() < 10) {
                    JsfUti.messageFatal(null, "Numero de Documento es invalido.", "");
                    return;
                }
            }
            inscripcion.getMovClientNew().setCedula(inscripcion.getCedula());
            inscripcion.getMovClientNew().setNombres(inscripcion.getNombres());
            if (inscripcion.getItemSelec() != null) {
                inscripcion.getMovClientNew().setEstado(inscripcion.getItemSelec().getCodename());
            }
            if (inscripcion.getCargoSel() != null) {
                inscripcion.getMovClientNew().setEstado(inscripcion.getCargoSel().getNombre());
            }
            inscripcion.getMovimientoClienteList().add(inscripcion.getMovClientNew());
            inscripcion.setCedula("");
            inscripcion.setNombres("");

        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class
                    .getName()).log(Level.SEVERE, null, e);
        }

        JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
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

        JsfUti.update("formNuevInsc:tVdetalle:dtInterviniente");
        JsfUti.executeJS("PF('dlgEditInter').hide();");

    }

    public void agregarCapital() {
        if (inscripcion.getMovimientoCapitalNuevo().getCapital() == null) {
            JsfUti.messageInfo(null, "Debe escoger un tipo de Capital Obligatoriamente ", "");
            return;
        }
        if (inscripcion.getMovimientoCapitalNuevo().getValor().compareTo(BigDecimal.ONE) >= 0) {
            inscripcion.getMovimientoCapitalList().add(inscripcion.getMovimientoCapitalNuevo());
            inscripcion.setMovimientoCapitalNuevo(new RegMovimientoCapital());
            JsfUti.update("formNuevInsc:tVdetalle:dtCapital");
            JsfUti.update("formNuevInsc:tVdetalle:pnlGdCapital");
        } else {
            JsfUti.messageInfo(null, "El campo Capital Obligatoriamente ", "");
        }

    }

    public void agregarMovimientosReferencia(RegMovimiento movRef) {
        for (RegMovimientoReferencia mr : inscripcion.getListMovsRef()) {
            if (Objects.equals(mr.getMovimientoReff().getId(), movRef.getId())) {
                JsfUti.messageInfo(null, "Movimiento ya fue agregado.", "");
                return;
            }
        }
        RegMovimientoReferencia nuevo = new RegMovimientoReferencia();
        nuevo.setMovimientoReff(movRef);
        inscripcion.getListMovsRef().add(nuevo);
        JsfUti.update("formNuevInsc:tVdetalle:dtMovimientosReff");
    }

    public void limpiarDatos() {
        movClientEdit.setCedula(null);
        movClientEdit.setNombres(null);
        movClientEdit.setEstado(null);
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

    public void eliminarInterviniente(int inter) {
        RegMovimientoCliente temp = inscripcion.getMovimientoClienteList().remove(inter);
        if (temp.getId() != null) {
            inscripcion.getListClientsDell().add(temp);
        }
    }

    public void eliminarFicha(int indice) {
        RegMovimientoFicha temp = inscripcion.getMovimientoFichaList().remove(indice);
        if (temp.getId() != null) {
            inscripcion.getListaFichasDell().add(temp);
        }
        JsfUti.update("formNuevInsc:tVdetalle:tVsubBienes:dtFichas");
    }

    public void eliminarMovimientReff(int moviReff) {
        RegMovimientoReferencia temp = inscripcion.getListMovsRef().remove(moviReff);
        if (temp.getId() != null) {
            inscripcion.getListMovsRefDel().add(temp);
        }
        JsfUti.update("formNuevInsc:tVdetalle:dtMovimientosReff");
    }

    public void guardarInterviniente() {
        inscripcion.setCedula("");
        inscripcion.setNombres("");
        inscripcion.setCargoSel(null);
        inscripcion.setItemSelec(null);
        if (inscripcion.getNuevoInterviniente().getTipoInterv() == null) {
            JsfUti.messageInfo(null, "Seleccione tipo de persona.", "");
            return;
        }
        if (inscripcion.getNuevoInterviniente().getNombre() == null) {
            JsfUti.messageInfo(null, "Ingrese nombre. ", "");
            return;
        }
        inscripcion.getNuevoInterviniente().setUsuario(session.getName_user());
        inscripcion.getNuevoInterviniente().setFecha(cal.getTime());
        if (inscripcion.getNuevoInterviniente().getCedRuc() != null) {
            if (!Utils.validateNumberPattern(inscripcion.getNuevoInterviniente().getCedRuc().trim())) {
                JsfUti.messageFatal(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            //RegEnteInterviniente buscado
            //        = ejbIns.bucarRegInterv(inscripcion.getNuevoInterviniente().getCedRuc(), inscripcion.getNuevoInterviniente().getNombre(), inscripcion.getNuevoInterviniente().getTipoInterv());
            RegEnteInterviniente buscado;
            if ("J".equals(inscripcion.getNuevoInterviniente().getTipoInterv())) {
                buscado = ejbIns.bucarRegInterv(inscripcion.getNuevoInterviniente().getCedRuc(), inscripcion.getNuevoInterviniente().getNombre(), inscripcion.getNuevoInterviniente().getTipoInterv());
            } else {
                buscado = ejbIns.bucarRegInterv(inscripcion.getNuevoInterviniente().getCedRuc(), inscripcion.getNuevoInterviniente().getApellidos() + " " + inscripcion.getNuevoInterviniente().getNombre(), inscripcion.getNuevoInterviniente().getTipoInterv());
            }
            if (buscado != null) {
                JsfUti.messageInfo(null, "Ya existe un interviniente con el mismo nombre, misma cedula y mismo tipo.", "");
            } else {
                inscripcion.setNuevoInterviniente(ejbIns.guardaRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
                if (inscripcion.getNuevoInterviniente().getId() == null) {
                    JsfUti.messageInfo(null, "No se guardo interviniente.", "");
                } else {
                    inscripcion.setMovClientNew(new RegMovimientoCliente());
                    inscripcion.getMovClientNew().setEnteInterv(inscripcion.getNuevoInterviniente());
                }
            }
        } else {
            inscripcion.setNuevoInterviniente(ejbIns.guardaRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
            if (inscripcion.getNuevoInterviniente().getId() != null) {
                inscripcion.setNuevoInterviniente(ejbIns.updateRegEnteInterviniente(inscripcion.getNuevoInterviniente()));
            }
            inscripcion.setMovClientNew(new RegMovimientoCliente());
            inscripcion.getMovClientNew().setEnteInterv(inscripcion.getNuevoInterviniente());
        }
        JsfUti.executeJS("PF('dlgCrearInterv').hide();");
        JsfUti.update("formNewInterv");
        JsfUti.executeJS("PF('dlgNewInter').show();");
    }

    public void guardarTomo() {
        if (movimiento.getNumTomo() != null) {
            guardarMovimiento();
            JsfUti.executeJS("PF('dlgAsignacionTomo').hide();");
        } else {
            JsfUti.messageError(null, "Debe Ingresar el Num Tomo ", "");
        }
    }

    public Boolean camposObligatorioInscripcion() {
        if (movimiento.getFechaOto() == null) {
            JsfUti.messageError(null, "Fecha de Otorgamiento no debe estar vacia.", "");
            return false;
        }
        if (movimiento.getFechaOto() != null && movimiento.getFechaOto().after(cal.getTime())) {
            JsfUti.messageError(null, "Fecha de Otorgamiento debe ser menor a la fecha actual.", "");
            return false;
        }
        if (movimiento.getFechaResolucion() != null && movimiento.getFechaResolucion().after(cal.getTime())) {
            JsfUti.messageError(null, "Fecha de Resolucion debe ser menor a la fecha actual.", "");
            return false;
        }
        if (movimiento.getLibro() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Libro.", "");
            return false;
        }
        if (movimiento.getActo() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Acto.", "");
            return false;
        }
        if (movimiento.getEnteJudicial() == null) {
            JsfUti.messageError(null, "Debe Seleccionar la Notaria/Juzgado.", "");
            return false;
        }
        if (movimiento.getCodigoCan() == null) {
            JsfUti.messageError(null, "Debe Seleccionar un Canton.", "");
            return false;
        }
        if (movimiento.getTransferenciaDominio() == null) {
            JsfUti.messageError(null, "Debe seleccionar si la inscripcion pasa a Castastrar.", "");
            return false;
        }
        if (inscripcion.getMovimientoClienteList() != null && !inscripcion.getMovimientoClienteList().isEmpty()) {
            Boolean marcado = true;
            for (RegMovimientoCliente ll : inscripcion.getMovimientoClienteList()) {
                if (ll.getPapel() == null) {
                    JsfUti.messageError(null, "Debe Seleccionar el Papel del Interviniente, " + ll.getCedula(), "Columna Papel");
                    return false;
                }
                if (movimiento.getTransferenciaDominio() && ll.getPropietario()) {
                    marcado = false;
                }
            }
            if (movimiento.getTransferenciaDominio() && marcado) {
                JsfUti.messageError(null, "Debe seleccionar el/los propietario/s actual/es.", "");
                return false;
            }
            if (movimiento.getObservacion() != null) {
                return true;
            } else {
                JsfUti.messageError(null, "Debe Ingresar la Observacion correspondiente de la Inscripcion", "");
                return false;
            }
        } else {
            JsfUti.messageError(null, "Debe Ingresar los Intervinientes Involucrados en la Inscripcion", "");
            return false;
        }
    }

    public void showDlgCancelar() {
        if (movimiento.getNumRepertorio() != null) {
            JsfUti.messageError(null, "No se puede Cancelar Inscripcion, por que ya tiene generado el numero de Repertorio.", "");
            return;
        }
        if (!inscripcion.getMovimientoClienteList().isEmpty()) {
            JsfUti.messageError(null, "La lista de Intervinientes no esta vacia. No se puede Cancelar Inscripcion.", "");
            return;
        }
        if (!inscripcion.getMovimientoFichaList().isEmpty()) {
            JsfUti.messageError(null, "La lista de Fichas no esta vacia. No se puede Cancelar Inscripcion.", "");
            return;
        }
        if (!inscripcion.getListMovsRef().isEmpty()) {
            JsfUti.messageError(null, "La lista de Referencias no esta vacia. No se puede Cancelar Inscripcion.", "");
            return;
        }
        if (!inscripcion.getListClientsDell().isEmpty() || !inscripcion.getListaFichasDell().isEmpty() || !inscripcion.getListMovsRefDel().isEmpty()) {
            JsfUti.messageError(null, "Primero de Clic en el boton Guardado Parcial.", "");
            return;
        }
        JsfUti.update("formCancel");
        JsfUti.executeJS("PF('dlgCancelar').show();");
    }

    public void cancelarInscripcion() {
        try {
            if (movimiento.getObsCancela() != null) {
                movimiento.setEstado("IN");
                movimiento.setNumTramite(null);
                movimiento.setNumRepertorio(null);
                acl.persist(movimiento);

                cert.setRealizado(true);
                cert.setFechaFin(cal.getTime());
                cert.setObservacion("Inscripcion CANCELADA");
                cert.setAclUser(session.getUserId());
                acl.persist(cert);

                inscripcion.setOcultarBtnGuardar(true);
                JsfUti.update("formNuevInsc");
                JsfUti.executeJS("PF('dlgCancelar').hide();");
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardadoParcial() {
        try {
            acl.persist(movimiento);
            ejbIns.deleteListsOfMovimiento(inscripcion.getListClientsDell(), new ArrayList<RegMovimientoRepresentante>(),
                    new ArrayList<RegMovimientoCapital>(), new ArrayList<RegMovimientoSocios>(), inscripcion.getListaFichasDell(),
                    inscripcion.getListMovsRefDel());
            if (!inscripcion.getMovimientoClienteList().isEmpty()) {
                inscripcion.setMovimientoClienteList(ejbIns.guardarMovsClientes(inscripcion.getMovimientoClienteList(), movimiento));
            }
            if (!inscripcion.getMovimientoFichaList().isEmpty()) {
                inscripcion.setMovimientoFichaList(ejbIns.guardarMovsFichas(inscripcion.getMovimientoFichaList(), movimiento));
            }
            if (!inscripcion.getListMovsRef().isEmpty()) {
                inscripcion.setListMovsRef(ejbIns.guardarMovsReferencia(inscripcion.getListMovsRef(), movimiento));
            }
            JsfUti.messageInfo(null, "Guardado Exitoso.", "");
            JsfUti.update("formNuevInsc");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarMovimiento() {
        if (camposObligatorioInscripcion()) {
            if (movimiento.getNumTomo() != null) {
                ejbIns.deleteListsOfMovimiento(inscripcion.getListClientsDell(), new ArrayList<RegMovimientoRepresentante>(),
                        new ArrayList<RegMovimientoCapital>(), new ArrayList<RegMovimientoSocios>(), inscripcion.getListaFichasDell(),
                        inscripcion.getListMovsRefDel());
                ejbIns.guadarEntesMovimientos(inscripcion.getMovimientoClienteList(), inscripcion.getMovimientoRepresentanteList(), inscripcion.getMovimientoSocioList());
                if (movimiento.getTransferenciaDominio()) {
                    ejbIns.inactivaPropieatariosFichas(inscripcion.getMovimientoFichaList());
                    ejbIns.saveFichaPropietarios(inscripcion.getMovimientoClienteList(), inscripcion.getMovimientoFichaList(), movimiento);
                }
                movimiento.setFechaInscripcion(cal.getTime());
                movimiento.setFechaIngreso(cal.getTime());
                movimiento.setFechaRepertorio(cal.getTime());
                movimiento.setRegMovimientoClienteCollection(inscripcion.getMovimientoClienteList());
                movimiento.setRegMovimientoFichaCollection(inscripcion.getMovimientoFichaList());
                movimiento.setRegMovimientoCapitalCollection(inscripcion.getMovimientoCapitalList());
                movimiento.setRegMovimientoRepresentanteCollection(inscripcion.getMovimientoRepresentanteList());
                movimiento.setRegMovimientoSociosCollection(inscripcion.getMovimientoSocioList());
                movimiento.setUserCreador(new AclUser(session.getUserId()));
                if (registrador != null) {
                    movimiento.setRegistrador(registrador);
                }
                movimiento.setFechaInscripcion(cal.getTime());
                if (movimiento.getAnexoNegativa() != null) {
                    movimiento.setAnexoNegativa(HtmlUtil.cleanHtml(movimiento.getAnexoNegativa()));
                }
                movimiento = ejbIns.guardarMovimientoNuevo(movimiento, inscripcion.getListMovsRef());
                if (movimiento.getId() != null) {
                    inscripcion.setOcultarBtnGuardar(true);
                    viewer = 1;
                    JsfUti.messageInfo(null, "Guardado Exitoso", "");
                    JsfUti.update("formNuevInsc");
                }
            } else {
                Calendar cl1 = Calendar.getInstance();
                cl1.setTime(movimiento.getFechaInscripcion());
                Integer anio = cl1.get(Calendar.YEAR);
                movimientosPorTomosList = new ArrayList<>();
                movimientosPorTomosList = ejbIns.getRegMovimientosPorLibroAnio(anio, movimiento.getLibro().getId());
                if (movimientosPorTomosList == null) {
                    movimientosPorTomosList = new ArrayList<>();
                }
                JsfUti.update("formAsignacionTomo");
                JsfUti.executeJS("PF('dlgAsignacionTomo').show();");
            }
        }
    }

    public void redirect() {
        if (movimiento.getEstado().equals("AC")) {
            if (movimiento.getRazonImpresa() && movimiento.getInscripcionImpresa()) {
                //this.actualizarTarea();
                JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
            } else {
                JsfUti.messageInfo(null, "Debe imprimir la Inscripcion y la Razon de Inscripcion.", "");
            }
        } else {
            JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
        }
    }

    /*public void contarRazon() {
     cal.setTime(movimiento.getFechaInscripcion());
     Integer dia = cal.get(Calendar.DAY_OF_MONTH);
     Integer mes = cal.get(Calendar.MONTH) + 1;
     Integer anio = cal.get(Calendar.YEAR);
     NumeroLetra n = new NumeroLetra();
     servletSession.instanciarParametros();
     String fecha = n.Convertir(dia.toString(), true) + " DE " + Utils.convertirMesALetra(mes) + " DEL " + n.Convertir(anio.toString(), true);
     servletSession.agregarParametro("P_FECREGISTRO", fecha);
     servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
     servletSession.agregarParametro("ID_MOV", movimiento.getId());
     //movimiento = this.updateAndCountPageRazonInscripcion(movimiento);
     btnContRaz = true;
     btnImpRaz = false;
     viewer = 4;
     }*/
    public void imprimirRazonInscripcion() {
        try {
            cal.setTime(movimiento.getFechaInscripcion());
            Integer dia = cal.get(Calendar.DAY_OF_MONTH);
            Integer mes = cal.get(Calendar.MONTH) + 1;
            Integer anio = cal.get(Calendar.YEAR);
            NumeroLetra n = new NumeroLetra();
            if (movimiento.getInscripcionImpresa()) {
                if (!movimiento.getRazonImpresa()) {
                    this.actualizarTarea();
                    String fecha = n.Convertir(dia.toString(), true) + " DE " + Utils.convertirMesALetra(mes) + " DEL " + n.Convertir(anio.toString(), true);
                    movimiento.setRazonImpresa(Boolean.TRUE);
                    movimiento = (RegMovimiento) acl.persist(movimiento);
                    /**
                     * LLENAR ESTAS VARIABLES AQUI PARA QUE APARESCA EL CODIGO
                     * QR EN EL REPORTE PERO SOLO EN EL MOMENTO QUE SE REALIZA
                     * LA INSCRIPCION
                     * servletSession.agregarParametro("codigoQR", "url del
                     * documento"); servletSession.agregarParametro("validador",
                     * "codigo validador del documento");
                     */
                    servletSession.instanciarParametros();
                    servletSession.agregarParametro("P_FECREGISTRO", fecha);
                    servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
                    servletSession.agregarParametro("ID_MOV", movimiento.getId());
                    servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("IMG_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
                    servletSession.setNombreReporte("CabCertificadoPropiedadMercantil");
                    servletSession.setNombreSubCarpeta("registroPropiedad");
                    servletSession.setTieneDatasource(true);
                    servletSession.setEncuadernacion(Boolean.TRUE);
                    viewer = 0;
                    JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                    JsfUti.update("formNuevInsc");
                } else {
                    JsfUti.messageInfo(null, "La Razon ya fue generada si desea ir al menu Registro/Inscripciones Ingresadas", "");
                }
            } else {
                JsfUti.messageInfo(null, "Debe imprimir primero el documento de la Inscripcion.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void contarInscripcion() {
        try {
            List<Map> par = new ArrayList<>();
            List<String> paths = new ArrayList<>();
            servletSession.instanciarParametros();
            servletSession.agregarParametro("P_MOVIMIENTO", movimiento.getId());
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
            par.add(servletSession.getParametros());
            cal.setTime(movimiento.getFechaInscripcion());
            Integer dia = cal.get(Calendar.DAY_OF_MONTH);
            Integer mes = cal.get(Calendar.MONTH) + 1;
            Integer anio = cal.get(Calendar.YEAR);
            NumeroLetra n = new NumeroLetra();
            servletSession.instanciarParametros();
            String fecha = n.Convertir(dia.toString(), true) + " DE " + Utils.convertirMesALetra(mes) + " DEL " + n.Convertir(anio.toString(), true);
            servletSession.agregarParametro("P_FECREGISTRO", fecha);
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
            servletSession.agregarParametro("ID_MOV", movimiento.getId());
            par.add(servletSession.getParametros());
            paths.add(JsfUti.getRealPath("//reportes//registroPropiedad//RegistroInscripcion.jasper"));
            paths.add(JsfUti.getRealPath("//reportes//registroPropiedad//CabCertificadoPropiedadMercantil.jasper"));
            movimiento = RegistroUtil.updateAndCountPageInscripcion(movimiento, par, paths).getMovimiento();
            viewer = 2;
            JsfUti.messageInfo(null, "Foliacion con Exito.", "");
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirInscripcion() {
        try {
            if (!movimiento.getInscripcionImpresa()) {
                movimiento.setInscripcionImpresa(Boolean.TRUE);
                movimiento = (RegMovimiento) acl.persist(movimiento);
                /**
                 * LLENAR ESTAS VARIABLES AQUI PARA QUE APARESCA EL CODIGO QR EN
                 * EL REPORTE PERO SOLO EN EL MOMENTO QUE SE REALIZA LA
                 * INSCRIPCION servletSession.agregarParametro("codigoQR", "url
                 * del documento"); servletSession.agregarParametro("validador",
                 * "codigo validador del documento");
                 */
                servletSession.instanciarParametros();
                servletSession.agregarParametro("P_MOVIMIENTO", movimiento.getId());
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/registroPropiedad/");
                servletSession.setNombreReporte("RegistroInscripcion");
                servletSession.setTieneDatasource(true);
                servletSession.setNombreSubCarpeta("registroPropiedad");
                servletSession.setEncuadernacion(Boolean.TRUE);
                viewer = 3;
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                JsfUti.update("formNuevInsc");
            } else {
                JsfUti.messageInfo(null, "La Inscripcion ya fue generada si desea ir al menu Registro/Inscripciones Ingresadas", "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarTarea() {
        try {
            cert.setRealizado(true);
            cert.setFechaFin(cal.getTime());
            cert.setAclUser(session.getUserId());
            cert.setIdMovimiento(movimiento.getId());
            cert.setObservacion("Libro:" + movimiento.getLibro().getNombre() + "/Insc.:" + movimiento.getNumInscripcion() + "/Rep.:" + movimiento.getNumRepertorio() + "/Tomo:" + movimiento.getNumTomo());
            acl.persist(cert);
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarLinderosFicha() {
        try {
            if (ejbFfic.actualizarRegFicha(inscripcion.getFichaSeleccionada())) {
                JsfUti.messageInfo(null, "Guardado exitoso.", "");
            } else {
                JsfUti.messageInfo(null, "Error al grabar actualize la tarea.", "");

            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionNueva.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizaNegativa() {
        movimiento.setAnexoNegativa("");
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

    /*public List<RegMovimiento> getMovimientosReff() {
     return movimientosReff;
     }

     public void setMovimientosReff(List<RegMovimiento> movimientosReff) {
     this.movimientosReff = movimientosReff;
     }*/
    public String getIdprocess() {
        return idprocess;
    }

    public void setIdprocess(String idprocess) {
        this.idprocess = idprocess;
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public Long getIdActo() {
        return idActo;
    }

    public void setIdActo(Long idActo) {
        this.idActo = idActo;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
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

    public Boolean getOcultarGuardar() {
        return ocultarGuardar;
    }

    public void setOcultarGuardar(Boolean ocultarGuardar) {
        this.ocultarGuardar = ocultarGuardar;
    }

    public Boolean getDatosCatastro() {
        return datosCatastro;
    }

    public void setDatosCatastro(Boolean datosCatastro) {
        this.datosCatastro = datosCatastro;
    }

    public List<RegLibro> getRegLibroList() {
        return regLibroList;
    }

    public void setRegLibroList(List<RegLibro> regLibroList) {
        this.regLibroList = regLibroList;
    }

    public Boolean getMostrarDatos() {
        return mostrarDatos;
    }

    public void setMostrarDatos(Boolean mostrarDatos) {
        this.mostrarDatos = mostrarDatos;
    }

    public List<RegMovimiento> getMovimientosPorTomosList() {
        return movimientosPorTomosList;
    }

    public void setMovimientosPorTomosList(List<RegMovimiento> movimientosPorTomosList) {
        this.movimientosPorTomosList = movimientosPorTomosList;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
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

    public Integer getViewer() {
        return viewer;
    }

    public void setViewer(Integer viewer) {
        this.viewer = viewer;
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
