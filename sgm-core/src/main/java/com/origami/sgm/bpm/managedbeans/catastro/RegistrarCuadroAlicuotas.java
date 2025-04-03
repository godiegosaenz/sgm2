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
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioAlicuotaComponente;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.CatPredioLazy;
import com.origami.sgm.entities.predio.models.NivelModel;
import com.origami.sgm.services.ejbs.censocat.OmegaUploader;
import com.origami.sgm.services.ejbs.censocat.UploadDocumento;
import com.origami.sgm.services.interfaces.catastro.AvaluosServices;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.sgm.services.interfaces.catastro.FusionDivisionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import util.Faces;
import util.HiberUtil;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author dfcalderio
 */
@Named(value = "cuadroAlicuotasView")
@ViewScoped
public class RegistrarCuadroAlicuotas implements Serializable {

    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    protected FusionDivisionServices fusionDivisionEjb;
    @Inject
    protected UserSession sess;
    @Inject
    protected ServletSession ss;
    @javax.inject.Inject
    protected CatastroServices catastroService;
    @Inject
    protected UploadDocumento documentoBean;
    @Inject
    protected OmegaUploader fserv;
    @Inject
    private AvaluosServices avaluosServices;

    private Long idDoc;

    protected CatPredioLazy predios;
    protected CatPredio predioSeleccionado;
    protected CatPredio predioMatriz;
    protected List<CatCiudadela> ciudadelas;
    protected Collection<CatPredioEdificacion> edificaciones;
    protected CatPredioEdificacion edificacionSeleccionada;
    protected CatEscritura escritura;
    protected int cantAlicuotasBloque;
    protected List<NivelModel> niveles;
    protected List<CatPredio> prediosGenerados;
    protected List<CatPredio> prediosGeneradosFiltrados;
    protected Boolean fichaMatriz;
    protected BigDecimal totalAlicuotasComunal;
    protected CatPredioPropietario propietarioSeleccionado;
    protected int index;

    protected CatEnteLazy entes;
    protected CatEnte enteSeleccionado;
    protected CatPredioPropietario prop;

    protected CatPredioAlicuotaComponente componenteSeleccionado;
    protected List<CatPredioAlicuotaComponente> componenteEliminar;
    protected List<CatCanton> cantones;
    protected boolean tieneEscritura;
    protected boolean skipGenerarPh = true;
    protected boolean stepSavePhs = false;
    private Boolean esModificatoria = false;

    @PostConstruct
    public void init() {
        predios = new CatPredioLazy("A");
        predioSeleccionado = new CatPredio();
        ciudadelas = manager.findAllOrdered(CatCiudadela.class, new String[]{"nombre"}, new Boolean[]{true});
        escritura = new CatEscritura();
        escritura.setTipoPh(0);
        escritura.setCantAlicuotas(0);
        escritura.setCantBloques(0);
        edificaciones = new ArrayList<>();
        edificacionSeleccionada = new CatPredioEdificacion();
        edificacionSeleccionada.setNiveles(new ArrayList<>());
        niveles = new ArrayList<>();
        prediosGenerados = new ArrayList<>();
        fichaMatriz = Boolean.FALSE;
        totalAlicuotasComunal = BigDecimal.ZERO;
        enteSeleccionado = new CatEnte();
        entes = new CatEnteLazy(true);
        componenteSeleccionado = new CatPredioAlicuotaComponente();
        componenteEliminar = new ArrayList<>();
        cantones = manager.findAll(CatCanton.class);
    }

