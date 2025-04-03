/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Henry
 */
@Named
@ViewScoped
public class InscripcionSecuencial extends BpmManageBeanBaseRoot implements Serializable{

    /**
     * Creates a new instance of InscripcionSecuencial
     */
    @javax.inject.Inject
    private Entitymanager serv;
    
    protected RegLibro libro;
    protected Long repertorio;
    protected Long inscripcion;
    protected Date fecha;
    protected Long secuencia;
    
    protected RegMovimiento movimiento;
    protected Integer secuenciaMax;
    protected RegMovimiento movimientoGenerado;
    
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

    private void initView() {
        try {
            
        } catch (Exception e) {
            Logger.getLogger(InscripcionSecuencial.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void buscarInscripcion(){
        try {
            if (libro!=null && repertorio!=null && inscripcion!=null && fecha!=null && secuencia!=null) {
                secuenciaMax=(Integer)serv.find(Querys.getRegMovimientoEspecificoMaximo, new String[]{"libro","repertorio","inscripcion","fecha"}, new Object[]{libro,repertorio,inscripcion,fecha});
                if(secuenciaMax!=null){
                    movimiento=(RegMovimiento)serv.find(Querys.getRegMovimientoEspecifico, new String[]{"libro","repertorio","inscripcion","fecha","secuencia"}, new Object[]{libro,repertorio,inscripcion,fecha,secuenciaMax});
                    JsfUti.update("formInfInscripcion");
                    JsfUti.executeJS("PF('infInscripcion').show();");
                }else{
                    JsfUti.messageInfo(null, "Inscripción no encontrada.", "");
                }
            }else{
                JsfUti.messageInfo(null, "Campos vacios.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(InscripcionSecuencial.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void generarInscripcion(){
        movimiento.setId(null);
        movimiento.setIndice(secuenciaMax+1);
        movimiento.setUserCreador(new AclUser(session.getUserId()));
        movimiento.setFechaIngreso(new Date());
        movimiento.setRegMovimientoCapitalCollection(null);
        movimiento.setRegMovimientoClienteCollection(null);
        movimiento.setRegMovimientoFichaCollection(null);
        movimiento.setRegMovimientoReferenciaCollection(null);
        movimiento.setRegMovimientoRepresentanteCollection(null);
        movimiento.setRegMovimientoSociosCollection(null);
        movimientoGenerado=(RegMovimiento)serv.persist(movimiento);
        if (movimientoGenerado!=null) {
            JsfUti.update("formInscripSec");
            JsfUti.executeJS("PF('infInscripcion').hide();");
            JsfUti.messageInfo(null, "Generación de movimiento exitoso.", "");
        }else{
            JsfUti.messageInfo(null, "Error al grabar realize el proceso nuevamente.", "");
        }
        
    }
    
    public void redirectEditarMovimiento(Long id) {
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "vistaprocesos/registroPropiedad/inscripcionEdicion.xhtml?idmov=" + id);
    }
    
    public List<RegLibro> getLibros(){
        return serv.findAll(Querys.getRegLibroList);
    }

    public RegLibro getLibro() {
        return libro;
    }

    public void setLibro(RegLibro libro) {
        this.libro = libro;
    }

    public Long getRepertorio() {
        return repertorio;
    }

    public void setRepertorio(Long repertorio) {
        this.repertorio = repertorio;
    }

    public Long getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Long inscripcion) {
        this.inscripcion = inscripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Long secuencia) {
        this.secuencia = secuencia;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public Integer getSecuenciaMax() {
        return secuenciaMax;
    }

    public void setSecuenciaMax(Integer secuenciaMax) {
        this.secuenciaMax = secuenciaMax;
    }

    public RegMovimiento getMovimientoGenerado() {
        return movimientoGenerado;
    }

    public void setMovimientoGenerado(RegMovimiento movimientoGenerado) {
        this.movimientoGenerado = movimientoGenerado;
    }
    
}
