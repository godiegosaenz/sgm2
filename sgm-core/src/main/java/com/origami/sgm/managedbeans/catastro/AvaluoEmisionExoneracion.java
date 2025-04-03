/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioRuralModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CoaJuicioPredio;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracionPredios;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MejDetRubroMejoras;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenSolicitudesLiquidacion;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.entities.models.SolicitudExoneracionEnte;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import util.EntityBeanCopy;
import util.Faces;
import util.GroovyUtil;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author dfcalderio
 */
@Named(value = "emisionView")
@ViewScoped
public class AvaluoEmisionExoneracion implements Serializable {

    @Inject
    private UserSession session;
    @Inject
    protected ServletSession ss;
    @Inject
    private Entitymanager manager;
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    private FnExoneracionTipo tipo;
    private FnSolicitudExoneracionPredios predioSolicitud;
    private FnSolicitudExoneracion solicitud;
    private List<CatPredio> catPrediosEstanExonerados;
    private Integer anioEmision;
    private FnSolicitudTipoLiquidacionExoneracion tipoLiqSol;
    private String query;
    private String getSolicitudExoneracionByPredio;

    private String mensajeEmision = "";

    @PostConstruct
    protected void init() {
        anioEmision = Utils.getAnio(new Date());

        //INICIO QUERYS
    }

    public Integer getAnioEmision() {
        return anioEmision;
    }

    public void setAnioEmision(Integer anioEmision) {
        this.anioEmision = anioEmision;
    }

    /*Agregados para liquidacion*/
    public void clonarValores() {
        mensajeEmision = "";
        HiberUtil.newTransaction();
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();

        Integer result = sess.doReturningWork(new ReturningWork<Integer>() {
            @Override
            public Integer execute(Connection cnctn) throws SQLException {
                System.out.println("enter execute");
                CallableStatement callableStatement;
                System.out.println("ANIO: " + anioEmision);
                callableStatement = cnctn.prepareCall("{call " + "sgm_app.insert_values_vs_coef_dep" + " (?)}");
                callableStatement.setInt(1, anioEmision);

                int estatus = callableStatement.executeUpdate();
                System.out.println("estatus" + estatus);
                return estatus;
            }
        });

        if (result == 0) {
            mensajeEmision = "VALORES CLONADOS CORRECTA";
        } else {
            mensajeEmision = "ERROR EN CLONAR VALORES";
        }
        JsfUti.executeJS("PF('dlgMensaje').show()");
    }

    /*Agregados para liquidacion*/
    public void valorizacionPredial() {
        mensajeEmision = "";
        HiberUtil.newTransaction();
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();

        Integer result = sess.doReturningWork(new ReturningWork<Integer>() {
            @Override
            public Integer execute(Connection cnctn) throws SQLException {
                System.out.println("enter execute");
                CallableStatement callableStatement;
                System.out.println("ANIO: " + anioEmision);
                callableStatement = cnctn.prepareCall("{call " + "sgm_app.avaluar_predio" + " (?,?,?,?)}");
                callableStatement.setLong(1, 0L);
                callableStatement.setString(2, session.getName_user());
                callableStatement.setInt(3, anioEmision);
                callableStatement.setInt(4, anioEmision);
                int estatus = callableStatement.executeUpdate();
                System.out.println("estatus" + estatus);
                return estatus;
            }
        });

        if (result == 0) {
            mensajeEmision = "VALORIZACION CORRECTAMENTE REALIZADA";
        } else {
            mensajeEmision = "ERROR EN LA VALORIZACION";
        }
        JsfUti.executeJS("PF('dlgMensaje').show()");
    }

    public void emitir() {
        mensajeEmision = "";
        HiberUtil.newTransaction();
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();

        Integer result = sess.doReturningWork(new ReturningWork<Integer>() {
            @Override
            public Integer execute(Connection cnctn) throws SQLException {
                System.out.println("enter execute");
                CallableStatement callableStatement;
                System.out.println("ANIO: " + anioEmision);
                callableStatement = cnctn.prepareCall("{call " + "sgm_app.emision_predial_ren_liquidacion" + " (?,?,?,?)}");
                callableStatement.setLong(1, 0L);
                callableStatement.setString(2, session.getName_user());
                callableStatement.setInt(3, anioEmision);
                callableStatement.setInt(4, anioEmision);

                int estatus = callableStatement.executeUpdate();
                System.out.println("estatus" + estatus);
                return estatus;
            }
        });

        if (result == 0) {
            mensajeEmision = "EMISION CORRECTAMENTE REALIZADA";
        } else {
            mensajeEmision = "ERROR EN LA EMISION";
        }
        JsfUti.executeJS("PF('dlgMensaje').show()");
    }

