/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.bpm.models.CombinacionPH;
import com.origami.sgm.bpm.models.ModelPrediosPH;
import com.origami.sgm.bpm.models.ModelPropiedadHorizontal;
import com.origami.sgm.bpm.models.ModelPropietariosPredio;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
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
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 * Contiene todos los metodos necesarios para el proceso de PropiedadHorizontal
 *
 * @author Angel Navarro
 */
@Local
public interface PropiedadHorizontalServices {

    /**
     * Obtiene todos los registros de la tabla PeFirma que tengan estado "A".
     *
     * @return Lista de PeFirma
     */
    public List<PeFirma> getListPeFirma();

    /**
     * Busca en la tabla CatCanton por el Id que se le pasa por parametro.
     *
     * @param id Id de la Tabla CatCanton.
     * @return Entity CatCanton.
     */
    public CatCanton getCatCantonById(Long id);

    /**
     * Realiza el calculo de Avaluo de construcción, multiplicando la propiedad
     * areaEdificacion con 520.
     *
     * @param reporte entity HistoricoTramiteDet.
     * @return Avaluo de Construcción.
     */
    public BigDecimal calcularAvaluoConstruccion(HistoricoTramiteDet reporte);

    /**
     * Realiza el calculo de Avaluo de Propiedad, sumando el avaluoSolar con
     * avaluoConstruccion
     *
     * @param reporte Entity HistoricoTramiteDet
     * @return Valor de AvaluoPropiedad.
     */
    public BigDecimal calculoAvaluoPropiedad(HistoricoTramiteDet reporte);

    /**
     * Calcula en Valor de la Liquidación, primero divide la propiedad
     * baseCalculo para 100 despues el valor obtenido lo multiplica por la
     * propiedad avaluoEdificacion y lo retorna redondeado con dos decimales.
     *
     * @param reporte Entity HistoricoTramiteDet
     * @return Valor Liquidación .
     */
    public BigDecimal totalPagar(HistoricoTramiteDet reporte);

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
    public HistoricoTramiteDet guadarTasaLiquidacion(HistoricoTramiteDet reporte, HistoricoTramites ht, List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar);

    /**
     * Con los parametros recibidos crea un HistoricoReporteTramite y los envia
     * a persistrir
     *
     * @param numReporte Número de reporte si es nulo envia a generar uno nuevo.
     * @param ht HistoricoTramites.
     * @param nombreReporte Nombre del Reporte.
     * @param taskDefinitionKey Nombre de la Tarea.
     * @return HistoricoReporteTramite.
     */
    public HistoricoReporteTramite guardarHistoricoReporteTramite(BigInteger numReporte, HistoricoTramites ht, String nombreReporte, String taskDefinitionKey);

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
    public Observaciones guardarObservaciones(HistoricoTramites ht, String nameUser, String observaciones, String taskDefinitionKey);

