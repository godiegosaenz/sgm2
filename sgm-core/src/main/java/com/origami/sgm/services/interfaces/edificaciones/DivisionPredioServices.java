/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.edificaciones;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.Resolucion;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Joao Sanga
 */
@Local
public interface DivisionPredioServices {
    
    public Boolean guardarResolucion(Resolucion resolucion, Observaciones obs, HistoricoTramites ht);
    
    /**
     * Retorna un objeto CtlgItem de acuerdo a su id en la base de datos.
     * 
     * @param id
     * @return 
     */
    public CtlgItem obtenerCtlgItemPorID(Long id);
    
    /**
     * Retorna una lista de  objetos CtlgItem de acuerdo al nombre de su catálogo.
     * 
     * @param nombreDeCatalogo
     * @return 
     */
    public List<CtlgItem> obtenerCtlgItemListByNombreDeCatalogo(String nombreDeCatalogo);
    
    
    /**
     * Retorna un objeto HistoricoTramites de acuerdo a su id en la base de datos.
     * 
     */
    public HistoricoTramites obtenerHistoricoTramitePorID(Long id);
    
    public CatPredio obtenerCatPredioPorQuery(String query, String[] parametros, Object[] valores);
    
    public AclUser obtenerAclUserPorQuery(String query, String[] parametros, Object[] valores);
    
    public CatEnte obtenerCatEntePorQuery(String query, String[] parametros, Object[] valores);
    
    public CatPredioPropietario obtenerCatPredioPropietarioByID(Long id);
    
    public List<PeFirma> obtenerPeFirmaListPorQuery(String query, String[] parametros, Object[] valores);
    
    public List<CatPredio> obtenerCatPrediosHijos(String query, String[] parametros, Object[] valores);
    
    public List<HistoricoReporteTramite> obtenerHistoricoReporteTramiteListPorQuery(String query, String[] parametros, Object[] valores);
    
    public List<CatCanton> obtenerCantonesList();
    
    public List<CatEdfCategProp> obtenerCatCategoriasPropConstruccion();
    
    public List<CatTiposDominio> obtenerTipoDominioList();
    
    public HistoricoReporteTramite guardarHistoricoReporteTramite(HistoricoReporteTramite hrt);
    
    public HistoricoTramiteDet guardarHistoricoTramiteDetalle(HistoricoTramiteDet hrt);
    
    public CatPredioEdificacion guardarCatPredioEdificacion(CatPredioEdificacion cpe);
    
    public HistoricoTramites guardarHistoricoTramite(HistoricoTramites ht);
    
    public Observaciones guardarObservacion(Observaciones obs);
    
    public CatPredioPropietario guardarCatPredioPropietario(CatPredioPropietario propietario);
    
    public CatPredio guardarCatPredio(CatPredio predio);
    
    public List<CtlgItem> obtenerCtlgItemList();
    
    public Boolean actualizarHistoricoReporteTramite(HistoricoReporteTramite hrt);
    
    public Boolean actualizarCatPredioEdificacion(CatPredioEdificacion cpe);
    
    public Boolean actualizarHistoricoTramiteDetalle(HistoricoTramiteDet htd);
    
    public Boolean actualizarPredio(CatPredio predio);
    
    public Boolean actualizarPredioPropietario(CatPredioPropietario predio);
    
    public Boolean actualizarHistoricoTramite(HistoricoTramites ht);
    
    /**
     * Método booleano que indica si existe o no un predio en la base de datos.
     * 
     * @param predio
     * @return 
     */
    public Boolean existePredio(CatPredio predio);
    
    /**
     * Guarda las caracteristicas S4 y S6 de cada predio, al mismo tiempo actualiza
     * el histórico trámite.
     * 
     * @param predios
     * @param ht
     * @return 
     */
    public Boolean guardarCatPredioS4S6(List<CatPredio> predios, HistoricoTramites ht);
    
    /**
     * Guarda las caracteristicas S12 de cada predio.
     * 
     * @param predios
     * @return 
     */
    public Boolean guardarCatPredioS12(List<CatPredio> predios);
    
    /**
     * Toma la lista de predios que fueron generados y van a ser guardados. 
     * Todos los predios deben tener al menos un propietario antes de ser guardado.
     * 
     * @param predios
     * @return 
     */
    public Boolean guardarCatPredioDivisionPredio(List<CatPredio> predios);
    
    /**
     * Guarda las edificaciones de cada predio de la lista de predios que se le pasa como
     * parámetro.
     * 
     * @param predios
     * @return 
     */
    public Boolean guardarEdificaciones(List<CatPredio> predios);
    
    /**
     * Guarda los datos de la tasa de liquidación. También guarda todo lo necesario
     * para la culminación de la tarea.
     * 
     * @param obs
     * @param predio
     * @param htd
     * @param propietariosList
     * @param hrtList
     * @param htdList
     * @param hrt
     * @return 
     */
    public Boolean guardarTasaDeLiquidacion(Observaciones obs, CatPredio predio, HistoricoTramiteDet htd, List<CatPredioPropietario> propietariosList, List<HistoricoReporteTramite> hrtList, List<HistoricoTramiteDet> htdList, HistoricoReporteTramite hrt);
    
}
