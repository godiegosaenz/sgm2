/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.denuncias;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.runtime.ProcessInstance;
import util.Archivo;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class RequisitosDN extends ClienteTramite implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;

    @Inject
    private ServletSession servletSession;
    
    @javax.inject.Inject
    private SeqGenMan seq;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    private CatPredioLazy predioLazy;
    private Observaciones obs;
    private String mzUrb, solarUrb, cedulaRuc;
    private GeTipoTramite tp;
    private HistoricoTramites ht;
    private CatEnte solicitante;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private HashMap<String, Object> parametros;
    private CatEnte enteEncontrado;
    private List<CatEnte> enteList;
    private Boolean codUrban;
    private Boolean userValido;
    private List<CatPredio> predioList;
    private Boolean mostrarRequisitos;
    private String codigoPredial;
    private CatPredio predio;
    private List<GeRequisitosTramite> requisitos;
    
    @PostConstruct
    public void initView() {
        if (uSession.esLogueado() != null) {
            uSession.setActKey("comisaria");
            if (uSession.getActKey() != null) {
                try {
                    userValido = false;
                    obs = new Observaciones();
                    tp = (GeTipoTramite) services.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, session.getActKey()});
                    solicitante = new CatEnte();
                    requisitos = this.getRequisitos(tp, true, session.getActKey());
                    ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
                }catch(Exception e){
                    e.printStackTrace();                   
                }
            }
        }
    }
    
    public void setearYMostrarRequisitos(CatPredio prd) {
        codigoPredial = "";
        if (prd != null) {
            this.predio = prd;
            this.mostrarRequisitos = true;
            codigoPredial = predio.getSector() + "." + predio.getMz() + "." + predio.getCdla() + "." + predio.getMzdiv() + "." + predio.getSolar() + "." + predio.getDiv1() + "." + predio.getDiv2() + "." + predio.getDiv3() + "." + predio.getDiv4() + "." + predio.getDiv5() + "." + predio.getDiv6() + "." + predio.getDiv7() + "." + predio.getDiv8() + "." + predio.getDiv9();
        } else {
            this.predio = null;
            this.mostrarRequisitos = false;
        }

    }
    
    public void observacionDefault(){
        obs.setObservacion("Ingreso de "+tp.getDescripcion());
    }
    
    public void iniciarProceso() {
        try {
            parametros = new HashMap<>();
            if (this.getFiles().isEmpty()) {
                parametros.put("tdocs", false);
            } else {
                parametros.put("tdocs", true);
            }
            HistoricoTramites h;
            CatEnte enteTemp;
            ht = new HistoricoTramites();
            //this.parametros.put("tareaRequerimiento", session.getName_user());
            //this.parametros.put("departamento", session.getDepartamento());
            //this.parametros.put("nom_Solicitante", session.getNombrePersonaLogeada());

            if (tp.getDisparador() != null) {
                for (ParametrosDisparador pd : tp.getParametrosDisparadorCollection()) {
                    parametros.put(pd.getVarInterfaz(), pd.getInterfaz());
                    if (pd.getResponsable() != null) {
                        parametros.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                    }
                }
            }
            this.parametros.put("reasignar", 2);
            this.parametros.put("carpeta", tp.getDisparador().getCarpeta());
            this.parametros.put("listaArchivos", this.getFiles());
            this.parametros.put("listaArchivosFinal", new ArrayList<Archivo>());
            this.parametros.put("prioridad", 50);
            this.parametros.put("iniciar", false);
            this.parametros.put("tiene_inspeccion", true);
            this.parametros.put("tipo_comisaria", 1);
            this.parametros.put("comisario", "vsolano");
            this.parametros.put("descripcion", tp.getDescripcion());

            //for (GeTareaUsuario t : tp.getGeTareaUsuarioCollection()) {
            //    parametros.put(t.getVarRef(), t.getUsuario().getUsuario());
            //}
            Calendar cl = Calendar.getInstance();
            Integer anio = cl.get(Calendar.YEAR);
            
            ht.setEstado("Pendiente");
            ht.setFecha(new Date());
            ht.setTipoTramite(tp);
            ht.setId(seq.getSecuenciasTram("SGM"));
            //ht.setNumPredio(predio.getNumPredio());
            ht.setUserCreador(session.getUserId());
            ht.setMz(mzUrb);
            ht.setSolar(solarUrb);
            if(ciudadela!=null)
                ht.setUrbanizacion(ciudadela);
            ht.setTipoTramiteNombre(tp.getDescripcion());
            
            //BigInteger tramiteXDepartamento = new BigInteger(seq.get(anio, ht.getTipoTramite().getId()).toString());
            //ht.setNumTramiteXDepartamento(tramiteXDepartamento);

            if (enteEncontrado.getEsPersona() == true) {
                ht.setNombrePropietario(enteEncontrado.getApellidos() + " " + enteEncontrado.getNombres());
            } else {
                ht.setNombrePropietario(enteEncontrado.getRazonSocial());
            }
            //ht.setNumPredio(predio.getNumPredio());
            //ht.setMz(predio.getUrbMz());
            ht.setSolicitante(enteEncontrado);
            //ht.setSolar(predio.getSolar() + "");
            ht.setLiquidacionAprobada(Boolean.FALSE);
            h = (HistoricoTramites) services.persist(ht);

            if (h != null) {
                parametros.put("tramite", h.getId());

                ProcessInstance p = this.startProcessByDefinitionKey(tp.getDisparador().getDescripcion(), parametros);
                if (p != null) {

                    obs.setEstado(Boolean.TRUE);
                    obs.setFecCre(new Date());
                    obs.setIdTramite(h);
                    obs.setUserCre(session.getName_user());
                    obs.setTarea("Validacion de requisitos");
                    h.setIdProcesoTemp(p.getId());
                    h.setCarpetaRep(h.getId() + "-" + p.getId());

                    services.update(h);
                    if (services.persist(obs) != null) {
                        //
                        enteTemp = enteList.get(0);
                        servletSession.instanciarParametros();
                        servletSession.agregarParametro("P_TITULO", "Número de Trámite");
                        servletSession.agregarParametro("P_SUBTITULO", "");
                        servletSession.agregarParametro("P_NUMERO_TRAMITE", h.getId().toString());
                        servletSession.agregarParametro("NOM_SOLICITANTE", h.getNombrePropietario());
                        if(ciudadela!=null)
                            servletSession.agregarParametro("DIRECCION", this.ciudadela.getNombre()+" MZ: "+this.mzUrb+" SL: "+this.solarUrb);
                        else
                            servletSession.agregarParametro("DIRECCION", " MZ: " + Utils.isEmpty(this.mzUrb) + " SL: " + Utils.isEmpty(this.solarUrb));
                        
                        servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                        servletSession.agregarParametro("DESCRIPCION", tp.getDescripcion());
                        servletSession.setNombreReporte("plantilla1");
                        servletSession.setTieneDatasource(false);
                        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

                        this.continuar();

                    } else {
                        JsfUti.messageFatal(null, "Error", Messages.transacError);
                    }
                }
            } else {
                JsfUti.messageFatal(null, "Error", Messages.iniciarProcesoError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void buscarEnte() {
        CatPredio cpr;
        List<CatPredioPropietario> tempList;
        enteList = new ArrayList<>();
        predioList = new ArrayList<>();
        boolean add;
        //enteEncontrado = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        enteEncontrado = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        /*
         try{
         tempList = (List<CatPredioPropietario>) services.findAll(Querys.getPropietariosByCiOrRUC, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
         }catch(Exception e){
         e.printStackTrace();
         }*/

        if (enteEncontrado != null) {
            enteList.add(enteEncontrado);
            userValido = true;
            tempList = enteEncontrado.getCatPredioPropietarioCollection();
            for (CatPredioPropietario var : tempList) {
                add = true;
                cpr = var.getPredio();
                if (cpr != null && cpr.getNumPredio() != null && cpr.getEstado().equals("A")) {
                    for(CatPredio cp : predioList){
                        if(cp.getCodigoPredial().equals(var.getPredio().getCodigoPredial()))
                            add = false;
                    }
                    if(add && cpr.getCatEscrituraCollection()!=null && !cpr.getCatEscrituraCollection().isEmpty())
                        predioList.add(cpr);
                }
            }
        } else {
            userValido = false;
            enteEncontrado = null;
            solicitante = new CatEnte();
            this.inicializarVariables();
            persona.setCiRuc(cedulaRuc);
            JsfUti.messageInfo(null, Messages.enteNoExiste, "");
            JsfUti.update("formSolicitante");
            JsfUti.executeJS("PF('dlgSolInf').show();");
        }

    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
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

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public GeTipoTramite getTp() {
        return tp;
    }

    public void setTp(GeTipoTramite tp) {
        this.tp = tp;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
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

    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }

    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public List<CatPredio> getPredioList() {
        return predioList;
    }

    public void setPredioList(List<CatPredio> predioList) {
        this.predioList = predioList;
    }    

    public Boolean getCodUrban() {
        return codUrban;
    }

    public void setCodUrban(Boolean codUrban) {
        this.codUrban = codUrban;
    }

    public Boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(Boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public String getCodigoPredial() {
        return codigoPredial;
    }

    public void setCodigoPredial(String codigoPredial) {
        this.codigoPredial = codigoPredial;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }
    
}
