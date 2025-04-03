/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.catastro;

import com.origami.sgm.entities.AvalBandaImpositiva;
import com.origami.sgm.entities.AvalValorSuelo;
import com.origami.sgm.entities.AvalCoeficientesSuelo;
import com.origami.sgm.entities.AvalDetCobroImpuestoPredios;
import com.origami.sgm.entities.AvalEdadZonaConst;
import com.origami.sgm.entities.AvalImpuestoPredios;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.historic.ValoracionPredial;
import com.origami.sgm.predio.models.AvaluosModel;
import com.origami.sgm.services.interfaces.models.avaluos.ResultadoAvaluos;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface AvaluosServices {

    public Future<ResultadoAvaluos> getEmisionGeneral(String user, Integer periodo, Integer version);

    public Object getNumVersion();

    public Future<ValoracionPredial> getEmisionPredial(String user, Integer periodo, BigInteger numPredio, Boolean normal);

    public ValoracionPredial getDatosPredioBase(Integer periodo, BigInteger numPredio, String user);

    public List<ValoracionPredial> prediosRecalculados();

    public Boolean actualizarDatosVersion(ValoracionPredial h, Boolean normal);

    public ValoracionPredial recalcular(String usuario, Long liquidacion, Boolean normal);

    public Future<Boolean> registrarLiquidacion(ValoracionPredial v, String user);
    
    public Future<Boolean> registrarLiquidacion(CatPredio cp, Integer periodo, String user);

    public Future<List<AvaluosModel>> getLiquidacionesPagadas(Long liquidacion);
    
    public AvalValorSuelo saveAvaluoCategoriaValorM2(AvalValorSuelo avalValorSuelo);
    
    public AvalBandaImpositiva saveOrUpdateAvalBandaImpositiva(AvalBandaImpositiva avalBandaImpositiva); 
    
    public Boolean saveAvalCoeficientesSuelo(AvalCoeficientesSuelo coefPadre, List<AvalCoeficientesSuelo> coeficientesFactoriales, Boolean control, Integer anioCoeficientesSolarInicio, Integer anioCoeficientesSolarFin, Integer anioCoeficientesConstruccionInicio, Integer anioCoeficientesConstruccionFin );

    public Boolean saveAvalEdadZonaConst(AvalEdadZonaConst avalEdadZonaConst);
    
    public Object generateAvaluo(List<CatPredio> prediosSeleccionados, Integer anioValorizarInicio, Integer anioValorizarFin, Boolean control, String usuario);
    
    public Object generateEmisionPredial(List<CatPredio> prediosSeleccionados, Integer anioValorizarInicio, Integer anioValorizarFin, Boolean control, String usuario);
    
    public AvalImpuestoPredios saveAvalImpuestoPredios(AvalImpuestoPredios avalImpuestoPredios);
    
    public AvalDetCobroImpuestoPredios saveAvalDetCobroImpuestoPredios(List<AvalDetCobroImpuestoPredios> cobroImpuestoPrediosList);
    
    public AvalImpuestoPredios saveAvalImpuestoPrediosAndDetCobro(AvalImpuestoPredios avalImpuestoPredios, List<AvalDetCobroImpuestoPredios> cobroImpuestoPrediosList);
    
    
}
