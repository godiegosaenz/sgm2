/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.solicitudServicio;

import com.origami.sgm.bpm.models.SolicitudDepartamentoModel;
import com.origami.sgm.database.Querys;
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
import com.origami.sgm.services.bpm.BpmBaseEngine;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import util.Messages;

/**
 *
 * @author Angel Navarro.
 */
@Stateless(name = "solicitudServ")
@Interceptors(value = {HibernateEjbInterceptor.class})
public class SolicitudServicosEjb implements SolicitudServicosServices {

    private static final Logger logger = Logger.getLogger(SolicitudServicosEjb.class.getName());

    @javax.inject.Inject
    private PropiedadHorizontalServices propiedadH;

    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    @javax.inject.Inject
    protected Entitymanager manager;

    @javax.inject.Inject
    protected BpmBaseEngine engine;
    
    @javax.inject.Inject
    protected SeqGenMan secuencia;

    /**
     * Metodos de el proceso Propiedad Horizontal.
     *
     * @return interfas PropiedadHorizontalServices.
     */
    @Override
    public PropiedadHorizontalServices getPropiedadHorizontalServices() {
        return propiedadH;
    }

    /**
     * Buscar por el id de la tabla.
     *
     * @param id id de la tabla VuCatalogo.
     * @return vuCatalogo.
     */
    @Override
    public VuCatalogo getVuCatalogoById(long id) {
        return manager.find(VuCatalogo.class, id);
    }

    /**
     * Metodos del proceso Norma de Construcción
     *
     * @return Interface NormasConstruccionServices.
     */
    @Override
    public NormasConstruccionServices getNormasConstruccion() {
        return normasServices;
    }

