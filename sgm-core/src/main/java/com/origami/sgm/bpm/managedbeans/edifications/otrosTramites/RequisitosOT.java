/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

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
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.sgm.reportes.ReportesView;
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
import org.primefaces.model.UploadedFile;
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
public class RequisitosOT extends ClienteTramite implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession sess;
    @Inject
    private ReportesView reportes;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    private SeqGenMan seq;
    
    @Inject
    private ServletSession servletSession;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    
    private Boolean userValido = false;
    private List<GeRequisitosTramite> requisitos;
    private GeTipoTramite tp;
    private UploadedFile file;
    private HashMap<String, Object> parametros = new HashMap<>();
    private Observaciones obs = null;
    private HistoricoTramites ht = null;
    private String cedulaRuc;
    private List<CatEnte> enteList = null;
    private List<CatPredio> predioList;
    private CatPredio predio;
    private Boolean mostrarRequisitos = false;
    private String codigoPredial;
    private CatEnte enteEncontrado;
    private CatEnte solicitante;
    private OtrosTramites tramite;
    private List<OtrosTramites> tramites;
    private Boolean codUrban;
    private CatPredioLazy predioLazy;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private Boolean sinPredio = true;
    private String mzUrb, solarUrb;
    
    
    @PostConstruct
    public void initView() {
        if (sess.esLogueado() != null) {
            if (sess.getActKey() != null) {
                try {
                    obs = new Observaciones();
                    tp = (GeTipoTramite) services.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, session.getActKey()});
                    tramites = services.findAll(Querys.getOtrosTramites, new String[]{}, new Object[]{});
                    //tp = servicesDP.obtenerGeTipoTramitePorQuery(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{true, session.getActKey()});
                    requisitos = this.getRequisitos(tp, true, sess.getActKey());
                    solicitante = new CatEnte();
                    predioLazy = new CatPredioLazy("A");
                    ciudadelas = permisoService.getNormasConstruccion().getCatCiudadelas();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Validación de acuerdo al tipo de trámite.
     */
    public void validar(){
        if(predio == null && tramite.getNecesitaPredio()){
            JsfUti.messageError(null, "Error", "Debe seleccionar un predio antes de continuar.");
        }else{
            JsfUti.executeJS("PF('obs').show()");
        }
    }
    
    public void setPersonaEdit(){
        if(enteList!=null && enteList.size()>0)
            persona = enteList.get(0);
    }
    
    /**
     * Inicia una instancia del proceso de acuerdo a los requisitos solicitados.
     */
    public void iniciarProceso() {
        try {
            if(tramite == null){
                JsfUti.messageError(null, "Error", "El trámite necesita un tipo para continuar");
                return;
            }
            if(tramite.getNecesitaPredio() && predio == null){
                JsfUti.messageError(null, "Error", "El trámite necesita un predio para continuar");
                return;
            }
            
            if (this.getFiles().isEmpty()) {
                parametros.put("tdocs", false);
            } else {
                parametros.put("tdocs", true);
            }
            HistoricoTramites h;
            CatEnte enteTemp;
            ht = new HistoricoTramites();
            ht.setUserCreador(sess.getUserId());
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
            ht.setTipoTramiteNombre(tramite.getTipoTramite().toUpperCase());
            ht.setMz(mzUrb);
            ht.setSolar(solarUrb);
            if(ciudadela!=null)
                ht.setUrbanizacion(ciudadela);

            if (enteEncontrado.getEsPersona() == true) {
                ht.setNombrePropietario(enteEncontrado.getApellidos() + " " + enteEncontrado.getNombres());
            } else {
                ht.setNombrePropietario(enteEncontrado.getRazonSocial());
            }
            
            ht.setSolicitante(enteEncontrado);
            if(predio!=null){
                ht.setSolar(predio.getUrbSolarnew()+ "");
                ht.setNumPredio(predio.getNumPredio());
                ht.setMz(predio.getUrbMz());
            }
            //BigInteger tramiteXDepartamento = new BigInteger(seq.getMaxSecuenciaTipoTramite(anio, ht.getTipoTramite().getId()).toString());
            //ht.setNumTramiteXDepartamento(tramiteXDepartamento);
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
                        //List<HistoricoTramites> list = new ArrayList();
                        //list.add(ht);
                        
                        //tramite.setHistoricoTramitesCollection(list);
                        services.persist(tramite);
                        h.setSubTipoTramite(tramite);
                        services.update(h);
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
                        servletSession.agregarParametro("DESCRIPCION", tramite.getTipoTramite());
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

    /**
     * Busca un ente a partir del número de cédula o RUC.
     */
    public void buscarEnte() {
        List<CatPredioPropietario> tempList;
        enteList = new ArrayList<>();
        predioList = new ArrayList<>();
        mostrarRequisitos = false;
        CatPredio cpr;
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

    /**
     * Carga en memoria el predio seleccionado.
     * 
     * @param prd 
     */
    public void setearYMostrarRequisitos(CatPredio prd) {
        codigoPredial = "";
        if (prd != null) {
            this.predio = prd;
            this.mostrarRequisitos = true;
            codigoPredial = predio.getSector() + "." + predio.getMz() + "." + predio.getCdla() + "." + predio.getMzdiv() + "." + predio.getSolar() + "." + predio.getDiv1() + "." + predio.getDiv2() + "." + predio.getDiv3() + "." + predio.getDiv4() + "." + predio.getDiv5() + "." + predio.getDiv6() + "." + predio.getDiv7() + "." + predio.getDiv8() + "." + predio.getDiv9();
            JsfUti.messageInfo(null, "Info", "Se seleccionó el predio correctamente.");
            
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
     * Inicializa las variables para volverlas a usar.
     */
    public void inicializarVariables() {
        emailNew = "";
        tlfnNew = "";
        persona = new CatEnte();
    }
    
    public void selectPredio(CatPredio pred) {
        if(predioList == null)
            predioList = new ArrayList();
        if(!predioList.contains(pred))
            predioList.add(pred);
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null && tp!=null && tramite!=null) {
            obs.setObservacion("INGRESO DE O.T. - "+tramite.getTipoTramite());
            
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

    public GeTipoTramite getTp() {
        return tp;
    }

    public void setTp(GeTipoTramite tp) {
        this.tp = tp;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public HashMap<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(HashMap<String, Object> parametros) {
        this.parametros = parametros;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
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

    public String getCodigoPredial() {
        return codigoPredial;
    }

    public void setCodigoPredial(String codigoPredial) {
        this.codigoPredial = codigoPredial;
    }

    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }

    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public List<OtrosTramites> getTramites() {
        return tramites;
    }

    public void setTramites(List<OtrosTramites> tramites) {
        this.tramites = tramites;
    }

    public OtrosTramites getTramite() {
        return tramite;
    }

    public void setTramite(OtrosTramites tramite) {
        /*
        try{
            switch(Integer.valueOf(tramite.getTipoDeTramite().getIdentificacion()+"")){
                case 1:
                    parametros.put("tipoOtrosTramites", tramite.getTipoDeTramite());
                    break;
                case 2:
                    parametros.put("tipoOtrosTramites", tramite.getTipoDeTramite());
                    break;
                case 3:
                    tp = (GeTipoTramite) services.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado","key"}, new Object[]{true, "otrosTramitesTasaCertificado"});
                    break;                
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/
        parametros.put("tipoOtrosTramites", tramite.getTipoDeTramite().getIdentificacion());
        this.tramite = tramite;
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
