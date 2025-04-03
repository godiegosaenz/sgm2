
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.models.CombinacionPH;
import com.origami.sgm.bpm.models.ModelPrediosPH;
import com.origami.sgm.bpm.models.ModelPropiedadHorizontal;
import com.origami.sgm.bpm.models.ModelPropietariosPredio;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatPropiedadItem;
import com.origami.sgm.entities.CatTenenciaItem;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.ProcesoReporte;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import util.Utils;

/**
 * ImplementaciÃƒÂ³n de los metodos del proceso de propiedadHorizontal
 *
 * @author Angel Navarro
 */
@Stateless(name = "propiedadHorizontal")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class PropiedadHorizontalEjb implements PropiedadHorizontalServices {

    @javax.inject.Inject
    protected Entitymanager manager;
    @javax.inject.Inject
    private SeqGenMan secuencias;
    @javax.inject.Inject
    protected NormasConstruccionServices normas;
    @javax.inject.Inject
    protected DivisionPredioServices divServices;
    @javax.inject.Inject
    private Entitymanager serv;;
    @javax.inject.Inject
    protected InscripcionNuevaServices inscripcion;
    @javax.inject.Inject
    public PermisoConstruccionServices permiso;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;
    @javax.inject.Inject
    private UserSession userSession;
    

    /**
     * Obtiene todos los registros de la tabla PeFirma que tengan estado "A".
     *
     * @return Lista de PeFirma
     */
    @Override
    public List<PeFirma> getListPeFirma() {
        return manager.findAll(Querys.getPeFirmas, new String[]{"estado"}, new Object[]{"A"});
    }

    /**
     * Busca en la tabla CatCanton por el Id que se le pasa por parametro.
     *
     * @param id Id de la Tabla CatCanton.
     * @return Entity CatCanton.
     */
    @Override
    public CatCanton getCatCantonById(Long id) {
        return manager.find(CatCanton.class, id);
    }

    /**
     * Metodos no usado esta en la tabla MatFormulaTramite con id 10.<br/>
     * Realiza el calculo de Avaluo de construcciÃ³n, multiplicando la propiedad
     * areaEdificacion con 520.
     *
     * @param reporte entity HistoricoTramiteDet.
     * @return Avaluo de ConstrucciÃ³n.
     */
    @Override
    public BigDecimal calcularAvaluoConstruccion(HistoricoTramiteDet reporte) {
        if (reporte.getAreaEdificacion() != null) {
            return reporte.getAreaEdificacion().multiply(new BigDecimal(520));
        }
        return null;
    }

    /**
     * Metodos no usado esta en la tabla MatFormulaTramite con id 10.<br/>
     *
     * Realiza el calculo de Avaluo de Propiedad, sumando el avaluoSolar con
     * avaluoConstruccion
     *
     * @param reporte Entity HistoricoTramiteDet
     * @return Valor de AvaluoPropiedad.
     */
    @Override
    public BigDecimal calculoAvaluoPropiedad(HistoricoTramiteDet reporte) {
        if (reporte.getAvaluoConstruccion() != null && reporte.getAvaluoSolar() != null) {
            return reporte.getAvaluoSolar().add(reporte.getAvaluoConstruccion());
        }
        return null;
    }

    /**
     * Metodos no usado esta en la tabla MatFormulaTramite con id 10.<br/>
     * Calcula en Valor de la LiquidaciÃ³n, primero divide la propiedad
     * baseCalculo para 100 despues el valor obtenido lo multiplica por la
     * propiedad avaluoEdificacion y lo retorna redondeado con dos decimales.
     *
     * @param reporte Entity HistoricoTramiteDet
     * @return Valor LiquidaciÃ³n .
     */
    @Override
    public BigDecimal totalPagar(HistoricoTramiteDet reporte) {
        if (reporte.getAvaluoEdificacion() != null) {
            BigDecimal temp = reporte.getAvaluoEdificacion().multiply(reporte.getBaseCalculo().divide(new BigDecimal(100)));
            return Utils.bigdecimalTo2Decimals(temp);
        }
        return null;
    }

    /**
     * Envia a persistir los propietarios nuevos y los existentes los envia a
     * actualizar e inactiva los eliminados, despues persiste
     * HistoricoTramiteDet y actualiza HistoricoTramites.
     *
     * @param reporte HistoricoTramiteDet.
     * @param ht HistoricoTramites.
     * @param listaPropietarios Lista de CatPredioPropietario a guardar o
     * actualizar.
     * @param listaPropietariosEliminar Lista de CatPredioPropietario a
     * eliminar.
     * @return HistoricoTramiteDet.
     */
    @Override
    public HistoricoTramiteDet guadarTasaLiquidacion(HistoricoTramiteDet reporte, HistoricoTramites ht, List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar) {
        normas.guardarOActualizarCatPredioPropietario(listaPropietarios, listaPropietariosEliminar, reporte.getPredio());

        reporte = (HistoricoTramiteDet) manager.persist(reporte);
        if (reporte != null) {
            normas.guardarHistoricoTranites(ht);
        }
        return reporte;
    }

    /**
     * Con los parametros recibidos crea un HistoricoReporteTramite y los envia
     * a persistrir
     *
     * @param numReporte NÃºmero de reporte si es nulo envia a generar uno nuevo.
     * @param ht HistoricoTramites.
     * @param nombreReporte Nombre del Reporte.
     * @param taskDefinitionKey Nombre de la Tarea.
     * @return HistoricoReporteTramite.
     */
    @Override
    public HistoricoReporteTramite guardarHistoricoReporteTramite(BigInteger numReporte, HistoricoTramites ht, String nombreReporte, String taskDefinitionKey) {
        Calendar cl = Calendar.getInstance();
        if (numReporte == null) {
            numReporte = secuencias.getSequence(Querys.getGenSecByPeriodo, new String[]{"periodo"}, new Object[]{cl.get(Calendar.YEAR)});
        }
        HistoricoReporteTramite vd;
        vd = new HistoricoReporteTramite();
        vd.setCodValidacion(numReporte + (ht.getIdProceso() == null? ht.getIdProcesoTemp() : ht.getIdProceso()));
        vd.setEstado(true);
        vd.setFecCre(cl.getTime());
        vd.setNombreReporte(nombreReporte + ht.getSolicitante().getCiRuc());
        vd.setNombreTarea(taskDefinitionKey);
        vd.setProceso((ht.getIdProceso() == null? ht.getIdProcesoTemp() : ht.getIdProceso()));
        vd.setSecuencia(numReporte);
        vd.setTramite(ht);
        return divServices.guardarHistoricoReporteTramite(vd);
    }

    /**
     * Con los parametros recibidos crea una un nuevo objecto de Observaciones y
     * los envia a persitir.
     *
     * @param ht HistoricoTramites
     * @param nameUser Nombre del Usuario Creador.
     * @param observaciones Observaciones realizadas.
     * @param taskDefinitionKey Nombre de la Tarea.
     * @return entity Observaciones.
     */
    @Override
    public Observaciones guardarObservaciones(HistoricoTramites ht, String nameUser, String observaciones, String taskDefinitionKey) {
        try {
            Observaciones observ = new Observaciones();
            observ.setEstado(true);
            observ.setFecCre(new Date());
            observ.setTarea(taskDefinitionKey);
            observ.setIdTramite(ht);
            observ.setUserCre(nameUser);
            observ.setObservacion(observaciones);
            return (Observaciones) manager.persist(observ);
        } catch (Exception e) {
            Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Busca en la tabla MsgFormatoNotificacion por el id del campo tipo.
     *
     * @param id Id de la MsgTipoFormatoNotificacion.
     * @return Entity MsgFormatoNotificacion
     */
    @Override
    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Long id) {
        return normas.getMsgFormatoNotificacionByTipo(id);
    }

    /**
     * Tabla Ya no usada ProcesoReporte
     *
     * @param idTramite Id de Tramite
     * @return ProcesoReporte.
     */
    @Override
    public ProcesoReporte getProcesoReportes(Long idTramite) {
        if (idTramite != null) {
            return (ProcesoReporte) manager.find(Querys.getProcesoReporteByTramite, new String[]{"numTramite"}, new Object[]{idTramite.toString()});
        }
        return null;
    }

    /**
     * Envia a persistir los propietarios nuevos y los existentes los envia a
     * actualizar e inactiva los eliminados, despues actualiza
     * HistoricoTramiteDet y actualiza HistoricoTramites.
     *
     * @param reporte HistoricoTramiteDet.
     * @param ht HistoricoTramites.
     * @param listaPropietarios Lista de CatPredioPropietario a guardar o
     * actualizar.
     * @param listaPropietariosEliminar Lista de CatPredioPropietario a
     * eliminar.
     * @return HistoricoTramiteDet.
     */
    @Override
    public HistoricoTramiteDet modificarTasaLiquidacion(HistoricoTramiteDet reporte, HistoricoTramites ht, List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar) {
        normas.guardarOActualizarCatPredioPropietario(listaPropietarios, listaPropietariosEliminar, reporte.getPredio());
        HistoricoTramiteDet temp = null;
        if (manager.update(reporte)) {
            normas.guardarHistoricoTranites(ht);
            temp = manager.find(HistoricoTramiteDet.class, reporte.getId());
        }
        return temp;
    }

    /**
     * Busca en la tabla HistoricoTramiteDet por el id de la tabla
     * HistoricoTramites.
     *
     * @param idTramite Id de HistoricoTramites.
     * @return Entity HistoricoTramiteDet.
     */
    @Override
    public HistoricoTramiteDet getHistoricoTramiteDetByTramite(Long idTramite) {
        return (HistoricoTramiteDet) manager.find(Querys.getHistoricoTramiteDetByTramite, new String[]{"numTramite"}, new Object[]{idTramite});
    }

    /**
     * Busca todos los registros que tengan el id de los roles pasados
     *
     * @param roles id de AclRol.
     * @return Lista de AclUser.
     */
    @Override
    public List<AclUser> getTecnicosByRol(List<Long> roles) {
        return serv.getTecnicosByRol(roles);
    }

    /**
     * Recibe la entity CatPredio y toma las propiedades "sector", "mz", "cdla",
     * "mzdiv", "solar", "div1", "div2", "div3", "div4", "div5", "div6", "div7",
     * "div8", "div9", para realizar la consulta en la tabla CatPredio.
     *
     * @param predio Entity CatPredio.
     * @return Lista de CatPredio.
     */
    @Override
    public List<CatPredio> getCatPredioList(CatPredio predio) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("sector", predio.getSector());
        paramt.put("mz", predio.getMz());
        paramt.put("cdla", predio.getCdla());
        paramt.put("mzdiv", predio.getMzdiv());
        paramt.put("solar", predio.getSolar());
        paramt.put("div1", predio.getDiv1());
        paramt.put("div2", predio.getDiv2());
        paramt.put("div3", predio.getDiv3());
        paramt.put("div4", predio.getDiv4());
        paramt.put("div5", predio.getDiv5());
        paramt.put("div6", predio.getDiv6());
        paramt.put("div7", predio.getDiv7());
        paramt.put("div8", predio.getDiv8());
        paramt.put("div9", predio.getDiv9());

        return manager.findObjectByParameterList(CatPredio.class, paramt);
    }

    /**
     * Generar la combinaciones de la propiedad Vertical.
     *
     * @param phvInicial Valor inicial de la Propiedad Vertial.
     * @param phv Valor Final de la Propiedad Vertical.
     * @return lista de combinaciÃƒÂ³n.
     */
    @Override
    public List<CombinacionPH> generarAlicuotas(short phvInicial, short phv) {
        List<CombinacionPH> list = null;
        if (phv >= 1) {
            list = new ArrayList<>();
            CombinacionPH p;
            for (int i = phvInicial; i <= phv; i++) {
                p = new CombinacionPH();
                p.setPhv((short) i);
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Genera el nÃƒÂºmero de Propiedad Horizontal que fueron ingresados para cada
     * uno de los Propiedades Verticales
     *
     * @param listPredio Lista de CatPredio
     * @param phhPhv Modelo de datos donde contiene dos campos phh y phv
     * @param phvPhhList Lista de alicuotas generadas
     * @param user Usuario Creador
     * @return Lista ModelPrediosPH
     */
    @Override
    public List<ModelPrediosPH> generarPredios(List<CatPredio> listPredio, CombinacionPH phhPhv, List<CombinacionPH> phvPhhList, AclUser user) {
        List<ModelPrediosPH> list = new ArrayList<>();
        ModelPrediosPH model = null;
        for (CombinacionPH p : phvPhhList) {
            for (int j = 1; j <= p.getPhh(); j++) {
                try {
                    model = new ModelPrediosPH();
                    model.getPredio().setAreaSolar(listPredio.get(0).getAreaSolar());
                    model.getPredio().setAvaluoConstruccion(listPredio.get(0).getAvaluoConstruccion());
                    model.getPredio().setAvaluoMunicipal(listPredio.get(0).getAvaluoMunicipal());
                    model.getPredio().setAvaluoSolar(listPredio.get(0).getAvaluoSolar());
                    model.getPredio().setCalle(listPredio.get(0).getCalle());
                    model.getPredio().setCdla(listPredio.get(0).getCdla());
                    model.getPredio().setCiudadela(listPredio.get(0).getCiudadela());
                    model.getPredio().setDiv1(listPredio.get(0).getDiv1());
                    model.getPredio().setDiv2(listPredio.get(0).getDiv2());
                    model.getPredio().setDiv3(listPredio.get(0).getDiv3());
                    model.getPredio().setDiv4(listPredio.get(0).getDiv4());
                    model.getPredio().setDiv5(listPredio.get(0).getDiv5());
                    model.getPredio().setDiv6(listPredio.get(0).getDiv6());
                    model.getPredio().setDiv7(listPredio.get(0).getDiv7());
                    model.getPredio().setDiv8(listPredio.get(0).getDiv8());
                    model.getPredio().setDiv9(listPredio.get(0).getDiv9());
                    model.getPredio().setEstado("G");
                    model.getPredio().setFormaSolar(listPredio.get(0).getFormaSolar());
                    model.getPredio().setInstCreacion(new Date());
                    model.getPredio().setMz(listPredio.get(0).getMz());
                    model.getPredio().setMzdiv(listPredio.get(0).getMzdiv());
                    model.getPredio().setNomCompPago(listPredio.get(0).getNomCompPago());
                    model.getPredio().setNombreUrb(listPredio.get(0).getNombreUrb());
                    model.getPredio().setPropiedadHorizontal(Boolean.TRUE);
                    model.getPredio().setPhh((short) j);
                    model.getPredio().setPhv(p.getPhv());
                    model.getPredio().setPredioRaiz(BigInteger.valueOf(listPredio.get(0).getId()));
                    model.getPredio().setPropiedad(listPredio.get(0).getPropiedad());
                    model.getPredio().setSector(listPredio.get(0).getSector());
                    model.getPredio().setSolar(listPredio.get(0).getSolar());
                    model.getPredio().setSoportaHipoteca(listPredio.get(0).getSoportaHipoteca());
                    model.getPredio().setTenencia(listPredio.get(0).getTenencia());
                    model.getPredio().setTipoConjunto(listPredio.get(0).getTipoConjunto());
                    model.getPredio().setUrbMz(listPredio.get(0).getUrbMz());
                    model.getPredio().setUrbSec(listPredio.get(0).getUrbSec());
                    model.getPredio().setUrbSecnew(listPredio.get(0).getUrbSecnew());
                    model.getPredio().setUrbSolarnew(listPredio.get(0).getUrbSolarnew());
                    model.getPredio().setUsuarioCreador(user);

                    model.setImage(Boolean.FALSE);

                } catch (Exception e) {
                    Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
                }

                list.add(model);
            }
        }
        return list;
    }

    /**
     * Filta los registros de la tabla CtlgItem por el nombre del catalogo.
     *
     * @param prediopropietarioTipo nombre del catalogo.
     * @return Lista de CtlgItem.
     */
    @Override
    public List<CtlgItem> getCtlgItem(String prediopropietarioTipo) {
        return inscripcion.lisCtlgItems(prediopropietarioTipo);
    }

    /**
     * Busca en la tabla CatEnte y los filtra por los Map de parametros pasado.
     *
     * @param paramt Map de parametros.
     * @return Si existe retorna la entity CatEnte caso contrario null.
     */
    @Override
    public CatEnte getCatEnteByParemt(Map paramt) {
        return (CatEnte) manager.findObjectByParameter(CatEnte.class, paramt);
    }

    /**
     * Busca todos los registro que contiene CatTenenciaItem y los ordena de
     * forma Asc.
     *
     * @return Lista de CatTenenciaItem.
     */
    @Override
    public List<CatTenenciaItem> getCatTenenciaItemList() {
        return manager.findAllObjectOrder(CatTenenciaItem.class, new String[]{"nombre"}, null);
    }

    /**
     * Busca en la tabla CatEscritura si existe el registro retorna el objeto.
     *
     * @param paramt
     * @return Lista de CatEscritura.
     */
    @Override
    public CatEscritura getCatEscrituraByNumPredio(Map paramt) {
        return (CatEscritura) manager.findObjectByParameter(CatEscritura.class, paramt);
    }

    /**
     * Busca todos los registro que contiene CatPropiedadItem y los ordena de
     * forma Asc.
     *
     * @return Lista de CatPropiedadItem.
     */
    @Override
    public List<CatPropiedadItem> getCatPropiedadItemList() {
        return manager.findAllObjectOrder(CatPropiedadItem.class, new String[]{"nombre"}, true);
    }

    /**
     * Busca todos los registro que contiene CatCanton y los ordena de forma
     * Asc.
     *
     * @return Lista de CatCanton.
     */
    @Override
    public List<CatCanton> getCatCantonList() {
        return manager.findAllObjectOrder(CatCanton.class, new String[]{"nombre"}, true);
    }

    /**
     * Busca todos los registro que contiene CatTiposDominio y los ordena de
     * forma Asc.
     *
     * @return Lista de CatTiposDominio.
     */
    @Override
    public List<CatTiposDominio> getCatTiposDominioList() {
        return manager.findAllObjectOrder(CatTiposDominio.class, new String[]{"nombre"}, Boolean.TRUE);
    }

    /**
     * Envia a persistir CatPredio y CatEscritura y les pone como estado "G"
     * para identificar que fue generado y despues que se completa la tarea se
     * actualiza el estado "A".
     *
     * @param alicuotasPredio Lista de ModelPrediosPH que contiene CatPredio,
     * CatEscritura, CatPredioS6, CatPredioS4.
     * @return Lista de ModelPrediosPH.
     */
    @Override
    public List<ModelPrediosPH> guardarPrediosPropiedadHorizontal(List<ModelPrediosPH> alicuotasPredio) {
        for (ModelPrediosPH ali : alicuotasPredio) {
            ali.getPredio().setEstado("G");
            ali.setPredio(secuencias.generarNumPredioAndGuardarCatPredio(ali.getPredio()));

            ali.getPredioS6().setPredio(ali.getPredio());
            ali.getPredioS6().setAlicuota(BigDecimal.ZERO);
            
            ali.getPredioS6().setTieneAceras(Boolean.FALSE);
            ali.getPredioS6().setTieneBordillo(Boolean.FALSE);
            ali.getPredioS6().setTieneCunetas(Boolean.FALSE);
            ali.setPredioS6((CatPredioS6) manager.persist(ali.getPredioS6()));
        }
        alicuotasPredio.size();
        return alicuotasPredio;
    }

    /**
     * Busca en CatPredio por el id si existe retorna la Entity.
     *
     * @param id Id de la tabla.
     * @return Entity CatPredio.
     */
    @Override
    public CatPredio getCatPredioById(Long id) {
        return manager.find(CatPredio.class, id);
    }

    /**
     * Busca en CatEscritura por el id si existe retorna la Entity.
     *
     * @param idEscritura Id de la tabla.
     * @return CatEscritura
     */
    @Override
    public CatEscritura getCatEscrituraById(Long idEscritura) {
        return manager.find(CatEscritura.class, idEscritura);
    }

    /**
     * Recibe en modelo de datos ModelPropiedadHorizontal que contiene una lista
     * de ModelPrediosPH que tiene todos los predios, escrituras y prediosS6 que
     * fueron generados, recorre cada una de los persiste, ademas asocia cada
     * predio a los propietarios que fueron asociado.
     *
     * @param model ModelPropiedadHorizontal.
     * @return modelo de datos ModelPropiedadHorizontal.
     */
    @Override
    public ModelPropiedadHorizontal guardarPrediosYEscrituras(ModelPropiedadHorizontal model) {
        for (ModelPrediosPH predios : model.getAlicuotasPredio()) {
            try {
                predios.getPredio().setEstado("A");
                manager.persist(predios.getPredio());

                for (ModelPropietariosPredio propietarios : predios.getListaPropietarios()) {
                    propietarios = guardarPropietariosModel(propietarios, predios.getPredio());
                }

                manager.persist(predios.getPredioS6());

            } catch (Exception e) {
                Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
            }

        }
        return model;
    }

    /**
     * Envia a Actualizar la entity HistoricoTramites.
     *
     * @param ht Entiy HistoricoTramites
     */
    @Override
    public void actualizarHistoricoTramites(HistoricoTramites ht) {
        divServices.actualizarHistoricoTramite(ht);
    }

    /**
     * Envia a persistir primero CatEnte y despues EnteTelefono y EnteCorreo,
     * despues envia a persistir CatPredioPropietarios.
     *
     * @param propietarios modelo ModelPropietariosPredio a persistir.
     * @param predio CatPredio persistido.
     * @return ModelPropietariosPredio
     */
    @Override
    public ModelPropietariosPredio guardarPropietariosModel(ModelPropietariosPredio propietarios, CatPredio predio) {
        try {
            propietarios.setEnte(guardarOActualizarEnteCorreosTlfns(propietarios.getEnte()));

            propietarios.setEnte(propietarios.getEnte());
            propietarios.getPropietarios().setEstado("A");
            propietarios.setPropietarios((CatPredioPropietario) manager.persist(propietarios.getPropietarios()));
        } catch (Exception e) {
            Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
        }

        return propietarios;

    }

    /**
     * Envia a persistir primero CatEnte y despues busca las Collection de
     * EnteTelefono y EnteCorreo si no estan vacios los envia a actualizar se
     * existe caso contrario a persistir.
     *
     * @param ente CatEnte a persistir o actualizar.
     * @return CatEnte.
     */
    @Override
    public CatEnte guardarOActualizarEnteCorreosTlfns(CatEnte ente) {
        try {
            if (ente.getId() != null) {
                ente.setUserMod(userSession.getName_user());
                ente.setFechaMod(new Date());
                manager.persist(ente);
                ente = manager.find(CatEnte.class, ente.getId());
            } else {
                ente.setEstado("A");
                ente.setFechaCre(new Date());
                ente = (CatEnte) manager.persist(ente);
            }
        } catch (Exception e) {
            Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return ente;
    }

    /**
     * Metodos que Permiso de ContrucciÃ³n.
     *
     * @return Metodos de PermisoConstruccionServices
     */
    @Override
    public PermisoConstruccionServices getPermiso() {
        return permiso;
    }

    /**
     * Metodos de Ingreso de ficha.
     *
     * @return Metodos de FichaIngresoNuevoServices
     */
    @Override
    public FichaIngresoNuevoServices getFichaServices() {
        return fichaServices;
    }

    /**
     * Metodos de inscripciÃ³n Nueva.
     *
     * @return Metodos de InscripcionNuevaServices
     */
    @Override
    public InscripcionNuevaServices getInscripcion() {
        return inscripcion;
    }

    @Override
    public MatFormulaTramite getMatFormulaTramite(Long l) {
        return manager.find(MatFormulaTramite.class, l);
    }

    @Override
    public void actualizarHistoricoReporte(HistoricoReporteTramite d) {
        try {
            manager.update(d);
        } catch (Exception e) {
            Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public CatParroquia getCatParroquia(Long id) {
        try {
            return manager.find(CatParroquia.class, id);
        } catch (Exception e) {
            Logger.getLogger(PropiedadHorizontalEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public HistoricoTramiteDet getHistoricoTramiteDetById(Long idHtd) {
        return manager.find(HistoricoTramiteDet.class, idHtd);
    }
}
