/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero.bancos;

import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.bancos.FormatoBanca;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.financiero.bancos.models.FormatoUnificado;
import com.origami.sgm.lazymodels.ConciliacionBancaria;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.financiero.bancos.ConsolidacionBancosServ;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class Consolidacion implements Serializable {

    private static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    private ConsolidacionBancosServ cbs;
    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private AvaluosServices avaluos;
    private FormatoBanca formato;
    private List<FormatoBanca> bancos;
    private ConciliacionBancaria conciliacion;
    private List<RenLiquidacion> seleccionados;
    private Date fecReferencia;
    private Integer periodo;
    private RenTipoLiquidacion tipoLiquidacion;
    private RenEstadoLiquidacion estadoLiquidacion;
    private SimpleDateFormat sdf = null;
    private Boolean existe = false;
    private List<FormatoUnificado> datosFormato;
    private List<String> resultado;
    private int total = 0;
    private String nombre;
    private BigInteger predio;
    private static final Logger logx = Logger.getLogger(Consolidacion.class.getName());

    @PostConstruct
    protected void init() {
        bancos = manager.findAllEntCopy(FormatoBanca.class);
        tipoLiquidacion = manager.find(RenTipoLiquidacion.class, 13L);
        estadoLiquidacion = manager.find(RenEstadoLiquidacion.class, 2L);
    }

    public void probar() throws InterruptedException {
        try {
            avaluos.getEmisionGeneral("admin", 2017, Integer.parseInt(avaluos.getNumVersion().toString())).get();
        } catch (ExecutionException ex) {
            Logger.getLogger(Consolidacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void emisionPredial() {
        try {
            avaluos.getDatosPredioBase(2017, predio, "admin");
        } catch (Exception ex) {
            Logger.getLogger(Consolidacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void emisionPredialIndividual() {
        try {
            ValoracionPredial h = avaluos.getEmisionPredial("admin", 2017, predio, true).get();
            //avaluos.actualizarDatosVersion(h);
        } catch (Exception ex) {
            Logger.getLogger(Consolidacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizacionCalculos() {
        avaluos.prediosRecalculados();

    }

    public void cargarLiquidaciones() {
        try {
            if (fecReferencia != null) {
                conciliacion = new ConciliacionBancaria();
                conciliacion.setTipoLiquidacion(tipoLiquidacion);
                conciliacion.setEstadoLiquidacion(estadoLiquidacion);
                this.getValorPeriodo();
                conciliacion.setPeriodo(periodo);
                conciliacion.setExluidos(this.getPredios());
                conciliacion.setSector(new Short("3"));
                existe = true;
            }
        } catch (NumberFormatException e) {
            logx.log(Level.SEVERE, null, e);
        }
    }

    public void getValorPeriodo() {
        sdf = new SimpleDateFormat("YYYY");
        periodo = Integer.parseInt((sdf.format(fecReferencia)));
    }

    protected List<CatPredio> getPredios() {
        List<CatPredio> lpreds = new ArrayList<>();
        List<RenLiquidacion> liqs = manager.findAll(QuerysFinanciero.getPrediosExcluidos, new String[]{"anioAnt", "sector", "tipoLiquidacion", "estadoLiquidacion"}, new Object[]{(periodo - 2), 2, tipoLiquidacion.getId(), estadoLiquidacion.getId()});
        for (RenLiquidacion r : liqs) {
            lpreds.add(r.getPredio());
        }
        return lpreds;
    }

    public void seleccionarPagos() {
        try {
            seleccionados = conciliacion.getResultado();
            if (seleccionados != null) {
                total = seleccionados.size();
                Faces.messageInfo(null, "Nota!", "Fueron seleccionaron " + seleccionados.size() + " predios para generar el fichero");
            }
        } catch (NumberFormatException e) {
            logx.log(Level.SEVERE, null, e);
        }
    }

    public void generarArchivo() throws InterruptedException, ExecutionException {
        if (formato != null) {
            if (seleccionados != null) {
                if (datosFormato != null) {
                    if (datosFormato.size() == total) {
                        resultado = cbs.getArchivo(datosFormato, formato);
                    } else {
                        conciliacion.procesar();
                        datosFormato = cbs.getPagosPrediales(conciliacion.getResultado()).get();
                        //datosFormato = cbs.getPagosPrediales(seleccionados).get();
                        resultado = cbs.getArchivo(datosFormato, formato);
                    }
                } else {
                    conciliacion.procesar();
                    datosFormato = cbs.getPagosPrediales(conciliacion.getResultado()).get();
                    //datosFormato = cbs.getPagosPrediales(seleccionados).get();
                    resultado = cbs.getArchivo(datosFormato, formato);
                }
                try {
                    nombre = formato.getTipo() + "" + formato.getArchivo() + "_" + periodo + formato.getTipoArchivo();
                } catch (Exception e) {
                    //System.out.println("Nombre incorrecto " + e.getMessage());
                    logx.log(Level.SEVERE, "Nombre incorrecto ", e);
                }
            }
        } else {
            Faces.messageWarning(null, "Advertencia!", "Debe seleccionar el formato para el banco respectivo!");
        }
    }

    public void descargar() {
        try {
            if (resultado != null) {
                FacesContext fc = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
                response.setContentType("text/palin");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + nombre + "\"");
                ServletOutputStream out = response.getOutputStream();
                for (String r : resultado) {
                    out.write(r.getBytes());
                }
                out.flush();
                FacesContext.getCurrentInstance().responseComplete();
            }
        } catch (IOException e) {
            logx.log(Level.SEVERE, null, e);
        }
    }

    public ConciliacionBancaria getConciliacion() {
        return conciliacion;
    }

    public void setConciliacion(ConciliacionBancaria conciliacion) {
        this.conciliacion = conciliacion;
    }

    public List<RenLiquidacion> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<RenLiquidacion> seleccionados) {
        this.seleccionados = seleccionados;
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

    public Date getFecReferencia() {
        return fecReferencia;
    }

    public void setFecReferencia(Date fecReferencia) {
        this.fecReferencia = fecReferencia;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RenEstadoLiquidacion getEstadoLiquidacion() {
        return estadoLiquidacion;
    }

    public void setEstadoLiquidacion(RenEstadoLiquidacion estadoLiquidacion) {
        this.estadoLiquidacion = estadoLiquidacion;
    }

    public Boolean getExiste() {
        return existe;
    }

    public void setExiste(Boolean existe) {
        this.existe = existe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigInteger getPredio() {
        return predio;
    }

    public void setPredio(BigInteger predio) {
        this.predio = predio;
    }

}
