/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.app.AppConfig;
import com.origami.config.MainConfig;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.catastro.FichaPredial;
import com.origami.sgm.entities.CatBloqueObraEspecial;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEdificacionPisosDet;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.services.ejbs.DisabledPersistenceBloques;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.ejbs.censocat.UploadFotoBean;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import util.EntityBeanCopy;
import util.Faces;
import util.FilesUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author elcid
 */
@Named(value = "bloques")
@ViewScoped
@DisabledPersistenceBloques
public class Bloques implements Serializable {

    protected static final Logger LOG = Logger.getLogger(Bloques.class.getName());

    @Inject
    protected CatastroServices catast;
    @Inject
    protected OmegaUploader fserv;
    @Inject
    protected UploadFotoBean fotoBean;
    @Inject
    protected AppConfig conf;
    protected MainConfig mainConfig;

    @Inject
    protected Entitymanager manager;
    protected TreeNode nodo;
    protected TreeNode selectedNodo;

    protected List<FotoPredio> fotos;
    protected FotoPredio foto;
    protected List<FotoPredio> idFotoPredio;

    protected Boolean nuevo;
    protected CatPredioEdificacion bloq;
    protected String idCatPredioBloq;
    protected String idPredio;
    protected String esNuevo;
    protected String ver;
    protected String noPersist;
    protected Boolean esVer;

    protected Integer padreItem;
    protected Integer hijoItem;

    protected String Caracteristica;

    protected List<CatPredioEdificacion> seleccionados;

    // CATEGORIAS DE CONSTRUCCION
    protected CatEdfCategProp categoriaConst;
    protected CatEdfProp propiedadCategoria;
    protected List<CatEdfCategProp> categProp;
    protected List<CatEdfProp> prop;

    // categorias de construccion es por la columna orden
    protected Integer estructura = 1;
    protected Integer pared = 6;
    protected Integer revExterior = 10;
    protected Integer revInterior = 11;
    protected Integer puertas = 15;
    protected Integer ventanas = 16;
    protected Integer vidrios = 17;
    protected Integer cubreVentanas = 18;

    // Iterm de estructura es por la columna orden
    protected Integer aporticada = 1;
    protected Integer soportante = 2;
    protected Integer mixta = 3;
    protected Integer tipoEstructura;
    protected Boolean existePared = false;

    // NIVEL PISO
    protected CtlgItem nivel;
    protected BigDecimal areaNivel;
    protected Integer maxNivel = 0;

    @javax.inject.Inject
    protected CatastroServices ejb;
    @Inject
    protected UserSession us;

    protected Long idTramite;
    protected String tramite;

    protected String nombreTabEdificacion;

    protected Integer transitorio = 0;
    @Inject
    protected BloquesDialogConf bloqueDlgCnf;
    @Inject
    protected ServletSession ss;

