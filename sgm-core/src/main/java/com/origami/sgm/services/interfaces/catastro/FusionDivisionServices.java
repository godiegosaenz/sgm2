/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.catastro;

import com.origami.sgm.entities.AvalValorSuelo;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.enums.TipoProceso;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Xndy Sxnchez :v
 *
 */
public interface FusionDivisionServices {

    public void savePredioFusionDivision(CatPredio predioRaiz, CatPredio predioResultante, String tipo);

    public CtlgItem getTipoFraccionPredio(String tipo);

    public AvalValorSuelo getValorBaseM2Avaluo(CatPredio s1, Short solar);
    
    /**
     * Realiza la division de predios
     *
     * @param predioRaiz
     * @param predioDivision
     * @return
     */
    public ArrayList<CatPredio> saveDivisionPredio(CatPredio predioRaiz, ArrayList<CatPredio> predioDivision);

    public CatPredio registrarPredio(CatPredio px);

    public String claveCatastral(CatPredio px);

    public BigInteger generarNumPredio();

    public CatPredio clonePredioWithOutId(CatPredio predio, CatPredio resultante);

    public CatPredio saveAllDataPredio(CatPredio predio);

    public CatPredioEdificacion registrarEdificacion(CatPredioEdificacion edificacion);

    public CatPredioEdificacion clonarEdificacion(CatPredioEdificacion edificacion, CatPredio predio, Short nroEdificacion, boolean save);

    public CatPredioEdificacionProp registrarEdificacionProp(CatPredioEdificacionProp detalle);

    public CatPredioEdificacionProp clonarEdificacionProp(CatPredioEdificacionProp detalle, CatPredioEdificacion edificacion, CatEdfProp prop, boolean save);

    public CatPredioS4 registrarPredioS4(CatPredioS4 predioS4);

    public CatPredioS4 clonarPredioS4(CatPredioS4 predioS4, CatPredio predio, boolean save);

    public CatPredioS6 registrarPredioS6(CatPredioS6 predioS6);

    public CatPredioS6 registrarPredioS6(CatPredioS6 predioS6, Collection<CtlgItem> vias, Collection<CtlgItem> instalaciones);

    public CatPredioS6 clonarPredioS6(CatPredioS6 predioS6, CatPredio predio, Collection<CtlgItem> vias, Collection<CtlgItem> instalaciones, boolean save);

    public Collection<FotoPredio> cambiarPredio(CatPredio old, CatPredio predio);

    public void saveEdificacionesPredio(CatPredio predioRaiz, CatPredio predioResultante);

    public void saveFotosPredio(CatPredio predioRaiz, CatPredio predioResultante);

    public CatPredioPropietario clonarPropietario(CatPredioPropietario propietario, CatPredio predio);

    public CatPredio clonarPredioFusion(CatPredio predio, List<CatPredio> predios, Short codLote, boolean withEvent);
    

    public CatPredio clonarPredio(CatPredio predio, Short codProvincia);

    public void copiarInfoPredios(CatPredio predio, List<CatPredio> predios);

    /**
     * Rea;liza la division del predio
     *
     * @param numeroDivisiones Numero de divisiones a realizar
     * @param predio Predio a dividir
     * @return Lisado de Predios generados.
     */
    public List<CatPredio> dividirPredios(Integer numeroDivisiones, CatPredio predio);

    public List<CatPredio> registrarPredioGenerados(List<CatPredio> predios);

    public void actualizarEstadoPredioTramite(Long idTramite, CatPredio predio, Boolean estado);

    /**
     * Actualiza la columna numTasa de HistoricoTramiteDet que es la columna que
     * tomamos como estado para el registro de los tramites de division de
     * predios
     *
     * @param idTramite id del HistoricoTramites que es la columna idTramite
     * @param predio Id del predio
     * @param estado 1 Aprobado y 2 Sin Aprobar
     * @param tipo
     */
    public void actualizarEstadoPredioTramite(Long idTramite, CatPredio predio, Integer estado, TipoProceso tipo);

    public void actualizarDetalleTramite(Long idTramite, CatPredio predio, boolean estado, BigInteger numero, TipoProceso tipo);

    public CatastroServices getCatas();

    /**
     * Consulta el listado de todos los propietarios activos de un predio
     * @param predio
     * @return
     */
    public List<CatPredioPropietario> getPredioPropietarios(CatPredio predio);

    public void registrarPredios(List<CatPredio> prediosGenerados, CatPredio predioMatriz);
    
    public CatPredio clonarPredio(CatPredio predio, Short bloque, Short piso, Short unidad, BigInteger numP);
    
    public AvalValorSuelo saveValorM2Division(AvalValorSuelo suelo, Short solar);
    
    
    
}
