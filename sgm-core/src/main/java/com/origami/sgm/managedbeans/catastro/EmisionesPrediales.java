/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.Mensajes;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.CatPredioRusticoLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Henry Pilco
 */
@Named
@ViewScoped
public class EmisionesPrediales implements Serializable{
    public static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession session;
    private Map<String, Object> parametros;
    protected AclUser usuario;
    protected Long anioEmision;
    protected Boolean habilitaProceso= Boolean.FALSE;
    protected Boolean incluirMejoras= Boolean.TRUE;
    
    protected RenLiquidacion emision= new RenLiquidacion();
    protected CatPredioLazy prediosUrbanos = new CatPredioLazy();
    protected CatPredio predio;
    protected CatPredioRusticoLazy prediosRurales= new CatPredioRusticoLazy();
    protected CatPredioRustico predioRural;
    
    protected List<Mensajes> mensajes;
    protected Long totalEmisiones;
    
    @PostConstruct 
    public void initView() {        
        try{
            if(session!=null){
                parametros = new HashMap<>();
                parametros.put("usuario", session.getName_user());
                usuario = (AclUser)manager.findObjectByParameter(AclUser.class, parametros);
                anioEmision=new Long(Calendar.getInstance().get(Calendar.YEAR));
            }
        } catch (Exception e) {
            Logger.getLogger(EmisionesPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void inicializarVariablesExoAuto(){
        this.inicializarVariables();
        anioEmision = Utils.getAnio(new Date()).longValue();
    }
    /*
    public void generarExoneraciones(){
        if(this.anioEmision == null){
            JsfUti.messageInfo(null, "Info", "Debe ingresar el año a generar las solicitudes");
            return;
        }
        FnSolicitudExoneracionAutomatica exoAuto;
        List buscarExoneraciones = manager.findAll(QuerysFinanciero.getExoneracionesAutoByAnio, new String[]{"anio"}, new Object[]{this.anioEmision});
        
        try{
            if(buscarExoneraciones == null || buscarExoneraciones.isEmpty()){
                List exoneracionesDisponibles = manager.findAll(QuerysFinanciero.getExoneracionesDisponibles, new String[]{"anio"}, new Object[]{this.anioEmision});
                for(Object temp : exoneracionesDisponibles){
                    exoAuto = new FnSolicitudExoneracionAutomatica();
                    exoAuto.setFechaIngreso(new Date());
                    exoAuto.setSolicitudExoneracion((FnSolicitudExoneracion)temp);
                    exoAuto.setAnio(this.anioEmision.intValue());
                    exoAuto.setUsuarioIngreso(session.getName_user());
                    exoAuto.setEstado(new FnEstadoExoneracion(2L));
                    manager.persist(exoAuto);
                }
            }else{
                JsfUti.messageInfo(null, "Info", "Ya se generaron las exoneraciones del año ingresado");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    */
    public void inicializarVariables(){
        habilitaProceso= Boolean.FALSE;
        parametros = new HashMap<>();
        mensajes = new ArrayList<>();
        totalEmisiones=0L;
    }
    
    public void generarInformacion(Long l){
        try{
            habilitaProceso= Boolean.FALSE;
            Long cantidadEmisiones;
            Long cantidadObras;
            Long cantidadCalculosAnuales;
            Long cantidadAvaluosAnuales;
            parametros = new HashMap<>();
            mensajes = new ArrayList<>();
            switch(l.intValue()){
                case 1://URBANO
                    parametros.put("tipoLiquidacion", 13L);
                    parametros.put("anio", anioEmision);
                    cantidadEmisiones=(Long) manager.findObjectByParameter(Querys.emisionesByTipoAnio, parametros);
                    mensajes.add(new Mensajes("AÑO DE EMISION", cantidadEmisiones==0?"POR GENERAR":"GENERADO", cantidadEmisiones==0));                    
                    mensajes.add(new Mensajes("PROCESO DE MEJORAS", incluirMejoras?"GENERAR RUBROS DE MEJORAS":"EMISIONES SIN MEJORAS", Boolean.TRUE));
                    parametros = new HashMap<>();
                    parametros.put("anio", anioEmision);
                    cantidadObras=(Long) manager.findObjectByParameter(Querys.cantidadObrasByAnio, parametros);
                    cantidadCalculosAnuales=(Long) manager.findObjectByParameter(Querys.cantidadSumasAnuales, parametros);
                    //cantidadAvaluosAnuales=(Long) manager.findObjectByParameter(Querys.rangosAnualesUrb, parametros);
                    cantidadAvaluosAnuales=((BigInteger) manager.getNativeQuery(Querys.rangosAnualesUrb, new Object[]{anioEmision})).longValue();
                    mensajes.add(new Mensajes("RANGO AVALUOS", cantidadAvaluosAnuales>0?"RANGOS DE AVALUOS REGISTRADOS":"RANGOS DE AVALUOS NO REGISTRADOS", cantidadAvaluosAnuales>0));
                    if(incluirMejoras){
                        mensajes.add(new Mensajes("OBRAS", cantidadObras>0?"OBRAS REGISTRADAS":"NO SE HAN REGISTRADOS OBRAS", cantidadObras>0));
                        mensajes.add(new Mensajes("CALCULOS ANUALES", cantidadCalculosAnuales>0?"CALCULOS REGISTRADOS":"NO SE HA REALIZADO EL CALCULO ANUAL", cantidadCalculosAnuales>0));
                    }
                    habilitaProceso=cantidadEmisiones==0 && (!incluirMejoras || (cantidadObras>0&&cantidadCalculosAnuales>0)) && cantidadAvaluosAnuales>0;
                    break;
                case 2://RURAL
                    parametros.put("tipoLiquidacion", 7L);
                    parametros.put("anio", anioEmision);
                    cantidadEmisiones=(Long) manager.findObjectByParameter(Querys.emisionesByTipoAnio, parametros);
                    mensajes.add(new Mensajes("AÑO DE EMISION", cantidadEmisiones==0?"POR GENERAR":"GENERADO", cantidadEmisiones==0));
                    parametros = new HashMap<>();
                    parametros.put("anio", anioEmision);
                    //cantidadAvaluosAnuales=(Long) manager.findObjectByParameter(Querys.rangosAnualesRur, parametros);
                    cantidadAvaluosAnuales=((BigInteger) manager.getNativeQuery(Querys.rangosAnualesRur, new Object[]{anioEmision})).longValue();
                    mensajes.add(new Mensajes("RANGO AVALUOS", cantidadAvaluosAnuales>0?"RANGOS DE AVALUOS REGISTRADOS":"RANGOS DE AVALUOS NO REGISTRADOS", cantidadAvaluosAnuales>0));
                    habilitaProceso=cantidadEmisiones==0 && cantidadAvaluosAnuales>0;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(EmisionesPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void generarEmisiones(Long l){
        try{
            generarInformacion(l);
            parametros = new HashMap<>();
            parametros.put("anio", anioEmision);
            switch(l.intValue()){
                case 1://URBANO
                    if (habilitaProceso){
                        parametros.put("mejora", incluirMejoras?1:0);
                        recaudacion.grabarEmisionGlobal(parametros);
                    }
                    parametros.put("tipoLiquidacion", 13L);
                    break;
                case 2://RURAL
                    if(habilitaProceso)
                        recaudacion.grabarEmisionRuralGlobal(parametros);
                    parametros.put("tipoLiquidacion", 7L);
                    break;
                default:
                    break;
            }
            totalEmisiones=(Long) manager.findObjectByParameter(Querys.emisionesByTipoAnio, parametros);
        } catch (Exception e) {
            JsfUti.messageInfo(null, "Info", "Error al generar Proceso");
            Logger.getLogger(EmisionesPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void cargarPredio(CatPredio predio){
        this.predio=predio;
    }
    
    public void generarEmision() {
        try{
            if(this.predio!=null && this.anioEmision !=null && this.usuario!=null){
                parametros = new HashMap<>();
                parametros.put("idPredio", predio.getId());
                parametros.put("anio", this.anioEmision);
                this.emision= (RenLiquidacion) manager.findObjectByParameter(Querys.emisionExistente, parametros);
                if(this.emision==null){
                    this.emision=recaudacion.grabarEmisionPredial(this.predio, this.anioEmision, this.usuario);
                    if (this.emision!=null) {
                        if(this.emision.getId()!=null){
                            parametros = new HashMap<>();
                            parametros.put("idPredio", predio.getId());
                            parametros.put("anio", this.anioEmision);
                            this.emision.setTotalPago(this.emision.getTotalPago().add((BigDecimal)recaudacion.grabarMejora(parametros)));
                            this.emision.setSaldo(this.getEmision().getTotalPago());
                            recaudacion.editarLiquidacion(this.emision);
                            JsfUti.messageInfo(null, "Emisión Generada.", "");
                        }else
                            JsfUti.messageInfo(null, "Emisión sin Costo.", "El valor de la Emision fue 0.00 , revise los parametros");
                    }else
                        JsfUti.messageError(null, "Error", "No se pudo realizar la emisión del predio.");
                }
                else{
                    JsfUti.update("infEmision");
                    JsfUti.executeJS("PF('infEmisionDlg').show();");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(EmisionesPrediales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public Long getAnioEmision() {
        return anioEmision;
    }

    public void setAnioEmision(Long anioEmision) {
        this.anioEmision = anioEmision;
    }

    public Boolean getHabilitaProceso() {
        return habilitaProceso;
    }

    public void setHabilitaProceso(Boolean habilitaProceso) {
        this.habilitaProceso = habilitaProceso;
    }

    public Boolean getIncluirMejoras() {
        return incluirMejoras;
    }

    public void setIncluirMejoras(Boolean incluirMejoras) {
        this.incluirMejoras = incluirMejoras;
    }

    public RenLiquidacion getEmision() {
        return emision;
    }

    public void setEmision(RenLiquidacion emision) {
        this.emision = emision;
    }

    public CatPredioLazy getPrediosUrbanos() {
        return prediosUrbanos;
    }

    public void setPrediosUrbanos(CatPredioLazy prediosUrbanos) {
        this.prediosUrbanos = prediosUrbanos;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredioRusticoLazy getPrediosRurales() {
        return prediosRurales;
    }

    public void setPrediosRurales(CatPredioRusticoLazy prediosRurales) {
        this.prediosRurales = prediosRurales;
    }

    public CatPredioRustico getPredioRural() {
        return predioRural;
    }

    public void setPredioRural(CatPredioRustico predioRural) {
        this.predioRural = predioRural;
    }

    public List<Mensajes> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensajes> mensajes) {
        this.mensajes = mensajes;
    }

    public Long getTotalEmisiones() {
        return totalEmisiones;
    }

    public void setTotalEmisiones(Long totalEmisiones) {
        this.totalEmisiones = totalEmisiones;
    }
    
}
