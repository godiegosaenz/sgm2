/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.app.AppConfig;
import com.origami.censocat.restful.JsonUtils;
import com.origami.config.ConfigFichaPredial;
import com.origami.config.MainConfig;
import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CarteraVencida;
import com.origami.sgm.entities.TitulosPredio;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.CatCategoriasConstruccion;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdificacionPisosDet;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioLinderos;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatPropiedadItem;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.events.GenerarHistoricoPredioPost;
import com.origami.sgm.events.ValorarPredioPost;
import com.origami.sgm.predio.models.FichaEdificacionesModel;
import com.origami.sgm.entities.predio.models.FichaModel;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import util.Faces;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author CarlosLoorVargas
 */
//@Named
//@ViewScoped
//NO BORRAR EL ESPACIO AL FINAL DE LOS OBS
public abstract class PredioUtil {

    protected CatPredioEdificacion edif = new CatPredioEdificacion();
    protected List<CtlgItem> usosA = new ArrayList<>(), prototipos, vias, instalacionesEspeciales;
    protected int maxEdif = 1;
    protected String usr, obs, predioAnt, fichaEdifAnt, fichaEdifAct, fichaModelAnt, fichaModelAct;
    @Inject
    protected Entitymanager em;
    @Inject
    protected CatastroServices catas;
    @Inject
    protected RecaudacionesService recaudacionesService;
    @javax.inject.Inject
    protected AvaluosServices avaluos;
    protected Boolean avCalle = false;
    protected FotoPredio foto;
    protected SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
    protected Collection<CatEdificacionPisosDet> pisos;
    protected GeDocumentos documento;

    @Inject
    private AvaluosServices avaluosServices;
    protected ConfigFichaPredial configFichaPredial;
    protected static final long serialVersionUID = 1L;
    protected static final Logger log = Logger.getLogger(PredioUtil.class.getName());
    protected String idActionListenner;
    protected String remotteCommand;
    protected CatPredio predio;
    protected CatPredioS4 caracteristicas;
    protected CatPredioS6 servicios;
    protected CatEnte informante;
    protected CatPredio predioColind;
    protected CtlgItem orientacion;
    protected String nombreLindero;
    protected String enLongitudLindero;

    protected MainConfig mainConfig;
    @Inject
    protected AppConfig appconfig;
    protected Boolean ver = false;
    protected TreeNode nodoEdificaciones;
    protected List<CatPredioEdificacion> bloques;

    @Inject
    protected Event<GenerarHistoricoPredioPost> historicoEvent;
    @Inject
    protected Event<ValorarPredioPost> eventValoracion;
    @Inject
    protected UserSession sess;

    private List<RenLiquidacion> catPrediosAfectadosLiquidaciones;
    protected List<GeDocumentos> geDocumentosList;

    protected void init() {
        predioAnt = "";
        fichaEdifAnt = "";
        fichaEdifAct = "";
        fichaModelAct = "";
        fichaModelAnt = "";
        this.mainConfig = new MainConfig();
    }

    public void getDetallePiso(CatPredioEdificacion ed) {
        if (ed != null && ed.getId() != null && ed.getNumPisos() > 0) {
            if (ed.getCatEdificacionPisosDetCollection() != null && !ed.getCatEdificacionPisosDetCollection().isEmpty()) {
                pisos = ed.getCatEdificacionPisosDetCollection();
            } else {
                pisos = new ArrayList<>();
                for (int i = 1; i <= ed.getNumPisos(); i++) {
                    CatEdificacionPisosDet p = new CatEdificacionPisosDet();
                    p.setEdificacion(ed);
                    p.setAnio(ed.getAnioCons());
                    p.setEstado("A");
                    p.setFecCre(new Date());
                    p.setPiso(i);
                    pisos.add(p);
                }
            }
        } else {
            Faces.messageWarning(null, "Advertencia!", "La edificacion debe estar guardada, y el numero de pisos ser mayor que 0");
        }
    }

