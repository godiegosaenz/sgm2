/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
public class PagosView implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;

    @javax.inject.Inject
    private RentasServices servicesRentas;
    
    @Inject
    private ServletSession ss;
    
    private Integer modoPago=1;
    private List<RenEntidadBancaria> bancosList;
    private RenPago pago;
    private RenLiquidacion liquidacion = null;
    private BigDecimal saldo;
    private CatEnte comprador, vendedor;
    private List<RenPago> pagosList;
    private RenEstadoLiquidacion estadoLiq;
    
    @PostConstruct
    public void initView() {
        
        try {
            if (uSession != null) {
                this.inicializarPagos();
                if(ss.getParametros()!=null)
                    liquidacion = (RenLiquidacion) services.find(RenLiquidacion.class, ss.retornarValor("idLiq"));
                if(liquidacion==null)
                    return;
                bancosList = services.findAll(QuerysFinanciero.getRenEntidadBancariaList, new String[]{}, new Object[]{});
                
                if(liquidacion.getComprador()!=null){
                    comprador = (CatEnte) services.find(CatEnte.class, liquidacion.getComprador());
                }
                if(liquidacion.getVendedor()!=null){
                    vendedor = (CatEnte) services.find(CatEnte.class, liquidacion.getVendedor());
                }
                pagosList = (List<RenPago>) liquidacion.getRenPagoCollection();
                calcularSaldo();
            }
            estadoLiq = (RenEstadoLiquidacion) services.find(RenEstadoLiquidacion.class, new Long(2));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void calcularSaldo(){
        BigDecimal totalPagado = BigDecimal.ZERO;
        for(RenPago pago : pagosList){
            totalPagado = totalPagado.add(pago.getValor());
        }
        saldo = liquidacion.getTotalPago().subtract(totalPagado);
    }
    
    public void inicializarPagos(){
        pago = new RenPago();
        pago.setValor(BigDecimal.ZERO);
        pago.setEstado(true);
        pago.setFechaPago(new Date());
    }
    
    public void pagar(){
        try{
            /*if(!StringUtils.isNumeric(pago.getValor()+"")){
                JsfUti.messageError(null, "Error", "Ha ingresado un valor inválido");
                return;
            }*/
            if(pago.getValor().compareTo(BigDecimal.ZERO)<0 || pago.getValor().compareTo(saldo)>0){
                JsfUti.messageError(null, "Error", "Ha ingresado un valor inválido");
                return;
            }
            
            liquidacion.setSaldo(liquidacion.getSaldo().subtract(pago.getValor()));
            if(liquidacion.getSaldo().equals(BigDecimal.ZERO))
                liquidacion.setEstadoLiquidacion(estadoLiq);
            pago.setLiquidacion(liquidacion);
            pagosList.add(pago);
            pago = (RenPago) services.persist(pago);
            services.update(liquidacion);
            JsfUti.messageInfo(null, "Info", "Se ha guardado el pago correctamente. Valor: "+pago.getValor());
            this.inicializarPagos();
        }catch(Exception e){
            e.printStackTrace();
            JsfUti.messageError(null, "Error", "Ha ocurrido un error al tratar de guardar el pago");
        }
    }
    
    public void redirect(){
        JsfUti.redirectFaces("/");
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public Integer getModoPago() {
        return modoPago;
    }

    public void setModoPago(Integer modoPago) {
        this.modoPago = modoPago;
    }

    public RenPago getPago() {
        return pago;
    }

    public void setPago(RenPago pago) {
        this.pago = pago;
    }

    public List<RenEntidadBancaria> getBancosList() {
        return bancosList;
    }

    public void setBancosList(List<RenEntidadBancaria> bancosList) {
        this.bancosList = bancosList;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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

    public List<RenPago> getPagosList() {
        return pagosList;
    }

    public void setPagosList(List<RenPago> pagosList) {
        this.pagosList = pagosList;
    }
    
}
