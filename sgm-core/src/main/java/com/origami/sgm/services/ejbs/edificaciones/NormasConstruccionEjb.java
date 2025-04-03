/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.edificaciones;

import com.origami.sgm.database.Querys;
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
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Metodos para el proceso de Normas de ConstrucciÃ³n
 *
 * @author Angel Navarro
 */
@Stateless(name = "normasConstruccion")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class NormasConstruccionEjb implements NormasConstruccionServices {

    @javax.inject.Inject
    protected Entitymanager manager;

    @javax.inject.Inject
    private SeqGenMan secuencia;

    /**
     * Busca en la tabla GeTipoTramite por el estado y el campo activitykey
     *
     * @param b Boolen
     * @param actKey Campo activitykey
     * @return GeTipoTramite
     */
    @Override
    public GeTipoTramite getGeRequisitosTramite(boolean b, String actKey) {
        return (GeTipoTramite) manager.find(Querys.getGeTipoTramitesByActKey_State, new String[]{"estado", "key"}, new Object[]{b, actKey});
    }

    /**
     * Busca en la tabla CatEnte Por el NÃºmero de CÃ©dula o RUC
     *
     * @param cedulaRuc CÃ©dula o RUC
     * @return CatEnte
     */
    @Override
    public CatEnte getCatEnteByCiRuc(String cedulaRuc) {
        return (CatEnte) manager.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedulaRuc});
    }

    /**
     * Guarda un nuevo registro en la Tabla HistoricoTramites si el id de la
     * tabla es nulo, caso contario lo envia a actualizar.
     *
     * @param ht HistoricoTramites
     * @return HistoricoTramites
     */
    @Override
    public HistoricoTramites guardarHistoricoTranites(HistoricoTramites ht) {
        try {
            ht.setLiquidacionAprobada(Boolean.FALSE);
            if (ht.getIdTramite() != null) {
                if (manager.update(ht)) {
                    return ht;
                }
            } else {
//                Calendar cl = Calendar.getInstance();
//                Integer anio = cl.get(Calendar.YEAR);
//                BigInteger tramiteXDepartamento = new BigInteger(secuencia.getMaxSecuenciaTipoTramite(anio, ht.getTipoTramite().getId()).toString());
//                ht.setNumTramiteXDepartamento(tramiteXDepartamento);
                return (HistoricoTramites) manager.persist(ht);
            }
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    public HistoricoTramiteDet guardarHistoricoTranitesDetalle(HistoricoTramiteDet ht) {
        try {

            if (ht.getId() != null) {
                if (manager.update(ht)) {
                    return ht;
                }
            } else {

                return (HistoricoTramiteDet) manager.persist(ht);
            }
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     *
     * @param tipo
     * @return
     */
    @Override
    public MsgFormatoNotificacion getMsgFormatoNotificacionByTipo(Long tipo) {
        return (MsgFormatoNotificacion) manager.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{tipo});
    }

    /**
     * Busca en la tabla CatNormasConstruccionTipo y filtra por el campo
     * isEspecial
     *
     * @param isEspecial True o False
     * @return CatNormasConstruccionTipo
     */
    @Override
    public List<CatNormasConstruccionTipo> getCatNormasConstruccionTipo(Boolean isEspecial) {
        return manager.findAll(Querys.getCatNormasConstruccionTipoByIsEspecial, new String[]{"isEspecial"}, new Object[]{isEspecial});
    }

    /**
     * envia a persistir la entity CatNormasConstruccionTipo
     *
     * @param tipoNorma entity
     * @return CatNormasConstruccionTipo
     */
    @Override
    public CatNormasConstruccionTipo guardarCatNormasConstruccionTipo(CatNormasConstruccionTipo tipoNorma) {
        try {
            return (CatNormasConstruccionTipo) manager.persist(tipoNorma);
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Envia a actualizar el registro que contiene la entity CatEnte
     *
     * @param responsable entity
     */
    @Override
    public void actualizarCatEnte(CatEnte responsable) {
        try {
            manager.persist(responsable);
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

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
    @Override
    public CatNormasConstruccion getCatNormasConstruccion(Long tipoNorma, Long idCiudadela) {
        if (idCiudadela != null) {
            return (CatNormasConstruccion) manager.find(Querys.getCatNormasConstruccionByTipoNormaByCiudadela,
                    new String[]{"ciudadela", "tipoNorma"}, new Object[]{idCiudadela, tipoNorma});
        } else {
            return (CatNormasConstruccion) manager.find(Querys.getCatNormasConstruccionByTipoNorma,
                    new String[]{"tipoNorma"}, new Object[]{tipoNorma});
        }
    }

    /**
     * Busca en la tabla CatCiudadela por el Id
     *
     * @param idCiudadela id de CatCiudadela
     * @return CatCiudadela
     */
    @Override
    public CatCiudadela getCatCiudadelaById(Long idCiudadela) {
        return (CatCiudadela) manager.find(CatCiudadela.class, idCiudadela);
    }

    /**
     * Busca enla tabka CatNormasConstruccionTipo por el id
     *
     * @param tipoNorma id de CatNormasConstruccionTipo
     * @return CatNormasConstruccionTipo
     */
    @Override
    public CatNormasConstruccionTipo getCatNormasConstruccionTipoById(Long tipoNorma) {
        return (CatNormasConstruccionTipo) manager.find(CatNormasConstruccionTipo.class, tipoNorma);
    }

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
    @Override
    public void guardarOActualizarCatPredioPropietario(List<CatPredioPropietario> listaPropietarios, List<CatPredioPropietario> listaPropietariosEliminar, CatPredio predio) {
        try {
            if (!listaPropietarios.isEmpty()) {
                for (CatPredioPropietario lp : listaPropietarios) {
                    if (lp.getId() != null) {
                        manager.update(lp);
                    } else {
                        lp.setPredio(predio);
                        lp.setEstado("A");
                        manager.persist(lp);
                    }
                }
            }
            if (!listaPropietariosEliminar.isEmpty()) {
                for (CatPredioPropietario lp : listaPropietariosEliminar) {
                    lp.setEstado("I");
                    manager.update(lp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Envia a persistir la entity CatSolicitudNormaConstruccion
     *
     * @param solicitud CatSolicitudNormaConstruccion
     * @return CatSolicitudNormaConstruccion
     */
    @Override
    public CatSolicitudNormaConstruccion guardarCatSolicitudNormaConstruccion(CatSolicitudNormaConstruccion solicitud) {
        Map pa = new HashMap<>();
        pa.put("id", Long.valueOf(solicitud.getTramite().toString()));
        HistoricoTramites ht = (HistoricoTramites) manager.findObjectByParameter(HistoricoTramites.class, pa);
        BigInteger secRep = BigInteger.valueOf(this.secuencia.getMaxSecuenciaTipoTramite(solicitud.getAnioTramite(), ht.getTipoTramite().getId()));
        solicitud.setNumReporte(secRep);
        return (CatSolicitudNormaConstruccion) manager.persist(solicitud);
    }

    /**
     * Envia a persistir la entity CatNormasConstruccion y despues envia
     * apersistir cada uno de registros que contenga la lista
     * CatNormasConstruccionHasRetirosAumento
     *
     * @param normaConstruccion CatNormasConstruccion
     * @param listaNormas CatNormasConstruccionHasRetirosAumento
     * @return CatNormasConstruccion
     */
    @Override
    public CatNormasConstruccion guardarNormasConstrauccion(CatNormasConstruccion normaConstruccion, List<CatNormasConstruccionHasRetirosAumento> listaNormas) {
        try {
            normaConstruccion = (CatNormasConstruccion) manager.persist(normaConstruccion);
            if (!listaNormas.isEmpty()) {
                for (CatNormasConstruccionHasRetirosAumento dn : listaNormas) {
                    dn.setNormaConstruccion(normaConstruccion);
                    manager.persist(dn);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return normaConstruccion;
    }

    /**
     * Busca en la tabla CatNormasConstruccion por el id
     *
     * @param idNuevaNorma id de CatNormasConstruccion
     * @return CatNormasConstruccion
     */
    @Override
    public CatNormasConstruccion getCatNormasConstruccion(Long idNuevaNorma) {
        return manager.find(CatNormasConstruccion.class, idNuevaNorma);
    }

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
    @Override
    public Boolean actualizarNormasConstrauccion(CatNormasConstruccion normaConstruccion, List<CatNormasConstruccionHasRetirosAumento> listaNormas, List<CatNormasConstruccionHasRetirosAumento> retirosAumentosEliminar, byte[] buffer) {
        try {
            normaConstruccion.setCatNormasConstruccionHasRetirosAumentoCollection(null);
            normaConstruccion.setImafoto(null);
            manager.update(normaConstruccion);
            if (!listaNormas.isEmpty()) {
                for (CatNormasConstruccionHasRetirosAumento dn : listaNormas) {
                    if (dn.getId() != null) {
                        manager.update(dn);
                    } else {
                        dn.setNormaConstruccion(normaConstruccion);
                        manager.persist(dn);
                    }
                }
            }
            if (!retirosAumentosEliminar.isEmpty()) {
                for (CatNormasConstruccionHasRetirosAumento rt : retirosAumentosEliminar) {
                    manager.delete(rt);
                }
            }
            normaConstruccion.setImafoto(buffer);
            manager.update(normaConstruccion);
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Recibe el nombre de la entity para buscar en el paquete que contiene a
     * las entities, si no encuentra la entity displya una excepcion
     *
     * @param nameEntity Nombre de la entity.
     * @param id id de la tabla.
     * @return Object que contiene la entity buscada
     */
    @Override
    public Object getEntityById(String nameEntity, Long id) {
        Object entityObject = null;
        try {
            Class entityClass = Class.forName("com.origami.sgm.entities." + nameEntity);
            entityObject = manager.find(entityClass, id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NormasConstruccionEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entityObject;
    }

    @Override
    public List<CatCiudadela> getCatCiudadelas() {
        return manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE);
    }

}
