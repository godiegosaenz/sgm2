/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
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
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
import util.Faces;
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
public class RequisitosPC extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager service;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private UserSession sess;

    @Inject
    private ServletSession ss;
    private HashMap<String, Object> paramt;
    private String tipoConst;
    private String cedulaRuc;
    private String mzUrb;
    private String solarUrb;

    private Boolean mostraReq = false;
    private Boolean selectTipoConst = false;
    private Boolean mostrarDatos = false;
    private Boolean codUrban = true;
    private Boolean enteNuevo = false;
    private Boolean sinPredio = true;

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
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Long predRef;

    @PostConstruct
    public void initView() {
        if (sess != null) {
            histTramite = new HistoricoTramites();
            paramt = new HashMap<>();
            observaciones = new Observaciones();
            if (sess.getActKey() != null) {
                tramite = new GeTipoTramite();
                tramite = (GeTipoTramite) service.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, sess.getActKey()});
                requisitos = this.getRequisitos(tramite, true, sess.getActKey());
                tipoConstruccion = new ArrayList<>();
                tipoConstruccion = service.findAll(Querys.getListRequisitosTipoTramitesByTipTra, new String[]{"tipo"}, new Object[]{tramite.getId()});
//                ciudadela = new CatCiudadela();
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
                            if (var.getEstado().startsWith("A") && var.getPredio().getEstado().equals("A")) {
                                List<CatEscritura> escTemp = permisoService.getCatEscrituraByPredioList(var.getPredio().getId());
                                for (CatEscritura escTemp1 : escTemp) {
                                    if (escTemp1.getEstado().startsWith("A")) {
                                        predio.add(var.getPredio());
                                    }
                                }
                            }
                        }
                    }
                }
                mostrarDatos = true;
                JsfUti.update("reqPerCons");
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

    public void updateRequisito() {
        selectTipoConst = true;
        JsfUti.update("reqPerCons");
    }

    public void onRowSelect(SelectEvent event) {
        if (sinPredio) {
            mzUrb = predioSelect.getUrbMz();
            solarUrb = predioSelect.getUrbSolarnew();
            if (predioSelect.getCiudadela() != null) {
                ciudadela = predioSelect.getCiudadela();
            } else {
                JsfUti.messageWarning(null, "", "Predio pertenece a una ciudadela.");
            }
        }

        existeTramiteActivo();

        JsfUti.update("reqPerCons:pnlPerConsMain");
    }

    public void validar() {
        if (tipoConst == null) {
            JsfUti.messageWarning(null, "", Messages.documentos);
            JsfUti.update("pnlTipoConst");
            return;
        }
        if (ente.getEnteCorreoCollection().isEmpty()) {
            JsfUti.messageInfo(null, "Debe Ingresar por lo menos una dirección de correo electronico.", "");
            return;
        }
//        if (ente.getEnteTelefonoCollection().isEmpty()) {
//            JsfUti.messageInfo(null, "Debe Ingresar por lo menos un número de teléfono.", "");
//            return;
//        }
        if (!sinPredio) {
            existeTramiteActivo();
        } else {
            if (predioSelect == null) {
                JsfUti.messageError(null, Messages.sinPrediosPC, "");
                return;
            }
        }

        JsfUti.executeJS("PF('dlgObsPC').show()");
    }

    public void iniciarProceso() {
        if (observaciones.getObservacion() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar la Observación para iniciar el Tramite", "");
            return;
        }
        existeTramiteActivo();
        try {
            HistoricoTramites his = null;
            if (histTramite.getUrbanizacion() == null) {
                if (ciudadela != null) {
                    histTramite.setUrbanizacion(ciudadela);
                }
            }
            if (histTramite.getMz() == null) {
                histTramite.setMz(mzUrb);
            }
            if (histTramite.getSolar() == null) {
                histTramite.setSolar(solarUrb);
            }
            if (!sinPredio) {
                if (histTramite.getUrbanizacion() == null) {
                    histTramite.setUrbanizacion(ciudadela);
                }
                if (histTramite.getMz() == null) {
                    histTramite.setMz(mzUrb);
                }
                if (histTramite.getSolar() == null) {
                    histTramite.setSolar(solarUrb);
                }
                paramt.put("reasignar", 3);
            } else {
                histTramite.setNumPredio(predioSelect.getNumPredio());
                //histTramite.setMz(predioSelect.getUrbMz());
                //histTramite.setSolar(predioSelect.getUrbSolarnew() + "");
                if (histTramite.getMz() == null) {
                    histTramite.setMz(predioSelect.getUrbMz());
                }
                if (histTramite.getSolar() == null) {
                    histTramite.setSolar(predioSelect.getUrbSolarnew());
                }
                paramt.put("reasignar", 2);
                if (histTramite.getUrbanizacion() == null) {
                    if (predioSelect.getCiudadela() != null) {
                        histTramite.setUrbanizacion(predioSelect.getCiudadela());
                    }
                }
            }
            if (!ente.getEnteCorreoCollection().isEmpty()) {
                histTramite.setCorreos(getCorreos(ente.getEnteCorreoCollection()));
            }
            if (!ente.getEnteTelefonoCollection().isEmpty()) {
                histTramite.setTelefonos(ente.getEnteTelefonoCollection().get(0).getTelefono());
            }
            histTramite.setSolicitante(ente);
            histTramite.setEstado("Pendiente");
            histTramite.setFecha(new Date());
            histTramite.setTipoTramite(tramite);
            histTramite.setTipoTramiteNombre(tipoConst);
            if (ente.getEsPersona() == true) {
                histTramite.setNombrePropietario(getNombrePropietario(ente));
            } else {
                histTramite.setNombrePropietario(ente.getRazonSocial());
            }

            histTramite.setUserCreador(session.getUserId());
            histTramite.setId(permisoService.generarIdTramite());
            his = permisoService.getNormasConstruccion().guardarHistoricoTranites(histTramite);

            if (his != null) {
                if (tramite.getDisparador() != null) {
                    for (ParametrosDisparador pd : tramite.getParametrosDisparadorCollection()) {
                        paramt.put(pd.getVarInterfaz(), pd.getInterfaz());
                        if (pd.getResponsable() != null) {
                            paramt.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                        }
                    }
                }
                paramt.put("tdocs", !this.getFiles().isEmpty());

                paramt.put("carpeta", tramite.getDisparador().getCarpeta());
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("prioridad", 50);
                paramt.put("iniciar", false);
                paramt.put("descripcion", tipoConst);
                paramt.put("tramite", his.getId());

                if (!sinPredio) {
                    paramt.put("urlTec", "/faces/vistaprocesos/edificaciones/permisoConstruccion/verificarPredio.xhtml");
                    List<Long> rolesC = new ArrayList<>();
                    List<AclUser> usersC;
                    rolesC.add(68L); // Director Catastro 68
                    usersC = acl.getTecnicosByRol(rolesC);
                    if (!usersC.isEmpty()) {
                        for (AclUser users : usersC) {
                            if (users.getSisEnabled() && users.getUserIsDirector()) {
                                paramt.put("directorCatastro", users.getUsuario());
                            }
                        }
                    }
                }
                ProcessInstance pro = this.startProcessByDefinitionKey(tramite.getDisparador().getDescripcion(), paramt);
                if (pro != null) {
                    his.setCarpetaRep(his.getId() + "-" + pro.getId());
                    his.setIdProcesoTemp(pro.getId());
                    service.persist(his);

                    HistoricoTramiteDet htd = new HistoricoTramiteDet();
                    htd.setFecCre(new Date());
                    htd.setEstado(true);
                    htd.setPredio(predioSelect);
                    htd.setTramite(his);
                    permisoService.guardarHistoricoTramiteDet(htd);

                    observaciones.setEstado(Boolean.TRUE);
                    observaciones.setFecCre(new Date());
                    observaciones.setIdTramite(his);
                    observaciones.setUserCre(sess.getName_user());
                    observaciones.setTarea("Validar Requisitos");

                    if (service.persist(observaciones) != null) {
                        ss.instanciarParametros();
                        ss.agregarParametro("P_TITULO", "Número de Trámite");
                        ss.agregarParametro("P_SUBTITULO", "P.C. " + tipoConst);
                        ss.agregarParametro("P_NUMERO_TRAMITE", his.getId().toString());
                        ss.agregarParametro("NOM_SOLICITANTE", histTramite.getNombrePropietario());
//
//                        if (ciudadela != null) {
//                            ss.agregarParametro("DIRECCION", this.ciudadela.getNombre() + " MZ: " + this.mzUrbRep + " SL: " + this.solarUrbRep);
//                        } else {
//                            ss.agregarParametro("DIRECCION", " MZ: " + Utils.isEmpty(this.mzUrbRep) + " SL: " + Utils.isEmpty(this.solarUrbRep));
//                        }
                        ss.agregarParametro("DIRECCION", histTramite == null ? "" : ((histTramite.getUrbanizacion() == null ? "" : histTramite.getUrbanizacion().getNombre()) + (histTramite.getMz() == null ? "" : " MZ:" + histTramite.getMz()) + (histTramite.getSolar() == null ? "" : " SL:" + histTramite.getSolar())));
                        ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                        ss.agregarParametro("DESCRIPCION", tramite.getDescripcion());
                        ss.agregarParametro("FECHA", his.getFecha());
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
                    JsfUti.update("reqPerCons");
                }

            } else {
                JsfUti.messageWarning(null, "", Messages.transacError);
                JsfUti.executeJS("PF('dlgObsPC').hide()");
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosPC.class.getName()).log(Level.SEVERE, null, e);
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
        try {
            //telefono.setEnte(ente);
            ente.getEnteTelefonoCollection().add(telefono);
            telefono = new EnteTelefono();
        } catch (Exception e) {
            Logger.getLogger(RequisitosPC.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(RequisitosPC.class.getName()).log(Level.SEVERE, null, e);
        }
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

                permisoService.getFichaServices().actualizarCatEnteTelefEmails(ente, eliminatCorreo, eliminatTelefono);
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosPC.class.getName()).log(Level.SEVERE, null, e);
        }
        JsfUti.update("reqPerCons");
        JsfUti.update("reqPerCons:pDatSol");
        JsfUti.executeJS("PF('editEnt').hide()");
    }

    public void actualizar() {
        if (!sinPredio) {
            ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
        }
        JsfUti.update("reqPerCons:pInfSol");
    }

    public void selectPredio(CatPredio pred) {
        int x = 0;
        if (predio != null) {
            for (CatPredio p : predio) {
                if (p.getId().equals(pred.getId())) {
                    x++;
                }
            }
            if (x == 0) {
                predio.add(pred);
            } else {
                Faces.messageWarning(null, "Advertencia", "Este predio ya se encuentra asignado a este cliente");
            }
            mostraReq = true;
            predioSelect = pred;
            JsfUti.update("reqPerCons");
            JsfUti.update("reqPerCons:pInfSol");
            JsfUti.executeJS("PF('selPredio').hide()");
        }

    }

    public void buscar() {
        if (ciudadela == null) {
            JsfUti.messageError(null, Messages.campoVacio, "Ciudadela");
            return;
        }
        if (mzUrb == null) {
            JsfUti.messageError(null, Messages.campoVacio, "Manzana");
            return;
        }
        if (solarUrb != null) {
            existeTramiteActivo();
            JsfUti.update("reqPerCons");
        } else {
            JsfUti.messageError(null, Messages.campoVacio, "Solar");
        }
    }

    public void existeTramiteActivo() {
        Integer hist = permisoService.existeHistoricoTrámites(ciudadela.getId(), mzUrb, solarUrb);
        if (hist != null && hist > 0) {
            JsfUti.messageError(null, Messages.predioTramiteActivo, "");
            mostraReq = false;
            return;
        } else {
            mostraReq = true;
        }
    }

    public RequisitosPC() {
    }

    public void observacionDefault() {
        if (observaciones != null && observaciones.getObservacion() == null && tramite != null && tipoConst != null) {
            observaciones.setObservacion("INGRESO DE P.C. - " + tipoConst);

        }
    }

    public void referenciarPredio() {
        if (predRef != null) {
            Faces.messageWarning(null, "Advertencia", "Metodo no esta Implementado...");
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

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession session) {
        this.sess = session;
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

    public String getTipoConst() {
        return tipoConst;
    }

    public void setTipoConst(String tipoConst) {
        this.tipoConst = tipoConst;
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

    public Boolean getSinPredio() {
        return sinPredio;
    }

    public void setSinPredio(Boolean sinPredio) {
        this.sinPredio = sinPredio;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
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

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public Long getPredRef() {
        return predRef;
    }

    public void setPredRef(Long predRef) {
        this.predRef = predRef;
    }

}
