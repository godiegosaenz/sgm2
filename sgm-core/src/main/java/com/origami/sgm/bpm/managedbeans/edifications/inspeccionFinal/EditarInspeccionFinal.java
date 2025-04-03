/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
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
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetalleInspeccion;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
public class EditarInspeccionFinal implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;

    @Inject
    private ReportesView reportes;
    
    @Inject
    private RealizarInspeccion inspeccionManager;

    private Long idInspeccion;
    private PeInspeccionFinal inspeccion;
    private String nomResponsable, codigoCatastral, cedulaRuc, cedulaRucResp, mensajeResponsableTec;
    private HistoricoTramites ht;
    private CatPredio datosPredio;
    private List<CatPredioPropietario> lisPropietarios, propTemp;
    private CatCanton canton;
    private CatParroquia parroquia;
    private List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList;
    private PeInspeccionCabEdificacion peInspeccionCabEdificacion, peInspeccionCabEdificacionTemp;
    private List<CtlgItem> detallesList, listaDetalle;
    private CatEdfCategProp categoria;
    private List<CatEdfCategProp> catEdfCategPropList;
    private List<CatEdfProp> cepList;
    private PeDetalleInspeccion detalleInspeccion;
    private List<PeDetalleInspeccion> detalleInspeccionList, peDetallePerIFList;
    private List<CatEnte> enteList;
    private Boolean clickInspeccion;
    private PeTipoPermiso tipoInsp;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private PeFirma firma;
    private AclUser director, usuario;
    private PdfReporte reporte;
    private CatEnte solicitante, responsableTec;
    private CatPredioPropietario propietario;

    @PostConstruct
    public void initView() {
        try {
            if (sess.esLogueado()) {
                nomResponsable = sess.getNombrePersonaLogeada();
                idInspeccion = (Long) ss.retornarValor("idInspeccion");
                cargarDatosInsp();
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{sess.getName_user()});
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                director = firma.getAclUser();

                formula = (MatFormulaTramite) services.find(MatFormulaTramite.class, 6L);
                gutil = new GroovyUtil(formula.getFormula());
                tipoInsp = (PeTipoPermiso) services.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{"INF"});
                
                solicitante = ht.getSolicitante();
                clickInspeccion = false;
                detalleInspeccion = new PeDetalleInspeccion();
                detalleInspeccion.setEstado(true);
                detalleInspeccion.setPorcentaje(new BigDecimal(100));
                ss.borrarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Consulta la inspeccion si existe llenos los datos de la inspeccion.
     */
    private void cargarDatosInsp() {
        inspeccion = (PeInspeccionFinal) services.find(PeInspeccionFinal.class, idInspeccion);
        if (inspeccion != null) {
            if (inspeccion.getTramite() != null) {
                ht = inspeccion.getTramite();
                System.out.println("/*** "+ht.getNumPredio());
                datosPredio = (CatPredio) services.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                if(datosPredio==null){
                    datosPredio = inspeccion.getPredio();
                }
                if (datosPredio.getCiudadela() != null) {
                    if (datosPredio.getCiudadela().getCodParroquia() != null) {
                        parroquia = datosPredio.getCiudadela().getCodParroquia();
                        canton = parroquia.getIdCanton();
                    }
                }
                codigoCatastral = datosPredio.getSector() + "." + datosPredio.getMz() + "." + datosPredio.getCdla() + "." + datosPredio.getMzdiv() + "." + datosPredio.getSolar() + "." + datosPredio.getDiv1() + "." + datosPredio.getDiv2() + "." + datosPredio.getDiv3() + "." + datosPredio.getDiv4() + "." + datosPredio.getDiv5() + "." + datosPredio.getDiv6() + "." + datosPredio.getDiv7() + "." + datosPredio.getDiv8() + "." + datosPredio.getDiv9() + "." + datosPredio.getPhv() + "." + datosPredio.getPhh();

                propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();
            }

            peInspeccionCabEdificacionList = (List<PeInspeccionCabEdificacion>) inspeccion.getPeInspeccionCabEdificacionCollection();
            if (peInspeccionCabEdificacionList == null) {
                peInspeccionCabEdificacionList = new ArrayList<>();
            }
            peInspeccionCabEdificacionTemp = new PeInspeccionCabEdificacion();
            detallesList = servicesIF.obtenerCtlgItemListByNombreDeCatalogo("permiso_inspeccion.detalle");
            catEdfCategPropList = (List<CatEdfCategProp>) services.findAll(Querys.getCatCategoriasPropConstruccionList, new String[]{}, new Object[]{});
            lisPropietarios = new ArrayList<CatPredioPropietario>();

            for (CatPredioPropietario temp : propTemp) {
                if (temp.getEstado().equals("A")) {
                    lisPropietarios.add(temp);
                }
            }
            //sumarAreaConstruccionInit();
            if(inspeccion.getAreaConst()!=null && inspeccion.getAreaConst().compareTo(BigDecimal.ZERO)>0 && inspeccion.getAreaParqueos()!=null)
                inspeccion.setAreaConst(inspeccion.getAreaConst().subtract(inspeccion.getAreaParqueos()));
            responsableTec = inspeccion.getRespTecnico();
            cedulaRucResp = responsableTec.getCiRuc();
            inspeccion.setFechaIngreso(new Date());
            inspeccion.setUsuarioIngreso(sess.getName_user()+"-Modificacion");
        }
    }
    
    public void sumarAreaConstruccionInit(){
        BigDecimal areaTemp = BigDecimal.ZERO;
        for(PeInspeccionCabEdificacion temp : inspeccion.getPeInspeccionCabEdificacionCollection()){
            areaTemp.add(temp.getAreaConstruccion());
        }
        inspeccion.setAreaConst(areaTemp);
    }

    public void imprimirPDF() throws IOException, SQLException {
        try {
            if (!clickInspeccion) {
                JsfUti.messageError(null, "Error", "No se ha guardado la inspección final. Proceda a guardar la inpección antes de continuar");
                return;
            }

            String msg;

            ss.setReportePDF(reporte.generarPdf("/reportes/inspeccionFinal/LiquidacionInspeccionFinal.jasper", ss.getParametros()));
            reportes.descargarPDFarregloBytes(ss.getReportePDF());
            ss.borrarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca el predio según su código predial. Asimismo carga sus datos, como
     * los propietarios, etc.
     *
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;

        if ((CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()}) != null) {
            datosPredio = (CatPredio) services.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
            if (datosPredio.getNumPredio() != null) {
                //ht.setNumPredio(datosPredio.getNumPredio());
                parroquia = datosPredio.getCiudadela().getCodParroquia();
                canton = parroquia.getIdCanton();
                JsfUti.messageInfo(null, "Info", "Predio encontrado.");
            } else {
                JsfUti.messageError(null, "Error", "El predio encontrado no tiene número de predio.");
                return;
            }
        } else {

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
    
    public void buscarResponsable(){
        responsableTec = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
        if(responsableTec==null){
            JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
        }else{
            mensajeResponsableTec = "El responsable técnico es "+responsableTec.getNombres()+" "+responsableTec.getApellidos();
            JsfUti.update("respDlgForm");
            JsfUti.executeJS("PF('dlgResp').show()");
            inspeccion.setRespTecnico(responsableTec);
        }
    }

    public void onRowSelect(SelectEvent event) {
        peDetallePerIFList = (List<PeDetalleInspeccion>) peInspeccionCabEdificacion.getPeDetalleInspeccionCollection();
        if (peDetallePerIFList == null || peDetallePerIFList.isEmpty()) {
            peDetallePerIFList = new ArrayList<>();
            peInspeccionCabEdificacion.setPeDetalleInspeccionCollection(peDetallePerIFList);
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
     * Elimina un PeInspeccionCabEdificacion (Una edificación de
     * PeInspeccionFinal) de la lista.
     *
     * @param inspeccion
     */
    public void eliminarInspeccion(PeInspeccionCabEdificacion inspeccion) {
        BigDecimal temp;
        if (inspeccion.getId() != null) {
            inspeccion.setEstado(false);            
            services.update(inspeccion);

        }
        temp = this.inspeccion.getAreaConst();
        temp = temp.subtract(inspeccion.getAreaConstruccion());
        this.inspeccion.setAreaConst(temp);
        this.inspeccion.setEvaluoLiquidacion(temp.multiply(tipoInsp.getValor()));
        this.inspeccion.setEvaluoLiquidacion(this.inspeccion.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
        JsfUti.update("frmMain");
        peInspeccionCabEdificacionList.remove(inspeccion);
    }

    /**
     * Elimina un PeDetalleInspeccion (característica de una edificación de
     * PeInspeccionFinal) de la lista.
     *
     * @param detalleIns
     */
    public void eliminarPermiso(PeDetalleInspeccion detalleIns) {
        if (detalleIns.getId() != null) {
            detalleIns.setEstado(false);
            services.update(detalleIns);
        }
        peDetallePerIFList.remove(detalleIns);
    }

    /**
     * Agrega un propietario a la lista de propietarios de un predio.
     *
     * @param ente
     */
    public void agregarPropietario(CatEnte ente) {
        CatPredioPropietario propTemp = new CatPredioPropietario();
        for (CatPredioPropietario cpp : lisPropietarios) {
            if (cpp.getEnte().equals(ente) && cpp.getEstado().equals("A")) {
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
     * Elimina un propietario del predio.
     *
     * @param prop
     */
    public void eliminarPropietario(CatPredioPropietario prop) {
        if (prop.getId() != null) {
            prop.setEstado("I");
            services.update(prop);
        }
        lisPropietarios.remove(prop);
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
     * Guarda todos los datos editados de la inspección final, previamente
     * validados
     *
     */
    public void guardarDatos() {

        try {
            Observaciones obs = new Observaciones();
            reporte = new PdfReporte();
            BigInteger sec;
            String msg = "";
            BigDecimal total = new BigDecimal(0), temp;
            PePermiso pp = (PePermiso) services.find(PePermiso.class, inspeccion.getNumPermisoConstruc().longValue());

            if (peInspeccionCabEdificacionList == null || peInspeccionCabEdificacionList.isEmpty()) {
                JsfUti.messageError(null, "Error", "La inspección final debe tener una edificación al menos.");
                return;
            }
            
            for(PeInspeccionCabEdificacion t : peInspeccionCabEdificacionList){
                if(t.getNumEdificacion().equals(0) && t.getPeDetalleInspeccionCollection().size()<6){
                    JsfUti.messageInfo(null, "Info", "La edificación principal debe tener al menos 6 características principales.");
                    return;
                }
            }

            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            //ss.instanciarParametros();

            
            
            /*ss.agregarParametro("numReporte", inspeccion.getNumReporte());
            
            if(solicitante!=null){
                if (solicitante.getEsPersona()) {
                    ss.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
                } else {
                    ss.agregarParametro("nomPropietario", solicitante.getNombreComercial());
                }
            }*/

            // Valores de PeInspeccionFinal
            inspeccion.setAnioPermisoConstruc(inspeccion.getAnioPermisoConstruc());
            inspeccion.setTramite(ht);
            inspeccion.setPredio(datosPredio);
            inspeccion.setEstado("A");
            inspeccion.setUsuarioIngreso(sess.getName_user());
            inspeccion.setFechaIngreso(new Date());
            
            tipoInsp = servicesIF.getTipoPermiso("IN");
            inspeccion.setInspeccion(tipoInsp.getValor());
            total = total.add(inspeccion.getInspeccion());
            tipoInsp = servicesIF.getTipoPermiso("RV");
            inspeccion.setRevicion(tipoInsp.getValor());
            total = total.add(inspeccion.getRevicion());
            tipoInsp = servicesIF.getTipoPermiso("ND");
            inspeccion.setNoAdeudar(tipoInsp.getValor());
            total = total.add(inspeccion.getNoAdeudar());
            inspeccion.setAreaEdificada(BigDecimal.ZERO);
            if(propietario != null)
                inspeccion.setPropietario(propietario.getEnte());
            //total = total.add(inspeccion.getEvaluoLiquidacion());
            
            
            if(inspeccion.getAreaParqueos()==null)
                inspeccion.setAreaParqueos(BigDecimal.ZERO);
            inspeccion.setAreaConst(inspeccion.getAreaConst().add(inspeccion.getAreaParqueos()));
            tipoInsp = servicesIF.getTipoPermiso("INF");
            inspeccion.setEvaluoLiquidacion(inspeccion.getAreaConst().multiply(tipoInsp.getValor()));
            inspeccion.setEvaluoLiquidacion(inspeccion.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            tipoInsp = servicesIF.getTipoPermiso("IM");
            if (inspeccion.getEvaluoLiquidacion() != null) {
                //inspeccion.setImpuesto(inspeccion.getEvaluoLiquidacion().multiply(tipoInsp.getValor().divide(new BigDecimal(1000))));
                gutil.setProperty("avaluo", inspeccion.getEvaluoLiquidacion());
                gutil.setProperty("valorTipoIns", tipoInsp.getValor());
                gutil.getExpression("getAvaluos", null);
                inspeccion.setImpuesto(((BigDecimal) gutil.getProperty("total")).setScale(2, RoundingMode.HALF_UP));
                total = total.add(inspeccion.getImpuesto());
            }
            total = total.setScale(2, RoundingMode.HALF_UP);
            temp = ht.getValorLiquidacion();
            ht.setValorLiquidacion(total);
            services.update(ht);
            
            if (servicesIF.guardarTasaDeLiquidacionEdicionLocal(inspeccion, listaDetalle, null, lisPropietarios, null, peInspeccionCabEdificacionList)) {
                /*
                if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                    if(lisPropietarios.get(0).getEnte().getEsPersona()){
                        ss.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                    }else{
                        ss.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombreComercial());                        
                    }
                    ss.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
                }
                ss.agregarParametro("canton", "Samborondón");
                ss.agregarParametro("sector", "La Puntilla");
                ss.agregarParametro("inspeccion", inspeccion.getId());
                ss.agregarParametro("mz", datosPredio.getMz());
                ss.agregarParametro("solar", datosPredio.getSolar());
                ss.agregarParametro("nombreResponsable", inspeccion.getRespTecnico().getNombres() + " "+inspeccion.getRespTecnico().getApellidos());
                ss.agregarParametro("regProf", inspeccion.getRespTecnico().getRegProf());
                ss.agregarParametro("ciResp", inspeccion.getRespTecnico().getCiRuc());
                ss.agregarParametro("calle", "Vehicular");
                ss.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
                ss.agregarParametro("permisoConst", inspeccion.getNumPermisoConstruc());
                ss.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
                ss.agregarParametro("areaEdif", inspeccion.getAreaConst());
                ss.agregarParametro("areaSolar", inspeccion.getAreaSolar());
                ss.agregarParametro("codigoAnterior", "***************");
                ss.agregarParametro("imsadc", inspeccion.getImpuesto());
                ss.agregarParametro("revYAprobPlanos", inspeccion.getRevicion());
                ss.agregarParametro("noAdeudarMunicipio", inspeccion.getNoAdeudar());
                ss.agregarParametro("verificacionAreaEdificada", inspeccion.getAreaEdificada());
                ss.agregarParametro("totalAPagar", total.toString());
                ss.agregarParametro("codigoQR", "Tasa de liquidación editada");
                ss.agregarParametro("nombreIng", firma.getNomCompleto());
                ss.agregarParametro("cargoIng", firma.getCargo());
                if(usuario!=null &&  usuario.getEnte()!=null)
                    ss.agregarParametro("responsable", usuario.getEnte().getNombres() + " " + usuario.getEnte().getApellidos());
                else
                    ss.agregarParametro("responsable", sess.getName_user());
                ss.agregarParametro("avaluoConstruccion", inspeccion.getEvaluoLiquidacion());
                ss.agregarParametro("dia", new SimpleDateFormat("dd").format(new Date()));
                ss.agregarParametro("mes", new SimpleDateFormat("MM").format(new Date()));
                ss.agregarParametro("anio", new SimpleDateFormat("yyyy").format(new Date()));
                ss.agregarParametro("numTramiteSeq", inspeccion.getNumReporte());
                ss.agregarParametro("numTramite", ht.getId() + "-" + new SimpleDateFormat("yyyy").format(new Date()));
                ss.agregarParametro("logoImg", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));
                ss.agregarParametro("inspeccionId", inspeccion.getId());
                ss.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
                ss.setNombreReporte("LiquidacionInspeccionFinal");
                ss.setTieneDatasource(true);
                */
                
                inspeccionManager.generarDatosReporteLiquidacion(inspeccion, lisPropietarios, datosPredio, inspeccion.getRespTecnico(), ht, firma, null);
                obs = new Observaciones();
                obs.setEstado(true);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setObservacion("Se editó la tasa de liquidación de inspección final #"+inspeccion.getNumReporte()+"-"+inspeccion.getAnioInspeccion()+". El valor total a pagar anterior: " +temp+" - El valor a pagar actual: "+ht.getValorLiquidacion());
                obs.setUserCre(sess.getName_user());
                obs.setTarea("Edición de Inspección Final - panel");
                services.persist(obs);
                clickInspeccion = true;
                JsfUti.messageInfo(null, "Info", "Se generaron los cambios correctamente. Proceda a imprimir la tasa de liquidación.");
            } else {
                JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega una edificación a la lista de edificaciones de la inspección
     * final.
     */
    public void agregarEdificacion() {
        try {
            for(PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList){
                if(temp.getNumEdificacion().equals(peInspeccionCabEdificacionTemp.getNumEdificacion())){
                    JsfUti.messageInfo(null, "Info", "El número de edificación no está disponible");
                    return;
                }
            }
            if (peInspeccionCabEdificacionTemp.getAreaConstruccion() != null && peInspeccionCabEdificacionTemp.getDescEdificacion() != null && peInspeccionCabEdificacionTemp.getCantidadPisos() != null) {
                peInspeccionCabEdificacionList.add(peInspeccionCabEdificacionTemp);
                this.sumarEdificaciones();
            } else {
                JsfUti.messageError(null, "Error", "No ha ingresado toda la información de la edificación.");
                return;
            }
            peInspeccionCabEdificacionTemp = new PeInspeccionCabEdificacion();
            peInspeccionCabEdificacionTemp.setEstado(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Suma el área de construcción total del predio en base a las áreas de cada
     * una de sus edificaciones.
     *
     */
    public void sumarEdificaciones() {
        try {
            BigDecimal total = new BigDecimal(0);
            for (PeInspeccionCabEdificacion temp : peInspeccionCabEdificacionList) {
                total = total.add(temp.getAreaConstruccion());
            }
            inspeccion.setAreaConst(total);
            inspeccion.setEvaluoLiquidacion(total.multiply(tipoInsp.getValor()));
            inspeccion.setEvaluoLiquidacion(inspeccion.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            JsfUti.update("frmMain");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Agrega una característica a la lista de característica de una edificación
     * de una inspección final.
     *
     */
    public void agregarCaracteristica() {

        if (detalleInspeccion != null && categoria != null) {
            detalleInspeccion.getCaracteristica().setCategoria(categoria);
            peDetallePerIFList.add(detalleInspeccion);
        }
        categoria = new CatEdfCategProp();
        cepList = null;
        detalleInspeccion = new PeDetalleInspeccion();
        detalleInspeccion.setPorcentaje(new BigDecimal(100));
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public PeInspeccionFinal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(PeInspeccionFinal inspeccion) {
        this.inspeccion = inspeccion;
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

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
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

    public List<PeInspeccionCabEdificacion> getPeInspeccionCabEdificacionList() {
        return peInspeccionCabEdificacionList;
    }

    public void setPeInspeccionCabEdificacionList(List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList) {
        this.peInspeccionCabEdificacionList = peInspeccionCabEdificacionList;
    }

    public PeInspeccionCabEdificacion getPeInspeccionCabEdificacion() {
        return peInspeccionCabEdificacion;
    }

    public void setPeInspeccionCabEdificacion(PeInspeccionCabEdificacion peInspeccionCabEdificacion) {
        this.peInspeccionCabEdificacion = peInspeccionCabEdificacion;
    }

    public PeInspeccionCabEdificacion getPeInspeccionCabEdificacionTemp() {
        return peInspeccionCabEdificacionTemp;
    }

    public void setPeInspeccionCabEdificacionTemp(PeInspeccionCabEdificacion peInspeccionCabEdificacionTemp) {
        this.peInspeccionCabEdificacionTemp = peInspeccionCabEdificacionTemp;
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

    public List<CtlgItem> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<CtlgItem> detallesList) {
        this.detallesList = detallesList;
    }

    public List<CtlgItem> getListaDetalle() {
        return listaDetalle;
    }

    public void setListaDetalle(List<CtlgItem> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    public List<CatEdfCategProp> getCatEdfCategPropList() {
        return catEdfCategPropList;
    }

    public void setCatEdfCategPropList(List<CatEdfCategProp> catEdfCategPropList) {
        this.catEdfCategPropList = catEdfCategPropList;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        if (categoria != null) {
            cepList = (List<CatEdfProp>) services.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
        } else {
            cepList = null;
        }
        this.categoria = categoria;
    }

    public List<CatEdfProp> getCepList() {
        return cepList;
    }

    public void setCepList(List<CatEdfProp> cepList) {
        this.cepList = cepList;
    }

    public PeDetalleInspeccion getDetalleInspeccion() {
        return detalleInspeccion;
    }

    public void setDetalleInspeccion(PeDetalleInspeccion detalleInspeccion) {
        this.detalleInspeccion = detalleInspeccion;
    }

    public List<PeDetalleInspeccion> getDetalleInspeccionList() {
        return detalleInspeccionList;
    }

    public void setDetalleInspeccionList(List<PeDetalleInspeccion> detalleInspeccionList) {
        this.detalleInspeccionList = detalleInspeccionList;
    }

    public List<PeDetalleInspeccion> getPeDetallePerIFList() {
        return peDetallePerIFList;
    }

    public void setPeDetallePerIFList(List<PeDetalleInspeccion> peDetallePerIFList) {
        this.peDetallePerIFList = peDetallePerIFList;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public Boolean getClickInspeccion() {
        return clickInspeccion;
    }

    public void setClickInspeccion(Boolean clickInspeccion) {
        this.clickInspeccion = clickInspeccion;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public RealizarInspeccion getInspeccionManager() {
        return inspeccionManager;
    }

    public void setInspeccionManager(RealizarInspeccion inspeccionManager) {
        this.inspeccionManager = inspeccionManager;
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

    public String getMensajeResponsableTec() {
        return mensajeResponsableTec;
    }

    public void setMensajeResponsableTec(String mensajeResponsableTec) {
        this.mensajeResponsableTec = mensajeResponsableTec;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }
}
