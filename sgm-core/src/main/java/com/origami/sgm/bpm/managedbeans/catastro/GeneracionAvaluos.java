/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.AvalBandaImpositiva;
import com.origami.sgm.entities.AvalValorSuelo;
import com.origami.sgm.entities.AvalCoeficientesSuelo;
import com.origami.sgm.entities.AvalDepreciacionSolar;
import com.origami.sgm.entities.AvalEdadZonaConst;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioAvalHistorico;
import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.models.PrediosManzanaDTO;
import com.origami.sgm.lazymodels.CatPredioAvalHistoricoLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.reportes.ReportesView;
import com.origami.sgm.reportes.PdfReporte;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.Faces;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author XndySxnchez
 */
@Named(value = "generacionAvaluo")
@ViewScoped
public class GeneracionAvaluos implements Serializable {

    @Inject
    private UserSession sess;
    @Inject
    private CatastroServices catast;
    @Inject
    private Entitymanager manager;
    @Inject
    protected ServletSession ss;
    @Inject
    protected ReportesView reportes;
    protected PdfReporte reporte;
    @Inject
    private AvaluosServices avaluosServices;
    private String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
    private List<PrediosManzanaDTO> prediosXManzana, zonasPredios;
    private List<AvalEdadZonaConst> edadesXZona;
    private PrediosManzanaDTO predioSeleccionado;
    private Integer anioInicioValorSuelo, anioFinValorSuelo, anioCoeficientesSolarInicio,
            anioCoeficientesSolarFin, anioCoeficientesConstruccionInicio,
            anioCoeficientesConstruccionFin, anioDepreciacionInicio, anioDepreciacionFin, 
            anioValorizarFin, anioValorizarInicio, anioInicioReporte, anioFinReporte;
    private BigDecimal valorM2;
    private List<AvalCoeficientesSuelo> coeficientesFactoriales;
    private List<CatEdfCategProp> catEdfCategProp;
    private AvalCoeficientesSuelo coef, coefSearch, coefPadre;
    private CtlgItem itemSolar;
    private CatEdfCategProp cecp;
    private CatPredioLazy predios;
    private List<CatPredio> prediosSeleccionados;
    private List<AvalValorSuelo> valorM2ParroquiaList;

    private List<AvalDepreciacionSolar> depreciacionList;
    private AvalValorSuelo categoriaValorSueloSeleccionado;

    private TreeNode coeficientesTree;
    private TreeNode nodeSolar, nodeSolarItem, nodeConstruc, nodeConstrucItem;

    private CatPredioAvalHistoricoLazy avalHistoricoLazy;
    private List<CatPredioAvalHistorico> avalHistoricoList;
    private List<CatPredioAvalHistorico> avalHistoricoNotInList;
    
    private String tipoDefinicion;

