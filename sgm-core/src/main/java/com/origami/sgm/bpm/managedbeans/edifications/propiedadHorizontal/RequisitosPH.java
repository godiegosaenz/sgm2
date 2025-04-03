/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeRequisitosTipoTramite;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.runtime.ProcessInstance;
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
public class RequisitosPH extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager service;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private ServletSession ss;

    private HashMap<String, Object> paramt;
    private String cedulaRuc;
    private Boolean mostraReq = false;
    private Boolean selectTipoConst = false;
    private Boolean mostrarDatos = false;
    private Boolean codUrban = true;
    private Boolean enteNuevo = false;

    private GeTipoTramite tramite;
    private HistoricoTramites histTramite;
    private Observaciones observaciones;
    private CatPredio predioSelect = new CatPredio();
    private CatEnte ente = new CatEnte();
    private EnteCorreo correo = new EnteCorreo();
    private EnteTelefono telefono = new EnteTelefono();

    private List<EnteTelefono> eliminatTelefono = new ArrayList<>();
    private List<EnteCorreo> eliminatCorreo = new ArrayList<>();
    private List<CatPredio> predio;
    private List<GeRequisitosTramite> requisitos;
    private List<GeRequisitosTipoTramite> tipoConstruccion;
    protected CatPredioLazy predioLazy = new CatPredioLazy();
    protected MsgFormatoNotificacion msg;

    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Boolean sinPredio = true;
    private String mzUrb, solarUrb;

