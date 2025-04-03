/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoBienCaracteristica;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class TipoBienes implements Serializable {

    @javax.inject.Inject
    protected RegistroPropiedadServices services;
    protected List<RegTipoBien> listBien = new ArrayList<>();
    protected List<RegTipoBienCaracteristica> CaractBien = new ArrayList<>();
    protected RegTipoBienCaracteristica agragarCaract;
    protected RegTipoBien tipoBien = new RegTipoBien();
    protected Boolean edit = false;
    protected Boolean editBien = false;

    @PostConstruct
    public void initView() {
        listBien = services.getRegTipoBienList();
    }

    public TipoBienes() {
    }

    public void editarBien(RegTipoBien bien) {
        tipoBien = new RegTipoBien();
        tipoBien = bien;
        editBien = true;
        JsfUti.executeJS("PF('EditBien').show()");
        JsfUti.update("formEditTipoBien");
    }

    public void nuevaCaracteristicaBien() {
        agragarCaract = new RegTipoBienCaracteristica();
        if (editBien) {
            JsfUti.update("formNueCaractBien");
            JsfUti.executeJS("PF('EditBien').hide();");
            JsfUti.executeJS("PF('agregarCaractBien').show();");
        } else {
            JsfUti.update("formNueCaractBien");
            JsfUti.executeJS("PF('nuevoBien').hide();");
            JsfUti.executeJS("PF('agregarCaractBien').show();");
        }
    }

    public void agregarCaracteristicaBien() {
        if (editBien) {
            tipoBien.getRegTipoBienCaracteristicaCollection().add(agragarCaract);
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('EditBien').show();");
            JsfUti.update("formEditTipoBien");
        } else {
            CaractBien.add(agragarCaract);
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('nuevoBien').show();");
            JsfUti.update("formNewTipoBien");
        }
    }

    public void EditCaract(RegTipoBienCaracteristica caract) {
        agragarCaract = new RegTipoBienCaracteristica();
        agragarCaract = caract;
        edit = true;
        JsfUti.update("formNueCaractBien");
        JsfUti.executeJS("PF('agregarCaractBien').show();");
    }

    public void cancelarCaract() {
        if (editBien) {
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('EditBien').show();");
        } else {
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('nuevoBien').show();");
        }
    }

    public void guardarCaract() {
        boolean prin = false;
        for (RegTipoBienCaracteristica lst : CaractBien) {
            if (lst.getIsMain()) {
                prin = true;
            }
        }
        if (!prin) {
            JsfUti.messageInfo(null, "Debe escoger una Característica como PRINCIPAL.", "");
            return;
        }
        tipoBien.setRegTipoBienCaracteristicaCollection(CaractBien);
        tipoBien = services.guardarRegTipoBienAndRegTipoBienCaracteristica(tipoBien);
        if (tipoBien != null) {
            JsfUti.redirectFaces("/faces/admin/registro/tipoBienesIngresados.xhtml");
        }
    }

    public void guardarCaractEdit() {
        boolean prin = false;
        for (RegTipoBienCaracteristica lst : tipoBien.getRegTipoBienCaracteristicaCollection()) {
            if (lst.getIsMain()) {
                prin = true;
            }
        }
        if (!prin) {
            JsfUti.messageInfo(null, "Debe escoger una Característica como PRINCIPAL.", "");
            return;
        }
        Boolean ok = services.updateRegTipoBienAndRegTipoBienCaracteristica(tipoBien);
        if (ok) {
            JsfUti.redirectFaces("/faces/admin/registro/tipoBienesIngresados.xhtml");
        }
    }

    public void agregarCaractEdit() {
        if (editBien) {
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('EditBien').show();");
            JsfUti.update("formEditTipoBien");
        } else {
            JsfUti.executeJS("PF('agregarCaractBien').hide();");
            JsfUti.executeJS("PF('nuevoBien').show();");
            JsfUti.update("formNewTipoBien");
        }
    }

    public List<RegTipoBien> getListBien() {
        return listBien;
    }

    public void setListBien(List<RegTipoBien> listBien) {
        this.listBien = listBien;
    }

    public List<RegTipoBienCaracteristica> getCaractBien() {
        return CaractBien;
    }

    public void setCaractBien(List<RegTipoBienCaracteristica> CaractBien) {
        this.CaractBien = CaractBien;
    }

    public RegTipoBien getTipoBien() {
        return tipoBien;
    }

    public void setTipoBien(RegTipoBien tipoBien) {
        this.tipoBien = tipoBien;
    }

    public RegTipoBienCaracteristica getAgragarCaract() {
        return agragarCaract;
    }

    public void setAgragarCaract(RegTipoBienCaracteristica agragarCaract) {
        this.agragarCaract = agragarCaract;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public Boolean getEditBien() {
        return editBien;
    }

    public void setEditBien(Boolean editBien) {
        this.editBien = editBien;
    }

}
