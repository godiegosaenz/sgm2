/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.coactiva;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CoaAbogado;
import com.origami.sgm.entities.CoaEstadoJuicio;
import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.lazymodels.CoaJuicioLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class JuiciosCoactivos extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected RecaudacionesService rs;
    
    @javax.inject.Inject
    private Entitymanager manager;

    @Inject
    private ServletSession ss;

    protected String noOficio = "";
    protected CoaJuicioLazy juicios;
    protected CoaJuicio juicio = new CoaJuicio();
    protected List<CoaAbogado> listAbogados = new ArrayList<>();
    protected List<CoaEstadoJuicio> listEstados = new ArrayList<>();
    protected List<RenLiquidacion> titulos = new ArrayList<>();
    protected List<RenLiquidacion> titulosConsulta = new ArrayList<>();
    protected List<Attachment> listAttach = new ArrayList<>();
    protected List<HistoricTaskInstance> tareas = new ArrayList<>();
    protected CoaJuicioPredio juicioPredio;
    protected Observaciones obs;
    protected String observacion;
    protected Long tipoReporte;
    protected List<AclUser> funcionarios;
    protected List<Long> listAbogadosReporte = new ArrayList<>();
    protected List<Long> listEstadosReporte = new ArrayList<>();
    protected List<Long> funcionariosReporte;
    protected Date fechaJuicioDesde;
    protected Date fechaJuicioHasta;
    protected Date fechaIngresoDesde;
    protected Date fechaIngresoHasta;
    protected Integer numDesde;
    protected Integer numHasta;
    protected Integer anioDesde;
    protected Integer anioHasta;
    protected Boolean porNumero=Boolean.FALSE;
    protected Boolean porAnio=Boolean.FALSE;
    protected Boolean porFechaJuicio=Boolean.FALSE;
    protected Boolean porFechaIngreso=Boolean.FALSE;
    protected Observaciones observacionInactivo;
    protected CatPredio predioConsulta;
    protected List<CoaJuicio> juiciosConsulta;
    protected CatPredioModel predioModel = new CatPredioModel();
    private Map<String, Object> parametros;
    protected List<CatPredio> prediosUrbanosConsultaSeleccionados;
    protected List<CatPredio> prediosUrbanosConsulta;
    protected Boolean inactivar=Boolean.FALSE;
    protected Boolean esPdf, esExcel;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        esPdf = true;
        esExcel = false;
        juicios = new CoaJuicioLazy();
        listAbogados = rs.getListAbogadosJuicios();
        listEstados = rs.getListEstadosJuicios();
        funcionarios=rs.getUsuariosByRolId(200L);
        juiciosConsulta= new ArrayList<>();
    }

    public void generarMemo() {
        try{
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("coactiva");
            ss.agregarParametro("ES_PDF", esPdf);
            ss.agregarParametro("FORMATO_ACTUAL", Boolean.TRUE);
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
            
            if(porNumero){
                ss.agregarParametro("NUM_DESDE", numDesde);
                ss.agregarParametro("NUM_HASTA", numHasta);
            }
            if(porAnio){
                ss.agregarParametro("ANIO_DESDE", anioDesde);
                ss.agregarParametro("ANIO_HASTA", anioHasta);
            }
            ss.agregarParametro("POR_JUICIO", porFechaJuicio);
            if(porFechaJuicio){
                ss.agregarParametro("F_JUICIO_DESDE", fechaJuicioDesde==null?(Date) manager.find(Querys.getMimFechaJuicio):fechaJuicioDesde);
                ss.agregarParametro("F_JUICIO_HASTA", fechaJuicioHasta==null?new Date():fechaJuicioHasta);
            }
            ss.agregarParametro("POR_INGRESO", porFechaIngreso);
            if(porFechaIngreso){
                ss.agregarParametro("F_INGRESO_DESDE", fechaIngresoDesde==null?(Date) manager.find(Querys.getMimFechaIngreso):fechaIngresoDesde);
                ss.agregarParametro("F_INGRESO_HASTA", fechaIngresoHasta==null?new Date():fechaIngresoHasta);
            }
            ss.agregarParametro("ESTADOS", listEstadosReporte);
            ss.agregarParametro("USUARIO", session.getName_user());
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
            switch(this.tipoReporte.intValue()){
                case 1:
                    ss.setNombreReporte("juiciosIngresados");
                    break;
                case 2:
                    ss.setNombreReporte("juiciosAbogadosFuncionarios");
                    ss.agregarParametro("ABOGADOS", listAbogadosReporte);
                    ss.agregarParametro("FUNCIONARIOS", funcionariosReporte);
                    break;
                case 5:
                    ss.setNombreReporte("detalleJuicioEstado");
                    ss.agregarParametro("DESDE", fechaIngresoDesde==null?(Date) manager.find(Querys.getMimFechaIngreso):fechaIngresoDesde);
                    ss.agregarParametro("HASTA", fechaIngresoHasta==null?new Date():fechaIngresoHasta);
                    ss.agregarParametro("ESTADOS", listEstadosReporte);
                    break;
                case 6:
                    ss.setNombreReporte("cobroCoactivaPorAbogado");
                    ss.agregarParametro("DESDE", fechaIngresoDesde==null?new Date():fechaIngresoDesde);
                    ss.agregarParametro("HASTA", fechaIngresoHasta==null?new Date():fechaIngresoHasta);
                    ss.agregarParametro("ABOGADOS", listAbogadosReporte);
                    break;
                case 7:
                    ss.setNombreReporte("inventarioJuicioAbogadoEstado");
                    ss.agregarParametro("DESDE", fechaIngresoDesde==null?(Date) manager.find(Querys.getMimFechaIngreso):fechaIngresoDesde);
                    ss.agregarParametro("HASTA", fechaIngresoHasta==null?new Date():fechaIngresoHasta);
                    ss.agregarParametro("ABOGADOS", listAbogadosReporte);
                    break;
                default:
                    break;
            }
            if(esPdf)
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            else
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void cambiarBoolean(){
        esExcel = !esPdf;
    }
    
    public void cambiarBoolean2(){
        esPdf = !esExcel;
    }

    public void showDlgInfJuicio(CoaJuicio ju) {
        try {
            tareas = new ArrayList<>();
            listAttach = new ArrayList<>();
            titulos = rs.getLiquidacionesCoactivaByJuicio(ju.getId());
            if (ju.getTramite() != null) {
                if (ju.getTramite().getIdProceso() != null) {
                    tareas = this.getTaskByProcessInstanceIdMain(ju.getTramite().getIdProceso());
                    listAttach = this.getProcessInstanceAllAttachmentsFiles(ju.getTramite().getIdProceso());
                }
            }
            juicio = ju;
            JsfUti.update("formInformCoa");
            JsfUti.executeJS("PF('dlgVerInfoCoa').show();");
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgEditJuicio(CoaJuicio ju) {
        observacionInactivo= new Observaciones();
        juicio = ju;
        /*JsfUti.update("formJuicio");
        JsfUti.executeJS("PF('dlgEditJuicio').show();");*/
    }

    public void updateJuicio() {
        try {
            if (juicio.getId() != null) {
                juicio.setFechaEdicion(new Date());
                juicio.setUsuarioEdicion(session.getName_user());
                if(juicio.getEstadoJuicio().getId().equals(4L)){
                    for (CoaJuicioPredio jp : juicio.getCoaJuicioPredioCollection()) {
                        if(jp.getLiquidacion().getEstadoLiquidacion().getId().equals(2L)){
                            JsfUti.messageWarning(null, "Mensaje", "Existen Emisiones Pendientes de Pagos. El Juicio no puede Pasar a 'BAJA Y ARCHIVO DE LA CAUSA'");
                            return;
                        }
                    }
                }
                acl.persist(juicio);
                JsfUti.update("mainForm");
                JsfUti.executeJS("PF('dlgEditJuicio').hide();");
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void inactivarJuicio(){
        // A LAS EMISIONES SE ELIMINA LA COACTIVA
        try{
            if(this.juicio!=null){
                //NUEVA VERSION SE GRABA EN OBSERVACIONES // SE DA DE BAJA EL FLUJO // INACTIVA EL HT
                if(observacionInactivo.getObservacion()!=null && observacionInactivo.getObservacion().length()>0){
                    for (CoaJuicioPredio jp : juicio.getCoaJuicioPredioCollection()) {
                        if(jp.getLiquidacion()!=null){
                            jp.getLiquidacion().setCoactiva(Boolean.FALSE);
                            jp.getLiquidacion().setEstadoCoactiva(1);
                            acl.persist(jp.getLiquidacion());
                        }
                    }
                    juicio.setFechaEdicion(new Date());
                    juicio.setUsuarioEdicion(session.getName_user());
                    juicio.setEstado(Boolean.FALSE);
                    if(this.juicio.getTramite()!=null){
                        observacionInactivo.setEstado(Boolean.TRUE);
                        observacionInactivo.setFecCre(new Date());
                        observacionInactivo.setIdTramite(juicio.getTramite());
                        observacionInactivo.setTarea("INACTIVACION DE JUICIO");
                        observacionInactivo.setUserCre(session.getName_user());
                        acl.persist(observacionInactivo);
                        if(juicio.getTramite().getEstado().equalsIgnoreCase("Pendiente")){
                           this.engine.deleteProcessInstance(juicio.getTramite().getIdProceso(), "Eliminado por usuario - " + (new Date()).getTime()); 
                        }
                        juicio.getTramite().setEstado("Inactivo");
                        acl.persist(juicio.getTramite());
                    }
                    acl.persist(juicio);
                    JsfUti.update("mainForm");
                    JsfUti.executeJS("PF('dlgInactivarJuicio').hide();");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarTitulosJuicio(CoaJuicio ju) {
        try {
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreReporte("masterTituloCredito");
            ss.setNombreSubCarpeta("coactiva");
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/coactiva/"));
            ss.agregarParametro("ID_JUICIO", ju.getId());
            ss.agregarParametro("TRAMITE", ju.getTramite()==null?null:ju.getTramite().getId());
            if(esPdf)
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            else
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void seleccionarJuicioPredio(CoaJuicioPredio juicioPredio){
        try{
            if(juicioPredio!=null){
                this.juicioPredio=juicioPredio;
                observacion="AB. PREVIO: "+(juicioPredio.getAbogadoJuicio()!=null?juicioPredio.getAbogadoJuicio().getDetalle():"SIN AB.");
            }
            else{
                this.juicioPredio= new CoaJuicioPredio();
                observacion="AB. PREVIO: ";
                if(this.juicio!=null && this.juicio.getCoaJuicioPredioCollection()!=null && !this.juicio.getCoaJuicioPredioCollection().isEmpty() && this.predioModel!=null){
                    for (CoaJuicioPredio jl : this.juicio.getCoaJuicioPredioCollection()) {
                        this.predioModel.setNumPredio(jl.getLiquidacion().getPredio().getNumPredio());
                        this.consultarPredioUrbano();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void actualizarAbogadoPredio(){
        if(this.juicioPredio.getId()!=null){
            manager.persist(this.juicioPredio);
        }else{
            if(this.juicio!=null && this.juicio.getCoaJuicioPredioCollection()!=null && !this.juicio.getCoaJuicioPredioCollection().isEmpty()){
                for (CoaJuicioPredio jp : juicio.getCoaJuicioPredioCollection()) {
                    observacion=observacion+jp.getAbogadoJuicio().getDetalle()+", ";
                    jp.setAbogadoJuicio(this.juicioPredio.getAbogadoJuicio());
                    manager.persist(jp);
                }
            }
        }
        observacion=observacion+"- AB. ACTUAL:"+this.juicioPredio.getAbogadoJuicio().getDetalle();
        if(this.juicio!=null && this.juicio.getTramite()!=null){
            obs= new Observaciones();
            obs.setIdTramite(this.juicio.getTramite());
            obs.setTarea("Actualizacion de Abogado");
            obs.setObservacion(observacion);
            obs.setUserCre(session.getName_user());
            obs.setFecCre(new Date());
            obs.setEstado(Boolean.TRUE);
            manager.persist(obs);
        }
    }
    
    public void desactivarJuicioPredio(){
        try{
            if(juicio!=null && juicioPredio!=null){
                //SE INACTIVA LA EMISION DEL JUICIO
                //LA EMISION VUELVE A UN ESTADO INCIAL COACTIVA: FALSE ESATDO_COACTIVA:1
                juicioPredio.setEstado(Boolean.FALSE);
                acl.persist(juicioPredio);
                juicioPredio.getLiquidacion().setCoactiva(Boolean.FALSE);
                juicioPredio.getLiquidacion().setEstadoCoactiva(1);
                acl.persist(juicioPredio.getLiquidacion());
                String observacionInactivacion=(juicioPredio.getJuicio().getObservacion()==null?"":juicioPredio.getJuicio().getObservacion()+"\n")+"INACTIVACION TITULO: "+juicioPredio.getLiquidacion().getPredio().getNumPredio()+"-"+juicioPredio.getLiquidacion().getAnio()+". VALOR PREVIO:"+juicioPredio.getJuicio().getTotalDeuda();
                juicioPredio.getJuicio().setTotalDeuda(juicioPredio.getJuicio().getTotalDeuda().subtract(juicioPredio.getLiquidacion().getTotalPago()));
                observacionInactivacion=observacionInactivacion+"; VALOR ACTUAL:"+juicioPredio.getJuicio().getTotalDeuda();
                juicioPredio.getJuicio().setObservacion(observacionInactivacion);
                acl.persist(juicioPredio.getJuicio());
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /*public void generarTituloCredito(RenLiquidacion emision) {
     try {
     ss.borrarParametros();
     ss.instanciarParametros();
     ss.setTieneDatasource(Boolean.TRUE);
     ss.setNombreReporte("tituloCredito");
     ss.setNombreSubCarpeta("coactiva");
     ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
     ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/coactiva/");
     ss.agregarParametro("ruta_firmas", JsfUti.getRealPath("/") + "/css/firmas/");
     ss.agregarParametro("ID_TITULO", emision.getId());
     JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
     } catch (Exception e) {
     Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
     }
     }*/
    
    public void consultarPredioUrbano(){
        predioConsulta=null;
        prediosUrbanosConsultaSeleccionados=null;
        prediosUrbanosConsulta = new ArrayList<>();
        parametros = new HashMap<>();
        try {
            switch (predioModel.getTipoConsultaUrbano().intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", predioModel.getNumPredio());
                        predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getCdla() > 0 || predioModel.getMzDiv() > 0 || predioModel.getSolar() > 0 || predioModel.getDiv1() > 0 || predioModel.getDiv2() > 0 || predioModel.getDiv3() > 0 || predioModel.getDiv4() > 0 || predioModel.getDiv5() > 0 || predioModel.getDiv6() > 0 || predioModel.getDiv7() > 0 || predioModel.getDiv8() > 0 || predioModel.getDiv9() > 0 || predioModel.getPhv() > 0 || predioModel.getPhh() > 0) {
                        parametros.put("estado", "A");
                        if(predioModel.getSector()>0)
                            parametros.put("sector", predioModel.getSector());
                        if(predioModel.getMz()>0)
                            parametros.put("mz", predioModel.getMz());
                        if(predioModel.getCdla()>0)
                            parametros.put("cdla", predioModel.getCdla());
                        if(predioModel.getMzDiv()>0)
                            parametros.put("mzdiv", predioModel.getMzDiv());
                        if(predioModel.getSolar()>0)
                            parametros.put("solar", predioModel.getSolar());
                        if(predioModel.getDiv1()>0)
                            parametros.put("div1", predioModel.getDiv1());
                        if(predioModel.getDiv2()>0)
                            parametros.put("div2", predioModel.getDiv2());
                        if(predioModel.getDiv3()>0)
                            parametros.put("div3", predioModel.getDiv3());
                        if(predioModel.getDiv4()>0)
                            parametros.put("div4", predioModel.getDiv4());
                        if(predioModel.getDiv5()>0)
                            parametros.put("div5", predioModel.getDiv5());
                        if(predioModel.getDiv6()>0)
                            parametros.put("div6", predioModel.getDiv6());
                        if(predioModel.getDiv7()>0)
                            parametros.put("div7", predioModel.getDiv7());
                        if(predioModel.getDiv8()>0)
                            parametros.put("div8", predioModel.getDiv8());
                        if(predioModel.getDiv9()>0)
                            parametros.put("div9", predioModel.getDiv9());
                        if(predioModel.getPhv()>0)
                            parametros.put("phv", predioModel.getPhv());
                        if(predioModel.getPhh()>0)
                            parametros.put("phh", predioModel.getPhh());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4://UBICACION
                    if(predioModel.getCiudadela()!=null || predioModel.getMzUrb()!=null || predioModel.getSlUrb()!=null){
                        if(predioModel.getCiudadela()!=null)
                            parametros.put("ciudadela", predioModel.getCiudadela());
                        if(predioModel.getMzUrb()!=null)
                            parametros.put("urbMz", predioModel.getMzUrb());
                        if(predioModel.getSlUrb()!=null)
                            parametros.put("urbSolarnew", predioModel.getSlUrb());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                default:
                    break;
            }
            if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty() && prediosUrbanosConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("numPredio", prediosUrbanosConsulta.get(0).getNumPredio());
                predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
            }
            if(predioConsulta!=null){
                
            }else{
                if (prediosUrbanosConsulta != null && prediosUrbanosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                }else{
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void seleccionarPredio(Long tipoPredio) {
        try {
            parametros = new HashMap<>();
            switch (tipoPredio.intValue()) {
                case 1:
                    if (prediosUrbanosConsultaSeleccionados != null && !prediosUrbanosConsultaSeleccionados.isEmpty()) {
                        for (CatPredio pucs : prediosUrbanosConsultaSeleccionados) {
                            parametros.put("numPredio", pucs.getNumPredio());
                            predioConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            /*if(!this.prediosUrbanos.contains(predioUrbanoConsulta)){
                                this.prediosUrbanos.add(predioUrbanoConsulta);
                                predioSeleccionado(predioUrbanoConsulta);
                            }*/
                        }
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(InicioCoactiva.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void limpiarConsultaJuicios(){
        juiciosConsulta= new ArrayList<>();
        predioConsulta=null;
        predioModel= new CatPredioModel();
    }
    public void consultarTitulosEnJuicios(){
        try{
            if(predioConsulta!=null){
                List<CoaJuicio> jConsulta= manager.findAll(Querys.juiciosPorEmisiones, new String[]{"predio"}, new Object[]{predioConsulta});
                if(jConsulta!=null && !jConsulta.isEmpty()){
                    for (CoaJuicio jc : jConsulta){
                        if(!juiciosConsulta.contains(jc))
                            juiciosConsulta.add(jc);
                    }
                }else{
                    JsfUti.messageInfo(null, "Mensaje", "No se encontraron Juicios para el predio Consultado"+predioConsulta.getNumPredio()+ " Cod:"+predioConsulta.getCodigoPredialCompleto());
                }
            }else{
                JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Consultar Juicio");
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void removerEmisionJuicioPredio(){
        if(juicio!=null && juicioPredio!=null){
            //SE INACTIVA LA EMISION DEL JUICIO
            //LA EMISION VUELVE A UN ESTADO INCIAL COACTIVA: FALSE ESATDO_COACTIVA:1
            juicioPredio.setEstado(Boolean.FALSE);
            acl.persist(juicioPredio);
        }
    }
    
    public void consultarTituloPorPredio(){
        try{
            if(predioConsulta!=null){
                List<RenLiquidacion> tc= rs.getEmisionesCoactivaAntigua(predioConsulta);
                if(tc!=null && !tc.isEmpty()){
                    for (RenLiquidacion l : tc){
                        if(!titulosConsulta.contains(l))
                            titulosConsulta.add(l);
                    }
                }else{
                    JsfUti.messageInfo(null, "Mensaje", "No se encontraron Emisiones para el predio Consultado"+predioConsulta.getNumPredio()+ " Cod:"+predioConsulta.getCodigoPredialCompleto());
                }
            }else{
                JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Consultar Titulos");
            }
        } catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void deleteLiquidacion(int indice) {
        titulosConsulta.remove(indice);
    }
    
    public void agregarEmisionesJuicio(){
        try{
            if(titulosConsulta!=null && !titulosConsulta.isEmpty()){
                if(juicio!=null){
                    CoaAbogado ab=juicio.getAbogadoJuicio();
                    BigDecimal valorAdicional=new BigDecimal("0.00");
                    for (RenLiquidacion t : titulosConsulta) {
                        juicioPredio= new CoaJuicioPredio();
                        juicioPredio.setEstado(Boolean.TRUE);
                        juicioPredio.setJuicio(juicio);
                        juicioPredio.setLiquidacion(t);
                        juicioPredio.setPredio(t.getPredio());
                        juicioPredio.setAnioDeuda(t.getAnio());
                        juicioPredio.setValorDeuda(t.getTotalPago());//****
                        juicioPredio.setAbogadoJuicio(ab);///***
                        valorAdicional=valorAdicional.add(t.getTotalPago());
                        //CREAR JUICIOPREDIO
                        manager.persist(juicioPredio);
                        //ACTUALIZA LA EMISION
                        t.setCoactiva(Boolean.TRUE);
                        t.setEstadoCoactiva(2);
                        acl.persist(t);
                    }
                    this.juicio.setTotalDeuda(this.juicio.getTotalDeuda().add(valorAdicional));
                    acl.persist(juicio);
                    titulosConsulta = new ArrayList<>();
                }
            }else{
                JsfUti.messageInfo(null, "Mensaje", "La lista de Titulos no debe estar Vacia");
            }
        }catch (Exception e) {
            Logger.getLogger(JuiciosCoactivos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public CoaJuicioLazy getJuicios() {
        return juicios;
    }

    public void setJuicios(CoaJuicioLazy juicios) {
        this.juicios = juicios;
    }

    public CoaJuicio getJuicio() {
        return juicio;
    }

    public void setJuicio(CoaJuicio juicio) {
        this.juicio = juicio;
    }

    public List<Attachment> getListAttach() {
        return listAttach;
    }

    public void setListAttach(List<Attachment> listAttach) {
        this.listAttach = listAttach;
    }

    public List<HistoricTaskInstance> getTareas() {
        return tareas;
    }

    public void setTareas(List<HistoricTaskInstance> tareas) {
        this.tareas = tareas;
    }

    public List<RenLiquidacion> getTitulos() {
        return titulos;
    }

    public void setTitulos(List<RenLiquidacion> titulos) {
        this.titulos = titulos;
    }

    public String getNoOficio() {
        return noOficio;
    }

    public void setNoOficio(String noOficio) {
        this.noOficio = noOficio;
    }

    public List<CoaAbogado> getListAbogados() {
        return listAbogados;
    }

    public void setListAbogados(List<CoaAbogado> listAbogados) {
        this.listAbogados = listAbogados;
    }

    public List<CoaEstadoJuicio> getListEstados() {
        return listEstados;
    }

    public void setListEstados(List<CoaEstadoJuicio> listEstados) {
        this.listEstados = listEstados;
    }

    public CoaJuicioPredio getJuicioPredio() {
        return juicioPredio;
    }

    public void setJuicioPredio(CoaJuicioPredio juicioPredio) {
        this.juicioPredio = juicioPredio;
    }

    public Long getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Long tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public List<AclUser> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<AclUser> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public List<Long> getListAbogadosReporte() {
        return listAbogadosReporte;
    }

    public void setListAbogadosReporte(List<Long> listAbogadosReporte) {
        this.listAbogadosReporte = listAbogadosReporte;
    }

    public List<Long> getListEstadosReporte() {
        return listEstadosReporte;
    }

    public void setListEstadosReporte(List<Long> listEstadosReporte) {
        this.listEstadosReporte = listEstadosReporte;
    }

    public List<Long> getFuncionariosReporte() {
        return funcionariosReporte;
    }

    public void setFuncionariosReporte(List<Long> funcionariosReporte) {
        this.funcionariosReporte = funcionariosReporte;
    }

    public Date getFechaJuicioHasta() {
        return fechaJuicioHasta;
    }

    public void setFechaJuicioHasta(Date fechaJuicioHasta) {
        this.fechaJuicioHasta = fechaJuicioHasta;
    }

    public Date getFechaIngresoDesde() {
        return fechaIngresoDesde;
    }

    public void setFechaIngresoDesde(Date fechaIngresoDesde) {
        this.fechaIngresoDesde = fechaIngresoDesde;
    }

    public Date getFechaIngresoHasta() {
        return fechaIngresoHasta;
    }

    public void setFechaIngresoHasta(Date fechaIngresoHasta) {
        this.fechaIngresoHasta = fechaIngresoHasta;
    }

    public Integer getNumDesde() {
        return numDesde;
    }

    public void setNumDesde(Integer numDesde) {
        this.numDesde = numDesde;
    }

    public Integer getNumHasta() {
        return numHasta;
    }

    public void setNumHasta(Integer numHasta) {
        this.numHasta = numHasta;
    }

    public Integer getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(Integer anioDesde) {
        this.anioDesde = anioDesde;
    }

    public Integer getAnioHasta() {
        return anioHasta;
    }

    public void setAnioHasta(Integer anioHasta) {
        this.anioHasta = anioHasta;
    }

    public Boolean getPorNumero() {
        return porNumero;
    }

    public void setPorNumero(Boolean porNumero) {
        this.porNumero = porNumero;
    }

    public Boolean getPorAnio() {
        return porAnio;
    }

    public void setPorAnio(Boolean porAnio) {
        this.porAnio = porAnio;
    }

    public Boolean getPorFechaJuicio() {
        return porFechaJuicio;
    }

    public void setPorFechaJuicio(Boolean porFechaJuicio) {
        this.porFechaJuicio = porFechaJuicio;
    }

    public Boolean getPorFechaIngreso() {
        return porFechaIngreso;
    }

    public void setPorFechaIngreso(Boolean porFechaIngreso) {
        this.porFechaIngreso = porFechaIngreso;
    }

    public Date getFechaJuicioDesde() {
        return fechaJuicioDesde;
    }

    public void setFechaJuicioDesde(Date fechaJuicioDesde) {
        this.fechaJuicioDesde = fechaJuicioDesde;
    }

    public Observaciones getObservacionInactivo() {
        return observacionInactivo;
    }

    public void setObservacionInactivo(Observaciones observacionInactivo) {
        this.observacionInactivo = observacionInactivo;
    }

    public CatPredio getPredioConsulta() {
        return predioConsulta;
    }

    public void setPredioConsulta(CatPredio predioConsulta) {
        this.predioConsulta = predioConsulta;
    }

    public List<CoaJuicio> getJuiciosConsulta() {
        return juiciosConsulta;
    }

    public void setJuiciosConsulta(List<CoaJuicio> juiciosConsulta) {
        this.juiciosConsulta = juiciosConsulta;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<CatPredio> getPrediosUrbanosConsultaSeleccionados() {
        return prediosUrbanosConsultaSeleccionados;
    }

    public void setPrediosUrbanosConsultaSeleccionados(List<CatPredio> prediosUrbanosConsultaSeleccionados) {
        this.prediosUrbanosConsultaSeleccionados = prediosUrbanosConsultaSeleccionados;
    }

    public List<CatPredio> getPrediosUrbanosConsulta() {
        return prediosUrbanosConsulta;
    }

    public void setPrediosUrbanosConsulta(List<CatPredio> prediosUrbanosConsulta) {
        this.prediosUrbanosConsulta = prediosUrbanosConsulta;
    }

    public Boolean getInactivar() {
        return inactivar;
    }

    public void setInactivar(Boolean inactivar) {
        this.inactivar = inactivar;
    }

    public List<RenLiquidacion> getTitulosConsulta() {
        return titulosConsulta;
    }

    public void setTitulosConsulta(List<RenLiquidacion> titulosConsulta) {
        this.titulosConsulta = titulosConsulta;
    }

    public Boolean getEsPdf() {
        return esPdf;
    }

    public void setEsPdf(Boolean esPdf) {
        this.esPdf = esPdf;
    }

    public Boolean getEsExcel() {
        return esExcel;
    }

    public void setEsExcel(Boolean esExcel) {
        this.esExcel = esExcel;
    }

}
