package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.bpm.models.PagoTituloReporteModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenParametrosInteresMulta;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoEntidadBancaria;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.RenLiquidacionesLazy;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.TitulosReporteCacheLocal;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.Faces;
import util.JsfUti;
import util.Messages;
import util.Utils;
import util.VerCedulaUtils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class Recaudaciones implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(Recaudaciones.class.getName());

    @javax.inject.Inject
    private Entitymanager acl;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private TitulosReporteCacheLocal titulosReporte;
    @Inject
    private UserSession session;
    @Inject
    private ServletSession ss;

    protected TreeNode root = new DefaultTreeNode("Titulos", null);
    protected TreeNode selectedNode;
    protected AclUser cajero;
    private Boolean esLiq;
    //TITULO DE CREDITO
    protected RenLiquidacionesLazy titulosCredito;
    //TITULO DE CREDITO PAGADO
    protected RenLiquidacionesLazy titulosCreditoPagado;

    //EMISIONES PREDIALES
    //protected RenLiquidacionesLazy emisionesPrediales;
    //COBROS GENERALES
    protected TreeNode root2;
    protected RenTipoLiquidacion liquidacionCG;
    protected List<RenRubrosLiquidacion> rubrosSeleccionado = new ArrayList<>();
    protected RenDetLiquidacion detalle = new RenDetLiquidacion();
    protected RenRubrosLiquidacion rubroSelect = new RenRubrosLiquidacion();
    protected RenLiquidacion cobrosGenerales = new RenLiquidacion();
    protected RenLiquidacion liquidacion;
    protected RecActasEspeciesDet acta = new RecActasEspeciesDet();
    protected CatEnteLazy solicitantes;
    protected CatPredio pr = new CatPredio();
    protected Integer numPredio;
    protected Integer cantidad;
    protected Integer desdeTemp;
    protected List<String> cdlas;
    protected List<RenLiquidacion> emisiones = new ArrayList<>();
    protected RenLiquidacion liqSelect = new RenLiquidacion();
    protected List<RenEntidadBancaria> bancos;
    protected List<RenEntidadBancaria> tarjetas;
    protected BigDecimal totalCoactiva = new BigDecimal("0.00");
    private Map<String, Object> paramt;
    protected PagoTituloReporteModel modelPago = new PagoTituloReporteModel();
    protected Boolean pagoRealizado = Boolean.FALSE;

    protected Integer tipoConsulta = 1;
    protected Integer tipoSolicitante;
    protected CatPredioModel predioModel = new CatPredioModel();
    protected CatEnte ente = new CatEnte();
    protected List<CatPredio> propiedades = new ArrayList<>();

    protected Date fechaReporte = new Date();
    protected Date fechaCierre = new Date();
    protected Long tipoReporte;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    protected CatEnte registroEnte = new CatEnte();
    private Integer tipoEnte = 1;

    protected Boolean excepcionalEmpresa = Boolean.FALSE;
    protected String ciRucCobros;

    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;

    @javax.inject.Inject
    private SeqGenMan seqServices;

    protected Boolean variosPagos = Boolean.FALSE;
    private RenLocalComercialLazy localesComercialesLazy;
    private RenLocalComercial localComercial;

    @PostConstruct
    public void initView() {
        try {
            if (session != null) {
                solicitantes = new CatEnteLazy();
                esLiq = true;
                root2 = titulosReporte.getTree();
                paramt = new HashMap<>();
                paramt.put("usuario", session.getName_user());
                cajero = (AclUser) manager.findObjectByParameter(AclUser.class, paramt);
                paramt = new HashMap<>();
                paramt.put("estado", Boolean.TRUE);
                paramt.put("tipo", new RenTipoEntidadBancaria(1L));
                bancos = manager.findObjectByParameterList(RenEntidadBancaria.class, paramt);
                paramt.put("tipo", new RenTipoEntidadBancaria(2L));
                tarjetas = manager.findObjectByParameterList(RenEntidadBancaria.class, paramt);
                localesComercialesLazy = new RenLocalComercialLazy(Boolean.FALSE);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void onChangeTab(TabChangeEvent event) {
        try {
            if (event.getTab().getId().equals("tituloCredito")) {

                if (titulosCredito == null) {
                    emisiones = new ArrayList<>();
                    liquidacion = new RenLiquidacion();
                    cobrosGenerales = new RenLiquidacion();
                    titulosCredito = new RenLiquidacionesLazy("mainForm:tvRecaudaciones:dtLiquidaciones");
                    JsfUti.update("mainForm:tvRecaudaciones:dtLiquidaciones");

                }
            }
            if (event.getTab().getId().equals("tituloCreditoPagado")) {

                if (titulosCreditoPagado == null) {
                    emisiones = new ArrayList<>();
                    liquidacion = new RenLiquidacion();
                    cobrosGenerales = new RenLiquidacion();
                    titulosCreditoPagado = new RenLiquidacionesLazy("mainForm:tvRecaudaciones:dtLiquidacionesPagadas");
                    JsfUti.update("mainForm:tvRecaudaciones:dtLiquidacionesPagadas");
                }

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    public void encerarValor() {
        if (rubroSelect.getValor() == null) {
            return;
        }
        if (rubroSelect.getValor().compareTo(BigDecimal.ZERO) == 0) {
            rubroSelect.setValor(null);
            JsfUti.update("formValores");
        }
    }

    public void showDlgEntes(Integer codigo) {
        try {
            VerCedulaUtils validacion = new VerCedulaUtils();
            tipoSolicitante = codigo;
            if (this.ciRucCobros != null && this.ciRucCobros.length() == 10 && validacion.isCIValida(ciRucCobros)) {
                if (this.registroEnte == null) {
                    this.registroEnte = new CatEnte();
                }
                this.registroEnte.setCiRuc(this.ciRucCobros);
                this.existeCedula();
                if (this.registroEnte != null && (this.registroEnte.getId() != null || this.registroEnte.getCiRuc() != null)) {
                    if (this.registroEnte.getId() == null) {
                        registroEnte.setUserCre(session.getName_user());
                        registroEnte.setFechaCre(new Date());
                        registroEnte = seqServices.guardarOActualizarEnte(registroEnte);
                    }
                    this.cobrosGenerales.setComprador(this.registroEnte);
                } else {
                    JsfUti.update("frmSolicitante");
                    JsfUti.executeJS("PF('dlgSolicitante').show();");
                }
            } else {
                JsfUti.update("frmSolicitante");
                JsfUti.executeJS("PF('dlgSolicitante').show();");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void cambioTipoConsulta() {
        totalCoactiva = new BigDecimal("0.00");
        emisiones = new ArrayList<>();
        pr = new CatPredio();
        ente = new CatEnte();
        predioModel = new CatPredioModel();
    }

    public void consultarPredios() {
        try {
            switch (tipoConsulta) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        paramt = new HashMap<>();
                        paramt.put("numPredio", predioModel.getNumPredio());
                        paramt.put("estado", "A");
                        pr = (CatPredio) manager.findObjectByParameter(CatPredio.class, paramt);
                        if (pr != null) {
                            this.calculoValoresCoactiva();
                        } else {
                            JsfUti.messageError(null, "Error", "Numero de Predio no es valido.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Numero de Predio no es valido.");
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (ente.getId() != null) {
                        propiedades = recaudacion.getListPrediosByPropietario(ente.getId());
                        if (propiedades != null && !propiedades.isEmpty()) {
                            JsfUti.update("formPredSel");
                            JsfUti.executeJS("PF('selPredio').show();");
                        } else {
                            propiedades = new ArrayList<>();
                            JsfUti.messageError(null, "Error", "No se encontraron predios.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Debe seleccionar un contribuyente.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getCdla() > 0 || predioModel.getMzDiv() > 0 || predioModel.getSolar() > 0 || predioModel.getDiv1() > 0 || predioModel.getDiv2() > 0 || predioModel.getDiv3() > 0 || predioModel.getDiv4() > 0 || predioModel.getDiv5() > 0 || predioModel.getDiv6() > 0 || predioModel.getDiv7() > 0 || predioModel.getDiv8() > 0 || predioModel.getDiv9() > 0 || predioModel.getPhv() > 0 || predioModel.getPhh() > 0) {
                        propiedades = recaudacion.getListPrediosByCodigoPredial(predioModel);
                        if (propiedades != null && !propiedades.isEmpty()) {
                            JsfUti.update("formPredSel");
                            JsfUti.executeJS("PF('selPredio').show();");
                        } else {
                            propiedades = new ArrayList<>();
                            JsfUti.messageError(null, "Error", "No se encontraron predios.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                default:
                    JsfUti.messageError(null, "ERROR", "OPCION NO VALIDA.");
                    break;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void updateValoresEspecies() {
        acta.setDisponibles(acta.getDisponiblesTemp() - rubroSelect.getCantidad());
        acta.setHastaTemp(acta.getDesdeTemp() + rubroSelect.getCantidad() - 1);
        rubroSelect.setValorTotal(rubroSelect.getValor().multiply(new BigDecimal(rubroSelect.getCantidad())));
    }

    public void onRowSelect() {
        try {
            if (rubroSelect.getId() != null) {
                rubroSelect = manager.find(RenRubrosLiquidacion.class, rubroSelect.getId());
                if (rubrosSeleccionado.contains(rubroSelect)) {
                    JsfUti.messageError(null, "Ya esta seleccionado el mismo rubro.", "");
                } else {

                    rubroSelect.setCantidad(1);
                    rubroSelect.setAnio(Utils.getAnio(new Date()));
                    rubroSelect.setMes(Utils.getMes(new Date()) + 1);
                    if (rubroSelect.getRecEspecies() != null) {
                        acta = recaudacion.getActaByEspecieYUser(rubroSelect.getRecEspecies().getId(), session.getUserId());
                        if (acta != null) {
                            acta.setDisponiblesTemp(acta.getDisponibles());
                            acta.setDisponibles(acta.getDisponibles() - 1);
                            if (acta.getUltimoVendido() == 0) {
                                acta.setDesdeTemp(acta.getDesde());
                                acta.setHastaTemp(acta.getDesde());
                            } else {
                                acta.setDesdeTemp(acta.getUltimoVendido() + 1);
                                acta.setHastaTemp(acta.getDesdeTemp());
                            }
                            rubroSelect.setValorTotal(rubroSelect.getValor());
                            JsfUti.update("formEspecies");
                            JsfUti.executeJS("PF('dlgEspeciesDisp').show();");
                        } else {
                            JsfUti.messageError(null, "No tiene asignado especies.", "");
                        }
                    } else {
                        JsfUti.update("formValores");
                        JsfUti.executeJS("PF('dlgValorRubro').show();");
                    }
                }
            } else {
                JsfUti.messageInfo(null, "Intente Nuevamente.", "");
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void onRowSelectLocalComercial() {
        try {
            if (rubroSelect.getId() != null) {
                rubroSelect = manager.find(RenRubrosLiquidacion.class, rubroSelect.getId());
                if (rubrosSeleccionado.contains(rubroSelect)) {
                    JsfUti.messageError(null, "Ya esta seleccionado el mismo rubro.", "");
                } else {
                    if (this.cobrosGenerales.getLocalComercial() == null) {
                        JsfUti.messageError(null, "Debe Seleccionar un Local Comercial.", "");
                        return;
                    }
                    rubroSelect.setCantidad(1);
                    rubroSelect.setAnio(Utils.getAnio(new Date()));
                    rubroSelect.setMes(Utils.getMes(new Date()) + 1);
                    rubroSelect.setValor(rubroSelect.getValor().multiply(this.cobrosGenerales.getLocalComercial().getArea()).setScale(2, RoundingMode.HALF_UP));
                    if (rubroSelect.getRecEspecies() != null) {
                        acta = recaudacion.getActaByEspecieYUser(rubroSelect.getRecEspecies().getId(), session.getUserId());
                        if (acta != null) {
                            acta.setDisponiblesTemp(acta.getDisponibles());
                            acta.setDisponibles(acta.getDisponibles() - 1);
                            if (acta.getUltimoVendido() == 0) {
                                acta.setDesdeTemp(acta.getDesde());
                                acta.setHastaTemp(acta.getDesde());
                            } else {
                                acta.setDesdeTemp(acta.getUltimoVendido() + 1);
                                acta.setHastaTemp(acta.getDesdeTemp());
                            }
                            rubroSelect.setValorTotal(rubroSelect.getValor());
                            JsfUti.update("formEspecies");
                            JsfUti.executeJS("PF('dlgEspeciesDisp').show();");
                        } else {
                            JsfUti.messageError(null, "No tiene asignado especies.", "");
                        }
                    } else {
                        JsfUti.update("formValores");
                        JsfUti.executeJS("PF('dlgValorRubro').show();");
                    }
                }
            } else {
                JsfUti.messageInfo(null, "Intente Nuevamente.", "");
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void guardaRubro() {
        String dialogo = "dlgValorRubro";
        if (rubroSelect.getAnio() == null) {
            JsfUti.messageError(null, "No debe ingresar el Año", "");
            return;
        }
        if (rubroSelect.getValor() == null) {
            return;
        }

        if (rubroSelect.getRecEspecies() != null) {
            if (rubroSelect.getCantidad() > acta.getDisponiblesTemp()) {
                JsfUti.messageError(null, "No debe vender mas especies de las disponibles.", "");
                return;
            }
            rubroSelect.setValor(rubroSelect.getValorTotal());
            rubroSelect.setActa(acta);
            dialogo = "dlgEspeciesDisp";
        }
        this.cobrosGenerales.setAnio(rubroSelect.getAnio());
        this.cobrosGenerales.setMes(rubroSelect.getMes());
        rubrosSeleccionado.add(rubroSelect);
        this.calcularTotalCobroGeneral();
        JsfUti.update("mainForm:tvRecaudaciones:panelDetalle");
        JsfUti.executeJS("PF('" + dialogo + "').hide();");
    }

    public void deleteRubrosDetalle(int indice) {
        rubrosSeleccionado.remove(indice);
        this.calcularTotalCobroGeneral();
        JsfUti.update("mainForm:tvRecaudaciones:panelDetalle");
    }

    public void onNodeSelect() {
        if (selectedNode != null) {
            cobrosGenerales.setTipoLiquidacion(manager.find(RenTipoLiquidacion.class, ((RenTipoLiquidacion) selectedNode.getData()).getId()));
        }
    }

    public void seleccionarLiquidacion() {
        pagoRealizado = Boolean.FALSE;
        cobrosGenerales.setSaldo(cobrosGenerales.getTotalPago());
        this.liquidacion = cobrosGenerales;
        if (this.liquidacion != null && this.liquidacion.getTipoLiquidacion() != null && this.rubrosSeleccionado != null && !this.rubrosSeleccionado.isEmpty()) {
            if ((this.liquidacion.getComprador() != null || this.liquidacion.getNombreComprador() != null) && this.liquidacion.getSaldo() != null && this.liquidacion.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                this.liquidacion.calcularPago();
                //SE AGREGA PARA SALDO DE PAGO
                modelPago = new PagoTituloReporteModel(this.liquidacion.getPagoFinal(), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());
                JsfUti.update("formProcesar");
                JsfUti.executeJS("PF('dlgProcesar').show();");
            } else {
                if (this.liquidacion.getComprador() == null) {
                    JsfUti.messageInfo(null, "Mensaje", "Debe seleccionar un Solicitante.");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Valor del Cobro debe ser mayor a 0.00.");
                }
            }
        } else {
            JsfUti.messageInfo(null, "Mensaje", "Seleccione un elemento de la lista.");
        }
    }

    public void seleccionarLiquidacionLocalComercial() {
        if (cobrosGenerales.getLocalComercial() == null) {
            JsfUti.messageError(null, "Mensaje", "Debe seleccionar un Local Comercial.");
        } else {
            pagoRealizado = Boolean.FALSE;
            cobrosGenerales.setSaldo(cobrosGenerales.getTotalPago());
            this.liquidacion = cobrosGenerales;
            if (this.liquidacion != null && this.liquidacion.getTipoLiquidacion() != null && this.rubrosSeleccionado != null && !this.rubrosSeleccionado.isEmpty()) {
                if ((this.liquidacion.getComprador() != null || this.liquidacion.getNombreComprador() != null) && this.liquidacion.getSaldo() != null && this.liquidacion.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                    this.liquidacion.calcularPago();
                    //SE AGREGA PARA SALDO DE PAGO
                    this.generarLiquidacionLocalComercial();
                    JsfUti.update("formLiq");
                    JsfUti.executeJS("PF('dlgNumeroLiquidacion').show();");
                    initView();
                } else {
                    if (this.liquidacion.getComprador() == null) {
                        JsfUti.messageInfo(null, "Mensaje", "Debe seleccionar un Solicitante.");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Valor del Cobro debe ser mayor a 0.00.");
                    }
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje", "Seleccione un elemento de la lista.");
            }
        }

    }

    public void imprimirTituloLocalComercial() {

        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarParametros();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("planificacion/certificados");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.setNombreReporte(liquidacion.getTipoLiquidacion().getNombreReporte());
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        initView();
    }

    public void llenarArbol() {
        try {
            List<RenTipoLiquidacion> raices;
            raices = acl.findAll(QuerysFinanciero.getRenTransaccionesHijos, new String[]{"idPadre"}, new Object[]{0L});
            for (RenTipoLiquidacion temp : raices) {
                if (!temp.getTomado()) {
                    temp.setTomado(true);
                    TreeNode node = new DefaultTreeNode(temp, root2);
                    llenarHijosArbol(temp, node);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void llenarHijosArbol(RenTipoLiquidacion hoja, TreeNode padre) {
        try {
            List<RenTipoLiquidacion> hijos;

            hijos = acl.findAll(QuerysFinanciero.getRenTransaccionesHijos, new String[]{"idPadre"}, new Object[]{hoja.getId()});

            if (hijos == null || hijos.isEmpty()) {
                return;
            }

            for (RenTipoLiquidacion temp2 : hijos) {
                if (!temp2.getTomado()) {
                    TreeNode node = new DefaultTreeNode(temp2, padre);
                    temp2.setTomado(true);
                    llenarHijosArbol(temp2, node);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Recaudaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getDescripcionRubro(Long idRubro) {
        return (String) acl.find(Querys.getDesripcionRubro, new String[]{"idRubro"}, new Object[]{idRubro});
    }

    public void calculosAdicionales() {
        if (this.liquidacion != null) {
            if (this.liquidacion.getTipoLiquidacion().getId() == 13L) {
                try {
                    this.liquidacion = recaudacion.realizarDescuentoRecargaInteresPredial(this.liquidacion, null);
                } catch (Exception ex) {
                    Logger.getLogger(Recaudaciones.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            List<RenParametrosInteresMulta> parametrosInteresMultas = recaudacion.getListParametrosInteresMulta(liquidacion);
            if (parametrosInteresMultas != null && !parametrosInteresMultas.isEmpty()) {//VERIFICAR SI EMITE MULTA-INTERES
                for (RenParametrosInteresMulta interesMulta : parametrosInteresMultas) {
                    if (interesMulta.getTipo().equalsIgnoreCase("I")) {
                        try {
                            Calendar fecha = Calendar.getInstance();
                            fecha.set(Calendar.DAY_OF_MONTH, interesMulta.getDia().intValue());
                            fecha.set(Calendar.MONTH, interesMulta.getMes().intValue() - 1);
                            fecha.set(Calendar.YEAR, liquidacion.getAnio());
                            liquidacion.setInteres(recaudacion.generarInteres(liquidacion.getSaldo(), liquidacion.getAnio()));
                        } catch (Exception ex) {
                            Logger.getLogger(Recaudaciones.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (interesMulta.getTipo().equalsIgnoreCase("M")) {
                        liquidacion.setRecargo(recaudacion.generarMultas(liquidacion, interesMulta));
                    }
                }
            }
            this.liquidacion.calcularPago();
        }
    }

    public void generarLiquidacion() {
        if (session != null && session.getName_user() != null) {
            if (liquidacion != null && liquidacion.getId() != null) {
                esLiq = false;
                return;
            }
            liquidacion.setUsuarioIngreso(session.getName_user());
            liquidacion.getTipoLiquidacion().setRenRubrosLiquidacionCollection(rubrosSeleccionado);
            liquidacion.setAnio(Calendar.getInstance().get(Calendar.YEAR));
            liquidacion = recaudacion.grabarLiquidacion(liquidacion);
            liquidacion.calcularPago();
            /*COBRO PARA LIQUDACIONES DE COACTIVA*/
 /*if (liquidacion.getTipoLiquidacion().getId() == 49L) { // TIPO LIQUIDACION COACTIVA
             liquidacion = recaudacion.grabaLiquidacionRubro(liquidacion, detalle);
             } else {
             liquidacion.getTipoLiquidacion().setRenRubrosLiquidacionCollection(rubrosSeleccionado);
             liquidacion = recaudacion.grabarLiquidacion(liquidacion);
             }*/

        }
    }

    public void generarLiquidacionLocalComercial() {
        if (session != null && session.getName_user() != null) {
            if (liquidacion != null && liquidacion.getId() != null) {
                esLiq = false;
                return;
            }
            liquidacion.setUsuarioIngreso(session.getName_user());
            liquidacion.getTipoLiquidacion().setRenRubrosLiquidacionCollection(rubrosSeleccionado);
            if (liquidacion.getAnio() == null) {
                liquidacion.setAnio(Calendar.getInstance().get(Calendar.YEAR));
            }

            liquidacion = recaudacion.grabarLiquidacion(liquidacion);
            JsfUti.messageInfo(null, "Mensaje", "Liquidacion: " + liquidacion.getIdLiquidacion() + " Generada con exito");
            liquidacion.calcularPago();
            /*COBRO PARA LIQUDACIONES DE COACTIVA*/
 /*if (liquidacion.getTipoLiquidacion().getId() == 49L) { // TIPO LIQUIDACION COACTIVA
             liquidacion = recaudacion.grabaLiquidacionRubro(liquidacion, detalle);
             } else {
             liquidacion.getTipoLiquidacion().setRenRubrosLiquidacionCollection(rubrosSeleccionado);
             liquidacion = recaudacion.grabarLiquidacion(liquidacion);
             }*/

        }
    }

    /*METODO MODIFICADO PARA LOS COMPROBANTE GENERALES*/
    public void generarComprobante(RenPago p) {
        List<RenPago> pagos = new ArrayList<>();
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/Emision/"));
            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("ID_LIQUIDACION", p.getLiquidacion().getId());
            ss.agregarParametro("NUM_COMPROBANTE", p.getNumComprobante());
            ss.setTieneDatasource(Boolean.TRUE);
            if (liquidacion.getTipoLiquidacion().getNombreReporte() == null) {
                ss.setNombreReporte("sCobrosGenerales");
            } else {
                ss.setNombreReporte(liquidacion.getTipoLiquidacion().getNombreReporte());
                if (p.getLiquidacion().getTipoLiquidacion().getId() == 98L) {
                    if (p.getLiquidacion().getRenValoresPlusvalia() == null) {
                        ss.setNombreReporte("sCobrosGenerales");
                    }
                }
            }
            pagos.add(p);
            ss.agregarParametro("liquidaciones", pagos);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Comprobantes");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void seleccionar() {
        if (this.cobrosGenerales.getComprador() == null) {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar un solicitante del listado");
        } else {
            this.ciRucCobros = this.cobrosGenerales.getComprador().getCiRuc();
            if (tipoSolicitante == 1) {
                JsfUti.update("mainForm:tvRecaudaciones:panelInfoAdc");
            } else if (tipoSolicitante == 2) {
                ente = this.cobrosGenerales.getComprador();
                JsfUti.update("mainForm:tvRecaudaciones:groupCoactiva");
            }
            Faces.messageInfo(null, "Mensaje", "Contribuyente seleccionado.");
            JsfUti.executeJS("PF('dlgSolicitante').hide();");
        }
    }

    public void imprimirTitulo() {

        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarParametros();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("rentas/liquidaciones");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("LOGO2", path + SisVars.sisLogo);
        ss.agregarParametro("LOGO_FOOTER", path + "/template/balcon/banderaSanVicente.jpg");
        ss.agregarParametro("ID", liquidacion.getId());
        ss.setNombreReporte("tituloCreditoArrendamiento");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        initView();
    }

    public void calcularTotalCobroGeneral() {
        cobrosGenerales.setTotalPago(new BigDecimal("0.00"));
        if (this.rubrosSeleccionado != null && !this.rubrosSeleccionado.isEmpty()) {
            for (RenRubrosLiquidacion r : rubrosSeleccionado) {
                cobrosGenerales.setTotalPago(cobrosGenerales.getTotalPago().add(r.getValor()));
            }
        } else {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar el rubro");
        }
    }

    public void actualizarLiquidacion() {
        liquidacion = recaudacion.editarLiquidacion(liquidacion);
    }

    //Método que contiene los métodos de generar la liquidación y el pago
    public void pago() {
        try {
            if (this.liquidacion != null && this.modelPago != null && this.liquidacion.getTipoLiquidacion().getId().equals(181L) && this.liquidacion.getSaldo().compareTo(this.modelPago.getValorTotal()) > 0) {
                JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser igual a la recaudación");
                return;
            }
            this.generarLiquidacion();
            this.realizarPago();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void pagarLiquidacion(RenLiquidacion liq) {
        this.liquidacion = liq;
        this.procesarPago();
    }

    public void procesarPago() {
        try {
            pagoRealizado = Boolean.FALSE;
            //modelPago = new PagoTituloReporteModel();
            if (this.liquidacion != null) {
                List<RenParametrosInteresMulta> parametrosInteresMultas = recaudacion.getListParametrosInteresMulta(liquidacion);
                if (parametrosInteresMultas != null && !parametrosInteresMultas.isEmpty()) {//VERIFICAR SI EMITE MULTA-INTERES
                    for (RenParametrosInteresMulta interesMulta : parametrosInteresMultas) {
                        if (interesMulta.getTipo().equalsIgnoreCase("I")) {
                            Calendar fecha = Calendar.getInstance();
                            fecha.set(Calendar.DAY_OF_MONTH, interesMulta.getDia().intValue());
                            fecha.set(Calendar.MONTH, interesMulta.getMes().intValue() - 1);
                            fecha.set(Calendar.YEAR, liquidacion.getAnio());
                            liquidacion.setInteres(recaudacion.generarInteres(liquidacion.getSaldo(), liquidacion.getAnio()));
                        }
                        if (interesMulta.getTipo().equalsIgnoreCase("M")) {
                            liquidacion.setRecargo(recaudacion.generarMultas(liquidacion, interesMulta));
                        }
                    }
                }
                this.liquidacion.calcularPago();
                modelPago = new PagoTituloReporteModel(this.liquidacion.getPagoFinal(), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void realizarPago() {
        RenPago p;
        try {
            if (modelPago.getValorTotal().compareTo(liquidacion.getPagoFinal()) <= 0) {
                if (modelPago.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                    liquidacion.calcularPago();
                    //liquidacion=recaudacion.realizarPago(liquidacion, modelPago.realizarPago(liquidacion), cajero);
                    p = recaudacion.realizarPago(liquidacion, modelPago.realizarPago(liquidacion), cajero, true);

                    if (esLiq) {
                        liquidacion.setNumLiquidacion(new BigInteger(p.getNumComprobante() + ""));
                        liquidacion.setNumComprobante(new BigInteger(p.getNumComprobante() + ""));
                        liquidacion.setIdLiquidacion(liquidacion.getTipoLiquidacion().getPrefijo() + "-" + p.getNumComprobante());
                        manager.persist(liquidacion);
                    }
                    esLiq = true;
                    /*REVISAR PARA LOS PAGOS DE COACTIVA*/
 /*NUNCA ENTRA AQUI*/
 /*if (liquidacion.getTipoLiquidacion().getId() == 49L) {
                     if (modelPago.getValorTotal().compareTo(liquidacion.getSaldo()) == 0) {
                     System.out.println("// pago completo");
                     recaudacion.actualizaEmisionesCoactiva(liquidacion.getId());
                     }
                     }*/
                    if (p != null) {
                        modelPago = new PagoTituloReporteModel(new BigDecimal("0.00"), this.variosPagos, this.modelPago.getPagoNotaCredio(), this.modelPago.getPagoCheque(), this.modelPago.getPagoTarjetaCredito(), this.modelPago.getPagoTransferencia());
                        pagoRealizado = Boolean.TRUE;
                        rubrosSeleccionado = new ArrayList<>();
                        generarComprobante(p);
                        onNodeSelect();
                        this.cobrosGenerales.setTotalPago(null);
                        //this.cobrosGenerales.setComprador(null);
                        //this.cobrosGenerales.setObservacion(null);
                        this.cobrosGenerales.setNumLiquidacion(null);
                        liquidacion = null;
                        //this.ciRucCobros=null;
                        //this.cobrosGenerales.setNombreComprador(null);
                    }
                } else {
                    JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados debe ser mayor a 0.00");
                }
            } else {
                JsfUti.messageInfo(null, "Verifique el valor a cobrar", "Los valores ingresados no deben ser mayor al de la Recaudacion");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void limpiarDatosContribuyente() {
        if (this.cobrosGenerales != null && this.cobrosGenerales.getComprador() != null) {
            this.cobrosGenerales.setComprador(null);
            this.ciRucCobros = null;
        }
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = cdlas.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(cdlas.get(i), cdlas.get(i));
        }
        return options;
    }

    public void anularTituloReporte() {
        try {
            if (this.liquidacion != null) {
                this.liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
                this.liquidacion = recaudacion.editarLiquidacion(this.liquidacion);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void seleccionaPredio(CatPredio predio) {
        pr = predio;
        this.calculoValoresCoactiva();
        JsfUti.messageInfo(null, "Mensaje", "No de Predio: " + pr.getNumPredio());
    }

    public void calculoValoresCoactiva() {
        try {
            paramt = new HashMap<>();
            paramt.put("tipoLiquidacion", new RenTipoLiquidacion(13L));
            paramt.put("estadoLiquidacion", new RenEstadoLiquidacion(2L));
            paramt.put("predio", pr);
            paramt.put("estadoCoactiva", 2);
            emisiones = manager.findObjectByParameterOrderList(RenLiquidacion.class, paramt, new String[]{"anio"}, Boolean.TRUE);
            desdeTemp = (Integer) manager.getNativeQuery(Querys.getMinAnioImpuestoCoactiva, new Object[]{pr.getId()});
            totalCoactiva = new BigDecimal("0.00");
            if (emisiones != null && !emisiones.isEmpty()) {
                for (RenLiquidacion e : emisiones) {
                    e = recaudacion.realizarDescuentoRecargaInteresPredial(e, null);
                    e.calcularPago();
                    totalCoactiva = totalCoactiva.add(e.getValorCoactiva());
                }
            } else {
                emisiones = new ArrayList<>();
                JsfUti.messageInfo(null, "Mensaje", "El predio seleccionado no tiene emisiones prediales en coactiva.");
            }
            JsfUti.update("mainForm:tvRecaudaciones:dtEmisionesCoactiva");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void seleccionarEmision() {
        totalCoactiva = new BigDecimal("0.00");
        cantidad = 0;
        for (RenLiquidacion e : emisiones) {
            if (e.getAnio() <= liqSelect.getAnio()) {
                cantidad++;
                totalCoactiva = totalCoactiva.add(e.getValorCoactiva());
            }
        }
        JsfUti.update("mainForm:tvRecaudaciones:dtEmisionesCoactiva");
    }

    public void ingresarCoactiva() {
        pagoRealizado = Boolean.FALSE;
        try {
            if (liqSelect == null) {
                JsfUti.messageInfo(null, "Mensaje", "Debe seleccionar hasta la liquidacion que va a cancelar.");
            } else {
                paramt = new HashMap<>();
                paramt.put("tipoLiquidacion", new RenTipoLiquidacion(49L));
                paramt.put("codigoRubro", 1L);
                RenRubrosLiquidacion rubro = manager.findObjectByParameter(RenRubrosLiquidacion.class, paramt);

                liquidacion = new RenLiquidacion();
                liquidacion.setAnio(Calendar.getInstance().get(Calendar.YEAR));
                liquidacion.setComprador(liqSelect.getComprador());
                liquidacion.setTotalPago(totalCoactiva);
                liquidacion.setTipoLiquidacion(rubro.getTipoLiquidacion());
                liquidacion.setPredio(liqSelect.getPredio());
                liquidacion.setSaldo(totalCoactiva);
                liquidacion.setObservacion("Codigo Predial: " + pr.getCodigoPredialCompleto());

                rubrosSeleccionado = new ArrayList<>();
                detalle = new RenDetLiquidacion();
                detalle.setRubro(rubro.getId());
                detalle.setValor(totalCoactiva);
                detalle.setCantidad(cantidad);
                detalle.setDesde(new BigInteger(desdeTemp.toString()));
                detalle.setHasta(new BigInteger(liqSelect.getAnio().toString()));
                JsfUti.update("formProcesar");
                JsfUti.executeJS("PF('dlgProcesar').show();");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void generarReporteCajero() {
        try {
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("recaudaciones");
            ss.agregarParametro("USUARIO", session.getUserId());
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
            ss.agregarParametro("LOGO_URL", Faces.getRealPath("/").concat(SisVars.logoReportes));
            ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.logoReportes));
            ss.agregarParametro("FECHA", sdf.format(fechaReporte));
            switch (this.tipoReporte.intValue()) {
                case 1:
                    ss.setNombreReporte("cierreCaja");
                    ss.agregarParametro("NAME_SUP", recaudacion.getNameUserByRol(75L)); // ROL SUPERVISOR CAJA
                    ss.agregarParametro("NAME_TES", recaudacion.getNameUserByRol(104L)); // ROL JEFE TESORERIA
                    if (fechaCierre != null) {
                        ss.agregarParametro("FECHA_CIERRE", fechaCierre);
                    }
                    break;
                case 2:
                    ss.setNombreReporte("recaudacionesPorTransaccion");
                    ss.agregarParametro("DESDE", sdf.format(fechaReporte));
                    ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(fechaReporte, 1)));
                    ss.agregarParametro("USUARIO", session.getName_user());
                    ss.agregarParametro("SUBREPORT_TITLE", Faces.getRealPath("/reportes/").concat("/"));
                    if (fechaCierre != null) {
                        ss.agregarParametro("FECHA_CIERRE", fechaCierre);
                    }
                    break;
                case 3:
                    ss.setNombreReporte("desgloseImpuestoUrbano");
                    ss.agregarParametro("SUBREPORT_TITLE", Faces.getRealPath("/reportes/").concat("/"));
                    break;
                default:
                    return;
            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void actualizarEnte() {
        registroEnte = new CatEnte();
    }

    public void existeCedula() {
        VerCedulaUtils validacion = new VerCedulaUtils();
        String identificacion = registroEnte.getCiRuc();
        if (registroEnte.getCiRuc() != null && registroEnte.getCiRuc().length() > 0) {
            registroEnte = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{identificacion});
            if (registroEnte == null) {
                registroEnte = new CatEnte();
                registroEnte.setCiRuc(identificacion);
                if (tipoEnte == 1 || this.ciRucCobros != null) {
                    if (validacion.isCIValida(identificacion)) {
                        DatoSeguro ds = datoSeguroSeguro.getDatos(identificacion, false, 0);
                        registroEnte = datoSeguroSeguro.llenarEnte(ds, registroEnte, false);
                    }
                }
            }
        } else {
            registroEnte = new CatEnte();
        }
    }

    public void guardarEnte() {
        try {
            VerCedulaUtils validacion = new VerCedulaUtils();
            paramt = new HashMap<>();
            Boolean esExcepcional = false;

            if (tipoEnte == 1) {
                if (registroEnte.getCiRuc() == null || registroEnte.getApellidos() == null || registroEnte.getNombres() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", registroEnte.getCiRuc());
                if (manager.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isCIValida(registroEnte.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                registroEnte.setEsPersona(Boolean.TRUE);
            }
            if (tipoEnte == 2) {
                if (registroEnte.getCiRuc() == null || registroEnte.getRazonSocial() == null) {
                    JsfUti.messageInfo(null, Messages.faltanCampos, "");
                    return;
                }
                paramt.put("ciRuc", registroEnte.getCiRuc());
                if (manager.findObjectByParameter(CatEnte.class, paramt) != null) {
                    JsfUti.messageInfo(null, Messages.ciRucExiste, "");
                }
                if (!validacion.isRucValido(registroEnte.getCiRuc())) {
                    JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
                    return;
                }
                registroEnte.setEsPersona(Boolean.FALSE);
            }
            if (tipoEnte == 3) {
                esExcepcional = true;
                if (excepcionalEmpresa) {
                    if (registroEnte.getRazonSocial() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                    //liqSelect.setNombreComprador(registroEnte.getRazonSocial());
                    cobrosGenerales.setNombreComprador(registroEnte.getRazonSocial());
                } else {
                    if (registroEnte.getApellidos() == null || registroEnte.getNombres() == null) {
                        JsfUti.messageInfo(null, Messages.faltanCampos, "");
                        return;
                    }
                    //liqSelect.setNombreComprador(registroEnte.getNombres() + " " + registroEnte.getApellidos());
                    cobrosGenerales.setNombreComprador(registroEnte.getNombres() + " " + registroEnte.getApellidos());
                }
                /*registroEnte.setEsPersona(!excepcionalEmpresa);
                registroEnte.setExcepcionales(Boolean.TRUE);
                registroEnte.setUserCre(session.getName_user());
                registroEnte.setFechaCre(new Date());
                registroEnte=seqServices.guardarOActualizarEnte(registroEnte);*/
                JsfUti.messageInfo(null, "Seleccionado correctamente", "");
                JsfUti.executeJS("PF('dlgNewClient').hide();");
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgSolicitante').hide();");
            }

            if (registroEnte != null && registroEnte.getId() == null && !esExcepcional) {
                registroEnte = (CatEnte) manager.persist(registroEnte);
            }
            if (registroEnte != null && !esExcepcional) {
                JsfUti.messageInfo(null, "Registro Grabado", "");
                cobrosGenerales.setComprador((CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{registroEnte.getCiRuc()}));
                JsfUti.executeJS("PF('dlgNewClient').hide();");
                seleccionar();
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }

    public void loadRubroLocalComercial(RenLocalComercial comercial) {
        try {
            if (comercial != null) {
                pr = new CatPredio();
                this.cobrosGenerales.setComprador(comercial.getPropietario());
                this.cobrosGenerales.setLocalComercial(comercial);
                this.ciRucCobros = this.cobrosGenerales.getComprador().getCiRuc();
                JsfUti.update("mainForm:tvRecaudaciones:panelInfoAdc");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getRoot2() {
        return root2;
    }

    public void setRoot2(TreeNode root2) {
        this.root2 = root2;
    }

    public RenTipoLiquidacion getLiquidacionCG() {
        return liquidacionCG;
    }

    public void setLiquidacionCG(RenTipoLiquidacion liquidacionCG) {
        this.liquidacionCG = liquidacionCG;
    }

    public List<RenRubrosLiquidacion> getRubrosSeleccionado() {
        return rubrosSeleccionado;
    }

    public void setRubrosSeleccionado(List<RenRubrosLiquidacion> rubrosSeleccionado) {
        this.rubrosSeleccionado = rubrosSeleccionado;
    }

    public RenLiquidacionesLazy getTitulosCredito() {
        return titulosCredito;
    }

    public void setTitulosCredito(RenLiquidacionesLazy titulosCredito) {
        this.titulosCredito = titulosCredito;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public List<RenEntidadBancaria> getBancos() {
        return bancos;
    }

    public void setBancos(List<RenEntidadBancaria> bancos) {
        this.bancos = bancos;
    }

    public RenLiquidacion getCobrosGenerales() {
        return cobrosGenerales;
    }

    public void setCobrosGenerales(RenLiquidacion cobrosGenerales) {
        this.cobrosGenerales = cobrosGenerales;
    }

    public Date getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public CatPredio getPr() {
        return pr;
    }

    public void setPr(CatPredio pr) {
        this.pr = pr;
    }

    public List<RenLiquidacion> getEmisiones() {
        return emisiones;
    }

    public void setEmisiones(List<RenLiquidacion> emisiones) {
        this.emisiones = emisiones;
    }

    public PagoTituloReporteModel getModelPago() {
        return modelPago;
    }

    public void setModelPago(PagoTituloReporteModel modelPago) {
        this.modelPago = modelPago;
    }

    public RenRubrosLiquidacion getRubroSelect() {
        return rubroSelect;
    }

    public void setRubroSelect(RenRubrosLiquidacion rubroSelect) {
        this.rubroSelect = rubroSelect;
    }

    public RecActasEspeciesDet getActa() {
        return acta;
    }

    public void setActa(RecActasEspeciesDet acta) {
        this.acta = acta;
    }

    public Integer getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Integer numPredio) {
        this.numPredio = numPredio;
    }

    public BigDecimal getTotalCoactiva() {
        return totalCoactiva;
    }

    public void setTotalCoactiva(BigDecimal totalCoactiva) {
        this.totalCoactiva = totalCoactiva;
    }

    public RenLiquidacion getLiqSelect() {
        return liqSelect;
    }

    public void setLiqSelect(RenLiquidacion liqSelect) {
        this.liqSelect = liqSelect;
    }

    public Boolean getPagoRealizado() {
        return pagoRealizado;
    }

    public void setPagoRealizado(Boolean pagoRealizado) {
        this.pagoRealizado = pagoRealizado;
    }

    public List<RenEntidadBancaria> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<RenEntidadBancaria> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public Integer getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Integer tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public Integer getTipoSolicitante() {
        return tipoSolicitante;
    }

    public void setTipoSolicitante(Integer tipoSolicitante) {
        this.tipoSolicitante = tipoSolicitante;
    }

    public List<CatPredio> getPropiedades() {
        return propiedades;
    }

    public void setPropiedades(List<CatPredio> propiedades) {
        this.propiedades = propiedades;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }

    public CatEnte getRegistroEnte() {
        return registroEnte;
    }

    public void setRegistroEnte(CatEnte registroEnte) {
        this.registroEnte = registroEnte;
    }

    public Long getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Long tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Boolean getExcepcionalEmpresa() {
        return excepcionalEmpresa;
    }

    public void setExcepcionalEmpresa(Boolean excepcionalEmpresa) {
        this.excepcionalEmpresa = excepcionalEmpresa;
    }

    public String getCiRucCobros() {
        return ciRucCobros;
    }

    public void setCiRucCobros(String ciRucCobros) {
        this.ciRucCobros = ciRucCobros;
    }

    public Boolean getVariosPagos() {
        return variosPagos;
    }

    public void setVariosPagos(Boolean variosPagos) {
        this.variosPagos = variosPagos;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public RenLiquidacionesLazy getTitulosCreditoPagado() {
        return titulosCreditoPagado;
    }

    public void setTitulosCreditoPagado(RenLiquidacionesLazy titulosCreditoPagado) {
        this.titulosCreditoPagado = titulosCreditoPagado;
    }

    public RenLocalComercialLazy getLocalesComercialesLazy() {
        return localesComercialesLazy;
    }

    public void setLocalesComercialesLazy(RenLocalComercialLazy localesComercialesLazy) {
        this.localesComercialesLazy = localesComercialesLazy;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

}
