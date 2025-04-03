package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.MessagesRentas;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class ResolucionesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private BaseLazyDataModel<FnResolucion> fnResoluciones;

    private FnResolucion resolucion, res;
    private Boolean nuevo = false;
    private String headerDLG;
    private String mensajeConfirmacion;
    private Date fechaMax;
    private final Long tipo = 1L;

    @Inject
    private UserSession session;

    @javax.inject.Inject
    private RentasServices rentasServices;

    @javax.inject.Inject
    private Entitymanager services;

    @PostConstruct
    public void initView() {
        fnResoluciones = new BaseLazyDataModel<>(FnResolucion.class, "fechaIngreso", "desc");
        fechaMax = new Date();
    }

    public void editar(FnResolucion ban) {
        resolucion = new FnResolucion();
        resolucion = ban;
        nuevo = false;
        headerDLG = "Editar Resolución";
        JsfUti.update("frmdlgBan");
        JsfUti.executeJS("PF('dlgBanco').show()");
    }

    public void nuevo() {
        Integer anioDesv = Utils.getAnio(new Date());
        if (true) {
            resolucion = new FnResolucion();
            resolucion.setEstado(true);
            resolucion.setFechaIngreso(new Date());
            nuevo = true;
            headerDLG = "Ingreso Resolución";
            JsfUti.update("frmdlgBan");
            JsfUti.executeJS("PF('dlgBanco').show()");
        } else {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.existeDesvalorizacion.concat("en curso"));
        }
    }

    public void guardar() {

        if (resolucion.getNumeroResolucion() == null) {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.faltaIngresar.concat("el número de resolución."));
            return;
        }
        if (resolucion.getFechaResolucion() == null) {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.faltaIngresar.concat("fecha de resolución."));
            return;
        }
        if (resolucion.getFechaResolucion().after(new Date())) {
            JsfUti.messageError(null, MessagesRentas.advert, "Fecha de resolución no puede ser mayor a la fecha actual.");
            return;
        }
        resolucion.setUsuarioCreacion(session.getName_user());
        resolucion = rentasServices.guardarResolucion(resolucion);

        if (resolucion != null) {
            if (nuevo) {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.guardadoCorrecto);
            } else {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.modificacionCorrecta);
            }
            JsfUti.executeJS("PF('dlgBanco').hide()");
        } else {
            JsfUti.messageInfo(null, MessagesRentas.advert, Messages.errorTransaccion);
        }
        fnResoluciones = new BaseLazyDataModel<>(FnResolucion.class, "fechaIngreso", "desc");
    }

    public void accion(FnResolucion ban) {
        if (ban.getEstado()) {
            mensajeConfirmacion = "Está seguro de que desea dar de baja la resolución?";
        } else {
            mensajeConfirmacion = "Está seguro de que desea reactivar la resolución?";
        }
        if (ban != null) {
            res = ban;
            JsfUti.executeJS("PF('dlgMensaje').show()");
            JsfUti.update("mensajeDlg:frmMensaje");
        }
    }

    public void metodoSel() {
        if (res.getEstado()) {
            anular();
        } else {
            reactivar();
        }
    }

    public void anular() {
        try {
            res.setEstado(false);
            services.update(res);
            JsfUti.messageInfo(null, "Info", "Resolución actualizada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reactivar() {
        try {
            res.setEstado(true);
            services.update(res);
            JsfUti.messageInfo(null, "Info", "Resolución actualizada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResolucionesView() {
    }

    public BaseLazyDataModel<FnResolucion> getFnResoluciones() {
        return fnResoluciones;
    }

    public void setFnResoluciones(BaseLazyDataModel<FnResolucion> fnResoluciones) {
        this.fnResoluciones = fnResoluciones;
    }

    public FnResolucion getResolucion() {
        return resolucion;
    }

    public void setResolucion(FnResolucion resolucion) {
        this.resolucion = resolucion;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public String getHeaderDLG() {
        return headerDLG;
    }

    public void setHeaderDLG(String headerDLG) {
        this.headerDLG = headerDLG;
    }

    public Date getFechaMax() {
        return fechaMax;
    }

    public void setFechaMax(Date fechaMax) {
        this.fechaMax = fechaMax;
    }

    public String getMensajeConfirmacion() {
        return mensajeConfirmacion;
    }

    public void setMensajeConfirmacion(String mensajeConfirmacion) {
        this.mensajeConfirmacion = mensajeConfirmacion;
    }

}
