/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeRequisitosTipoTramite;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.OtrosTramites;
import com.origami.sgm.entities.PeTipoPermisoAdicionales;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenClaseLocal;
import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.entities.VuItems;
import com.origami.sgm.restful.models.GeRequisitosTramitesModel;
import com.origami.sgm.restful.models.GeTipoTramiteModel;
import com.origami.sgm.util.EjbsCaller;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Joao Sanga
 */
@Path("tipotramite")
@Produces({"application/Json; charset=utf-8", "text/xml"})
public class WSTipoTramite implements Serializable {

    private static final long serialVersionUID = 1L;

    @GET
    @Path("/{tipotramite}")
    public List<GeTipoTramiteModel> getGeTipoTramites(@PathParam("tipotramite") Long tipotramite) {

        List<GeTipoTramiteModel> list = new ArrayList();
        List<GeRequisitosTramitesModel> list2;
        List<GeTipoTramite> listTramites;
        List<GeRequisitosTramite> listRequisitos;

        listTramites = (List<GeTipoTramite>) EjbsCaller.getTransactionManager().findAll(Querys.getGeTipoTramiteByReqWS, new String[]{"tipoWS"}, new Object[]{tipotramite});

        for (GeTipoTramite temp : listTramites) {
            GeTipoTramiteModel t1 = new GeTipoTramiteModel();
            t1.setId(temp.getId());
            t1.setDescripcion(temp.getDescripcion());
            t1.setActkey(temp.getActivitykey());

            listRequisitos = (List<GeRequisitosTramite>) temp.getGeRequisitosTramiteCollection();
            for (GeRequisitosTramite temp2 : listRequisitos) {
                GeRequisitosTramitesModel t2 = new GeRequisitosTramitesModel();
                t2.setId(temp2.getId());
                t2.setNombre(temp2.getNombre());
                t2.setTiene_comprobante(temp2.getTieneComprobante());

                t1.getRequisitos().add(t2);
            }

            list.add(t1);
        }
        return list;
    }

    @GET
    @Path("/subTipo/{subTipotramite}")
    public List<GeTipoTramiteModel> getSubTipoTramites(@PathParam("subTipotramite") Long tipotramite) {
        List<GeTipoTramiteModel> list = new ArrayList();
        switch (tipotramite.intValue()) {
            case 2:
                GeTipoTramite tipo = EjbsCaller.getTransactionManager().find(GeTipoTramite.class, tipotramite);
//                List<PeTipoPermiso> stp = EjbsCaller.getTransactionManager().findAllOrdEntCopy(PeTipoPermiso.class,
//                        new String[]{"descripcion"}, new Boolean[]{true});
                for (GeRequisitosTipoTramite st : tipo.getGeRequisitosTipoTramiteCollection()) {
                    GeTipoTramiteModel m = new GeTipoTramiteModel();
                    m.setId(st.getId());
                    m.setDescripcion(st.getNombre());
                    list.add(m);
                }
                break;
            case 14:
                List<OtrosTramites> ots = EjbsCaller.getTransactionManager().findAllOrdEntCopy(OtrosTramites.class,
                        new String[]{"tipoTramite"}, new Boolean[]{true});
                for (OtrosTramites ot : ots) {
                    GeTipoTramiteModel m = new GeTipoTramiteModel();
                    m.setId(ot.getId());
                    m.setDescripcion(ot.getTipoTramite());
//                    m.setActkey();
                    list.add(m);
                }
                break;
            case 6:
                List<PeTipoPermisoAdicionales> pa = EjbsCaller.getTransactionManager().findAllOrdEntCopy(PeTipoPermisoAdicionales.class,
                        new String[]{"descripcion"}, new Boolean[]{true});
                for (PeTipoPermisoAdicionales ot : pa) {
                    GeTipoTramiteModel m = new GeTipoTramiteModel();
                    m.setId(ot.getId());
                    m.setDescripcion(ot.getDescripcion());
                    m.setActkey(ot.getCodigo());
                    list.add(m);
                }
                break;
            case 20:
                VuCatalogo ct = EjbsCaller.getTransactionManager().find(VuCatalogo.class, 42L);
                VuCatalogo au = EjbsCaller.getTransactionManager().find(VuCatalogo.class, 43L);
                for (VuItems ot : ct.getVuItemsCollection()) {
                    GeTipoTramiteModel m = new GeTipoTramiteModel();
                    m.setId(ot.getId());
                    m.setDescripcion(ot.getNombre());
                    m.setActkey(ot.getCodigoCiuu());
                    if (ot.getId() == 783L) {
                        m.setRequisitos(new ArrayList<GeRequisitosTramitesModel>());
                        for (VuItems aud : au.getVuItemsCollection()) {
                            GeRequisitosTramitesModel dt = new GeRequisitosTramitesModel();
                            dt.setId(aud.getId());
                            dt.setNombre(aud.getNombre());
                            m.getRequisitos().add(dt);
                        }
                    }
                    list.add(m);
                }
                break;
            case 50:
                List<RenClaseLocal> cl = EjbsCaller.getTransactionManager().findAllOrdEntCopy(RenClaseLocal.class, new String[]{"descripcion"}, new Boolean[]{Boolean.TRUE});
                if (cl != null) {
                    for (RenClaseLocal c : cl) {
                        GeTipoTramiteModel m = new GeTipoTramiteModel();
                        m.setId(c.getId());
                        m.setDescripcion(c.getDescripcion());
                        list.add(m);
                    }
                }
                break;
            default:
                break;
        }
        return list;
    }

    @GET
    @Path("/afiliacionCamara")
    public List<GeTipoTramiteModel> getSubTipoTramites() {
        List<GeTipoTramiteModel> list = new ArrayList();
        List<RenAfiliacionCamaraProduccion> rafl = EjbsCaller.getTransactionManager().findAllOrdEntCopy(RenAfiliacionCamaraProduccion.class, new String[]{"descripcion"}, new Boolean[]{Boolean.TRUE});
        for (RenAfiliacionCamaraProduccion a : rafl) {
            if (a.getEstado()) {
                GeTipoTramiteModel m = new GeTipoTramiteModel();
                m.setId(a.getId());
                m.setDescripcion(a.getDescripcion());
                list.add(m);
            }
        }
        return list;
    }

}
