/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.database.Querys;
import com.origami.sgm.restful.models.SolicitudServicioModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author Joao Sanga
 */
@Path("solicitudservicio")
@Produces({"application/Json; charset=utf-8", "text/xml"})
public class WSSolicitudServicio implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private Entitymanager services;

    @GET
    public SolicitudServicioModel getSolicitudServicio() {
        SolicitudServicioModel model = new SolicitudServicioModel();
        
        model.llenarListas(services.findAll(Querys.getVuCatalogo)
                , services.findAll(Querys.getCatParroquias));
        
        return model;
    }
    
    @GET
    @Path(value = "/parroquias")
    public SolicitudServicioModel getParroquias() {
        SolicitudServicioModel model = new SolicitudServicioModel();
        
        model.llenarListas(null
                , services.findAll(Querys.getCatParroquias));
        
        return model;
    }
    
    
}
