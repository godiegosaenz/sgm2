/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.financiero;

import com.origami.sgm.entities.CarteraVencida;
import com.origami.sgm.entities.TitulosPredio;
import com.origami.sgm.bpm.models.CatPredioModel;
import com.origami.sgm.bpm.models.CatPredioRuralModel;
import com.origami.sgm.bpm.models.Cobros;
import com.origami.sgm.bpm.models.ModelCarteraVencida;
import com.origami.sgm.bpm.models.ModelCarteraVencidaParroquia;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CoaAbogado;
import com.origami.sgm.entities.CoaEstadoJuicio;
import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.RecActasEspecies;
import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.entities.RecEspecies;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenIntereses;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenPago;
import com.origami.sgm.entities.RenParametrosInteresMulta;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.models.ParteRecaudaciones;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author HenryPilco
 */
@Local
public interface RecaudacionesService {

    public HistoricoTramites getHistoricoTramiteByNumTramite(Long tramite);

    public AclUser getAclUserByRol(Long idRol);

    public String getNameUserAssigne(Long user, Boolean flag);

    public List<AclUser> getUsuariosByRolId(Long id);

    public String getNameUserByRol(Long idRol);

    public List<RecEspecies> getEspeciesActivas();

    public List<RecActasEspeciesDet> getActasEspeciesActivas();

    public String saveActaEspecies(RecActasEspecies acta);

    public String reAsignarActaEspecies(List<RecActasEspeciesDet> list, Long idAclUser, String nameUser, BigDecimal valor);

    public List<CoaAbogado> getListAbogadosJuicios();

    public List<CoaEstadoJuicio> getListEstadosJuicios();

    public List<RenLiquidacion> getPagoAnualByPredioPendientesCoactiva(Long idPredio, Long idEstado);

    public List<RenLiquidacion> getEmisionesCoactivaAntigua(CatPredio predio);

    public List<CatPredioPropietario> getPropietariosActivosPredio(Long idPredio);

    public Boolean consultaJuicioByNumeroYanio(Integer numero, Integer anio);

    public Boolean guardarJuicioCoactivoAntiguo(CoaJuicio juicio, List<RenLiquidacion> list);

    public HistoricoTramites saveJuicioCoactivoNuevo(HistoricoTramites ht, CoaJuicio juicio, List<RenLiquidacion> list);

    public Boolean updateEmisionesJuicio(Long idJuicio);

    public List<RenLiquidacion> getLiquidacionesCoactivaByJuicio(Long idJuicio);

    public RecActasEspeciesDet getActaByEspecieYUser(Long especie, Long user);

    /**
     * Genera la Emision del Pago Predial Anual
     *
     * @param predio
     * @param anio
     * @param usuario
     * @return
     */
    public RenLiquidacion grabarEmisionPredial(CatPredio predio, Long anio, AclUser usuario);

    /**
     * Actualiza la emision predial Descuento/Recarga
     *
     * @param emision
     * @param fechaPago
     * @return
     */
    public RenLiquidacion realizarDescuentoRecargaInteresPredial(RenLiquidacion emision, Date fechaPago);

    public RenLiquidacion realizarPagosCoactiva(List<RenLiquidacion> emisiones, String cajero);

    public RenLiquidacion realizarUnPagoCoactiva(RenLiquidacion liquidacion, BigDecimal total, String cajero);

    public RenPago realizarPago(RenLiquidacion liquidacion, RenPago pago, AclUser cajero, Boolean isSac);

    public RenLiquidacion editarLiquidacion(RenLiquidacion liquidacion);

    public RenLiquidacion grabarLiquidacion(RenLiquidacion liquidacion);

    public RenLiquidacion grabaLiquidacionRubro(RenLiquidacion liquidacion, RenDetLiquidacion detalle);

    public Boolean actualizaEmisionesCoactiva(Long idLiquidacion);

    public Object grabarMejora(Map<String, Object> parametros);

    public Object grabarMejora(List<Object> parametros);
    
    public Long grabarEmision(List<Object> parametros);
    
    public BigInteger grabarEmisionRural(List<Object> parametros);
    