    public void grabarCuadroAlicuotas() {
        try {
            if (validarSumaAlicuotas()) {
                componenteEliminar.forEach((cpc) -> {
                    manager.delete(cpc);
                });
                if (!fichaMatriz) {
                    if (tieneEscritura) {
                        String[] param = {"idPredio"};
                        Object[] val = {predioMatriz.getId()};
                        List<CatEscritura> escs = manager.findAll(Querys.getEscriturasByPredioDesc, param, val);
                        if (!escs.isEmpty()) {
                            escs.stream().map((e) -> {
                                e.setEstado("I");
                                return e;
                            }).forEachOrdered((e) -> {
                                manager.persist(e);
                            });
                        }
                        escritura.setAreaSolar(predioMatriz.getAreaSolar());
                        escritura.setPredio(predioMatriz);
                        escritura.setEstado("A");
                        escritura.setFecCre(new Date());
                        escritura = (CatEscritura) manager.persist(escritura);
                    }

                    predioMatriz.setFichaMadre(Boolean.TRUE);
                    predioMatriz.setPropiedadHorizontal(Boolean.TRUE);
                    predioMatriz.setCantAlicuotas(escritura.getCantAlicuotas());
                    predioMatriz = (CatPredio) manager.persist(predioMatriz);

                }
                try {
                    if (prediosGenerados != null) {
                        prediosGenerados.forEach((p) -> {
                            if (p.getEscritura() != null) {
                                if (p.getEscritura().getIdEscritura() != null) {
                                    p.setTieneEscritura(true);
                                }
                            }
                        });
                        fusionDivisionEjb.registrarPredios(prediosGenerados, predioMatriz);
                    }
                } catch (Exception e) {
                    Logger.getLogger(RegistrarCuadroAlicuotas.class.getName()).log(Level.SEVERE, "Actulizar Escrituras", e);
                }
                System.out.println("prediosGenerados" + prediosGenerados.size());
                avaluosServices.generateAvaluo(prediosGenerados,
                                Utils.getAnio(new Date()), Utils.getAnio(new Date()), false, sess.getName_user());
                Faces.messageInfo(null, "Gestion", " de alicuotas realizado satisfactoriamente.");
                Faces.update("growl");
                Faces.redirectFaces("/faces/vistaprocesos/catastro/gestionarPH/gestionarPH.xhtml");
            } else {
                Faces.messageWarning(null, "Suma de alicuotas es mayor a 100, rectificar la asignación.", "");
                Faces.update("growl");
            }
        } catch (Exception e) {
            Faces.messageError(null, "Error", " Hubo un error al procesar la inforamción. " + e.getMessage());
            Faces.update("growl");
            Faces.redirectFaces("/faces/vistaprocesos/catastro/gestionarPH/gestionarPH.xhtml");
        }
    }

    public void procesarCuadroAlicuotas() {
        int cantidad = 0;
        cantidad = edificaciones.stream().map((e) -> e.getCantAlicuotas()).reduce(cantidad, Integer::sum);
        if (cantidad != escritura.getCantAlicuotas() && !esModificatoria) {
            Faces.messageWarning(null, "La suma de las alícuotas de los bloques tiene que ser " + escritura.getCantAlicuotas() + " y no " + cantidad, "");
            Faces.update("growl");
        } else {
            niveles = new ArrayList<>();
            edificaciones.forEach((e) -> {
                niveles.addAll(generarNiveles(e));
            });
        }
    }

    public void procesarCuadroAlicuotasCancelar() {
        niveles = new ArrayList<>();
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

    public List<CatTiposDominio> getDominios() {
        return manager.findAllObjectOrder(CatTiposDominio.class, new String[]{"nombre"}, true);
    }

    public List<CtlgItem> getCatalogoItems(String argumento) {
        HiberUtil.newTransaction();
        List<CtlgItem> ctlgItem = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
        return ctlgItem;
    }

    public Integer cantidadEdificaciones(CatPredio p) {

        Collection<CatPredioEdificacion> eds = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{p.getId()});
        return eds != null ? eds.size() : 0;
    }

    public String onFlowProcess(FlowEvent event) {
        if (event.getNewStep().equals("escritura_ficha_madre")) {
            if (predioMatriz == null) {
                Faces.messageWarning(null, "Debe seleccionar una predio para continuar", "");
                Faces.update("growl");
                return event.getOldStep();
            } else {
                System.out.println("Verificando Predios Generados...");
//                if (!prediosGenerados.isEmpty()) {
//                    sumaAlicuotasComunal();
//                    return "predios_generados";
//                }
                if (!prediosGenerados.isEmpty()) {
                    esModificatoria = true;
                    verificarTipoPh();
                    return "generar_phs";
                }
                if (cantidadEdificaciones(predioMatriz) == 0) {
                    Faces.messageWarning(null, "Debe seleccionar un predio con edificaciones", "");
                    Faces.update("growl");
                    return event.getOldStep();
                }
            }
        }
        if (event.getNewStep().equals("predios_generados")) {
//            if (!prediosGenerados.isEmpty()) {
//                return "predios_generados";
//            }

            if (niveles.isEmpty() && escritura.getTipoPh() != 1) {
                Faces.messageWarning(null, "Debe procesar las edificaciones para continuar", "");
                Faces.update("growl");
                return event.getOldStep();
            } else {
                if (!validarAlicuotasPorNivel()) {
                    System.out.println("False Validar Niveles");
                    return event.getOldStep();
                } else {
                    System.out.println("True Validar Niveles");
                    this.stepSavePhs = true;
                    generarAlicuotas();
                }
            }
        }
        if (event.getOldStep().equals("predios_generados")) {
            niveles.clear();
            prediosGenerados.clear();
            predioSeleccionado = new CatPredio();
            predioMatriz = null;
            return "ficha_madre";
        }
        if (event.getNewStep().equals("generar_phs")) {
            skipGenerarPh = escritura.getTipoPh() == 1;
            if (skipGenerarPh) {
                System.out.println("Generando alicuotas por favor espere...");
                this.generarAlicuotas();
                return "predios_generados";
            }
        }
        if (event.getOldStep().equals("generar_phs") && !esModificatoria) {
            if (skipGenerarPh) {
                return "escritura_ficha_madre";
            }
        }

        return event.getNewStep();
    }

