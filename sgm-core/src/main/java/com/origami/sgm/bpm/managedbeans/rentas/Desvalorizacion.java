package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
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
public class Desvalorizacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private BaseLazyDataModel<RenDesvalorizacion> desvalorizaciones;

    private RenDesvalorizacion desvalorizacion;
    private Boolean nuevo = false;
    private String headerDLG;
    private final Long tipo = 1L;
    
    @Inject
    private UserSession session;

    @javax.inject.Inject
    private RentasServices services;

    @PostConstruct
    public void initView() {
        desvalorizaciones = new BaseLazyDataModel<>(RenDesvalorizacion.class, "anio", "desc");
    }

    public void editar(RenDesvalorizacion ban) {
        desvalorizacion = new RenDesvalorizacion();
        desvalorizacion = ban;
        nuevo = false;
        headerDLG = "Editar Valor de Desvalorización";
        JsfUti.update("frmdlgBan");
        JsfUti.executeJS("PF('dlgBanco').show()");
    }

    public void nuevo() {
        Integer anioDesv = Utils.getAnio(new Date());
        if (!checkDesvalorizacionAnio(anioDesv)) {
            desvalorizacion = new RenDesvalorizacion();
            desvalorizacion.setEstado(true);
            desvalorizacion.setAnio(anioDesv);
//            desvalorizacion.setValor(new BigDecimal("0.00"));
            nuevo = true;
            headerDLG = "Ingreso Valor de Desvalorización anual.";
            JsfUti.update("frmdlgBan");
            JsfUti.executeJS("PF('dlgBanco').show()");
        } else {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.existeDesvalorizacion.concat("en curso"));
        }
    }

    public void guardar() {

        if (desvalorizacion.getValor() == null) {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.faltaNombreInst);
            return;
        }
        if (nuevo) {
            if (checkDesvalorizacionAnio(desvalorizacion.getAnio())) {
                JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.existeDesvalorizacion.concat("ingresado"));
                return;
            }
        }
        desvalorizacion.setUsuarioCreac(session.getName_user());
        desvalorizacion.setFechaCreac(new Date());
        desvalorizacion = services.guardarDesvalorizacion(desvalorizacion);

        if (desvalorizacion != null) {
            if (nuevo) {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.guardadoCorrecto);
            } else {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.modificacionCorrecta);
            }
            JsfUti.executeJS("PF('dlgBanco').hide()");
        } else {
            JsfUti.messageInfo(null, MessagesRentas.advert, Messages.errorTransaccion);
        }
        desvalorizaciones = new BaseLazyDataModel<>(RenDesvalorizacion.class, "anio", "desc");
    }

    public Desvalorizacion() {
    }

    public BaseLazyDataModel<RenDesvalorizacion> getDesvalorizaciones() {
        return desvalorizaciones;
    }

    public void setDesvalorizaciones(BaseLazyDataModel<RenDesvalorizacion> desvalorizaciones) {
        this.desvalorizaciones = desvalorizaciones;
    }

    public RenDesvalorizacion getDesvalorizacion() {
        return desvalorizacion;
    }

    public void setDesvalorizacion(RenDesvalorizacion desvalorizacion) {
        this.desvalorizacion = desvalorizacion;
    }

    public String getHeaderDLG() {
        return headerDLG;
    }

    public void setHeaderDLG(String headerDLG) {
        this.headerDLG = headerDLG;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    private Boolean checkDesvalorizacionAnio(Integer anioDesv) {
        return services.getDesvalorizacionAnio(anioDesv) != null;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

}