    /**
     * Busca en la tabla MsgFormatoNotificacion por el id del campo tipo.
     *
     * @param id Id de la MsgTipoFormatoNotificacion.
     * @return Entity MsgFormatoNotificacion
     */
    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Long id);

    /**
     * Tabla Ya no usada ProcesoReporte
     *
     * @param idTramite Id de Tramite
     * @return ProcesoReporte.
     */
    public ProcesoReporte getProcesoReportes(Long idTramite);

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
    public HistoricoTramiteDet modificarTasaLiquidacion(HistoricoTramiteDet reporte, HistoricoTramites ht, List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar);

    /**
     * Busca en la tabla HistoricoTramiteDet por el id de la tabla
     * HistoricoTramites.
     *
     * @param idTramite Id de HistoricoTramites.
     * @return Entity HistoricoTramiteDet.
     */
    public HistoricoTramiteDet getHistoricoTramiteDetByTramite(Long idTramite);

    /**
     * Busca todos los registros que tengan el id de los roles pasados
     *
     * @param roles id de AclRol.
     * @return Lista de AclUser.
     */
    public List<AclUser> getTecnicosByRol(List<Long> roles);

    /**
     * Recibe la entity CatPredio y toma las propiedades "sector", "mz", "cdla",
     * "mzdiv", "solar", "div1", "div2", "div3", "div4", "div5", "div6", "div7",
     * "div8", "div9", para realizar la consulta en la tabla CatPredio.
     *
     * @param predio Entity CatPredio.
     * @return Lista de CatPredio.
     */
    public List<CatPredio> getCatPredioList(CatPredio predio);

    /**
     * Generar la combinaciones de la propiedad Vertical.
     *
     * @param phvInicial Valor inicial de la Propiedad Vertial.
     * @param phv Valor Final de la Propiedad Vertical.
     * @return lista de combinación.
     */
    public List<CombinacionPH> generarAlicuotas(short phvInicial, short phv);

    /**
     * Genera el número de Propiedad Horizontal que fueron ingresados para cada
     * uno de los Propiedades Verticales
     *
     * @param listPredio Lista de CatPredio
     * @param phhPhv Modelo de datos donde contiene dos campos phh y phv
     * @param phvPhhList Lista de alicuotas generadas
     * @param user Usuario Creador
     * @return Lista ModelPrediosPH
     */
    public List<ModelPrediosPH> generarPredios(List<CatPredio> listPredio, CombinacionPH phhPhv, List<CombinacionPH> phvPhhList, AclUser user);

    /**
     * Filta los registros de la tabla CtlgItem por el nombre del catalogo.
     *
     * @param prediopropietarioTipo nombre del catalogo.
     * @return Lista de CtlgItem.
     */
    public List<CtlgItem> getCtlgItem(String prediopropietarioTipo);

    /**
     * Busca en la tabla CatEnte y los filtra por los Map de parametros pasado.
     *
     * @param paramt Map de parametros.
     * @return Si existe retorna la entity CatEnte caso contrario null.
     */
    public CatEnte getCatEnteByParemt(Map paramt);

    /**
     * Busca todos los registro que contiene CatTenenciaItem y los ordena de
     * forma Asc.
     *
     * @return Lista de CatTenenciaItem.
     */
    public List<CatTenenciaItem> getCatTenenciaItemList();

    /**
     * Busca en la tabla CatEscritura si existe el registro retorna el objeto.
     *
     * @param paramt
     * @return Lista de CatEscritura.
     */
    public CatEscritura getCatEscrituraByNumPredio(Map paramt);

    /**
     * Busca todos los registro que contiene CatPropiedadItem y los ordena de
     * forma Asc.
     *
     * @return Lista de CatPropiedadItem.
     */
    public List<CatPropiedadItem> getCatPropiedadItemList();

    /**
     * Busca todos los registro que contiene CatCanton y los ordena de forma
     * Asc.
     *
     * @return Lista de CatCanton.
     */
    public List<CatCanton> getCatCantonList();

    /**
     * Busca todos los registro que contiene CatTiposDominio y los ordena de
     * forma Asc.
     *
     * @return Lista de CatTiposDominio.
     */
    public List<CatTiposDominio> getCatTiposDominioList();

    /**
     * Envia a persistir CatPredio y CatEscritura y les pone como estado "G"
     * para identificar que fue generado y despues que se completa la tarea se
     * actualiza el estado "A".
     *
     * @param alicuotasPredio Lista de ModelPrediosPH que contiene CatPredio,
     * CatEscritura, CatPredioS6, CatPredioS4.
     * @return Lista de ModelPrediosPH.
     */
    public List<ModelPrediosPH> guardarPrediosPropiedadHorizontal(List<ModelPrediosPH> alicuotasPredio);

    /**
     * Busca en CatPredio por el id si existe retorna la Entity.
     *
     * @param id Id de la tabla.
     * @return Entity CatPredio.
     */
    public CatPredio getCatPredioById(Long id);

    /**
     * Busca en CatEscritura por el id si existe retorna la Entity.
     *
     * @param idEscritura Id de la tabla.
     * @return CatEscritura
     */
    public CatEscritura getCatEscrituraById(Long idEscritura);

    /**
     * Recibe en modelo de datos ModelPropiedadHorizontal que contiene una lista
     * de ModelPrediosPH que tiene todos los predios, escrituras y prediosS6 que
     * fueron generados, recorre cada una de los persiste, ademas asocia cada
     * predio a los propietarios que fueron asociado.
     *
     * @param model ModelPropiedadHorizontal.
     * @return modelo de datos ModelPropiedadHorizontal.
     */
    public ModelPropiedadHorizontal guardarPrediosYEscrituras(ModelPropiedadHorizontal model);

    /**
     * Envia a Actualizar la entity HistoricoTramites.
     *
     * @param ht Entiy HistoricoTramites
     */
    public void actualizarHistoricoTramites(HistoricoTramites ht);

    /**
     * Envia a persistir primero CatEnte y despues EnteTelefono y EnteCorreo,
     * despues envia a persistir CatPredioPropietarios.
     *
     * @param propietarios modelo ModelPropietariosPredio a persistir.
     * @param predio CatPredio persistido.
     * @return ModelPropietariosPredio
     */
    public ModelPropietariosPredio guardarPropietariosModel(ModelPropietariosPredio propietarios, CatPredio predio);

    /**
     * Envia a persistir primero CatEnte y despues busca las Collection de
     * EnteTelefono y EnteCorreo si no estan vacios los envia a actualizar se
     * existe caso contrario a persistir.
     *
     * @param ente CatEnte a persistir o actualizar.
     * @return CatEnte.
     */
    public CatEnte guardarOActualizarEnteCorreosTlfns(CatEnte ente);

    /**
     * Metodos que Permiso de Contrucción.
     *
     * @return Metodos de PermisoConstruccionServices
     */
    public PermisoConstruccionServices getPermiso();

    /**
     * Metodos de Ingreso de ficha.
     *
     * @return Metodos de FichaIngresoNuevoServices
     */
    public FichaIngresoNuevoServices getFichaServices();

    /**
     * Metodos de inscripción Nueva.
     *
     * @return Metodos de InscripcionNuevaServices
     */
    public InscripcionNuevaServices getInscripcion();

    /**
     * Realiza la busqueda por el id de la tabla.
     *
     * @param l Primary key de la tabla.
     * @return Entity MatFormulaTramite si existe, caso contrario null.
     */
    public MatFormulaTramite getMatFormulaTramite(Long l);

    /**
     * Realiza la Actialización en la base de datos de la entity
     * HistoricoReporteTramite
     *
     * @param d Entity HistoricoReporteTramite.
     */
    public void actualizarHistoricoReporte(HistoricoReporteTramite d);

    /**
     * Realiza la busqueda en la tabla por el id.
     *
     * @param id Primary key de la tabla.
     * @return Entity CatParroquia si existe, caso contrario null.
     */
    public CatParroquia getCatParroquia(Long id);

    /**
     * Realiza la consulta por el id de la tabla.
     *
     * @param idHtd Id de la tabla.
     * @return Si existe {@link HistoricoTramiteDet} caso contrario null.
     */
    public HistoricoTramiteDet getHistoricoTramiteDetById(Long idHtd);

}
