/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.runtime.ProcessInstance;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.EntityBeanCopy;
import util.JsfUti;
import util.Messages;
import util.VerCedulaUtils;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class MultasView extends ClienteTramite implements Serializable {
    
    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(MultasView.class.getName());
    
    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;
    @javax.inject.Inject
    private SeqGenMan seq;
    
    @javax.inject.Inject
    private DatoSeguroServices datoSeguroSeguro;
    
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private Integer tipoMulta;
    
    private CatEnte enteEncontrado;
    private List<CatEnte> enteList;
    private List<CatPredio> predioList;
    private CatPredio predio;
    private String cedulaRuc;
    protected CatEnte solicitante;
    private RenLocalComercial localComercial;
    private CmMultas multa;
    private GeTipoTramite tipoTramite;
    private HashMap<String, Object> parametros;
    private BigDecimal valorMulta;
    private Boolean multar;
    private Integer tipoEnte;
    
    private String ciRuc;
    private CatEnte demandado;
    
    @PostConstruct
    public void initView(){
        try{
            if (session.esLogueado()){ //&& session.getTaskID() != null) {
                entradas = new HashMap<>();
                parametros = new HashMap<>();
                entradas.put("obs", new Observaciones());
                valorMulta = BigDecimal.ZERO;
                if(session.getTaskID() != null){
                    this.setTaskId(session.getTaskID());
                    ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                    if (ht != null) {
                        tipoTramite = ht.getTipoTramite();
                        permiso = ht.getPermisoDeFuncionamientoLC();
                        if(permiso != null){
                            entradas.put("localComercialList", new ArrayList<RenLocalComercial>());
                            ((List)entradas.get("localComercialList")).add(permiso.getLocalComercial());
                        }
                    }
                }
                else{
                    ht = new HistoricoTramites();
                    tipoTramite = services.geTipoTramiteByAbr("COM");
                    ht.setTipoTramite(tipoTramite);
                    ht.setTipoTramiteNombre(tipoTramite.getDescripcion());
                }
                tipoMulta = 1;
                multa = new CmMultas();
                multa.setValor(BigDecimal.ZERO);
                if(demandado == null)
                    demandado = new CatEnte();
                tipoEnte = 1;
            } else {
                this.continuar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void buscarEnte() {
        try{
            CatPredio cpr;
            List<CatPredioPropietario> tempList;
            enteList = new ArrayList<>();
            predioList = new ArrayList<>();
            boolean add;
            //enteEncontrado = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
            enteEncontrado = (CatEnte) servicesACL.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
            /*
             try{
             tempList = (List<CatPredioPropietario>) services.findAll(Querys.getPropietariosByCiOrRUC, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
             }catch(Exception e){
             e.printStackTrace();
             }*/

            if (enteEncontrado != null) {
                enteEncontrado = (CatEnte)EntityBeanCopy.clone(enteEncontrado);
                JsfUti.messageInfo(null, "Info", "Ente encontrado");
            } else {
                enteEncontrado = null;
                solicitante = new CatEnte();
                this.inicializarVariables();
                persona.setCiRuc(cedulaRuc);
                JsfUti.messageInfo(null, Messages.enteNoExiste, "");
                JsfUti.update("formSolicitante");
                JsfUti.executeJS("PF('dlgSolInf').show();");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void inicializarVariables(){
        predio = null;
        localComercial = null;
    }
    
    public void mostrarPredios() {
        try {
            if(tipoMulta == 2)
                this.mostrarDialog("/resources/dialog/predios");
            if(tipoMulta == 3)
                this.mostrarDialog("/resources/dialog/localesComerciales");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }
    
    public void seleccionarObject(SelectEvent event) {
        try {
            if(tipoMulta == 2){
                if(((List<CatPredio>) event.getObject()).size()>0)
                    predio = ((List<CatPredio>) event.getObject()).get(0);
            }else
                localComercial = (RenLocalComercial) event.getObject();
            JsfUti.update("frmMain");
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }
    
    public void mostrarDialog(String urlFacelet) {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "80%");
        options.put("position", "center");
        options.put("closable", true);
        options.put("closeOnEscape", true);
        options.put("responsive", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog(urlFacelet, options, null);
    }
    
    public void completarTareaFlujo(){
        try{
            if(multa.getValor().compareTo(BigDecimal.ZERO) > 0 && ((Observaciones)entradas.get("obs")).getObservacion() != null){
                ((Observaciones)entradas.get("obs")).setFecCre(new Date());
                ((Observaciones)entradas.get("obs")).setEstado(true);
                ((Observaciones)entradas.get("obs")).setUserCre(session.getName_user());
                ((Observaciones)entradas.get("obs")).setTarea("Ingreso de multa");
                
                multa.setObservacion(((Observaciones)entradas.get("obs")).getObservacion());
                multa.setDemandado(ht.getSolicitante());
                if(permiso != null)
                    multa.setLocalComercial(permiso.getLocalComercial());
                if(ht.getNumPredio() != null)
                    multa.setPredio((CatPredio)servicesACL.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{ht.getNumPredio()}));
                multa.setFechaIngreso(new Date());
                multa.setUsuarioIngreso(session.getName_user());
                
                if(!multar)
                    multa.setValor(BigDecimal.ZERO);
                
                if(this.getTaskDataByTaskID() != null)
                    ht.setIdProceso(this.getTaskDataByTaskID().getProcessInstanceId());
                                
                services.guardarMultaFlujoInit(((Observaciones)entradas.get("obs")), multa, ht);
                this.parametros.put("prioridad", 50);
                this.parametros.put("descripcion", tipoTramite.getDescripcion());
                this.parametros.put("tramite", ht.getId());
                this.parametros.put("ingresar_multa", multar);
                this.parametros.put("aprobado", true);
                this.parametros.put("tipo_comisaria", 0);
                this.parametros.put("task_def", this.getTaskDataByTaskID().getTaskDefinitionKey());
                
                
                this.completeTask(this.getTaskId(), parametros);
                this.continuar();
            }else{
                JsfUti.messageInfo(null, "Info", "Debe ingresar el valor de la multa y la observación");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void completarTarea(){
        try{
            Integer cont = 0;
            if(ht.getId() != null){
                completarTareaFlujo();
                this.continuar();
                return;
            }
            
            if(multa.getValor().compareTo(BigDecimal.ZERO) > 0 && ((Observaciones)entradas.get("obs")).getObservacion() != null){
                ((Observaciones)entradas.get("obs")).setFecCre(new Date());
                ((Observaciones)entradas.get("obs")).setEstado(true);
                ((Observaciones)entradas.get("obs")).setUserCre(session.getName_user());
                ((Observaciones)entradas.get("obs")).setTarea("Ingreso de multa");
                
                multa.setObservacion(((Observaciones)entradas.get("obs")).getObservacion());
                multa.setDemandado(enteEncontrado);
                multa.setLocalComercial(localComercial);
                multa.setPredio(predio);
                multa.setFechaIngreso(new Date());
                multa.setUsuarioIngreso(session.getName_user());


                ht.setEstado("Pendiente");
                ht.setFecha(new Date());
                ht.setId(seq.getSecuenciasTram("SGM"));
                ht.setUserCreador(session.getUserId());
                if(tipoMulta == 1){
                    if (enteEncontrado.getEsPersona() == true) {
                        ht.setNombrePropietario(enteEncontrado.getApellidos() + " " + enteEncontrado.getNombres());
                    } else {
                        ht.setNombrePropietario(enteEncontrado.getRazonSocial());
                    }
                    ht.setSolicitante(enteEncontrado);
                }
                
                if(tipoMulta == 2){
                    ht.setNombrePropietario(predio.getPropietarios());
                    for(CatPredioPropietario temp : predio.getCatPredioPropietarioCollection()){
                        if(cont == 0)
                            ht.setSolicitante(temp.getEnte());
                        cont++;
                    }
                }
                
                if(tipoMulta == 3){
                    if (localComercial.getPropietario().getEsPersona() == true) {
                        ht.setNombrePropietario(localComercial.getPropietario().getApellidos() + " " + localComercial.getPropietario().getNombres());
                    } else {
                        ht.setNombrePropietario(localComercial.getPropietario().getRazonSocial());
                    }
                    ht.setSolicitante(localComercial.getPropietario());
                }
                ht.setValorLiquidacion(valorMulta);
                ht = services.guardarMultaFlujoInit(((Observaciones)entradas.get("obs")), multa, ht);
                
                for (ParametrosDisparador pd : tipoTramite.getParametrosDisparadorCollection()) {
                    if (pd.getResponsable() != null) 
                        parametros.put(pd.getVarResp(), pd.getResponsable().getUsuario());
                }
                
                this.parametros.put("prioridad", 50);
                this.parametros.put("descripcion", tipoTramite.getDescripcion());
                this.parametros.put("tramite", ht.getId());
                this.parametros.put("tiene_inspeccion", false);
                this.parametros.put("tiene_permiso", false);
                this.parametros.put("aprobado", true);
                this.parametros.put("tipo_comisaria", 0);
                this.parametros.put("comisario", "vsolano");

                ProcessInstance p = this.startProcessByDefinitionKey(tipoTramite.getActivitykey(), parametros);
                ht.setIdProceso(p.getId());
                ht.setIdProcesoTemp(p.getId());
                servicesACL.persist(ht);
                JsfUti.executeJS("PF('dlgIdLiquidacion').show()");
                JsfUti.update("numTra:dlgIdLiquidacion");
                JsfUti.update("numTra:dlgDilLiq");
            }else{
                JsfUti.messageInfo(null, "Info", "Debe ingresar el valor de la multa y la observación");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
          
    public void showDlgEntes(Integer codigo) {
        try{
            VerCedulaUtils validacion = new VerCedulaUtils();
            if(this.demandado.getCiRuc()!=null && this.demandado.getCiRuc().length()==10 && validacion.isCIValida(demandado.getCiRuc())){
                if(this.demandado==null)
                    this.demandado=new CatEnte();
                this.existeCedula();
                if(this.demandado!=null && (this.demandado.getId()!=null||this.demandado.getCiRuc()!=null)){
                    if(this.demandado.getId()==null){
                        demandado.setUserCre(session.getName_user());
                        demandado.setFechaCre(new Date());
                        demandado=seq.guardarOActualizarEnte(demandado);
                    }
                    this.ht.setSolicitante(this.demandado);
                }else{
                    JsfUti.update("formNewClient");
                    JsfUti.executeJS("PF('dlgNewClient').show();");
                }
            }else{
                JsfUti.update("formNewClient");
                JsfUti.executeJS("PF('dlgNewClient').show();");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
        
    public void existeCedula() {
        VerCedulaUtils validacion = new VerCedulaUtils();
        String identificacion = demandado.getCiRuc();
        if (demandado.getCiRuc() != null && demandado.getCiRuc().length() > 0) {
            demandado = (CatEnte) acl.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{identificacion});
            if (demandado == null) {
                demandado = new CatEnte();
                demandado.setCiRuc(identificacion);
                if (this.demandado.getCiRuc()!=null) {
                    if (validacion.isCIValida(identificacion)) {
                        DatoSeguro ds = datoSeguroSeguro.getDatos(identificacion, false, 0);
                        demandado = datoSeguroSeguro.llenarEnte(ds, demandado, false);
                    }
                }
                this.ht.setSolicitante(this.demandado);
            }
        } else {
            demandado = new CatEnte();
        }
    }
    
    
    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }

    public Integer getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(Integer tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }

    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public CmMultas getMulta() {
        return multa;
    }

    public void setMulta(CmMultas multa) {
        this.multa = multa;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public Boolean getMultar() {
        return multar;
    }

    public void setMultar(Boolean multar) {
        this.multar = multar;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
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

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }

    public CatEnte getDemandado() {
        return demandado;
    }

    public void setDemandado(CatEnte demandado) {
        this.demandado = demandado;
    }

    public Integer getTipoEnte() {
        return tipoEnte;
    }

    public void setTipoEnte(Integer tipoEnte) {
        this.tipoEnte = tipoEnte;
    }
    
}
