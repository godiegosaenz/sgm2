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
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
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
import org.activiti.engine.task.Attachment;
import org.apache.commons.io.IOUtils;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class GenerarCertificadoIF extends BpmManageBeanBaseRoot implements Serializable {
    
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
    private HistoricoTramiteDet htd, htdResponsable;
    private List<HistoricoTramiteDet> htdList;
    private AclUser usuario;
    private MsgFormatoNotificacion formatoMsg;
    private CatEnte solicitante;
    private List<Attachment> listaArchivos;
    private List<Attachment> archivos = new ArrayList();
    private List<Attachment> archivos2 = new ArrayList();
    private List<Attachment> archivos3 = new ArrayList();
    private List<Attachment> archivos4 = new ArrayList();
    private PePermiso pp;
    private PeFirma firma;
    private CatEnte responsableTec;
    private PeInspeccionFinal peInspeccionFinalV;
    private List<CatPredioPropietario> lisPropietarios;
    
    @PostConstruct
    public void init() {
        List<CatPredioPropietario> propTemp;
        try {
            if (uSession.esLogueado() && uSession.getTaskID() != null) {
                String s = "";
                paramsActiviti = new HashMap<String, Object>();
                obs = new Observaciones();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                htd = new HistoricoTramiteDet();
                solicitante = ht.getSolicitante();
                obtenerArchivosSubidos();
                this.setTaskId(uSession.getTaskID());                
                usuario = (AclUser) services.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{uSession.getName_user()});
                //pif = ht.getPeInspeccionFinal();
                peInspeccionFinalV = ht.getPeInspeccionFinal();
                pp = (PePermiso) services.find(Querys.getPePermisoById, new String[]{"id"}, new Object[]{Long.parseLong(peInspeccionFinalV.getNumPermisoConstruc()+"")});
                hrt = new HistoricoReporteTramite();
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
                datosPredio = peInspeccionFinalV.getPredio();
                firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
                formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("1")});
                htdList = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
                htdResponsable = htdList.get(htdList.size()-1);
                htd.setResponsable(htdResponsable.getResponsable());
                
                responsableTec = peInspeccionFinalV.getRespTecnico();
                lisPropietarios = new ArrayList<CatPredioPropietario>();

                propTemp = (List<CatPredioPropietario>) datosPredio.getCatPredioPropietarioCollection();

                for (CatPredioPropietario temp : propTemp) {
                    if (temp.getEstado().equals("A")) {
                        lisPropietarios.add(temp);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void obtenerArchivosSubidos(){
        List<HistoricoArchivo> archivos = services.findAll(Querys.getHistoricoArchivosList, new String[]{"tramiteId", "carpeta"}, new Object[]{ht.getIdTramite(), "fotosInspeccion-"+ht.getId()});
        HistoricoArchivo temp;
        if(archivos==null || archivos.isEmpty())
            return;
        else
            temp = archivos.get(archivos.size()-1);
        
        listaArchivos = this.getProcessInstanceAttachmentsFiles(temp.getProcessInstance());
        
        for(Attachment att : listaArchivos){
            if(att.getDescription().equals("tipo1"))
                this.archivos.add(att);
            if(att.getDescription().equals("tipo2"))
                archivos2.add(att);
            if(att.getDescription().equals("tipo3"))
                archivos3.add(att);
            if(att.getDescription().equals("tipo4"))
                archivos4.add(att);
        }
        
    }
    
    public void imageDownload(String url) throws IOException{
        
        String s[] = url.split("nodeRef=");
        InputStream is;
        reporte = new PdfReporte();
        
        if(s==null)
            return;
        try{
            servletSession.instanciarParametros();
            servletSession.setNombreReporte(solicitante.getCiRuc());  
            reportes.descargarImagenArregloBytes(IOUtils.toByteArray(reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1])));
            JsfUti.messageInfo(null, "Info", "Se Descargó la imagen correctamente.");

            servletSession.borrarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirCertificado(){
        reporte = new PdfReporte();
        BigInteger sec;
        String msg="";
        try{
            if (this.getTaskDataByTaskID() == null) {
                JsfUti.messageInfo(null, "Nota", "La liquidacion ya fue generada.");
                return;
            }
            if(obs.getObservacion()==null || obs.getObservacion()==""){
                JsfUti.messageError(null, "Error", "Debe ingresar la observación antes de continuar.");
                return;
            }
        
            
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(uSession.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            
            Long anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));

            hrt.setFecCre(new Date());
            hrt.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
            hrt.setProceso(ht.getIdProceso());
            hrt.setTramite(ht);
            hrt.setNombreReporte("Certificado_IF-" + solicitante.getCiRuc());
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
            
            services.persist(obs);
            msg = formatoMsg.getHeader() + "Se generó el certificado de Inspección Final." + formatoMsg.getFooter();
            
            this.guardarDatosReporteCertificado(peInspeccionFinalV, lisPropietarios, responsableTec, datosPredio, pp, firma, ht, hrt);
            
            this.llenarImagenes();
            
            servletSession.setReportePDF(reporte.generarPdf("/reportes/inspeccionFinal/CertificadoInspeccionFinal.jasper", servletSession.getParametros()));
            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
            JsfUti.messageInfo(null, "Info", "Se generó la tasa correctamente...");
            
            
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("idReporte", hrt.getId());
            paramsActiviti.put("archivo", servletSession.getReportePDF());
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("nombreArchivoByteArray", new Date().getTime() + servletSession.getNombreReporte() + solicitante.getCiRuc() + ".pdf");
            paramsActiviti.put("tipoArchivoByteArray", "application/pdf");
            paramsActiviti.put("to", getCorreosByCatEnte(usuario.getEnte()));
            paramsActiviti.put("message", msg);
            paramsActiviti.put("subject", "Trámite: "+ht.getId());
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            
            this.completeTask(this.getTaskId(), paramsActiviti);
            servletSession.borrarDatos();
            
        }catch(Exception e){
            e.printStackTrace();
            
        }
    }
    
    public void guardarDatosReporteCertificado(PeInspeccionFinal peInspeccionFinalV, List<CatPredioPropietario> lisPropietarios, CatEnte responsableTec,
            CatPredio datosPredio, PePermiso pp, PeFirma firma, HistoricoTramites ht, HistoricoReporteTramite hrt){
        try{
            servletSession.instanciarParametros();
            servletSession.agregarParametro("numReporte", peInspeccionFinalV.getNumReporte()+"-"+peInspeccionFinalV.getAnioInspeccion());
            /*
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres() + " " + solicitante.getApellidos());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombreComercial());
            }*/
            
            servletSession.agregarParametro("numeroTramiteInspeccion", peInspeccionFinalV.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
            servletSession.agregarParametro("dia",new SimpleDateFormat("dd").format(new Date()));
            servletSession.agregarParametro("mes",new SimpleDateFormat("MM").format(new Date()));
            servletSession.agregarParametro("anio",new SimpleDateFormat("yyyy").format(new Date()));
            if(ht!=null)
                servletSession.agregarParametro("numTramite",ht.getId()+"-"+peInspeccionFinalV.getAnioInspeccion());
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
            }/*
            if(peInspeccionFinalV.getSolicitante() != null){
                if (peInspeccionFinalV.getSolicitante().getEsPersona()) {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getNombres() + " " + peInspeccionFinalV.getSolicitante().getApellidos());
                } else {
                    servletSession.agregarParametro("nomPropietario", peInspeccionFinalV.getSolicitante().getRazonSocial());
                }
                servletSession.agregarParametro("ciRuc", peInspeccionFinalV.getSolicitante().getCiRuc());
            }  */
            servletSession.agregarParametro("nombreTecnico", responsableTec.getNombreCompleto());
            servletSession.agregarParametro("ciTecnico", responsableTec.getCiRuc());
            servletSession.agregarParametro("canton","Samborondón");
            servletSession.agregarParametro("sector", "La Puntilla");
            servletSession.agregarParametro("calle", "Vehicular");
            servletSession.agregarParametro("manzana", datosPredio.getUrbMz());
            servletSession.agregarParametro("solar", datosPredio.getUrbSolarnew());
            servletSession.agregarParametro("codigoNuevo", datosPredio.getCodigoPredial());
            servletSession.agregarParametro("codigoAnterior", datosPredio.getPredialant());
            servletSession.agregarParametro("urbanizacion", datosPredio.getCiudadela().getNombre());
            servletSession.agregarParametro("observacion", "");
            servletSession.agregarParametro("validador", "");
            servletSession.agregarParametro("areaSolar", pp.getAreaSolar());
            servletSession.agregarParametro("areaConstruccion", pp.getAreaConstruccion());
            servletSession.agregarParametro("areaSolarIns", peInspeccionFinalV.getAreaSolar());
            servletSession.agregarParametro("areaConstIns", peInspeccionFinalV.getAreaConst());
            servletSession.agregarParametro("descPermiso", pp.getDescFamiliar());
            servletSession.agregarParametro("descInspeccion", peInspeccionFinalV.getDescEdificacion());
            servletSession.agregarParametro("retFron",pp.getRetiroFrontal());
            servletSession.agregarParametro("retl1", pp.getRetiroLateral1());
            servletSession.agregarParametro("retl2", pp.getRetiroLateral2());
            servletSession.agregarParametro("retPost", pp.getRetiroPosterior());
            servletSession.agregarParametro("retFronIns", peInspeccionFinalV.getRetiroFrontal());
            servletSession.agregarParametro("retl1In", peInspeccionFinalV.getRetiroLateral1());
            servletSession.agregarParametro("retl2Ins",peInspeccionFinalV.getRetiroLateral2());
            servletSession.agregarParametro("retPostIns",peInspeccionFinalV.getRetiroPosterior());
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase() + " " + firma.getDepartamento().toUpperCase());
            if(uSession.getNombrePersonaLogeada()!="")
                servletSession.agregarParametro("responsable", uSession.getNombrePersonaLogeada());
            else
                servletSession.agregarParametro("responsable", uSession.getName_user());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("inspeccionId", peInspeccionFinalV.getId());
            servletSession.agregarParametro("permisoId", pp.getId());
            servletSession.agregarParametro("regProfTec", responsableTec.getRegProf());
            servletSession.agregarParametro("registroPermiso", pp.getNumReporte()+"-"+pp.getAnioPermiso());
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.agregarParametro("SUBREPORT_DIR",JsfUti.getRealPath("//reportes//inspeccionFinal//"));
            if(hrt!=null)
                servletSession.agregarParametro("codigoQR",SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.agregarParametro("SUBREPORT_DIR2",JsfUti.getRealPath("//reportes//permisoConstruccion//"));           
            servletSession.setNombreReporte("CertificadoInspeccionFinal");
            servletSession.setNombreSubCarpeta("inspeccionFinal");
            servletSession.setTieneDatasource(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ServletSession getServletSession() {
        return servletSession;
    }
    
    public void llenarImagenes(){
        
        String s[] = null, url;
        InputStream is;
        reporte = new PdfReporte();
        int centinela;
        try{
            centinela = archivos.size();
            for(int i= 0 ; i < 3 ; i++){
                if(centinela>i){
                    url = archivos.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if(s!=null){
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        servletSession.agregarParametro("img"+(i+1), is);
                    }else{
                        servletSession.agregarParametro("img"+(i+1), null);
                    }
                }else{
                    servletSession.agregarParametro("img"+(i+1), null);
                }
            }
            centinela = archivos2.size();
            for(int i= 0 ; i < 3 ; i++){
                if(centinela>i){
                    url = archivos2.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if(s!=null){
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        servletSession.agregarParametro("img"+(i+4), is);
                    }else{
                        servletSession.agregarParametro("img"+(i+4), null);
                    }
                }else{
                    servletSession.agregarParametro("img"+(i+7), null);
                }
            }
            centinela = archivos3.size();
            for(int i= 0 ; i < 3 ; i++){
                if(centinela>i){
                    url = archivos3.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if(s!=null){
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        servletSession.agregarParametro("img"+(i+7), is);
                    }else{
                        servletSession.agregarParametro("img"+(i+7), null);
                    }
                }else{
                    servletSession.agregarParametro("img"+(i+7), null);
                }
            }
            centinela = archivos4.size();
            for(int i= 0 ; i < 3 ; i++){
                if(centinela>i){
                    url = archivos4.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if(s!=null){
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        servletSession.agregarParametro("img"+(i+10), is);
                    }else{
                        servletSession.agregarParametro("img"+(i+10), null);
                    }
                }else{
                    servletSession.agregarParametro("img"+(i+10), null);
                }
            }
            /*
            is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);

            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(false);   

            servletSession.agregarParametro("parameter1", is);
            servletSession.agregarParametro("parameter2", "Hola mundo!");
            servletSession.setNombreReporte("Prueba_Imagen");  
            
            servletSession.setReportePDF(reporte.generarPdf("/reportes/inspeccionFinal/report2.jasper", servletSession.getParametros()));
            reportes.descargarPDFarregloBytes(servletSession.getReportePDF());
            servletSession.borrarParametros();
                */
            //servletSession.instanciarParametros();
            //servletSession.setNombreReporte(solicitante.getCiRuc());  
            //reportes.descargarImagenArregloBytes(IOUtils.toByteArray(reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1])));
            //JsfUti.messageInfo(null, "Info", "Se Descargó la imagen correctamente.");

            //servletSession.borrarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
        
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

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public List<Attachment> getListaArchivos() {
        return listaArchivos;
    }

    public void setListaArchivos(List<Attachment> listaArchivos) {
        this.listaArchivos = listaArchivos;
    }

    public List<Attachment> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Attachment> archivos) {
        this.archivos = archivos;
    }

    public List<Attachment> getArchivos2() {
        return archivos2;
    }

    public void setArchivos2(List<Attachment> archivos2) {
        this.archivos2 = archivos2;
    }

    public List<Attachment> getArchivos3() {
        return archivos3;
    }

    public void setArchivos3(List<Attachment> archivos3) {
        this.archivos3 = archivos3;
    }

    public List<Attachment> getArchivos4() {
        return archivos4;
    }

    public void setArchivos4(List<Attachment> archivos4) {
        this.archivos4 = archivos4;
    }

    public PePermiso getPp() {
        return pp;
    }

    public void setPp(PePermiso pp) {
        this.pp = pp;
    }
    
}
