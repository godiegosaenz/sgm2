/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.PePermisoLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.primefaces.event.FileUploadEvent;
import util.Archivo;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class RealizarInspeccion extends BpmManageBeanBaseRoot implements Serializable {
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @Inject
    private ServletSession servletSession;
    
    @javax.inject.Inject
    private InspeccionFinalServices servicesIF;
    
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private HistoricoTramites ht;
    private String inspeccionRealizada;
    private Boolean inspeccion = false, clickInspeccion = true, centinela = false, permisoAntiguo = false;
    private Date fechaInspeccion, fechaEmision = new Date();
    private List<Archivo> archivos = new ArrayList();
    private List<Archivo> archivos2 = new ArrayList();
    private List<Archivo> archivos3 = new ArrayList();
    private List<Archivo> archivos4 = new ArrayList();
    private List<Archivo> archivosFinal = new ArrayList();
    private HistoricoArchivo historicoArchivo;
    private Boolean sePuedeRealizarInspeccion = true;
    private String mensaje, mensajeInspeccion;
    private Boolean inspeccionEfectuada, tieneRegProf;
    private AclRol rolDir;
    private AclUser usuario, director, digitalizador;
    
    // Variables de la tasa de inspección    
    
    private HistoricoReporteTramite hrt;
    private GroovyUtil gutil;
    private MatFormulaTramite formula;
    private CatPredio datosPredio;
    private PePermisoLazy permisosList;
    private PePermiso pePermiso;
    private PeInspeccionFinal peInspeccionFinalV;
    private CatCanton canton;
    private CatParroquia parroquia;
    private List<CatPredioPropietario> lisPropietarios;
    private String codigoCatastral, cedulaRuc, cedulaRucResp;
    private List<PePermisoCabEdificacion> pePermisosIFList;
    private PeTipoPermiso tipoInsp;
    private List<CtlgItem> listaDetalle, detallesList;
    private List<PeDetallePermiso> peDetallePerIFList;
    private PePermisoCabEdificacion pePermisosCabEdifIF, pePermisosCabEdifIF2;
    private List<CatEnte> enteList;
    private HistoricoTramiteDet htd;
    private PeFirma firma;
    private PdfReporte reporte;
    private CatEnte solicitante;
    private MsgFormatoNotificacion formatoMsg;
    private PeDetallePermiso pdp;
    private CatEdfCategProp categoria;
    private List<CatEdfCategProp> catEdfCategPropList;
    private List<CatEdfProp> cepList;
    private List<HistoricoTramiteDet> htdList;
    private CatEnte responsableTec;
    private CatPredioPropietario propietario;
    private String mensajeResponsableTec;
    
    @PostConstruct
    public void init(){
        PePermisoCabEdificacion cabTemp;
        
        if (uSession.esLogueado() && uSession.getTaskID() != null ) {
            this.setTaskId(uSession.getTaskID());
            fechaInspeccion = (Date) this.getVariable(uSession.getTaskID(), "fechaInspeccion");
            
            /*if(new Date().before(fechaInspeccion)){
                sePuedeRealizarInspeccion = false;
                mensaje = "La fecha de inspección no sucede aún. Debe esperar al "+new SimpleDateFormat("dd/MM/yyyy").format(fechaInspeccion)+" para realizar la tarea.";
                //JsfUti.messageError(null, "Error", "La fecha de inspección no sucede aún. Debe esperar al "+new SimpleDateFormat("dd/MM/yyyy").format(fechaInspeccion)+" para realizar la tarea.");
                return;
            }*/
            ht = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            if(ht == null){
                JsfUti.messageError(null, "Error", "Error al encontrar el trámite.");
                return;
            }
            solicitante = ht.getSolicitante();
            htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
            obs = new Observaciones();
            paramsActiviti = new HashMap<String, Object>();     
            inspeccionEfectuada = false;
            inspeccionRealizada = "Sí";
            rolDir = (AclRol) acl.find(Querys.getAclRolByIdDirector, new String[]{"idRol"}, new Object[]{new Long(68)});
            for(AclUser temp : rolDir.getAclUserCollection()){
                if(temp.getUserIsDirector()){
                    director = temp;
                    break;
                }
            }
            
            List<ParametrosDisparador> p = permisoServices.getParametroDisparadorByTipoTramite(ht.getTipoTramite().getId());
            for (ParametrosDisparador p1 : p) {
                if ("digitalizador".equals(p1.getVarResp())) {
                    digitalizador = (AclUser) EntityBeanCopy.clone(acl.find(AclUser.class, p1.getResponsable().getId()));
                }
            }
            //digitalizador = (AclUser) acl.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariable(uSession.getTaskID(), "digitalizador")});
            
            // Parte de la generación de la tasa de liquidación
            
            htd = new HistoricoTramiteDet();
            usuario = (AclUser) acl.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
            
            tipoInsp = (PeTipoPermiso) acl.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{"INF"});
            formula = (MatFormulaTramite) acl.find(MatFormulaTramite.class, 6L);
            gutil = new GroovyUtil(formula.getFormula());
            hrt = new HistoricoReporteTramite();
            detallesList = servicesIF.obtenerCtlgItemListByNombreDeCatalogo("permiso_inspeccion.detalle");
            firma = (PeFirma) acl.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
            formatoMsg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("4")});
            permisosList = new PePermisoLazy();
            pePermisosCabEdifIF = new PePermisoCabEdificacion();
            pdp = new PeDetallePermiso();
            pdp.setPorcentaje(new BigDecimal(100));
            catEdfCategPropList = (List<CatEdfCategProp>) acl.findAll(Querys.getCatCategoriasPropConstruccionList, new String[]{}, new Object[]{});
            peInspeccionFinalV = ht.getPeInspeccionFinal();
            if(peInspeccionFinalV==null){
                peInspeccionFinalV = new PeInspeccionFinal();
                peInspeccionFinalV.setFechaInspeccion(fechaInspeccion);
            }else{
                JsfUti.messageInfo(null, "Info", "Inspección encontrada");
                for(PeInspeccionCabEdificacion temp : peInspeccionFinalV.getPeInspeccionCabEdificacionCollection()){
                    temp.setEstado(false);
                    acl.persist(temp);
                }
            }
            peInspeccionFinalV.setFechaIngreso(new Date());
            peInspeccionFinalV.setUsuarioIngreso(uSession.getName_user());
            peInspeccionFinalV.setSolicitante(solicitante);
            
        }
    }
    
    /**
     * Cuando el técnico cancela la inspección por algún inconveniente se da esta
     * opción.
     */
    public void cancelarInspeccion(){
        inspeccionRealizada = "No";
        inspeccion = false;
        archivos = null;
        archivos2 = null;
        archivos3 = null;
        archivos4 = null;
        archivosFinal = null;
    }
    
    /**
     * El técnico seleccona un PePermiso de la lista de permisos y en base al mismo
     * procede a guardar los datos del mismo.
     * 
     * @param permiso 
     */
    public void onRowSelectPermiso(PePermiso permiso){
        String pagado = null;
        HistoricoTramites htTemp = null;   
        CatEdfProp temp;
        
        try{
            pePermiso = permiso;
            buscarPredioPorPermiso();

            peInspeccionFinalV.setAreaSolar(pePermiso.getAreaSolar());
            peInspeccionFinalV.setAltura(pePermiso.getAltura());
            peInspeccionFinalV.setPisosBnb(pePermiso.getPisosBnb());
            peInspeccionFinalV.setPisosSbn(pePermiso.getPisosSnb());
            peInspeccionFinalV.setCantParqueos(BigInteger.valueOf(pePermiso.getCantidadParqueos()));
            peInspeccionFinalV.setAreaParqueos(pePermiso.getAreaParqueos());
            peInspeccionFinalV.setDescEdificacion(pePermiso.getDescFamiliar());
            peInspeccionFinalV.setCantEdificaciones(new BigDecimal(pePermiso.getCantidadEdificaciones()));
            peInspeccionFinalV.setRetiroFrontal(pePermiso.getRetiroFrontal());
            peInspeccionFinalV.setRetiroLateral1(pePermiso.getRetiroLateral1());
            peInspeccionFinalV.setRetiroLateral2(pePermiso.getRetiroLateral2());
            peInspeccionFinalV.setRetiroPosterior(pePermiso.getRetiroPosterior());
            if(permiso.getTramite()!=null){
                htTemp = (HistoricoTramites) acl.findNoProxy(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{permiso.getTramite().getId()});
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
     * 
     */
    public void buscarPredioPorPermiso(){
        CatEdfProp temp1, temp2, temp3, temp4, temp5, temp6;
        datosPredio = pePermiso.getIdPredio();
        if(datosPredio != null){
            parroquia = datosPredio.getCiudadela().getCodParroquia();
            canton = parroquia.getIdCanton();
            this.buscarPredio();
            pePermisosIFList = (List<PePermisoCabEdificacion>) pePermiso.getPePermisoCabEdificacionCollection();
            for(PePermisoCabEdificacion temp : pePermisosIFList){
                if(temp.getNumEdificacion() == 0){
                    peDetallePerIFList = (List)temp.getPeDetallePermisoCollection();
                    
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 1L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(1));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp1 = cepList.get(0);
                        pdp.setIdCatEdfProp(temp1);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 2L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(2));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp2 = cepList.get(6);
                        pdp = new PeDetallePermiso();
                        pdp.setIdCatEdfProp(temp2);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 3L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(3));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp3 = cepList.get(6);
                        pdp = new PeDetallePermiso();
                        pdp.setIdCatEdfProp(temp3);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 4L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(4));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp4 = cepList.get(6);
                        pdp = new PeDetallePermiso();
                        pdp.setIdCatEdfProp(temp4);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 5L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(5));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp5 = cepList.get(6);
                        pdp = new PeDetallePermiso();
                        pdp.setIdCatEdfProp(temp5);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                    if(!this.existePeDetallePermiso((List)temp.getPeDetallePermisoCollection(), 6L)){
                        categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(6));
                        cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
                        temp6 = cepList.get(6);
                        pdp = new PeDetallePermiso();
                        pdp.setIdCatEdfProp(temp6);
                        pdp.setPorcentaje(BigDecimal.valueOf(100));
                        peDetallePerIFList.add(pdp);
                    }
                }
            }
            this.sumarEdificaciones();
        }else{
            JsfUti.messageError(null, "Error", "No se ha encontrado ningún predio.");
            return;
        }
    }
    
    public Boolean existePeDetallePermiso(List<PeDetallePermiso> list, Long index){
        for(PeDetallePermiso temp : list){
            if(temp.getIdCatEdfProp()!=null){
                if(temp.getIdCatEdfProp().getCategoria().getId() == index){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * 
     */
    public void buscarPredio() {
        List<CatPredioPropietario> propTemp;
        
        if((CatPredio) acl.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()}) != null){
            datosPredio = (CatPredio) acl.find(Querys.getPredioByCod, new String[]{"sectorp", "mzp", "cdlap", "mzdivp", "solarp", "div1p", "div2p", "div3p", "div4p", "div5p", "div6p", "div7p", "div8p", "div9p", "phhp", "phvp"}, new Object[]{datosPredio.getSector(), datosPredio.getMz(), datosPredio.getCdla(), datosPredio.getMzdiv(), datosPredio.getSolar(), datosPredio.getDiv1(), datosPredio.getDiv2(), datosPredio.getDiv3(), datosPredio.getDiv4(), datosPredio.getDiv5(), datosPredio.getDiv6(), datosPredio.getDiv7(), datosPredio.getDiv8(), datosPredio.getDiv9(), datosPredio.getPhh(), datosPredio.getPhv()});
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
        if(lisPropietarios!=null && !lisPropietarios.isEmpty())
            propietario = lisPropietarios.get(0);
    }
    
    public void eliminarPropietario(CatPredioPropietario prop) {
        if(prop.getId()!=null){
            prop.setEstado("I");
            acl.persist(prop);
        }
        lisPropietarios.remove(prop);
    }
    
    public void setearValores() {
        cedulaRuc = "";
        enteList = new ArrayList<>();
    }
    
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
    
    public void onRowSelect(){
        peDetallePerIFList = (List<PeDetallePermiso>) pePermisosCabEdifIF2.getPeDetallePermisoCollection();
        if(peDetallePerIFList==null || peDetallePerIFList.isEmpty()){
            peDetallePerIFList = new ArrayList<>();
            pePermisosCabEdifIF2.setPeDetallePermisoCollection(peDetallePerIFList);
        }
        JsfUti.executeJS("PF('dlgMostrarDetalle').show()");
    }
    
    public void eliminarPermiso(PeDetallePermiso permiso){
        peDetallePerIFList.remove(permiso);
    }
    
    /**
     * Busca un ente a partir del número de cédula o RUC.
     */
    public void buscarEnte() {
        //CatEnte tempEnte = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        CatEnte tempEnte = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
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
    
    public void agregarCaracteristica(){
        if(pdp!=null && categoria != null){
            pdp.getIdCatEdfProp().setCategoria(categoria);
            peDetallePerIFList.add(pdp);
        }
        categoria = new CatEdfCategProp();
        pdp = new PeDetallePermiso();
        pdp.setPorcentaje(new BigDecimal(100));
        cepList = null;
    }
    
    public void eliminarInspeccion(PePermisoCabEdificacion inspeccion){
        try{
            BigDecimal total = peInspeccionFinalV.getAreaConst();
            total = total.subtract(inspeccion.getAreaConstruccion());
            peInspeccionFinalV.setAreaConst(total);
            peInspeccionFinalV.setEvaluoLiquidacion(total.multiply(tipoInsp.getValor()));
            peInspeccionFinalV.setEvaluoLiquidacion(peInspeccionFinalV.getEvaluoLiquidacion().setScale(2, RoundingMode.UP));
            pePermisosIFList.remove(inspeccion);
            JsfUti.update("frmMain:tdatos:panelInspeccion");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void buscarResponsable(){
        responsableTec = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRucResp});
        if(responsableTec==null)
            JsfUti.messageError(null, "Error", "No se encontró ningún ente con esa cédula/RUC");
        else{
            mensajeResponsableTec = "El responsable técnico es: "+responsableTec.getNombres() + " " +responsableTec.getApellidos();
            if(responsableTec.getRegProf() == null || responsableTec.getRegProf() == "")
                tieneRegProf = false;
            else
                tieneRegProf = true;
            JsfUti.update("respDlgForm");
            JsfUti.executeJS("PF('dlgResp').show()");
            htd.setResponsable(responsableTec.getNombres() + " " + responsableTec.getApellidos());
            peInspeccionFinalV.setRespTecnico(responsableTec);
        }
    }
    
    public void agregarEdificacion(){
        CatEdfProp temp1, temp2, temp3, temp4, temp5, temp6;
        for(PePermisoCabEdificacion temp : pePermisosIFList){
            if(temp.getNumEdificacion().equals(pePermisosCabEdifIF.getNumEdificacion())){
                JsfUti.messageInfo(null, "Info", "El número de edificación no está disponible");
                return;
            }
        }
        if(pePermisosCabEdifIF.getNumEdificacion() == 0){
            
            peDetallePerIFList = new ArrayList();
            
            //ESTRUCTURA
                    
            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(1));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp1 = cepList.get(0);
            pdp.setIdCatEdfProp(temp1);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            //PISO

            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(2));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp2 = cepList.get(6);
            pdp = new PeDetallePermiso();
            pdp.setIdCatEdfProp(temp2);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            //SOBREPISO

            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(3));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp3 = cepList.get(6);
            pdp = new PeDetallePermiso();
            pdp.setIdCatEdfProp(temp3);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            //PAREDES

            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(4));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp4 = cepList.get(6);
            pdp = new PeDetallePermiso();
            pdp.setIdCatEdfProp(temp4);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            //CUBIERTA

            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(5));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp5 = cepList.get(6);
            pdp = new PeDetallePermiso();
            pdp.setIdCatEdfProp(temp5);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            //TUMBADO

            categoria = (CatEdfCategProp) acl.find(CatEdfCategProp.class, new Long(6));
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
            temp6 = cepList.get(6);
            pdp = new PeDetallePermiso();
            pdp.setIdCatEdfProp(temp6);
            pdp.setPorcentaje(BigDecimal.valueOf(100));
            peDetallePerIFList.add(pdp);

            pePermisosCabEdifIF.setPeDetallePermisoCollection(peDetallePerIFList);
              
            pdp = new PeDetallePermiso();
            categoria = null;
            cepList = null;
        }
        pePermisosIFList.add(pePermisosCabEdifIF);
        this.sumarEdificaciones();
        pePermisosCabEdifIF = new PePermisoCabEdificacion();
        pePermisosCabEdifIF.setEstado(true);        
    }
    
    public void verificarCaracteristicas(){  
        PeDetallePermiso temp = null;
        try{
            if(pePermisosCabEdifIF2 != null && pePermisosCabEdifIF2.getNumEdificacion() == 0){
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
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());   
            
            if(permisoAntiguo){
                paramsActiviti.put("prioridad", 50);
                paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                paramsActiviti.put("tienepermiso", !permisoAntiguo);
                paramsActiviti.put("idTarea", this.getTaskId());

                acl.persist(obs);
                this.completeTask(this.getTaskId(), paramsActiviti);
                this.continuar();
                return;
            }
            
            if(pePermisosIFList != null){
                for(PePermisoCabEdificacion temp : pePermisosIFList){
                    if(temp.getNumEdificacion().equals(0) && temp.getPeDetallePermisoCollection().size()<6){
                        JsfUti.messageInfo(null, "Info", "La edificación principal debe tener al menos 6 características principales.");
                        return;
                    }
                }
            }
            
            reporte = new PdfReporte();
            
            String msg="";
            BigDecimal total = new BigDecimal(0);

            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
             
            if(responsableTec==null){
                JsfUti.messageError(null, "Error", "Debe ingresar el responsable técnico antes de continuar.");
                return;
            }    
            if(pePermisosIFList == null ||pePermisosIFList.isEmpty()){
                JsfUti.messageError(null, "Error", "La inspección final debe tener una edificación al menos.");
                return;
            }
            if(responsableTec==null){
                JsfUti.messageError(null, "Error", "Debe registrar Responsable Tecnico.");
                return;
            }
            if(!archivos.isEmpty() && !archivos2.isEmpty()){
                llenarListaPrincipal();
            }else{
                JsfUti.messageError(null, "Error", "Debe ingresar al menos una foto de la inspección final.");
                return;
            }
            
            /*if(vaidarEdificaciones(pePermisosIFList)){
                JsfUti.messageError(null, "Error", "Todas las edificaciones deben tener al menos una característica.");
                return;
            }
        */
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            
            ht.setNumPredio(datosPredio.getNumPredio());
            ht.setMz(datosPredio.getUrbMz());
            ht.setSolar(datosPredio.getSolar() + "");
            
            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_IF-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);         
            
            htd.setTramite(ht);
            htd.setEstado(true);
            htd.setFecCre(new Date());
            htd.setPredio(datosPredio);
            
            // Valores de PeInspeccionFinal
            peInspeccionFinalV.setAnioPermisoConstruc(BigInteger.valueOf(pePermiso.getAnioPermiso()));
            peInspeccionFinalV.setTramite(ht);
            peInspeccionFinalV.setPredio(datosPredio);
            peInspeccionFinalV.setEstado("A");
            peInspeccionFinalV.setMostrarCertificado(true);
            peInspeccionFinalV.setUsuario(usuario);
            peInspeccionFinalV.setNumPermisoConstruc(new BigInteger(pePermiso.getId().toString()));
            peInspeccionFinalV.setCalle("Vehicular");
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
            total = total.setScale(2, RoundingMode.HALF_UP);
            peInspeccionFinalV.setAnioInspeccion(BigInteger.valueOf(Long.parseLong(new SimpleDateFormat("yyyy").format(new Date()))));
            ht.setValorLiquidacion(total);
            htd.setTotal(total);
            
            hrt = (HistoricoReporteTramite) acl.persist(hrt);
            
            if((peInspeccionFinalV = servicesIF.guardarTasaDeLiquidacion(pePermiso, peInspeccionFinalV, listaDetalle, obs, htd, hrt, lisPropietarios, pePermisosIFList, peDetallePerIFList, htdList, ht))!=null){
                
                this.generarDatosReporteLiquidacion(peInspeccionFinalV, lisPropietarios, datosPredio, responsableTec, ht, firma, hrt);
                
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
    
    public Boolean vaidarEdificaciones(List<PePermisoCabEdificacion> edificaciones){
        
        for(PePermisoCabEdificacion edif : edificaciones){
            if(edif.getPeDetallePermisoCollection() == null || edif.getPeDetallePermisoCollection().isEmpty())
                return true;
        }
        
        return false;
    }
    
    public void generarDatosReporteLiquidacion(PeInspeccionFinal peInspeccionFinalV, List<CatPredioPropietario> lisPropietarios, CatPredio datosPredio, CatEnte responsableTec, HistoricoTramites ht, PeFirma firma, HistoricoReporteTramite hrt){
        try{
            servletSession.instanciarParametros();
            
            servletSession.agregarParametro("numReporte", peInspeccionFinalV.getNumReporte()+"-"+peInspeccionFinalV.getAnioInspeccion());
            
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
            /*if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                if(lisPropietarios.get(0).getEnte().getEsPersona()){
                    servletSession.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                }else{
                    servletSession.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombreComercial());                        
                }
                servletSession.agregarParametro("ciRuc", lisPropietarios.get(0).getEnte().getCiRuc());
            }*/
            servletSession.agregarParametro("canton", "Samborondón");
            servletSession.agregarParametro("observacion", peInspeccionFinalV.getDescEdificacion());
            servletSession.agregarParametro("sector", "La Puntilla");
            servletSession.agregarParametro("inspeccion", peInspeccionFinalV.getInspeccion());
            servletSession.agregarParametro("mz",datosPredio.getUrbMz());
            servletSession.agregarParametro("solar",datosPredio.getUrbSolarnew());
            servletSession.agregarParametro("nombreResponsable", responsableTec.getNombres() + " " + responsableTec.getApellidos());
            servletSession.agregarParametro("regProf", responsableTec.getRegProf());
            servletSession.agregarParametro("ciResp", responsableTec.getCiRuc());
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            servletSession.agregarParametro("permisoConst", peInspeccionFinalV.getNumPermisoConstruc());
            servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
            servletSession.agregarParametro("areaEdif", peInspeccionFinalV.getAreaConst());
            servletSession.agregarParametro("areaSolar", peInspeccionFinalV.getAreaSolar());
            servletSession.agregarParametro("codigoAnterior", datosPredio.getPredialant());
            servletSession.agregarParametro("imsadc",peInspeccionFinalV.getImpuesto());
            servletSession.agregarParametro("revYAprobPlanos",peInspeccionFinalV.getRevicion());
            servletSession.agregarParametro("noAdeudarMunicipio",peInspeccionFinalV.getNoAdeudar());
            servletSession.agregarParametro("verificacionAreaEdificada", 0);
            servletSession.agregarParametro("totalAPagar", ht.getValorLiquidacion());
            if(hrt!=null)
                servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.agregarParametro("nombreIng",firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+" "+firma.getDepartamento().toUpperCase());
            servletSession.agregarParametro("responsable", uSession.getNombrePersonaLogeada());
            servletSession.agregarParametro("avaluoConstruccion", peInspeccionFinalV.getEvaluoLiquidacion());
            servletSession.agregarParametro("dia", new SimpleDateFormat("dd").format(new Date()));
            servletSession.agregarParametro("mes", new SimpleDateFormat("MM").format(new Date()));
            servletSession.agregarParametro("anio", new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.agregarParametro("numTramiteSeq", Long.parseLong(peInspeccionFinalV.getNumReporte().toString())+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("numTramite", ht.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("logoImg", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));          
            servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
            servletSession.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
            servletSession.setNombreReporte("LiquidacionInspeccionFinal");
            servletSession.setNombreSubCarpeta("inspeccionFinal");
            servletSession.setTieneDatasource(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
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
        
        // Código de "Realizar Inspección"
        historicoArchivo = new HistoricoArchivo();

        if(archivosFinal==null)
            archivosFinal = new ArrayList<>();

        paramsActiviti.put("nomSubCarpeta", "fotosInspeccion-"+ht.getId());

        historicoArchivo.setEstado(true);
        historicoArchivo.setFechaCreacion(new Date());
        historicoArchivo.setTaskId(this.getTaskId());
        historicoArchivo.setTramite(ht);
        historicoArchivo.setProcessInstance(this.getProcessInstanceByTareaID(this.getTaskId()));
        historicoArchivo.setCarpetaContenedora(paramsActiviti.get("nomSubCarpeta").toString());

        peInspeccionFinalV = (PeInspeccionFinal) acl.find(PeInspeccionFinal.class, peInspeccionFinalV.getId());
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
            paramsActiviti.put("listaArchivos", archivosFinal);
            paramsActiviti.put("aprobado", inspeccion);
            paramsActiviti.put("digitalizador", digitalizador.getUsuario());
            paramsActiviti.put("idTarea", this.getTaskId());
            paramsActiviti.put("directorCatastro", director.getUsuario());
            paramsActiviti.put("listaArchivosFinal", new ArrayList<Archivo>());
            paramsActiviti.put("tienepermiso", !permisoAntiguo);
            
            acl.persist(historicoArchivo);
            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
        }else{
            JsfUti.messageError(null, "Error", "Hubo un problema al generar la tasa de liquidación. Refresque la página e inténtelo nuevamente.");
        }
    }
    
    public void agregarDatos(PePermisoCabEdificacion temp){
        peDetallePerIFList = (List<PeDetallePermiso>) temp.getPeDetallePermisoCollection();
        if(peDetallePerIFList==null || peDetallePerIFList.isEmpty()){
            peDetallePerIFList = new ArrayList<>();
            pePermisosCabEdifIF2.setPeDetallePermisoCollection(peDetallePerIFList);
        }
        JsfUti.executeJS("PF('dlgMostrarDetalle').show()");
    }
    
    // Métodos originales de "Realizar Inspección":
    
    public void validar(){
        if(inspeccionRealizada.equals("Sí")){
            inspeccion = true;
        }else{
            inspeccion = false;
            JsfUti.executeJS("PF('dlgObs').show()");
        }
    }
    
    public void completar(){
        try{
            if(obs == null || obs.getObservacion() == null){
                JsfUti.messageInfo(null, "Info", "Debe ingresar una observación");
                return;
            }
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName()); 
            
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("tienepermiso", true);
            paramsActiviti.put("aprobado", false);
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            paramsActiviti.put("idTarea", this.getTaskId());

            acl.persist(obs);
            this.completeTask(this.getTaskId(), paramsActiviti);
            this.continuar();
            return;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void validarArchivos(){
        if(!archivos.isEmpty() && !archivos2.isEmpty()){
            llenarListaPrincipal();
            JsfUti.executeJS("PF('dlgObs').show()");
        }else{
            JsfUti.messageError(null, "Error", "Debe ingresar al menos 1 archivo en todos los campos.");
        }
    }
    
    public void eliminarArchivo1(Archivo file){
        archivos.remove(file);
    }
    
    public void eliminarArchivo2(Archivo file){
        archivos2.remove(file);
    }
    
    public void eliminarArchivo3(Archivo file){
        archivos3.remove(file);
    }
    
    public void eliminarArchivo4(Archivo file){
        archivos4.remove(file);
    }
    
    public void handleFileUpload1(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + event.getFile().getFileName());
            documento.setDescripcion("tipo1");
            documento.setNombre("Fachada_Posterior"+(this.archivos.size()+1));
            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);
            this.archivos.add(documento);
                    
        } catch (IOException e) {
            Logger.getLogger(BpmManageBeanBase.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void handleFileUpload2(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + event.getFile().getFileName());
            documento.setDescripcion("tipo2");
            documento.setNombre("Fachada_Frontal"+(this.archivos2.size()+1));
            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);
            this.archivos2.add(documento);
                    
        } catch (IOException e) {
            Logger.getLogger(BpmManageBeanBase.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void handleFileUpload3(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + event.getFile().getFileName());
            documento.setDescripcion("tipo3");
            documento.setNombre("Fachada_Izquierda"+(this.archivos3.size()+1));
            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);
            this.archivos3.add(documento);
            JsfUti.update("frmMain:tdatos:panel3");
                    
        } catch (IOException e) {
            Logger.getLogger(BpmManageBeanBase.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void handleFileUpload4(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            File file = new File(rutaArchivo);
            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            Archivo documento = new Archivo();
            documento.setNombre(d.getTime() + event.getFile().getFileName());
            documento.setDescripcion("tipo4");
            documento.setNombre("Fachada_Derecha"+(this.archivos4.size()+1));
            documento.setTipo(event.getFile().getContentType());
            documento.setRuta(rutaArchivo);
            this.archivos4.add(documento);
            JsfUti.update("frmMain:tdatos:panel4");
                    
        } catch (IOException e) {
            Logger.getLogger(BpmManageBeanBase.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void llenarListaPrincipal(){
        for(Archivo arc : archivos){
            archivosFinal.add(arc);
        }
        for(Archivo arc : archivos2){
            archivosFinal.add(arc);
        }
        for(Archivo arc : archivos3){
            archivosFinal.add(arc);
        }
        for(Archivo arc : archivos4){
            archivosFinal.add(arc);
        }
    }
    
    public void camibarMensajeInspeccion(){
        if(inspeccionEfectuada){
            mensajeInspeccion = "Ingrese sus bservaciones.";
            JsfUti.messageInfo(null, "Info", "Se pudo realizar la inspeccion con éxito");
        }else{
            mensajeInspeccion = "Ingrese los motivos por el que no se efectuó la inspección.";
            JsfUti.messageError(null, "Info", "No se pudo realizar la inspección.");
        }
    }
    
    public void actualizarTecnico(){
        try{
            if(responsableTec.getRegProf() !=null){
                if(!tieneRegProf){
                    acl.persist(responsableTec);
                    JsfUti.messageInfo(null, "Info", "Se ha actualizado el técnico");
                }
                JsfUti.executeJS("PF('dlgResp').hide()");
            }else{
                JsfUti.messageInfo(null, "Info", "Debe ingresar el registro profesional del técnico");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
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

    public String getInspeccionRealizada() {
        return inspeccionRealizada;
    }

    public void setInspeccionRealizada(String inspeccionRealizada) {
        this.inspeccionRealizada = inspeccionRealizada;
    }

    public Boolean getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(Boolean inspeccion) {
        this.inspeccion = inspeccion;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public List<Archivo> getArchivos2() {
        return archivos2;
    }

    public void setArchivos2(List<Archivo> archivos2) {
        this.archivos2 = archivos2;
    }

    public List<Archivo> getArchivos3() {
        return archivos3;
    }

    public void setArchivos3(List<Archivo> archivos3) {
        this.archivos3 = archivos3;
    }

    public List<Archivo> getArchivos4() {
        return archivos4;
    }

    public void setArchivos4(List<Archivo> archivos4) {
        this.archivos4 = archivos4;
    }

    public Boolean getSePuedeRealizarInspeccion() {
        return sePuedeRealizarInspeccion;
    }

    public void setSePuedeRealizarInspeccion(Boolean sePuedeRealizarInspeccion) {
        this.sePuedeRealizarInspeccion = sePuedeRealizarInspeccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(Date fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public Boolean getInspeccionEfectuada() {
        return inspeccionEfectuada;
    }

    public void setInspeccionEfectuada(Boolean inspeccionEfectuada) {
        this.inspeccionEfectuada = inspeccionEfectuada;
    }

    public String getMensajeInspeccion() {
        return mensajeInspeccion;
    }

    public void setMensajeInspeccion(String mensajeInspeccion) {
        this.mensajeInspeccion = mensajeInspeccion;
    }

    public CatPredio getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(CatPredio datosPredio) {
        this.datosPredio = datosPredio;
    }

    public PePermisoLazy getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(PePermisoLazy permisosList) {
        this.permisosList = permisosList;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public PeInspeccionFinal getPeInspeccionFinalV() {
        return peInspeccionFinalV;
    }

    public void setPeInspeccionFinalV(PeInspeccionFinal peInspeccionFinalV) {
        this.peInspeccionFinalV = peInspeccionFinalV;
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

    public List<PePermisoCabEdificacion> getPePermisosIFList() {
        return pePermisosIFList;
    }

    public void setPePermisosIFList(List<PePermisoCabEdificacion> pePermisosIFList) {
        this.pePermisosIFList = pePermisosIFList;
    }

    public List<CtlgItem> getListaDetalle() {
        return listaDetalle;
    }

    public void setListaDetalle(List<CtlgItem> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    public List<CtlgItem> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<CtlgItem> detallesList) {
        this.detallesList = detallesList;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public List<PeDetallePermiso> getPeDetallePerIFList() {
        return peDetallePerIFList;
    }

    public void setPeDetallePerIFList(List<PeDetallePermiso> peDetallePerIFList) {
        this.peDetallePerIFList = peDetallePerIFList;
    }

    public PePermisoCabEdificacion getPePermisosCabEdifIF2() {
        return pePermisosCabEdifIF2;
    }

    public void setPePermisosCabEdifIF2(PePermisoCabEdificacion pePermisosCabEdifIF2) {
        this.pePermisosCabEdifIF2 = pePermisosCabEdifIF2;
    }

    public PePermisoCabEdificacion getPePermisosCabEdifIF() {
        return pePermisosCabEdifIF;
    }

    public void setPePermisosCabEdifIF(PePermisoCabEdificacion pePermisosCabEdifIF) {
        this.pePermisosCabEdifIF = pePermisosCabEdifIF;
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

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public PeDetallePermiso getPdp() {
        return pdp;
    }

    public void setPdp(PeDetallePermiso pdp) {
        this.pdp = pdp;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        if(categoria!=null)
            cepList = (List<CatEdfProp>) acl.findAll(Querys.getCatEdfPropListByCategoria, new String[]{"categ"}, new Object[]{categoria});
        else
            cepList = null;
        this.categoria = categoria;
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

    public Boolean getPermisoAntiguo() {
        return permisoAntiguo;
    }

    public void setPermisoAntiguo(Boolean permisoAntiguo) {
        this.permisoAntiguo = permisoAntiguo;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }    
}
