/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.RecActasEspecies;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RenSecuenciaNumComprobante;
import com.origami.sgm.entities.UserConTareas;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface SeqGenMan {

    public BigInteger getSequence(String query, String[] params, Object[] values);

    public Object getSequences(String query, String[] params, Object[] values);

    public RegpLiquidacionDerechosAranceles saveRegpLiqDerAranc(RegpLiquidacionDerechosAranceles liq);

    public UserConTareas getUserConMenosTareas(Long rol, Integer cantidad);

    public RegFicha savRegFichaPredialSecuencia(RegFicha ficha);
    
    public Long getMaxNumeroFichaByTipo(Long tipoFicha);
    
    public Long saveListFichasPredial(List<RegFicha> list, List<RegMovimiento> movimientos);

    public RegEnteInterviniente getMaxInterviniente(RegEnteInterviniente interv);

    public RegMovimiento getMaxNumRepertRegMovimiento(RegMovimiento mov, int anio);
    
    public List<RegpCertificadosInscripciones> getRepertoriosPorAnio(Integer anio, Integer cantidad, Long idLiq);

    public RegMovimiento getNumInscripcionRegmovimientobyAnioLibro(int anio, Long idLibro, RegMovimiento movimiento);

    public RecActasEspecies maxNumeroEspecie(RecActasEspecies acta);
    
    public Long getSecuenciasTram(String app);

    public PePermiso getSequences(PePermiso permisoNuevo);

    /**
     * Consulta el ultimo Número de predio generado en CatPredio y lo incrementa
     * en uno, se asigna el número de predio al campo numPredio y envia a
     * persistir la entiti para retornar la misma entiti persistida.
     *
     * @param predio Entiti CatPredio
     * @return CatPredio
     */
    public CatPredio generarNumPredioAndGuardarCatPredio(CatPredio predio);

    public Long getMaxSecuenciaTipoTramite(Integer anio, Long idTipoTramite);

    public CatEnte guardarOActualizarEnte(CatEnte ente);
    
    public BigInteger getMaxSecuenciaTipoLiquidacion(Integer anio, Long idTipoLiquidacion);
    
    public Long getNumComprobante();

}
