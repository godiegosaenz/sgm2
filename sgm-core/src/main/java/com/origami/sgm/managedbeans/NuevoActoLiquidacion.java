/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.entities.RegpActoTipoActo;
import com.origami.sgm.entities.RegpActoTipoCobro;
import com.origami.sgm.entities.RegpActoTipoTarea;
import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
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
import util.JsfUti;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class NuevoActoLiquidacion implements Serializable {

    @javax.inject.Inject
    private Entitymanager acl;

    @Inject
    protected UserSession session;
    
    @javax.inject.Inject
    protected RegistroPropiedadServices reg;
    
    protected String codigo;
    protected String nombre = "";
    protected RegpActosIngreso acto;
    protected Boolean flag = false;
    protected RegpActoTipoActo tipoActo;
    protected RegpActoTipoCobro tipoCobro;
    protected RegpActoTipoTarea tipoTarea;
    protected List<RegpActoTipoActo> listTipoActo = new ArrayList<>();
    protected List<RegpActoTipoCobro> listTipoCobro = new ArrayList<>();
    protected List<RegpActoTipoTarea> listTipoTarea = new ArrayList<>();
    
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    public void iniView() {
        try {
            if (codigo == null) {
                acto = new RegpActosIngreso();
            } else {
                acto = (RegpActosIngreso) acl.find(RegpActosIngreso.class, new Long(codigo));
                nombre = acto.getNombre();
                tipoActo = acto.getTipoActo();
                tipoCobro = acto.getTipoCobro();
                tipoTarea = acto.getTipoTarea();
            }
            listTipoActo = acl.findAllEntCopy(RegpActoTipoActo.class);
            listTipoCobro = acl.findAllEntCopy(RegpActoTipoCobro.class);
            listTipoTarea = acl.findAllEntCopy(RegpActoTipoTarea.class);
        } catch (Exception e) {
            Logger.getLogger(NuevoActoLiquidacion.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void guardar(){
        try {
            if(this.validar()){
                Boolean temp;
                acto.setNombre(nombre);
                acto.setTipoActo(tipoActo);
                acto.setTipoCobro(tipoCobro);
                acto.setTipoTarea(tipoTarea);
                if(acto.getId() == null){
                    acto.setUserCre(session.getName_user());
                    acto.setFechaCre(new Date());
                    temp = reg.guardarActoLiquidacion(acto);
                } else {
                    acto.setUserEdicion(session.getName_user());
                    acto.setFechaEdicion(new Date());
                    temp = reg.actualizarActoLiquidacion(acto, flag);
                }
                if(temp){
                    JsfUti.redirectFaces("/admin/registro/actosLiquidacion.xhtml");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(NuevoActoLiquidacion.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public Boolean validar(){
        if(!nombre.equals(acto.getNombre())){
            flag = true;
        }
        
        if(nombre == null){
            JsfUti.messageInfo(null, "Debe llenar el campo Nombre.", "");
        } else if(tipoActo == null){
            JsfUti.messageInfo(null, "Debe seleccionar el Tipo de Acto.", "");
        } else if(tipoCobro == null){
            JsfUti.messageInfo(null, "Debe seleccionar el Tipo de Cobro.", "");
        } else if(tipoTarea == null){
            JsfUti.messageInfo(null, "Debe seleccionar el Tipo de Tarea.", "");
        } else if(acto.getValor() == null){
            JsfUti.messageInfo(null, "Debe ingresar el Valor del Acto.", "");
        } else {
            return true;
        }
        return false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RegpActosIngreso getActo() {
        return acto;
    }

    public void setActo(RegpActosIngreso acto) {
        this.acto = acto;
    }

    public RegpActoTipoActo getTipoActo() {
        return tipoActo;
    }

    public void setTipoActo(RegpActoTipoActo tipoActo) {
        this.tipoActo = tipoActo;
    }

    public RegpActoTipoCobro getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(RegpActoTipoCobro tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public RegpActoTipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(RegpActoTipoTarea tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public List<RegpActoTipoActo> getListTipoActo() {
        return listTipoActo;
    }

    public void setListTipoActo(List<RegpActoTipoActo> listTipoActo) {
        this.listTipoActo = listTipoActo;
    }

    public List<RegpActoTipoCobro> getListTipoCobro() {
        return listTipoCobro;
    }

    public void setListTipoCobro(List<RegpActoTipoCobro> listTipoCobro) {
        this.listTipoCobro = listTipoCobro;
    }

    public List<RegpActoTipoTarea> getListTipoTarea() {
        return listTipoTarea;
    }

    public void setListTipoTarea(List<RegpActoTipoTarea> listTipoTarea) {
        this.listTipoTarea = listTipoTarea;
    }

}
