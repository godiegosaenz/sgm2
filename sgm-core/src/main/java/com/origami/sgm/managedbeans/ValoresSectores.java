/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.avaluos.SectorValorizacion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class ValoresSectores implements Serializable {

    private BaseLazyDataModel<SectorValorizacion> lazy;
    private SectorValorizacion sector;

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession session;

    @PostConstruct
    public void initView() {
        lazy = new BaseLazyDataModel<>(SectorValorizacion.class, "sector", "desc");
    }

    public void editar() {
        if (sector == null) {
            JsfUti.messageError(null, "Sector", "No ha seleccionado ningun sector");
            return;
        }
        sector.setFecAct(new Date());
        sector.setUsrAct(session.getName_user());
        manager.persist(sector);
    }

    public void  eliminar(SectorValorizacion sv){
        manager.delete (sv);
    }

    public void nuevo() {
        if (sector.getSector() == null) {
            JsfUti.messageError(null, "Sector", "Debe Ingresar el Sector");
            return;
        }
        if (sector.getValorM2() == null) {
            JsfUti.messageError(null, "Valor por metros", "Debe ingresar el valor por metros");
            return;
        }
        if (sector.getServicios() == null) {
            JsfUti.messageError(null, "Servicios", "Debe ingresar los servicos del sector");
            return;
        }

        sector.setFecCre(new Date());
        sector.setUsrCre(session.getName_user());
        manager.persist(sector);
    }

    public void mostrar(SectorValorizacion sv) {
        if (sv == null) {
            sector = new SectorValorizacion();
        } else {
            sector = sv;
        }
    }

    public List<CtlgItem> getListado(String argumento) {
        return manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
    }

    public BaseLazyDataModel<SectorValorizacion> getLazy() {
        return lazy;
    }

    public void setLazy(BaseLazyDataModel<SectorValorizacion> lazy) {
        this.lazy = lazy;
    }

    public SectorValorizacion getSector() {
        return sector;
    }

    public void setSector(SectorValorizacion sector) {
        this.sector = sector;
    }

}
