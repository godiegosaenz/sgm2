/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.reselladoPlanos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.DisparadorTramites;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.SeqGenMan;
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
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class RequisitosRP extends ClienteTramite implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    protected Entitymanager serv;
    @Inject
    private ServletSession servletSession;
    @javax.inject.Inject
    private SeqGenMan seq;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    private Boolean userValido = false;
    private GeTipoTramite tramite;
    private CatEnte solicitante;
    private List<GeRequisitosTramite> requisitos;
    private HashMap<String, Object> params;
    private HistoricoTramites ht = null;
    private Observaciones obs = null;
    private DisparadorTramites dis = null;
    private boolean iniciar = true;
    private String cedulaRuc;
    private List<CatPredio> predios;
    private CatPredio predio;
    private Boolean mostrarRequisitos = false;
    private static final long serialVersionUID = 1L;
    
    private Boolean codUrban;
    private CatPredioLazy predioLazy;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Boolean sinPredio = true;
    private String mzUrb, solarUrb;

    @PostConstruct
    public void initView() {
        try {
            if (sess != null) {
                obs = new Observaciones();
                params = new HashMap<>();
                if (sess.getActKey() != null) {
                    tramite = (GeTipoTramite) serv.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, sess.getActKey()});
                    requisitos = this.getRequisitos(tramite, true, sess.getActKey());
                    predioLazy = new CatPredioLazy();
                    ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void buscarEnte() {
        try {
            predios = new ArrayList<>();
            mostrarRequisitos = false;
            solicitante = (CatEnte) serv.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
            if (solicitante != null) {
                userValido = true;
                for (CatPredioPropietario var : solicitante.getCatPredioPropietarioCollection()) {
                    if (var.getPredio() != null && var.getPredio().getEstado().equals("A")) {
                        predios.add(var.getPredio());
                    }
                }
            } else {
                userValido = false;
                persona = new CatEnte();
                persona.setCiRuc(cedulaRuc);
                JsfUti.messageInfo(null, Messages.enteNoExiste, "");
                JsfUti.update("formSolicitante");
                JsfUti.executeJS("PF('dlgSolInf').show();");
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void mostrarReq() {
        mostrarRequisitos = predio != null;
    }
    
    /**
     * Métodos de ingreso del solicitante.
     */
    public void guardarSolicitante() {
        Boolean flag;
        if (solicitante.getId() == null) {
            flag = guardarCliente();
        } else {
            flag = editarCliente();
        }
        if (flag) {
            solicitante = persona;
            JsfUti.executeJS("PF('dlgSolInf').hide();");
            JsfUti.messageInfo(null, "Info", "Se creó el usuario correctamente.");
        }else{
            JsfUti.messageError(null, "Error", "No se pudo guardar los datos correctamente. Modifique los datos e intente de nuevo.");
        }
    }
    
    public void setearPersona(){
        if(solicitante!=null)
            persona = solicitante;
    }

    public void iniciarProceso() {
        try {
            if (predio != null) {
                //if (this.getFiles().isEmpty() != true) {
                if (tramite.getDisparador() != null) {
                    for (ParametrosDisparador pd : tramite.getParametrosDisparadorCollection()) {
                        params.put(pd.getVarInterfaz(), pd.getInterfaz());
                        if (pd.getResponsable() != null) {
                            params.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                        }
                    }
                }
                HistoricoTramites h;
                ht = new HistoricoTramites();
                if (this.getFiles().isEmpty()) {
                    params.put("tdocs", false);
                } else {
                    params.put("tdocs", true);
                }
                params.put("reasignar", 2);
                params.put("carpeta", tramite.getDisparador().getCarpeta());
                params.put("listaArchivos", this.getFiles());
                params.put("listaArchivosFinal", new ArrayList<>());
                params.put("prioridad", 50);
                params.put("iniciar", false);
                params.put("descripcion", tramite.getDescripcion());
                ht.setSolicitante(solicitante);
                ht.setEstado("Pendiente");
                ht.setFecha(new Date());
                ht.setTipoTramite(tramite);
                ht.setTipoTramiteNombre(tramite.getDescripcion());
                if (solicitante.getEsPersona() == true) {
                    ht.setNombrePropietario(solicitante.getApellidos() + " " + solicitante.getNombres());
                } else {
                    ht.setNombrePropietario(solicitante.getRazonSocial());
                }
                ht.setId(seq.getSecuenciasTram("SGM"));
                ht.setNumPredio(predio.getNumPredio());
                ht.setMz(mzUrb);
                ht.setSolar(solarUrb);
                ht.setLiquidacionAprobada(Boolean.FALSE);
                if(ciudadela!=null)
                    ht.setUrbanizacion(ciudadela);
                h = (HistoricoTramites) serv.persist(ht);
                if (h != null) {
                    params.put("tramite", h.getId());
                    ProcessInstance p = this.startProcessByDefinitionKey(tramite.getDisparador().getDescripcion(), params);
                    if (p != null) {
                        obs.setEstado(Boolean.TRUE);
                        obs.setFecCre(new Date());
                        obs.setIdTramite(h);
                        obs.setUserCre(sess.getName());
                        obs.setTarea("Validacion de requisitos");
                        h.setCarpetaRep(h.getId() + "-" + p.getId());
                        //h.setIdProceso(p.getId());
                        h.setIdProcesoTemp(p.getId());
                        serv.persist(h);
                        if (serv.persist(obs) != null) {
                            servletSession.instanciarParametros();
                            servletSession.agregarParametro("P_TITULO", tramite.getDescripcion());
                            servletSession.agregarParametro("P_SUBTITULO", "");
                            servletSession.agregarParametro("P_NUMERO_TRAMITE", h.getId().toString());
                            servletSession.agregarParametro("NOM_SOLICITANTE", h.getNombrePropietario());
                            if(ciudadela!=null)
                                servletSession.agregarParametro("DIRECCION", this.ciudadela.getNombre()+" MZ: "+this.mzUrb+" SL: "+this.solarUrb);
                            else
                                servletSession.agregarParametro("DIRECCION", " MZ: " + Utils.isEmpty(this.mzUrb) + " SL: " + Utils.isEmpty(this.solarUrb));
                            servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                            servletSession.agregarParametro("DESCRIPCION", tramite.getDescripcion());
                            servletSession.setNombreReporte("plantilla1");
                            servletSession.setTieneDatasource(false);
                            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                            this.continuar();
                        }
                    } else {
                        JsfUti.messageFatal(null, "Error", Messages.transacError);
                    }
                } else {
                    JsfUti.messageFatal(null, "Error", Messages.transacError);
                }
//                } else {
//                    JsfUti.messageWarning(null, "Advertencia", Messages.documentos);
//                }
            } else {
                JsfUti.messageWarning(null, "Advertencia", Messages.sinPrediosSuf);
            }
        } catch (Exception e) {
            Logger.getLogger(RequisitosRP.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void selectPredio(CatPredio pred) {
        if(predios == null)
            predios = new ArrayList();
        if(!predios.contains(pred))
            predios.add(pred);
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null && tramite!=null) {
            obs.setObservacion("INGRESO DE "+tramite.getDescripcion());
            
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
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

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(Boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
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
