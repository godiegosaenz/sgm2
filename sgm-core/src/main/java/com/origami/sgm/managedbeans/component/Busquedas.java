/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.JsfUti;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
public abstract class Busquedas extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private RentasServices services;

    private CatEnte comprador = new CatEnte();
    private CatEnte vendedorPredioOtroCanton = new CatEnte();
    private Boolean esPersonaComp = true;
    private Boolean esPersonaVend = true;
    protected List<CatPredio> predios;

    public CatPredio consultar(Integer tipoCons, CatPredio predio) {
        CatPredio temp = new CatPredio();
        switch (tipoCons) {
            case 1: // Codigo Anterior

                break;
            case 2: // Codigo Nuevo

                temp = services.permisoServices().getCatPredioByCodigoPredio(predio.getProvincia(),
                        predio.getCanton(), predio.getParroquia(), predio.getZona(), predio.getSector(),
                        predio.getMz(), predio.getLote(), predio.getBloque(), predio.getPiso(), predio.getUnidad(), "A");

                break;
            case 3:// Numero de Predio
                if (predio.getNumPredio() == null) {
                    JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.faltaNumPredio);
                    return null;
                }
                temp = services.permisoServices().getFichaServices().getPredioByNum(predio.getNumPredio().longValue());
                break;

        }
        if (temp != null) {
            return temp;
        } else {
            JsfUti.messageError(null, MessagesRentas.error, MessagesRentas.predioNoEncontrado);
        }
//        JsfUti.update("frmAlcPlus");
        return null;
    }

    public void seleccionarComprador(SelectEvent event) {
        comprador = (CatEnte) EntityBeanCopy.clone(event.getObject());
        if (comprador != null) {
            esPersonaComp = comprador.getEsPersona();
        }
    }

    public void seleccionarVendedor(SelectEvent event) {
        vendedorPredioOtroCanton = (CatEnte) EntityBeanCopy.clone(event.getObject());
        if (vendedorPredioOtroCanton != null) {
            esPersonaVend = vendedorPredioOtroCanton.getEsPersona();
        }
    }
    
    public void seleccionarPredios(SelectEvent event) {
        predios = (List<CatPredio>) event.getObject();
        esPersonaComp = comprador.getEsPersona();
    }

    /**
     * Muestra el listado de los entes
     */
    public void buscarEnte() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/dialogEnte", options, null);
    }

    public Busquedas() {
    }

    public CatEnte getComprador() {
        return comprador;
    }

    public void setComprador(CatEnte comprador) {
        this.comprador = comprador;
    }

    public Boolean getEsPersonaComp() {
        return esPersonaComp;
    }

    public void setEsPersonaComp(Boolean esPersonaComp) {
        this.esPersonaComp = esPersonaComp;
    }

    public Boolean getEsPersonaVend() {
        return esPersonaVend;
    }

    public void setEsPersonaVend(Boolean esPersonaVend) {
        this.esPersonaVend = esPersonaVend;
    }

    public CatEnte getVendedorPredioOtroCanton() {
        return vendedorPredioOtroCanton;
    }

    public void setVendedorPredioOtroCanton(CatEnte vendedorPredioOtroCanton) {
        this.vendedorPredioOtroCanton = vendedorPredioOtroCanton;
    }
 
    public RentasServices getServices() {
        return services;
    }

    public void mostrarDialog(String urlFacelet) {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "80%");
        options.put("position", "center");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog(urlFacelet, options, null);
    }

    public String claveCatastral(CatPredioModel px) {

        String clave = "";
        clave = Utils.completarCadenaConCeros(String.valueOf(px.getProvincia()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getCanton()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getParroquiaShort()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getZona()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getSector()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getMz()), 3)
                + Utils.completarCadenaConCeros(String.valueOf(px.getSolar()), 3)
                + Utils.completarCadenaConCeros(String.valueOf(px.getBloque()), 3)
                + Utils.completarCadenaConCeros(String.valueOf(px.getPiso()), 2)
                + Utils.completarCadenaConCeros(String.valueOf(px.getUnidad()), 3);

        System.out.println("Clave " + clave);
        return clave;

    }

}
