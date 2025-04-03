package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenFactorPorCapital;
import com.origami.sgm.entities.RenFactorPorMetro;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.models.LiquidacionesPermisosFuncionamiento;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;
import util.Archivo;
import util.EntityBeanCopy;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class GenerarPermisosFuncionamiento extends BpmManageBeanBaseRoot implements Serializable {

    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(GenerarPermisosFuncionamiento.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @Inject
    private ServletSession ss;
    @javax.inject.Inject
    private Entitymanager servicesACL;

    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private BigDecimal areaBombero;
    private List<RenTipoLiquidacion> tiposLiquidacions;
    private CatPredio predio;
    private Calendar c;

    private LiquidacionesPermisosFuncionamiento actAnual;
    private LiquidacionesPermisosFuncionamiento tasaHab;
    private LiquidacionesPermisosFuncionamiento rotulos;
    private LiquidacionesPermisosFuncionamiento turismo;
    private LiquidacionesPermisosFuncionamiento patente;

    // Inicio Variables para caculos de valores 
    private MatFormulaTramite mft;
    private GroovyUtil util;
    // Fin Variables formula
    private Integer maxAnio;
    private Integer maxAnioAct;
    private Integer minAnio = 1990;

    @PostConstruct
    public void initView() {
        try {
            if (session != null && session.getTaskID() != null) {
                c = Calendar.getInstance();
                entradas = new HashMap<>();
                this.setTaskId(session.getTaskID());
                ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    if (ht.getMultasCollection() != null && !ht.getMultasCollection().isEmpty()) {
                        JsfUti.redirectFaces("/vistaprocesos/edificaciones/aprobacionDocumentoGeneral.xhtml");
                        return;
                    }
                    permiso = ht.getPermisoDeFuncionamientoLC();
                    if (permiso.getLocalComercial() != null) {
                        if (permiso.getLocalComercial().getNumPredio() != null) {
                            predio = services.permisoServices().getFichaServices().getPredioByNum(permiso.getLocalComercial().getNumPredio().longValue());
                        }
                    }
                    entradas.put("solicitud", services.getSolicitudExoneracion(ht));
                    entradas.put("obs", new Observaciones());
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>());
                    tiposLiquidacions = services.gettiposLiquidacionByCodTitRep(2);
                    // Se procesede a realizar los caculos de los diferentes permisos 
                    // se que hayan agregado 
                    iniciarDatos();
                }

            } else {
                this.continuar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Caculos de los valores de los permisos que se va generan
     */
    public void iniciarDatos() {
        mft = services.getMatFormulaByPrefijo("ACT");
        if (mft != null) {
            util = new GroovyUtil(mft.getFormula());
        } else {
            JsfUti.messageError(null, "Advertencia", "No se encontro formulas de calculos para este tipo de liquidacion (ACT) ");
        }
        BigDecimal valor;
        for (RenTipoLiquidacion tiposLiquidacion : tiposLiquidacions) {
            if (null != tiposLiquidacion.getCodigoTituloReporte()) {
                switch (tiposLiquidacion.getCodigoTituloReporte().intValue()) {
                    case 11:
                        //PAGO ANUAL DE ACTIVOS TOTALES
                        if (permiso.getActivos()) {
                            actAnual = new LiquidacionesPermisosFuncionamiento(tiposLiquidacion);
                            actAnual.inicarActivos(permiso.getActivosPermiso());
                            actAnual.getLiquidacion().setComprador(permiso.getLocalComercial().getPropietario());
                            actAnual.getLiquidacion().setFechaIngreso(new Date());
                            actAnual.getLiquidacion().setUsuarioIngreso(session.getName_user());
                            actAnual.getLiquidacion().setTipoLiquidacion(tiposLiquidacion);
                            actAnual.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                            actAnual.getDetalle().add(0, new RenDetLiquidacion(BigDecimal.ZERO, null, "Valor de Activos"));
                            actAnual.getDetalle().add(1, new RenDetLiquidacion(BigDecimal.ZERO, null, "Valor de Pasivos"));
                            actAnual.getDetalle().add(2, new RenDetLiquidacion(BigDecimal.ZERO, null, "Diferencia de Activos vs Pasivos"));
                            actAnual.getDetalle().add(3, new RenDetLiquidacion(BigDecimal.ZERO, null, "Base Imponible para el Cálculo"));
                            actAnual.agregarRubrosDetalle(services.getRubrosPorLiquidacion(tiposLiquidacion.getId()));
                            sumarActivos();
                            sumarPasivos();
                        }
                        break;
                    case 14:
                        //PATENTE  ANUAL MUNICIPAL
                        if (permiso.getPatente()) {
                            patente = new LiquidacionesPermisosFuncionamiento(tiposLiquidacion);
                            patente.iniciarBalance(permiso.getBalancePermiso());
                            patente.getLiquidacion().setTipoLiquidacion(tiposLiquidacion);
                            patente.getLiquidacion().setComprador(permiso.getLocalComercial().getPropietario());
                            patente.getLiquidacion().setFechaIngreso(new Date());
                            patente.getLiquidacion().setUsuarioIngreso(session.getName_user());
                            patente.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                            patente.agregarRubrosDetalle(services.getRubrosPorLiquidacion(tiposLiquidacion.getId()));
                            calcualrPatente();
                        }
                        break;
                    // TASA POR SERVICIO DE PREVENCION  C. BOMBEROS
                    case 53:
                        break;
                    case 206:
                        // ROTULOS PUBLICITARIOS
                        if (permiso.getRotulos()) {
                            rotulos = new LiquidacionesPermisosFuncionamiento(tiposLiquidacion);
                            rotulos.getLiquidacion().setCodigoLocal(permiso.getTipoRotulo());
                            rotulos.getLiquidacion().setAreaTotal(permiso.getMtrsRotulo());
                            rotulos.getLiquidacion().setTipoLiquidacion(tiposLiquidacion);
                            rotulos.getLiquidacion().setComprador(permiso.getLocalComercial().getPropietario());
                            rotulos.getLiquidacion().setFechaIngreso(new Date());
                            rotulos.getLiquidacion().setUsuarioIngreso(session.getName_user());
                            rotulos.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                            rotulos.agregarRubrosDetalle(services.getRubrosPorLiquidacion(tiposLiquidacion.getId()));
                            calcularValorRotulos();
                        }
                        break;
                    case 98:
                        // TASA DE LICENCIA ANUAL DE TURISMO
                        if (permiso.getTurismo()) {
                            turismo = new LiquidacionesPermisosFuncionamiento(tiposLiquidacion);
                            turismo.getLiquidacion().setTipoLiquidacion(tiposLiquidacion);
                            turismo.getLiquidacion().setComprador(permiso.getLocalComercial().getPropietario());
                            turismo.getLiquidacion().setFechaIngreso(new Date());
                            turismo.getLiquidacion().setUsuarioIngreso(session.getName_user());
                            turismo.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                            turismo.agregarRubrosDetalle(services.getRubrosPorLiquidacion(tiposLiquidacion.getId()));
                            calcularTurismo();
                        }
                        break;
                    case 15:
                        // TASA DE HABILITACIÓN Y CONTROL DE ESTABLECIMIENTOS
                        if (permiso.getTasaHabilitacion()) {
                            tasaHab = new LiquidacionesPermisosFuncionamiento(tiposLiquidacion);
                            tasaHab.getLiquidacion().setTipoLiquidacion(tiposLiquidacion);
                            tasaHab.getLiquidacion().setComprador(permiso.getLocalComercial().getPropietario());
                            tasaHab.getLiquidacion().setFechaIngreso(new Date());
                            tasaHab.getLiquidacion().setUsuarioIngreso(session.getName_user());
                            tasaHab.getLiquidacion().setLocalComercial(permiso.getLocalComercial());
                            tasaHab.agregarRubrosDetalle(services.getRubrosPorLiquidacion(tiposLiquidacion.getId()));
                            calcularTasaHab();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        maxAnio = Utils.getAnio(new Date()) - 1;
        maxAnioAct = Utils.getAnio(new Date());
    }

    public void validar() {

        JsfUti.executeJS("PF('obs').show()");
    }

    public void completarTarea() {
        try {
            String obs = ((Observaciones) entradas.get("obs")).getObservacion();
            if (obs == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            if (obs.length() == 0) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            c.setTime(actAnual.getLiquidacion().getFechaIngreso());
            c.add(Calendar.YEAR, 1);
            permiso.setFechaCaducidad(c.getTime());
            
            HashMap<String, Object> paramsActiviti = new HashMap();
            ((Observaciones) entradas.get("obs")).setEstado(Boolean.TRUE);
            ((Observaciones) entradas.get("obs")).setFecCre(new Date());
            ((Observaciones) entradas.get("obs")).setIdTramite(ht);
            ((Observaciones) entradas.get("obs")).setUserCre(session.getName_user());
            ((Observaciones) entradas.get("obs")).setTarea(this.getTaskDataByTaskID().getName());

            if (servicesACL.persist(((Observaciones) entradas.get("obs"))) != null) {
                List<RenLiquidacion> permisos = services.guardarPermisosFuncionamiento(permiso, actAnual, tasaHab, rotulos, turismo, patente, session.getName_user());
                if (permisos == null) {
                    JsfUti.messageError(null, "Advertencia", "Ocurrio un error al guardar los datos.");
                    return;
                }
                paramsActiviti.put("carpeta", ht.getCarpetaRep());
                paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                paramsActiviti.put("prioridad", 50);
                paramsActiviti.put("aprobado", !permiso.getInspeccionComisaria());
                paramsActiviti.put("tiene_inspeccion", true);

                if (!permiso.getInspeccionComisaria()) {
                    ht.setEstado("Finalizado");
                    servicesACL.persist(permiso);
                    servicesACL.persist(ht);
//                    System.out.println("Actualizar HT " + servicesACL.persist(ht));
                    imprimirLiquidaciones();
                }
                this.completeTask(this.getTaskId(), paramsActiviti);
                FacesContext.getCurrentInstance().responseComplete();
                this.continuar();
            } else {
                JsfUti.messageError(null, "Advertencia", "Ocurrio un error al guardar los datos.");
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "COMPLETAR TAREA RENTAS", e);
        }
    }

    private void imprimirLiquidaciones() {
        final Long id = ht.getId();
        ht = new HistoricoTramites();
        ht = services.permisoServices().getHistoricoTramiteById(id);
        if (ht != null) {
            List<RenLiquidacion> li = (List<RenLiquidacion>) ht.getRenLiquidacionCollection();
            int count = 0;
            String path = Faces.getRealPath("//");
            for (RenLiquidacion liquidacion : li) {
                Map<String, Object> addReport = new HashMap<>();
                addReport.put("LOGO", path.concat(SisVars.logoReportes));
                addReport.put("LIQUIDACION", liquidacion.getId());
                addReport.put("NUM_TRAMITE", ht.getId().toString());
                switch (liquidacion.getTipoLiquidacion().getCodigoTituloReporte().intValue()) {
                    case 11: //PAGO ANUAL DE ACTIVOS TOTALES
                        addReport.put("nombreReporte","activosTotales");
                        break;
                    case 14: //PATENTE  ANUAL MUNICIPAL
                        addReport.put("nombreReporte","patenteAnual");
                        break;
                    case 206: // ROTULOS PUBLICITARIOS
                        addReport.put("nombreReporte","vallasPublicitarias");
                        break;
                    case 98: // TASA DE LICENCIA ANUAL DE TURISMO
                        addReport.put("nombreReporte","turismo");
                        break;
                    case 15: // TASA DE HABILITACIÓN Y CONTROL DE ESTABLECIMIENTOS
                        addReport.put("nombreReporte","tasaHabilitacion");
                        break;
                    default:
                        break;
                }
                if (count == 0) {
                    ss.instanciarParametros();
                    ss.setTieneDatasource(Boolean.TRUE);
                    ss.setParametros(addReport);
                    ss.setNombreSubCarpeta("rentas");
                    ss.setAgregarReporte(Boolean.TRUE);
                    ss.setNombreReporte(addReport.get("nombreReporte").toString());
                } else {
                    ss.addParametrosReportes(addReport);
                }
                count++;
            }
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }
    }

    public void actualizarValores() {
        sumarActivos();
        sumarPasivos();
        calcualrPatente();
        calcularValorRotulos();
        calcularTurismo();
        calcularTasaHab();
    }

    public void validarDoc() {
        if (this.getFiles() == null || this.getFiles().isEmpty()) {
            JsfUti.messageInfo(null, "Info", "Debe ingresar un documento");
        } else {
            JsfUti.executeJS("PF('obs').show()");
        }
    }

    //********* inicio de metodos de calculos **********
    public void sumarActivos() {
        if (actAnual.getActivos().getActivoTotal() == null) {
            actAnual.getActivos().setActivoTotal(BigDecimal.ZERO);
        }
        if (actAnual.getActivos().getActivoContingente() == null) {
            actAnual.getActivos().setActivoContingente(BigDecimal.ZERO);
        }
        actAnual.getDetalle().get(0).setValor((BigDecimal) util.getExpression("sumarActivos", new Object[]{actAnual.getActivos()}));
        impuesto();
    }

    public void sumarPasivos() {
        if (actAnual.getActivos().getPasivoTotal() == null) {
            actAnual.getActivos().setPasivoTotal(BigDecimal.ZERO);
        }
        if (actAnual.getActivos().getPasivoContingente() == null) {
            actAnual.getActivos().setPasivoContingente(BigDecimal.ZERO);
        }
        actAnual.getDetalle().get(1).setValor((BigDecimal) util.getExpression("sumarPasivos", new Object[]{actAnual.getActivos()}));
        actAnual.getDetalle().get(2).setValor((BigDecimal) util.getExpression("diferenciaActivosVsPasivos", new Object[]{actAnual.getActivos()}));
        impuesto();
    }

    public void impuesto() {
        BigDecimal base = (BigDecimal) util.getExpression("baseImponible", new Object[]{actAnual.getActivos()});
        BigDecimal total = (BigDecimal) util.getExpression("impuesto", new Object[]{actAnual.getActivos()});
        if (total.signum() == -1) {
            total = BigDecimal.ZERO;
            actAnual.getLiquidacion().setEstadoLiquidacion(new RenEstadoLiquidacion(5L));
        }

        actAnual.getDetalle().get(3).setValor(base);
        actAnual.getLiquidacion().setTotalPago(total);
        actAnual.getDetalle().get(4).setValor(actAnual.getLiquidacion().getTotalPago());
        actAnual.getDetalle().get(5).setValor(BigDecimal.ZERO);
    }

    public void calcualrPatente() {
        RenFactorPorCapital factor2;
        BigDecimal valor = BigDecimal.ZERO;
        if (patente.getBalance().getCapital() != null && patente.getBalance().getCapital().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal total = BigDecimal.ZERO;
            factor2 = (RenFactorPorCapital) servicesACL.find(QuerysFinanciero.getValorBasePatenteComercial, new String[]{"capital"}, new Object[]{patente.getBalance().getCapital()});
            valor = (BigDecimal) util.getExpression("patenteComercial",
                    new Object[]{patente.getBalance().getCapital(), factor2});

            exonerarPagoArtesanos(permiso.getAfiliacionCamara(), patente.getLiquidacion(), valor);
            for (RenDetLiquidacion temp : patente.getDetalle()) {
                if (temp.getCodigoRubro() == 1l) {
                    patente.getDetalle().get(0).setValor(patente.getLiquidacion().getTotalPago());
                }
            }
        }
    }

    /**
     * Se realiza el calculo del valor que se va exonerar dependiendo del
     * porcentaje de rebaja que tenga la camara
     *
     * @param camara RenAfiliacionCamaraProduccion a la que esta afiliado
     * @param liquidacion RenLiquidacion
     * @param valor Valor a calcular el descuento
     */
    public void exonerarPagoArtesanos(RenAfiliacionCamaraProduccion camara, RenLiquidacion liquidacion, BigDecimal valor) {
        if (camara != null) {
            BigDecimal porc = camara.getPorcentExoneracion().divide(BigDecimal.valueOf(100));
            liquidacion.setTotalPago(valor.subtract(valor.multiply(porc)));
            if (liquidacion.getTotalPago().doubleValue() == 0) {
                LOG.info("EXONERADO DE PAGO");
                liquidacion.setEstadoLiquidacion(new RenEstadoLiquidacion(5L));
            }
        } else {
            liquidacion.setTotalPago(valor);
        }
    }

    public void calcularTasaHab() {
        BigDecimal areaL = (permiso.getLocalComercial().getArea() == null ? BigDecimal.ZERO : permiso.getLocalComercial().getArea());
        BigDecimal area = (permiso.getAreaBombero() == null ? BigDecimal.ZERO : permiso.getAreaBombero());
        RenFactorPorMetro factor1 = (RenFactorPorMetro) servicesACL.find(QuerysFinanciero.getValorBaseTasaHabilitacion,
                new String[]{"metros"}, new Object[]{area});
        tasaHab.getLiquidacion().setTotalPago((BigDecimal) util.getExpression("tasaHabilitacion",
                new Object[]{factor1.getValor(), permiso.getLocalComercial(), factor1.getFraccion()}));
        for (RenDetLiquidacion temp : tasaHab.getDetalle()) {
            if (temp.getCodigoRubro() == 1) {
                temp.setValor(tasaHab.getLiquidacion().getTotalPago());
            }
        }
    }

    public void calcularValorRotulos() {
        BigDecimal valor;
        CtlgSalario salario = null;
        if (rotulos.getLiquidacion().getAnio() != null) {
            salario = services.getSalarioBasico(rotulos.getLiquidacion().getAnio());
        }
        if (salario != null) {

            valor = (BigDecimal) util.getExpression("vallasPublicitarias",
                    new Object[]{rotulos.getLiquidacion(), salario.getValor()});
            if (valor != null) {
                rotulos.getLiquidacion().setTotalPago(valor);
                for (RenDetLiquidacion rb : rotulos.getDetalle()) {
                    rb.setCobrar(true);
                    rb.setValor(valor);
                }
                rotulos.getLiquidacion().setTotalPago(valor);
            }
        } else {
            JsfUti.messageError(null, "Error", "No existe salario basico registrado para el año (" + rotulos.getLiquidacion().getAnio() + ")");
        }
    }

    public void calcularTurismo() {
        BigDecimal valor;
        List<RenLocalCantidadAccesorios> accesorioses = services.getAcesorios(permiso.getLocalComercial());
        if (accesorioses == null) {
            JsfUti.messageError(null, "Error", "Local no tiene ingresado la cantidad de Mesas, Habitaciones, o Plaza");
            return;
        }
        if (permiso.getLocalComercial().getRenActividadComercialCollection() == null) {
            JsfUti.messageError(null, "Advertencia", "Local no tiene ninguna actividad registrada");
            return;
        }
        List<RenTasaTurismo> tasaTurismo = services.getTasasTurismo(permiso.getLocalComercial());
        valor = (BigDecimal) util.getExpression("getTasaTurismo",
                new Object[]{tasaTurismo, accesorioses});
        if (valor != null) {
            turismo.getLiquidacion().setTotalPago(valor);
            for (RenDetLiquidacion rb : turismo.getDetalle()) {
                rb.setCobrar(true);
                rb.setValor(valor);
            }
        }
        turismo.getLiquidacion().setTotalPago(valor);
    }

    public List<RenActividadComercial> getActividadesLocal() {
        List l = null;
        if (permiso.getLocalComercial() != null) {
            l = (List) EntityBeanCopy.clone(permiso.getLocalComercial().getRenActividadComercialCollection());
        } else {
            l = null;
        }
        return l;
    }

    public List<RenTasaTurismo> getValorTasa() {
        if (permiso == null) {
            return null;
        }
        if (permiso.getLocalComercial() == null) {
            return null;
        }
        return services.getTasasTurismo(permiso.getLocalComercial());
    }

    ///********** fin de metodos de calculos ***********
    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }

    public BigDecimal getAreaBombero() {
        return areaBombero;
    }

    public void setAreaBombero(BigDecimal areaBombero) {
        this.areaBombero = areaBombero;
    }

    public List<RenTipoLiquidacion> getTiposLiquidacions() {
        return tiposLiquidacions;
    }

    public void setTiposLiquidacions(List<RenTipoLiquidacion> tiposLiquidacions) {
        this.tiposLiquidacions = tiposLiquidacions;
    }

    public Integer getMaxAnio() {
        return maxAnio;
    }

    public void setMaxAnio(Integer maxAnio) {
        this.maxAnio = maxAnio;
    }

    public Integer getMaxAnioAct() {
        return maxAnioAct;
    }

    public void setMaxAnioAct(Integer maxAnioAct) {
        this.maxAnioAct = maxAnioAct;
    }

    public Integer getMinAnio() {
        return minAnio;
    }

    public void setMinAnio(Integer minAnio) {
        this.minAnio = minAnio;
    }

    public LiquidacionesPermisosFuncionamiento getActAnual() {
        return actAnual;
    }

    public void setActAnual(LiquidacionesPermisosFuncionamiento actAnual) {
        this.actAnual = actAnual;
    }

    public LiquidacionesPermisosFuncionamiento getTasaHab() {
        return tasaHab;
    }

    public void setTasaHab(LiquidacionesPermisosFuncionamiento tasaHab) {
        this.tasaHab = tasaHab;
    }

    public LiquidacionesPermisosFuncionamiento getRotulos() {
        return rotulos;
    }

    public void setRotulos(LiquidacionesPermisosFuncionamiento rotulos) {
        this.rotulos = rotulos;
    }

    public LiquidacionesPermisosFuncionamiento getTurismo() {
        return turismo;
    }

    public void setTurismo(LiquidacionesPermisosFuncionamiento turismo) {
        this.turismo = turismo;
    }

    public LiquidacionesPermisosFuncionamiento getPatente() {
        return patente;
    }

    public void setPatente(LiquidacionesPermisosFuncionamiento patente) {
        this.patente = patente;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

}
