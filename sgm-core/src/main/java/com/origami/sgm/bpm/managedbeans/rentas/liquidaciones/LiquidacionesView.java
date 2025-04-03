/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.lazymodels.RenLiquidacionesLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class LiquidacionesView implements Serializable{
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    protected Entitymanager services;
    
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    
    @Inject
    private ServletSession ss;
    
    private RenLiquidacionesLazy liquidaciones;
    private RenLiquidacion liquidacion;
    private CatEnte comprador, vendedor;
    private List<RenRubrosLiquidacion> rubrosList;
    
    @PostConstruct
    public void initView() {
        
        try {
            if (uSession != null) {
                liquidaciones = new RenLiquidacionesLazy("frmMain:dtLiquidaciones");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirComprobante(RenLiquidacion liquidacion){
    
    }
    /*
    public void asignarRubros(RenLiquidacion liquidacion){        
        
        this.liquidacion = liquidacion;
        rubrosList = (List<RenRubrosLiquidacion>) this.liquidacion.getRenRubrosLiquidacionCollection();
        for(RenRubrosLiquidacion temp: rubrosList)
            if(temp.getEstado())
                transaccionesList.remove(temp.getTransaccion());
        
    }*/
    
    /*
    public void agregarTransaccion(RenTransacciones tr){
        RenRubrosLiquidacion temp = new RenRubrosLiquidacion();
        temp.setLiquidacion(liquidacion);
        temp.setTransaccion(tr);
        temp.setEstado(true);
        rubrosList.add(temp);
        transaccionesList.remove(tr);
    }*/
    
    public void eliminarTransaccion(RenRubrosLiquidacion rrl){
        if(rrl.getId()!=null){            
            rrl.setEstado(false);
            services.persist(rrl);
        }
        rubrosList.remove(rrl);
    }
    
    public void pagar(RenLiquidacion rl){
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.agregarParametro("idLiq", rl.getId());
        JsfUti.redirectFacesNewTab("/rentas/liquidaciones/pagos.xhtml");
    }
    
    public void masInfo(RenLiquidacion rl){
        liquidacion = rl;
        if(liquidacion.getComprador()!=null){
            comprador = (CatEnte) services.find(CatEnte.class, liquidacion.getComprador());
        }
        if(liquidacion.getVendedor()!=null){
            vendedor = (CatEnte) services.find(CatEnte.class, liquidacion.getVendedor());
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public RenLiquidacionesLazy getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(RenLiquidacionesLazy liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public CatEnte getComprador() {
        return comprador;
    }

    public void setComprador(CatEnte comprador) {
        this.comprador = comprador;
    }

    public CatEnte getVendedor() {
        return vendedor;
    }

    public void setVendedor(CatEnte vendedor) {
        this.vendedor = vendedor;
    }
    
}
