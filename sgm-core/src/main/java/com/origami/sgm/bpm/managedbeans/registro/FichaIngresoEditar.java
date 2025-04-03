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
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegFichaBien;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;
import util.Faces;
import util.JsfUti;
import util.PhoneUtils;
import util.Utils;

/**
 * Edicion de Ficha de Registro de Propiedad
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class FichaIngresoEditar implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    protected InscripcionNuevaServices inscripcionServices;

    @Inject
    private UserSession session;

    private FichaIngreso edicion = new FichaIngreso();
    private Long idFicha;
    private String tipoPredio;
    private String tipoBien;
    private String legend;
    private Boolean esInmobiliaria;
    private Boolean esMercantil;
    private Boolean esFichaBien;
    private Boolean esBoolean, permitido = false;
    private Boolean isPropiedadHorizontal;
    private CatEnteLazy entesLazy;

    private Boolean selectMenu = true;

    protected List<CatParroquia> parroquiaList = new ArrayList<>();
    protected List<CatCiudadela> ciudadelaList = new ArrayList<>();
    protected List<RegMovimientoFicha> listMovFich = new ArrayList<>();
    protected List<RegMovimientoFicha> listMovFichElim = new ArrayList<>();
    protected List<RegFichaBien> listFichaBien = new ArrayList<>();
    protected List<CatPredioPropietario> propietarios = new ArrayList<>();
    private List<CatEnte> seleccionados;
    protected RegMovimiento movimiento = new RegMovimiento();
    protected ConsultaMovimientoModel modelo = new ConsultaMovimientoModel();

    protected List<CtlgItem> estadosRegFichaList;

    protected CatPredioLazy predioLazy = new CatPredioLazy();
    protected RegMovimientosLazy movimientosLazy = new RegMovimientosLazy();

    protected RegMovimientoFicha mfEliminar;

    public FichaIngresoEditar() {
    }

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        if (idFicha != null && session != null) {
            entesLazy = new CatEnteLazy();
            estadosRegFichaList = inscripcionServices.lisCtlgItems("ficha.estado");
            parroquiaList = fichaServices.getCatPerroquiasListByCanton(1L);
            RegFicha temp = fichaServices.getRegFichaByIdFicha(idFicha);
            temp.setDescripcionTemp(temp.getObsvEstado(temp.getEstado()));
            //edicion.setFicha(fichaServices.getRegFichaByIdFicha(idFicha));
            edicion.setFicha(temp);
            edicion.setTipo(fichaServices.getListNombresCdla());

            //CtlgItem estadoFicha=edicion.getFicha().getEstado();
            if (edicion.getFicha().getTipoPredio().equalsIgnoreCase("U")) {
                edicion.setUrbano(true);
                tipoPredio = "Urbano";
                edicion.getFicha().setCatEscrituraRural(null);
                if (edicion.getFicha().getPredio() != null) {
                    edicion.setCodPredio(edicion.getFicha().getPredio().getNumPredio().toString());
                    //edicion.setEscritura(fichaServices.getCatEscrituraByPredio(edicion.getFicha().getPredio().getId()));
                    edicion.setPredio(edicion.getFicha().getPredio());

                    propietarios = (List<CatPredioPropietario>) edicion.getPredio().getCatPredioPropietarioCollection();
                    edicion.setPropiedadHorz(edicion.getFicha().getPredio().getPhh() > 0 && edicion.getFicha().getPredio().getPhv() > 0);
                }
            } else if (edicion.getFicha().getTipoPredio().equalsIgnoreCase("R")) {
                edicion.getFicha().setPredio(null);
                if (edicion.getFicha().getCatEscrituraRural() != null) {
                    edicion.setEscrituraRural(edicion.getFicha().getCatEscrituraRural());
                    edicion.setPropiedadHorz(edicion.getEscrituraRural().getPropiedadHorizontal());
                    edicion.setRegCatrast(edicion.getEscrituraRural().getRegistroCatastral());
                    edicion.setIdentfPredio(edicion.getEscrituraRural().getIdentificacionPredial());
                }
                edicion.setUrbano(false);
                tipoPredio = "Rural";
            }
            this.actualizarCiudadelas();
            if (edicion.getFicha().getTipo().getId() == 1L) {
                legend = "Datos de Ficha Registral - Inmobiliaria";
                esInmobiliaria = true;
                listMovFich = fichaServices.getRegMovimientoFichasList(edicion.getFicha().getId());

            } else if (edicion.getFicha().getTipo().getId() == 2L) {
                legend = "Datos de Ficha Mercantil";
                esMercantil = true;
                if (edicion.getFicha().getEnte() != null) {
                    edicion.setEnte(edicion.getFicha().getEnte());
                    if (!(edicion.getEnte().getCiRuc().equals(edicion.getFicha().getCodigoPredial()))) {
                        edicion.setDatosComp(true);
                        //edicion.getFicha().setCodigoPredial(edicion.getEnte().getCiRuc());
                    }
                } else {
                    edicion.setEnte(fichaServices.getCatEnte(edicion.getFicha().getCodigoPredial()));
                }
                listMovFich = fichaServices.getRegMovimientoFichasList(edicion.getFicha().getId());

            } else if (edicion.getFicha().getTipo().getId() == 3L) {
                legend = "Datos de Ficha Mercantil - Bienes";
                esFichaBien = true;
                if (edicion.getFicha().getEnte() != null) {
                    edicion.setListCorreo(edicion.getFicha().getEnte().getEnteCorreoCollection());
                    edicion.setListTelefonos(edicion.getFicha().getEnte().getEnteTelefonoCollection());
                }
                listFichaBien = (List<RegFichaBien>) edicion.getFicha().getRegFichaBienCollection();
                if (!listFichaBien.isEmpty()) {
                    tipoBien = listFichaBien.get(0).getCaracteristica().getTipoBien().getNombre();
                }
            } else {
                esBoolean = true;
            }
            //selectMenu = edicion.getEscritura().getAreaConstruccion() != null;
            if (edicion.getFicha().getAlicuotaEscritura() != null && edicion.getFicha().getAlicuotaEscritura().compareTo(BigDecimal.ZERO) > 0) {
                selectMenu = false;
            }
            if (edicion.getFicha().getPredio() != null) {
                if (edicion.getFicha().getPredio().getCiudadela() != null) {
                    edicion.getFicha().setCiudadela(edicion.getFicha().getPredio().getCiudadela());
                }
                /*edicion.getFicha().setCodigoPredial(edicion.getFicha().getPredio().getSector() + "." + edicion.getFicha().getPredio().getMz() + "." + edicion.getFicha().getPredio().getCdla() + "." + edicion.getFicha().getPredio().getMzdiv() + "."
                 + edicion.getFicha().getPredio().getSolar() + "." + edicion.getFicha().getPredio().getDiv1() + "." + edicion.getFicha().getPredio().getDiv2() + "." + edicion.getFicha().getPredio().getDiv3() + "." + edicion.getFicha().getPredio().getDiv4() + "."
                 + edicion.getFicha().getPredio().getDiv5() + "." + edicion.getFicha().getPredio().getDiv6() + "." + edicion.getFicha().getPredio().getDiv7() + "." + edicion.getFicha().getPredio().getDiv8() + "." + edicion.getFicha().getPredio().getDiv9() + "."
                 + edicion.getFicha().getPredio().getPhv() + "." + edicion.getFicha().getPredio().getPhh());*/
            }
            this.validaRoles();
            if (edicion.getFicha() != null) {
                selectMenu = edicion.getFicha().getAreaEscritura() != null;
            }
        } else {
            Faces.redirectFaces("/faces/vistaprocesos/dashBoard.xhtml");
        }
    }

    public void validaRoles() {
        if (session != null) {
            if (session.getRoles() != null && !session.getRoles().isEmpty()) {
                for (Long rl : session.getRoles()) {
                    if (rl.equals(80L)) {
                        permitido = true;
                        break;
                    }
                }
            }
        }
    }

    public void showDlgMovimientos() {
        movimientosLazy = new RegMovimientosLazy();
        JsfUti.update("formSelecMov");
        JsfUti.executeJS("PF('selMov').show();");
    }

    public void actualizarCiudadelas() {
        if (edicion.getFicha().getParroquia() != null) {
            ciudadelaList = fichaServices.getCiudadelasByParroquia(edicion.getFicha().getParroquia().getId());
        }
        JsfUti.update("formEditFicha");
    }

    public void encerarDatosMenu() {
        this.edicion.getFicha().setAreaEscritura(BigDecimal.ZERO);
        this.edicion.getFicha().setAlicuotaEscritura(BigDecimal.ZERO);
    }

    public void redirectEditEnte(Long id) {
        JsfUti.redirectFacesNewTab("/generic/editente.xhtml?param=" + id);
    }

    public void redirecNuevoEnte() {
        JsfUti.redirectFacesNewTab("/generic/editente.xhtml");
    }

    public void redirectEditarMovimiento(Long id) {
        Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + id);
        //JsfUti.redirectNewTab(SisVars.urlServidorCompleta + "/faces/vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + id);
    }

    public void buscarPredio() {
        if (edicion.getPredio().getNumPredio() == null) {
            JsfUti.messageError(null, "No ha ingresado Numero de Predio a Buscar", "");
            return;
        }
        if (!Utils.validateNumberPattern(edicion.getPredio().getNumPredio().toString())) {
            JsfUti.messageError(null, "Solo Debe Ingresar Numeros", "");
            return;
        }
        CatPredio predio = fichaServices.getPredioByNum(edicion.getPredio().getNumPredio().longValue());
        if (predio != null) {
            RegFicha f = fichaServices.getRegFichaNumPredio(predio.getId());
            if (f != null) {
                JsfUti.messageError(null, "Este predio ya ha sido ingresado por favor ingrese uno nuevo", "");
                edicion.setPropiedadHorz(false);
            } else {
                propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
                edicion.getFicha().setPredio(predio);
                edicion.setPredio(predio);
                edicion.setCodPredio(predio.getSector() + "." + predio.getMz() + "." + predio.getCdla() + "." + predio.getMzdiv() + "." + predio.getSolar() + "." + predio.getDiv1() + "." + predio.getDiv2() + "." + predio.getDiv3() + "." + predio.getDiv4()
                        + "." + predio.getDiv5() + "." + predio.getDiv6() + "." + predio.getDiv7() + "." + predio.getDiv8() + "." + predio.getDiv9() + "." + predio.getPhv() + "." + predio.getPhh());

                //edicion.getFicha().setCodigoPredial(edicion.getCodPredio());
                edicion.getFicha().setCiudadela(predio.getCiudadela());
                //edicion.getFicha().setParroquia(predio.getCiudadela().getCodParroquia());

                if (predio.getPhv() > 0 || predio.getPhh() > 0) {
                    edicion.setPropiedadHorz(true);
                }
                actualizarCiudadelas();
                /*CatEscritura escrituraT = fichaServices.getCatEscrituraByPredio(predio.getId());
                 if (escrituraT != null) {
                 edicion.setEscritura(escrituraT);
                 }*/
                edicion.setMostrarUrbano(Boolean.TRUE);
            }
        } else {
            JsfUti.messageError(null, "Numero de Predio Ingresado no existe", "");
            edicion.setPropiedadHorz(false);
        }
    }

    public void buscarByCodPredio() {
        /*if (edicion.getRegCatrast() == null || edicion.getIdentfPredio() == null) {
         JsfUti.messageInfo(null, "Los dos campos son requeridos para la consulta", "");
         return;
         }*
         /*CatEscrituraRural rural = fichaServices.getCatEscrituraRural(edicion.getRegCatrast(), edicion.getIdentfPredio());
         if (rural == null) {
         edicion.setEscrituraRural(new CatEscrituraRural());
         edicion.getEscrituraRural().setRegistroCatastral(edicion.getRegCatrast());
         edicion.getEscrituraRural().setIdentificacionPredial(edicion.getIdentfPredio());
         edicion.getEscrituraRural().setUserCreador(session.getName_user());
         edicion.getEscrituraRural().setFechaCreador(new Date());
         JsfUti.messageInfo(null, "Por favor, llene los datos de la escritura rural.", "");
         } else {
         RegFicha temp = fichaServices.getRegFichaByEscrituraRural(rural.getId());
         if (temp == null) {
         edicion.setEscrituraRural(rural);
         edicion.getEscrituraRural().setUserEdicion(session.getName_user());
         edicion.getEscrituraRural().setFechaEdicion(new Date());
         } else {
         JsfUti.messageInfo(null, "El Codigo Predial Rural ya se encuentra registrado en otra ficha.", "");
         }
         }*/
    }

    public void seleccionarPredio() {
        predioLazy = new CatPredioLazy();
        edicion.setPredio(new CatPredio());
        edicion.getPredio().setCatPredioS6(new CatPredioS6());
        propietarios = new ArrayList<>();
        JsfUti.update("formSelecPredio");
        JsfUti.executeJS("PF('dlgSelectPredio').show();");
    }

    public void selectPredio(CatPredio pred) {
        try {
            RegFicha ficha = fichaServices.getRegFichaByPredio(pred.getId());
            if (ficha != null) {
                JsfUti.messageInfo(null, "El Predio con numero: " + pred.getNumPredio() + ", ya esta registrado con la Ficha: " + ficha.getNumFicha(), "");
                return;
            }

            edicion.getFicha().setPredio(pred);
            edicion.setPredio(pred);
            propietarios = (List<CatPredioPropietario>) pred.getCatPredioPropietarioCollection();
            edicion.setNumeroFicha(pred.getNumPredio().longValue());
            if (pred.getPhv() > 0 || pred.getPhh() > 0) {
                edicion.setPropiedadHorz(Boolean.TRUE);
            }
            edicion.setCodPredio(pred.getSector() + "." + pred.getMz() + "." + pred.getCdla() + "." + pred.getMzdiv() + "." + pred.getSolar() + "." + pred.getDiv1() + "." + pred.getDiv2() + "." + pred.getDiv3() + "." + pred.getDiv4()
                    + "." + pred.getDiv5() + "." + pred.getDiv6() + "." + pred.getDiv7() + "." + pred.getDiv8() + "." + pred.getDiv9() + "." + pred.getPhv() + "." + pred.getPhh());
            //edicion.getFicha().setCodigoPredial(edicion.getCodPredio());
            if (pred.getCatPredioS6() == null) {
                edicion.getPredio().setCatPredioS6(new CatPredioS6());
            }
            if (pred.getCiudadela() != null) {
                edicion.getFicha().setCiudadela(pred.getCiudadela());
                /*if (pred.getCiudadela().getCodParroquia() != null) {
                    edicion.getFicha().setParroquia(pred.getCiudadela().getCodParroquia());
                    this.actualizarCiudadelas();
                }*/
            }
            /*CatEscritura escrituraT = fichaServices.getCatEscrituraByPredio(pred.getId());
             if (escrituraT != null) {
             edicion.setEscritura(escrituraT);
             }*/
            edicion.setMostrarUrbano(true);
            JsfUti.update("formEditFicha");
            JsfUti.executeJS("PF('dlgSelectPredio').hide()");
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoEditar.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarMovientos(RegMovimiento mov) {
        RegMovimientoFicha mf = new RegMovimientoFicha();
        if (!existeRegMovimientoAgregado(mov)) {
            mf.setMovimiento(mov);
            listMovFich.add(mf);
        } else {
            JsfUti.messageInfo(null, "Este movimiento ya fue agregado a esta ficha", "");
        }
        JsfUti.update("formEditFicha");
    }

    public String getCorreo(CatEnte e) {
        List<EnteCorreo> ec = fichaServices.getEnteCorreoList(e.getId());
        if (ec.size() > 0) {
            return ec.get(0).getEmail();
        }
        return null;
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = edicion.getTipo().size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(edicion.getTipo().get(i), edicion.getTipo().get(i));
        }
        return options;
    }

    public void eliminarEmail(EnteCorreo c) {
        int index = 0;
        for (EnteCorreo ec : edicion.getListCorreo()) {
            if (c.getEmail().compareTo(ec.getEmail()) == 0) {
                index = edicion.getListCorreo().indexOf(ec);
                break;
            }
        }
        if (c.getId() != null) {
            edicion.getListCorreoElim().add(edicion.getListCorreo().get(index));
        }
        edicion.getListCorreo().remove(index);
    }

    public void eliminarTelefono(EnteTelefono t) {
        int index = 0;
        if (t.getId() != null) {
            for (EnteTelefono et : edicion.getListTelefonos()) {
                if (t.getId().compareTo(et.getId()) == 0) {
                    index = edicion.getListTelefonos().indexOf(et);
                    break;
                }
            }
            edicion.getListTelefonosElim().add(edicion.getListTelefonos().get(index));
        } else {
            for (EnteTelefono et : edicion.getListTelefonos()) {
                if (t.getTelefono().compareTo(et.getTelefono()) == 0) {
                    index = edicion.getListTelefonos().indexOf(et);
                    break;
                }
            }
        }
        edicion.getListTelefonos().remove(index);
    }

    public void editarMovimiento(RegMovimiento m) {
        Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "faces/vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + m.getId());
    }

    public void ingresoObservacion(RegMovimientoFicha mf) {
        mfEliminar = mf;
    }

    public void eliminarMovimiento() {
        int index = -1;
        int i = 0;
        if (mfEliminar.getMovimiento().getObservacionEliminacion() != null) {
            for (RegMovimientoFicha m : listMovFich) {
                if (m.getMovimiento().getId().compareTo(mfEliminar.getMovimiento().getId()) == 0) {
                    index = i;
                }
                i++;
            }
            if (mfEliminar.getId() != null) {
                listMovFichElim.add(listMovFich.get(index));
            }
            listMovFich.remove(index);
            JsfUti.executeJS("PF('dlgEliminarReferencia').hide();");
        } else {
            JsfUti.messageInfo(null, "Ingresar observacion", "");
        }
    }

    public void saveValorCaract(RegFichaBien b) {
        edicion.setNumIdentificador("");
        if ((b.getValor() == null) || (b.getValor().equals(""))) {
            JsfUti.messageInfo(null, "El campo 'Valor' no puede estar vacio.", "");
            return;
        }
        if (b.getCaracteristica().getIsMain()) {
            edicion.setNumIdentificador(b.getValor());
        }
        JsfUti.update("formEditFicha:dtTipoBien");
    }

    public void agregarMail() {
        edicion.setCorreo(edicion.getCorreo().trim());
        if (Utils.validarEmailConExpresion(edicion.getCorreo())) {
            EnteCorreo cor = new EnteCorreo();
            cor.setEmail(edicion.getCorreo());
            edicion.getListCorreo().add(cor);
            edicion.setCorreo("");
            JsfUti.update("formEditFicha:dtEnteCorr");

        } else {
            JsfUti.messageError(null, "Correo Ingresado es invalido", "");
        }

    }

    public void agregarTelefono() {
        edicion.setTelefono(edicion.getTelefono().trim());
        if (edicion.getTelefono() == null) {
            JsfUti.messageError(null, "No a Ingresado nigun Numero de telefono", "");
            return;
        }
        if (edicion.getTelefono().equals("0999999999")) {
            JsfUti.messageError(null, "Numero de telefono Ingresado es Invalido", "");
            return;
        }
        if (!Utils.validateNumberPattern(edicion.getTelefono())) {
            JsfUti.messageError(null, "Numero de telefono Ingresado es Invalido", "");
            return;
        }
        if (PhoneUtils.getValidNumber(edicion.getTelefono(), SisVars.region)) {
            EnteTelefono tel = new EnteTelefono();
            tel.setTelefono(edicion.getTelefono());
            edicion.getListTelefonos().add(tel);
            edicion.setTelefono("");
            JsfUti.update("formEditFicha:dtEnteTel");
        }

    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue == null) {
            JsfUti.messageInfo(null, "Debe Ingresar un Valor para la caracteristica ", "");
        }
        JsfUti.update("formEditFicha:dtTipoBien");
    }

    public void guardarRegFichaBien() {
        if (edicion.getFicha().getParroquia() == null) {
            JsfUti.messageInfo(null, "Seleccione Parroquia.", "");
            return;
        }
        if (edicion.getFicha().getCiudadela() == null) {
            JsfUti.messageInfo(null, "Seleccione Ciudadela.", "");
            return;
        }
        if (edicion.getListCorreo().isEmpty()) {
            JsfUti.messageInfo(null, "Ingrese minimo un e-mail.", "");
            return;
        }
        if (edicion.getListTelefonos().isEmpty()) {
            JsfUti.messageInfo(null, "Ingrese minimo un telefono.", "");
            return;
        }
        edicion.getFicha().setRegFichaBienCollection(listFichaBien);
        CatEnte e = edicion.getFicha().getEnte();
        e.setEnteCorreoCollection(edicion.getListCorreo());
        e.setEnteTelefonoCollection(edicion.getListTelefonos());
        fichaServices.actualizarRegFichaBien(edicion.getFicha());
        fichaServices.actualizarCatEnteTelefEmails(e, edicion.getListCorreoElim(), edicion.getListTelefonosElim());
        JsfUti.redirectFaces("/faces/vistaprocesos/registroPropiedad/fichasIngresadas.xhtml");
    }

    public void guardarRegFichaMercantilCia() {
        if (listMovFich.isEmpty()) {
            JsfUti.messageInfo(null, "Debe ingresar Movimientos para esta Ficha.", "");
            return;
        }
        if (edicion.getDatosComp()) {
            edicion.getFicha().setNumFicha(Long.parseLong(edicion.getEnte().getCiRuc()));
            //edicion.getFicha().setCodigoPredial(edicion.getEnte().getCiRuc());
        }
        if (edicion.getEnte() != null) {
            edicion.getFicha().setLinderos(edicion.getEnte().getNombres());
            edicion.getFicha().setObservacion(edicion.getEnte().getDireccion());
            edicion.getFicha().setEnte(edicion.getEnte());
        }
        edicion.getFicha().setRegMovimientoFichaCollection(listMovFich);
        fichaServices.actualizarRegFichaAndListMov(edicion.getFicha(), listMovFichElim);
        JsfUti.redirectFaces("/faces/vistaprocesos/registroPropiedad/fichasIngresadas.xhtml");
    }

    public void guardarRegFichaInmobiliaria() {
        try {
            edicion.getFicha().setFechaEdicion(new Date());
            edicion.getFicha().setUserEdicion(session.getName_user());
            edicion.getFicha().setRegMovimientoFichaCollection(listMovFich);

            fichaServices.updateRegFicha(edicion.getFicha(), null, listMovFichElim, permitido);

            /*if (edicion.getFicha().getTipoPredio().equalsIgnoreCase("U")) {
             fichaServices.updateRegFicha(edicion.getFicha(), null, listMovFichElim, permitido);
             } else if (edicion.getFicha().getTipoPredio().equalsIgnoreCase("R")) {
             if (edicion.getEscrituraRural().getRegistroCatastral() != null && edicion.getEscrituraRural().getIdentificacionPredial() != null) {
             edicion.getFicha().setCatEscrituraRural(edicion.getEscrituraRural());
             }
             fichaServices.updateRegFicha(edicion.getFicha(), null, listMovFichElim, permitido);
             }*/
            if (modelo.getListMovCliSelect() != null && !modelo.getListMovCliSelect().isEmpty() && edicion.getPredio() != null) {
                fichaServices.transferirPropietarios(modelo.getListMovCliSelect(), edicion.getPredio(), propietarios, null, session.getName_user());
            } else if (edicion.getPredio() != null && seleccionados != null) {
                fichaServices.transferirPropietarios(null, edicion.getPredio(), propietarios, seleccionados, session.getName_user());
            } else if (edicion.getPredio() != null && edicion.getPredio().getId() != null) {
                manager.persist(edicion.getPredio());
            }
            JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/fichasIngresadas.xhtml");
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoEditar.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private boolean existeRegMovimientoAgregado(RegMovimiento mov) {
        for (RegMovimientoFicha mf : listMovFich) {
            if (mf.getMovimiento().equals(mov)) {
                return true;
            }
        }
        return false;
    }

    public void mostarMovimientoRegistral(RegMovimiento mov) {
        try {
            movimiento = mov;
            modelo = reg.getConsultaMovimiento(mov.getId());
            if (modelo != null) {
                JsfUti.update("formMovRegistralSelec");
                JsfUti.executeJS("PF('dlgMovRegistralSelec').show();");
            } else {
                JsfUti.messageError(null, "No se pudo hacer la consulta.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoEditar.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void listarPropietarios() {
        entesLazy = new CatEnteLazy();
    }

    public FichaIngreso getEdicion() {
        return edicion;
    }

    public void setEdicion(FichaIngreso edicion) {
        this.edicion = edicion;
    }

    public Long getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Long idFicha) {
        this.idFicha = idFicha;
    }

    public Boolean getEsInmobiliaria() {
        return esInmobiliaria;
    }

    public void setEsInmobiliaria(Boolean esInmobiliaria) {
        this.esInmobiliaria = esInmobiliaria;
    }

    public Boolean getEsMercantil() {
        return esMercantil;
    }

    public void setEsMercantil(Boolean esMercantil) {
        this.esMercantil = esMercantil;
    }

    public Boolean getEsFichaBien() {
        return esFichaBien;
    }

    public void setEsFichaBien(Boolean esFichaBien) {
        this.esFichaBien = esFichaBien;
    }

    public Boolean getEsBoolean() {
        return esBoolean;
    }

    public void setEsBoolean(Boolean esBoolean) {
        this.esBoolean = esBoolean;
    }

    public List<CatParroquia> getParroquiaList() {
        return parroquiaList;
    }

    public void setParroquiaList(List<CatParroquia> parroquiaList) {
        this.parroquiaList = parroquiaList;
    }

    public List<CatCiudadela> getCiudadelaList() {
        return ciudadelaList;
    }

    public void setCiudadelaList(List<CatCiudadela> ciudadelaList) {
        this.ciudadelaList = ciudadelaList;
    }

    public List<RegMovimientoFicha> getListMovFich() {
        return listMovFich;
    }

    public void setListMovFich(List<RegMovimientoFicha> listMovFich) {
        this.listMovFich = listMovFich;
    }

    public List<RegFichaBien> getListFichaBien() {
        return listFichaBien;
    }

    public void setListFichaBien(List<RegFichaBien> listFichaBien) {
        this.listFichaBien = listFichaBien;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public List<CtlgItem> getEstadosRegFichaList() {
        return estadosRegFichaList;
    }

    public void setEstadosRegFichaList(List<CtlgItem> estadosRegFichaList) {
        this.estadosRegFichaList = estadosRegFichaList;
    }

    public Boolean getIsPropiedadHorizontal() {
        return isPropiedadHorizontal;
    }

    public void setIsPropiedadHorizontal(Boolean isPropiedadHorizontal) {
        this.isPropiedadHorizontal = isPropiedadHorizontal;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public String getTipoBien() {
        return tipoBien;
    }

    public void setTipoBien(String tipoBien) {
        this.tipoBien = tipoBien;
    }

    public RegMovimientosLazy getMovimientosLazy() {
        return movimientosLazy;
    }

    public void setMovimientosLazy(RegMovimientosLazy movimientosLazy) {
        this.movimientosLazy = movimientosLazy;
    }

    public List<RegMovimientoFicha> getListMovFichElim() {
        return listMovFichElim;
    }

    public void setListMovFichElim(List<RegMovimientoFicha> listMovFichElim) {
        this.listMovFichElim = listMovFichElim;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public ConsultaMovimientoModel getModelo() {
        return modelo;
    }

    public void setModelo(ConsultaMovimientoModel modelo) {
        this.modelo = modelo;
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RegMovimientoFicha getMfEliminar() {
        return mfEliminar;
    }

    public void setMfEliminar(RegMovimientoFicha mfEliminar) {
        this.mfEliminar = mfEliminar;
    }

    public Boolean getSelectMenu() {
        return selectMenu;
    }

    public void setSelectMenu(Boolean selectMenu) {
        this.selectMenu = selectMenu;
    }

    public Boolean getPermitido() {
        return permitido;
    }

    public void setPermitido(Boolean permitido) {
        this.permitido = permitido;
    }

    public CatEnteLazy getEntesLazy() {
        return entesLazy;
    }

    public void setEntesLazy(CatEnteLazy entesLazy) {
        this.entesLazy = entesLazy;
    }

    public List<CatEnte> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatEnte> seleccionados) {
        this.seleccionados = seleccionados;
    }

}