    public void reporte() {
        mensajeEmision = "";
        HiberUtil.newTransaction();
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();

        Integer result = sess.doReturningWork(new ReturningWork<Integer>() {
            @Override
            public Integer execute(Connection cnctn) throws SQLException {
                System.out.println("enter execute");
                CallableStatement callableStatement;
                System.out.println("ANIO: " + anioEmision);
                callableStatement = cnctn.prepareCall("{call " + "sgm_app.insert_values_iniciales_liquidaciones" + " (?,?)}");
                callableStatement.setString(1, session.getName_user());
                callableStatement.setInt(2, anioEmision);
                int estatus = callableStatement.executeUpdate();
                System.out.println("estatus" + estatus);
                return estatus;
            }
        });

        System.out.println("finished");

        ss.borrarDatos();
        ss.instanciarParametros();
        ss.setNombreSubCarpeta("recaudaciones");
        ss.setTieneDatasource(true);
        ss.setFondoBlanco(Boolean.TRUE);
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/") + "reportes/recaudaciones/");
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.agregarParametro("TIPO_LIQUIDACION", 13L);
        ss.agregarParametro("ANIO", anioEmision);
        ss.agregarParametro("USUARIO", session.getName_user());
        ss.agregarParametro("LOGO", path + SisVars.sisLogo);
        ss.setNombreReporte("sReporteEmision");
        //ss.setNombreReporte("saldoPorRubrosPrediales");
        Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

    }

    public void exonerar() {
        mensajeEmision = "";
        getSolicitudExoneracionByPredio = "SELECT f FROM FnSolicitudExoneracion f WHERE f.predio =:predio AND f.exoneracionTipo != 40 and f.anioInicio =:anioInicio";
        query = "SELECT  cp FROM CatPredio cp WHERE cp.estaExonerado = TRUE";
        catPrediosEstanExonerados = manager.findAll(query);
        Integer anio = anioEmision - 1;
        FnSolicitudExoneracion exoneracion, exoneracionIngresada, exoneracionSegunAnioIngreso;
        if (catPrediosEstanExonerados != null) {
            for (CatPredio cp : catPrediosEstanExonerados) {
                exoneracionSegunAnioIngreso = (FnSolicitudExoneracion) manager.find(getSolicitudExoneracionByPredio, new String[]{"predio", "anioInicio"}, new Object[]{cp.getId(), anioEmision});
                if (exoneracionSegunAnioIngreso == null) {
                    exoneracionIngresada = (FnSolicitudExoneracion) manager.find(getSolicitudExoneracionByPredio, new String[]{"predio", "anioInicio"}, new Object[]{cp.getId(), anio});
                    if (exoneracionIngresada != null) {
                        HiberUtil.newTransaction();
                        System.out.println("exoneracion " + cp.getId());
                        exoneracion = new FnSolicitudExoneracion();
                        exoneracion.setSolicitante(exoneracionIngresada.getSolicitante());
                        exoneracion.setExoneracionTipo(exoneracionIngresada.getExoneracionTipo());
                        exoneracion.setId(null);
                        exoneracion.setAnioFin(anioEmision);
                        exoneracion.setAnioInicio(anioEmision);
                        exoneracion.setFechaIngreso(new Date());
                        exoneracion.setUsuarioCreacion(session.getName_user());
                        exoneracion.setPredio(cp);
                        exoneracion.setTipoValor(exoneracionIngresada.getTipoValor());
                        exoneracion.setValor(exoneracionIngresada.getValor());
                        exoneracion.setEstado(new FnEstadoExoneracion(2L));
                        aplicarExoneracionSinTramite(cp, exoneracion);
                    }
                }
            }
            mensajeEmision = "EXONERACIÒN CORRECTAMENTE REALIZADA";
            JsfUti.executeJS("PF('dlgMensaje').show()");
        }
    }

    public Boolean verificarPredioExonerado(CatPredio catPredio, CatPredioRuralModel catPredioRural, FnExoneracionTipo exoneracionTipo, Integer anio) {
        FnSolicitudExoneracion solicitudConsulta = null;
        if (catPredio != null) {
            solicitudConsulta = (FnSolicitudExoneracion) manager.find(QuerysFinanciero.getSolicitudExoneracionByPredioAndTipoAndAnioFin, new String[]{"predio", "tipo", "anio"}, new Object[]{catPredio, exoneracionTipo, anio});
        }
        return solicitudConsulta == null;
    }

    public void aplicarExoneracionSinTramite(CatPredio predio, FnSolicitudExoneracion exoneracion) {

        try {
            List<FnSolicitudExoneracionPredios> prediosEnSolicitud = new ArrayList<>();
            if ((exoneracion.getTipoValor() != null)) {
                if ((exoneracion.getValor() == null)) {
                    return;
                }
            }
            if (!this.verificarPredioExonerado(predio, null, exoneracion.getExoneracionTipo(), exoneracion.getAnioInicio())) {
                return;
            }
            exoneracion = (FnSolicitudExoneracion) manager.persist(exoneracion);
            predioSolicitud = new FnSolicitudExoneracionPredios();
            predioSolicitud.setPredio(predio);
            prediosEnSolicitud.add(predioSolicitud);
            servicesRentas.registarDatoSolicitudExoneracion(exoneracion, null, prediosEnSolicitud);
            servicesRentas.aplicarExoneracion(null, exoneracion, session.getName_user());
        } catch (Exception e) {

        }
    }

    public String getMensajeEmision() {
        return mensajeEmision;
    }

    public void setMensajeEmision(String mensajeEmision) {
        this.mensajeEmision = mensajeEmision;
    }

}
