/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.entities.CatNormasConstruccionHasRetirosAumento;
import com.origami.sgm.entities.CatNormasConstruccionTipo;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatSolicitudNormaConstruccion;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;
import util.Utils;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class SolicitudNormasConstruccion extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;
    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    @javax.inject.Inject
    protected DivisionPredioServices divisonServices;

    @Inject
    private ServletSession ss;
    protected PdfReporte reporte;

    protected Boolean subirPdf = false;
    protected Boolean impreso = false;
    protected Boolean ocultarGuardar = false;
    protected Boolean nuevaNorma = false;
    protected Boolean hayImagen = false;
    protected Boolean isEspecial = null;
    protected Long numPredio;
    protected Long idNorma;
    protected Long idNuevaNorma;
    protected String representanteLegal;

    private MsgFormatoNotificacion ms;
    protected GeTipoTramite tramite;
    protected HistoricoTramites ht;
    protected Observaciones obs;
    protected CatPredio predio;
    protected CatEscritura escritura;
    protected CatCiudadela ciudadela;
    protected CatEnte responsable;
    protected CatEnte propietario;
    protected CatNormasConstruccionTipo nuevoTipoNorma;
    protected CatNormasConstruccion normaConstruccion;
    protected CatSolicitudNormaConstruccion solicitud;
    protected CatEnteLazy enteLazy;

    protected List<CatPredioPropietario> listaPropietarios;
    protected List<CatPredioPropietario> listaPropietariosEliminar;
    protected List<CatCiudadela> ciudadelaList;
    protected List<CatNormasConstruccionHasRetirosAumento> listaRetiros;
    protected List<CatNormasConstruccionHasRetirosAumento> listaAlturas;
    protected List<CatNormasConstruccionHasRetirosAumento> listaDisposicionGeneral;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            initView();
        }
    }

