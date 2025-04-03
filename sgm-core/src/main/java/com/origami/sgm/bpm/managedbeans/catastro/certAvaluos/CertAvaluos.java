/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro.certAvaluos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.catastro.PredioUtil;
import com.origami.sgm.database.Querys;
import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.FormatoReporte;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenSecuenciaNumComprobante;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.CertAvaluosLazy;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;
import util.GroovyUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CertAvaluos extends PredioUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    protected SeqGenMan seq;
    @javax.inject.Inject
    protected CatastroServices catastro;
    protected CatPredioLazy predios;
    @Inject
    protected OmegaUploader omegaUploader;
    protected CertAvaluosLazy certificados;
    protected CatCertificadoAvaluo cert;
    protected List<CatCiudadela> cdlas;
    protected CatEnteLazy solicitantes;
    @Inject
    protected UserSession sess;
    @Inject
    protected ServletSession ss;
    protected boolean contenido = false;
    protected FormatoReporte frep = null;
    protected GroovyUtil gUtil;
    protected CatPredioPropietario cpp;
    protected List<CatPredioPropietario> cppList;
    protected String path, emailDir, emailSolicitante;
    protected MsgFormatoNotificacion msg;
    protected StringBuilder sb;
    protected HashMap<String, Object> params;
    protected Boolean tipoEntidad = false;
    protected Boolean visualizarCert = false;

    ////EXTRAS CDI
    private GroovyUtil groovyUtil;
    private BigDecimal porcentajeDivision, numeroAccion = BigDecimal.ZERO, totalAccion = BigDecimal.ZERO;
    private Boolean desmiembraConstruccion = Boolean.FALSE, desmiembraTerreno = Boolean.FALSE,
            tipoCalculo = Boolean.FALSE, porcentajeAConstruccion = Boolean.FALSE;
    private BigDecimal totalAvaluoTerreno, totalAvaluoConstruccion, areaConstruccionDesmembrar,
            areaSolarDesmembrar, valorXAccion, valorXAccionConstruccion, valorXAccionComercial;

    private List<CatPredio> getListPredioByPropietarios;
    private String porcentajeDetalle;

    String nombresPropietarios = " - ";

    @PostConstruct
    protected void load() {
        try {
            contenido = true;
            visualizarCert = Boolean.TRUE;
            ss.instanciarParametros();
            params = new HashMap<>();
            msg = manager.find(MsgFormatoNotificacion.class, 5L);
            path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            cppList = new ArrayList();
            if (sess != null) {

                predios = new CatPredioLazy("A");
                certificados = new CertAvaluosLazy(true, "frmMain:dtCertificados");
                cdlas = manager.findAllEntCopy(CatCiudadela.class);
                solicitantes = new CatEnteLazy();
                frep = (FormatoReporte) manager.find(Querys.getFormatoxCodigo, new String[]{"codigo"}, new Object[]{"CPU-16"});
                if (frep != null) {
                    gUtil = new GroovyUtil(frep.getFormato());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(CertAvaluos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void getListPrediosByPropietario() {
        Map<String, Object> map = new HashMap<>();
        getListPredioByPropietarios = new ArrayList<>();
        try {
            if (cert.getSolicitante() != null) {
                if (cert.getSolicitante().getId() != null) {
                    map.put("idEnte", cert.getSolicitante().getId());
                    getListPredioByPropietarios = manager.findNamedQuery(Querys.getPrediosByPropietario, map);
                }
            }

        } catch (Exception e) {
            Logger.getLogger(CertAvaluos.class.getName()).log(Level.SEVERE, "getListPrediosByPropietario", e);
        }
        if (getListPredioByPropietarios == null) {
            getListPredioByPropietarios = new ArrayList<>();
        }
    }

    public void nuevo() {
        cert = new CatCertificadoAvaluo();
        cert.setFecha(new Date());
        cert.setEstado(Boolean.TRUE);
        Long comp = null;
        comp = (Long) manager.find(QuerysFinanciero.getRenSecuenciaMaxNumComprobante, new String[]{"anio"}, new Object[]{Utils.getAnio(new Date())});
        if (comp != null) {
            comp = comp + 1L;
            cert.setCodComprobante(BigInteger.valueOf(comp));
        } else {
            comp = 1L;
            cert.setCodComprobante(BigInteger.valueOf(comp));
        }
        sb = new StringBuilder();
    }

    public void selectSolicitante(CatEnte sol) {
        cert.setSolicitante(sol);
        getListPrediosByPropietario();
    }

    public void getDataDirectorAvaluo() {

        AclUser user = (AclUser) manager.find(AclUser.class, sess.getUserId());
        if (user != null && user.getEnte() != null) {
            CatEnte ente = (CatEnte) manager.find(CatEnte.class, user.getEnte().getId());
            cert.setDirCat(ente.getApellidos().toUpperCase() + " " + ente.getNombres().toUpperCase());
        }
    }

    public void selectPredio(CatPredio pred) throws Exception {
        if (cert != null) {
            cleanData();
        }
        if (pred != null) {
            if (cert.getSolicitante() != null) {
                RenLiquidacion liquidacion = getLastPay(pred);
                if (liquidacion == null) {
                    CatCertificadoAvaluo ca = new CatCertificadoAvaluo();
                    ca.setAlicuota(pred.getAlicuotaUtil() == null ? BigDecimal.ZERO + "" : pred.getAlicuotaUtil() + "");
                    ca.setAreaSolar(pred.getAreaSolar() == null ? BigDecimal.ZERO : pred.getAreaSolar());
                    ca.setAreaConstruccion(pred.getAreaDeclaradaConst() == null ? BigDecimal.ZERO : pred.getAreaDeclaradaConst());
                    ca.setAvalConstruccion(pred.getAvaluoConstruccion() == null ? BigDecimal.ZERO : pred.getAvaluoConstruccion());
                    ca.setValorM2(pred.getValorAfectacionCoeficenteTerreno());
                    ca.setAvalSolar(pred.getAvaluoSolar() == null ? BigDecimal.ZERO : pred.getAvaluoSolar());
                    ca.setAvalPropiedad(pred.getAvaluoMunicipal() == null ? BigDecimal.ZERO : pred.getAvaluoMunicipal());
                    ca.setCodigoActual(pred.getCodigoPredialCompleto());
                    ca.setCodigoAnterior(pred.getPredialant());
                    ca.setPredio(pred);
                    ca.setUsuario(sess.getName_user());
                    cert.setPredio(pred);
                    cert.setAlicuota(ca.getAlicuota());
                    cert.setAreaSolar(ca.getAreaSolar());
                    cert.setAreaConstruccion(ca.getAreaConstruccion());
                    cert.setAvalConstruccion(ca.getAvalConstruccion());
                    cert.setAvalSolar(ca.getAvalSolar());
                    cert.setAvalPropiedad(ca.getAvalPropiedad());
                    cert.setValorM2(ca.getValorM2());
                    cert.setCodigoActual(ca.getCodigoActual());
                    cert.setCodigoAnterior(ca.getCodigoAnterior());
                    getDataDirectorAvaluo();
                    contenido = true;
                    ca.setDirCat(cert.getDirCat());
                    gUtil.setProperty("cert", ca);
                    if (cert.getSolicitante().getEsPersona()) {
                        gUtil.setProperty("solicitante", cert.getSolicitante().getApellidos() + " " + cert.getSolicitante().getNombres());
                    } else {
                        gUtil.setProperty("solicitante", cert.getSolicitante().getRazonSocial());
                    }
                    gUtil.setProperty("nombrecanton", SisVars.NOMBRECANTON);
                    if (cert.getDirCat() != null) {
                        gUtil.setProperty("directorcatastro", cert.getDirCat());
                    }

                } else {
                    Faces.messageWarning(null, "Advertencia", "El predio posee una deuda de: " + liquidacion.getSaldo());
                }

            } else {
                Faces.messageWarning(null, "Advertencia", "Debe seleccionar el solicitante y el propietario respectivo");
            }
        }
    }

    public void cleanData() {
        if (cert.getPredio() != null) {
            cert.setPredio(null);
        }
        cert.setAlicuota(null);
        cert.setAreaSolar(null);
        cert.setAvalConstruccion(null);
        cert.setAvalSolar(null);
        cert.setAvalPropiedad(null);
        cert.setCodigoActual(null);
        cert.setCodigoAnterior(null);
        totalAvaluoTerreno = BigDecimal.ZERO;
        areaSolarDesmembrar = BigDecimal.ZERO;
    }

    public RenLiquidacion getLastPay(CatPredio predio) {
        RenLiquidacion liquidacion = (RenLiquidacion) manager.find(QuerysFinanciero.getLiquidacionPendientexAnioPredioTipo, new String[]{"anio", "tipo", "predio"}, new Object[]{Utils.getAnio(new Date()), 13L, predio.getNumPredio()});
        if (liquidacion != null) {
            if (liquidacion.getPredio() != null) {
                if (liquidacion.getPredio().getPropiedad() != null) {
                    if (!liquidacion.getPredio().getPropiedad().getNombre().equals("PUBLICO")
                            || !liquidacion.getPredio().getPropiedad().getNombre().equals("JURISDICCIÓN MUNICIPAL")
                            || !liquidacion.getPredio().getPropiedad().getNombre().equals("ESTADO CENTRAL")) {
                        return liquidacion;
                    } else {
                        return null;
                    }
                }
            }

        }
        return liquidacion;
    }

    public void ver() {
        this.desmiembraTerreno = Boolean.FALSE;
        String nombresPropietarios = "";
        System.out.println("cppList " + cppList.size());
        if (cppList != null && !cppList.isEmpty()) {
            for (CatPredioPropietario cp : cppList) {
                if (cp.getEnte().getEsPersona()) {
                    nombresPropietarios = cp.getEnte().getApellidos() + " " + cp.getEnte().getNombres() + " " + nombresPropietarios;
                } else {
                    nombresPropietarios = cp.getEnte().getRazonSocial() + " " + nombresPropietarios;
                }
            }
            gUtil.setProperty("propietario", nombresPropietarios);
            cert.setDetalle("<div style=\"text-align:justify\">" + gUtil.getExpression("getDetalle", null).toString() + "</div>");
            cert.setTipo("normal");
//            cert.setExtras(cert.getExtras());
            JsfUti.executeJS("PF('dlgCertificado').show()");
        } //        if (cpp != null) {
        //
        //            if (cpp.getEnte().getEsPersona()) {
        //                gUtil.setProperty("propietario", cpp.getEnte().getApellidos() + " " + cert.getSolicitante().getNombres());
        //            } else {
        //                gUtil.setProperty("propietario", cpp.getEnte().getRazonSocial());
        //            }
        //            //cleanData();
        //            cert.setDetalle("<div style=\"text-align:justify\">" + gUtil.getExpression("getDetalle", null).toString() + "</div>");
        //            cert.getExtras().setTipo("normal");
        //            cert.setExtras(cert.getExtras());
        //            JsfUti.executeJS("PF('dlgCertificado').show()");
        //        }
        else {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar el propietario respectivo");
        }
    }

    public void verDialogCalculo() {
        this.desmiembraTerreno = Boolean.TRUE;
        String nombresPropietarios = "";
        if (cppList != null && !cppList.isEmpty()) {
            for (CatPredioPropietario cp : cppList) {
                if (cp.getEnte().getEsPersona()) {
                    nombresPropietarios = cp.getEnte().getApellidos() + " " + cp.getEnte().getNombres() + " " + nombresPropietarios;
                } else {
                    nombresPropietarios = cp.getEnte().getRazonSocial() + " " + nombresPropietarios;
                }
            }
            gUtil.setProperty("propietario", nombresPropietarios);

            //        cleanData();
            areaSolarDesmembrar = new BigDecimal(BigInteger.ZERO);
            cert.setDetalle("<div style=\"text-align:justify\">" + gUtil.getExpression("getDetalle", null).toString() + "</div>");
            cert.setTipo("desmembracion");
            //cert.setExtras(cert.getExtras());
            JsfUti.executeJS("PF('dlgCalculo').show()");
        } else {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar el propietario respectivo");
        }
    }

    public void calculate() {

        if (areaSolarDesmembrar.compareTo(new BigDecimal(BigInteger.ZERO)) == 0) {
            Faces.messageWarning(null, "Advertencia", "El Área a Desmembrar debe ser Mayor a Cero");
        } else {
            int res;
            res = areaSolarDesmembrar.compareTo(cert.getPredio().getAreaSolar());
            if (res == 1) {
                Faces.messageWarning(null, "Advertencia", "El Área a Desmembrar no debe ser Mayor al Area del Solar");
            } else {
                if (cert.getValorM2() != null) {
                    cert.setAreaDesmembrada(areaSolarDesmembrar);
                    //cert.setExtras(cert.getExtras());
                    if (desmiembraConstruccion) {
                        totalAvaluoTerreno = cert.getValorM2().multiply(areaSolarDesmembrar);
                        totalAvaluoTerreno = totalAvaluoTerreno.add(cert.getAvalConstruccion());
                        cert.setIdentPredial(BigInteger.ONE);
                    } else {
                        totalAvaluoTerreno = cert.getValorM2().multiply(areaSolarDesmembrar);
                        cert.setIdentPredial(BigInteger.ZERO);
                    }
                    //cert.setAvalPropiedad(totalAvaluoTerreno);
                } else {
                    Faces.messageWarning(null, "Advertencia", "No existe un valor de m2 registrado para el predio");
                }
                JsfUti.update("frmCalculo");
            }
        }
    }

    public void guardar() {
        try {
            if (cert != null && cert.getId() == null) {
                RenSecuenciaNumComprobante comprobante = new RenSecuenciaNumComprobante();
                cert.setUsuario(sess.getName_user());
                if (cert.getSolicitante().getEnteCorreoCollection() != null && !cert.getSolicitante().getEnteCorreoCollection().isEmpty()) {
                    emailSolicitante = cert.getSolicitante().getEnteCorreoCollection().get(0).getEmail();
                }
                AclUser user = (AclUser) manager.find(AclUser.class, sess.getUserId());
                if (user != null && user.getEnte() != null) {
                    CatEnte ente = (CatEnte) manager.find(CatEnte.class, user.getEnte().getId());
                    cert.setDirCat(ente.getApellidos().toUpperCase() + " " + ente.getNombres().toUpperCase());
                }

                cert = catastro.guardarCertificado(cert);
                comprobante.setNumComprobante(cert.getCodComprobante().longValue());
                comprobante.setAnio(Utils.getAnio(new Date()).longValue());
                comprobante = catastro.saveRenSecuenciaNumComprobante(comprobante);

                if (cert.getId() != null) {
                    String solicitante;
                    if (cert.getSolicitante().getEsPersona()) {
                        solicitante = cert.getSolicitante().getApellidos() + " " + cert.getSolicitante().getNombres();
                    } else {
                        solicitante = cert.getSolicitante().getRazonSocial();
                    }
                    sb.append(msg.getHeader());
                    sb.append("<table style=\"height: 60px;\" width=\"314\">\n <tbody>\n");
                    if (cert.getCodComprobante() != null) {
                        sb.append("<tr>\n <td><strong>No COMPROBANTE</strong></td>\n <td>").append(cert.getCodComprobante().toString()).append("</td>\n </tr>");
                    } else {
                        sb.append("<tr>\n <td><strong>No COMPROBANTE</strong></td>\n <td>").append(cert.getIdentificacion().toString()).append("</td>\n </tr>");
                    }
                    sb.append("<tr>\n <td><strong>FECHA</strong></td>\n <td>").append(cert.getFecha().toString()).append("</td>\n </tr>");
                    sb.append("<tr>\n <td><strong>MATRICULA INMOBILIARIA</strong></td>\n <td>").append(cert.getPredio().getNumPredio()).append("</td>\n </tr>");
                    sb.append("<tr> <td><strong>SOLICITANTE</strong></td> <td>").append(solicitante.toUpperCase()).append("</td></tr>");
                    sb.append("<tr> <td><strong>REALIZADO POR</strong></td> <td>").append(cert.getUsuario().toUpperCase()).append("</td></tr>");
                    sb.append("</tbody>\n </table>");
                    sb.append("<div style=\"width: 550px; height: 10px; background-color: #ffffff;\" align=\"center\">&nbsp;</div>\n"
                            + "<div style=\"width: 550px; height: 10px; background-color: #ffffff;\" align=\"center\">&nbsp;</div>");
                    sb.append("<h2 style=\"width: 550px; height: 10px; background-color: #ffffff;\" align=\"center\"><span style=\"color: #003366;\"><strong><a style=\"color: #003366;\"title=\"Certificado\" href=\"").append(SisVars.urlPublica).append("/certificadoAvaluo?cert=").append(cert.getCodigo()).append("\" target=\"_blank\">Descargar certificado</a></strong></span></h2>");
                    sb.append(msg.getFooter());
                    Faces.executeJS("PF('dlgNuevo').hide()");
                    Faces.executeJS("PF('dlgCertificado').hide()");
                    Faces.messageInfo(null, "Nota", "Certificado No." + cert.getId() + " generado satisfactoriamente.");
                    ss.instanciarParametros();
                    ss.setTieneDatasource(Boolean.TRUE);
                    ss.agregarParametro("id", cert.getId());
                    ss.agregarParametro("logo", path + SisVars.logoReportes);
                    ss.agregarParametro("NOMBRECANTON", path + SisVars.NOMBRECANTON);
                    ss.setNombreSubCarpeta("catastro/San Vicente/certificados/");
                    // CARGAR FOTOS E IMAGEN DEL PREDIO
                    int numFotos = 1;
                    List<FotoPredio> fotos = null;
                    if (cert.getPredio().getPredioRaiz() == null) {
                        fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{cert.getPredio().getId()});
                    } else {
                        fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{cert.getPredio().getPredioRaiz().longValue()});
                    }
                    if (Utils.isNotEmpty(fotos)) {
                        for (FotoPredio foto : fotos) {
                            switch (numFotos) {
                                case 1:
                                    ss.agregarParametro("FachadaFrontal", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                                case 2:
                                    ss.agregarParametro("FachadaIzquierda", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                                case 3:
                                    ss.agregarParametro("FachadaDerecha", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                                case 4:
                                    ss.agregarParametro("FachadaPosterior", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                            }
                            numFotos++;
                        }
                    }
                    if (cert.getTipo().equals("normal")) {
                        ss.setNombreReporte("CertificadoAvaluoPropiedad");
                    } else {
                        if (cert.getTipo().equals("desmembracion")) {
                            ss.setNombreReporte("CertificadoAvaluoPropiedadPorDesmebracion");
                        }
                        if (cert.getTipo().equals("porcentual")) {
                            ss.setNombreReporte("CertificadoAvaluoPropiedadPorcentual");
                        }
                        if (cert.getTipo().equals("accion")) {
                            ss.setNombreReporte("CertificadoAvaluoPropiedadPorAccion");
                        }

                    }

                    if (cert.getPredio().getPredioRaiz() != null) {
                        CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{cert.getPredio().getPredioRaiz()});
                        ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
                    } else {
                        ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + cert.getPredio().getNumPredio());
                    }
                    // FIN DE CARGA DE FOTOS
                    Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                    contenido = true;
                    visualizarCert = Boolean.TRUE;
                }
            } else {
                manager.persist(cert);
                contenido = false;
            }
        } catch (Exception e) {
            Logger.getLogger(CertAvaluos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editar(CatCertificadoAvaluo cert) {
        if (cert != null) {

        }
    }

    public void openDialogPredio() {

    }

    public void imprimir(CatCertificadoAvaluo cert) {
        if (cert != null) {

            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("id", cert.getId());
            ss.agregarParametro("logo", path + SisVars.logoReportes);
            ss.agregarParametro("NOMBRECANTON", path + SisVars.NOMBRECANTON);
            ss.setNombreSubCarpeta("catastro/San Vicente/certificados/");
            // CARGAR FOTOS E IMAGEN DEL PREDIO
            int numFotos = 1;
            List<FotoPredio> fotos = null;
            if (cert.getPredio().getPredioRaiz() == null) {
                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{cert.getPredio().getId()});
            } else {
                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{cert.getPredio().getPredioRaiz().longValue()});
            }
            if (Utils.isNotEmpty(fotos)) {
                for (FotoPredio foto : fotos) {
                    switch (numFotos) {
                        case 1:
                            ss.agregarParametro("FachadaFrontal", omegaUploader.streamFile(foto.getFileOid()));
                            break;
                        case 2:
                            ss.agregarParametro("FachadaIzquierda", omegaUploader.streamFile(foto.getFileOid()));
                            break;
                        case 3:
                            ss.agregarParametro("FachadaDerecha", omegaUploader.streamFile(foto.getFileOid()));
                            break;
                        case 4:
                            ss.agregarParametro("FachadaPosterior", omegaUploader.streamFile(foto.getFileOid()));
                            break;
                    }
                    numFotos++;
                }
            }
            if (cert.getTipo().equals("normal")) {
                ss.setNombreReporte("CertificadoAvaluoPropiedad");
            } else {
                if (cert.getTipo().equals("desmembracion")) {
                    ss.setNombreReporte("CertificadoAvaluoPropiedadPorDesmebracion");
                }
                if (cert.getTipo().equals("porcentual")) {
                    ss.setNombreReporte("CertificadoAvaluoPropiedadPorcentual");
                }
                if (cert.getTipo().equals("accion")) {
                    ss.setNombreReporte("CertificadoAvaluoPropiedadPorAccion");
                }
            }

            if (cert.getPredio().getPredioRaiz() != null) {
                CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{cert.getPredio().getPredioRaiz()});
                ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
            } else {
                ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + cert.getPredio().getNumPredio());
            }

            // FIN DE CARGA DE FOTOS
            Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            contenido = false;
        } else {
            Faces.messageWarning(null, "Advertencia", "No se pudo imprimir el certificado, verificar que los datos esten correctos");
        }
    }

    public void validarComprobante() {
        try {
            if (!tipoEntidad) {
                contenido = true;
                visualizarCert = Boolean.TRUE;
            } else {
                contenido = cert.getIdentificacion() != null;
                visualizarCert = Boolean.TRUE;
            }
        } catch (Exception e) {
            Logger.getLogger(CertAvaluos.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void openDialogVisualizarReporteDesmembrazion() {

        if (areaSolarDesmembrar.compareTo(new BigDecimal(BigInteger.ZERO)) == 0) {
            Faces.messageWarning(null, "Advertencia", "El Área a Desmembrar debe ser Mayor a Cero");
        } else {
            int res;
            res = areaSolarDesmembrar.compareTo(cert.getPredio().getAreaSolar());
            if (res == 1) {
                Faces.messageWarning(null, "Advertencia", "El Área a Desmembrar no debe ser Mayor al Area del Solar");
            } else {
                if (cert.getValorM2() != null) {
                    cert.setAreaDesmembrada(areaSolarDesmembrar);
                    ///cert.setExtras(cert.getExtras());
                    if (desmiembraConstruccion) {
                        totalAvaluoTerreno = cert.getValorM2().multiply(areaSolarDesmembrar);
                        cert.setAvalSolar(totalAvaluoTerreno);
                        totalAvaluoTerreno = totalAvaluoTerreno.add(cert.getAvalConstruccion());
                        cert.setAvalPropiedad(totalAvaluoTerreno);
                        cert.setIdentPredial(BigInteger.ONE);
                    } else {
                        totalAvaluoTerreno = cert.getValorM2().multiply(areaSolarDesmembrar);
                        cert.setAvalConstruccion(BigDecimal.ZERO);
                        cert.setAvalSolar(totalAvaluoTerreno);
                        cert.setAvalPropiedad(totalAvaluoTerreno);
                        cert.setIdentPredial(BigInteger.ZERO);
                    }
                    JsfUti.executeJS("PF('dlgCalculo').hide()");
                    JsfUti.executeJS("PF('dlgCertificado').show()");
                } else {
                    Faces.messageWarning(null, "Advertencia", "No existe un valor de m2 registrado para el predio");
                }
                JsfUti.update("frmCalculo");

            }
        }
    }

    public void openDialogPropietariosByAccion() {
        String nombresPropietarios = "";
        if (cppList != null && !cppList.isEmpty()) {
            for (CatPredioPropietario cp : cppList) {
                if (cp.getEnte().getEsPersona()) {
                    nombresPropietarios = cp.getEnte().getApellidos() + " " + cp.getEnte().getNombres() + " " + nombresPropietarios;
                } else {
                    nombresPropietarios = cp.getEnte().getRazonSocial() + " " + nombresPropietarios;
                }
            }
            gUtil.setProperty("propietario", nombresPropietarios);
            areaSolarDesmembrar = new BigDecimal(BigInteger.ZERO);
            cert.setTipo("porcentual");
            //   cert.setExtras(cert.getExtras());
            JsfUti.executeJS("PF('dlgPropietariosByAccion').show()");
        } else {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar el propietario respectivo");
        }
    }

    public void openDialogVisualizarCertificadoByAcciones() {
        if (tipoCalculo) {
            if (numeroAccion == null) {
                numeroAccion = BigDecimal.ZERO;
            }
            if (numeroAccion.compareTo(BigDecimal.ZERO) > 0) {
                for (CatPredioPropietario cpp : cert.getPredio().getCatPredioPropietarioCollection()) {
                    if (cpp.getEnte().getEsPersona()) {
                        nombresPropietarios = cpp.getEnte().getApellidos() + " " + cpp.getEnte().getNombres() + nombresPropietarios;
                    } else {
                        nombresPropietarios = cpp.getEnte().getRazonSocial() + nombresPropietarios;
                    }

                }
                Double totalXAccion = cert.getAvalPropiedad().divide(totalAccion, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double total = 0.0;
                for (int i = 1; i <= numeroAccion.intValue(); i++) {
                    total = total + totalXAccion;
                }
                cert.setAvaluoPorAccion(new BigDecimal(total));
                cert.setTotalAccciones(totalAccion.intValue());
                cert.setNumeroAccciones(numeroAccion.intValue());
            } else {
                Faces.messageWarning(null, "Advertencia", "El  Número de Acciones debe ser mayor a Cero");
            }
        } else {

            for (CatPredioPropietario propietario : cert.getPredio().getCatPredioPropietarioCollection()) {
                if (propietario.getEnte().getEsPersona()) {
                    nombresPropietarios
                            = propietario.getEnte().getApellidos()
                            + " " + propietario.getEnte().getNombres()
                            + "(" + propietario.getPorcentajeAvalDivision() != null ? propietario.getPorcentajeAvalDivision().toString() : "" + "%)" + nombresPropietarios;
                } else {
                    nombresPropietarios = propietario.getEnte().getRazonSocial()
                            + "(" + propietario.getPorcentajeAvalDivision() != null ? propietario.getPorcentajeAvalDivision().toString() : "" + "%)"
                            + nombresPropietarios;
                }

            }

            List<CatPredioPropietario> catPredioPropietariosList = new ArrayList(cert.getPredio().getCatPredioPropietarioCollection());
            if (cert.getPredio().getCatPredioPropietarioCollection().contains(cpp)) {
                Integer index = catPredioPropietariosList.indexOf(cpp);
                if (index != null) {
                    CatPredioPropietario cppTemp = catPredioPropietariosList.get(index);
                    if (cppTemp.getPorcentajeAvalDivision() != null) {
                        this.porcentajeDetalle = cppTemp.getPorcentajeAvalDivision().toString();
                        cert.setPorcentajeAvalDivision(cppTemp.getPorcentajeAvalDivision());
                        // cert.setExtras(cert.getExtras());
                        valorXAccion = cert.getAvalSolar().multiply(cppTemp.getPorcentajeAvalDivision());
                        valorXAccion = valorXAccion.divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP);
                        if (porcentajeAConstruccion) {
                            valorXAccionConstruccion = cert.getAvalConstruccion().multiply(cppTemp.getPorcentajeAvalDivision());
                            valorXAccionConstruccion = valorXAccionConstruccion.divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_UP);
                            valorXAccionComercial = valorXAccion.add(valorXAccionConstruccion);
                        } else {
                            valorXAccionComercial = valorXAccion;
                        }

                    } else {
                        Faces.messageWarning(null, "Advertencia", "Los valores ingresados deben ser mayores a Cero");
                    }

                }
            }

        }

    }

    public void getReportFormat() {
        FormatoReporte fr = (FormatoReporte) manager.find(Querys.getFormatoxCodigo, new String[]{"codigo"}, new Object[]{"CPU-17"});
        if (frep != null) {
            this.groovyUtil = new GroovyUtil(fr.getFormato());
        }
    }

    public void acceptDivisionPorcentual() {
        if (cpp == null || cpp.getId() == null) {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar el beneneficiario");
            return;
        }
        ///PORCENTUAL :D :D 
        if (!tipoCalculo) {
            cert.setDetalle("<div style=\"text-align:justify\">" + getDetalleCalculoPorAcciones()
                    + " <br></br><b>NOTA: </b> El Avalúo correspondiente al  "
                    + this.porcentajeDetalle
                    + "% de Derechos y Acciones del Total del 100% de Acciones pertenecientes al Sr(a). "
                    + cpp.getEnte().getNombreCompleto()
                    + ".  </div>");
            if (porcentajeAConstruccion) {
                cert.setAvalSolar(valorXAccion);
                cert.setAvalConstruccion(valorXAccionConstruccion);
                cert.setAvalPropiedad(valorXAccion.add(valorXAccionConstruccion));
            } else {
                cert.setAvalSolar(valorXAccion);
                cert.setAvalPropiedad(valorXAccion);
            }
            JsfUti.executeJS("PF('dlgPropietariosByAccion').hide()");
            JsfUti.executeJS("PF('dlgCertificadoPorcentual').show()");
        } //# DE ACCIONES
        else {
            cert.setTipo("accion");
            cert.setDetalle("<div style=\"text-align:justify\">" + getDetalleCalculoPorAcciones() + " <br></br><b>NOTA: </b> El Avalúo correspondiente a la venta de <b>"
                    + (cert.getNumeroAccciones() < 10 ? "0" + cert.getNumeroAccciones() : cert.getNumeroAccciones())
                    + "</b>  de Derechos y Acciones  a favor de <b>"
                    + cpp.getEnte().getNombreCompleto()
                    + ".</div>");
            JsfUti.executeJS("PF('dlgPropietariosByAccion').hide()");
            JsfUti.executeJS("PF('dlgCertificadoPorcentual').show()");
        }

    }

    public String getDetalleCalculoPorAcciones() {
        AclUser user = (AclUser) manager.find(AclUser.class, sess.getUserId());

        String parroquia, dciudadela, conjunto, director = "";
        if (user != null && user.getEnte() != null) {
            CatEnte ente = (CatEnte) manager.find(CatEnte.class, user.getEnte().getId());
            director = ente.getApellidos().toUpperCase() + " " + ente.getNombres().toUpperCase();
        }
        try {
            if (cert.getPredio().getCiudadela() != null && cert.getPredio().getCiudadela().getCodTipoConjunto() != null) {
                conjunto = cert.getPredio().getCiudadela().getCodTipoConjunto().getNombre();
                dciudadela = cert.getPredio().getCiudadela().getNombre();
            } else {
                conjunto = "";
                dciudadela = "";
            }
            if (cert.getPredio().getParroquia() == 50) {
                parroquia = "SAN VICENTE";
            } else {
                parroquia = "CANOA";
            }

            return "<b>ING. " + director + ", ANALISTA DE LA SECCIÓN DE AVALÚOS Y CATASTRO MUNICIPAL </b> "
                    + "EN LEGAL USO DE SUS FUNCIONES <b>CERTIFICA</b> QUE EL(LA) SEÑOR(A): " + nombresPropietarios
                    + " POSEE Y SON CONDUEÑOS DE LOS DERECHOS Y ACCIONES UNA PROPIEDAD <b>URBANA</b> UBICADA EN: <b> "
                    + "" + conjunto + ": " + dciudadela + " CALLE(S): " + cert.getPredio().getCalleS() + " </b>  "
                    + "DE LA PARROQUIA " + parroquia + ", DEL CANTÓN SAN VICENTE.";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public Boolean getDesmiembraConstruccion() {
        return desmiembraConstruccion;
    }

    public void setDesmiembraConstruccion(Boolean desmiembraConstruccion) {
        this.desmiembraConstruccion = desmiembraConstruccion;
    }

    public BigDecimal getTotalAvaluoTerreno() {
        return totalAvaluoTerreno;
    }

    public void setTotalAvaluoTerreno(BigDecimal totalAvaluoTerreno) {
        this.totalAvaluoTerreno = totalAvaluoTerreno;
    }

    public BigDecimal getTotalAvaluoConstruccion() {
        return totalAvaluoConstruccion;
    }

    public void setTotalAvaluoConstruccion(BigDecimal totalAvaluoConstruccion) {
        this.totalAvaluoConstruccion = totalAvaluoConstruccion;
    }

    public BigDecimal getPorcentajeDivision() {
        return porcentajeDivision;
    }

    public void setPorcentajeDivision(BigDecimal porcentajeDivision) {
        this.porcentajeDivision = porcentajeDivision;
    }

    public BigDecimal getAreaSolarDesmembrar() {
        return areaSolarDesmembrar;
    }

    public void setAreaSolarDesmembrar(BigDecimal areaSolarDesmembrar) {
        this.areaSolarDesmembrar = areaSolarDesmembrar;
    }

    public BigDecimal getAreaConstruccionDesmembrar() {
        return areaConstruccionDesmembrar;
    }

    public void setAreaConstruccionDesmembrar(BigDecimal areaConstruccionDesmembrar) {
        this.areaConstruccionDesmembrar = areaConstruccionDesmembrar;
    }

    public List<CatPredio> getGetListPredioByPropietarios() {
        return getListPredioByPropietarios;
    }

    public void setGetListPredioByPropietarios(List<CatPredio> getListPredioByPropietarios) {
        this.getListPredioByPropietarios = getListPredioByPropietarios;
    }

    public Boolean getDesmiembraTerreno() {
        return desmiembraTerreno;
    }

    public void setDesmiembraTerreno(Boolean desmiembraTerreno) {
        this.desmiembraTerreno = desmiembraTerreno;
    }

    public Boolean getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Boolean tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public BigDecimal getNumeroAccion() {
        return numeroAccion;
    }

    public void setNumeroAccion(BigDecimal numeroAccion) {
        this.numeroAccion = numeroAccion;
    }

    public Boolean getPorcentajeAConstruccion() {
        return porcentajeAConstruccion;
    }

    public void setPorcentajeAConstruccion(Boolean porcentajeAConstruccion) {
        this.porcentajeAConstruccion = porcentajeAConstruccion;
    }

    public BigDecimal getValorXAccion() {
        return valorXAccion;
    }

    public void setValorXAccion(BigDecimal valorXAccion) {
        this.valorXAccion = valorXAccion;
    }

    public BigDecimal getValorXAccionConstruccion() {
        return valorXAccionConstruccion;
    }

    public void setValorXAccionConstruccion(BigDecimal valorXAccionConstruccion) {
        this.valorXAccionConstruccion = valorXAccionConstruccion;
    }

    public BigDecimal getValorXAccionComercial() {
        return valorXAccionComercial;
    }

    public void setValorXAccionComercial(BigDecimal valorXAccionComercial) {
        this.valorXAccionComercial = valorXAccionComercial;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public CertAvaluosLazy getCertificados() {
        return certificados;
    }

    public void setCertificados(CertAvaluosLazy certificados) {
        this.certificados = certificados;
    }

    public CatCertificadoAvaluo getCert() {
        return cert;
    }

    public void setCert(CatCertificadoAvaluo cert) {
        visualizarCert = Boolean.FALSE;
        this.cert = cert;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<CatCiudadela> getCdlas() {
        return cdlas;
    }

    public void setCdlas(List<CatCiudadela> cdlas) {
        this.cdlas = cdlas;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public boolean getContenido() {
        return contenido;
    }

    public void setContenido(boolean contenido) {
        this.contenido = contenido;
    }

    public CatPredioPropietario getCpp() {
        return cpp;
    }

    public void setCpp(CatPredioPropietario cpp) {
        this.cpp = cpp;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public String getEmailSolicitante() {
        return emailSolicitante;
    }

    public void setEmailSolicitante(String emailSolicitante) {
        this.emailSolicitante = emailSolicitante;
    }

    public Boolean getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(Boolean tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public Boolean getVisualizarCert() {
        return visualizarCert;
    }

    public void setVisualizarCert(Boolean visualizarCert) {
        this.visualizarCert = visualizarCert;
    }

    public List<CatPredioPropietario> getCppList() {
        return cppList;
    }

    public void setCppList(List<CatPredioPropietario> cppList) {
        this.cppList = cppList;
    }

    public BigDecimal getTotalAccion() {
        return totalAccion;
    }

    public void setTotalAccion(BigDecimal totalAccion) {
        this.totalAccion = totalAccion;
    }

}
