/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.app.AppConfig;
import com.origami.config.MainConfig;
import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.historic.Predio;
import com.origami.sgm.entities.models.EstadosPredio;
import com.origami.sgm.events.EliminacionPredioPost;
import com.origami.sgm.geo.GeodataService;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.lazymodels.HistoricoPredioLazy;
import com.origami.sgm.lazymodels.PropietariosLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.reportes.ReportesView;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.catastro.FusionDivisionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author EquipoOrigami
 */
@Named
@ViewScoped
public class GestionPredios extends PredioUtil implements Serializable {

    @Inject
    protected UserSession sess;
    @javax.inject.Inject
    protected CatastroServices catast;
    @Inject
    protected AppConfig appconfig;

    @Inject
    protected ServletSession ss;
    @Inject
    protected ReportesView reportes;
    protected PdfReporte reporte;
    protected int optFiltro;
    protected Boolean filtro, act = false;
    protected Boolean isExcel = Boolean.FALSE;
    protected CatCiudadela ciudadela;
    private CtlgItem usoSolar;
    protected List<CatCiudadela> ciudadelas;
    protected CatPredio predio;
    protected CatPredio p;
    protected Date fecha;
    protected Date fecha_hasta;
    protected CatPredioLazy predios;
    protected HistoricoPredioLazy historico;
    protected String nombres, apellidos, rsocial, ciruc;
    protected HashMap<String, Object> params;
    protected List<CatPredio> predSel;
    @javax.inject.Inject
    protected Entitymanager manager;
    protected Integer minimo, maximo;
    protected CatPredioModel predioModel = new CatPredioModel();
    protected PropietariosLazy propietarios;
    protected CatPredioPropietario propietario;
    protected CatEnte contribuyenteConsulta;
    protected List<CatEnte> contribuyenteList;
    protected static final long serialVersionUID = 1L;
    protected CatParroquia parroquia;
    protected Integer anio = Utils.getAnio(new Date());
    protected Integer tipoReporte;
    protected List<AclUser> usuarios;
    protected AclUser user;

    protected Long canton;
    //VARIABLES DIVISIO DE PREDIOS
    protected ArrayList<CatPredio> predioDivision, prediosResultantes;
    protected BigDecimal areaSolar = null;
    protected Integer solar, unidad;

    @Inject
    protected OmegaUploader omegaUploader;
    @javax.inject.Inject
    protected GeodataService geodataService;
    @javax.inject.Inject
    protected FusionDivisionServices fusionDivisionEjb;

    protected boolean accionDivisionFusion;
    protected MainConfig config;
    protected String tipoPredio = "U";

    protected String messageConfirm;
    protected Long numPrediosActivos, numPredios, numPrediosInactivos, numPrediosHistorico, numPrediosPrivados, numPrediosPublicos;
    protected BigDecimal avaluosTerrenos, avaluosConstruccion, avaluosPropiedad;

    private CatTipoConjunto tipoConjunto;

    @Inject
    protected Event<EliminacionPredioPost> eventEliminarPredio;

    protected CatEnteLazy solicitantes;

