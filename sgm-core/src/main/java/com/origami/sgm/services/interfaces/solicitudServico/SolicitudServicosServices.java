/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.solicitudServico;

import com.origami.sgm.bpm.models.SolicitudDepartamentoModel;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Angel Navarro
 */
@Local
public interface SolicitudServicosServices {

    /**
     * Retorna todos los metodos de Propiedad Horizontal
     *
     * @return interface PropiedadHorizontalServices.
     */
    public PropiedadHorizontalServices getPropiedadHorizontalServices();

    /**
     * Buscar por el id de la tabla.
     *
     * @param id id de la tabla VuCatalogo.
     * @return vuCatalogo.
     */
    public VuCatalogo getVuCatalogoById(long id);

    /**
     * Metodos del proceso Norma de Construcción
     *
     * @return Interface NormasConstruccionServices.
     */
    public NormasConstruccionServices getNormasConstruccion();

    /**
     * Guarda o actualiza en la tabla SvSolicitudServicios si tiene asignados
     * los departamentos tambien los envia a persistir.
     *
     * @param solicitud SvSolicitudServicios a perisitir.
     * @return SvSolicitudServicios ya persistido.
     */
    public SvSolicitudServicios guardarOActualizarSolicitudServicos(SvSolicitudServicios solicitud);

    /**
     * Busca en la table SvSolicitudServicios y los filtra por el id de la tabla
     * HistoricoTramites.
     *
     * @param id Id de HistoricoTramites.
     * @return
     */
    public SvSolicitudServicios getSolicitudServicioByTramite(Long id);

    /**
     * Actualiza HistoricoTramites, SvSolicitudServicios, guardar las
     * observaciones de la tarea.
     *
     * @param solicitud SvSolicitudServicios a actualizar.
     * @param ht HistoricoTramites a actualizar.
     * @param nameUserCreador User.
     * @param observ Observaciones de la tarea.
     * @param nombreTarea Nombre de la tarea.
     * @return True si no hubo ningun error en la transación caso contrario
     * false.
     */
    public SvSolicitudServicios actualizarSolicitudServcioyObservaciones(SvSolicitudServicios solicitud, HistoricoTramites ht, String nameUserCreador, String observ, String nombreTarea);

    /**
     * Obtiene todos los Departamentos que contenga la tabla GeDepartamento.
     *
     * @return Lista de GeDepartamento.
     */
    public List<GeDepartamento> getDepartamentos();

    /**
     * Obtiene todos las Direcciones.
     *
     * @return Lista de GeDepartamento.
     */
    public List<GeDepartamento> getDirecciones();

    /**
     * Busca en la tabla AclUser por los id que se pasa como parametros y llena
     * Lista de Modelo de Datos.
     *
     * @param list Lista de id de la tabla AclUser.
     * @return Lista ModelUsuarios.
     */
    //public List<ModelUsuarios> getUsuariosXDepartamento(List<Long> list);
    /**
     * Busca Todos los departamentos por el id.
     *
     * @param list Lista de id.
     * @return Lista de GeDepartamento.
     */
    public List<GeDepartamento> getDepartamentosById(List<Long> list);

    /**
     * Obtiene lista del id de los roles por departamento
     *
     * @param deps Lista de GeDepartamento.
     * @return Lista de Id de los departamentos.
     */
    public List<Long> getListRol(List<GeDepartamento> deps);

    /**
     * Busca por el id
     *
     * @param idRol Id de la tabla
     * @return AclRol
     */
    public AclRol getRol(Long idRol);

    /**
     * Obtiene la lista de entity SvSolicitudDepartamento filtrando por el id de
     * de la entity SvSolicitudServicios
     *
     * @param IdSolic Primery key de SvSolicitudServicios
     * @return Lista de entity SvSolicitudDepartamento si existe caso contrario
     * retorna null.
     */
    public List<SvSolicitudDepartamento> getSolicitudDepartamentoByIdSol(Long IdSolic);

