/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenTipoEntidadBancaria;
import com.origami.sgm.lazymodels.RenEntidadBancariaLazy;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.MessagesRentas;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class TarjetaCreditos implements Serializable {
    
    private static final Long serialVersionUID = 1L;

    private RenEntidadBancariaLazy lazyBancos;

    private List<RenEntidadBancaria> bancosList;
    private RenEntidadBancaria banco;
    private Boolean nuevo = false;
    private String headerDLG;
    private final Long tipo = 2L;

    @javax.inject.Inject
    private RentasServices services;

    @PostConstruct
    public void initView() {
        lazyBancos = new RenEntidadBancariaLazy(tipo);
    }

    public void editar(RenEntidadBancaria ban) {
//        bancosList = services.getBancos(1L);
        banco = new RenEntidadBancaria();
        banco = ban;
        nuevo = false;
        headerDLG = "Editar Tarjeta Crédito";
        JsfUti.update("frmDlg");
        JsfUti.executeJS("PF('dlgTarjCred').show()");
    }

    public void nuevo() {
//        bancosList = services.getBancos(1L);
        banco = new RenEntidadBancaria();
        banco.setEstado(true);
        nuevo = true;
        headerDLG = "Ingreso de Trajeta de Crédito";
        JsfUti.update("frmDlg");
        JsfUti.executeJS("PF('dlgTarjCred').show()");
    }

    public void guardar() {
        if (banco.getDescripcion() == null) {
            JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.faltaNombreInst);
            return;
        }
        Long existeBanco = services.existeRenEntidadBancaria(banco.getDescripcion());
        if (existeBanco != null) {
            if (!nuevo) {
                if (banco.getId().compareTo(existeBanco) == 0) {

                } else {
                    JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.existeInst);
                    return;
                }
            } else {
                JsfUti.messageError(null, MessagesRentas.advert, MessagesRentas.existeInst);
                return;
            }
        }
        banco.setTipo(new RenTipoEntidadBancaria(tipo));
        banco = services.guardarBanco(banco);

        if (banco != null) {
            if (nuevo) {
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.institucionGuarda);
            }else{
                JsfUti.messageInfo(null, MessagesRentas.info, MessagesRentas.institucionModificada);
            }
            JsfUti.executeJS("PF('dlgTarjCred').hide()");
        } else {
            JsfUti.messageInfo(null, MessagesRentas.advert, MessagesRentas.institucionGuarda);
        }
        lazyBancos = new RenEntidadBancariaLazy(tipo);
    }

    public RenEntidadBancariaLazy getLazyBancos() {
        return lazyBancos;
    }

    public void setLazyBancos(RenEntidadBancariaLazy lazyBancos) {
        this.lazyBancos = lazyBancos;
    }

    public RenEntidadBancaria getBanco() {
        return banco;
    }

    public void setBanco(RenEntidadBancaria banco) {
        this.banco = banco;
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

    public TarjetaCreditos() {
    }

    public List<RenEntidadBancaria> getBancosList() {
        return bancosList;
    }

    public void setBancosList(List<RenEntidadBancaria> bancosList) {
        this.bancosList = bancosList;
    }

}