    public void onRowSelect(SelectEvent event) {
        predioSeleccionado = (CatPredio) event.getObject();
        escritura.setCantBloques(cantidadEdificaciones(predioSeleccionado));
        edificaciones = manager.findAll(Querys.edificacionesByPredio, new String[]{"idPredio"}, new Object[]{predioSeleccionado.getId()});
        predioMatriz = manager.find(CatPredio.class, predioSeleccionado.getId());
        predioSeleccionado = new CatPredio();
        String[] params = {"predioRaiz", "estado"};
        Object[] vals = {predioMatriz.getId(), "A"};
        prediosGenerados = manager.findAll(Querys.getPHsByMatriz, params, vals);
        if (!prediosGenerados.isEmpty()) {
            String[] param = {"idPredio"};
            Object[] val = new Object[1];

            for (int i = 0; i < prediosGenerados.size(); i++) {
                val[0] = prediosGenerados.get(i).getId();
                prediosGenerados.get(i).setEscritura(new CatEscritura());
                if (prediosGenerados.get(i).getTieneEscritura() != null) {
                    if (Objects.equals(prediosGenerados.get(i).getTieneEscritura(), Boolean.TRUE)) {
                        CatEscritura e = (CatEscritura) manager.find(Querys.getEscriturasByPredioDesc, param, val);
                        if (e != null) {
                            prediosGenerados.get(i).setEscritura(e);
                        }
                    }
                }
                Collection<CatPredioPropietario> propietarios = (Collection<CatPredioPropietario>) manager.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{prediosGenerados.get(i).getId()});
                if (propietarios == null) {
                    propietarios = new ArrayList<>();
                }
                prediosGenerados.get(i).setCatPredioPropietarioCollection(propietarios);
            }
            fichaMatriz = Boolean.TRUE;
            sumaAlicuotasComunal();
        }

    }

    public void onCellEditNiveles(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
    }

    public void onCellEditPredios(CellEditEvent event) {

    }

    private List<NivelModel> generarNiveles(CatPredioEdificacion edf) {
        String edif = "Nro. ";
        int cont = 0;
        List<NivelModel> lista = new ArrayList<>();
        if (edf.getCantAlicuotas() != 0) {
            if (edf.getNumPisos() != null) {
                for (int i = 1; i <= edf.getNumPisos(); i++) {
                    NivelModel nivel = new NivelModel(i, edf.getNoEdificacion());
                    nivel.setEdificacion(edf.getId());
                    lista.add(nivel);
                }
            } else {
                if (cont == 0) {
                    edif = edf.getNoEdificacion() + "";
                } else {
                    edif += ", " + edf.getNoEdificacion();
                }
                cont++;
            }
        }
        if (cont == 1) {
            Faces.messageWarning(null, "La Edificacion " + edif + " no tiene Nro nivel registrado, no se pueden generar las alicuotas. ", "");
            Faces.update("growl");
        }
        if (cont > 1) {
            Faces.messageWarning(null, "Las edificaciones " + edif + " no tienen Nro nivel registrado, no se pueden generar las alicuotas. ", "");
            Faces.update("growl");
        }

        return lista;
    }

    private boolean validarAlicuotasPorNivel() {
        for (CatPredioEdificacion ed : edificaciones) {
            int totalAlicuotasPorNivel = 0;
            totalAlicuotasPorNivel = niveles.stream().filter((n)
                    -> (Objects.equals(n.getNoEdificacion(), ed.getNoEdificacion()))).map((n)
                    -> (n.getCantDpto() + n.getCantBodegas() + n.getCantParqueos())).reduce(totalAlicuotasPorNivel, Integer::sum);
            if (totalAlicuotasPorNivel != ed.getCantAlicuotas()) {
                Faces.messageWarning(null, "La edificacion " + ed.getNoEdificacion() + " tiene " + ed.getCantAlicuotas() + " y la suma de alicuotas de los niveles es " + totalAlicuotasPorNivel, "");
                Faces.update("growl");
                return false;
            }
        }
        return true;
    }

    public void generarAlicuotas() {
        if (esModificatoria) {
            System.out.println("Agregando nuevas Phs");
            for (CatPredio pg : prediosGenerados) {
                if (pg.getPiso() > 0 && pg.getUnidad() > 0) {
                    skipGenerarPh = false;
                    break;
                } else {
                    skipGenerarPh = true;
                }
            }
        } else {
            System.out.println("Generando Phs");
            prediosGenerados = new ArrayList<>();
        }

        BigInteger numP = fusionDivisionEjb.generarNumPredio();
        System.out.println("skipGenerarPh " + skipGenerarPh);
        System.out.println("escritura.getCantAlicuotas() " + escritura.getCantAlicuotas());
        if (!skipGenerarPh) {
            for (NivelModel n : niveles) {
                short unidad = this.getUnidad(n);
                for (int i = 0; i < n.getCantDpto(); i++) {
                    prediosGenerados.add(copyPredio(0, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                    numP = numP.add(new BigInteger("1"));
                }
                for (int i = 0; i < n.getCantBodegas(); i++) {
                    prediosGenerados.add(copyPredio(1, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                    numP = numP.add(new BigInteger("1"));
                }
                for (int i = 0; i < n.getCantParqueos(); i++) {
                    prediosGenerados.add(copyPredio(2, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                    numP = numP.add(new BigInteger("1"));
                }
            }
        } else {
            switch (this.escritura.getTipoPh()) {
                case 1: // PHH
                    for (int i = 0; i < escritura.getCantAlicuotas(); i++) {
                        System.out.println(" phh " + i);
                        prediosGenerados.add(copyPredio(1, Short.valueOf("" + (i + this.getBloque())), Short.valueOf("0"), Short.valueOf("0"), numP));
                        numP = numP.add(new BigInteger("1"));
                    }
                    break;
                default: //PHV O PHV Y PHH
                    for (NivelModel n : niveles) {
                        short unidad = this.getUnidad(n);
                        for (int i = 0; i < n.getCantDpto(); i++) {
                            prediosGenerados.add(copyPredio(0, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                            numP = numP.add(new BigInteger("1"));
                        }
                        for (int i = 0; i < n.getCantBodegas(); i++) {
                            prediosGenerados.add(copyPredio(1, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                            numP = numP.add(new BigInteger("1"));
                        }
                        for (int i = 0; i < n.getCantParqueos(); i++) {
                            prediosGenerados.add(copyPredio(2, n.getNoEdificacion(), (short) n.getNroNivel(), unidad++, numP));
                            numP = numP.add(new BigInteger("1"));
                        }
                    }
                    break;
            }
        }
        System.out.println("stepSavePhs " + stepSavePhs);
        if (stepSavePhs) {
            List<CatPredio> cph = new ArrayList();
            for (CatPredio cp : prediosGenerados) {
                cp = (CatPredio) manager.persist(cp);
                cph.add(cp);
            }
            prediosGenerados = new ArrayList();
            prediosGenerados = cph;
        }

    }

    public void verificarTipoPh() {
        for (CatPredio pg : prediosGenerados) {
            if (pg.getPiso() > 0 && pg.getUnidad() > 0) {
                skipGenerarPh = false;
                this.escritura.setTipoPh(2);
                break;
            } else {
                skipGenerarPh = true;
                if (this.escritura == null) {
                    this.escritura = new CatEscritura();
                }
                this.escritura.setTipoPh(1);
            }
        }
    }

    /**
     * Realizamos la busqueda de la unidad del bloque para el caso de
     * modificatoria
     *
     * @param n Nivel a verificar si existen phs
     * @return si no existe retorna 1, caso contrario el numero de nivel que
     * corresponde
     */
    public Short getUnidad(NivelModel n) {
        if (Utils.isEmpty(prediosGenerados)) {
            return 1;
        }
        short index = 0;
        for (CatPredio p : prediosGenerados) {
            if (p.getBloque() == n.getNoEdificacion() && p.getPiso() == n.getNroNivel()) {
                if (index < p.getUnidad()) {
                    index = p.getUnidad();
                }
            }
        }
        if (index == 0) {
            index = 1;
        } else {
            index++;
        }
        return index;
    }

    public Short getBloque() {
        if (Utils.isEmpty(prediosGenerados)) {
            return 1;
        }
        short index = 0;
        for (CatPredio p : prediosGenerados) {
            if (index < p.getBloque()) {
                index = p.getBloque();
            }
        }
        return (short) (index + Short.valueOf("1"));
    }
//    protected CatPredio copyPredio(int tipo, Short bloque, Short piso, Short unidad, BigInteger numP) {
//        try {
//            CatPredio ph = new CatPredio();
//            BeanUtils.copyProperties(predioMatriz, ph);
//            ph.setId(null);
//            ph.setInstCreacion(new Date());
//            ph.setBloque(bloque);
//            ph.setPiso(piso);
//            ph.setUnidad(unidad);
//            ph.setPredioRaiz(new BigInteger(predioMatriz.getId().toString()));
//            ph.setPropiedadHorizontal(Boolean.TRUE);
//            ph.setEscritura(new CatEscritura());
//            ph.setNumDepartamento(tipo == 0 ? (unidad + 100) + "" : null);
//            ph.setAreaDeclaradaConst(null);
//            ph.setCatPredioPropietarioCollection(new ArrayList<>());
//            ph.setAlicuotaComponentes(new ArrayList<>());
//            ph.setNumPredio(numP);
//            ph.setNumeroFicha(numP);
//            ph.setObservaciones(null);
//            ph = fusionDivisionEjb.registrarPredio(ph);
//            return ph;
//        } catch (Exception ex) {
//            Logger.getLogger(RegistrarCuadroAlicuotas.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    protected CatPredio copyPredio(int tipo, Short bloque, Short piso, Short unidad, BigInteger numP) {
        try {
            CatPredio ph = fusionDivisionEjb.clonarPredio(predioMatriz, bloque, piso, unidad, numP);
            CatEscritura e = new CatEscritura();
            e.setPredio(ph);
            ph.setEscritura(e);
            return ph;
        } catch (Exception ex) {
            Logger.getLogger(RegistrarCuadroAlicuotas.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validarSumaAlicuotas() {
        return totalAlicuotasComunal.compareTo(new BigDecimal("100")) < 1;
    }

    public void sumaAlicuotasComunal() {
        totalAlicuotasComunal = BigDecimal.ZERO;
        prediosGenerados.stream().filter((p) -> (p.getAlicuotaConst() != null)).forEachOrdered((p) -> {
            totalAlicuotasComunal = totalAlicuotasComunal.add(p.getAlicuotaConst());
        });

        if (totalAlicuotasComunal.compareTo(new BigDecimal("100")) > 0) {
            Faces.messageWarning(null, "La suma total de alicuotas supera el 100%", "");
            Faces.update("growl");
        }
    }

    public void addComponenteAlicuotas(int pos) {
        index = pos;
        componenteSeleccionado.setPredio(prediosGenerados.get(index));

        componenteSeleccionado = (CatPredioAlicuotaComponente) manager.persist(componenteSeleccionado);
        prediosGenerados.get(index).getAlicuotaComponentes().add(componenteSeleccionado);

        List<CatPredioAlicuotaComponente> comps = prediosGenerados.get(index).getAlicuotaComponentes();
        BigDecimal areaConstruccion = BigDecimal.ZERO;
        BigDecimal areaDeclarada = BigDecimal.ZERO;
        BigDecimal alicuotaUtil = BigDecimal.ZERO;
        BigDecimal alicuotaComunal = BigDecimal.ZERO;

        for (CatPredioAlicuotaComponente c : comps) {
            if (c.getAlicuotaComunal() != null) {
                alicuotaComunal = alicuotaComunal.add(c.getAlicuotaComunal());
            }
            if (c.getAlicuotaUtil() != null) {
                alicuotaUtil = alicuotaUtil.add(c.getAlicuotaUtil());
            }
            if (c.getAreaConstruccion() != null) {
                areaConstruccion = areaConstruccion.add(c.getAreaConstruccion());
            }
            if (c.getAreaDeclarada() != null) {
                areaDeclarada = areaDeclarada.add(c.getAreaDeclarada());
            }
        }
        prediosGenerados.get(index).setAlicuotaConst(alicuotaComunal.equals(BigDecimal.ZERO) ? null : alicuotaComunal);
        prediosGenerados.get(index).setAlicuotaUtil(alicuotaUtil.equals(BigDecimal.ZERO) ? null : alicuotaUtil);
        prediosGenerados.get(index).setAreaConstPh(areaConstruccion.equals(BigDecimal.ZERO) ? null : areaConstruccion);
        prediosGenerados.get(index).setAreaDeclaradaConst(areaDeclarada.equals(BigDecimal.ZERO) ? null : areaDeclarada);

        componenteSeleccionado = new CatPredioAlicuotaComponente();

        sumaAlicuotasComunal();
    }

    public void updatePH(int pos) {
        index = pos;

        List<CatPredioAlicuotaComponente> comps = prediosGenerados.get(index).getAlicuotaComponentes();
        BigDecimal areaConstruccion = BigDecimal.ZERO;
        BigDecimal areaDeclarada = BigDecimal.ZERO;
        BigDecimal alicuotaUtil = BigDecimal.ZERO;
        BigDecimal alicuotaComunal = BigDecimal.ZERO;

        for (CatPredioAlicuotaComponente c : comps) {
            if (c.getAlicuotaComunal() != null) {
                alicuotaComunal = alicuotaComunal.add(c.getAlicuotaComunal());
            }
            if (c.getAlicuotaUtil() != null) {
                alicuotaUtil = alicuotaUtil.add(c.getAlicuotaUtil());
            }
            if (c.getAreaConstruccion() != null) {
                areaConstruccion = areaConstruccion.add(c.getAreaConstruccion());
            }
            if (c.getAreaDeclarada() != null) {
                areaDeclarada = areaDeclarada.add(c.getAreaDeclarada());
            }
        }
        prediosGenerados.get(index).setAlicuotaConst(alicuotaComunal.equals(BigDecimal.ZERO) ? null : alicuotaComunal);
        prediosGenerados.get(index).setAlicuotaUtil(alicuotaUtil.equals(BigDecimal.ZERO) ? null : alicuotaUtil);
        prediosGenerados.get(index).setAreaConstPh(areaConstruccion.equals(BigDecimal.ZERO) ? null : areaConstruccion);
        prediosGenerados.get(index).setAreaDeclaradaConst(areaDeclarada.equals(BigDecimal.ZERO) ? null : areaDeclarada);

        sumaAlicuotasComunal();
    }

    public void deleteComponenteAlicuotas(int posPredio, int postComponente) {
        index = posPredio;

        CatPredioAlicuotaComponente comp = prediosGenerados.get(index).getAlicuotaComponentes().get(postComponente);
        if (!componenteEliminar.contains(comp)) {
            componenteEliminar.add(comp);
        }
        prediosGenerados.get(index).getAlicuotaComponentes().remove(postComponente);
    }

    public void updateEscritura(int pos, CatEscritura e) {
        index = pos;
        prediosGenerados.get(index).setEscritura(e);

    }

    public void grabarEscritura(int pos) {
        index = pos;
        CatEscritura e = prediosGenerados.get(index).getEscritura();
        if (e.getIdEscritura() == null) {
            e.setFecCre(new Date());
        }
        prediosGenerados.get(index).setTieneEscritura(Boolean.TRUE);
        CatPredio p = (CatPredio) manager.persist(prediosGenerados.get(index));
        prediosGenerados.remove(index);
        prediosGenerados.add(index, p);
        e.setPredio(prediosGenerados.get(index));
        e.setEstado("A");
        e.setCantAlicuotas(null);
        try {

            e = (CatEscritura) manager.persist(e);
            prediosGenerados.get(index).setEscritura(e);
            Faces.messageInfo(null, "Escritura", " actualizada con exito.");
            Faces.update("growl");
        } catch (Exception ex) {
            Faces.messageError(null, "Error: Escritura no grabada.", "");
            Faces.update("growl");
        }

    }

    public void propietario(CatPredioPropietario propietario, int pos) {

        index = pos;
        Map<String, List<String>> params = new HashMap<>();
        List<String> p = new ArrayList<>();
        p.add(prediosGenerados.get(index).getId().toString());
        params.put("idPredio", p);
        p = new ArrayList<>();
        if (propietario != null && propietario.getId() != null) {
            p.add(propietario.getId().toString());
        }
        params.put("idCatPredioPro", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        params.put("nuevo", p);
        p = new ArrayList<>();
        if (propietario == null) {
            p.add("true");
        } else {
            p.add("false");
        }
        p = new ArrayList<>();
        p.add("true");
        params.put("editar", p);

        Map<String, Object> options = new HashMap<>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        options.put("width", "85%");
        options.put("height", "450");
        options.put("closable", true);
        options.put("contentWidth", "100%");
        RequestContext.getCurrentInstance().openDialog("/resources/dialog/propietarios", options, params);
    }

    public void procesarPropietario(SelectEvent event) {

        propietarioSeleccionado = (CatPredioPropietario) event.getObject();
        if (propietarioSeleccionado != null) {

            if (!prediosGenerados.get(index).getCatPredioPropietarioCollection().contains(propietarioSeleccionado)) {
                prediosGenerados.get(index).getCatPredioPropietarioCollection().add(propietarioSeleccionado);
            } else {
            }

            Faces.messageInfo(null, "Nota!", "Propietarios actualizadas satisfactoriamente");
            Faces.update("phs:" + index + ":dtPropietarios");
        }
    }

    public void eliminarPropietario(CatPredioPropietario propietario, int post) {
        index = post;
        propietario.setEstado("I");
        propietario.setModificado(sess.getName_user());
        propietario = catastroService.guardarPropietario(propietario, sess.getName_user());
        prediosGenerados.get(index).getCatPredioPropietarioCollection().remove(propietario);
        JsfUti.messageInfo(null, "Propietario", "Propietario eliminado.");
        Faces.update("phs:" + index + ":dtPropietarios");
    }

    public List<CatPredioPropietario> propietariosPredio(CatPredio p) {

        List<CatPredioPropietario> list = new ArrayList<>();
        list.addAll(p.getCatPredioPropietarioCollection());

        return list;
    }

    public void handleFileDocumentBySave(FileUploadEvent event) {
        try {
            Date d = new Date();
            File file = new File(SisVars.rutaRepotiorioArchivo + d.getTime() + event.getFile().getFileName());

            InputStream is;
            is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Long documentoId = fserv.uploadFile(event.getFile().getInputstream(), event.getFile().getFileName(), event.getFile().getContentType());
            documentoBean.setFechaCreacion(new Date());
            documentoBean.setNombre(event.getFile().getFileName());
            documentoBean.setRaiz(predioMatriz.getId());
            documentoBean.setContentType(event.getFile().getContentType());
            documentoBean.setDocumentoId(documentoId);
            documentoBean.setIdentificacion("Propiedad Horizontal");
            GeDocumentos saveDocumento = documentoBean.saveDocumento();
            if (saveDocumento != null) {
                this.idDoc = saveDocumento.getId();
            }
            is.close();
            out.close();
            Faces.messageInfo(null, "Información", "Archivo cargado satisfactoriamente.");
        } catch (IOException e) {
            Logger.getLogger(RegistrarCuadroAlicuotas.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void calcularAlicuota(CatPredio pt) {
        if (pt == null) {
            return;
        }
        if (pt.getAlicuotaUtil() == null) {
            Faces.messageInfo(null, "Información", "Debe ingresar la alicuota.");
            return;
        }
        if (predioMatriz.getAreaSolar() == null) {
            predioMatriz.setAreaSolar(BigDecimal.ZERO);
        }
        pt.setAreaTerrenoAlicuota(predioMatriz.getAreaSolar().multiply(pt.getAlicuotaUtil())
                .divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP));
        pt.setAreaSolar(predioMatriz.getAreaSolar().multiply(pt.getAlicuotaUtil())
                .divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP));
        pt.setAreaDeclaradaConst(predioMatriz.getAreaDeclaradaConst().multiply(pt.getAlicuotaUtil())
                .divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP));
        pt.setAlicuotaConst(pt.getAlicuotaUtil());
        pt.setAlicuotaTerreno(pt.getAlicuotaUtil());
    }

    public void checkTieneEscritura() {
        predioMatriz.setCantAlicuotas(escritura.getCantAlicuotas());
    }

    public CatPredioLazy getPredios() {
        return predios;
    }

    public void setPredios(CatPredioLazy predios) {
        this.predios = predios;
    }

    public CatPredio getPredioSeleccionado() {
        return predioSeleccionado;
    }

    public void setPredioSeleccionado(CatPredio predioSeleccionado) {
        this.predioSeleccionado = predioSeleccionado;
    }

    public FusionDivisionServices getFusionDivisionEjb() {
        return fusionDivisionEjb;
    }

    public void setFusionDivisionEjb(FusionDivisionServices fusionDivisionEjb) {
        this.fusionDivisionEjb = fusionDivisionEjb;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public CatastroServices getCatastroService() {
        return catastroService;
    }

    public void setCatastroService(CatastroServices catastroService) {
        this.catastroService = catastroService;
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public List<CatCiudadela> getCiudadelas() {
        return ciudadelas;
    }

    public void setCiudadelas(List<CatCiudadela> ciudadelas) {
        this.ciudadelas = ciudadelas;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public Collection<CatPredioEdificacion> getEdificaciones() {
        return edificaciones;
    }

    public void setEdificaciones(Collection<CatPredioEdificacion> edificaciones) {
        this.edificaciones = edificaciones;
    }

    public CatPredioEdificacion getEdificacionSeleccionada() {
        return edificacionSeleccionada;
    }

    public void setEdificacionSeleccionada(CatPredioEdificacion edificacionSeleccionada) {
        this.edificacionSeleccionada = edificacionSeleccionada;
    }

    public int getCantAlicuotasBloque() {
        return cantAlicuotasBloque;
    }

    public void setCantAlicuotasBloque(int cantAlicuotasBloque) {
        this.cantAlicuotasBloque = cantAlicuotasBloque;
    }

    public List<NivelModel> getNiveles() {
        return niveles;
    }

    public void setNiveles(List<NivelModel> niveles) {
        this.niveles = niveles;
    }

    public List<CatPredio> getPrediosGenerados() {
        return prediosGenerados;
    }

    public void setPrediosGenerados(List<CatPredio> prediosGenerados) {
        this.prediosGenerados = prediosGenerados;
    }

    public List<CatPredio> getPrediosGeneradosFiltrados() {
        return prediosGeneradosFiltrados;
    }

    public void setPrediosGeneradosFiltrados(List<CatPredio> prediosGeneradosFiltrados) {
        this.prediosGeneradosFiltrados = prediosGeneradosFiltrados;
    }

    public CatPredio getPredioMatriz() {
        return predioMatriz;
    }

    public void setPredioMatriz(CatPredio predioMatriz) {
        this.predioMatriz = predioMatriz;
    }

    public Boolean getFichaMatriz() {
        return fichaMatriz;
    }

    public void setFichaMatriz(Boolean fichaMatriz) {
        this.fichaMatriz = fichaMatriz;
    }

    public BigDecimal getTotalAlicuotasComunal() {
        return totalAlicuotasComunal;
    }

    public void setTotalAlicuotasComunal(BigDecimal totalAlicuotasComunal) {
        this.totalAlicuotasComunal = totalAlicuotasComunal;
    }

    public CatPredioPropietario getPropietarioSeleccionado() {
        return propietarioSeleccionado;
    }

    public void setPropietarioSeleccionado(CatPredioPropietario propietarioSeleccionado) {
        this.propietarioSeleccionado = propietarioSeleccionado;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public CatEnteLazy getEntes() {
        return entes;
    }

    public void setEntes(CatEnteLazy entes) {
        this.entes = entes;
    }

    public CatEnte getEnteSeleccionado() {
        return enteSeleccionado;
    }

    public void setEnteSeleccionado(CatEnte enteSeleccionado) {
        this.enteSeleccionado = enteSeleccionado;
    }

    public CatPredioPropietario getProp() {
        return prop;
    }

    public void setProp(CatPredioPropietario prop) {
        this.prop = prop;
    }

    public CatPredioAlicuotaComponente getComponenteSeleccionado() {
        return componenteSeleccionado;
    }

    public void setComponenteSeleccionado(CatPredioAlicuotaComponente componenteSeleccionado) {
        this.componenteSeleccionado = componenteSeleccionado;
    }

    public List<CatPredioAlicuotaComponente> getComponenteEliminar() {
        return componenteEliminar;
    }

    public void setComponenteEliminar(List<CatPredioAlicuotaComponente> componenteEliminar) {
        this.componenteEliminar = componenteEliminar;
    }

    public UploadDocumento getDocumentoBean() {
        return documentoBean;
    }

    public void setDocumentoBean(UploadDocumento documentoBean) {
        this.documentoBean = documentoBean;
    }

    public OmegaUploader getFserv() {
        return fserv;
    }

    public void setFserv(OmegaUploader fserv) {
        this.fserv = fserv;
    }

    public Long getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(Long idDoc) {
        this.idDoc = idDoc;
    }

    public List<CatCanton> getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }

    public boolean isTieneEscritura() {
        return tieneEscritura;
    }

    public void setTieneEscritura(boolean tieneEscritura) {
        this.tieneEscritura = tieneEscritura;
    }

    public boolean isSkipGenerarPh() {
        return skipGenerarPh;
    }

    public void setSkipGenerarPh(boolean skipGenerarPh) {
        this.skipGenerarPh = skipGenerarPh;
    }

    public List<CtlgItem> getListado(String argumento) {
        HiberUtil.newTransaction();
        List<CtlgItem> ctlgItem = (List<CtlgItem>) manager.findAllEntCopy(Querys.getCtlgItemaASC, new String[]{"catalogo"}, new Object[]{argumento});
        return ctlgItem;
    }

    public Boolean getEsModificatoria() {
        return esModificatoria;
    }

    public void setEsModificatoria(Boolean esModificatoria) {
        this.esModificatoria = esModificatoria;
    }
}
