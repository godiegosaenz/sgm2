/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.session.UserSession;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.FnConvenioPago;
import com.origami.sgm.entities.FnConvenioPagoObservacion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.JsfUti;

/**
 *
 * @author Dairon Freddy
 */
@Named(value = "convenioPagoView")
@ViewScoped
public class DialogConvenioPago implements Serializable {

    @Inject
    private Entitymanager manager;
    @Inject
    private UserSession userSession;

    private FnConvenioPago convenio;

    private short crear, calculoInteres;
    private String esNuevo, calculaInteres, aplicaRemision;
    private String idEnte;
    private String idConvenio;
    private String deudaString;
    private String descripcion;
    private BigDecimal deudaInicial;
    private BigDecimal tasaActivaReferencial;
    private boolean aplicaFull = true;
    private BigDecimal cuotaMensual;
    private String observaciones;
    ///VARIABLE PARA EXONERAR EL 20 % DEL PAGO INICIAL
    private Boolean exoneraPagoInicial;

    public void initView() {
        if (!JsfUti.isAjaxRequest()) {
            aplicaFull = true;
            cuotaMensual = BigDecimal.ZERO;
            tasaActivaReferencial = new BigDecimal(9.33);
            crear = Short.valueOf(esNuevo);
            calculoInteres = Short.valueOf(calculaInteres);
            deudaInicial = new BigDecimal(deudaString);
            if (aplicaRemision != null) {
                exoneraPagoInicial = Boolean.valueOf(aplicaRemision);
            } else {
                exoneraPagoInicial = Boolean.FALSE;
            }
            if (exoneraPagoInicial == null) {
                exoneraPagoInicial = Boolean.FALSE;
            }
            if (idConvenio != null) {
                convenio = manager.find(FnConvenioPago.class, Long.parseLong(idConvenio));
                cuotaMensual = convenio.getDeudaDiferir().divide(new BigDecimal(convenio.getCantidadMesesDiferir()), 2, RoundingMode.HALF_UP);
            } else {
                if (crear == 0 || crear == 1) {
                    if (convenio == null) {
                        convenio = new FnConvenioPago();
                        convenio.setDeudaInicial(deudaInicial);
                        convenio.setDescripcion(descripcion);
                        convenio.setFechaInicio(new Date());

                        if (idEnte != null) {
                            convenio.setContribuyente(manager.find(CatEnte.class, Long.valueOf(idEnte)));
                        }
                        calcular(true);
                    }
                }
            }
            deudaInicial = convenio.getDeudaInicial();
        }

    }

    public void formConvenio() {
        try {
            if (convenio != null) {
                if (convenio.getId() == null) {
                    convenio.setFechaIngreso(new Date());
                    convenio.setUsuarioIngreso(userSession.getName_user());
                }
                convenio.setDescripcion(descripcion.toUpperCase());
                if (convenio.getPorcientoInicial().compareTo(new BigDecimal("20")) >= 0 && !exoneraPagoInicial) {
                    dataConvenio();
                } else {
                    if (exoneraPagoInicial) {
                        dataConvenio();
                    } else {
                        JsfUti.messageError(null, "Info", "El porciento de entrada debe ser mayor o igual al 20 %.");
                    }

                }
            }
        } catch (Exception ex) {
            RequestContext.getCurrentInstance().closeDialog(null);
        }
    }

    public void dataConvenio() {
        if (crear != 0) {

            if (convenio.getId() != null) {
                convenio.setEstado((short) 1);
            }
            convenio = (FnConvenioPago) manager.persist(convenio);

            FnConvenioPagoObservacion observacionConvenio = new FnConvenioPagoObservacion();
            observacionConvenio.setConvenio(convenio);
            observacionConvenio.setEstado(Boolean.TRUE);
            observacionConvenio.setEstadoConvenio(convenio.getEstado());
            observacionConvenio.setObservacion(convenio.getObservacion());
            observacionConvenio.setUsuarioIngreso(userSession.getName_user());
            observacionConvenio.setFechaIngreso(new Date());

            manager.persist(observacionConvenio);
        }
        RequestContext.getCurrentInstance().closeDialog(convenio);
    }

