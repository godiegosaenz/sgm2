/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro.certAvaluos;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.DatoSeguro;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.FormatoReporte;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class CertificadosView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CertificadosView.class.getName());

    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    protected CatastroServices catastro;
    @Inject
    protected OmegaUploader omegaUploader;
    protected CatCertificadoAvaluo cert;
    @Inject
    protected UserSession sess;
    @Inject
    protected ServletSession ss;
    @Inject
    private DatoSeguroServices datoservice;
    protected FormatoReporte frep = null;
    protected Integer opcionesReporte = 1;
    protected Integer tipoConsulta = 1;
    protected CatPredio seleccion;
    protected List<CatPredio> predios;
    protected List<FormatoReporte> formatosReportes;
    protected List<CatPredioPropietario> predioPropietarios;
    protected CatPredioPropietario cpp;
    protected String propietarios;
    protected String ciuRuc;
    protected String detalle;
    protected String clavesPredios;
    protected String petNombres;
    protected String otroNombres;
    protected String otroCedRUc;
    protected String otroObservaciones;
    protected String otroTipo;
    protected String petIdentificacion;
    protected String codigo;
    protected Boolean otroValidar;
    protected Boolean showCerts;
    protected BigDecimal avaluoPlussolar;
    protected BigDecimal avaluoPlusconstruccion;
    protected BigDecimal AvaluoPluObraComplement;
    protected BigDecimal avaluoPlusmunicipal;
    protected String footer;

    @PostConstruct
    protected void load() {
        try {
            ss.instanciarParametros();
            if (sess != null) {
                Map<String, Object> pm = new HashMap<>();
                pm.put("estado", Boolean.TRUE);
                formatosReportes = manager.findObjectByParameterList(FormatoReporte.class, pm);
                showCerts = false;
            }
        } catch (Exception e) {
            Logger.getLogger(CertificadosView.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void imprimirCertificado() {
        if (frep == null) {
            return;
        }
        if (Utils.isEmpty(predios) && frep.getId().intValue() != 2) {
            return;
        }
        if (Utils.isEmpty(predios)) {
            predios = null;
        }
        switch (frep.getId().intValue()) {
            case 1: // Avaluo es de un predio
                if (predios.size() > 1 && seleccion == null) {
                    JsfUti.messageError(null, "Error", "Debe seleccionar un predio.");
                    return;
                }
                if (seleccion != null) {
                    predios = Arrays.asList(seleccion);
                }
                this.generarCertificado("CERTIFICACIÓN DE AVALÚO");
                break;
            case 2: // Bienes todos los predios
                this.generarCertificado("CERTIFICACIÓN DE BIENES");
                break;
            case 3: // Ficha catastral solo un predio
                if (predios.size() > 1 && seleccion == null) {
                    JsfUti.messageError(null, "Error", "Debe seleccionar un predio.");
                    return;
                }
                if (seleccion != null) {
                    predios = Arrays.asList(seleccion);
                }
                this.generarCertificado("FICHA CATASTRAL");
                break;
            case 4: // Certificado de plusvalia solo un predio
                if (predios.size() > 1 && seleccion == null) {
                    JsfUti.messageError(null, "Error", "Debe seleccionar un predio.");
                    return;
                }

                if (seleccion != null) {

                    if (seleccion.getAvaluoPlussolar() != null) {
                        avaluoPlussolar = seleccion.getAvaluoPlussolar();
                    } else {
                        avaluoPlussolar = BigDecimal.ZERO;
                    }

                    if (seleccion.getAvaluoPlusconstruccion() != null) {
                        avaluoPlusconstruccion = seleccion.getAvaluoPlusconstruccion();
                    } else {
                        avaluoPlusconstruccion = BigDecimal.ZERO;
                    }

                    if (seleccion.getAvaluoPluObraComplement() != null) {
                        AvaluoPluObraComplement = seleccion.getAvaluoPluObraComplement();
                    } else {
                        AvaluoPluObraComplement = BigDecimal.ZERO;
                    }

                    if (seleccion.getAvaluoPlusmunicipal() != null) {
                        avaluoPlusmunicipal = seleccion.getAvaluoPlusmunicipal();
                    } else {
                        avaluoPlusmunicipal = BigDecimal.ZERO;
                    }

                    JsfUti.executeJS("PF('dlgPlusvalia').show()");
                    JsfUti.update("frmplusvalia");
                }

                break;

            default:
                break;
        }
    }

    /**
     * Genera el certificado
     */
    private void generarCertificado(String nombreCertificado) {

        try {

            clavesPredios = "";
            if (!Utils.isEmpty(predios) && predios != null) {
                clavesPredios = " /Claves catastrales/";
                predios.forEach((predio) -> {
                    clavesPredios += predio.getClaveCat() + "/";
                });
                clavesPredios = detalle + clavesPredios;
            }
            try {
                cert = new CatCertificadoAvaluo();
                cert.setFormato(frep);
                cert.setIdentificacion(petIdentificacion + "/" + petNombres);
                cert.setDetalle(clavesPredios);
                cert.setUsuario(sess.getName_user());
                cert.setFecha(new Date());
                cert.setEstado(true);
                cert = (CatCertificadoAvaluo) manager.persist(cert);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Guardado de certificado", e);
            }

            ss.instanciarParametros();
            ss.setNombreReporte(frep.getReporte());
            if (nombreCertificado.equals("CERTIFICADO DE AVALUO ACTUALIZADO")) {
                manager.persist(predios.get(0));
            }
            ss.setNombreSubCarpeta("/catastro/certificados/");
            ss.agregarParametro("LOGO_1", SisVars.logoReportes);
            ss.agregarParametro("LOGO", SisVars.sisLogo1);
            ss.agregarParametro("LOGO_FOOTER", footer);
            ss.agregarParametro("TITULO", SisVars.NOMBREMUNICIPIO);
            ss.agregarParametro("NOMBRE_CERTIFICADO", nombreCertificado);
            ss.agregarParametro("DETALLE", detalle);
            ss.agregarParametro("USUARIO", cert.getUsuario());
            ss.agregarParametro("FECHA", cert.getFecha());
            ss.agregarParametro("PETIDOR", petNombres);
            ss.agregarParametro("PERTIDOR_IDNT", petIdentificacion);
            ss.agregarParametro("CODIGO", cert.getId());
            ss.agregarParametro("SUBREPORT_DIR", SisVars.REPORTES + "/");
            System.out.println(sess.getNombreBienvenida() + " >> " + sess.getNombrePersonaLogeada() + " >> " + SisVars.REPORTES
                    + " >> " + SisVars.URL_REPORTES);
            ss.agregarParametro("FUNCIONARIO", sess.getNombrePersonaLogeada());
            ss.setTieneDatasource(Boolean.FALSE);
            ss.setDataSource(predios);

            if (nombreCertificado.equals("FICHA CATASTRAL")) {
                Map<String, Object> reporteadd = new HashMap<>();
                reporteadd.put("nombreSubCarpeta", "/catastro/Ibarra");
                reporteadd.put("nombreReporte", "fichaMiduvi");

                CatPredio predio = predios.get(0);
                if (predio != null) {
                    String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
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
                                    reporteadd.put("FachadaFrontal", omegaUploader.streamFile(foto.getFileOid()));

                                    break;
                                case 2:
                                    reporteadd.put("FachadaIzquierda", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                                case 3:
                                    reporteadd.put("FachadaDerecha", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                                case 4:
                                    reporteadd.put("FachadaPosterior", omegaUploader.streamFile(foto.getFileOid()));
                                    break;
                            }
                            numFotos++;
                        }
                    }
                    reporteadd.put("predio", predio.getId());

                    if (predio.getPredioRaiz() != null) {
                        CatPredio predioRaiz = (CatPredio) manager.find(Querys.getPrediosById, new String[]{"predioID"}, new Object[]{predio.getPredioRaiz()});
                        reporteadd.put("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predioRaiz.getNumPredio());
                    } else {
                        reporteadd.put("IMAGEN_PREDIO", SisVars.URLPLANOIMAGENPREDIO + predio.getNumPredio());
                    }
                    reporteadd.put("LOGO", path + SisVars.sisLogo);
                    reporteadd.put("LOGO2", path + SisVars.sisLogo1);
                    reporteadd.put("SUBREPORT_DIR_TITLE", path + "reportes/");
                    reporteadd.put("SUBREPORT_DIR", path + "reportes/catastro/Ibarra/");
                    ss.addParametrosReportes(reporteadd);

                    if (predio.getCatPredioEdificacionCollection() != null && !Utils.isEmpty(predio.getCatPredioEdificacionCollection())) {
                        int count = 0;
                        System.out.println("Agregando construcciones");
                        String edificaciones = "";
                        for (CatPredioEdificacion edif : predio.getCatPredioEdificacionCollection()) {
                            count++;
                            edificaciones += edif.getId().toString() + ",";
                            if ((count % 4) == 0 || (predio.getCatPredioEdificacionCollection().size() - count) == 0) {
                                edificaciones = edificaciones.substring(0, edificaciones.length() - 1);
                                reporteadd = new HashMap<>();
                                reporteadd.put("nombreSubCarpeta", "/catastro/Ibarra");
                                reporteadd.put("nombreReporte", "fichaMiduviBloques");
                                reporteadd.put("predio", predio.getId());
                                reporteadd.put("edificaciones", edificaciones);
                                ss.addParametrosReportes(reporteadd);
                                edificaciones = "";
                            }

                        }
                    }

                    ss.setAgregarReporte(true);
                    ss.getReportes().size();
                }
            }
            showCerts = false;
            codigo = "";
            JsfUti.redirect(SisVars.urlbase + "Documento");

            // JsfUti.update("mainform");
        } catch (Exception e) {
            Logger.getLogger(CertificadosView.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void verificarCodigo() {
        try {
            showCerts = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Mostrar predios", e);
        }
    }

    public void visualizarPredios(String page) {
        try {
            Utils.openDialog(page, null, "550", "80");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Mostrar predios", e);
        }
    }

    public boolean mostrarAgregar() {
        if (frep == null) {
            return false;
        } else if (frep.getCodigo().equals("CERTIFICADO DE BIENES")) {
            return true;
        }
        return false;
    }

    public void procesarPredios(SelectEvent event) {
        try {
            if (predios == null) {
                predios = new ArrayList<>();
            }
            if (event.getObject() instanceof CatEnte) {
                CatEnte en = (CatEnte) event.getObject();
                ciuRuc = en.getCiRuc();
                petNombres = en.getNombresCompletos();
                petIdentificacion = en.getCiRuc();
                en.getCatPredioPropietarioCollection().stream().filter((cpps) -> (!predios.contains(cpps.getPredio()))).forEachOrdered((cpps) -> {
                    cpps.getPredio().setCatParroquia(catastro.getCatParroquia(cpps.getPredio().getParroquia()));
                    if (!predios.contains(cpps.getPredio())) {
                        predios.add(cpps.getPredio());
                    }
                });
                predioPropietarios = en.getCatPredioPropietarioCollection();
            } else {
                this.llenarPredios((List<CatPredio>) event.getObject());
                this.llenarNombres(predioPropietarios);
            }
            this.llenarFormato();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Procesar predios", e);
        }
    }

    public void procesarPetidor(SelectEvent event) {
        try {

            CatEnte en = (CatEnte) event.getObject();
            petNombres = en.getNombresCompletos();
            petIdentificacion = en.getCiRuc();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Procesar Petidor", e);
        }
    }

    public void procesarOtros(SelectEvent event) {
        try {

            CatEnte en = (CatEnte) event.getObject();
            otroNombres = en.getNombresCompletos();
            otroCedRUc = en.getCiRuc();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Procesar Petidor", e);
        }
    }

    public void abrirOtros() {
        otroNombres = "";
        otroCedRUc = "";
        otroObservaciones = "";
        otroTipo = "";
        otroValidar = true;

        JsfUti.executeJS("PF('dlgprop').show()");
        JsfUti.update("frmprop");
    }

    public void actulizarPredio() {

        if (Utils.isNotEmpty(predioPropietarios)) {
            for (CatPredioPropietario predioPropietario : predioPropietarios) {
                if (predioPropietario.getPredio() != null) {
                    if (!predios.contains(predioPropietario.getPredio())) {
                        predios.add(predioPropietario.getPredio());
                    }
                }
            }
            this.llenarFormato();
        }

    }

    public void llenarPredios(List<CatPredio> p) {
        predioPropietarios = new ArrayList<>();
        List<CatPredio> temp = new ArrayList<>();
        p.forEach((catPredio) -> {
            catPredio.setCatParroquia(catastro.getCatParroquia(catPredio.getParroquia()));
            if (Utils.isNotEmpty(catPredio.getCatPredioPropietarioCollection())) {
                for (CatPredioPropietario pp : catPredio.getCatPredioPropietarioCollection()) {
                    boolean equals = false;
                    if (Utils.isNotEmpty(predioPropietarios)) {
                        for (CatPredioPropietario pps : predioPropietarios) {
                            equals = pps.getCiuCedRuc().equals(pp.getCiuCedRuc());
                        }
                    }
                    if (!equals) {
                        predioPropietarios.add(pp);
                    }
                }
            } else {
                System.out.println(">>>>>> Collection Propietarios vacia");
            }
            if (!predios.contains(catPredio)) {
                temp.add(catPredio);
            }
        });
        predios.addAll(temp);
    }

    public void onRowSelect(SelectEvent event) {
        try {
            if (cpp != null) {
                if (cpp.getCiuCedRuc() == null) {
                    ciuRuc = cpp.getEnte().getCiRuc();
                } else {
                    ciuRuc = cpp.getCiuCedRuc();
                }
                petNombres = cpp.getEnte().getNombresCompletos();
                petIdentificacion = cpp.getEnte().getCiRuc();
                predios = new ArrayList<>();
                if (cpp.getPredio() != null) {
                    predios.add(cpp.getPredio());
                    predios.forEach((predio) -> {
                        predio.setCatParroquia(catastro.getCatParroquia(predio.getParroquia()));
                    });
                }
                llenarNombres(Arrays.asList(cpp));
                this.llenarFormato();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Procesar predios", e);
        }
    }

    public void sumarPlusvalias() {
        avaluoPlusmunicipal = (avaluoPlussolar.add(avaluoPlusconstruccion).add(AvaluoPluObraComplement));
        JsfUti.update("frmplusvalia:tot_plusvalia");
    }

    public void llenarNombres(Collection<CatPredioPropietario> props) {
        if (props != null) {
            if (ciuRuc == null) {
                ciuRuc = Utils.get(props, 0).getCiuCedRuc();
            }
            propietarios = "";
            for (Iterator<CatPredioPropietario> iterator = props.iterator(); iterator.hasNext();) {
                CatPredioPropietario next = iterator.next();
                propietarios += next.getEnte().getNombreCompleto() + " con el número de identificación: " + next.getEnte().getCiRuc();

                if (iterator.hasNext()) {
                    propietarios = propietarios + " Y ";
                }
            }
        }
    }

    public void llenarFormato() {
        if (frep == null) {
            return;
        }

        try {
            detalle = String.format(frep.getFormato(), SisVars.NOMBREMUNICIPIO, propietarios);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Llenar Formato Certificado", e);
        }
    }

    public void elimiar(CatPredio cp) {
        if (cp != null) {
            predios.remove(cp);
        }
    }

    public void imprimir(CatCertificadoAvaluo cert) {
        if (cert != null) {
            int numFotos = 1;
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
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
            ss.agregarParametro("id", cert.getId());
            ss.agregarParametro("logo", SisVars.logoReportes);
            ss.setNombreSubCarpeta("catastro");
            JsfUti.redirectNewTab("/sgmEE/Documento");
        } else {
            Faces.messageWarning(null, "Advertencia", "No se pudo imprimir el certificado, verificar que los datos esten correctos");
        }
    }

    public void guardarPlusvalia() {
        if (seleccion != null) {
            seleccion.setAvaluoPlussolar(avaluoPlussolar);
            seleccion.setAvaluoPluObraComplement(AvaluoPluObraComplement);
            seleccion.setAvaluoPlusconstruccion(avaluoPlusconstruccion);
            seleccion.setAvaluoPlusmunicipal(avaluoPlusmunicipal);
            predios = Arrays.asList(seleccion);
            this.generarCertificado("CERTIFICADO DE AVALUO ACTUALIZADO");
            JsfUti.executeJS("PF('dlgPlusvalia').hide()");

        }
    }

    public void agregarOtros() {
        if (otroObservaciones == null || otroObservaciones.trim().isEmpty()) {
            Faces.messageWarning(null, "Advertencia", "Debe seleccionar la observación");
            return;
        }
        CatPredioPropietario prop = new CatPredioPropietario();
        prop.setCiuCedRuc(otroCedRUc);
        prop.setId(Double.valueOf(Math.random() * 100).longValue());
//        prop.getExtras().setNombresCompletos(otroNombres);
//        prop.getExtras().setOtros(otroObservaciones);

        if (predioPropietarios == null) {
            predioPropietarios = new ArrayList<>();
        } else {
            for (CatPredioPropietario predioPropietario : predioPropietarios) {
//                if (predioPropietario.getExtras().getOtros() != null && !predioPropietario.getExtras().getOtros().trim().isEmpty()) {
//                    if (!predioPropietario.getExtras().getOtros().equals(otroObservaciones)) {
//                        Faces.messageWarning(null, "Advertencia", "Exiten propietarios con observaciones diferentes a " + otroObservaciones);
//                        return;
//                    }
//                }
            }
        }
        predioPropietarios.add(prop);
        this.llenarNombres(predioPropietarios);
        llenarFormato();
        JsfUti.executeJS("PF('dlgprop').hide()");
        JsfUti.update("mainForm:frmCertificados");

    }

    public void verificarCiudadano() {
        DatoSeguro data;
        Boolean empresa = false;
        if (otroTipo == null) {
            JsfUti.messageInfo(null, "Advertencia", "El tipo de identificación no puede estar vacio");
            return;
        }
        if (otroCedRUc == null) {
            JsfUti.messageInfo(null, "Advertencia", "El número de identificación no puede estar vacio");
            return;
        }
        if (otroCedRUc != null) {

            if (otroTipo.equals("C") || otroTipo.equals("R")) {
                if (otroTipo.equals("R")) {
                    empresa = true;
                }

                data = datoservice.getDatos(otroCedRUc, empresa, 0);
                if (data != null) {
                    llenarCiudadano(data);
                    JsfUti.update("frmprop:nombre_input");

                } else {
                    if (!Utils.validateCCRuc(otroCedRUc)) {
                        JsfUti.messageInfo(null, "Advertencia", "El número de identificación es incorrecto");
                        return;
                    }
                }

            }
        }

    }

    public void removerPropietario(CatPredioPropietario propietario) {
        if (predioPropietarios == null || !Utils.isNotEmpty(predioPropietarios)) {
            return;
        }
//        if (propietario.getExtras().getOtros() == null) {
//            JsfUti.messageInfo(null, "Advertencia", "No se puede remover el propietario");
//            return;
//        }
        int index = predioPropietarios.indexOf(propietario);
        predioPropietarios.remove(index);
        this.llenarNombres(predioPropietarios);
        llenarFormato();
//          System.out.println("propietarios: " + propietarios);
        JsfUti.update("mainForm:frmCertificados");
    }

    public void llenarCiudadano(DatoSeguro data) {
        String fields[];
        Integer num;
        String nombre = "";
        String aux = "";

        try {
            if (data != null) {
                data.setDescripcion(verificarContenido(data.getDescripcion()));
                fields = data.getDescripcion().split(" ");
                num = fields.length;

                switch (num) {
                    case 3:

                        nombre = fields[0] + " " + fields[1] + " " + fields[2];

                        break;
                    case 4:
                        nombre = fields[0] + " " + fields[1] + " " + fields[2] + " " + fields[3];
                        break;
                    case 5:
                        nombre = fields[0] + " " + fields[1] + " " + fields[2] + " " + fields[3] + " " + fields[4];
                        break;
                    default:
                        nombre = fields[0] + " " + fields[1];

                        for (int i = 2; i < num; i++) {
                            aux = aux + fields[i];
                            if (i != num - 1) {
                                aux = aux + " ";
                            }
                        }
                        nombre = fields[0] + " " + fields[1] + " " + aux;
                        break;
                }
                otroNombres = nombre;
                otroCedRUc = data.getIdentificacion();

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "llenar Ciudadano", e);
        }

    }

    private String verificarContenido(String descripcion) {
        Charset utf8 = Charset.forName("UTF-8");
        String Buffer = new String(descripcion.getBytes(), utf8);
        return Buffer;
    }

    public CatCertificadoAvaluo getCert() {
        return cert;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
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

    public Integer getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(Integer tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Integer getOpcionesReporte() {
        return opcionesReporte;
    }

    public void setOpcionesReporte(Integer opcionesReporte) {
        this.opcionesReporte = opcionesReporte;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public List<CatPredioPropietario> getPredioPropietarios() {
        return predioPropietarios;
    }

    public void setPredioPropietarios(List<CatPredioPropietario> predioPropietarios) {
        this.predioPropietarios = predioPropietarios;
    }

    public String getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(String propietarios) {
        this.propietarios = propietarios;
    }

    public String getCiuRuc() {
        return ciuRuc;
    }

    public void setCiuRuc(String ciuRuc) {
        this.ciuRuc = ciuRuc;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public CatPredio getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(CatPredio seleccion) {
        this.seleccion = seleccion;
    }

    public String getOtroObservaciones() {
        return otroObservaciones;
    }

    public void setOtroObservaciones(String otroObservaciones) {
        this.otroObservaciones = otroObservaciones;
    }

    public List<FormatoReporte> getFormatosReportes() {
        return formatosReportes;
    }

    public void setFormatosReportes(List<FormatoReporte> formatosReportes) {
        this.formatosReportes = formatosReportes;
    }

    public FormatoReporte getFrep() {
        return frep;
    }

    public void setFrep(FormatoReporte frep) {
        this.frep = frep;
    }

    public String getPetNombres() {
        return petNombres;
    }

    public void setPetNombres(String petNombres) {
        this.petNombres = petNombres;
    }

    public String getPetIdentificacion() {
        return petIdentificacion;
    }

    public String getOtroNombres() {
        return otroNombres;
    }

    public void setOtroNombres(String otroNombres) {
        this.otroNombres = otroNombres;
    }

    public String getOtroCedRUc() {
        return otroCedRUc;
    }

    public void setOtroCedRUc(String otroCedRUc) {
        this.otroCedRUc = otroCedRUc;
    }

    public void setPetIdentificacion(String petIdentificacion) {
        this.petIdentificacion = petIdentificacion;
    }

    public BigDecimal getAvaluoPlussolar() {
        return avaluoPlussolar;
    }

    public void setAvaluoPlussolar(BigDecimal avaluoPlussolar) {
        this.avaluoPlussolar = avaluoPlussolar;
    }

    public BigDecimal getAvaluoPlusconstruccion() {
        return avaluoPlusconstruccion;
    }

    public void setAvaluoPlusconstruccion(BigDecimal avaluoPlusconstruccion) {
        this.avaluoPlusconstruccion = avaluoPlusconstruccion;
    }

    public BigDecimal getAvaluoPluObraComplement() {
        return AvaluoPluObraComplement;
    }

    public void setAvaluoPluObraComplement(BigDecimal AvaluoPluObraComplement) {
        this.AvaluoPluObraComplement = AvaluoPluObraComplement;
    }

    public BigDecimal getAvaluoPlusmunicipal() {
        return avaluoPlusmunicipal;
    }

    public void setAvaluoPlusmunicipal(BigDecimal avaluoPlusmunicipal) {
        this.avaluoPlusmunicipal = avaluoPlusmunicipal;
    }

    public String getOtroTipo() {
        return otroTipo;
    }

    public void setOtroTipo(String otroTipo) {
        this.otroTipo = otroTipo;
    }

    public Boolean getOtroValidar() {
        return otroValidar;
    }

    public void setOtroValidar(Boolean otroValidar) {
        this.otroValidar = otroValidar;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getShowCerts() {
        return showCerts;
    }

    public void setShowCerts(Boolean showCerts) {
        this.showCerts = showCerts;
    }

}
