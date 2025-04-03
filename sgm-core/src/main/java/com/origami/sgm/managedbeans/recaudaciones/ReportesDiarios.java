/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.recaudaciones;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.models.ParteRecaudaciones;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel
 * Navarro
 * @Date 26/07/2016
 */
@Named
@ViewScoped
public class ReportesDiarios implements Serializable {

    private static final Logger LOG = Logger.getLogger(ReportesDiarios.class.getName());

    @javax.inject.Inject
    private Entitymanager manager;
    private Map<String, Object> paramt;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private Date fecha;
    private Date fechaHasta;
    private Integer numInforme = 00001;
    private Integer tipoReporte;
    private Integer anio;
    private Integer anioMax;
    private String oficio;

    // Variables Render
    private Boolean desde = false;
    private Boolean hasta = false;
    private Boolean informe = false;
    private Boolean oficioRender = false;
    protected Date fechaCaja = new Date();

    @javax.inject.Inject
    private RecaudacionesService service;

    @Inject
    private ServletSession ss;
    @Inject
    private UserSession session;

    @PostConstruct
    protected void initView() {
        iniciarDatos();
    }

    private void iniciarDatos() {
        fecha = new Date();
        fechaHasta = new Date();
        desde = false;
        hasta = false;
        informe = false;
        oficioRender = false;
    }

