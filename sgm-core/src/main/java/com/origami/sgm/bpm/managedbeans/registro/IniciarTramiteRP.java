/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegTablaCuantia;
import com.origami.sgm.entities.RegTablaCuantiaDeterminada;
import com.origami.sgm.entities.RegpActoTipoActo;
import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.sgm.entities.RegpIntervinientes;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RegpLiquidacionDetalles;
import com.origami.sgm.entities.VuItems;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
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
public class IniciarTramiteRP extends ClienteTramite implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    //@javax.inject.Inject
    //protected ConsultasSqlLocal con;
    @javax.inject.Inject
    protected CatastroServices cat;

    @Inject
    private ServletSession servletSession;

    /**
     * ***********************************************************************
     * Variables para la edicion de la liquidacion
     */
    protected Long idMov;
    protected RegpLiquidacionDerechosAranceles liq;
    protected HistoricoTramites ht;

    /**
     * ***********************************************************************
     * Variables para la edicion de la liquidacion
     */
    protected BigInteger numTramiteAntiguo;
    protected Date fechaAntigua;
    protected BigInteger numComprobante;
    protected Boolean antiguo = false;
    protected Boolean oldValido = false;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    /**
     * ***********************************************************************
     */

    protected String state = "";
    protected String vacio = "";
    protected Integer numPredio;
    protected String urbanizacion = "";
    protected String manzana = "";
    protected String solar = "";

    protected String ciInterv = "";
    protected String parentesco = "";
    protected String obsAdicional = "";
    protected String nombreInterv = "";
    protected String nomInscrip = "", nomCertf = "";
    protected String listCorreos = "", listTlfns = "";
    protected CatEnte solicitante = new CatEnte();
    protected RegpIntervinientes propietario = new RegpIntervinientes();
    protected Boolean disableBtn = false;
    protected Boolean esPersona = true;
    protected Boolean esNatural = true;
    protected Boolean exonerado = false;
    protected Boolean esInscripcion = false;
    protected Boolean esCertificado = false;
    protected Boolean esPropiedad;
    protected Boolean solicitanteFlag = false;
    protected VuItems usoDocumento;
    protected VuItems nuevoUsoDoc;
    protected RegpLiquidacionDerechosAranceles rlda = new RegpLiquidacionDerechosAranceles();
    protected List<RegpIntervinientes> listInterv = new ArrayList<>();
    protected List<EnteCorreo> correosSol = new ArrayList<>();
    protected List<EnteTelefono> tlfnsSol = new ArrayList<>();
    protected String processDefinitionKey;
    protected BigDecimal subTotal = BigDecimal.ZERO;
    protected BigDecimal totalPagar = BigDecimal.ZERO;
    protected BigDecimal gastosGenerales = BigDecimal.ZERO;
    protected BigDecimal descuentoPorcentaje = BigDecimal.ZERO;
    protected BigDecimal descuentoValor = BigDecimal.ZERO;
    protected Integer cantidadCatastro = 0;
    protected BigDecimal cantidad = BigDecimal.ONE;
    protected BigDecimal valorCatastro = BigDecimal.ZERO;
    protected RegpActosIngreso actoNew = new RegpActosIngreso();
    protected List<RegpLiquidacionDetalles> actosPorPagar = new ArrayList<>();
    protected List<RegpActosIngreso> listActosInscrp = new ArrayList<>();
    protected List<RegpActosIngreso> listActosCertf = new ArrayList<>();
    protected List<RegpActosIngreso> actosSelect = new ArrayList<>();
    protected HashMap<String, Object> parametros;
    protected CatEnteLazy entesLazy;
    protected GeTipoTramite tramite;
    protected Boolean esAvaluo;

    protected BigDecimal avaluo;
    protected BigDecimal cuantia;
    protected BigDecimal valorIngresado = BigDecimal.ZERO;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            if (state != null) {
                switch (state) {
                    case "new":
                        oldValido = true;
                        break;
                    case "edit":
                        oldValido = true;
                        this.cargarDatosEdicion();
                        break;
                    case "old":
                        antiguo = true;
                        break;
                    default:
                        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
                        break;
                }
                this.cargarListas();
            } else {
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
            }
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarDatosEdicion() {
        if (idMov != null) {
            liq = (RegpLiquidacionDerechosAranceles) acl.find(RegpLiquidacionDerechosAranceles.class, idMov);
            if (liq == null) {
                JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
            } else {
                ht = liq.getHistoricTramite();
                solicitante = ht.getSolicitante();
                parentesco = liq.getParentescoSolicitante();
                listInterv = (List<RegpIntervinientes>) liq.getRegpIntervinientesCollection();
                actosPorPagar = (List<RegpLiquidacionDetalles>) liq.getRegpLiquidacionDetallesCollection();
                descuentoPorcentaje = liq.getDescuentoPorc();
                this.sumaTotal();
                if (liq.getInfAdicional() != null) {
                    obsAdicional = liq.getInfAdicional();
                }
                if (liq.getUsoDocumento() != null) {
                    usoDocumento = liq.getUsoDocumento();
                }
                exonerado = liq.getIsExoneracion();
                valorCatastro = liq.getTasaCatastro();
                cantidadCatastro = liq.getCantidadTasasCatastro().intValue();
            }
        } else {
            JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
        }
    }

    public void cargarListas() {
        RegpActoTipoActo inscripciones = (RegpActoTipoActo) acl.find(RegpActoTipoActo.class, 1L);
        RegpActoTipoActo certificados = (RegpActoTipoActo) acl.find(RegpActoTipoActo.class, 2L);
        listActosInscrp = (List<RegpActosIngreso>) inscripciones.getRegpActosIngresoCollection();
        listActosCertf = (List<RegpActosIngreso>) certificados.getRegpActosIngresoCollection();
        processDefinitionKey = "transferenciaDominio";
        esPropiedad = true;
    }

    public void validarInfoAntiguo() {
        try {
            //if (con.getConnection() == null) {
            //JsfUti.messageWarning(null, "No hay conexion con el Sistema de cobro. Comuniquese con sistemas.", "");
            //} else {
            if (numTramiteAntiguo != null && fechaAntigua != null && numComprobante != null
                    && Utils.validateNumberPattern(numTramiteAntiguo.toString()) && Utils.validateNumberPattern(numComprobante.toString())) {
                rlda = reg.getLiquidacionByNumYFecha(numTramiteAntiguo, sdf.format(fechaAntigua));
                if (rlda == null) {
                    oldValido = true;
                    JsfUti.messageInfo(null, "Numero de comprobante aceptado. Ingrese la informacion del tramite.", "");
                    JsfUti.update("mainForm");
                    /*Object ob = con.getUniqueResult(Querys.getTotalPagarByNumComprebanteSAC, new Object[]{numComprobante.longValue()});
                     if (ob != null) {
                     oldValido = true;
                     JsfUti.messageInfo(null, "Numero de comprobante aceptado. Ingrese la informacion del tramite.", "");
                     JsfUti.update("mainForm");
                     } else {
                     JsfUti.messageWarning(null, "Numero de Comprobante de Pago no se encuentra en el sistema.", "");
                     }*/
                } else {
                    JsfUti.messageWarning(null, "Ya se encuentra una liquidacion con ese numero de tramite y el mismo anio.", "");
                }
            } else {
                JsfUti.messageInfo(null, "Valores no Validos", "Todos los campos son obligatorios y solo numeros.");
            }
            //}
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarSolicitante() {
        if (solicitante.getCiRuc() != null) {
            if (!Utils.isNum(solicitante.getCiRuc())) {
                JsfUti.messageError(null, "Numero de cedula o ruc ingresado es invalido.", "");
                return;
            }
            if (solicitante.getCiRuc().length() == 10 || solicitante.getCiRuc().length() == 13) {
                //solicitanteNatural = solicitante.getCiRuc().length() == 10;
                String temp = solicitante.getCiRuc();
                solicitante = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{solicitante.getCiRuc()});
                if (solicitante == null) {
                    solicitante = new CatEnte();
                    this.inicializarVariables();
                    persona.setEsPersona(temp.length() == 10);
                    persona.setCiRuc(temp);
                    JsfUti.messageInfo(null, Messages.enteNoExiste, "");
                    JsfUti.update("formSolicitante");
                    JsfUti.executeJS("PF('dlgSolInf').show();");
                }
            } else {
                JsfUti.messageError(null, "Numero de cedula o ruc ingresado es invalido.", "");
            }
        } else {
            solicitanteFlag = true;
            entesLazy = new CatEnteLazy(esNatural);
            JsfUti.update("formSelectInterv");
            JsfUti.executeJS("PF('dlgSelectInt').show();");
        }
    }

    public void showDlgEditarSol() {
        if (solicitante.getId() != null) {
            persona = solicitante;
            JsfUti.update("formSolicitante");
            JsfUti.executeJS("PF('dlgSolInf').show();");
        } else {
            JsfUti.messageError(null, Messages.noAsignadoPersona, "");
        }
    }

    public void showDlgSolicitanteSinDoc() {
        solicitante = new CatEnte();
        JsfUti.update("formSolSinDoc");
        JsfUti.executeJS("PF('solSinDoc').show();");
    }

    public void saveSolicitanteSinDoc() {
        try {
            if (solicitante.getEsPersona()) {
                if (solicitante.getNombres() == null || solicitante.getApellidos() == null) {
                    JsfUti.messageError(null, "Los Nombres y Apellidos son obligatorios.", "");
                    return;
                }
            } else {
                if (solicitante.getRazonSocial() == null) {
                    JsfUti.messageError(null, "EL campo Razon Social es obligatorio.", "");
                    return;
                }
            }
            solicitante = reg.saveEnteSinCedRuc(solicitante);
            //solicitante = (CatEnte) acl.find(CatEnte.class, solicitante.getId());
            JsfUti.executeJS("PF('solSinDoc').hide();");
            JsfUti.executeJS("PF('dlgSelectInt').hide();");
            JsfUti.update("mainForm:accPanelRP:panelSolicitante");
            JsfUti.messageInfo(null, "Solicitante guardado con exito.", "");
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarSolicitante() {
        Boolean flag;
        if (solicitante.getId() == null) {
            flag = guardarClienteSinCorreo();
        } else {
            flag = editarClienteSinCorreo();
        }
        if (flag) {
            solicitante = persona;
            JsfUti.executeJS("PF('dlgSolInf').hide();");
            JsfUti.update("mainForm:accPanelRP:panelSolicitante");
        }
    }

    public void cancelarGuardado() {
        inicializarVariables();
    }

    public List<RegCatPapel> complete(String query) {
        List<RegCatPapel> results = acl.findMax(Querys.getRegCatPapelByPapel, new String[]{"papel"}, new Object[]{query + "%"}, 5);
        return results;
    }

    public void buscarInterviniente() {
        if (ciInterv != null) {
            //if (vcu.comprobarDocumento(ciInterv)) {
            if (Utils.isNum(ciInterv)) {
                if (ciInterv.length() == 10 || ciInterv.length() == 13) {
                    if (!this.comprobarExitente(ciInterv)) {
                        CatEnte temp = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciInterv});
                        if (temp != null) {
                            RegpIntervinientes intv = new RegpIntervinientes();
                            if (temp.getEsPersona()) {
                                intv.setNombres(temp.getNombres() + " " + temp.getApellidos());
                            } else {
                                intv.setNombres(temp.getRazonSocial());
                            }
                            intv.setEnte(temp);
                            listInterv.add(intv);
                            ciInterv = "";
                        } else {
                            this.inicializarVariables();
                            persona.setCiRuc(ciInterv);
                            persona.setEsPersona(ciInterv.length() == 10);
                            esPersona = ciInterv.length() == 10;
                            JsfUti.update("formInterv");
                            JsfUti.executeJS("PF('dlgInterv').show();");
                        }
                    } else {
                        JsfUti.messageWarning(null, Messages.elementoRepetido, "");
                    }
                } else {
                    JsfUti.messageError(null, Messages.cedulaCIinvalida, "");
                }
            } else {
                JsfUti.messageError(null, Messages.cedulaCIinvalida, "");
            }
        } else {
            JsfUti.messageWarning(null, Messages.faltanCampos, "");
        }
    }

    public Boolean comprobarExitente(String doc) {
        if (!listInterv.isEmpty()) {
            for (RegpIntervinientes r : listInterv) {
                if (r.getEnte() != null) {
                    if (r.getEnte().getCiRuc().equals(doc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean comprobarExitenteID(Long id) {
        if (!listInterv.isEmpty()) {
            for (RegpIntervinientes r : listInterv) {
                if (r.getEnte() != null) {
                    if (r.getEnte().getId().equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void guardarInterviniente() {
        Boolean flag;
        Boolean nuevo;
        persona.setEsPersona(esPersona);
        if (persona.getId() == null) {
            flag = guardarClienteSinCorreo();
            nuevo = true;
        } else {
            flag = editarClienteSinCorreo();
            nuevo = false;
        }
        if (flag) {
            CatEnte temp = (CatEnte) acl.find(CatEnte.class, persona.getId());
            RegpIntervinientes intv = new RegpIntervinientes();
            intv.setEnte(temp);
            if (temp.getEsPersona()) {
                intv.setNombres(temp.getNombres() + " " + temp.getApellidos());
            } else {
                intv.setNombres(temp.getRazonSocial());
            }
            if (nuevo) {
                listInterv.add(intv);
            } else {
                int ind = returnIndex(persona);
                listInterv.set(ind, intv);
            }
            JsfUti.executeJS("PF('dlgInterv').hide();");
            JsfUti.update("mainForm:accPanelRP:dtIntervinientes");
        }
    }

    public void showDlgEditarInterv(int index) {
        RegpIntervinientes intv = listInterv.get(index);
        if (intv.getEnte() == null) {
            nombreInterv = intv.getNombres();
            listInterv.remove(index);
            JsfUti.update("formNombreInterv");
            JsfUti.executeJS("PF('dlgIntervSinDoc').show();");
        } else {
            persona = intv.getEnte();
            esPersona = intv.getEnte().getEsPersona();
            JsfUti.update("formInterv");
            JsfUti.executeJS("PF('dlgInterv').show();");
        }
    }

    public int returnIndex(CatEnte e) {
        int temp = 0;
        int ind = -1;
        for (RegpIntervinientes i : listInterv) {
            if (i.getEnte() != null) {
                if (i.getEnte().getId().equals(e.getId())) {
                    ind = temp;
                }
            }
            temp++;
        }
        return ind;
    }

    public void eliminarInterviniente(int index) {
        try {
            RegpIntervinientes in = listInterv.get(index);
            listInterv.remove(index);
            if (in.getId() != null) {
                acl.delete(in);
            }
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgSelectInterv() {
        solicitanteFlag = false;
        entesLazy = new CatEnteLazy(esNatural);
        JsfUti.update("formSelectInterv");
        JsfUti.executeJS("PF('dlgSelectInt').show();");
    }

    public void agregarInterv(CatEnte ente) {
        if (solicitanteFlag) {
            solicitante = ente;
            JsfUti.executeJS("PF('dlgSelectInt').hide();");
            JsfUti.update("mainForm:accPanelRP:panelSolicitante");
        } else {
            if (!this.comprobarExitenteID(ente.getId())) {
                RegpIntervinientes temp = new RegpIntervinientes();
                temp.setEnte(ente);
                if (ente.getEsPersona()) {
                    temp.setNombres(ente.getNombres() + " " + ente.getApellidos());
                } else {
                    temp.setNombres(ente.getRazonSocial());
                }
                listInterv.add(temp);
                JsfUti.executeJS("PF('dlgSelectInt').hide();");
                JsfUti.update("mainForm:accPanelRP:dtIntervinientes");
            } else {
                JsfUti.messageWarning(null, Messages.elementoRepetido, "");
            }
        }
    }

    public void seleccionarInterv(int index) {
        for (RegpIntervinientes inter : listInterv) {
            inter.setEsBeneficiario(false);
        }
        listInterv.get(index).setEsBeneficiario(true);
    }

    public void cambioTipoPersona() {
        entesLazy = new CatEnteLazy(esNatural);
    }

    public void showDlgNombreInterv() {
        nombreInterv = "";
        JsfUti.executeJS("PF('dlgSelectInt').hide();");
        JsfUti.update("formNombreInterv");
        JsfUti.executeJS("PF('dlgIntervSinDoc').show();");
    }

    public void agregarIntervSinDoc() {
        if (nombreInterv != null) {
            RegpIntervinientes temp = new RegpIntervinientes();
            temp.setNombres(nombreInterv);
            listInterv.add(temp);
            JsfUti.executeJS("PF('dlgIntervSinDoc').hide();");
            JsfUti.update("mainForm:accPanelRP:dtIntervinientes");
        } else {
            JsfUti.messageWarning(null, Messages.campoVacio, "");
        }
    }

    public void buscarInscripciones() {
        if (nomInscrip != null) {
            listActosInscrp = acl.findAll(Querys.getListRegpActosInscripByNom, new String[]{"nombre", "idTipo"}, new Object[]{"%" + nomInscrip.toUpperCase() + "%", 1L});
        } else {
            RegpActoTipoActo inscripciones = (RegpActoTipoActo) acl.find(RegpActoTipoActo.class, 1L);
            listActosInscrp = (List<RegpActosIngreso>) inscripciones.getRegpActosIngresoCollection();
        }
    }

    public void buscarCertificados() {
        if (nomCertf != null) {
            listActosCertf = acl.findAll(Querys.getListRegpActosCertifByNom, new String[]{"nombre", "idTipo"}, new Object[]{"%" + nomCertf.toUpperCase() + "%", 2L});
        } else {
            RegpActoTipoActo certificados = (RegpActoTipoActo) acl.find(RegpActoTipoActo.class, 2L);
            listActosCertf = (List<RegpActosIngreso>) certificados.getRegpActosIngresoCollection();
        }
    }

    public void agregarDetalles() {
        actoNew = actosSelect.get(0);
        switch (actoNew.getTipoCobro().getId().intValue()) {
            case 1:
                // TIPO DE ACTO SE CALCULA DIRECTO
                this.calcularValores();
                break;
            case 2: // NUMERO - CANTIDAD
                cantidad = BigDecimal.ONE;
                JsfUti.update("formTipoActo2");
                JsfUti.executeJS("PF('dlgTipoActo2').show();");
                break;
            case 3: // CALCULO CON AVALUO/CUANTIA - PROPIEDAD
                JsfUti.update("formTipoActo3");
                JsfUti.executeJS("PF('dlgTipoActo3').show();");
                break;
            case 4: // INGRESA VALOR A PAGAR
                cantidad = BigDecimal.ONE;
                valorIngresado = BigDecimal.ZERO;
                JsfUti.update("formTipoActo4");
                JsfUti.executeJS("PF('dlgTipoActo4').show();");
                break;
            case 5: // PORCENTAJE
                JsfUti.update("formTipoActo5");
                JsfUti.executeJS("PF('dlgTipoActo5').show();");
                break;
            case 6: // NUMERO DE HEREDEROS
                cantidad = BigDecimal.ZERO;
                JsfUti.update("formTipoActo6");
                JsfUti.executeJS("PF('dlgTipoActo6').show();");
                break;
            case 7: // VALOR MAXIMO - es el mismo dialogo del tipo 3
                JsfUti.update("formTipoActo3");
                JsfUti.executeJS("PF('dlgTipoActo3').show();");
                break;
            case 8: // Proyecto Urbanistico
                JsfUti.update("formTipoActo8");
                JsfUti.executeJS("PF('dlgTipoActo8').show();");
                break;
            case 9: // Ingresa Cantidad y Valor por Unidad
                cantidad = BigDecimal.ONE;
                valorIngresado = BigDecimal.ZERO;
                JsfUti.update("formTipoActo9");
                JsfUti.executeJS("PF('dlgTipoActo9').show();");
                break;
            default:
                JsfUti.messageWarning(null, Messages.error, "");
                break;
        }
        actosSelect = new ArrayList<>();
    }

    public void calcularValores() {
        RegpLiquidacionDetalles d;
        switch (actoNew.getTipoCobro().getId().intValue()) {
            case 1: // TIPO DE ACTO SE CALCULA DIRECTO
                this.ingresarActoPorCantidad(BigDecimal.ONE);
                break;
            case 2: // NUMERO - CANTIDAD
                if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
                    this.ingresarActoPorCantidad(cantidad);
                    JsfUti.executeJS("PF('dlgTipoActo2').hide();");
                } else {
                    JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                }
                break;
            case 3: // CALCULO CON AVALUO/CUANTIA - PROPIEDAD
                if (validaCuantiaAvaluo()) {
                    d = new RegpLiquidacionDetalles();
                    d.setNumPredio(numPredio);
                    d.setNomUrb(urbanizacion);
                    d.setMzUrb(manzana);
                    d.setSlUrb(solar);
                    d.setCantidad(1);
                    if (avaluo != null) {
                        d.setAvaluo(avaluo);
                    }
                    if (cuantia != null) {
                        d.setCuantia(cuantia);
                    }
                    d.setValorUnitario(actoNew.getValor());
                    if (esAvaluo) {
                        d.setValorTotal(this.calculoCuantia(avaluo));
                    } else {
                        d.setValorTotal(this.calculoCuantia(cuantia));
                    }
                    d.setActo(actoNew);
                    actosPorPagar.add(d);
                    cuantia = null;
                    esAvaluo = null;
                    numPredio = null;
                    this.inicializaInfoPredio();
                    JsfUti.executeJS("PF('dlgTipoActo3').hide();");
                    JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                    JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                }
                break;
            case 4: // INGRESA VALOR A COBRAR
                if (cantidad != null && valorIngresado != null) {
                    if (valorIngresado.compareTo(BigDecimal.ZERO) > 0 && valorIngresado.compareTo(BigDecimal.ZERO) > 0) {
                        d = new RegpLiquidacionDetalles();
                        d.setCantidad(cantidad.intValue());
                        d.setValorUnitario(actoNew.getValor());
                        d.setValorTotal(valorIngresado.setScale(2));
                        d.setActo(actoNew);
                        actosPorPagar.add(d);
                        JsfUti.executeJS("PF('dlgTipoActo4').hide();");
                        JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                        JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                    } else {
                        JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                    }
                } else {
                    JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                }
                break;
            case 5: // PORCENTAJE
                if (validaCuantiaAvaluo()) {
                    d = new RegpLiquidacionDetalles();
                    d.setCantidad(1);
                    if (avaluo != null) {
                        d.setAvaluo(avaluo);
                    }
                    if (cuantia != null) {
                        d.setCuantia(cuantia);
                    }
                    d.setValorUnitario(actoNew.getValor());
                    BigDecimal temp;
                    if (esAvaluo) {
                        temp = this.calculoCuantia(avaluo).multiply(actoNew.getPorcentaje());
                    } else {
                        temp = this.calculoCuantia(cuantia).multiply(actoNew.getPorcentaje());
                    }
                    d.setValorTotal(Utils.bigdecimalTo2Decimals(temp));
                    d.setActo(actoNew);
                    actosPorPagar.add(d);
                    avaluo = null;
                    cuantia = null;
                    esAvaluo = null;
                    JsfUti.executeJS("PF('dlgTipoActo5').hide();");
                    JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                    JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                }
                break;
            case 6: // NUMERO DE HEREDEROS
                if (this.validaCuantiaAvaluo()) {
                    if (cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0) {
                        d = new RegpLiquidacionDetalles();
                        d.setNumPredio(numPredio);
                        d.setNomUrb(urbanizacion);
                        d.setMzUrb(manzana);
                        d.setSlUrb(solar);
                        d.setCantidad(1);
                        if (avaluo != null) {
                            d.setAvaluo(avaluo);
                        }
                        if (cuantia != null) {
                            d.setCuantia(cuantia);
                        }
                        d.setValorUnitario(actoNew.getValor());
                        BigDecimal temp;
                        if (esAvaluo) {
                            temp = avaluo.divide(cantidad, 6, RoundingMode.HALF_UP);
                        } else {
                            temp = cuantia.divide(cantidad, 6, RoundingMode.HALF_UP);
                        }
                        d.setValorTotal(this.calculoCuantia(temp).multiply(cantidad));
                        d.setActo(actoNew);
                        actosPorPagar.add(d);
                        cuantia = null;
                        esAvaluo = null;
                        numPredio = null;
                        this.inicializaInfoPredio();
                        JsfUti.executeJS("PF('dlgTipoActo6').hide();");
                        JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                        JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                    } else {
                        JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                    }
                }
                break;
            case 7: // VALOR MAXIMO
                if (this.validaCuantiaAvaluo()) {
                    d = new RegpLiquidacionDetalles();
                    d.setNumPredio(numPredio);
                    d.setNomUrb(urbanizacion);
                    d.setMzUrb(manzana);
                    d.setSlUrb(solar);
                    d.setCantidad(1);
                    if (avaluo != null) {
                        d.setAvaluo(avaluo);
                    }
                    if (cuantia != null) {
                        d.setCuantia(cuantia);
                    }
                    d.setValorUnitario(actoNew.getValor());
                    d.setValorTotal(this.calculoValorMaximo());
                    d.setActo(actoNew);
                    actosPorPagar.add(d);
                    cuantia = null;
                    esAvaluo = null;
                    numPredio = null;
                    this.inicializaInfoPredio();
                    JsfUti.executeJS("PF('dlgTipoActo3').hide();");
                    JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                    JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                }
                break;
            case 8: //Proyecto urbanistico - se calcula como el tipo 3 (Compraventa) pero se ingresa la cantidad de lotes
                if (validaCuantiaAvaluo()) {
                    d = new RegpLiquidacionDetalles();
                    d.setCantidad(cantidad.intValue());
                    if (avaluo != null) {
                        d.setAvaluo(avaluo);
                    }
                    if (cuantia != null) {
                        d.setCuantia(cuantia);
                    }
                    d.setValorUnitario(actoNew.getValor());
                    if (esAvaluo) {
                        d.setValorTotal(this.calculoCuantia(avaluo));
                    } else {
                        d.setValorTotal(this.calculoCuantia(cuantia));
                    }
                    d.setActo(actoNew);
                    actosPorPagar.add(d);
                    avaluo = null;
                    cuantia = null;
                    esAvaluo = null;
                    JsfUti.executeJS("PF('dlgTipoActo8').hide();");
                    JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                    JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                }
                break;
            case 9:
                if (cantidad != null && valorIngresado != null) {
                    if (valorIngresado.compareTo(BigDecimal.ZERO) > 0 && valorIngresado.compareTo(BigDecimal.ZERO) > 0) {
                        d = new RegpLiquidacionDetalles();
                        d.setCantidad(cantidad.intValue());
                        d.setValorUnitario(valorIngresado);
                        d.setValorTotal(cantidad.multiply(valorIngresado).setScale(2));
                        d.setActo(actoNew);
                        d.setObservacion("($" + valorIngresado.setScale(2) + " c/u)");
                        actosPorPagar.add(d);
                        JsfUti.executeJS("PF('dlgTipoActo9').hide();");
                        JsfUti.update("mainForm:accPanelRP:dtSubTotal");
                        JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
                    } else {
                        JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                    }
                } else {
                    JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
                }
                break;
            default:
                JsfUti.messageWarning(null, Messages.error, "");
                break;

        }
        this.sumaTotal();
    }

    public void ingresarActoPorCantidad(BigDecimal cant) {
        RegpLiquidacionDetalles d = new RegpLiquidacionDetalles();
        d.setCantidad(cant.intValue());
        d.setValorUnitario(actoNew.getValor());
        d.setValorTotal(cant.multiply(actoNew.getValor()));
        d.setActo(actoNew);
        actosPorPagar.add(d);
        JsfUti.update("mainForm:accPanelRP:dtSubTotal");
        JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
    }

    public Boolean validaCuantiaAvaluo() {
        Boolean flag = false;
        if (esAvaluo == null) {
            JsfUti.messageWarning(null, "Debe seleccionar la Base Imponible de calculo.", "");
        } else {
            BigDecimal temp;
            if (esAvaluo) {
                temp = avaluo;
            } else {
                temp = cuantia;
            }
            if (temp == null) {
                JsfUti.messageWarning(null, "Valor ingresado es incorrecto.", "");
            } else {
                if (temp.compareTo(BigDecimal.valueOf(20)) > 0) {
                    flag = true;
                } else {
                    JsfUti.messageWarning(null, "El valor debe ser mayor a 20.", "");
                }
            }
        }
        return flag;
    }

    public BigDecimal calculoCuantia(BigDecimal valor) {
        BigDecimal total = BigDecimal.ZERO;
        List<RegTablaCuantia> list = acl.findAll(RegTablaCuantia.class);
        if (list == null) {
            JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
        } else {
            for (RegTablaCuantia t : list) {
                if (t.getValor2() != null) {
                    if ((valor.compareTo(t.getValor1()) >= 0) && (valor.compareTo(t.getValor2()) <= 0)) {
                        total = t.getCancelar();
                    }
                }
            }
            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                for (RegTablaCuantia t : list) {
                    if (t.getCancelar() == null) {
                        total = t.getValorBase();
                        valor = valor.subtract(t.getCantidadBase());
                        valor = valor.multiply(t.getExceso());
                        total = total.add(valor);
                        total = Utils.bigdecimalTo2Decimals(total);
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calculoCuantiaDeterminada(BigDecimal valor) {
        BigDecimal total = BigDecimal.ZERO;
        List<RegTablaCuantiaDeterminada> list = acl.findAll(RegTablaCuantiaDeterminada.class);
        if (list == null) {
            JsfUti.messageWarning(null, Messages.valorIncorrecto, "");
        } else {
            for (RegTablaCuantiaDeterminada t : list) {
                if (t.getValorFinal() != null) {
                    if ((valor.compareTo(t.getValorInicial()) >= 0) && (valor.compareTo(t.getValorFinal()) <= 0)) {
                        total = t.getTotalCobrar();
                    }
                }
            }
            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                for (RegTablaCuantiaDeterminada t : list) {
                    if (t.getValorFinal() == null) {
                        total = t.getValorBase();
                        valor = valor.subtract(t.getCantidadBase());
                        valor = valor.multiply(t.getExcesoValor());
                        total = total.add(valor);
                        total = Utils.bigdecimalTo2Decimals(total);
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal calculoValorMaximo() {
        BigDecimal valorTemp = BigDecimal.ZERO;
        BigDecimal cuantiaTemp;
        int cont = 0;
        for (RegpLiquidacionDetalles acc : actosPorPagar) {
            if (acc.getActo().getId().equals(actoNew.getId())) {
                valorTemp = valorTemp.add(acc.getValorTotal());
                cont++;
            }
        }
        if (esAvaluo) {
            cuantiaTemp = this.calculoCuantia(avaluo);
        } else {
            cuantiaTemp = this.calculoCuantia(cuantia);
        }

        if (valorTemp.compareTo(BigDecimal.ZERO) > 0) {
            valorTemp = valorTemp.add(cuantiaTemp);
            if (valorTemp.compareTo(actoNew.getValorMax()) == 1) {
                BigDecimal division = actoNew.getValorMax().divide(BigDecimal.valueOf(cont + 1), 6, RoundingMode.HALF_UP);
                division = Utils.bigdecimalTo2Decimals(division);
                for (RegpLiquidacionDetalles acc : actosPorPagar) {
                    if (acc.getActo().getId().equals(actoNew.getId())) {
                        acc.setValorTotal(division);
                    }
                }
                BigDecimal multi = division.multiply(BigDecimal.valueOf(cont + 1));
                if (multi.compareTo(actoNew.getValorMax()) > 0) {
                    valorTemp = multi.subtract(actoNew.getValorMax());
                    division = division.subtract(valorTemp);
                } else if (multi.compareTo(actoNew.getValorMax()) < 0) {
                    valorTemp = actoNew.getValorMax().subtract(multi);
                    division = division.add(valorTemp);
                }
                cuantiaTemp = division;
            }
        } else {
            if (cuantiaTemp.compareTo(actoNew.getValorMax()) == 1) {
                cuantiaTemp = actoNew.getValorMax();
            }
        }
        return cuantiaTemp;
    }

    public void eliminarDetalle(int rowIndex) {
        try {
            RegpLiquidacionDetalles de = actosPorPagar.get(rowIndex);
            actosPorPagar.remove(rowIndex);
            if (de.getId() != null) {
                acl.delete(de);
            }
            this.sumaTotal();
            JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void sumaTotal() {
        subTotal = BigDecimal.ZERO;
        gastosGenerales = BigDecimal.ZERO;
        for (RegpLiquidacionDetalles a : actosPorPagar) {
            if (a.getActo().getGastosGenerales()) {
                gastosGenerales = gastosGenerales.add(a.getValorTotal());
            }
            subTotal = subTotal.add(a.getValorTotal());
        }
        totalPagar = subTotal.add(gastosGenerales);
        this.aplicarDescuento();
    }

    public void aplicarDescuento() {
        if (descuentoPorcentaje.compareTo(BigDecimal.ZERO) > 0) {
            descuentoValor = (subTotal.add(gastosGenerales)).multiply(descuentoPorcentaje.divide(new BigDecimal(100)));
            descuentoValor = Utils.bigdecimalTo2Decimals(descuentoValor);
            descuentoValor = descuentoValor.multiply(new BigDecimal(-1));
            totalPagar = subTotal.add(gastosGenerales.add(descuentoValor));
        }
    }

    public void showDlgCambioGastos() {
        JsfUti.update("formGastos");
        JsfUti.executeJS("PF('dlgCambioGastos').show();");
    }

    public void cambioGastosGenerales() {
        if (gastosGenerales != null) {
            totalPagar = subTotal.add(gastosGenerales);
            this.aplicarDescuento();
            JsfUti.executeJS("PF('dlgCambioGastos').hide();");
            JsfUti.update("mainForm:accPanelRP:pnlDescuentos");
        }
    }

    public void showDlgConfirmacion() {
        try {
            Boolean beneficiario = true;
            Boolean papel = false;
            esInscripcion = false;
            esCertificado = false;
            for (RegpIntervinientes i : listInterv) {
                if (i.getEsBeneficiario()) {
                    beneficiario = false;
                    propietario = i;
                }
                if (i.getPapel() == null) {
                    papel = true;
                }
            }
            Boolean usoDoc = false;
            for (RegpLiquidacionDetalles d : actosPorPagar) {
                if (d.getActo().getTipoActo().getId() == 1L || d.getActo().getTipoActo().getId() == 3L) {
                    esInscripcion = true;
                }
                if (d.getActo().getTipoActo().getId() == 2L || d.getActo().getTipoActo().getId() == 4L) {
                    esCertificado = true;
                    if (usoDocumento == null) {
                        usoDoc = true;
                    }
                }
            }
            if (solicitante.getId() == null) {
                JsfUti.messageWarning(null, "Debe buscar Solicitante.", "");
                //} else if (solicitante.getEnteCorreoCollection().isEmpty()) {
                //JsfUti.messageWarning(null, "Solicitante no tiene correo.", "");
            } else if (parentesco == null) {
                JsfUti.messageWarning(null, "Falta ingresar la condicion del Solicitante.", "");
            } else if (listInterv.isEmpty()) {
                JsfUti.messageWarning(null, "Debe ingresar Intervinientes.", "");
            } else if (beneficiario) {
                JsfUti.messageWarning(null, "Debe seleccionar el beneficiario.", "");
            } else if (papel) {
                JsfUti.messageWarning(null, "Debe ingresar el papel de los intervinientes.", "");
            } else if (actosPorPagar.isEmpty()) {
                JsfUti.messageWarning(null, "Debe seleccionar el Acto de tramite.", "");
            } else if (usoDoc) {
                JsfUti.messageWarning(null, "Debe seleccionar el uso del documento.", "");
//            } else if (con.getConnection() == null) {
//                JsfUti.messageWarning(null, "No hay conexion con el Sistema de cobro. Comuniquese con sistemas.", "");
            } else {
                JsfUti.update("formConfirmacion");
                JsfUti.executeJS("PF('dlgConfirmTramite').show();");
            }
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarTramite() {
        try {
            if (cantidadCatastro == null || valorCatastro == null) {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            } else if (cantidadCatastro < 0 && valorCatastro.compareTo(BigDecimal.ZERO) < 0) {
                JsfUti.messageWarning(null, "Cantidad y Valores de Tasa Catastro con error.", "");
            } else {
                tramite = (GeTipoTramite) acl.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, processDefinitionKey});
                if (tramite != null) {
                    disableBtn = true;
                    JsfUti.update("formConfirmacion");
                    this.leerCorreosTlfns();
                    switch (state) {
                        case "new":
                            this.guardarTramiteNuevo();
                            break;
                        case "edit":
                            this.guardarTramiteEditado();
                            break;
                        case "old":
                            this.guardarTramiteAntiguo();
                            break;
                        default:
                            JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/liquidacionesIngresadasRp.xhtml");
                            break;
                    }
                    this.imprimirDocumentos();
                } else {
                    JsfUti.messageWarning(null, Messages.error, "");
                }
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarTramiteNuevo() {
        ht = new HistoricoTramites();
        ht.setSolicitante(solicitante);
        ht.setEstado("Pendiente");
        ht.setTipoTramite(tramite);
        ht.setFecha(new Date());
        ht.setNombrePropietario(propietario.getNombres());
        ht.setTipoTramiteNombre(tramite.getDescripcion());
        ht.setUserCreador(session.getUserId());
        ht.setCorreos(listCorreos);
        ht.setTelefonos(listTlfns);

        liq = new RegpLiquidacionDerechosAranceles();
        liq.setFecha(new Date());
        liq.setTasaCatastro(valorCatastro);
        if (obsAdicional != null) {
            liq.setInfAdicional(obsAdicional);
        }
        liq.setUserCreador(session.getUserId());

        liq.setIsRegistroPropiedad(esPropiedad);
        liq.setGastosGenerales(gastosGenerales);
        liq.setDescuentoPorc(descuentoPorcentaje);
        liq.setDescuentoValor(descuentoValor);
        liq.setParentescoSolicitante(parentesco);
        liq.setCantidadTasasCatastro(BigInteger.valueOf(cantidadCatastro));
        liq.setValorActos(totalPagar);
        liq.setTotalPagar(totalPagar.add(valorCatastro));
        liq.setIsExoneracion(exonerado);
        liq.setEstado(1);
        if (usoDocumento != null) {
            liq.setUsoDocumento(usoDocumento);
        }
        if (exonerado) {
            liq.setEstadoPago("P");
        } else {
            liq.setEstadoPago("A");
        }
        liq.setInscripcion(esInscripcion);
        liq.setCertificado(esCertificado);
        liq.setHistoricTramite(ht);
        liq.setRegpIntervinientesCollection(listInterv);
        liq.setRegpLiquidacionDetallesCollection(actosPorPagar);
        liq = reg.guardarLiquidacionRegistro(liq, session.getName_user(), tramite.getFlagOne());
    }

    public void guardarTramiteEditado() {
        ht.setSolicitante(solicitante);
        ht.setNombrePropietario(propietario.getNombres());
        ht.setCorreos(listCorreos);
        ht.setTelefonos(listTlfns);
        liq.setHistoricTramite(ht);
        liq.setUserEdicion(session.getUserId());
        liq.setFechaEdicion(new Date());
        liq.setTasaCatastro(valorCatastro);
        if (obsAdicional != null) {
            liq.setInfAdicional(obsAdicional);
        }
        liq.setIsRegistroPropiedad(esPropiedad);
        liq.setGastosGenerales(gastosGenerales);
        liq.setDescuentoPorc(descuentoPorcentaje);
        liq.setDescuentoValor(descuentoValor);
        liq.setParentescoSolicitante(parentesco);
        liq.setCantidadTasasCatastro(BigInteger.valueOf(cantidadCatastro));
        liq.setValorActos(totalPagar);
        liq.setTotalPagar(totalPagar.add(valorCatastro));
        liq.setIsExoneracion(exonerado);
        liq.setEstado(1);
        if (usoDocumento != null) {
            liq.setUsoDocumento(usoDocumento);
        }
        if (exonerado) {
            liq.setEstadoPago("P");
        } else {
            liq.setEstadoPago("A");
        }
        liq.setInscripcion(esInscripcion);
        liq.setCertificado(esCertificado);
        liq.setRegpIntervinientesCollection(listInterv);
        liq.setRegpLiquidacionDetallesCollection(actosPorPagar);
        reg.editarLiquidacionRegistro(liq, session.getName_user(), tramite.getFlagOne());
    }

    public void guardarTramiteAntiguo() {
        try {
            ht = new HistoricoTramites();
            ht.setSolicitante(solicitante);
            ht.setEstado("Pendiente");
            ht.setObservacion("Liquidacion Antigua Registro Propiedad");
            ht.setTipoTramite(tramite);
            ht.setFecha(new Date());
            ht.setNombrePropietario(propietario.getNombres());
            ht.setTipoTramiteNombre(tramite.getDescripcion());
            ht.setUserCreador(session.getUserId());
            //ht.setCorreos(listCorreos); SE COMENTA POR MOTIVO DE SER TRAMITE ANTIGUO NO DEBE DE INGRESAR CORREO
            ht.setCorreos(SisVars.correoClienteGenerico);
            ht.setTelefonos(listTlfns);
            ht.setNumTramiteXDepartamento(numTramiteAntiguo);

            liq = new RegpLiquidacionDerechosAranceles();
            liq.setFecha(fechaAntigua);
            liq.setTasaCatastro(valorCatastro);
            if (obsAdicional != null) {
                liq.setInfAdicional(obsAdicional);
            }
            liq.setUserCreador(session.getUserId());
            liq.setIsRegistroPropiedad(esPropiedad);
            liq.setGastosGenerales(gastosGenerales);
            liq.setDescuentoPorc(descuentoPorcentaje);
            liq.setDescuentoValor(descuentoValor);
            liq.setParentescoSolicitante(parentesco);
            liq.setCantidadTasasCatastro(BigInteger.valueOf(cantidadCatastro));
            liq.setValorActos(totalPagar);
            liq.setTotalPagar(totalPagar.add(valorCatastro));
            liq.setIsExoneracion(true);
            liq.setEstadoPago("P");
            liq.setEstado(1);
            if (usoDocumento != null) {
                liq.setUsoDocumento(usoDocumento);
            }
            liq.setInscripcion(esInscripcion);
            liq.setCertificado(esCertificado);
            liq.setHistoricTramite(ht);
            liq.setNumTramiteRp(numTramiteAntiguo);
            liq.setNumeroComprobante(numComprobante);
            liq.setRegpIntervinientesCollection(listInterv);
            liq.setRegpLiquidacionDetallesCollection(actosPorPagar);

            liq = reg.guardarLiquidacionAntigua(liq, ht);
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirDocumentos() {
        if (liq != null && liq.getId() != null) {
            List<String> urlList = new ArrayList<>();
            Long id = liq.getHistoricTramite().getId();
            String url = "/vistaprocesos/registroPropiedad/iniciarTramiteRP.xhtml";
            switch (state) {
                case "new":
                    this.imprimirTicket(id);
                    urlList.add(SisVars.urlServidorPublica + "/Documento");
                    urlList.add(SisVars.urlServidorPublica + "/PreformaIngreso?codigo=" + id);
                    if (esInscripcion) {
                        urlList.add(SisVars.urlServidorPublica + "/AcuerdoIngreso?codigo=" + id);
                    }
                    if (esCertificado) {
                        urlList.add(SisVars.urlServidorPublica + "/SolicitudIngreso?codigo=" + id);
                    }
                    break;
                case "edit":
                    this.imprimirTicket(id);
                    urlList.add(SisVars.urlServidorPublica + "/Documento");
                    urlList.add(SisVars.urlServidorPublica + "/PreformaIngreso?codigo=" + id);
                    if (esInscripcion) {
                        urlList.add(SisVars.urlServidorPublica + "/AcuerdoIngreso?codigo=" + id);
                    }
                    if (esCertificado) {
                        urlList.add(SisVars.urlServidorPublica + "/SolicitudIngreso?codigo=" + id);
                    }
                    break;
                case "old":
                    if (esInscripcion) {
                        urlList.add(SisVars.urlServidorPublica + "/AcuerdoIngreso?codigo=" + id);
                    }
                    if (esCertificado) {
                        urlList.add(SisVars.urlServidorPublica + "/SolicitudIngreso?codigo=" + id);
                    }
                    break;
            }
            JsfUti.redirectMultipleConIP_V2(url, urlList);
        } else {
            JsfUti.messageWarning(null, Messages.error, "");
        }
    }

    public void imprimirTicket(Long id) {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("tramiteRegistroPropiedadMercantil");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("NUMTRAMITE", id);
            servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void leerCorreosTlfns() {
        listCorreos = "no_ingreso_correo@hotmail.com";
        if (solicitante.getEnteCorreoCollection() != null) {
            if (!solicitante.getEnteCorreoCollection().isEmpty()) {
                listCorreos = "";
                for (EnteCorreo c : solicitante.getEnteCorreoCollection()) {
                    listCorreos = listCorreos + "," + c.getEmail();
                }
                listCorreos = listCorreos.substring(1);
            }
        }

        if (solicitante.getEnteTelefonoCollection() != null) {
            if (!solicitante.getEnteTelefonoCollection().isEmpty()) {
                for (EnteTelefono t : solicitante.getEnteTelefonoCollection()) {
                    listTlfns = listTlfns + "," + t.getTelefono();
                }
                listTlfns = listTlfns.substring(1);
            }
        }
    }

    public void showDlgUsoDoc() {
        nuevoUsoDoc = new VuItems();
        JsfUti.update("formUsoDoc");
        JsfUti.executeJS("PF('usoDocumento').show();");
    }

    public void showDlgEditUsoDoc() {
        if (usoDocumento != null) {
            nuevoUsoDoc = usoDocumento;
            JsfUti.update("formUsoDoc");
            JsfUti.executeJS("PF('usoDocumento').show();");
        } else {
            JsfUti.messageWarning(null, "Debe seleccionar el elemento para editar.", "");
        }
    }

    public void guardarUsoDoc() {
        if (nuevoUsoDoc.getNombre() != null) {
            usoDocumento = reg.saveVuItmenRegistro(nuevoUsoDoc);
            JsfUti.update("mainForm:accPanelRP:pnlUsoDoc");
            JsfUti.executeJS("PF('usoDocumento').hide();");
        } else {
            JsfUti.messageWarning(null, "El campo nombre esta vacio.", "");
        }
    }

    public void buscarPredio() {
        try {
            if (numPredio != null) {
                CatPredio predio = cat.getPredioNumPredio(Long.valueOf(numPredio));
                if (predio != null) {
                    if (predio.getCiudadela() != null) {
                        urbanizacion = predio.getCiudadela().getNombre();
                    } else {
                        urbanizacion = "";
                    }
                    if (predio.getUrbMz() != null) {
                        manzana = predio.getUrbMz();
                    } else {
                        manzana = "";
                    }
                    if (predio.getUrbSolarnew() != null) {
                        solar = predio.getUrbSolarnew();
                    } else {
                        solar = "";
                    }
                    if (predio.getAvaluoMunicipal() != null) {
                        avaluo = predio.getAvaluoMunicipal();
                    } else {
                        avaluo = null;
                    }
                } else {
                    this.inicializaInfoPredio();
                    JsfUti.messageWarning(null, "No se encontro informacion del predio.", "");
                }
            } else {
                this.inicializaInfoPredio();
                JsfUti.messageWarning(null, "El campo numero de predio esta vacio.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(IniciarTramiteRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void inicializaInfoPredio() {
        urbanizacion = "";
        manzana = "";
        solar = "";
        avaluo = null;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getCiInterv() {
        return ciInterv;
    }

    public void setCiInterv(String ciInterv) {
        this.ciInterv = ciInterv;
    }

    public List<RegpIntervinientes> getListInterv() {
        return listInterv;
    }

    public void setListInterv(List<RegpIntervinientes> listInterv) {
        this.listInterv = listInterv;
    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public List<EnteCorreo> getCorreosSol() {
        return correosSol;
    }

    public void setCorreosSol(List<EnteCorreo> correosSol) {
        this.correosSol = correosSol;
    }

    public List<EnteTelefono> getTlfnsSol() {
        return tlfnsSol;
    }

    public void setTlfnsSol(List<EnteTelefono> tlfnsSol) {
        this.tlfnsSol = tlfnsSol;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public Boolean getEsNatural() {
        return esNatural;
    }

    public void setEsNatural(Boolean esNatural) {
        this.esNatural = esNatural;
    }

    public CatEnteLazy getEntesLazy() {
        return entesLazy;
    }

    public void setEntesLazy(CatEnteLazy entesLazy) {
        this.entesLazy = entesLazy;
    }

    public String getNombreInterv() {
        return nombreInterv;
    }

    public void setNombreInterv(String nombreInterv) {
        this.nombreInterv = nombreInterv;
    }

    public String getNomInscrip() {
        return nomInscrip;
    }

    public void setNomInscrip(String nomInscrip) {
        this.nomInscrip = nomInscrip;
    }

    public String getNomCertf() {
        return nomCertf;
    }

    public void setNomCertf(String nomCertf) {
        this.nomCertf = nomCertf;
    }

    public List<RegpActosIngreso> getListActosInscrp() {
        return listActosInscrp;
    }

    public void setListActosInscrp(List<RegpActosIngreso> listActosInscrp) {
        this.listActosInscrp = listActosInscrp;
    }

    public List<RegpActosIngreso> getListActosCertf() {
        return listActosCertf;
    }

    public void setListActosCertf(List<RegpActosIngreso> listActosCertf) {
        this.listActosCertf = listActosCertf;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public BigDecimal getGastosGenerales() {
        return gastosGenerales;
    }

    public void setGastosGenerales(BigDecimal gastosGenerales) {
        this.gastosGenerales = gastosGenerales;
    }

    public String getObsAdicional() {
        return obsAdicional;
    }

    public void setObsAdicional(String obsAdicional) {
        this.obsAdicional = obsAdicional;
    }

    public List<RegpLiquidacionDetalles> getActosPorPagar() {
        return actosPorPagar;
    }

    public void setActosPorPagar(List<RegpLiquidacionDetalles> actosPorPagar) {
        this.actosPorPagar = actosPorPagar;
    }

    public List<RegpActosIngreso> getActosSelect() {
        return actosSelect;
    }

    public void setActosSelect(List<RegpActosIngreso> actosSelect) {
        this.actosSelect = actosSelect;
    }

    public RegpActosIngreso getActoNew() {
        return actoNew;
    }

    public void setActoNew(RegpActosIngreso actoNew) {
        this.actoNew = actoNew;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public BigDecimal getDescuentoValor() {
        return descuentoValor;
    }

    public void setDescuentoValor(BigDecimal descuentoValor) {
        this.descuentoValor = descuentoValor;
    }

    public VuItems getUsoDocumento() {
        return usoDocumento;
    }

    public void setUsoDocumento(VuItems usoDocumento) {
        this.usoDocumento = usoDocumento;
    }

    public Boolean getExonerado() {
        return exonerado;
    }

    public void setExonerado(Boolean exonerado) {
        this.exonerado = exonerado;
    }

    public Integer getCantidadCatastro() {
        return cantidadCatastro;
    }

    public void setCantidadCatastro(Integer cantidadCatastro) {
        this.cantidadCatastro = cantidadCatastro;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorCatastro() {
        return valorCatastro;
    }

    public void setValorCatastro(BigDecimal valorCatastro) {
        this.valorCatastro = valorCatastro;
    }

    public Boolean getDisableBtn() {
        return disableBtn;
    }

    public void setDisableBtn(Boolean disableBtn) {
        this.disableBtn = disableBtn;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public BigDecimal getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(BigDecimal avaluo) {
        this.avaluo = avaluo;
    }

    public BigDecimal getCuantia() {
        return cuantia;
    }

    public void setCuantia(BigDecimal cuantia) {
        this.cuantia = cuantia;
    }

    public Boolean getEsAvaluo() {
        return esAvaluo;
    }

    public void setEsAvaluo(Boolean esAvaluo) {
        this.esAvaluo = esAvaluo;
    }

    public String getVacio() {
        return vacio;
    }

    public void setVacio(String vacio) {
        this.vacio = vacio;
    }

    public BigDecimal getValorIngresado() {
        return valorIngresado;
    }

    public void setValorIngresado(BigDecimal valorIngresado) {
        this.valorIngresado = valorIngresado;
    }

    public Long getIdMov() {
        return idMov;
    }

    public void setIdMov(Long idMov) {
        this.idMov = idMov;
    }

    public VuItems getNuevoUsoDoc() {
        return nuevoUsoDoc;
    }

    public void setNuevoUsoDoc(VuItems nuevoUsoDoc) {
        this.nuevoUsoDoc = nuevoUsoDoc;
    }

    public Integer getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Integer numPredio) {
        this.numPredio = numPredio;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getManzana() {
        return manzana;
    }

    public void setManzana(String manzana) {
        this.manzana = manzana;
    }

    public String getSolar() {
        return solar;
    }

    public void setSolar(String solar) {
        this.solar = solar;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getAntiguo() {
        return antiguo;
    }

    public void setAntiguo(Boolean antiguo) {
        this.antiguo = antiguo;
    }

    public Boolean getOldValido() {
        return oldValido;
    }

    public void setOldValido(Boolean oldValido) {
        this.oldValido = oldValido;
    }

    public BigInteger getNumTramiteAntiguo() {
        return numTramiteAntiguo;
    }

    public void setNumTramiteAntiguo(BigInteger numTramiteAntiguo) {
        this.numTramiteAntiguo = numTramiteAntiguo;
    }

    public Date getFechaAntigua() {
        return fechaAntigua;
    }

    public void setFechaAntigua(Date fechaAntigua) {
        this.fechaAntigua = fechaAntigua;
    }

    public BigInteger getNumComprobante() {
        return numComprobante;
    }

    public void setNumComprobante(BigInteger numComprobante) {
        this.numComprobante = numComprobante;
    }

    public Boolean getSolicitanteFlag() {
        return solicitanteFlag;
    }

    public void setSolicitanteFlag(Boolean solicitanteFlag) {
        this.solicitanteFlag = solicitanteFlag;
    }

}
