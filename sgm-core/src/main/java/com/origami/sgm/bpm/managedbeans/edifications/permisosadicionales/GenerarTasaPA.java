/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisosadicionales;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeDetallePermisosAdicionales;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.entities.PeUnidadMedida;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisosAdicionalesServices;
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
import org.apache.commons.lang.math.NumberUtils;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarTasaPA extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected SeqGenMan secuencia;
    
    @javax.inject.Inject
    protected PermisosAdicionalesServices servicesPA;

    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    
    private List<CatPredioPropietario> lisPropietarios;
    private PdfReporte reporte;
    private HistoricoReporteTramite hrt;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private List<CatEnte> enteList;
    private HistoricoTramites ht, htEncontrado;
    private CatPredio datosPredio;
    private String codigoCatastral;
    private String cedulaRuc;
    private PePermisosAdicionales permisoAdicional;
    private List<PePermisosAdicionales> permisosAdicionalesList;
    private PeDetallePermisosAdicionales detallesPermisosAdicionales;
    private CatPredio predioTemp;
    private List<CatEdfProp> estructuraList, instalacionesElecList, pisosList, cubiertaList, paredesList;
    private List<PeUnidadMedida> unidadMedidaList;
    private Boolean b1, b2, b3, b4, b5, permisoAdicionalHabilitado = false;
    private List<CatCiudadela> catCiudadelaList;
    private String ciResponsable, regProfResponsable, nombreResponsable, cedulaRucResp;
    private CatEnte ente, solicitante, responsableTec;
    private HistoricoTramiteDet htd;
    private List<HistoricoReporteTramite> hrts;
    private List<HistoricoTramiteDet> htdList;
    private Boolean nuevoReporte = true;
    private String tipoPermisoAdicional;
    private AclUser usuario;
    private List<PeFirma> firmas;
    private String numTramite;
    private PePermiso pePermiso;
    private PeInspeccionFinal inspeccion;
    private PeTipoPermiso tipoInsp;
    private CatEdfProp estructura, instalacion, pisoPB, pisoPA, cubierta, paredes;
    private List<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesList;
    private String descripcion;
    private BigDecimal lineaFabrica = new BigDecimal(0);
    private Boolean clickInspeccion = true;
    private CatCanton canton;
    private CatParroquia parroquia;
    private Boolean centinela = false;
    private GroovyUtil gutil;
    private BigInteger sec = null;
    private MatFormulaTramite formula;
    private MsgFormatoNotificacion formatoMsg;
    private AclUser digitalizador;
    
    @PostConstruct
    public void init() {
        try {
            lisPropietarios = new ArrayList<>();
            detallesPermisosAdicionales = new PeDetallePermisosAdicionales();
            detallesPermisosAdicionales.setEstado(true);
            permisoAdicional = new PePermisosAdicionales();
            
            reporte = new PdfReporte();
            predioTemp = new CatPredio();
            paramsActiviti = new HashMap<>();
            hrt = new HistoricoReporteTramite();
            obs = new Observaciones();
            String s ="";
            enteList = new ArrayList<>();
            b1 = false; b2 = false; b3 = false; b4 = false; b5 = false;
            peDetallePermisosAdicionalesList = new ArrayList();
            htd = new HistoricoTramiteDet();
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                permisosAdicionalesList = services.findAll(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                if(permisosAdicionalesList != null && !permisosAdicionalesList.isEmpty()){
                    permisoAdicional = permisosAdicionalesList.get(0);
                    if(permisoAdicional.getPermiso() != null){
                        pePermiso = permisoAdicional.getPermiso();
                        b3 = true;
                    }
                    estructura = permisoAdicional.getEstructura();
                    instalacion = permisoAdicional.getInstalaciones();
                    pisoPB = permisoAdicional.getPlantaBaja();
                    pisoPA = permisoAdicional.getPlantaAlta();
                    cubierta = permisoAdicional.getCubierta();
                    paredes = permisoAdicional.getParedes();
                    descripcion = permisoAdicional.getDescripcion();
                    lineaFabrica = permisoAdicional.getLineaFabrica();
                    peDetallePermisosAdicionalesList = (List<PeDetallePermisosAdicionales>) permisoAdicional.getPeDetallePermisosAdicionalesCollection();
                }else{
                    permisoAdicional.setPeDetallePermisosAdicionalesCollection(new ArrayList());
                }
                    
                if(ht!=null){
                    usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                    if(usuario!=null){
                        b1 = true;                        
                    }
                    solicitante = ht.getSolicitante();
                    permisoAdicional.setPropietario(solicitante);
                    if(ht.getNumPredio()!=null)
                        datosPredio = (CatPredio) services.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                    else{
                        JsfUti.messageError(null, "Error", "El trámite no tiene asignado un predio.");
                        return;
                    }
                    if(datosPredio!=null){
                        b2 = true;
                        parroquia = datosPredio.getCiudadela().getCodParroquia();
                        canton = parroquia.getIdCanton();
                        firmas = services.findAllEntCopy(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
                        estructuraList = servicesPA.getMaterialesPorNombreCategoria("Estructura");
                        instalacionesElecList = servicesPA.getMaterialesPorNombreCategoria("Instalaciones Eléctricas");
                        pisosList = servicesPA.getMaterialesPorNombreCategoria("Piso Inferior");
                        cubiertaList = servicesPA.getMaterialesPorNombreCategoria("Cubierta");
                        paredesList = servicesPA.getMaterialesPorNombreCategoria("Paredes");
                        catCiudadelaList = services.findAll(Querys.getCatCiudadelaList, new String[]{}, new Object[]{});
                        unidadMedidaList = servicesPA.getUnidadesMedida();
                        permisoAdicional.setCalle(datosPredio.getCalle());
                        permisoAdicional.setUrbmz(datosPredio.getUrbMz());
                        permisoAdicional.setSector(datosPredio.getSector()+"");
                        permisoAdicional.setUrbsolar(datosPredio.getUrbSolarnew());
                        permisoAdicional.setUrbanizacion(datosPredio.getCiudadela());
                        permisoAdicional.setPredio(datosPredio);
                        this.buscarPredio();
                    }
                    if(this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef")!=null){
                        s = this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef").toString();
                        hrts = services.findAll(Querys.getReporteByNombreTareaSinEstado, new String[]{"idProceso"}, new Object[]{this.ht.getIdProceso()});                
                        if (hrts != null && !hrts.isEmpty()) {
                            nuevoReporte = false;
                            hrt.setNombreTarea(hrts.get(0).getNombreTarea());
                            hrt.setSecuencia(hrts.get(0).getSecuencia());
                            htd.setNumTasa(hrts.get(0).getSecuencia());
                            responsableTec = permisoAdicional.getRespTecn();
                            cedulaRucResp = responsableTec.getCiRuc();
                        }
                    }else{
                        nuevoReporte = true;
                        hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                    }
                    
                    htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                    /*
                    htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                    if(htdList==null || htdList.isEmpty())
                        htd = new HistoricoTramiteDet();
                    else
                        htd = htdList.get(htdList.size()-1);
                    */
                    formula = (MatFormulaTramite) services.find(Querys.getMatFormulaByTipoTramiteID, new String[]{"idTipoTramite"}, new Object[]{ht.getTipoTramite().getId()});
                    gutil = new GroovyUtil(formula.getFormula());
                    formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                }
            }
            List<ParametrosDisparador> p = permisoServices.getParametroDisparadorByTipoTramite(ht.getTipoTramite().getId());
            for (ParametrosDisparador p1 : p) {
                if ("digitalizador".equals(p1.getVarResp())) {
                    digitalizador = (AclUser) EntityBeanCopy.clone(acl.find(AclUser.class, p1.getResponsable().getId()));
                }
            }
            if(permisoAdicional.getTotalEdificar()==null)
                permisoAdicional.setTotalEdificar(BigDecimal.ZERO);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Método que busca un ente solicitante y lo carga en memoria.
     */
    public void buscarEnte(){
        if (ciResponsable != null) {
            ente = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciResponsable});
            if(ente!=null){
                nombreResponsable = ente.getNombres() + " " + ente.getApellidos();
                regProfResponsable = ente.getRegProf();
            }else{
                JsfUti.messageInfo(null, "Info!", "No se ha encontrado ninguna persona.");
                return;
            }
        }else{
            JsfUti.messageError(null, "Error", "No se ha ingresado ningún número de cédula.");
            return;
        }
    }
    
    public void buscarResponsable(){
        try{
            if(cedulaRucResp!=null && cedulaRucResp.length()>0){
                responsableTec = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
                if(responsableTec==null)
                    JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
                else{
                    JsfUti.update("respDlgForm");
                    JsfUti.executeJS("PF('dlgResp').show()");
                    htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());
                    permisoAdicional.setRespTecn(responsableTec);
                }
            }else{
                JsfUti.messageInfo(null, "Información", "Ingrese Datos para la Busqueda");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Método que busca un ente predio y lo carga en memoria.
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;
        lisPropietarios = new ArrayList<CatPredioPropietario>();
        
        datosPredio = (CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
        //datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
        if(datosPredio != null){
            datosPredio.setEstado("A");
            codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

            propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

            for (CatPredioPropietario temp : propTemp) {
                if (temp.getEstado().equals("A")) {
                    lisPropietarios.add(temp);
                }
            }
            permisoAdicional.setPredio(datosPredio);
        }else{
            JsfUti.messageInfo(null, "Info", "Predio no encontrado");
        }
    }
    
    /**
     * Busca primero un PePermiso y a partir de él le extrae su HistóricoTrámite.
     * Finalmente verifica si el valor del permiso ya fue cancelado.
     */
    public void buscarTramite(){
        GeTipoTramite tipoTramite;
        permisoAdicionalHabilitado = false;
        
        try{
            if(NumberUtils.isNumber(numTramite)){
                HistoricoTramites htTemp = (HistoricoTramites)services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(numTramite)});
                if(htTemp!=null){
                    JsfUti.messageInfo(null, "Info", "Trámite encontrado");
                    String pagado = null;
                    
                    pePermiso = (PePermiso) services.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{Long.parseLong(htTemp.getIdTramite()+"")});
                    
                    if(pePermiso!= null){
                        permisoAdicional.setPermiso(pePermiso);
                        permisoAdicional.setAreaSolar(pePermiso.getAreaSolar());
                        permisoAdicional.setNumPisosBnb(pePermiso.getPisosBnb());
                        permisoAdicional.setNumPisosSnb(pePermiso.getPisosSnb());
                        permisoAdicional.setAlturaConst(pePermiso.getAltura());
                        b3 = true;     
                        JsfUti.messageInfo(null, "Info", "Permiso encontrado");

                        htEncontrado = pePermiso.getTramite();
                        tipoTramite = (GeTipoTramite) services.find(GeTipoTramite.class, htEncontrado.getTipoTramite().getId());
                        if (tipoTramite.getId().intValue() == 7 || tipoTramite.getId().intValue() == 8 || tipoTramite.getId().intValue() == 9
                        || tipoTramite.getId().intValue() == 4 || tipoTramite.getId().intValue() == 6 // Línea agregada por Joao Sanga, para adicionar la tasa de la inspección y la tasa de permisos adicionales 
                        || tipoTramite.getId().intValue() == 15 || tipoTramite.getId().intValue() == 17 || (htEncontrado.getId().compareTo(new Long("1450")) == 1)) {
                            if (htEncontrado.getNumLiquidacion() != null) {
                                pagado = permisoServices.consultaPagoLiquidacion(tipoTramite.getId().intValue(), htEncontrado);
                            }
                            if (pagado != null) {
                                if ("P".equalsIgnoreCase(pagado)) {
                                                                   
                                    JsfUti.messageInfo(null, "Info", "Tasa cancelada.");
                                } else {
                                    JsfUti.messageError(null, "Error", "Hasta el Momento el Usuario aún NO CANCELA su Tasa de Liquidación.");
                                    return;
                                }
                            }
                            else {
                                JsfUti.messageError(null, "Error", "El permiso encontrado no tiene número de liquidación.");
                                return;
                            }
                        } else {
                            JsfUti.messageError(null, "Error", "Tasa de liquidación no encontrada.");
                            return;
                        }
                    }else{
                        JsfUti.messageError(null, "Error", "No se ha encontrado el permiso de construcción previo.");
                    }
                }
                
                    /*
                    if(htEncontrado!=null && htEncontrado.getNumPredio()!=null){
                        inspeccion =  htEncontrado.getPeInspeccionFinal();
                        if(inspeccion!=null){
                            JsfUti.messageInfo(null, "Info", "Inspección encontrada");
                            permisoAdicional.setInspeccion(inspeccion.getInspeccion());
                        }
                    }else{
                        JsfUti.messageError(null, "Error", "No se ha encontrado ningún trámite.");    
                    }*/
                
            }else{
                JsfUti.messageError(null, "Error", "No se ha ingresado ningún número de trámite.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarDatosResponsable(){
        /*if(ciResponsable != null && nombreResponsable != null && regProfResponsable != null){
            b1 = true;
        }else{
            b1 = false;
        }*/
        b1 = true;
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarDatosPredio(){
        if(services.update(datosPredio)){
            b2 = true;
        }else{
            b2 = false;
        }
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarPermisos(){
        b3 = true;      
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarMaterialesConst(){
        try{
            if(lineaFabrica != null && descripcion != null){
                permisoAdicional.setLineaFabrica(lineaFabrica);
                permisoAdicional.setDescripcion(descripcion);
                permisoAdicional.setEstructura(estructura);
                permisoAdicional.setInstalaciones(instalacion);
                permisoAdicional.setPlantaBaja(pisoPB);
                permisoAdicional.setPlantaAlta(pisoPA);
                permisoAdicional.setCubierta(cubierta);
                permisoAdicional.setParedes(paredes);

                b4 = true;
            }
        }catch(Exception e){
            e.printStackTrace();
            b4 = false;
        }
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarPermisosConstruccion(){
        try{
            permisoAdicional.setAreaSolar(pePermiso.getAreaSolar());
            permisoAdicional.setNumPisosSnb(pePermiso.getPisosSnb());
            permisoAdicional.setNumPisosBnb(pePermiso.getPisosBnb());
            permisoAdicional.setAlturaConst(pePermiso.getAltura());
            b3 = true;
        }catch(Exception e){
            e.printStackTrace();
            b3 = false;
        }
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void guardarDetallesConstruccion(){
        if(this.peDetallePermisosAdicionalesList != null && !this.peDetallePermisosAdicionalesList.isEmpty()){
            b5 = true;
        }else{
            b5 = false;
        }
    }
    
    /**
     * Verifica que los datos no estén nulos
     */
    public void agregarDetalle(){
        try{
            if(detallesPermisosAdicionales.getDescripcion() !=null && detallesPermisosAdicionales.getArea() != null && detallesPermisosAdicionales.getUnidadMedida() != null){
                if(permisoAdicional!=null)
                    detallesPermisosAdicionales.setIdPermisosAdicionales(permisoAdicional);

                permisoAdicional.setTotalEdificar(permisoAdicional.getTotalEdificar().add(detallesPermisosAdicionales.getArea()));

                peDetallePermisosAdicionalesList.add(detallesPermisosAdicionales);
                detallesPermisosAdicionales = new PeDetallePermisosAdicionales();
                detallesPermisosAdicionales.setEstado(true);
            }
            else{
                JsfUti.messageError(null, "Error", "Uno o más campos no han sido ingresados. Asegúrese de llenar todos los campos antes de agregar.");
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina un PeDetallePermisosAdicionales de la lista de detalles. Si existe en
     * la base de datos solo le cambia el estado.
     * 
     * @param pdpa 
     */
    public void eliminarDetallePermisoAdicional(Integer index){
        PeDetallePermisosAdicionales pdpa = this.peDetallePermisosAdicionalesList.get(index);
        this.peDetallePermisosAdicionalesList.remove(pdpa);
        if (pdpa.getId() != null) {
            pdpa.setEstado(false);
            services.update(pdpa);
        }
        permisoAdicional.setTotalEdificar(permisoAdicional.getTotalEdificar().subtract(pdpa.getArea()));
    }
    
    /**
     * Inicializa las variables para volverlas a usar.
     */
    public void setearValores() {
        cedulaRuc = "";
        enteList = new ArrayList<>();
    }
    
    /**
     * Elimina a un propietario de un predio si no existe en la base de datos,
     * caso contrario solo le cambia de estado
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
     * Verifica que todos los datos ingresados estén correctamente almacenados en
     * mrmoria, si todo está bien procede a guardar los datos de la tasa de 
     * liquidación.
     */
    public void guardarDatos(){
        BigDecimal total = new BigDecimal(0);
        BigDecimal areaTotal = BigDecimal.ZERO, avaluo;
        
        try{
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar al menos una observación antes de continuar.");
                return;
            }
            if(responsableTec == null){
                JsfUti.messageError(null, "Error", "Debe ingresar un responsable técnico antes de continuar.");
                return;
            }
            if(permisoAdicional.getFechaCaducidad()==null || permisoAdicional.getFechaEmision()==null){
                JsfUti.messageError(null, "Error", "Debe ingresar las fechas de caducidad y emisión antes de continuar.");
                return;
            }
            if(b1 && b2 && b3 && b4 && b5){
                //digitalizador = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariableByPorcessIntance(this.getProcessInstanceByTareaID(uSession.getTaskID()), "digitalizador")});
                servletSession.instanciarParametros();
                hrt.setFecCre(new Date());
                //hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                hrt.setProceso(ht.getIdProceso());
                hrt.setTramite(ht);
                hrt.setNombreReporte("TasaLiq_PA-" + solicitante.getCiRuc());
                hrt.setEstado(Boolean.TRUE);
                Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
                
                
                
                if(permisoAdicional.getNumReporte() == null){                    
                    sec = new BigInteger(secuencia.getMaxSecuenciaTipoTramite(Integer.valueOf(anio+""), ht.getTipoTramite().getId()).toString());
                    permisoAdicional.setNumReporte(sec);
                }
                hrt.setSecuencia(permisoAdicional.getNumReporte());
                hrt = (HistoricoReporteTramite) services.persist(hrt);                
                
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                
                permisoAdicional.setNumTramite(ht.getId());
                permisoAdicional.setFechaIngresoTramite(new Date());
                permisoAdicional.setAnioTramite(BigInteger.valueOf(Long.parseLong(new SimpleDateFormat("yyyy").format(new Date()))));
                permisoAdicional.setMostrarCertificado(true);
                permisoAdicional.setCalle("Vehicular");
                permisoAdicional.setUsuario(usuario);
                permisoAdicional.setDescripcion(obs.getObservacion() == null ? "" : obs.getObservacion().toUpperCase());
                
                htd.setTramite(ht);
                htd.setEstado(true);
                htd.setFecCre(new Date());
                htd.setPredio(datosPredio);
                tipoInsp = servicesIF.getTipoPermiso("IM");            
                                
                gutil.setProperty("areaTotal", permisoAdicional.getTotalEdificar());
                avaluo = (BigDecimal) gutil.getExpression("getAvaluo", null);
                permisoAdicional.setAvaluoConstrTasas(avaluo);    
                
                permisoAdicional.setImpMunicipalAreaedif(((BigDecimal)gutil.getExpression("getImpuestoMunicipal", null)).setScale(2, RoundingMode.HALF_UP));
                servletSession.agregarParametro("imsade", permisoAdicional.getImpMunicipalAreaedif());
                
                tipoInsp = servicesIF.getTipoPermiso("IN");
                //permisoAdicional.setInspeccion((BigDecimal)gutil.getExpression("getValorInspeccion", null));
                permisoAdicional.setInspeccion(tipoInsp.getValor());  
                servletSession.agregarParametro("inspeccion", tipoInsp.getValor()); 
                
                tipoInsp = servicesIF.getTipoPermiso("RV");     
                servletSession.agregarParametro("revyaprovplanos", tipoInsp.getValor());
                permisoAdicional.setRevisionAprobPlanos(tipoInsp.getValor());
                
                tipoInsp = servicesIF.getTipoPermiso("ND");
                servletSession.agregarParametro("deudaMunicipio", tipoInsp.getValor());
                permisoAdicional.setNoAdeudarMun(tipoInsp.getValor());
                
                gutil.setProperty("valorlineafabrica", lineaFabrica);
                permisoAdicional.setLineaFabricaCalculada(((BigDecimal)gutil.getExpression("getValorLineaFabrica", null)).setScale(2, RoundingMode.HALF_UP));
                servletSession.agregarParametro("lineaFabrica", permisoAdicional.getLineaFabrica());
                
                total = permisoAdicional.getImpMunicipalAreaedif().add(permisoAdicional.getInspeccion()).add(permisoAdicional.getRevisionAprobPlanos()).add(permisoAdicional.getNoAdeudarMun()).add(permisoAdicional.getLineaFabricaCalculada());
                permisoAdicional.setTotalPagar(total);
                //permisoAdicional.setTotalPagar(total);
                ht.setValorLiquidacion(total);
                htd.setTotal(total);
                if(permisoAdicional.getId()==null)
                    services.persist(permisoAdicional);
                
                services.update(permisoAdicional);
                if(servicesPA.guardarPermisoAdicional(permisoAdicional, hrt, htd, htdList, hrts, peDetallePermisosAdicionalesList, obs, datosPredio, lisPropietarios, ht)){
                
                    if(nuevoReporte)
                        servletSession.agregarParametro("numReporte", permisoAdicional.getNumReporte()+"-"+new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
                    else
                        servletSession.agregarParametro("numReporte", permisoAdicional.getNumReporte()+"-"+new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
                    if (solicitante.getEsPersona()) {
                        servletSession.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
                    } else {
                        servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial());
                    }


                    servletSession.agregarParametro("nombreCiudad", "Samborondón");
                    servletSession.agregarParametro("numTramite", ht.getId()+"-"+new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
                    servletSession.agregarParametro("fechaIngreso", new SimpleDateFormat("dd/MM/yyyy").format(permisoAdicional.getFechaEmision()));
                    servletSession.agregarParametro("tipoPermiso", permisoAdicional.getTipoPermisoAdicional().getDescripcion());
                    servletSession.agregarParametro("dia",new SimpleDateFormat("dd").format(new Date()));
                    servletSession.agregarParametro("mes",new SimpleDateFormat("MM").format(new Date()));
                    servletSession.agregarParametro("anio", new SimpleDateFormat("yyyy").format(new Date()));
                    servletSession.agregarParametro("ciPropietario", solicitante.getCiRuc());
                    servletSession.agregarParametro("sector", "La Puntilla");
                    servletSession.agregarParametro("ciudadela", datosPredio.getCiudadela().getNombre());
                    servletSession.agregarParametro("calle", "Vehicular");
                    servletSession.agregarParametro("mz", datosPredio.getUrbMz());
                    servletSession.agregarParametro("solar", datosPredio.getUrbSolarnew());
                    servletSession.agregarParametro("codCatastral", datosPredio.getCodigoPredial());
                    servletSession.agregarParametro("nombresRespTecnico", responsableTec.getNombres() + " " + responsableTec.getApellidos());
                    servletSession.agregarParametro("regProfResponsable", responsableTec.getRegProf());
                    servletSession.agregarParametro("ciResponsable", responsableTec.getCiRuc());
                    servletSession.agregarParametro("numPisosSB", permisoAdicional.getNumPisosSnb());
                    servletSession.agregarParametro("numPisosBB",permisoAdicional.getNumPisosBnb());
                    servletSession.agregarParametro("alturaConst", permisoAdicional.getAlturaConst());
                    servletSession.agregarParametro("construccion", permisoAdicional.getTotalEdificar());
                    servletSession.agregarParametro("areaSolar", permisoAdicional.getAreaSolar());
                    servletSession.agregarParametro("descripcion", permisoAdicional.getDescripcion());
                    
                    servletSession.agregarParametro("lineaFabrica", permisoAdicional.getLineaFabricaCalculada());
                    servletSession.agregarParametro("totalAPagar", permisoAdicional.getTotalPagar());
                    if(pePermiso!=null)
                        servletSession.agregarParametro("permisoConst", pePermiso.getId());
                    else
                        servletSession.agregarParametro("permisoConst", "");
                    servletSession.agregarParametro("nombreIng", htd.getFirma().getNomCompleto());
                    servletSession.agregarParametro("cargoIng",htd.getFirma().getCargo() + " " + htd.getFirma().getDepartamento());
                    servletSession.agregarParametro("responsable", nombreResponsable);
                    servletSession.agregarParametro("permiso_adicional", permisoAdicional.getId());
                    servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
                    servletSession.agregarParametro("logo_img", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("firma_img", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
                    servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
                    servletSession.setNombreReporte("permisosAdicionalesTasaLiquidacion-"+solicitante.getCiRuc());
                    servletSession.setTieneDatasource(true);

                    clickInspeccion = false;
                    centinela = true;
                    JsfUti.messageInfo(null, "Info", "Se guardaron los datos correctamente. Puede proceder a imprimir la tasa.");
                }else{
                    JsfUti.messageError(null, "Error", "No se guardó correctamente, revise que toda la información sea la correcta.");
                }
            }else{
                JsfUti.messageError(null, "Error", "Debe ingresar todos los datos de la sección 'Ingreso de Datos'.");
                return;
            }    
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Método que genera el pdf de la tasa de liquidación previamente almacenada.
     * 
     * @throws IOException
     * @throws SQLException 
     */
    public void imprimirPDF() throws IOException, SQLException{
        
        if (this.getTaskDataByTaskID() == null) {
            JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
            return;
        }
        if(obs.getObservacion()==null || obs.getObservacion()==""){
            JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
            return;
        }
        if(!centinela){
            JsfUti.messageError(null, "Error", "No ha guardado los datos de la tasa de liquidación.");
            return;
        }
        
        permisoAdicional = (PePermisosAdicionales) services.find(PePermisosAdicionales.class, permisoAdicional.getId());
        
        servletSession.setReportePDF(reporte.generarPdf("//reportes//permisosAdicionales//TasaLiq_PA.jasper", servletSession.getParametros()));
                
        reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
        
        paramsActiviti.put("prioridad", 50);
        paramsActiviti.put("idReporte", hrt.getId());
        paramsActiviti.put("archivo", servletSession.getReportePDF());
        paramsActiviti.put("carpeta", ht.getCarpetaRep());
        paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
        paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
        paramsActiviti.put("to", getCorreosByCatEnte(solicitante));
        paramsActiviti.put("message", "Se generó una tasa de liquidación de: "+ht.getTipoTramiteNombre()+". El número de liquidacion es: "+ht.getId());
        paramsActiviti.put("subject", "Trámite: "+ht.getId());
        paramsActiviti.put("digitalizador", digitalizador.getUsuario());
        if(nuevoReporte)
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
        
        JsfUti.messageInfo(null, "Info", "Se generó la tasa correctamente.");
        JsfUti.update("frmMain:tdatos:botonesGroup");
        this.completeTask(this.getTaskId(), paramsActiviti);
        servletSession.borrarDatos();
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

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public HistoricoReporteTramite getHrt() {
        return hrt;
    }

    public void setHrt(HistoricoReporteTramite hrt) {
        this.hrt = hrt;
    }

    public HashMap<String, Object> getParamsActiviti() {
        return paramsActiviti;
    }

    public void setParamsActiviti(HashMap<String, Object> paramsActiviti) {
        this.paramsActiviti = paramsActiviti;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
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

    public PePermisosAdicionales getPermisoAdicional() {
        return permisoAdicional;
    }

    public void setPermisoAdicional(PePermisosAdicionales permisoAdicional) {
        this.permisoAdicional = permisoAdicional;
    }

    public CatPredio getPredioTemp() {
        return predioTemp;
    }

    public void setPredioTemp(CatPredio predioTemp) {
        this.predioTemp = predioTemp;
    }

    public List<CatEdfProp> getEstructuraList() {
        return estructuraList;
    }

    public void setEstructuraList(List<CatEdfProp> estructuraList) {
        this.estructuraList = estructuraList;
    }

    public List<CatEdfProp> getInstalacionesElecList() {
        return instalacionesElecList;
    }

    public void setInstalacionesElecList(List<CatEdfProp> instalacionesElecList) {
        this.instalacionesElecList = instalacionesElecList;
    }

    public List<CatEdfProp> getPisosList() {
        return pisosList;
    }

    public void setPisosList(List<CatEdfProp> pisosList) {
        this.pisosList = pisosList;
    }

    public List<CatEdfProp> getCubiertaList() {
        return cubiertaList;
    }

    public void setCubiertaList(List<CatEdfProp> cubiertaList) {
        this.cubiertaList = cubiertaList;
    }

    public List<CatEdfProp> getParedesList() {
        return paredesList;
    }

    public void setParedesList(List<CatEdfProp> paredesList) {
        this.paredesList = paredesList;
    }

    public List<PeUnidadMedida> getUnidadMedidaList() {
        return unidadMedidaList;
    }

    public void setUnidadMedidaList(List<PeUnidadMedida> unidadMedidaList) {
        this.unidadMedidaList = unidadMedidaList;
    }

    public PeDetallePermisosAdicionales getDetallesPermisosAdicionales() {
        return detallesPermisosAdicionales;
    }

    public void setDetallesPermisosAdicionales(PeDetallePermisosAdicionales detallesPermisosAdicionales) {
        this.detallesPermisosAdicionales = detallesPermisosAdicionales;
    }

    public Boolean getB1() {
        return b1;
    }

    public void setB1(Boolean b1) {
        this.b1 = b1;
    }

    public Boolean getB2() {
        return b2;
    }

    public void setB2(Boolean b2) {
        this.b2 = b2;
    }

    public Boolean getB3() {
        return b3;
    }

    public void setB3(Boolean b3) {
        this.b3 = b3;
    }

    public Boolean getB4() {
        return b4;
    }

    public void setB4(Boolean b4) {
        this.b4 = b4;
    }

    public Boolean getB5() {
        return b5;
    }

    public void setB5(Boolean b5) {
        this.b5 = b5;
    }

    public List<CatCiudadela> getCatCiudadelaList() {
        return catCiudadelaList;
    }

    public void setCatCiudadelaList(List<CatCiudadela> catCiudadelaList) {
        this.catCiudadelaList = catCiudadelaList;
    }

    public String getCiResponsable() {
        return ciResponsable;
    }

    public void setCiResponsable(String ciResponsable) {
        this.ciResponsable = ciResponsable;
    }

    public String getRegProfResponsable() {
        return regProfResponsable;
    }

    public void setRegProfResponsable(String regProfResponsable) {
        this.regProfResponsable = regProfResponsable;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public List<HistoricoTramiteDet> getHtdList() {
        return htdList;
    }

    public void setHtdList(List<HistoricoTramiteDet> htdList) {
        this.htdList = htdList;
    }

    public Boolean getNuevoReporte() {
        return nuevoReporte;
    }

    public void setNuevoReporte(Boolean nuevoReporte) {
        this.nuevoReporte = nuevoReporte;
    }

    public String getTipoPermisoAdicional() {
        return tipoPermisoAdicional;
    }

    public void setTipoPermisoAdicional(String tipoPermisoAdicional) {
        this.tipoPermisoAdicional = tipoPermisoAdicional;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public List<PeFirma> getFirmas() {
        return firmas;
    }

    public void setFirmas(List<PeFirma> firmas) {
        this.firmas = firmas;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public Boolean getPermisoAdicionalHabilitado() {
        return permisoAdicionalHabilitado;
    }

    public void setPermisoAdicionalHabilitado(Boolean permisoAdicionalHabilitado) {
        this.permisoAdicionalHabilitado = permisoAdicionalHabilitado;
    }

    public HistoricoTramites getHtEncontrado() {
        return htEncontrado;
    }

    public void setHtEncontrado(HistoricoTramites htEncontrado) {
        this.htEncontrado = htEncontrado;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public PeInspeccionFinal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(PeInspeccionFinal inspeccion) {
        this.inspeccion = inspeccion;
    }

    public List<PeDetallePermisosAdicionales> getPeDetallePermisosAdicionalesList() {
        return peDetallePermisosAdicionalesList;
    }

    public void setPeDetallePermisosAdicionalesList(List<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesList) {
        this.peDetallePermisosAdicionalesList = peDetallePermisosAdicionalesList;
    }

    public CatEdfProp getEstructura() {
        return estructura;
    }

    public void setEstructura(CatEdfProp estructura) {
        this.estructura = estructura;
    }

    public CatEdfProp getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(CatEdfProp instalacion) {
        this.instalacion = instalacion;
    }

    public CatEdfProp getPisoPB() {
        return pisoPB;
    }

    public void setPisoPB(CatEdfProp pisoPB) {
        this.pisoPB = pisoPB;
    }

    public CatEdfProp getPisoPA() {
        return pisoPA;
    }

    public void setPisoPA(CatEdfProp pisoPA) {
        this.pisoPA = pisoPA;
    }

    public CatEdfProp getCubierta() {
        return cubierta;
    }

    public void setCubierta(CatEdfProp cubierta) {
        this.cubierta = cubierta;
    }

    public CatEdfProp getParedes() {
        return paredes;
    }

    public void setParedes(CatEdfProp paredes) {
        this.paredes = paredes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getLineaFabrica() {
        return lineaFabrica;
    }

    public void setLineaFabrica(BigDecimal lineaFabrica) {
        this.lineaFabrica = lineaFabrica;
    }

    public Boolean getClickInspeccion() {
        return clickInspeccion;
    }

    public void setClickInspeccion(Boolean clickInspeccion) {
        this.clickInspeccion = clickInspeccion;
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

    public String getCedulaRucResp() {
        return cedulaRucResp;
    }

    public void setCedulaRucResp(String cedulaRucResp) {
        this.cedulaRucResp = cedulaRucResp;
    }

    public CatEnte getResponsableTec() {
        return responsableTec;
    }

    public void setResponsableTec(CatEnte responsableTec) {
        this.responsableTec = responsableTec;
    }
    
}
