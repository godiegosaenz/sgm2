/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.tesoreria;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Henry
 * Pilco
 */
@Named(value = "procesarLiquidaciones")
@ViewScoped
public class ProcesarLiquidaciones implements Serializable {

    @Inject
    private ServletSession ss;

    @javax.inject.Inject
    private Entitymanager manager;

    protected CatPredioModel predioModel = new CatPredioModel();
    protected List<CatPredio> prediosUrbanos;
    protected List<CatPredioRustico> prediosRurales;
    protected Long tipoConsultaUrbano = 1L;
    protected Long tipoConsultaRural = 1L;
    private CatEnteLazy contribuyentes;
    protected CatEnte contribuyenteConsulta;
    protected List<CatCiudadela> ciudadelas;
    protected List<CatParroquia> parroquiasRurales;
    private Map<String, Object> parametros;
    protected List<CatPredio> prediosUrbanosConsulta;
    protected List<CatPredioRustico> prediosRuralesConsulta;
    protected Long opcionesReporte = 2L;
    protected Boolean porAnio = Boolean.FALSE;
    protected Long anioReporte;
    protected CatPredio predioUrbanoConsulta;
    protected CatPredioRustico predioRuralConsulta;
    private List<Long> predios;
    protected List<CatPredio> prediosUrbanosConsultaSeleccionados;
    protected List<CatPredioRustico> prediosRuralesConsultaSeleccionados;
    ///////////////
    protected String codigoRural2017;
    protected EmisionesRuralesExcel predioRural2017;
    protected List<EmisionesRuralesExcel> prediosRurales2017 = new ArrayList<>();

