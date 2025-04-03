/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.censocat.restful.JsonUtils;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.util.ValidField;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatEscrituraPropietario;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CatTransferenciaDetalle;
import com.origami.sgm.entities.CatTransferenciaDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.events.GenerarHistoricoPredioPost;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.HiberUtil;
import util.JsfUti;

/**
 *
 * @author dfcalderio
 */
@Named(value = "transferenciaView")
@ViewScoped
public class TransferenciaDominio implements Serializable {

    @Inject
    protected UserSession sess;
    @Inject
    private CatastroServices catastroService;
    @Inject
    protected Entitymanager manager;
    @Inject
    private ServletSession ss;

    @Inject
    protected Event<GenerarHistoricoPredioPost> eventHistorico;

    protected List<CatPredioPropietario> propietarios;
    protected List<CatPredioPropietario> propietariosNuevos;

    protected CatEscritura escritura;
    protected List<CatCanton> cantones;
    protected List<CatEscrituraPropietario> propietariosEscrituras;
    protected List<CatEscritura> escrituras;

    protected boolean addEscritura;
    protected CatPredio predio;
    protected Long idPredio;

    protected String predioAnterior;
    protected String predioActual;
    protected JsonUtils js;
    
    private CatTransferenciaDominio catTransferenciaDominio;
    private CatTransferenciaDetalle catTransferenciaDetalle;

