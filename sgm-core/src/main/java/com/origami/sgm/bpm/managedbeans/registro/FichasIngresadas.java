/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegRegistrador;
import com.origami.sgm.lazymodels.RegFichaLazy;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Angel Navarro, CarlosLoorVargas
 */
@Named
@ViewScoped
public class FichasIngresadas implements Serializable {

    private static final long serialVersionUID = 1L;

    protected RegRegistrador registrador;
    protected RegFichaLazy listadoFichas;
    protected RegFicha fichaSel = new RegFicha();
    protected List<RegMovimiento> movimientosFicha = new ArrayList<>();
    protected Boolean supRegistral = false;

    @Inject
    private ServletSession servletSession;

    @Inject
    private UserSession session;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices service;

    @javax.inject.Inject
    private Entitymanager services;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            listadoFichas = new RegFichaLazy(1L);
            registrador = (RegRegistrador) services.find(Querys.getRegRegistrador);
            this.validaRoles();
        } catch (Exception e) {
            Logger.getLogger(FichasIngresadas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validaRoles() {
        if (session != null) {
            if (session.getRoles() != null && !session.getRoles().isEmpty()) {
                for (Long rl : session.getRoles()) {
                    if (rl.equals(195L)) {
                        supRegistral = true;
                        break;
                    }
                }
            }
        }
    }

    public void imprimirFichaRegistral(RegFicha ficha, String reporte) {
        if (ficha.getEstado().getValor().equalsIgnoreCase("INACTIVO")) {
            JsfUti.messageError(null, "No se imprimir Ficha Registral, estado de Ficha: INACTIVA.", "");
        } else {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setNombreReporte(reporte);
            servletSession.agregarParametro("ID_FICHA", ficha.getId());
            servletSession.agregarParametro("registrador", registrador.getNombreCompleto());
            servletSession.agregarParametro("tituloregistrador", registrador.getTituloCompleto());
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
            servletSession.agregarParametro("USER_NAME", session.getName_user());
            servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.setEncuadernacion(Boolean.TRUE);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        }
    }

    public void showDlgFichaSelect(RegFicha f) {
        try {
            fichaSel = f;
            fichaSel.setDescripcionTemp(f.getObsvEstado(f.getEstado()));
            movimientosFicha = service.getMovimientosByFicha(f.getId());
            if (fichaSel.getTipoPredio() != null) {
                if (fichaSel.getTipoPredio().equalsIgnoreCase("U")) {
                    fichaSel.setTipoPredioTemp("Urbano");
                } else if (fichaSel.getTipoPredio().equalsIgnoreCase("R")) {
                    fichaSel.setTipoPredioTemp("Rural");
                } else if (fichaSel.getTipoPredio().equalsIgnoreCase("I")) {
                    fichaSel.setTipoPredioTemp("IIIIIIIIII");
                }
            }
            JsfUti.update("formFichaSelect");
            JsfUti.executeJS("PF('dlgFichaSelect').show();");
        } catch (Exception e) {
            Logger.getLogger(FichasIngresadas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgLiberaFicha(RegFicha f) {
        if (f.getPredio() != null) {
            fichaSel = f;
            Faces.executeJS("PF('dlgLiberarFicha').show()");
        } else {
            Faces.messageWarning(null, "Advertencia", "Para realizar esta accion, la ficha registral debe tener un predio asociado");
        }
    }

    public void liberarFicha() {
        try {
            if (fichaSel != null && fichaSel.getPredio() != null) {
                fichaSel.setAlicuotaEscritura(null);
                fichaSel.setAreaEscritura(null);
                fichaSel.setPredio(null);
                fichaSel.setUserEdicion(session.getName_user());
                fichaSel.setFechaEdicion(new Date());
                if (services.persist(fichaSel) != null) {
                    Faces.messageInfo(null, "Nota!", "Ficha liberada Satisfactoriamente");
                } else {
                    Faces.messageWarning(null, "Advertencia", "No se pudo liberar la ficha");
                }
            }
        } catch (Exception e) {
            Faces.messageError(null, Messages.error, e.getMessage());
            Logger.getLogger(FichasIngresadas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirBitacora() {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.setNombreReporte("bitacoraSgm");
            servletSession.agregarParametro("codMovimiento", null);
            servletSession.agregarParametro("numFicha", fichaSel.getNumFicha());
            servletSession.agregarParametro("titulo", Messages.bitacoraFicha);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, e.getMessage());
            Logger.getLogger(FichasIngresadas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void redirectFichaNueva() {
        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/fichaIngresoNuevo.xhtml");
    }

    public void redirectEditarFicha(RegFicha ficha) {
        JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/fichaIngresoEditar.xhtml?idficha=" + ficha.getId());
    }

    public RegFichaLazy getListadoFichas() {
        return listadoFichas;
    }

    public void setListadoFichas(RegFichaLazy listadoFichas) {
        this.listadoFichas = listadoFichas;
    }

    public RegFicha getFichaSel() {
        return fichaSel;
    }

    public void setFichaSel(RegFicha fichaSel) {
        this.fichaSel = fichaSel;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public List<RegMovimiento> getMovimientosFicha() {
        return movimientosFicha;
    }

    public void setMovimientosFicha(List<RegMovimiento> movimientosFicha) {
        this.movimientosFicha = movimientosFicha;
    }

    public Boolean getSupRegistral() {
        return supRegistral;
    }

    public void setSupRegistral(Boolean supRegistral) {
        this.supRegistral = supRegistral;
    }

}
