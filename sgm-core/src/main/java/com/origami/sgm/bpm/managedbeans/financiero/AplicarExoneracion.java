/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.edifications.ConsultaPrediosEdifView;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.CatPredioRuralModel;
import com.origami.sgm.bpm.models.RubrosPorTipoLiq;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracionPredios;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.PropietariosLazy;
import com.origami.sgm.services.ejbs.ServiceLists;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Angel
 * Navarro
 * @Date 27/05/2016
 */
@Named
@ViewScoped
public class AplicarExoneracion extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(AplicarExoneracion.class.getName());

    @Inject
    private UserSession uSession;

    @Inject
    private ConsultaPrediosEdifView predioConsulta;

    @javax.inject.Inject
    protected RentasServices servicesRentas;

    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    @javax.inject.Inject
    private ServiceLists serviceLists;

    private HistoricoTramites ht;
    private Observaciones obs;

    private FnSolicitudExoneracion solicitud;
    private RenLiquidacion original, posterior;
    private FnExoneracionClase exoneracionClase;
    private FnExoneracionTipo exoneracionTipo;
    private List<FnExoneracionLiquidacion> exoneraciones;
    private FnExoneracionLiquidacion exoneracion;
    private CatEnte solicitante;
    private CatEnteLazy solicitantes;
    private CatPredio predio;
    private String cedulaRuc;
    private Integer aplicacionEstado;
    private List<FnExoneracionClase> clases;
    private List<FnExoneracionTipo> tipos;
    private FnExoneracionClase clase;
    private FnExoneracionTipo tipo;
    private List<RubrosPorTipoLiq> detList1, detList2;
    private BigDecimal valor;
    private List<RenDetLiquidacion> detLiq;
    private Map<String, Object> parametros;
    private List<RenTipoLiquidacion> tipoLiquidaciones;
    private RenTipoLiquidacion tipoLiquidacion;
    private RenRubrosLiquidacion rubroLiquidacion;
    private List<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionesEnSolitud = new ArrayList<>();
    private List<FnSolicitudExoneracionPredios> prediosEnSolicitud = new ArrayList<>();
    protected CatPredioModel predioModel = new CatPredioModel();
    protected PropietariosLazy propietarios;
    protected List<CatPredio> prediosUrbanos = new ArrayList<>();
    protected List<CatPredio> prediosUrbanosAExonerar = new ArrayList<>();

    protected List<CatPredioRuralModel> prediosRurales = new ArrayList<>();
    protected List<CatPredio> predios;
    private Boolean revision500Exoneraciones = Boolean.FALSE;

    @PostConstruct
    public void init() {

        try {
            if (uSession.esLogueado()) {
                propietarios = new PropietariosLazy();
                this.params = new HashMap();
                this.setTaskId(uSession.getTaskID());
                if (this.getTaskId() != null) {
                    ht = servicesRentas.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString()));
                    if (ht == null) {
                        return;
                    }
                    solicitud = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByTramite, new String[]{"idTramite", "estado"}, new Object[]{ht, 2L});
                    if (solicitud == null) {
                        return;
                    }
                    if (ht != null) {
                        predios = new ArrayList<>();
                        if (ht.getHistoricoTramiteDetCollection() != null) {
                            for (HistoricoTramiteDet col : ht.getHistoricoTramiteDetCollection()) {
                                predios.add(col.getPredio());
                            }
                        }
                    }
                } else {
                    solicitud = new FnSolicitudExoneracion();
                    solicitud.setNumResolucionSac("0001");
                    solicitud.setAnioFin(Utils.getAnio(new Date()));
                    solicitud.setAnioInicio(Utils.getAnio(new Date()));
                    solicitud.setFechaAprobacion(new Date());
                    clase = serviceLists.getClases().get(0);
                    this.selClase();
                }
                cedulaRuc = null;
                obs = new Observaciones();
                solicitantes = new CatEnteLazy();
            } else {
                this.continuar();
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void aplicarExoneracionSinTramite(Integer caso) {
        FnSolicitudExoneracionPredios predioSolicitud;
        
        try {
            predio = predioConsulta.getPredioConsulta();
            if (predio == null && (prediosUrbanos == null || prediosUrbanos.isEmpty()) && (prediosRurales == null || prediosRurales.isEmpty())) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar un predio");
                return;
            }
            if ((solicitud.getAnioInicio() == null & solicitud.getAnioFin() == null) || (solicitud.getAnioInicio() > solicitud.getAnioFin())) {
                JsfUti.messageInfo(null, "Info", "Ingrese Correctamente los años de la solicitud");
                return;
            }
            if ((solicitud.getAnioInicio() < Utils.getAnio(new Date()))) {
                JsfUti.messageInfo(null, "Info", "Solo se pueden exonerar el año actual");
                return;
            }

            if (tipo == null) {
                JsfUti.messageInfo(null, "Info", "Debe seleccionar el tipo de exoneracion");
                return;
            }
            /*for(Integer i = solicitud.getAnioInicio() ; i <= solicitud.getAnioFin() ; i++ ){
             if(manager.find(QuerysFinanciero.getSolicitudExoneracionByPredioAndTipo, new String[]{"predio", "tipo"}, new Object[]{predio, tipo}) != null){
             JsfUti.messageInfo(null, "Info", "El predio tiene aplicada una exoneracion ");
             return;
             }
             }*/
            if ((solicitud.getTipoValor() != null)) {
                if ((solicitud.getValor() == null)) {
                    JsfUti.messageInfo(null, "Info", "Debe ingresar el valor correspondiente a su tipo");
                    return;
                }
            }
            //VERIFICACION DE PREDIO EXONERADO
            if (!prediosUrbanos.isEmpty()) {
                predio = prediosUrbanos.get(0);
                if (!this.verificarPredioExonerado(predio, null, tipo, solicitud.getAnioInicio())) {
                    //ABRIL DIALOGO INFO
                    JsfUti.messageInfo(null, "Información", "Solicitud Registrada para el Predio en el Periodo");
                    return;
                }
            }
            if (!prediosRurales.isEmpty()) {
                for (CatPredioRuralModel pRural : prediosRurales) {
                    if (!this.verificarPredioExonerado(null, pRural, tipo, solicitud.getAnioInicio())) {
                        //ABRIL DIALOGO INFO
                        JsfUti.messageInfo(null, "Información", "Solicitud Registarda para el Predio en el Periodo");
                        return;
                    }
                }
            }

            //VERIFICACION DE EMISIONES PAGADAS Y ABONADAS
            List<RenPago> pagos;
            if (!prediosUrbanos.isEmpty()) {
                predio = prediosUrbanos.get(0);
                if (this.tipo.getId() != 40L) {
                    for (CatPredio pu : prediosUrbanos) {
                        pagos = recaudacion.getPagosByPredioTipoLiquidacionAnio(pu, null, new RenTipoLiquidacion(13L), solicitud.getAnioInicio(), solicitud.getAnioFin());
                        if (pagos != null && !pagos.isEmpty()) {
                            JsfUti.messageInfo(null, "Información", "Solo procede las Emisiones Pendientes de Pago");
                            return;
                        }
                    }
                } else {
                    for (CatPredio pu : prediosUrbanos) {
                        pagos = recaudacion.getPagosByPredioTipoLiquidacionAnioPagada(pu, null, new RenTipoLiquidacion(13L), solicitud.getAnioInicio(), solicitud.getAnioFin());
                        if (pagos != null && !pagos.isEmpty()) {
                            JsfUti.messageInfo(null, "Información", "Solo procede las Emisiones Pendientes de Pago");
                            return;
                        }
                    }
                }
            }
            if (tipo.getId().intValue() == 19 || tipo.getId().intValue() == 37 || tipo.getId().intValue() == 44 ) {
                if(prediosUrbanos.size() > 1){
                    JsfUti.messageInfo(null, "Para las exoneraciones de " + tipo.getDescripcion(), "Solo se pueden realizar sobre un solo bien "
                            + "inmueble con un avalúo máximo de quinientas (500) remuneraciones básicas");
                    return;
                }
            }

            if(validarExoneracion()){
                return;
            }
            
            ////VALIDA LAS REMUNERACIONES BASICAS ANTES DE MANDAR A GRABAR TODO U.U 
            //SOLO CUANDO ES --ANCIANO DISCAPACITADO Y CIEGO BY ANDY =(
            if (!revision500Exoneraciones) {
                if (tipo.getId().intValue() ==  17 || tipo.getId().intValue() == 18 || tipo.getId().intValue() == 19 
                        || tipo.getId().intValue() == 37 || tipo.getId().intValue() == 44) {
                    CtlgSalario salario;
                    BigDecimal salarioMax = BigDecimal.ZERO;
                    salario = (CtlgSalario) manager.find(QuerysFinanciero.getSalarioByAnio,
                            new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
                    salarioMax = salario.getValor().multiply(new BigDecimal(500));
                    if (exoneracionA500Remuneraciones(salarioMax)) {
                        prediosQuePuedenSerExonerados(prediosUrbanos, salarioMax);
                        return;
                    }

                }
            }
            solicitud.setExoneracionTipo(tipo);
            solicitud.setFechaIngreso(new Date());
            solicitud.setUsuarioCreacion(uSession.getName_user());
            solicitud.setSolicitante(solicitante);
            solicitud.setPredio(predio);
            solicitud.setEstado(new FnEstadoExoneracion(2L));
            solicitud = (FnSolicitudExoneracion) manager.persist(solicitud);

            for (CatPredio prediosUrbano : prediosUrbanos) {
                predioSolicitud = new FnSolicitudExoneracionPredios();
                predioSolicitud.setPredio(prediosUrbano);
                prediosEnSolicitud.add(predioSolicitud);
            }
            for (CatPredioRuralModel pRural : prediosRurales) {
                predioSolicitud = new FnSolicitudExoneracionPredios();
                if (pRural.getPredioRustico() != null) {
                    predioSolicitud.setPredioRural(pRural.getPredioRustico());
                }
//                if (pRural.getPredioRusctico2017() != null) {
//                    predioSolicitud.setPredioRural2017(pRural.getPredioRusctico2017());
//                }
                prediosEnSolicitud.add(predioSolicitud);
            }         
            servicesRentas.registarDatoSolicitudExoneracion(solicitud, tipoLiquidacionesEnSolitud, prediosEnSolicitud);

            if (caso == 2) {
                exoneraciones = servicesRentas.aplicarExoneracion(null, solicitud, uSession.getName_user());
                if (exoneraciones != null && !exoneraciones.isEmpty()) {
                    detList1 = new ArrayList();
                    detList2 = new ArrayList();
                    RenRubrosLiquidacion rubro;
                    exoneracion = exoneraciones.get(0);
                    if (exoneracion.getLiquidacionOriginal() != null) {
                        original = exoneracion.getLiquidacionOriginal();
                        detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{original});
                        for (RenDetLiquidacion temp : detLiq) {
                            rubro = (RenRubrosLiquidacion) manager.find(RenRubrosLiquidacion.class, temp.getRubro());
                            detList1.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp.getValor()));
                        }
                    }
                    if (exoneracion.getLiquidacionPosterior() != null) {
                        posterior = exoneracion.getLiquidacionPosterior();
                        posterior.setTotalPago(posterior.getTotalPago().setScale(2, RoundingMode.HALF_UP));
                        posterior.setUsuarioIngreso(uSession.getName_user());
                        posterior = (RenLiquidacion) manager.persist(posterior);
                        posterior = (RenLiquidacion) manager.find(RenLiquidacion.class, posterior.getId());

                        detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{posterior});
                        for (RenDetLiquidacion temp2 : detLiq) {
                            rubro = (RenRubrosLiquidacion) manager.find(RenRubrosLiquidacion.class, temp2.getRubro());
                            detList2.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp2.getValor()));
                        }
                    }
                    JsfUti.update("frmExoAp");
                    JsfUti.executeJS("PF('dlgExoAplicada').show()");
                    JsfUti.messageInfo(null, "Info", "Exoneracion aplicada correctamente");
                    prediosEnSolicitud = new ArrayList<>();
                    exoneraciones = new ArrayList();
                    exoneracion = new FnExoneracionLiquidacion();
                    solicitud = new FnSolicitudExoneracion();
                    posterior = new RenLiquidacion();
                    original = new RenLiquidacion();
                } else {
                    JsfUti.messageInfo(null, "Info", "Hubo un problema al aplicar la exoneracion, Revise las emisiones realizadas con anterioridad");
                }
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Registrar Solicitud", e);
        }
    }

    public Boolean exoneracionA500Remuneraciones(BigDecimal salarioMax) {

        BigDecimal totalesAvaluos = BigDecimal.ZERO;
        for (CatPredio prediosUrbano : prediosUrbanos) {
            if (prediosUrbano.getAvaluoMunicipal() != null) {
                totalesAvaluos = totalesAvaluos.add(prediosUrbano.getAvaluoMunicipal());
            }
        }
        int res = totalesAvaluos.compareTo(salarioMax);
        if (res == 1) {
            JsfUti.messageFatal(null, "La sumatoria de los Avaluos Supera las 500 remuneraciones Basicas", "");
            return true;
        } else {
            List<CatPredio> prediosUrbanosExoneracion = new ArrayList();
            for (CatPredio prediosUrbano : prediosUrbanos) {
                prediosUrbano.setAvaluoMunicipal(BigDecimal.ZERO);
                prediosUrbanosExoneracion.add(prediosUrbano);
            }
            prediosUrbanos = new ArrayList();
            prediosUrbanos = prediosUrbanosExoneracion;
        }

        return false;
    }

    public void prediosQuePuedenSerExonerados(List<CatPredio> prediosSeleccionados, BigDecimal salarioMax) {
        Collections.sort(prediosSeleccionados, (CatPredio p1, CatPredio p2)
                -> new Integer(p1.getAvaluoMunicipal().toBigInteger().intValue())
                        .compareTo(p2.getAvaluoMunicipal().toBigInteger().intValue()));
        this.prediosUrbanosAExonerar = new ArrayList();
        for (CatPredio cp : prediosSeleccionados) {
            if (cp.getAvaluoMunicipal().compareTo(salarioMax) == 1) {
                cp.setAvaluoMunicipal(cp.getAvaluoMunicipal().subtract(salarioMax));
                this.prediosUrbanosAExonerar.add(cp);
                break;
            } else {
                salarioMax = salarioMax.subtract(cp.getAvaluoMunicipal());
                cp.setAvaluoMunicipal(BigDecimal.ZERO);
                this.prediosUrbanosAExonerar.add(cp);
            }
        }
        JsfUti.update("frmPrediosASerExonerados");
        JsfUti.executeJS("PF('dlgPrediosQuePuedenSerExonerados').show()");
    }

    public void exonerarPrediosMenoresA500Remuneraciones() {

        JsfUti.executeJS("PF('dlgPrediosQuePuedenSerExonerados').hide()");
        this.prediosUrbanos = this.prediosUrbanosAExonerar;
        this.revision500Exoneraciones = Boolean.TRUE;
        aplicarExoneracionSinTramite(2);
    }

    public Boolean verificarPredioExonerado(CatPredio catPredio, CatPredioRuralModel catPredioRural, FnExoneracionTipo exoneracionTipo, Integer anio) {
        FnSolicitudExoneracion solicitudConsulta = null;
        if (catPredio != null) {
            solicitudConsulta = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByPredioAndTipoAndAnioFin, new String[]{"predio", "tipo", "anio"}, new Object[]{catPredio, exoneracionTipo, anio});
        }
        if (catPredioRural != null) {
            if (catPredioRural.getPredioRustico() != null) {
                solicitudConsulta = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByPredioRuralAndTipoAndAnioFin, new String[]{"predio", "tipo", "anio"}, new Object[]{catPredioRural.getPredioRustico(), exoneracionTipo, anio});
            }