    /**
     * La carga Incial de esta metodo esta sobreescrito en la BloquesIbarra.
     */
    public void initView() {
        System.out.println("Trasnsitorio inicial = " + transitorio);
        try {
            if (!JsfUti.isAjaxRequest()) {
                nuevo = Boolean.valueOf(esNuevo);
                esVer = Boolean.valueOf(ver);
                mainConfig = conf.getMainConfig();
                if (noPersist != null) {
                    transitorio = Integer.valueOf(noPersist);
                    bloqueDlgCnf.setTransitorio(Boolean.TRUE);
                }
                if (tramite != null) {
                    idTramite = Long.valueOf(tramite);
                }
                if (nuevo) {
                    if (idPredio == null) {
                        return;
                    }
                    bloq = new CatPredioEdificacion();
                    bloq.setPredio((CatPredio) EntityBeanCopy.clone(catast.getPredioId(Long.valueOf(idPredio))));
                    bloq.setUsuario(us.getName_user());
                    bloq.setEstado("A");
                    bloq.setNoEdificacion(catast.obtenerNumEdificacion(bloq.getPredio()));
                    /*if (bloq.getPredio() == null) { // para que no clone las caracteristicas del bloque matriz
                        if (bloq.getPredio() != null && bloq.getPredio().getPredioRaiz() != null && bloq.getPredio().getPropiedadHorizontal()) {
                            CatPredio predioRaiz = catast.getPredioId(bloq.getPredio().getPredioRaiz().longValue());
                            if (predioRaiz != null && Utils.isNotEmpty(predioRaiz.getCatPredioEdificacionCollection())) {
                                CatPredioEdificacion ed = Utils.get(predioRaiz.getCatPredioEdificacionCollection(), 0);
                                bloq.setCatEdificacionPisosDetCollection(new ArrayList<>());
                                BigDecimal areaTemp = BigDecimal.ZERO; 
                                if (Utils.isNotEmpty(ed.getCatEdificacionPisosDetCollection())) {
                                    for (CatEdificacionPisosDet pdt : ed.getCatEdificacionPisosDetCollection()) {
                                        pdt.setId(null);
                                        pdt.setEdificacion(null);
                                        areaTemp = areaTemp.add(pdt.getArea());
                                        bloq.getCatEdificacionPisosDetCollection().add(pdt);
                                    }
                                }
                                bloq.setCatPredioEdificacionPropCollection(new ArrayList<>());
                                if (Utils.isNotEmpty(ed.getCatPredioEdificacionPropCollection())) {
                                    ed.getCatPredioEdificacionPropCollection().stream().map((edt) -> {
                                        edt.setId(null);
                                        return edt;
                                    }).map((edt) -> {
                                        edt.setEdificacion(null);
                                        return edt;
                                    }).forEachOrdered((edt) -> {
                                        bloq.getCatPredioEdificacionPropCollection().add(edt);
                                    });
                                }
                                bloq.setCondicionFisica(ed.getCondicionFisica());
                                bloq.setEstadoConservacion(ed.getEstadoConservacion());
                                bloq.setNumPisos(ed.getNumPisos());
                                bloq.setValorCultural(ed.getValorCultural());
                                bloq.setExtras(ed.getExtras());
                                bloq.setAreaBloque(areaTemp);
                            }
                        }
                    }*/
                } else {
                    if (idCatPredioBloq == null) {
                        return;
                    }
                    if (Long.valueOf(idCatPredioBloq) > 0) {
                        bloq = ejb.getPredioBloqueById(Long.valueOf(idCatPredioBloq));
                        if (bloq.getAnioCons() != null) {
                            this.devolver_edad(bloq.getAnioCons());
                        }
                        if (Utils.isNotEmpty((List<?>) bloq.getCatEdificacionPisosDetCollection())) {
                            bloq.getCatEdificacionPisosDetCollection().stream().filter((pb) -> (maxNivel < pb.getNivel().getOrden())).forEachOrdered((pb) -> {
                                maxNivel = pb.getNivel().getOrden();
                            });
                        }
                    } else {
                        bloq = (CatPredioEdificacion) Faces.getSessionBean("bloque");
                        Faces.setSessionBean("bloque", null);
                        if (bloq == null) {
                            return;
                        }
                    }
                    Collections.sort(bloq.getCatPredioEdificacionPropCollection(), (CatPredioEdificacionProp p1, CatPredioEdificacionProp p2) -> Integer.valueOf(p1.getProp().getCategoria().getGuiOrden()).compareTo(Integer.valueOf(p2.getProp().getCategoria().getGuiOrden())));
                    int i = 0;
                    bloq.getCatPredioEdificacionPropCollection().stream().map((catBloqueCaracteristica) -> {
                        if (catBloqueCaracteristica.getProp().getCategoria().getGuiOrden() == estructura) {
                            tipoEstructura = catBloqueCaracteristica.getProp().getOrden().intValue();
//                            break;
                        }
                        return catBloqueCaracteristica;
                    }).filter((catBloqueCaracteristica) -> (catBloqueCaracteristica.getProp().getCategoria().getGuiOrden() == pared)).forEachOrdered((_item) -> {
                        existePared = true;
                    });
                    cargarFotos();
                    validarCaracteristicasEdif();
                }
                llenarNodo();
            }
        } catch (NumberFormatException ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }

    public List<CatPredioEdificacionProp> getCaracteristicasEdificacion(CatPredioEdificacion edf) {
        return catast.getCaracteristicasEdificacion(edf);
    }

    public void obraEspecial(CatBloqueObraEspecial obraEspecial) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(bloq.getId().toString());
        params.put("idBloque", p);
        p = new ArrayList<>();
        if (obraEspecial != null && obraEspecial.getId() != null) {
            p.add(obraEspecial.getId().toString());
        }
        params.put("idCatBloqueObraEspecial", p);
        p = new ArrayList<>();
        if (obraEspecial == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        params.put("nuevo", p);
        Utils.openDialog("/resources/dialog/obraEspecial", params);
    }

    public Boolean validarCampos() {
        if (mainConfig.getFichaPredial().getMostrarPorcentajeEdificacion()) {
            if (!this.validarPorcentajes()) {
                return false;
            }
        }
        if (this.bloq.getNumPisos() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el Número de Pisos");
            return false;
        }
        if (this.bloq.getEstadoConservacion() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el Estado Conservación");
            return false;
        }
        if (this.bloq.getAnioCons() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el Año Construcción");
            return false;
        }
        if (!mainConfig.getFichaPredial().getRedenerFichaIb()) {
            if (this.bloq.getAreaConsCenso() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el Area de Construccion");
                return false;
            }
            if (this.bloq.getCategoria() == null) {
                JsfUti.messageError(null, "Advertencia", "Debe seleccionar la Categoria de construcción");
                return false;
            }
            return true;
        }
        if (this.bloq.getNoEdificacion() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el Número de Bloque");
            return false;
        }

        if (this.bloq.getUsoConstructivoPiso() == null) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el uso constructivo");
            return false;
        }
        if (Utils.isEmpty((List<?>) this.bloq.getCatEdificacionPisosDetCollection())) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar al menos un piso o nivel para el bloque");
            return false;
        }
        if (this.bloq.getNumPisos() != this.bloq.getCatEdificacionPisosDetCollection().size()) {
            JsfUti.messageError(null, "Advertencia", "El número de piso debe ser igual al número de niveles agregados");
            return false;

        }
        if (Utils.isEmpty(this.bloq.getCatPredioEdificacionPropCollection())) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar características del bloque");
            return false;
        }
        if (this.bloq.getNumPisos() > 1 && !this.existeCaracteristica(Short.valueOf("5"))) {
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el Entrepiso");
            return false;
        }
        if (Objects.equals(this.tipoEstructura, this.aporticada)) {
            if (!this.existeCaracteristica(Short.valueOf("2"))) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el tipo de columnas y pilastres");
                return false;
            }
            if (!this.existeCaracteristica(Short.valueOf("3"))) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el tipo de vigas y cadenas");
                return false;
            }
        } else {
            if (!this.existeCaracteristica(Short.valueOf("6"))) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar el tipo de pared");
                return false;
            }
        }
        if (!this.existeCaracteristica(Short.valueOf("4"))) { // contrapiso
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el tipo de contrapiso");
            return false;
        }
        if (!this.existeCaracteristica(Short.valueOf("8"))) { // cubirta
            JsfUti.messageError(null, "Advertencia", "Debe ingresar el tipo de cubierta");
            return false;
        }
        return true;
    }

    public void agregarBloque() {
        try {
            if (bloq == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            if (!validarCampos()) {
                return;
            }
            CatPredioEdificacion guardarBloque = ejb.guardarBloque(bloq, us.getName_user());
            if (Utils.isNotEmpty(idFotoPredio)) {
                idFotoPredio.stream().map((fotoPredio) -> {
                    System.out.println("Foto: " + fotoPredio.getId() + " bloque " + fotoPredio.getBloque());
                    return fotoPredio;
                }).filter((fotoPredio) -> (fotoPredio.getBloque() == null)).map((fotoPredio) -> {
                    fotoPredio.setBloque(guardarBloque.getId());
                    return fotoPredio;
                }).forEachOrdered((fotoPredio) -> {
                    catast.actualizarFotos(fotoPredio);
                });
            }

            RequestContext.getCurrentInstance().closeDialog(guardarBloque);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void devolver_edad(Integer edadCosntruccion) {
        if (edadCosntruccion == null || edadCosntruccion == 0) {
            bloq.setEdadConstruccion(new Short("0"));
        }

        bloq.setEdadConstruccion(new Short("" + (Utils.getAnio(new Date()) - edadCosntruccion)));
        if (bloq.getEdadConstruccion() == 0) {
            bloq.setEdadConstruccion(new Short("1"));
        }

    }

    public void modificarBloque() {
        try {
            if (bloq == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            if (!validarCampos()) {
                return;
            }
            bloq.setModificado(us.getName_user());
            bloq = ejb.guardarBloque(bloq, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(bloq);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void agregarCaracteristicaNivel() {
        try {
            if (nivel == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Nivel.", "");
                return;
            }
            if (areaNivel == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Area.", "");
                return;
            }
            if (Utils.isEmpty((List<?>) bloq.getCatEdificacionPisosDetCollection())) {
                bloq.setCatEdificacionPisosDetCollection(new ArrayList<>());
            } else {
                for (CatEdificacionPisosDet bc : bloq.getCatEdificacionPisosDetCollection()) {
                    if (bc.getNivel() != null) {
                        if (bc.getNivel().equals(nivel)) {
                            JsfUti.messageInfo(null, "Error", "Nivel ya fue agregada.");
                            return;
                        }
                    }
                }
            }
            if (!validarNiveles(nivel)) {
                JsfUti.messageInfo(null, "No puede agregar un nivel mayor.", "");
                return;
            }
            CatEdificacionPisosDet c = new CatEdificacionPisosDet();
            c.setEdificacion(bloq);
            c.setEstado("A");
            c.setNivel(nivel);
            c.setArea(areaNivel);
            bloq.getCatEdificacionPisosDetCollection().add(c);
            if (bloq.getAreaBloque() == null) {
                bloq.setAreaBloque(BigDecimal.ZERO);
            }
            if (maxNivel < nivel.getOrden()) {
                maxNivel = nivel.getOrden();
            }
            bloq.setAreaBloque(bloq.getAreaBloque().add(areaNivel));
            bloq.setAreaConsCenso(bloq.getAreaBloque());
            nivel = null;
            areaNivel = null;
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    protected Boolean validarNiveles(CtlgItem nv) {
        CtlgItem ct = this.catalogoDefault();
        Boolean existeDefault = false;
        try {
            for (CatEdificacionPisosDet bp : bloq.getCatEdificacionPisosDetCollection()) {
                if (bp.getNivel().equals(ct)) {
                    existeDefault = true;
                    break;
                }
            }
            Integer orden = ct.getOrden();
            if (nv.getOrden() > orden) {
                // SIEMPRE DEBE ESTAR LLENO EL CAMPO ORDEN DEL CTLG_ITEM ESO ES LO PERMITE VALIDAR
                // EL INGRESO ORDENADO DE LOS DIFERENTES NIVELES
                return nv.getOrden() == (maxNivel + 1);
            } else {
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    protected Boolean validarNivelesEliminar(CtlgItem nv) {
        CtlgItem ct = this.catalogoDefault();
        Boolean existeDefault = false;
        try {
            if (nv == null) {
                return false;
            }
            Integer orden = ct.getOrden();
            if (nv.getOrden() > orden) {
                // SIEMPRE DEBE ESTAR LLENO EL CAMPO ORDEN DEL CTLG_ITEM ESO ES LO PERMITE VALIDAR
                // EL INGRESO ORDENADO DE LOS DIFERENTES NIVELES
                return nv.getOrden() == (maxNivel - 1);
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void eliminarCaracteristicaNivel(CatEdificacionPisosDet caract) {
        try {
            if (Utils.isEmpty(bloq.getCatEdificacionPisosDetCollection())) {
                JsfUti.messageError(null, "Eliminar caracteristica", "No hay caracteristicas para eliminar");
                return;
            }
            if (validarNivelesEliminar(caract.getNivel())) {
                JsfUti.messageInfo(null, "No puede eliminar un nivel intermedio.", "");
                return;
            }
            caract.setEstado("I");
            if (caract.getId() != null) {
                catast.guardarCaracteristica(caract);
            }
            int index = 0;
            if (caract.getId() == null) {
                for (CatEdificacionPisosDet pbp : bloq.getCatEdificacionPisosDetCollection()) {
                    if (pbp.getNivel() == caract.getNivel()) {
                        break;
                    }
                    index++;
                }
                bloq.setCatEdificacionPisosDetCollection(Utils.removerItemCollecion(bloq.getCatEdificacionPisosDetCollection(), index));
            } else {
                bloq.getCatEdificacionPisosDetCollection().remove(caract);
            }
            if (bloq.getAreaBloque() == null) {
                bloq.setAreaBloque(BigDecimal.ZERO);
            }
            bloq.setAreaBloque(bloq.getAreaBloque().subtract(caract.getArea()));
            bloq.setAreaConsCenso(bloq.getAreaBloque());
            if (caract.getNivel().getOrden() > this.catalogoDefault().getOrden()) {
                maxNivel--;
            }
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void agregarCaracteristica(NodeSelectEvent event) {
        try {
            if (bloq.getNumPisos() == null) {
                JsfUti.messageInfo(null, "Error.", "Debe ingresar el numero de piso");
                return;
            }
            if (selectedNodo == null) {
                return;
            }
            if (selectedNodo.getData() instanceof CatEdfCategProp) {
                return;
            } else {
                propiedadCategoria = (CatEdfProp) selectedNodo.getData();
                categoriaConst = (CatEdfCategProp) EntityBeanCopy.clone(selectedNodo.getParent().getData());
                propiedadCategoria.setCategoria(categoriaConst);
            }

            if (propiedadCategoria == null) {
                JsfUti.messageInfo(null, "Debe Seleccionar Característica.", "");
                return;
            }
            if (!this.existePared) {
                existePared = categoriaConst.getGuiOrden() == pared;
            }
            if (Utils.isEmpty(bloq.getCatPredioEdificacionPropCollection())) {
                bloq.setCatPredioEdificacionPropCollection(new ArrayList<>());
            } else {
                for (CatPredioEdificacionProp bc : bloq.getCatPredioEdificacionPropCollection()) {
                    if (bc.getProp() != null) {
                        if (bc.getProp().equals(propiedadCategoria)) {
                            JsfUti.messageInfo(null, "Error", "Característica ya fue agregada.");
                            return;
                        }
                    }
                }
            }
            if (bloq.getCatPredioEdificacionPropCollection().isEmpty()) {
                if (categoriaConst.getGuiOrden() > estructura) {
                    JsfUti.messageInfo(null, "Error", "Debe seleccionar la estructura para continuar.");
                    return;
                }
            }
            CatPredioEdificacionProp c = new CatPredioEdificacionProp();
            c.setEdificacion(bloq);
            c.setEstado(true);
            c.setFecha(new Date());
            c.setUsuario(us.getName_user());
            c.setProp(propiedadCategoria);
            if (mainConfig.getFichaPredial().getMostrarPorcentajeEdificacion()) {
                c.setPorcentaje(BigDecimal.valueOf(100.00));
            }
            if (categoriaConst.getGuiOrden() == estructura && mainConfig.getFichaPredial().getRedenerFichaIb()) {
                tipoEstructura = propiedadCategoria.getOrden().intValue();
                validarCaracteristicasEdif();
            }
            bloq.getCatPredioEdificacionPropCollection().add(c);
            categoriaConst = null;
            propiedadCategoria = null;
            this.llenarNodo();
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void eliminarCaracteristica(CatPredioEdificacionProp caract) {
        try {
            if (Utils.isEmpty(bloq.getCatPredioEdificacionPropCollection())) {
                JsfUti.messageError(null, "Eliminar caracteristica", "No hay caracteristicas para eliminar");
            }
            caract.setEstado(false);
            if (caract.getId() != null) {
                catast.guardarCaracteristica(caract);
            }
            int index = 0;
            if (caract.getId() == null) {
                for (CatPredioEdificacionProp pbp : bloq.getCatPredioEdificacionPropCollection()) {
                    if (pbp.getProp() == caract.getProp()) {
                        break;
                    }
                    index++;
                }
                bloq.getCatPredioEdificacionPropCollection().remove(index);
            } else {
                bloq.getCatPredioEdificacionPropCollection().remove(caract);
            }
            this.validarCaracteristicasEdif();
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<CatEdfCategProp> getCategoriasEdificacion() {
        try {
            return catast.getCategoriasConst();
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, "Cargar Categorias de Contruccion", e);
        }
        return null;
    }

    public List<CatEdfProp> getPropsCategoria(CatEdfCategProp cc) {
        if (cc != null) {
            List<CatEdfProp> result = new ArrayList<>();
            try {
                List<CatEdfProp> props = catast.getPropiedadesConst(cc);
                props.forEach((prop1) -> {
                    if (prop1.getTipoEstruc() != null) {
                        if (Objects.equals(tipoEstructura, aporticada)) { // si es aproticada 
                            if (prop1.getTipoEstruc().contains("1")) {
                                if (existePared) {
                                    result.add(prop1);
                                } else { // No tiene Pared
                                    if (prop1.getCategoria().getGuiOrden() == puertas) {
                                        if (prop1.getOrden().intValue() == 1) {// comparo por el orden que sea no tiene
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == ventanas) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == cubreVentanas) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == revExterior) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == revInterior) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else {
                                        result.add(prop1);
                                    }
                                }
                            }
                        } else {// SI ES SOPORTANTE O MIXTA
                            if (prop1.getTipoEstruc().contains("2")) {
                                if (existePared) {
                                    result.add(prop1);
                                } else { // No tiene Pared
                                    if (prop1.getCategoria().getGuiOrden() == puertas) {
                                        if (prop1.getOrden().intValue() == 1) {// comparo por el orden que sea no tiene
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == ventanas) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == cubreVentanas) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == revExterior) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else if (prop1.getCategoria().getGuiOrden() == revInterior) {
                                        if (prop1.getOrden().intValue() == 1) {
                                            result.add(prop1);
                                        }
                                    } else {
                                        result.add(prop1);
                                    }
                                }
                            }
                        }
                    } else { // carga todo pero se valida que escoga la estructura
                        if (existePared) {
                            result.add(prop1);
                        } else { // No tiene Pared
                            if (prop1.getCategoria().getGuiOrden() == puertas) {
                                if (prop1.getOrden().intValue() == 1) {// comparo por el orden que sea no tiene
                                    result.add(prop1);
                                }
                            } else if (prop1.getCategoria().getGuiOrden() == ventanas) {
                                if (prop1.getOrden().intValue() == 1) {
                                    result.add(prop1);
                                }
                            } else if (prop1.getCategoria().getGuiOrden() == cubreVentanas) {
                                if (prop1.getOrden().intValue() == 1) {
                                    result.add(prop1);
                                }
                            } else if (prop1.getCategoria().getGuiOrden() == revExterior) {
                                if (prop1.getOrden().intValue() == 1) {
                                    result.add(prop1);
                                }
                            } else if (prop1.getCategoria().getGuiOrden() == revInterior) {
                                if (prop1.getOrden().intValue() == 1) {
                                    result.add(prop1);
                                }
                            } else {
                                result.add(prop1);
                            }
                        }
                    }
                });
                return result;
            } catch (Exception e) {
                Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, "Cargar Categorias de Contruccion", e);
            }
        }
        return null;
    }

    public Boolean validarCaracteristicasEdif() {
        try {
            categProp = new ArrayList<>();
            if (bloq.getNumPisos() == null) {
                bloq.setNumPisos(Short.valueOf("0"));
            }
            if (bloq.getNumPisos() == 1 && mainConfig.getFichaPredial().getRedenerFichaIb()) {
                getCategoriasEdificacion().forEach((cecp) -> {
                    if (cecp.getGuiOrden() == 5 || cecp.getGuiOrden() == 7
                            || cecp.getGuiOrden() == 12) {
                    } else {
                        if (categoriaConst != null) {
                            if (categoriaConst.getGuiOrden() == estructura) {
                                if (Objects.equals(tipoEstructura, aporticada)) {
                                    if (cecp.getTipoEstruc().contains("1")) {
                                        categProp.add((CatEdfCategProp) EntityBeanCopy.clone(cecp));
                                    }
                                } else if (Objects.equals(tipoEstructura, soportante)) {
                                    if (cecp.getTipoEstruc().contains("2")) {
                                        categProp.add((CatEdfCategProp) EntityBeanCopy.clone(cecp));
                                    }
                                } else {
                                    if (cecp.getTipoEstruc().contains("2")) {
                                        categProp.add((CatEdfCategProp) EntityBeanCopy.clone(cecp));
                                    }
                                }
                            }
                        } else {
                            if (cecp.getGuiOrden() == 5 || cecp.getGuiOrden() == 7
                                    || cecp.getGuiOrden() == 12) {
                            } else {
                                categProp.add((CatEdfCategProp) EntityBeanCopy.clone(cecp));
                            }
                        }
                    }
                });
            } else {
                categProp = (List<CatEdfCategProp>) EntityBeanCopy.clone(this.getCategoriasEdificacion());
            }
            llenarNodo();
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public void llenarNodo() {
        nodo = new DefaultTreeNode();
        if (Utils.isEmpty(categProp)) {
            categProp = getCategoriasEdificacion();
        }
        categProp.forEach((cd) -> {
            TreeNode chaild = new DefaultTreeNode(cd, nodo);
            if (mainConfig.getFichaPredial().getRedenerFichaIb()) {
                getPropsCategoria(cd).forEach((p) -> {
                    TreeNode item = new DefaultTreeNode(p, chaild);
                });
            } else {
                catast.getPropiedadesConst(cd).forEach((p) -> {
                    TreeNode item = new DefaultTreeNode(p, chaild);
                });
            }
        });
    }

    public void cerrar() {
        try {
            if (bloq.getId() == null) {
                System.out.println("Bloque sin guardar...");
                if (Utils.isNotEmpty(idFotoPredio)) {
                    idFotoPredio.stream().map((fotoPredio) -> {
                        catast.quitarFoto(fotoPredio);
                        return fotoPredio;
                    }).forEachOrdered((fotoPredio) -> {
                        fserv.deleteFile(fotoPredio.getFileOid());
                    });
                }
            }
            RequestContext.getCurrentInstance().closeDialog(null);
        } catch (Exception e) {
            Logger.getLogger(Bloques.class.getName()).log(Level.SEVERE, "Cerrar Dialog", e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            try (InputStream is = FilesUtil.copyFileServer1(event)) {
                Long fileId = fserv.uploadFile(is, event.getFile().getFileName(), event.getFile().getContentType());
                fotoBean.setNombre(event.getFile().getFileName());
                fotoBean.setPredioId(bloq.getPredio().getNumPredio().longValue());
                if (bloq.getId() != null) {
                    fotoBean.setBloque(bloq.getId());
                }
                fotoBean.setContentType(event.getFile().getContentType());
                fotoBean.setFileId(fileId);
                if (idFotoPredio == null) {
                    idFotoPredio = new ArrayList<>();
                }
                idFotoPredio.add((FotoPredio) EntityBeanCopy.clone(fotoBean.saveFotoBloque()));
                FotoPredio ftpTemp;
                if (idFotoPredio.size() > 0) {
                    fotos = new ArrayList<>();
                    ftpTemp = idFotoPredio.get(idFotoPredio.size() - 1);
                    fotos.add(ftpTemp);
                } else {
                    ftpTemp = idFotoPredio.get(idFotoPredio.size());
                    fotos.add(ftpTemp);
                }
            }
            cargarFotos();
            Faces.messageInfo(null, "Nota!", "Foto guardada satisfactoriamente");
        } catch (IOException e) {
            Logger.getLogger(FichaPredial.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cargarFotos() {
        if (bloq == null) {
            return;
        }
        if (bloq.getId() == null) {
            return;
        }
        fotos = this.catast.getFotosBloque(bloq);

    }

    public void validarSumaPorcentaje(CatPredioEdificacionProp prop) {
        BigDecimal totalPorcentaje = BigDecimal.ZERO;
        if (prop.getPorcentaje() == null) {
            prop.setPorcentaje(BigDecimal.ZERO);
        }
        if (Utils.isNotEmpty(bloq.getCatPredioEdificacionPropCollection())) {
            for (CatPredioEdificacionProp tempprop : bloq.getCatPredioEdificacionPropCollection()) {
                CatEdfCategProp tempCat = (CatEdfCategProp) EntityBeanCopy.clone(tempprop.getProp().getCategoria());
                CatEdfCategProp tempCat1 = (CatEdfCategProp) EntityBeanCopy.clone(prop.getProp().getCategoria());
                if (tempCat.equals(tempCat1)) {
                    if (tempprop.getPorcentaje() == null) {
                        tempprop.setPorcentaje(BigDecimal.ZERO);
                    }
                    totalPorcentaje = totalPorcentaje.add(tempprop.getPorcentaje());
                }
            }
        }
        if (totalPorcentaje.doubleValue() > 100d) {
            JsfUti.messageInfo(null, "Nota!", "El porcentaje no debe exceder el 100%");
            return;
        }
    }

    public void eliminarFoto() {
        if (catast.quitarFoto(foto)) {
            fserv.deleteFile(foto.getFileOid());
            fotos.remove(foto);
            Faces.messageInfo(null, "Nota!", "Foto eliminada satisfactoriamente, actualice la pagina para continuar");
        } else {
            Faces.messageWarning(null, "Advertencia!", "No se pudo eliminar la foto seleccionada");
        }
    }

    public void selecEdificaciones() {
        if (seleccionados != null) {
            RequestContext.getCurrentInstance().closeDialog(seleccionados);
        }
    }

    public CtlgItem catalogoDefault() {
        return (CtlgItem) EntityBeanCopy.clone(this.catast.getDefaultItem("bloque.nivel"));
    }

    public List<CtlgItem> getListadoCultivos() {
        if (padreItem != null && padreItem > 0) {
            return this.catast.getListadoCultivos(padreItem);
        }
        return null;
    }

    public List<CtlgItem> getListadoItemsCultivos() {
        if (hijoItem != null && hijoItem > 0) {
            return this.catast.getListadoItemsCultivos(hijoItem);
        }
        return null;
    }

    public Boolean existeCaracteristica(Short numOrdenCacact) {
        if (bloq.getCatPredioEdificacionPropCollection().stream().anyMatch((cbc) -> (cbc.getProp().getCategoria().getGuiOrden() == numOrdenCacact))) {
            return true;
        }
        return false;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public CatPredioEdificacion getBloq() {
        return bloq;
    }

    public void setBloq(CatPredioEdificacion edif) {
        this.bloq = edif;
    }

    public String getIdCatPredioBloq() {
        return idCatPredioBloq;
    }

    public void setIdCatPredioBloq(String idCatPredioBloq) {
        this.idCatPredioBloq = idCatPredioBloq;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    public List<CatPredioEdificacion> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatPredioEdificacion> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public List<CtlgItem> catalogos(String catalogo) {
        return ejb.getItemsByCatalogo(catalogo);
    }

    public List<CtlgItem> getCondicionFisica() {
        return ejb.propiedadHorizontal().getCtlgItem("predio.bloque.condicionfisica");
    }

    public List<CtlgItem> getValorCultural() {
        return ejb.propiedadHorizontal().getCtlgItem("predio.bloque.valorcultural");
    }

    public List<CtlgItem> getEstadoConservacion() {
        return ejb.propiedadHorizontal().getCtlgItem("predio.bloque.estadoconservaci");
    }

    public List<CtlgItem> getUsoConstructivo() {
        return ejb.propiedadHorizontal().getCtlgItem("predio.bloque.usoconstructivo");
    }

    public String getCaracteristica() {
        return Caracteristica;
    }

    public void setCaracteristica(String Caracteristica) {
        this.Caracteristica = Caracteristica;
    }

    public CatEdfCategProp getCategoriaConst() {
        return categoriaConst;
    }

    public void setCategoriaConst(CatEdfCategProp categoriaConst) {
        this.categoriaConst = categoriaConst;
    }

    public CatEdfProp getPropiedadCategoria() {
        return propiedadCategoria;
    }

    public void setPropiedadCategoria(CatEdfProp propiedadCategoria) {
        this.propiedadCategoria = propiedadCategoria;
    }

    public CtlgItem getNivel() {
        return nivel;
    }

    public void setNivel(CtlgItem nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getAreaNivel() {
        return areaNivel;
    }

    public void setAreaNivel(BigDecimal areaNivel) {
        this.areaNivel = areaNivel;
    }

    public List<FotoPredio> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoPredio> fotos) {
        this.fotos = fotos;
    }

    public FotoPredio getFoto() {
        return foto;
    }

    public void setFoto(FotoPredio foto) {
        this.foto = foto;
    }

    public Integer getPadreItem() {
        return padreItem;
    }

    public void setPadreItem(Integer padreItem) {
        this.padreItem = padreItem;
    }

    public Integer getHijoItem() {
        return hijoItem;
    }

    public void setHijoItem(Integer hijoItem) {
        this.hijoItem = hijoItem;
    }

    public List<CatEdfCategProp> getCategProp() {
        return categProp;
    }

    public void setCategProp(List<CatEdfCategProp> categProp) {
        this.categProp = categProp;
    }

    public List<CatEdfProp> getProp() {
        return prop;
    }

    public void setProp(List<CatEdfProp> prop) {
        this.prop = prop;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Boolean getEsVer() {
        return esVer;
    }

    public void setEsVer(Boolean esVer) {
        this.esVer = esVer;
    }

    public TreeNode getNodo() {
        return nodo;
    }

    public void setNodo(TreeNode nodo) {
        this.nodo = nodo;
    }

    public TreeNode getSelectedNodo() {
        return selectedNodo;
    }

    public void setSelectedNodo(TreeNode selectedNodo) {
        this.selectedNodo = selectedNodo;
    }

    protected boolean validarPorcentajes() {
        Map<String, BigDecimal> suma = new HashMap<>();
        try {
            bloq.getCatPredioEdificacionPropCollection().forEach((p) -> {
                BigDecimal get = suma.get(p.getProp().getCategoria().getNombre());
                if (p.getPorcentaje() == null) {
                    p.setPorcentaje(BigDecimal.ZERO);
                }
                if (get == null) {
                    suma.put(p.getProp().getCategoria().getNombre(), p.getPorcentaje());
                } else {
                    suma.put(p.getProp().getCategoria().getNombre(), get.add(p.getPorcentaje()));
                }
            });
            for (Map.Entry<String, BigDecimal> entry : suma.entrySet()) {
                BigDecimal value = entry.getValue();
                if (value.doubleValue() <= 0 || value.doubleValue() > 100) {
                    JsfUti.messageError(null, "Error", "La suma de las caracteristicas de "
                            + entry.getKey() + " no debe ser igual a cero o mayor a 100, valor actual es: " + value);
                    return false;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return true;
    }

    public Integer getTransitorio() {
        return transitorio;
    }

    public void setTransitorio(Integer transitorio) {
        if (Integer.valueOf(1).equals(transitorio)) {
            bloqueDlgCnf.setTransitorio(true);
        } else {
            bloqueDlgCnf.setTransitorio(false);
        }
        this.transitorio = transitorio;
    }

    public String getNoPersist() {
        return noPersist;
    }

    public void setNoPersist(String noPersist) {
        this.noPersist = noPersist;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public String getTramite() {
        return tramite;
    }

    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    public String getNombreTabEdificacion() {
        nombreTabEdificacion = "Caracteristicas del bloque";
        return nombreTabEdificacion;
    }

}
