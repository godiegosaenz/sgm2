/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.BaseCalculoOtrosTramites;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.OtrosTramitesServices;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class EditarLiquidacionOT extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(EditarLiquidacionOT.class.getName());
    
    @Inject
    private ServletSession servletSession;
    
    @Inject
    private ReportesView reportes;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected SeqGenMan secuencia;
    
    @javax.inject.Inject
    protected OtrosTramitesServices otServices;
    
    private PePermiso pePermiso = null;
    private List<PePermiso> pePermisoListTemp;
    private PePermisoLazy permisosList;
    private List<HistoricoTramiteDet> htdList;
    private CatPredio datosPredio;
    private Observaciones obs;
    private HistoricoTramites ht;
    private HistoricoReporteTramite hrt;
    private List<HistoricoReporteTramite> hrts;
    private PdfReporte reporte;
    private HistoricoTramiteDet htd;
    private Boolean clickInspeccion = true;
    private AclUser usuario;
    private CatEnte solicitante, responsableTec;
    private HashMap<String, Object> paramsActiviti;
    private CatCanton canton;
    private CatParroquia parroquia;
    private List<CatPredioPropietario> lisPropietarios;
    private String codigoCatastral, cedulaRucResp;
    private List<CatEnte> enteList;
    private String cedulaRuc;
    private List<BaseCalculoOtrosTramites> baseCalculoList;
    private BaseCalculoOtrosTramites baseSeleccionada;
    private BigDecimal valor;
    private BigDecimal total;
    private String descripcion, nota;
    private PeFirma firma;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private Boolean mostrarLista;
    private OtrosTramites oTramite;
    private OtrosTramitesHasPermiso othp;
    
    private CtlgSalario salario;
    private BigDecimal areaConst;
    
    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                oTramite = ht.getSubTipoTramite();
                salario = (CtlgSalario)services.find(Querys.salario, new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
                htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                if(ht==null)
                    return;
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                hrt = new HistoricoReporteTramite();
                htd = new HistoricoTramiteDet();
                
                htd.setFirma(htdList.get(0).getFirma());
                htd.setNumTasa(htdList.get(0).getNumTasa());
                htd.setTramite(htdList.get(0).getTramite());
                htd.setResponsable(htdList.get(0).getResponsable());
                htd.setFecCre(new Date());
                htd.setEstado(true);
                obs = new Observaciones();
                othp = ht.getOtrosTramitesHasPermiso();
                if(othp != null){
                    baseSeleccionada = oTramite.getBaseCalculo();
                    pePermiso = othp.getPePermiso();
                }
                paramsActiviti = new HashMap<String, Object>();
                pePermisoListTemp = (List<PePermiso>) ht.getPePermisoCollection();
                if(pePermisoListTemp != null && !pePermisoListTemp.isEmpty())
                    pePermiso = pePermisoListTemp.get(0);
                if(ht.getNumPredio()!=null)
                    datosPredio = (CatPredio) services.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                solicitante = ht.getSolicitante();
                baseCalculoList = new ArrayList();
                for(BaseCalculoOtrosTramites temp: (List<BaseCalculoOtrosTramites>)services.findAll(Querys.getBaseCalculoOT, new String[]{}, new Object[]{})){
                    baseCalculoList.add((BaseCalculoOtrosTramites) EntityBeanCopy.clone(temp));
                }
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                formula = (MatFormulaTramite) services.find(MatFormulaTramite.class, 28L);
                gutil = new GroovyUtil(formula.getFormula());
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                permisosList = new PePermisoLazy();
                total = ht.getValorLiquidacion();
            }
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Error", e);
        }
    }
    
    public void calcularTotal(){
        try{
            if(oTramite.getId() == 30){
                gutil.setProperty("salario_basico", salario.getValor());
                if(areaConst!=null){
                    gutil.setProperty("area_cons", areaConst);
                }else{
                    JsfUti.messageInfo(null, "Info", "No se ha ingresado el área de construccion");
                    return;
                }
                if(this.othp.getValor() != null){
                    gutil.setProperty("linea_fabrica", this.othp.getValor());
                    if(this.othp.getValor() != null && this.othp.getFactor2() != null)
                        gutil.setProperty("linea_fabrica", this.othp.getValor().multiply(this.othp.getFactor2()));
                }else{
                    JsfUti.messageInfo(null, "Info", "No se ha ingresado el valor de la línea de fábrica");
                    return;
                }
                gutil.setProperty("tipo_ot", oTramite.getId());
            }else{
                gutil.setProperty("avaluo", this.othp.getValor());
                if(this.othp.getValor() != null && this.othp.getFactor2() != null)
                        gutil.setProperty("avaluo", this.othp.getValor().multiply(this.othp.getFactor2()));
                if(baseSeleccionada==null)
                        gutil.setProperty("base", 1);
                else
                    gutil.setProperty("base", this.baseSeleccionada.getValorBase()); 
                gutil.setProperty("divisor", this.oTramite.getFactor());            

            }
            gutil.getExpression("getTotal", null);
            total = (BigDecimal)gutil.getProperty("total");
            total =total.setScale(2, RoundingMode.HALF_UP);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Busca el predio según su código predial. Asimismo carga sus datos, como los
     * propietarios, etc.
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
     * Cambia de valor a una variable booleana que controla si muestra o no la lista
     * de PePermiso en el facelete.
     * 
     */
    public void mostrarLista(){
        mostrarLista = true;
    }
    
    /**
     * Al seleccionar un permiso de la lista de permisos, se procede a cargar todos
     * sus datos en memoria y exponerlos al usuario.
     * 
     * @param permiso 
     */
    public void onRowSelectPermiso(PePermiso permiso){
        String pagado = null;
        HistoricoTramites htTemp = null;
        mostrarLista = false;
        try{
            if(permiso.getTramite()!=null)
                htTemp = (HistoricoTramites) services.findNoProxy(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{permiso.getTramite().getId()});
            
            if(htTemp != null){
                JsfUti.messageInfo(null, "Info", "Trámite asociado encontrado.");
                if(htTemp.getEstado().equals("finalizado") || htTemp.getEstado().equals("Finalizado") ){
                    pePermiso = permiso;
                    JsfUti.update("frmMain");
                    JsfUti.messageInfo(null, "Info", "Tasa cancelada.");
                    JsfUti.messageInfo(null, "Info", "Se cargó la información del permiso.");
                    if(datosPredio==null)
                        JsfUti.messageInfo(null, "Info", "Trámite no asociado a ningún predio.");
                    else
                        JsfUti.messageInfo(null, "Info", "Predio encontrado.");
                }else{
                    JsfUti.messageError(null, "Error", "Hasta el Momento el Usuario aún NO CANCELA su Tasa de Liquidación.");
                }
            }else{
                JsfUti.messageError(null, "Error", "Trámite asociado no encontrado.");
            }
        }catch(Exception e){
            LOG.log(Level.SEVERE, "Error", e);
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
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Genera el pdf según la información ingresada por el usuario.
     * 
     */
    public void imprimirPDF(){
        try{
            reporte = new PdfReporte();
            BigInteger sec;

            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(obs.getObservacion()==null || "".equals(obs.getObservacion())){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            } 
            if(othp.getValor() == null || othp.getDescripcion() == null){
                JsfUti.messageError(null, "Error", "Faltan datos que debe ingresar.");
                return;
            }

            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.instanciarParametros();

            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_OT-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);

            
            hrt.setCodValidacion((hrts.get(0).getSecuencia().add(BigInteger.valueOf(hrts.size()))) + "" + hrt.getProceso());
            
            hrt.setSecuencia(hrts.get(0).getSecuencia());
            
            hrt = (HistoricoReporteTramite) services.persist(hrt);

            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());

            /*gutil.setProperty("avaluo", this.othp.getValor());
            if(baseSeleccionada==null)
                gutil.setProperty("base", 1000);
            else
                gutil.setProperty("base", this.baseSeleccionada.getValorBase());                
            gutil.getExpression("getTotal", null);
            total = (BigDecimal)gutil.getProperty("total");
            total =total.setScale(2, RoundingMode.HALF_UP);
            valor = BigDecimal.valueOf(Double.parseDouble(this.othp.getValor()+"")).setScale(2, RoundingMode.HALF_UP);
                    */
            // <!-- -->
           /* gutil.setProperty("avaluo", this.othp.getValor());
            if(baseSeleccionada==null)
                    gutil.setProperty("base", 1);
            else
                gutil.setProperty("base", this.baseSeleccionada.getValorBase()); 
            gutil.setProperty("divisor", this.oTramite.getFactor());
            gutil.getExpression("getTotal", null);
            total = (BigDecimal)gutil.getProperty("total");
            total =total.setScale(2, RoundingMode.HALF_UP);*/
            // <!-- -->
            valor = BigDecimal.valueOf(Double.parseDouble(this.othp.getValor()+"")).setScale(2, RoundingMode.HALF_UP);
            
            ht.setValorLiquidacion(total);
            htd.setTotal(total);
            //services.update(ht);
            
            if(baseSeleccionada!=null)
                oTramite.setBaseCalculo(baseSeleccionada);
            switch(Integer.parseInt(oTramite.getTipoSeleccion()+"")){
                case 3:
                    servletSession.agregarParametro("sel3", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 4:
                    servletSession.agregarParametro("sel4", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 6:
                    servletSession.agregarParametro("sel6", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 7:
                    servletSession.agregarParametro("sel7", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 8:
                    servletSession.agregarParametro("sel8", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 9:
                    servletSession.agregarParametro("sel9", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 10:
                    servletSession.agregarParametro("sel3", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 11:
                    servletSession.agregarParametro("sel4", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
                case 12:
                    servletSession.agregarParametro("sel5", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                    break;
            }
            othp.setPePermiso(pePermiso);
            //services.update(oTramite);
            
            if(otServices.guardarOtrosTramitesEdicion(hrt, htd, obs, othp, oTramite, ht, htdList, hrts)){
                if (solicitante.getEsPersona()) {
                    servletSession.agregarParametro("nomPropietario", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
                    servletSession.agregarParametro("nomSolicitante", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
                } else {
                    servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial().toUpperCase());
                    servletSession.agregarParametro("nomSolicitante", solicitante.getRazonSocial());
                }

                
                servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
                servletSession.agregarParametro("numReporte", ht.getNumTramiteXDepartamento()+"-"+new SimpleDateFormat("yyyy").format(new Date()));

                if(datosPredio!=null){
                    servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                    servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                    servletSession.agregarParametro("mz",datosPredio.getUrbMz());
                    servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                    servletSession.agregarParametro("codCatastral", datosPredio.getCodigoPredial().toUpperCase());
                    servletSession.agregarParametro("urb", datosPredio.getCiudadela().getNombre().toUpperCase());
                    servletSession.agregarParametro("solar",datosPredio.getUrbSolarnew());
                    servletSession.agregarParametro("codCatAnt", datosPredio.getPredialant());
                }else{
                    servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                    servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                    servletSession.agregarParametro("mz",ht.getMz());
                    servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                    servletSession.agregarParametro("codCatastral", null);
                    servletSession.agregarParametro("urb", ht.getUrbanizacion() != null ? ht.getUrbanizacion().getNombre() : null);
                    servletSession.agregarParametro("solar",ht.getSolar());
                }
                if(responsableTec != null){
                    servletSession.agregarParametro("ciResponsable", responsableTec.getCiRuc());
                    servletSession.agregarParametro("responsable", responsableTec.getNombres() + " " + responsableTec.getApellidos());
                }
                servletSession.agregarParametro("presupuesto", valor+"");
                servletSession.agregarParametro("numTramite", ht.getId()+"-"+new SimpleDateFormat("yyyy").format(new Date()));
                servletSession.agregarParametro("descripcion", othp.getDescripcion().toUpperCase());
                servletSession.agregarParametro("descripcionTramite", ht.getTipoTramiteNombre());
                if(baseSeleccionada==null)
                    servletSession.agregarParametro("base1", "***");
                else
                    servletSession.agregarParametro("base1", baseSeleccionada.getValorBase());
                if(othp.getFactor2()==null)
                    servletSession.agregarParametro("base2", "***");
                else
                    servletSession.agregarParametro("base2", othp.getFactor2());
                servletSession.agregarParametro("total", total+"");
                servletSession.agregarParametro("nombreIng", firma.getNomCompleto().toUpperCase());
                servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+ " " + firma.getDepartamento());
                servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
                servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
                servletSession.setNombreReporte("LiquidacionOtrosTramites");
                servletSession.setTieneDatasource(false);

                if(oTramite.getNecesitaPredio())
                    servletSession.setReportePDF(reporte.generarPdf("/reportes/otrosTramites/TasaLiquidacionOT.jasper", servletSession.getParametros()));
                else
                    servletSession.setReportePDF(reporte.generarPdf("/reportes/otrosTramites/TasaLiquidacionOTSinPredio.jasper", servletSession.getParametros()));
                reportes.descargarPDFarregloBytes(servletSession.getReportePDF());

                for(HistoricoReporteTramite temp : hrts){
                    temp.setEstado(false);
                    services.update(temp);
                }
                
            }else{
                JsfUti.messageError(null, "Error", "Hubo un error al crear la tasa de liquidación. Inténtelo nuevamente.");
            }
            paramsActiviti.put("idReporte", hrt.getId());
            if(pePermiso!=null)
                paramsActiviti.put("idPermiso", pePermiso.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            servletSession.borrarDatos();

            this.completeTask(this.getTaskId(), paramsActiviti);
        }catch(NumberFormatException | SQLException | IOException e){
            LOG.log(Level.SEVERE, "Error", e);
        }
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
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

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
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

    public List<CatPredioPropietario> getLisPropietarios() {
        return lisPropietarios;
    }

    public void setLisPropietarios(List<CatPredioPropietario> lisPropietarios) {
        this.lisPropietarios = lisPropietarios;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
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

    public List<BaseCalculoOtrosTramites> getBaseCalculoList() {
        return baseCalculoList;
    }

    public void setBaseCalculoList(List<BaseCalculoOtrosTramites> baseCalculoList) {
        this.baseCalculoList = baseCalculoList;
    }

    public BaseCalculoOtrosTramites getBaseSeleccionada() {
        return baseSeleccionada;
    }

    public void setBaseSeleccionada(BaseCalculoOtrosTramites baseSeleccionada) {
        this.baseSeleccionada = baseSeleccionada;
    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public PePermisoLazy getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(PePermisoLazy permisosList) {
        this.permisosList = permisosList;
    }

    public Boolean getMostrarLista() {
        return mostrarLista;
    }

    public void setMostrarLista(Boolean mostrarLista) {
        this.mostrarLista = mostrarLista;
    }

    public OtrosTramites getoTramite() {
        return oTramite;
    }

    public void setoTramite(OtrosTramites oTramite) {
        this.oTramite = oTramite;
    }

    public OtrosTramitesHasPermiso getOthp() {
        return othp;
    }

    public void setOthp(OtrosTramitesHasPermiso othp) {
        this.othp = othp;
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

    public List<HistoricoTramiteDet> getHtdList() {
        return htdList;
    }

    public void setHtdList(List<HistoricoTramiteDet> htdList) {
        this.htdList = htdList;
    }

    public Boolean getClickInspeccion() {
        return clickInspeccion;
    }

    public void setClickInspeccion(Boolean clickInspeccion) {
        this.clickInspeccion = clickInspeccion;
    }

    public CtlgSalario getSalario() {
        return salario;
    }

    public void setSalario(CtlgSalario salario) {
        this.salario = salario;
    }

    public BigDecimal getAreaConst() {
        return areaConst;
    }

    public void setAreaConst(BigDecimal areaConst) {
        this.areaConst = areaConst;
    }
    
}