//            if (catPredioRural.getPredioRusctico2017() != null) {
//                solicitudConsulta = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByPredioRural2017AndTipoAndAnioFin, new String[]{"predio", "tipo", "anio"}, new Object[]{catPredioRural.getPredioRusctico2017(), exoneracionTipo, anio});
//            }
        }
        return solicitudConsulta == null;
    }
    
    public Boolean validarExoneracion(){
        Boolean exonera = Boolean.FALSE;
        if (tipo.getId().intValue() == 29 || tipo.getId().intValue() == 35) {
            if(solicitud.getTipoValor().getId().intValue() == 7){
                for(CatPredio cp : prediosUrbanos){
                    if(cp.getAvaluoMunicipal().compareTo(solicitud.getValor()) < 0){
                        JsfUti.messageFatal(null, "El valor ingresado no puede ser mayor al del Avalùo del Predio", "");
                        exonera = Boolean.TRUE;
                    }
                }
            }else{
                if(solicitud.getValor().compareTo(new BigDecimal(100)) < 0){
                    JsfUti.messageFatal(null, "El valor ingresado no puede ser mayor al 100%", "");
                    exonera = Boolean.TRUE;
                }
            }
        }
        return exonera;
    }

    public void abrirDialogoTipoLiquidaciones() {
        parametros = new HashMap<>();
        parametros.put("estado", Boolean.TRUE);
        parametros.put("permiteExoneracion", Boolean.TRUE);
        tipoLiquidaciones = manager.findObjectByParameterOrderList(RenTipoLiquidacion.class, parametros, new String[]{"nombreTransaccion"}, Boolean.TRUE);
        JsfUti.executeJS("PF('tipoLiquidacionesDlg').show();");
    }

    public void agregarTipoLiquidacionSolicitud() {
        FnSolicitudTipoLiquidacionExoneracion tipoLiqSol;
        try {
            if (this.tipoLiquidacion != null && this.rubroLiquidacion != null) {
                for (FnSolicitudTipoLiquidacionExoneracion tls : this.tipoLiquidacionesEnSolitud) {
                    if (tls.getTipoLiquidacion().equals(this.tipoLiquidacion)) {
                        JsfUti.messageInfo(null, "Informacion", "Tipo Liquidacion se encuentra agregada");
                        return;
                    }
                }
                tipoLiqSol = new FnSolicitudTipoLiquidacionExoneracion();
                tipoLiqSol.setTipoLiquidacion(this.tipoLiquidacion);
                tipoLiqSol.setRubro(this.rubroLiquidacion);
                this.tipoLiquidacionesEnSolitud.add(tipoLiqSol);
            } else {
                JsfUti.messageInfo(null, "Informacion", "Seleccione Tipo Liquidacion y Rubro para Exoneracion");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void eliminarTipoLiquidacionSolicitud(FnSolicitudTipoLiquidacionExoneracion tipoLiqSol) {
        for (FnSolicitudTipoLiquidacionExoneracion tls : this.tipoLiquidacionesEnSolitud) {
            if (tls.getTipoLiquidacion().equals(tipoLiqSol.getTipoLiquidacion())) {
                this.tipoLiquidacionesEnSolitud.remove(tls);
                return;
            }
        }
    }

    public void consultarPredioUrbano() {
        List<CatPredio> prediosConsulta = recaudacion.getListPrediosByPredioModel(predioModel);
        if (prediosUrbanos == null) {
            prediosUrbanos = new ArrayList<>();
        }
        if (prediosConsulta != null && !prediosConsulta.isEmpty()) {
            for (CatPredio p : prediosConsulta) {

                if (!this.prediosUrbanos.contains(p)) {
                    this.prediosUrbanos.add(p);
                }
            }
        }
    }

    public void buscarPredios() {
        for (CatPredioPropietario catPredioPropietario : solicitante.getCatPredioPropietarioCollection()) {
            if (!this.prediosUrbanos.contains(catPredioPropietario.getPredio())) {
                this.prediosUrbanos.add(catPredioPropietario.getPredio());
            }
            System.out.println("Buscado Propietarios...");
        }
    }

    public void consultarPredioRural() {
        System.out.println("/***" + predioModel.getTipoConsultaRural());
        List<CatPredioRuralModel> prediosRConsulta = recaudacion.getListPrediosRuralModelByPredioModel(predioModel);
        Boolean verificador = false;
        if (prediosRConsulta != null && !prediosRConsulta.isEmpty()) {
            for (CatPredioRuralModel p : prediosRConsulta) {
                for (CatPredioRuralModel prediosRurale : prediosRurales) {
                    if (prediosRurale.getPredioRustico() != null && p.getPredioRustico() != null && prediosRurale.getPredioRustico().equals(p.getPredioRustico())) {
                        verificador = true;
                        break;
                    }
                }
                if (p.getPredioRustico() != null && !verificador) {
                    prediosRurales.add(p);
                    break;
                }
                verificador = false;
                for (CatPredioRuralModel prediosRurale : prediosRurales) {
                    if (prediosRurale.getPredioRusctico2017() != null && p.getPredioRusctico2017() != null && prediosRurale.getPredioRusctico2017().equals(p.getPredioRusctico2017())) {
                        verificador = true;
                        break;
                    }
                }
                if (p.getPredioRusctico2017() != null && !verificador) {
                    prediosRurales.add(p);
                    break;
                }
            }
        }
        if (verificador) {
            JsfUti.messageInfo(null, "Informacion", "Predio se encuentra agregado");
        }
    }

    public void eliminarPredioRural(CatPredioRuralModel rural) {
        this.prediosRurales.remove(rural);
    }

    public void eliminarPredioUrbano(int indice) {
        prediosUrbanos.remove(indice);
    }

    public void onChangeRadio() {
        //predioUrbanoConsulta = null;
    }

    public void seleccionarContribuyente() {
        if (cedulaRuc == null || cedulaRuc.equals("")) {
            this.cedulaRuc = predioModel.getContribuyenteConsulta().getCiRuc();
        }

    }

    public void openDlgSolicitante() {
        //findEnte = new CatEnte();o
        this.cedulaRuc = null;
        JsfUti.executeJS("PF('dlgSolicitante').show();");
    }

    public void buscarEnte() {
        solicitante = (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        if (solicitante == null) {
            JsfUti.messageInfo(null, Messages.enteNoExiste, "");
        } else {
            getPredioByPropietario(solicitante);
            JsfUti.messageInfo(null, "Info", "Solicitante: " + solicitante.getNombreCompleto());
        }
    }

    public void getPredioByPropietario(CatEnte ente) {

        prediosUrbanos = recaudacion.getListPrediosByPropietario(ente.getId());
        if (prediosUrbanos == null && prediosUrbanos.isEmpty()) {
            JsfUti.messageError(null, "Error", "No se encontraron predios.");
            prediosUrbanos = new ArrayList<>();
        }
    }

    public void selClase() {
        tipos = (List) manager.findAll(QuerysFinanciero.getTipoExoneracionTipoByClaseAndState, new String[]{"clase"}, new Object[]{clase});
    }

    public void aplicarExoneracionAutomatico() {
        try {
            HashMap<String, Object> paramt = new HashMap<>();

            if (servicesRentas.aplicarExoneracion(null, solicitud, uSession.getName_user()) != null) {
                paramt.put("aprobado", true);
                paramt.put("correccion", null);
                JsfUti.messageInfo(null, "Info", "Se ha aplicado la exoneración satisfactoriamente");
                this.completeTask(this.getTaskId(), paramt);
            } else {
                JsfUti.messageError(null, "Error", "Se produjo un error al procesar la solicitud, inténtelo nuevamente");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Aplicar Exoneracion Auto", e);
        }
    }

    public void aplicarExoneracion() {
        try {
            HashMap<String, Object> paramt = new HashMap<>();
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea("Ingreso de solicitud de exoneración");
            manager.persist(obs);
            ht.setEstado("Finalizado");
            manager.persist(ht);

            switch (aplicacionEstado) {
                case 1:
                    if (solicitud.getValor() == null) {
                        JsfUti.messageInfo(null, "Info", "Debe ingresar un valor y cerciorarse de que haya seleccionado el tipo de valor correcto");
                        return;
                    }
                    manager.persist(solicitud);
                    if (servicesRentas.aplicarExoneracion(ht, null, uSession.getName_user()) != null) {
                        solicitud.setFechaAprobacion(new Date());
                        manager.persist(solicitud);
                        paramt.put("aprobado", true);
                        paramt.put("correccion", null);
                        JsfUti.messageInfo(null, "Info", "Se ha aprobado la exoneración satisfactoriamente");
                        this.completeTask(this.getTaskId(), paramt);
                    } else {
                        JsfUti.messageError(null, "Error", "Se produjo un error al procesar la solicitud, inténtelo nuevamente");
                    }

                    break;

                case 2:
                    if (servicesRentas.rechazarExoneracion(ht)) {
                        paramt.put("aprobado", false);
                        paramt.put("correccion", null);
                        JsfUti.messageInfo(null, "Info", "Se ha rechazado la exoneración satisfactoriamente");
                        this.completeTask(this.getTaskId(), paramt);
                    } else {
                        JsfUti.messageError(null, "Error", "Se produjo un error al procesar la solicitud, inténtelo nuevamente");
                    }
                    break;

                case 3:
                    if (servicesRentas.corregirExoneracion(ht)) {
                        paramt.put("correccion", true);
                        paramt.put("aprobado", null);
                        JsfUti.messageInfo(null, "Info", "Se ha enviado la exoneración a revisión");
                        this.completeTask(this.getTaskId(), paramt);
                    } else {
                        JsfUti.messageError(null, "Error", "Se produjo un error al procesar la solicitud, inténtelo nuevamente");
                    }
                    break;
            }
            this.continuar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public FnSolicitudExoneracion getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(FnSolicitudExoneracion solicitud) {
        this.solicitud = solicitud;
    }

    public FnExoneracionClase getExoneracionClase() {
        return exoneracionClase;
    }

    public void setExoneracionClase(FnExoneracionClase exoneracionClase) {
        this.exoneracionClase = exoneracionClase;
    }

    public FnExoneracionTipo getExoneracionTipo() {
        return exoneracionTipo;
    }

    public void setExoneracionTipo(FnExoneracionTipo exoneracionTipo) {
        this.exoneracionTipo = exoneracionTipo;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Integer getAplicacionEstado() {
        return aplicacionEstado;
    }

    public void setAplicacionEstado(Integer aplicacionEstado) {
        this.aplicacionEstado = aplicacionEstado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<FnExoneracionClase> getClases() {
        return clases;
    }

    public void setClases(List<FnExoneracionClase> clases) {
        this.clases = clases;
    }

    public List<FnExoneracionTipo> getTipos() {
        return tipos;
    }

    public void setTipos(List<FnExoneracionTipo> tipos) {
        this.tipos = tipos;
    }

    public FnExoneracionClase getClase() {
        return clase;
    }

    public void setClase(FnExoneracionClase clase) {
        this.clase = clase;
    }

    public FnExoneracionTipo getTipo() {
        return tipo;
    }

    public void setTipo(FnExoneracionTipo tipo) {
        this.tipo = tipo;
    }

    public ConsultaPrediosEdifView getPredioConsulta() {
        return predioConsulta;
    }

    public void setPredioConsulta(ConsultaPrediosEdifView predioConsulta) {
        this.predioConsulta = predioConsulta;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public RenLiquidacion getOriginal() {
        return original;
    }

    public void setOriginal(RenLiquidacion original) {
        this.original = original;
    }

    public RenLiquidacion getPosterior() {
        return posterior;
    }

    public void setPosterior(RenLiquidacion posterior) {
        this.posterior = posterior;
    }

    public List<RubrosPorTipoLiq> getDetList1() {
        return detList1;
    }

    public void setDetList1(List<RubrosPorTipoLiq> detList1) {
        this.detList1 = detList1;
    }

    public List<RubrosPorTipoLiq> getDetList2() {
        return detList2;
    }

    public void setDetList2(List<RubrosPorTipoLiq> detList2) {
        this.detList2 = detList2;
    }

    public FnExoneracionLiquidacion getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnExoneracionLiquidacion exoneracion) {
        this.exoneracion = exoneracion;
    }

    public List<RenTipoLiquidacion> getTipoLiquidaciones() {
        return tipoLiquidaciones;
    }

    public void setTipoLiquidaciones(List<RenTipoLiquidacion> tipoLiquidaciones) {
        this.tipoLiquidaciones = tipoLiquidaciones;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RenRubrosLiquidacion getRubroLiquidacion() {
        return rubroLiquidacion;
    }

    public void setRubroLiquidacion(RenRubrosLiquidacion rubroLiquidacion) {
        this.rubroLiquidacion = rubroLiquidacion;
    }

    public List<FnSolicitudTipoLiquidacionExoneracion> getTipoLiquidacionesEnSolitud() {
        return tipoLiquidacionesEnSolitud;
    }

    public void setTipoLiquidacionesEnSolitud(List<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionesEnSolitud) {
        this.tipoLiquidacionesEnSolitud = tipoLiquidacionesEnSolitud;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public PropietariosLazy getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(PropietariosLazy propietarios) {
        this.propietarios = propietarios;
    }

    public List<CatPredio> getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(List<CatPredio> prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public List<CatPredioRuralModel> getPrediosRurales() {
        return prediosRurales;
    }

    public void setPrediosRurales(List<CatPredioRuralModel> prediosRurales) {
        this.prediosRurales = prediosRurales;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public List<CatPredio> getPrediosUrbanosAExonerar() {
        return prediosUrbanosAExonerar;
    }

    public void setPrediosUrbanosAExonerar(List<CatPredio> prediosUrbanosAExonerar) {
        this.prediosUrbanosAExonerar = prediosUrbanosAExonerar;
    }

    public Boolean getRevision500Exoneraciones() {
        return revision500Exoneraciones;
    }

    public void setRevision500Exoneraciones(Boolean revision500Exoneraciones) {
        this.revision500Exoneraciones = revision500Exoneraciones;
    }

}
