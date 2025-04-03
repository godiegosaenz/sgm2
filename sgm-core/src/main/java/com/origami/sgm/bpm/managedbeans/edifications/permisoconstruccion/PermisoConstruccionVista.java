/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class PermisoConstruccionVista implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    private ReportesView generarReportes;
    @Inject
    private UserSession sess;
    @Inject
    private ServletSession servletSession;

    @javax.inject.Inject
    private Entitymanager service;
    @javax.inject.Inject
    protected DivisionPredioServices divisonServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    private Boolean ListGuardado = false;
    private Boolean listoImpr = false;

    private HashMap<String, Object> paramt;
    private String nombreTec;
    private String representanteLegal;
    private String observacion;
    private Boolean imprimir = false;
    private Boolean esRenovacion = false;
    private Boolean impresoLiquidacion = false;
    private Short numEdificacion;
    protected Long idTramite;
    protected Long idPermiso;

    protected PdfReporte reporte = new PdfReporte();
    protected GeTipoTramite tramite = new GeTipoTramite();
    protected CatPredio predio;
    protected PePermiso permisoNuevo;
    protected CatEnte respTec = new CatEnte();
    protected PePermisoCabEdificacion permisoSelect;
    protected PeDetallePermiso detallePermiso = new PeDetallePermiso();
    protected CatEdfCategProp categoria = new CatEdfCategProp();
    protected CatEdfProp carateristicas = new CatEdfProp();

    protected List<CatEdfCategProp> listCat = new ArrayList<>();
    protected List<PeTipoPermiso> listRequisTra = new ArrayList<>();
    protected List<CatPredioPropietario> listaPropietarios;
    protected List<PePermisoCabEdificacion> detallesEdific;
    protected List<CatEdfProp> lisCatEdfProp = new ArrayList<>();
    protected List<PeDetallePermiso> lisPeDetallePermisos = new ArrayList<>();
    protected CatEnteLazy enteLazy;

    private HistoricoTramites ht = new HistoricoTramites();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    public void initView() {
        if (idPermiso != null) {
            listaPropietarios = new ArrayList<>();
            permisoSelect = new PePermisoCabEdificacion();
            detallesEdific = new ArrayList<>();

            listRequisTra = permisoServices.getPeTipoPermisoList();
            predio = new CatPredio();
            permisoNuevo = new PePermiso();

            PePermiso p;
            p = permisoServices.getPePermisoById(idPermiso);
            if (p != null) {
                if (p.getTramite() != null) {
                    if (p.getTramite().getId() != null) {
                        ht = permisoServices.getHistoricoTramiteById(p.getTramite().getId());
                    }

                    if (ht != null && ht.getTipoTramite() != null) {
                        tramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());
                    }
                }
                permisoNuevo = p;
                predio = p.getIdPredio();
                respTec = p.getResponsablePersona();

                if (p.getPePermisoCabEdificacionCollection().size() > 0) {
                    detallesEdific = (List<PePermisoCabEdificacion>) p.getPePermisoCabEdificacionCollection();
                    detallePermiso.setPorcentaje(new BigDecimal("0.00"));
                }

                if (detallesEdific.size() > 0) {
                    permisoSelect = detallesEdific.get(0);
                }
                permisoNuevo.setPropietarioPersona(p.getPropietarioPersona());

                permisoNuevo.setResponsablePersona(respTec);
                listCat = service.findAll(CatEdfCategProp.class);
            }
            if (predio.getNumPredio() != null) {
                consultarNumPredio();
            } else {
                consultarIdPredio();
            }
        }

    }

    public void redirecPermiso() {
        JsfUti.redirectFaces("/vistaprocesos/edificaciones/permisoConstruccion/permisoConstruccionConsulta.xhtml");
    }

    public void consultarCodPredio() {
        listaPropietarios = new ArrayList<>();
        CatPredio pred = permisoServices.getCatPredioByCodigoPredio(predio.getZona(), predio.getSector(), predio.getMz(), predio.getSolar());
        if (pred != null) {
            predio = pred;
            List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equals(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
            permisoNuevo.setIdPredio(predio);
            JsfUti.update("forGenLiq:panelCons");
        } else {
            JsfUti.messageError(null, "No hay registro con el Código de predio ingresado ir al DEPARTAMENTO DE CATASTRO PARA QUE INGRESEN EL PREDIO", "");
        }
    }

    public void consultarNumPredio() {
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
        List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
        if (!propietariosTemp.isEmpty()) {
            for (CatPredioPropietario temp : propietariosTemp) {
                if ("A".equals(temp.getEstado())) {
                    listaPropietarios.add(temp);
                }
            }
        }
        permisoNuevo.setIdPredio(predio);
    }

    public void actulizarLista() {
        if (categoria.getId() != null) {
            lisCatEdfProp = permisoServices.getCatEdfPropList(categoria.getId());
            JsfUti.update("forGenLiq:lisCaract");
        }
    }

    public void calculoAreaConstruccion() {
        BigDecimal totArea = new BigDecimal(0);
        for (PePermisoCabEdificacion det : detallesEdific) {
            totArea = totArea.add(det.getAreaConstruccion());
        }
        permisoNuevo.setAreaEdificaciones(totArea);
        if (permisoNuevo.getAreaParqueos() != null) {
            totArea = totArea.add((permisoNuevo.getAreaParqueos()));
        }
        permisoNuevo.setAreaConstruccion(totArea);
        JsfUti.update("forGenLiq:ac");
    }

    public void onRowSelect(SelectEvent event) {
        lisPeDetallePermisos = (List<PeDetallePermiso>) permisoSelect.getPeDetallePermisoCollection();
        JsfUti.update("forGenLiq:dtCartEdif");
    }

    private void consultarIdPredio() {
        if (!ht.getHistoricoTramiteDetCollection().isEmpty()) {
            List<HistoricoTramiteDet> htd = (List<HistoricoTramiteDet>) ht.getHistoricoTramiteDetCollection();
            HistoricoTramiteDet histdet = htd.get(0);
            predio = histdet.getPredio();
        }
        if (predio.getId() != null) {
            List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equals(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
            permisoNuevo.setIdPredio(predio);
        }
    }

    public void redirecDashBoard() {
        JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HashMap<String, Object> getParamt() {
        return paramt;
    }

    public void setParamt(HashMap<String, Object> paramt) {
        this.paramt = paramt;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<PeTipoPermiso> getListRequisTra() {
        return listRequisTra;
    }

    public void setListRequisTra(List<PeTipoPermiso> listRequisTra) {
        this.listRequisTra = listRequisTra;
    }

    public PePermiso getPermisoNuevo() {
        return permisoNuevo;
    }

    public void setPermisoNuevo(PePermiso permisoNuevo) {
        this.permisoNuevo = permisoNuevo;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public CatEnte getRespTec() {
        return respTec;
    }

    public void setRespTec(CatEnte respTec) {
        this.respTec = respTec;
    }

    public String getNombreTec() {
        return nombreTec;
    }

    public void setNombreTec(String nombreTec) {
        this.nombreTec = nombreTec;
    }

    public List<PePermisoCabEdificacion> getDetallesEdific() {
        return detallesEdific;
    }

    public void setDetallesEdific(List<PePermisoCabEdificacion> detallesEdific) {
        this.detallesEdific = detallesEdific;
    }

    public List<CatEdfCategProp> getListCat() {
        return listCat;
    }

    public void setListCat(List<CatEdfCategProp> listCat) {
        this.listCat = listCat;
    }

    public PeDetallePermiso getDetallePermiso() {
        return detallePermiso;
    }

    public void setDetallePermiso(PeDetallePermiso detallePermiso) {
        this.detallePermiso = detallePermiso;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        this.categoria = categoria;
    }

    public List<CatEdfProp> getLisCatEdfProp() {
        return lisCatEdfProp;
    }

    public void setLisCatEdfProp(List<CatEdfProp> lisCatEdfProp) {
        this.lisCatEdfProp = lisCatEdfProp;
    }

    public PePermisoCabEdificacion getPermisoSelect() {
        return permisoSelect;
    }

    public void setPermisoSelect(PePermisoCabEdificacion permisoSelect) {
        this.permisoSelect = permisoSelect;
    }

    public CatEdfProp getCarateristicas() {
        return carateristicas;
    }

    public void setCarateristicas(CatEdfProp carateristicas) {
        this.carateristicas = carateristicas;
    }

    public List<PeDetallePermiso> getLisPeDetallePermisos() {
        return lisPeDetallePermisos;
    }

    public void setLisPeDetallePermisos(List<PeDetallePermiso> lisPeDetallePermisos) {
        this.lisPeDetallePermisos = lisPeDetallePermisos;
    }

    public Boolean getImprimir() {
        return imprimir;
    }

    public void setImprimir(Boolean imprimir) {
        this.imprimir = imprimir;
    }

    public Boolean getEsRenovacion() {
        return esRenovacion;
    }

    public void setEsRenovacion(Boolean esRenovacion) {
        this.esRenovacion = esRenovacion;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public Boolean getImpresoLiquidacion() {
        return impresoLiquidacion;
    }

    public void setImpresoLiquidacion(Boolean impresoLiquidacion) {
        this.impresoLiquidacion = impresoLiquidacion;
    }

    public Short getNumEdificacion() {
        return numEdificacion;
    }

    public void setNumEdificacion(Short numEdificacion) {
        this.numEdificacion = numEdificacion;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public ReportesView getGenerarReportes() {
        return generarReportes;
    }

    public void setGenerarReportes(ReportesView generarReportes) {
        this.generarReportes = generarReportes;
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

    public Boolean getListGuardado() {
        return ListGuardado;
    }

    public void setListGuardado(Boolean ListGuardado) {
        this.ListGuardado = ListGuardado;
    }

    public Boolean getListoImpr() {
        return listoImpr;
    }

    public void setListoImpr(Boolean listoImpr) {
        this.listoImpr = listoImpr;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public Long getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
    }

}
