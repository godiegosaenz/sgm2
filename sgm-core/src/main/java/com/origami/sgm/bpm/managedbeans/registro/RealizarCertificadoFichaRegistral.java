/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author hpilco
 */
@Named
@ViewScoped
public class RealizarCertificadoFichaRegistral extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private ServletSession servletSession;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaRegServices;

    protected Calendar cal = Calendar.getInstance();
    protected Long idTarea;
    protected String lindero;
    protected Long numeroFicha;
    protected Long numeroPredio;
    protected CatPredio predio;
    protected CatEscritura escritura;
    protected List<CatPredioPropietario> propietarios;
    protected RegFicha regFichaTable;
    protected Boolean guardar = false;
    protected Boolean print = false;
    protected RegFicha regFicha = new RegFicha();
    protected List<RegMovimiento> movimientosFicha = new ArrayList<>();
    protected RegpCertificadosInscripciones tarea = new RegpCertificadosInscripciones();

    protected RegRegistrador registrador = new RegRegistrador();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        if (idTarea != null) {
            if (session.getTaskID() != null) {
                this.setTaskId(session.getTaskID());
            }
            tarea = (RegpCertificadosInscripciones) acl.find(RegpCertificadosInscripciones.class, idTarea);
            registrador = (RegRegistrador) acl.find(Querys.getRegRegistrador);
        } else {
            this.continuar();
        }
    }

    public void buscarFichaRegistral() {
        try {
            Boolean flag;
            if (numeroFicha != null) {
                if (this.getTaskId() != null) {
                    flag = fichaRegServices.compruebaFichaTareaRegistro(tarea.getLiquidacion().getId(), null, numeroFicha);
                } else {
                    flag = fichaRegServices.compruebaFichaTareaRegistro(null, tarea.getTareaDinardap().getId(), numeroFicha);
                }
                if (flag) {
                    JsfUti.update("formConfirmFicha");
                    JsfUti.executeJS("PF('dlgConfirmFicha').show();");
                } else {
                    this.busqueda();
                }
            } else {
                JsfUti.messageInfo(null, "Debe Ingresar el Numero de Ficha.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RealizarCertificadoFichaRegistral.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void busqueda() {
        regFicha = fichaRegServices.getFichaByNumFichaByTipo(numeroFicha, 1L);
        if (regFicha != null) {
            if (regFicha.getEstado().getValor().equalsIgnoreCase("INACTIVO")) {
                JsfUti.messageError(null, "No se puede imprimir Ficha Registral, estado de Ficha: INACTIVA.", "");
            } else {
                JsfUti.messageInfo(null, "El estado de la Ficha es: " + regFicha.getEstado().getValor(), "");
                regFicha.setDescripcionTemp(regFicha.getObsvEstado(regFicha.getEstado()));
                movimientosFicha = fichaRegServices.getMovimientosByFicha(regFicha.getId());
                JsfUti.update("mainForm");
            }
        } else {
            JsfUti.messageWarning(null, "El Numero de Ficha: " + numeroFicha + ", no se encuentra.", "");
        }
        JsfUti.executeJS("PF('dlgConfirmFicha').hide();");
    }

    public void onRowSelect(SelectEvent event) {
        regFichaTable = (RegFicha) event.getObject();
        if (regFichaTable != null) {
            regFicha = regFichaTable;
        }
        if (regFicha != null && regFicha.getPredio() != null) {
            numeroPredio = regFicha.getPredio().getNumPredio().longValue();
            buscarPredio();
        }
    }

    public void buscarPredio() {
        if (numeroPredio != null) {
            predio = fichaRegServices.getPredioByNum(numeroPredio);
            if (predio != null) {
                escritura = fichaRegServices.getCatEscrituraByPredio(predio.getId());
                propietarios = fichaRegServices.getPropietariosByPredio(predio.getId());
            } else {
                JsfUti.messageInfo(null, "No se encontro el predio.", "");
            }
        } else {
            JsfUti.messageInfo(null, "No se encontro el predio.", "");
        }
    }

    public String getCorreo(CatEnte e) {
        List<EnteCorreo> ec = fichaRegServices.getEnteCorreoList(e.getId());
        if (ec.size() > 0) {
            return ec.get(0).getEmail();
        }
        return null;
    }

    private boolean existeRegMovimientoAgregado(RegMovimiento mov) {
        for (RegMovimientoFicha mf : regFicha.getRegMovimientoFichaCollection()) {
            if (mf.getMovimiento().equals(mov)) {
                return true;
            }
        }
        return false;
    }

    public void agregarMovientos(RegMovimiento mov) {
        RegMovimientoFicha mf = new RegMovimientoFicha();
        if (!existeRegMovimientoAgregado(mov)) {
            mf.setMovimiento(mov);
            mf.setFicha(regFicha);
            regFicha.getRegMovimientoFichaCollection().add(mf);
        } else {
            JsfUti.messageInfo(null, "Este movimiento ya fue agregado a esta ficha", "");
        }
    }

    public void eliminarMovimiento(RegMovimientoFicha mf) {
        int index = -1;
        int i = 0;
        ArrayList<RegMovimientoFicha> listado = new ArrayList<>(regFicha.getRegMovimientoFichaCollection());
        for (RegMovimientoFicha m : listado) {
            if (m.getMovimiento().getId().compareTo(mf.getMovimiento().getId()) == 0) {
                index = i;
            }
            i++;
        }
        listado.remove(index);
        regFicha.setRegMovimientoFichaCollection(listado);
    }

    public void showDlgConfirmFicha() {
        if (regFicha.getId() != null) {
            JsfUti.update("formConfirmFicha");
            JsfUti.executeJS("PF('dlgConfirmFicha').show();");
        } else {
            JsfUti.messageError(null, "Primero debe buscar la ficha a imprimir.", "");
        }
    }

    public void imprimirFichaRegistral(String nombreCertificado) {
        try {
            if (regFicha != null && regFicha.getId() != null) {
                print = true;
                servletSession.instanciarParametros();
                servletSession.agregarParametro("registrador", registrador.getNombreCompleto());
                servletSession.agregarParametro("tituloregistrador", registrador.getTituloCompleto());
                servletSession.setTieneDatasource(true);
                servletSession.setNombreSubCarpeta("registroPropiedad");
                servletSession.setNombreReporte(nombreCertificado);
                servletSession.agregarParametro("ID_FICHA", regFicha.getId());
                servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
                servletSession.agregarParametro("USER_NAME", session.getName_user());
                servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                servletSession.setEncuadernacion(Boolean.TRUE);
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            } else {
                JsfUti.messageError(null, "Primero debe buscar la ficha a imprimir.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarCertificadoFichaRegistral.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void actualizarTarea() {
        try {
            if (print) {
                if (regFicha.getId() != null) {
                    if(tarea.getLiquidacion() != null){
                        fichaRegServices.registrarImpresionFicha(regFicha, BigInteger.valueOf(cal.get(Calendar.YEAR)), tarea.getLiquidacion().getNumTramiteRp());
                    } else {
                        fichaRegServices.registrarImpresionFicha(regFicha, BigInteger.valueOf(cal.get(Calendar.YEAR)), null);
                    }
                    tarea.setRealizado(true);
                    tarea.setFechaFin(new Date());
                    tarea.setObservacion("Ficha Registral: " + regFicha.getNumFicha());
                    tarea.setNumFicha(regFicha.getNumFicha());
                    tarea.setAclUser(session.getUserId());
                    acl.persist(tarea);
                    this.continuar();
                } else {
                    JsfUti.messageError(null, "Primero debe buscar la ficha a imprimir.", "");
                }
            } else {
                JsfUti.messageError(null, "Debe generar primero el Certificado para completar tarea.", "");
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RealizarCertificadoFichaRegistral.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getLindero() {
        return lindero;
    }

    public void setLindero(String lindero) {
        this.lindero = lindero;
    }

    public Long getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(Long numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public RegFicha getRegFicha() {
        return regFicha;
    }

    public void setRegFicha(RegFicha regFicha) {
        this.regFicha = regFicha;
    }

    public RegFicha getRegFichaTable() {
        return regFichaTable;
    }

    public void setRegFichaTable(RegFicha regFichaTable) {
        this.regFichaTable = regFichaTable;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Long getNumeroPredio() {
        return numeroPredio;
    }

    public void setNumeroPredio(Long numeroPredio) {
        this.numeroPredio = numeroPredio;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public List<CatPredioPropietario> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<CatPredioPropietario> propietarios) {
        this.propietarios = propietarios;
    }

    public Boolean getGuardar() {
        return guardar;
    }

    public void setGuardar(Boolean guardar) {
        this.guardar = guardar;
    }

    public Long getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Long idTarea) {
        this.idTarea = idTarea;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public List<RegMovimiento> getMovimientosFicha() {
        return movimientosFicha;
    }

    public void setMovimientosFicha(List<RegMovimiento> movimientosFicha) {
        this.movimientosFicha = movimientosFicha;
    }

}
