/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.lazymodels.RegMovimientosLazy;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class RevisarFolios implements Serializable{

    /**
     * Creates a new instance of RevisarFolios
     */
    
    @javax.inject.Inject
    private InscripcionNuevaServices ejbIns;
    
    protected RegMovimientosLazy foliosLazy;
    protected RegMovimiento movimiento;
    protected List<RegLibro> regLibroList;
    protected RegLibro libro;
    protected Date inscripcionDesde;
    protected Date inscripcionHasta;
    
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        try {
            regLibroList=ejbIns.getRegLibroList();
        } catch (Exception e) {
            Logger.getLogger(RevisarFolios.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void consultarFolios(){
        try{
            foliosLazy=null;
            if(libro!=null && inscripcionDesde!=null && inscripcionHasta!=null){
                if (!inscripcionDesde.after(inscripcionHasta)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date limite = sdf.parse("01-01-2016");
                    //ESTA FECHA ESTA QUEMADA EN EL CODIGO POR QUE
                    //DESDE AQUI LAS INSCRIPCIONES SE HICIERON EN SGM
                    if (!limite.after(inscripcionHasta)) {
                        limite = Utils.sumarRestarDiasFecha(inscripcionHasta, 1);
                    }else{
                        limite= inscripcionHasta;
                    }
                    foliosLazy= new RegMovimientosLazy(libro, inscripcionDesde, limite);
                } else {
                    JsfUti.messageWarning(null, "Fecha Hasta debe ser mayor o igual a Fecha Desde.", "");
                }
            }else{
                JsfUti.messageWarning(null, "Libro y Fechas campos obligatorios para la busqueda.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(RevisarFolios.class.getName()).log(Level.SEVERE, null, e);
        }
        
    }
    
    public void showDlgFolioSelect(RegMovimiento mov){
        try{
            movimiento=mov;
            JsfUti.executeJS("PF('dlgMovRegSelec').show();");
        } catch(Exception e){
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(RevisarFolios.class.getName()).log(Level.SEVERE,null,e);
        }
    }

    public RegMovimientosLazy getFoliosLazy() {
        return foliosLazy;
    }

    public void setFoliosLazy(RegMovimientosLazy foliosLazy) {
        this.foliosLazy = foliosLazy;
    }

    public List<RegLibro> getRegLibroList() {
        return regLibroList;
    }

    public void setRegLibroList(List<RegLibro> regLibroList) {
        this.regLibroList = regLibroList;
    }

    public RegLibro getLibro() {
        return libro;
    }

    public void setLibro(RegLibro libro) {
        this.libro = libro;
    }

    public Date getInscripcionDesde() {
        return inscripcionDesde;
    }

    public void setInscripcionDesde(Date inscripcionDesde) {
        this.inscripcionDesde = inscripcionDesde;
    }

    public Date getInscripcionHasta() {
        return inscripcionHasta;
    }

    public void setInscripcionHasta(Date inscripcionHasta) {
        this.inscripcionHasta = inscripcionHasta;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }
    
}
