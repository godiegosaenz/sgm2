/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisosadicionales;

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
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeTipoPermisoAdicionales;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisosAdicionalesServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
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
public class RequisitosPA extends ClienteTramite implements Serializable{
 
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @Inject
    private ServletSession servletSession;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    @javax.inject.Inject
    protected Entitymanager services;
    
    @javax.inject.Inject
    protected PermisosAdicionalesServices servicesPA;
    
    @javax.inject.Inject
    private SeqGenMan seq;
    
    private HashMap<String, Object> parametros = new HashMap<>();
    
    private Observaciones obs;
    private GeTipoTramite tp;
    private List<GeRequisitosTramite> requisitos;
    private String codigoPredial;
    private Boolean mostrarRequisitos = false;
    private HistoricoTramites ht;
    private CatEnte enteEncontrado, solicitante;
    private Boolean userValido = false;
    private List<CatEnte> enteList;
    private List<CatPredio> predioList;
    private CatPredio predio;
    private String cedulaRuc;
    private List<PeTipoPermisoAdicionales> permisosList;
    private PeTipoPermisoAdicionales tipoPermiso;
    private PePermisosAdicionales permisoAdicional;
    private String tipoRequisito;
    private Boolean codUrban;
    private CatPredioLazy predioLazy;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Boolean sinPredio = true;
    private String mzUrb, solarUrb;
    
