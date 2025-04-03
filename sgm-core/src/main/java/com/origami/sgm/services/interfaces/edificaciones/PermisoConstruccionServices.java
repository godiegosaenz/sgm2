/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Angel Navarro
 */
@Local
public interface PermisoConstruccionServices {

    /**
     * Busca en la tabla GeTipoTramite por el id
     *
     * @param id de GeTipoTramite
     * @return GeTipoTramite
     */
    public GeTipoTramite getGeTipoTramiteById(Long id);

    /**
     * Busca en PeTipoPermiso que tengan los códigos 'AR','CN','RN','RM','RP'
     *
     * @return Lista PeTipoPermiso
     */
    public List<PeTipoPermiso> getPeTipoPermisoByCodigo();

    /**
     * Busca todos los registros de la tablas CatEdfCategProp
     *
     * @return Lista CatEdfCategProp
     */
    public List<CatEdfCategProp> getCatEdfCategPropList();

    /**
     * Busca el la tabla CatPredio por el código predial
     *
     * @param sector Sector
     * @param mz Manzana
     * @param cdla Ciudadela
     * @param mzdiv División de Manzana
     * @param solar Solar
     * @param div1 División 1
     * @param div2 División 2
     * @param div3 División 3
     * @param div4 División 4
     * @param div5 División 5
     * @param div6 División 6
     * @param div7 División 7
     * @param div8 División 8
     * @param div9 División 9
     * @param phh Propiedad Hizontal
     * @param phv Propiedad Vertical
     * @return CatPredio
     */
    public CatPredio getCatPredioByCodigoPredio(Short zona, short sector, short mz, short solar);

    /**
     * Busca en la tabla CatEnte por el número de cédula y si es persona natural
     * o jurídica
     *
     * @param ciRuc Cédula O RUC
     * @param esPersona si es persona true, Persona Jurídica false
     * @return CatEnte
     */
    public CatEnte getCatEnteByCiRucByEsPersona(String ciRuc, boolean esPersona);

    /**
     * Busca en la tabla CatEdfProp por el id de CatEdfCategProp y retorna una
     * lista CatEdfProp
     *
     * @param id Entity CatEdfCategProp
     * @return Lista CatEdfProp
     */
    public List<CatEdfProp> getCatEdfPropList(Long id);

    /**
     * Verifica si el id es nulo para enviar a guardar caso contario se
     * actualiza
     *
     * @param listaPropietarios Lista de CatPredioPropietario a Guardar o
     * actualizar
     */
    public void guardarOActualizarCatPredioPropietario(List<CatPredioPropietario> listaPropietarios);

    /**
     * Busca el tabla PeTipoPermiso por el nombre
     *
     * @param tipoTramiteNombre Nombre del PeTipoPermiso
     * @return PeTipoPermiso
     */
    public PeTipoPermiso getPeTipoPermisoByDesc(String tipoTramiteNombre);

    /**
     * Obtiene todo los registro que tenga la PeTipoPermiso
     *
     * @return Lista PeTipoPermiso
     */
    public List<PeTipoPermiso> getPeTipoPermisoList();

    /**
     * Genera la tasa de liquidación, obtiene los valores de de revisión e
     * inspección, se realiza el calculo de impuesto y despues envia a guardar
     * PePermiso
     *
     * @param permisoNuevo PePermiso
     * @param formulas Formulas para realizar los calculos de liquidacion.
     * @return PePermiso
     */
    public PePermiso guardarPePermiso(PePermiso permisoNuevo, MatFormulaTramite formulas);
    
    public PePermiso guardarPePermisoInspeccion(PePermiso permisoNuevo, MatFormulaTramite formulas);

    /**
     * Envia a guardar PePermisoCabEdificacion y por cada registro busca los
     * detalles PeDetallePermiso de viene en la en la collection de
     * PePermisoCabEdificacion y las ve guardando
     *
     * @param p PePermiso
     * @param DetallesEdific PePermisoCabEdificacion
     * @param permisoSelect PePermisoCabEdificacion contiene la la Collection de
     * PeDetallePermiso
     */
    public void guardarPePermisoCabEdificacionAndPeDetallePermiso(PePermiso p, List<PePermisoCabEdificacion> DetallesEdific, PePermisoCabEdificacion permisoSelect);

    /**
     * Busca el la tabla AclUser por el User
     *
     * @param userDireccion Nombre de User
     * @return AclUser
     */
    public AclUser getAclUserByUser(String userDireccion);

    /**
     * Envia actualiza la tabla PePermiso
     *
     * @param permisoNuevo PePermiso
     * @return True si fue actualizada Caso contrario false
     */
    public boolean actualizarPePermiso(PePermiso permisoNuevo);

    /**
     * consulta PePermiso la secuencia del reporte por año
     *
     * @param anio Año del tramite
     * @return La Secuencia del reporte
     */
    public BigInteger getSecuenciaNumReporte(Short anio);

    /**
     * Obtiene el id para la tabla HistoricoTramites
     *
     * @return Id
     */
    public Long generarIdTramite();

