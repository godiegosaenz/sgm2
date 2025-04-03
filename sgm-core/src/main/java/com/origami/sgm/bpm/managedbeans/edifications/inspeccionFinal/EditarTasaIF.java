/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetalleInspeccion;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class EditarTasaIF extends BpmManageBeanBaseRoot implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;

    @javax.inject.Inject
    protected SeqGenMan secuencia;
    
    @Inject
    private ReportesView reportes;
    
    private HashMap<String, Object> paramsActiviti;
    private List<HistoricoReporteTramite> hrts;
    private CatPredio datosPredio;
    private Observaciones obs;
    private HistoricoTramites ht, htEncontrado;
    private HistoricoReporteTramite hrt;
    private PdfReporte reporte;
    private HistoricoTramiteDet htd;
    private List<HistoricoTramiteDet> htdList;
    private CatEnte solicitante, responsableTec;
    private CatPredioPropietario propietario;
    private PeInspeccionFinal peInspeccionFinalV;
    private List<PeDetalleInspeccion> detalleInspeccionList;
    private AclUser usuario, director;
    private String numTramite, codigoCatastral, cedulaRuc, cedulaRucResp;
    private List<CatPredioPropietario> lisPropietarios;
    private List<CatEnte> enteList;
    private PeDetalleInspeccion detalleInspeccion;
    private CatEdfProp caracteristica;
    private PeDetallePermiso pdp;
    private List<PeDetalleInspeccion> peDetallePerIFList;
    private List<CatEdfProp> cepList;
    private CatEdfCategProp categoria;
    private String edifPrincipal, mensaje;
    private Boolean nuevoReporte = false, inspeccionEncontrada = true, clickInspeccion = false;
    private List<CtlgItem> detallesList, listaDetalle;
    private MsgFormatoNotificacion formatoMsg;
    private List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList;
    private List<CatEdfCategProp> catEdfCategPropList;
    private PeFirma firma;
    private PePermiso permiso;
    private PeInspeccionCabEdificacion peInspeccionCabEdificacion, peInspeccionCabEdificacionTemp;
    private PeTipoPermiso tipoInsp;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private CatCanton canton;
    private CatParroquia parroquia;
    private PePermisoLazy permisosList;
    private List<PePermisoCabEdificacion> pePermisosIFList;
    
    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                String s = "";
                paramsActiviti = new HashMap<String, Object>();
                detalleInspeccionList = new ArrayList();
                listaDetalle = new ArrayList();
                obs = new Observaciones();
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                
                peInspeccionFinalV = ht.getPeInspeccionFinal();
                
                
                if(peInspeccionFinalV == null){
                    inspeccionEncontrada = false;
                    mensaje = "No se ha encontrado la inspección previa. Regrese a su bandeja de tareas.";
                    return;
                }else{
                    datosPredio = peInspeccionFinalV.getPredio();
                    peInspeccionFinalV.setFechaIngreso(new Date());
                    peInspeccionFinalV.setUsuarioIngreso(uSession.getName_user()+"-Modificacion");
                    if(datosPredio!=null){
                        parroquia = datosPredio.getCiudadela().getCodParroquia();
                        canton = parroquia.getIdCanton();
                        this.buscarPredio();
                    }
                    peInspeccionCabEdificacionTemp = new PeInspeccionCabEdificacion();
                    peInspeccionCabEdificacionList = (List<PeInspeccionCabEdificacion>) peInspeccionFinalV.getPeInspeccionCabEdificacionCollection();
                    if(peInspeccionCabEdificacionList==null)
                        peInspeccionCabEdificacionList = new ArrayList<>();
                    if(peInspeccionFinalV.getAreaConst()!=null && peInspeccionFinalV.getAreaConst().compareTo(BigDecimal.ZERO)>0 && peInspeccionFinalV.getAreaParqueos()!=null)
                        peInspeccionFinalV.setAreaConst(peInspeccionFinalV.getAreaConst().subtract(peInspeccionFinalV.getAreaParqueos()));
                    detalleInspeccion = new PeDetalleInspeccion();
                    clickInspeccion = false;
                    detalleInspeccion.setPorcentaje(new BigDecimal(100));
                    permiso = (PePermiso) services.find(Querys.getPePermisoById, new String[]{"id"}, new Object[]{Long.parseLong(peInspeccionFinalV.getNumPermisoConstruc()+"")});
                    
                }
                
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                detallesList = servicesIF.obtenerCtlgItemListByNombreDeCatalogo("permiso_inspeccion.detalle");
                catEdfCategPropList = (List<CatEdfCategProp>) services.findAll(Querys.getCatCategoriasPropConstruccionList, new String[]{}, new Object[]{});
                solicitante = ht.getSolicitante();
                
                //Validación de que haya un documento anterior                
                
                htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                if(this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef")!=null)
                        s = this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef").toString();
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                hrt = new HistoricoReporteTramite();
                htd = new HistoricoTramiteDet();
                if (hrts == null || hrts.isEmpty()) {
                    nuevoReporte = true;
                }else{
                    hrt.setNombreTarea(hrts.get(0).getNombreTarea());
                }
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("4")});
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                director = firma.getAclUser();
                
                formula = (MatFormulaTramite) services.find(MatFormulaTramite.class, 6L);
                gutil = new GroovyUtil(formula.getFormula());
                tipoInsp = (PeTipoPermiso) services.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{"INF"});
                permisosList = new PePermisoLazy();
                responsableTec = peInspeccionFinalV.getRespTecnico();
                cedulaRucResp = responsableTec.getCiRuc();
                //this.sumarAreaConstruccionInit();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void sumarAreaConstruccionInit(){
        BigDecimal areaTemp = BigDecimal.ZERO;
        for(PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList){
            areaTemp.add(temp.getAreaConstruccion());
        }
        peInspeccionFinalV.setAreaConst(areaTemp);
    }
    
    /**
     * Carga los datos necesarios para iniciar la tarea.
     * 
     */
    public void buscarTramite(){
        try{
            
            datosPredio = permiso.getIdPredio();

            if(datosPredio != null){
                this.buscarPredio();
                this.sumarEdificaciones();
            }else{
                JsfUti.messageError(null, "Error", "No se ha encontrado ningún predio.");
                return;
            }
                
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void buscarResponsable(){
        try{
            responsableTec = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
            if(responsableTec==null)
                JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
            else{
                JsfUti.update("respDlgForm");
                JsfUti.executeJS("PF('dlgResp').show()");
                htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());
                peInspeccionFinalV.setRespTecnico(responsableTec);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Suma el área de construcción total del predio en base a las áreas de cada
     * una de sus edificaciones.
     * 
     */
    public void sumarEdificaciones(){
        try{
            BigDecimal total = new BigDecimal(0);
            for(PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList){
                total = total.add(temp.getAreaConstruccion());
            }
            peInspeccionFinalV.setAreaConst(total);
            peInspeccionFinalV.setEvaluoLiquidacion(total.multiply(tipoInsp.getValor()));
            peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            JsfUti.update("frmMain");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Suma el área de construcción total del predio en base a las áreas de cada
     * una de sus edificaciones.
     */
    public void sumarEdificacionesPermiso(){
        BigDecimal total = new BigDecimal(0);
        PeInspeccionCabEdificacion tempEdif;
        PeDetalleInspeccion tempDet;
        List<PeDetalleInspeccion> listDet;
        peInspeccionCabEdificacionList = new ArrayList();
        
        for(PePermisoCabEdificacion temp : pePermisosIFList){
            tempEdif = new PeInspeccionCabEdificacion();
            listDet = new ArrayList<>();
            tempEdif.setAreaConstruccion(temp.getAreaConstruccion());
            tempEdif.setCantidadPisos(Integer.parseInt(temp.getNumeroPisos()+""));
            tempEdif.setDescEdificacion(temp.getDescEdificacion());
            tempEdif.setEstado(true);
            tempEdif.setNumEdificacion(Integer.parseInt(temp.getNumEdificacion()+""));
            for(PeDetallePermiso tempDetalle : temp.getPeDetallePermisoCollection()){
                tempDet = new PeDetalleInspeccion();
                tempDet.setArea(tempDetalle.getArea());
                tempDet.setCaracteristica(tempDetalle.getIdCatEdfProp());
                tempDet.setPorcentaje(tempDetalle.getPorcentaje());
                tempDet.setEdificacion(tempEdif);
                listDet.add(tempDet);
            }
            tempEdif.setPeDetalleInspeccionCollection(listDet);
            total = total.add(temp.getAreaConstruccion());
            peInspeccionCabEdificacionList.add(tempEdif);
        }
        peInspeccionFinalV.setAreaConst(total);
        peInspeccionFinalV.setEvaluoLiquidacion(total.multiply(tipoInsp.getValor()));
        peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
        JsfUti.update("frmMain");
    }
    
    /**
     * Busca un ente a partir del número de cédula o RUC.
     */
    public void buscarEnte() {
        //CatEnte tempEnte = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        CatEnte tempEnte = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        if (tempEnte != null) {
            enteList.add(tempEnte);
            cedulaRuc = "";
        }
    }
    
    /**
     * Inicializa valores que volverán a ser usados.
     * 
     */
    public void setearValores() {
        cedulaRuc = "";
        enteList = new ArrayList<>();
    }
    
    /**
     * Elimina un propietario del predio.
     * 
     * @param prop 
     */
    public void eliminarPropietario(CatPredioPropietario prop) {
        if(prop.getId()!=null){
            prop.setEstado("I");
            services.update(prop);
        }
        lisPropietarios.remove(prop);
    }
    
    /**
     * Al seleccionar una edificación se instancia una lista de detalle en caso de
     * que esta esté nula, para poder agregar detalles.
     * 
     * @param event 
     */
    public void onRowSelect(SelectEvent event) {  
        peDetallePerIFList = (List<PeDetalleInspeccion>) peInspeccionCabEdificacion.getPeDetalleInspeccionCollection();
        if(peDetallePerIFList==null || peDetallePerIFList.isEmpty()){
            peDetallePerIFList = new ArrayList<>();
            peInspeccionCabEdificacion.setPeDetalleInspeccionCollection(peDetallePerIFList);
        }
    }
    
    /**
     * Busca el predio según su código predial. Asimismo carga sus datos, como los
     * propietarios, etc.
     * 
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;
        
        if((CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()}) != null){
            datosPredio = (CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
            if(datosPredio.getNumPredio() != null){
                ht.setNumPredio(datosPredio.getNumPredio());
                parroquia = datosPredio.getCiudadela().getCodParroquia();
                canton = parroquia.getIdCanton();
                JsfUti.messageInfo(null, "Info", "Predio encontrado.");
            }else{
                JsfUti.messageError(null, "Error", "El predio encontrado no tiene número de predio.");
                return;
            }                
        }else{
            
            JsfUti.messageError(null, "Error", "No se encontró el predio.");
            return;
        }
        lisPropietarios = new ArrayList<CatPredioPropietario>();
        codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

        propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

        for (CatPredioPropietario temp : propTemp) {
            if (temp.getEstado().equals("A")) {
                lisPropietarios.add(temp);
            }
        }
    }
    
    /**
     * Elimina un PeDetalleInspeccion (característica de una edificación de 
     * PeInspeccionFinal) de la lista.
     * 
     * @param detalleIns 
     */
    public void eliminarPermiso(PeDetalleInspeccion detalleIns){
        if(detalleIns.getId()!=null){
            detalleIns.setEstado(false);
            services.update(detalleIns);
        }
        peDetallePerIFList.remove(detalleIns);
    }
    
    /**
     * Elimina un PeInspeccionCabEdificacion (Una edificación de PeInspeccionFinal)
     * de la lista.
     * 
     * @param inspeccion 
     */
    public void eliminarInspeccion(PeInspeccionCabEdificacion inspeccion){
        BigDecimal temp;
        if(inspeccion.getId()!=null){
            inspeccion.setEstado(false);
            services.update(inspeccion);
        }
        temp = this.peInspeccionFinalV.getAreaConst();
        temp = temp.subtract(inspeccion.getAreaConstruccion());
        this.peInspeccionFinalV.setAreaConst(temp);
        this.peInspeccionFinalV.setEvaluoLiquidacion(temp.multiply(tipoInsp.getValor()));
        this.peInspeccionFinalV.setEvaluoLiquidacion(this.peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
        JsfUti.update("frmMain");
        peInspeccionCabEdificacionList.remove(inspeccion);
    }
    
    public void verificarCaracteristicas(){  
        PeDetallePermiso temp = null;
        try{
            if(peInspeccionCabEdificacion != null && peInspeccionCabEdificacion.getNumEdificacion() == 0){
                    if(peDetallePerIFList.size()>5){
                        JsfUti.messageInfo(null, "Info", "Características de la edificación ingresadas correctamente.");
                    }else{            
                        JsfUti.messageInfo(null, "Info", "La edificación principal debe tener al menos 6 características principales.");
                        return;
                    }
            }else{
                if(peDetallePerIFList.size()>6){
                    JsfUti.messageInfo(null, "Info", "Características de la edificación ingresadas correctamente.");
                }else{            
                    JsfUti.messageInfo(null, "Info", "Las edificaciones deben tener al menos 6 características principales.");
                }
            }
            JsfUti.executeJS("PF('dlgMostrarDetalle').hide()");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda todos los datos editados de la inspección final, previamente validados
     * 
     */
    public void guardarDatos(){
        
        try{
            reporte = new PdfReporte();
            BigInteger sec;
            String msg="";
            BigDecimal total = new BigDecimal(0);

            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }
            if(responsableTec==null){
                JsfUti.messageError(null, "Error", "Debe seleccionar un responsable técnico antes de continuar.");
                return;
            }
            if(peInspeccionCabEdificacionList == null || peInspeccionCabEdificacionList.isEmpty()){
                JsfUti.messageError(null, "Error", "La inspección final debe tener una edificación al menos.");
                return;
            }
            
            for(PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList){
                if(temp.getNumEdificacion().equals(0) && temp.getPeDetalleInspeccionCollection().size()<6){
                    JsfUti.messageInfo(null, "Info", "La edificación principal debe tener al menos 6 características principales.");
                    return;
                }
            }
        
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.instanciarParametros();

            hrt.setFecCre(new Date());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_IF-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);

            //sec = secuencia.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{anio});

            hrt.setCodValidacion((hrts.get(0).getSecuencia().add(BigInteger.valueOf(hrts.size()))) + "" + hrt.getProceso());
            hrt.setSecuencia(hrts.get(0).getSecuencia());
            if (hrt.getId() == null) {
                hrt = (HistoricoReporteTramite) services.persist(hrt);
            }
            
            htd.setTramite(ht);
            htd.setEstado(true);
            htd.setFecCre(new Date());
            htd.setPredio(datosPredio);
            
            if(nuevoReporte)
                servletSession.agregarParametro("numReporte", hrt.getId());
            else
                servletSession.agregarParametro("numReporte", hrts.get(0).getId());
            
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial());
            }
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            // Valores de PeInspeccionFinal
            peInspeccionFinalV.setAnioPermisoConstruc(peInspeccionFinalV.getAnioPermisoConstruc());
            peInspeccionFinalV.setTramite(ht);
            peInspeccionFinalV.setPredio(datosPredio);
            peInspeccionFinalV.setEstado("A");
            if(propietario != null)
                peInspeccionFinalV.setPropietario(propietario.getEnte());
            
            tipoInsp = servicesIF.getTipoPermiso("IN");
            peInspeccionFinalV.setInspeccion(tipoInsp.getValor());
            total = total.add(peInspeccionFinalV.getInspeccion());
            tipoInsp = servicesIF.getTipoPermiso("RV");
            peInspeccionFinalV.setRevicion(tipoInsp.getValor());
            total = total.add(peInspeccionFinalV.getRevicion());
            tipoInsp = servicesIF.getTipoPermiso("ND");
            peInspeccionFinalV.setNoAdeudar(tipoInsp.getValor());
            total = total.add(peInspeccionFinalV.getNoAdeudar());
            peInspeccionFinalV.setAreaEdificada(BigDecimal.ZERO);
            //total = total.add(peInspeccionFinalV.getEvaluoLiquidacion());
            total = total.setScale(2, RoundingMode.HALF_UP);
            
            if(peInspeccionFinalV.getAreaParqueos()==null)
                peInspeccionFinalV.setAreaParqueos(BigDecimal.ZERO);
            peInspeccionFinalV.setAreaConst(peInspeccionFinalV.getAreaConst().add(peInspeccionFinalV.getAreaParqueos()));
            tipoInsp = servicesIF.getTipoPermiso("INF");
            peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getAreaConst().multiply(tipoInsp.getValor()));
            peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            tipoInsp = servicesIF.getTipoPermiso("IM");
            if(peInspeccionFinalV.getEvaluoLiquidacion()!=null){
                //peInspeccionFinalV.setImpuesto(peInspeccionFinalV.getEvaluoLiquidacion().multiply(tipoInsp.getValor().divide(new BigDecimal(1000))));
                gutil.setProperty("avaluo", peInspeccionFinalV.getEvaluoLiquidacion());
                gutil.setProperty("valorTipoIns", tipoInsp.getValor());
                gutil.getExpression("getAvaluos", null);
                peInspeccionFinalV.setImpuesto(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
                total = total.add(peInspeccionFinalV.getImpuesto());
            }
            ht.setValorLiquidacion(total);
            htd.setTotal(total);
            services.update(ht);
            
            if(servicesIF.guardarTasaDeLiquidacionEdicion(peInspeccionFinalV, listaDetalle, obs, htd, lisPropietarios, hrts, htdList, peInspeccionCabEdificacionList)){
                if(peInspeccionFinalV.getPropietario() != null){
                    if(peInspeccionFinalV.getPropietario().getEsPersona()){
                        servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getPropietario().getNombres()+" "+peInspeccionFinalV.getPropietario().getApellidos());
                    }else{
                        servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getPropietario().getRazonSocial());                        
                    }
                    servletSession.agregarParametro("ciRuc", peInspeccionFinalV.getPropietario().getCiRuc());
                }else{
                    if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                        if(lisPropietarios.get(0).getEnte().getEsPersona()){
                            servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                        }else{
                            servletSession.agregarParametro("nomPropietario", lisPropietarios.get(0).getEnte().getRazonSocial());                        
                        }
                        servletSession.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
                    }
                }
                servletSession.agregarParametro("canton", "Samborondón");
                servletSession.agregarParametro("observacion", obs.getObservacion());
                servletSession.agregarParametro("sector", "La Puntilla");
                servletSession.agregarParametro("inspeccion", peInspeccionFinalV.getInspeccion());
                servletSession.agregarParametro("mz",datosPredio.getMz());
                servletSession.agregarParametro("solar",datosPredio.getSolar());
                servletSession.agregarParametro("nombreResponsable", responsableTec.getNombres() == null ? "" : responsableTec.getNombres() + " " + responsableTec.getApellidos() == null ? "" : responsableTec.getApellidos());
                servletSession.agregarParametro("regProf", responsableTec.getRegProf());
                servletSession.agregarParametro("ciResp", responsableTec.getCiRuc());
                servletSession.agregarParametro("calle", "Vehicular");
                servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
                servletSession.agregarParametro("permisoConst", permiso.getId());
                servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
                servletSession.agregarParametro("areaEdif", peInspeccionFinalV.getAreaConst());
                servletSession.agregarParametro("areaSolar", peInspeccionFinalV.getAreaSolar());
                servletSession.agregarParametro("codigoAnterior", datosPredio.getPredialant());
                servletSession.agregarParametro("imsadc",peInspeccionFinalV.getImpuesto());
                servletSession.agregarParametro("revYAprobPlanos",peInspeccionFinalV.getRevicion());
                servletSession.agregarParametro("noAdeudarMunicipio",peInspeccionFinalV.getNoAdeudar());
                servletSession.agregarParametro("verificacionAreaEdificada",peInspeccionFinalV.getAreaEdificada());
                servletSession.agregarParametro("totalAPagar", total.toString());
                servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
                servletSession.agregarParametro("nombreIng",firma.getNomCompleto());
                servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+" "+firma.getDepartamento().toUpperCase());
                //servletSession.agregarParametro("responsable", responsableTec.getNombres() + " " + responsableTec.getApellidos());
                servletSession.agregarParametro("avaluoConstruccion", peInspeccionFinalV.getEvaluoLiquidacion());
                servletSession.agregarParametro("dia", new SimpleDateFormat("dd").format(new Date()));
                servletSession.agregarParametro("mes", new SimpleDateFormat("MM").format(new Date()));
                servletSession.agregarParametro("anio", new SimpleDateFormat("yyyy").format(new Date()));
                servletSession.agregarParametro("numTramiteSeq", hrts.get(0).getSecuencia());
                servletSession.agregarParametro("numTramite", ht.getId()+"-"+new SimpleDateFormat("yyyy").format(new Date()));
                servletSession.agregarParametro("logoImg", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));          
                servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
                servletSession.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                servletSession.setNombreReporte("LiquidacionInspeccionFinal");
                servletSession.setTieneDatasource(true);   
                
                clickInspeccion = true;
                JsfUti.messageInfo(null, "Info", "Se generaron los cambios correctamente. Proceda a imprimir la tasa de liquidación.");
            }else{
                JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
            } 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Imprime la tasa de liquidación que fue guardada anteriormente y completa
     * la tarea.
     * 
     * @throws IOException
     * @throws SQLException 
     */
    public void imprimirPDF() throws IOException, SQLException{
        try{
            if(!clickInspeccion){
                JsfUti.messageError(null, "Error", "No se ha guardado la inspección final. Proceda a guardar la inpección antes de continuar");
                return;
            }

            String msg;

            msg = formatoMsg.getHeader() + "Se generó una tasa de liquidación." + formatoMsg.getFooter();

            servletSession.setReportePDF(reporte.generarPdf("/reportes/inspeccionFinal/LiquidacionInspeccionFinal.jasper", servletSession.getParametros()));
            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());

            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            //if(director!=null)
            //    paramsActiviti.put("to", getCorreosByCatEnte(director.getEnte()));
            //paramsActiviti.put("message", msg);
            //paramsActiviti.put("subject", "Trámite: "+ht.getId()+". Tasa Inspección Final.");
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());

            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Agrega una edificación a la lista de edificaciones de la inspección final.
     */
    public void agregarEdificacion(){
        try{
            CatEdfProp temp1, temp2, temp3, temp4, temp5, temp6;
            for(PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList){
                if(temp.getNumEdificacion().equals(peInspeccionCabEdificacionTemp.getNumEdificacion())){
                    JsfUti.messageInfo(null, "Info", "El número de edificación no está disponible");
                    return;
                }
            }
             if(peInspeccionCabEdificacionTemp.getNumEdificacion() == 0){
            
                peDetallePerIFList = new ArrayList();

                //ESTRUCTURA

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(1));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp1 = cepList.get(0);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp1);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                //PISO

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(2));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp2 = cepList.get(6);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp2);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                //SOBREPISO

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(3));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp3 = cepList.get(6);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp3);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                //PAREDES

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(4));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp4 = cepList.get(6);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp4);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                //CUBIERTA

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(5));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp5 = cepList.get(6);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp5);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                //TUMBADO

                categoria = (CatEdfCategProp) services.find(CatEdfCategProp.class, new Long(6));
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                temp6 = cepList.get(6);
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setCaracteristica(temp6);
                detalleInspeccion.setPorcentaje(BigDecimal.valueOf(100));
                peDetallePerIFList.add(detalleInspeccion);

                peInspeccionCabEdificacionTemp.setPeDetalleInspeccionCollection(peDetallePerIFList);

                pdp = new PeDetallePermiso();
                categoria = null;
                cepList = null;
            }
             

            peInspeccionCabEdificacionList.add(peInspeccionCabEdificacionTemp);
            this.sumarEdificaciones();
           
            peInspeccionCabEdificacionTemp = new PeInspeccionCabEdificacion();
            peInspeccionCabEdificacionTemp.setEstado(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Agrega una característica a la lista de característica de una edificación de una
     * inspección final.
     * 
     */
    public void agregarCaracteristica(){
        try{
            if(detalleInspeccion != null && categoria != null){
                detalleInspeccion.getCaracteristica().setCategoria(categoria);
                peDetallePerIFList.add(detalleInspeccion);
            }
            categoria = new CatEdfCategProp();
            cepList = null;
            detalleInspeccion = new PeDetalleInspeccion();
            detalleInspeccion.setPorcentaje(new BigDecimal(100));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Agrega un propietario a la lista de propietarios de un predio.
     * @param ente 
     */
    public void agregarPropietario(CatEnte ente) {
        CatPredioPropietario propTemp = new CatPredioPropietario();
        for(CatPredioPropietario cpp : lisPropietarios){
            if(cpp.getEnte().equals(ente) && cpp.getEstado().equals("A")){
                JsfUti.messageError(null, "Error", "El usuario ya ha sido agregado anteriormente. No lo puede volver a agregar.");
                return;
            }
        }
        
        if (!lisPropietarios.isEmpty()) {
            propTemp.setPredio(lisPropietarios.get(0).getPredio());
            propTemp.setTipo(lisPropietarios.get(0).getTipo());
            propTemp.setEsResidente(lisPropietarios.get(0).getEsResidente());
            propTemp.setEstado(lisPropietarios.get(0).getEstado());
            propTemp.setModificado(lisPropietarios.get(0).getModificado());
            if (propTemp != null) {
                propTemp.setEnte(ente);
                lisPropietarios.add(propTemp);
                enteList.remove(0);
                cedulaRuc = "";
            }
        }
    }
    
    /**
     * El técnico selecciona un permiso de la lista de permisos, y le extrae toda 
     * la información necesaria.
     * 
     * @param permiso 
     */
    public void onRowSelectPermiso(PePermiso permiso){
        String pagado = null;
        HistoricoTramites htTemp = null;        
        
        try{
            this.permiso = permiso;
            buscarPredioPorPermiso();

            peInspeccionFinalV.setAreaSolar(this.permiso.getAreaSolar());
            if(permiso.getTramite()!=null){
                htTemp = (HistoricoTramites) services.findNoProxy(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{permiso.getTramite().getId()});
                if(htTemp != null)
                    JsfUti.messageInfo(null, "Info", "Trámite asociado encontrado.");
            }
            else{
                JsfUti.messageError(null, "Error", "Trámite asociado no encontrado.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * De acuerdo al permiso seleccionado por el técnico, se le extrae la información
     * del predio.
     */
    public void buscarPredioPorPermiso(){
        datosPredio = this.permiso.getIdPredio();
        if(datosPredio != null){
            parroquia = datosPredio.getCiudadela().getCodParroquia();
            canton = parroquia.getIdCanton();
            this.buscarPredio();
            pePermisosIFList = (List<PePermisoCabEdificacion>) this.permiso.getPePermisoCabEdificacionCollection();
            this.sumarEdificacionesPermiso();
        }else{
            JsfUti.messageError(null, "Error", "No se ha encontrado ningún predio.");
            return;
        }
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public HashMap<String, Object> getParamsActiviti() {
        return paramsActiviti;
    }

    public void setParamsActiviti(HashMap<String, Object> paramsActiviti) {
        this.paramsActiviti = paramsActiviti;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public HistoricoTramites getHtEncontrado() {
        return htEncontrado;
    }

    public void setHtEncontrado(HistoricoTramites htEncontrado) {
        this.htEncontrado = htEncontrado;
    }

    public HistoricoReporteTramite getHrt() {
        return hrt;
    }

    public void setHrt(HistoricoReporteTramite hrt) {
        this.hrt = hrt;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public List<HistoricoTramiteDet> getHtdList() {
        return htdList;
    }

    public void setHtdList(List<HistoricoTramiteDet> htdList) {
        this.htdList = htdList;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public PeInspeccionFinal getPeInspeccionFinalV() {
        return peInspeccionFinalV;
    }

    public void setPeInspeccionFinalV(PeInspeccionFinal peInspeccionFinalV) {
        this.peInspeccionFinalV = peInspeccionFinalV;
    }

    public List<PeDetalleInspeccion> getDetalleInspeccionList() {
        return detalleInspeccionList;
    }

    public void setDetalleInspeccionList(List<PeDetalleInspeccion> detalleInspeccionList) {
        this.detalleInspeccionList = detalleInspeccionList;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public PeDetalleInspeccion getDetalleInspeccion() {
        return detalleInspeccion;
    }

    public void setDetalleInspeccion(PeDetalleInspeccion detalleInspeccion) {
        this.detalleInspeccion = detalleInspeccion;
    }

    public CatEdfProp getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(CatEdfProp caracteristica) {
        this.caracteristica = caracteristica;
    }

    public PeDetallePermiso getPdp() {
        return pdp;
    }

    public void setPdp(PeDetallePermiso pdp) {
        this.pdp = pdp;
    }

    public List<PeDetalleInspeccion> getPeDetallePerIFList() {
        return peDetallePerIFList;
    }

    public void setPeDetallePerIFList(List<PeDetalleInspeccion> peDetallePerIFList) {
        this.peDetallePerIFList = peDetallePerIFList;
    }

    public List<CatEdfProp> getCepList() {
        return cepList;
    }

    public void setCepList(List<CatEdfProp> cepList) {
        this.cepList = cepList;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        if(categoria!=null)
            cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
        else
            cepList = null;
        this.categoria = categoria;
    }

    public String getEdifPrincipal() {
        return edifPrincipal;
    }

    public void setEdifPrincipal(String edifPrincipal) {
        this.edifPrincipal = edifPrincipal;
    }

    public Boolean getNuevoReporte() {
        return nuevoReporte;
    }

    public void setNuevoReporte(Boolean nuevoReporte) {
        this.nuevoReporte = nuevoReporte;
    }

    public List<CtlgItem> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<CtlgItem> detallesList) {
        this.detallesList = detallesList;
    }

    public List<PeInspeccionCabEdificacion> getPeInspeccionCabEdificacionList() {
        return peInspeccionCabEdificacionList;
    }

    public void setPeInspeccionCabEdificacionList(List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList) {
        this.peInspeccionCabEdificacionList = peInspeccionCabEdificacionList;
    }

    public List<CatEdfCategProp> getCatEdfCategPropList() {
        return catEdfCategPropList;
    }

    public void setCatEdfCategPropList(List<CatEdfCategProp> catEdfCategPropList) {
        this.catEdfCategPropList = catEdfCategPropList;
    }

    public Boolean getInspeccionEncontrada() {
        return inspeccionEncontrada;
    }

    public void setInspeccionEncontrada(Boolean inspeccionEncontrada) {
        this.inspeccionEncontrada = inspeccionEncontrada;
    }

    public PeInspeccionCabEdificacion getPeInspeccionCabEdificacion() {
        return peInspeccionCabEdificacion;
    }

    public void setPeInspeccionCabEdificacion(PeInspeccionCabEdificacion peInspeccionCabEdificacion) {
        this.peInspeccionCabEdificacion = peInspeccionCabEdificacion;
    }

    public List<CtlgItem> getListaDetalle() {
        return listaDetalle;
    }

    public void setListaDetalle(List<CtlgItem> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    public Boolean getClickInspeccion() {
        return clickInspeccion;
    }

    public void setClickInspeccion(Boolean clickInspeccion) {
        this.clickInspeccion = clickInspeccion;
    }

    public PeInspeccionCabEdificacion getPeInspeccionCabEdificacionTemp() {
        return peInspeccionCabEdificacionTemp;
    }

    public void setPeInspeccionCabEdificacionTemp(PeInspeccionCabEdificacion peInspeccionCabEdificacionTemp) {
        this.peInspeccionCabEdificacionTemp = peInspeccionCabEdificacionTemp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public PePermisoLazy getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(PePermisoLazy permisosList) {
        this.permisosList = permisosList;
    }

    public PePermiso getPermiso() {
        return permiso;
    }

    public void setPermiso(PePermiso permiso) {
        this.permiso = permiso;
    }

    public CatEnte getResponsableTec() {
        return responsableTec;
    }

    public void setResponsableTec(CatEnte responsableTec) {
        this.responsableTec = responsableTec;
    }

    public String getCedulaRucResp() {
        return cedulaRucResp;
    }

    public void setCedulaRucResp(String cedulaRucResp) {
        this.cedulaRucResp = cedulaRucResp;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }   
}