//    public void doPreRenderView() {
//        if (!JsfUti.isAjaxRequest()) {
//            initView();
//        }
//    }
    @PostConstruct
    public void initView() {
        if (session != null) {
            paramt = new HashMap<>();
            observaciones = new Observaciones();
            if (session.getActKey() != null) {
                tramite = new GeTipoTramite();
                tramite = (GeTipoTramite) service.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, session.getActKey()});
                requisitos = this.getRequisitos(tramite, true, session.getActKey());
                tipoConstruccion = new ArrayList<>();
                tipoConstruccion = service.findAll(Querys.getListRequisitosTipoTramitesByTipTra, new String[]{"tipo"}, new Object[]{tramite.getId()});
                ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
            }
        } else {
            this.continuar();
        }
    }

    public void buscarEnte() {
        try {
            if (cedulaRuc == null) {
                JsfUti.messageInfo(null, "Debe Ingresar Número de Cédula o Ruc.", "");
                return;
            }
            if (!Utils.validateNumberPattern(cedulaRuc)) {
                JsfUti.messageInfo(null, "Debe solo números.", "");
                return;
            }
            if (!Utils.validateCCRuc(cedulaRuc)) {
                JsfUti.messageInfo(null, "Número de Documento es Invalido.", "");
                return;
            }
            predio = new ArrayList<>();
            CatEnte temp = (CatEnte) service.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
            enteNuevo = (temp == null);
            if (temp != null) {
                ente = new CatEnte();
                ente = temp;
                if (ente.getCatPredioPropietarioCollection() != null && !ente.getCatPredioPropietarioCollection().isEmpty()) {
                    for (CatPredioPropietario var : ente.getCatPredioPropietarioCollection()) {
                        if (var.getPredio() != null) {
                            if (var.getEstado().startsWith("A")) {
                                List<CatEscritura> escTemp = permisoService.getCatEscrituraByPredioList(var.getPredio().getId());
                                for (CatEscritura escTemp1 : escTemp) {
                                    if (escTemp1.getEstado().startsWith("A") && var.getPredio().getPhh() <= 0 && var.getPredio().getPhv() <= 0) {
                                        predio.add(var.getPredio());
                                    }
                                }
                            }
                        }
                    }
                }
                mostrarDatos = true;
                JsfUti.update("repPropHoriz");
            } else {
                Boolean esPersonaTemp = cedulaRuc.length() == 10;
                ente = new CatEnte();
                ente.setEsPersona(esPersonaTemp);
                ente.setCiRuc(cedulaRuc);

                JsfUti.update("formEditEnt");
                JsfUti.executeJS("PF('editEnt').show();");
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosPH.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void updateRequisito() {
        selectTipoConst = true;
        JsfUti.update("repPropHoriz");
    }

    public void onRowSelect(SelectEvent event) {
        if (predioSelect.getPhh() > 0 && predioSelect.getPhv() > 0) {
            JsfUti.messageError(null, Messages.prediosNoPH, "");
            return;
        }
        try {
            ciudadela = predioSelect.getCiudadela();
            mzUrb = predioSelect.getUrbMz();
            solarUrb = predioSelect.getUrbSolarnew();
        } catch (Exception e) {
        }
        mostraReq = true;
        JsfUti.update("repPropHoriz");
    }

    public void validar() {
        if (predioSelect == null) {
            JsfUti.messageError(null, Messages.sinPrediosPC, "");
            return;
        }

        JsfUti.executeJS("PF('dlgObsPC').show()");
    }

    public void iniciarProceso() {
        if (observaciones.getObservacion() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar la Observación para iniciar el Tramite", "");
            JsfUti.executeJS("PF('dlgObsPC').hide()");
            return;
        }
        if (ente.getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageInfo(null, "Debe Ingresar por lo menos una dirección de correo electronico.", "");
            JsfUti.executeJS("PF('dlgObsPC').hide()");
            return;
        }
//        if (ente.getEnteTelefonoCollection().isEmpty()) {
//            JsfUti.messageInfo(null, "Debe Ingresar por lo menos un número de teléfono.", "");
//            return;
//        }
        try {
            HistoricoTramites his;
            if (tramite.getDisparador() != null) {
                for (ParametrosDisparador pd : tramite.getParametrosDisparadorCollection()) {
                    paramt.put(pd.getVarInterfaz(), pd.getInterfaz());
                    if (pd.getResponsable() != null) {
                        paramt.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                    }
                }
            }
            paramt.put("tdocs", !this.getFiles().isEmpty());
            paramt.put("reasignar", 2);
            paramt.put("carpeta", tramite.getDisparador().getCarpeta());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<>());
            paramt.put("prioridad", 50);
            paramt.put("iniciar", false);
            paramt.put("descripcion", tramite.getDescripcion());

            histTramite = new HistoricoTramites();
            if (!ente.getEnteCorreoCollection().isEmpty()) {
                String corroes = null;
                for (EnteCorreo c : ente.getEnteCorreoCollection()) {
                    if (corroes == null) {
                        corroes = c.getEmail();
                    } else {
                        corroes = corroes + "," + c.getEmail();
                    }
                }
                histTramite.setCorreos(corroes);
            }
            if (!ente.getEnteTelefonoCollection().isEmpty()) {
                histTramite.setTelefonos(ente.getEnteTelefonoCollection().get(0).getTelefono());
            }
            histTramite.setSolicitante(ente);
            histTramite.setEstado("Pendiente");
            histTramite.setFecha(new Date());
            histTramite.setTipoTramite(tramite);
            histTramite.setTipoTramiteNombre(tramite.getDescripcion());
            if (ente.getEsPersona() == true) {
                histTramite.setNombrePropietario(ente.getApellidos() + " " + ente.getNombres());
            } else {
                histTramite.setNombrePropietario(ente.getRazonSocial());
            }
            histTramite.setNumPredio(predioSelect.getNumPredio());
            histTramite.setMz(mzUrb);
            histTramite.setSolar(solarUrb);
            if (ciudadela != null) {
                histTramite.setUrbanizacion(ciudadela);
            }
            histTramite.setUserCreador(session.getUserId());
            histTramite.setId(permisoService.generarIdTramite());
            his = (HistoricoTramites) service.persist(histTramite);
            if (his != null) {
                Map paramts = new HashMap<>();
                paramts.put("tipo.id", 2L);
                msg = permisoService.getMsgFormatoNotificacionByTipo(paramts);
                paramt.put("tramite", his.getId());
                paramt.put("to", his.getCorreos());
                paramt.put("subject", "Trámite Propiedad Horizontal #. " + his.getId());
                paramt.put("message", msg.getHeader()
                        + "<br/><br/> Su trámite de Propiedad Horizontal se ha generado. <br/><br/>"
                        + msg.getFooter());

                ProcessInstance pro = this.startProcessByDefinitionKey(tramite.getDisparador().getDescripcion(), paramt);
                if (pro != null) {

                    HistoricoTramiteDet htd = new HistoricoTramiteDet();
                    htd.setFecCre(new Date());
                    htd.setEstado(true);
                    htd.setPredio(predioSelect);
                    htd.setTramite(his);
                    htd = permisoService.guardarHistoricoTramiteDet(htd);
                    if (htd != null) {
                        observaciones.setEstado(Boolean.TRUE);
                        observaciones.setFecCre(new Date());
                        observaciones.setIdTramite(his);
                        observaciones.setUserCre(session.getName_user());
                        observaciones.setTarea("Validar Requisitos");
                    }
                    his.setCarpetaRep(his.getId() + "-" + pro.getId());
                    his.setIdProcesoTemp(pro.getId());
                    service.persist(his);

                    if (service.persist(observaciones) != null) {
                        ss.instanciarParametros();
                        ss.agregarParametro("P_TITULO", "Número de Trámite");
                        ss.agregarParametro("P_SUBTITULO", "P.H. " + tramite.getDescripcion());
                        ss.agregarParametro("P_NUMERO_TRAMITE", his.getId().toString());
                        ss.agregarParametro("NOM_SOLICITANTE", histTramite.getNombrePropietario());
                        if (ciudadela != null) {
                            ss.agregarParametro("DIRECCION", this.ciudadela.getNombre() + " MZ: " + this.mzUrb + " SL: " + this.solarUrb);
                        } else {
                            ss.agregarParametro("DIRECCION", " MZ: " + Utils.isEmpty(this.mzUrb) + " SL: " + Utils.isEmpty(this.solarUrb));
                        }
                        ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                        ss.agregarParametro("DESCRIPCION", tramite.getDescripcion());
                        ss.setNombreReporte("plantilla1");
                        ss.setTieneDatasource(false);
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

                    } else {
                        JsfUti.messageError(null, "", Messages.transacError);
                        JsfUti.executeJS("PF('dlgObsPC').hide()");

                    }
                } else {
                    JsfUti.messageError(null, "", Messages.transacError);
                    JsfUti.executeJS("PF('dlgObsPC').hide()");
                    JsfUti.update("repPropHoriz");
                }

            } else {
                JsfUti.messageWarning(null, "", Messages.transacError);
                JsfUti.executeJS("PF('dlgObsPC').hide()");
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosPH.class.getName()).log(Level.SEVERE, null, e);
        }
        this.continuar();
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
        //telefono.setEnte(ente);
        ente.getEnteTelefonoCollection().add(telefono);
        telefono = new EnteTelefono();
        JsfUti.update("formEditEnt:dtTel");
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
        //correo.setEnte(ente);
        ente.getEnteCorreoCollection().add(correo);
        correo = new EnteCorreo();
        JsfUti.update("formEditEnt:dtCorr");
    }

    public void eliminarTelefono(int index) {
        EnteTelefono temp = ente.getEnteTelefonoCollection().remove(index);
        if (temp.getId() != null) {
            eliminatTelefono.add(temp);
        }
        JsfUti.update("formEditEnt:dtTel");
    }

    public void eliminarCorreo(int index) {
        EnteCorreo temp = ente.getEnteCorreoCollection().remove(index);
        if (temp.getId() != null) {
            eliminatCorreo.add(temp);
        }
        JsfUti.update("formEditEnt:dtCorr");
    }

    public void actualizarEnte() {
        if (ente.getCiRuc() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Número de Cédula o Ruc.", "");
            return;
        }
        if (!Utils.validateNumberPattern(ente.getCiRuc())) {
            JsfUti.messageInfo(null, "Debe solo números.", "");
            return;
        }
        if (!Utils.validateCCRuc(ente.getCiRuc())) {
            JsfUti.messageInfo(null, "Número de Documento es Invalido.", "");
            return;
        }
        if (enteNuevo) {
            if (ente.getEnteCorreoCollection().isEmpty()) {
                JsfUti.messageInfo(null, "Debe Ingresar por lo menos una dirección de correo electronico.", "");
                return;
            }
//            if (ente.getEnteTelefonoCollection().isEmpty()) {
//                JsfUti.messageInfo(null, "Debe Ingresar por lo menos un número de teléfono.", "");
//                return;
//            }
            ente.setUserCre(session.getName_user());
            ente.setFechaCre(new Date());
            ente = permisoService.getFichaServices().guardarCatEnteTelefEmails(ente);
            buscarEnte();
        } else {
            permisoService.getFichaServices().actualizarCatEnteTelefEmails(ente, eliminatCorreo, eliminatTelefono);
        }
        JsfUti.update("repPropHoriz");
        JsfUti.update("repPropHoriz:pDatSol");
        JsfUti.executeJS("PF('editEnt').hide()");
    }

    public void selectPredio(CatPredio pred) {
        predio.add(pred);
        mostraReq = true;
        predioSelect = pred;
        JsfUti.update("repPropHoriz");
        JsfUti.update("repPropHoriz:pInfSol");
        JsfUti.executeJS("PF('selPredio').hide()");
    }

    public RequisitosPH() {
    }

    public void observacionDefault() {
        if (observaciones != null && observaciones.getObservacion() == null && tramite != null) {
            observaciones.setObservacion("INGRESO DE " + tramite.getDescripcion());

        }
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public Boolean getMostraReq() {
        return mostraReq;
    }

    public void setMostraReq(Boolean mostraReq) {
        this.mostraReq = mostraReq;
    }

    public Boolean getSelectTipoConst() {
        return selectTipoConst;
    }

    public void setSelectTipoConst(Boolean selectTipoConst) {
        this.selectTipoConst = selectTipoConst;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public HistoricoTramites getHistTramite() {
        return histTramite;
    }

    public void setHistTramite(HistoricoTramites histTramite) {
        this.histTramite = histTramite;
    }

    public Observaciones getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Observaciones observaciones) {
        this.observaciones = observaciones;
    }

    public HashMap getParamt() {
        return paramt;
    }

    public void setParamt(HashMap paramt) {
        this.paramt = paramt;
    }

    public List<GeRequisitosTipoTramite> getTipoConstruccion() {
        return tipoConstruccion;
    }

    public void setTipoConstruccion(List<GeRequisitosTipoTramite> tipoConstruccion) {
        this.tipoConstruccion = tipoConstruccion;
    }

    public List<CatPredio> getPredio() {
        return predio;
    }

    public void setPredio(List<CatPredio> predio) {
        this.predio = predio;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatPredio getPredioSelect() {
        return predioSelect;
    }

    public void setPredioSelect(CatPredio predioSelect) {
        this.predioSelect = predioSelect;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Boolean getMostrarDatos() {
        return mostrarDatos;
    }

    public void setMostrarDatos(Boolean mostrarDatos) {
        this.mostrarDatos = mostrarDatos;
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

    public List<EnteTelefono> getEliminatTelefono() {
        return eliminatTelefono;
    }

    public void setEliminatTelefono(List<EnteTelefono> eliminatTelefono) {
        this.eliminatTelefono = eliminatTelefono;
    }

    public List<EnteCorreo> getEliminatCorreo() {
        return eliminatCorreo;
    }

    public void setEliminatCorreo(List<EnteCorreo> eliminatCorreo) {
        this.eliminatCorreo = eliminatCorreo;
    }

    public Boolean getCodUrban() {
        return codUrban;
    }

    public void setCodUrban(Boolean codUrban) {
        this.codUrban = codUrban;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public Boolean getSinPredio() {
        return sinPredio;
    }

    public void setSinPredio(Boolean sinPredio) {
        this.sinPredio = sinPredio;
    }

    public String getMzUrb() {
        return mzUrb;
    }

    public void setMzUrb(String mzUrb) {
        this.mzUrb = mzUrb;
    }

    public String getSolarUrb() {
        return solarUrb;
    }

    public void setSolarUrb(String solarUrb) {
        this.solarUrb = solarUrb;
    }

}
