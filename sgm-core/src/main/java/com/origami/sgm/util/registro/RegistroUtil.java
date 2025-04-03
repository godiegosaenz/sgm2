/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.util.registro;

import com.origami.sgm.bpm.models.DatosInscripcion;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.util.EjbsCaller;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.HiberUtil;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
public abstract class RegistroUtil {

    //public synchronized static DatosInscripcion updateAndCountPageInscripcion(RegMovimiento mov, Map params, String ruta) {
    public synchronized static DatosInscripcion updateAndCountPageInscripcion(RegMovimiento mov, List<Map> params, List<String> ruta) {
        DatosInscripcion rm = null;
        Connection cx = null;
        RegMovimiento m = null;
        JasperPrint jp = null;
        Integer npagRazon = 0, npagInscrip = 0;
        Calendar cl = Calendar.getInstance();
        Integer folioFinal = 0;
        try {
            HiberUtil.requireTransaction();
            Session sess = HiberUtil.getSession();
            if (sess != null) {
                SessionImplementor sImp = (SessionImplementor) sess;
                cx = sImp.getJdbcConnectionAccess().obtainConnection();
                if (cx != null) {
                    rm = new DatosInscripcion();
                    List<JasperPrint> jspr = new ArrayList<>();
                    folioFinal = (Integer) EjbsCaller.getTransactionManager().getNativeQuery("Select max(m.folio_fin) FROM  sgm_app.reg_movimiento m where to_char(m.fecha_inscripcion,'YYYY')='" + cl.get(Calendar.YEAR) + "' and m.libro=" + mov.getLibro().getId());
                    if (folioFinal == null) {
                        folioFinal = 0;
                    }
                    mov.setFolioAnterior(folioFinal);
                    mov.setFolioInicio(folioFinal + 1);
                    mov.setFolioFin(folioFinal + mov.getNumPaginasContabilizada());
                    
                    jp = JasperFillManager.fillReport(ruta.get(0), params.get(0), cx);
                    npagInscrip = Utils.getNumberOfPagesDocumento(JasperExportManager.exportReportToPdf(jp));
                    mov.setNumPaginaInscripcion(getPaginado(npagInscrip));
                    jspr.add(jp);
                    jp = JasperFillManager.fillReport(ruta.get(1), params.get(1), cx);
                    npagRazon = Utils.getNumberOfPagesDocumento(JasperExportManager.exportReportToPdf(jp));
                    mov.setNumPaginaRazon(getPaginado(npagRazon));
                    
                    mov.setFolioFin(mov.getFolioFin() + mov.getNumPaginaInscripcion() + mov.getNumPaginaRazon());
                    jspr.add(jp);
                    rm.setjPrints(jspr);
                    m = (RegMovimiento) sess.merge(mov);
                    sess.flush();
                    sess.refresh(m);
                    if (m != null) {
                        rm.setMovimiento(m);
                        rm.setjPrint(jp);
                        sess.getTransaction().commit();
                        cx.close();
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException | JRException | HibernateException e) {
            Logger.getLogger(RegistroUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return rm;
    }

    private static Integer getPaginado(Integer npag) {
        if ((npag % 2) == 0) {
            return (npag / 2);
        } else {
            return ((npag + 1) / 2);
        }
    }

}
