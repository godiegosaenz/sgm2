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
import com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion.GenerarLiquidacionPC;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Hibernate;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.GroovyUtil;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class PermisoConstruccionAntiguo extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;

    @Inject
    private ServletSession servletSession;
    
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;
    
    private Boolean imprimir = false, continuarb= false;
    private Boolean guardado = true;
    private Boolean banTarea = false;
    private Boolean confirmarPermiso, mostrarPanelInicial;
    private Boolean impresoLiquidacion = false;
    private PdfReporte reporte = new PdfReporte();
    private HashMap<String, Object> paramt;
    private Observaciones obs = null;
    private String numReporte, anio, nombreTipoPermiso;
    private PePermiso permisoNuevo;
    private String nombreTec;
    private Long idPermiso;
    private HistoricoReporteTramite hrt;
    protected CatEnte respTec, respTecNuevo;
    protected CatPredio predio;
    protected PePermisoCabEdificacion nuevDetalleEdif;
    protected PePermisoCabEdificacion permisoSelect;
    protected CatEdfProp carateristicas;
    protected PeDetallePermiso detallePermiso;
    protected List<CatEdfCategProp> listCat;
    protected List<PeTipoPermiso> listRequisTra;
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<PePermisoCabEdificacion> detallesEdific;
    protected List<CatEdfProp> lisCatEdfProp = new ArrayList<>();
    protected List<PeDetallePermiso> lisPeDetallePermisos = new ArrayList<>();
    protected CatEnteLazy enteLazy;
    protected HistoricoTramites ht;
    protected GeTipoTramite tipoTramite;
    protected CatEdfCategProp categoria;
    private Boolean codUrban;
    private CatPredioLazy predioLazy;
    private Short numEdificacion;
    
    protected MatFormulaTramite formulas;
    protected GroovyUtil groovyUtil;
    
    @PostConstruct
    public void initView() {
        if (uSession.esLogueado() && uSession.getTaskID() != null) {
            try {
                obs = new Observaciones();
                predio = new CatPredio();
                hrt = new HistoricoReporteTramite();
                nuevDetalleEdif = new PePermisoCabEdificacion();
                detallesEdific = new ArrayList<>();
                respTec = new CatEnte();
                detallePermiso = new PeDetallePermiso();
                listaPropietarios = new ArrayList<>();
                this.setTaskId(uSession.getTaskID());
                confirmarPermiso = true;
                mostrarPanelInicial = true;
                Calendar cl = Calendar.getInstance();
                ht = permisoService.getHistoricoTramiteById(Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString()));
                tipoTramite = ht.getTipoTramite();
                listRequisTra = (List<PeTipoPermiso>) EntityBeanCopy.clone(permisoService.getPeTipoPermisoByCodigo());
                listCat = permisoService.getCatEdfCategPropList();

                //predio.setNumPredio(ht.getNumPredio());
                permisoNuevo = new PePermiso();
                idPermiso = (Long)this.getVariable(uSession.getTaskID(), "idPermiso");
                if(idPermiso != null){                    
                    permisoNuevo = (PePermiso)services.find(PePermiso.class, idPermiso);
                    predio = permisoNuevo.getIdPredio();
                    respTec = permisoNuevo.getResponsablePersona();
                    detallesEdific = (List<PePermisoCabEdificacion>)permisoNuevo.getPePermisoCabEdificacionCollection();
                    nombreTec = permisoNuevo.getResponsable();
                    nombreTipoPermiso = (String)this.getVariable(uSession.getTaskID(), "tipo_PC");
                    continuarb = true;
                    numReporte = ""+permisoNuevo.getNumReporte();
                    anio = ""+permisoNuevo.getAnioPermiso();
                    JsfUti.messageInfo(null, "Info", "Permiso rechazado. Proceda a editarlo y guardarlo");
                    JsfUti.messageInfo(null, "Info", "Número de reporte: "+permisoNuevo.getNumReporte()+"\n Año de permiso: " +permisoNuevo.getAnioPermiso());
                    JsfUti.update("frmMain");
                }else{
                    permisoNuevo.setFechaEmision(cl.getTime());
                    permisoNuevo.setAnioTramite(Short.parseShort(Integer.toString(cl.get(Calendar.YEAR))));
                    permisoNuevo.setTramite(ht);
                    permisoNuevo.setCalle("Vehicular");
                    permisoNuevo.setAreaEdificaciones(BigDecimal.ZERO);
                    permisoNuevo.setEsMacroLote(false);
                    permisoNuevo.setDescFamiliar("1 RESIDENCIA UNIFAMILIAR");                    
                }
                
                if (ht.getIdProceso() != null) {
                    permisoNuevo.setIdprocess(new BigInteger(ht.getIdProceso()));
                }
                
                if (predio.getNumPredio() != null) {
                    consultarNumPredio();
                } else {
                    consultarIdPredio();
                }

                cl.add(Calendar.YEAR, 1);
                permisoNuevo.setFechaCaducidad(cl.getTime());
                predioLazy = new CatPredioLazy();
                
                formulas = formulas = permisoService.getMatFormulaTramite(2L);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void validarIngresoDePermiso(){
        if(!confirmarPermiso){
            JsfUti.update("frmObs");
            JsfUti.executeJS("PF('dlgObs').show()");
        }else{
            mostrarPanelInicial = false;
            JsfUti.update("frmMain");
        }
    }
    
    public void consultarNumPredio() {
        try {
            if (predio.getNumPredio() == null) {
                JsfUti.messageError(null, "Debe Ingresar el Número de Predio", "");
                return;
            }
            CatPredio pred1 = fichaServices.getPredioByNum(predio.getNumPredio().longValue());
            if (pred1 == null) {
                JsfUti.messageError(null, "No hay registro con el Número de predio ingresado", "");
                return;
            }
            predio = pred1;
            agregarAListProp((List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection());
            permisoNuevo.setIdPredio(predio);
            permisoNuevo.setUrbmz(predio.getUrbMz());
            permisoNuevo.setUrbsolar(predio.getUrbSolarnew());
            if (predio.getCiudadela() != null) {
                permisoNuevo.setNombUrbanizacionImpresa(predio.getCiudadela().getNombre());
            } else {
                permisoNuevo.setNombUrbanizacionImpresa(null);
            }
            ht.setNumPredio(predio.getNumPredio());
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private void agregarAListProp(List<CatPredioPropietario> propietarios) {
        if (!propietarios.isEmpty()) {
            listaPropietarios = new ArrayList<>();
            for (CatPredioPropietario temp : propietarios) {
                if ("A".equals(temp.getEstado())) {
                    listaPropietarios.add(temp);
                }
            }
        }
    }
    
    public void validar() {
        
        if (permisoNuevo.getIdPredio().getId() == null) {
            JsfUti.messageError(null, "Debe Buscar el predio", "");
            return;
        }
        if (permisoNuevo.getResponsablePersona() == null) {
            JsfUti.messageError(null, "Falta Seleccionar al Responsable Tecnico", "");
            return;
        }
        if (permisoNuevo.getDescFamiliar() == null) {
            JsfUti.messageError(null, "Debe llenar Descripcion de Edificacion. por 1 RESIDENCIA UNIFAMILIAR o 1 RESIDENCIA MULTIFAMILIAR o 1 RESIDENCIA BIFAMILIAR", "");
            return;
        }
        if (permisoNuevo.getFechaEmision() == null && permisoNuevo.getFechaCaducidad() == null && permisoNuevo.getTramite() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos en la sección Datos de Permiso", "");
            return;
        }
        if (permisoNuevo.getRetiroLateral1() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro lateral 1", "");
            return;
        }
        if (permisoNuevo.getRetiroLateral2() == null) {
            JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro Lateral 2", "");
            return;
        }
        JsfUti.executeJS("PF('dlgObs').show()");
    }
    
    public void validarIngreso(){
        try{
            Long idPermiso;
            Integer anio = Utils.getAnio(new Date());
            if(Integer.parseInt(this.anio)>=anio){
                JsfUti.messageInfo(null, "Info", "Debe ingresar un año menor al actual");
                return;
            }
            idPermiso = (Long)services.find(Querys.getPePermisoAntiguoByEstado, new String[]{"numReporte", "anio", "estado"}, new Object[]{Long.parseLong(this.numReporte), Integer.parseInt(this.anio), "A"});
            if(idPermiso == null){
                continuarb = true;
                JsfUti.messageInfo(null, "Info", "Puede proceder a crear el permiso");
                idPermiso = (Long)services.find(Querys.getPePermisoAntiguoByEstado, new String[]{"numReporte", "anio", "estado"}, new Object[]{Long.parseLong(this.numReporte), Integer.parseInt(this.anio), "I"});
                if(idPermiso!=null){
                    JsfUti.messageInfo(null, "Info", "Se encontró un permiso denegado. Proceda a editarlo");
                    permisoNuevo = (PePermiso)services.find(PePermiso.class, idPermiso);
                    predio = permisoNuevo.getIdPredio();
                    consultarNumPredio();
                    respTec = permisoNuevo.getResponsablePersona();
                    detallesEdific = (List<PePermisoCabEdificacion>)permisoNuevo.getPePermisoCabEdificacionCollection();
                    nombreTec = permisoNuevo.getResponsable();
                    nombreTipoPermiso = (String)this.getVariable(uSession.getTaskID(), "tipo_PC");
                }
            }
            else{
                continuarb = false;
                JsfUti.messageInfo(null, "Info", "No se puede crear el permiso, porque ya ha sido creado");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminarEdif(PePermisoCabEdificacion edf) {
        try {
            permisoSelect = edf;
            if(permisoSelect.getId() != null){
                permisoSelect.setEstado(false);
                services.update(permisoSelect);
            }
            int index = 0;
            int i = 0;
            for (PePermisoCabEdificacion list : detallesEdific) {
                if (list.getNumEdificacion().compareTo(edf.getNumEdificacion()) == 0) {
                    index = i;
                }
                i++;
            }
            detallesEdific.remove(index);
            if (permisoSelect.getPeDetallePermisoCollection() != null) {
                permisoSelect = null;
            }
            calculoAreaConstruccion();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("frmMain:tdatos:ad");
        JsfUti.update("frmMain:tdatos:ac");
        JsfUti.update("frmMain:tdatos:dtEdif");
        JsfUti.update("frmMain:tdatos:dtCartEdif");
    }
    
    public void eliminarDetalleEdif(PeDetallePermiso dpermiso) {
        try {
            List<PeDetallePermiso> d = new ArrayList<>();
            for (PeDetallePermiso listCat1 : permisoSelect.getPeDetallePermisoCollection()) {
                if (listCat1.getIdCatEdfProp().getId().compareTo(dpermiso.getIdCatEdfProp().getId()) != 0) {
                    d.add(listCat1);
                }
            }
            if(dpermiso.getId() != null){
                dpermiso.setEstado(false);
                services.update(dpermiso);
            }
            permisoSelect.setPeDetallePermisoCollection(new ArrayList<PeDetallePermiso>());
            permisoSelect.getPeDetallePermisoCollection().addAll((Collection<PeDetallePermiso>) d);
            permisoSelect.getPeDetallePermisoCollection().size();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("frmMain:tdatos:dtCartEdif");
    }
    
    public void onRowSelect(SelectEvent event) {
        lisPeDetallePermisos = (List<PeDetallePermiso>) permisoSelect.getPeDetallePermisoCollection();
        JsfUti.update("frmMain:tdatos:dtCartEdif");
    }
    
    public void imprimirLiquidacion(Long idPermiso) {
        if (!permisoNuevo.getMostrarCertificado()) {
            try {
                paramt = new HashMap<>();
                this.validar();
                Calendar cl = Calendar.getInstance();

                HistoricoReporteTramite vd = new HistoricoReporteTramite();
                vd.setCodValidacion(permisoNuevo.getNumReporte() + ht.getIdProceso());
                vd.setEstado(true);
                vd.setFecCre(cl.getTime());
                vd.setNombreReporte("LiquidacionTasasPermiso" + ht.getSolicitante().getCiRuc());
                vd.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                vd.setProceso(ht.getIdProceso());
                vd.setSecuencia(permisoNuevo.getNumReporte());
                vd.setTramite(ht);
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                vd = (HistoricoReporteTramite) services.persist(vd);

                AclUser firmaDirector = permisoService.getAclUserByUser(tipoTramite.getUserDireccion());
                AclUser firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);

                if (vd != null) {
                    String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + vd.getCodValidacion();
                    servletSession.instanciarParametros();
                    servletSession.agregarParametro("permiso", permisoNuevo.getId());
                    servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
                    servletSession.agregarParametro("SUBREPORT_DIR", path + "reportes/permisoConstruccion/");
                    servletSession.agregarParametro("firmaDirec", path + "css/firmas/" + firmaDir.getRutaImagen() + ".jpg");
                    servletSession.agregarParametro("firmaTecni", path + "css/firmas/" + new AclUser(uSession.getUserId()).getRutaImagen() + ".jpg");
                    servletSession.agregarParametro("idUser", uSession.getUserId());
                    servletSession.agregarParametro("validador", vd.getId().toString());
                    servletSession.agregarParametro("codigoQR", codigoQR);
                    servletSession.setNombreReporte("LiquidacionTasasPermiso");
                    servletSession.setTieneDatasource(true);
                    //paramt.put("idReporte", vd.getId());
                    servletSession.setNombreSubCarpeta("permisoConstruccion");
                }

                servletSession.setReportePDF(reporte.generarPdf("reportes/permisoConstruccion/LiquidacionTasasPermiso.jasper", servletSession.getParametros()));

                paramt.put("prioridad", 50);
                paramt.put("archivo", servletSession.getReportePDF());
                paramt.put("carpeta", ht.getCarpetaRep());
                paramt.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
                paramt.put("tipoArchivoByteArray", "application/pdf");
                agregarCartaAdosamieto();
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("cancelar_permiso", false);
                paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                paramt.put("to", ht.getCorreos());
                paramt.put("from", SisVars.correo);
                paramt.put("idProcess", uSession.getTaskID());
                paramt.put("tipo_PC", nombreTipoPermiso);
                paramt.put("idPermiso", idPermiso);
                permisoNuevo.setMostrarCertificado(true);

                if (!impresoLiquidacion) {
                    if (permisoService.actualizarPePermiso(permisoNuevo)) {
                        this.completeTask(this.getTaskId(), paramt);
                    }
                }
                impresoLiquidacion = true;
                banTarea = true;
                imprimir = false;
            } catch (Exception e) {
                Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        //JsfUti.update("frmMain:tdatos");
        JsfUti.executeJS("PF('dlgObs').hide()");
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        
    }
    
    public void guardarPermiso() {        
        if (obs == null || obs.getObservacion().trim().length() <= 0) {
            JsfUti.messageError(null, "La Observacion es Obligatorio", "");
            return;
        }
        if (this.getTaskDataByTaskID() == null) {
            JsfUti.messageInfo(null, "Info", "El permiso ya fue guardado");
            return;
        }
        
        
        if(!confirmarPermiso){     
            try{
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName()); 
                
                services.persist(obs);
                
                paramt = new HashMap<>();
                paramt.put("cancelar_permiso", true);
                paramt.put("prioridad", 50);
                paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                this.completeTask(this.getTaskId(), paramt);        
                this.continuar();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            permisoService.actualizarHistoricoTramites(ht);
            ht = permisoService.getHistoricoTramiteById(ht.getId());
            try {
    //            if (!esRenovacion) {
    //            }
                if (permisoNuevo.getIdPredio().getId() == null) {
                    JsfUti.messageError(null, "Debe Buscar el predio", "");
                    return;
                }
                if (permisoNuevo.getResponsablePersona() == null) {
                    JsfUti.messageError(null, "Falta Seleccionar al Responsable Tecnico", "");
                    return;
                }
                if (permisoNuevo.getDescFamiliar() == null) {
                    JsfUti.messageError(null, "Debe llenar Descripcion de Edificacion. por 1 RESIDENCIA UNIFAMILIAR o 1 RESIDENCIA MULTIFAMILIAR o 1 RESIDENCIA BIFAMILIAR", "");
                    return;
                }
                if (permisoNuevo.getFechaEmision() == null && permisoNuevo.getFechaCaducidad() == null && permisoNuevo.getTramite() == null) {
                    JsfUti.messageError(null, "Faltan llenar algunos campos en la sección Datos de Permiso", "");
                    return;
                }
                if (permisoNuevo.getRetiroLateral1() == null) {
                    JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro lateral 1", "");
                    return;
                }
                if (permisoNuevo.getRetiroLateral2() == null) {
                    JsfUti.messageError(null, "Faltan llenar algunos campos: Retiro Lateral 2", "");
                    return;
                }
                if (listaPropietarios.isEmpty()) {
                    JsfUti.messageError(null, "Debe Seleccionar un Propietario", "");
                    return;
                }
    //            if (permisoNuevo.getObservacion() == null) {
    //                JsfUti.messageError(null, "La Observacion es Obligatorio", "");
    //                return;
    //            }
                if (predio.getCatPredioS4() == null) {
                    JsfUti.messageError(null, "Predio sin datos de Caracteristicas Fisicas del Solar, Editar Predio", "");
                    return;
                }
                if (predio.getCatPredioS4().getLocManzana() == null) {
                    JsfUti.messageError(null, "Predio no tiene Localización en Manzana", "");
                    return;
                }
                PeTipoPermiso tipoPermiso = permisoService.getPeTipoPermisoByDesc(nombreTipoPermiso);
                if (tipoPermiso == null) {
                    JsfUti.messageError(null, "Debe seleccionar el tipo de Permiso.", "");
                    return;
                }

                obs = permisoService.guardarActualizarObservacionProp(null, listaPropietarios,
                        ht, uSession.getName_user(), obs.getObservacion(), this.getTaskDataByTaskID().getName());
                if (obs != null) {

                    ht = permisoService.getHistoricoTramiteById(ht.getId());

                    if (permisoNuevo.getPropietarioPersona() == null) {
                        permisoNuevo.setPropietarioPersona(listaPropietarios.get(0).getEnte());
                    }
                    permisoNuevo.setEstado("I");
                    permisoNuevo.setCedulaResponsableTecnico(respTec.getCiRuc());
                    permisoNuevo.setIdPredio(predio);
                    permisoNuevo.setTipoPermiso(tipoPermiso);
                    permisoNuevo.setUsuarioCreador(new AclUser(uSession.getUserId()));
                    permisoNuevo.setFechaCreacion(new Date());
                    permisoNuevo.setResponsable(nombreTec);
                    permisoNuevo.setTecnicoRegistroProfesional(respTec.getRegProf());
                    permisoNuevo.setMostrarCertificado(true);

                    Calendar c = Calendar.getInstance();
                    c.setTime(permisoNuevo.getFechaEmision());
                    permisoNuevo.setMostrarCertificado(false);
                    permisoNuevo.setTramite(ht);
                    permisoNuevo.setCartaAdosamiento(Utils.isNotEmpty(this.getCartaAdosamiento()));
                    permisoNuevo.setNumReporte(BigInteger.valueOf(Long.parseLong(numReporte)));
                    permisoNuevo.setAnioPermiso(Short.parseShort(anio));
                    PePermiso p = permisoService.guardarPePermisoInspeccion(permisoNuevo, formulas);
                    if (p != null) {
                        permisoNuevo = p;
                        permisoService.guardarPePermisoCabEdificacionAndPeDetallePermiso(p, detallesEdific, permisoSelect);

                        groovyUtil = new GroovyUtil(formulas.getFormula());
                        groovyUtil.setProperty("permiso", permisoNuevo);
                        ht.setValorLiquidacion(((BigDecimal) groovyUtil.getExpression("getValorLiquidacion", new Object[]{})));

                        if (services.update(ht)) {
                            Hibernate.initialize(ht);
                            imprimir = true;
                        } else {
                            JsfUti.messageError(null, Messages.transacError, "");
                        }
                    } else {
                        JsfUti.messageError(null, Messages.error, "");
                        return;
                    }
                }
                guardado = false;            
                imprimirLiquidacion(permisoNuevo.getId());
            } catch (Exception ex) {
                Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void consultarIdPredio() {
        if (!ht.getHistoricoTramiteDetCollection().isEmpty()) {
            List<HistoricoTramiteDet> htd = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
            HistoricoTramiteDet histdet = htd.get(0);
            predio = permisoService.getCatPredioById(histdet.getPredio().getId());
        }
        List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        if (propietariosTemp != null) {
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equals(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
        }
        permisoNuevo.setIdPredio(predio);
        if (predio.getCiudadela() != null) {
            permisoNuevo.setNombUrbanizacionImpresa(predio.getCiudadela().getNombre());
        } else {
            permisoNuevo.setNombUrbanizacionImpresa(null);
        }
    }
    
    public void guardarPermisoAntiguo(){
        BigDecimal total = new BigDecimal(0);
        BigDecimal areaTotal = BigDecimal.ZERO, avaluo;
        
        try{
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar al menos una obsación antes de continuarb.");
                return;
            }
            
            servletSession.instanciarParametros();
            hrt.setFecCre(new Date());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("TasaLiq_PC-" + ht.getSolicitante().getCiRuc());
            hrt.setEstado(Boolean.TRUE);
            
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));            
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
                
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void agregarDetalleEdif() {
        Boolean detPer = true;
        if (permisoSelect == null) {
            JsfUti.messageError(null, "Debe Seleccionar una Edificación", "");
            return;
        }
        if (carateristicas == null && categoria == null) {
            JsfUti.messageError(null, "Debe Seleccionar las caracteristicas", "");
            return;
        }
        for (PeDetallePermiso dp : permisoSelect.getPeDetallePermisoCollection()) {
            if (dp.getIdCatEdfProp().getId().compareTo(carateristicas.getId()) == 0) {
                detPer = false;
            }
        }
        try {
            if (detPer) {
                detallePermiso.setIdCatEdfProp(carateristicas);
                detallePermiso.setIdPermisoEdificacion(permisoSelect);
                PeDetallePermiso p = new PeDetallePermiso();
                p.setArea(detallePermiso.getArea());
                p.setIdCatEdfProp(detallePermiso.getIdCatEdfProp());
                p.setIdPermisoEdificacion(permisoSelect);
                p.setPorcentaje(detallePermiso.getPorcentaje());
                p.setEstado(true);
                permisoSelect.getPeDetallePermisoCollection().add(p);
//            lisPeDetallePermisos.add(p);
                carateristicas = new CatEdfProp();
                categoria = new CatEdfCategProp();
                detallePermiso = new PeDetallePermiso();
                detallePermiso.setPorcentaje(new BigDecimal("0.00"));
            } else {
                JsfUti.messageError(null, "Caracteristica ya fue agregada", "");
            }
            calculoAreaConstruccion();
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("frmMain:tdatos:lisCartg");
        JsfUti.update("frmMain:tdatos:lisCaract");
        JsfUti.update("frmMain:tdatos:por");
        JsfUti.update("frmMain:tdatos:dtCartEdif");
    }
    
    public void agregarEdifi() {
        Boolean edf = true;
//        if (!Utils.validateNumberPattern(nuevDetalleEdif.getNumeroPisos().toString())) {
//            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
//            return;
//        }
        if (!Utils.validateNumberPattern(numEdificacion.toString())) {
            JsfUti.messageInfo(null, "Solo Debe ingresar Números", "");
            return;
        }
        if (nuevDetalleEdif.getNumeroPisos() != null) {
            if (!Utils.validateNumberPattern(nuevDetalleEdif.getNumeroPisos().toString())) {
                JsfUti.messageInfo(null, "Debe ingresar solo Números", "");
                return;
            }
        }
        if (numEdificacion != null) {
            try {
                nuevDetalleEdif.setNumEdificacion(numEdificacion);
                if (numEdificacion == 0) {
                    nuevDetalleEdif.setDescripcion("Edificacion Principal");
                } else {
                    nuevDetalleEdif.setDescripcion("Anexo " + numEdificacion);
                }
                for (PePermisoCabEdificacion list : detallesEdific) {
                    if (list.getNumEdificacion().compareTo(numEdificacion) == 0) {
                        edf = false;
                    }
                }
                if (edf) {
                    nuevDetalleEdif.setIdPermiso(permisoNuevo);
                    detallesEdific.add(nuevDetalleEdif);
                    nuevDetalleEdif = new PePermisoCabEdificacion();
                    detallePermiso.setPorcentaje(new BigDecimal("0.00"));
                } else {
                    JsfUti.messageError(null, "Edificacion Ya fue agregada", "");
                }
                numEdificacion = null;
                calculoAreaConstruccion();
                JsfUti.update("frmMain:tdatos:dtEdif");//dtEdif, edif
                JsfUti.update("frmMain:tdatos:ad");
                JsfUti.update("frmMain:tdatos:ac");
            } catch (Exception e) {
                Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    public void calculoAreaConstruccion() {
        try {
            BigDecimal totArea = new BigDecimal(0);
            for (PePermisoCabEdificacion det : detallesEdific) {
                totArea = totArea.add(det.getAreaConstruccion());
            }
            permisoNuevo.setAreaEdificaciones(totArea);
            if (permisoNuevo.getAreaParqueos() != null) {
                totArea = totArea.add((permisoNuevo.getAreaParqueos()));
            }
            permisoNuevo.setAreaConstruccion(totArea);
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("frmMain:tdatos:ac");
    }
    
    public void buscarTecnico() {
        try {
            if (respTec.getCiRuc() == null) {
                JsfUti.messageError(null, "Debe Ingresar Número de Cédula", "");
                return;
            }
            CatEnte ente = permisoService.getCatEnteByCiRucByEsPersona(respTec.getCiRuc(), true);
            if (ente != null) {
                nombreTec = Utils.isEmpty(ente.getTituloProf()) + " " + Utils.isEmpty(ente.getApellidos()) + " " + (ente.getNombres());
                respTec = ente;
                permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
                this.validarRegProf(ente);
                JsfUti.update("frmMain:tdatos:resT");
                JsfUti.update("frmMain:tdatos:regP");
            } else {
                respTecNuevo = new CatEnte();
                respTecNuevo.setCiRuc(respTec.getCiRuc());
                JsfUti.executeJS("PF('dlgNuevRespTec').show()");
            }
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }

    }
    
    public void editarResTecn() {
        if (respTec == null) {
            JsfUti.messageError(null, "No hay seleccionado ningún Responzable Técnico", nombreTec);
            return;
        }
        fichaServices.actualizarCatEnteTelefEmails(respTec, null, null);
        JsfUti.update("frmMain:tdatos:resT");
        JsfUti.update("frmMain:tdatos:regP");
        JsfUti.update("frmMain:tdatos:ciIde");
        JsfUti.executeJS("PF('dlgEditRespTec').hide()");
    }
    
    public void guardarResTecn() {
        try {
            if (!Utils.validateCCRuc(respTecNuevo.getCiRuc())) {
                JsfUti.messageError(null, "", "Número de documento es invalido.");
                return;
            }
            if (respTecNuevo.getCiRuc() != null && respTecNuevo.getApellidos() != null && respTecNuevo.getNombres() != null) {
                respTecNuevo.setEsPersona(true);
                respTecNuevo.setEstado("A");
                respTec = new CatEnte();
                if (fichaServices.getCatEnte(respTecNuevo.getCiRuc()) == null) {
                    respTec = fichaServices.guardarCatEnte(respTecNuevo);
                } else {
                    JsfUti.messageError(null, nombreTec, "Ya existe un cliente registrado con el mismo número de cédula");
                }
            } else {
                respTecNuevo.setEstado("A");
                fichaServices.guardarCatEnte(respTec);
            }
            permisoNuevo.setResponsablePersona((CatEnte) EntityBeanCopy.clone(respTec));
            permisoNuevo.setResponsable(nombreTec);
            nombreTec = Utils.isEmpty(respTec.getTituloProf()) + ". " + Utils.isEmpty(respTec.getApellidos()) + " " + Utils.isEmpty(respTec.getNombres());
            JsfUti.update("frmMain:tdatos:resT");
            JsfUti.update("frmMain:tdatos:regP");
            JsfUti.update("frmMain:tdatos:ciIde");
            JsfUti.executeJS("PF('dlgEditRespTec').hide()");
        } catch (Exception e) {
            Logger.getLogger(GenerarLiquidacionPC.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void actulizarLista() {
        if (categoria.getId() != null) {
            lisCatEdfProp = permisoService.getCatEdfPropList(categoria.getId());
            JsfUti.update("frmMain:tdatos:lisCaract");
        }
    }
    
    public List<CatCiudadela> getCiudadelas() {
        return permisoService.getFichaServices().getCiudadelas();
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

    public PePermiso getPermisoNuevo() {
        return permisoNuevo;
    }

    public void setPermisoNuevo(PePermiso permisoNuevo) {
        this.permisoNuevo = permisoNuevo;
    }

    public CatEnte getRespTec() {
        return respTec;
    }

    public void setRespTec(CatEnte respTec) {
        this.respTec = respTec;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
        consultarNumPredio();
    }

    public PePermisoCabEdificacion getNuevDetalleEdif() {
        return nuevDetalleEdif;
    }

    public void setNuevDetalleEdif(PePermisoCabEdificacion nuevDetalleEdif) {
        this.nuevDetalleEdif = nuevDetalleEdif;
    }

    public PePermisoCabEdificacion getPermisoSelect() {
        return permisoSelect;
    }

    public void setPermisoSelect(PePermisoCabEdificacion permisoSelect) {
        this.permisoSelect = permisoSelect;
    }

    public List<CatEdfCategProp> getListCat() {
        return listCat;
    }

    public void setListCat(List<CatEdfCategProp> listCat) {
        this.listCat = listCat;
    }

    public List<PeTipoPermiso> getListRequisTra() {
        return listRequisTra;
    }

    public void setListRequisTra(List<PeTipoPermiso> listRequisTra) {
        this.listRequisTra = listRequisTra;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public List<PePermisoCabEdificacion> getDetallesEdific() {
        return detallesEdific;
    }

    public void setDetallesEdific(List<PePermisoCabEdificacion> detallesEdific) {
        this.detallesEdific = detallesEdific;
    }

    public List<CatEdfProp> getLisCatEdfProp() {
        return lisCatEdfProp;
    }

    public void setLisCatEdfProp(List<CatEdfProp> lisCatEdfProp) {
        this.lisCatEdfProp = lisCatEdfProp;
    }

    public List<PeDetallePermiso> getLisPeDetallePermisos() {
        return lisPeDetallePermisos;
    }

    public void setLisPeDetallePermisos(List<PeDetallePermiso> lisPeDetallePermisos) {
        this.lisPeDetallePermisos = lisPeDetallePermisos;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public String getNombreTec() {
        return nombreTec;
    }

    public void setNombreTec(String nombreTec) {
        this.nombreTec = nombreTec;
    }

    public CatEnte getRespTecNuevo() {
        return respTecNuevo;
    }

    public void setRespTecNuevo(CatEnte respTecNuevo) {
        this.respTecNuevo = respTecNuevo;
    }

    public Boolean getCodUrban() {
        return codUrban;
    }

    public void setCodUrban(Boolean codUrban) {
        this.codUrban = codUrban;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public Short getNumEdificacion() {
        return numEdificacion;
    }

    public void setNumEdificacion(Short numEdificacion) {
        this.numEdificacion = numEdificacion;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        this.categoria = categoria;
    }

    public CatEdfProp getCarateristicas() {
        return carateristicas;
    }

    public void setCarateristicas(CatEdfProp carateristicas) {
        this.carateristicas = carateristicas;
    }

    public PeDetallePermiso getDetallePermiso() {
        return detallePermiso;
    }

    public void setDetallePermiso(PeDetallePermiso detallePermiso) {
        this.detallePermiso = detallePermiso;
    }

    public String getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(String numReporte) {
        this.numReporte = numReporte;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public Boolean getContinuarb() {
        return continuarb;
    }

    public void setContinuarb(Boolean continuarb) {
        this.continuarb = continuarb;
    }

    public String getNombreTipoPermiso() {
        return nombreTipoPermiso;
    }

    public void setNombreTipoPermiso(String nombreTipoPermiso) {
        this.nombreTipoPermiso = nombreTipoPermiso;
    }

    public Boolean getConfirmarPermiso() {
        return confirmarPermiso;
    }

    public void setConfirmarPermiso(Boolean confirmarPermiso) {
        this.confirmarPermiso = confirmarPermiso;
    }

    public Boolean getMostrarPanelInicial() {
        return mostrarPanelInicial;
    }

    public void setMostrarPanelInicial(Boolean mostrarPanelInicial) {
        this.mostrarPanelInicial = mostrarPanelInicial;
    }
    
}
