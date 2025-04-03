package com.origami.sgm.managedbeans.rentas;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.FnConvenioPago;
import com.origami.sgm.entities.FnConvenioPagoDetalle;
import com.origami.sgm.entities.FnConvenioPagoObservacion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenParametrosInteresMulta;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.FnConvenioPagoLazy;
import com.origami.sgm.lazymodels.RenLiquidacionesLazy;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
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
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import util.Faces;
import util.JsfUti;
import util.NumberToLatter;
import util.Utils;

/**
 *
 * @author dfcalderio
 */
@Named(value = "generarConvenioView")
@ViewScoped
public class GenerarConvenioPago implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(GenerarConvenioPago.class.getName());

    @Inject
    private RentasServices rentasServices;

    @javax.inject.Inject
    private RecaudacionesService service;
    @Inject
    private Entitymanager manager;
    @Inject
    private UserSession session;
    @Inject
    private ServletSession ss;
    @Inject
    private SeqGenMan seq;

    private RenParametrosInteresMulta interesMulta;
    private RenLiquidacionesLazy liquidaciones;
    private List<RenLiquidacion> seleccionadas;
    private CatEnteLazy solicitantes;

    private FnConvenioPago convenioPago;
    private BigDecimal deudaTotal;

    private FnConvenioPagoLazy convenios, conveniosAgua;
    private String observaciones;
    private boolean crear = false;

    private String observacionesAguaPotable;

    private String solicitante = null;

    /*AGREGADO*/
    protected List<String> user = new ArrayList<>();

    @PostConstruct
    public void initView() {
        deudaTotal = BigDecimal.ZERO;
        seleccionadas = new ArrayList<>();
        solicitantes = new CatEnteLazy();
        liquidaciones = new RenLiquidacionesLazy(new RenEstadoLiquidacion(6L));
        if (session.getDepts().contains(8L)) {
            convenios = new FnConvenioPagoLazy(new Short[]{0, 1, 2, 3, 4, 5, 6}, null);
        } else {
            if (session.getDepts().contains(51L)) {
                conveniosAgua = new FnConvenioPagoLazy(new Short[]{0, 1, 2, 3, 4, 5, 6}, Boolean.TRUE);
            } else {
                convenios = new FnConvenioPagoLazy(new Short[]{0, 1, 2, 3, 4, 5, 6}, Boolean.FALSE);
            }
        }
        crear = false;
    }

    public void asignarLiquidaciones() {

    }

    public void openDlgConvenio(FnConvenioPago convenioPago) {
        if (!tienePagoInicial(convenioPago)) {
            Map<String, List<String>> params = new HashMap<>();
            List<String> p = new ArrayList<>();
            if (convenioPago != null) {
                p.add(convenioPago.getId().toString());
                params.put("idConvenio", p);
                p = new ArrayList<>();
                p.add(convenioPago.getDescripcion());
                params.put("descripcion", p);
                crear = false;
                p = new ArrayList<>();
                p.add(convenioPago.getContribuyente().getId().toString());
                params.put("contribuyente", p);

            } else {
                crear = true;
            }
            p = new ArrayList<>();
            p.add("1");
            params.put("nuevo", p);

            p = new ArrayList<>();
            p.add(deudaTotal.toString());
            params.put("deudaInicial", p);
            p = new ArrayList<>();
            p.add("5");
            params.put("calculaInteres", p);
            if (!seleccionadas.isEmpty()) {
                if (seleccionadas.get(0).getTipoLiquidacion().getId() != 13) {
                    p = new ArrayList<>();
                    p.add(seleccionadas.get(0).getComprador().getId().toString());
                    params.put("contribuyente", p);
                }
            }
            Map<String, Object> options = new HashMap<>();
            options.put("resizable", true);
            options.put("draggable", true);
            options.put("modal", true);
            options.put("closable", true);
            RequestContext.getCurrentInstance().openDialog("/resources/dialog/dlgConvenioPago", options, params);
        }
    }

    public Boolean tienePagoInicial(FnConvenioPago convenioPago) {
        Boolean tienePagoInicial = Boolean.FALSE;
        if (convenioPago.getPorcientoInicial().compareTo(BigDecimal.ZERO) == 0) {
            JsfUti.messageError(null, "Info", "El convenio es por medio de Remision de Intereses no necesita pago inicial");
            tienePagoInicial = Boolean.TRUE;
        } else {
            RenLiquidacion temp = (RenLiquidacion) manager.find(QuerysFinanciero.getLiqInicialByConvenioAndTipoLiquidacion, new String[]{"convenio", "tipo"}, new Object[]{convenioPago.getId(), 260L});
            if (temp != null) {
                if (temp.getEstadoLiquidacion().getId() == 1L || temp.getEstadoLiquidacion().getId() == 2L) {
                    JsfUti.messageError(null, "Info", "El convenio esta en proceso del cobro Inicial");
                    tienePagoInicial = Boolean.TRUE;
                }
            }
        }
        return tienePagoInicial;
    }

    public BigDecimal calcularInteres(RenLiquidacion liq) {

        BigDecimal interes = rentasServices.interesCalculado(liq, null);
        liq.setInteres(interes);
        liq.calcularPago();

        return interes;
    }

    public void procesarConvenio(SelectEvent event) {
        convenioPago = (FnConvenioPago) event.getObject();
        convenioPago = (FnConvenioPago) manager.persist(convenioPago);
        seleccionadas.stream().map((l) -> {
            l.setUsuarioValida(session.getName_user());
            return l;
        }).map((l) -> {
            l.setConvenioPago(convenioPago);
            return l;
        }).map((l) -> {
            l.setEstadoLiquidacion(new RenEstadoLiquidacion(7L));
            return l;
        }).forEachOrdered((l) -> {
            manager.persist(l);
        });

        initView();

        JsfUti.messageInfo(null, "Info", "El convenio se ha elaborado con exito.");
    }

    public void procesarConvenioAgua(SelectEvent event) {
        convenioPago = (FnConvenioPago) event.getObject();
        convenioPago = (FnConvenioPago) manager.persist(convenioPago);
        seleccionadas.stream().map((l) -> {
            l.setUsuarioValida(session.getName_user());
            return l;
        }).map((l) -> {
            l.setConvenioPago(convenioPago);
            return l;
        }).map((l) -> {
            l.setEstadoLiquidacion(new RenEstadoLiquidacion(7L));
            return l;
        }).forEachOrdered((l) -> {
            manager.persist(l);
        });

        initView();

        JsfUti.messageInfo(null, "Info", "El convenio se ha elaborado con exito.");
    }

    public void editarConvenio(SelectEvent event) {
        convenioPago = (FnConvenioPago) event.getObject();
        JsfUti.messageInfo(null, "Info", "Convenio editado con exito.");
    }

    public void aprobarConvenio(FnConvenioPago convenio, boolean aprobar) {
        Boolean tienePagoInicial = tienePagoInicial(convenio);
        FnConvenioPagoObservacion observacionConvenio = new FnConvenioPagoObservacion();
        observacionConvenio.setConvenio(convenio);
        observacionConvenio.setEstado(Boolean.TRUE);
        observacionConvenio.setUsuarioIngreso(session.getName_user());
        observacionConvenio.setFechaIngreso(new Date());

        if (!tienePagoInicial && aprobar) {
            //activarConvenio(convenio, aprobar);
            generarLiquidacionInicialConvenio(convenio);
            convenio.setEstado((short) 2);
            convenio = (FnConvenioPago) manager.persist(convenio);
            JsfUti.messageInfo(null, "Info", "Pago del porcentaje inicial para procesar el convenio generado con exito.");
        } else {

            if (!tienePagoInicial && !aprobar) {
                List<RenLiquidacion> liqs = manager.findAll(QuerysFinanciero.getRenLiquidacionesByConvenioPago, new String[]{"convenio", "estadoLiquidacion"}, new Object[]{convenio.getId(), 1L});
                //List<RenLiquidacion> liqInicial = manager.findAll(QuerysFinanciero.getRenLiquidacionesByConvenioPago, new String[]{"convenio", "estadoLiquidacion"}, new Object[]{convenio.getId(), 2L});
                for (RenLiquidacion l : liqs) {
                    if (l.getTipoLiquidacion().getPrefijo().equals("ICP") || l.getTipoLiquidacion().getPrefijo().equals("CCP")) {
                        if (l.getEstadoLiquidacion().getId() != 1L) {
                            l.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));
                        }
                    } else {
                        l.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                    }
                    l.setConvenioPago(null);
                    manager.persist(l);
                }
                if (convenio.getEstado() == 3) {
                    convenio.setEstado((short) 5);
                } else {
                    convenio.setEstado((short) 4);
                }
                convenio = (FnConvenioPago) manager.persist(convenio);
                JsfUti.messageInfo(null, "Info", "El convenio se ha cancelado con exito.");
            }
        }
        ///VARIABLEE PARA LA ELIMINACION = O    
        if (!tienePagoInicial) {
            observacionConvenio.setObservacion(convenio.getObservacion());
            observacionConvenio.setEstadoConvenio(convenio.getEstado());
            manager.persist(observacionConvenio);
        }

    }

    public void activarConvenio(FnConvenioPago convenio, boolean activar) {
        if (!deshabilitarOpcionAprobar(convenio)) {
            FnConvenioPagoObservacion observacionConvenio = new FnConvenioPagoObservacion();
            observacionConvenio.setConvenio(convenio);
            observacionConvenio.setEstado(Boolean.TRUE);

            observacionConvenio.setUsuarioIngreso(session.getName_user());
            observacionConvenio.setFechaIngreso(new Date());
            if (activar) {
                RenPago pagoInicial = (RenPago) manager.find(QuerysFinanciero.getPagoInicialConvenio, new String[]{"convenioPago"}, new Object[]{convenio.getId()});
                convenio.setPagoInicial(pagoInicial);
                //if (convenio.getConvenioAgua()) {
                //     cabeceraMemoConvenioAgua(convenio);
                // } else {
                cabeceraMemoConvenio(convenio);
                //  }

                JsfUti.messageInfo(null, "Info", "El convenio se ecuentra activado.");
            } else {
                List<RenLiquidacion> liqs = manager.findAll(QuerysFinanciero.getRenLiquidacionesByConvenioPago, new String[]{"convenio", "estadoLiquidacion"}, new Object[]{convenio.getId(), 1L});
                // List<RenLiquidacion> liqInicial = manager.findAll(QuerysFinanciero.getRenLiquidacionesByConvenioPago, new String[]{"convenio", "estadoLiquidacion"}, new Object[]{convenio.getId(), 2L});

                for (RenLiquidacion l : liqs) {
                    if (l.getTipoLiquidacion().getPrefijo().equals("ICP") || l.getTipoLiquidacion().getPrefijo().equals("CCP")) {
                        l.setEstadoLiquidacion(new RenEstadoLiquidacion(3L));

                    } else {
                        l.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                    }

                    manager.persist(l);
                }

                convenio.setEstado((short) 5);
                convenio = (FnConvenioPago) manager.persist(convenio);
                JsfUti.messageInfo(null, "Info", "Convenio CANCELADO con exito.");
            }

            observacionConvenio.setEstadoConvenio(convenio.getEstado());
            observacionConvenio.setObservacion(convenio.getObservacion());
            manager.persist(observacionConvenio);
            JsfUti.update("frmTexto");
            JsfUti.executeJS("PF('dlgMemoConvenio').show();");
        } else {
            JsfUti.messageError(null, "Info", "El convenio esta en proceso del cobro Inicial");
        }

    }

    public void imprimirConvenioCuotaInicial(FnConvenioPago fnc) {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.borrarParametros();
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("rentas/ConveniosdePago/");
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/rentas/ConveniosdePago/"));
        ss.agregarParametro("ID", fnc.getId());
        ss.setNombreReporte("sConveniodePago");
        ss.agregarParametro("Marca", null);
        ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void onRowSelect(SelectEvent event) {
        BigDecimal suma = BigDecimal.ZERO;
        for (RenLiquidacion liq : seleccionadas) {
            suma = suma.add(liq.getTotalPago()).add(calcularInteres(liq));
        }
        deudaTotal = suma;
    }

    public void onRowUnselect(UnselectEvent event) {
        BigDecimal suma = BigDecimal.ZERO;
        for (RenLiquidacion liq : seleccionadas) {
            suma = suma.add(liq.getTotalPago()).add(calcularInteres(liq));
        }
        deudaTotal = suma;
    }

    public boolean generarLiquidacionInicialConvenio(FnConvenioPago convenio) {
        if (convenio != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                RenLiquidacion liq = new RenLiquidacion();

                RenTipoLiquidacion tipoLiq = (RenTipoLiquidacion) manager.find(QuerysFinanciero.getRenTipoLiquidacionByCodigoReporte, new String[]{"tituloReporte"}, new Object[]{260});

                liq.setTipoLiquidacion(tipoLiq);
                liq.setAnio(calendar.get(Calendar.YEAR));
                liq.setTotalPago(convenio.getValorPorcientoInicial());
                liq.setUsuarioIngreso(session.getName_user());
                //liq.setValidada(Boolean.TRUE);
                liq.setComprador(convenio.getContribuyente());
                liq.setVendedor(convenio.getContribuyente());
                liq.setConvenioPago(convenio);
                liq.setSaldo(convenio.getValorPorcientoInicial());
                liq.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
                liq.setUsuarioValida(session.getName_user());
                liq.setCoactiva(Boolean.FALSE);
                liq.setEstadoCoactiva(1);
//                if (convenio.getConvenioAgua()) {
//                    if (convenio.getCuentaAgua() != null) {
//                        liq.setCuenta(convenio.getCuentaAgua());
//                    }
//                } else {
                if (convenio.getPredio() != null) {
                    liq.setPredio(convenio.getPredio());
                }
//                }
                liq = (RenLiquidacion) manager.persist(liq);

                liq.setNumLiquidacion(seq.getMaxSecuenciaTipoLiquidacion(calendar.get(Calendar.YEAR), tipoLiq.getId()));

                liq.setIdLiquidacion(Utils.getAnio(new Date()).toString().concat("-").concat(Utils.completarCadenaConCeros(liq.getNumLiquidacion().toString(), 6)).concat("-").concat(tipoLiq.getPrefijo()));

                liq = (RenLiquidacion) manager.persist(liq);

                RenDetLiquidacion det = new RenDetLiquidacion();
                det.setLiquidacion(liq);
                det.setRubro(703L);
                det.setValor(liq.getTotalPago());
                det.setEstado(true);
                det.setValorRecaudado(BigDecimal.ZERO);
                manager.persist(det);

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        return false;
    }

    public boolean generarDetallesConvenio(FnConvenioPago convenio) {
        if (convenio != null) {
            try {

                BigDecimal valorCuota = convenio.getDeudaDiferir().divide(new BigDecimal(convenio.getCantidadMesesDiferir()), 2, RoundingMode.HALF_UP);
                BigDecimal diferencia = convenio.getDeudaDiferir().subtract(valorCuota.multiply(new BigDecimal(convenio.getCantidadMesesDiferir())));

                Calendar calendar = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTime(convenio.getFechaPrimeraCuota());

                //BigDecimal cuotas = convenio.getDeudaDiferir().divide(new BigDecimal(convenio.getCantidadMesesDiferir()), 2, RoundingMode.HALF_UP);
                RenTipoLiquidacion tipoLiq = (RenTipoLiquidacion) manager.find(QuerysFinanciero.getRenTipoLiquidacionByCodigoReporte, new String[]{"tituloReporte"}, new Object[]{261});
                for (int i = 0; i < convenio.getCantidadMesesDiferir(); i++) {
                    RenLiquidacion liq = new RenLiquidacion();
                    liq.setTipoLiquidacion(tipoLiq);
                    liq.setAnio(calendar.get(Calendar.YEAR));

                    liq.setUsuarioIngreso(session.getName_user());
//                    liq.setValidada(Boolean.TRUE);
                    liq.setComprador(convenio.getContribuyente());
                    liq.setVendedor(convenio.getContribuyente());
                    liq.setConvenioPago(convenio);
                    liq.setSaldo(valorCuota);
                    liq.setTotalPago(valorCuota);
                    liq.setCoactiva(Boolean.FALSE);
                    liq.setEstadoCoactiva(1);
                    if (i == 0) {
                        liq.setSaldo(valorCuota.add(diferencia));
                        liq.setTotalPago(valorCuota.add(diferencia));
                    }
                    liq.setEstadoLiquidacion(new RenEstadoLiquidacion(8L));
                    liq.setUsuarioValida(session.getName_user());
//                    if (convenio.getConvenioAgua()) {
//                        if (convenio.getCuentaAgua() != null) {
//                            liq.setCuenta(convenio.getCuentaAgua());
//                        }
//                    } else {
                    if (convenio.getPredio() != null) {
                        liq.setPredio(convenio.getPredio());
                    }
//                    }
                    liq = (RenLiquidacion) manager.persist(liq);
                    liq.setNumLiquidacion(seq.getMaxSecuenciaTipoLiquidacion(calendar.get(Calendar.YEAR), tipoLiq.getId()));
                    liq.setIdLiquidacion(Utils.getAnio(new Date()).toString().concat("-").concat(Utils.completarCadenaConCeros(liq.getNumLiquidacion().toString(), 6)).concat("-").concat(tipoLiq.getPrefijo()));

                    liq = (RenLiquidacion) manager.persist(liq);

                    RenDetLiquidacion det = new RenDetLiquidacion();
                    det.setLiquidacion(liq);
                    det.setRubro(704L);
                    det.setValor(liq.getTotalPago());
                    det.setEstado(true);
                    det.setValorRecaudado(BigDecimal.ZERO);
                    manager.persist(det);

                    FnConvenioPagoDetalle detalle = new FnConvenioPagoDetalle();
                    detalle.setConvenio(convenio);
                    detalle.setMes(i + 1);
                    detalle.setDeuda(liq.getTotalPago());
                    detalle.setEstado(Boolean.TRUE);
                    detalle.setLiquidacion(liq);
                    if (i == 0) {
                        detalle.setFechaMaximaPago(c.getTime());
                    } else {
                        c.add(Calendar.DATE, 30);
                        detalle.setFechaMaximaPago(c.getTime());
                    }

                    manager.persist(detalle);
                }

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        return false;
    }

    public boolean deshabilitarOpcionEditar(FnConvenioPago convenio) {
        RenLiquidacion temp = (RenLiquidacion) manager.find(QuerysFinanciero.getLiqInicialByConvenioAndTipoLiquidacion, new String[]{"convenio", "tipo"}, new Object[]{convenio.getId(), 260L});
        if (temp != null) {
            if (temp.getEstadoLiquidacion().getId() == 1L || temp.getEstadoLiquidacion().getId() == 2L) {
                return true;
            }
        }
        return false;
    }

    public boolean deshabilitarOpcionCancelar(FnConvenioPago convenio) {

        return convenio.getEstado() >= 4;
    }

    public boolean deshabilitarOpcionPagoInicial(FnConvenioPago convenio) {
        RenLiquidacion temp = (RenLiquidacion) manager.find(QuerysFinanciero.getLiqInicialByConvenioAndTipoLiquidacion, new String[]{"convenio", "tipo"}, new Object[]{convenio.getId(), 260L});
        if (temp != null) {
            if (temp.getEstadoLiquidacion().getId() == 1L || temp.getEstadoLiquidacion().getId() == 2L) {
                return true;
            }
        }
        return false;
    }

    public boolean deshabilitarOpcionMemo(FnConvenioPago convenio) {
        if (convenio.getEstado() == 3 || convenio.getEstado() == 6) {
            return false;
        }
        return true;
    }

    public boolean deshabilitarOpcionAprobar(FnConvenioPago convenio) {
        if (convenio.getPorcientoInicial().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        } else {
            RenLiquidacion temp = (RenLiquidacion) manager.find(QuerysFinanciero.getLiqInicialByConvenioAndTipoLiquidacion, new String[]{"convenio", "tipo"}, new Object[]{convenio.getId(), 260L});
            if (temp != null) {
                if (temp.getEstadoLiquidacion().getId() == 1L && convenio.getEstado() == 3) {
                    return true;
                }
                if (temp.getEstadoLiquidacion().getId() == 1L && (convenio.getEstado() == 0 || convenio.getEstado() == 1 || convenio.getEstado() == 2)) {
                    return false;
                }
                if (temp.getEstadoLiquidacion().getId() == 2L) {
                    return true;
                }
            }
            return false;
        }
    }

    public void restaurarLiquidacion(RenLiquidacion liq) {
        liq.setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
        manager.persist(liq);
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public RenLiquidacionesLazy getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(RenLiquidacionesLazy liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public List<String> getUser() {
        return user;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public List<RenLiquidacion> getSeleccionadas() {
        return seleccionadas;
    }

    public void setSeleccionadas(List<RenLiquidacion> seleccionadas) {
        this.seleccionadas = seleccionadas;
    }

    public RenParametrosInteresMulta getInteresMulta() {
        return interesMulta;
    }

    public void setInteresMulta(RenParametrosInteresMulta interesMulta) {
        this.interesMulta = interesMulta;
    }

    public FnConvenioPago getConvenioPago() {
        return convenioPago;
    }

    public void setConvenioPago(FnConvenioPago convenioPago) {
        this.convenioPago = convenioPago;
    }

    public BigDecimal getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(BigDecimal deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    public FnConvenioPagoLazy getConvenios() {
        return convenios;
    }

    public void setConvenios(FnConvenioPagoLazy convenios) {
        this.convenios = convenios;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String cabeceraMemoConvenio(FnConvenioPago convenio) {

        BigInteger numComprobanteRentas = (BigInteger) manager.find(QuerysFinanciero.getRenLiquidacionComprobanteCuentaPagoInicial, new String[]{"convenio"}, new Object[]{convenio.getId()});
        if (numComprobanteRentas == null) {
            numComprobanteRentas = BigInteger.ZERO;
        }
        String representante = "";
        this.convenioPago = convenio;
        boolean personaNatural = false;
        solicitante = convenioPago.getContribuyente().getNombreCompleto();
        if (convenio.getContribuyente().getEsPersona()) {
            personaNatural = true;
            representante = solicitante;
        } else {
            if (convenioPago.getContribuyente().getRepresentanteLegal() != null) {
                CatEnte rep = manager.find(CatEnte.class, convenioPago.getContribuyente().getRepresentanteLegal().longValue());
                representante = rep.getNombreCompleto();
            } else {
                representante = "NO TIENE REPRESENTANTE";
            }
        }
        List<RenLiquidacion> ls = manager.findAll(QuerysFinanciero.getLiqInicialByConvenioAndTipoLiquidacionJC, new String[]{"convenio"}, new Object[]{convenioPago.getId()});
        String impuestos = "";
        List<String> nombresLiquidaciones = new ArrayList<>();
        String result = "";
        int cant = 0;
        if (ls.size() == 1) {
            nombresLiquidaciones.add(ls.get(0).getTipoLiquidacion().getNombreTransaccion());
            impuestos = ls.get(0).getTipoLiquidacion().getNombreTransaccion();
        } else {
            for (RenLiquidacion l : ls) {
                if (!nombresLiquidaciones.contains(ls.get(cant).getTipoLiquidacion().getNombreTransaccion())) {
                    nombresLiquidaciones.add(ls.get(cant).getTipoLiquidacion().getNombreTransaccion());
                }
                cant++;
            }
        }
        for (int i = 0; i < nombresLiquidaciones.size(); i++) {
            if (i == 0) {
                impuestos = nombresLiquidaciones.get(i);
            } else {
                if (i < nombresLiquidaciones.size() - 1) {
                    impuestos += (", " + nombresLiquidaciones.get(i));
                } else {
                    impuestos += (" y " + nombresLiquidaciones.get(i));
                }
            }
        }

        if (cant == 0) {
            result = " impuesto de <span style=\"font-weight: bold;\">" + impuestos;
        } else {
            result = " impuestos de <span style=\"font-weight: bold;\">" + impuestos;
        }

        String fecha_Cadena = NumberToLatter.convertNumberToLetter(Utils.getPartDate(convenio.getFechaInicio(), 1), false);
        fecha_Cadena += " días  del mes " + Utils.getPartDate(convenio.getFechaInicio(), 2);
        fecha_Cadena += " del año " + NumberToLatter.convertNumberToLetter(Utils.getPartDate(convenio.getFechaInicio(), 3), false);

        //String nombrePersona1Cabecera = " DEUDOR";
        String nombrePersona1Cabecera = " ";
        if (!personaNatural) {
            nombrePersona1Cabecera = " en su calidad de Representante Legal de la Compañía <span style=\"font-weight: bold;\"> " + representante;
        }
        String Cabecera = "En la ciudad de San Vicente, a los " + fecha_Cadena + ", comparecen, por una parte <span style=\"font-weight: bold;\"> Ing." + service.getNameUserByRol(104L) + "</span>"
                + ", en su calidad de <span style=\"font-weight: bold;\">TESORERO(A) MUNICIPAL  </span> <span style=\"font-weight: bold;\"></span> "
                + "y , por otra parte el Sr(a).<span style=\"font-weight: bold;\">" + solicitante + " , " + convenio.getContribuyente().getCiRuc() + "</span>" + nombrePersona1Cabecera + "</span>"
                + " quienes celebran el convenio de pago al tenor de las siguientes cláusulas:<br><br>";

        String nombrePersona1RA = " ";
        //String nombrePersona1RA = " DEUDOR";
        if (!personaNatural) {
            nombrePersona1RA = " en calidad de Representante Legal de <span style=\"font-weight: bold;\">" + representante + "</span> DEUDOR";
        }
        String Primera = "<span style=\"font-weight: bold;\">PRIMERA.-</span> Antecedentes.-El Sr(a).<span style=\"font-weight: bold;\">" + solicitante + "</span>" + nombrePersona1RA
                + " , debe al Gobierno Municipal de este Cantón San Vicente, por concepto de " + result + "</span>\n"
                + " la suma de <span style=\"font-weight: bold;\">$" + convenio.getDeudaInicial() + "</span>" + " dólares correspondientes al ejercicio fiscal año <span style=\"font-weight: bold;\">" + Utils.getPartDate(convenio.getFechaInicio(), 3) + ".</span><br><br>";

        String nombrePersona2DA = "";
        if (!personaNatural) {
            nombrePersona2DA = " Representante legal de <span style=\"font-weight: bold;\">" + representante + "</span>";
        }

        String Segunda = "<span style=\"font-weight: bold;\">SEGUNDA.-</span> Convenio.- Al efecto el Sr.<span style=\"font-weight: bold;\">" + solicitante + "</span>" + nombrePersona2DA
                + " Mediante petición dirigida al <span style=\"font-weight: bold;\"> Ing." + service.getNameUserByRol(104L) + "</span> Tesorero del GAD. Municipal de San Vicente, encargado de recaudar los tributos en mención,solicita se le conceda las facilidades de pago conforme lo dispuesto en los Arts.152 y 153 de la Nueva Codificación del "
                + "Código Tributario, ofreciendo cancelar una cuota de <span style=\"font-weight: bold;\">$" + convenio.getPorcientoInicial() + "</span> dólares del monto de la deuda y el saldo de la liquidación  previa aceptación y firma de convenio.Petición aceptada por el/la Tesorero(a) Municipal y amparada en el art 46 del código tributario, se procede a realizar el presente CONVENIO DE PAGO.<br><br>";

        String Tercera = "<span style=\"font-weight: bold;\">TERCERA.-</span> Forma de Pago.-Con el recibo <span style=\"font-weight: bold;\">#" + numComprobanteRentas.toString() + "</span> cancela la suma de <span style=\"font-weight: bold;\">$" + convenio.getValorPorcientoInicial() + "</span> dólares americano como cuota inicial sobre el monto del valor a pagar por concepto de " + result + "</span>, se establece que el obligado ha cancelado al Sr(a)."
                + "<span style=\"font-weight: bold;\"> Ing." + service.getNameUserByRol(104L) + "</span> Tesorero(a) Municipal dicho valor, con un saldo pendiente de pago en la suma de <span style=\"font-weight: bold;\">$" + convenio.getDiferenciaFinanciar() + " </span> dólares, cancelar a <span style=\"font-weight: bold;\">" + NumberToLatter.convertNumberToLetter(convenio.getCantidadMesesDiferir(), false) + "</span>"
                + "meses plazo. <br><br>";

        String rep = "a los intereses de su representada";
        if (personaNatural) {
            rep = "";
        }
        String Cuarta = "<span style=\"font-weight: bold;\">CUARTA.-</span> Aceptación.- El Sr(a).<span style=\"font-weight: bold;\">" + solicitante + "</span>" + nombrePersona2DA
                + " Acepta la suscripción del presente convenio por convenir " + rep + " y se compromete al pago puntual de las cuotas establecidas para fiel cumplimiento de la obligación, una vez cumplida se emitirá el título de crédito objeto del presente convenio. <br><br>";

        String nombrePersona5TA = "";
        if (!personaNatural) {
            nombrePersona5TA = " Representante legal de <span style=\"font-weight: bold;\">" + representante + "</span> En Representación de esta";
        }
        String Quinta = "<span style=\"font-weight: bold;\">QUINTA.-</span> Jurisdicción y Competencia.-El Sr. <span style=\"font-weight: bold;\">" + solicitante + "</span>" + nombrePersona5TA
                + ", en caso de incumplimiento del convenio por la falta de pago, de uno o más dividendos, se somete a la jurisdicción coactiva y al trámite establecido en la Ley.<br><br>En el cuadro se detalla a continuación: <br><br>";

//        this.observaciones = Cabecera + Primera + Segunda + Tercera + Cuarta + Quinta;
//        return Cabecera + Primera + Segunda + Tercera + Cuarta + Quinta;
        this.observaciones = Cabecera + Primera + Segunda + Tercera + Cuarta;
        return Cabecera + Primera + Segunda + Tercera + Cuarta;

    }

    public void generarMemoConvenio(FnConvenioPago c) {
        try {
            String detalle;
            if (c != null) {
                convenioPago = c;
                detalle = convenioPago.getMemoDetalle();
            } else {
                convenioPago.setEstado((short) 3);
                generarDetallesConvenio(convenioPago);
                convenioPago.setMemoDetalle(this.observaciones);
                manager.persist(convenioPago);
                detalle = this.observaciones;
            }
            System.out.println("PPPP" + detalle);
            SimpleDateFormat df = new SimpleDateFormat("MMMMM", new Locale("es", "ES"));
            String Fecha = "San Vicente, " + String.format("%02d", Utils.getPartDate(this.convenioPago.getFechaInicio(), 1))
                    + " de " + df.format(new Date()) + " del " + Utils.getPartDate(this.convenioPago.getFechaInicio(), 3);
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("rentas/ConveniosdePago/");
            ss.setNombreReporte("sMemoConvenioPagoFinal");
            // ss.agregarParametro("Fondo", "");
            ss.agregarParametro("USUARIO_ELABORA", convenioPago.getUsuarioIngreso());
            ss.agregarParametro("Firma_Agua", null);
            ss.agregarParametro("Firma_Tesorero", null);
            ss.agregarParametro("USUARIO_INGRESO", convenioPago.getUsuarioIngreso());
            //  ss.agregarParametro("Fondo_Centrado", "");
            ss.agregarParametro("DETALLE", detalle);
            ss.agregarParametro("DETALLE_FINAL", detalle);
            ss.agregarParametro("ID", convenioPago.getId());
            ss.agregarParametro("TESORERO", service.getNameUserByRol(104L));
            ss.agregarParametro("ABOGADO", "Dr. Cesar Zambrano Vera.");
            ss.agregarParametro("REPRESENTANTE", convenioPago.getContribuyente().getNombreCompleto());
            ss.agregarParametro("CONTRIBUYENTE", convenioPago.getContribuyente().getNombreCompleto());
            ss.agregarParametro("CI/RUC", convenioPago.getContribuyente().getCiRuc());
            ss.agregarParametro("FECHA_MEMO", Fecha);
            ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));
            ss.agregarParametro("LOGO_MUNICIPIO", null);
            //ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
        }
    }

//    public String getFechasConvenioLiquidaciones(Cuenta c) {
//        String mesInicio = (String) manager.getNativeQuery(QuerysAguaPotable.mesInicioConvenio, new Object[]{c.getId()});
//        String mesFin = (String) manager.getNativeQuery(QuerysAguaPotable.mesFinConvenio, new Object[]{c.getId()});
//
//        return "Deuda desde: " + mesInicio + " Hasta: " + mesFin;
//    }
    public FnConvenioPagoLazy getConveniosAgua() {
        return conveniosAgua;
    }

    public void setConveniosAgua(FnConvenioPagoLazy conveniosAgua) {
        this.conveniosAgua = conveniosAgua;
    }

}
