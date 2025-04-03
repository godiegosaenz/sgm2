/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.mejoras;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AvalEdadZonaConst;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatUbicacion;
import com.origami.sgm.entities.MejObra;
import com.origami.sgm.entities.MejTipoObra;
import com.origami.sgm.entities.MejValoresObraUbicacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.models.PrediosManzanaDTO;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.MejObraLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author HenryPilco, XndySxnchez
 */
@Named
@ViewScoped
public class Obras implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.inject.Inject
    private Entitymanager manager;
    private Map<String, Object> parametros = new HashMap<>();

    @Inject
    private ServletSession servletSession;

    protected MejObraLazy obras;
    protected MejObra obra;
    protected List<MejTipoObra> tiposObra;
    protected List<CatUbicacion> ubicaciones;
    protected Long anioReporte;

    private Boolean nextPage = Boolean.FALSE;
    private String tipoDefinicion;

    private List<PrediosManzanaDTO> prediosXManzana, zonasPredios, sectoresPredios;

    private List<PrediosManzanaDTO> prediosDTOSeleccionados;
    protected CatPredioLazy predios;
    private List<CatPredio> catPredios;

    protected CatParroquia parroquia;

    private List<RenRubrosLiquidacion> rubrosList;

    private RenRubrosLiquidacion rubroSelected;

    @PostConstruct
    public void initView() {
        try {
            obras = new MejObraLazy();
            obra = new MejObra();
            parametros.put("estado", Boolean.TRUE);
            tiposObra = manager.findObjectByParameterList(MejTipoObra.class, parametros);
            parametros = new HashMap<>();
            parametros.put("estado", Boolean.TRUE);
            ubicaciones = manager.findObjectByParameterList(CatUbicacion.class, parametros);
        } catch (Exception e) {
            Logger.getLogger(Obras.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void loadRegistersObras() {
        this.catPredios = new ArrayList();
        this.prediosXManzana = new ArrayList<>();
        this.prediosDTOSeleccionados = new ArrayList<>();
        this.predios = new CatPredioLazy("A");
        this.tipoDefinicion = "";
        this.prediosXManzana = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getPrediosXManzana, new String[]{"estado"}, new Object[]{"A"});
        this.zonasPredios = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getZonaPredios, new String[]{"estado"}, new Object[]{"A"});
        this.sectoresPredios = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getSectoresPredios, new String[]{"estado"}, new Object[]{"A"});
        this.rubrosList = manager.findAll(QuerysFinanciero.getRubrosByTipoLiquidacionCodRubroASC, new String[]{"tipoLiq"}, new Object[]{13L});
        updatePrediosDTO();
    }

    

    public void updatePrediosDTO() {
        int count = 0;
        for (PrediosManzanaDTO prediosManzanaDTO : this.prediosXManzana) {
            prediosManzanaDTO.setId(count);
            this.prediosXManzana.set(count, prediosManzanaDTO);
            count++;
        }
        count = 0;
        for (PrediosManzanaDTO prediosManzanaDTO : this.zonasPredios) {
            prediosManzanaDTO.setId(count);
            this.zonasPredios.set(count, prediosManzanaDTO);
            count++;
        }
        count = 0;
        for (PrediosManzanaDTO prediosManzanaDTO : this.sectoresPredios) {
            prediosManzanaDTO.setId(count);
            this.sectoresPredios.set(count, prediosManzanaDTO);
            count++;
        }
    }

    public SelectItem[] getListTipoObras() {
        int cantRegis = tiposObra.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(tiposObra.get(i).getDescripcion(), tiposObra.get(i).getDescripcion());
        }
        return options;
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

    public void registerObraPage() {
        Faces.redirectFaces("/faces/vistaprocesos/mejoras/_registroObras.xhtml");
        nextPage = Boolean.TRUE;
    }

    public void redirectToCreateNewRubros(RenTipoLiquidacion tipoLiquidacion) {
        servletSession.instanciarParametros();
        servletSession.agregarParametro("idTipoLiquidacion", 13);
        JsfUti.redirectFacesNewTab("/rentas/mantenimiento/asignacionrubros.xhtml");

    }

    public String onFlowProcess(FlowEvent event) {
        if (event.getNewStep().equals("ubicacion")) {
//            if (obra != null) {
//                if (obra.getTipoObra() == null || obra.getAnio() == null || obra.getCostoTotal() == null
//                        || obra.getSubsidio() == null || obra.getPlazo() == null) {
//                    Faces.messageWarning(null, "Debe registrar todos los  campos Obligatorios", "");
//                    Faces.update("growl");
//                    return event.getOldStep();
//                }
//            }
//
        }
        if (event.getNewStep().equals("rubrosSelection")) {
            if (this.tipoDefinicion.equals("Parroquia")) {

            } else {
                if (this.tipoDefinicion.equals("Zona") || this.tipoDefinicion.equals("Mz")
                        || this.tipoDefinicion.equals("Sector")) {
                    if (prediosDTOSeleccionados != null) {
                        if (prediosDTOSeleccionados.isEmpty()) {
                            Faces.messageWarning(null, "Debe Seleccionar la(s) Ubicaciones Correspondientes", "");
                            Faces.update("growl");
                            return event.getOldStep();
                        }
                    } else {
                        Faces.messageWarning(null, "Debe Seleccionar la(s) Ubicaciones Correspondientes", "");
                        Faces.update("growl");
                        return event.getOldStep();
                    }

                }
                if (this.tipoDefinicion.equals("Lote")) {
                    System.out.println("lote :D ");
                    if (catPredios != null) {
                        if (catPredios.isEmpty()) {
                            Faces.messageWarning(null, "Debe Seleccionar la(s) Ubicaciones Correspondientes", "");
                            Faces.update("growl");
                            return event.getOldStep();
                        }
                    } else {
                        Faces.messageWarning(null, "Debe Seleccionar la(s) Ubicaciones Correspondientes", "");
                        Faces.update("growl");
                        return event.getOldStep();
                    }

                }
            }

        }
        if (event.getNewStep().equals("confirmar")) {
            if (rubroSelected == null) {
                Faces.messageWarning(null, "Debe Asignar Rubros al tipo de Obra Escogida", "");
                Faces.update("growl");
                return event.getOldStep();
            }
        }
        return event.getNewStep();
    }

    public void cleanVarPrediosDTO() {
        System.out.println("vamo" + this.tipoDefinicion);
        this.catPredios = new ArrayList();
        this.prediosDTOSeleccionados = new ArrayList();
        System.out.println("vamo" + this.tipoDefinicion);
    }
    

    public void seleccionarObra(MejObra obra) {
        if (obra == null) {
            this.obra = new MejObra();
        } else {
            this.obra = obra;
        }
    }

    public void calcularValores() {
        if (obra != null && obra.getCostoTotal() != null && obra.getSubsidio() != null) {
            obra.setValorSubcidiado(obra.getCostoTotal().multiply(obra.getSubsidio()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));
            obra.setValorRecuperar(obra.getCostoTotal().subtract(obra.getValorSubcidiado()));
        }
        if (obra != null && obra.getPlazo() != null && obra.getValorRecuperar() != null) {
            obra.setValorEmisionAnual(obra.getValorRecuperar().divide(new BigDecimal(obra.getPlazo()), 2, BigDecimal.ROUND_HALF_UP));
        }
    }

    public void seleccionUbicacion() {
        if (obra.getUbicacion() != null && obra.getUbicacion().getId().equals(5L)) {
            MejValoresObraUbicacion ubicacion;
            obra.setValoresObraUbicacionsCollection(new ArrayList<MejValoresObraUbicacion>());
            for (CatUbicacion u : ubicaciones) {
                if (!u.getId().equals(5L)) {
                    ubicacion = new MejValoresObraUbicacion();
                    ubicacion.setUbicacion(u);
                    obra.getValoresObraUbicacionsCollection().add(ubicacion);
                }
            }
        } else {
            obra.setValoresObraUbicacionsCollection(null);
        }
    }

    public void calcularValoresUbicacion(MejValoresObraUbicacion obraUbicacion) {
        obraUbicacion.setValorRecuperar(obra.getValorEmisionAnual().multiply(obraUbicacion.getPorcentaje()).divide(new BigDecimal("100")));
    }

    public void guardarObra() {
        try {
            if (obra != null && obra.getTipoObra() != null && obra.getUbicacion() != null && obra.getAnio() != null && obra.getCostoTotal() != null
                    && obra.getSubsidio() != null && obra.getPlazo() != null) {
                List<MejValoresObraUbicacion> listObraUbicacion = (List<MejValoresObraUbicacion>) obra.getValoresObraUbicacionsCollection();
                obra = (MejObra) manager.persist(obra);
                if (listObraUbicacion != null) {
                    for (MejValoresObraUbicacion item : listObraUbicacion) {
                        item.setObra(obra);
                        manager.persist(item);
                    }
                } else {
                    MejValoresObraUbicacion obraUbicacion = new MejValoresObraUbicacion();
                    obraUbicacion.setObra(obra);
                    obraUbicacion.setUbicacion(obra.getUbicacion());
                    obraUbicacion.setPorcentaje(new BigDecimal("100.00"));
                    obraUbicacion.setValorRecuperar(obra.getValorRecuperar());
                    manager.persist(obraUbicacion);
                }
                JsfUti.messageInfo(null, "Mensaje.", "Guardado Exitoso.");
            } else {
                JsfUti.messageInfo(null, "Mensaje.", "Los campos del Formulario son Obligatorios.");
            }
        } catch (Exception e) {
            Logger.getLogger(Obras.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarReporteObras() {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("obras");
            servletSession.setNombreSubCarpeta("mejoras");
            servletSession.agregarParametro("LOGO", path + SisVars.sisLogo);
            servletSession.agregarParametro("ANIO", this.anioReporte);
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");

        } catch (Exception e) {
            Logger.getLogger(Obras.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CatParroquia> getParroquias() {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("idCanton", manager.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA}));
        return manager.findObjectByParameterOrderList(CatParroquia.class, paramt, new String[]{"idCanton"}, true);
    }

    public MejObra getObra() {
        return obra;
    }

    public void setObra(MejObra obra) {
        this.obra = obra;
    }

    public MejObraLazy getObras() {
        return obras;
    }

    public void setObras(MejObraLazy obras) {
        this.obras = obras;
    }

    public List<MejTipoObra> getTiposObra() {
        return tiposObra;
    }

    public void setTiposObra(List<MejTipoObra> tiposObra) {
        this.tiposObra = tiposObra;
    }

    public List<CatUbicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<CatUbicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public Long getAnioReporte() {
        return anioReporte;
    }

    public void setAnioReporte(Long anioReporte) {
        this.anioReporte = anioReporte;
    }

    public List<PrediosManzanaDTO> getPrediosXManzana() {
        return prediosXManzana;
    }

    public void setPrediosXManzana(List<PrediosManzanaDTO> prediosXManzana) {
        this.prediosXManzana = prediosXManzana;
    }

    public String getTipoDefinicion() {
        return tipoDefinicion;
    }

    public void setTipoDefinicion(String tipoDefinicion) {
        this.tipoDefinicion = tipoDefinicion;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public List<PrediosManzanaDTO> getZonasPredios() {
        return zonasPredios;
    }

    public void setZonasPredios(List<PrediosManzanaDTO> zonasPredios) {
        this.zonasPredios = zonasPredios;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public List<PrediosManzanaDTO> getSectoresPredios() {
        return sectoresPredios;
    }

    public void setSectoresPredios(List<PrediosManzanaDTO> sectoresPredios) {
        this.sectoresPredios = sectoresPredios;
    }

    public List<PrediosManzanaDTO> getPrediosDTOSeleccionados() {
        return prediosDTOSeleccionados;
    }

    public void setPrediosDTOSeleccionados(List<PrediosManzanaDTO> prediosDTOSeleccionados) {
        this.prediosDTOSeleccionados = prediosDTOSeleccionados;
    }

    public List<CatPredio> getCatPredios() {
        return catPredios;
    }

    public void setCatPredios(List<CatPredio> catPredios) {
        this.catPredios = catPredios;
    }

    public List<RenRubrosLiquidacion> getRubrosList() {
        return rubrosList;
    }

    public void setRubrosList(List<RenRubrosLiquidacion> rubrosList) {
        this.rubrosList = rubrosList;
    }

    public RenRubrosLiquidacion getRubroSelected() {
        return rubroSelected;
    }

    public void setRubroSelected(RenRubrosLiquidacion rubroSelected) {
        this.rubroSelected = rubroSelected;
    }

}