    @PostConstruct
    protected void load() {
        messageConfirm = " ¿ Esta seguro que desea eliminar el predio seleccionado ? ";
        try {
            if (sess != null) {
                solicitantes = new CatEnteLazy();
                solar = 0;
                unidad = 0;
                params = new HashMap<>();
                optFiltro = 4;
                filtro = true;
                ss.instanciarParametros();
                reporte = new PdfReporte();
                ciudadelas = manager.findAllEntCopy(CatCiudadela.class);
                propietarios = new PropietariosLazy();
                p = new CatPredio();
                usuarios = manager.findAllEntCopy(AclUser.class);
                fecha = new Date();
                fecha_hasta = new Date();
                predioDivision = new ArrayList<>();
                prediosResultantes = new ArrayList<>();
                accionDivisionFusion = true;
                config = new MainConfig();
                predios = new CatPredioLazy();
                predios.setTipoPredio(tipoPredio);
                parroquia = new CatParroquia();
                getTotalesPredios();
                contribuyenteList = new ArrayList<>();
                this.setUsr(sess.getName_user());
            }
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void getTotalesPredios() {
        numPredios = (Long) manager.find(Querys.numPredios);
        numPrediosActivos = (Long) manager.find(Querys.numPrediosActivos);
        numPrediosInactivos = (Long) manager.find(Querys.numPrediosInactivos);
        numPrediosHistorico = (Long) manager.find(Querys.numPrediosHistorico);
        avaluosTerrenos = (BigDecimal) manager.find(Querys.getTotalesAvaluosTerrenos);
        avaluosConstruccion = (BigDecimal) manager.find(Querys.getTotalesAvaluosConstruccion);
        avaluosPropiedad = (BigDecimal) manager.find(Querys.getTotalesAvaluosPropiedad);
        numPrediosPrivados = (Long) manager.find(Querys.getTotalesPrediosPrivados, new String[]{"nombre"}, new Object[]{"PRIVADO" + "%"});
        numPrediosPublicos = (Long) manager.find(Querys.getTotalesPrediosPublicos, new String[]{"nombre"}, new Object[]{"PUBLICO" + "%"});
    }

    public void filtrar() {
        filtro = optFiltro == 1;
    }

    public void predioCiudadeleas() {
        if (ciudadela != null) {
            try {
                ss.instanciarParametros();
                ss.agregarParametro("id_ciudadela", ciudadela.getId());
                ss.agregarParametro("SUBREPORT_DIR", Faces.getRealPath("/") + "reportes/catastro/");
                ss.setTieneDatasource(Boolean.TRUE);
                ss.setNombreReporte("/catastro/predios_por_ciudadela");
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            } catch (Exception ex) {
                Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void imprimirCatastroPredial() throws IOException {
        List<CatPredio> getListPredios = manager.findAllEntCopy(Querys.getFichaCatastral);
        HttpServletResponse response = reportes.getResponse();
        int x = 0;
        String rutaRepo = "";
        String slash = "/";
        for (CatPredio pre : getListPredios) {

            rutaRepo = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

            File ruta = new File(SisVars.rutaRepotiorioFichas + pre.getParroquia().toString() + "-"
                    + pre.getZona().toString() + "-" + pre.getSector().toString() + "-" + pre.getMz().toString());

            if (!ruta.exists()) {
                System.out.println("El Directorio " + ruta.getName() + " no Existe");
                if (ruta.mkdir()) {
                    System.out.println("Directorio  Creado Correctamente");

                    imprimirFicha(pre, response, ruta.toString() + slash);
                } else {
                    System.out.println("No se ha  Crear Correctamente el Directorio " + ruta.getName());
                    imprimirFicha(pre, response, ruta.toString() + slash);
                }
            } else {
                imprimirFicha(pre, response, ruta.toString() + slash);
                System.out.println("VAMO POR EL: " + x++);
            }
            imprimirFicha(pre, response, ruta.toString() + slash);
        }
    }

    public void imprimirFicha(CatPredio p, HttpServletResponse response, String ruta) {
        try {
            if (p != null) {
                ss.borrarDatos();
                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                int numFotos = 1;
                List<FotoPredio> fotos = null;
                if (p.getPredioRaiz() == null) {
                    fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{p.getId()});
                } else {
                    fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{p.getPredioRaiz().longValue()});
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
                if (p.getClaveReordenada() != null) {

                    ss.agregarParametro("IMAGEN_PREDIO", geodataService.getUrlPredioImage(p.getClaveReordenada().getClaveCat(), null, null));
                    ss.agregarParametro("COLINDANTES", geodataService.getUrlColindantesImage(p.getClaveReordenada().getClaveCat(), null, null));
                } else {
                    ss.agregarParametro("IMAGEN_PREDIO", geodataService.getUrlPredioImage(p.getClaveCat(), null, null));
                    ss.agregarParametro("COLINDANTES", geodataService.getUrlColindantesImage(p.getClaveCat(), null, null));
                }
                // ss.agregarParametro("MUNICIPIO", Utils.nombreMunicipio);

                ss.agregarParametro("predio", p.getId());
                ss.agregarParametro("LOGO", Faces.getRealPath(SisVars.sisLogo));
                ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + p.getNumPredio());
                ss.setNombreReporte(p.getSolar() + "-" + p.getNumPredio());
                ss.agregarParametro("SUBREPORT_DIR", path + "reportes//catastro//San Vicente//");
                ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//");

                List<CatPredioEdificacion> ed = new ArrayList<CatPredioEdificacion>();
                ed = manager.findAll(Querys.getCatPredioEdificacionByPredio, new String[]{"predio"}, new Object[]{p.getId()});

                int cont = 0;
                if (Utils.isNotEmpty(ed)) {
                    for (CatPredioEdificacion e : ed) {
                        cont++;
                    }
                }
                if (cont > 20) {
                    ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/fichaMiduviExtendidoII.jasper", ss.getParametros()));
                }
                if (cont >= 9 && cont <= 20) {
                    ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/fichaMiduviExtendido.jasper", ss.getParametros()));
                }
                if (cont < 9) {
                    ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/fichaMiduvi.jasper", ss.getParametros()));
                }

                reportes.downloadPDFarregloBytesConsecutive(ss.getReportePDF(), response, ruta);

            }
        } catch (SQLException | IOException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirFicha(Boolean controlCroquis) {
        if (predio != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro/San Vicente");
            int numFotos = 1;
            List<FotoPredio> fotos = null;
            if (predio.getPredioRaiz() == null) {
                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()});
            } else {
                fotos = manager.findAll(Querys.getFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getPredioRaiz().longValue()});
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
                }
                ss.agregarParametro("predio", predio.getId());
                ss.setNombreReporte("Ficha predial " + predio.getNumPredio());
                ss.setTieneDatasource(Boolean.TRUE);
                if (predio.getPredioRaiz() != null) {
                    CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{predio.getPredioRaiz()});
                    ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
                } else {
                    ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predio.getNumPredio());
                }
            }
            ss.agregarParametro("predio", predio.getId());
            //ss.setNombreReporte("Ficha predial " + predio.getNumPredio());
            if (predio.getPredioRaiz() != null) {
                System.out.println("SisVars.URLPLANOIMAGENPREDIO " + SisVars.URLPLANOIMAGENPREDIO);
                CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{predio.getPredioRaiz()});
                ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
            } else {
                System.out.println("SisVars.URLPLANOIMAGENPREDIO " + SisVars.URLPLANOIMAGENPREDIO);
                ss.agregarParametro("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predio.getNumPredio());
            }
            ss.agregarParametro("LOGO", path + SisVars.sisLogo);
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.agregarParametro("SUBREPORT_DIR", path + "reportes/catastro/San Vicente/");
            if (!controlCroquis) {
                List<CatPredioEdificacion> ed = new ArrayList<CatPredioEdificacion>();
                ed = manager.findAll(Querys.getCatPredioEdificacionByPredio, new String[]{"predio"}, new Object[]{predio.getId()});

                int cont = 0;
                if (Utils.isNotEmpty(ed)) {
                    for (CatPredioEdificacion e : ed) {
                        cont++;
                    }
                }
                if (cont > 20) {
                    ss.setNombreReporte("fichaMiduviExtendidoII");
                    //ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/fichaMiduviExtendidoII.jasper", ss.getParametros()));
                }
                if (cont >= 9 && cont <= 20) {
                    ss.setNombreReporte("fichaMiduviExtendido");
                    //ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/fichaMiduviExtendido.jasper", ss.getParametros()));
                }
                if (cont < 9) {
                    ss.setNombreReporte("fichaMiduvi");
                    //ss.setReportePDF(reporte.generarPdf("reportes/catastro/San Vicente/fichaMiduvi.jasper", ss.getParametros()));
                }
                this.saveHistoric(predio, "IMPRESIÒN DE FICHA CATASTRAL", null, null, null, null, Boolean.FALSE);
            } else {
                ss.setNombreReporte("croquis");
                this.saveHistoric(predio, "IMPRESIÒN DE CROQUIS CATASTRAL", null, null, null, null, Boolean.FALSE);
            }
            JsfUti.redirectNewTab("/sgmEE/Documento");
            //reportes.descargarPDFarregloBytes(ss.getReportePDF());
        }
    }

    public void generarReportePrediosByUsos(Boolean excel) throws IOException, SQLException {
        if (p != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro//");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            List<Short> codParroquiaList = new ArrayList<>();
            if (parroquia == null) {
                for (CatParroquia codParroquia : this.getParroquias()) {
                    codParroquiaList.add(codParroquia.getCodigoParroquia().shortValue());
                }
                ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
            } else {
                if (parroquia.getCodigoParroquia() == null) {
                    codParroquiaList.add(parroquia.getCodNac());
                    ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
                } else {
                    codParroquiaList.add(parroquia.getCodigoParroquia().shortValue());
                    ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
                }
            }

            if (p != null) {
                List<Short> zonaList = new ArrayList<>();
                List<Short> mzList = new ArrayList<>();
                List<Short> sectorList = new ArrayList<>();
                if (p.getZona() != null) {
                    zonaList.clear();
                    if (p.getZona() == 0) {
                        zonaList = manager.findAll(Querys.getZonas);
                    } else {
                        zonaList.add(p.getZona());
                        ss.agregarParametro("ZONACOLL", zonaList);
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor de la Zona");
                }
                if (p.getSector() != null) {
                    sectorList.clear();
                    if (p.getSector() == 0) {
                        sectorList = manager.findAll(Querys.getSector);
                    } else {

                        sectorList.add(p.getSector());
                        ss.agregarParametro("SECTORCOLL", sectorList);
                    }

                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor del Sector");
                }
                if (p.getMz() != null) {
                    mzList.clear();
                    if (p.getMz() == 0) {
                        mzList = manager.findAll(Querys.getMz);
                        ss.agregarParametro("MZLIST", mzList);
                    } else {
                        mzList.add(p.getMz());
                        ss.agregarParametro("MZLIST", mzList);
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor de la Manzana");
                }

            }
            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.setNombreReporte("informePrediosByUsos");
//            ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/informePredios.jasper", ss.getParametros()));

            //reportes.descargarPDFarregloBytes(ss.getReportePDF());
            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

            }
//            JsfUti.redirectNewTab("/sgmEE/Documento");
        }
    }

    public void generarReportePredios(Boolean excel) throws IOException, SQLException {
        if (p != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro/San Vicente/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            List<Short> codParroquiaList = new ArrayList<>();
            if (parroquia == null) {
                for (CatParroquia codParroquia : this.getParroquias()) {
                    codParroquiaList.add(codParroquia.getCodigoParroquia().shortValue());
                }
                ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
            } else {
                if (parroquia.getCodigoParroquia() == null) {
                    codParroquiaList.add(parroquia.getCodNac());
                    ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
                } else {
                    codParroquiaList.add(parroquia.getCodigoParroquia().shortValue());
                    ss.agregarParametro("PARROQUIACOLL", codParroquiaList);
                }
            }

            if (p != null) {
                List<Short> zonaList = new ArrayList<>();
                List<Short> mzList = new ArrayList<>();
                List<Short> sectorList = new ArrayList<>();
                if (p.getZona() != null) {
                    zonaList.clear();
                    if (p.getZona() == 0) {
                        zonaList = manager.findAll(Querys.getZonas);
                    } else {
                        zonaList.add(p.getZona());
                        ss.agregarParametro("ZONACOLL", zonaList);
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor de la Zona");
                }
                if (p.getSector() != null) {
                    sectorList.clear();
                    if (p.getSector() == 0) {
                        sectorList = manager.findAll(Querys.getSector);
                    } else {

                        sectorList.add(p.getSector());
                        ss.agregarParametro("SECTORCOLL", sectorList);
                    }

                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor del Sector");
                }
                if (p.getMz() != null) {
                    System.out.println("getMz" + p.getMz());
                    mzList.clear();
                    if (p.getMz() == 0) {
                        mzList = manager.findAll(Querys.getMz);
                        ss.agregarParametro("MZLIST", mzList);
                    } else {
                        mzList.add(p.getMz());
                        ss.agregarParametro("MZLIST", mzList);
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Debe Ingresar el valor de la Manzana");
                }

            }
            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.setNombreReporte("informePredios");
            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
            }
        }
    }

    public void generarReportePrediosByCiudadela(Boolean excel) throws IOException, SQLException {
        if (p != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro/San Vicente/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("CIUDADELA", ciudadela.getId());
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.setNombreReporte("informePrediosCiudadela");

            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

            }
//            JsfUti.redirectNewTab("/sgmEE/Documento");
        }
    }
    
    public void generarReportePrediosByUsoSolar(Boolean excel) throws IOException, SQLException {
        if (p != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro/San Vicente/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("CIUDADELA", usoSolar.getId());
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.setNombreReporte("informePredioUsoSuelo");

            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

            }
//            JsfUti.redirectNewTab("/sgmEE/Documento");
        }
    }

    public void generarReportePrediosPropietarios(Boolean excel) throws IOException, SQLException {
        if (p != null) {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("catastro/San Vicente/");
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            List<Long> prediosList = new ArrayList<>();
            Long id;
            if (p.getMz() != null) {
                System.out.println("getMz" + p.getMz());
                prediosList.clear();
                for (CatEnte ente : contribuyenteList) {
                    id = (Long) manager.find(Querys.getPredioIdByEntes, new String[]{"ente"}, new Object[]{ente.getId()});
                    if (id != null) {
                        prediosList.add(id);
                    }
                }

                for (Long l : prediosList) {
                    System.out.println("Long" + l);
                }
                ss.agregarParametro("PREDIOSLIST", prediosList);

            }

            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes/");
            ss.setNombreReporte("informePrediosPropietarios");
//            ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Vicente/informePredios.jasper", ss.getParametros()));

            //reportes.descargarPDFarregloBytes(ss.getReportePDF());
            if (excel) {
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");

            } else {
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");

            }
//            JsfUti.redirectNewTab("/sgmEE/Documento");
        }
    }

    public void seleccionar() throws IOException, SQLException {
        if (contribuyenteList == null || contribuyenteList.isEmpty()) {
            Faces.messageInfo(null, "Mensaje", "DEBE SELECCIONAR CONTRIBUYENTES.");
        } else {
            Faces.messageInfo(null, "Mensaje", "Contribuyente seleccionado.");
            JsfUti.executeJS("PF('dlgSolicitanteCertificado').hide();");
            generarReportePrediosPropietarios(this.isExcel);
        }
    }

    public void reporteModificacionesxUsuario() {

        try {
            if (p != null) {
                ss.borrarDatos();
                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                if (user == null) {
                    ss.agregarParametro("USUARIO", 0);
                } else {
                    ss.agregarParametro("USUARIO", Integer.parseInt(user.getId().toString()));
                }
                ss.setNombreReporte("informePrediosxUsuario");
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fecha_hasta);

                ss.agregarParametro("LOGO", Faces.getRealPath(SisVars.logoReportes));
                ss.agregarParametro("LOGO2", Faces.getRealPath(SisVars.sisLogo1));
//                 ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//catastro//");
                ss.agregarParametro("SUBREPORT_DIR", path + "reportes//");
                ss.setReportePDF(reporte.generarPdf("/reportes/catastro/Ibarra/informePrediosxUsuario.jasper", ss.getParametros()));
                reportes.descargarPDFarregloBytes(ss.getReportePDF());

            }
        } catch (IOException | NumberFormatException | SQLException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reporteModificacionesxFecha() {
        try {

            if (p != null) {

                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("HASTA", fecha_hasta);
                ss.agregarParametro("LOGO", Faces.getRealPath(SisVars.sisLogo));
                ss.agregarParametro("LOGO2", Faces.getRealPath(SisVars.sisLogo1));
                ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//");
                ss.setNombreReporte("informeModificacionesxFecha");
                ss.setAgregarReporte(Boolean.FALSE);
                reporte.setServletSession(ss);
                ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Miguel/informePrediosMods.jasper", ss.getParametros()));
                reportes.descargarPDFarregloBytes(ss.getReportePDF());
            }
        } catch (IOException | SQLException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String nombresPropietarios(CatPredio pt) {
        if (pt == null) {
            return null;
        }
        return pt.getNombrePropietarios();
    }

    public void imprimirReporte() {
        switch (this.tipoReporte) {
            case 1:
                this.reportePredioUsuario();
                break;
            case 2:
                this.reporteModificacionesxUsuario();
                break;
            case 3:
                this.reporteModificacionesxFecha();
                break;
            case 4:
                this.reporteModificacionesxFecha();
                break;
        }
    }

    public void reporteCatastro() {
        try {
            if (p != null) {

                ss.instanciarParametros();
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                if (parroquia == null) {
                    ss.agregarParametro("PARROQUIA", null);
                } else {
                    ss.agregarParametro("PARROQUIA", parroquia.getCodigoParroquia().shortValue());
                }
                ss.agregarParametro("SECTOR", p.getSector());
                ss.agregarParametro("MZ", p.getMz());
                ss.agregarParametro("ANIO", anio);
                ss.agregarParametro("LOGO", Faces.getRealPath(SisVars.sisLogo));
                ss.agregarParametro("LOGO2", Faces.getRealPath(SisVars.sisLogo1));
                ss.setNombreReporte("ReporteCatastro" + "");
                ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//");
                ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Miguel/informeCatastroPrediosUrb.jasper", ss.getParametros()));
                reportes.descargarPDFarregloBytes(ss.getReportePDF());

            }
        } catch (SQLException | IOException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reportePredioUsuario() {
        try {
            if (p != null) {

                ss.borrarDatos();
                ss.instanciarParametros();
                ss.setNombreSubCarpeta("catastro//San Miguel");
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
                if (user == null) {
                    ss.agregarParametro("USUARIO", 0);
                } else {
                    ss.agregarParametro("USUARIO", Integer.parseInt(user.getId().toString()));
                }
                ss.agregarParametro("DESDE", fecha);
                ss.agregarParametro("LOGO", path + SisVars.sisLogo);
                ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
                ss.setNombreReporte("informePrediosporUsuario");
                ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//");
                ss.setTieneDatasource(true);
                //ss.setReportePDF(reporte.generarPdf("/reportes/catastro/San Miguel/informePrediosporUsuario.jasper", ss.getParametros()));
                //reportes.descargarPDFarregloBytes(ss.getReportePDF());
                Faces.redirectNewTab(com.origami.config.SisVars.urlbase + "DocumentoExcel");
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public SelectItem[] getLisUrbanizaciones() {
        int cantRegis = ciudadelas.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(ciudadelas.get(i).getNombre(), ciudadelas.get(i).getNombre());
        }
        return options;
    }

    public void descargarDocumento(String url) {
        try {
            if (url != null && url.trim().length() > 0) {
                JsfUti.redirectNewTab(SisVars.urlbase + "DescargarDocsRepositorio?idDoc=" + url + "&type=pdf");
            } else {
                Faces.messageWarning(null, "No Existen Documentos para el Predio Seleccionado", "");
            }
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void ver(boolean edt) {
        if (predio != null) {
            ss.borrarDatos();
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.agregarParametro("numPredio", predio.getNumPredio());
            ss.agregarParametro("idPredio", predio.getId());
            ss.agregarParametro("edit", edt);
            if (edt) {
                if (!this.predioActivo()) {
                    return;
                }
            }
            predioModel = new CatPredioModel();
            predios.setModel(predioModel);
            ///GUARDA EDICION
            if (edt) {
                this.saveHistoric(predio, "ABRIÓ OPCION EDITAR  FICHA", null, null, null, null, Boolean.FALSE);
            } else {
                this.saveHistoric(predio, "ABRIÓ OPCION VISUALIZAR FICHA", null, null, null, null, Boolean.FALSE);
            }
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml");
        }
    }

    public void verAvaluos() {
        if (predio != null) {
            ss.borrarDatos();
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.agregarParametro("numPredio", predio.getNumPredio());
            ss.agregarParametro("idPredio", predio.getId());

            if (!this.predioActivo()) {
                return;
            }

            predioModel = new CatPredioModel();
            predios.setModel(predioModel);
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/avaluos.xhtml");
        }
    }

    public void fusionar() {
        if (predio != null) {
            if (!this.predioActivo()) {
                return;
            }
            if (this.poseeDeudas(predio)) {
                Faces.messageWarning(null, "Advertencia!", "El predio seleccionado posee deudas");
            } else {
                ss.agregarParametro("numPredio", predio.getNumPredio());
                ss.agregarParametro("idPredio", predio.getId());
                Faces.redirectFaces("/faces/vistaprocesos/catastro/fusionarPredios.xhtml");
            }

        }
    }

    public void transferenciaDominio() {
        if (predio != null) {
            if (!this.predioActivo()) {
                return;
            }
            if (this.poseeDeudas(predio)) {
                Faces.messageWarning(null, "Advertencia!", "El predio seleccionado posee deudas");
            } else {
                ss.agregarParametro("numPredio", predio.getNumPredio());
                ss.agregarParametro("idPredio", predio.getId());
                this.saveHistoric(predio, "ABRIÒ OPCION TRANSFERENCIA DE DOMINIO ", null, null, null, null, Boolean.FALSE);
                Faces.redirectFaces("/faces/vistaprocesos/catastro/transferenciaDominio/transferenciaDominio.xhtml");
            }

        }
    }

    public void openDialogDividePredio() {
        if (predio != null) {
            if (!this.predioActivo()) {
                return;
            }
            if (predio.getEstado().equals("I") || predio.getEstado().equals("H")) {
                Faces.messageWarning(null, "Advertencia!", "Los Predios en estado Inactivo o Historicos no pueden ser Fraccionados");
            } else {

                if (this.poseeDeudas(predio)) {
                    Faces.messageWarning(null, "Advertencia!", "El predio no puede ser fracionado debido a que posee deudas");

                } else {
                    if (predio.getAreaSolar() == null) {
                        Faces.messageWarning(null, "Advertencia!", "Para fraccionar un predio el Area de Solar debe ser mayor a cero");
                    } else {
                        JsfUti.executeJS("PF('dlgDivision').show()");
                    }
                }

            }
        }
    }

    public void openDialogBPMActualizarConstrucciones() {
        try {
            if (this.predio == null) {
                JsfUti.messageError(null, "Info.", "Debe seleccionar el predio a actualizar construcciones.");
                return;
            }
            if (!this.predioActivo()) {
                return;
            }

//            Lanza el evento com el predio pra proder recibirlo en el dialogo
//            event.fire(new FichaPredialOnLoadEvent(predio));
            Utils.openDialog("/vistaprocesos/catastro/bpm/actualizarConstrucciones/iniciarTramite", null);
        } catch (Exception ex) {

        }
    }

    public void verHistorial() {
        if (predio != null) {
            historico = new HistoricoPredioLazy(predio.getNumPredio().longValue());
        }
    }

    public void verFichaHistorico(Predio px) {
        if (px != null) {
            Faces.redirectNewTab(SisVars.urlbase + "/faces/vistaprocesos/catastro/historicoPredio.xhtml?val=" + px.getId());
        } else {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar un predio del historico");
        }
    }

    public Boolean seleccionarContribuyente() {
        if (this.propietario != null) {
            if (this.propietario.getEstado().equalsIgnoreCase("A")) {
                this.setContribuyenteConsulta(this.propietario.getEnte());
                this.getPredioModel().setNumPredio(this.propietario.getPredio().getNumPredio());
                return true;
            } else {
                Faces.messageWarning(null, "Advertencia!", "El propietario referenciado se encuentra inactivo");
                return false;
            }
        }
        return false;
    }

    public void consultar() {
        try {
            switch (optFiltro) {
                case 1:
                    if (predioModel.getNumPredio() != null) {
                        predios.setModel(predioModel);
                    } else {
                        Faces.messageWarning(null, "Advertenia!", "La matricula inmobiliaria del predio es obligatoria");
                    }
                    break;
                case 2:
                    if (this.seleccionarContribuyente()) {
                        predios.setModel(predioModel);
                    } else {
                        Faces.messageWarning(null, "Advertenia!", "El contribuyente seleccionado no cuenta con predios registrados, o se encuentra inactivo");
                    }
                    break;
                case 6:
                    predios.setEsPropiedadHorizontal(true);
                default:
                    predios.setModel(predioModel);
            }
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiarVariablesConsulta() {
        predioModel = new CatPredioModel();
    }

    public void buscarRes() {
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "75%");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/listadoPropietarios", options, null);
    }

    public void linkearFicha() {
        if (predio != null && predio.getRegFicha() != null) {
            Faces.redirectFacesNewTab("/vistaprocesos/registroPropiedad/fichaIngresoEditar.xhtml?idficha=" + predio.getRegFicha().getId());
        }
    }

    public void consultarPredios() {
        ss.instanciarParametros();
        if (parroquia == null) {
            ss.agregarParametro("PARROQUIA", null);
            ss.setNombreDocumento("Todas las Parroquia ");
        } else {
            ss.agregarParametro("PARROQUIA", parroquia.getCodigoParroquia());
            ss.setNombreDocumento("Parroquia " + parroquia.getDescripcion());
        }
        ss.agregarParametro("LOGO", Faces.getRealPath(SisVars.sisLogo));
        ss.agregarParametro("LOGO2", Faces.getRealPath(SisVars.sisLogo1));
        ss.setNombreReporte("reportesPrediosUrbanizacion");
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        ss.agregarParametro("SUBREPORT_DIR", path + "reportes//");
        ss.setNombreSubCarpeta("catastro");
        ss.setTieneDatasource(true);
        JsfUti.redirectNewTab(SisVars.urlbase + "Documento");
    }

    public List<CatParroquia> getParroquias() {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("idCanton", manager.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA}));
        return manager.findObjectByParameterOrderList(CatParroquia.class, paramt, new String[]{"idCanton"}, true);
//        return serv.findAllEntCopy(CatParroquia.class) ;
    }

    public void addDivisionPredio() {

        if (predio != null) {
            if (predio.getPropiedadHorizontal() != null && predio.getPropiedadHorizontal() == Boolean.FALSE) {
                if (solar == 0) {
                    this.solar = catast.getSolarMaxPredio(predio);
                }
            } else {
                if (unidad == 0) {
                    this.unidad = catast.getUnidadMaxPredio(predio);
                }
            }
            CatPredio p = new CatPredio();
            p.setParroquia(predio.getParroquia());
            p.setZona(predio.getZona());
            p.setSector(predio.getSector());
            p.setMz(predio.getMz());
            p.setBloque(predio.getBloque());
            p.setPiso(predio.getPiso());
            p.setAreaSolar(new BigDecimal("0.00"));

            if (areaSolar == null) {
                areaSolar = predio.getAreaSolar();
            }
            if (predioDivision.isEmpty()) {
                if (predio.getPropiedadHorizontal() != null && predio.getPropiedadHorizontal()) {
                    p.setUnidad((short) (unidad + 1));
                } else {
                    p.setLote((short) (solar + 1));
                    p.setSolar((short) (solar + 1));
                    p.setUnidad(predio.getUnidad());
                }
                predioDivision.add(p);
            } else {
                CatPredio pTemp = new CatPredio();
                pTemp = predioDivision.get(predioDivision.size() - 1);
                if (predio.getPropiedadHorizontal() != null && predio.getPropiedadHorizontal()) {
                    p.setUnidad((short) (pTemp.getUnidad() + 1));
                } else {
                    p.setLote((short) (pTemp.getSolar() + 1));
                    p.setSolar((short) (pTemp.getSolar() + 1));
                    p.setUnidad(predio.getUnidad());
                }
                predioDivision.add(p);
            }

        }

    }

    public void removePredioDivision(Short solar) {
        try {
            int index = -1;
            if (solar != null) {
                for (CatPredio cp : predioDivision) {
                    index++;
                    if (cp.getSolar().equals(solar)) {
                        predioDivision.remove(index);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Nada que Mostrar ;) ");
        }

    }

    public void validateAreaSolarDivisionCellRow(CellEditEvent event) {
        int index = -1;
        BigDecimal newSolar = (BigDecimal) event.getNewValue();
        try {
            if (newSolar.compareTo(areaSolar) > 0) {
                for (CatPredio cp : predioDivision) {
                    index++;
                    if (cp.getAreaSolar().equals(newSolar)) {
                        Faces.messageWarning(null, "Advertencia!", "El Area de Solar asignada no debe superar al Area Original");
                        cp.setAreaSolar((new BigDecimal("0.00")));
                        predioDivision.set(index, cp);
                    }
                }
            }
            sumAreaValidate("");
        } catch (Exception e) {
        }

    }

    public Boolean sumAreaValidate(String control) {
        int index = -1;
        BigDecimal acum = new BigDecimal("0.00");
        for (CatPredio cp : predioDivision) {
            acum = acum.add(cp.getAreaSolar());
            index++;
            if (acum.compareTo(areaSolar) > 0) {
                Faces.messageWarning(null, "Advertencia!", "La Sumatoria de las areas no debe ser mayor al Area Original");
                cp.setAreaSolar((new BigDecimal("0.00")));
                predioDivision.set(index, cp);
            }
            if ((acum.compareTo(BigDecimal.ZERO) == 0) && control.equals("save")) {
                return true;
            }
            if ((acum.compareTo(areaSolar) == 1) && control.equals("save")) {
                return true;
            }
            if ((cp.getAreaSolar().compareTo(BigDecimal.ZERO) == 0) && control.equals("save")) {
                return true;
            }
        }
        return false;
    }

    public void clearDivisionPredio() {
        predioDivision = new ArrayList<CatPredio>();
    }

    public String claveCatastral(CatPredio px) {
        return Utils.completarCadenaConCeros(px.getProvincia().toString(), 2)
                + Utils.completarCadenaConCeros(px.getCanton().toString(), 2)
                + Utils.completarCadenaConCeros(px.getParroquia().toString(), 2)
                + Utils.completarCadenaConCeros(px.getZona().toString(), 2)
                + Utils.completarCadenaConCeros(px.getSector().toString(), 2)
                + Utils.completarCadenaConCeros(px.getMz().toString(), 3)
                + Utils.completarCadenaConCeros(px.getSolar().toString(), 3)
                + Utils.completarCadenaConCeros(px.getBloque().toString(), 2)
                + Utils.completarCadenaConCeros(px.getPiso().toString(), 3)
                + Utils.completarCadenaConCeros(px.getUnidad().toString(), 3);
    }

    public void substractAreaSolar() {
        for (CatPredio cp : predioDivision) {
            if (this.predio.getAreaSolar().compareTo(cp.getAreaSolar()) == 1) {
                this.predio.setAreaSolar(this.predio.getAreaSolar().subtract(cp.getAreaSolar()));
            }

        }
    }

    public void saveDivisionPredio() {
        try {
            if (!predioDivision.isEmpty()) {
                if (!sumAreaValidate("save")) {
                    this.prediosResultantes = fusionDivisionEjb.saveDivisionPredio(predio, predioDivision);
                    clearDivisionPredio();
                    this.saveHistoric(predio, "DIVISION DE PREDIOS", null, null, null, null, Boolean.FALSE);
                    JsfUti.executeJS("PF('dlgDivision').hide()");
                    JsfUti.executeJS("PF('dlgMatricula').show()");
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Para poder Fraccionar un predio la suma de las Areas deben ser mayor a 0 e igual al Area del Solar Original");
                }
            } else {
                Faces.messageWarning(null, "Advertencia!", "El numero de fracciones para el predio debe de ser mas de uno y tener el area de solar ser mayor a cero ");
            }

        } catch (Exception e) {
        }

    }

    public boolean predioActivo() {
        if (!predio.getEstado().equalsIgnoreCase(EstadosPredio.ACTIVO)) {
            Faces.messageWarning(null, "Advertencia", "El predio no se encuentra Activo.");
            return false;
        }
        if (appconfig.isLocked(sess.getName_user(), predio.getId())) {
            Faces.messageWarning(null, "Advertencia", "El predio está siendo editado por otro usuario");
            return false;
        }
        appconfig.lockPredio(sess.getName_user(), predio.getId());
        return true;
    }

    public void continuar(CatPredio pre) {
        if (pre.getId() != null) {
            ss.instanciarParametros();
            ss.agregarParametro("idPredio", pre.getId());
            ss.agregarParametro("edit", true);
            ss.agregarParametro("numPredio", pre.getNumPredio());
            Faces.redirectFacesNewTab("/faces/vistaprocesos/catastro/fichaPredial/fichaPredial.xhtml");
            predio = new CatPredio();
        } else {
            Faces.messageWarning(null, "Advertencia!", "El predio no registra ninguna matricula inmobiliaria, revise que los datos ingresados sean correctos");
        }
    }

    public String claveCroquis(CatPredio px) {
        return Utils.completarCadenaConCeros(px.getZona().toString(), 2) + "-"
                + Utils.completarCadenaConCeros(px.getSector().toString(), 3) + "-"
                + Utils.completarCadenaConCeros(px.getMz().toString(), 3) + "-"
                + Utils.completarCadenaConCeros(px.getSolar().toString(), 2);
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public int getOptFiltro() {
        return optFiltro;
    }

    public void setOptFiltro(int optFiltro) {
        this.optFiltro = optFiltro;
    }

    public boolean getFiltro() {
        return filtro;
    }

    public void setFiltro(boolean filtro) {
        this.filtro = filtro;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public ReportesView getReportes() {
        return reportes;
    }

    public void setReportes(ReportesView reportes) {
        this.reportes = reportes;
    }

    public PdfReporte getReporte() {
        return reporte;
    }

    public void setReporte(PdfReporte reporte) {
        this.reporte = reporte;
    }

    public HistoricoPredioLazy getHistorico() {
        return historico;
    }

    public void setHistorico(HistoricoPredioLazy historico) {
        this.historico = historico;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRsocial() {
        return rsocial;
    }

    public void setRsocial(String rsocial) {
        this.rsocial = rsocial;
    }

    public String getCiruc() {
        return ciruc;
    }

    public void setCiruc(String ciruc) {
        this.ciruc = ciruc;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public List<CatPredio> getPredSel() {
        return predSel;
    }

    public void setPredSel(List<CatPredio> predSel) {
        this.predSel = predSel;
    }

    public Boolean getAct() {
        return act;
    }

    public void setAct(Boolean act) {
        this.act = act;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public void setMinimo(Integer minimo) {
        this.minimo = minimo;
    }

    public Integer getMaximo() {
        return maximo;
    }

    public void setMaximo(Integer maximo) {
        this.maximo = maximo;
    }

    public CatPredioModel getPredioModel() {
        return predioModel;
    }

    public void setPredioModel(CatPredioModel predioModel) {
        this.predioModel = predioModel;
    }

    public PropietariosLazy getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(PropietariosLazy propietarios) {
        this.propietarios = propietarios;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }

    public CatEnte getContribuyenteConsulta() {
        return contribuyenteConsulta;
    }

    public void setContribuyenteConsulta(CatEnte contribuyenteConsulta) {
        this.contribuyenteConsulta = contribuyenteConsulta;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredio getP() {
        return p;
    }

    public void setP(CatPredio p) {
        this.p = p;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(Integer tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public List<AclUser> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<AclUser> usuarios) {
        this.usuarios = usuarios;
    }

    public AclUser getUser() {
        return user;
    }

    public void setUser(AclUser user) {
        this.user = user;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getCanton() {
        return canton;
    }

    public void setCanton(Long canton) {
        this.canton = canton;
    }

    public void gestionarAlicuotas() {
        if (predio != null) {
            ss.agregarParametro("numPredio", predio.getNumPredio());
            ss.agregarParametro("idPredio", predio.getId());
            Faces.redirectFaces("/faces/vistaprocesos/catastro/gestionarPH.xhtml");
        }
    }

    public ArrayList<CatPredio> getPredioDivision() {
        return predioDivision;
    }

    public void setPredioDivision(ArrayList<CatPredio> predioDivision) {
        this.predioDivision = predioDivision;
    }

    public ArrayList<CatPredio> getPrediosResultantes() {
        return prediosResultantes;
    }

    public void setPrediosResultantes(ArrayList<CatPredio> prediosResultantes) {
        this.prediosResultantes = prediosResultantes;
    }

    public boolean isAccionDivisionFusion() {
        return accionDivisionFusion;
    }

    public void setAccionDivisionFusion(boolean accionDivisionFusion) {
        this.accionDivisionFusion = accionDivisionFusion;
    }

    public void checkEstado() {

        if (predio != null) {
            if (predio.getEstado().equals("I") || predio.getEstado().equals("H")) {
                accionDivisionFusion = false;
            } else {
                accionDivisionFusion = true;
            }
        }
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public void consultaTipoPredio() {
        predios.setTipoPredio(tipoPredio);
    }

    public Date getFecha_hasta() {
        return fecha_hasta;
    }

    public void setFecha_hasta(Date fecha_hasta) {
        this.fecha_hasta = fecha_hasta;
    }

    public void eliminarPredio() {
        if (!this.predioActivo()) {
            Faces.update("frmPredios");
        } else {
            EliminacionPredioPost ev = new EliminacionPredioPost();
            ev.setCodPredio(predio.getClaveCat());
            ev.setNumPredio(predio.getNumPredio().longValue());
            ev.setPredio(predio);
            eventEliminarPredio.fire(ev);
            Faces.redirectFaces("/faces/vistaprocesos/catastro/predios.xhtml");
        }
    }

    public void activarPredio() {
        if (this.predio.getEstado().equals("A")) {
            Faces.messageWarning(null, "Advertencia!", "El predio se encuentra en estado Activo");
        } else {
            EliminacionPredioPost ev = new EliminacionPredioPost();
            ev.setCodPredio(predio.getClaveCat());
            ev.setNumPredio(predio.getNumPredio().longValue());
            ev.setPredio(predio);
            ev.setHabilitar(Boolean.TRUE);
            eventEliminarPredio.fire(ev);
            Faces.redirectFaces("/faces/vistaprocesos/catastro/predios.xhtml");
        }
    }

    public void activarPredioSV() {
        if (this.predio.getEstado().equals("A")) {
            Faces.messageWarning(null, "Advertencia!", "El predio se encuentra en estado Activo");
        } else {
            EliminacionPredioPost ev = new EliminacionPredioPost();
            ev.setCodPredio(predio.getClaveCat());
            ev.setNumPredio(predio.getNumPredio().longValue());
            ev.setPredio(predio);
            ev.setHabilitar(Boolean.TRUE);
            eventEliminarPredio.fire(ev);
            this.saveHistoric(predio, "ACTIVACIÓN DEL PREDIO", null, null, null, null, Boolean.FALSE);
            Faces.redirectFaces("/faces/vistaprocesos/catastro/prediosSV.xhtml");
        }
    }

    public void eliminarPredioSV() {
        if (!this.predioActivo()) {
            Faces.update("frmPredios");
        } else {
            EliminacionPredioPost ev = new EliminacionPredioPost();
            ev.setCodPredio(predio.getClaveCat());
            ev.setNumPredio(predio.getNumPredio().longValue());
            ev.setPredio(predio);
            eventEliminarPredio.fire(ev);
            this.saveHistoric(predio, "ELIMINACIÓN DEL PREDIO", null, null, null, null, Boolean.FALSE);
            Faces.redirectFaces("/faces/vistaprocesos/catastro/prediosSV.xhtml");
        }
    }

    public void cession() {
        if (!this.predioActivo()) {
            Faces.update("frmPredios");
        } else {
            throw new RuntimeException("Opcion no se encuentra implementada.");
        }
    }

    public String getMessageConfirm() {
        return messageConfirm;
    }

    public void setMessageConfirm(String messageConfirm) {
        this.messageConfirm = messageConfirm;
    }

    public void updateMessage(String message) {
        System.out.println("Entro al update message");
        this.messageConfirm = message;
    }

    public Long getNumPrediosActivos() {
        return numPrediosActivos;
    }

    public void setNumPrediosActivos(Long numPrediosActivos) {
        this.numPrediosActivos = numPrediosActivos;
    }

    public Long getNumPredios() {
        return numPredios;
    }

    public void setNumPredios(Long numPredios) {
        this.numPredios = numPredios;
    }

    public Long getNumPrediosInactivos() {
        return numPrediosInactivos;
    }

    public void setNumPrediosInactivos(Long numPrediosInactivos) {
        this.numPrediosInactivos = numPrediosInactivos;
    }

    public Long getNumPrediosHistorico() {
        return numPrediosHistorico;
    }

    public void setNumPrediosHistorico(Long numPrediosHistorico) {
        this.numPrediosHistorico = numPrediosHistorico;
    }

    public BigDecimal getAvaluosTerrenos() {
        return avaluosTerrenos;
    }

    public void setAvaluosTerrenos(BigDecimal avaluosTerrenos) {
        this.avaluosTerrenos = avaluosTerrenos;
    }

    public BigDecimal getAvaluosConstruccion() {
        return avaluosConstruccion;
    }

    public void setAvaluosConstruccion(BigDecimal avaluosConstruccion) {
        this.avaluosConstruccion = avaluosConstruccion;
    }

    public BigDecimal getAvaluosPropiedad() {
        return avaluosPropiedad;
    }

    public void setAvaluosPropiedad(BigDecimal avaluosPropiedad) {
        this.avaluosPropiedad = avaluosPropiedad;
    }

    public Long getNumPrediosPrivados() {
        return numPrediosPrivados;
    }

    public void setNumPrediosPrivados(Long numPrediosPrivados) {
        this.numPrediosPrivados = numPrediosPrivados;
    }

    public Long getNumPrediosPublicos() {
        return numPrediosPublicos;
    }

    public void setNumPrediosPublicos(Long numPrediosPublicos) {
        this.numPrediosPublicos = numPrediosPublicos;
    }

    public List<CatEnte> getContribuyenteList() {
        return contribuyenteList;
    }

    public void setContribuyenteList(List<CatEnte> contribuyenteList) {
        this.contribuyenteList = contribuyenteList;
    }

    public CatEnteLazy getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(CatEnteLazy solicitantes) {
        this.solicitantes = solicitantes;
    }

    public Boolean getIsExcel() {
        return isExcel;
    }

    public void setIsExcel(Boolean isExcel) {
        this.isExcel = isExcel;
    }

    public CatTipoConjunto getTipoConjunto() {
        return tipoConjunto;
    }

    public void setTipoConjunto(CatTipoConjunto tipoConjunto) {
        this.tipoConjunto = tipoConjunto;
    }

    public CtlgItem getUsoSolar() {
        return usoSolar;
    }

    public void setUsoSolar(CtlgItem usoSolar) {
        this.usoSolar = usoSolar;
    }
    
    

}
