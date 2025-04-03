/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.bpm.managedbeans.catastro.PredioUtil;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PeInspeccionFotos;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.PeInspeccionFinalLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.InspeccionFinalServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.task.Attachment;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import util.ApplicationContextUtils;
import util.CmisUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class InspeccionFinalConsulta implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(InspeccionFinalConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    private Entitymanager services;
    @javax.inject.Inject
    protected InspeccionFinalServices servicesIF;
    @Inject
    private UserSession uSession;
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    @Inject
    protected BpmManageBeanBase manageBeanBase;
    @Inject
    private ReportesView reportes;
    @Inject
    private EditarFotosViewIF editFotos;
    @Inject
    private GuardadoIF inspeccionManager;

    protected PeInspeccionFinalLazy inspeccionlazy;
    protected AclUser firmaDir;
    protected String path;
    protected List<Attachment> listaArchivos;
    private List<ContentStream> archivos;
    private List<ContentStream> archivos2;
    private List<ContentStream> archivos3;
    private List<ContentStream> archivos4;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;
    private String codigoQR;

    private List<PeInspeccionFinal> inspecciones;
    private CmisUtil alfrescoUtils;
    private PeTipoPermiso tipoInsp;

    public InspeccionFinalConsulta() {
    }

    @PostConstruct
    public void initView() {
        uSession.setTaskID(null);
        if (uSession.esLogueado()) {
            inspeccionlazy = new PeInspeccionFinalLazy();
            //inspecciones = services.findAll(Querys.getPeInspeccionList, new String[]{}, new Object[]{});
            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            firmaDir = new AclUser();
            tipoInsp = (PeTipoPermiso) services.find(Querys.getPeTipoPermisoCodigoAnt, new String[]{"codigo"}, new Object[]{"INF"});
        }
    }

    /**
     * Descarga la liquidación del repositorio que fue generado.
     *
     * @param inspeccion Entity {@link PeInspeccionFinal}
     */
    public void imprimirLiquidaciónGuardado(PeInspeccionFinal inspeccion) {
        HistoricoTramites ht = permisoServices.getHistoricoTramiteById(inspeccion.getTramite().getId());
        imprimirLiquidación(inspeccion);

    }

    /**
     * Descarga el certificado desde el repositorio.
     *
     * @param inspeccion Entity {@link PeInspeccionFinal}
     */
    public void imprimirCertificadoGuardado(PeInspeccionFinal inspeccion) {
        HistoricoTramites ht = permisoServices.getHistoricoTramiteById(inspeccion.getTramite().getId());
        try {
            if (ht.getObservacion() != null && ht.getObservacion().equals("Trámite migrado")) {
                //this.imprimirCertificado(inspeccion);
                this.obtenerArchivosSubidosMigrados(inspeccion.getId());
                this.imprimirCertificado(inspeccion);
                return;
            } else {
                this.obtenerDatosInspeccion(ht.getId());
                this.imprimirCertificado(inspeccion);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redirectFotosInspeccion() {
        JsfUti.redirectFacesNewTab("/vistaprocesos/edificaciones/inspeccionFinal/migrarFotos.xhtml");
    }

    public void redirectEditarInspeccion(PeInspeccionFinal inspeccion) {
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.agregarParametro("idInspeccion", inspeccion.getId());
        JsfUti.redirectFacesNewTab("/vistaprocesos/edificaciones/inspeccionFinal/editarInspeccionFinal.xhtml");
    }

    public void redirectEditarFotosInspeccion(PeInspeccionFinal inspeccion) throws IOException {

        try {
            editFotos.borrarTodo();
            editFotos.setNomResponsable(sess.getNombrePersonaLogeada());
            editFotos.setIdInspeccion(inspeccion.getId());
            editFotos.setIdTramite(inspeccion.getTramite().getIdTramite());
            editFotos.initView();
            JsfUti.redirectFacesNewTab("/vistaprocesos/edificaciones/inspeccionFinal/editarFotosInspeccion.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void imprimirLiquidación(PeInspeccionFinal inspeccion) {
        //ss.instanciarParametros();
        //ss.setNombreSubCarpeta("inspeccionFinal");
        BigDecimal total = new BigDecimal(0);

        List<CatPredioPropietario> propietarios, lisPropietarios;
        lisPropietarios = new ArrayList();
        CatPredio predio = inspeccion.getPredio();
        propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        for (CatPredioPropietario temp : propietarios) {
            if (temp.getEstado().equals("A")) {
                lisPropietarios.add(temp);
            }
        }
        HistoricoTramites ht = inspeccion.getTramite();
        //AclUser firmaDirector = permisoServices.getAclUserByUser(ht.getTipoTramite().getUserDireccion());
        //firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);
        PeFirma firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
        AclUser director = firma.getAclUser();

        CatEnte tecnico = inspeccion.getRespTecnico();

        try {

            this.inspeccionManager.generarDatosReporteLiquidacion(inspeccion, lisPropietarios, predio, tecnico, ht, firma, null);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imprimirCertificado(PeInspeccionFinal inspeccion) {
        List<PeInspeccionFotos> fotos;
        List<CatPredioPropietario> propietarios, lisPropietarios;
        lisPropietarios = new ArrayList();
        CatPredio predio = inspeccion.getPredio();
        propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();

        for (CatPredioPropietario temp : propietarios) {
            if (temp.getEstado().equals("A")) {
                lisPropietarios.add(temp);
            }
        }
        HistoricoTramites ht = inspeccion.getTramite();
        PePermiso pp = permisoServices.getPePermisoById(inspeccion.getNumPermisoConstruc().longValue());
        fotos = (List<PeInspeccionFotos>) inspeccion.getFotos();
        //AclUser firmaDirector = permisoServices.getAclUserByUser(ht.getTipoTramite().getUserDireccion());
        //firmaDir = (AclUser) EntityBeanCopy.clone(firmaDirector);
        PeFirma firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
        AclUser director = firma.getAclUser();
        int cont1 = 1, cont2 = 4, cont3 = 7, cont4 = 10;
        InputStream is;

        CatEnte tecnico = inspeccion.getRespTecnico();
        //ss.instanciarParametros();
        try {
            /*
            ss.setNombreSubCarpeta("inspeccionFinal");
            ss.agregarParametro("numReporte", "");
            if(ht.getObservacion()!=null && ht.getObservacion().equals("Trámite migrado"))
                ss.agregarParametro("codigoQR", "Inspección Final migrada.");
            else
                ss.agregarParametro("codigoQR", codigoQR);
            ss.agregarParametro("numeroTramiteInspeccion", inspeccion.getNumReporte() + "-"+new SimpleDateFormat("yyyy").format(ht.getFecha()));
            if(lisPropietarios!=null && !lisPropietarios.isEmpty()){
                if(lisPropietarios.get(0).getEnte().getEsPersona()){
                    ss.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombres()+" "+lisPropietarios.get(0).getEnte().getApellidos());
                }else{
                    ss.agregarParametro("nombrePropietario", lisPropietarios.get(0).getEnte().getNombreComercial());                        
                }
                ss.agregarParametro("ciRucPropietario", lisPropietarios.get(0).getEnte().getCiRuc());
            }
            ss.agregarParametro("dia", new SimpleDateFormat("dd").format(inspeccion.getFechaInspeccion()));
            ss.agregarParametro("mes", new SimpleDateFormat("MM").format(inspeccion.getFechaInspeccion()));
            ss.agregarParametro("anio", new SimpleDateFormat("yyyy").format(inspeccion.getFechaInspeccion()));
            ss.agregarParametro("numTramite", ht.getId()+ "-" + new SimpleDateFormat("yyyy").format(ht.getFecha()));
            //ss.agregarParametro("nombrePropietario", getPropietario(propietarios.get(0).getEnte()));
            if(tecnico!=null){
                ss.agregarParametro("nombreTecnico", getPropietario(tecnico));
                ss.agregarParametro("ciTecnico", tecnico.getCiRuc());
            }
            ss.agregarParametro("canton", "Samborondón");
            ss.agregarParametro("sector", "La Puntilla");
            ss.agregarParametro("calle", "Vehicular");
            ss.agregarParametro("manzana", predio.getUrbMz());
            ss.agregarParametro("solar", predio.getUrbSolarnew());
            ss.agregarParametro("codigoNuevo", predio.getCodigoPredial());
            ss.agregarParametro("codigoAnterior", "***************");
            ss.agregarParametro("urbanizacion", predio.getCiudadela().getNombre());
            ss.agregarParametro("observacion", "");
            ss.agregarParametro("validador", "");
            ss.agregarParametro("areaSolar", pp.getAreaSolar());
            ss.agregarParametro("areaConstruccion", pp.getAreaConstruccion());
            ss.agregarParametro("areaSolarIns", inspeccion.getAreaSolar());
            ss.agregarParametro("areaConstIns", inspeccion.getAreaConst());
            ss.agregarParametro("descPermiso", pp.getDescFamiliar());
            ss.agregarParametro("descInspeccion", inspeccion.getDescEdificacion());
            ss.agregarParametro("retFron", pp.getRetiroFrontal());
            ss.agregarParametro("retl1", pp.getRetiroLateral1());
            ss.agregarParametro("retl2", pp.getRetiroLateral2());
            ss.agregarParametro("retPost", pp.getRetiroPosterior());
            ss.agregarParametro("retFronIns", inspeccion.getRetiroFrontal());
            ss.agregarParametro("retl1Ins", inspeccion.getRetiroLateral1());
            ss.agregarParametro("retl2Ins", inspeccion.getRetiroLateral2());
            ss.agregarParametro("retPostIns", inspeccion.getRetiroPosterior());
            ss.agregarParametro("nombreIng", firma.getNomCompleto());
            ss.agregarParametro("cargoIng", firma.getCargo()+ " "+firma.getDepartamento());
            
            ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("inspeccionId", inspeccion.getId());
            ss.agregarParametro("permisoId", pp.getId());
            ss.agregarParametro("registroPermiso", pp.getNumReporte()+"-"+pp.getAnioPermiso());
            if(tecnico!=null)
                ss.agregarParametro("regProfTec", tecnico.getRegProf());
            ss.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//inspeccionFinal//"));
            ss.setNombreReporte("CertificadoInspeccionFinal");
            //this.llenarImagenes();
            ss.setTieneDatasource(true);
             */
            inspeccionManager.guardarDatosReporteCertificado(inspeccion, lisPropietarios, tecnico, predio, pp, firma, ht, null);

            for (ContentStream temp : archivos) {
                is = temp.getStream();
                ss.agregarParametro("img" + cont1, is);
                cont1++;
            }
            for (ContentStream temp : archivos2) {
                is = temp.getStream();
                ss.agregarParametro("img" + cont2, is);
                cont2++;
            }
            for (ContentStream temp : archivos3) {

                is = temp.getStream();
                ss.agregarParametro("img" + cont3, is);
                cont3++;
            }
            for (ContentStream temp : archivos4) {
                is = temp.getStream();
                ss.agregarParametro("img" + cont4, is);
                cont4++;
            }

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obtenerDatosInspeccion(Long idHistoricoTramites) {
        Folder carpetaPadre;
        int cont = 0;
        ContentStream doc;
        archivos = new ArrayList();
        archivos2 = new ArrayList();
        archivos3 = new ArrayList();
        archivos4 = new ArrayList();
        HistoricoArchivo ha;

        try {
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            carpetaPadre = alfrescoUtils.getFolder("fotosInspeccion-" + idHistoricoTramites);
            ItemIterable<CmisObject> it = carpetaPadre.getChildren();
            for (CmisObject temp : it) {

                doc = alfrescoUtils.getDocument(temp.getId());

                if (doc != null) {
                    if (doc.getFileName().contains("Fachada_Posterior")) {
                        this.archivos.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Frontal")) {
                        archivos2.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Izquierda")) {
                        archivos3.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Derecha")) {
                        archivos4.add(doc);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void obtenerArchivosSubidosMigrados(Long inspeccionId) {
        Folder carpetaPadre;
        int cont = 0;
        ContentStream doc;
        archivos = new ArrayList();
        archivos2 = new ArrayList();
        archivos3 = new ArrayList();
        archivos4 = new ArrayList();
        HistoricoArchivo ha;

        try {
            alfrescoUtils = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            carpetaPadre = alfrescoUtils.getFolder("migracion-" + inspeccionId);
            if (carpetaPadre == null) {
                JsfUti.messageInfo(null, "Info", "La liquidación no tiene fotos");
                return;
            }
            ItemIterable<CmisObject> it = carpetaPadre.getChildren();
            for (CmisObject temp : it) {

                doc = alfrescoUtils.getDocument(temp.getId());

                if (doc != null) {
                    if (doc.getFileName().contains("Fachada_Posterior")) {
                        this.archivos.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Frontal")) {
                        archivos2.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Izquierda")) {
                        archivos3.add(doc);
                    }
                    if (doc.getFileName().contains("Fachada_Derecha")) {
                        archivos4.add(doc);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getPropietario(CatEnte soli) {
        if (soli.getEsPersona()) {
            return Utils.isEmpty(soli.getApellidos()) + " " + Utils.isEmpty(soli.getNombres());
        } else {
            return soli.getRazonSocial();
        }
    }

    /**
     * Muestra el dialogo para generar el reporte.
     */
    public void mostrar() {
        Calendar cl = Calendar.getInstance();
        anioDesde = (cl.get(Calendar.YEAR));
        memorandum2 = memorandum2 + "" + anioDesde;
        JsfUti.update("frmConsulta");
        JsfUti.executeJS("PF('dlgConsulta').show()");
    }

    /**
     * Imprimir el reporte de todos las inspecciones que se han generado de un
     * año determinado y los números de reporte
     */
    public void reporteSemanalIF() {
        ss.instanciarParametros();
        ss.setNombreReporte("reporteInspeccion");
        ss.setNombreSubCarpeta("inspeccionFinal");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("MEMORANDUM", memorandum + " " + memorandum2);
        ss.agregarParametro("DE", new Long(permisoDesde));
        ss.agregarParametro("HASTA", new Long(permisoHasta));
        ss.agregarParametro("DESDE_ANIO", new Long(anioDesde));
        ss.agregarParametro("DIRIGIDO_A", dirigioA);
        ss.agregarParametro("CARGO", cargo);

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    /**
     * Llena todas las ciudadelas en un {@link SelectItem}
     *
     * @return Arreglo de {@link SelectItem} con los nombre de las ciudadelas.
     */
    public SelectItem[] getListCiudadelas() {
        int cantRegis = getListCiudadela().size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            String nombre = getListCiudadela().get(i).getNombre();
            options[i + 1] = new SelectItem(nombre, nombre);
        }
        return options;
    }

    /**
     * Carga todas la ciudadelas
     *
     * @return Lista de {@link CatCiudadela}.
     */
    public List<CatCiudadela> getListCiudadela() {
        return permisoServices.getFichaServices().getCiudadelas();
    }

    public void obtenerArchivosSubidos(HistoricoTramites ht) {
        List<HistoricoArchivo> archivos = services.findAll(Querys.getHistoricoArchivosList, new String[]{"tramiteId", "carpeta"}, new Object[]{ht.getIdTramite(), "fotosInspeccion"});
        HistoricoArchivo temp;
        if (archivos == null) {
            return;
        } else {
            temp = archivos.get(archivos.size() - 1);
        }

        listaArchivos = manageBeanBase.getProcessInstanceAttachmentsFiles(temp.getProcessInstance());
    }

    public void llenarImagenes() {
        List<Attachment> archivos = new ArrayList();
        List<Attachment> archivos2 = new ArrayList();
        List<Attachment> archivos3 = new ArrayList();
        List<Attachment> archivos4 = new ArrayList();
        for (Attachment att : listaArchivos) {
            if (att.getDescription().equals("tipo1")) {
                archivos.add(att);
            }
            if (att.getDescription().equals("tipo2")) {
                archivos2.add(att);
            }
            if (att.getDescription().equals("tipo3")) {
                archivos3.add(att);
            }
            if (att.getDescription().equals("tipo4")) {
                archivos4.add(att);
            }
        }

        String s[] = null, url;
        InputStream is;
        PdfReporte reporte = new PdfReporte();
        int centinela;
        try {

            centinela = archivos.size();
            for (int i = 0; i < 3; i++) {
                if (centinela > i) {
                    url = archivos.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if (s != null) {
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        ss.agregarParametro("img" + (i + 1), is);
                    } else {
                        ss.agregarParametro("img" + (i + 1), null);
                    }
                } else {
                    ss.agregarParametro("img" + (i + 1), null);
                }
            }
            centinela = archivos2.size();
            for (int i = 0; i < 3; i++) {
                if (centinela > i) {
                    url = archivos2.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if (s != null) {
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        ss.agregarParametro("img" + (i + 4), is);
                    } else {
                        ss.agregarParametro("img" + (i + 4), null);
                    }
                } else {
                    ss.agregarParametro("img" + (i + 7), null);
                }
            }
            centinela = archivos3.size();
            for (int i = 0; i < 3; i++) {
                if (centinela > i) {
                    url = archivos3.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if (s != null) {
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        ss.agregarParametro("img" + (i + 7), is);
                    } else {
                        ss.agregarParametro("img" + (i + 7), null);
                    }
                } else {
                    ss.agregarParametro("img" + (i + 7), null);
                }
            }
            centinela = archivos4.size();
            for (int i = 0; i < 3; i++) {
                if (centinela > i) {
                    url = archivos4.get(i).getUrl();
                    s = url.split("nodeRef=");
                    if (s != null) {
                        is = reportes.descargarByteArrayDesdeAlfrescoPorURL(s[1]);
                        ss.agregarParametro("img" + (i + 10), is);
                    } else {
                        ss.agregarParametro("img" + (i + 10), null);
                    }
                } else {
                    ss.agregarParametro("img" + (i + 10), null);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error", e);
        }

    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public PeInspeccionFinalLazy getInspeccionlazy() {
        return inspeccionlazy;
    }

    public void setInspeccionlazy(PeInspeccionFinalLazy inspeccionlazy) {
        this.inspeccionlazy = inspeccionlazy;
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

    public String getMemorandum() {
        return memorandum;
    }

    public void setMemorandum(String memorandum) {
        this.memorandum = memorandum;
    }

    public String getMemorandum2() {
        return memorandum2;
    }

    public void setMemorandum2(String memorandum2) {
        this.memorandum2 = memorandum2;
    }

    public String getDirigioA() {
        return dirigioA;
    }

    public void setDirigioA(String dirigioA) {
        this.dirigioA = dirigioA;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPermisoDesde() {
        return permisoDesde;
    }

    public void setPermisoDesde(String permisoDesde) {
        this.permisoDesde = permisoDesde;
    }

    public String getPermisoHasta() {
        return permisoHasta;
    }

    public void setPermisoHasta(String permisoHasta) {
        this.permisoHasta = permisoHasta;
    }

    public int getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(int anioDesde) {
        this.anioDesde = anioDesde;
    }

    public BpmManageBeanBase getManageBeanBase() {
        return manageBeanBase;
    }

    public void setManageBeanBase(BpmManageBeanBase manageBeanBase) {
        this.manageBeanBase = manageBeanBase;
    }

    public List<PeInspeccionFinal> getInspecciones() {
        return inspecciones;
    }

    public void setInspecciones(List<PeInspeccionFinal> inspecciones) {
        this.inspecciones = inspecciones;
    }

    public EditarFotosViewIF getEditFotos() {
        return editFotos;
    }

    public void setEditFotos(EditarFotosViewIF editFotos) {
        this.editFotos = editFotos;
    }

    public GuardadoIF getInspeccionManager() {
        return inspeccionManager;
    }

    public void setInspeccionManager(GuardadoIF inspeccionManager) {
        this.inspeccionManager = inspeccionManager;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

}
