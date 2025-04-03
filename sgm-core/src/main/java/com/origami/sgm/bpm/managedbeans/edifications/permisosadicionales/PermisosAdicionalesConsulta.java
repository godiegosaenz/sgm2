/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisosadicionales;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBase;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.lazymodels.HistoricoTramiteDetLazy;
import com.origami.sgm.lazymodels.PePermisosAdicionalesLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class PermisosAdicionalesConsulta implements Serializable {

    private static final Logger LOG = Logger.getLogger(PermisosAdicionalesConsulta.class.getName());

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession sess;
    @Inject
    private BpmManageBeanBase base;

    protected HistoricoTramiteDetLazy lazy;
    protected PePermisosAdicionalesLazy permisosAdicionales;
    protected AclUser firmaDir;
    protected AclUser user;
    protected String path;
    protected Boolean tieneTasa = false;

    protected int anioDesde;
    protected String memorandum;
    protected String memorandum2 = " LPC-DPU-";
    protected String dirigioA;
    protected String cargo;
    protected String permisoDesde;
    protected String permisoHasta;

    public PermisosAdicionalesConsulta() {
    }

    @PostConstruct
    public void initView() {
        GeTipoTramite tra = permisoServices.getGeTipoTramiteById(6L);
        lazy = new HistoricoTramiteDetLazy(6L);
        permisosAdicionales = new PePermisosAdicionalesLazy();
        path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        firmaDir = new AclUser();
        if (tra.getUserDireccion() != null) {
            firmaDir = permisoServices.getAclUserByUser(tra.getUserDireccion());
        }
        user = permisoServices.getAclUserById(sess.getUserId());
    }

    public void imprimirLiquidación(PePermisosAdicionales permiso) {
        HistoricoTramites ht = permisoServices.getHistoricoTramiteById(permiso.getNumTramite());
        List<CatPredioPropietario> propietarios, lisPropietarios = new ArrayList();
        CatEnte prop = null;

        try {
            ss.instanciarParametros();

            CatPredio predio = permiso.getPredio();
            propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();

            for (CatPredioPropietario temp : propietarios) {
                if (temp.getEstado().equals("A")) {
                    lisPropietarios.add(temp);
                }
            }
            if (lisPropietarios != null && !lisPropietarios.isEmpty()) {
                prop = lisPropietarios.get(0).getEnte();
            }
            ss.agregarParametro("imsade", permiso.getImpMunicipalAreaedif());
            ss.agregarParametro("inspeccion", permiso.getInspeccion());
            ss.agregarParametro("revyaprovplanos", permiso.getRevisionAprobPlanos());
            ss.agregarParametro("deudaMunicipio", permiso.getNoAdeudarMun());

            ss.agregarParametro("numReporte", permiso.getNumReporte() + "-" + new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));
            HistoricoTramiteDet htd = null;
            if (ht != null) {
                ss.agregarParametro("numTramite", ht.getId() + "-" + new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));
                if (prop != null) {
                    if (prop.getEsPersona()) {
                        ss.agregarParametro("nomPropietario", prop.getNombres() + " " + prop.getApellidos());
                    } else {
                        ss.agregarParametro("nomPropietario", prop.getRazonSocial());
                    }
                    ss.agregarParametro("ciPropietario", prop.getCiRuc());
                }

                htd = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());

                if (Utils.isEmpty((List<?>) ht.getHistoricoReporteTramiteCollection())) {
                    for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
                        if (r.getEstado()) {
                            ss.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + r.getCodValidacion());
                        }
                    }
                } else {
                    ss.agregarParametro("codigoQR", null);
                }

            } else {
                if (prop != null) {
                    if (prop.getEsPersona()) {
                        ss.agregarParametro("nomPropietario", prop.getNombres() + " " + prop.getApellidos());
                    } else {
                        ss.agregarParametro("nomPropietario", prop.getRazonSocial());
                    }
                    ss.agregarParametro("ciPropietario", prop.getCiRuc());
                }
            }
            if (htd != null) {
                ss.agregarParametro("nombreIng", htd.getFirma().getNomCompleto());
                ss.agregarParametro("cargoIng", htd.getFirma().getCargo() + " " + htd.getFirma().getDepartamento());
            } else {
                if (firmaDir != null) {
                    if (firmaDir.getFirma() != null) {
                        ss.agregarParametro("nombreIng", firmaDir.getFirma().getNomCompleto());
                        ss.agregarParametro("cargoIng", firmaDir.getFirma().getCargo() + " " + firmaDir.getFirma().getDepartamento());
                    } else {
                        ss.agregarParametro("nombreIng", "");
                        ss.agregarParametro("cargoIng", "");
                    }
                }
            }

            ss.agregarParametro("nombreCiudad", "Samborondón");
            ss.agregarParametro("fechaIngreso", new SimpleDateFormat("dd/MM/yyyy").format(permiso.getFechaEmision()));
            ss.agregarParametro("tipoPermiso", permiso.getTipoPermisoAdicional().getDescripcion());
            ss.agregarParametro("dia", new SimpleDateFormat("dd").format(permiso.getFechaEmision()));
            ss.agregarParametro("mes", new SimpleDateFormat("MM").format(permiso.getFechaEmision()));
            ss.agregarParametro("anio", new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));
            ss.agregarParametro("sector", "La Puntilla");
            if (permiso.getPredio().getCiudadela() != null) {
                ss.agregarParametro("ciudadela", permiso.getPredio().getCiudadela().getNombre());
            } else {
                ss.agregarParametro("ciudadela", "**********");
            }
            ss.agregarParametro("calle", "Vehicular");
            ss.agregarParametro("mz", permiso.getPredio().getMz());
            ss.agregarParametro("solar", permiso.getPredio().getSolar());
            ss.agregarParametro("codCatastral", permiso.getPredio().getCodigoPredialCompleto());
            if (permiso.getRespTecn() != null) {
                ss.agregarParametro("nombresRespTecnico", base.getNombrePropietario(permiso.getRespTecn()));
                ss.agregarParametro("regProfResponsable", Utils.isEmpty(permiso.getRespTecn().getRegProf()));
                ss.agregarParametro("ciResponsable", Utils.isEmpty(permiso.getRespTecn().getCiRuc()));
            }
            
            ss.agregarParametro("npisossnbPC", permiso.getNumPisosSnb());
            ss.agregarParametro("alturaConsPC", permiso.getAlturaConst());
            ss.agregarParametro("areaSolar", permiso.getAreaSolar());
            ss.agregarParametro("npisosbnb", permiso.getNumPisosBnb());
            ss.agregarParametro("totalAEdificar", permiso.getTotalEdificar());
            
            ss.agregarParametro("descripcion", permiso.getDescripcion());
            ss.agregarParametro("lineaFabrica", permiso.getLineaFabrica());
            ss.agregarParametro("totalAPagar", permiso.getTotalPagar());
            if (permiso.getPermiso() != null) {
                ss.agregarParametro("permisoConst", permiso.getPermiso().getId());
            } else {
                ss.agregarParametro("permisoConst", "");
            }
            if (permiso.getUsuario() != null) {
                if (permiso.getUsuario().getEnte() != null) {
                    ss.agregarParametro("responsable", base.getNombrePropietario(permiso.getUsuario().getEnte()));
                } else {
                    ss.agregarParametro("responsable", "");
                }
            } else {
                if (user.getEnte() != null) {
                    ss.agregarParametro("responsable", base.getNombrePropietario(user.getEnte()));
                } else {
                    ss.agregarParametro("responsable", "");
                }
            }

            ss.agregarParametro("permiso_adicional", permiso.getId());
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
            ss.agregarParametro("logo_img", JsfUti.getRealPath(SisVars.logoReportes));
            ss.agregarParametro("firma_img", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            ss.setNombreSubCarpeta("permisosAdicionales");
            ss.setNombreReporte("TasaLiq_PA");
            ss.setTieneDatasource(true);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL GENERAR LIQUIDACIÓN", e);
        }

        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public void imprimirCertificado(PePermisosAdicionales permiso) {
        List<CatPredioPropietario> propietarios, lisPropietarios = new ArrayList();
        CatEnte prop = null;

        try {
            ss.instanciarParametros();

            CatPredio predio = permiso.getPredio();
            propietarios = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();

            for (CatPredioPropietario temp : propietarios) {
                if (temp.getEstado().equals("A")) {
                    lisPropietarios.add(temp);
                }
            }
            if (lisPropietarios != null && !lisPropietarios.isEmpty()) {
                prop = lisPropietarios.get(0).getEnte();
            }

            HistoricoTramites ht = permisoServices.getHistoricoTramiteById(permiso.getNumTramite());
            HistoricoTramiteDet htd = null;

            if (ht != null) {
                ss.agregarParametro("numReporte", permiso.getNumReporte() + "-" + new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));
                if (prop != null) {
                    if (prop.getEsPersona()) {
                        ss.agregarParametro("nombrePropietario", prop.getNombres() + " " + prop.getApellidos());
                    } else {
                        ss.agregarParametro("nombrePropietario", prop.getRazonSocial());
                    }
                    ss.agregarParametro("ciRuc", prop.getCiRuc());
                }
                ss.agregarParametro("tramite", ht.getId() + "-" + new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));

                htd = permisoServices.getHistoricoTramiteDetByTramite(ht.getIdTramite());

                if (Utils.isEmpty((List<?>) ht.getHistoricoReporteTramiteCollection())) {
                    for (HistoricoReporteTramite r : ht.getHistoricoReporteTramiteCollection()) {
                        if (r.getEstado()) {
                            ss.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + r.getCodValidacion());
                        }
                    }
                } else {
                    ss.agregarParametro("codigoQR", SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + null);
                }

            } else {
                if (permiso.getPropietario() != null) {
                    ss.agregarParametro("nombrePropietario", base.getNombrePropietario(permiso.getPropietario()));
                    ss.agregarParametro("ciRuc", permiso.getPropietario().getCiRuc());
                } else {
                    ss.agregarParametro("nombrePropietario", " ");
                    ss.agregarParametro("ciRuc", "");
                }
            }

            ss.agregarParametro("diaE", new SimpleDateFormat("dd").format(permiso.getFechaEmision()));
            ss.agregarParametro("mesE", new SimpleDateFormat("MM").format(permiso.getFechaEmision()));
            ss.agregarParametro("anioE", new SimpleDateFormat("yyyy").format(permiso.getFechaEmision()));
            ss.agregarParametro("diaC", new SimpleDateFormat("dd").format(permiso.getFechaCaducidad()));
            ss.agregarParametro("mesC", new SimpleDateFormat("MM").format(permiso.getFechaCaducidad()));
            ss.agregarParametro("anioC", new SimpleDateFormat("yyyy").format(permiso.getFechaCaducidad()));
            ss.agregarParametro("tipoPermiso", permiso.getTipoPermisoAdicional().getDescripcion().toUpperCase());

            ss.agregarParametro("sector", "La Puntilla");
            ss.agregarParametro("calle", "Vehicular");
            if (permiso.getPredio() != null) {
                ss.agregarParametro("mz", permiso.getPredio().getUrbMz());
                ss.agregarParametro("solar", permiso.getPredio().getUrbSolarnew());
                ss.agregarParametro("codigoCatastral", permiso.getPredio().getCodigoPredialCompleto());
                ss.agregarParametro("urbanizacion", permiso.getPredio().getCiudadela().getNombre());
            }
            if (permiso.getEstructura() != null) {
                ss.agregarParametro("estructura", permiso.getEstructura().getNombre());
            }
            if (permiso.getInstalaciones() != null) {
                ss.agregarParametro("instalacion", permiso.getInstalaciones().getNombre());
            }
            if (permiso.getPlantaAlta() != null && permiso.getPlantaBaja() != null) {
                ss.agregarParametro("pisos", permiso.getPlantaAlta().getNombre() + "-" + permiso.getPlantaBaja().getNombre());
            }
            if (permiso.getCubierta() != null) {
                ss.agregarParametro("cubierta", permiso.getCubierta().getNombre());
            }
            if (permiso.getParedes() != null) {
                ss.agregarParametro("paredes", permiso.getParedes().getNombre());
            }
            
            ss.agregarParametro("npisossnbPC", permiso.getNumPisosSnb());
            ss.agregarParametro("alturaConsPC", permiso.getAlturaConst());
            ss.agregarParametro("areaSolar", permiso.getAreaSolar());
            ss.agregarParametro("npisosbnb", permiso.getNumPisosBnb());
            ss.agregarParametro("totalAEdificar", permiso.getTotalEdificar());
            
            ss.agregarParametro("lineaDeFabrica", permiso.getLineaFabrica());
            ss.agregarParametro("descripcion", permiso.getDescripcion());
            ss.agregarParametro("idPermisoAdicional", permiso.getId());
            if (permiso.getPermiso() != null) {
                ss.agregarParametro("permisoConstruccion", permiso.getPermiso().getId());
            }

            if (firmaDir != null) {
                if (firmaDir.getFirma() != null) {
                    ss.agregarParametro("nombreIng", firmaDir.getFirma().getNomCompleto());
                    ss.agregarParametro("cargoIng", firmaDir.getFirma().getCargo() + " " + firmaDir.getFirma().getDepartamento());
                } else {
                    ss.agregarParametro("nombreIng", "");
                    ss.agregarParametro("cargoIng", "");
                }
            }

            ss.agregarParametro("validador", "");
            if (permiso.getRespTecn() != null) {
                ss.agregarParametro("nombresResponsable", base.getNombrePropietario(permiso.getRespTecn()));
                ss.agregarParametro("regProf", Utils.isEmpty(permiso.getRespTecn().getRegProf()));
                ss.agregarParametro("ciTecnico", Utils.isEmpty(permiso.getRespTecn().getCiRuc()));
            }
            ss.agregarParametro("firmaImg", JsfUti.getRealPath("//css//firmas//lilianaGuerrero.jpg"));
            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("//reportes//permisosAdicionales//"));
            ss.agregarParametro("logo", JsfUti.getRealPath(SisVars.logoReportes));

            ss.setNombreSubCarpeta("permisosAdicionales");
            ss.setNombreReporte("CertificadoPermisosAdicionales");
            ss.setTieneDatasource(true);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redirectVistaPermiso(PePermisosAdicionales liquidacion) {
        try {
            ss.instanciarParametros();
            ss.agregarParametro("tramite", liquidacion.getId());
            ss.agregarParametro("edicion", false);
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisosAdicionales/vistaLiquidacionPA.xhtml");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR AL REDIRECCIONAR A LA VISUALIZACION", e);
        }
    }

    public void redirectEditarPermiso(PePermisosAdicionales liquidacion) {
        try {
            ss.instanciarParametros();
            ss.agregarParametro("tramite", liquidacion.getId());
            ss.agregarParametro("edicion", true);
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/permisosAdicionales/edicionLiquidacionPA.xhtml");
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

    public PePermisosAdicionalesLazy getPermisosAdicionales() {
        return permisosAdicionales;
    }

    public void setPermisosAdicionales(PePermisosAdicionalesLazy permisosAdicionales) {
        this.permisosAdicionales = permisosAdicionales;
    }

    public BpmManageBeanBase getBase() {
        return base;
    }

    public void setBase(BpmManageBeanBase base) {
        this.base = base;
    }

}
