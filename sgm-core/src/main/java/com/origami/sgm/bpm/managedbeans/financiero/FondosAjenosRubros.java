/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author origami
 */
@Named(value = "fondosAjenosRubros")
@ViewScoped
public class FondosAjenosRubros implements Serializable{
    
    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;
    protected Integer tipoReporte;
    protected Date fechaDesde;
    protected Date fechaHasta;
    protected List<Long> listRubros;
    

    @PostConstruct
    public void initView() {
        try{
            
        } catch (Exception e) {
            Logger.getLogger(FondosAjenosRubros.class.getName()).log(Level.SEVERE, null, e);
        }
        
    }
    
    public void generarReporte(){
        try{
            if(this.tipoReporte==null || this.fechaDesde==null || this.fechaHasta==null){
                JsfUti.messageInfo(null, "Mensaje", "Los parametros son obligatorios");
                return;
            }
            listRubros = new ArrayList<>();
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("recaudaciones");
            ss.setNombreReporte("fondosAjenos");
            ss.setNombreDocumento("fondosAjenos");
            ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.sisLogo1));
            ss.agregarParametro("DESDE", this.fechaDesde);
            ss.agregarParametro("HASTA", this.fechaHasta);
            ss.agregarParametro("USUARIO", session.getName_user());
            ss.agregarParametro("TARIFA", Boolean.FALSE);
            
            switch (tipoReporte) {                
                case 1:
                    ss.agregarParametro("NOMBRE_REPORTE", "MUNICIPIO DE GUAYAQUIL");
                    break;
                case 2:
                    ss.agregarParametro("NOMBRE_REPORTE", "DEFENSA NACIONAL");
                    break;
                case 3:
                    ss.agregarParametro("NOMBRE_REPORTE", "CONSEJO PROVINCIAL");
                    listRubros.add(339L);
                    break;
                case 4:
                    ss.agregarParametro("NOMBRE_REPORTE", "AGUA POTABLE");
                    
                    break;
                case 5:
                    ss.agregarParametro("NOMBRE_REPORTE", "BOMBEROS DE TARIFA");
                    ss.agregarParametro("TARIFA", Boolean.TRUE);
                    listRubros.add(12L);
                    listRubros.add(21L);
                    break;
                case 6:
                    ss.agregarParametro("NOMBRE_REPORTE", "BOMBEROS DE SAMBORONDON");
                    listRubros.add(12L);
                    listRubros.add(21L);
                    break;
                case 7:
                    ss.agregarParametro("NOMBRE_REPORTE", "VIVIENDA RURAL");
                    listRubros.add(13L);
                    break;
                case 8:
                    ss.agregarParametro("NOMBRE_REPORTE", "JUNTA DE BENEFICENCIA");
                    listRubros.add(340L);
                    listRubros.add(57L);
                    break;
                case 9:
                    ss.agregarParametro("NOMBRE_REPORTE", "SALUD PECUARIA");
                    listRubros.add(19L);
                    break;
                case 10:
                    ss.agregarParametro("NOMBRE_REPORTE", "CENTRO AGRICOLA");
                    listRubros.add(22L);
                    break;
                case 11:
                    ss.agregarParametro("NOMBRE_REPORTE", "COACTIVA");
                    break;
                case 12:
                    ss.agregarParametro("NOMBRE_REPORTE", "TRANSPORTE TERRESTRE Y SEGURIDAD VIAL");
                    listRubros.add(590L);
                    listRubros.add(591L);
                    break;
                default:
            }
            if (listRubros==null || listRubros.isEmpty()) {
                JsfUti.messageInfo(null, "Mensaje", "Reporte no puede ser generado");
                return;
            }
            ss.agregarParametro("RUBROS", listRubros);
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            Logger.getLogger(FondosAjenosRubros.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
    
}
