/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.models.ModelPrediosPH;
import com.origami.sgm.bpm.models.ModelPropiedadHorizontal;
import com.origami.sgm.bpm.models.ModelPropietariosPredio;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPropiedadItem;
import com.origami.sgm.entities.CatTenenciaItem;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.JsfUti;
import util.Messages;
import util.PhoneUtils;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class GenerarPH1 implements Serializable {

    @javax.inject.Inject
    protected PropiedadHorizontalServices services;

    protected Boolean esResidente = false;
    protected Boolean nuevo = true;
    protected Boolean esPropietario;
    protected String representanteLegal;
    protected String top;

    protected List<ModelPrediosPH> modelPredio;
    protected List<ModelPropietariosPredio> propietarios;
    protected ModelPropietariosPredio nuevoPropietario;
    protected ModelPropietariosPredio enteSelect;
    protected ModelPrediosPH modelPredios;
    protected ModelPropiedadHorizontal model;
    protected CatEnte enteNuevo;
    protected CtlgItem tipoPropietario;
    protected CatEnteLazy enteLazy;
    protected EnteCorreo correo;
    protected EnteTelefono telefono;
    private List<EnteTelefono> eliminatTelefono;
    private List<EnteCorreo> eliminatCorreo;

    protected CatPropiedadItem propiedad;
    protected CatTenenciaItem tenencia;
    protected CatCanton canton;
    protected CatTiposDominio dominio;

    @PostConstruct
    public void initView() {
        modelPredio = new ArrayList<>();
        propietarios = new ArrayList<>();
        modelPredios = new ModelPrediosPH();
        model = new ModelPropiedadHorizontal();
        model = (ModelPropiedadHorizontal) JsfUti.getSessionBean("ModelPropiedadHorizontal");
        if (model != null) {
            modelPredios = model.getPrediosPH();
            esPropietario = true;
            top = "Agregar Porpietarios.";
            modelPredios.getPredio().setNomCompPago(null);
        } else {
            model = new ModelPropiedadHorizontal();
        }
        eliminatTelefono = new ArrayList<>();
        eliminatCorreo = new ArrayList<>();

    }

    public void agregarPropietarios() {
        nuevo = true;
        enteNuevo = new CatEnte();
        correo = new EnteCorreo();
        telefono = new EnteTelefono();
        nuevoPropietario = new ModelPropietariosPredio();
        representanteLegal = "";
        JsfUti.update("formProp");
        JsfUti.executeJS("PF('dIngPro').show()");
    }

    public List<CtlgItem> getTiposPropietarios() {
        return services.getCtlgItem("predio.propietario.tipo");
    }

    public void buscarEnte() {
        if (model == null) {
            JsfUti.messageInfo(null, "Se Perdio la sessión Realizar nuevamente la tarea.", "");
            return;
        }
        if (enteNuevo.getCiRuc() != null) {
            Map paramt = new HashMap<>();
            paramt.put("ciRuc", enteNuevo.getCiRuc());
            paramt.put("esPersona", enteNuevo.getEsPersona());
            CatEnte nuwEnt = services.getCatEnteByParemt(paramt);
            if (nuwEnt != null) {
                for (ModelPropietariosPredio p : propietarios) {
                    if (p.getEnte().getId().compareTo(nuwEnt.getId()) == 0) {
                        JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                        return;
                    }
                }
                CatPredioPropietario entP = new CatPredioPropietario();
                enteNuevo = nuwEnt;
                entP.setEnte(enteNuevo);
                entP.setPredio(services.getCatPredioById(model.getPrediosPH().getPredio().getId()));
                entP.setEstado("A");
                entP.setModificado("Catastro");
                entP.setTipo(tipoPropietario);
                entP.setEsResidente(esResidente);
                nuevoPropietario.setEnte(enteNuevo);
                nuevoPropietario.setPropietarios(entP);
                propietarios.add(nuevoPropietario);
//                enteNuevo = new CatEnte();
                JsfUti.update("formIngPropPH:dtProp");
                JsfUti.executeJS("PF('dIngPro').hide()");
            } else {
                JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "");
            }
        }
    }

    public void seleccionarReprest() {
        enteLazy = new CatEnteLazy(true);
        JsfUti.update("formSelectRep");
        JsfUti.executeJS("PF('dSelectRep').show();");
    }

    public void agregarRepresentante(CatEnte represt) {
        enteNuevo.setRepresentanteLegal(new BigInteger(represt.getId().toString()));
        representanteLegal = nombreCompletoEnte(enteNuevo);
        JsfUti.update("formProp");
        JsfUti.executeJS("PF('dSelectRep').hide()");
    }

    public void agregarTelefono() {
        if (telefono.getTelefono() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Número de Telefonico.", "");
            return;
        }
        if (!Utils.validateNumberPattern(telefono.getTelefono())) {
            JsfUti.messageInfo(null, "solo debe Ingresar Números", "");
            return;
        }

        if (!PhoneUtils.getValidNumber(telefono.getTelefono(), "EC")) {
            JsfUti.messageInfo(null, "Número Telefonico invalido", "");
            return;
        }
        telefono.setEnte(enteNuevo);
        enteNuevo.getEnteTelefonoCollection().add(telefono);
        telefono = new EnteTelefono();
        JsfUti.update("formProp:dtTel");
    }

    public void agregarCorreo() {
        if (correo.getEmail() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Correo.", "");
            return;
        }
        if (!Utils.validarEmailConExpresion(correo.getEmail())) {
            JsfUti.messageInfo(null, "Correo Ingresado es invalido.", "");
            return;
        }
        correo.setEnte(enteNuevo);
        enteNuevo.getEnteCorreoCollection().add(correo);
        correo = new EnteCorreo();
        JsfUti.update("formProp:dtCorr");
    }

    public void eliminarTelefono(EnteTelefono tel) {
        if (tel.getId() != null) {
            eliminatTelefono.add(tel);
        }
        enteNuevo.getEnteTelefonoCollection().remove(tel);
        JsfUti.update("formProp:dtTel");
    }

    public void eliminarCorreo(EnteCorreo corr) {
        if (corr.getId() != null) {
            eliminatCorreo.add(corr);
        }
        enteNuevo.getEnteCorreoCollection().remove(corr);
        JsfUti.update("formProp:dtCorr");
    }

    public void agragarNuevoProp() {
        if (enteNuevo.getCiRuc() == null) {
            JsfUti.messageInfo(null, Messages.campoVacio, "Cédula / RUC");
            return;
        }
        if (!Utils.validateNumberPattern(enteNuevo.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.valorIncorrecto, "Cédula / RUC");
            return;
        }
        if (!Utils.validateCCRuc(enteNuevo.getCiRuc())) {
            JsfUti.messageInfo(null, Messages.cedulaCIinvalida, "Cédula / RUC");
            return;
        }
        Map par = new HashMap<>();
        par.put("ciRuc", enteNuevo.getCiRuc());
        CatEnte existeCiRuc = services.getCatEnteByParemt(par);
        if (existeCiRuc != null) {
            JsfUti.messageInfo(null, "Cédula / RUC", "Ya existe un usuario registrado con la mismo documento de Identidad");
            buscarEnte();
            return;
        }
        ModelPropietariosPredio p = new ModelPropietariosPredio();
        CatPredioPropietario cpp = new CatPredioPropietario();
        cpp.setEnte(enteNuevo);
        cpp.setEsResidente(esResidente);
        cpp.setPredio(modelPredios.getPredio());
        cpp.setEstado("A");
        p.setEnte(enteNuevo);
        p.setPropietarios(cpp);

        propietarios.add(p);

        JsfUti.executeJS("PF('dIngPro').hide()");
        JsfUti.update("formIngPropPH:dtProp");
    }

    public void eliminarPropietario(ModelPropietariosPredio p) {
        int index = 0;
        int i = 0;
        if (!propietarios.isEmpty()) {
            for (ModelPropietariosPredio pp : propietarios) {
                if (pp.getPropietarios().getEnte().equals(p.getEnte())) {
                    i = index;
                }
                index++;
            }
            propietarios.remove(i);
            JsfUti.update("formIngPropPH:dtProp");
        }
    }

    public void editarEnte(ModelPropietariosPredio p) {
        nuevo = false;
        enteNuevo = p.getEnte();
        correo = new EnteCorreo();
        telefono = new EnteTelefono();
        nuevoPropietario = p;
        representanteLegal = nombreCompletoEnte(enteNuevo);
        esResidente = p.getPropietarios().getEsResidente();
        JsfUti.update("formProp");
        JsfUti.executeJS("PF('dIngPro').show()");
    }

    public void modificarEnte() {
        modelPredios.getPredio().setNomCompPago(nombreCompletoEnte(enteNuevo));
        JsfUti.update("formIngPropPH:dtProp");
        JsfUti.executeJS("PF('dIngPro').hide()");
    }

    private String nombreCompletoEnte(CatEnte enteNuevo) {
        String nombre = null;
        if (enteNuevo.getApellidos() != null && enteNuevo.getNombres() != null) {
            nombre = enteNuevo.getApellidos() + " " + enteNuevo.getNombres();
        } else if (enteNuevo.getApellidos() != null) {
            nombre = enteNuevo.getApellidos();
        } else if (enteNuevo.getNombres() != null) {
            nombre = enteNuevo.getNombres();
        }
        return nombre;
    }

    public void onRowSelect(SelectEvent event) {
        if (enteSelect != null) {
            modelPredios.getPredio().setNomCompPago(nombreCompletoEnte(enteSelect.getEnte()));
        }
        JsfUti.update("formIngPropPH");
    }

    public void siguiente() {
        if (propietarios.isEmpty()) {
            JsfUti.messageInfo(null, "Debe Agregar una persona juridica o natural por lo menos.", "");
            return;
        }
        if (modelPredios.getPredio().getNomCompPago() == null) {
            JsfUti.messageInfo(null, "Debe seleccionar a la persona para llenar el campo Comprobante de Pago.", "");
            return;
        }
        canton = services.getCatCantonById(1L);
        modelPredios.setListaPropietarios(propietarios);
        esPropietario = false;
        top = "Datos de escritura.";

    }

    // Datos Catastro
    public List<CatPropiedadItem> getCatPropiedadItemList() {
        return services.getCatPropiedadItemList();
    }

    public List<CatTenenciaItem> getCatTenenciaItemList() {
        return services.getCatTenenciaItemList();
    }

    public List<CatCanton> getCatCantonList() {
        return services.getCatCantonList();
    }

    public List<CatTiposDominio> getCatTiposDominio() {
        return services.getCatTiposDominioList();
    }

    public void guardarDatosEscritura() {
        if (canton == null) {
            JsfUti.messageInfo(null, "No ha seleccionado el Cantón.", "Cantón.");
            return;
        }
        if (modelPredios.getPredioS6().getTelfFijo() != null) {
            if (Utils.validateNumberPattern(modelPredios.getPredioS6().getTelfFijo())) {
                if (!PhoneUtils.getValidNumber(modelPredios.getPredioS6().getTelfFijo(), SisVars.region)) {
                    JsfUti.messageInfo(null, "Número Télefono Fijo.", "Número Invalido");
                    return;
                }
            }
        }

        modelPredios.getPredioS6().setCanton(canton);
        modelPredios.getPredio().setTenencia(tenencia);
        modelPredios.getPredio().setPropiedad(propiedad);
//        modelPredios.getPredioS6().setTraslDom(dominio);
        modelPredios.setImage(true);
        model.setPrediosPH(modelPredios);

        int index = 0;
        int mod = 0;
        for (ModelPrediosPH alicuota : model.getAlicuotasPredio()) {
            if (alicuota.getPredio().getId().compareTo(modelPredios.getPredio().getId()) == 0) {
                mod = index;
                break;
            }
            index++;
        }

        model.getAlicuotasPredio().set(mod, modelPredios);
        JsfUti.setSessionBean("ModelPropiedadHorizontal", model);
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/propiedadHorizontal/generarPropiedadHorizontal.xhtml");
    }

    public List<ModelPrediosPH> getModelPredio() {
        return modelPredio;
    }

    public void setModelPredio(List<ModelPrediosPH> modelPredio) {
        this.modelPredio = modelPredio;
    }

    public CatEnte getEnteNuevo() {
        return enteNuevo;
    }

    public void setEnteNuevo(CatEnte enteNuevo) {
        this.enteNuevo = enteNuevo;
    }

    public CtlgItem getTipoPropietario() {
        return tipoPropietario;
    }

    public void setTipoPropietario(CtlgItem tipoPropietario) {
        this.tipoPropietario = tipoPropietario;
    }

    public Boolean getEsResidente() {
        return esResidente;
    }

    public void setEsResidente(Boolean esResidente) {
        this.esResidente = esResidente;
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

    public EnteCorreo getCorreo() {
        return correo;
    }

    public void setCorreo(EnteCorreo correo) {
        this.correo = correo;
    }

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public List<ModelPropietariosPredio> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<ModelPropietariosPredio> propietarios) {
        this.propietarios = propietarios;
    }

    public ModelPrediosPH getModelPredios() {
        return modelPredios;
    }

    public void setModelPredios(ModelPrediosPH modelPredios) {
        this.modelPredios = modelPredios;
    }

    public ModelPropiedadHorizontal getModel() {
        return model;
    }

    public void setModel(ModelPropiedadHorizontal model) {
        this.model = model;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public Boolean getEsPropietario() {
        return esPropietario;
    }

    public void setEsPropietario(Boolean esPropietario) {
        this.esPropietario = esPropietario;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public ModelPropietariosPredio getEnteSelect() {
        return enteSelect;
    }

    public void setEnteSelect(ModelPropietariosPredio enteSelect) {
        this.enteSelect = enteSelect;
    }

    public CatPropiedadItem getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(CatPropiedadItem propiedad) {
        this.propiedad = propiedad;
    }

    public CatTenenciaItem getTenencia() {
        return tenencia;
    }

    public void setTenencia(CatTenenciaItem tenencia) {
        this.tenencia = tenencia;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CatTiposDominio getDominio() {
        return dominio;
    }

    public void setDominio(CatTiposDominio dominio) {
        this.dominio = dominio;
    }

    public GenerarPH1() {
    }

}