    @PostConstruct
    public void initView() {
        try {
            contribuyentes = new CatEnteLazy();
            ciudadelas = manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
            parroquiasRurales = manager.findAllEntCopy(Querys.parroquiasRurales);
            prediosUrbanos = new ArrayList<>();
            prediosRurales = new ArrayList<>();
        } catch (Exception e) {
            Logger.getLogger(ProcesarLiquidaciones.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void onChangeRadio() {
        predioUrbanoConsulta = null;
    }

    public void onChangeTab(TabChangeEvent event) {
        if (event.getTab().getId().equals("predioRural")) {
            prediosUrbanos = null;
        }
        if (event.getTab().getId().equals("predioUrbano")) {
            prediosRurales = null;
        }
        predioModel = new CatPredioModel();
    }

    public void consultarPredioUrbano() {
        predioUrbanoConsulta = null;
        prediosUrbanosConsultaSeleccionados = null;
        prediosUrbanosConsulta = new ArrayList<>();
        parametros = new HashMap<>();
        try {
            switch (tipoConsultaUrbano.intValue()) {
                case 1://NUMERO PREDIAL
                    if (predioModel.getNumPredio() != null && predioModel.getNumPredio().compareTo(BigInteger.ZERO) > 0) {
                        parametros.put("numPredio", predioModel.getNumPredio());
                        predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioPropietarioCollection() != null && !contribuyenteConsulta.getCatPredioPropietarioCollection().isEmpty()) {
                            if (contribuyenteConsulta.getCatPredioPropietarioCollection().size() == 1) {
                                parametros.put("numPredio", contribuyenteConsulta.getCatPredioPropietarioCollection().get(0).getPredio().getNumPredio());
                                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            } else {
                                for (CatPredioPropietario pp : contribuyenteConsulta.getCatPredioPropietarioCollection()) {
                                    prediosUrbanosConsulta.add(pp.getPredio());
                                }
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                case 3://CODIGO PREDIAL
                    if (predioModel.getSector() > 0 || predioModel.getMz() > 0 || predioModel.getProvincia() > 0 || predioModel.getCanton() > 0
                            || predioModel.getParroquiaShort() > 0 || predioModel.getZona() > 0 || predioModel.getLote() > 0 || predioModel.getPiso() > 0
                            || predioModel.getUnidad() > 0 || predioModel.getBloque() > 0 || predioModel.getSolar() > 0) {

                        parametros.put("estado", "A");
                        if (predioModel.getSector() > 0) {
                            parametros.put("sector", predioModel.getSector());
                        }
                        if (predioModel.getMz() > 0) {
                            parametros.put("mz", predioModel.getMz());
                        }
                        if (predioModel.getProvincia() > 0) {
                            parametros.put("provincia", predioModel.getProvincia());
                        }
                        if (predioModel.getCanton() > 0) {
                            parametros.put("canton", predioModel.getCanton());
                        }
                        if (predioModel.getSolar() > 0) {
                            parametros.put("solar", predioModel.getSolar());
                        }
                        if (predioModel.getParroquiaShort() > 0) {
                            parametros.put("parroquia", predioModel.getParroquiaShort());
                        }
                        if (predioModel.getZona() > 0) {
                            parametros.put("zona", predioModel.getZona());
                        }
                        if (predioModel.getLote() > 0) {
                            parametros.put("lote", predioModel.getLote());
                        }
                        if (predioModel.getPiso() > 0) {
                            parametros.put("piso", predioModel.getPiso());
                        }
                        if (predioModel.getUnidad() > 0) {
                            parametros.put("unidad", predioModel.getUnidad());
                        }
                        if (predioModel.getBloque() > 0) {
                            parametros.put("bloque", predioModel.getBloque());
                        }
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Codigo Predial no es valido.");
                    }
                    break;
                case 4://UBICACION
                    if (predioModel.getCiudadela() != null || predioModel.getMzUrb() != null || predioModel.getSlUrb() != null) {
                        if (predioModel.getCiudadela() != null) {
                            parametros.put("ciudadela", predioModel.getCiudadela());
                        }
                        if (predioModel.getMzUrb() != null) {
                            parametros.put("urbMz", predioModel.getMzUrb());
                        }
                        if (predioModel.getSlUrb() != null) {
                            parametros.put("urbSolarnew", predioModel.getSlUrb());
                        }
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 5:
                    if (predioModel.getProvinciaAnt() != null && predioModel.getCantonAnt() != null && predioModel.getParroquiaAnt() != null
                            && predioModel.getManzanaAnt() != null && predioModel.getSolarAnt() != null && predioModel.getPropiedadHorizontalAnt() != null) {
                        parametros = new HashMap<>();
                        parametros.put("estado", "A");
                        parametros.put("predialant", predioModel.getProvinciaAnt() + predioModel.getCantonAnt() + predioModel.getParroquiaAnt()
                                + predioModel.getManzanaAnt() + predioModel.getSolarAnt() + predioModel.getPropiedadHorizontalAnt());
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 6:
                    if (predioModel.getClaveCat() != null) {
                        parametros = new HashMap<>();
                        parametros.put("claveCat", predioModel.getClaveCat());
                        parametros.put("estado", "A");
                        prediosUrbanosConsulta = manager.findObjectByParameterList(CatPredio.class, parametros);
                        /*if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty()) {
                            if (prediosConsulta.size() == 1) {
                                paramtr = new HashMap<>();
                                paramtr.put("numPredio", prediosConsulta.get(0).getNumPredio());
                                paramtr.put("estado", "A");
                                predioConsulta = (CatPredio) entityManager.findObjectByParameter(CatPredio.class, paramtr);
                            }
                        } else {
                            JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");*/
                    }
                    break;
                default:
                    break;
            }
            if (prediosUrbanosConsulta != null && !prediosUrbanosConsulta.isEmpty() && prediosUrbanosConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("numPredio", prediosUrbanosConsulta.get(0).getNumPredio());
                predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
            }
            if (predioUrbanoConsulta != null) {
                if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                    this.prediosUrbanos.add(predioUrbanoConsulta);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            } else {
                if (prediosUrbanosConsulta != null && prediosUrbanosConsulta.size() > 1) {
                    JsfUti.update("frmPredios");
                    JsfUti.executeJS("PF('dlgPrediosConsulta').show();");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ProcesarLiquidaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPredioRural() {
        predioRuralConsulta = null;
        prediosRuralesConsulta = new ArrayList<>();
        prediosRuralesConsultaSeleccionados = null;
        parametros = new HashMap<>();
        try {
            switch (tipoConsultaRural.intValue()) {
                case 1://CODIGO PREDIAL.
                    if (predioModel.getRegCatastral() != null || predioModel.getIdPredial() != null || predioModel.getParroquia() != null) {
                        if (predioModel.getRegCatastral() != null) {
                            parametros.put("regCatastral", predioModel.getRegCatastral().trim());
                        }
                        if (predioModel.getIdPredial() != null) {
                            parametros.put("idPredial", predioModel.getIdPredial());
                        }
                        if (predioModel.getParroquia() != null) {
                            parametros.put("parroquia", predioModel.getParroquia());
                        }
                        prediosRuralesConsulta = manager.findObjectByParameterList(CatPredioRustico.class, parametros);
                    } else {
                        JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
                    }
                    break;
                case 2://CONTRIBUYENTE
                    if (contribuyenteConsulta != null) {
                        if (contribuyenteConsulta.getCatPredioRusticos() != null && !contribuyenteConsulta.getCatPredioRusticos().isEmpty()) {
                            if (contribuyenteConsulta.getCatPredioRusticos().size() == 1) {
                                parametros.put("regCatastral", contribuyenteConsulta.getCatPredioRusticos().get(0).getRegCatastral());
                                parametros.put("idPredial", contribuyenteConsulta.getCatPredioRusticos().get(0).getIdPredial());
                                parametros.put("parroquia", contribuyenteConsulta.getCatPredioRusticos().get(0).getParroquia());
                                predioRuralConsulta = (CatPredioRustico) manager.findObjectByParameter(CatPredioRustico.class, parametros);
                            } else {
                                prediosRuralesConsulta.addAll(contribuyenteConsulta.getCatPredioRusticos());
                            }
                        } else {
                            JsfUti.messageInfo(null, "Contribuyente no posee Predios", "");
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "Realice la busqueda del Contribuyente.");
                    }
                    break;
                default:
                    break;
            }
            if (prediosRuralesConsulta != null && !prediosRuralesConsulta.isEmpty() && prediosRuralesConsulta.size() == 1) {
                parametros = new HashMap<>();
                parametros.put("id", prediosRuralesConsulta.get(0).getId());
                predioRuralConsulta = (CatPredioRustico) manager.findObjectByParameter(CatPredioRustico.class, parametros);
            }
            if (predioRuralConsulta != null) {
                if (!this.prediosRurales.contains(predioRuralConsulta)) {
                    this.prediosRurales.add(predioRuralConsulta);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            } else {
                if (prediosRuralesConsulta != null && prediosRuralesConsulta.size() > 1) {
                    JsfUti.update("frmPrediosRural");
                    JsfUti.executeJS("PF('dlgPrediosRuralConsulta').show();");
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio no encontrado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ProcesarLiquidaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarPredioRural2017() {
        predioRuralConsulta = null;
        parametros = new HashMap<>();
        try {
            if (codigoRural2017 != null && codigoRural2017.trim().length() > 0) {
                parametros.put("codigoCatastral", codigoRural2017.trim());
                predioRural2017 = (EmisionesRuralesExcel) manager.findObjectByParameter(EmisionesRuralesExcel.class, parametros);
            } else {
                JsfUti.messageError(null, "Error", "Datos no validos para la Consulta");
            }
            if (predioRural2017 != null) {
                if (!this.prediosRurales2017.contains(predioRural2017)) {
                    this.prediosRurales2017.add(predioRural2017);
                } else {
                    JsfUti.messageInfo(null, "Mensaje", "Predio ya se encuentra agregado.");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void seleccionarPredio(Long tipoPredio) {
        try {
            parametros = new HashMap<>();
            switch (tipoPredio.intValue()) {
                case 1:
                    if (prediosUrbanosConsultaSeleccionados != null && !prediosUrbanosConsultaSeleccionados.isEmpty()) {
                        for (CatPredio pucs : prediosUrbanosConsultaSeleccionados) {
                            parametros.put("numPredio", pucs.getNumPredio());
                            predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                            if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                                this.prediosUrbanos.add(predioUrbanoConsulta);
                            }
                        }
                        JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                case 2:
                    if (prediosRuralesConsultaSeleccionados != null && !prediosRuralesConsultaSeleccionados.isEmpty()) {
                        for (CatPredioRustico prcs : prediosRuralesConsultaSeleccionados) {
                            if (!this.prediosRurales.contains(prcs)) {
                                this.prediosRurales.add(prcs);
                            }
                        }
                        JsfUti.executeJS("PF('dlgPrediosRuralConsulta').hide();");
                    } else {
                        JsfUti.messageInfo(null, "Mensaje", "Seleccione un predio, luego clic en Seleccionar");
                    }
                    break;
                case 3:
                    for (CatPredio pucs : prediosUrbanosConsulta) {
                        parametros.put("numPredio", pucs.getNumPredio());
                        predioUrbanoConsulta = (CatPredio) manager.findObjectByParameter(CatPredio.class, parametros);
                        if (!this.prediosUrbanos.contains(predioUrbanoConsulta)) {
                            this.prediosUrbanos.add(predioUrbanoConsulta);
                        }
                    }
                    JsfUti.executeJS("PF('dlgPrediosConsulta').hide();");
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Logger.getLogger(ProcesarLiquidaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarPredioUrbano(CatPredio urbano) {
        this.prediosUrbanos.remove(urbano);
    }

    public void eliminarPredioRural(CatPredioRustico rural) {
        this.prediosRurales.remove(rural);
    }

    public void eliminarPredioRural2017(EmisionesRuralesExcel pr) {
        this.prediosRurales2017.remove(pr);
    }

    public void limpiarTablas() {
        try {
            prediosRurales2017 = new ArrayList<>();
        } catch (Exception e) {
            Logger.getLogger(InformesResoluciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarReporte(Long tipoReporte) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            predios = new ArrayList<>();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("recaudaciones");
            System.out.println("path" + path);
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            //ss.agregarParametro("LOGO_URL", path + SisVars.sisLogo1);
            ss.agregarParametro("LOGO", path + SisVars.sisLogo1);
            System.out.println("path" + path);
            ss.agregarParametro("OPCION_REPORTE", opcionesReporte);
            if (porAnio) {
                ss.agregarParametro("ANIO", anioReporte);
            } else {

                ss.agregarParametro("ANIO", new Long(Calendar.getInstance().get(Calendar.YEAR) + ""));
            }
            switch (tipoReporte.intValue()) {
                case 1:
                    for (CatPredio prediosUrbano : prediosUrbanos) {
                        predios.add(prediosUrbano.getId());
                    }
                    ss.setNombreReporte("liquidacionPrediosUrbanos");
                    break;
                case 2:
                    for (CatPredioRustico r : prediosRurales) {
                        predios.add(r.getId());
                    }
                    ss.setNombreReporte("liquidacionPrediosRurales");
                    break;
                case 3:
                    ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/"));
                    ss.setNombreSubCarpeta("coactiva");
                    for (CatPredio prediosUrbano : prediosUrbanos) {
                        predios.add(prediosUrbano.getId());
                    }
                    ss.setNombreReporte("estadoCuentaPredial");
                    break;
                default:
                    break;
            }
            ss.agregarParametro("FECHA", new Date());
            ss.agregarParametro("PREDIOS", (Collection) predios);
            if (predios != null && !predios.isEmpty()) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            } else {
                JsfUti.messageInfo(null, "Mensaje", "La lista de Predios no debe estar Vacia");
            }
        } catch (Exception e) {
            Logger.getLogger(ProcesarLiquidaciones.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public List<CatPredio> getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(List<CatPredio> prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public List<CatPredioRustico> getPrediosRurales() {
        return prediosRurales;
    }

    public void setPrediosRurales(List<CatPredioRustico> prediosRurales) {
        this.prediosRurales = prediosRurales;
    }

    public Long getTipoConsultaUrbano() {
        return tipoConsultaUrbano;
    }

    public void setTipoConsultaUrbano(Long tipoConsultaUrbano) {
        this.tipoConsultaUrbano = tipoConsultaUrbano;
    }

    public CatEnteLazy getContribuyentes() {
        return contribuyentes;
    }

    public void setContribuyentes(CatEnteLazy contribuyentes) {
        this.contribuyentes = contribuyentes;
    }

    public CatEnte getContribuyenteConsulta() {
        return contribuyenteConsulta;
    }

    public void setContribuyenteConsulta(CatEnte contribuyenteConsulta) {
        this.contribuyenteConsulta = contribuyenteConsulta;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public Long getTipoConsultaRural() {
        return tipoConsultaRural;
    }

    public void setTipoConsultaRural(Long tipoConsultaRural) {
        this.tipoConsultaRural = tipoConsultaRural;
    }

    public List<CatParroquia> getParroquiasRurales() {
        return parroquiasRurales;
    }

    public void setParroquiasRurales(List<CatParroquia> parroquiasRurales) {
        this.parroquiasRurales = parroquiasRurales;
    }

    public Boolean getPorAnio() {
        return porAnio;
    }

    public void setPorAnio(Boolean porAnio) {
        this.porAnio = porAnio;
    }

    public Long getAnioReporte() {
        return anioReporte;
    }

    public void setAnioReporte(Long anioReporte) {
        this.anioReporte = anioReporte;
    }

    public List<CatPredio> getPrediosUrbanosConsulta() {
        return prediosUrbanosConsulta;
    }

    public void setPrediosUrbanosConsulta(List<CatPredio> prediosUrbanosConsulta) {
        this.prediosUrbanosConsulta = prediosUrbanosConsulta;
    }

    public List<CatPredioRustico> getPrediosRuralesConsulta() {
        return prediosRuralesConsulta;
    }

    public void setPrediosRuralesConsulta(List<CatPredioRustico> prediosRuralesConsulta) {
        this.prediosRuralesConsulta = prediosRuralesConsulta;
    }

    public CatPredio getPredioUrbanoConsulta() {
        return predioUrbanoConsulta;
    }

    public void setPredioUrbanoConsulta(CatPredio predioUrbanoConsulta) {
        this.predioUrbanoConsulta = predioUrbanoConsulta;
    }

    public CatPredioRustico getPredioRuralConsulta() {
        return predioRuralConsulta;
    }

    public void setPredioRuralConsulta(CatPredioRustico predioRuralConsulta) {
        this.predioRuralConsulta = predioRuralConsulta;
    }

    public Long getOpcionesReporte() {
        return opcionesReporte;
    }

    public void setOpcionesReporte(Long opcionesReporte) {
        this.opcionesReporte = opcionesReporte;
    }

    public List<CatPredio> getPrediosUrbanosConsultaSeleccionados() {
        return prediosUrbanosConsultaSeleccionados;
    }

    public void setPrediosUrbanosConsultaSeleccionados(List<CatPredio> prediosUrbanosConsultaSeleccionados) {
        this.prediosUrbanosConsultaSeleccionados = prediosUrbanosConsultaSeleccionados;
    }

    public List<CatPredioRustico> getPrediosRuralesConsultaSeleccionados() {
        return prediosRuralesConsultaSeleccionados;
    }

    public void setPrediosRuralesConsultaSeleccionados(List<CatPredioRustico> prediosRuralesConsultaSeleccionados) {
        this.prediosRuralesConsultaSeleccionados = prediosRuralesConsultaSeleccionados;
    }

    public String getCodigoRural2017() {
        return codigoRural2017;
    }

    public void setCodigoRural2017(String codigoRural2017) {
        this.codigoRural2017 = codigoRural2017;
    }

    public EmisionesRuralesExcel getPredioRural2017() {
        return predioRural2017;
    }

    public void setPredioRural2017(EmisionesRuralesExcel predioRural2017) {
        this.predioRural2017 = predioRural2017;
    }

    public List<EmisionesRuralesExcel> getPrediosRurales2017() {
        return prediosRurales2017;
    }

    public void setPrediosRurales2017(List<EmisionesRuralesExcel> prediosRurales2017) {
        this.prediosRurales2017 = prediosRurales2017;
    }

}
