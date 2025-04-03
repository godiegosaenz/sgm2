/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 29/07/2016
 */
@Named
@ViewScoped
public class GenerarSolicitudesAnuales implements Serializable {

    // Ejbs 
    @javax.inject.Inject
    private RentasServices services;

    // Sessions
    @Inject
    private UserSession session;

    // Variables de instancia
    List<FnSolicitudExoneracion> solicitudesAprob;
    List<FnSolicitudExoneracion> solicitudNoGenerada;

    /**
     * Inicio de parametos para la generacion de las solicitudes que deben
     * iniciarse manualmente
     */
    @PostConstruct
    protected void initView() {
        List<FnExoneracionTipo> tipos = services.FnExoneracionesTipoByAplica(Arrays.asList("T", "C", "D"));
        solicitudesAprob = services.getSolicitudesAutomaticas(Utils.getAnio(new Date()) - 1, tipos);
//        System.out.println("inicio " + solicitudesAprob.size());
    }

    public Integer getSizeList() {
        if (solicitudesAprob != null) {
            return solicitudesAprob.size();
        } else {
            return 0;
        }
    }

    /**
     * Generar las solicitudes anuales
     */
    public void generarSolicitudes() {
        System.out.println("Numero de Solicitudes a generar (" + solicitudesAprob.size() + ")");
        solicitudNoGenerada = new ArrayList<>();
        int countGeneradas = 0;
        int countNoGenerada = 0;
        for (FnSolicitudExoneracion solicitud : solicitudesAprob) {
            if (solicitud.getSolicitante() != null) {
                Boolean propietariIgualSolicitante = services.verificarSolicitanteSolicutud(solicitud);
                System.out.print("Consultando Solicitante de Solicitud " + solicitud.getId());
                if (propietariIgualSolicitante) {
                    //Boolean clonada = services.generarExoneracionAuto(solicitud, session.getName_user());
                    Boolean clonada = true;
                    if (clonada) {
                        System.out.println(" Nueva Solicitud Generada");
                        countGeneradas++;
                    } else {
                        System.out.println(" no se pudo generar Solicitud Nueva");
                        countNoGenerada++;
                    }
                } else {
                    System.out.println(" Solicitante ya no es dueÃ±o del predio id Solicitud (" + solicitud.getId() + ")");
                    solicitudNoGenerada.add(solicitud);
                }
            } else {
                System.out.println(" Solicitud no tiene un Solicitante por favor verificar" + solicitud);
            }
        }
        System.out.println("Solicitudes generadas (" + countGeneradas + ")"
                + ", No generadas (" + countNoGenerada + "), " + "con dueÃ±o diferentes (" + solicitudNoGenerada.size() + ")");
        JsfUti.redirectFaces("/vistaprocesos/financiero/generarSolicitudesAnuales.xhtml");
    }
    
    public void verInformacion(FnSolicitudExoneracion exoneracion){
        //solicitudes = (List<FnSolicitudExoneracionAutomatica>) exoneracion.getFnSolicitudExoneracionAutomaticaCollection();
        JsfUti.executeJS("PF('detalle').show()");
        JsfUti.update("detalle");
    }

    public List<FnSolicitudExoneracion> getSolicitudesAprob() {
        return solicitudesAprob;
    }

    public void setSolicitudesAprob(List<FnSolicitudExoneracion> solicitudesAprob) {
        this.solicitudesAprob = solicitudesAprob;
    }

}