    public void mostrarEtiquetas() {

        try {
            iniciarDatos();
            if (tipoReporte == 18 || tipoReporte == 1 || tipoReporte == 3 || tipoReporte == 4 || tipoReporte == 5 || tipoReporte == 6 || tipoReporte == 8 || tipoReporte == 9 || tipoReporte == 14 || tipoReporte == 17) {
                desde = true;
                hasta = true;
            } else if (tipoReporte == 2) {
                desde = true;
                informe = true;
                anio = Utils.getAnio(new Date());
                anioMax = anio;
            } else if (tipoReporte == 7) {
                desde = true;
                informe = true;
                anio = Utils.getAnio(new Date());
                anioMax = anio;
                oficioRender = true;
            } else if (tipoReporte == 12) {
                desde = true;
                hasta = true;
                oficioRender = true;
            } else if (tipoReporte == 13) {
                desde = true;
                anio = Utils.getAnio(new Date());
                anioMax = anio;
                informe = true;
            } else if (tipoReporte == 15) {
                desde = true;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void generar(Boolean excel) {
        if (fecha == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha desde");
            return;
        }
        String path = Faces.getRealPath("//");
        ss.borrarDatos();
        ss.instanciarParametros();
        ss.setTieneDatasource(Boolean.TRUE);
        ss.setNombreSubCarpeta("informeDiarios");
        ss.agregarParametro("LOGO", path.concat(SisVars.logoReportes));
        ss.agregarParametro("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
        ss.agregarParametro("LOGO2", JsfUti.getRealPath(SisVars.sisLogo1));
        ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
        ss.agregarParametro("DESDE", fecha);
        ss.agregarParametro("HASTA", fechaHasta);
        ss.agregarParametro("USUARIO", session.getName_user());
        ss.agregarParametro("SUBREPORT_TITLE", Faces.getRealPath("/reportes/").concat("/"));
        if (session != null && (session.getUserId() == 1L || session.getUserId() == 144L)) {
            ss.agregarParametro("FECHA_CIERRE", fechaCaja);
        } else {
            ss.agregarParametro("FECHA_CIERRE", new Date());
        }
        Map<String, Object> parametrosReporteAdicional = new HashMap<>();// AGREGAR EL NOMBRE Y EL MAP DE PARAMETROS
        Map<String, Object> parametrosReporteAdicional2 = new HashMap<>();// AGREGAR EL NOMBRE Y EL MAP DE PARAMETROS
        switch (tipoReporte) {
            case 1:
                if (fechaHasta == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha hasta");
                    return;
                }
                ss.setNombreDocumento("InformeRecaudacionesPorRecaudador");
                ss.setNombreReporte("Reporte_Resumen_Diario_Recaudacion");
                ss.agregarParametro("ILUSTRE_MUNICIPIO", Utils.ilustreMunicipio);
                ss.agregarParametro("Usuario", session.getName_user());
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                break;
            case 2: //DIARIO
                if (anio == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar el año");
                    return;
                }
                if (numInforme == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar el Numero de Informe");
                    return;
                }
              //  ss.setAgregarReporte(Boolean.TRUE);
                ss.agregarParametro("LOGO2", JsfUti.getRealPath(SisVars.sisLogo1));
                ss.agregarParametro("SUBREPORT_TITLE", Faces.getRealPath("/reportes/").concat("/"));
                ss.setNombreDocumento("InformeRecaudaciones");
                ss.setNombreReporte("informeDiarioRecaudaciones1");
                ss.agregarParametro("USUARIO", session.getName_user());
                ss.agregarParametro("MUNICIPIO", SisVars.NOMBREMUNICIPIO);
                ss.agregarParametro("FECHA", fecha);
                ss.agregarParametro("ANIO", anio);
                ss.agregarParametro("NUM_ACTA", numInforme);
//                    ss.agregarParametro("DETALLE", detalle);
//                    ss.agregarParametro("BOMBEROS", bomberos);
//                    ss.agregarParametro("FONDO_AJENOS", fondosAjenos);
//                    ss.agregarParametro("TOTAL", total);
//                    ss.agregarParametro("TOTAL_BOM", totalBom);
//                    ss.agregarParametro("TOTAL_FON_AJ", totalFonAj);
//                    ss.addParametrosReportes(parametrosReporteAdicional);
//                    ss.addParametrosReportes(parametrosReporteAdicional2);
//                } catch (Exception e) {
//                    LOG.log(Level.SEVERE, null, e);
//                }
                break;
            case 3: // ESPECIES
                if (fechaHasta == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha hasta");
                    return;
                }

                ss.setNombreDocumento("InformeRecaudacionesEspecies");
                ss.setNombreReporte("Reporte_Recaudacion_Especies_V");
                ss.agregarParametro("Usuario", session.getName_user());
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                ss.agregarParametro("SUBREPORT_DIR", path.concat("/reportes/informeDiarios/"));
                break;
            case 4: // ANULADOS
                if (fechaHasta == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar la fecha hasta");
                    return;
                }
                ss.setNombreDocumento("InformeComprobantesAnulados");
                ss.setNombreReporte("Recibo_Comprobante_Anulado");
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                break;
            case 5://REPORTES POR TRANSACCION :V
                ss.setNombreReporte("recaudacionesPorTransaccion");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("FECHA", sdf.format(fechaHasta));
                ss.agregarParametro("DESDE", sdf.format(fecha));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(fechaHasta, 1)));
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
                break;
            case 6://REVISAR ESTSA NOTA YAA QUE ES EL MISMO DEL CASO 5 :( 
                ss.setNombreReporte("recaudacionesPorTransaccion");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("FECHA", sdf.format(fechaHasta));
                ss.agregarParametro("DESDE", sdf.format(fecha));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(fechaHasta, 1)));
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
                break;
            case 7:
                ss.setNombreReporte("recaudacionesTipoLiquidacion");
                ss.agregarParametro("FECHA", fecha);
                ss.agregarParametro("FECHA_HASTA", fecha);
                ss.agregarParametro("OFICIO", oficio);
                paramt = new HashMap<>();
                paramt.put("departamento", new GeDepartamento(8L));
                paramt.put("isDirector", true);
                AclRol rol = (AclRol) manager.findObjectByParameter(AclRol.class, paramt);
                AclUser director = null;
                for (AclUser u : rol.getAclUserCollection()) {
                    if (u.getUserIsDirector() && u.getSisEnabled()) {
                        director = u;
                        break;
                    }
                }
                ss.agregarParametro("DIRECTOR", director == null ? "ASIGNAR" : director.getEnte().getTituloProf() + " " + director.getEnte().getNombres() + " " + director.getEnte().getApellidos());
                paramt = new HashMap<>();
                paramt.put("departamento", new GeDepartamento(20L));
                paramt.put("esSubDirector", true);
                rol = (AclRol) manager.findObjectByParameter(AclRol.class, paramt);
                director = null;
                for (AclUser u : rol.getAclUserCollection()) {
                    if (u.getSisEnabled()) {
                        director = u;
                        break;
                    }
                }
                ss.agregarParametro("TESORERA", director == null ? "ASIGNAR" : Utils.isEmpty(director.getEnte().getTituloProf()) + " " + director.getEnte().getNombres() + " " + director.getEnte().getApellidos());
                break;
            case 8:
                ss.setNombreReporte("desgloseRubrosUrbanoParroquia");
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                break;
            case 9:
                ss.setNombreReporte("desgloseRubrosRurales");
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                break;
            case 10:
                ss.setNombreReporte("desgloseImpuestoUrbano");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("MUNICIPIO", Utils.ilustreMunicipio);
                ss.agregarParametro("FECHA", sdf.format(fecha));
                ss.agregarParametro("USUARIO", session.getUserId());
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
                break;
            case 11:
                ss.setNombreReporte("cierreCaja");
                ss.setNombreSubCarpeta("recaudaciones");
                ss.agregarParametro("FECHA", sdf.format(fecha));
                ss.agregarParametro("USUARIO", session.getUserId());
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/recaudaciones/");
                ss.agregarParametro("NAME_SUP", service.getNameUserByRol(75L)); // ROL SUPERVISOR CAJA
                ss.agregarParametro("NAME_TES", service.getNameUserByRol(104L)); // ROL JEFE TESORERIA
                break;
            case 12:// AGREGAR EL NOMBRE Y EL MAP DE PARAMETROS
                parametrosReporteAdicional.put("nombreReporte", "memoCobrosUrbanosCoactiva");
                parametrosReporteAdicional.put("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                parametrosReporteAdicional.put("LOGO_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
                parametrosReporteAdicional.put("NO_OFICIO", oficio);
                parametrosReporteAdicional.put("USUARIO", session.getName_user());
                parametrosReporteAdicional.put("SUBREPORT_DIR", Faces.getRealPath("/reportes/").concat("/"));
                if (session != null && (session.getUserId() == 1L || session.getUserId() == 144L)) {
                    parametrosReporteAdicional.put("FECHA_CIERRE", fechaCaja);
                } else {
                    parametrosReporteAdicional.put("FECHA_CIERRE", new Date());
                }
                ss.setNombreReporte("cobrosUrbanosEnCoactiva");
                ss.setNombreSubCarpeta("coactiva");
                ss.setAgregarReporte(Boolean.TRUE);
                ss.agregarParametro("MUNICIPIO", Utils.ilustreMunicipio);
                ss.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("FECHA_DESDE", sdf.format(fecha));
                ss.agregarParametro("FECHA_HASTA", sdf.format(fechaHasta));
                ss.agregarParametro("HASTA", sdf.format(Utils.sumarRestarDiasFecha(fechaHasta, 1)));
                ss.agregarParametro("NO_OFICIO", oficio);
                ss.addParametrosReportes(parametrosReporteAdicional);
//                ss.agregarParametro("parametrosReporteAdicional", parametrosReporteAdicional);
                break;
            /*PARA EL REPORTE DESGLOSE URBANO (PARROQUIA)*/
            case 13:
                ss.setNombreReporte("desgloseUrbanoParroquia");
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("ANIO", anio);
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/informeDiarios/");
                break;
            /*CATASTRO PREDIAL URBANO RESUMEN DE VALORES COBRADOS*/
            case 14:
                ss.setNombreReporte("resumenValoresUrbanos");
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fechaHasta);
                break;
            /**
             * BOLETIN
             * DIARIO
             * DE
             * RECAUDACION
             * IMPUESTO
             * DE
             * PARROQUIAS
             * URBANA
             */
            case 15:
                ss.setNombreReporte("boletinDiarioParroquiasUrbanas");
                ss.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "/reportes/informeDiarios/");
                break;

            /*  case 14:
                ss.setNombreReporte("cobroMercadoCCSantaAna");
                ss.agregarParametro("TIPO_REPORTE", 117L);
                break;*/
//            case 15:
//                ss.setNombreReporte("recaudacionesTipoLiquidacion");
//                ss.agregarParametro("MUNICIPIO", Utils.nombreMunicipio);
//                ss.agregarParametro("FECHA", fecha);
//                ss.agregarParametro("FECHA_HASTA", fechaHasta);
//                break;
            case 16:
                ss.setNombreReporte("DesgloseCarpetas");
                ss.agregarParametro("DESDE", fecha);
                ss.setNombreSubCarpeta("recaudaciones");
                ss.setFondoBlanco(false);
                ss.setOnePagePerSheet(true);
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

                break;
            case 17:
                ss.setNombreReporte("totalizadoTipoLiquidacion");
                ss.agregarParametro("FECHA", fecha);
                ss.agregarParametro("FECHA_HASTA", fechaHasta);
                break;
            case 18: //DIARIO
               
                if (numInforme == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar el Numero de Informe");
                    return;
                }
                //ss.setAgregarReporte(Boolean.TRUE);
                ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
                ss.agregarParametro("SUBREPORT_TITLE", Faces.getRealPath("/reportes/").concat("/"));
                ss.setNombreDocumento("InformeRecaudaciones");
                ss.setNombreReporte("informeMensualRecaudaciones");
                ss.agregarParametro("USUARIO", session.getName_user());
                ss.agregarParametro("MUNICIPIO", SisVars.NOMBREMUNICIPIO);
                ss.agregarParametro("FECHA", fecha);
                ss.agregarParametro("FECHA_DESDE", fecha);
                ss.agregarParametro("FECHA_HASTA", fechaHasta);
                ss.agregarParametro("ANIO", anio);
                ss.agregarParametro("NUM_ACTA", numInforme);
                break;
            default:
                break;
        }
        if (excel) {
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

        } else {
            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

        }
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public Integer getNumInforme() {
        return numInforme;
    }

    public void setNumInforme(Integer numInforme) {
        this.numInforme = numInforme;
    }

    public Boolean getDesde() {
        return desde;
    }

    public void setDesde(Boolean desde) {
        this.desde = desde;
    }

    public Boolean getHasta() {
        return hasta;
    }

    public void setHasta(Boolean hasta) {
        this.hasta = hasta;
    }

    public Boolean getInforme() {
        return informe;
    }

    public void setInforme(Boolean informe) {
        this.informe = informe;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getAnioMax() {
        return anioMax;
    }

    public void setAnioMax(Integer anioMax) {
        this.anioMax = anioMax;
    }

    public String getOficio() {
        return oficio;
    }

    public void setOficio(String oficio) {
        this.oficio = oficio;
    }

    public Boolean getOficioRender() {
        return oficioRender;
    }

    public void setOficioRender(Boolean oficioRender) {
        this.oficioRender = oficioRender;
    }

    public Date getFechaCaja() {
        return fechaCaja;
    }

    public void setFechaCaja(Date fechaCaja) {
        this.fechaCaja = fechaCaja;
    }

    /**
     * Creates a new
     * instance of
     * ReportesDiarios
     */
    public ReportesDiarios() {
    }

}
