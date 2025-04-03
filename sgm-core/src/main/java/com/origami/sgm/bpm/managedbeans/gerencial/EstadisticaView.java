/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.gerencial;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.EstadisticaModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.SQLQuery;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.PieChartModel;
import util.ApplicationContextUtils;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class EstadisticaView implements Serializable {

    private static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager services;

    @Inject
    private UserSession uSession;

    private AclUser user;

    private PieChartModel pieModel1;
    private PieChartModel pieModel2;
    private PieChartModel pieModel3;

    private List<GeTipoTramite> tipoTramiteList;
    private List<HistoricoTramites> htList;
    private BigInteger num1, num2, num3, totalTramites;
    private SQLQuery query;
    private Long anio;
    private LineChartModel areaModel;

    private HorizontalBarChartModel horizontalBarModel;
    private BarChartModel barModel;
    private ComboPooledDataSource dsc;
    private Connection conect;
    private Statement stmt;
    private ResultSet rs;
    private PieChartModel pieModelDeadlock;
    private EstadisticaModel model;
    private List<EstadisticaModel> modelList;
    private Integer modoFiltro;
    private List<GeDepartamento> departamentosList;
    private GeDepartamento departamento;
    private List<AclRol> roles;
    private String anio2;
    private ChartSeries finalizados = null;
    private ChartSeries pendientes = null;
    private ChartSeries inactivos = null;

    @PostConstruct
    public void init() {
        try {
            if (uSession != null) {
                user = (AclUser) services.find(AclUser.class, uSession.getUserId());
                if (user.getUserIsDirector()) {
                    modoFiltro = 2;
                    roles = (List) user.getAclRolCollection();
                    departamento = roles.get(0).getDepartamento();
                    dsc = (ComboPooledDataSource) ApplicationContextUtils.getBean("dataSource");
                    /*
                    
                    areaModel = new LineChartModel();     
                    pieModelDeadlock = new PieChartModel();
                    horizontalBarModel = new HorizontalBarChartModel();
                    barModel = new BarChartModel();*/
                    iniciar();
                    this.aplicarFiltros();
                    //this.iniciar();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciar() {
        try {

            pieModel1 = new PieChartModel();
            pieModel2 = new PieChartModel();
            pieModel3 = new PieChartModel();
            modelList = new ArrayList();

            areaModel = new LineChartModel();

            pieModelDeadlock = new PieChartModel();
            horizontalBarModel = new HorizontalBarChartModel();
            barModel = new BarChartModel();
            anio = new Long(new SimpleDateFormat("yyyy").format(new Date()));

            tipoTramiteList = services.findAll(Querys.getGeTipoTramiteListByEstado, new String[]{}, new Object[]{});
            departamentosList = services.findAllEntCopy(Querys.getGeDepartamentoByEstado, new String[]{"estado"}, new Object[]{true});

            conect = dsc.getConnection();
            stmt = conect.createStatement();

            this.llenarData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void llenarData() throws SQLException {
        try {

            BigInteger count;

            int tam = 0;

            for (GeTipoTramite temp : tipoTramiteList) {
                count = BigInteger.ZERO;
                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='finalizado' and ht.tipo_tramite = " + temp.getId());//Querys.getTramitesByTipoAndEstado, new String[]{"tipo", "estado"}, new Object[]{temp, "finalizado"});
                num1 = (BigInteger) query.uniqueResult();
                pieModel1.set(temp.getDescripcion(), num1);

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='pendiente' and ht.tipo_tramite = " + temp.getId());//Querys.getTramitesByTipoAndEstado, new String[]{"tipo", "estado"}, new Object[]{temp, "finalizado"});
                num2 = (BigInteger) query.uniqueResult();
                pieModel2.set(temp.getDescripcion(), num2);

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='inactivo' and ht.tipo_tramite = " + temp.getId());//Querys.getTramitesByTipoAndEstado, new String[]{"tipo", "estado"}, new Object[]{temp, "finalizado"});
                num3 = (BigInteger) query.uniqueResult();
                pieModel3.set(temp.getDescripcion(), num3);

                rs = stmt.executeQuery(Querys.getCountTramitesMuertosPorTipoTramite + "'" + temp.getDescripcion().toLowerCase() + "'");

                while (rs.next()) {
                    count = BigInteger.valueOf(rs.getLong(1));
                }
                pieModelDeadlock.set(temp.getDescripcion(), count);
                model = new EstadisticaModel(temp.getDescripcion(), num1, num2, num3, count);
                modelList.add(model);
            }

            pieModel1.setTitle("Gráfico pastel - trámites finalizados");
            pieModel1.setLegendPosition("w");
            pieModel1.setShowDataLabels(true);
            pieModel1.setDiameter(200);
            /*
            pieModel2.setTitle("Gráfico pastel");
            pieModel2.setLegendPosition("w");
            pieModel2.setShowDataLabels(true);
            pieModel2.setDiameter(200);

            pieModel3.setTitle("Gráfico pastel");
            pieModel3.setLegendPosition("w");
            pieModel3.setShowDataLabels(true);
            pieModel3.setDiameter(200);
            
            pieModelDeadlock.setTitle("Gráfico pastel");
            pieModelDeadlock.setLegendPosition("w");
            pieModelDeadlock.setShowDataLabels(true);
            pieModelDeadlock.setDiameter(200);
             */
            pendientes = new ChartSeries();
            pendientes.setLabel("Pendientes");

            finalizados = new ChartSeries();
            finalizados.setLabel("Finalizados");

            inactivos = new ChartSeries();
            inactivos.setLabel("Inactivos");

            for (int i = 2014; i <= Integer.valueOf("" + anio); i++) {
                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='pendiente' and EXTRACT(YEAR FROM ht.fecha) = " + i);
                num1 = (BigInteger) query.uniqueResult();

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where EXTRACT(YEAR FROM ht.fecha) = " + i);
                totalTramites = (BigInteger) query.uniqueResult();
                if (Integer.valueOf(totalTramites + "") > tam) {
                    tam = Integer.valueOf(totalTramites + "");
                }

                pendientes.set(i, num1);

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='finalizado' and EXTRACT(YEAR FROM ht.fecha) = " + i);
                num1 = (BigInteger) query.uniqueResult();

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where EXTRACT(YEAR FROM ht.fecha) = " + i);
                totalTramites = (BigInteger) query.uniqueResult();
                if (Integer.valueOf(totalTramites + "") > tam) {
                    tam = Integer.valueOf(totalTramites + "");
                }

                finalizados.set(i, num1);

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='inactivo' and EXTRACT(YEAR FROM ht.fecha) = " + i);
                num1 = (BigInteger) query.uniqueResult();

                query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where EXTRACT(YEAR FROM ht.fecha) = " + i);
                totalTramites = (BigInteger) query.uniqueResult();
                if (Integer.valueOf(totalTramites + "") > tam) {
                    tam = Integer.valueOf(totalTramites + "");
                }

                inactivos.set(i, num1);
            }

            areaModel.setTitle("Gráfico de líneas de la cantidad total de trámites");
            areaModel.setLegendPosition("e");
            areaModel.setShowPointLabels(true);
            areaModel.getAxes().put(AxisType.X, new CategoryAxis("Años"));
            Axis yAxis = areaModel.getAxis(AxisType.Y);
            yAxis.setLabel("Trámites");
            yAxis.setMin(0);
            yAxis.setMax(tam + 20);

            areaModel.addSeries(finalizados);
            areaModel.addSeries(pendientes);
            areaModel.addSeries(inactivos);

            horizontalBarModel.setTitle("Horizontal and Stacked");
            horizontalBarModel.setLegendPosition("e");
            horizontalBarModel.setStacked(true);

            Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
            xAxis.setLabel("num");
            xAxis.setMin(0);
            xAxis.setMax(tam + 100);

            horizontalBarModel.addSeries(finalizados);
            horizontalBarModel.addSeries(pendientes);
            horizontalBarModel.addSeries(inactivos);

            barModel.setTitle("Barra de cantidad total de trámites");
            barModel.setLegendPosition("e");
            barModel.setStacked(true);

            Axis yAxis2 = barModel.getAxis(AxisType.Y);
            yAxis2.setLabel("Cantidad de trámites");
            yAxis2.setMin(0);
            yAxis2.setMax(tam + 100);

            Axis xAxis2 = barModel.getAxis(AxisType.X);
            xAxis2.setLabel("Años");
            xAxis2.setMin(1);
            xAxis2.setMax(3);

            barModel.addSeries(finalizados);
            barModel.addSeries(pendientes);
            barModel.addSeries(inactivos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            stmt.close();
            conect.close();
        }
    }

    public void aplicarFiltros() throws SQLException {
        BigInteger count, fnlzds, pndnts, inctvs;
        fnlzds = BigInteger.valueOf(0);
        pndnts = BigInteger.valueOf(0);
        inctvs = BigInteger.valueOf(0);

        conect = dsc.getConnection();
        stmt = conect.createStatement();

        try {
            switch (modoFiltro) {
                case 1:

                    this.iniciar();
                    break;
                case 2:
                    if (departamento != null) {
                        if (!validarDepartamento()) {
                            JsfUti.messageError(null, "Error", "El departamento no tiene trámites asociados");
                            return;
                        }
                        pieModel1 = new PieChartModel();
                        modelList = new ArrayList<>();
                        tipoTramiteList = (List<GeTipoTramite>) departamento.getGeTipoTramiteCollection();
                        for (GeTipoTramite temp : tipoTramiteList) {
                            count = BigInteger.ZERO;
                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='finalizado' and ht.tipo_tramite = " + temp.getId() + "");
                            num1 = (BigInteger) query.uniqueResult();
                            pieModel1.set(temp.getDescripcion(), num1);

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='pendiente' and ht.tipo_tramite = " + temp.getId());
                            num2 = (BigInteger) query.uniqueResult();

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='inactivo' and ht.tipo_tramite = " + temp.getId());
                            num3 = (BigInteger) query.uniqueResult();

                            rs = stmt.executeQuery(Querys.getCountTramitesMuertosPorTipoTramite + "'" + temp.getDescripcion().toLowerCase() + "'");

                            while (rs.next()) {
                                count = BigInteger.valueOf(rs.getLong(1));
                            }
                            pieModelDeadlock.set(temp.getDescripcion(), count);
                            model = new EstadisticaModel(temp.getDescripcion(), num1, num2, num3, count);
                            modelList.add(model);
                            pieModel1.setTitle("Gráfico pastel - trámites finalizados");
                            pieModel1.setLegendPosition("w");
                            pieModel1.setShowDataLabels(true);
                            pieModel1.setDiameter(200);

                        }
                    } else {
                        JsfUti.messageError(null, "Error", "No seleccionó un departamento");
                        return;
                    }
                    break;
                case 3:

                    if (!NumberUtils.isNumber(anio2)) {
                        JsfUti.messageError(null, "Error", "No ingresó un año válido");
                        return;
                    }
                    if (anio2 != null) {
                        pendientes = new ChartSeries();
                        pendientes.setLabel("Pendientes");

                        finalizados = new ChartSeries();
                        finalizados.setLabel("Finalizados");

                        inactivos = new ChartSeries();
                        inactivos.setLabel("Inactivos");
                        pieModel1 = new PieChartModel();
                        modelList = new ArrayList<>();
                        barModel = new BarChartModel();

                        for (GeTipoTramite temp : tipoTramiteList) {
                            count = BigInteger.ZERO;
                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='finalizado' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num1 = (BigInteger) query.uniqueResult();
                            pieModel1.set(temp.getDescripcion(), num1);
                            fnlzds = fnlzds.add(num1);

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='pendiente' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num2 = (BigInteger) query.uniqueResult();
                            //pieModel2.set(temp.getDescripcion(), num2);
                            pndnts = pndnts.add(num2);

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='inactivo' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num3 = (BigInteger) query.uniqueResult();
                            //pieModel3.set(temp.getDescripcion(), num3);
                            inctvs = inctvs.add(num3);

                            rs = stmt.executeQuery(Querys.getCountTramitesMuertosPorTipoTramite + "'" + temp.getDescripcion().toLowerCase() + "'");

                            while (rs.next()) {
                                count = BigInteger.valueOf(rs.getLong(1));
                            }
                            pieModelDeadlock.set(temp.getDescripcion(), count);
                            model = new EstadisticaModel(temp.getDescripcion(), num1, num2, num3, count);
                            modelList.add(model);
                            pieModel1.setTitle("Gráfico pastel - trámites finalizados");
                            pieModel1.setLegendPosition("w");
                            pieModel1.setShowDataLabels(true);
                            pieModel1.setDiameter(200);
                        }
                        finalizados.set(anio2, fnlzds);
                        pendientes.set(anio2, pndnts);
                        inactivos.set(anio2, inctvs);
                        barModel.addSeries(finalizados);
                        barModel.addSeries(pendientes);
                        barModel.addSeries(inactivos);
                    } else {
                        JsfUti.messageError(null, "Error", "No ingresó un año válido");
                        return;
                    }
                    break;
                case 4:
                    if (!NumberUtils.isNumber(anio2)) {
                        JsfUti.messageError(null, "Error", "No ingresó un año válido");
                        return;
                    }
                    if (departamento != null && anio2 != null) {
                        pendientes = new ChartSeries();
                        pendientes.setLabel("Pendientes");

                        finalizados = new ChartSeries();
                        finalizados.setLabel("Finalizados");

                        inactivos = new ChartSeries();
                        inactivos.setLabel("Inactivos");
                        if (!validarDepartamento()) {
                            JsfUti.messageError(null, "Error", "El departamento no tiene trámites asociados");
                            return;
                        }
                        pieModel1 = new PieChartModel();
                        modelList = new ArrayList<>();
                        barModel = new BarChartModel();
                        tipoTramiteList = (List<GeTipoTramite>) departamento.getGeTipoTramiteCollection();
                        for (GeTipoTramite temp : tipoTramiteList) {
                            count = BigInteger.ZERO;
                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='finalizado' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num1 = (BigInteger) query.uniqueResult();
                            pieModel1.set(temp.getDescripcion(), num1);
                            fnlzds = fnlzds.add(num1);

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='pendiente' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num2 = (BigInteger) query.uniqueResult();
                            pndnts = pndnts.add(num2);

                            query = services.getSession().createSQLQuery("select count(*) from " + SchemasConfig.FLOW + ".historico_tramites as ht where lower(ht.estado)='inactivo' and extract(year from date(ht.fecha)) = " + anio2 + " and ht.tipo_tramite = " + temp.getId());
                            num3 = (BigInteger) query.uniqueResult();
                            inctvs = inctvs.add(num3);

                            rs = stmt.executeQuery(Querys.getCountTramitesMuertosPorTipoTramite + "'" + temp.getDescripcion().toLowerCase() + "'");

                            while (rs.next()) {
                                count = BigInteger.valueOf(rs.getLong(1));
                            }
                            pieModelDeadlock.set(temp.getDescripcion(), count);
                            model = new EstadisticaModel(temp.getDescripcion(), num1, num2, num3, count);
                            modelList.add(model);
                            pieModel1.setTitle("Gráfico pastel - trámites finalizados");
                            pieModel1.setLegendPosition("w");
                            pieModel1.setShowDataLabels(true);
                            pieModel1.setDiameter(200);
                        }
                        finalizados.set(anio2, fnlzds);
                        pendientes.set(anio2, pndnts);
                        inactivos.set(anio2, inctvs);
                        barModel.addSeries(finalizados);
                        barModel.addSeries(pendientes);
                        barModel.addSeries(inactivos);
                    } else {
                        JsfUti.messageError(null, "Error", "No seleccionó un departamento o no ingresó un año.");
                        return;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            stmt.close();
            conect.close();
        }
    }

    public Boolean validarDepartamento() {
        if (departamento.getId() == 1 || departamento.getId() == 4 || departamento.getId() == 9) {
            return true;
        } else {
            return false;
        }
    }

    public PieChartModel getPieModel1() {
        return pieModel1;
    }

    public void setPieModel1(PieChartModel pieModel1) {
        this.pieModel1 = pieModel1;
    }

    public PieChartModel getPieModel2() {
        return pieModel2;
    }

    public void setPieModel2(PieChartModel pieModel2) {
        this.pieModel2 = pieModel2;
    }

    public PieChartModel getPieModel3() {
        return pieModel3;
    }

    public void setPieModel3(PieChartModel pieModel3) {
        this.pieModel3 = pieModel3;
    }

    public LineChartModel getAreaModel() {
        return areaModel;
    }

    public void setAreaModel(LineChartModel areaModel) {
        this.areaModel = areaModel;
    }

    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }

    public void setHorizontalBarModel(HorizontalBarChartModel horizontalBarModel) {
        this.horizontalBarModel = horizontalBarModel;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public PieChartModel getPieModelDeadlock() {
        return pieModelDeadlock;
    }

    public void setPieModelDeadlock(PieChartModel pieModelDeadlock) {
        this.pieModelDeadlock = pieModelDeadlock;
    }

    public List<EstadisticaModel> getModelList() {
        return modelList;
    }

    public void setModelList(List<EstadisticaModel> modelList) {
        this.modelList = modelList;
    }

    public Integer getModoFiltro() {
        return modoFiltro;
    }

    public void setModoFiltro(Integer modoFiltro) {
        this.modoFiltro = modoFiltro;
    }

    public List<GeDepartamento> getDepartamentosList() {
        return departamentosList;
    }

    public void setDepartamentosList(List<GeDepartamento> departamentosList) {
        this.departamentosList = departamentosList;
    }

    public Long getAnio() {
        return anio;
    }

    public void setAnio(Long anio) {
        this.anio = anio;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public String getAnio2() {
        return anio2;
    }

    public void setAnio2(String anio2) {
        this.anio2 = anio2;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public AclUser getUser() {
        return user;
    }

    public void setUser(AclUser user) {
        this.user = user;
    }

}
