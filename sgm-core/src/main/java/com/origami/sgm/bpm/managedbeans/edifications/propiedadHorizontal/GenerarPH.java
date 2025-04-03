/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.CombinacionPH;
import com.origami.sgm.bpm.models.ModelPrediosPH;
import com.origami.sgm.bpm.models.ModelPropiedadHorizontal;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Utils;

/**
 * Genarar la divisiones de Propiedad Horizontal
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class GenerarPH extends BpmManageBeanBaseRoot implements Serializable {

    protected static final long serialVersionUID = 1L;
    @javax.inject.Inject
    protected PropiedadHorizontalServices services;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    protected HistoricoTramites ht;

    protected String observ;
    protected Boolean rechazado = false;

    // Variable Propiedad Horizontal
    protected Boolean prediosGenerados = false;
    protected Boolean alicuotas = false;
    protected Boolean generarPredios = false;
    protected CatPredio predio;
    protected List<CatPredio> listPredio;
    protected List<CombinacionPH> phvPhhList;
    protected ModelPrediosPH modelPredios;
    protected List<ModelPrediosPH> alicuotasPredio;
    protected ModelPropiedadHorizontal model;
    protected CombinacionPH phhPhv;

    protected CatCanton canton;
    protected List<CatCanton> catCantonsList;
    protected CatParroquia parroquia;
    protected List<CatParroquia> catParroquiaList;

    @PostConstruct
    public void initView() {
        if (session != null) {

            canton = new CatCanton();
            parroquia = new CatParroquia();
            canton = services.getCatCantonById(1L);
            catCantonsList = services.getInscripcion().getCatCantonList();
            catParroquiaList = services.getFichaServices().getCatPerroquiasListByCanton(canton.getId());

            model = new ModelPropiedadHorizontal();
            this.setTaskId(session.getTaskID());
            ht = new HistoricoTramites();
            predio = new CatPredio();
            modelPredios = new ModelPrediosPH();
            model = (ModelPropiedadHorizontal) JsfUti.getSessionBean("ModelPropiedadHorizontal");
            if (session.getTaskID() != null) {
                ht = permisoServices.getHistoricoTramiteById(Long.parseLong(getVariable(getTaskId(), "tramite").toString()));
                if (model != null) {
                    prediosGenerados = true;
                    alicuotasPredio = model.getAlicuotasPredio();

                } else {
                    model = new ModelPropiedadHorizontal();
                }
                for (HistoricoTramiteDet h : ht.getHistoricoTramiteDetCollection()) {
                    CatPredio p;
                    if (h.getEstado()) {
                        p = services.getCatPredioById(h.getPredio().getId());
                        if (p.getCalle() == null) {
                            p.setCalle("Vehicular");
                        }
                        if (p.getCiudadela() != null) {
                            if (p.getCiudadela().getCodParroquia() != null) {
                                parroquia = services.getCatParroquia(p.getCiudadela().getCodParroquia().getId());
                                canton = services.getCatCantonById(parroquia.getIdCanton().getId());
                            } else {
                                parroquia = Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1);
                            }
                        } else {
                            parroquia = Utils.get(canton.getCatParroquiaCollection(), canton.getCatParroquiaCollection().size() - 1);
                        }
                        predio = p;
                    }
                }
            }
        } else {
            this.continuar();
        }
    }

    public void verificarPredio() {
        listPredio = new ArrayList<>();
        listPredio = services.getCatPredioList(predio);
        if (!listPredio.isEmpty()) {
            List<ModelPrediosPH> al = new ArrayList<>();

            for (CatPredio l : listPredio) {
                ModelPrediosPH m = new ModelPrediosPH();
                if (l.getEstado().equals("G")) {
                    m.setPredio(l);
                    if (l.getCatPredioS6() != null) {
                        CatPredioS6 s6 = l.getCatPredioS6();
                        m.setPredioS6(s6);
                    } else {
                        m.getPredioS6().setAlicuota(BigDecimal.ZERO);
                    }
                    al.add(m);
                }
            }
            if (!al.isEmpty()) {
                alicuotas = false;
                alicuotasPredio = al;
                prediosGenerados = true;
            } else {
                modelPredios.setPredio(listPredio.get(0));
                alicuotas = true;
                phhPhv = new CombinacionPH();
            }

        } else {
            JsfUti.messageInfo(null, "Propiedad no esta ingresada", "");
        }
    }

    public void generarAlicuotas() {
        phvPhhList = new ArrayList<>();
        if (phhPhv.getPhv() >= 1) {
            phvPhhList = services.generarAlicuotas(phhPhv.getPhh(), phhPhv.getPhv());
            generarPredios = true;
        }
    }

    public void generarPredios() {
        alicuotasPredio = new ArrayList<>();
        AclUser user = permisoServices.getAclUserByUser(session.getName_user());
        Boolean generar = false;
        for (CombinacionPH l : phvPhhList) {
            if (l.getPhh() == 0) {
                JsfUti.messageInfo(null, "No ha ingresado el Número de Propiedad Horizontal", "");
                return;
            }
            generar = true;
            break;
        }
        if (generar) {
            alicuotasPredio = services.generarPredios(listPredio, phhPhv, phvPhhList, user);
            prediosGenerados = true;
            alicuotasPredio = services.guardarPrediosPropiedadHorizontal(alicuotasPredio);
        }

    }

    public void agregarPropietario(ModelPrediosPH pg) {
        model.setPrediosPH(new ModelPrediosPH());
        model.setPrediosPH(pg);
        model.setAlicuotasPredio(alicuotasPredio);
        JsfUti.setSessionBean("ModelPropiedadHorizontal", model);
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/propiedadHorizontal/generarPropiedadHorizontal1.xhtml");
    }

    public void borrarPredio(ModelPrediosPH pg) {
        JsfUti.messageInfo(null, "Predio Borrado.", "");
        alicuotasPredio.remove(pg);
        JsfUti.update("formPropHoriz:dtPredGener");
    }

    public void rechazar() {
        rechazado = true;
        JsfUti.update("forObsPC");
        JsfUti.executeJS("PF('obs').show();");
    }

    public void guardar() {
        BigDecimal alicuotaAcumul = BigDecimal.ZERO;
        int contarPredios = 0;
        for (ModelPrediosPH predios : model.getAlicuotasPredio()) {
            if (predios.getPredioS6().getAlicuota() != null) {
                alicuotaAcumul = alicuotaAcumul.add(predios.getPredioS6().getAlicuota());
                if (predios.getImage() != null && predios.getImage()) {
                    contarPredios++;
                }
            }
        }
        alicuotaAcumul = alicuotaAcumul.setScale(0, RoundingMode.HALF_UP);
        if (alicuotaAcumul.compareTo(new BigDecimal("100")) != 0 || alicuotaAcumul.compareTo(new BigDecimal("100.0")) != 0 || alicuotaAcumul.compareTo(new BigDecimal("100.00")) != 0) {
            System.out.println(alicuotaAcumul);
            JsfUti.messageError(null, "Error En Alicuotas", "La suma de la alicuotas debe ser 100.");
            return;
        }
        if (contarPredios == model.getAlicuotasPredio().size()) {
            JsfUti.setSessionBean("ModelPropiedadHorizontal", model);
            model = services.guardarPrediosYEscrituras(model);
            if (model != null) {
                rechazado = false;
                JsfUti.update("forObsPC");
                JsfUti.executeJS("PF('obs').show();");
                JsfUti.removeSessionBean("ModelPropiedadHorizontal");
            } else {
                JsfUti.messageError(null, "Error", "Ocurrio Un Error al intentar Guardar.");
            }
        } else {
            JsfUti.messageError(null, "Error", "Faltan por Ingresar " + (model.getAlicuotasPredio().size() - contarPredios) + " Propiedades.");
        }
//        JsfUti.update("formPropHoriz:dtPredGener");
    }

    public void completar(boolean aprobado) {
        if (observ == null) {
            JsfUti.messageInfo(null, "Debe Ingresar las Observaciones", "");
            return;
        }
        Observaciones ob = services.guardarObservaciones(ht, session.getName_user(), observ, this.getTaskDataByTaskID().getTaskDefinitionKey());

        if (ob != null) {
            AclUser director = services.getPermiso().getAclUserByUser(ht.getTipoTramite().getUserDireccion());
            Map paramts = new HashMap<>();
            paramts.put("id", 2L);
            MsgFormatoNotificacion formato = services.getPermiso().getMsgFormatoNotificacionByTipo(paramts);
            String mensage = formato.getHeader()
                    + " <br/><br/><h2>Su trámite Propiedad Horizontal a culminado exitosamente"
                    + "	acercarse al municipio a retirarlo </h2><br/>"
                    + "	att.<br/>"
                    + "	Arq. " + director.getEnte().getApellidos() + " " + director.getEnte().getNombres()
                    + "<br/><br/>" + formato.getFooter();
            ht.setEstado("Finalizado");
            services.actualizarHistoricoTramites(ht);
            HashMap<String, Object> paramt = new HashMap<>();
            paramt.put("idProcess", session.getTaskID());
            paramt.put("subject", "EL trámite " + ht.getId() + " ha Finalizado");
            if (aprobado) {
                paramt.put("message", mensage);
            }
            paramt.put("tdocs", !this.getFiles().isEmpty());
            paramt.put("aprobado", aprobado);
            this.completeTask(session.getTaskID(), paramt);
            this.continuar();
        }
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public Boolean getAlicuotas() {
        return alicuotas;
    }

    public void setAlicuotas(Boolean alicuotas) {
        this.alicuotas = alicuotas;
    }

    public Boolean getGenerarPredios() {
        return generarPredios;
    }

    public void setGenerarPredios(Boolean generarPredios) {
        this.generarPredios = generarPredios;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<CombinacionPH> getPhvPhhList() {
        return phvPhhList;
    }

    public void setPhvPhhList(List<CombinacionPH> phvPhhList) {
        this.phvPhhList = phvPhhList;
    }

    public Boolean getPrediosGenerados() {
        return prediosGenerados;
    }

    public void setPrediosGenerados(Boolean prediosGenerados) {
        this.prediosGenerados = prediosGenerados;
    }

    public List<CatPredio> getListPredio() {
        return listPredio;
    }

    public void setListPredio(List<CatPredio> listPredio) {
        this.listPredio = listPredio;
    }

    public ModelPrediosPH getModelPredios() {
        return modelPredios;
    }

    public void setModelPredios(ModelPrediosPH modelPredios) {
        this.modelPredios = modelPredios;
    }

    public CombinacionPH getPhhPhv() {
        return phhPhv;
    }

    public void setPhhPhv(CombinacionPH phhPhv) {
        this.phhPhv = phhPhv;
    }

    public List<ModelPrediosPH> getAlicuotasPredio() {
        return alicuotasPredio;
    }

    public void setAlicuotasPredio(List<ModelPrediosPH> alicuotasPredio) {
        this.alicuotasPredio = alicuotasPredio;
    }

    public List<CatCanton> getCatCantonsList() {
        return catCantonsList;
    }

    public void setCatCantonsList(List<CatCanton> catCantonsList) {
        this.catCantonsList = catCantonsList;
    }

    public List<CatParroquia> getCatParroquiaList() {
        return catParroquiaList;
    }

    public void setCatParroquiaList(List<CatParroquia> catParroquiaList) {
        this.catParroquiaList = catParroquiaList;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public Boolean getRechazado() {
        return rechazado;
    }

    public void setRechazado(Boolean rechazado) {
        this.rechazado = rechazado;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public GenerarPH() {
    }

}
