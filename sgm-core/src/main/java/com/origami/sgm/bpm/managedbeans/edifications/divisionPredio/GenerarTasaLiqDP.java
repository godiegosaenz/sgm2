/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
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
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.ProcesoReporte;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarTasaLiqDP extends BpmManageBeanBaseRoot implements Serializable {
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;

    @javax.inject.Inject
    protected SeqGenMan secuencia;

    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    
    private HashMap<String, Object> paramsActiviti;
    private List<HistoricoReporteTramite> hrts;
    private CatPredio datosPredio;
    private String codigoCatastral;
    private List<CatPredioPropietario> lisPropietarios;
    private List<PeFirma> firmas;
    private String numTramite;
    private Observaciones obs;
    private GeTipoTramite tp;
    private HistoricoTramites ht;
    private HistoricoReporteTramite hrt;
    private PeFirma firma;
    private List<CatEnte> enteList;
    private CatEnte responsableTec;
    private String cedulaRuc, cedulaRucResp;
    private List<CatEnte> enteListAgregados;
    private CatCanton canton;
    private CatParroquia parroquia;
    private CatEnte solicitante;
    private AclUser usuario, digitalizador;
    private CatEnte ente;
    private String descripcionPredio;
    private PdfReporte reporte;
    private Boolean nuevoReporte = false;
    private String numReporte, realizadoPor, observacion;
    private HistoricoTramiteDet htd;
    private List<HistoricoTramiteDet> htdList;
    private BigDecimal baseCalculo1, baseCalculo2, totalPagar;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;   
    private AclRol dirSecGen, dirJuridico;
    private AclUser secGen, jur, cat;
    private List temp;
    
    /**
     * Inicializa todos los valores necesarios para generar y editar una tasa de
     * liquidación del proceso División de Predio.
     */
    @PostConstruct
    public void init() {
        try {
            lisPropietarios = new ArrayList<>();
            reporte = new PdfReporte();
            hrt = new HistoricoReporteTramite();
            paramsActiviti = new HashMap<>();
            obs = new Observaciones();
            String s ="";
            //ht = new HistoricoTramites();
            enteList = new ArrayList<>();
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                //ht = (HistoricoTramites) services.find(HistoricoTramites.class, Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString()));
                ht = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                solicitante = ht.getSolicitante();
                if (ht != null) {
                    //datosPredio = (CatPredio) acl.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                    if(ht.getNumPredio()!=null)
                        datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                    else{
                        JsfUti.messageError(null, "Error", "El trámite no tiene asignado un predio.");
                        return;
                    }
                        
                    if (datosPredio != null) {
                        this.buscarPredio();
                        parroquia = datosPredio.getCiudadela().getCodParroquia();
                        canton = parroquia.getIdCanton();
                    }
                    //usuario = (AclUser) acl.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                    usuario = servicesDP.obtenerAclUserPorQuery(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                    
                    if(usuario.getEnte()!=null){
                        ente = usuario.getEnte();
                        realizadoPor = ente.getNombres() + " " + ente.getApellidos();
                    }
                    numTramite = ht.getId().toString();
                    //firmas = acl.findAllEntCopy(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
                    firmas = servicesDP.obtenerPeFirmaListPorQuery(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
                    if(this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef")!=null)
                        s = this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef").toString();
                    //hrts = (List<HistoricoReporteTramite>) acl.findAllEntCopy(Querys.getReporteByNombreTareaSinEstado, new String[]{"nombreTarea", "idProceso"}, new Object[]{s, this.ht.getIdProceso()});
                    hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                    htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                    htd = new HistoricoTramiteDet();
                    
                    if (hrts == null || hrts.isEmpty()) {
                        nuevoReporte = true;
                        hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                    }else{
                        hrt.setNombreTarea(hrts.get(0).getNombreTarea());
                        hrt.setSecuencia(hrts.get(0).getSecuencia());
                        htd.setNumTasa(hrts.get(0).getSecuencia());
                    }
                    
                    formula = (MatFormulaTramite) acl.find(Querys.getMatFormulaByTipoTramiteID, new String[]{"idTipoTramite"}, new Object[]{ht.getTipoTramite().getId()});
                    gutil = new GroovyUtil(formula.getFormula());
                    htd.setBaseCalculo(new BigDecimal(1));
                    
                    //List<Long> roles = new ArrayList<>();
                    //roles.add(110L); // Secretaria General 110
                    List<AclUser> usersj = acl.findAll(Querys.getAclUserDirectorByRol, new String[]{"idRol"}, new Object[]{110L});
                    for (AclUser u : usersj) {
                        if (u.getSisEnabled() && u.getUserIsDirector()) {
                            secGen= u;
                            break;
                        }
                    }
                    //List<Long> rolesj = new ArrayList<>();
                    //rolesj.add(183L); // Secretaria General 91
                    usersj = acl.findAll(Querys.getAclUserDirectorByRol, new String[]{"idRol"}, new Object[]{183L});
                    for (AclUser u : usersj) {
                        if (u.getSisEnabled() && u.getUserIsDirector()) {
                            jur= u;
                            break;
                        }
                    }
                    
                    
                    usersj = acl.findAll(Querys.getAclUserDirectorByRol, new String[]{"idRol"}, new Object[]{68L});
                    for (AclUser u : usersj) {
                        if (u.getSisEnabled() && u.getUserIsDirector()) {
                            cat = u;
                            break;
                        }
                    }
                }
            }
            List<ParametrosDisparador> p = permisoServices.getParametroDisparadorByTipoTramite(ht.getTipoTramite().getId());
            for (ParametrosDisparador p1 : p) {
                if ("digitalizador".equals(p1.getVarResp())) {
                    digitalizador = (AclUser) EntityBeanCopy.clone(acl.find(AclUser.class, p1.getResponsable().getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Busca un ente a partir del número de cédula o RUC.
     * 
     */
    public void buscarEnte() {
        //CatEnte tempEnte = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        CatEnte tempEnte = servicesDP.obtenerCatEntePorQuery(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        if (tempEnte != null) {
            enteList.add(tempEnte);
        }
    }
    
    /**
     * Setea el valor del avalúo de la propiedad en el Historico Tramite Detalle.
     * 
     */
    public void calcularAvaluoPropiedad(){
        if(htd.getAvaluoSolar() != null && htd.getAvaluoConstruccion() != null){
            gutil.setProperty("avaluoS", htd.getAvaluoSolar());
            gutil.setProperty("avaluoC", htd.getAvaluoConstruccion());
            gutil.getExpression("getAvaluoPropiedad", null);
            htd.setAvaluoPropiedad(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
            this.getAvaluoTotal();
        }
        return;
    }
    
    public void buscarResponsable(){
        responsableTec = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
        if(responsableTec==null)
            JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
        else{
            JsfUti.update("respDlgForm");
            JsfUti.executeJS("PF('dlgResp').show()");
            htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());
        }
    }
    
    /**
     * Setea el valor del avalúo de la construcción en el Historico Tramite Detalle.
     * 
     */
    public void calcularAvaluoConstruccion(){
        if(htd.getAreaEdificacion()!=null){
            gutil.setProperty("areaEdif", htd.getAreaEdificacion());
            gutil.getExpression("getAvaluosConstruccion", null);
            htd.setAvaluoConstruccion(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
            this.getAvaluoTotal();
        }
        return;
    }
    
    /**
     * Setea el valor del avalúo total en el Historico Tramite Detalle.
     * 
     */
    public void getAvaluoTotal(){
        if(htd.getAvaluoPropiedad()!=null){
            gutil.setProperty("avaluoP", htd.getAvaluoPropiedad());
            gutil.getExpression("getAvaluoTotal", null);
            htd.setTotal(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
        }
        return;
    }

    /**
     * Agrega un propietario a la lista de propietarios de un predio.
     * 
     * @param ente 
     */
    public void agregarPropietario(CatEnte ente) {
        CatPredioPropietario propTemp = new CatPredioPropietario();
        CtlgItem propietario = (CtlgItem) acl.find(CtlgItem.class, 56L);
        for(CatPredioPropietario cpp : lisPropietarios){
            if(cpp.getEnte().equals(ente) && cpp.getEstado().equals("A")){
                JsfUti.messageError(null, "Error", "El usuario ya ha sido agregado anteriormente. No lo puede volver a agregar.");
                return;
            }
        }
        propTemp.setPredio(datosPredio);
        propTemp.setEstado("A");
        propTemp.setEsResidente(true);
        propTemp.setTipo(propietario);
        propTemp.setEnte(ente);
        lisPropietarios.add(propTemp);
        enteList.remove(0);
        cedulaRuc = "";
        
    }

    /**
     * Elimina un propietario del predio.
     * 
     * @param prop 
     */
    public void eliminarPropietario(CatPredioPropietario prop) {
        if(prop.getId()!=null){
            prop.setEstado("I");
            acl.persist(prop);
        }
        lisPropietarios.remove(prop);
    }

    /**
     * Inicializa valores  que volverán a ser usados.
     * 
     */
    public void setearValores() {
        cedulaRuc = "";
        enteList = new ArrayList<>();
    }

    /**
     * Genera la tasa de liquidación de la división de predio.
     * 
     * @throws IOException 
     */
    public void imprimirPDF() throws IOException {
        
        if (this.getTaskDataByTaskID() == null) {
            JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
            return;
        }
        if(observacion==null || observacion==""){
            JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
            return;
        }
        if(responsableTec==null){
            JsfUti.messageError(null, "Error", "Debe seleccionar un responsable técnico antes de continuar.");
            return;
        }
        if(htd.getNumPisosBajoBord() == null || htd.getNumPisosSobreBord() == null){
            JsfUti.messageError(null, "Error", "Cersiorese de ingresar los valores de número de pisos. En caso de no tenerlos ingrese 0.");
            return;
        }
        
        try {
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            BigInteger sec;
            servletSession.instanciarParametros();
            String lugarYFecha = parroquia.getDescripcion() + ", " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            hrt.setFecCre(new Date());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_DivisionPredio-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);
            
            if(hrts == null || hrts.isEmpty()){
                sec = new BigInteger(secuencia.getMaxSecuenciaTipoTramite(Integer.valueOf(anio+""), ht.getTipoTramite().getId()).toString());
                
                ht.setNumTramiteXDepartamento(sec);
                sec = secuencia.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{anio});
                hrt.setSecuencia(sec);
                htd.setNumTasa(sec);
                hrt.setCodValidacion(sec + "" + hrt.getProceso());
                
            }else{
                hrt.setCodValidacion((hrts.get(0).getSecuencia().add(BigInteger.valueOf(hrts.size()))) + "" + hrt.getProceso());
            }
            
            if (hrt.getId() == null) {
                hrt = (HistoricoReporteTramite) acl.persist(hrt);
            }
            
            htd.setTramite(ht);
            
            htd.setEstado(true);
            htd.setFecCre(new Date());
            htd.setPredio(datosPredio);
            htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());
            servletSession.agregarParametro("nombreCanton", "Gobierno Autónomo Descentralizado Municipal del Cantón " + canton.getNombre());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("lugarYFecha", lugarYFecha);
            if(nuevoReporte)
                servletSession.agregarParametro("numReporte", htd.getNumTasa());
            else
                servletSession.agregarParametro("numReporte", hrts.get(0).getId());
            servletSession.agregarParametro("numeroTramite", numTramite+"-"+new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
            
            servletSession.agregarParametro("nombres", solicitante.getNombreCompleto());/*
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nombres", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nombres", solicitante.getNombreComercial());
            }*/
            servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
            servletSession.agregarParametro("canton", canton.getNombre());
            servletSession.agregarParametro("parroquia", parroquia.getDescripcion());
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("codigoCatastral", codigoCatastral);
            servletSession.agregarParametro("mz", "" + datosPredio.getUrbMz());
            servletSession.agregarParametro("solar", "" + datosPredio.getUrbSolarnew());
            if(htd.getAvaluoConstruccion()!=null)
                servletSession.agregarParametro("avaluoConstruccion", "" + htd.getAvaluoConstruccion());
            if(htd.getAreaEdificacion()!=null)
                servletSession.agregarParametro("areaEdif", "" + htd.getAreaEdificacion());
            if(htd.getAvaluoConstruccion()!=null)
                servletSession.agregarParametro("avaluoConstruccion", "" + htd.getAvaluoConstruccion().toString());
            if(htd.getAvaluoSolar()!=null)
                servletSession.agregarParametro("avaluoSolar", "" + htd.getAvaluoSolar().toString());
            if(htd.getAvaluoPropiedad()!=null)
                servletSession.agregarParametro("avaluoPropiedad", "" + htd.getAvaluoPropiedad().toString());
            servletSession.agregarParametro("descripcion", "" + htd.getDescripcion());
            servletSession.agregarParametro("baseCalculo1", "" + htd.getBaseCalculo());
            servletSession.agregarParametro("baseCalculo2", "");
            servletSession.agregarParametro("totalAPagar", "" + htd.getTotal());
            servletSession.agregarParametro("nombreIng", htd.getFirma().getNomCompleto());
            servletSession.agregarParametro("pisosSNB", htd.getNumPisosSobreBord());
            servletSession.agregarParametro("pisosBNB", htd.getNumPisosBajoBord());
            servletSession.agregarParametro("areaEdif", htd.getAreaEdificacion());
            servletSession.agregarParametro("cargoIng", htd.getFirma().getCargo() + " " + htd.getFirma().getDepartamento());
            servletSession.agregarParametro("responsable", responsableTec.getNombres() + " " + responsableTec.getApellidos());
            servletSession.agregarParametro("numValidador", hrt.getCodValidacion());
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas/lilianaGuerrero.jpg"));
            servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.setNombreReporte("TasaLiq_DivisionPredio");
            servletSession.setTieneDatasource(false);
            
            ht.setValorLiquidacion(htd.getTotal());
            acl.persist(ht);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setObservacion(observacion);
            obs.setTarea(this.getTaskDataByTaskID().getName());
                
            servletSession.setReportePDF(reporte.generarPdf("/reportes/divisionPredioReporte.jasper", servletSession.getParametros()));

            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("to", getCorreosByCatEnte(solicitante));
            paramsActiviti.put("message", "Se generó una tasa de liquidación de división de predios.");
            paramsActiviti.put("subject", "Trámite: "+ht.getId());
            if(nuevoReporte)
                paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            paramsActiviti.put("directorJuridico", jur.getUsuario());
            paramsActiviti.put("secretariaGeneral", secGen.getUsuario());
            paramsActiviti.put("digitalizador", digitalizador.getUsuario());
            //paramsActiviti.put("secretariaAlcaldia", "alcaldia");
            //paramsActiviti.put("directorJuridico", "juridico");
            paramsActiviti.put("directorCatastro", cat.getUsuario());
            //paramsActiviti.put("urlTec", "/faces/vistaprocesos/edificaciones/divisionPredio/realizarDP.xhtml");
            
            if(servicesDP.guardarTasaDeLiquidacion(obs, datosPredio, htd, lisPropietarios, hrts, htdList, hrt)){
                reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
                JsfUti.messageInfo(null, "Info", "Se generó la tasa correctamente...");
                this.completeTask(this.getTaskId(), paramsActiviti);
                servletSession.borrarDatos();
            }else{
                JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca un predio de acuerdo a su código predial.
     * 
     */
    public void buscarPredio() {
        List<ProcesoReporte> prList;
        List<CatPredioPropietario> propTemp;
        lisPropietarios = new ArrayList<CatPredioPropietario>();
        
        //datosPredio = (CatPredio) acl.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
        datosPredio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
        datosPredio.setEstado("A");
        codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

        propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

        prList = (List<ProcesoReporte>) datosPredio.getProcesoReporteCollection();

        for (CatPredioPropietario temp : propTemp) {
            if (temp.getEstado().equals("A")) {
                lisPropietarios.add(temp);
            }
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

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
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

    public GeTipoTramite getTp() {
        return tp;
    }

    public void setTp(GeTipoTramite tp) {
        this.tp = tp;
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

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
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

    public List<CatEnte> getEnteListAgregados() {
        return enteListAgregados;
    }

    public void setEnteListAgregados(List<CatEnte> enteListAgregados) {
        this.enteListAgregados = enteListAgregados;
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

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public String getDescripcionPredio() {
        return descripcionPredio;
    }

    public void setDescripcionPredio(String descripcionPredio) {
        this.descripcionPredio = descripcionPredio;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public Boolean getNuevoReporte() {
        return nuevoReporte;
    }

    public void setNuevoReporte(Boolean nuevoReporte) {
        this.nuevoReporte = nuevoReporte;
    }

    public String getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(String numReporte) {
        this.numReporte = numReporte;
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

    public BigDecimal getBaseCalculo1() {
        return baseCalculo1;
    }

    public void setBaseCalculo1(BigDecimal baseCalculo1) {
        this.baseCalculo1 = baseCalculo1;
    }

    public BigDecimal getBaseCalculo2() {
        return baseCalculo2;
    }

    public void setBaseCalculo2(BigDecimal baseCalculo2) {
        this.baseCalculo2 = baseCalculo2;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getRealizadoPor() {
        return realizadoPor;
    }

    public void setRealizadoPor(String realizadoPor) {
        this.realizadoPor = realizadoPor;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
