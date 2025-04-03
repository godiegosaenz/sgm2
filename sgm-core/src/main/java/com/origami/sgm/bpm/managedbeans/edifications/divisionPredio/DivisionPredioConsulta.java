package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.HistoricoTramiteDetLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
@SuppressWarnings("serial")
public class DivisionPredioConsulta implements Serializable {

    private static final Logger LOG = Logger.getLogger(DivisionPredioConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    @Inject
    private BpmManageBeanBase base;

    protected HistoricoTramiteDetLazy lazy;
    protected AclUser firmaDir;
    protected String path;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;
    
    private HistoricoTramites ht;

    public DivisionPredioConsulta() {
    }

    @PostConstruct
    public void initView() {
        if (sess != null) {
            lazy = new HistoricoTramiteDetLazy(7L);

            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            firmaDir = new AclUser();
        } else {
            base.continuar();
        }
    }

    /**
     * Descarga la liquidación del repositorio que fue generado.
     *
     * @param liquidacion Entity {@link HistoricoTramiteDet}
     */
    public void imprimirLiquidacionGuardado(HistoricoTramiteDet liquidacion) {
        try{
            ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
            this.imprimirLiquidación(liquidacion);
            return;
            
            /*
            HistoricoReporteTramite hrt = null;
            CatEnte solicitante = permisoServices.getFichaServices().getCatEnteById(liquidacion.getTramite().getSolicitante().getId());

            for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
                if (r.getNombreReporte().equalsIgnoreCase("TasaLiq_DivisionPredio-" + solicitante.getCiRuc())) {
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirLiquidación(HistoricoTramiteDet htd){
        ss.instanciarParametros();
        List<CatPredioPropietario> propietarios;
        CatPredio predio = htd.getPredio();
        propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        
        try{
            ss.agregarParametro("nombreCanton", "Gobierno Autónomo Descentralizado Municipal del Cantón Samborondón");
            ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("lugarYFecha", predio.getCiudadela().getCodParroquia().getDescripcion()+", "+new SimpleDateFormat("dd/MM/yyyy").format(htd.getFecCre()));

            ss.agregarParametro("numReporte", htd.getNumTasa());
            ss.agregarParametro("numeroTramite", htd.getTramite().getId()+"-"+new SimpleDateFormat("yyyy").format(htd.getFecCre()));
            ss.agregarParametro("seleccionadoImg", JsfUti.getRealPath("/css/homeIconsImages/seleccionado.png"));
            if(!propietarios.isEmpty()){
                ss.agregarParametro("nombres", getPropietario(propietarios.get(0).getEnte()));            
                ss.agregarParametro("ciRuc", propietarios.get(0).getEnte().getCiRuc());
            }
            ss.agregarParametro("canton", "Samborondón");
            ss.agregarParametro("parroquia", predio.getCiudadela().getCodParroquia().getDescripcion());
            ss.agregarParametro("urbanizacion", predio.getCiudadela().getNombre());
            ss.agregarParametro("calle", "Vehicular");
            ss.agregarParametro("codigoCatastral", predio.getCodigoPredialCompleto());
            ss.agregarParametro("mz", "" + predio.getUrbMz());
            ss.agregarParametro("solar", "" + predio.getUrbSolarnew());
            if(htd.getAreaEdificacion()!=null)
                ss.agregarParametro("areaEdif", "" + htd.getAreaEdificacion());
            if(htd.getAvaluoConstruccion()!=null)
                ss.agregarParametro("avaluoConstruccion", "" + htd.getAvaluoConstruccion().setScale(2, RoundingMode.CEILING).toString());
            if(htd.getAvaluoSolar()!=null)
                ss.agregarParametro("avaluoSolar", "" + htd.getAvaluoSolar().setScale(2, RoundingMode.CEILING).toString());
            if(htd.getAvaluoPropiedad()!=null)
                ss.agregarParametro("avaluoPropiedad", "" + htd.getAvaluoPropiedad().setScale(2, RoundingMode.CEILING).toString());
            if(htd.getDescripcion()!=null)
                ss.agregarParametro("descripcion", "" + htd.getDescripcion());
            ss.agregarParametro("baseCalculo1", "" + htd.getBaseCalculo().setScale(2, RoundingMode.CEILING));
            ss.agregarParametro("baseCalculo2", "");
            ss.agregarParametro("totalAPagar", "" + htd.getTotal());
            ss.agregarParametro("nombreIng", htd.getFirma().getNomCompleto());
            ss.agregarParametro("pisosSNB", htd.getNumPisosSobreBord());
            ss.agregarParametro("pisosBNB", htd.getNumPisosBajoBord());
            ss.agregarParametro("areaEdif", htd.getAreaEdificacion());
            ss.agregarParametro("cargoIng", htd.getFirma().getCargo() + " " + htd.getFirma().getDepartamento());
            ss.agregarParametro("responsable", htd.getResponsable());
            ss.agregarParametro("numValidador", "");
            ss.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas/lilianaGuerrero.jpg"));
            ss.agregarParametro("codigoQR", "Trámite migrado");
            ss.setNombreReporte("divisionPredioReporte");
            ss.setTieneDatasource(false);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Descarga el certificado desde el repositorio.
     *
     * @param liquidacion Entity {@link HistoricoTramiteDet}
     */
    public void imprimirCertificadoGuardado(HistoricoTramiteDet liquidacion) {
//        HistoricoTramites ht = permisoServices.getHistoricoTramiteById(liquidacion.getTramite().getId());
        HistoricoReporteTramite hrt = null;
        CatEnte solicitante = permisoServices.getFichaServices().getCatEnteById(liquidacion.getTramite().getSolicitante().getId());

        for (HistoricoReporteTramite r : liquidacion.getTramite().getHistoricoReporteTramiteCollection()) {
            if (r.getNombreReporte().equalsIgnoreCase("Certificado_IF-" + solicitante.getCiRuc())) {
                if (r.getEstado() && r.getUrl() != null) {
                    hrt = r;
                }
            }
        }
        if (hrt != null) {
            base.descargarDocumento(hrt.getUrl());
        } else {
            JsfUti.messageError(null, "No se ha generado el certificado aun", "");
        }
    }

    public void redirectVistaPermiso(HistoricoTramiteDet liquidacion) {
        try {
            ss.instanciarParametros();
//            if (liquidacion.getObservacion().contentEquals("Trámite migrado")) {
//
//            } else {
            ss.agregarParametro("tramite", liquidacion.getId());
//            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/verLiquidacionHTD.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA VISUALIZACION", e);
        }
    }
    
    private String getPropietario(CatEnte soli) {
        if (soli.getEsPersona()) {
            return Utils.isEmpty(soli.getApellidos()) + " " + Utils.isEmpty(soli.getNombres());
        } else {
            return soli.getRazonSocial();
        }
    }

    public void redirectEditarPermiso(HistoricoTramiteDet liquidacion) {
        try {
            ss.instanciarParametros();
//            if (liquidacion.getObservacion().contentEquals("Trámite migrado")) {
//
//            } else {
            ss.agregarParametro("tramite", liquidacion.getId());
//            }
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/editarLiquidacionHTD.xhtml");
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

}