    public void grabarEmisionGlobal(Map<String, Object> parametros);

    public void grabarEmisionRuralGlobal(Map<String, Object> parametros);

    public List<Cobros> getCobros(RenPago pago);

    public List<Cobros> getComprobante(RenLiquidacion liquidacion);

    public List<RenLiquidacion> getEmisionesByPredio(CatPredio p, Map<String, Object> parametros);

    public List<RenLiquidacion> getEmisionesByPredioRustico(CatPredioRustico pr, Map<String, Object> parametros);
    
    public List<RenLiquidacion> getEmisionesByPredioRustico2017(EmisionesRuralesExcel rural2017, Map<String, Object> parametros);
    
    public List<RenLiquidacion> getEmisionesByAME(String claveAME, Integer anio);

    public BigDecimal getTotalEmisionesByPredio(CatPredio p, Map<String, Object> parametros);

    public BigDecimal generarInteres(BigDecimal valor, Integer anio);

    public RenIntereses grabraInteres(RenIntereses interes);
    
    public CtlgSalario grabraSalario(CtlgSalario salario);

    public List<ParteRecaudaciones> parteRecaudacioneses(Date fechaParte, BigDecimal total);

    public List<CatPredio> getListPrediosByPropietario(Long idEnte);

    public List<CatPredio> getListPrediosByCodigoPredial(CatPredioModel model);

    public RenPago reversarPago(RenPago pago);

    public RenPago ultimoPago(RenLiquidacion liquidacion);

    public List<RenParametrosInteresMulta> getListParametrosInteresMulta(RenLiquidacion liquidacion);

    public BigDecimal generarMultas(RenLiquidacion liquidacion, RenParametrosInteresMulta interesMulta);

    public BigDecimal valorRecaudarCoactiva(BigDecimal valorTotal);

    public CoaAbogado grabarAbogado(CoaAbogado abogado);

    public boolean ultimaEspecie(RenLiquidacion liquidacion);
    
    public List<CatPredio> getListPrediosByPredioModel(CatPredioModel model);
    
    public List<CatPredioRustico> getListPrediosRuralesByPredioModel(CatPredioModel model);
    
    public FnSolicitudExoneracion aplicarSolicitud(FnSolicitudExoneracion solicitudExoneracion);
    
    public Boolean verificarPagoBanco(CatPredio predio);
    
    public Long cantidadPagosByLiquidacion(RenLiquidacion liquidacion);

    public List<CatPredioRuralModel> getListPrediosRuralModelByPredioModel(CatPredioModel model);

    public List<ModelCarteraVencidaParroquia> getCarteraModelParroquias(List<CatParroquia> parroquias);
    
    public List<ModelCarteraVencida> getCarteraModel(CatParroquia parroquia);
    
    public List<RenPago> getPagosByPredioTipoLiquidacionAnio(CatPredio predio, CatPredioRuralModel predioRural, RenTipoLiquidacion tipo,Integer desde, Integer hasta);
    
    public List<RenPago> getPagosByPredioTipoLiquidacionAnioPagada(CatPredio predio, CatPredioRuralModel predioRural, RenTipoLiquidacion tipo,Integer desde, Integer hasta);
    
    public List<CarteraVencida> getCarteraVencidaAME(String claveCatastralAnterior);
    
    public List<TitulosPredio> getTitulosPredioAME(String claveCatastralAnterior);
   
    ///DEVUELVE LISTADO DE PREDIOS AFECTADOS POR LAS ACTUALIZACIONES DE LAS LIQUIDACIONES RELACIONADAS AL PREDIO EN CUESTION
    public List<RenLiquidacion> updateLiquidacionesByIdTituloLiquidacion(List<String> idTitulosLiquidacion, CatPredio catPredio, Boolean segundaVuelta);
    
    public void emisionUrbana(Long idPredio, Long anio, Long idUsuario, BigDecimal avaluoSolar,  BigDecimal avaluoConstruccion, BigDecimal avaluoMunicipal, BigDecimal cemParquesPlazas,  BigDecimal cemAlcantarillado);
}