    /**
     * Guarda o actualiza en la tabla SvSolicitudServicios si tiene asignados
     * los departamentos tambien los envia a persistir.
     *
     * @param solicitud SvSolicitudServicios a perisitir.
     * @return SvSolicitudServicios ya persistido.
     */
    @Override
    public SvSolicitudServicios guardarOActualizarSolicitudServicos(SvSolicitudServicios solicitud) {
        SvSolicitudServicios s;
        try {
            List<SvSolicitudDepartamento> list = (List<SvSolicitudDepartamento>) solicitud.getSvSolicitudDepartamentoCollection();
            solicitud.setSvSolicitudDepartamentoCollection(null);
            if (solicitud.getId() != null) {
                manager.persist(solicitud);
                s = solicitud;
            } else {
                s = (SvSolicitudServicios) manager.persist(solicitud);
            }
            if (solicitud.getAsignado()) {
                for (SvSolicitudDepartamento d : list) {
                    d.setSolicitud(s);
                    manager.persist(d);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
            return null;
        }
        return s;
    }

    /**
     * Busca en la table SvSolicitudServicios y los filtra por el id de la tabla
     * HistoricoTramites.
     *
     * @param id Id de HistoricoTramites.
     * @return
     */
    @Override
    public SvSolicitudServicios getSolicitudServicioByTramite(Long id) {
        Map<String, Object> paramt = new HashMap<>();
        paramt.put("tramite.idTramite", id);
        return manager.findObjectByParameter(SvSolicitudServicios.class, paramt);
    }

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
    @Override
    public SvSolicitudServicios actualizarSolicitudServcioyObservaciones(SvSolicitudServicios solicitud, HistoricoTramites ht, String nameUserCreador, String observ, String nombreTarea) {
        try {
            propiedadH.actualizarHistoricoTramites(ht);
            if (propiedadH.guardarObservaciones(ht, nameUserCreador, observ, nombreTarea) != null) {
                return guardarOActualizarSolicitudServicos(solicitud);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
            return null;
        }
        return null;
    }

    /**
     * Obtiene todos los Departamentos que contenga la tabla GeDepartamento.
     *
     * @return Lista de GeDepartamento.
     */
    @Override
    public List<GeDepartamento> getDepartamentos() {
        return manager.findAllObjectOrder(GeDepartamento.class, new String[]{"nombre"}, Boolean.TRUE);
    }

    /**
     * Obtiene todos las Direcciones.
     *
     * @return Lista de GeDepartamento.
     */
    @Override
    public List<GeDepartamento> getDirecciones() {
        Map<String, Object> paMap = new HashMap<>();
        paMap.put("direccion", true);
        paMap.put("estado", true);
        return manager.findObjectByParameterOrderList(GeDepartamento.class, paMap, new String[]{"nombre"}, Boolean.TRUE);
    }

    /**
     * Busca en la tabla AclUser por los id que se pasa como parametros y llena
     * Lista de Modelo de Datos.
     *
     * @param list Lista de id de la tabla AclUser.
     * @return Lista ModelUsuarios.
     */
    /*@Override
     public List<ModelUsuarios> getUsuariosXDepartamento(List<Long> list) {
     List<ModelUsuarios> usuarios = new ArrayList<>();
     try {
     if (!list.isEmpty()) {
     List<AclUser> u = acl.getTecnicosByRol(list);
     for (AclUser u1 : u) {
     ModelUsuarios model = new ModelUsuarios();
     model.setIdUser(u1.getId());
     if (u1.getEnte() != null) {
     model.setNombre(Utils.isEmpty(u1.getEnte().getNombres()) + " " + Utils.isEmpty(u1.getEnte().getApellidos()));
     if (!u1.getEnte().getEnteCorreoCollection().isEmpty()) {
     model.setCorreo(u1.getEnte().getEnteCorreoCollection().get(0).getEmail());
     } else {
     model.setCorreo(null);
     }
     } else {
     model.setNombre("");
     model.setCorreo(null);
     }

     model.setUser(u1.getUsuario());
     usuarios.add(model);
     }
     }
     } catch (Exception e) {
     logger.log(Level.SEVERE, Messages.errorTransaccion, e);
     return null;
     }
     return usuarios;
     }*/
    /**
     * Busca Todos los departamentos por el id.
     *
     * @param list Lista de id.
     * @return Lista de GeDepartamento.
     */
    @Override
    public List<GeDepartamento> getDepartamentosById(List<Long> list) {
        List<GeDepartamento> d;
        try {
            d = new ArrayList<>();
            for (Long d1 : list) {
                GeDepartamento de = manager.find(GeDepartamento.class, d1);
                d.add(de);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
            return null;
        }
        return d;
    }

    /**
     * Obtiene lista del id de los roles por departamento
     *
     * @param deps Lista de GeDepartamento.
     * @return Lista de Id de los departamentos.
     */
    @Override
    public List<Long> getListRol(List<GeDepartamento> deps) {
        List<AclRol> roles = new ArrayList<>();
        List<Long> longRol = new ArrayList<>();
        try {
            for (GeDepartamento role : deps) {
                roles.addAll(role.getAclRolCollection());
            }
            for (AclRol rol : roles) {
                if (!longRol.contains(rol.getId())) {
                    longRol.add(rol.getId());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
            return null;
        }
        return longRol;

    }

    /**
     * Busca por el id
     *
     * @param idRol Id de la tabla
     * @return AclRol
     */
    @Override
    public AclRol getRol(Long idRol) {
        return manager.find(AclRol.class, idRol);
    }

    @Override
    public List<SvSolicitudDepartamento> getSolicitudDepartamentoByIdSol(Long IdSolic) {
        Map<String, Object> paMap = new HashMap<>();
        paMap.put("solicitud.id", IdSolic);
        return manager.findObjectByParameterList(SvSolicitudDepartamento.class, paMap);
    }

    @Override
    public List<SvSolicitudServicios> getListSolicitudDepartamento(Long depts, String estado, Date fechaDesde, Date fechaHasta) {

        String adj = "";
        List<Object> values = new ArrayList<>();
        List<String> properties = new ArrayList<>();

        values.add(depts);
        properties.add("id");

        if (estado.equalsIgnoreCase("pendientes")) {
            adj = " AND s.tramite.estado = :estado";
            values.add("Pendiente");
            properties.add("estado");
        }
        if (fechaDesde != null) {
            adj = adj + " AND s.fechaCreacion >= :desde";
            values.add(fechaDesde);
            properties.add("desde");
        }
        if (fechaHasta != null) {
            adj = adj + " AND s.fechaCreacion <= :hasta";
            values.add(fechaHasta);
            properties.add("hasta");
        }

        List<SvSolicitudServicios> l = new ArrayList<>();
        l.addAll(manager.findAll(Querys.getSvSolicitudServicion + adj + " ORDER BY s.tramite DESC", getProperties(properties), converterArray(values)));

        return l;
    }

    private String[] getProperties(List<String> properties) {
        String[] p = new String[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            p[i] = properties.get(i);
        }
        return p;
    }

    private Object[] converterArray(List<Object> properties) {
        Object[] p = new Object[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            p[i] = properties.get(i);
        }
        return p;
    }

    @Override
    public List<SolicitudDepartamentoModel> getModelSolicitud(String estado, Date fechaDesde, Date fechaHasta, Long get) {
        List<SolicitudDepartamentoModel> model = new ArrayList<>();
        List<SvSolicitudServicios> list = getListSolicitudDepartamento(get, estado, fechaDesde, fechaHasta);
        for (SvSolicitudServicios l : list) {
            SolicitudDepartamentoModel m = new SolicitudDepartamentoModel();
            m.setSolicitud(l);
            m.setSolicitante(getNombrePropietario(l.getEnteSolicitante()));
            model.add(m);
        }
        return model;
    }

    public String getNombrePropietario(CatEnte ente) {
        if (ente.getEsPersona()) {
            if (ente.getApellidos() != null && ente.getNombres() != null) {
                return ente.getApellidos().toUpperCase() + " " + ente.getNombres().toUpperCase();
            } else if (ente.getApellidos() != null && ente.getNombres() == null) {
                return ente.getApellidos().toUpperCase();
            } else if (ente.getApellidos() == null && ente.getNombres() != null) {
                return ente.getNombres().toUpperCase();
            }
        } else {
            if (ente.getRazonSocial() != null) {
                return ente.getRazonSocial();
            } else {
                return "";
            }
        }

        return "";
    }

    @Override
    public SvSolicitudServicios actualizarSolicitudServcioyObservaciones(SvSolicitudServicios solicitud, HistoricoTramites ht, String name_user, String observ, String tarea, List<SvSolicitudDepartamento> departamentos) {
        actualizarSolicitudServcioyObservaciones(solicitud, ht, name_user, observ, tarea);
        try {
            if (!departamentos.isEmpty()) {
                for (SvSolicitudDepartamento d : solicitud.getSvSolicitudDepartamentoCollection()) {
                    d.setEstado(false);
                    manager.update(d);
                }

                for (SvSolicitudDepartamento dp : departamentos) {
                    dp.setEstado(Boolean.TRUE);
                    manager.persist(dp);
                }
            }
            return solicitud;
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
        return null;
    }

    @Override
    public SvSolicitudDepartamento getSvSolicitudDepartamentoById(Long id) {
        return manager.find(SvSolicitudDepartamento.class, id);
    }

    @Override
    public SvSolicitudDepartamento getSvSolicitudDepartamentoByIdPadre(Long id) {
        Map<String, Object> paMap = new HashMap<>();
        paMap.put("padre", id);
        return manager.findObjectByParameter(SvSolicitudDepartamento.class, paMap);
    }

    @Override
    public SvSolicitudDepartamento guardarSvSolicitudDepartamentoById(SvSolicitudDepartamento solicitudDepartamento) {
        solicitudDepartamento = (SvSolicitudDepartamento) manager.persist(solicitudDepartamento);
        return solicitudDepartamento;
    }

    @Override
    public SvSolicitudDepartamento getSvSolicitudDepartamentoByIdDireccion(SvSolicitudServicios solicitud, GeDepartamento direccion, Boolean estado) {
        Map<String, Object> paMap = new HashMap<>();
        paMap.put("solicitud", solicitud);
        paMap.put("departamento", direccion);
        paMap.put("estado", estado);
        return manager.findObjectByParameter(SvSolicitudDepartamento.class, paMap);
    }

    @Override
    public AclUser getUsuario(Long id) {
        return manager.findNoProxy(AclUser.class, id);
    }

    @Override
    public List<CatEnte> getEntesByRazonSocial(String razonSocial) {
        try {
            return manager.findAll(Querys.CatEnteByRazonSocialList, new String[]{"razonSocial"}, new Object[]{"%" + razonSocial + "%"});
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
        return null;
    }

    @Override
    public void guardarEnteCorreosTlfnos(CatEnte ente, List<EnteCorreo> eliminarCorreo, List<EnteTelefono> eliminarTelefono) {
        
        if (eliminarTelefono != null) {
            if (!eliminarTelefono.isEmpty()) {
                for (EnteTelefono listTelefono : eliminarTelefono) {
                    manager.delete(listTelefono);
                }
            }
        }
        if (eliminarCorreo != null) {
            if (!eliminarCorreo.isEmpty()) {
                for (EnteCorreo listCorreos : eliminarCorreo) {
                    manager.delete(listCorreos);
                }
            }
        }
        manager.guardarEnteCorreosTlfnos(ente);
    }
    
    @Override
    public SvSolicitudServicios editarSolicitud(SvSolicitudServicios s){
        try {
            return (SvSolicitudServicios) manager.persist(s);
        } catch (Exception e) {
            logger.log(Level.SEVERE, Messages.errorTransaccion, e);
        }
        return null;
    }
    
    @Override
    public Long existeEnteByCiRuc(String[] param, Object[] values) {
        return (Long) manager.findNoProxy(Querys.getIdEnte, param, values);
    }
    
    public CatEnte guardarEnteCorreosTlfnos(CatEnte ente) {
        try {
            Boolean nuevo = false;
            final List<EnteCorreo> enteCorreoCollection = ente.getEnteCorreoCollection();
            final List<EnteTelefono> enteTelefonoCollection = ente.getEnteTelefonoCollection();
            if (!ente.getEsPersona()) {
                if (ente.getCiRuc() == null || ente.getCiRuc().equals("")) {
                    ente.setExcepcionales(Boolean.TRUE);
                    ente = secuencia.guardarOActualizarEnte(ente);
                    nuevo = true;
                } else {
                    if (ente.getId() == null) {
                        ente = (CatEnte) manager.persist(ente);
                        nuevo = true;
                    } else {
                        manager.persist(ente);
                    }
                }
            } else {
                if (ente.getId() == null) {
                    ente = (CatEnte) manager.persist(ente);
                    nuevo = true;
                } else {
                    manager.persist(ente);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SolicitudServicosEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return ente;
    }
}
