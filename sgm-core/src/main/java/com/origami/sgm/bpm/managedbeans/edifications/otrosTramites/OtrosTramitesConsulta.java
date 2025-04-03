/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.OtrosTramitesHasPermiso;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.lazymodels.HistoricoTramiteDetLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class OtrosTramitesConsulta implements Serializable {

    private static final Logger LOG = Logger.getLogger(OtrosTramitesConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    protected Entitymanager services;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    @Inject
    private BpmManageBeanBase base;
    @Inject
    private ServletSession servletSession;

    protected HistoricoTramiteDetLazy lazy;
    protected AclUser firmaDir;
    protected String path;
    protected Boolean tieneCertificado;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;
    private HistoricoTramites ht;
    private OtrosTramites ot;
    private HistoricoTramiteDet htd;
    private CatEnte responsableTec, solicitante;
    private OtrosTramitesHasPermiso othp;
    private PeFirma firma;

    public OtrosTramitesConsulta() {
    }

    @PostConstruct
    public void initView() {
        lazy = new HistoricoTramiteDetLazy(14L);

        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        firmaDir = new AclUser();
        firma = (PeFirma) services.find(Querys.getPeFirmaByID, new String[]{"id"}, new Object[]{new Long(4)});
    }

    public Boolean tieneLiqui(HistoricoTramiteDet tram) {
        OtrosTramites otros = tram.getTramite().getSubTipoTramite();
        if (otros.getTipoDeTramite() != null) {
            return otros.getTipoDeTramite().getIdentificacion().intValue() == 2
                    || otros.getTipoDeTramite().getIdentificacion().intValue() == 3;
        }
        return false;
    }

    public Boolean tieneCertf(HistoricoTramiteDet tram) {
        OtrosTramites otros = tram.getTramite().getSubTipoTramite();
        if (otros.getTipoDeTramite() != null) {
            return otros.getTipoDeTramite().getIdentificacion().intValue() == 3;
        }
        return false;
    }

    /**
     * Descarga la liquidación del repositorio que fue generado.
     *
     * @param liquidacion Entity {@link HistoricoTramiteDet}
     */
    public void imprimirLiquidacionGuardado(HistoricoTramiteDet liquidacion) throws IOException {
        htd = liquidacion;
        ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
        ot = ht.getSubTipoTramite();
        othp = ht.getOtrosTramitesHasPermiso();
        
        if(ot.getTipoDeTramite().getId() == 2 || ot.getTipoDeTramite().getId() == 3)        
            imprimirLiquidacionPDF();
        else
            JsfUti.messageInfo(null, "Info", "Este trámite no genera liquidación");
            /*
        for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
            if (r.getNombreReporte().equalsIgnoreCase("TasaLiq_OT-" + solicitante.getCiRuc())) { //Certificado_OT
                if (r.getEstado() && r.getUrl() != null) {
                    hrt = r;
                }
            }
        }
        if (hrt != null) {
            base.descargarDocumento(hrt.getUrl());
        } else {
            JsfUti.messageError(null, "No se ha generado la liquidación aun", "");
        }
                    */
    }
    
    public void imprimirCertificadoGuardado(HistoricoTramiteDet liquidacion) {
        try{
            ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
            ot = ht.getSubTipoTramite();
            HistoricoReporteTramite hrt = null;
            CatEnte solicitante = permisoServices.getFichaServices().getCatEnteById(liquidacion.getTramite().getSolicitante().getId());
            if(ot.getTipoDeTramite().getId() == 2 || ot.getTipoDeTramite().getId() == 3)        
                imprimirCertificadoPDF();
            else
                JsfUti.messageInfo(null, "Info", "Este trámite no genera certificado");
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirLiquidacionPDF() throws IOException {
        
        try{
            CatPredio datosPredio = null;
            CatEnte solicitante = ht.getSolicitante();
            if(ht.getNumPredio()!=null)
                datosPredio = (CatPredio)services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
            servletSession.instanciarParametros();
            
            switch(Integer.parseInt(ot.getTipoSeleccion()+"")){
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
            
            if (solicitante.getEsPersona()) {
                servletSession.agregarParametro("nomPropietario", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
                servletSession.agregarParametro("nomSolicitante", solicitante.getNombres().toUpperCase() + " " + solicitante.getApellidos().toUpperCase());
            } else {
                servletSession.agregarParametro("nomPropietario", solicitante.getRazonSocial().toUpperCase());
                servletSession.agregarParametro("nomSolicitante", solicitante.getRazonSocial().toUpperCase());
            }
            servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());
            servletSession.agregarParametro("numReporte", ht.getNumTramiteXDepartamento()+"-"+new SimpleDateFormat("yyyy").format(htd.getFecCre()));

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
            servletSession.agregarParametro("descripcionTramite", ht.getTipoTramiteNombre());
            if(ht!=null && ht.getOtrosTramitesHasPermiso()!=null && ht.getOtrosTramitesHasPermiso().getResponsableTec()!=null){
                responsableTec=(CatEnte)services.find(CatEnte.class, new Long(ht.getOtrosTramitesHasPermiso().getResponsableTec()+""));
            }
            if(responsableTec!=null){
                servletSession.agregarParametro("responsable", responsableTec.getNombres() + " " + responsableTec.getApellidos());
                servletSession.agregarParametro("ciResponsable", responsableTec.getCiRuc());
                servletSession.agregarParametro("regProf", responsableTec.getRegProf());
            }
            
            
            if(htd.getTotal()!=null)
                servletSession.agregarParametro("presupuesto", htd.getTotal().divide(new BigDecimal(ot.getBaseCalculo().getValorBase()), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)+"");
            servletSession.agregarParametro("numTramite", ht.getId()+"-"+new SimpleDateFormat("yyyy").format(htd.getFecCre()));

            servletSession.agregarParametro("descripcion", othp.getDescripcion() == null ? "" : othp.getDescripcion().toUpperCase());
            if(ot.getBaseCalculo()==null)
                servletSession.agregarParametro("base1", "***");
            else
                servletSession.agregarParametro("base1", ot.getBaseCalculo().getValorBase());
            if(othp.getFactor2()==null)
                servletSession.agregarParametro("base2", "***");
            else
                servletSession.agregarParametro("base2", othp.getFactor2());
            servletSession.agregarParametro("total", htd.getTotal()+"");
            servletSession.agregarParametro("nombreIng", firma.getNomCompleto().toUpperCase());
            servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+ " " + firma.getDepartamento());
            //servletSession.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + hrt.getCodValidacion());
            servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            servletSession.setNombreReporte("TasaLiquidacionOT");
            servletSession.setNombreSubCarpeta("otrosTramites");
            servletSession.setTieneDatasource(false);

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirCertificadoPDF(){
        servletSession.instanciarParametros();
        solicitante = ht.getSolicitante();
        CatEnte responsable = null;
        CatPredio datosPredio = null;
        PePermiso permiso = null;
        
        if(ht.getNumPredio()!=null)
            datosPredio = (CatPredio)services.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()});
        othp = ht.getOtrosTramitesHasPermiso();
        if(othp!=null){
            permiso = othp.getPePermiso();
            if(othp.getResponsableTec()!=null){
                //responsable = (CatEnte) services.find(CatEnte.class, othp.getResponsableTec());
                responsable=(CatEnte)services.find(CatEnte.class, new Long(othp.getResponsableTec()+""));
            }
            if(othp.getFechaCaducidad() != null){
                servletSession.agregarParametro("diaC",new SimpleDateFormat("dd").format(othp.getFechaCaducidad()));
                servletSession.agregarParametro("mesC",new SimpleDateFormat("MM").format(othp.getFechaCaducidad()));
                servletSession.agregarParametro("anioC",new SimpleDateFormat("yyyy").format(othp.getFechaCaducidad()));
            }
        }
        if (solicitante.getEsPersona()) {
            servletSession.agregarParametro("nombres", solicitante.getNombres() + " " + solicitante.getApellidos());
        } else {
            servletSession.agregarParametro("nombres", solicitante.getNombreComercial());
        }

        servletSession.agregarParametro("numTramite",ht.getId()+"-"+new SimpleDateFormat("yyyy").format(new Date()));
        servletSession.agregarParametro("numReporte", ht.getNumTramiteXDepartamento()+"-"+new SimpleDateFormat("yyyy").format(new Date()));
        servletSession.agregarParametro("ciRuc", solicitante.getCiRuc());

        servletSession.agregarParametro("diaE",new SimpleDateFormat("dd").format(new Date()));
        servletSession.agregarParametro("mesE",new SimpleDateFormat("MM").format(new Date()));
        servletSession.agregarParametro("anioE",new SimpleDateFormat("yyyy").format(new Date()));
        

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
        servletSession.agregarParametro("nota", ot.getNota());
        servletSession.agregarParametro("tipoPermiso", ot.getTipoTramite().toUpperCase());
        servletSession.agregarParametro("nombreIng", firma.getNomCompleto());
        servletSession.agregarParametro("cargoIng", firma.getCargo().toUpperCase()+ " " + firma.getDepartamento());
        servletSession.agregarParametro("validador", "");
        if(responsable!=null){
            servletSession.agregarParametro("nombresResponsable", responsable.getNombres() + " " + responsable.getApellidos());
            servletSession.agregarParametro("regProf", responsable.getRegProf());
            servletSession.agregarParametro("ciTecnico", responsable.getCiRuc());
        }
        if(othp.getDescripcion()!=null)
                servletSession.agregarParametro("descripcion", othp.getDescripcion().toUpperCase());
        servletSession.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
        servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
        servletSession.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
        servletSession.setNombreReporte("CertificadoOT");
        servletSession.setNombreSubCarpeta("otrosTramites");
        servletSession.setTieneDatasource(true);
        
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void redirectVistaPermiso(HistoricoTramiteDet liquidacion) {
        try {
            ss.instanciarParametros();
//            if (liquidacion.getObservacion().contentEquals("Trámite migrado")) {
//
//            } else {
            ss.agregarParametro("tramite", liquidacion.getId());
//            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/otrosTramites/editarLiquidacionOtrosTram.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA VISUALIZACION", e);
        }
    }

    public void redirectEditarPermiso(HistoricoTramiteDet liquidacion) {
        try {
            if (liquidacion.getTramite().getValorLiquidacion() == null && liquidacion.getTotal() == null) {
                JsfUti.messageError(null, "", "No se genarado la Tasa de liquidación");
            } else {
                ss.instanciarParametros();
                ss.agregarParametro("tramite", liquidacion.getId());
                JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/otrosTramites/editarLiquidacionOtrosTram.xhtml");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA EDICION", e);
        }
    }

    public void mostrar() {
        Calendar cl = Calendar.getInstance();
        anioDesde = (cl.get(Calendar.YEAR));
        memorandum2 = memorandum2 + "" + anioDesde;
        JsfUti.update("frmConsulta");
        JsfUti.executeJS("PF('dlgConsulta').show()");
    }

    public void reporteSemanalPC() {
        ss.instanciarParametros();
        ss.setNombreReporte("listado_permiso_construccion");
        ss.setNombreSubCarpeta("permisoConstruccion");
        ss.setTieneDatasource(Boolean.TRUE);
        ss.agregarParametro("MEMO", memorandum + " " + memorandum2);
        ss.agregarParametro("DE", new Long(permisoDesde));
        ss.agregarParametro("HASTA", new Long(permisoHasta));
        ss.agregarParametro("DESDE_ANIO", new Long(anioDesde));
        ss.agregarParametro("DIRIGIDO_A", dirigioA);
        ss.agregarParametro("CARGO", cargo);

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public HistoricoTramiteDetLazy getLazy() {
        return lazy;
    }

    public void setLazy(HistoricoTramiteDetLazy lazy) {
        this.lazy = lazy;
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

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

    public Boolean getTieneCertificado() {
        return tieneCertificado;
    }

    public void setTieneCertificado(Boolean tieneCertificado) {
        this.tieneCertificado = tieneCertificado;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public CatEnte getResponsableTec() {
        return responsableTec;
    }

    public void setResponsableTec(CatEnte responsableTec) {
        this.responsableTec = responsableTec;
    }
    
}
