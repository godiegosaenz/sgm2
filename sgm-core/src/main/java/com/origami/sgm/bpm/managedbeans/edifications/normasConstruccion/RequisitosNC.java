/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion.RequisitosPC;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Iniciar Parametros para empezar el proceso de normas de ConstrucciÃ³n
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class RequisitosNC extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private ServletSession ss;

    protected String cedulaRuc;
    protected Boolean mostrarDatos = false;
    protected Boolean mostrarReq = false;
    protected Boolean codUrban = false;
    protected Boolean enteNuevo = false;
    protected HashMap<String, Object> paramt;

    protected HistoricoTramites ht;
    protected GeTipoTramite tramite;
    protected List<GeRequisitosTramite> requisitos;

    protected List<CatPredio> predio;
    protected List<EnteTelefono> eliminarTelefono;
    protected List<EnteCorreo> eliminarCorreo;
    protected CatEnte ente;
    protected Observaciones observaciones;
    protected CatPredio predioSelect;
    protected EnteTelefono telefono = new EnteTelefono();
    protected EnteCorreo correo = new EnteCorreo();
    protected CatPredioLazy predioLazy;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Boolean sinPredio = true;
    private String mzUrb, solarUrb;

    @PostConstruct
    public void initView() {
        if (this.session != null) {
            tramite = new GeTipoTramite();
            requisitos = new ArrayList<>();
            if (this.session.getActKey() != null) {
                tramite = normasServices.getGeRequisitosTramite(true, session.getActKey());
                requisitos = this.getRequisitos(tramite, true, session.getActKey());
            }
            ente = new CatEnte();
            eliminarCorreo = new ArrayList();
            eliminarTelefono = new ArrayList();
            predioLazy = new CatPredioLazy();
            ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
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
            CatEnte temp = normasServices.getCatEnteByCiRuc(cedulaRuc);
            enteNuevo = (temp == null);
            if (temp != null) {
                ente = new CatEnte();
                ente = temp;
                if (ente.getCatPredioPropietarioCollection() != null && !ente.getCatPredioPropietarioCollection().isEmpty()) {
                    for (CatPredioPropietario var : ente.getCatPredioPropietarioCollection()) {
                        if (var.getEstado().startsWith("A")) {
                            List<CatEscritura> escTemp = permisoService.getCatEscrituraByPredioList(var.getPredio().getId());
                            for (CatEscritura escTemp1 : escTemp) {
                                if (escTemp1.getEstado().startsWith("A")) {
                                    predio.add(var.getPredio());
                                }
                            }
                        }
                    }
                }
                mostrarDatos = true;
                JsfUti.update("formReqNC");
            } else {
                Boolean esPersonaTemp = cedulaRuc.length() == 10;
                ente = new CatEnte();
                ente.setEsPersona(esPersonaTemp);
                ente.setCiRuc(cedulaRuc);

                JsfUti.update("formEditEnt");
                JsfUti.executeJS("PF('editEnt').show();");
            }

        } catch (Exception e) {
            Logger.getLogger(RequisitosPC.class.getName()).log(Level.SEVERE, null, e);
        }
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
        try {
            //telefono.setEnte(ente);
            ente.getEnteTelefonoCollection().add(telefono);
            telefono = new EnteTelefono();
        } catch (Exception e) {
            Logger.getLogger(RequisitosNC.class.getName()).log(Level.SEVERE, null, e);
        }
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
        try {
            //correo.setEnte(ente);
            ente.getEnteCorreoCollection().add(correo);
            correo = new EnteCorreo();
        } catch (Exception e) {
            Logger.getLogger(RequisitosNC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("formEditEnt:dtCorr");
    }

    public void eliminarTelefono(int index) {
        EnteTelefono temp = ente.getEnteTelefonoCollection().remove(index);
        if (temp.getId() != null) {
            eliminarTelefono.add(temp);
        }
        JsfUti.update("formEditEnt:dtTel");
    }

    public void eliminarCorreo(int index) {        
        EnteCorreo temp = ente.getEnteCorreoCollection().remove(index);
        if (temp.getId() != null) {
            eliminarCorreo.add(temp);
        }
        JsfUti.update("formEditEnt:dtCorr");
    }

    public void actualizarEnte() {
        if (ente.getCiRuc() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar Número de CÃ©dula o Ruc.", "");
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

        try {
            if (enteNuevo) {
                if (ente.getEnteCorreoCollection().isEmpty()) {
                    JsfUti.messageInfo(null, "Debe Ingresar por lo menos una dirección de correo electronico.", "");
                    return;
                }
//                if (ente.getEnteTelefonoCollection().isEmpty()) {
//                    JsfUti.messageInfo(null, "Debe Ingresar por lo menos un número de teléfono.", "");
//                    return;
//                }
                ente.setUserCre(session.getName_user());
                ente.setFechaCre(new Date());
                ente = permisoService.getFichaServices().guardarCatEnteTelefEmails(ente);
                buscarEnte();
            } else {
                fichaServices.actualizarCatEnteTelefEmails(ente, eliminarCorreo, eliminarTelefono);
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosNC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("formReqNC");
        JsfUti.update("formReqNC:pDatSol");
        JsfUti.executeJS("PF('editEnt').hide()");
    }

    public void selectPredio(CatPredio pred) {
        predio.add(pred);
        mostrarReq = true;
        predioSelect = pred;
        JsfUti.update("formReqNC");
        JsfUti.update("formReqNC:pInfSol");
        JsfUti.executeJS("PF('selPredio').hide()");
    }

    public void onRowSelect(SelectEvent event) {
        try {
            ciudadela = predioSelect.getCiudadela();
            mzUrb = predioSelect.getUrbMz();
            solarUrb = predioSelect.getUrbSolarnew();
        } catch (Exception e) {
        }
        mostrarReq = true;
        JsfUti.update("formReqNC");
    }

    public void validar() {
        if (predioSelect == null) {
            JsfUti.messageError(null, Messages.sinPrediosPC, "");
            return;
        }
        observaciones = new Observaciones();
        JsfUti.executeJS("PF('dlgObsPC').show()");
    }

    public void iniciarProceso() {
        try {
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
//            if (ente.getEnteTelefonoCollection().isEmpty()) {
//                JsfUti.messageInfo(null, "Debe Ingresar por lo menos un número de teléfono.", "");
//                JsfUti.executeJS("PF('dlgObsPC').hide()");
//                return;
//            }
            paramt = new HashMap<>();
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

            ht = new HistoricoTramites();

            ht.setCorreos(getCorreos(ente.getEnteCorreoCollection()));
            if (!ente.getEnteTelefonoCollection().isEmpty()) {
                ht.setTelefonos(ente.getEnteTelefonoCollection().get(0).getTelefono());
            }
            ht.setSolicitante(ente);
            ht.setEstado("Pendiente");
            ht.setFecha(new Date());
            ht.setTipoTramite(tramite);
            ht.setTipoTramiteNombre(tramite.getDescripcion());
            if (ente.getEsPersona() == true) {
                ht.setNombrePropietario(getNombrePropietario(ente));
            } else {
                ht.setNombrePropietario(ente.getRazonSocial());
            }
            ht.setNumPredio(predioSelect.getNumPredio());
            ht.setMz(mzUrb);
            ht.setSolar(solarUrb);
            if(ciudadela!=null)
                ht.setUrbanizacion(ciudadela);
            ht.setUserCreador(session.getUserId());
            ht.setId(permisoService.generarIdTramite());
            his = normasServices.guardarHistoricoTranites(ht);
            if (his != null) {
                paramt.put("tramite", his.getId());
                ProcessInstance pro = this.startProcessByDefinitionKey(tramite.getDisparador().getDescripcion(), paramt);
                if (pro != null) {

                    HistoricoTramiteDet htd = new HistoricoTramiteDet();
                    htd.setFecCre(new Date());
                    htd.setEstado(true);
                    htd.setPredio(predioSelect);
                    htd.setTramite(his);
                    permisoService.guardarHistoricoTramiteDet(htd);
                    observaciones.setEstado(Boolean.TRUE);
                    observaciones.setFecCre(new Date());
                    observaciones.setIdTramite(his);
                    observaciones.setUserCre(session.getName_user());
                    observaciones.setTarea("Validar Requisitos");
                    his.setCarpetaRep(his.getId() + "-" + pro.getId());
                    his.setIdProcesoTemp(pro.getId());
                    permisoService.actualizarHistoricoTramites(his);
                    ss.instanciarParametros();
                    if (permisoService.guardarObservacion(observaciones) != null) {
                        ss.agregarParametro("P_TITULO", "Número de Trámite");
                        ss.agregarParametro("P_SUBTITULO", "N.C. " + tramite.getDescripcion());
                        ss.agregarParametro("P_NUMERO_TRAMITE", his.getId().toString());
                        ss.agregarParametro("NOM_SOLICITANTE", ht.getNombrePropietario());
                        
                        if(ciudadela!=null){
                            ss.agregarParametro("DIRECCION", this.ciudadela.getNombre()+" MZ: "+this.mzUrb+" SL: "+this.solarUrb);
                        }else{
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
                    JsfUti.update("formReqNC");
                }

            } else {
                JsfUti.messageWarning(null, "", Messages.transacError);
                JsfUti.executeJS("PF('dlgObsPC').hide()");
                JsfUti.update("formReqNC");
                return;
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosNC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.executeJS("PF('dlgObsPC').hide()");
        this.continuar();
    }
    
    public void observacionDefault(){
        if (observaciones!=null && observaciones.getObservacion()==null && tramite!=null) {
            observaciones.setObservacion("INGRESO DE "+tramite.getDescripcion());
        }
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public Boolean getMostrarDatos() {
        return mostrarDatos;
    }

    public void setMostrarDatos(Boolean mostrarDatos) {
        this.mostrarDatos = mostrarDatos;
    }

    public Boolean getMostrarReq() {
        return mostrarReq;
    }

    public void setMostrarReq(Boolean mostrarReq) {
        this.mostrarReq = mostrarReq;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public List<CatPredio> getPredio() {
        return predio;
    }

    public void setPredio(List<CatPredio> predio) {
        this.predio = predio;
    }

    public List<EnteTelefono> getEliminarTelefono() {
        return eliminarTelefono;
    }

    public void setEliminarTelefono(List<EnteTelefono> eliminarTelefono) {
        this.eliminarTelefono = eliminarTelefono;
    }

    public List<EnteCorreo> getEliminarCorreo() {
        return eliminarCorreo;
    }

    public void setEliminarCorreo(List<EnteCorreo> eliminarCorreo) {
        this.eliminarCorreo = eliminarCorreo;
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

    public EnteTelefono getTelefono() {
        return telefono;
    }

    public void setTelefono(EnteTelefono telefono) {
        this.telefono = telefono;
    }

    public EnteCorreo getCorreo() {
        return correo;
    }

    public void setCorreo(EnteCorreo correo) {
        this.correo = correo;
    }

    public Observaciones getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Observaciones observaciones) {
        this.observaciones = observaciones;
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

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public RequisitosNC() {
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
