/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero.bancos;

import com.origami.sgm.services.interfaces.financiero.bancos.ConsolidacionBancosServ;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.entities.bancos.ConsolidacionBanco;
import com.origami.sgm.entities.bancos.FormatoBanca;
import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;

import org.primefaces.event.FileUploadEvent;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import java.io.File;
import javax.inject.Inject;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author Juan Carlos, CarlosLoorVargas
 */
@Named
@ViewScoped
public class ProcesamientoBancos extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private ConsolidacionBancosServ cbs;

    @javax.inject.Inject
    private Entitymanager manager;
    private FormatoBanca formato;
    private List<FormatoBanca> bancos;
    private List<ConsolidacionBanco> consolidados;
    private File file;

    /*FECHAS*/
    private Date fechaDesde;
    private Date fechaHasta;

        
    @Inject
    private ServletSession session;
    protected String path;

    @PostConstruct
    protected void init() {
        bancos = manager.findAllEntCopy(FormatoBanca.class);

    }

    public void procesar() {
        try {
            consolidados = cbs.getProcesarPagos(file, formato);
        } catch (Exception e) {
        }
    }

    /*FUNCION PARA LA CARGA DEL ARCGHIVO*/
    public void handleFileUpload(FileUploadEvent event) {
        try {
            Date d = new Date();
            String rutaArchivo = SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName();
            file = new File(rutaArchivo);
            cbs.getProcesarPagos(file, formato);
        } catch (Exception e) {
            System.out.println("Error al Procesar el Archivo");
        }
    }

    /*FUNCION PARA LISTAR LA INFORMACION SEGUN LAS FECHAS SELECCIONADAS*/
    public void listaInformacion() {
        try {
            if (fechaDesde != null && fechaHasta != null) {
                session.instanciarParametros();
                consolidados = manager.findAll(QuerysFinanciero.reporteBancoBolivariano, new String[]{"inicio", "fin"}, new Object[]{fechaDesde, fechaHasta});

            } else {
                JsfUti.messageError(null, "Debe de seleccionar las los Fechas para la consulta.", "");
                session.borrarDatos();
            }
        } catch (Exception e) {
            System.out.println("Proceso Exitoso");
        }
    }
    
    /*FUNCION PARA IMPRIMIR EL REPORTE SEGUN LAS FECHAS*/
    public  void imprimirReporte(){
        try {
            if (fechaDesde != null && fechaHasta != null) {
                if (fechaDesde.compareTo(fechaDesde) != 1) {      

                    session.borrarParametros();
                    session.instanciarParametros();
                    session.setTieneDatasource(Boolean.TRUE);
                    session.agregarParametro("inicio", fechaDesde);
                    session.agregarParametro("fin", fechaHasta);

                    session.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/reportes/").concat("/"));
                    session.setNombreSubCarpeta("banco/");
                    session.setNombreReporte("reporte_banco_bolivariano");
                    JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                } else {
                    JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
                }
            } else {
                    JsfUti.messageError(null, "La Fecha Fin no puede ser menor a la Fecha Inicio.", "");
            }           
        } catch (Exception e) {
            System.out.println("ERROR DE PROCESAMIENTO DE DATOS");
        }
    }       
//    
//    public  void imprimirReporte(){
//        try {
//            if(fechaDesde != null && fechaHasta != null){
//                session.borrarParametros();
//                session.instanciarParametros();
//                session.setTieneDatasource(Boolean.TRUE);
//                session.agregarParametro("inicio", fechaDesde);
//                session.agregarParametro("fin", fechaHasta);
//                
//                session.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/reportes/").concat("/"));
//                session.setNombreSubCarpeta("banco/");
//                session.setNombreReporte("reporte_banco_bolivariano");
//                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");                
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
//            
//            ss.agregarParametro("NOMBRE_TIPO_LIQUIDACION", tipoSeleccionado.getNombreTipoLiq());
//            ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
//            ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/reportes/").concat("/"));
//            ss.agregarParametro("FIRMAS_URL", JsfUti.getRealPath("/") + "/css/firmas/");
//            ss.agregarParametro("ruta_firmas", Faces.getRealPath("/css/firmas/").concat("/"));
//            ss.setNombreSubCarpeta("recaudaciones/");
//            ss.setNombreReporte("reporte_fondos_ajenos");
//            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<ConsolidacionBanco> getConsolidados() {
        return consolidados;
    }

    public void setConsolidados(List<ConsolidacionBanco> consolidados) {
        this.consolidados = consolidados;
    }

    public FormatoBanca getFormato() {
        return formato;
    }

    public void setFormato(FormatoBanca formato) {
        this.formato = formato;
    }

    public List<FormatoBanca> getBancos() {
        return bancos;
    }

    public void setBancos(List<FormatoBanca> bancos) {
        this.bancos = bancos;
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
