/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenPagoDetalle;
import com.origami.sgm.entities.models.PagoCheque;
import com.origami.sgm.entities.models.PagoNotaCredito;
import com.origami.sgm.entities.models.PagoTarjetaCredito;
import com.origami.sgm.entities.models.PagoTransferencia;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.JsfUti;

/**
 *
 * @author origami
 */
public class PagoTituloReporteModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal valorRecibido;
    private BigDecimal valorCobrar;
    private BigDecimal valorCambio;
    private BigDecimal valorRecibidoCompensacion;
    private BigDecimal valorCambioCompensacion;
    private BigDecimal valorCobrarCompensacion;

    private PagoCheque pagoCheque;
    private PagoNotaCredito pagoNotaCredio;
    private PagoTarjetaCredito pagoTarjetaCredito;
    private PagoTransferencia pagoTransferencia;

    protected List<PagoCheque> listPagoCheque;
    protected List<PagoNotaCredito> listPagoNotaCredio;
    protected List<PagoTarjetaCredito> listPagoTarjetaCredito;
    protected List<PagoTransferencia> listPagoTransferencia;

    private BigDecimal valorTotalEfectivo;
    private BigDecimal valorTotalNotasCredito;
    private BigDecimal valorTotalCheques;
    private BigDecimal valorTotalTarjetas;
    private BigDecimal valorTotalTransferencias;
    private BigDecimal valorTotalCompesacion;
    private BigDecimal valorTotal;
    //private BigDecimal valorCoactiva;

    private String observacion;
    private BigDecimal valorSaldoPago;
    private BigDecimal valorSaldoPagoFinal;

    public PagoTituloReporteModel() {
        //this.valorRecibido = new BigDecimal("0.00");
        this.valorCobrar = new BigDecimal("0.00");
        this.valorCambio = new BigDecimal("0.00");
        this.listPagoCheque = new ArrayList<>();
        this.listPagoNotaCredio = new ArrayList<>();
        this.listPagoTarjetaCredito = new ArrayList<>();
        this.listPagoTransferencia = new ArrayList<>();
        this.valorTotalEfectivo = new BigDecimal("0.00");
        this.valorTotalNotasCredito = new BigDecimal("0.00");
        this.valorTotalCheques = new BigDecimal("0.00");
        this.valorTotalTarjetas = new BigDecimal("0.00");
        this.valorTotalTransferencias = new BigDecimal("0.00");
        this.valorTotal = new BigDecimal("0.00");
        //this.valorCoactiva = new BigDecimal("0.00");
        this.pagoCheque = new PagoCheque();
        this.pagoNotaCredio = new PagoNotaCredito();
        this.pagoTarjetaCredito = new PagoTarjetaCredito();
        this.pagoTransferencia = new PagoTransferencia();
    }
    
    public PagoTituloReporteModel(BigDecimal valorSaldoPago, Boolean variosPagos,PagoNotaCredito pagoNotaCredio,PagoCheque pagoCheque,PagoTarjetaCredito pagoTarjetaCredito,PagoTransferencia pagoTransferencia) {
        //this.valorRecibido = new BigDecimal("0.00");
        this.valorCobrar = new BigDecimal("0.00");
        this.valorCambio = new BigDecimal("0.00");
        this.valorCobrarCompensacion = new BigDecimal("0.00");
        this.valorCambioCompensacion = new BigDecimal("0.00");
        this.valorRecibidoCompensacion = new BigDecimal("0.00");
        this.listPagoCheque = new ArrayList<>();
        this.listPagoNotaCredio = new ArrayList<>();
        this.listPagoTarjetaCredito = new ArrayList<>();
        this.listPagoTransferencia = new ArrayList<>();
        this.valorTotalEfectivo = new BigDecimal("0.00");
        this.valorTotalNotasCredito = new BigDecimal("0.00");
        this.valorTotalCheques = new BigDecimal("0.00");
        this.valorTotalTarjetas = new BigDecimal("0.00");
        this.valorTotalTransferencias = new BigDecimal("0.00");
        this.valorTotalCompesacion  = new BigDecimal("0.00");
        this.valorTotal = new BigDecimal("0.00");
        
        //this.valorCoactiva = new BigDecimal("0.00");
        if(variosPagos){
            this.pagoCheque = pagoCheque;
            this.pagoNotaCredio = pagoNotaCredio;
            this.pagoTarjetaCredito = pagoTarjetaCredito;
            this.pagoTransferencia = pagoTransferencia;
        }else{
            this.pagoCheque = new PagoCheque();
            this.pagoNotaCredio = new PagoNotaCredito();
            this.pagoTarjetaCredito = new PagoTarjetaCredito();
            this.pagoTransferencia = new PagoTransferencia();
        }
        this.valorSaldoPago=valorSaldoPago;
        this.valorSaldoPagoFinal=valorSaldoPago;
    }

    public void calcularCompensacion() {
        if (valorRecibidoCompensacion != null) {
            valorRecibidoCompensacion = valorRecibidoCompensacion.setScale(2, RoundingMode.UP);
        }
        if (valorCobrarCompensacion != null) {
            valorCobrarCompensacion = valorCobrarCompensacion.setScale(2, RoundingMode.UP);
        }
        if (valorRecibidoCompensacion != null && valorCobrarCompensacion != null) {
            valorCambioCompensacion = valorRecibidoCompensacion.subtract(valorCobrarCompensacion).setScale(2, RoundingMode.UP);
            valorTotalCompesacion = valorCobrarCompensacion;
        } else {
            valorTotalCompesacion = new BigDecimal("0.00");
            valorCambioCompensacion = null;
        }
        this.calcularTotalPago();
    }
    
    public void calcularEfectivo() {
        if (valorRecibido != null) {
            valorRecibido = valorRecibido.setScale(2, RoundingMode.UP);
        }
        if (valorCobrar != null) {
            valorCobrar = valorCobrar.setScale(2, RoundingMode.UP);
        }
        if (valorRecibido != null && valorCobrar != null) {
            valorCambio = valorRecibido.subtract(valorCobrar).setScale(2, RoundingMode.UP);
            valorTotalEfectivo = valorCobrar;
        } else {
            valorTotalEfectivo = new BigDecimal("0.00");
            valorCambio = null;
        }
        this.calcularTotalPago();
    }

    public void agregarPago(Integer tipoPago) {
        switch (tipoPago) {
            case 2:
                if (pagoTarjetaCredito != null && pagoTarjetaCredito.getValor() != null && pagoTarjetaCredito.getValor().compareTo(new BigDecimal("0.00")) > 0) {
                    if (!listPagoTarjetaCredito.contains(pagoTarjetaCredito)) {
                        PagoTarjetaCredito ptc= new PagoTarjetaCredito();
                        ptc.setAutorizacion(pagoTarjetaCredito.getAutorizacion());
                        ptc.setBanco(pagoTarjetaCredito.getBanco());
                        ptc.setBaucher(pagoTarjetaCredito.getBaucher());
                        ptc.setFechaCaducidad(pagoTarjetaCredito.getFechaCaducidad());
                        ptc.setNombreTitular(pagoTarjetaCredito.getNombreTitular());
                        ptc.setNumTarjeta(pagoTarjetaCredito.getNumTarjeta());
                        ptc.setValor(pagoTarjetaCredito.getValor());
                        ptc.setTipoPago(tipoPago);
                        listPagoTarjetaCredito.add(ptc);
                        valorTotalTarjetas = valorTotalTarjetas.add(ptc.getValor());
                        //pagoTarjetaCredito = new PagoTarjetaCredito();
                    } else {
                        JsfUti.messageInfo(null, "Verifique los datos.", "");
                    }
                } else {
                    JsfUti.messageInfo(null, "Para agregar el detalle, debe ingresar los parametros y el valor debe ser mayor a CERO.", "");
                }
                break;
            case 3:
                if (pagoNotaCredio != null && pagoNotaCredio.getValor() != null && pagoNotaCredio.getValor().compareTo(new BigDecimal("0.00")) > 0) {
                    if (!listPagoNotaCredio.contains(pagoNotaCredio)) {
                        PagoNotaCredito pnc= new PagoNotaCredito();
                        pnc.setFecha(pagoNotaCredio.getFecha());
                        pnc.setNumCredito(pagoNotaCredio.getNumCredito());
                        pnc.setValor(pagoNotaCredio.getValor());
                        pnc.setTipoPago(tipoPago);
                        listPagoNotaCredio.add(pnc);
                        valorTotalNotasCredito = valorTotalNotasCredito.add(pnc.getValor());
                        //pagoNotaCredio = new PagoNotaCredito();
                    } else {
                        JsfUti.messageInfo(null, "Verifique los datos.", "");
                    }
                } else {
                    JsfUti.messageInfo(null, "Para agregar el detalle, debe ingresar los parametros y el valor debe ser mayor a CERO.", "");
                }
                break;
            case 4:
                if (pagoCheque != null && pagoCheque.getValor() != null && pagoCheque.getValor().compareTo(new BigDecimal("0.00")) > 0) {
                    if (!listPagoCheque.contains(pagoCheque)) {
                        PagoCheque pch=new PagoCheque();
                        pch.setBanco(pagoCheque.getBanco());
                        pch.setNumCheque(pagoCheque.getNumCheque());
                        pch.setNumCuenta(pagoCheque.getNumCuenta());
                        pch.setValor(pagoCheque.getValor());
                        pch.setTipoPago(tipoPago);
                        listPagoCheque.add(pch);
                        valorTotalCheques = valorTotalCheques.add(pch.getValor());
                        //pagoCheque = new PagoCheque();
                    } else {
                        JsfUti.messageInfo(null, "Verifique los datos.", "");
                    }
                } else {
                    JsfUti.messageInfo(null, "Para agregar el detalle, debe ingresar los parametros y el valor debe ser mayor a CERO.", "");
                }
                break;
            case 5:
                if (pagoTransferencia != null && pagoTransferencia.getValor() != null && pagoTransferencia.getValor().compareTo(new BigDecimal("0.00")) > 0) {
                    if (!listPagoTransferencia.contains(pagoTransferencia)) {
                        PagoTransferencia pt= new PagoTransferencia();
                        pt.setBanco(pagoTransferencia.getBanco());
                        pt.setFecha(pagoTransferencia.getFecha());
                        pt.setNumTransferencia(pagoTransferencia.getNumTransferencia());
                        pt.setTipoPago(tipoPago);
                        pt.setValor(pagoTransferencia.getValor());
                        listPagoTransferencia.add(pt);
                        valorTotalTransferencias = valorTotalTransferencias.add(pt.getValor());
                        //pagoTransferencia = new PagoTransferencia();
                    } else {
                        JsfUti.messageInfo(null, "Verifique los datos.", "");
                    }
                } else {
                    JsfUti.messageInfo(null, "Para agregar el detalle, debe ingresar los parametros y el valor debe ser mayor a CERO.", "");
                }
                break;
            default:
                break;
        }
        this.calcularTotalPago();
    }

    public void eliminarPagoTC(PagoTarjetaCredito tc) {
        listPagoTarjetaCredito.remove(tc);
        valorTotalTarjetas = valorTotalTarjetas.subtract(tc.getValor());
        this.calcularTotalPago();
    }

    public void eliminarPagoNC(PagoNotaCredito nc) {
        listPagoNotaCredio.remove(nc);
        valorTotalNotasCredito = valorTotalNotasCredito.subtract(nc.getValor());
        this.calcularTotalPago();
    }

    public void eliminarPagoCh(PagoCheque ch) {
        listPagoCheque.remove(ch);
        valorTotalCheques = valorTotalCheques.subtract(ch.getValor());
        this.calcularTotalPago();
    }

    public void eliminarPagoTransferencia(PagoTransferencia t) {
        listPagoTransferencia.remove(t);
        valorTotalTransferencias = valorTotalTransferencias.subtract(t.getValor());
        this.calcularTotalPago();
    }

    public int cantidadTipoPagos() {
        int i = 0;
        if (valorTotalEfectivo.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        if (valorTotalCheques.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        if (valorTotalNotasCredito.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        if (valorTotalTarjetas.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        if (valorTotalTransferencias.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        if (valorTotalCompesacion.compareTo(new BigDecimal("0.00")) > 0) {
            i++;
        }
        return i;
    }
    

    public void calcularTotalPago() {
        valorTotal = valorTotalEfectivo.add(valorTotalNotasCredito).add(valorTotalTarjetas).add(valorTotalCheques).add(valorTotalTransferencias).add(valorTotalCompesacion);
        if(valorSaldoPago == null){
            valorSaldoPago = BigDecimal.ZERO;
        }
        valorSaldoPagoFinal= valorSaldoPago.subtract(valorTotal);
    }

    public RenPago realizarPago(RenLiquidacion liquidacion) {
        RenPago pago = new RenPago();
        RenPagoDetalle detalle;
        List<RenPagoDetalle> listDetPago = new ArrayList<>();
        BigDecimal valorPago = new BigDecimal("0.00");
        try {
            if (valorTotalEfectivo.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                detalle = new RenPagoDetalle();
                if (liquidacion.getPagoFinal().compareTo(valorTotalEfectivo) >= 0) {
                    //System.out.println("/*** EL VALOR DE LA LIQUIDACION ES MAYOR O IGUAL AL VALOR RECAUDADO (ABONO)");
                    detalle.setValor(this.valorTotalEfectivo);
                } else {
                    //System.out.println("/*** EL VALOR DE LA RECAUDACION ES MAYOR AL DE LA LIQUIDACION (VARIOS PAGOS)");
                    //SE ACTULIZA EL VALOR TOTAL DEL MODELO DEL PAGO
                    detalle.setValor(liquidacion.getPagoFinal());//EL SALDO ES DEL VALOR ORIGINAL - DEBE EXITIR UN TRASIEN CON UN VALOR TOTAL
                }
                valorTotalEfectivo=valorTotalEfectivo.subtract(detalle.getValor());
                this.actualizarValorTotal(detalle.getValor());
                liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                detalle.setTipoPago(1);
                listDetPago.add(detalle);
                //actualizarValoresTotal(detalle.getValor());
            }
            if (valorTotalCheques.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                for (PagoCheque pch : listPagoCheque) {
                    detalle = new RenPagoDetalle();
                    if (liquidacion.getPagoFinal().compareTo(pch.getValor()) >= 0) {
                        detalle.setValor(pch.getValor());
                    } else {
                        detalle.setValor(liquidacion.getPagoFinal());
                    }
                    valorTotalCheques=valorTotalCheques.subtract(detalle.getValor());
                    pch.setValor(pch.getValor().subtract(detalle.getValor()));
                    this.actualizarValorTotal(detalle.getValor());
                    liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                    detalle.setChBanco(pch.getBanco());
                    detalle.setChNumCheque(pch.getNumCheque());
                    detalle.setChNumCuenta(pch.getNumCuenta());
                    detalle.setTipoPago(pch.getTipoPago());
                    listDetPago.add(detalle);
                    if(liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))<=0){
                        break;
                    }
                }
            }
            if (valorTotalNotasCredito.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                for (PagoNotaCredito pnc : listPagoNotaCredio) {
                    detalle = new RenPagoDetalle();
                    if (liquidacion.getPagoFinal().compareTo(pnc.getValor()) >= 0) {
                        detalle.setValor(pnc.getValor());
                    } else {
                        detalle.setValor(liquidacion.getPagoFinal());
                    }
                    valorTotalNotasCredito=valorTotalNotasCredito.subtract(detalle.getValor());
                    pnc.setValor(pnc.getValor().subtract(detalle.getValor()));
                    this.actualizarValorTotal(detalle.getValor());
                    liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                    detalle.setNcNumCredito(pnc.getNumCredito());
                    detalle.setNcFecha(pnc.getFecha());
                    detalle.setTipoPago(pnc.getTipoPago());
                    listDetPago.add(detalle);
                    if(liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))<=0){
                        break;
                    }
                }
            }
            if (valorTotalTarjetas.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                for (PagoTarjetaCredito ptc : listPagoTarjetaCredito) {
                    detalle = new RenPagoDetalle();
                    if (liquidacion.getPagoFinal().compareTo(ptc.getValor()) >= 0) {
                        detalle.setValor(ptc.getValor());
                    } else {
                        detalle.setValor(liquidacion.getPagoFinal());
                    }
                    valorTotalTarjetas=valorTotalTarjetas.subtract(detalle.getValor());
                    ptc.setValor(ptc.getValor().subtract(detalle.getValor()));
                    this.actualizarValorTotal(detalle.getValor());
                    liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                    detalle.setTcAutorizacion(ptc.getAutorizacion());
                    detalle.setTcBanco(ptc.getBanco());
                    detalle.setTcBaucher(ptc.getBaucher());
                    detalle.setTcFechaCaducidad(ptc.getFechaCaducidad());
                    detalle.setTcNumTarjeta(ptc.getNumTarjeta());
                    detalle.setTcTitular(ptc.getNombreTitular());
                    detalle.setTipoPago(ptc.getTipoPago());
                    listDetPago.add(detalle);
                    if(liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))<=0){
                        break;
                    }
                }
            }
            if (valorTotalTransferencias.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                for (PagoTransferencia pt : listPagoTransferencia) {
                    detalle = new RenPagoDetalle();
                    if (liquidacion.getPagoFinal().compareTo(pt.getValor()) >= 0) {
                        detalle.setValor(pt.getValor());
                    } else {
                        detalle.setValor(liquidacion.getPagoFinal());
                    }
                    valorTotalTransferencias=valorTotalTransferencias.subtract(detalle.getValor());
                    pt.setValor(pt.getValor().subtract(detalle.getValor()));
                    this.actualizarValorTotal(detalle.getValor());
                    liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                    detalle.setTrBanco(pt.getBanco());
                    detalle.setTrFecha(pt.getFecha());
                    detalle.setTrNumTransferencia(pt.getNumTransferencia());
                    detalle.setTipoPago(pt.getTipoPago());
                    listDetPago.add(detalle);
                    if(liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))<=0){
                        break;
                    }
                }
            }
            //COMPENSACIONES :(
            
            if (valorTotalCompesacion.compareTo(new BigDecimal("0.00")) > 0 && valorTotal.compareTo(new BigDecimal("0.00"))>0 && liquidacion.getPagoFinal().compareTo(new BigDecimal("0.00"))>0) {
                detalle = new RenPagoDetalle();
                if (liquidacion.getPagoFinal().compareTo(valorTotalCompesacion) >= 0) {
                    //System.out.println("/*** EL VALOR DE LA LIQUIDACION ES MAYOR O IGUAL AL VALOR RECAUDADO (ABONO)");
                    detalle.setValor(this.valorTotalCompesacion);
                } else {
                    //System.out.println("/*** EL VALOR DE LA RECAUDACION ES MAYOR AL DE LA LIQUIDACION (VARIOS PAGOS)");
                    //SE ACTULIZA EL VALOR TOTAL DEL MODELO DEL PAGO
                    detalle.setValor(liquidacion.getPagoFinal());//EL SALDO ES DEL VALOR ORIGINAL - DEBE EXITIR UN TRASIEN CON UN VALOR TOTAL
                }
                valorTotalCompesacion=valorTotalCompesacion.subtract(detalle.getValor());
                this.actualizarValorTotal(detalle.getValor());
                liquidacion.setPagoFinal(liquidacion.getPagoFinal().subtract(detalle.getValor()));
                detalle.setTipoPago(6);
                listDetPago.add(detalle);
                //actualizarValoresTotal(detalle.getValor());
            }
            for (RenPagoDetalle d : listDetPago) {
                valorPago = valorPago.add(d.getValor());
            }
            pago.setRenPagoDetalles(listDetPago);
            pago.setValor(valorPago);
            pago.setObservacion(observacion);
        } catch (Exception e) {
            Logger.getLogger(PagoTituloReporteModel.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return pago;
    }

    public void actualizarValorTotal(BigDecimal valor) {
        valorTotal = valorTotal.subtract(valor);
        valorSaldoPagoFinal= valorSaldoPago.add(valorTotal);
    }

    /*public void calculoValoresPago() {
        BigDecimal temp = this.valorTotal.multiply(BigDecimal.TEN).divide(new BigDecimal("11"), 2, RoundingMode.HALF_UP);
        System.out.println("// valor sin coactiva: " + temp);
        this.valorCoactiva = this.valorTotal.subtract(temp);
        System.out.println("// valor coactiva: " + valorCoactiva);
        this.valorTotal = temp;
        System.out.println("// valor final: " + valorTotal);
    }*/

    public BigDecimal getValorRecibido() {
        return valorRecibido;
    }

    public void setValorRecibido(BigDecimal valorRecibido) {
        this.valorRecibido = valorRecibido;
    }

    public BigDecimal getValorCobrar() {
        return valorCobrar;
    }

    public void setValorCobrar(BigDecimal valorCobrar) {
        this.valorCobrar = valorCobrar;
    }

    public BigDecimal getValorCambio() {
        return valorCambio;
    }

    public void setValorCambio(BigDecimal valorCambio) {
        this.valorCambio = valorCambio;
    }

    public List<PagoCheque> getListPagoCheque() {
        return listPagoCheque;
    }

    public void setListPagoCheque(List<PagoCheque> listPagoCheque) {
        this.listPagoCheque = listPagoCheque;
    }

    public List<PagoNotaCredito> getListPagoNotaCredio() {
        return listPagoNotaCredio;
    }

    public void setListPagoNotaCredio(List<PagoNotaCredito> listPagoNotaCredio) {
        this.listPagoNotaCredio = listPagoNotaCredio;
    }

    public List<PagoTarjetaCredito> getListPagoTarjetaCredito() {
        return listPagoTarjetaCredito;
    }

    public void setListPagoTarjetaCredito(List<PagoTarjetaCredito> listPagoTarjetaCredito) {
        this.listPagoTarjetaCredito = listPagoTarjetaCredito;
    }

    public List<PagoTransferencia> getListPagoTransferencia() {
        return listPagoTransferencia;
    }

    public void setListPagoTransferencia(List<PagoTransferencia> listPagoTransferencia) {
        this.listPagoTransferencia = listPagoTransferencia;
    }

    public BigDecimal getValorTotalEfectivo() {
        return valorTotalEfectivo;
    }

    public void setValorTotalEfectivo(BigDecimal valorTotalEfectivo) {
        this.valorTotalEfectivo = valorTotalEfectivo;
    }

    public BigDecimal getValorTotalNotasCredito() {
        return valorTotalNotasCredito;
    }

    public void setValorTotalNotasCredito(BigDecimal valorTotalNotasCredito) {
        this.valorTotalNotasCredito = valorTotalNotasCredito;
    }

    public BigDecimal getValorTotalCheques() {
        return valorTotalCheques;
    }

    public void setValorTotalCheques(BigDecimal valorTotalCheques) {
        this.valorTotalCheques = valorTotalCheques;
    }

    public BigDecimal getValorTotalTarjetas() {
        return valorTotalTarjetas;
    }

    public void setValorTotalTarjetas(BigDecimal valorTotalTarjetas) {
        this.valorTotalTarjetas = valorTotalTarjetas;
    }

    public BigDecimal getValorTotalTransferencias() {
        return valorTotalTransferencias;
    }

    public void setValorTotalTransferencias(BigDecimal valorTotalTransferencias) {
        this.valorTotalTransferencias = valorTotalTransferencias;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public PagoCheque getPagoCheque() {
        return pagoCheque;
    }

    public void setPagoCheque(PagoCheque pagoCheque) {
        this.pagoCheque = pagoCheque;
    }

    public PagoNotaCredito getPagoNotaCredio() {
        return pagoNotaCredio;
    }

    public void setPagoNotaCredio(PagoNotaCredito pagoNotaCredio) {
        this.pagoNotaCredio = pagoNotaCredio;
    }

    public PagoTarjetaCredito getPagoTarjetaCredito() {
        return pagoTarjetaCredito;
    }

    public void setPagoTarjetaCredito(PagoTarjetaCredito pagoTarjetaCredito) {
        this.pagoTarjetaCredito = pagoTarjetaCredito;
    }

    public PagoTransferencia getPagoTransferencia() {
        return pagoTransferencia;
    }

    public void setPagoTransferencia(PagoTransferencia pagoTransferencia) {
        this.pagoTransferencia = pagoTransferencia;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /*public BigDecimal getValorCoactiva() {
        return valorCoactiva;
    }

    public void setValorCoactiva(BigDecimal valorCoactiva) {
        this.valorCoactiva = valorCoactiva;
    }*/

    public BigDecimal getValorSaldoPago() {
        return valorSaldoPago;
    }

    public void setValorSaldoPago(BigDecimal valorSaldoPago) {
        this.valorSaldoPago = valorSaldoPago;
    }

    public BigDecimal getValorSaldoPagoFinal() {
        return valorSaldoPagoFinal;
    }

    public void setValorSaldoPagoFinal(BigDecimal valorSaldoPagoFinal) {
        this.valorSaldoPagoFinal = valorSaldoPagoFinal;
    }

    public BigDecimal getValorRecibidoCompensacion() {
        return valorRecibidoCompensacion;
    }

    public void setValorRecibidoCompensacion(BigDecimal valorRecibidoCompensacion) {
        this.valorRecibidoCompensacion = valorRecibidoCompensacion;
    }
    
    

    public BigDecimal getValorTotalCompesacion() {
        return valorTotalCompesacion;
    }

    public void setValorTotalCompesacion(BigDecimal valorTotalCompesacion) {
        this.valorTotalCompesacion = valorTotalCompesacion;
    }

    public BigDecimal getValorCambioCompensacion() {
        return valorCambioCompensacion;
    }

    public void setValorCambioCompensacion(BigDecimal valorCambioCompensacion) {
        this.valorCambioCompensacion = valorCambioCompensacion;
    }

    public BigDecimal getValorCobrarCompensacion() {
        return valorCobrarCompensacion;
    }

    public void setValorCobrarCompensacion(BigDecimal valorCobrarCompensacion) {
        this.valorCobrarCompensacion = valorCobrarCompensacion;
    }
    
    
    
}
