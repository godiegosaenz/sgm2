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
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisosAdicionalesServices;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Hibernate;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarCertificadoPA extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected PermisosAdicionalesServices servicesPA;
    
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;

    @Inject
    private ReportesView reportes;
    
    private Observaciones obs;
    private HistoricoTramites ht;
    private PePermisosAdicionales permisoAdicional;
    private List<PePermisosAdicionales> permisosAdicionalesList;
    private HistoricoTramiteDet htd;
    private HistoricoReporteTramite hrt;
    private List<HistoricoReporteTramite> hrts;
    private MsgFormatoNotificacion formatoMsg;
    private CatEnte solicitante;
    private CatPredio datosPredio;
    private PePermiso permiso;
    private PeFirma firma;
    private PdfReporte reporte;
    private AclUser usuario;
    private Boolean clickInspeccion = true;
    private HashMap<String, Object> paramsActiviti;
    
    private AclUser digitalizador;
    
    private AclRol dirEdif;
    private AclUser edif;
    private List temp;
    
    @PostConstruct
    public void init(){
        String s;
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                obs = new Observaciones();
                reporte = new PdfReporte();
                paramsActiviti = new HashMap<>();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                permisosAdicionalesList = services.findAll(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                if(permisosAdicionalesList != null && !permisosAdicionalesList.isEmpty())
                    permisoAdicional = permisosAdicionalesList.get(0);
                hrt = new HistoricoReporteTramite();
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                solicitante = (CatEnte) services.find(CatEnte.class, ht.getSolicitante().getId());
                permisosAdicionalesList = services.findAll(Querys.getPePermisosAdicionalesByTramiteID, new String[]{"tramiteId"}, new Object[]{ht.getId()});
                if(permisosAdicionalesList != null && !permisosAdicionalesList.isEmpty()){
                    permisoAdicional = permisosAdicionalesList.get(0);
                    if(permisoAdicional.getPermiso()!=null)
                        permiso = permisoAdicional.getPermiso();
                    datosPredio = permisoAdicional.getPredio();
                }
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                //digitalizador = (AclUser) services.find(Querys.getAclUserByID, new String[]{"id"}, new Object[]{new Long(293)});
                
                
                
                dirEdif = (AclRol) services.find(AclRol.class, new Long(67));
                Hibernate.initialize(dirEdif.getAclUserCollection());
                temp =  (List) dirEdif.getAclUserCollection();
                edif = (AclUser) temp.get(0);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Método que genera el pdf de la tasa de liquidación previamente almacenada.
     * 
     * @throws IOException 
     */
    public void imprimirPDF() throws IOException{
        if (this.getTaskDataByTaskID() == null) {
            JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
            return;
        }
        if(obs.getObservacion()==null || obs.getObservacion()==""){
            JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
            return;
        }/*
        if(!servletSession.validarCantidadDeParametrosDelServlet()){
            JsfUti.messageError(null, "Error", "No ha guardado los datos del certificado.");
            return;
        }*/
        try{
            
            BigInteger sec;
            String msg="";

            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.instanciarParametros();
            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("Certificado_PA-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);
            
            //sec = secuencia.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{anio});
            hrt.setCodValidacion(null);
            
            if(hrts != null && !hrts.isEmpty())
                hrt.setCodValidacion((hrts.get(0).getSecuencia().add(BigInteger.valueOf(hrts.size()))) + "" + hrt.getProceso());
            hrt.setSecuencia(permisoAdicional.getNumReporte());
            
            hrt = (HistoricoReporteTramite) services.persist(hrt);
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            services.persist(obs);
            
            servletSession.agregarParametro("numReporte", permisoAdicional.getNumReporte()+ "-"+new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
            
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nombrePropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nombrePropietario", solicitante.getNombreComercial());
            }
            
            msg = formatoMsg.getHeader() + "Se generó el certificado de Permisos Adicionales." + formatoMsg.getFooter();
            
            servletSession.agregarParametro("diaE",new SimpleDateFormat("dd").format(permisoAdicional.getFechaEmision()));
            servletSession.agregarParametro("mesE",new SimpleDateFormat("MM").format(permisoAdicional.getFechaEmision()));
            servletSession.agregarParametro("anioE",new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
            servletSession.agregarParametro("diaC",new SimpleDateFormat("dd").format(permisoAdicional.getFechaCaducidad()));
            servletSession.agregarParametro("mesC",new SimpleDateFormat("MM").format(permisoAdicional.getFechaCaducidad()));
            servletSession.agregarParametro("anioC",new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaCaducidad()));
            servletSession.agregarParametro("tipoPermiso", permisoAdicional.getTipoPermisoAdicional().getDescripcion().toUpperCase());
            servletSession.agregarParametro("tramite",ht.getId()+"-"+new SimpleDateFormat("yyyy").format(permisoAdicional.getFechaEmision()));
            servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
            
            servletSession.agregarParametro("sector", "La Puntilla");
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("mz", datosPredio.getUrbMz());
            servletSession.agregarParametro("solar", datosPredio.getUrbSolarnew());
            servletSession.agregarParametro("codigoCatastral", datosPredio.getCodigoPredialCompleto());
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            if(permisoAdicional.getEstructura()!=null)
                servletSession.agregarParametro("estructura", permisoAdicional.getEstructura().getNombre());
            if(permisoAdicional.getInstalaciones()!=null)
                servletSession.agregarParametro("instalacion", permisoAdicional.getInstalaciones().getNombre());
            if(permisoAdicional.getPlantaAlta()!=null && permisoAdicional.getPlantaBaja()!=null)
                servletSession.agregarParametro("pisos", permisoAdicional.getPlantaAlta().getNombre()+"-"+permisoAdicional.getPlantaBaja().getNombre());
            if(permisoAdicional.getCubierta()!=null)
                servletSession.agregarParametro("cubierta", permisoAdicional.getCubierta().getNombre());
            if(permisoAdicional.getParedes()!=null)
                servletSession.agregarParametro("paredes", permisoAdicional.getParedes().getNombre());
            servletSession.agregarParametro("lineaDeFabrica", permisoAdicional.getLineaFabrica());
            servletSession.agregarParametro("descripcion", permisoAdicional.getDescripcion());
            servletSession.agregarParametro("idPermisoAdicional", permisoAdicional.getId());
            if(permiso!=null)
                servletSession.agregarParametro("permisoConstruccion", permiso.getId());
            
            servletSession.agregarParametro("npisossnbPC", permisoAdicional.getNumPisosSnb());
            servletSession.agregarParametro("alturaConsPC", permisoAdicional.getAlturaConst());
            servletSession.agregarParametro("areaSolar", permisoAdicional.getAreaSolar());
            servletSession.agregarParametro("npisosbnb", permisoAdicional.getNumPisosBnb());
            servletSession.agregarParametro("totalAEdificar", permisoAdicional.getTotalEdificar());
            
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo() + " " + firma.getDepartamento());
            servletSession.agregarParametro("validador", "");
            if(permisoAdicional.getRespTecn()!=null){
                servletSession.agregarParametro("nombresResponsable", permisoAdicional.getRespTecn().getNombres() + " " + permisoAdicional.getRespTecn().getApellidos());
                servletSession.agregarParametro("regProf", permisoAdicional.getRespTecn().getRegProf());
                servletSession.agregarParametro("ciTecnico", permisoAdicional.getRespTecn().getCiRuc());
            }
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
            servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.setNombreReporte("permisosAdicionalesCertificado-"+solicitante.getCiRuc());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            
            servletSession.setTieneDatasource(true);
            clickInspeccion = false;
            
            JsfUti.messageInfo(null, "Info", "Se guardaron los datos correctamente. Puede proceder a imprimir el certificado.");
            servletSession.setReportePDF(reporte.generarPdf("//reportes//permisosAdicionales//CertificadoPermisosAdicionales.jasper", servletSession.getParametros()));

            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());

            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("to", this.getCorreosByCatEnte(edif.getEnte()));
            paramsActiviti.put("message", "msg");
            paramsActiviti.put("subject", "Trámite: "+ht.getId());

            JsfUti.messageInfo(null, "Info", "Se generó el certificado correctamente.");
            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public PePermisosAdicionales getPermisoAdicional() {
        return permisoAdicional;
    }

    public void setPermisoAdicional(PePermisosAdicionales permisoAdicional) {
        this.permisoAdicional = permisoAdicional;
    }

    public List<PePermisosAdicionales> getPermisosAdicionalesList() {
        return permisosAdicionalesList;
    }

    public void setPermisosAdicionalesList(List<PePermisosAdicionales> permisosAdicionalesList) {
        this.permisosAdicionalesList = permisosAdicionalesList;
    }

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public HistoricoReporteTramite getHrt() {
        return hrt;
    }

    public void setHrt(HistoricoReporteTramite hrt) {
        this.hrt = hrt;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public Boolean getClickInspeccion() {
        return clickInspeccion;
    }

    public void setClickInspeccion(Boolean clickInspeccion) {
        this.clickInspeccion = clickInspeccion;
    }
    
}