    /**
     * Busca en HistoricoTramites por el campo id
     *
     * @param id Campo id de la tabla HistoricoTramites
     * @return HistoricoTramites
     */
    public HistoricoTramites getHistoricoTramiteById(Long id);

    /**
     * Enivia a peristir la entity HistoricoTramiteDet
     *
     * @param htd entity HistoricoTramiteDet
     * @return HistoricoTramiteDet persistida
     */
    public HistoricoTramiteDet guardarHistoricoTramiteDet(HistoricoTramiteDet htd);

    /**
     * Obtiene la lista de parametros por el id de GeTipoTramite
     *
     * @param tipoTramite id de GeTipoTramite
     * @return Lista ParametrosDisparador
     */
    public List<ParametrosDisparador> getParametroDisparadorByTipoTramite(Long tipoTramite);

    /**
     * Envia actualizar HistoricoTramites
     *
     * @param ht HistoricoTramites
     */
    public void actualizarHistoricoTramites(HistoricoTramites ht);

    /**
     * Consulta en la table RT_LIQUIDACION
     *
     * @param tipoTramite Id de GeTipoTramite
     * @param ht
     * @return Estado de la liquidaciÃ³n P,A,I
     */
    public String consultaPagoLiquidacion(int tipoTramite, HistoricoTramites ht);
    
    
    public Boolean consultaPagoLiquidacion(HistoricoTramites ht);
    
    /**
     * Envia a Guardar en la tabla Observaciones
     *
     * @param obs Observaciones
     * @return Observaciones
     */
    public Observaciones guardarObservacion(Observaciones obs);

    /**
     * realiza los cambios en CatPredioPropietario, primero envia actualizar o
     * guardar la lista de propietarios, luego le cambia de estado a los
     * propietarios de son eliminados.
     * <p/>
     * Se genera los valores para el avaluo de liquidacion, impuesto, impeción,
     * revision, no adeudar y la linea de fabrica, despues envia a guadar en
     * PePermisoCabEdificacion las edificaciones del predio que son nuevas y
     * actualiza las que existen, seguido guardar o actualiza las
     * especificaciones de cada una de la edificaciones en la tabla
     * PeDetallePermiso.
     * <p/>
     * Despues envia a eliminar las edificaciones que son eliminadas y las
     * especificaciones de cada edificación, por ultimo actualiza PePermiso.
     *
     * @param listaPropietariosEliminar lista CatPredioPropietario a eliminar
     * @param listaPropietarios CatPredioPropietario a guardar a actualizar
     * @param permisoNuevo PePermiso
     * @param DetallesEdific PePermisoCabEdificacion a Guardar o actualizar,
     * tambien contiene la coleccion de PeDetallePermiso de cada una de
     * edificaciones del predio.
     * @param detallesEdificEliminar PePermisoCabEdificacion a eliminar
     * @param peDetallePermisoEliminar PeDetallePermiso a eliminar
     * @param formulas formulas para realizar calculos
     * @return PePermiso
     */
    public PePermiso modificarLiquidacion(List<CatPredioPropietario> listaPropietariosEliminar, List<CatPredioPropietario> listaPropietarios, PePermiso permisoNuevo, List<PePermisoCabEdificacion> DetallesEdific, List<PePermisoCabEdificacion> detallesEdificEliminar, List<PeDetallePermiso> peDetallePermisoEliminar, MatFormulaTramite formulas);

    /**
     * Busca en la tabla PeTipoPermiso por el codigo Ejemplo "IM"
     *
     * @param codigo Codigo de la columna codigo
     * @return PeTipoPermiso
     */
    public PeTipoPermiso getTipoPermiso(String codigo);

    /**
     * Realiza el calculo del valor de LiquidaciÃ³n y envia a actualizar
     * HistoricoTramites
     *
     * @param avaluoLiquidacion El valor de la liquidacion.
     * @param ht HistoricoTramites
     */
    public void actualizarHistoricoTramitesAndValorLiquidacion(BigDecimal avaluoLiquidacion, HistoricoTramites ht);

    /**
     * Busca en la tabla por el número de tramite
     *
     * @param numTramite Número de Tramite
     * @return PePermiso
     */
    public PePermiso getPePermisoByNumTramite(String numTramite);

    /**
     * Consulta en HistoricoReporteTramites por el nombre de tarea y por el id
     * del proceso y cambia el estado a false y envia a actualizar
     * HistoricoReporteTramites
     *
     * @param nombreTarea Nombre de tarea
     * @param idPreoceso Id de Proceso
     */
    public void actualizarHistoricoReporteTramitesByTaskDef(String nombreTarea, String idPreoceso);

    /**
     * Busca en la tabla CatPredio por el código predio Y el estado
     *
     * @param provincia
     * @param canton
     * @param parroquia
     * @param zona
     * @param sector
     * @param mz
     * @param estado
     * @param lote
     * @param piso
     * @param unidad
     * @param bloque
     * @return CatPredio
     */
    public CatPredio getCatPredioByCodigoPredio(short provincia, short canton, short parroquia, short zona, short sector, short mz, short lote, short bloque, short piso, short unidad, String estado);

