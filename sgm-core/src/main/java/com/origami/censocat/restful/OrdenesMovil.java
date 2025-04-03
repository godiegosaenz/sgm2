/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful;

import com.origami.sgm.entities.OrdenDet;
import com.origami.sgm.entities.OrdenTrabajo;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.Utils;

/**
 * Realiza el envio de ordenes para la mobil
 *
 * @author Angel Navarro
 */
@Named
@RequestScoped
@Path("ordenes")
@Produces({"application/Json", "text/xml"})
public class OrdenesMovil implements Serializable {

    private static final long serialVersionUID = 1l;
    private static final Logger LOG = Logger.getLogger(OrdenesMovil.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    private JsonUtils jsonUtils;

    @GET
    @Path(value = "/auth/userName/{userName}/password/{password}")
    public Response getAuth(@PathParam("userName") String userName, @PathParam("password") String password) {
        AclUser u = (AclUser) manager.find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{userName, Utils.encriptSHAHex(password)});
        try {
//            if (u != null) {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(u);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
            //}
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Copiar Ordenes.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "/copiar/userName/{userName}/password/{password}")
    public Response getOrdenesFecha(@PathParam("userName") String userName, @PathParam("password") String password) {
        AclUser u = (AclUser) manager.find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{userName, Utils.encriptSHAHex(password)});
        try {
            List<OrdenTrabajo> ot = null;
            if (u != null) {
                ot = manager.findAll(com.origami.censocat.querys.Querys.getOrdenes, new String[]{"responsable", "estadoOt", "estadoDet"}, new Object[]{u, EstadoMovil.PENDIENTE, EstadoMovil.PENDIENTE});
            }
            //if (ot != null) {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ot);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
            // }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Copiar Ordenes.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "/copiar/userName/{userName}/password/{password}/fechaCreacion/{fechaCreacion}")
    public Response getOrdenesFecha(@PathParam("userName") String userName, @PathParam("password") String password, @PathParam("fechaCreacion") Long fecha) {
        AclUser u = (AclUser) manager.find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{userName, Utils.encriptSHAHex(password)});
        try {
            List<OrdenTrabajo> ot = null;
            if (u != null) {
                Date d = new Date(fecha);
                System.out.println("Fecha" + d);
                ot = manager.findAll(com.origami.censocat.querys.Querys.getOrdenesMayorFecha, new String[]{"responsable", "estadoOt", "fecCre"}, new Object[]{u, EstadoMovil.PENDIENTE, d});
            }
            //if (ot != null) {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ot);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
            //  }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Copiar Ordenes.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path(value = "/sincronizar/userName/{userName}/password/{password}/numOrden/{numOrden}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDescargarOrdenes(@PathParam("userName") String userName, @PathParam("password") String password, @PathParam("numOrden") Long numOrden, String data) throws Exception {
        jsonUtils = new JsonUtils();
        Boolean ok = false;
        try {
//            System.out.println(data);
            OrdenTrabajo ordenMovil = (OrdenTrabajo) jsonUtils.jsonToObject(data, OrdenTrabajo.class);
//            String base64Image = data.split(",")[1];
//            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
            if (ordenMovil != null) {
                LOG.info("Iniciando sincronizacion de orden # " + ordenMovil.getNumOrden());
                AclUser u = (AclUser) manager.find(Querys.getUsuariobyUserPass, new String[]{"user", "pass"}, new Object[]{userName, Utils.encriptSHAHex(password)});
                OrdenTrabajo ot = getOrdenUser(u, numOrden);
                if (ot != null) {
                    ot.setFecAct(new Date());
                    try {
                        ot.setObservaciones(jsonUtils.getElementFromJson(data, "fotos"));
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Obtener Fotos.", e);
                    }
                    ot.setEstadoOt(EstadoMovil.CENSADA); // Cargada
                    if (Utils.isNotEmpty(ordenMovil.getOrdenDetCollection())) {
                        for (OrdenDet dtMovil : ordenMovil.getOrdenDetCollection()) {
                            if (Utils.isNotEmpty(ot.getOrdenDetCollection())) {
                                OrdenDet get = ((List<OrdenDet>) ot.getOrdenDetCollection())
                                        .get(((List<OrdenDet>) ot.getOrdenDetCollection()).indexOf(dtMovil));
                                if (get != null) {
                                    get.setDatoAct(dtMovil.getDatoRef());
                                    get.setEstadoDet(EstadoMovil.CENSADA);
                                }
                            }
                        }
                    }
                    manager.saveList((List) ot.getOrdenDetCollection());
                    manager.persist(ot);
                    ok = true;
                }
            }
            if (ok) {
                LOG.info("Finalizando sincronizacion de orden # " + ordenMovil.getNumOrden());
                String js = jsonUtils.generarJson("{}");
                return Response.status(Response.Status.OK).entity(js)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE")
                        .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .allow("OPTIONS")
                        .build();
            } else {
                LOG.info("Error al sincronizar orden...");
                return Response.status(Response.Status.NOT_FOUND)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE")
                        .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .allow("OPTIONS")
                        .build();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Descarga de Ordenes.", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .allow("OPTIONS")
                    .build();
        }
    }

    private OrdenTrabajo getOrdenUser(AclUser u, Long numOrden) {
        try {
            Map<String, Object> paramt = new HashMap<>();
            paramt.put("numOrden", numOrden);
            paramt.put("responsable", u);
            return manager.findObjectByParameter(OrdenTrabajo.class, paramt);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Obtener Ordenes.", e);
        }
        return null;
    }

}
