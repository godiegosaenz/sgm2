/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermisosAdicionales;
import com.origami.sgm.entities.PePermisosAdicionales;
import com.origami.sgm.entities.PeUnidadMedida;
import java.util.List;
import javax.ejb.Local;
import org.activiti.engine.runtime.ProcessInstance;

/**
 *
 * @author Joao Sanga
 */
@Local
public interface PermisosAdicionalesServices {

    /**
     * Retorna un ente de acuerdo a los parámetros ingresados.
     *
     * @param query
     * @param parametros
     * @param valores
     * @return
     */
    public CatEnte obtenerCatEntePorQuery(String query, String[] parametros, Object[] valores);

    /**
     * Método que me permite iniciar una instancia. Además guarda todas las
     * entidades necesarias para iniciar.
     *
     * @param ht
     * @param obs
     * @param p
     * @return
     */
    public HistoricoTramites iniciarProceso(HistoricoTramites ht, Observaciones obs, ProcessInstance p);

    /**
     * Retorna una lista de la entidad CatEdfProp de acuerdo al nombre de la
     * categoria
     *
     * @param nomCategoria
     * @return
     */
    public List<CatEdfProp> getMaterialesPorNombreCategoria(String nomCategoria);

    /**
     * Retorna una lista de la entidad UnidadesMedida
     *
     * @return
     */
    public List<PeUnidadMedida> getUnidadesMedida();

    /**
     * Guarda la entidad de permiso adicional que se genera y los demás objetos
     * necesarios para completar la tarea.
     *
     * @param pAdicional
     * @param hrt
     * @param htd
     * @param htdList
     * @param hrtList
     * @param pdpaList
     * @param obs
     * @param datosPredio
     * @param lisPropietarios
     * @param ht
     * @return
     */
    public Boolean guardarPermisoAdicional(PePermisosAdicionales pAdicional, HistoricoReporteTramite hrt, HistoricoTramiteDet htd, List<HistoricoTramiteDet> htdList, List<HistoricoReporteTramite> hrtList, List<PeDetallePermisosAdicionales> pdpaList, Observaciones obs, CatPredio datosPredio, List<CatPredioPropietario> lisPropietarios, HistoricoTramites ht);

    /**
     * Busca en la table por el id
     *
     * @param idHtd id de la table
     * @return Entity {@link PePermisosAdicionales} si existe caso contrario null
     */
    public PePermisosAdicionales getPePermisoAdicionalesById(Long idHtd);

    public boolean actualizarPermisoAdicionales(PePermisosAdicionales permisoAdicional, List<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesList, List<CatPredioPropietario> lisPropietarios, HistoricoTramiteDet htd, HistoricoTramites ht, CatPredio datosPredio);

}