    @PostConstruct
    public void init() {
        js = new JsonUtils();
        cantones = getCantones();
        try {
            if (sess != null) {

                if (ss.getParametros().get("idPredio") != null) {
                    idPredio = Long.parseLong(ss.getParametros().get("idPredio").toString());
                    predio = new CatPredio();
                    predio = catastroService.getPredioId(idPredio);
                    if (predio != null) {
                        predioAnterior = js.generarJson(predio);
                        escrituras = manager.findAll(Querys.getEscriturasByPredioDescId, new String[]{"idPredio"}, new Object[]{predio.getId()});
                        if (escrituras == null) {
                            escrituras = new LinkedList<>();
                        }
                        if (predio.getCatPredioPropietarioCollection() != null) {
                            propietarios = (List<CatPredioPropietario>) manager.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{predio.getId()});
                        }
                        propietariosNuevos = new LinkedList<>();
                        escritura = new CatEscritura();
                    } else {
                        JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
                    }
                } else {
                    JsfUti.redirectFaces("/vistaprocesos/catastro/predios.xhtml");
                }
            }

        } catch (NumberFormatException e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void transferenciaDominio() {

        try {
            escrituras.remove(escritura);
            escritura.setPredio(predio);
            escritura.setIdEscritura(null);
            escritura.setEstado("A");
            escritura = (CatEscritura) manager.persist(escritura);

            for (CatEscritura e : escrituras) {
                e.setEstado("I");
                manager.persist(e);
            }

            catTransferenciaDominio = new CatTransferenciaDominio();
            catTransferenciaDominio.setFecha(new Date());
            catTransferenciaDominio.setPredio(predio);
            catTransferenciaDominio = (CatTransferenciaDominio) manager.persist(catTransferenciaDominio);

            for (CatPredioPropietario pt : propietariosNuevos) {
                catTransferenciaDetalle = new CatTransferenciaDetalle();
                pt.setEstado("A");
                pt.setId(null);
                pt = (CatPredioPropietario) manager.persist(pt);
                CatEscrituraPropietario ep = new CatEscrituraPropietario();
                ep.setEnte(pt.getEnte());
                ep.setEscritura(escritura);
                ep.setPropietario(pt);
                ep.setUsuario(sess.getName_user());
                ep.setFecha(new Date());
                if (pt.getCopropietario()) {
                    ep.setCopropietario(Boolean.TRUE);
                    ep.setPorcentajePosecion(pt.getPorcentajePosecion());
                }
                manager.persist(ep);
                manager.persist(ep);
                catTransferenciaDetalle.setActual(Boolean.TRUE);
                catTransferenciaDetalle.setTransferencia(catTransferenciaDominio);
                catTransferenciaDetalle.setEnte(pt.getEnte());
                manager.persist(catTransferenciaDetalle);
            }
            for (CatPredioPropietario pt : propietarios) {
                pt.setEstado("I");
                manager.persist(pt);
            }
            eventHistorico.fire(new GenerarHistoricoPredioPost(predioAnterior, js.generarJson(predio), "Tranferencia de dominio", sess.getName_user(),predio));
            Faces.messageInfo(null, "Nota!", "Transferencia de dominio realiza con éxito.");
            Faces.redirectFaces("/faces/vistaprocesos/catastro/predios.xhtml");

        } catch (Exception e) {
            Logger.getLogger(TransferenciaDominio.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void propietario(CatPredioPropietario propietario, Boolean editar) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(predio.getId().toString());
        params.put("idPredio", p);
        p = new ArrayList<>();
        if (propietario != null && propietario.getId() != null) {
            p.add(propietario.getId().toString());
        }
        params.put("idCatPredioPro", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        params.put("nuevo", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        p = new ArrayList<>();
        p.add(editar.toString());
        params.put("editar", p);

        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "85%");
        options.put("height", "450");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/propietariosUnSave", options, params);
    }

    public void procesarPropietario(SelectEvent event) {
        CatPredioPropietario propietario = (CatPredioPropietario) event.getObject();

        if (propietario != null) {
            propietario.setId((long) (propietariosNuevos.size() + 1));
            if (!propietariosNuevos.contains(propietario)) {
                propietariosNuevos.add(propietario);
            } else {
                propietariosNuevos.set(propietariosNuevos.indexOf(propietario), propietario);
            }

//            this.setFichaEdifAnt(this.getFichaEdifAct());
//            catast.guardarHistoricoPredio(predio.getNumPredio().longValue(), this.getPredioAnt(), gson2.toJson(predio), sess.getName_user(), "Actualizacion Informacion de propietarios ", getFichaEdifAnt(), getFichaEdifAct(), getFichaModelAnt(), getFichaModelAct(), getDocumento());
            Faces.messageInfo(null, "Nota!", "Propietarios actualizados satisfactoriamente");
        }
    }

    public void eliminarPropietario(CatPredioPropietario propietario) {
        propietario.setEstado("I");
        propietario.setModificado(sess.getName_user());
        //propietario = catastroService.guardarPropietario(propietario, sess.getName_user());

        propietariosNuevos.remove(propietario);
        JsfUti.messageInfo(null, "Propietario", "Propietario eliminado.");
    }

    public void saveEscrituraControl() {
        cantones = manager.findAllEntCopy(CatCanton.class
        );

    }

    public void saveEscritura() {
        String msg = "adicionada";
        if (escrituras.contains(escritura)) {
            escrituras.remove(escritura);
            msg = "editada";
        }
        escritura.setIdEscritura(new Long("0"));
        escritura.setFecCre(new Date());
        escritura.setEstado("A");
        escrituras.forEach((e) -> {
            e.setEstado("I");
        });
        escrituras.add(0, escritura);

        JsfUti.messageInfo(null, "Info !", "Escritura " + msg + " satisfactoriamente.");
        JsfUti.executeJS("PF('dlgEscritura').hide()");
        try {

            //this.predio.setCatEscrituraCollection(escriturasConsulta);
//                if (saveHistoric(predio, "ACTUALIZACION DE ESCRITURAS", getFichaEdifAnt(), getFichaEdifAct(), getFichaModelAnt(), getFichaModelAct())) {
//                    JsfUti.messageInfo(null, "Exito", "Datos grabados Satisfactoriamente");
//                } else {
//                    JsfUti.messageInfo(null, "Exito", "Datos grabados Satisfactoriamente");
//                }
        } catch (Exception e) {
            JsfUti.messageInfo(null, "Error al Guardar", "");
        }
    }

    public void editEscritura(CatEscritura e) {
        escritura = e;

        try {

        } catch (Exception ex) {
            JsfUti.messageInfo(null, "Error al Guardar", "");
        }
    }

    public void deleteEscritura(CatEscritura e) {
        escrituras.remove(e);
        escritura = new CatEscritura();

        try {

        } catch (Exception ex) {
            JsfUti.messageInfo(null, "Error al Guardar", "");
        }
    }

    public List<CtlgItem> getListado(String argumento) {
        HiberUtil.newTransaction();
        List<CtlgItem> ctlgItem = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
        return ctlgItem;
    }

    public List<CatTiposDominio> getDominios() {
        return manager.findAllObjectOrder(CatTiposDominio.class,
                new String[]{"nombre"}, true);
    }

    public Boolean disabledAdd(CatEscritura e) {
        return e.getIdEscritura() != 0;
    }

    public Boolean disabledBtnTransferencia() {

        return !(escritura.getIdEscritura() != null && !propietariosNuevos.isEmpty());
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public CatastroServices getCatastroService() {
        return catastroService;
    }

    public void setCatastroService(CatastroServices catastroService) {
        this.catastroService = catastroService;
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public List<CatEscrituraPropietario> getPropietariosEscrituras() {
        return propietariosEscrituras;
    }

    public void setPropietariosEscrituras(List<CatEscrituraPropietario> propietariosEscrituras) {
        this.propietariosEscrituras = propietariosEscrituras;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public List<CatEscritura> getEscrituras() {
        return escrituras;
    }

    public void setEscrituras(List<CatEscritura> escrituras) {
        this.escrituras = escrituras;
    }

    public boolean isAddEscritura() {
        return addEscritura;
    }

    public void setAddEscritura(boolean addEscritura) {
        this.addEscritura = addEscritura;
    }

    public List<CatPredioPropietario> getPropietariosNuevos() {
        return propietariosNuevos;
    }

    public void setPropietariosNuevos(List<CatPredioPropietario> propietariosNuevos) {
        this.propietariosNuevos = propietariosNuevos;
    }

    public List<CatCanton> getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }

    public String getPredioAnterior() {
        return predioAnterior;
    }

    public void setPredioAnterior(String predioAnterior) {
        this.predioAnterior = predioAnterior;
    }

    public String getPredioActual() {
        return predioActual;
    }

    public void setPredioActual(String predioActual) {
        this.predioActual = predioActual;
    }

    public void validarFormulario(ComponentSystemEvent event) {

        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();
        UIForm form = (UIForm) components.findComponent("frmDlgEscr");

        Set<VisitHint> hints = EnumSet.of(VisitHint.SKIP_UNRENDERED);
        ValidField visitor = new ValidField();

        form.visitTree(VisitContext.createVisitContext(fc, null, hints), visitor);

        int errores = 0;

        UIInput uiCanton = (UIInput) components.findComponent("cmbCanton");
        String canton = uiCanton.getLocalValue() == null ? ""
                : uiCanton.getLocalValue().toString();

        UIInput uiTipoPro = (UIInput) components.findComponent("tipoProtclz");
        String tipoPro = uiTipoPro.getLocalValue() == null ? ""
                : uiTipoPro.getLocalValue().toString();

        if (canton.equals("")) {
            errores++;
            uiCanton.setValid(false);
        }
        if (tipoPro.equals("")) {
            errores++;
            uiTipoPro.setValid(false);
        }

        errores += visitor.getInvalidFields();

        if (errores != 0) {
            FacesMessage msg = new FacesMessage();
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            msg.setDetail("Existen errores en el formulario");
            fc.addMessage(null, msg);
        }

        fc.renderResponse();
    }

}
