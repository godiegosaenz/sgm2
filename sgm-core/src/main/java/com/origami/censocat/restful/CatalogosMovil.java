/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.restful;

import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatPropiedadItem;
import com.origami.sgm.entities.CatProvincia;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.models.ModelCatEdfCategProp;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@RequestScoped
@Path("movil")
@Produces({"application/Json", "text/xml"})
public class CatalogosMovil implements Serializable {

    private static final long serialVersionUID = 1l;
    private static final Logger LOG = Logger.getLogger(CatalogosMovil.class.getName());

    @Inject
    private Entitymanager manager;
    private JsonUtils jsonUtils;

    @GET
    @Path(value = "ctlgCatalogo")
    public Response getCatalogo() {
        List<CtlgCatalogo> ccat = manager.findAllObjectOrder(CtlgCatalogo.class, new String[]{"nombre"}, true);
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "ctlgCatalogoCultSub")
    public Response getCatalogoCult() {
        List<CtlgItem> ccat = subCatCult();
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private List<CtlgItem> subCatCult() {
        List<CtlgItem> listItems = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", "cultivo.tipo");
        List<CtlgCatalogo> ccat = manager.findObjectByParameterList(CtlgCatalogo.class, map);
        if (Utils.isNotEmpty(ccat)) {
            ccat.forEach((ctlgCatalogo) -> {
                ctlgCatalogo.getCtlgItemCollection().stream().map((ctlgItem) -> {
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("padre", BigInteger.valueOf(ctlgItem.getId()));
                    List<CtlgItem> items = manager.findObjectByParameterList(CtlgItem.class, map1);
                    return items;
                }).filter((items) -> (Utils.isNotEmpty(items))).forEachOrdered((items) -> {
                    listItems.addAll(items);
                });
            });
        }
        return listItems;
    }

    @GET
    @Path(value = "ctlgCatalogoMaterialObra")
    public Response getCatalogoMaterialObra() {
        List<CtlgItem> ccat = materialObraInterna();
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private List<CtlgItem> materialObraInterna() {
        List<CtlgItem> listItems = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", "predio.tipo.obra");
        List<CtlgCatalogo> ccat = manager.findObjectByParameterList(CtlgCatalogo.class, map);
        if (Utils.isNotEmpty(ccat)) {
            ccat.forEach((ctlgCatalogo) -> {
                ctlgCatalogo.getCtlgItemCollection().stream().map((ctlgItem) -> {
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("padre", BigInteger.valueOf(ctlgItem.getId()));
                    List<CtlgItem> items = manager.findObjectByParameterList(CtlgItem.class, map1);
                    return items;
                }).filter((items) -> (Utils.isNotEmpty(items))).forEachOrdered((items) -> {
                    listItems.addAll(items);
                });
            });
        }
        return listItems;
    }

    @GET
    @Path(value = "ctlgCatalogoPlantaciones")
    public Response getCatalogoPlantaciones() {
        List<CtlgItem> temp = subCatCult();
        List<CtlgItem> ccat = new ArrayList<>();
        for (CtlgItem ctlgItem : temp) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("hijo", BigInteger.valueOf(ctlgItem.getId()));
            List<CtlgItem> items = manager.findObjectByParameterList(CtlgItem.class, map1);
            if (Utils.isNotEmpty(items)) {
                ccat.addAll(items);
            }
        }
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "categoriasConstruccion")
    public Response getCategoriasConst() {
        try {
            List<CatEdfCategProp> ccat = manager.findAllObjectOrder(CatEdfCategProp.class, new String[]{"guiOrden"}, true);
            List<ModelCatEdfCategProp> models = new ArrayList<>();
            ccat.forEach((ce) -> {
                ModelCatEdfCategProp m = new ModelCatEdfCategProp();
                m.setCatEdfPropCollection(ce.getCatEdfPropCollection());
                m.setGuiOrden(ce.getGuiOrden());
                m.setId(ce.getId());
                m.setNombre(ce.getNombre());
                m.setTipoEstruc(ce.getTipoEstruc());
                models.add(m);
            });
            try {
                jsonUtils = new JsonUtils();
                String js = jsonUtils.generarJson(models);
                return Response.status(Response.Status.OK).entity(js)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                        .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                        .header("Content-Type", "application/json;charset=UTF-8").build();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Catalogos.", e);
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception ex) {
            Logger.getLogger(CatalogosMovil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "tenencia")
    public Response getPropiadadItem() {
        List<CatPropiedadItem> ccat = manager.findAllObjectOrder(CatPropiedadItem.class, new String[]{"orden"}, true);
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "espacioUrbanoCiudadela")
    public Response getEspacioUrbano() {
        List<CatTipoConjunto> ccat = manager.findAll(CatTipoConjunto.class);
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value = "provinciasCantones")
    public Response getProvicias() {
        List<CatProvincia> ccat = manager.findAllObjectOrder(CatProvincia.class, new String[]{"codNac"}, true);
        try {
            jsonUtils = new JsonUtils();
            String js = jsonUtils.generarJson(ccat);
            return Response.status(Response.Status.OK).entity(js)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("Content-Type", "application/json;charset=UTF-8").build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Catalogos.", e);
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
