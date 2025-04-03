/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.entities.CatNormasConstruccionHasRetirosAumento;
import com.origami.sgm.entities.CatNormasConstruccionTipo;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatSolicitudNormaConstruccion;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import java.util.List;
import javax.ejb.Local;

/**
 * Metodos para el proceso de Normas de Construcción
 *
 * @author Angel Navarro
 */
@Local
public interface NormasConstruccionServices {

    /**
     * Busca en la tabla GeTipoTramite por el estado y el campo activitykey
     *
     * @param b Boolen
     * @param actKey Campo activitykey
     * @return GeTipoTramite
     */
    public GeTipoTramite getGeRequisitosTramite(boolean b, String actKey);

    /**
     * Busca en la tabla CatEnte Por el NÃºmero de CÃ©dula o RUC
     *
     * @param cedulaRuc CÃ©dula o RUC
     * @return CatEnte
     */
    public CatEnte getCatEnteByCiRuc(String cedulaRuc);

    /**
     * Guarda un nuevo registro en la Tabla HistoricoTramites si el id de la
     * tabla es nulo, caso contario lo envia a actualizar.
     *
     * @param ht HistoricoTramites
     * @return HistoricoTramites
     */
    public HistoricoTramites guardarHistoricoTranites(HistoricoTramites ht);

    public HistoricoTramiteDet guardarHistoricoTranitesDetalle(HistoricoTramiteDet ht);

    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Long tipo);

    /**
     * Busca en la tabla CatNormasConstruccionTipo y filtra por el campo
     * isEspecial
     *
     * @param isEspecial True o False
     * @return CatNormasConstruccionTipo
     */
    public List<CatNormasConstruccionTipo> getCatNormasConstruccionTipo(Boolean isEspecial);

    /**
     * envia a persistir la entity CatNormasConstruccionTipo
     *
     * @param tipoNorma entity
     * @return CatNormasConstruccionTipo
     */
    public CatNormasConstruccionTipo guardarCatNormasConstruccionTipo(CatNormasConstruccionTipo tipoNorma);

    /**
     * Envia a actualizar el registro que contiene la entity CatEnte
     *
     * @param responsable entity
     */
    public void actualizarCatEnte(CatEnte responsable);

    /**
     * Pregunta si el parametro idCiudadela no es nulo para realizar la busqueda
     * por el tipo de norma y el id de ciudadela, si es nulo solo realiza la
     * busqueda por el tipo de norma, en las dos consulta busca las ciudadelas
     * que tengan estado "A"
     *
     * @param tipoNorma Id de CatNormasConstruccionTipo
     * @param idCiudadela Id de CatCiudadela
     * @return CatNormasConstruccion
     */
    public CatNormasConstruccion getCatNormasConstruccion(Long tipoNorma, Long idCiudadela);

    /**
     * Busca en la tabla CatCiudadela por el Id
     *
     * @param idCiudadela id de CatCiudadela
     * @return CatCiudadela
     */
    public CatCiudadela getCatCiudadelaById(Long idCiudadela);

    /**
     * Busca enla tabka CatNormasConstruccionTipo por el id
     *
     * @param tipoNorma id de CatNormasConstruccionTipo
     * @return CatNormasConstruccionTipo
     */
    public CatNormasConstruccionTipo getCatNormasConstruccionTipoById(Long tipoNorma);

    /**
     * Reciba la lista de CatPredioPropietario, verifica si el id es nulo y lo
     * envia a persistir caso contrario solo lo actializa, la lista de
     * CatPredioPropietario a eliminar solo le cambia de estado a "I"
     *
     * @param listaPropietarios Lista de CatPredioPropietario a guadar o
     * actualizar
     * @param listaPropietariosEliminar Lista de CatPredioPropietario a cambiar
     * de estado "I"
     * @param predio CatPredio a asignar a los registros nuevos
     */
    public void guardarOActualizarCatPredioPropietario(List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar, CatPredio predio);

    /**
     * Envia a persistir la entity CatSolicitudNormaConstruccion
     *
     * @param solicitud CatSolicitudNormaConstruccion
     * @return CatSolicitudNormaConstruccion
     */
    public CatSolicitudNormaConstruccion guardarCatSolicitudNormaConstruccion(CatSolicitudNormaConstruccion solicitud);

    /**
     * Envia a persistir la entity CatNormasConstruccion y despues envia
     * apersistir cada uno de registros que contenga la lista
     * CatNormasConstruccionHasRetirosAumento
     *
     * @param normaConstruccion CatNormasConstruccion
     * @param listaNormas CatNormasConstruccionHasRetirosAumento
     * @return CatNormasConstruccion
     */
    public CatNormasConstruccion guardarNormasConstrauccion(CatNormasConstruccion normaConstruccion, List<CatNormasConstruccionHasRetirosAumento> listaNormas);

    /**
     * Busca en la tabla CatNormasConstruccion por el id
     *
     * @param idNuevaNorma id de CatNormasConstruccion
     * @return CatNormasConstruccion
     */
    public CatNormasConstruccion getCatNormasConstruccion(Long idNuevaNorma);

    /**
     * Primero actualiza la lista de CatNormasConstruccionHasRetirosAumento si
     * ya existen caso contrario los actualiza, despues envia a eliminar la
     * lista de CatNormasConstruccionHasRetirosAumento, por ultimo envia a
     * guardar la entity CatNormasConstruccion.
     *
     * @param normaConstruccion Entiti CatNormasConstruccion.
     * @param listaNormas Lista de CatNormasConstruccionHasRetirosAumento a
     * actualizar.
     * @param retirosAumentosEliminar Lista de
     * CatNormasConstruccionHasRetirosAumento a eliminar.
     * @param buffer Arreglo de byte que contiene la imagen.
     * @return True si actualizo caso contrario false.
     */
    public Boolean actualizarNormasConstrauccion(CatNormasConstruccion normaConstruccion, List<CatNormasConstruccionHasRetirosAumento> listaNormas, List<CatNormasConstruccionHasRetirosAumento> retirosAumentosEliminar, byte[] buffer);

    /**
     * Recibe el nombre de la entity para buscar en el paquete que contiene las
     * entities, si no encuentra la entity lanza una excepcion
     * ClassNotFoundException
     *
     * @param nameEntity Nombre de la entity.
     * @param id id de la tabla.
     * @return Object que contiene la entity buscada
     */
    public Object getEntityById(String nameEntity, Long id);

    /**
     * Obtiene todas la ciudadelas y las ordena por el Nombre.
     *
     * @return Lista de {@link CatCiudadela}
     */
    public List<CatCiudadela> getCatCiudadelas();
}
