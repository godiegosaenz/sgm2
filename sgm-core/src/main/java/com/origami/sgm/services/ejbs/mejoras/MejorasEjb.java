/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.mejoras;

import com.origami.sgm.entities.CatUbicacion;
import com.origami.sgm.entities.MejObra;
import com.origami.sgm.entities.MejObraUbicacion;
import com.origami.sgm.entities.MejValoresObraUbicacion;
import com.origami.sgm.managedbeans.mejoras.Obras;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.ejbs.catastro.CatastroEjb;
import com.origami.sgm.services.interfaces.mejoras.MejorasServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import util.JsfUti;

/**
 *
 * @author andysanchez
 */
@Stateless(name = "mejorasEjb")
@Interceptors(value = {HibernateEjbInterceptor.class})
@Dependent
public class MejorasEjb implements MejorasServices {

    @Inject
    private Entitymanager manager;

    @Override
    public MejObra saveObra(MejObra obra, List<CatUbicacion> catUbicaciones) {
        try {
            obra = (MejObra) manager.persist(obra);
            ///GURDAS LA SMEJORAS SEGUN LAS UBICAACIONES COMO PARROQUIAS, SECTORE, ZONAAS, MZ, E INCLUSO SOLAR ES UNA RELACION DE UNO SA MUCHOS
            MejObraUbicacion mejObraUbicacion = null;
            for (CatUbicacion ubicacion : catUbicaciones) {
                mejObraUbicacion = new MejObraUbicacion();
                mejObraUbicacion.setUbicacion(ubicacion);
                mejObraUbicacion.setMejora(obra);
                manager.persist(mejObraUbicacion);
            }

            if (this.saveMejValoresObraUbicacion(obra, catUbicaciones) != null) {
                return obra;
            } else {
                return null;
            }

        } catch (Exception e) {
            Logger.getLogger(MejorasEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public List<MejValoresObraUbicacion> saveMejValoresObraUbicacion(MejObra obra, List<CatUbicacion> catUbicaciones) {
        try {
            MejValoresObraUbicacion ubicacion;
            obra.setValoresObraUbicacionsCollection(new ArrayList<>());
            for (CatUbicacion u : catUbicaciones) {
                ubicacion = new MejValoresObraUbicacion();
                ubicacion.setUbicacion(u);
                ubicacion.setObra(obra);
                ubicacion.setPorcentaje(obra.getSubsidio());
                ubicacion.setValorRecuperar(obra.getValorRecuperar());
                ubicacion.setTotalCemAnio(u.getTotalCemAnio());
                obra.getValoresObraUbicacionsCollection().add(ubicacion);
            }
            List<MejValoresObraUbicacion> listObraUbicacion = (List<MejValoresObraUbicacion>) obra.getValoresObraUbicacionsCollection();
            if (listObraUbicacion != null) {
                for (MejValoresObraUbicacion item : listObraUbicacion) {
                    System.out.println("item" + item);
                    manager.persist(item);
                }
            }
            return listObraUbicacion;
        } catch (Exception e) {
            Logger.getLogger(MejorasEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }

    }

    @Override
    public List<CatUbicacion> saveUbicaciones(List<CatUbicacion> catUbicaciones) {
        List<CatUbicacion> listUbicaciones = null;
        CatUbicacion ubicacion = null;
        try {
            listUbicaciones = new ArrayList<>();
            for (CatUbicacion u : catUbicaciones) {
                ubicacion = new CatUbicacion();
                ubicacion = (CatUbicacion) manager.persist(u);
                ubicacion.setAvaluoMunicipal(u.getAvaluoMunicipal());
                ubicacion.setPorcentajeAvaluo(u.getPorcentajeAvaluo());
                ubicacion.setTotalCem(u.getTotalCem());
                ubicacion.setTotalCemAnio(u.getTotalCemAnio());
                listUbicaciones.add(ubicacion);
            }
            return listUbicaciones;
        } catch (Exception e) {
            Logger.getLogger(MejorasEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

}