    @PostConstruct
    protected void load() {
        try {
            if (sess != null) {
                tipoDefinicion = "M";
                reporte = new PdfReporte();
                ss.instanciarParametros();
                avalHistoricoNotInList = new ArrayList<>();
                avalHistoricoList = new ArrayList<>();
                avalHistoricoLazy = new CatPredioAvalHistoricoLazy();
                anioDepreciacionInicio = Utils.getAnio(new Date());
                anioDepreciacionFin = Utils.getAnio(new Date());
                valorM2ParroquiaList = new ArrayList<>();
                categoriaValorSueloSeleccionado = new AvalValorSuelo();
                cecp = new CatEdfCategProp();
                catEdfCategProp = new ArrayList<>();
                valorM2 = new BigDecimal("0.00");
                prediosSeleccionados = new ArrayList<>();
                prediosXManzana = new ArrayList<>();
                prediosXManzana = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getPrediosXManzana, new String[]{"estado"}, new Object[]{"A"});
                zonasPredios = manager.getSqlQueryParametros(PrediosManzanaDTO.class, Querys.getZonaPredios, new String[]{"estado"}, new Object[]{"A"});
                if (dataBaseConnect()) {
                    catEdfCategProp = manager.findAll(CatEdfCategProp.class);
                } else {
                    catEdfCategProp = catast.getCategoriasConstByEstado();
                }

                valorM2ParroquiaList = manager.findAll(AvalValorSuelo.class);
                depreciacionList = new ArrayList<>();
                depreciacionList = manager.findAll(Querys.getDepreciacion, new String[]{"anioInicio", "anioFin"}, new Object[]{anioDepreciacionInicio, anioDepreciacionFin});
                predios = new CatPredioLazy("A");
                updatePrediosDTO();
                updateEdadesXZona();
                createCoeficientesTreeNode();
                reloadVar();
                coefPadre = new AvalCoeficientesSuelo();
                itemSolar = new CtlgItem();
                coeficientesFactoriales = new ArrayList<>();
            }
        } catch (Exception e) {
            Logger.getLogger(GestionPredios.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    //metodo en el que se cargan los datos del DSTO para que se agreguen al entity 
    public void updateEdadesXZona() {
        if (!zonasPredios.isEmpty()) {
            edadesXZona = new ArrayList<>();
            AvalEdadZonaConst edadZonaConst = new AvalEdadZonaConst();
            for (PrediosManzanaDTO pmdto : zonasPredios) {
                if (pmdto != null) {
                    if (pmdto.getZona() != null && pmdto.getParroquia() != null) {
                        try {
                            edadZonaConst = (AvalEdadZonaConst) manager.find(Querys.getEdadesXZona, new String[]{"zon", "parr"}, new Object[]{pmdto.getZona(), pmdto.getParroquia()});
                            if (edadZonaConst != null) {
                                edadesXZona.add(edadZonaConst);
                            } else {
                                edadZonaConst = new AvalEdadZonaConst();
                                edadZonaConst.setZona(pmdto.getZona());
                                edadZonaConst.setParroquia(pmdto.getParroquia());
                                edadesXZona.add(edadZonaConst);
                            }
                        } catch (Exception e) {
                        }

                    }
                }
            }
        }
    }

    public void createCoeficientesTreeNode() {
        coeficientesTree = new DefaultTreeNode("Root", null);
        nodeSolar = new DefaultTreeNode("Solar", coeficientesTree);
        nodeConstruc = new DefaultTreeNode("Construcción", coeficientesTree);
        List<CtlgItem> addDataToNodeSolar = getListadoAvaluo("predio.avaluo");
        for(CtlgItem i : addDataToNodeSolar){
            nodeSolarItem = new DefaultTreeNode(i.getValor(), nodeSolar);
            addDataToNodeSolar(i);
        }
        for(CatEdfCategProp i : catEdfCategProp){
            nodeConstrucItem = new DefaultTreeNode(i.getNombre(), nodeConstruc);
            addDataNodeConstruccion(i);
        }
    }

    public void saveAvaluoEdadesXZonas() {
        Integer count = edadesXZona.size();
        Integer countSave = 0;
        Boolean x = Boolean.FALSE;
        for (AvalEdadZonaConst aezc : edadesXZona) {
            x = avaluosServices.saveAvalEdadZonaConst(aezc);
            if (x = Boolean.TRUE) {
                countSave++;
            }
        }
        if (count.equals(countSave)) {
            Faces.messageInfo(null, "Éxito !", "Datos Registrados Correctamente");
        } else {
            Faces.messageFatal(null, "Error !", "Ocurrio un error mientras se persistian los datos");
        }

        reloadVar();

    }

    public void updatePrediosDTO() {
        int count = 0;
        for (PrediosManzanaDTO prediosManzanaDTO : this.prediosXManzana) {
            prediosManzanaDTO.setId(count);
            this.prediosXManzana.set(count, prediosManzanaDTO);
            count++;
        }
    }

    public void saveValorMetroCuadrado() throws IllegalAccessException, InvocationTargetException {
        if (anioInicioValorSuelo == null || anioInicioValorSuelo == 0
                || anioInicioValorSuelo > Utils.getAnio(new Date()) || anioInicioValorSuelo < 2000
                || anioFinValorSuelo == null || anioFinValorSuelo == 0
                || anioFinValorSuelo < Utils.getAnio(new Date()) || anioFinValorSuelo < 2000) {
            Faces.messageError(null, "Advertencia", "Verifique los Datos Registrados");
            reloadVar();
            return;
        }
        if (valorM2 == null) {
            Faces.messageError(null, "Advertencia", "Verifique los Datos Registrados");
            reloadVar();
            return;
        }

        if (this.categoriaValorSueloSeleccionado == null) {
            Faces.messageError(null, "Advertencia", "Verifique los Datos Registrados");
            reloadVar();
            return;
        }

        if (this.categoriaValorSueloSeleccionado.getValorM2().compareTo(BigDecimal.ZERO) == 0) {
            Faces.messageError(null, "Advertencia", "Verifique los Datos Registrados");
            reloadVar();
            return;
        }

        if (valorM2.compareTo(BigDecimal.ZERO) > 1) {
            categoriaValorSueloSeleccionado.setValorM2(valorM2);
            reloadVar();
        }

        if (this.categoriaValorSueloSeleccionado.getMz() > 0 && this.categoriaValorSueloSeleccionado.getParroquia() > 0
                && this.categoriaValorSueloSeleccionado.getSector() >= 0
                && this.categoriaValorSueloSeleccionado.getZona() >= 0) {

            this.categoriaValorSueloSeleccionado.setAnioInicio(anioInicioValorSuelo);
            this.categoriaValorSueloSeleccionado.setAnioFin(anioFinValorSuelo);

            AvalValorSuelo acvsResult = (AvalValorSuelo) avaluosServices.saveAvaluoCategoriaValorM2(this.categoriaValorSueloSeleccionado);
            if (acvsResult != null) {
                Faces.messageInfo(null, "Éxito !", "Datos Registrados Correctamente");
                this.valorM2ParroquiaList.add(acvsResult);
                reloadVar();
                valorM2 = new BigDecimal("0.00");
                return;
            } else {
                Faces.messageFatal(null, "Error !", "Ha Ocurrido un error de registro");
                reloadVar();
                valorM2 = new BigDecimal("0.00");
                return;
            }
        } else {
            Faces.messageFatal(null, "Error !", "Verifique los Datos Registrados");
            valorM2 = new BigDecimal("0.00");
            reloadVar();
        }
        reloadVar();
    }

    public void reloadVar() {
        predioSeleccionado = new PrediosManzanaDTO();
        ///categoriaValorSueloSeleccionado = new AvalValorSuelo();
    }

    public void deleteAllAvaluos(Boolean control) {
        if (control == Boolean.TRUE) {
            for (CatPredio cp : prediosSeleccionados) {
                manager.ejecutarFuncionCleanAvaluos(sess.getName_user(), cp.getId());
            }
        } else {
            manager.ejecutarFuncionCleanAvaluos(sess.getName_user(), 0L);
        }
        Faces.messageInfo(null, "Datos de Avalúos !", "Eliminados Correctamente");
    }

    public List<CtlgItem> getListadoAvaluo(String argumento) {
        List<CtlgItem> ctlgItem = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaAvaluosASC, new String[]{"catalogo"}, new Object[]{argumento});
        return ctlgItem;
    }

    public void startValorizacion(Boolean control) {
        if (prediosSeleccionados.isEmpty() && control == Boolean.FALSE) {
            Faces.messageFatal(null, "Advertencia!", "Debe Seleccionar los predios a Valorizar");
            return;
        }
        CatPredioAvalHistorico predioAvalHistorico = null;
        Object o;
        o = avaluosServices.generateAvaluo(prediosSeleccionados, anioValorizarInicio, anioValorizarFin, control, sess.getName_user());
        if (control == Boolean.FALSE) {
            for (CatPredio cp : prediosSeleccionados) {
                predioAvalHistorico = (CatPredioAvalHistorico) manager.find(Querys.getAvaluosHistoricoActualizados, new String[]{"predioId", "anioInicio", "anioFin"}, new Object[]{cp.getId(), anioValorizarInicio, anioValorizarFin});
                if (predioAvalHistorico != null) {
                    this.avalHistoricoList.add(predioAvalHistorico);
                } else {
                    predioAvalHistorico = new CatPredioAvalHistorico();
                    predioAvalHistorico.setPredio(cp);
                    this.avalHistoricoNotInList.add(predioAvalHistorico);
                }
            }
        } else {
            if (dataBaseConnect()) {
                HiberUtil.newTransaction();
            }
            this.avalHistoricoList = manager.findAll(Querys.getAllAvaluosHistoricoActualizados, new String[]{"anioInicio", "anioFin"}, new Object[]{anioValorizarInicio, anioValorizarFin});
            prediosSeleccionados = new ArrayList<>();
            if (dataBaseConnect()) {
                HiberUtil.newTransaction();
            }
            prediosSeleccionados = (List<CatPredio>) manager.getNativeQuery(CatPredio.class, Querys.getAllPrediosNotInListAvaluosHistorico, new String[]{""}, new Object[]{});
            if (prediosSeleccionados != null && !prediosSeleccionados.isEmpty()) {
                for (CatPredio cp : prediosSeleccionados) {
                    predioAvalHistorico = new CatPredioAvalHistorico();
                    if (cp != null) {
                        predioAvalHistorico.setPredio(cp);
                        this.avalHistoricoNotInList.add(predioAvalHistorico);
                    }

                }
            }

        }

        JsfUti.update("frmAaluosDialog");
        JsfUti.executeJS("PF('dlgResultAvaluos').show()");

        prediosSeleccionados = new ArrayList<>();
        reloadVar();
    }

    public void cleanVar() {
        avalHistoricoList = new ArrayList<>();
        avalHistoricoNotInList = new ArrayList<>();
        JsfUti.executeJS("PF('dlgResultAvaluos').hide()");
    }

    public void addDataToAvalCoeficientesSueloolar() {
        coeficientesFactoriales = new ArrayList<>();
        coefPadre = new AvalCoeficientesSuelo();
        if (itemSolar != null) {
            if (itemSolar.getPadre() != null) {
                CtlgCatalogo catalogo = (CtlgCatalogo) manager.find(CtlgCatalogo.class, this.itemSolar.getPadre().longValue());
                List<CtlgItem> ctlgItemList = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{catalogo.getNombre()});
                ctlgItemList.forEach((i) -> {
                    addDataToListCategoriaSolar(i);
                });
            } else {
                List<CtlgItem> control = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemabyPadreASC, new String[]{"padre", "referencia"},
                        new Object[]{this.itemSolar.getId(), this.itemSolar.getId()});
                control.forEach((i) -> {
                    addDataToListCategoriaSolar(i);
                });
            }
            coefPadre.setCategoriaSolar(itemSolar);
            coefPadre.setAnioFin(anioCoeficientesSolarFin);
            coefPadre.setAnioInicio(anioCoeficientesSolarInicio);
        } else {
            Faces.messageFatal(null, "Advertencia!", "Debe de Seleccionaar al menos una opción");
        }
        reloadVar();
    }

    public void addDataToListCategoriaSolar(CtlgItem i) {
        coef = new AvalCoeficientesSuelo();
        coefSearch = new AvalCoeficientesSuelo();
        coefSearch = (AvalCoeficientesSuelo) manager.find(Querys.getAvaluosCoeficientesSolar, new String[]{"anioInicio", "anioFin", "categoriaSolar"}, new Object[]{anioCoeficientesSolarInicio, anioCoeficientesSolarFin, i.getId()});
        if (coefSearch != null) {
            coefPadre.setValorCoeficiente(coefSearch.getValorCoeficiente());
            coeficientesFactoriales.add(coefSearch);
        } else {
            coef.setCategoriaSolar(i);
            coef.setValorCoeficiente(new BigDecimal("0.00"));
            coef.setValorCoefInferior(new BigDecimal("0.00"));
            coef.setValorCoefSuperior(new BigDecimal("0.00"));
            coeficientesFactoriales.add(coef);
        }
    }

    public void addDataToNodeSolar(CtlgItem itSolar) {
        HiberUtil.newTransaction();
        coefPadre = new AvalCoeficientesSuelo();
        if (itSolar != null) {
            if (itSolar.getPadre() != null) {
                CtlgCatalogo catalogo = (CtlgCatalogo) manager.find(CtlgCatalogo.class, itSolar.getPadre().longValue());
                List<CtlgItem> ctlgItemList = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{catalogo.getNombre()});
                ctlgItemList.forEach((i) -> {
                    addDataToListNodeSolar(i);
                });
            } else {
                List<CtlgItem> control = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemabyPadreASC, new String[]{"padre", "referencia"},
                        new Object[]{itSolar.getId(), itSolar.getId()});
                control.forEach((i) -> {
                    addDataToListNodeSolar(i);
                });
            }
        } else {
            Faces.messageFatal(null, "Advertencia!", "Debe de Seleccionaar al menos una opción");
        }
        reloadVar();
    }

    public void addDataToListNodeSolar(CtlgItem i) {
        coef = new AvalCoeficientesSuelo();
        coefSearch = new AvalCoeficientesSuelo();
        coefSearch = (AvalCoeficientesSuelo) manager.find(Querys.getAvaluosCoeficientesSolar, new String[]{"anioInicio", "anioFin", "categoriaSolar"}, new Object[]{Utils.getAnio(new Date()), Utils.getAnio(new Date()), i.getId()});
        if (coefSearch != null) {
            coefPadre.setValorCoeficiente(coefSearch.getValorCoeficiente());
            nodeSolarItem.getChildren().add(new DefaultTreeNode(coefSearch.getCategoriaSolar().getValor() + " - " + coefSearch.getValorCoeficiente().toString() + " - " + coefSearch.getValorCoefInferior().toString() + " - " + coefSearch.getValorCoefSuperior().toString()));
        } else {
            coef.setCategoriaSolar(i);
            coef.setValorCoeficiente(new BigDecimal("0.00"));
            coef.setValorCoefInferior(new BigDecimal("0.00"));
            coef.setValorCoefSuperior(new BigDecimal("0.00"));
            nodeSolarItem.getChildren().add(new DefaultTreeNode(coef.getCategoriaSolar().getValor() + " - " + coef.getValorCoeficiente().toString() + " - " + coef.getValorCoefInferior().toString() + " - " + coef.getValorCoefSuperior().toString()));
        }
    }

    public void addDataToAvalCoeficienteConstruccion() {
        coeficientesFactoriales = new ArrayList<>();
        CatEdfCategProp cprop = (CatEdfCategProp) manager.find(CatEdfCategProp.class, this.cecp.getId());
        List<CatEdfProp> propList = manager.findAll(Querys.getCatEdfPropList, new String[]{"idCateg"}, new Object[]{cprop.getId()});
        propList.forEach((i) -> {
            addDataToListCategoriaConstruccion(i);
        });
        reloadVar();
    }

    public void addDataToListCategoriaConstruccion(CatEdfProp i) {
        coef = new AvalCoeficientesSuelo();
        coefSearch = new AvalCoeficientesSuelo();
        coefSearch = (AvalCoeficientesSuelo) manager.find(Querys.getAvaluosCoeficientesConstruccion, new String[]{"anioInicio", "anioFin", "categoriaConstruccion"}, new Object[]{anioCoeficientesConstruccionInicio, anioCoeficientesConstruccionFin, i.getId()});
        if (coefSearch != null) {
            coeficientesFactoriales.add(coefSearch);
        } else {
            coef.setCategoriaConstruccion(i);
            coef.setValorCoeficiente(new BigDecimal("0.00"));
            coef.setValorCoefInferior(new BigDecimal("0.00"));
            coef.setValorCoefSuperior(new BigDecimal("0.00"));
            coeficientesFactoriales.add(coef);
        }
    }

    public void addDataNodeConstruccion(CatEdfCategProp cecpNode) {
        HiberUtil.newTransaction();
        CatEdfCategProp cprop = new CatEdfCategProp();
        List<CatEdfProp> propList = new ArrayList();

        if (dataBaseConnect()) {
            cprop = (CatEdfCategProp) manager.find(CatEdfCategProp.class, cecpNode.getId());
            propList = manager.findAll(Querys.getCatEdfPropList, new String[]{"idCateg"}, new Object[]{cprop.getId()});
        } else {
            cprop = (CatEdfCategProp) manager.find(Querys.getCatEdfCategPropByEstadoId, new String[]{"id"}, new Object[]{cecpNode.getId()});
            propList = manager.findAll(Querys.getCatEdfPropListOracle, new String[]{"idCateg"}, new Object[]{cprop.getId()});
        }
        propList.forEach((i) -> {
            addDataToListNodeConstruccion(i);
        });
        reloadVar();
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

    public void addDataToListNodeConstruccion(CatEdfProp i) {
        coef = new AvalCoeficientesSuelo();
        coefSearch = new AvalCoeficientesSuelo();
        coefSearch = (AvalCoeficientesSuelo) manager.find(Querys.getAvaluosCoeficientesConstruccion, new String[]{"anioInicio", "anioFin", "categoriaConstruccion"}, new Object[]{Utils.getAnio(new Date()), Utils.getAnio(new Date()), i.getId()});
        if (coefSearch != null) {
            nodeConstrucItem.getChildren().add(new DefaultTreeNode(coefSearch.getCategoriaConstruccion().getNombre() + " - " + coefSearch.getValorCoeficiente().toString() + " - " + coefSearch.getValorCoefInferior().toString() + " - " + coefSearch.getValorCoefSuperior().toString()));
        } else {
            coef.setCategoriaConstruccion(i);
            coef.setValorCoeficiente(new BigDecimal("0.00"));
            coef.setValorCoefInferior(new BigDecimal("0.00"));
            coef.setValorCoefSuperior(new BigDecimal("0.00"));
            nodeConstrucItem.getChildren().add(new DefaultTreeNode(coef.getCategoriaConstruccion().getNombre() + " - " + coef.getValorCoeficiente().toString() + " - " + coef.getValorCoefInferior().toString() + " - " + coef.getValorCoefSuperior().toString()));

        }
    }

    public void saveAvalCoeficientesSuelo(Boolean control) {

        Boolean result = avaluosServices.saveAvalCoeficientesSuelo(coefPadre, coeficientesFactoriales, control, anioCoeficientesSolarInicio, anioCoeficientesSolarFin, anioCoeficientesConstruccionInicio, anioCoeficientesConstruccionFin);
        if (result == Boolean.TRUE) {
            coeficientesFactoriales = new ArrayList<>();
            Faces.messageInfo(null, "Éxito !", "Datos Registrados Correctamente");
        } else {
            Faces.messageError(null, "Error !", "Ocurrior un error mientras se registraba la Informacion");
        }

        reloadVar();

    }

    public void imprimirValoresCoeficientes() {
        try {
            ss.borrarDatos();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.agregarParametro("ANIO_INICIO", anioInicioReporte);
            ss.agregarParametro("ANIO_FIN", anioFinReporte);
            ss.agregarParametro("LOGO", path + SisVars.logoReportes);
            ss.agregarParametro("LOGO2", path + SisVars.sisLogo1);
            ss.agregarParametro("USUARIO", sess.getName_user());
            ss.setNombreSubCarpeta("catastro/avaluos");
            if (dataBaseConnect()) {
                    ss.setNombreReporte("coeficientesAvaluos");
                } else {
                    ss.setNombreReporte("coeficientesAvaluos");
                }
            
            
            ss.agregarParametro("SUBREPORT_DIR_TITLE", path + "reportes//");
            ss.agregarParametro("SUBREPORT_DIR", path + "reportes//");
            JsfUti.redirectNewTab("/sgmEE/Documento");

        } catch (Exception e) {
            Logger.getLogger(GeneracionAvaluos.class.getName()).log(Level.SEVERE, null, e);
        } //To change body of generated methods, choose Tools | Templates.

    }

    public List<CatEdfCategProp> getCatEdfCategProp() {
        return catEdfCategProp;
    }

    public void setCatEdfCategProp(List<CatEdfCategProp> catEdfCategProp) {
        this.catEdfCategProp = catEdfCategProp;
    }

    public CatEdfCategProp getCecp() {
        return cecp;
    }

    public void setCecp(CatEdfCategProp cecp) {
        this.cecp = cecp;
    }

    public void setPrediosXManzana(List<PrediosManzanaDTO> prediosXManzana) {
        this.prediosXManzana = prediosXManzana;
    }

    public PrediosManzanaDTO getPredioSeleccionado() {
        return predioSeleccionado;
    }

    public void setPredioSeleccionado(PrediosManzanaDTO predioSeleccionado) {
        setAvalValorM2Suelo(predioSeleccionado);
        this.predioSeleccionado = predioSeleccionado;
    }

    public void setAvalValorM2Suelo(PrediosManzanaDTO predioSeleccionado) {
        if (predioSeleccionado != null) {
            categoriaValorSueloSeleccionado = new AvalValorSuelo();
            if (predioSeleccionado.getParroquia() != null) {
                categoriaValorSueloSeleccionado.setParroquia(predioSeleccionado.getParroquia());
            }
            if (predioSeleccionado.getMz() != null) {
                categoriaValorSueloSeleccionado.setMz(predioSeleccionado.getMz());
            }
            if (predioSeleccionado.getParroquia() != null) {
                categoriaValorSueloSeleccionado.setParroquia(predioSeleccionado.getParroquia());
            }
            if (predioSeleccionado.getSector() != null) {
                categoriaValorSueloSeleccionado.setSector(predioSeleccionado.getSector());
            }
            if (predioSeleccionado.getZona() != null) {
                categoriaValorSueloSeleccionado.setZona(predioSeleccionado.getZona());
            }
            if (predioSeleccionado.getSolar() != null) {
                categoriaValorSueloSeleccionado.setSolar(predioSeleccionado.getSolar());
            }
            categoriaValorSueloSeleccionado.setValorM2(valorM2);
        }

    }

    public AvalValorSuelo getCategoriaValorSueloSeleccionado() {
        if (categoriaValorSueloSeleccionado != null) {
            if (categoriaValorSueloSeleccionado.getValorM2() != null) {
                if (categoriaValorSueloSeleccionado.getValorM2().compareTo(BigDecimal.ZERO) > 1) {
                    categoriaValorSueloSeleccionado.setValorM2(valorM2);
                }
            }

        }

        return categoriaValorSueloSeleccionado;
    }

    public void setCategoriaValorSueloSeleccionado(AvalValorSuelo categoriaValorSueloSeleccionado) {
        this.categoriaValorSueloSeleccionado = categoriaValorSueloSeleccionado;
    }

    public Integer getAnioInicioValorSuelo() {
        anioInicioValorSuelo = Utils.getAnio(new Date());
        return anioInicioValorSuelo;
    }

    public void setAnioInicioValorSuelo(Integer anioInicioValorSuelo) {
        this.anioInicioValorSuelo = anioInicioValorSuelo;
    }

    public Integer getAnioFinValorSuelo() {
        anioFinValorSuelo = Utils.getAnio(new Date());
        return anioFinValorSuelo;
    }

    public void setAnioFinValorSuelo(Integer anioFinValorSuelo) {
        this.anioFinValorSuelo = anioFinValorSuelo;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public List<AvalCoeficientesSuelo> getCoeficientesFactoriales() {
        return coeficientesFactoriales;
    }

    public void setCoeficientesFactoriales(List<AvalCoeficientesSuelo> coeficientesFactoriales) {
        this.coeficientesFactoriales = coeficientesFactoriales;
    }

    public AvalCoeficientesSuelo getCoef() {
        return coef;
    }

    public void setCoef(AvalCoeficientesSuelo coef) {
        this.coef = coef;
    }

    public CtlgItem getItemSolar() {
        return itemSolar;
    }

    public void setItemSolar(CtlgItem itemSolar) {
        this.itemSolar = itemSolar;
    }

    public void onRowSelect(SelectEvent event) {
    }

    public void onRowUnselect(UnselectEvent event) {
    }

    public Integer getAnioCoeficientesSolarInicio() {
        anioCoeficientesSolarInicio = Utils.getAnio(new Date());
        return anioCoeficientesSolarInicio;
    }

    public void setAnioCoeficientesSolarInicio(Integer anioCoeficientesSolarInicio) {
        this.anioCoeficientesSolarInicio = anioCoeficientesSolarInicio;
    }

    public Integer getAnioCoeficientesSolarFin() {
        anioCoeficientesSolarFin = Utils.getAnio(new Date());
        return anioCoeficientesSolarFin;
    }

    public void setAnioCoeficientesSolarFin(Integer anioCoeficientesSolarFin) {
        this.anioCoeficientesSolarFin = anioCoeficientesSolarFin;
    }

    public Integer getAnioCoeficientesConstruccionInicio() {
        anioCoeficientesConstruccionInicio = Utils.getAnio(new Date());
        return anioCoeficientesConstruccionInicio;
    }

    public void setAnioCoeficientesConstruccionInicio(Integer anioCoeficientesConstruccionInicio) {
        this.anioCoeficientesConstruccionInicio = anioCoeficientesConstruccionInicio;
    }

    public Integer getAnioCoeficientesConstruccionFin() {
        anioCoeficientesConstruccionFin = Utils.getAnio(new Date());
        return anioCoeficientesConstruccionFin;
    }

    public void setAnioCoeficientesConstruccionFin(Integer anioCoeficientesConstruccionFin) {
        this.anioCoeficientesConstruccionFin = anioCoeficientesConstruccionFin;
    }

    public Integer getAnioValorizarFin() {
        anioValorizarFin = Utils.getAnio(new Date());
        return anioValorizarFin;
    }

    public void setAnioValorizarFin(Integer anioValorizarFin) {
        this.anioValorizarFin = anioValorizarFin;
    }

    public Integer getAnioValorizarInicio() {
        anioValorizarInicio = Utils.getAnio(new Date());
        return anioValorizarInicio;
    }

    public void setAnioValorizarInicio(Integer anioValorizarInicio) {
        this.anioValorizarInicio = anioValorizarInicio;
    }

    public AvalCoeficientesSuelo getCoefPadre() {
        return coefPadre;
    }

    public void setCoefPadre(AvalCoeficientesSuelo coefPadre) {
        this.coefPadre = coefPadre;
    }

    public List<AvalEdadZonaConst> getEdadesXZona() {
        return edadesXZona;
    }

    public void setEdadesXZona(List<AvalEdadZonaConst> edadesXZona) {
        this.edadesXZona = edadesXZona;
    }

    public Integer getAnioDepreciacionInicio() {
        return anioDepreciacionInicio;
    }

    public void setAnioDepreciacionInicio(Integer anioDepreciacionInicio) {
        this.anioDepreciacionInicio = anioDepreciacionInicio;
    }

    public Integer getAnioDepreciacionFin() {
        return anioDepreciacionFin;
    }

    public void setAnioDepreciacionFin(Integer anioDepreciacionFin) {
        this.anioDepreciacionFin = anioDepreciacionFin;
    }

    public List<PrediosManzanaDTO> getZonasPredios() {
        return zonasPredios;
    }

    public void setZonasPredios(List<PrediosManzanaDTO> zonasPredios) {
        this.zonasPredios = zonasPredios;
    }

    public List<PrediosManzanaDTO> getPrediosXManzana() {
        return prediosXManzana;
    }

    public List<AvalDepreciacionSolar> getDepreciacionList() {
        return depreciacionList;
    }

    public void setDepreciacionList(List<AvalDepreciacionSolar> depreciacionList) {
        this.depreciacionList = depreciacionList;
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public List<CatPredio> getPrediosSeleccionados() {
        return prediosSeleccionados;
    }

    public void setPrediosSeleccionados(List<CatPredio> prediosSeleccionados) {
        this.prediosSeleccionados = prediosSeleccionados;
    }

    public TreeNode getCoeficientesTree() {
        return coeficientesTree;
    }

    public void setCoeficientesTree(TreeNode coeficientesTree) {
        this.coeficientesTree = coeficientesTree;
    }

    public List<AvalValorSuelo> getValorM2ParroquiaList() {
        return valorM2ParroquiaList;
    }

    public void setValorM2ParroquiaList(List<AvalValorSuelo> valorM2ParroquiaList) {
        this.valorM2ParroquiaList = valorM2ParroquiaList;
    }

    public CatPredioAvalHistoricoLazy getAvalHistoricoLazy() {
        return avalHistoricoLazy;
    }

    public void setAvalHistoricoLazy(CatPredioAvalHistoricoLazy avalHistoricoLazy) {
        this.avalHistoricoLazy = avalHistoricoLazy;
    }

    public List<CatPredioAvalHistorico> getAvalHistoricoList() {
        return avalHistoricoList;
    }

    public void setAvalHistoricoList(List<CatPredioAvalHistorico> avalHistoricoList) {
        this.avalHistoricoList = avalHistoricoList;
    }

    public List<CatPredioAvalHistorico> getAvalHistoricoNotInList() {
        return avalHistoricoNotInList;
    }

    public void setAvalHistoricoNotInList(List<CatPredioAvalHistorico> avalHistoricoNotInList) {
        this.avalHistoricoNotInList = avalHistoricoNotInList;
    }

    public Integer getAnioInicioReporte() {
        anioInicioReporte  = Utils.getAnio(new Date());
        return anioInicioReporte;
    }

    public void setAnioInicioReporte(Integer anioInicioReporte) {
        this.anioInicioReporte = anioInicioReporte;
    }

    public Integer getAnioFinReporte() {
        anioFinReporte  = Utils.getAnio(new Date());
        return anioFinReporte;
    }

    public void setAnioFinReporte(Integer anioFinReporte) {
        this.anioFinReporte = anioFinReporte;
    }

    public String getTipoDefinicion() {
        System.out.println("definicion " + tipoDefinicion);
        return tipoDefinicion;
    }

    public void setTipoDefinicion(String tipoDefinicion) {
        System.out.println("definicion2 " + tipoDefinicion);
        this.tipoDefinicion = tipoDefinicion;
    }
    
}
