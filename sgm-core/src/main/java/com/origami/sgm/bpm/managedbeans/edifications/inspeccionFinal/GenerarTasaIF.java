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
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeFirma;
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
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
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
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarTasaIF extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices servicesPC;

    @javax.inject.Inject
    protected SeqGenMan secuencia;
    
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    
    private HashMap<String, Object> paramsActiviti;
    private List<HistoricoReporteTramite> hrts;
    private CatPredio datosPredio;
    private Observaciones obs;
    private HistoricoTramites ht, htEncontrado;
    private HistoricoReporteTramite hrt;
    private PdfReporte reporte;
    private HistoricoTramiteDet htd;
    private List<HistoricoTramiteDet> htdList;
    private CatEnte solicitante;
    private PePermiso pePermiso;
    private PePermisoLazy permisosList;
    private PeInspeccionFinal peInspeccionFinalV;
    private String codigoCatastral, numTramite;
    private List<CatPredioPropietario> lisPropietarios;
    private List<CatEnte> enteList;
    private String cedulaRuc, cedulaRucResp;
    private CatEnte responsableTec;
    private List<PePermisoCabEdificacion> pePermisosIFList;
    private PePermisoCabEdificacion pePermisosCabEdifIF, pePermisosCabEdifIF2;
    private List<PeDetallePermiso> peDetallePerIFList;
    private PeDetallePermiso pdp;
    private List<CtlgItem> detallesList, detallesSeleccionadosList, listaDetalle;
    private String edifPrincipal;
    private CatEdfCategProp categoria;
    private List<CatEdfCategProp> catEdfCategPropList;
    private CatEdfProp caracteristica;
    private List<CatEdfProp> cepList;
    private Boolean nuevoReporte = false;
    private AclUser usuario, director;
    private PeTipoPermiso tipoInsp;
    private MsgFormatoNotificacion formatoMsg;
    private PeFirma firma;
    private Boolean clickInspeccion = true;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private CatCanton canton;
    private CatParroquia parroquia;
    private Boolean centinela = false;
    private Date fechaEmision = new Date(), fechaInspeccion;
    private GeTipoTramite tipoTramite;
    
    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                String s = "";
                fechaInspeccion = (Date) this.getVariable(uSession.getTaskID(), "fechaInspeccion");
                peInspeccionFinalV = new PeInspeccionFinal();
                peInspeccionFinalV.setFechaInspeccion(fechaInspeccion);
                
                paramsActiviti = new HashMap<String, Object>();
                pdp = new PeDetallePermiso();
                pdp.setPorcentaje(new BigDecimal(100));
                hrt = new HistoricoReporteTramite();
                htd = new HistoricoTramiteDet();
                pePermisosCabEdifIF = new PePermisoCabEdificacion();
                permisosList = new PePermisoLazy();
                obs = new Observaciones();
                
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                catEdfCategPropList = (List<CatEdfCategProp>) services.findAll(Querys.getCatCategoriasPropConstruccionList, new String[]{}, new Object[]{});
                solicitante = ht.getSolicitante();
                detallesList = servicesIF.obtenerCtlgItemListByNombreDeCatalogo("permiso_inspeccion.detalle");
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                
                htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                if(this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef")!=null)
                        s = this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef").toString();
                hrts = services.findAll(Querys.getReporteByNombreTareaSinEstado, new String[]{"nombreTarea", "idProceso"}, new Object[]{s, this.ht.getIdProceso()});
                hrt = new HistoricoReporteTramite();
                
                htd = new HistoricoTramiteDet();                
                
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                director = firma.getAclUser();
                
                tipoInsp = (PeTipoPermiso) services.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{"INF"});
                formula = (MatFormulaTramite) services.find(MatFormulaTramite.class, 6L);
                gutil = new GroovyUtil(formula.getFormula());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Busca primero un PePermiso y a partir de él le extrae su HistóricoTrámite.
     */
    public void buscarTramite(){
        try{
            if(numTramite!=null){
                //htEncontrado = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(numTramite)});
                pePermiso = (PePermiso) services.find(Querys.getPePermisoByNumTra, new String[]{"numTramite"}, new Object[]{Long.parseLong(numTramite)});
                if(pePermiso!= null){
                    //htEncontrado = pePermiso.getTramite();
                    htEncontrado = (HistoricoTramites) services.find(Querys.getHistoricoTramiteByIdNew, new String[]{"id"}, new Object[]{Long.parseLong(pePermiso.getTramite().getIdTramite().toString())});
                    if(htEncontrado!=null && htEncontrado.getNumPredio()!=null){
                        datosPredio = (CatPredio) services.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{htEncontrado.getNumPredio()});

                        if(datosPredio != null){
                            parroquia = datosPredio.getCiudadela().getCodParroquia();
                            canton = parroquia.getIdCanton();
                            this.buscarPredio();
                            pePermisosIFList = (List<PePermisoCabEdificacion>) pePermiso.getPePermisoCabEdificacionCollection();

                        }else{
                            JsfUti.messageError(null, "Error", "No se ha encontrado ningún predio.");
                            return;
                        }
                    }else{
                        JsfUti.messageError(null, "Error", "No se ha encontrado ningún trámite.");                        
                        return;
                    }
                }else{
                    JsfUti.messageError(null, "Error", "No se ha encontrado el permiso de construcción previo.");
                    
                    return;
                }
            }else{
                JsfUti.messageError(null, "Error", "No se ha ingresado ningún número de trámite.");
                return;
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
        datosPredio = pePermiso.getIdPredio();
        if(datosPredio != null){
            parroquia = datosPredio.getCiudadela().getCodParroquia();
            canton = parroquia.getIdCanton();
            this.buscarPredio();
            pePermisosIFList = (List<PePermisoCabEdificacion>) pePermiso.getPePermisoCabEdificacionCollection();
            this.sumarEdificaciones();
        }else{
            JsfUti.messageError(null, "Error", "No se ha encontrado ningún predio.");
            return;
        }
    }
    
    /**
     * Toma una edificación y le extrae la lista de detalles que posee. En caso de
     * no tener lista de detalle se le setea una.
     * 
     * @param temp 
     */
    public void agregarDatos(PePermisoCabEdificacion temp){
        peDetallePerIFList = (List<PeDetallePermiso>) temp.getPeDetallePermisoCollection();
        if(peDetallePerIFList==null || peDetallePerIFList.isEmpty()){
            peDetallePerIFList = new ArrayList<>();
            pePermisosCabEdifIF2.setPeDetallePermisoCollection(peDetallePerIFList);
        }
        JsfUti.executeJS("PF('dlgMostrarDetalle').show()");
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
     * Toma una edificación y le extrae la lista de detalles que posee. En caso de
     * no tener lista de detalle se le setea una.
     * 
     */
    public void onRowSelect(){
        peDetallePerIFList = (List<PeDetallePermiso>) pePermisosCabEdifIF2.getPeDetallePermisoCollection();
        if(peDetallePerIFList==null || peDetallePerIFList.isEmpty()){
            peDetallePerIFList = new ArrayList<>();
            pePermisosCabEdifIF2.setPeDetallePermisoCollection(peDetallePerIFList);
        }
        JsfUti.executeJS("PF('dlgMostrarDetalle').show()");
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
            if(permiso.getTramite()!=null){
                htTemp = (HistoricoTramites) services.findNoProxy(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{permiso.getTramite().getId()});
                if(htTemp != null){
                    pePermiso = permiso;
                    buscarPredioPorPermiso();
                    peInspeccionFinalV.setAreaSolar(pePermiso.getAreaSolar());
                    JsfUti.update("frmMain");
                    /*
                    JsfUti.messageInfo(null, "Info", "Trámite asociado encontrado.");
                    if(htTemp.getEstado().equals("finalizado") || htTemp.getEstado().equals("Finalizado") ){
                        pePermiso = permiso;
                        buscarPredioPorPermiso();
                        peInspeccionFinalV.setAreaSolar(pePermiso.getAreaSolar());
                        JsfUti.update("frmMain");
                        JsfUti.messageInfo(null, "Info", "Tasa cancelada.");
                        JsfUti.messageInfo(null, "Info", "Se cargó la información del permiso.");
                    }else{
                        JsfUti.messageError(null, "Error", "Hasta el Momento el Usuario aún NO CANCELA su Tasa de Liquidación.");
                    }
                    */
                }else{
                    JsfUti.messageError(null, "Error", "El trámite asociado al permiso tiene errores.");
                    return;
                }
            }else{
                pePermiso = permiso;
                buscarPredioPorPermiso();
                peInspeccionFinalV.setAreaSolar(pePermiso.getAreaSolar());
                JsfUti.update("frmMain");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Descripción: Se toma la variable PePermiso y se carga sus datos en el facelet, el técnico puede editar y agregar nuevos valores
     *              pero estos nuevos valores no serán actualizados en sus tablas en la base de datos, sino que se tomarán todos
     *              todos los valores y se crearán instancias que replicarán estos nuevos datos en las tablas de la inspección y así 
     *              contrastarlos en un futuro el por qué hubo cambios en la parte del permiso con la parte de la inspección.
     *              A continuación se detalla la relación que poseen estas tablas.
     * 
     *              PePermiso (PeDetallePermiso, PePermisoCabEdificacion)
     *              PeInspeccionFinal (PeDetalleInspeccion, PeInspeccionCabEdificacion)
     * 
     * @throws IOException 
     */
    public void guardarDatos() throws IOException {
        
        try{
            reporte = new PdfReporte();
            BigInteger sec;
            String msg="";
            BigDecimal total = new BigDecimal(0);

            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(responsableTec==null){
                JsfUti.messageError(null, "Error", "Debe seleccionar un responsable técnico antes de continuar.");
                return;
            }
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }    
            if(pePermisosIFList == null ||pePermisosIFList.isEmpty()){
                JsfUti.messageError(null, "Error", "La inspección final debe tener una edificación al menos.");
                return;
            }
        
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.instanciarParametros();
            
            ht.setNumPredio(datosPredio.getNumPredio());
            ht.setMz(datosPredio.getUrbMz());
            ht.setSolar(datosPredio.getSolar() + "");
            
            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_IF-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());

            sec = new BigInteger(secuencia.getMaxSecuenciaTipoTramite(Integer.valueOf(anio+""), ht.getTipoTramite().getId()).toString());
            peInspeccionFinalV.setNumReporte(sec);
            hrt.setCodValidacion(sec + "" + hrt.getProceso());
            hrt.setSecuencia(sec);
            ht.setNumTramiteXDepartamento(sec);
            if (hrt.getId() == null) {
                hrt = (HistoricoReporteTramite) services.persist(hrt);
            }
            
            htd.setTramite(ht);
            htd.setEstado(true);
            htd.setFecCre(new Date());
            htd.setPredio(datosPredio);
            
            servletSession.agregarParametro("numReporte", hrt.getId());
            
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial());
            }
            
            // Valores de PeInspeccionFinal
            peInspeccionFinalV.setAnioPermisoConstruc(BigInteger.valueOf(pePermiso.getAnioPermiso()));
            peInspeccionFinalV.setTramite(ht);
            peInspeccionFinalV.setPredio(datosPredio);
            peInspeccionFinalV.setEstado("A");
            peInspeccionFinalV.setMostrarCertificado(true);
            peInspeccionFinalV.setCalle("Vehicular");
            
            peInspeccionFinalV.setNumPermisoConstruc(new BigInteger(pePermiso.getId().toString()));
            tipoInsp = servicesIF.getTipoPermiso("IM");
            if(peInspeccionFinalV.getEvaluoLiquidacion()!=null){
                //peInspeccionFinalV.setImpuesto(peInspeccionFinalV.getEvaluoLiquidacion().multiply(tipoInsp.getValor().divide(new BigDecimal(1000))));
                gutil.setProperty("avaluo", peInspeccionFinalV.getEvaluoLiquidacion());
                gutil.setProperty("valorTipoIns", tipoInsp.getValor());
                gutil.getExpression("getAvaluos", null);
                peInspeccionFinalV.setImpuesto(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
                total = total.add(peInspeccionFinalV.getImpuesto());
            }
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
            peInspeccionFinalV.setAnioInspeccion(BigInteger.valueOf(Long.parseLong(new SimpleDateFormat("yyyy").format(new Date()))));
            peInspeccionFinalV.setUsuario(usuario);
            ht.setValorLiquidacion(total);
            htd.setTotal(total);
            services.update(ht);
            peInspeccionFinalV.setAreaConst(peInspeccionFinalV.getAreaConst().add(peInspeccionFinalV.getAreaParqueos()));
            
            if((peInspeccionFinalV = servicesIF.guardarTasaDeLiquidacion(pePermiso, peInspeccionFinalV, listaDetalle, obs, htd, hrt, lisPropietarios, pePermisosIFList, peDetallePerIFList, htdList, ht))!=null){
                
                if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                    if(lisPropietarios.get(0).getEnte().getEsPersona()){
                        servletSession.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                    }else{
                        servletSession.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombreComercial());                        
                    }
                    servletSession.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
                }
                servletSession.agregarParametro("canton", "Samborondón");
                servletSession.agregarParametro("observacion", obs.getObservacion());
                servletSession.agregarParametro("sector", "La Puntilla");
                servletSession.agregarParametro("inspeccion", peInspeccionFinalV.getInspeccion());
                servletSession.agregarParametro("mz",datosPredio.getUrbMz());
                servletSession.agregarParametro("solar",datosPredio.getUrbSolarnew());
                servletSession.agregarParametro("nombreResponsable", usuario.getEnte().getNombres() == null ? "" : usuario.getEnte().getNombres() + " " + usuario.getEnte().getApellidos() == null ? "" : usuario.getEnte().getApellidos());
                servletSession.agregarParametro("regProf", responsableTec.getRegProf());
                servletSession.agregarParametro("ciResp", usuario.getEnte().getCiRuc());
                servletSession.agregarParametro("calle", "Vehicular");
                servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
                servletSession.agregarParametro("permisoConst", pePermiso.getId());
                servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
                servletSession.agregarParametro("areaEdif", peInspeccionFinalV.getAreaConst());
                servletSession.agregarParametro("areaSolar", peInspeccionFinalV.getAreaSolar());
                servletSession.agregarParametro("codigoAnterior", "***************");
                servletSession.agregarParametro("imsadc",peInspeccionFinalV.getImpuesto());
                servletSession.agregarParametro("revYAprobPlanos",peInspeccionFinalV.getRevicion());
                servletSession.agregarParametro("noAdeudarMunicipio",peInspeccionFinalV.getNoAdeudar());
                servletSession.agregarParametro("verificacionAreaEdificada", 0);
                servletSession.agregarParametro("totalAPagar", total.toString());
                servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
                servletSession.agregarParametro("nombreIng",firma.getNomCompleto());
                servletSession.agregarParametro("cargoIng", firma.getCargo() + " " + firma.getDepartamento());
                servletSession.agregarParametro("responsable", uSession.getNombrePersonaLogeada());
                servletSession.agregarParametro("avaluoConstruccion", peInspeccionFinalV.getEvaluoLiquidacion());
                servletSession.agregarParametro("dia", new SimpleDateFormat("dd").format(new Date()));
                servletSession.agregarParametro("mes", new SimpleDateFormat("MM").format(new Date()));
                servletSession.agregarParametro("anio", new SimpleDateFormat("yyyy").format(new Date()));
                servletSession.agregarParametro("numTramiteSeq", Long.parseLong(sec.toString())+"-"+peInspeccionFinalV.getAnioInspeccion());
                servletSession.agregarParametro("numTramite", ht.getId()+"-"+new SimpleDateFormat("yyyy").format(ht.getFecha()));
                servletSession.agregarParametro("logoImg", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));          
                servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
                servletSession.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                servletSession.setNombreReporte("LiquidacionInspeccionFinal");
                servletSession.setTieneDatasource(true); 
                
                clickInspeccion = false;
                centinela = true;
                JsfUti.messageInfo(null, "Info", "Se generó la tasa de liquidación correctamente. Proceda a imprimirlo.");
            }else{
                JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
            }
        }catch(Exception e){
            e.printStackTrace();
            JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
        }
    }
    
    /**
     * Imprime el pdf de la inspección guardada anteriormente y termina la tarea.
     * 
     * @throws IOException
     * @throws SQLException 
     */
    public void imprimirPDF() throws IOException, SQLException{
        if(!centinela){
            JsfUti.messageError(null, "Error", "No se ha guardado la inspección final. Proceda a guardar la inpección antes de continuar");
            return;
        }
        if (this.getTaskDataByTaskID() == null) {
            JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
            return;
        }
        if(!servletSession.validarCantidadDeParametrosDelServlet()){
            JsfUti.messageError(null, "Error", "No ha guardado los datos de la tasa de liquidación.");
            return;
        }
        peInspeccionFinalV = (PeInspeccionFinal) services.find(PeInspeccionFinal.class, peInspeccionFinalV.getId());
        String msg;

        msg = formatoMsg.getHeader() + "Se generó una tasa de liquidación." + formatoMsg.getFooter();
        
        if(peInspeccionFinalV.getId()!=null){
            servletSession.setReportePDF(reporte.generarPdf("/reportes/inspeccionFinal/LiquidacionInspeccionFinal.jasper", servletSession.getParametros()));
            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
            
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("to", getCorreosByCatEnte(director.getEnte()));
            paramsActiviti.put("message", msg);
            paramsActiviti.put("subject", "Trámite: "+ht.getId()+". Tasa Inspección Final.");
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());

            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
        }else{
            JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
        }
    }
    
    /**
     * Elimina un propietario de la lista de propietarios de un predio.
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
     * Inicializa valores que volverán a usarse.
     */
    public void setearValores() {
        cedulaRuc = "";
        enteList = new ArrayList<>();
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
     * Busca el predio según su código predial. Asimismo carga sus datos, como los
     * propietarios, etc.
     * 
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;
        
        if((CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()}) != null){
            datosPredio = (CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
            if(datosPredio.getNumPredio() != null){
                parroquia = datosPredio.getCiudadela().getCodParroquia();
                canton = parroquia.getIdCanton();
                ht.setNumPredio(datosPredio.getNumPredio());
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
    * Agrega una edificación a la lista de edificaciones de la inspección final.
     */
    public void agregarEdificacion(){
        if(pePermisosCabEdifIF.getAreaConstruccion()!=null && pePermisosCabEdifIF.getDescEdificacion()!=null && pePermisosCabEdifIF.getNumeroPisos()!=null){
            pePermisosIFList.add(pePermisosCabEdifIF);            
        }else{
            JsfUti.messageError(null, "Error", "No ha ingresado toda la información de la edificación.");
            return;
        }
        pePermisosCabEdifIF = new PePermisoCabEdificacion();
        pePermisosCabEdifIF.setEstado(true);
        this.sumarEdificaciones();
    }
    
    /**
     * Suma el área de construcción total del predio en base a las áreas de cada
     * una de sus edificaciones.
     */
    public void sumarEdificaciones(){
        BigDecimal total = new BigDecimal(0);
        for(PePermisoCabEdificacion temp : pePermisosIFList){
            total = total.add(temp.getAreaConstruccion());
        }
        peInspeccionFinalV.setAreaConst(total);
        peInspeccionFinalV.setEvaluoLiquidacion(total.multiply(tipoInsp.getValor()));
        peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
        JsfUti.update("frmMain");
    }
    
    /**
     * Elimina un PeInspeccionCabEdificacion (Una edificación de PeInspeccionFinal)
     * de la lista.
     * 
     * @param inspeccion 
     */
    public void eliminarInspeccion(PePermisoCabEdificacion inspeccion){
        BigDecimal temp;
        if(inspeccion.getId()!=null){
            inspeccion.setEstado(false);
            temp = this.peInspeccionFinalV.getAreaConst();
            temp = temp.subtract(inspeccion.getAreaConstruccion());
            this.peInspeccionFinalV.setAreaConst(temp);
            this.peInspeccionFinalV.setEvaluoLiquidacion(temp.multiply(tipoInsp.getValor()));
            this.peInspeccionFinalV.setEvaluoLiquidacion(this.peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            JsfUti.update("frmMain");
            services.update(inspeccion);
        }
        pePermisosIFList.remove(inspeccion);
    }
    
    /**
     * Elimina un PeDetalleInspeccion (característica de una edificación de 
     * PeInspeccionFinal) de la lista.
     * 
     * @param permiso 
     */
    public void eliminarPermiso(PeDetallePermiso permiso){
        if(permiso.getId()!=null){
            permiso.setEstado(false);
            services.update(permiso);
        }
        peDetallePerIFList.remove(permiso);
    }
    
    /**
     * Agrega una característica a la lista de característica de una edificación de una
     * inspección final.
     * 
     */
    public void agregarCaracteristica(){
        try{
            if(pdp!=null && categoria != null){
                pdp.getIdCatEdfProp().setCategoria(categoria);
                peDetallePerIFList.add(pdp);
            }
            categoria = new CatEdfCategProp();
            pdp = new PeDetallePermiso();
            pdp.setPorcentaje(new BigDecimal(100));
            cepList = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void continuar() {
        JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
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

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public PePermisoLazy getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(PePermisoLazy permisosList) {
        this.permisosList = permisosList;
    }

    public PeInspeccionFinal getPeInspeccionFinalV() {
        return peInspeccionFinalV;
    }

    public void setPeInspeccionFinalV(PeInspeccionFinal peInspeccionFinalV) {
        this.peInspeccionFinalV = peInspeccionFinalV;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public HistoricoTramites getHtEncontrado() {
        return htEncontrado;
    }

    public void setHtEncontrado(HistoricoTramites htEncontrado) {
        this.htEncontrado = htEncontrado;
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

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public List<PePermisoCabEdificacion> getPePermisosIFList() {
        return pePermisosIFList;
    }

    public void setPePermisosIFList(List<PePermisoCabEdificacion> pePermisosIFList) {
        this.pePermisosIFList = pePermisosIFList;
    }

    public List<PeDetallePermiso> getPeDetallePerIFList() {
        return peDetallePerIFList;
    }

    public void setPeDetallePerIFList(List<PeDetallePermiso> peDetallePerIFList) {
        this.peDetallePerIFList = peDetallePerIFList;
    }

    public List<CtlgItem> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<CtlgItem> detallesList) {
        this.detallesList = detallesList;
    }

    public List<CtlgItem> getDetallesSeleccionadosList() {
        return detallesSeleccionadosList;
    }

    public void setDetallesSeleccionadosList(List<CtlgItem> detallesSeleccionadosList) {
        this.detallesSeleccionadosList = detallesSeleccionadosList;
    }

    public String getEdifPrincipal() {
        return edifPrincipal;
    }

    public void setEdifPrincipal(String edifPrincipal) {
        this.edifPrincipal = edifPrincipal;
    }

    public List<CatEdfCategProp> getCatEdfCategPropList() {
        return catEdfCategPropList;
    }

    public void setCatEdfCategPropList(List<CatEdfCategProp> catEdfCategPropList) {
        this.catEdfCategPropList = catEdfCategPropList;
    }

    public List<CatEdfProp> getCepList() {
        return cepList;
    }

    public void setCepList(List<CatEdfProp> cepList) {
        this.cepList = cepList;
    }

    public CatEdfProp getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(CatEdfProp caracteristica) {
        this.caracteristica = caracteristica;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        try{
            if(categoria!=null)
                cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            else
                cepList = null;
            this.categoria = categoria;
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public PePermisoCabEdificacion getPePermisosCabEdifIF() {
        return pePermisosCabEdifIF;
    }

    public void setPePermisosCabEdifIF(PePermisoCabEdificacion pePermisosCabEdifIF) {
        this.pePermisosCabEdifIF = pePermisosCabEdifIF;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public PePermisoCabEdificacion getPePermisosCabEdifIF2() {
        return pePermisosCabEdifIF2;
    }

    public void setPePermisosCabEdifIF2(PePermisoCabEdificacion pePermisosCabEdifIF2) {
        this.pePermisosCabEdifIF2 = pePermisosCabEdifIF2;
    }

    public PeDetallePermiso getPdp() {
        return pdp;
    }

    public void setPdp(PeDetallePermiso pdp) {
        this.pdp = pdp;
    }

    public List<CtlgItem> getListaDetalle() {
        return listaDetalle;
    }

    public void setListaDetalle(List<CtlgItem> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    public PeTipoPermiso getTipoInsp() {
        return tipoInsp;
    }

    public void setTipoInsp(PeTipoPermiso tipoInsp) {
        this.tipoInsp = tipoInsp;
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

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
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