    /**
     * Realiza la busqueda en la tabla SvSolicitudServicios con los parametros
     * que se envian como parametro.
     *
     * @param depts Id de GeDepartamento
     * @param estado Estado de la solicitud ejemplo: pendientes
     * @param fechaDesde Fecha desde donde inicia el filtro.
     * @param fechaHasta Fecha hasta donde va realizar el filtro.
     * @return Lista de Entity SvSolicitudServicios que estan dentro de los
     * parametros de la consulta.
     */
    public List<SvSolicitudServicios> getListSolicitudDepartamento(Long depts, String estado, Date fechaDesde, Date fechaHasta);

    /**
     * Realiza la busqueda en la tabla SvSolicitudServicios con los parametros
     * que se la pasa como parametros y despues llena el modelo de datos.
     *
     * @param estado Estado de la entity SvSolicitudServicios.
     * @param fechaDesde Fecha de inicio.
     * @param fechaHasta Fecha final.
     * @param get Id del departamento.
     * @return Lista del modelos de datos.
     */
    public List<SolicitudDepartamentoModel> getModelSolicitud(String estado, Date fechaDesde, Date fechaHasta, Long get);

    /**
     * Actualiza la entity solicitud y crea un nuevo de registro de observacion
     * de la tarea, despues actualiza los departamentos existentes cambiando el
     * campo estado a false y envia persitir los nuevos departamentos
     *
     * @param solicitud Entity SvSolicitudServicios.
     * @param ht HistoricoTramites.
     * @param name_user Usuario que completa la tarea.
     * @param observ Observaciones del trámite.
     * @param tarea Nombre de tarea.
     * @param departamentos Lista de departamentos.
     * @return Entity SvSolicitudServicios persistida si no hay error en la
     * tranzacción caso contrario null.
     */
    public SvSolicitudServicios actualizarSolicitudServcioyObservaciones(SvSolicitudServicios solicitud, HistoricoTramites ht, String name_user, String observ, String tarea, List<SvSolicitudDepartamento> departamentos);

    /**
     * Obtiene entity SvSolicitudDepartamento por el primary key.
     *
     * @param id Primary key.
     * @return Entity SvSolicitudDepartamento si exsite en la base de datos caso
     * contrario null.
     */
    public SvSolicitudDepartamento getSvSolicitudDepartamentoById(Long id);
    
    /**
     * 
     * @param id
     * @return 
     */
    public SvSolicitudDepartamento getSvSolicitudDepartamentoByIdPadre(Long id);

    /**
     * Envia persistir solicitudDepartamento en la base de datos.
     *
     * @param solicitudDepartamento
     * @return Entity SvSolicitudDepartamento persistida en la base de datos.
     */
    public SvSolicitudDepartamento guardarSvSolicitudDepartamentoById(SvSolicitudDepartamento solicitudDepartamento);

    /**
     * Lee los parametros y realiza la busqueda en la tabla
     * SvSolicitudDepartamento
     *
     * @param solicitud Entity SvSolicitudServicios
     * @param direccion Entity GeDepartamento
     * @param estado Estado de SvSolicitudDepartamento
     * @return Entity SvSolicitudDepartamento si existe caso contrario null.
     */
    public SvSolicitudDepartamento getSvSolicitudDepartamentoByIdDireccion(SvSolicitudServicios solicitud, GeDepartamento direccion, Boolean estado);
    
    public AclUser getUsuario(Long id);

    public List<CatEnte> getEntesByRazonSocial(String razonSocial);

    public void guardarEnteCorreosTlfnos(CatEnte ente, List<EnteCorreo> eliminarCorreo, List<EnteTelefono> eliminarTelefono);
    
    public SvSolicitudServicios editarSolicitud(SvSolicitudServicios s);

    public Long existeEnteByCiRuc(String[] string, Object[] object);
}