//    @PostConstruct
    public void initView() {
        try{
            System.out.println("task id " + session.getTaskID());
            if (session != null && session.getTaskID() != null) {
                ht = new HistoricoTramites();
                tramite = new GeTipoTramite();
                setTaskId(session.getTaskID());

                ht = permisoService.getHistoricoTramiteById((Long) this.getVariable(session.getTaskID(), "tramite"));
                update();
                tramite = permisoService.getGeTipoTramiteById(ht.getTipoTramite().getId());
                ciudadela = new CatCiudadela();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void update() {
        if (subirPdf) {
//            obs = new Observaciones();
        } else {
            predio = new CatPredio();
            responsable = new CatEnte();
            ciudadela = new CatCiudadela();
            solicitud = new CatSolicitudNormaConstruccion();
            normaConstruccion = new CatNormasConstruccion();
            ciudadelaList = new ArrayList<>();
            listaPropietarios = new ArrayList<>();
            listaPropietariosEliminar = new ArrayList<>();
            ciudadelaList = fichaServices.getCiudadelas();

            Calendar calender = Calendar.getInstance();
            int anio = calender.get(Calendar.YEAR);
            solicitud.setTramite(ht.getId().intValue());
            solicitud.setAnioTramite(anio);
            solicitud.setUrbMz(ht.getMz());
            solicitud.setUrbSolar(ht.getSolar());

            HistoricoTramiteDet h;
            for (HistoricoTramiteDet d : ht.getHistoricoTramiteDetCollection()) {
                if (d.getEstado()) {
                    predio = d.getPredio();
                }
            }
            if (predio != null) {
                consultarCodPredio();
            }
            if (idNuevaNorma != null) {
                CatNormasConstruccion norm = normasServices.getCatNormasConstruccion(idNuevaNorma);
                if (norm != null) {
                    idNorma = norm.getTipoNorma().getId();
                    if (!isEspecial) {
                        ciudadela = norm.getIdCiudadela();
                    }
                    normaSelecionada(isEspecial);
                }
            }

        }
        JsfUti.update("formNormaConst");
    }

    public void validar() {
        if (validaFiles()) {
            obs = new Observaciones();
            JsfUti.update("frmObsCor");
            JsfUti.executeJS("PF('obs').show();");
        }
    }

    public void consultarNumPredio() {
        try{
            if (numPredio == null) {
                JsfUti.messageError(null, "Debe Ingresar el NÃºmero de Predio", "");
                return;
            }
            CatPredio pred1 = fichaServices.getPredioByNum(numPredio);
            if (pred1 == null) {
                JsfUti.messageError(null, "No hay registro con el NÃºmero de predio ingresado", "");
                return;
            }
            predio = pred1;
            solicitud.setIdPredio(predio);
    //        solicitud.setUrbMz(predio.getUrbMz());
    //        solicitud.setUrbSolar(predio.getUrbSolarnew());

            escritura = new CatEscritura();
            if (solicitud.getIdPredio().getId().compareTo(Utils.isNull(solicitud.getIdPredio().getPredioRaiz()).longValue()) == 0) {

                escritura = fichaServices.getCatEscrituraByPredio(predio.getId());
                /*solicitud.setLinderoEsteEscritura((escritura.getLindEscrEste() != null) ? new BigDecimal(escritura.getLindEscrEste()) : null);
                solicitud.setLinderoOesteEscritura((escritura.getLindEscrOeste() != null) ? new BigDecimal(escritura.getLindEscrOeste()) : null);
                solicitud.setLinderoNorteEscritura((escritura.getLindEscrNorte() != null) ? new BigDecimal(escritura.getLindEscrNorte()) : null);
                solicitud.setLinderoSurEscritura((escritura.getLindEscrSur() != null) ? new BigDecimal(escritura.getLindEscrSur()) : null);*/
//                solicitud.setLinderoEsteEscritura(escritura.getLindEscrEsteCon());
//                solicitud.setLinderoOesteEscritura(escritura.getLindEscrOesteCon());
//                solicitud.setLinderoNorteEscritura(escritura.getLindEscrNorteCon());
//                solicitud.setLinderoSurEscritura(escritura.getLindEscrSurCon());
            } else {
                if (predio.getPredioRaiz() != null) {
                    escritura = fichaServices.getCatEscrituraByPredio(predio.getPredioRaiz().longValue());
                }else{
                    escritura = fichaServices.getCatEscrituraByPredio(predio.getId());
                }
//                solicitud.setLinderoEsteEscritura(escritura.getLindEscrEsteCon());
//                solicitud.setLinderoOesteEscritura(escritura.getLindEscrOesteCon());
//                solicitud.setLinderoNorteEscritura(escritura.getLindEscrNorteCon());
//                solicitud.setLinderoSurEscritura(escritura.getLindEscrSurCon());
            }
            listaPropietarios = new ArrayList<>();
            List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
            if (!propietariosTemp.isEmpty()) {
                for (CatPredioPropietario temp : propietariosTemp) {
                    if ("A".equalsIgnoreCase(temp.getEstado())) {
                        listaPropietarios.add(temp);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void consultarCodPredio() {
        try{
            CatPredio pred = permisoService.getCatPredioByCodigoPredio(predio.getProvincia(), predio.getCanton(), predio.getParroquia(), predio.getZona(), predio.getSector(), predio.getMz(), predio.getLote(),
                    predio.getBloque(), predio.getPiso(), predio.getUnidad(), "A");
            if (pred != null) {
                predio = pred;
                if (predio.getNumPredio() != null) {
                    numPredio = predio.getNumPredio().longValue();
                } else {
                    numPredio = null;
                }
                solicitud.setIdPredio(predio);
    //            solicitud.setUrbMz(predio.getUrbMz());
    //            solicitud.setUrbSolar(predio.getUrbSolarnew());
                escritura = new CatEscritura();
                if (solicitud.getIdPredio().getId().compareTo(Utils.isNull(solicitud.getIdPredio().getPredioRaiz()).longValue()) == 0) {
                    escritura = fichaServices.getCatEscrituraByPredio(predio.getId());
                    if (escritura != null) {
//                        if (escritura.getLindEscrEste() != null) {
//                            solicitud.setLinderoEsteEscritura(new BigDecimal(escritura.getLindEscrEste()));
//                        }
//                        if (escritura.getLindEscrOeste() != null) {
//                            solicitud.setLinderoOesteEscritura(new BigDecimal(escritura.getLindEscrOeste()));
//                        }
//                        if (escritura.getLindEscrNorte() != null) {
//                            solicitud.setLinderoNorteEscritura(new BigDecimal(escritura.getLindEscrNorte()));
//                        }
//                        if (escritura.getLindEscrSur() != null) {
//                            solicitud.setLinderoSurEscritura(new BigDecimal(escritura.getLindEscrSur()));
//                        }
                    }

                } else {
                    if (predio.getPredioRaiz() != null) {
                        escritura = fichaServices.getCatEscrituraByPredio(predio.getPredioRaiz().longValue());
                    }else{
                        escritura = fichaServices.getCatEscrituraByPredio(predio.getId());
                    }
                    if (escritura != null) {
//                        solicitud.setLinderoEsteEscritura(escritura.getLindEscrEsteCon());
//                        solicitud.setLinderoOesteEscritura(escritura.getLindEscrOesteCon());
//                        solicitud.setLinderoNorteEscritura(escritura.getLindEscrNorteCon());
//                        solicitud.setLinderoSurEscritura(escritura.getLindEscrSurCon());
                    }

                }
                listaPropietarios = new ArrayList<>();
                List<CatPredioPropietario> propietariosTemp = (List<CatPredioPropietario>) predio.getCatPredioPropietarioCollection();
                if (!propietariosTemp.isEmpty()) {
                    for (CatPredioPropietario temp : propietariosTemp) {
                        if ("A".equalsIgnoreCase(temp.getEstado())) {
                            listaPropietarios.add(temp);
                        }
                    }
                }
            } else {
                JsfUti.messageError(null, "No hay registro con el CÃ³digo de predio ingresado ir al DEPARTAMENTO DE CATASTRO PARA QUE INGRESEN EL PREDIO", "");
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void renderPersNat() {
        propietario = new CatEnte();
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgNuevPro').show()");
    }

    public void buscarPropietario() {
        if (propietario.getCiRuc() != null) {
            CatEnte nuwEnt = fichaServices.getCatEnte(propietario.getCiRuc());
            if (nuwEnt != null) {
                for (CatPredioPropietario listCat1 : listaPropietarios) {
                    if (listCat1.getEnte().getId().compareTo(nuwEnt.getId()) == 0) {
                        JsfUti.messageInfo(null, "Cliente ya fue agregado al predio", "");
                        return;
                    }
                }
                CatPredioPropietario entP = new CatPredioPropietario();
                propietario = nuwEnt;
                entP.setEnte(propietario);
                entP.setPredio(predio);
                entP.setEstado("A");
                listaPropietarios.add(entP);
                propietario = new CatEnte();
                JsfUti.update("formNormaConst:dtPropiet");
                JsfUti.executeJS("PF('dlgNuevPro').hide()");
            }
        }
    }

    public void agregarPropietario() {
        if (propietario != null) {
            if (!propietario.getEsPersona()) {
                if (representanteLegal == null) {
                    JsfUti.messageInfo(null, "Debe seleccionar el Representante Legal", "");
                    return;
                }
            }
            CatEnte nuwEnt = null;
            if (propietario.getCiRuc() != null) {
                nuwEnt = fichaServices.getCatEnte(propietario.getCiRuc());
            }
            if (nuwEnt != null) {
                for (CatPredioPropietario listCat1 : listaPropietarios) {
                    if (listCat1.getEnte().getCiRuc().compareTo(propietario.getCiRuc()) == 0) {
                        JsfUti.messageInfo(null, "Ya fue agregado un propietario con el mismo nÃºmero de documento", "");
                        return;
                    }
                }
            }
            CatPredioPropietario entP = new CatPredioPropietario();
            entP.setEstado("A");
            entP.setEnte(propietario);
            entP.setPredio(predio);
            entP.setModificado("edificacion");
            listaPropietarios.add(entP);
            propietario = null;
            representanteLegal = "";
            JsfUti.executeJS("PF('dlgNuevPro').hide()");
            JsfUti.update("formNormaConst:dtPropiet");
        }
    }

    public void eliminarProp(CatPredioPropietario prop) {
        if (listaPropietarios.size() == 1) {
            JsfUti.messageError(null, "No puede Eliminar todos los propietarios", "");
            return;
        }
        int index = 0;
        int i = 0;
        for (CatPredioPropietario listCat1 : listaPropietarios) {
            if (listCat1.getId() != null) {
                if (listCat1.getId().compareTo(prop.getId()) == 0) {
                    index = i;
                    break;
                }
            } else {
                if (listCat1.getEnte().equals(prop.getEnte())) {
                    index = i;
                    break;
                }
            }
            i++;
        }
        if (prop.getId() != null) {
            listaPropietariosEliminar.add(listaPropietarios.get(index));
        }

        prop.setModificado("edificacion");
        prop.setEstado("I");
        listaPropietarios.remove(index);
        JsfUti.update("formNormaConst:dtPropiet");
    }

    public void seleccionarReprest() {
        enteLazy = new CatEnteLazy(true);
        JsfUti.update("formSelectInterv");
        JsfUti.executeJS("PF('dlgSelectReprest').show();");
    }

    public void agregarRepresentante(CatEnte represt) {
        propietario.setRepresentanteLegal(new BigInteger(represt.getId().toString()));
        if (represt.getApellidos() != null && represt.getNombres() != null) {
            representanteLegal = represt.getApellidos() + " " + represt.getNombres();
        } else if (represt.getApellidos() != null) {
            representanteLegal = represt.getApellidos();
        } else if (propietario.getNombres() != null) {
            representanteLegal = represt.getNombres();
        }
        JsfUti.update("formNuProp");
        JsfUti.executeJS("PF('dlgSelectReprest').hide()");
    }

    public List<CatNormasConstruccionTipo> getListNormaTipo() {
        return normasServices.getCatNormasConstruccionTipo(false);
    }

    public List<CatNormasConstruccionTipo> getListNormaTipoEspecial() {
        return normasServices.getCatNormasConstruccionTipo(true);
    }

    public void tipoNormaEspecial() {
        nuevoTipoNorma = new CatNormasConstruccionTipo();
        JsfUti.executeJS("PF('tipoNormEsp').show();");
    }

    public void ingresarNuevoTipo() {
        nuevoTipoNorma = new CatNormasConstruccionTipo();
        nuevaNorma = true;
//        JsfUti.update("formTipoNormEsp:nuevoTipo");
    }

    public void guardarNuevaNormaTipo() {
        if (nuevoTipoNorma.getTipo() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar el Nombre del: TIPO DE NORMA", "");
            return;
        }
        List<CatNormasConstruccionTipo> list = getListNormaTipo();
        for (CatNormasConstruccionTipo l : list) {
            if (l.getTipo().compareToIgnoreCase(nuevoTipoNorma.getTipo()) == 0) {
                JsfUti.messageInfo(null, "TIPO NORMA ya existe. ", "");
                return;
            }
        }
        nuevoTipoNorma.setIsEspecial(true);
        normasServices.guardarCatNormasConstruccionTipo(nuevoTipoNorma);
        nuevaNorma = false;
        JsfUti.update("formTipoNormEsp");
    }

    public void guardarNuevaNormaTipoCiud() {
        if (nuevoTipoNorma.getTipo() == null) {
            JsfUti.messageInfo(null, "Debe Ingresar el Nombre del: TIPO DE NORMA", "");
            return;
        }
        List<CatNormasConstruccionTipo> list = getListNormaTipo();
        for (CatNormasConstruccionTipo l : list) {
            if (l.getTipo().compareToIgnoreCase(nuevoTipoNorma.getTipo()) == 0) {
                JsfUti.messageInfo(null, "TIPO NORMA ya existe. ", "");
                return;
            }
        }
        nuevoTipoNorma.setIsEspecial(false);
        normasServices.guardarCatNormasConstruccionTipo(nuevoTipoNorma);
        nuevaNorma = false;
        JsfUti.update("formTipoNorm");
    }

    public void normaSelecionada(Boolean isEspecial) {
        if (idNorma != null) {
            if (isEspecial) {
                normaConstruccion = normasServices.getCatNormasConstruccion(idNorma, null);
            } else {
                normaConstruccion = normasServices.getCatNormasConstruccion(idNorma, ciudadela.getId());
            }
            if (normaConstruccion != null) {
                listaRetiros = new ArrayList<>();
                listaAlturas = new ArrayList<>();
                listaDisposicionGeneral = new ArrayList<>();
                List<CatNormasConstruccionHasRetirosAumento> list = (List<CatNormasConstruccionHasRetirosAumento>) normaConstruccion.getCatNormasConstruccionHasRetirosAumentoCollection();
                for (CatNormasConstruccionHasRetirosAumento list1 : list) {
                    switch (list1.getCodigo().intValue()) {
                        case 1:
                            list1.setNombreCodigo("Retiro Frontal");
                            listaRetiros.add(list1);
                            break;
                        case 2:
                            list1.setNombreCodigo("Retiro Lateral");
                            listaRetiros.add(list1);
                            break;
                        case 3:
                            list1.setNombreCodigo("Retiro Posterior");
                            listaRetiros.add(list1);
                            break;
                        case 4:
                            list1.setNombreCodigo("Altura Max. EdificaciÃ³n");
                            listaAlturas.add(list1);
                            break;
                        case 5:
                            list1.setNombreCodigo("Altura Max. Cerramiento");
                            listaAlturas.add(list1);
                            break;
                        case 6:
                            list1.setNombreCodigo("Altura Antepechos de Ventanas");
                            listaAlturas.add(list1);
                            break;
                        case 7:
                            list1.setNombreCodigo("DisposiciÃ³n General");
                            listaDisposicionGeneral.add(list1);
                            break;
                    }
                }
                solicitud.setNormaConstruccion(normaConstruccion);
                solicitud.setTipoEdificacion(normaConstruccion.getTipoNorma().getTipo());
                hayImagen = normaConstruccion.getImafoto() != null;
                JsfUti.update("formNormaConst");
            } else {
                redirectNuevaNorma(isEspecial);
            }
        }
    }

    private void redirectNuevaNorma(Boolean especial) {
        List<CatNormasConstruccionTipo> list;
        if (especial) {
            list = getListNormaTipoEspecial();
        } else {
            list = getListNormaTipo();
        }
        Long id = null;
        if (idNorma != null) {
            for (CatNormasConstruccionTipo list1 : list) {
                if (list1.getId().compareTo(idNorma) == 0) {
                    id = list1.getId();
                }
            }
        }
        if (especial) {
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccion.xhtml?proceso="
                    + session.getTaskID() + "&tipoNorma=" + id);
        } else {
            JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccion.xhtml?proceso="
                    + session.getTaskID() + "&idCiudadela=" + ciudadela.getId() + "&tipoNorma=" + id);
        }
    }

    public void buscarResponsable() {
        if (responsable.getCiRuc() == null) {
            JsfUti.messageError(null, "Debe Ingresar NÃºmero de CÃ©dula", "");
            return;
        }
        CatEnte ente = permisoService.getCatEnteByCiRucByEsPersona(responsable.getCiRuc(), true);
        if (ente != null) {
            responsable = ente;
            solicitud.setIdResponsable(responsable);
            JsfUti.update("formNormaConst:resp");
            JsfUti.update("formNormaConst:RegPro");
        } else {
            String ced = responsable.getCiRuc();
            responsable = new CatEnte();
            responsable.setCiRuc(ced);
            responsable.setEsPersona(true);
            JsfUti.update("formEditResp");
            JsfUti.executeJS("PF('dlgEditRespTec').show()");
        }
    }

    public void editarResponsable() {
        if (solicitud.getIdResponsable() != null) {
            responsable = new CatEnte();
            responsable = solicitud.getIdResponsable();
            JsfUti.update("formEditResp");
            JsfUti.executeJS("PF('dlgEditRespTec').show();");
        } else {
            JsfUti.messageInfo(null, "No hay ningun Responsable Ingresado", "");
        }
    }

    public void guardarResponsable() {
        if (responsable.getCiRuc() == null) {
            JsfUti.messageError(null, "Debe Ingresar NÃºmero de CÃ©dula", "");
            return;
        }
        if (!Utils.validateNumberPattern(responsable.getCiRuc())) {
            JsfUti.messageInfo(null, "Solo Debe ingresar NÃºmeros", "");
            return;
        }
        if (!Utils.validateCCRuc(responsable.getCiRuc())) {
            JsfUti.messageInfo(null, "NÃºmero de CÃ©dula Invalido", "");
            return;
        }
        if (responsable.getId() == null) {
            CatEnte ente = permisoService.getCatEnteByCiRucByEsPersona(responsable.getCiRuc(), true);
            if (ente != null) {
                JsfUti.messageInfo(null, "Ya existe una persona registrada con el mismo nÃºmero de cÃ©dula", "");
                return;
            }
//            responsable.setEsPersona(true);
            responsable.setEstado("A");
            responsable = fichaServices.guardarCatEnte(responsable);
        } else {
            normasServices.actualizarCatEnte(responsable);
        }
        JsfUti.executeJS("PF('dlgEditRespTec').hide();");
        JsfUti.update("formNormaConst:resp");
        JsfUti.update("formNormaConst:RegPro");
    }

    public void buscarNormaAsociada() {
        idNorma = null;
        nuevoTipoNorma = new CatNormasConstruccionTipo();
        JsfUti.executeJS("PF('tipoNorm').show()");
    }

    public void grabarSolicitudNormas() {
        if (solicitud.getIdResponsable() == null) {
            JsfUti.messageInfo(null, "Falta de buscar al Responsable TÃ©cnico dar clic en la Lupa que se encuentra despues de Ingresar El NÃºmero de Cedula", "");
            return;
        }
        if (!normaConstruccion.getTipoNorma().getIsEspecial()) {
            if (ciudadela == null) {
                JsfUti.messageInfo(null, "Falta seleccionar la ciudadela.", "");
                return;
            }
        }

        ocultarGuardar = false;
        JsfUti.update("formNormaConst");
        Calendar cl = Calendar.getInstance();
        solicitud.setFechaEmision(cl.getTime());
        cl.add(Calendar.MONTH, 6);
        solicitud.setFechaCaducidad(cl.getTime());

        if (ciudadela != null) {
            solicitud.setIdCiudadela(ciudadela);
        }
        if (solicitud.getLinderoEsteEscritura() != null) {
//            escritura.setLindEscrEsteCon(solicitud.getLinderoEsteEscritura());
        }
        if (solicitud.getLinderoOesteEscritura() != null) {
//            escritura.setLindEscrOesteCon(solicitud.getLinderoOesteEscritura());
        }
        if (solicitud.getLinderoNorteEscritura() != null) {
//            escritura.setLindEscrNorteCon(solicitud.getLinderoNorteEscritura());
        }
        if (solicitud.getLinderoSurEscritura() != null) {
//            escritura.setLindEscrSurCon(solicitud.getLinderoSurEscritura());
        }
        if (solicitud.getAreaTotal() != null) {
            escritura.setAreaSolar(solicitud.getAreaTotal());
        }

        normasServices.guardarOActualizarCatPredioPropietario(listaPropietarios, listaPropietariosEliminar, predio);

        solicitud = normasServices.guardarCatSolicitudNormaConstruccion(solicitud);
        if (solicitud != null) {
            JsfUti.messageInfo(null, "Datos Grabados con exito", "");
            ocultarGuardar = true;
        } else {
            JsfUti.messageInfo(null, "Error", "");
        }
        JsfUti.update("formNormaConst");
    }

    public void mostarObservacion() {
        obs = new Observaciones();
        JsfUti.executeJS("PF('obs').show()");
    }

    public void imprimirNorma() {
        if (obs.getObservacion() == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        obs.setEstado(Boolean.TRUE);
        obs.setFecCre(new Date());
        obs.setIdTramite(ht);
        obs.setUserCre(session.getName_user());
        obs.setTarea("Subir Pdf de Norma");
        if (permisoService.guardarObservacion(obs) != null) {
            String nombrereporte;
            if (normaConstruccion.getTipoNorma().getIsEspecial()) {
                nombrereporte = "normaConstruccionSinCiudadela";
            } else {
                nombrereporte = "reportePrueba";
            }
            try {
                ss.instanciarParametros();
                AclUser user = permisoService.getAclUserByUser(session.getName_user());
                String nombreTecnico = null;
                if (user.getEnte() != null) {
                    nombreTecnico = this.getNombrePropietario(user.getEnte());
                }else{
                    nombreTecnico = user.getUsuario();
                }
                if (nombreTecnico != null) {
                        nombreTecnico = nombreTecnico.toUpperCase();
                    }
                AclUser firmaDirector = permisoService.getAclUserByUser(tramite.getUserDireccion());
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

                CatNormasConstruccion normaConst = solicitud.getNormaConstruccion();
                if (normaConst.getImafoto() != null) {
                    ss.agregarParametro("logo", new ByteArrayInputStream(normaConst.getImafoto()));
                } else {
                    ss.agregarParametro("logo", null);
                }
                ss.agregarParametro("firmaDirec", path.concat("css/firmas/" + firmaDirector.getRutaImagen() + ".jpg"));
                if (user.getRutaImagen() != null) {
                    ss.agregarParametro("firmaTecni", path.concat("css/firmas/" + user.getRutaImagen() + ".jpg"));
                } else {
                    ss.agregarParametro("firmaTecni", null);
                }
                ss.agregarParametro("logotipo", path.concat("css/homeIconsImages/logo.jpg"));
                ss.agregarParametro("id_solicitud", solicitud.getId());
                ss.agregarParametro("nomresponsable", Utils.isEmpty(solicitud.getIdResponsable().getTituloProf()) + " " + solicitud.getIdResponsable().getApellidos() + " " + solicitud.getIdResponsable().getNombres());
                ss.agregarParametro("regprofesional", solicitud.getIdResponsable().getRegProf());
                ss.agregarParametro("ciprofesional", solicitud.getIdResponsable().getCiRuc());
                ss.agregarParametro("SUBREPORT_DIR", path + "reportes/normasConstruccion/");
                ss.agregarParametro("NUMTRAMITE", solicitud.getTramite() + "-" + solicitud.getAnioTramite());
                ss.agregarParametro("NOMBRE_TECNICO", nombreTecnico);
                ss.setNombreReporte(nombrereporte);
                ss.setNombreSubCarpeta("normasConstruccion");
                ss.setTieneDatasource(true);

                HistoricoReporteTramite vd = new HistoricoReporteTramite();
                vd.setCodValidacion(solicitud.getNumReporte() + ht.getIdProceso());
                vd.setEstado(true);
                vd.setFecCre(new Date());
                vd.setNombreReporte("NormasConstruccion" + ht.getSolicitante().getCiRuc());
                vd.setNombreTarea(this.getTaskDataByTaskID().getTaskDefinitionKey());
                vd.setProceso(ht.getIdProceso());
                vd.setSecuencia(solicitud.getNumReporte());
                vd.setTramite(ht);
                vd = divisonServices.guardarHistoricoReporteTramite(vd);

                reporte = new PdfReporte();
                String codigoQR = SisVars.urlServidorCompleta + "/DescargarDocsRepositorio?id=" + vd.getCodValidacion();
                ss.agregarParametro("validador", vd.getId().toString());
                ss.agregarParametro("codigoQR", codigoQR);

                ss.setReportePDF(reporte.generarPdf("reportes/normasConstruccion/" + nombrereporte + ".jasper", ss.getParametros()));

                if (session.getTaskID() != null) {
                    HashMap<String, Object> paramt = new HashMap<>();
                    ms = new MsgFormatoNotificacion();
                    ms = normasServices.getMsgFormatoNotificacionByTipo(2L);
                    paramt.put("idReporte", vd.getId());
                    paramt.put("prioridad", 50);
                    paramt.put("archivo", ss.getReportePDF());
                    paramt.put("carpeta", ht.getCarpetaRep());
                    paramt.put("nombreArchivoByteArray", new Date().getTime() + ss.getNombreReporte() + ht.getSolicitante().getCiRuc() + ".pdf");
                    paramt.put("tipoArchivoByteArray", "application/pdf");
                    paramt.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                    paramt.put("to", ht.getCorreos());
                    paramt.put("from", SisVars.correo);
                    paramt.put("subject", ht.getTipoTramiteNombre()+" - "+ht.getId());
                    String mensaje = ms.getHeader() + "Se ha Generado las NORMAS DE CONSTRUCCIÃ“N del tramite numero: " 
                            + ht.getId() + "<br/><br/><br/> Haga clic en el anlace para descargar la norma: "
                            + "<a  href='"+SisVars.urlPublica + "/DescargarDocumento?id="+vd.getId().toString()+"'>Descargar</a>"
                            + "<br/><br/>" 
                            + ms.getFooter();
                    paramt.put("message", mensaje);
                    paramt.put("enviado", (ss.getReportePDF() != null));
                    paramt.put("reenviar", (ss.getReportePDF() == null));
                    List<ParametrosDisparador> list = permisoService.getParametroDisparadorByTipoTramite(tramite.getId());
                    for (ParametrosDisparador p : list) {
                        if ("digitalizador".equalsIgnoreCase(p.getVarResp())) {

                            paramt.put("digitalizador", p.getResponsable().getUsuario());
                        }
                    }

                    this.completeTask(this.getTaskId(), paramt);
                }
                JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
                ht.setEstado("Finalizado");
                permisoService.actualizarHistoricoTramites(ht);
                impreso = true;

            } catch (Exception e) {
                Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        JsfUti.executeJS("PF('obs').hide()");
    }

    public void subirNormaPdf() {
        if (obs.getObservacion() == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        if (this.getFiles().isEmpty()) {
            JsfUti.messageInfo(null, Messages.faltaSubirDocumento, "");
            return;
        }
        obs.setEstado(Boolean.TRUE);
        obs.setFecCre(new Date());
        obs.setIdTramite(ht);
        obs.setUserCre(session.getName_user());
        obs.setTarea("Subir Pdf de Norma");

        if (permisoService.guardarObservacion(obs) != null) {
            try {
                HashMap<String, Object> paramt = new HashMap<>();
                paramt.put("listaArchivos", this.getFiles());
                paramt.put("listaArchivosFinal", new ArrayList<>());
                paramt.put("carpeta", ht.getCarpetaRep());
                paramt.put("enviado", (this.getFiles() != null));
                paramt.put("reenviar", (this.getFiles() == null));

                this.completeTask(session.getTaskID(), paramt);
                ht.setEstado("Finalizado");
                permisoService.actualizarHistoricoTramites(ht);
                this.continuar();
            } catch (Exception e) {
                Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        JsfUti.executeJS("PF('obs').hide()");
    }

    public void irBadejaTarea() {
        if (impreso) {
            this.continuar();
        } else {
            JsfUti.messageInfo(null, "Debe Imprimir la Norma para completar la tarea...", "");
        }
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public Boolean getSubirPdf() {
        return subirPdf;
    }

    public void setSubirPdf(Boolean subirPdf) {
        this.subirPdf = subirPdf;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatSolicitudNormaConstruccion getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(CatSolicitudNormaConstruccion solicitud) {
        this.solicitud = solicitud;
    }

    public List<CatPredioPropietario> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<CatPredioPropietario> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    public List<CatCiudadela> getCiudadelaList() {
        return ciudadelaList;
    }

    public void setCiudadelaList(List<CatCiudadela> ciudadelaList) {
        this.ciudadelaList = ciudadelaList;
    }

    public Boolean getNuevaNorma() {
        return nuevaNorma;
    }

    public void setNuevaNorma(Boolean nuevaNorma) {
        this.nuevaNorma = nuevaNorma;
    }

    public Long getIdNorma() {
        return idNorma;
    }

    public void setIdNorma(Long idNorma) {
        this.idNorma = idNorma;
    }

    public CatEnte getResponsable() {
        return responsable;
    }

    public void setResponsable(CatEnte responsable) {
        this.responsable = responsable;
    }

    public CatNormasConstruccionTipo getNuevoTipoNorma() {
        return nuevoTipoNorma;
    }

    public void setNuevoTipoNorma(CatNormasConstruccionTipo nuevoTipoNorma) {
        this.nuevoTipoNorma = nuevoTipoNorma;
    }

    public CatNormasConstruccion getNormaConstruccion() {
        return normaConstruccion;
    }

    public void setNormaConstruccion(CatNormasConstruccion normaConstruccion) {
        this.normaConstruccion = normaConstruccion;
    }

    public SolicitudNormasConstruccion() {
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnteLazy getEnteLazy() {
        return enteLazy;
    }

    public void setEnteLazy(CatEnteLazy enteLazy) {
        this.enteLazy = enteLazy;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getListaRetiros() {
        return listaRetiros;
    }

    public void setListaRetiros(List<CatNormasConstruccionHasRetirosAumento> listaRetiros) {
        this.listaRetiros = listaRetiros;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getListaAlturas() {
        return listaAlturas;
    }

    public void setListaAlturas(List<CatNormasConstruccionHasRetirosAumento> listaAlturas) {
        this.listaAlturas = listaAlturas;
    }

    public List<CatNormasConstruccionHasRetirosAumento> getListaDisposicionGeneral() {
        return listaDisposicionGeneral;
    }

    public void setListaDisposicionGeneral(List<CatNormasConstruccionHasRetirosAumento> listaDisposicionGeneral) {
        this.listaDisposicionGeneral = listaDisposicionGeneral;
    }

    public Boolean getOcultarGuardar() {
        return ocultarGuardar;
    }

    public void setOcultarGuardar(Boolean ocultarGuardar) {
        this.ocultarGuardar = ocultarGuardar;
    }

    public Long getIdNuevaNorma() {
        return idNuevaNorma;
    }

    public void setIdNuevaNorma(Long idNuevaNorma) {
        this.idNuevaNorma = idNuevaNorma;
    }

    public Boolean getIsEspecial() {
        return isEspecial;
    }

    public void setIsEspecial(Boolean isEspecial) {
        this.isEspecial = isEspecial;
    }

    public Boolean getHayImagen() {
        return hayImagen;
    }

    public void setHayImagen(Boolean hayImagen) {
        this.hayImagen = hayImagen;
    }

}