    @PostConstruct
    public void initView() {
        if (uSession != null) {
            if (uSession.getActKey() != null) {
                try {
                    obs = new Observaciones();
                    tp = (GeTipoTramite) services.findAll(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, uSession.getActKey()});
                    requisitos = this.getRequisitos(tp, true, uSession.getActKey());
                    permisoAdicional = new PePermisosAdicionales();
                    //tiposPermisosA = services.findAll(Querys.getListRequisitosTipoTramitesByTipTra, new String[]{"tipo"}, new Object[]{tp.getId()});
                    permisosList = services.findAll(PeTipoPermisoAdicionales.class);
                    solicitante = new CatEnte();
                    predioLazy = new CatPredioLazy("A");
                    ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void verificarEnte(){
        if(enteEncontrado.getEnteCorreoCollection().isEmpty() || enteEncontrado.getEnteTelefonoCollection().isEmpty()){
            JsfUti.messageInfo(null, "Info", "La persona debe tener al menos un teléfono y un correo");
        }else{
            JsfUti.update("frmObs");
            JsfUti.executeJS("PF('obs').show()");
        }
    }
    
    public void setPersonaEdit(){
        if(enteList!=null && enteList.size()>0)
            persona = enteList.get(0);
    }
    
    /**
     * Método que inicia una instancia del proceso en base a los requerimientos del
     * mismo.
     */
    public void iniciarProceso(){
        try {
            if(predio == null){
                JsfUti.messageError(null, "Error", "El trámite necesita un predio para continuar");
                return;
            }
            
            if (this.getFiles().isEmpty()) {
                parametros.put("tdocs", false);
            } else {
                parametros.put("tdocs", true);
            }
            ht = new HistoricoTramites();

            if(tipoPermiso == null || predio == null){
                JsfUti.messageFatal(null, "Error", "No se puede comenzar el proceso. Refresque la página e inicie de nuevo.");
                return;
            }

            if (tp.getDisparador() != null) {
                for (ParametrosDisparador pd : tp.getParametrosDisparadorCollection()) {
                    parametros.put(pd.getVarInterfaz(), pd.getInterfaz());
                    if (pd.getResponsable() != null) {
                        parametros.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                    }
                }
            }
            this.parametros.put("reasignar", 2);
            this.parametros.put("carpeta", "disparadorEdificaciones");
            this.parametros.put("listaArchivos", this.getFiles());
            this.parametros.put("listaArchivosFinal", new ArrayList<Archivo>());
            this.parametros.put("prioridad", 50);
            this.parametros.put("iniciar", false);
            this.parametros.put("descripcion", tp.getDescripcion());
            this.parametros.put("tipoRequisito", this.tipoRequisito);

            Calendar cl = Calendar.getInstance();
            Integer anio = cl.get(Calendar.YEAR);
            //BigInteger tramiteXDepartamento = new BigInteger(seq.getMaxSecuenciaTipoTramite(anio, ht.getTipoTramite().getId()).toString());
            
            ht.setSolicitante(enteEncontrado);
            ht.setEstado("Pendiente");
            ht.setFecha(new Date());
            ht.setId(seq.getSecuenciasTram("SGM"));
            ht.setTipoTramite(tp);
            ht.setTipoTramiteNombre(tipoPermiso.getDescripcion().toUpperCase());
            ht.setMz(mzUrb);
            ht.setSolar(solarUrb);
            ht.setNumPredio(predio.getNumPredio());
            if(ciudadela!=null)
                    ht.setUrbanizacion(ciudadela);
            //ht.setNumTramiteXDepartamento(tramiteXDepartamento);
            ht.setUserCreador(uSession.getUserId());
            ht.setLiquidacionAprobada(Boolean.FALSE);
            ht = (HistoricoTramites) services.persist(ht);
            permisoAdicional.setTipoPermisoAdicional(tipoPermiso);
            permisoAdicional.setPredio(predio);
            permisoAdicional.setFechaEmision(new Date());
            permisoAdicional.setCartaAdosamiento(false);
            permisoAdicional.setNumTramite(ht.getId());
            permisoAdicional = (PePermisosAdicionales) services.persist(permisoAdicional);
            

            this.parametros.put("tramite", ht.getId());

            if (enteEncontrado.getEsPersona() == true) {
                ht.setNombrePropietario(enteEncontrado.getApellidos() + " " + enteEncontrado.getNombres());
            } else {
                ht.setNombrePropietario(enteEncontrado.getRazonSocial());
            }

            ProcessInstance p = this.startProcessByDefinitionKey(tp.getDisparador().getDescripcion(), parametros);
            if(p != null){

                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setUserCre(session.getName_user());
                obs.setTarea("Validacion de requisitos");
            }
            ht = servicesPA.iniciarProceso(ht, obs, p);
            if(ht.getId() != null){
                servletSession.instanciarParametros();
                servletSession.agregarParametro("P_TITULO", "Número de Trámite");
                servletSession.agregarParametro("P_SUBTITULO", "");
                servletSession.agregarParametro("P_NUMERO_TRAMITE", ht.getId().toString());
                servletSession.agregarParametro("NOM_SOLICITANTE", ht.getNombrePropietario());
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
            }else{
                JsfUti.messageFatal(null, "Error", Messages.noCumpleRequisitos);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Método que busca un ente solicitante y lo carga en memoria.
     */
    public void buscarEnte() {
        List<CatPredioPropietario> tempList;
        enteList = new ArrayList<>();
        predioList = new ArrayList<>();
        mostrarRequisitos = false;
        CatPredio cpr;
        boolean add;
        enteEncontrado = servicesPA.obtenerCatEntePorQuery(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
        
        if (enteEncontrado != null) {
            enteList.add(enteEncontrado);
            userValido = true;
            //tempList = services.findAll(Querys.getCatPropietariosByEnte, new String[]{"enteId"}, new Object[]{enteEncontrado.getId()});
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
    
    public void selectPredio(CatPredio pred) {
        if(predioList == null)
            predioList = new ArrayList();
        if(!predioList.contains(pred))
            predioList.add(pred);
    }
    
    /**
     * Verifica que los datos hayan sido encontrados, caso contrario los setea en
     * null.
     * 
     * @param prd 
     */
    public void setearYMostrarRequisitos(CatPredio prd) {
        codigoPredial = "";
        if (prd != null && enteEncontrado != null && tipoPermiso != null) {
            this.predio = prd;
            this.mostrarRequisitos = true;
            codigoPredial = predio.getSector() + "." + predio.getMz() + "." + predio.getCdla() + "." + predio.getMzdiv() + "." + predio.getSolar() + "." + predio.getDiv1() + "." + predio.getDiv2() + "." + predio.getDiv3() + "." + predio.getDiv4() + "." + predio.getDiv5() + "." + predio.getDiv6() + "." + predio.getDiv7() + "." + predio.getDiv8() + "." + predio.getDiv9();
        } else {
            this.predio = null;
            this.mostrarRequisitos = false;
        }

    }
    
    /**
     * Métodos de ingreso del solicitante.
     */
    public void guardarSolicitante() {
        try{
            Boolean flag;
            if (persona.getId() == null) {
                flag = guardarCliente();
            } else {
                flag = editarCliente();
            }
            if (flag) {
                solicitante = persona;
                JsfUti.executeJS("PF('dlgSolInf').hide();");
                JsfUti.update("frmMain");
                JsfUti.messageInfo(null, "Info", "Se editó el usuario correctamente.");
            }else{
                JsfUti.messageError(null, "Error", "No se pudo guardar los datos correctamente. Modifique los datos e intente de nuevo.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cancelarGuardado() {
        inicializarVariables();
    }
    
    /**
     * Inicializa las variables que volverán a ser usadas.
     */
    public void inicializarVariables() {
        emailNew = "";
        tlfnNew = "";
        persona = new CatEnte();
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null && tp!=null && tipoPermiso!=null) {
            obs.setObservacion("INGRESO DE P.A. - "+tipoPermiso.getDescripcion());
            
        }
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public GeTipoTramite getTp() {
        return tp;
    }

    public void setTp(GeTipoTramite tp) {
        this.tp = tp;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public String getCodigoPredial() {
        return codigoPredial;
    }

    public void setCodigoPredial(String codigoPredial) {
        this.codigoPredial = codigoPredial;
    }

    public Boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(Boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public HashMap<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(HashMap<String, Object> parametros) {
        this.parametros = parametros;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }

    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public List<CatEnte> getEnteList() {
        return enteList;
    }

    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }

    public List<CatPredio> getPredioList() {
        return predioList;
    }

    public void setPredioList(List<CatPredio> predioList) {
        this.predioList = predioList;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public String getTipoRequisito() {
        return tipoRequisito;
    }

    public void setTipoRequisito(String tipoRequisito) {
        this.tipoRequisito = tipoRequisito;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public List<PeTipoPermisoAdicionales> getPermisosList() {
        return permisosList;
    }

    public void setPermisosList(List<PeTipoPermisoAdicionales> permisosList) {
        this.permisosList = permisosList;
    }

    public PeTipoPermisoAdicionales getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(PeTipoPermisoAdicionales tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public PePermisosAdicionales getPermisoAdicional() {
        return permisoAdicional;
    }

    public void setPermisoAdicional(PePermisosAdicionales permisoAdicional) {
        this.permisoAdicional = permisoAdicional;
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
