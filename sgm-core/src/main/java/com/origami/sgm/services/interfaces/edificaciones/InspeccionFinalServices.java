/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoArchivo;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PeInspeccionCabEdificacion;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.PePermisoCabEdificacion;
import com.origami.sgm.entities.PeTipoPermiso;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Joao Sanga
 */
public interface InspeccionFinalServices {
    
    /**
     * Devuelve una lista de la entidad CtlgItem de acuerdo al nombre del catálogo.
     * 
     * @param nombreDeCatalogo
     * @return 
     */
    public List<CtlgItem> obtenerCtlgItemListByNombreDeCatalogo(String nombreDeCatalogo);
    
    /**
     * Guarda los datos de la tasa de liquidación. También guarda todo lo necesario
     * para la culminación de la tarea.
     * 
     * @param permiso
     * @param inspeccionFinalV
     * @param listaDetalle
     * @param obs
     * @param htd
     * @param hrt
     * @param propietariosList
     * @param pePermisoCabEdificacionList
     * @param peDetallePermisoList
     * @param htdList
     * @param ht
     * @return 
     */
    public PeInspeccionFinal guardarTasaDeLiquidacion(PePermiso permiso, PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, Observaciones obs, HistoricoTramiteDet htd, HistoricoReporteTramite hrt, List<CatPredioPropietario> propietariosList, List<PePermisoCabEdificacion> pePermisoCabEdificacionList, List<PeDetallePermiso> peDetallePermisoList, List<HistoricoTramiteDet> htdList, HistoricoTramites ht);
    
    /**
     * Guarda los datos de la tasa de liquidación. También guarda todo lo necesario
     * para la culminación de la tarea.
     * 
     * @param inspeccionFinalV
     * @param listaDetalle
     * @param obs
     * @param htd
     * @param propietariosList
     * @param hrtList
     * @param htdList
     * @param peInspeccionCabEdificacionList
     * @return 
     */
    public Boolean guardarTasaDeLiquidacionEdicion(PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, Observaciones obs, HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoReporteTramite> hrtList, List<HistoricoTramiteDet> htdList, List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList);
    
    
    public Boolean guardarTasaDeLiquidacionEdicionLocal(PeInspeccionFinal inspeccionFinalV, List<CtlgItem> listaDetalle, HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoTramiteDet> htdList, List<PeInspeccionCabEdificacion> peInspeccionCabEdificacionList);
    
    /**
     * Devuelve un objeto PeTipoDeAcuerdo de acuerdo a su código.
     * 
     * @param codigo
     * @return 
     */
    public PeTipoPermiso getTipoPermiso(String codigo);
    
    /**
     * Guarda la observación de la tarea y el Historico Archivo de las imágenes
     * que se suben de la inspección.
     * 
     * @param obs
     * @param ha
     * @return 
     */
    public Boolean guardarArchivosInspeccion(Observaciones obs, HistoricoArchivo ha);
    
    /**
     * Actualiza los datos de la inspección realizada en base a a información del predio.
     * 
     * @param predio
     * @param inspeccion
     * @return 
     */
    public Boolean actualizarDatosPredio(CatPredio predio, PeInspeccionFinal inspeccion);
    
    public BigDecimal valoresAvaluoOrdenanzaPermiso(List<PePermisoCabEdificacion> edificacionesList);
    
    public BigDecimal valoresAvaluoOrdenanzaInspeccion(List<PeInspeccionCabEdificacion> edificacionesList);
    
}