    /**
     * Busca en la Tabla PePermiso por el id de la tabla
     *
     * @param idPermiso id de la tabla PePermiso
     * @return PePermiso
     */
    public PePermiso getPePermisoById(Long idPermiso);

    /**
     * Busca en la tabla por HistoricoReporteTramite por el nombre de tarea el
     * id de proceso y el estado si existe retorna la entiti caso contrario
     * false
     *
     * @param idProceso Id de proceso
     * @param estado true o false
     * @return HistoricoReporteTramite
     */
    public HistoricoReporteTramite getHistoricoTramiteDet(String idProceso, Boolean estado);

    /**
     * Consulta en la tabla HistoricoTramiteDet por id de HistoricoTramite
     *
     * @param idTramite Id de la tabla HistoricoTramite
     * @return HistoricoTramiteDet
     */
    public HistoricoTramiteDet getHistoricoTramiteDetByTramite(Long idTramite);

    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Map paramts);

    /**
     * Contiene todos los metodos de ingreso de ficha.
     *
     * @return Todos los metodos FichaIngresoNuevoServices
     */
    public FichaIngresoNuevoServices getFichaServices();

    /**
     * Busca en la tabla MatFormulaTramite por el id de GeTipoTramite
     *
     * @param l id de GeTipoTramite
     * @return MatFormulaTramite
     */
    public MatFormulaTramite getMatFormulaTramite(Long l);

    /**
     * Busca en AclUser por el id.
     *
     * @param id Id de la tabla.
     * @return AclUser.
     */
    public AclUser getAclUserById(Long id);

    /**
     * Obtiene CatPredio Por el Id.
     *
     * @param id id de la tabla CatPredio
     * @return CatPredio
     */
    public CatPredio getCatPredioById(Long id);

    /**
     * Le cambia a false el valor del campo estado y realiza la actualización en
     * la base de datos.
     *
     * @param historicoReporteTramiteCollection Lista de entity
     * HistoricoReporteTramite.
     */
    public void actualizarHistoricoReporteTramites(Collection<HistoricoReporteTramite> historicoReporteTramiteCollection);

    /**
     * Realiza la busqueda en PePermiso por el campo idPredio que es wl id de
     * CatPredio.
     *
     * @param id Id del CatPredio
     * @return PePermiso Entity PePermiso si existe en la base de datos caso
     * contrario null.
     */
    public PePermiso getPePermisoByIdPredio(Long id);

    /**
     * Busca todas la escrituras que son parte de un predio.
     *
     * @param id Del predio
     * @return Lista de CatEscritura si existen, caso contrario null.
     */
    public List<CatEscritura> getCatEscrituraByPredioList(Long id);

    /**
     * Metodos de Normas de construcción.
     *
     * @return NormasConstruccionServices
     */
    public NormasConstruccionServices getNormasConstruccion();

    /**
     * Envia a actualizar el campo estado de CatPrediPropitarios de lospredios
     * que se envian a eliminar y los nuevos los envia a persistir en la base de
     * datos despues llena todos los datos de la entity Observaciones para
     * enviarlos a persistir en la base de datos.
     *
     * @param listaPropietariosEliminar Lista de propietarios a eliminar
     * @param listaPropietarios Lista de propietarios a persistir.
     * @param histTramite Entity HistoricoTramites
     * @param name_user Nombre de Usuario ejemplo: "jmiguel"
     * @param observacion Observaciones de la tarea.
     * @param nameTask Nombre de tarea.
     * @return Entity Observaciones persistida si no hubo error en la
     * transacción caso contrario null.
     */
    public Observaciones guardarActualizarObservacionProp(List<CatPredioPropietario> listaPropietariosEliminar, List<CatPredioPropietario> listaPropietarios, HistoricoTramites histTramite, String name_user, String observacion, String nameTask);

    /**
     * Busca en la tabla {@link CatPredio} por el id de la ciudadela, Manzana y 
     * Solar.
     * 
     * @param id Id de entity {@link CatCiudadela}
     * @param mzUrb Manzana 
     * @param solarUrb Solar
     * @return Entity {@link CatCiudadela} si existe caso contrario null.
     */
    public CatPredio getCatPredioByCiudadelaMzSolar(Long id, String mzUrb, String solarUrb);

    /**
     * Busca en la tabla {@link HistoricoTramites} por el id de la urbanizacion, Manzana y 
     * Solar.
     * 
     * @param id Id de entity {@link CatCiudadela}
     * @param mzUrb Manzana 
     * @param solarUrb Solar
     * @return 1 si ya existe un tramite con la misma ciudadela, manzana y solar caso contrario null.
     */
    public Integer existeHistoricoTrámites(Long id, String mzUrb, String solarUrb);
    
    public CatEnte getCatEnteByCiRuc(String ciRuc);

    public GeTipoTramite getGeTipoTramiteByActivitiKey(String solicitudServicio);

    /**
     * Si el parametro buscarDatoSeg es true envia a buscar a Dato seguro caso contrario solo en la base local 
     * @param ente
     * @param buscarDatoSeg true para buscar en dato seguro
     * @return 
     */
    public CatEnte buscarEnte(CatEnte ente, Boolean buscarDatoSeg);

}
