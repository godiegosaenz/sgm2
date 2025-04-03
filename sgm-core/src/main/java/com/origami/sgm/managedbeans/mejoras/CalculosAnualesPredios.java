/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.mejoras;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredioSumasAnualesUbicacion;
import com.origami.sgm.entities.CatUbicacion;
import com.origami.sgm.lazymodels.CatPredioSumasAnualesUbicacionLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class CalculosAnualesPredios implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.inject.Inject
    private Entitymanager manager;
    private Map<String, Object> parametros = new HashMap<>();

    @Inject
    private ServletSession servletSession;

    protected CatPredioSumasAnualesUbicacionLazy calculosAnuales;
    protected List<CatUbicacion> ubicaciones;
    protected List<CatPredioSumasAnualesUbicacion> calculosAnualesIngreso;
    protected Long anioEmision;
    protected Long numPredios;
    protected BigDecimal totalAvaluos;
    protected BigDecimal totalAreas;
    protected BigDecimal totalAvaluosParroquias;
    protected BigDecimal totalAreasParroquias;
    protected Long anioReporte;

    @PostConstruct
    public void initView() {
        try {
            calculosAnuales = new CatPredioSumasAnualesUbicacionLazy();
            parametros = new HashMap<>();
            parametros.put("estado", Boolean.TRUE);
            ubicaciones = manager.findObjectByParameterList(CatUbicacion.class, parametros);
            calculosAnualesIngreso = new ArrayList<>();
            Calendar c = Calendar.getInstance();
            anioEmision = new Long(c.get(Calendar.YEAR));
        } catch (Exception e) {
            Logger.getLogger(CalculosAnualesPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void realizarCalculos() {
        try {
            System.out.println("sizze" + ubicaciones.size());
            calculosAnualesIngreso = new ArrayList<>();
            totalAvaluosParroquias = BigDecimal.ZERO;
            totalAreasParroquias = BigDecimal.ZERO;
            numPredios = (Long) manager.find(Querys.numPrediosActivos);
            totalAvaluos = ((BigDecimal) manager.find(Querys.totalAvaluosMunicipal)).setScale(2, RoundingMode.UP);
            totalAreas = (BigDecimal) manager.find(Querys.totalAreaSolar);
            loadUbicacionesWithAvals();
        } catch (Exception e) {
            Logger.getLogger(CalculosAnualesPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void loadUbicacionesWithAvals() {
        CatPredioSumasAnualesUbicacion valorAnual;
        for (CatUbicacion ubicacion : ubicaciones) {
            valorAnual = new CatPredioSumasAnualesUbicacion();
            valorAnual.setUbicacion(ubicacion);
            //VALOR PPOR TODO EL CANTON 
            if (ubicacion.getParroquia() == -1 && ubicacion.getZona() == -1 && ubicacion.getSector() == -1 && ubicacion.getMz() == -1
                    && ubicacion.getSolar() == -1) {
                valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalByAll));
                valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarByAll));
            } else {
                //POR PAARROQUIAS
                if (ubicacion.getParroquia() > -1 && ubicacion.getZona() == -1 && ubicacion.getSector() == -1 && ubicacion.getMz() == -1
                        && ubicacion.getSolar() == -1) {
                    valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalByParroquia, new String[]{"parroquiaS"}, new Object[]{ubicacion.getParroquia()}));
                    valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarByParroquia, new String[]{"parroquiaS"}, new Object[]{ubicacion.getParroquia()}));
                }
                //POR ZONAS
                if (ubicacion.getParroquia() > -1 && ubicacion.getZona() > -1 && ubicacion.getSector() == -1 && ubicacion.getMz() == -1
                        && ubicacion.getSolar() == -1) {
                    valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalByZona, new String[]{"parroquiaS", "zonaS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona()}));
                    valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarByZona, new String[]{"parroquiaS", "zonaS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona()}));
                }
                //POR SECTORES
                if (ubicacion.getParroquia() > -1 && ubicacion.getZona() > -1 && ubicacion.getSector() > -1 && ubicacion.getMz() == -1
                        && ubicacion.getSolar() == -1) {
                    valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalBySector, new String[]{"parroquiaS", "zonaS", "sectorS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector()}));
                    valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarBySector, new String[]{"parroquiaS", "zonaS", "sectorS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector()}));
                }
                //POR MZ
                if (ubicacion.getParroquia() > -1 && ubicacion.getZona() > -1 && ubicacion.getSector() > -1 && ubicacion.getMz() > -1
                        && ubicacion.getSolar() == -1) {
                    valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalByMz, new String[]{"parroquiaS", "zonaS", "sectorS", "mzS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector(), ubicacion.getMz()}));
                    valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarByMz, new String[]{"parroquiaS", "zonaS", "sectorS", "mzS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector(), ubicacion.getMz()}));
                }
                //POR LOTES
                if (ubicacion.getParroquia() > -1 && ubicacion.getZona() > -1 && ubicacion.getSector() > -1 && ubicacion.getMz() > -1
                        && ubicacion.getSolar() > -1) {
                    valorAnual.setAvaluosTotales((BigDecimal) manager.find(Querys.totalAvaluosMunicipalBySolar, new String[]{"parroquiaS", "zonaS", "sectorS", "mzS", "solarS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector(), ubicacion.getMz(), ubicacion.getSolar()}));
                    valorAnual.setAreaSolarTotal((BigDecimal) manager.find(Querys.totalAreaSolarBySolar, new String[]{"parroquiaS", "zonaS", "sectorS", "mzS", "solarS"}, new Object[]{ubicacion.getParroquia(), ubicacion.getZona(), ubicacion.getSector(), ubicacion.getMz(), ubicacion.getSolar()}));
                }
                
            }
            if (valorAnual.getAreaSolarTotal() != null && valorAnual.getAreaSolarTotal().compareTo(BigDecimal.ZERO) > 0 && valorAnual.getAvaluosTotales() != null && valorAnual.getAvaluosTotales().compareTo(BigDecimal.ZERO) > 0) {
                valorAnual.setAvaluosTotales(valorAnual.getAvaluosTotales().setScale(2, RoundingMode.UP));
                totalAvaluosParroquias = totalAvaluosParroquias.add(valorAnual.getAvaluosTotales());
                totalAreasParroquias = totalAreasParroquias.add(valorAnual.getAreaSolarTotal());
                calculosAnualesIngreso.add(valorAnual);
            }
        }
    }

    public SelectItem[] getListUbicaciones() {
        int cantRegis = ubicaciones.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(ubicaciones.get(i).getNombre(), ubicaciones.get(i).getNombre());
        }
        return options;
    }

    public void registrarCalculos() {
        try {
            if (this.anioEmision != null) {
                parametros = new HashMap<>();
                parametros.put("anio", anioEmision);
                List<CatPredioSumasAnualesUbicacion> calculosExistentes = manager.findObjectByParameterList(CatPredioSumasAnualesUbicacion.class, parametros);
                if (calculosExistentes == null || calculosExistentes.isEmpty()) {
                    for (CatPredioSumasAnualesUbicacion calculoAnual : calculosAnualesIngreso) {
                        calculoAnual.setAnio(anioEmision);
                        manager.persist(calculoAnual);
                    }
                    JsfUti.messageInfo(null, "Mensaje.", "Guardado Exitoso.");
                } else {
                    JsfUti.messageInfo(null, "Mensaje.", "Ya se realizarón los calculos para el año ingresado.");
                }
            } else {
                JsfUti.messageInfo(null, "Mensaje.", "Debe ingresar el Año al cual se calcularan las emisiones.");
            }
        } catch (Exception e) {
            Logger.getLogger(CalculosAnualesPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarReporte() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("calculosAnualesPrediales");
            servletSession.setNombreSubCarpeta("mejoras");
            servletSession.agregarParametro("LOGO", path + SisVars.sisLogo);
            servletSession.agregarParametro("ANIO", this.anioReporte);
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(CalculosAnualesPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public CatPredioSumasAnualesUbicacionLazy getCalculosAnuales() {
        return calculosAnuales;
    }

    public void setCalculosAnuales(CatPredioSumasAnualesUbicacionLazy calculosAnuales) {
        this.calculosAnuales = calculosAnuales;
    }

    public List<CatPredioSumasAnualesUbicacion> getCalculosAnualesIngreso() {
        return calculosAnualesIngreso;
    }

    public void setCalculosAnualesIngreso(List<CatPredioSumasAnualesUbicacion> calculosAnualesIngreso) {
        this.calculosAnualesIngreso = calculosAnualesIngreso;
    }

    public Long getAnioEmision() {
        return anioEmision;
    }

    public void setAnioEmision(Long anioEmision) {
        this.anioEmision = anioEmision;
    }

    public Long getNumPredios() {
        return numPredios;
    }

    public void setNumPredios(Long numPredios) {
        this.numPredios = numPredios;
    }

    public BigDecimal getTotalAvaluos() {
        return totalAvaluos;
    }

    public void setTotalAvaluos(BigDecimal totalAvaluos) {
        this.totalAvaluos = totalAvaluos;
    }

    public BigDecimal getTotalAreas() {
        return totalAreas;
    }

    public void setTotalAreas(BigDecimal totalAreas) {
        this.totalAreas = totalAreas;
    }

    public BigDecimal getTotalAvaluosParroquias() {
        return totalAvaluosParroquias;
    }

    public void setTotalAvaluosParroquias(BigDecimal totalAvaluosParroquias) {
        this.totalAvaluosParroquias = totalAvaluosParroquias;
    }

    public BigDecimal getTotalAreasParroquias() {
        return totalAreasParroquias;
    }

    public void setTotalAreasParroquias(BigDecimal totalAreasParroquias) {
        this.totalAreasParroquias = totalAreasParroquias;
    }

}
