/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.RubrosPorTipoLiq;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatCategoriasPredio;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.EntityBeanCopy;
import util.Faces;
import util.JsfUti;
import util.MessagesRentas;

/**
 *
 * @author Angel
 * Navarro
 * @Date 17/05/2016
 */
@Named
@ViewScoped
public class ExoneracionesView implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private RentasServices rentasServs;

    @javax.inject.Inject
    private Entitymanager services;

    @Inject
    private UserSession session;

    @javax.inject.Inject
    private Entitymanager manager;

    @Inject
    private ServletSession ss;

    private FnExoneracionClase exoneracion;
    private BaseLazyDataModel<FnExoneracionLiquidacion> exoneraciones;
    private FnExoneracionTipo tipoExoneracion;
    private FnExoneracionLiquidacion exoConsulta;
    private RenLiquidacion original, posterior;
    private List<CatCategoriasPredio> categoriasList, tempList;
    private List<RubrosPorTipoLiq> detList1, detList2;
    private List<RenDetLiquidacion> detLiq;
    private String mensajeExoneracion;
    private Boolean usuarioAutorizado;

    @PostConstruct
    public void initView() {
        try {
            if (uSession.esLogueado()) {
                CatCategoriasPredio temp;
                exoneraciones = new BaseLazyDataModel<>(FnExoneracionLiquidacion.class, "id", "DESC");
                exoneraciones.setColunmEstado("estado");
                exoneraciones.setValueEstado(Boolean.TRUE);
                exoneracion = new FnExoneracionClase();
                categoriasList = new ArrayList();
                List<CatCategoriasPredio> tempList = (List<CatCategoriasPredio>) services.findAll(QuerysFinanciero.getCatCategoriasPredio, new String[]{"estado"}, new Object[]{true});

                for (CatCategoriasPredio t : tempList) {
                    temp = (CatCategoriasPredio) EntityBeanCopy.clone(services.find(CatCategoriasPredio.class, t.getId()));
                    categoriasList.add(temp);
                }
                usuarioAutorizado = session.getEsDirector();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void anularExoneracion(FnExoneracionLiquidacion exo) {
        try {
            this.exoConsulta = exo;
            this.exoConsulta.setEstado(Boolean.FALSE);
            this.exoConsulta.getLiquidacionOriginal().setEstadoLiquidacion(new RenEstadoLiquidacion(2L));
            this.exoConsulta.getLiquidacionPosterior().setEstadoLiquidacion(new RenEstadoLiquidacion(5L));
            this.exoConsulta.getExoneracion().setEstado(new FnEstadoExoneracion(3L));
            manager.persist(this.exoConsulta.getExoneracion());
            manager.persist(this.exoConsulta.getLiquidacionOriginal());
            manager.persist(this.exoConsulta.getLiquidacionPosterior());
            manager.persist(this.exoConsulta);
            JsfUti.messageInfo(null, "Info", "Exoneracion anulada satisfactoriamente");
        } catch (Exception e) {
            e.printStackTrace();
            JsfUti.messageInfo(null, "Info", "Hubo un error al anular la exoneracion");
        }
    }

    public void verDetalles(FnExoneracionLiquidacion exo) {
        this.exoConsulta = exo;
        mensajeExoneracion = "Tiene una exoneración de: " + exo.getExoneracion().getExoneracionTipo().getDescripcion().toUpperCase()
                + "\nNúmero de resolución: " + exo.getExoneracion().getNumResolucionSac();
        detList1 = new ArrayList();
        detList2 = new ArrayList();
        RenRubrosLiquidacion rubro;
        original = this.exoConsulta.getLiquidacionOriginal();
        detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{original});
        for (RenDetLiquidacion temp : detLiq) {
            rubro = (RenRubrosLiquidacion) services.find(RenRubrosLiquidacion.class, temp.getRubro());
            detList1.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp.getValor()));
        }
        posterior = this.exoConsulta.getLiquidacionPosterior();
        if (posterior != null) {
            if (posterior.getTotalPago() != null) {
                posterior.setTotalPago(posterior.getTotalPago().setScale(2, RoundingMode.HALF_UP));
            }
        }

        posterior.setUsuarioIngreso(uSession.getName_user());
        posterior = (RenLiquidacion) manager.persist(posterior);
        posterior = (RenLiquidacion) manager.find(RenLiquidacion.class, posterior.getId());
        detLiq = manager.findAll(QuerysFinanciero.getDetalleDeLiquidacion, new String[]{"liquidacion"}, new Object[]{posterior});
        for (RenDetLiquidacion temp2 : detLiq) {
            rubro = (RenRubrosLiquidacion) services.find(RenRubrosLiquidacion.class, temp2.getRubro());
            detList2.add(new RubrosPorTipoLiq(rubro.getDescripcion(), temp2.getValor()));
        }
    }

    public void imprimirExoClase3y6(FnExoneracionLiquidacion exo) {
        FnSolicitudExoneracion exoneracion = exo.getExoneracion();
        List<FnExoneracionLiquidacion> exoneraciones = null;
        List parametros;
        RenLiquidacion liqPost;
        BigDecimal posterior = exo.getLiquidacionPosterior().getTotalPago(), original = exo.getLiquidacionOriginal().getTotalPago();
        try {

            ss.instanciarParametros();

            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.agregarParametro("LOGO", path + SisVars.sisLogo);
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/recaudaciones/").concat("/"));
            ss.agregarParametro("FECHA", exo.getFechaIngreso());
            ss.agregarParametro("IMP_ORIG", original.setScale(2, BigDecimal.ROUND_HALF_UP));
            ss.agregarParametro("DIFERENCIA", original.subtract(posterior).setScale(2, BigDecimal.ROUND_HALF_UP));
            ss.agregarParametro("IMP_NEW", posterior.setScale(2, BigDecimal.ROUND_HALF_UP));
            ss.agregarParametro("ID_SOLICITUD", exo.getId());
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("recaudaciones");
            ss.setNombreReporte("formulario_exoneracion_master");

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nuevaExoneracion() {
        this.exoneracion = new FnExoneracionClase();
        JsfUti.executeJS("PF('dlgExon').show()");
    }

    public void nuevoTipoExon() {
        tipoExoneracion = new FnExoneracionTipo();
        JsfUti.executeJS("PF('dlgTipExon').show()");
    }

    public void editarExoneracion() {
        if (exoneracion != null) {
            if (exoneracion.getDescripcion() == null || exoneracion.getDescripcion().trim().length() <= 0) {
                JsfUti.messageError(null, MessagesRentas.advert, "No ha ingresado el la descripción de la exoneración");
                return;
            }
            if (rentasServs.guadModExoneracion(exoneracion) != null) {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.guardadoCorrecto);
            } else {
                JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.ErrorGuardar);
            }
        }
    }

    public void guardarExoneracion() {
        if (exoneracion != null) {
            if (exoneracion.getDescripcion() == null || exoneracion.getDescripcion().trim().length() <= 0) {
                JsfUti.messageError(null, MessagesRentas.advert, "No ha ingresado el la descripción de la exoneración");
                return;
            }
            exoneracion.setUsuarioCreacion(session.getName_user());
            exoneracion.setFechaIngreso(new Date());
            exoneracion.setDescripcion(upperText(exoneracion.getDescripcion()));
            exoneracion = rentasServs.guadModExoneracion(exoneracion);
            if (exoneracion != null) {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.guardadoCorrecto);
            } else {
                JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.ErrorGuardar);
            }
        }
        exoneraciones = new BaseLazyDataModel<>(FnExoneracionLiquidacion.class, "id", "DESC");
        JsfUti.executeJS("PF('dlgExon').hide()");
    }

    public void agregarTipo() {

        if (tipoExoneracion.getDescripcion() == null || tipoExoneracion.getDescripcion().trim().length() <= 0) {
            JsfUti.messageError(null, MessagesRentas.advert, "No ha ingresado el la descripción del tipo exoneración");
            return;
        }
        tipoExoneracion.setDescripcion(upperText(tipoExoneracion.getDescripcion()));
        tipoExoneracion.setReglamento(upperText(tipoExoneracion.getReglamento()));
        if (tipoExoneracion.getId() == null) {
            tipoExoneracion.setEstado(Boolean.TRUE);
            tipoExoneracion.setFechaIngreso(new Date());
            tipoExoneracion.setUsuarioCreacion(session.getName_user());
            if (exoneracion.getFnExoneracionTipoCollection() == null) {
                exoneracion.setFnExoneracionTipoCollection(new ArrayList<FnExoneracionTipo>());
            }
            exoneracion.getFnExoneracionTipoCollection().add(tipoExoneracion);
        } else {

        }
        JsfUti.executeJS("PF('dlgTipExon').hide()");
    }

    public void continuar() {
        JsfUti.redirectFaces("/vistaprocesos/dashBoard.xhtml");
    }

    public FnExoneracionClase getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnExoneracionClase exoneracion) {
        this.exoneracion = exoneracion;
    }

    public BaseLazyDataModel<FnExoneracionLiquidacion> getExoneraciones() {
        return exoneraciones;
    }

    public void setExoneraciones(BaseLazyDataModel<FnExoneracionLiquidacion> exoneraciones) {
        this.exoneraciones = exoneraciones;
    }

    public List<CatCategoriasPredio> getTempList() {
        return tempList;
    }

    public void setTempList(List<CatCategoriasPredio> tempList) {
        this.tempList = tempList;
    }

    public FnExoneracionTipo getTipoExoneracion() {
        return tipoExoneracion;
    }

    public void setTipoExoneracion(FnExoneracionTipo tipoExoneracion) {
        this.tipoExoneracion = tipoExoneracion;
    }

    public List<CatCategoriasPredio> getCategoriasList() {
        return categoriasList;
    }

    public void setCategoriasList(List<CatCategoriasPredio> categoriasList) {
        this.categoriasList = categoriasList;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    /**
     * Creates a new
     * instance of
     * ExoneracionesView
     */
    public ExoneracionesView() {
    }

    private String upperText(String descripcion) {
        if (descripcion != null && descripcion.length() > 0) {
            return descripcion.toUpperCase();
        }
        return null;
    }

    public FnExoneracionLiquidacion getExoConsulta() {
        return exoConsulta;
    }

    public void setExoConsulta(FnExoneracionLiquidacion exoConsulta) {
        this.exoConsulta = exoConsulta;
    }

    public RenLiquidacion getOriginal() {
        return original;
    }

    public void setOriginal(RenLiquidacion original) {
        this.original = original;
    }

    public RenLiquidacion getPosterior() {
        return posterior;
    }

    public void setPosterior(RenLiquidacion posterior) {
        this.posterior = posterior;
    }

    public List<RubrosPorTipoLiq> getDetList1() {
        return detList1;
    }

    public void setDetList1(List<RubrosPorTipoLiq> detList1) {
        this.detList1 = detList1;
    }

    public List<RubrosPorTipoLiq> getDetList2() {
        return detList2;
    }

    public void setDetList2(List<RubrosPorTipoLiq> detList2) {
        this.detList2 = detList2;
    }

    public String getMensajeExoneracion() {
        return mensajeExoneracion;
    }

    public void setMensajeExoneracion(String mensajeExoneracion) {
        this.mensajeExoneracion = mensajeExoneracion;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<RenDetLiquidacion> getDetLiq() {
        return detLiq;
    }

    public void setDetLiq(List<RenDetLiquidacion> detLiq) {
        this.detLiq = detLiq;
    }

    public Boolean getUsuarioAutorizado() {
        return usuarioAutorizado;
    }

    public void setUsuarioAutorizado(Boolean usuarioAutorizado) {
        this.usuarioAutorizado = usuarioAutorizado;
    }

}
