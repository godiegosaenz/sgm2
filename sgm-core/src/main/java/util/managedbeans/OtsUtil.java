/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.managedbeans;

import com.origami.sgm.entities.OrdenDet;
import com.origami.censocat.querys.Querys;
import com.origami.censocat.service.catastro.PredioEjb;
import com.origami.sgm.entities.CatPredio;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class OtsUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager manag;
    @javax.inject.Inject
    private PredioEjb preds;
    private List<OrdenDet> ldts;

    @PostConstruct
    private void init() {
//        manag = (TransactionManager) EjbsCaller.getTransactionManager();

    }

    protected Boolean existe(CatPredio p) {
        if (p != null) {
//            ldts = manag.findAll(Querys.getDetOrdenes("numPredio", p.getNumPredio().toString()));
            Object id = manag.find(Querys.getOrdenPredio, new String[]{"idPredio"}, new Object[]{p.getId()});
            return id != null;
        } else {
            return false;
        }
    }

    protected Boolean validarOrdenes(List<CatPredio> sp) {
        try {
            for (CatPredio p : sp) {
                if (existe(p)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(OtsUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean removerDetalle(OrdenDet d) {
        if (d != null) {
            if (preds.removerDetalle(d)) {
                Faces.messageInfo(null, "Nota!", "Predio y detalle de orden removidos satisfactoriamente");
            }
        } else {
            Faces.messageWarning(null, "Advertencia!", "La orden debe tener un predio asociado");
        }
        return false;
    }

}
