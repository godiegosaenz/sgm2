/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RegpIntervinientes;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RegpLiquidacionDetalles;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.activiti.engine.runtime.ProcessInstance;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class IngresarLiquidacionAntigua extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices reg;

    protected Boolean mostrar = false;
    protected String numero = "";
    protected Integer btnState = 0;
    protected RegpLiquidacionDerechosAranceles liq;
    protected HistoricoTramites ht;
    protected HashMap<String, Object> params;
    protected Collection<RegpIntervinientes> listInterv = new ArrayList<>();
    protected Collection<RegpLiquidacionDetalles> actosPorPagar = new ArrayList<>();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {

    }

    public void buscarTramite() {
        try {
            if (numero != null) {
                if (Utils.validateNumberPattern(numero)) {
                    ht = reg.getHistoricoTramiteById(Long.valueOf(numero));
                    if (ht != null) {
                        if (ht.getRegpLiquidacionDerechosAranceles() != null) {
                            liq = ht.getRegpLiquidacionDerechosAranceles();
                            listInterv = liq.getRegpIntervinientesCollection();
                            actosPorPagar = liq.getRegpLiquidacionDetallesCollection();
                            if (liq.getEstado() == 3) {
                                btnState = 2;
                            }
                            mostrar = true;
                            JsfUti.update("mainForm");
                            JsfUti.messageWarning(null, "Este tramite se encuentra ingresado en el actual sistema.", "");
                        } else {
                            listInterv = new ArrayList<>();
                            actosPorPagar = new ArrayList<>();
                            btnState = 0;
                            mostrar = false;
                            JsfUti.update("mainForm");
                            JsfUti.messageWarning(null, "El tramite con numero de seguimiento: " + ht.getId()
                                    + ", es de tipo: " + ht.getTipoTramiteNombre(), "");
                        }
                    } else {
                        liq = reg.cargarLiquidacionAnterior(Long.valueOf(numero), session.getName_user());
                        if (liq == null) {
                            listInterv = new ArrayList<>();
                            actosPorPagar = new ArrayList<>();
                            btnState = 0;
                            mostrar = false;
                            JsfUti.update("mainForm");
                            JsfUti.messageWarning(null, "Error al cargar Datos.", "");
                        } else {
                            //liq = (RegpLiquidacionDerechosAranceles) acl.find(RegpLiquidacionDerechosAranceles.class, idLiq);
                            ht = liq.getHistoricTramite();
                            listInterv = liq.getRegpIntervinientesCollection();
                            actosPorPagar = liq.getRegpLiquidacionDetallesCollection();
                            btnState = 1;
                            mostrar = true;
                            JsfUti.update("mainForm");
                            JsfUti.messageInfo(null, "Debe guardar la liquidacion y despues ingresarla.", "");
                        }
                    }
                } else {
                    JsfUti.messageWarning(null, "Debe ingresar el numero del seguimiento. Solo numeros.", "");
                }
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(IngresarLiquidacionAntigua.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarTramite() {
        try {
            liq = reg.guardarLiquidacionAntigua(liq, ht);
            if (liq.getId() != null) {
                btnState = 2;
                ht = liq.getHistoricTramite();
                JsfUti.update("mainForm");
                JsfUti.messageInfo(null, "Guardado con Exito.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(IngresarLiquidacionAntigua.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void ingresarTramite() {
        try {
            this.llenarVariablesActiviti();
            ProcessInstance p = this.startProcessByDefinitionKey(ht.getTipoTramite().getActivitykey(), params);
            if (p != null) {
                ht.setCarpetaRep(ht.getId() + "-" + p.getId());
                ht.setIdProceso(p.getId());
                ht.setIdProcesoTemp(p.getId());
                liq.setEstado(4);// liquidacion migrada e ingresada
                liq.setHistoricTramite(ht);
                reg.iniciarTramiteRegistro(liq, session.getName_user(), "Tramite migrado de la version anterior del sistema SGM.");
                btnState = 0;
                JsfUti.update("mainForm");
                JsfUti.messageInfo(null, "Se ingreso el tramite correctamente.", "");
            } else {
                JsfUti.messageError(null, Messages.error, "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(IngresarLiquidacionAntigua.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void llenarVariablesActiviti() {
        Integer cantidad = this.actosPorPagar.size();
        Boolean sonCertificados = true;
        for (RegpLiquidacionDetalles d : this.actosPorPagar) {
            if (d.getActo().getTipoActo().getId() == 1L || d.getActo().getTipoActo().getId() == 3L) {
                sonCertificados = false;
            }
        }
        params = new HashMap<>();
        params.put("carpeta", ht.getTipoTramite().getCarpeta());
        params.put("listaArchivos", new ArrayList<>());
        params.put("listaArchivosFinal", new ArrayList<>());
        params.put("prioridad", 50);
        params.put("tramite", ht.getId());
        params.put("entregado", Boolean.FALSE);
        params.put("director", reg.getUsuarioDirector().getUsuario());
        params.put("supervisor", reg.getUsuarioSupervisor().getUsuario());
        params.put("digitalizador", reg.getUsuarioDigitalizador().getUsuario());
        params.put("listaLiquidadores", reg.getLiquidadoresRegistro());
        params.put("cantidad", cantidad);
        params.put("devolutiva", Boolean.FALSE);
        params.put("to", ht.getCorreos());
        params.put("subject", ht.getTipoTramiteNombre());

        if (ht.getTipoTramite().getAsignacionManual()) {
            params.put("tecnico", "");
            params.put("abogado", "");
            params.put("iniciarTramite", 1);
            if (sonCertificados) {
                params.put("asignado", 1);
            } else if (ht.getTipoTramite().getTieneDigitalizacion()) {
                params.put("asignado", 3);
            } else if (!ht.getTipoTramite().getTieneDigitalizacion()) {
                params.put("asignado", 2);
            }
        } else {
            params.put("tecnico", reg.getTecnicoRegistro(cantidad));
            if (sonCertificados) {
                params.put("iniciarTramite", 3);
            } else {
                params.put("abogado", reg.getAbogadoRegistro(cantidad));
                params.put("iniciarTramite", 2);
            }
        }
    }

    public Boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public RegpLiquidacionDerechosAranceles getLiq() {
        return liq;
    }

    public void setLiq(RegpLiquidacionDerechosAranceles liq) {
        this.liq = liq;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Integer getBtnState() {
        return btnState;
    }

    public void setBtnState(Integer btnState) {
        this.btnState = btnState;
    }

    public Collection<RegpIntervinientes> getListInterv() {
        return listInterv;
    }

    public void setListInterv(Collection<RegpIntervinientes> listInterv) {
        this.listInterv = listInterv;
    }

    public Collection<RegpLiquidacionDetalles> getActosPorPagar() {
        return actosPorPagar;
    }

    public void setActosPorPagar(Collection<RegpLiquidacionDetalles> actosPorPagar) {
        this.actosPorPagar = actosPorPagar;
    }

}
