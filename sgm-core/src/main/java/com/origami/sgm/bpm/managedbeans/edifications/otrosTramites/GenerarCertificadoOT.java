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
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
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
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarCertificadoOT extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    protected SeqGenMan secuencia;
    
    @Inject
    private ReportesView reportes;
    
    private CatPredio datosPredio;
    private Observaciones obs;
    private HistoricoTramites ht;
    private HistoricoReporteTramite hrt;
    private PdfReporte reporte;
    private HistoricoTramiteDet htd;
    private Boolean clickInspeccion = true;
    private AclUser usuario;
    private CatEnte solicitante, responsable;
    private HashMap<String, Object> paramsActiviti;
    private CatCanton canton;
    private CatParroquia parroquia;
    private List<CatPredioPropietario> lisPropietarios;
    private String codigoCatastral;
    private List<CatEnte> enteList;
    private String cedulaRuc, fecha;
    private PeFirma firma;
    private PePermiso permiso;
    private Long idPermiso;
    private MsgFormatoNotificacion formatoMsg;
    //private Date fechaC;
    private BigInteger sec;
    private OtrosTramites oTramite;
    private OtrosTramitesHasPermiso othp;
    
    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                htd = (HistoricoTramiteDet)services.find(Querys.getHistoricoTramiteDetByTramite, new String[]{"numTramite"}, new Object[]{ht.getIdTramite()});
                hrt = new HistoricoReporteTramite();
                obs = new Observaciones();
                paramsActiviti = new HashMap<String, Object>();
                solicitante = ht.getSolicitante();
                oTramite = ht.getSubTipoTramite();
                othp = ht.getOtrosTramitesHasPermiso();
                if(othp.getResponsableTec()!=null)
                    responsable = (CatEnte) services.find(CatEnte.class, othp.getResponsableTec().longValue());
                hrt.setNombreTarea(this.getvariableByExecutionId(this.getTaskDataByTaskID().getId(), "taskdef").toString());
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                if(this.getVariable(uSession.getTaskID(), "idPermiso")!=null){
                    idPermiso = Long.valueOf(this.getVariable(uSession.getTaskID(), "idPermiso").toString());
                    permiso = (PePermiso) services.find(PePermiso.class, idPermiso);
                }
                if(ht.getNumPredio()!=null)
                    datosPredio = (CatPredio)services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
                fecha = new SimpleDateFormat("dd/MM/yyy").format(new Date());      
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("4")});
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Método que genera el pdf del certificado con los datos de la tasa.
     */
    public void imprimirPDF() throws IOException {
        
        try{
            reporte = new PdfReporte();
            
            String msg="";
            
            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }
            if(othp.getFechaCaducidad()==null){
                JsfUti.messageError(null, "Error", "Hay datos que debe ingresar antes de continuar.");
                return;
            }
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));
            
            servletSession.instanciarParametros();
            
            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            sec = secuencia.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{anio});
            hrt.setSecuencia(sec);
            hrt.setCodValidacion(sec + "" + hrt.getProceso());
            hrt.setNombreReporte("Certificado_OT-" + solicitante.getCiRuc());
            hrt.setEstado(Boolean.TRUE);
            
            hrt = (HistoricoReporteTramite) services.persist(hrt);
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            services.persist(obs);
            
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nombres", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nombres", solicitante.getNombreComercial());
            }
            
            msg = formatoMsg.getHeader() + "Se generó el certificado de "+oTramite.getTipoTramite()+"." + formatoMsg.getFooter();
            
            servletSession.agregarParametro("numTramite",ht.getId()+"-"+new SimpleDateFormat("yyyy").format(htd.getFecCre()));
            servletSession.agregarParametro("numReporte", ht.getNumTramiteXDepartamento());
            servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
            
            servletSession.agregarParametro("diaE",new SimpleDateFormat("dd").format(new Date()));
            servletSession.agregarParametro("mesE",new SimpleDateFormat("MM").format(new Date()));
            servletSession.agregarParametro("anioE",new SimpleDateFormat("yyyy").format(new Date()));
            servletSession.agregarParametro("diaC",new SimpleDateFormat("dd").format(othp.getFechaCaducidad()));
            servletSession.agregarParametro("mesC",new SimpleDateFormat("MM").format(othp.getFechaCaducidad()));
            servletSession.agregarParametro("anioC",new SimpleDateFormat("yyyy").format(othp.getFechaCaducidad()));
            
            if(datosPredio!=null){
                servletSession.agregarParametro("sector", "La Puntilla");
                servletSession.agregarParametro("calle", "Vehicular");
                servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                servletSession.agregarParametro("mz",datosPredio.getUrbMz());
                servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                servletSession.agregarParametro("codCatastral", datosPredio.getCodigoPredial().toUpperCase());
                servletSession.agregarParametro("urb", datosPredio.getCiudadela().getNombre().toUpperCase());
                servletSession.agregarParametro("solar",datosPredio.getUrbSolarnew());
            }else{
                servletSession.agregarParametro("canton", "Samborondón".toUpperCase());
                servletSession.agregarParametro("sector", "La Puntilla".toUpperCase());
                servletSession.agregarParametro("mz",ht.getMz());
                servletSession.agregarParametro("calle", "Vehicular".toUpperCase());
                servletSession.agregarParametro("codCatastral", null);
                servletSession.agregarParametro("urb", ht.getUrbanizacion() != null ? ht.getUrbanizacion().getNombre() : null);
                servletSession.agregarParametro("solar",ht.getSolar());
            }
            if(permiso!=null){
                servletSession.agregarParametro("idPermiso", permiso.getId());
                servletSession.agregarParametro("npisossnbPC", permiso.getPisosSnb());
                servletSession.agregarParametro("alturaConsPC", permiso.getAltura());
                servletSession.agregarParametro("areaSolar", permiso.getAreaSolar());
                servletSession.agregarParametro("npisosbnb", permiso.getPisosBnb());
                servletSession.agregarParametro("totalAEdificar", permiso.getAreaConstruccion());
                servletSession.agregarParametro("lineaFabrica", permiso.getLineaFabrica()+"");
                if(permiso.getPropEstructura()!=null)
                    servletSession.agregarParametro("estructura", permiso.getPropEstructura().getNombre());
                if(permiso.getPropInstalaciones()!=null)
                    servletSession.agregarParametro("instalacion", permiso.getPropInstalaciones().getNombre());
                if(permiso.getPropPlantaalta()!=null && permiso.getPropPlantabaja()!=null)
                    servletSession.agregarParametro("pisos", permiso.getPropPlantaalta().getNombre() +" - "+permiso.getPropPlantabaja().getNombre());
                if(permiso.getPropCubierta()!=null)
                    servletSession.agregarParametro("cubierta", permiso.getPropCubierta().getNombre());
                if(permiso.getPropParedes()!=null)
                    servletSession.agregarParametro("paredes", permiso.getPropParedes().getNombre());                    
            }
            if(oTramite.getId() == 24 || oTramite.getId() == 30)
                servletSession.agregarParametro("nota", oTramite.getNota()+anio+" - "+(anio+1));
            else
                servletSession.agregarParametro("nota", oTramite.getNota());
            servletSession.agregarParametro("tipoPermiso", oTramite.getTipoTramite().toUpperCase());
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+ " "+ firma.getDepartamento().toUpperCase());
            servletSession.agregarParametro("validador", "");
            if(responsable!=null){
                servletSession.agregarParametro("nombresResponsable", responsable.getNombres() + " " + responsable.getApellidos());
                servletSession.agregarParametro("regProf", responsable.getRegProf());
                servletSession.agregarParametro("ciTecnico", responsable.getCiRuc());
            }
            servletSession.agregarParametro("descripcion", othp.getDescripcion());
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
            servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.setNombreReporte("otrosTramitesCertificado-"+solicitante.getCiRuc());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            
            servletSession.setTieneDatasource(true);
            clickInspeccion = false;
            services.update(othp);
            JsfUti.messageInfo(null, "Info", "Se guardaron los datos correctamente. Puede proceder a imprimir el certificado.");
            servletSession.setReportePDF(reporte.generarPdf("//reportes//otrosTramites//CertificadoOT.jasper", servletSession.getParametros()));

            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());

            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("to", getCorreosByCatEnte(solicitante));
            paramsActiviti.put("message", "Se generó un certificado de Otros Trámites.");
            paramsActiviti.put("subject", "Trámite: "+ht.getId());

            JsfUti.messageInfo(null, "Info", "Se generó el certificado correctamente.");
            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
            
        }catch(Exception e){
            e.printStackTrace();
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

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
    
}
