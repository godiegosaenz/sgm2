/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatCertificadoAvaluo;
import java.math.BigInteger;
import java.util.Map;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author CarlosLoorVargas
 */
public class CertAvaluosLazy extends BaseLazyDataModel<CatCertificadoAvaluo> {

    private Boolean estado;
    private Criteria pred;
    private String compName;
    private UIViewRoot root;
    private DataTable dt;

    public CertAvaluosLazy() {
        super(CatCertificadoAvaluo.class, "codComprobante", "DESC" );
    }

    public CertAvaluosLazy(boolean estado) {
        super(CatCertificadoAvaluo.class);
        this.estado = estado;
    }

    public CertAvaluosLazy(boolean estado, String compName) {
        super(CatCertificadoAvaluo.class, "id", "DESC" );
        this.estado = estado;
        this.compName = compName;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        pred = crit.createCriteria("predio");
        root = FacesContext.getCurrentInstance().getViewRoot();
        dt = (DataTable) root.findComponent(compName);
        if (dt.getSortColumn() != null) {
            //System.out.println("predio "+dt.getSortColumn().getColumnKey());
            if (dt.getSortColumn().getColumnKey() != null && dt.getSortColumn().getColumnKey().contains("predio")) {
                this.setOrderCrit(pred);
                this.setOrderField("numPredio");
            }
        }
        if (estado) {
            crit.add(Restrictions.eq("estado", estado));
        }
        if (filters.containsKey("numCert")) {
            crit.add(Restrictions.ilike("numCert", "%" + filters.get("numCert").toString() + "%"));
        }
        if (filters.containsKey("secuencia")) {
            crit.add(Restrictions.eq("secuencia", Long.parseLong(filters.get("secuencia").toString())));
        }
        if (filters.containsKey("codComprobante")) {
            crit.add(Restrictions.eq("codComprobante", new BigInteger(filters.get("codComprobante").toString())));
        }
        if (filters.containsKey("identPredial")) {
            crit.add(Restrictions.eq("identPredial", filters.get("identPredial")));
        }
        if (filters.containsKey("codigoActual")) {
            crit.add(Restrictions.ilike("codigoActual", "%" + filters.get("codigoActual").toString() + "%"));
        }
        if (filters.containsKey("ususario")) {
            crit.add(Restrictions.ilike("ususario", "%" + filters.get("ususario").toString() + "%"));
        }
        if (filters.containsKey("fecha")) {
            crit.add(Restrictions.eq("fecha", filters.get("fecha")));
        }
        if (filters.containsKey("predio.numPredio")) {
            pred.add(Restrictions.eq("numPredio", new BigInteger(filters.get("predio.numPredio").toString())));
        }
    }

}