    public Boolean guardarDetallePisos() {
        try {
            if (Utils.isEmpty((List<?>) pisos)) {
                return false;
            }
            for (CatEdificacionPisosDet d : pisos) {
                if (d.getArea().compareTo(BigDecimal.ZERO) <= 0) {
                    return false;
                }
                return catas.guardarDetallePisos((List) pisos);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void cargarDocumentos() {
        if (predio == null) {
            return;
        }
        geDocumentosList = em.findAll(Querys.getGeDocumentosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()});
    }

    public void descargarDocumento(Long fileOid) {
        try {
            String url = fileOid.toString();
            if (url != null && url.trim().length() > 0) {
                JsfUti.redirectNewTab(SisVars.urlbase + "DescargarDocsRepositorio?idDoc=" + url + "&type=pdf");
            } else {
                Faces.messageWarning(null, "No Existen Documentos para el Predio Seleccionado", "");
            }
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void inactivarDocumento(GeDocumentos documentos) {
        try {
            documentos = catas.saveInactivarDocumentos(documentos);
            if (!documentos.getEstado()) {
                JsfUti.messageInfo(null, "Exito!", "Datos Registrados Correctamente");
            } else {
                JsfUti.messageError(null, "", "Error al Eliminar los Archivos");
            }
            cargarDocumentos();
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CatPredio> IngresarPHs(List<CatPredio> v, List<CatPredio> h) {
        BigDecimal alics = BigDecimal.ZERO;
        for (CatPredio ph : h) {
            if (!ph.getCrear()) {
                if (ph.getAlicuotaUtil() == null) {
                    Faces.messageWarning(null, "Advertencia!", "Debe ingresar las alicuotas");
                    return null;
                } else {
                    if (ph.getAlicuotaUtil().compareTo(BigDecimal.ZERO) <= 0) {
//                        return null;
                    }
                }
                alics.add(ph.getAlicuotaUtil());
            }
        }
        for (CatPredio ph : v) {
            if (!ph.getCrear()) {
                if (ph.getAlicuotaUtil() == null) {
                    Faces.messageWarning(null, "Advertencia!", "Debe ingresar las alicuotas");
                    return null;
                } else {
//                    if (ph.getAlicuotaSac().compareTo(BigDecimal.ZERO) <= 0) {
//                        return null;
//                    }
                }
                alics.add(ph.getAlicuotaUtil());
            }
        }
        if (alics.compareTo(new BigDecimal("100")) < 0) {
            Faces.messageWarning(null, "Advertencia!", "La sumatoria de las alicuotas es menor que 100");
        }
        return catas.registrarPHs(v, h);
    }

    public CatPredio registrarPredio() {
        CatPredio pred = null;
        try {
            if (predio != null) {
                if (predio.getCiudadela() != null && predio.getTipoConjunto() != null) {
                    try {
                        predio.setClaveCat(claveCatastral(predio));
                    } catch (Exception e) {
                    }
                    pred = catas.guardarPredio(predio);
                    if (pred != null) {
                        Faces.messageInfo(null, "Nota!", "Datos prediales actualizados satisfactoriamente");
                    } else {
                        Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar la informacion predial, verifique que los campos esten ingresados correctamente");
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "El tipo de conjunto, la ciudadela y el subsector son obligatorios");
                }

            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return pred;
    }

    public void openDialog(ActionEvent event) {
        JsfUti.update(":frmSubirDocumentosDialog");
        JsfUti.executeJS("PF('dlgSubirDocumento').show()");
        this.idActionListenner = event.getComponent().getId();
    }

    public Boolean dataBaseConnect() {
        Boolean status = Boolean.FALSE;
        switch (SchemasConfig.DB_ENGINE) {
            case ORACLE:
                status = Boolean.FALSE;
                break;
            case POSTGRESQL:
                status = Boolean.TRUE;
                break;
            default:
                break;
        }
        return status;
    }

    public Boolean validateTipoConjuntoCiudadela(CatPredio cp) {
        if (dataBaseConnect()) {
            return cp.getCiudadela() != null && cp.getTipoConjunto() != null;
        } else {
            return true;
        }
    }

    public void updatePreviousKey() {
        if (!poseePagos(predio) && !poseeDeudas(predio)) {
            updateClaveAnterior();
        } else {
            JsfUti.executeJS("PF('dlgConfirmacionSiAceptaEltrato').show()");
            Faces.messageError(null, "Advertencia!", "El Predio posee emisiones al año actual");
        }
    }

    public void updateClaveAnterior() {
        predio.setClaveAnteriorVerificada(Boolean.TRUE);
        catas.guardarPredio(predio);
        catPrediosAfectadosLiquidaciones = null;
        List<String> idTitulosPrediales = new ArrayList();
        List<RenLiquidacion> carteraVencida = null;
        List<CarteraVencida> carteraVencidaList = null;
        guardarDatosPredio("ACTUALIZACION TITULOS ANTERIORES", informante);
        carteraVencidaList = recaudacionesService.getCarteraVencidaAME(this.predio.getPredialant());
        if (carteraVencidaList != null) {
            if (!carteraVencidaList.isEmpty()) {
                for (CarteraVencida cv : carteraVencidaList) {
                    idTitulosPrediales.add(cv.getCarveNumtitulo());
                }
            }
        }
        if (!idTitulosPrediales.isEmpty()) {
            carteraVencida = new ArrayList();
            carteraVencida = recaudacionesService.updateLiquidacionesByIdTituloLiquidacion(idTitulosPrediales, this.predio, Boolean.FALSE);
        }
        titulosPrediosAme(carteraVencida);
        JsfUti.executeJS("PF('dlgPrediosAfectados').show()");
    }

    public Boolean poseeDeudas(CatPredio cp) {
        RenLiquidacion liq = catas.getDeudasPredioAnioActual(cp, null);
        if (liq != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean poseePagos(CatPredio cp) {
        RenLiquidacion liq = catas.getPagadasPredioAnioActual(cp);
        if (liq != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void titulosPrediosAme(List<RenLiquidacion> carteraVencida) {
        List<String> idTitulosPrediales = new ArrayList();
        List<TitulosPredio> titulosPrediosList = null;
        titulosPrediosList = recaudacionesService.getTitulosPredioAME(this.predio.getPredialant().trim());
        if (titulosPrediosList != null) {
            if (!titulosPrediosList.isEmpty()) {
                for (TitulosPredio tp : titulosPrediosList) {
                    idTitulosPrediales.add(tp.getTitprNumtitulo());
                }
            }
        }
        if (!idTitulosPrediales.isEmpty()) {
            catPrediosAfectadosLiquidaciones = new ArrayList();
            catPrediosAfectadosLiquidaciones = recaudacionesService.updateLiquidacionesByIdTituloLiquidacion(idTitulosPrediales, this.predio, Boolean.TRUE);
        }
        if (carteraVencida != null) {
            for (RenLiquidacion rl : carteraVencida) {
                catPrediosAfectadosLiquidaciones.add(rl);
            }
        }

    }

    public Boolean guardarDatosPredio(String observacion, CatEnte informante) {
        System.out.println("");
        try {
            if (!processMethod()) {
                return false;
            }
            if (observacion == null || observacion.equals("")) {
                observacion = "Actualizacion Informacion de Datos catastrales ";
            }
            if (predio != null) {
                //System.out.println("predio.getAniosPosesion() " + predio.getAniosPosesion());
                if (predio.getAniosPosesion() != null) {
                    if (predio.getCiudadela() != null && predio.getTipoConjunto() != null) {
                        if (this.getAvCalle()) {
                            predio.setCalleAv("1");
                        } else {
                            predio.setCalleAv("2");
                        }
                        try {
                            predio.setClaveCat(claveCatastral(predio));
                        } catch (Exception e) {
                        }
                        if (predio.getEsAvaluoVerificado() != null) {
                            if (!predio.getEsAvaluoVerificado()) {
                                predio.setEsTributario(Boolean.TRUE);
                            }
                        }
                        predio.setInformante(informante);
                        CatPredio predioTmp = catas.guardarPredio(predio);
                        if (predioTmp != null) {
                            this.fichaModelAnt = this.fichaModelAct;
                            if (saveHistoric(predioTmp, observacion, this.fichaEdifAnt, this.fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
                                Faces.messageInfo(null, "Nota!", "Datos prediales actualizados satisfactoriamente");
                                /*
                            INICIO DE ACTUALIZACION DE LOS AVALUOS CADA VEZ QUE SE 
                            CAMBIA ALGUN DATO DE COEFICIENTES DE SOLAR Y DE CONSTRUCCION
                                 */
                                //   return true;
                                // } else {
                                //     Faces.messageWarning(null, "Advertencia", "Problemas al guardar Datos Historicos del Predio");
                                //    return false;
                                // }
                            } else {
                                Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar la informacion predial, verifique que los campos esten ingresados correctamente");
                            }
                        } else {
                            if (mainConfig.getFichaPredial().getRenderSanVicente()) {
                                this.updateAvaluoPredio(predioTmp);
                            }
                            Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar la informacion predial, verifique que los campos esten ingresados correctamente");
                        }
                        return true;
                    } else {
                        Faces.messageWarning(null, "Advertencia!", "El tipo de conjunto, la ciudadela y el subsector son obligatorios");
                    }
                } else {
                    Faces.messageFatal(null, "Advertencia!", "Debe Ingresar Años de Posesion");
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void updateAvaluoPredio(CatPredio cpTemp) {
        List<CatPredio> catPredioToAval = new ArrayList();
        catPredioToAval.add(cpTemp);
        avaluosServices.generateAvaluo(catPredioToAval,
                Utils.getAnio(new Date()), Utils.getAnio(new Date()), false, sess.getName_user());
    }

    public Boolean actualizarAvaluos(Boolean normal) {
        try {
            obs = "Actualizacion de Avaluos";
            if (predio != null && predio.getId() != null) {
                ValoracionPredial vp = avaluos.getEmisionPredial(usr, Integer.valueOf(sdf.format(new Date())), predio.getNumPredio(), normal).get();
                if (vp != null) {
                    predio.setAvaluoSolar(vp.getAvaluoSolar());
                    predio.setAvaluoConstruccion(vp.getAvaluoEdificacion());
                    predio.setAvaluoMunicipal(vp.getAvaluoMunicipal());
                    catas.guardarPredio(predio);
                    this.fichaModelAnt = this.fichaModelAct;
                    if (saveHistoric(predio, obs, this.fichaEdifAnt, this.fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
                        Faces.messageInfo(null, "Nota!", "Avaluos actualizados, Pulse la tecla F5 para visualizar los cambios");
                        return true;
                    } else {
                        Faces.messageWarning(null, "Advertencia", "Problemas al guardar Datos de los Avaluos actualizados");
                        return false;
                    }
                }
            }
        } catch (InterruptedException | NumberFormatException | ExecutionException e) {
            log.log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void guardarCaracteristicas(CatPredioS4 s4) {
        try {
            if (!processMethod()) {
                return;
            }
            obs = "Actualizacion Informacion de Caracteristicas ";
            if (this.guardarDatosPredio(obs, informante)) {
                if (s4 != null) {
                    if (catas.guardarPredioS4(s4) != null) {
                        Faces.messageInfo(null, "Nota!", "Caracteristicas prediales actualizadas satisfactoriamente");
                        if (mainConfig.getFichaPredial().getRenderSanVicente()) {
                            this.updateAvaluoPredio(s4.getPredio());
                        }
                    } else {
                        Faces.messageWarning(null, "Debe agregar al menos una caracteristica", "");
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public void guardarServicios(CatPredioS6 s6, List<CtlgItem> vias, List<CtlgItem> instalaciones, CatEnte enteHorizontal) {
        HiberUtil.newTransaction();
        if (enteHorizontal == null) {
            obs = "Actualizacion Informacion de Servicios";
        } else {
            obs = "ACTUALIZACION DE INFORMACION DE VIVIENDA CENSAL";
        }
        if (enteHorizontal != null) {
            if (enteHorizontal.getId() != null) {
                this.saveResponsable(null, null, enteHorizontal);
            }
            if (this.validarServicios(s6) == false) {
                return;
            }
        }
        try {
            predio = catas.guardarPredio(predio);
            CatPredioS6 s6Temp = catas.guardarPredioS6(s6, vias, instalaciones);
            if (s6Temp != null) {
                predio.setCatPredioS6(s6Temp);
                this.servicios = s6Temp;
                if (this.guardarDatosPredio(obs, informante) == true) {
                    Faces.messageInfo(null, "Nota!", "Datos de Infraestructura y Servicios Actualizados satisfactoriamente");
                    if (mainConfig.getFichaPredial().getRenderSanVicente()) {
                        this.updateAvaluoPredio(this.servicios.getPredio());
                    }
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al Actualizar, verifique que los campos esten ingresados correctamente");
                }
            } else {
                Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar los servicios prediales, verifique que los campos esten ingresados correctamente");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public Boolean validarServicios(CatPredioS6 s6) {
        if (s6 != null) {
            if (s6.getTieneAguaPotable() == null || s6.getTieneAguaPotable() != true) {
                if (s6.getAbastAgua() != null || s6.getAbastAguaRecibe() != null) {
                    Faces.messageWarning(null, "Para seleccionar un medio de Abastecimiento de Agua se debe habilitar la Opcion: 'Conexion"
                            + "Domiciliaria' en INFRAESTRUCTURA, INSTALACIONES Y SERVICIOS", "");
                    return false;
                }
            }
            if (s6.getTieneElectricidad() == null || s6.getTieneElectricidad() != true) {
                if (s6.getAbasteElectrico() != null) {
                    Faces.messageWarning(null, "Para seleccionar un medio de Abastecimiento Electrico se debe habilitar la Opcion: 'Electricidad"
                            + "' en INFRAESTRUCTURA, INSTALACIONES Y SERVICIOS", "");
                    return false;
                }
            }
            if (s6.getRecoleccionbasura() == null || s6.getRecoleccionbasura() != true) {
                if (s6.getRecolBasura() != null) {
                    Faces.messageWarning(null, "Para seleccionar un medio de Eliminacion de Basura se debe habilitar la Opcion: 'Recoleccion. Basura"
                            + "' en INFRAESTRUCTURA, INSTALACIONES Y SERVICIOS", "");
                    return false;
                }
            }
            if (s6.getTieneAguaPotable() != null && s6.getTieneAguaPotable()) {
                if (s6.getAbastAgua() != null && s6.getAbastAgua().getValor().equals("No tiene")) {
                    if (s6.getAbastAguaRecibe() != null && !s6.getAbastAguaRecibe().getValor().equals("No tiene")) {
                        Faces.messageWarning(null, "El medio de abastecimiento del Agua debe estar en: 'No tiene'", "");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Boolean saveHistoric(CatPredio pred, String observacion, String fedifAnt, String fedifAct, String fmAnt, String fmAct, Boolean avalua) {
        try {
            if (avalua) {
                this.updateAvaluoPredio(this.predio);
            }
            pred = (CatPredio) em.find(CatPredio.class, pred.getId());
            String js = generarJson(pred);
            catas.guardarHistoricoPredio(pred.getNumPredio().longValue(), getPredioAnt(), js, usr, observacion, fedifAnt, fedifAct, fmAnt, fmAct, this.getDocumento());
            this.setPredioAnt(js);

            return true;
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar el Historico de: " + observacion);
            return false;
        }
    }

    public void guardarUsos(CatPredioS12 s12, List<CtlgItem> usos) {
        try {
            if (catas.guardarPredioS12(s12, usos) != null) {
                obs = "Actualizacion Informacion de Uso y Permiso";
                this.fichaEdifAnt = this.fichaEdifAct;
                this.fichaModelAnt = this.fichaModelAct;
                if (saveHistoric(predio, obs, this.fichaEdifAnt, this.fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
                    Faces.messageInfo(null, "Infomación.", "Los datos para (Informacion de Uso y Permiso), fueron actualizados con éxito");
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar el Historico de Informacion de Uso y Permiso prediales");
                }
            } else {
                Faces.messageError(null, "Error.", "Ocurrio un error al intentar guardar.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public Boolean contarFotos() {
        Integer countFotos = 0;
        if (predio.getPredioRaiz() == null) {
            countFotos = countFotos.valueOf(String.valueOf(em.find(Querys.getCantidadFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getId()})));
        } else {
            countFotos = countFotos.valueOf(String.valueOf(em.find(Querys.getCantidadFotosIdPredio, new String[]{"predio"}, new Object[]{predio.getPredioRaiz().longValue()})));
        }
        if (countFotos == 0) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public void guardarEscritura(CatEscritura esc) {
        if (catas.editarEscritura(esc)) {
            obs = "ACTUALIZACION DATOS ESCRITURA";
            this.fichaEdifAnt = this.fichaEdifAct;
            this.fichaModelAnt = this.fichaModelAct;
            if (saveHistoric(predio, obs, fichaEdifAnt, fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
                Faces.messageInfo(null, "Nota", "Escrituras registradas satisfactoriamente");
            } else {
                Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar el Historico de Escrituras prediales");
            }
        } else {
            Faces.messageWarning(null, "Advertencia", "No se pudo realizar la actualizacion de la escritura");
        }
    }

    public Boolean validateEdifEspecif(CatPredioEdificacion cpe) {
        if (dataBaseConnect()) {
            if (cpe.getAnioCons() != null && cpe.getVidautil() != null
                    && cpe.getNumPisos() != null && cpe.getAreaConsCenso() != null
                    && (cpe.getAreaConsCenso().compareTo(BigDecimal.ZERO) > 0)
                    && cpe.getEstadoConservacion() != null
                    && cpe.getNumPisos() > 0 && cpe.getVidautil() > 0
                    && cpe.getAnioCons() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }

    public void saveResponsable(CatEnte actualizadorPredio, CatEnte fiscalizador, CatEnte enteHorizontal) {
        try {
            if (actualizadorPredio != null && actualizadorPredio.getId() != null) {
                predio.setResponsableActualizadorPredial(actualizadorPredio);
            }
            if (fiscalizador != null && fiscalizador.getId() != null) {
                predio.setResponsableFiscalizadorPredial(fiscalizador);
            }
            if (enteHorizontal != null && enteHorizontal.getId() != null) {
                predio.setEnteHorizontal(enteHorizontal);
            }

            if (this.guardarDatosPredio("ACTUALIZACION DE RESPONSABLES", informante) == true) {

                if (catas.saveUpdatePredio(predio) != null) {
                    Faces.messageInfo(null, "Datos de Responsables Agregados Correctamente", "");
                } else {
                    Faces.messageWarning(null, "Advertencia", "No se pudo realizar la Actualizacion");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void eliminarFoto() {
        if (catas.quitarFoto(foto)) {
            Faces.messageInfo(null, "Nota!", "Foto eliminada satisfactoriamente, actualice la pagina para continuar");
        } else {
            Faces.messageWarning(null, "Advertencia!", "No se pudo eliminar la foto seleccionada");
        }
    }

    public Boolean databaseConnect() {
        Boolean database = Boolean.FALSE;
        switch (SchemasConfig.DB_ENGINE) {
            case ORACLE:
                database = Boolean.FALSE;
                break;
            case POSTGRESQL:
                database = Boolean.TRUE;
                break;
        }
        return database;
    }

    public Boolean validateEdificacion() {
        Boolean control = Boolean.FALSE;
        if (databaseConnect()) {
            if (edif != null && edif.getPredio() != null
                    && edif.getAnioCons() != null && edif.getVidautil() != null
                    && edif.getNumPisos() != null && edif.getAreaConsCenso() != null
                    && (edif.getAreaConsCenso().compareTo(BigDecimal.ZERO) > 0)
                    && edif.getEstadoConservacion() != null
                    && edif.getNumPisos() > 0 && edif.getVidautil() > 0
                    && edif.getAnioCons() > 0) {
                control = Boolean.TRUE;
            }
        } else {
            control = Boolean.TRUE;
        }
        return control;
    }

    public void finalizar() {
        predio.setFecMod(new Date());
        predio.setUsrMod(usr);
        catas.guardarPredio(predio);
        obs = "PREDIO FINALIZADO";
        this.fichaEdifAnt = this.fichaEdifAct;
        this.fichaModelAnt = this.fichaModelAct;
        if (saveHistoric(predio, obs, this.fichaEdifAnt, this.fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
            Faces.messageInfo(null, "Nota!", "Predio finalizado satisfactoriamente");
        } else {
            Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar el Historico del predio");
        }

    }

    public Boolean seccion1() {
        if (appconfig.isLocked(usr, predio.getId())) {
            Faces.messageWarning(null, "Advertencia!", "El predio esta siendo editado por otro usuario no se puede realizar el guardado!");
            return false;
        }
        if (Utils.isEmpty(usosA)) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar por lo menos un uso del predio.");
            return false;
        }
        if (Objects.isNull(predio.getTipoConjunto())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar el Espacio Urbano / Rural.");
            return false;
        }
        if (Objects.isNull(predio.getCiudadela())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar el Nombre del espacio urbano / rural.");
            return false;
        }
        return true;
    }

    public Boolean seccion2() {
        if (Objects.isNull(predio.getPropiedad())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Tenencia.");
            return false;
        }
        if (Objects.isNull(predio.getFormaAdquisicion())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Forma de Adquisición o Tenencia.");
            return false;
        }
        return true;
    }

    public Boolean seccion3() {
        if (Objects.isNull(caracteristicas.getLocManzana())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Localizacion en Mz.");
            return false;
        }
        if (!this.estaEdificado() && Utils.isNotEmpty((List<?>) predio.getCatPredioEdificacionCollection())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe eliminar los bloques para continuar.");
            return false;
        }
        if (Objects.isNull(caracteristicas.getEstadoSolar())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Ocupacion.");
            return false;
        }
        if (Objects.isNull(predio.getTipoSuelo())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Caract. Suelo.");
            return false;
        }
        if (Objects.isNull(predio.getTopografiaSolar())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Topografia.");
            return false;
        }
        if (Objects.isNull(predio.getFormaSolar())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Forma del predio.");
            return false;
        }
        if (Objects.isNull(predio.getConstructividad())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Constructividad.");
            return false;
        }
        return true;
    }

    public Boolean seccion5() {
        if (Objects.isNull(predio.getClasificacionVivienda())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la  Clasificacion Vivienda.");
            return false;
        }
        if (Objects.isNull(predio.getTipoVivienda())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Tipo Vivienda.");
            return false;
        }
        if (Objects.isNull(predio.getCondicionVivienda())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Condicion Vivienda.");
            return false;
        }
        if (Objects.isNull(servicios.getAbastAgua())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar Agua Proviene.");
            return false;
        }
        if (Objects.isNull(servicios.getAbastAguaRecibe())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar Agua Recibe.");
            return false;
        }
        if (Objects.isNull(servicios.getEvacAguasServ())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar Eliminacion Excretas.");
            return false;
        }
        if (Objects.isNull(predio.getTenenciaVivienda())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe seleccionar la Tenencia de Vivienda.");
            return false;
        }
        if (Objects.isNull(predio.getNumHabitaciones())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar el Numero de Habitaciones.");
            return false;
        }
        if (Objects.isNull(predio.getNumDormitorios())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar el Numero de Dormitorios.");
            return false;
        }
        if (Objects.isNull(predio.getNumEspaciosBanios())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingressar los Espacios Para Bañarse o Duchas.");
            return false;
        }
        if (Objects.isNull(predio.getHabitantes())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar el Numero de Habitantes.");
            return false;
        }
        if (Objects.isNull(predio.getNumHogares())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar el Numero de Hogares.");
            return false;
        }
        if (Objects.isNull(predio.getNumCelulares())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar el Numero de Telefonos Celulares .");
            return false;
        }
        if (Objects.isNull(predio.getVivCencalPoseeTelfConvencional())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingressar si Posee Telefono Convencional.");
            return false;
        }
        if (Objects.isNull(predio.getVivCencalServInternet())) {
            JsfUti.messageWarning(null, "Advertencia!", "Debe ingresar si tiene Servicio de Internet.");
            return false;
        }
        return true;
    }

    public Boolean estaEdificado() {
        try {
            if (predio == null) {
                return false;
            }
            if (predio.getBloque() == 0) {
                if (predio != null && caracteristicas != null) {
                    if (caracteristicas.getEstadoSolar() != null) {
                        return caracteristicas.getEstadoSolar().getId() == 58
                                || caracteristicas.getEstadoSolar().getValor().equalsIgnoreCase("EDIFICADO");
                    }
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
        return false;
    }

    public Boolean validaciones() {
        switch (this.idActionListenner) {
            case "btnS1": // IDENTIFICACION Y UBICACION DEL PREDIO
                if (!seccion1()) {
                    return false;
                }
                break;
            case "btnS2": // IDENTIFICACION LEGAL
                if (!seccion1()) {
                    return false;
                }
                if (!seccion2()) {
                    return false;
                }
                break;
            case "btnS3": // CARACTERIZACION DEL LOTE
                if (!seccion1()) {
                    return false;
                }
                if (!seccion2()) {
                    return false;
                }
                if (!seccion3()) {
                    return false;
                }
                break;
            case "btnS7": //  LINDEROS
                if (!seccion1()) {
                    return false;
                }
                if (!seccion2()) {
                    return false;
                }
                if (!seccion3()) {
                    return false;
                }
                break;
            case "btnS10": // VIVIENDA CENSAL
                if (!seccion1()) {
                    return false;
                }
                if (!seccion2()) {
                    return false;
                }
                if (!seccion3()) {
                    return false;
                }
                if (!seccion5()) {
                    return false;
                }
                break;
            case "btnS8": // responsables
                if (!seccion1()) {
                    return false;
                }
                if (!seccion2()) {
                    return false;
                }
                if (!seccion3()) {
                    return false;
                }
                break;

            default:
                break;
        }
        return true;
    }

    public List<CatParroquia> getParroquias() {
        return catas.getProrroquias(null);
    }

    public List<CatPropiedadItem> getTiposPropiedad() {
        return em.findAllEntCopy(CatPropiedadItem.class);
    }

    public List<CatTipoConjunto> getTiposConjunto() {
        return em.findAllEntCopy(CatTipoConjunto.class);
    }

    public List<CatCiudadela> getCiudadelas() {
        try {
            System.out.println("predio.getTipoConjunto()" + predio.getTipoConjunto());
            System.out.println("this.predio.getTipoConjunto()" + this.predio.getTipoConjunto());
            if (predio != null) {
                return catas.getCiudadelasByTipoConjunto(this.predio.getTipoConjunto());
            } else {
                return catas.getCiudadelasByTipoConjunto(null);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "getCiudadelas", e);
        }
        return null;
    }

    public void agregarLindero() {
        try {
            if (orientacion == null) {
                JsfUti.messageInfo(null, "Nota!", "Debe seleccionar la orientación.");
                return;
            }
            if (nombreLindero == null) {
                JsfUti.messageInfo(null, "Nota!", "No ha Ingresado o seleccionado ningun predio.");
                return;
            }
            if (nombreLindero.length() == 0) {
                JsfUti.messageInfo(null, "Nota!", "No ha Ingresado o seleccionado ningun predio.");
                return;
            }
            if (Utils.isEmpty(predio.getPredioCollection())) {
                predio.setPredioCollection(new ArrayList<>());
            }
            for (CatPredioLinderos cpl : predio.getPredioCollection()) {
                if (cpl.getPredio() != null) {
                    if (cpl.getPredio().equals(predioColind) && cpl.getOrientacion().equals(orientacion)) {
                        JsfUti.messageInfo(null, "Nota!", "Predio ya ha sido ingresado.");
                        return;
                    }
                }
            }
            CatPredioLinderos c = new CatPredioLinderos();
            c.setColindante(nombreLindero);
            c.setEn(enLongitudLindero);
            c.setEstado("A");
            c.setOrientacion(orientacion);
            c.setPredio(predio);
            c.setPredioColindante(predioColind);
            if (predio.getPredioCollection().add(c)) {
                JsfUti.messageInfo(null, "Nota!", "Predio agregado correctamente.");
                predioColind = null;
                nombreLindero = null;
                orientacion = null;
                guardarLinderos();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void actualizarLindero(CatPredioLinderos c) {
        try {
            if (c.getId() != null) {
                if (catas.actualizarLindero(c)) {
                    JsfUti.messageInfo(null, "Nota!", "Linderos Actualizados Correctamente.");
                };

            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void eliminarLindero(CatPredioLinderos c) {
        try {
            if (c.getId() != null) {
                c.setEstado("I");
                if (catas.actualizarLindero(c)) {
                    predio.getPredioCollection().remove(c);
                }
            } else {
                int index = 0;
                for (CatPredioLinderos lindero : predio.getPredioCollection()) {
                    if (lindero.getOrientacion() == c.getOrientacion()
                            && lindero.getColindante() == c.getColindante()) {
                        break;
                    }
                    index++;
                }
                predio.getPredioCollection().remove(index);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
        }
    }

    public void guardarLinderos() {
        try {
            if (!processMethod()) {
                return;
            }
            obs = "Actualizacion Informacion de Linderos";
            List<CatPredioLinderos> lind = catas.guardarLinderos(predio.getPredioCollection());
            if (Utils.isNotEmpty(lind)) {
                predio.setPredioCollection(lind);
                this.fichaModelAnt = this.fichaModelAct;
                if (saveHistoric(predio, obs, this.fichaEdifAnt, this.fichaEdifAct, this.fichaModelAnt, this.fichaModelAct, Boolean.TRUE)) {
                    Faces.messageInfo(null, "Nota!", "Linderos prediales actualizadas satisfactoriamente");
                } else {
                    Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar el Historico de Linderos prediales");
                }
            } else {
                Faces.messageWarning(null, "Advertencia!", "Ha ocurrido un error al actualizar los Linderos prediales, verifique que los campos esten ingresados correctamente");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

    public void updateDibujo() {
        Map<String, Object> parametros;
        parametros = new HashMap<>();
        parametros.put("codigopredio", this.predio.getNumPredio());
        Object o = em.executeFunction("sgm_app.update_datos_geodata", parametros, Boolean.FALSE);
        if (o != null) {
            Faces.messageInfo(null, "Datos Geograficos Actualizados Correctamente", "");
        }

    }

    public Boolean processMethod() {
        if (this.getMainConfig().getFichaPredial().getRenderDialogUploadDocument()) {
            if (this.getDocumento() == null) {
                JsfUti.messageInfo(null, "", "Debe de Subir un Documento Antes de Continuar");
                return false;
            }
        }
        return true;
    }

    public void bloque(CatPredioEdificacion bloque) {

        if (dataBaseConnect()) {
            if (!this.estaEdificado()) {
                if (caracteristicas.getEstadoSolar() != null) {
                    JsfUti.messageWarning(null, "Advertencia", "El predio tiene Ocupacion: ." + caracteristicas.getEstadoSolar().getValor());
                    return;
                }

            }
        }

        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(predio.getId().toString());
        params.put("idPredio", p);
        p = new ArrayList<>();
        if (bloque != null && bloque.getId() != null) {
            p.add(bloque.getId().toString());
            params.put("idCatPredioBloq", p);
        }

        p = new ArrayList<>();
        p.add(Boolean.toString((bloque == null)));
        params.put("nuevo", p);
        p = new ArrayList<>();
        p.add(ver.toString());
        params.put("ver", p);
        ver = false;
        Utils.openDialog("/resources/dialog/edificacionesPredio", params, "500");
    }

    public void procesarBloque(SelectEvent event) {
        CatPredioEdificacion bloque = (CatPredioEdificacion) event.getObject();
        if (bloques == null) {
            bloques = new ArrayList<>();
        }
        if (bloque != null) {
            if (!bloques.contains(bloque)) {
                bloques.add(bloque);
            } else {
                bloques.set(bloques.indexOf(bloque), bloque);
            }
            if (predio.getAreaDeclaradaConst() == null) {
                predio.setAreaDeclaradaConst(BigDecimal.ZERO);
            }
            BigDecimal areaConst = BigDecimal.ZERO;
            for (CatPredioEdificacion b : bloques) {
                if (b.getAreaConsCenso() == null) {
                    b.setAreaConsCenso(BigDecimal.ZERO);
                }
                areaConst = areaConst.add(b.getAreaConsCenso());
            }
            predio.setAreaDeclaradaConst(areaConst);
            predio.setCatPredioEdificacionCollection(bloques);
            predio = catas.savePredio(predio);
            String js = generarJson(predio);
            if (this.saveHistoric(predio, "Actualizacion de Edificaciones", null, null, this.getPredioAnt(), js, Boolean.TRUE)) {
                Faces.messageInfo(null, "Nota!", "Bloques actualizados satisfactoriamente");
            }
        }
    }

    public void eliminarBloque(CatPredioEdificacion edificacion) {
        try {
            edificacion.setEstado("I");
            edificacion.setModificado(usr);
            //reemplaza el numero de bloque de los bloques superiores al eliminado
            short num_bloque = edificacion.getNoEdificacion();
            int diferencia = 0;

            for (CatPredioEdificacion bloq : bloques) {
                if (bloq.getNoEdificacion() > num_bloque) {
                    diferencia = (bloq.getNoEdificacion() - 1);
                    bloq.setNoEdificacion((short) diferencia);
                    catas.guardarEdificacion(bloq, usr);
                }
            }
            catas.guardarEdificacion(edificacion, usr);
            if (predio.getAreaDeclaradaConst() == null) {
                predio.setAreaDeclaradaConst(BigDecimal.ZERO);
            }
            if (edificacion.getAreaBloque() == null) {
                edificacion.setAreaBloque(BigDecimal.ZERO);
            }
            if (predio.getAreaDeclaradaConst().doubleValue() > 0) {
                predio.setAreaDeclaradaConst(predio.getAreaDeclaradaConst().subtract(edificacion.getAreaBloque()));
            }

            bloques.remove(edificacion);
            predio.setCatPredioEdificacionCollection(bloques);
            predio = catas.savePredio(predio);
            String js = generarJson(predio);
            if (this.saveHistoric(predio, "Actualizacion de Edificaciones", null, null, this.getPredioAnt(), js, Boolean.TRUE)) {
                JsfUti.messageInfo(null, "Edificacion", "Edificacion eliminada.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, usr, e);
        }
    }

    public void setNamePredioByCiudadela() {
        if (predio.getNombreEdificio() == null) {
            if (predio.getCiudadela() != null) {
                if (predio.getCiudadela().getNombre() != null) {
                    predio.setNombreEdificio(predio.getCiudadela().getNombre());
                }
            }
        } else {
            if (predio.getNombreEdificio().equals("")) {
                if (predio.getCiudadela() != null) {
                    if (predio.getCiudadela().getNombre() != null) {
                        predio.setNombreEdificio(predio.getCiudadela().getNombre());
                    }
                }
            }
        }
    }

    public CatPredioEdificacion getEdif() {
        return edif;
    }

    public void setEdif(CatPredioEdificacion edif) {
        this.edif = edif;
    }

    public int getMaxEdif() {
        return maxEdif;
    }

    public void setMaxEdif(int maxEdif) {
        this.maxEdif = maxEdif;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public List<CatCategoriasConstruccion> getCategroriasConst() {
        return em.findAllEntCopy(CatCategoriasConstruccion.class);
    }

    public CatastroServices getCatas() {
        return catas;
    }

    public void setCatas(CatastroServices catas) {
        this.catas = catas;
    }

    public String getPredioAnt() {
        return predioAnt;
    }

    public void setPredioAnt(String predioAnt) {
        this.predioAnt = predioAnt;
    }

    public Boolean getAvCalle() {
        return avCalle;
    }

    public void setAvCalle(Boolean avCalle) {
        this.avCalle = avCalle;
    }

    public FotoPredio getFoto() {
        return foto;
    }

    public void setFoto(FotoPredio foto) {
        this.foto = foto;
    }

    public Collection<CatEdificacionPisosDet> getPisos() {
        return pisos;
    }

    public void setPisos(Collection<CatEdificacionPisosDet> pisos) {
        this.pisos = pisos;
    }

    public String claveCatastral(CatPredio px) {

        String clave = "";
        switch (SchemasConfig.DB_ENGINE) {
            case ORACLE:
                clave = Utils.completarCadenaConCeros(px.getProvincia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getCanton().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getParroquia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getZona().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getSector().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getMz().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getSolar().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getBloque().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getPiso().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getUnidad().toString(), 3);

            case POSTGRESQL:

                clave = Utils.completarCadenaConCeros(px.getProvincia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getCanton().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getParroquia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getZona().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getSector().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getMz().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getSolar().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getBloque().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getPiso().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getUnidad().toString(), 3);

                break;
            default:
                break;
        }
        return clave;

    }

    public String claveCroquis(CatPredio px) {
        String clave = "";
        switch (SchemasConfig.DB_ENGINE) {
            case ORACLE:
                clave = Utils.completarCadenaConCeros(px.getZona().toString(), 2) + "-"
                        + Utils.completarCadenaConCeros(px.getSector().toString(), 3) + "-"
                        + Utils.completarCadenaConCeros(px.getMz().toString(), 3) + "-"
                        + Utils.completarCadenaConCeros(px.getSolar().toString(), 2);
                return clave;
            case POSTGRESQL:
                clave = Utils.completarCadenaConCeros(px.getProvincia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getCanton().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getParroquia().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getZona().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getSector().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getMz().toString(), 2)
                        + Utils.completarCadenaConCeros(px.getSolar().toString(), 3)
                        + Utils.completarCadenaConCeros(px.getBloque().toString(), 1)
                        + Utils.completarCadenaConCeros(px.getPiso().toString(), 1)
                        + Utils.completarCadenaConCeros(px.getUnidad().toString(), 1);

                break;
            default:
                break;
        }
        return clave;

    }

    public String getFichaEdifAnt() {
        return fichaEdifAnt;
    }

    public void setFichaEdifAnt(String fichaEdifAnt) {
        this.fichaEdifAnt = fichaEdifAnt;
    }

    public String getFichaEdifAct() {
        return fichaEdifAct;
    }

    public void setFichaEdifAct(String fichaEdifAct) {
        this.fichaEdifAct = fichaEdifAct;
    }

    public String getFichaModelAnt() {
        return fichaModelAnt;
    }

    public void setFichaModelAnt(String fichaModelAnt) {
        this.fichaModelAnt = fichaModelAnt;
    }

    public String getFichaModelAct() {
        return fichaModelAct;
    }

    public void setFichaModelAct(String fichaModelAct) {
        this.fichaModelAct = fichaModelAct;
    }

    public GeDocumentos getDocumento() {
        return documento;
    }

    public void setDocumento(GeDocumentos documento) {
        this.documento = documento;
    }

    public String getIdActionListenner() {
        return idActionListenner;
    }

    public void setIdActionListenner(String idActionListenner) {
        this.idActionListenner = idActionListenner;
    }

    public String getRemotteCommand() {
        return remotteCommand;
    }

    public void setRemotteCommand(String remotteCommand) {
        this.remotteCommand = remotteCommand;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEnte getInformante() {
        return informante;
    }

    public void setInformante(CatEnte informante) {
        this.informante = informante;
    }

    public CatPredio getPredioColind() {
        return predioColind;
    }

    public void setPredioColind(CatPredio predioColind) {
        this.predioColind = predioColind;
    }

    public CtlgItem getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(CtlgItem orientacion) {
        this.orientacion = orientacion;
    }

    public String getNombreLindero() {
        return nombreLindero;
    }

    public void setNombreLindero(String nombreLindero) {
        this.nombreLindero = nombreLindero;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public void setMainConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    public CatPredioS4 getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(CatPredioS4 caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public CatPredioS6 getServicios() {
        return servicios;
    }

    public void setServicios(CatPredioS6 servicios) {
        this.servicios = servicios;
    }

    public String completarCeros(Number n, Integer numCaracteres) {
        try {
            return Utils.completarCadenaConCeros(n.toString(), numCaracteres);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Completar con ceros", e);
        }
        return "";
    }

    public TreeNode getNodoEdificaciones() {
        return nodoEdificaciones;
    }

    public void setNodoEdificaciones(TreeNode nodoEdificaciones) {
        this.nodoEdificaciones = nodoEdificaciones;
    }

    public Boolean getVer() {
        return ver;
    }

    public void setVer(Boolean ver) {
        this.ver = ver;
    }

    public List<CatPredioEdificacion> getBloques() {
        return bloques;
    }

    public void setBloques(List<CatPredioEdificacion> bloques) {
        this.bloques = bloques;
    }

    public String generarJson(CatPredio p) {
        if (p.getTipoConjunto() != null) {
            p.getTipoConjunto().setCatCiudadelaCollection(null); // No cargar la collection de Ciudadelas.
        }
        if (p.getEscrituraLinderos() != null && p.getEscrituraLinderos().getCanton() != null) {
            p.getEscrituraLinderos().getCanton().setCatParroquiaCollection(null);// no cargar la collection y parroquias.
        }
        if (p.getCatEscrituraCollection() != null) {
            for (CatEscritura catEscritura : p.getCatEscrituraCollection()) {
                if (catEscritura.getCanton() != null) {
                    catEscritura.getCanton().setCatParroquiaCollection(null);
                }
            }
        }
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.generarJson(p);
    }

    public String generarJson(FichaEdificacionesModel p) {
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.generarJson(p);
    }

    public String generarJson(List<FichaModel> p) {
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.generarJson(p);
    }

    public List<RenLiquidacion> getCatPrediosAfectadosLiquidaciones() {
        return catPrediosAfectadosLiquidaciones;
    }

    public void setCatPrediosAfectadosLiquidaciones(List<RenLiquidacion> catPrediosAfectadosLiquidaciones) {
        this.catPrediosAfectadosLiquidaciones = catPrediosAfectadosLiquidaciones;
    }

    public String getEnLongitudLindero() {
        return enLongitudLindero;
    }

    public void setEnLongitudLindero(String enLongitudLindero) {
        this.enLongitudLindero = enLongitudLindero;
    }

    public List<GeDocumentos> getGeDocumentosList() {
        return geDocumentosList;
    }

    public void setGeDocumentosList(List<GeDocumentos> geDocumentosList) {
        this.geDocumentosList = geDocumentosList;
    }

}
