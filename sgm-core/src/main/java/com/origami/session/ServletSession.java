/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author origami-idea
 */
@Named
@SessionScoped
public class ServletSession implements Serializable {

    private List<Map> reportes;
    private Map parametros = null;
    private Boolean tieneDatasource;
    private String nombreReporte;
    private String nombreDocumento;
    private String nombreSubCarpeta;
    private byte[] reportePDF;
    private Boolean encuadernacion = Boolean.FALSE;
    private List dataSource;
    private Boolean agregarReporte = Boolean.FALSE;
    private Boolean fondoBlanco = Boolean.TRUE;
    private Boolean onePagePerSheet = Boolean.FALSE;

    public void instanciarParametros() {
        parametros = new HashMap();
    }

    public void agregarParametro(String nombre, Object value) {
        parametros.put(nombre, value);
    }

    public boolean tieneParametro(Object parametro) {
        return parametros.containsKey(parametro);
    }

    public List<Map> getReportes() {
        return reportes;
    }

    public void setReportes(List<Map> reportes) {
        this.reportes = reportes;
    }

    /**
     * <p>En el <code>Map<code/> debe esta incluido un parametro con <code>nombreReporte<code/>
     * donde se va tomar el nombre del reporte que se va agregar al final del primero.<p/>
     * <p>Si el reporte seencuentra en la misma carpeta tomara en el nombre de la variable
     * <code>nombreSubCarpeta<code/> para el caso que se encuentre en otra carpeta se debe 
     * incluir otro parametro <code>nombreSubCarpeta</code>.<p>
     * @param map
     */
    public void addParametrosReportes(Map map) {
        if (reportes == null) {
            reportes = new ArrayList<>();
        }
        reportes.add(map);
    }

    public boolean estaVacio() {
        if (parametros != null) {
            return parametros.isEmpty();
        } else {
            return true;
        }
    }

    public Object retornarValor(Object parametro) {
        return parametros.get(parametro);
    }

    public void borrarDatos() {
        parametros = null;
        tieneDatasource = null;
        nombreReporte = null;
        nombreDocumento = null;
        nombreSubCarpeta = null;
        reportePDF = null;
        encuadernacion = Boolean.FALSE;
        agregarReporte = Boolean.FALSE;
        reportes = null;
    }

    public void borrarParametros() {
        parametros = null;
        reportes = null;
        tieneDatasource = null;
    }

    public Boolean validarCantidadDeParametrosDelServlet() {
        return parametros != null && parametros.size() > 0;
    }

    public Map getParametros() {
        return parametros;
    }

    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    public Boolean getTieneDatasource() {
        return tieneDatasource;
    }

    public void setTieneDatasource(Boolean tieneDatasource) {
        this.tieneDatasource = tieneDatasource;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getNombreSubCarpeta() {
        return nombreSubCarpeta;
    }

    public void setNombreSubCarpeta(String nombreSubCarpeta) {
        this.nombreSubCarpeta = nombreSubCarpeta;
    }

    public byte[] getReportePDF() {
        return reportePDF;
    }

    public void setReportePDF(byte[] reportePDF) {
        this.reportePDF = reportePDF;
    }

    public Boolean getEncuadernacion() {
        return encuadernacion;
    }

    public void setEncuadernacion(Boolean encuadernacion) {
        this.encuadernacion = encuadernacion;
    }

    public List getDataSource() {
        return dataSource;
    }

    public void setDataSource(List dataSource) {
        this.dataSource = dataSource;
    }

    public Boolean getAgregarReporte() {
        return agregarReporte;
    }

    public void setAgregarReporte(Boolean agregarReporte) {
        this.agregarReporte = agregarReporte;
    }

    public Boolean getFondoBlanco() {
        return fondoBlanco;
    }

    public void setFondoBlanco(Boolean fondoBlanco) {
        this.fondoBlanco = fondoBlanco;
    }

    public Boolean getOnePagePerSheet() {
        return onePagePerSheet;
    }

    public void setOnePagePerSheet(Boolean onePagePerSheet) {
        this.onePagePerSheet = onePagePerSheet;
    }

   
}
