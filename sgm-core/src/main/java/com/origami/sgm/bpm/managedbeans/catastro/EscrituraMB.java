/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.lazymodels.CatEscrituraLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.Faces;
import util.JsfUti;
/**
 *
 * @author Joao Sanga
 */
@Named(value = "escrituraMB")
@ViewScoped
public class EscrituraMB  implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private Entitymanager manager;
    
    private CatEscritura escritura;
    private CatEscrituraLazy escrituras = new CatEscrituraLazy();
    private CatPredioLazy predios;
    private CatPredio predio = new CatPredio();
    private List<CatEscritura> escriturasConsulta;
    private List<CatCanton> cantones;
    private CatCanton canton = new CatCanton();;
    
    private Map<String, Object> paramt;
    private Boolean controlAddUp;
    
    @PostConstruct
    protected void load() {
        
        escritura = new CatEscritura();
        predios = new CatPredioLazy("A");
        escrituras = new CatEscrituraLazy();
        cantones = manager.findAllEntCopy(CatCanton.class);
        escriturasConsulta = new ArrayList<>();
    }
    
    public void saveEscritura(){
            Boolean controlEstado = false;
            
            if(this.predio.getId() != null && this.canton != null && this.escritura.getFecEscritura() != null
                    && this.escritura.getFecInscripcion()!= null && this.escritura.getNumRepertorio()!= null
                    && this.escritura.getNotaria()!= null && this.escritura.getAreaSolar()!= null){
                paramt = new HashMap<>();
                paramt.put("predio", predio);
                escriturasConsulta =  manager.findAll(Querys.getEscriturasByPredio, new String[]{"predio"},  new Object[]{predio});
                
                if((escriturasConsulta.isEmpty() || escriturasConsulta == null) && escritura.getEstado().equals("I") ){
                    JsfUti.messageInfo(null, "La primera escritura para el predio no debe estar Inactiva", "");
                    return;
                }
                
                if (escritura.getEstado().equals("A") && !escriturasConsulta.isEmpty() && escriturasConsulta != null ) {
                    for (CatEscritura escr : escriturasConsulta) {
                        escr.setEstado("I");
                        manager.update(escr);
                    }
                }else
                    if(escritura.getEstado().equals("I") && !escriturasConsulta.isEmpty() && escriturasConsulta != null){
                        for (CatEscritura escr : escriturasConsulta) {
                            if(escr.getEstado().equals("A")){
                                controlEstado = true;
                            };
                        }
                        if(controlEstado == true){
                            JsfUti.messageInfo(null, "Todas las escrituras no pueden tener Estado Inactivo", "");
                            return;
                        }
                }
                escritura.setPredio(predio);
                escritura.setCanton(canton);
                if(controlAddUp == true )
                    manager.saveAll(this.escritura);
                if(controlAddUp == false )
                    manager.persist(escritura);
                
                JsfUti.messageInfo(null, "Exito", "Datos grabados Satisfactoriamente");
                this.predio = null;
                this.canton = null;
                this.escritura = null;
                escriturasConsulta = new ArrayList<> ();
            }else
                 JsfUti.messageInfo(null, "Debe Registrar todos los campos Obligatorios", "");
    }
    
    
    
    public void updateEscritura(CatEscritura escr){
        controlAddUp = false;
        this.predio = new CatPredio();
        this.canton = new CatCanton();
        this.escritura = new CatEscritura();
        this.predio = escr.getPredio();
        this.canton = escr.getCanton();
        this.escritura = escr;
    }
    
    public void deleteEscritura(CatEscritura escr){
        try{
            manager.delete(escr);
            JsfUti.update("frmEscrituras");
        }catch(Exception e){
            Faces.messageWarning(null, "Error al borrar la escritura seleccionada" , " ");
        }
    }

    public void loadPredio(){
        controlAddUp = true;
        predios = new CatPredioLazy("A");
        this.predio = new CatPredio();
        this.canton = new CatCanton();
        this.escritura = new CatEscritura();
     //   JsfUti.executeJS("PF.('dlgPrediosConsulta').show()");
    }
    
    public void seleccionarPredio(CatPredio p) {
        if (p != null) {
            this.predio = p;
            } else {
                Faces.messageWarning(null, "Advertencia!", "Debe Seleccionar un Predio");
            }
        
    }
    
    public CatEscrituraLazy getEscrituras() {
        return escrituras;
    }

    public void setEscrituras(CatEscrituraLazy escrituras) {
        this.escrituras = escrituras;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }
    
    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }
    
    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public List<CatCanton>  getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }
   
    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public Boolean getControlAddUp() {
        return controlAddUp;
    }

    public void setControlAddUp(Boolean controlAddUp) {
        this.controlAddUp = controlAddUp;
    }
    
    

    
}