    public void calcular(boolean porciento) {
        BigDecimal div = new BigDecimal(100);
        BigDecimal divAnual = new BigDecimal(1200);
        if (porciento) {
            BigDecimal valorPorciento = convenio.getDeudaInicial().multiply(convenio.getPorcientoInicial()).divide(div, 2, RoundingMode.HALF_UP);
            convenio.setValorPorcientoInicial(valorPorciento);
            convenio.setDiferenciaFinanciar(convenio.getDeudaInicial().subtract(valorPorciento).setScale(2, RoundingMode.HALF_UP));
        } else {
            BigDecimal porcientoCalculado = convenio.getValorPorcientoInicial().multiply(div).divide(convenio.getDeudaInicial(), 2, RoundingMode.HALF_UP);
            convenio.setPorcientoInicial(porcientoCalculado);
            convenio.setDiferenciaFinanciar(convenio.getDeudaInicial().subtract(convenio.getValorPorcientoInicial()).setScale(2, RoundingMode.HALF_UP));
        }
        BigDecimal interesMensual;
        double valorI;
        if (aplicaFull) {
            valorI = tasaActivaReferencial.floatValue() * 1.5 / 1200;
//            interesMensual = new BigDecimal(valorI);
            interesMensual = tasaActivaReferencial.multiply(new BigDecimal(1.5)).divide(divAnual).setScale(4, RoundingMode.HALF_UP);
        } else {
            valorI = tasaActivaReferencial.floatValue() / 1200;
//            interesMensual = new BigDecimal(valorI);
            interesMensual = tasaActivaReferencial.divide(divAnual, 4, RoundingMode.HALF_UP);
        }
        if (calculoInteres == 1) {
            convenio.setTasaInteresMensual(BigDecimal.ZERO);
        } else {
            convenio.setTasaInteresMensual(interesMensual);
        }

        BigDecimal interesXmeses = convenio.getTasaInteresMensual().multiply(new BigDecimal(convenio.getCantidadMesesDiferir()));
        interesXmeses = interesXmeses.add(BigDecimal.ONE);

        BigDecimal valor = convenio.getDiferenciaFinanciar().multiply(interesXmeses).setScale(2, RoundingMode.HALF_UP);

        BigDecimal interesCausado = valor.subtract(convenio.getDiferenciaFinanciar()).setScale(2, RoundingMode.HALF_UP);

        convenio.setInteresCausado(interesCausado.setScale(2, RoundingMode.HALF_UP));

        convenio.setDeudaDiferir(convenio.getInteresCausado().add(convenio.getDiferenciaFinanciar()).setScale(2, RoundingMode.HALF_UP));

        cuotaMensual = convenio.getDeudaDiferir().divide(new BigDecimal(convenio.getCantidadMesesDiferir()), 2, RoundingMode.HALF_UP);

    }

    public FnConvenioPago getConvenio() {
        return convenio;
    }

    public void setConvenio(FnConvenioPago convenio) {
        this.convenio = convenio;
    }

    public short getCrear() {
        return crear;
    }

    public void setCrear(short crear) {
        this.crear = crear;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public String getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(String idConvenio) {
        this.idConvenio = idConvenio;
    }

    public String getDeudaString() {
        return deudaString;
    }

    public void setDeudaString(String deudaString) {
        this.deudaString = deudaString;
    }

    public BigDecimal getDeudaInicial() {
        return deudaInicial;
    }

    public void setDeudaInicial(BigDecimal deudaInicial) {
        this.deudaInicial = deudaInicial;
    }

    public BigDecimal getTasaActivaReferencial() {
        return tasaActivaReferencial;
    }

    public void setTasaActivaReferencial(BigDecimal tasaActivaReferencial) {
        this.tasaActivaReferencial = tasaActivaReferencial;
    }

    public boolean isAplicaFull() {
        return aplicaFull;
    }

    public void setAplicaFull(boolean aplicaFull) {
        this.aplicaFull = aplicaFull;
    }

    public BigDecimal getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdEnte() {
        return idEnte;
    }

    public void setIdEnte(String idEnte) {
        this.idEnte = idEnte;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCalculaInteres() {
        return calculaInteres;
    }

    public void setCalculaInteres(String calculaInteres) {
        this.calculaInteres = calculaInteres;
    }

    public short getCalculoInteres() {
        return calculoInteres;
    }

    public void setCalculoInteres(short calculoInteres) {
        this.calculoInteres = calculoInteres;
    }

    public String getAplicaRemision() {
        return aplicaRemision;
    }

    public void setAplicaRemision(String aplicaRemision) {
        this.aplicaRemision = aplicaRemision;
    }

    public Boolean getExoneraPagoInicial() {
        return exoneraPagoInicial;
    }

    public void setExoneraPagoInicial(Boolean exoneraPagoInicial) {
        this.exoneraPagoInicial = exoneraPagoInicial;
    }

}
