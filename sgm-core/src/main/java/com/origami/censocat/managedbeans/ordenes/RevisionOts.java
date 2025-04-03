package com.origami.censocat.managedbeans.ordenes;

import com.origami.sgm.entities.OrdenDet;
import com.origami.censocat.restful.EstadoMovil;
import com.origami.censocat.service.catastro.PredioEjb;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Inject;
import javax.faces.bean.ViewScoped;
import util.Faces;
import util.managedbeans.OtsUtil;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class RevisionOts extends OtsUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    private BaseLazyDataModel<OrdenDet> ordenes;
    @Inject
    private PredioEjb preds;
    @Inject
    private UserSession sess;
    private Map<String, Object> params;
    @Inject
    private ServletSession ss;

    @PostConstruct
    protected void load() {
        if (sess != null) {
            if (sess.getVarTemp() != null) {
                ordenes = new BaseLazyDataModel<>(OrdenDet.class, "id", "DESC");
                ordenes.setFilterss(new String[]{"numOrden"});
                ordenes.setFiltersValue(new Object[]{Long.parseLong(sess.getVarTemp())});
            } else {
                ordenes = new BaseLazyDataModel<>(OrdenDet.class, "id", "DESC");
            }
        }
    }

    public void editar(OrdenDet dt) {
        if (dt.getPredio() != null) {
            params = new HashMap();
            params.put("numPredio", dt.getPredio().getNumPredio());
            params.put("idPredio", dt.getPredio().getId());
            params.put("edit", true);
            ss.setParametros(params);
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/fichaPredial.xhtml");
        } else {
            Faces.messageWarning(null, "Advertencia", "El detalle de la orden debe ser procesada, para poder revisarla");
        }
    }

    public void revisar(OrdenDet dt) {
        try {
            if (!dt.getEstadoDet().equals(EstadoMovil.CENSADA)) {
                Faces.messageWarning(null, "Advertencia", "La orden no ha sido realizada por el investigador.");
                return;
            }
            if (!dt.getEstado()) {
                Faces.messageWarning(null, "Advertencia", "El detalle de la orden esta inactiva.");
                return;
            }
            params = new HashMap<>();
            params.put("idDetOrden", dt.getId());
            ss.setParametros(params);
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/ordenes/revisionPredio.xhtml");
        } catch (Exception e) {
            Faces.messageWarning(null, "Advertencia", "El detalle de la orden debe ser procesada, para poder revisarla");
        }
    }

    public void reprocesarPropietarios() {
        preds.reprocesarPropietarios();
    }

    public BaseLazyDataModel<OrdenDet> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(BaseLazyDataModel<OrdenDet> ordenes) {
        this.ordenes = ordenes;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

}
