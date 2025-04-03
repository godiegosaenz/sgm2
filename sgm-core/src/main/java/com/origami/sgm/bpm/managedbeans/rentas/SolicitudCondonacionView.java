/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.PrediosRusticosUrbanosModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnSolicitudCondonacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.managedbeans.ClienteTramite;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class SolicitudCondonacionView extends ClienteTramite implements Serializable{
    private static final Logger LOG = Logger.getLogger(SolicitudCondonacionView.class.getName());
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    
    private String cedulaRuc, codigoPredial;
    private List<CatEnte> enteList = null;
    private List<CatPredio> predioList;
    private Boolean mostrarRequisitos = false, userValido = false;
    private CatEnte enteEncontrado, solicitante;
    private CatPredio predio;
    private List<CatCiudadela> ciudadelas;
    private CatCiudadela ciudadela;
    private String mzUrb, solarUrb;
    private CatPredioLazy predioLazy;
    private Boolean codUrban;
    private Long tipoPredio;
    private FnSolicitudCondonacion solCond;
    private List<PrediosRusticosUrbanosModel> predios;
    private PrediosRusticosUrbanosModel predioSel;
    
    @PostConstruct
    public void init() {
        try {
            ciudadelas = permisoServices.getNormasConstruccion().getCatCiudadelas();
            predioLazy = new CatPredioLazy("A");
            solCond = new FnSolicitudCondonacion();
            solCond.setFechaIngreso(new Date());
            solCond.setUsuarioIngreso(uSession.getName_user());
            solCond.setEstado(new FnEstadoExoneracion(2L));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void mostrarPredios(Long tipo) {
        try {
            tipoPredio = tipo;
            if (tipo == 1) {
                this.mostrarDialog("/resources/dialog/predios");
            } else {
                this.mostrarDialog("/resources/dialog/prediosRustico");
            }
        } catch (Exception e) {
            LOG.log(Level.OFF, null, e);
        }
    }
    
    public void seleccionarObject(SelectEvent event) {
        try {
            if(tipoPredio == 1)
                almacenarPrediosEnModel((List<CatPredio>) event.getObject());
            else
                almacenarPrediosEnModelRustico((List<CatPredioRustico>) event.getObject());
            JsfUti.update("frmMain:dataLisPredios");
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
    
    public void almacenarPrediosEnModel(List<CatPredio> prediosList){
        PrediosRusticosUrbanosModel predioSel;
        
        if(predios == null)
            predios = new ArrayList<>();
        
        for(CatPredio temp : prediosList){
            predioSel = new PrediosRusticosUrbanosModel(temp.getCodigoPredialCompleto()+"", temp.getId(), 1L);
            if(!predios.contains(predioSel))
                predios.add(predioSel);
        }
    }
    
    public void almacenarPrediosEnModelRustico(List<CatPredioRustico> prediosList){
        PrediosRusticosUrbanosModel predioSel;
        
        if(predios == null)
            predios = new ArrayList<>();
        
        for(CatPredioRustico temp : prediosList){
            predioSel = new PrediosRusticosUrbanosModel(temp.getNumPredioRustico()+"", temp.getId(), 2L);
            if(!predios.contains(predioSel))
                predios.add(predioSel);
        }
    }
    
    /**
     * Busca un ente a partir del número de cédula o RUC.
     */
    public void buscarEnte() {
        CatPredio cpr;
        List<CatPredioPropietario> tempList;
        enteList = new ArrayList<>();
        predioList = new ArrayList<>();
        mostrarRequisitos = false;
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
            JsfUti.messageInfo(null, "Info", "Se encontró al ente asociado");
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
    
    public void guardarCondonacion(){
        try{
            if(predioSel.getTipoPredio() == 1){
                solCond.setTipoLiquidacion(new RenTipoLiquidacion(13L));
                solCond.setPredio((CatPredio)services.find(CatPredio.class, predioSel.getIdPredio()));
            }else{
                solCond.setTipoLiquidacion(new RenTipoLiquidacion(7L));
                solCond.setPredioRustico((CatPredioRustico)services.find(CatPredioRustico.class, predioSel.getIdPredio()));
            }
            solCond.setNumero(servicesRentas.obtenerNumeroCondonacion()+1);
            solCond.setSolicitante(enteEncontrado);
            
            solCond = (FnSolicitudCondonacion) services.persist(solCond);
            if(solCond!=null){
                JsfUti.messageInfo(null, "Info", "La condonación se guardó correctamente");
                this.continuar();
            }else{
                JsfUti.messageInfo(null, "Info", "Error al guardar la condonación");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
            JsfUti.update("frmMain");
            JsfUti.messageInfo(null, "Info", "Se creó el usuario correctamente.");
        }else{
            JsfUti.messageError(null, "Error", "No se pudo guardar los datos correctamente. Modifique los datos e intente de nuevo.");
        }
    }
    
    public void selectPredio(CatPredio pred) {
        if(predioList == null)
            predioList = new ArrayList();
        if(!predioList.contains(pred))
            predioList.add(pred);
        this.predio = pred;
        this.mostrarRequisitos = true;
        JsfUti.messageInfo(null, "Info", "Predio seleccionado correctamente");
    }
    
    public void seleccionarPredio(PrediosRusticosUrbanosModel pred){
        if (pred != null && enteEncontrado != null ) {
            this.predioSel = pred;
            this.mostrarRequisitos = true;
            codigoPredial = this.predioSel.getCodigoPredio();
            JsfUti.messageInfo(null, "Info", "Predio seleccionado correctamente");
        } else {
            this.predioSel = null;
            this.mostrarRequisitos = false;
        }
    }
    
    public void cancelarGuardado() {
        inicializarVariables();
    }

    public String getCedulaRuc() {
        return cedulaRuc;
    }

    public void setCedulaRuc(String cedulaRuc) {
        this.cedulaRuc = cedulaRuc;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
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

    public Boolean getMostrarRequisitos() {
        return mostrarRequisitos;
    }

    public void setMostrarRequisitos(Boolean mostrarRequisitos) {
        this.mostrarRequisitos = mostrarRequisitos;
    }

    public Boolean getUserValido() {
        return userValido;
    }

    public void setUserValido(Boolean userValido) {
        this.userValido = userValido;
    }

    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }

    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
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

    public CatPredioLazy getPredioLazy() {
        return predioLazy;
    }

    public void setPredioLazy(CatPredioLazy predioLazy) {
        this.predioLazy = predioLazy;
    }

    public Boolean getCodUrban() {
        return codUrban;
    }

    public void setCodUrban(Boolean codUrban) {
        this.codUrban = codUrban;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public String getCodigoPredial() {
        return codigoPredial;
    }

    public void setCodigoPredial(String codigoPredial) {
        this.codigoPredial = codigoPredial;
    }

    public FnSolicitudCondonacion getSolCond() {
        return solCond;
    }

    public void setSolCond(FnSolicitudCondonacion solCond) {
        this.solCond = solCond;
    }

    public List<PrediosRusticosUrbanosModel> getPredios() {
        return predios;
    }

    public void setPredios(List<PrediosRusticosUrbanosModel> predios) {
        this.predios = predios;
    }

    public PrediosRusticosUrbanosModel getPredioSel() {
        return predioSel;
    }

    public void setPredioSel(PrediosRusticosUrbanosModel predioSel) {
        this.predioSel = predioSel;
    }

    public Long getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(Long tipoPredio) {
        this.tipoPredio = tipoPredio;
    }
    
}
