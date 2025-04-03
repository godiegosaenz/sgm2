/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.bpm.models.FichaIngreso;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatEscrituraRural;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegFichaBien;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoBienCaracteristica;
import com.origami.sgm.entities.RegTipoFicha;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.RegEnteIntervinienteLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class FichaIngresoNuevo implements Serializable {

    private static final long serialVersionUID = 1L;

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices nuevaFicha;

    @javax.inject.Inject
    private Entitymanager services;

    @Inject
    private UserSession session;

    protected Long newFicha;
    private FichaIngreso ingreso = new FichaIngreso();
    protected RegMovimiento movSelec = new RegMovimiento();
    protected RegTipoFicha tipoFicha;
    protected RegTipoBien tipoBien;
    protected RegMovimientosLazy movimientosLazy = new RegMovimientosLazy();
    protected CatPredioLazy predioLazy = new CatPredioLazy();
    protected CatEnteLazy enteLazy = new CatEnteLazy();
    protected RegEnteIntervinienteLazy enteIntervinienteLazy = new RegEnteIntervinienteLazy();
    protected List<RegFicha> fichasByMov = new ArrayList<>();

    protected List<RegTipoFicha> listTipoFicha = new ArrayList<>();
    protected List<RegMovimiento> movimientos;
    protected List<CatParroquia> listParroqia = new ArrayList<>();
    protected List<CatCiudadela> listCiudadelas = new ArrayList<>();
    protected List<RegTipoBien> listTipoBien = new ArrayList<>();
    protected List<RegFichaBien> listFichaBien = new ArrayList<>();
    protected List<RegTipoBienCaracteristica> listFichaBienCaract = new ArrayList<>();

    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();

    @PostConstruct
    public void initView() {

        tipoFicha = nuevaFicha.getRegTipoFichaById(1L);
        listTipoFicha = nuevaFicha.getRegTipoFichaList();
        listParroqia = nuevaFicha.getCatPerroquiasListByCanton(1L);
        ingreso.setTipo(nuevaFicha.getListNombresCdla());
        ingreso.setFechaInscripcion(new Date());
        ingreso.setEscrituraRural(new CatEscrituraRural());
        ingreso.setUrbano(Boolean.TRUE);
        this.cargarFichaPredial();
    }

    public FichaIngresoNuevo() {

    }

    public void cargarFichaPredial() {
        ingreso.setDatosImob(Boolean.TRUE);
        ingreso.setDatosComp(false);
        ingreso.setDatosBien(false);
        ingreso.setMostrarUrbano(false);
        ingreso.setMostrarRural(false);
        ingreso.getFicha().setTipo(tipoFicha);
        ingreso.getFicha().setCodigoPredial("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        movimientos = new ArrayList<>();
    }

    public void mostarDatosFicha() {
        if (tipoFicha != null) {
            Long o = tipoFicha.getId();
            switch (o.intValue()) {
                case 1:
                    ingreso.setDatosImob(Boolean.TRUE);
                    ingreso.setDatosComp(false);
                    ingreso.setDatosBien(false);
                    ingreso.setMostrarUrbano(false);
                    ingreso.setMostrarRural(false);
                    ingreso.getFicha().setTipo(tipoFicha);
                    movimientos = new ArrayList<>();
                    break;
                case 2:
                    ingreso.setDatosImob(false);
                    ingreso.setDatosComp(true);
                    ingreso.setDatosBien(false);
                    movimientos = new ArrayList<>();
                    break;
                case 3:
                    ingreso.setDatosImob(false);
                    ingreso.setDatosComp(false);
                    ingreso.setDatosBien(true);
                    listTipoBien = nuevaFicha.getTipoBienList(true);
                    break;
            }
            JsfUti.update("formFichIng:datComp");
            JsfUti.update("formFichIng:datBien");
            JsfUti.update("formFichIng:datImob");
        }
    }

    public void actualizarInmobiliario() {
        ingreso.setNumeroFicha(null);
        JsfUti.update("formFichIng");
    }

    public void redirectNuevaFicha() {
        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/fichaIngresoNuevo.xhtml");
    }

    public void tipoPredioFicha() {
        ingreso.setPropiedadHorz(Boolean.FALSE);
        ingreso.getFicha().setParroquia(null);
        ingreso.getFicha().setCiudadela(null);
        ingreso.setEscritura(new CatEscritura());
        ingreso.setEscrituraRural(new CatEscrituraRural());
        ingreso.setMostrarUrbano(false);
        ingreso.setMostrarRural(false);
        ingreso.setNumPredio(null);
        ingreso.setCodPredio(null);
        ingreso.setRegCatrast(null);
        ingreso.setIdentfPredio(null);
    }

    public void buscarCodPredio() {
        try {
            if (ingreso.getRegCatrast() == null || ingreso.getIdentfPredio() == null) {
                JsfUti.messageError(null, "Los campos Registro Catastral e Identificacion Predial son requeridos para la consulta.", "");
                return;
            }
            if (!Utils.validateNumberPattern(ingreso.getRegCatrast().toString())) {
                JsfUti.messageError(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            if (!Utils.validateNumberPattern(ingreso.getIdentfPredio().toString())) {
                JsfUti.messageError(null, "Solo Debe Ingresar Numeros.", "");
                return;
            }
            CatEscrituraRural escrituraRural = nuevaFicha.getCatEscrituraRural(ingreso.getRegCatrast(), ingreso.getIdentfPredio());
            if (escrituraRural == null) {
                ingreso.setEscrituraRural(new CatEscrituraRural());
                ingreso.getEscrituraRural().setRegistroCatastral(ingreso.getRegCatrast());
                ingreso.getEscrituraRural().setIdentificacionPredial(ingreso.getIdentfPredio());
                ingreso.getEscrituraRural().setUserCreador(session.getName_user());
                ingreso.getEscrituraRural().setFechaCreador(new Date());
                ingreso.setMostrarRural(Boolean.TRUE);
                JsfUti.update("formFichIng:tvUnaFicha");
                JsfUti.messageInfo(null, "Por favor, llene los campos de la escritura rural.", "");
            } else {
                RegFicha temp = nuevaFicha.getRegFichaByEscrituraRural(escrituraRural.getId());
                if (temp == null) {
                    ingreso.setEscrituraRural(escrituraRural);
                    ingreso.getEscrituraRural().setUserEdicion(session.getName_user());
                    ingreso.getEscrituraRural().setFechaEdicion(new Date());
                    ingreso.setMostrarRural(Boolean.TRUE);
                    JsfUti.update("formFichIng:tvUnaFicha");
                } else {
                    JsfUti.messageError(null, "El Codigo Rural ya se encuentra registrado en otra ficha.", "");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarCiudadelas() {
        if (ingreso.getFicha().getParroquia() != null) {
            listCiudadelas = nuevaFicha.getCiudadelasByParroquia(ingreso.getFicha().getParroquia().getId());
        }
        //JsfUti.update("formFichIng:tvUnaFicha:somCd");
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = ingreso.getTipo().size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(ingreso.getTipo().get(i), ingreso.getTipo().get(i));
        }
        return options;
    }

    public void buscarFicha() {
        try {
            if (ingreso.getNumeroFicha() == null) {
                JsfUti.messageError(null, "Ingrese el numero de Ficha.", "");
                return;
            }
            RegFicha f = nuevaFicha.getFichaByNumFichaByTipo(ingreso.getNumeroFicha(), 1L);
            if (f != null) {
                ingreso.setFichaBuscada(f);
                ingreso.setLinderos(f.getLinderos());
                movimientos = nuevaFicha.getMovimientosByFicha(f.getId());
                if (movimientos == null) {
                    movimientos = new ArrayList<>();
                }
            } else {
                JsfUti.messageError(null, "No existe numero de ficha.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgMovimientos() {
        movimientosLazy = new RegMovimientosLazy();
        JsfUti.update("formSelecMov");
        JsfUti.executeJS("PF('selMov').show();");
    }

    public void showDlgPredios() {
        predioLazy = new CatPredioLazy();
        JsfUti.update("formPredSel");
        JsfUti.executeJS("PF('selPredio').show();");
    }

    public void BuscarFichaMatriz() {
        try {
            if (ingreso.getFichaHistorico() == null) {
                JsfUti.messageError(null, "Por favor, ingrese una Ficha valida.", "");
                return;
            }
            ingreso.setEsFichaHistorico(false);
            ingreso.setRegFichaHistorico(nuevaFicha.getFichaByNumFichaByTipo(ingreso.getFichaHistorico(), tipoFicha.getId()));
            if (ingreso.getRegFichaHistorico() != null) {
                movimientos = nuevaFicha.getMovimientosByFicha(ingreso.getRegFichaHistorico().getId());
                if (movimientos != null) {
                    ingreso.setEsFichaHistorico(true);
                } else {
                    JsfUti.messageInfo(null, "Ficha ingresada no tiene movimientos asociados.", "");
                    movimientos = new ArrayList<>();
                }
            } else {
                JsfUti.messageError(null, "Ficha Matriz ingresada no se encuentra, verifique la ficha o comuniquese con SISTEMAS.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarMovimiento(int indice) {
        movimientos.remove(indice);
    }

    public void eliminarEmail(EnteCorreo c) {
        int index = 0;
        for (EnteCorreo ec : ingreso.getListCorreo()) {
            if (c.getEmail().compareTo(ec.getEmail()) == 0) {
                index = ingreso.getListCorreo().indexOf(ec);
                break;
            }
        }
        if (c.getId() != null) {
            ingreso.getListCorreoElim().add(ingreso.getListCorreo().get(index));
        }
        ingreso.getListCorreo().remove(index);
    }

    public void eliminarTelefono(EnteTelefono t) {
        int index = 0;
        if (t.getId() != null) {
            for (EnteTelefono et : ingreso.getListTelefonos()) {
                if (t.getId().compareTo(et.getId()) == 0) {
                    index = ingreso.getListTelefonos().indexOf(et);
                    break;
                }
            }
            ingreso.getListTelefonosElim().add(ingreso.getListTelefonos().get(index));
        } else {
            for (EnteTelefono et : ingreso.getListTelefonos()) {
                if (t.getTelefono().compareTo(et.getTelefono()) == 0) {
                    index = ingreso.getListTelefonos().indexOf(et);
                    break;
                }
            }
        }
        ingreso.getListTelefonos().remove(index);
    }

    public void editarMovimiento(RegMovimiento m) {
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + m.getId());
    }

    public void verDetalleMovimiento(RegMovimiento mov) {
        try {
            movSelec = mov;
            modelo = reg.getConsultaMovimiento(mov.getId());
            if (modelo != null) {
                JsfUti.update("formMovRegSelec");
                JsfUti.executeJS("PF('dlgMovRegSelec').show();");
            } else {
                JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void saveValorCaract(RegFichaBien b) {
        ingreso.setNumIdentificador("");
        if ((b.getValor() == null) || (b.getValor().equals(""))) {
            JsfUti.messageInfo(null, "El campo 'Valor' no puede estar vacío.", "");
            return;
        }
        if (b.getCaracteristica().getIsMain()) {
            ingreso.setNumIdentificador(b.getValor());
        }
    }

    public void guardarCompania() {
        if (ingreso.getInter().getNombre() == null) {
            JsfUti.messageInfo(null, "Debe ingresar el nombre de la Compañía.", "");
            return;
        }
        if (ingreso.getInter().getTipoInterv() == null) {
            JsfUti.messageInfo(null, "Debe seleccionar el tipo de Compañía.", "");
            return;
        }
        if (ingreso.getInter().getCedRuc() != null) {
            RegEnteInterviniente buscado = nuevaFicha.buscaRegEnteInterv(ingreso.getInter().getCedRuc(), ingreso.getInter().getNombre(), ingreso.getInter().getTipoInterv());
            if (buscado != null) {
                JsfUti.messageInfo(null, "Ya existe un interviniente con el mismo nombre, misma cédula y mismo tipo.", "");
            } else {
                ingreso.setInter(nuevaFicha.guardaRegEnteInterviniente(ingreso.getInter()));
                if (ingreso.getInter().getId() == null) {
                    JsfUti.messageInfo(null, "No se guardo interviniente.", "");
                } else {
                    ingreso.getFicha().setCodigoPredial(ingreso.getInter().getCedRuc());
                    ingreso.getFicha().setNumFicha(Long.parseLong(ingreso.getFicha().getCodigoPredial()));
                    ingreso.getFicha().setLinderos(ingreso.getInter().getNombre());

                }
            }
        } else {
            ingreso.setInter(nuevaFicha.guardaRegEnteInterviniente(ingreso.getInter()));
            ingreso.setInter(nuevaFicha.updateRegEnteInterviniente(ingreso.getInter()));
            ingreso.getFicha().setCodigoPredial(ingreso.getInter().getCedRuc());
            ingreso.getFicha().setNumFicha(Long.parseLong(ingreso.getFicha().getCodigoPredial()));
            ingreso.getFicha().setLinderos(ingreso.getInter().getNombre());
        }
        JsfUti.update("formFichIng");
        JsfUti.executeJS("PF('nueComp').hide();");
    }

    public void guardarVariasFichas() {
        try {
            if (ingreso.getFichaBuscada() == null) {
                JsfUti.messageError(null, "Ficha Registral Inmueble no encontrada.", "");
                return;
            }
            if (ingreso.getCantidadFichas() == null) {
                JsfUti.messageError(null, "Debe Ingresar una cantidad mayor a Uno.", "");
                return;
            }
            if (ingreso.getCantidadFichas() < 2) {
                JsfUti.messageError(null, "La cantidad de Fichas debe ser mayor a Uno.", "");
                return;
            }

            ingreso.setMensaje(nuevaFicha.guardarFichasDuplicadas(ingreso.getFichaBuscada(), ingreso.getCantidadFichas().intValue(), session.getName_user(), ingreso.getLinderos(), movimientos));
            if (ingreso.getMensaje() != null) {
                ingreso.setHabilitado(Boolean.FALSE);
                JsfUti.update("formMensaje");
                JsfUti.executeJS("PF('dlgMensajes').show()");
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void grabarFichaNuevo() {
        try {
            if (ingreso.getLinderos() == null) {
                JsfUti.messageError(null, "Debe Ingresar los Linderos", "");
                return;
            }
            ingreso.getFicha().setTipo(tipoFicha);
            ingreso.getFicha().setDetFuncionario(session.getName_user());
            ingreso.getFicha().setFechaApe(ingreso.getFechaInscripcion());
            ingreso.getFicha().setLinderos(ingreso.getLinderos());
            if (ingreso.getCodPredio() != null) {
                ingreso.getFicha().setCodigoPredial(ingreso.getCodPredio());
            }
            if (ingreso.getEsFichaHistorico()) {
                ingreso.getFicha().setFichaMatriz(new BigInteger(ingreso.getFichaHistorico().toString()));
            }
            if (ingreso.getUrbano()) {
                newFicha = nuevaFicha.saveRegFichaPredialUrbano(ingreso, movimientos);
            } else {
                newFicha = nuevaFicha.saveRegFichaPredialRural(ingreso, movimientos);
            }
            if (newFicha != null) {
                JsfUti.update("formNewFic");
                JsfUti.executeJS("PF('dlgNuevaFi').show();");
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarFichaBien() {
        if (listFichaBien != null) {
            Boolean v = null;
            for (RegFichaBien fb : listFichaBien) {
                v = fb.getValor() == null;
            }
            if (v) {
                JsfUti.messageInfo(null, "Debe ingresar el Valor de la Característica que va a representar el Bien.", "");
                return;
            }
        }
        RegFicha fBuscada = nuevaFicha.getRegFichaByCodPredial(ingreso.getNumIdentificador(), tipoFicha.getId());
        if (fBuscada != null) {
            JsfUti.messageInfo(null, "Identificador del Bien se encuentra registrado a la ficha: " + fBuscada.getNumFicha(), "");
            return;
        }
        if ((ingreso.getListCorreo().isEmpty()) || (ingreso.getListTelefonos().isEmpty())) {
            JsfUti.messageInfo(null, "Debe ingresar mínimo un teléfono y un email.", "");
            return;
        }
        if (ingreso.getFicha().getParroquia() == null) {
            JsfUti.messageInfo(null, "Por favor, seleccione Parroquia.", "");
            return;
        }
        if (ingreso.getFicha().getCiudadela() == null) {
            JsfUti.messageInfo(null, "Por favor, seleccione Ciudadela.", "");
            return;
        }

        ingreso.getFicha().setRegFichaBienCollection(listFichaBien);
        ingreso.setTipoFicha(tipoFicha);
        RegFicha f = nuevaFicha.guardarTelefCorreosYContribuyenteAndFicha(ingreso, session.getName_user());

        if (f.getId() != null) {
            JsfUti.redirectFaces("/faces/vistaprocesos/registroPropiedad/fichasIngresadas.xhtml");
        } else {
            JsfUti.messageInfo(null, "Problemas de conexión, intente mas tarde.", "");
        }

    }

    public void guardarFichaMercantilEnte() {
        if (ingreso.getFicha().getCodigoPredial() == null) {
            JsfUti.messageInfo(null, "Debe asociar la ficha a una Compañía.", "");
            return;
        }
        if (Long.valueOf(ingreso.getFicha().getNumFicha()) == null) {
            JsfUti.messageInfo(null, "Faltan datos de llenar en la Ficha.", "");
            return;
        }
        ingreso.getFicha().setTipo(tipoFicha);
        ingreso.getFicha().setDetFuncionario(session.getName_user());
        RegFicha f = nuevaFicha.guardarFicha(ingreso.getFicha(), movimientos);

        if (f.getId() != null) {
            JsfUti.redirectFaces("/faces/vistaprocesos/registroPropiedad/fichasIngresadas.xhtml");
        } else {
            JsfUti.messageInfo(null, "Problemas de conexión, comuníquese con Sistemas.", "");
        }

    }

    public void agregarMail() {
        ingreso.setCorreo(ingreso.getCorreo().trim());
        if (Utils.validarEmailConExpresion(ingreso.getCorreo())) {
            EnteCorreo cor = new EnteCorreo();
            cor.setEmail(ingreso.getCorreo());
            ingreso.getListCorreo().add(cor);
            ingreso.setCorreo("");
            JsfUti.update("formFichIng:dtEnteCorr");

        } else {
            JsfUti.messageError(null, "Correo Ingresado es invalido", "");
        }

    }

    public void agregarTelefono() {
        ingreso.setTelefono(ingreso.getTelefono().trim());
        if (ingreso.getTelefono() == null) {
            JsfUti.messageError(null, "No a Ingresado nigun Número de teléfono", "");
            return;
        }
        if (ingreso.getTelefono().equals("0999999999")) {
            JsfUti.messageError(null, "Número de teléfono Ingresado es Invalido", "");
            return;
        }
        if (!Utils.validateNumberPattern(ingreso.getTelefono())) {
            JsfUti.messageError(null, "Número de teléfono Ingresado es Invalido", "");
            return;
        }
        if (PhoneUtils.getValidNumber(ingreso.getTelefono(), SisVars.region)) {
            EnteTelefono tel = new EnteTelefono();
            tel.setTelefono(ingreso.getTelefono());
            ingreso.getListTelefonos().add(tel);
            ingreso.setTelefono("");
            JsfUti.update("formFichIng:dtEnteTel");
        }

    }

    public void agregarMovsVariasFicha(RegMovimiento mov) {
        if (movimientos.contains(mov)) {
            JsfUti.messageInfo(null, "Ya fue seleccionado este movimiento.", "");
        } else {
            movimientos.add(mov);
            if (ingreso.getVariasFichas()) {
                JsfUti.update("formFichIng:pgVariasFichas");
            } else {
                JsfUti.update("formFichIng:tvUnaFicha:dtMovReg");
            }
        }
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue != null && !newValue.equals(oldValue)) {
//            JsfUti.messageInfo(null, "Old: " + oldValue + ", New:" + newValue, "");
        }
        JsfUti.update("formFichIng:dtTipoBien");
    }

    public void onCellEditCor(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        EnteCorreo cor = new EnteCorreo();
        if (newValue != null) {
            if (!newValue.equals(oldValue)) {
                cor.setEmail(newValue.toString());
                ingreso.getListCorreo().add(cor);
                ingreso.getListCorreo().add(new EnteCorreo());
                JsfUti.messageInfo(null, "Old: " + oldValue + ", New:" + newValue, "");
            }
        }
        JsfUti.update("formFichIng:dtEnteCorr");
    }

    public void onCellEditTele(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        EnteTelefono tel = new EnteTelefono();
        if (newValue != null && !newValue.equals(oldValue)) {
            tel.setTelefono(newValue.toString());
            ingreso.getListTelefonos().add(tel);
            ingreso.getListTelefonos().add(new EnteTelefono());
            JsfUti.messageInfo(null, "Old: " + oldValue + ", New:" + newValue, "");
        }
    }

    public void actualizarFichaBien() {
        listFichaBien = new ArrayList<>();
        if (tipoBien != null) {
            for (RegTipoBienCaracteristica li : tipoBien.getRegTipoBienCaracteristicaCollection()) {
                if (li.getEstado()) {
                    RegFichaBien b = new RegFichaBien();
                    b.setCaracteristica(li);
                    listFichaBien.add(b);
                }
            }
        }
        JsfUti.update("formFichIng:dtTipoBien");
    }

    public void buscarEnteCiRuc() {
        if (ingreso.getCiRUC() == null) {
            JsfUti.messageError(null, "No ha ingresado Número de Identificación", "");
            return;
        }
        CatEnte ent = nuevaFicha.getCatEnte(ingreso.getCiRUC());
        if (ent != null) {
            ingreso.setEnte(ent);
            ingreso.setListCorreo(ent.getEnteCorreoCollection());
            ingreso.setListTelefonos(ent.getEnteTelefonoCollection());
            JsfUti.messageInfo(null, "Ingrese los datos faltantes", "");
        } else {
            JsfUti.messageInfo(null, "Persona no registrada, ingrese todos los datos solicitados.", "");
        }

        JsfUti.update("formFichIng:datBien");
    }

    public void selectPredio(CatPredio pred) {
        try {
            RegFicha ficha = nuevaFicha.getRegFichaByPredio(pred.getId());
            if (ficha != null) {
                ingreso.getFicha().setCodigoPredial("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                JsfUti.messageInfo(null, "El Predio numero: " + pred.getNumPredio() + ", ya esta registrado con la Ficha: " + ficha.getNumFicha(), "");
                return;
            }
            ingreso.getFicha().setPredio(pred);
            ingreso.setNumPredio(pred.getNumPredio());
            if (pred.getPhv() > 0 || pred.getPhh() > 0) {
                ingreso.setPropiedadHorz(Boolean.TRUE);
            }
            ingreso.setCodPredio(pred.getSector() + "." + pred.getMz() + "." + pred.getCdla() + "." + pred.getMzdiv() + "." + pred.getSolar() + "." + pred.getDiv1() + "." + pred.getDiv2() + "." + pred.getDiv3() + "." + pred.getDiv4()
                    + "." + pred.getDiv5() + "." + pred.getDiv6() + "." + pred.getDiv7() + "." + pred.getDiv8() + "." + pred.getDiv9() + "." + pred.getPhv() + "." + pred.getPhh());
            ingreso.getFicha().setCodigoPredial(ingreso.getCodPredio());
            if (pred.getCiudadela() != null) {
                ingreso.getFicha().setCiudadela(pred.getCiudadela());
                if (pred.getCiudadela().getCodParroquia() != null) {
                    ingreso.getFicha().setParroquia(pred.getCiudadela().getCodParroquia());
                    this.actualizarCiudadelas();
                }
            }
            CatEscritura escrituraT = nuevaFicha.getCatEscrituraByPredio(pred.getId());
            if (escrituraT != null) {
                ingreso.setEscritura(escrituraT);
            }
            ingreso.setMostrarUrbano(true);
            JsfUti.update("formFichIng:tvUnaFicha");
            JsfUti.update("formFichIng:tvUnaFicha:pngUnaFicha");
            JsfUti.executeJS("PF('selPredio').hide()");
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevo.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void comprobarFichaEnte(RegEnteInterviniente compania) {
        RegFicha fBuscada;
        fBuscada = nuevaFicha.getFichaByNumFichaByTipo(Long.valueOf(compania.getCedRuc()), tipoFicha.getId());
        if (fBuscada != null) {
            JsfUti.messageInfo(null, "Ficha número: " + fBuscada.getNumFicha() + ", ya registrada con la Compañía: " + compania.getNombre(), "");
        } else {
//            enteNuevo = false;
            ingreso.getFicha().setCodigoPredial(compania.getCedRuc());
            ingreso.getFicha().setNumFicha(Long.parseLong(ingreso.getFicha().getCodigoPredial()));
            ingreso.getFicha().setLinderos(compania.getNombre());
            JsfUti.update("formFichIng");
            JsfUti.executeJS("PF('selCle').hide();");
            JsfUti.messageInfo(null, "Ingrese parroquia, ciudadela y movimientos de ficha.", "");
        }

    }

    public Long getNewFicha() {
        return newFicha;
    }

    public void setNewFicha(Long newFicha) {
        this.newFicha = newFicha;
    }

    public RegTipoFicha getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(RegTipoFicha tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public List<RegTipoFicha> getListTipoFicha() {
        return listTipoFicha;
    }

    public void setListTipoFicha(List<RegTipoFicha> listTipoFicha) {
        this.listTipoFicha = listTipoFicha;
    }

    public FichaIngreso getIngreso() {
        return ingreso;
    }

    public void setIngreso(FichaIngreso ingreso) {
        this.ingreso = ingreso;
    }

    public List<RegMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<RegMovimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public List<CatParroquia> getListParroqia() {
        return listParroqia;
    }

    public void setListParroqia(List<CatParroquia> listParroqia) {
        this.listParroqia = listParroqia;
    }

    public List<CatCiudadela> getListCiudadelas() {
        return listCiudadelas;
    }

    public void setListCiudadelas(List<CatCiudadela> listCiudadelas) {
        this.listCiudadelas = listCiudadelas;
    }

    public RegTipoBien getTipoBien() {
        return tipoBien;
    }

    public void setTipoBien(RegTipoBien tipoBien) {
        this.tipoBien = tipoBien;
    }

    public List<RegTipoBien> getListTipoBien() {
        return listTipoBien;
    }

    public void setListTipoBien(List<RegTipoBien> listTipoBien) {
        this.listTipoBien = listTipoBien;
    }

    public List<RegFichaBien> getListFichaBien() {
        return listFichaBien;
    }

    public void setListFichaBien(List<RegFichaBien> listFichaBien) {
        this.listFichaBien = listFichaBien;
    }

    public List<RegTipoBienCaracteristica> getListFichaBienCaract() {
        return listFichaBienCaract;
    }

    public void setListFichaBienCaract(List<RegTipoBienCaracteristica> listFichaBienCaract) {
        this.listFichaBienCaract = listFichaBienCaract;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public RegEnteIntervinienteLazy getEnteIntervinienteLazy() {
        return enteIntervinienteLazy;
    }

    public void setEnteIntervinienteLazy(RegEnteIntervinienteLazy enteIntervinienteLazy) {
        this.enteIntervinienteLazy = enteIntervinienteLazy;
    }

    public List<RegFicha> getFichasByMov() {
        return fichasByMov;
    }

    public void setFichasByMov(List<RegFicha> fichasByMov) {
        this.fichasByMov = fichasByMov;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public RegMovimiento getMovSelec() {
        return movSelec;
    }

    public void setMovSelec(RegMovimiento movSelec) {
        this.movSelec = movSelec;
    }

}
