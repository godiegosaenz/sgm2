/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.rentas;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.CtlgSalario;
import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import com.origami.sgm.entities.FnSolicitudExoneracionPredios;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenActivosLocalComercial;
import com.origami.sgm.entities.RenAfiliacionCamaraProduccion;
import com.origami.sgm.entities.RenBalanceLocalComercial;
import com.origami.sgm.entities.RenClaseLocal;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RenEntidadBancaria;
import com.origami.sgm.entities.RenEstadoLiquidacion;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenLocalCantidadAccesorios;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.RenTipoLocalComercial;
import com.origami.sgm.entities.RenTipoValor;
import com.origami.sgm.entities.RenTurismo;
import com.origami.sgm.entities.RenTurismoServicios;
import com.origami.sgm.entities.RenValoresPlusvalia;
import com.origami.sgm.entities.models.HabitacionTurismo;
import com.origami.sgm.entities.models.LiquidacionesPermisosFuncionamiento;
import com.origami.sgm.services.interfaces.datoSeguro.DatoSeguroServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 *
 * @author Joao Sanga
 */
@Local
public interface RentasServices {

    public PermisoConstruccionServices permisoServices();

    public RenEntidadBancaria guardarBanco(RenEntidadBancaria banco);

    public Long existeRenEntidadBancaria(String descripcion);

    public List<RenEntidadBancaria> getBancos(Long tipo);

    public Boolean guardarRubrosPorLiquidacion(List<RenDetLiquidacion> list);

    public Boolean guardarRubrosPorTipoLiquidacion(List<RenRubrosLiquidacion> list);

    public RenLiquidacion guardarLiquidacion(RenLiquidacion liquidacion, List<RenRubrosLiquidacion> rubrosLiquidacion, String prefijo, RenValoresPlusvalia valores);

    public RenTipoLiquidacion getTipoLiquidacion(Long tipoRubros);

    public RenEstadoLiquidacion getEstadoLiquidacionByDesc(Long idEstado);

    public List<RenTipoLiquidacion> getTiposLiquidacionList(Long l);

    public List<RenTipoLiquidacion> getTiposLiquidacionList();

    public RenDesvalorizacion getDesvalorizacionAnio(Integer anio);

    public RenDesvalorizacion guardarDesvalorizacion(RenDesvalorizacion desvalorizacion);

    public List<RenRubrosLiquidacion> getRubrosPorLiquidacion(Long idTipo);

    public MatFormulaTramite getMatFormulaByPrefijo(String prefijo);
    
    public RenLocalComercial guardarLocalComercial(RenLocalComercial local, RenActivosLocalComercial activosLocal);
    
    public HistoricoTramites guardarMultaFlujoInit(Observaciones obs, CmMultas multa, HistoricoTramites ht);

    /**
     * Recibe uno para consultar por titulo de reporte de plusvalia y alcabalas
     * y 2 pra locales comerciales
     *
     * @param tipo 1 - Plusvalia y alcabalas 2 - Local Comercial
     * @return {@link List<RenTipoLiquidacion>}
     */
    public List<RenTipoLiquidacion> gettiposLiquidacionByCodTitRep(int tipo);

    public RenLiquidacion guardarLiquidacionYRubros(RenLiquidacion liq, List<RenDetLiquidacion> rubros, RenTipoLiquidacion tipoLiq, RenActivosLocalComercial local, RenBalanceLocalComercial balance, HistoricoTramites ht, String numLiquidacion);

    public FnExoneracionClase guadModExoneracion(FnExoneracionClase exoneracion);

    public List<FnExoneracionClase> FnExoneraciones(Boolean b, int tipo);

    public List<FnExoneracionTipo> FnExoneracionesTipo(Boolean b, Long idExon);

    /**
     * Recibe los Id de la tabla {@link RenTipoValor} para realizar la busqueda
     * por select In
     *
     * @param ids Id de la Tabla {@link RenTipoValor}
     * @return Lista de {@link RenTipoValor}
     */
    public List<RenTipoValor> tipoValorList(Set<Long> ids);

    public FnSolicitudExoneracion registraSolicitudExoneracion(FnSolicitudExoneracion SolExo, Long user, List<CatPredio> predios, String obs);

    public FnSolicitudExoneracion registraSolicitudExoneracionRust(FnSolicitudExoneracion SolExo, Long userId, List<CatPredioRustico> prediosRusticos, String obs);
    
    public GeTipoTramite geTipoTramiteByAbr(String abreviatura);

    public Boolean actualizarsolicitudExoneracion(FnSolicitudExoneracion SolExo);

    public FnResolucion guardarResolucion(FnResolucion resolucion);

    public List<FnSolicitudExoneracion> verficarSolicitudExoneracion(FnExoneracionTipo idTipoExoneracion, Integer anioInicio, Integer anioFin, CatPredio predio);

    public GeTipoTramite buscarTipoTramite(GeDepartamento dep, String abr);

    public GeTipoTramite buscarTipoTramiteDep(Long idDep, String abr);

    public DatoSeguroServices getDatoSeguro();

    public CatEnte consultarEnte(String cedula, boolean compania, String UserCreacion);
    
    public List<FnExoneracionLiquidacion> aplicarExoneracion(HistoricoTramites ht, FnSolicitudExoneracion sol, String username);
    
    public Boolean rechazarExoneracion(HistoricoTramites ht);
    
    public Boolean corregirExoneracion(HistoricoTramites ht);

    public Object getSolicitudExoneracion(HistoricoTramites ht);

    public Observaciones guardarObservacionCraerResolucion(FnSolicitudExoneracion sol, FnResolucion res, Observaciones obs);
    /**
     * 
     * @param predio
     * @param sol
     * @param esUrbano
     * @return 
     */
    public List<RenLiquidacion> getPagosPredio(CatPredio predio, FnSolicitudExoneracion sol, Boolean esUrbano);

    public List<FnSolicitudExoneracion> verficarSolicitudExoneracionRust(FnExoneracionTipo exoneracionTipo, Integer anioInicio, Integer anioFin, CatPredioRustico prop1);

    public List<RenLiquidacion> getPagosPredioRusticos(CatPredioRustico prop1, FnSolicitudExoneracion SolExo);

    public List<CatPredio> getPredios(CatEnte comprador);

    public Boolean generarNumLiquidacion(RenLiquidacion liquidacion, String prefijo);

    public CtlgSalario getSalarioBasico(Integer anio);
    
    public RenTasaTurismo guardarTasaTurismo(RenTasaTurismo tasa);
    
    public Boolean editarRenTipoLiquidacion(RenTipoLiquidacion tipoLiq);
    
    public Long generarCodTitRep();
    
    public RenRubrosLiquidacion guardarRubroNuevo(RenRubrosLiquidacion rubro, RenTipoLiquidacion tipoLiquidacion);
    
    public Boolean eliminarRubro(RenRubrosLiquidacion rubro, RenTipoLiquidacion tipoLiquidacion);

    public Long obtenerNumeroCondonacion();

    public List<RenLocalCantidadAccesorios> getAcesorios(RenLocalComercial localSel);

    public List<RenTasaTurismo> getTasasTurismo(RenLocalComercial localSel);

    public List<RenActividadComercial> getActividadesLocal(RenLocalComercial loc);

    public List<FnExoneracionTipo> FnExoneracionesTipoByAplica(List aplica);

    public List<FnSolicitudExoneracion> getSolicitudesAutomaticas(Integer anio, List<FnExoneracionTipo> tipos);

    public Boolean verificarSolicitanteSolicutud(FnSolicitudExoneracion solicitudExoneracion);

    //public Boolean generarExoneracionAuto(FnSolicitudExoneracion solicitud, String userSession);

    public RenLocalComercial getLocalById(RenLiquidacion liquidacion);

    public RenActivosLocalComercial getActivosLocal(RenLiquidacion liquidacion);

    public RenBalanceLocalComercial getBalanceLocal(RenLiquidacion liquidacion);

    public List<RenClaseLocal> getClasesLocal();

    public List<RenAfiliacionCamaraProduccion> getAfiliacionCamara();

    public List<GeRequisitosTramite> requisitosTramite(boolean b);

    public List<RenLiquidacion> getLiquidacionesLocal(RenLocalComercial local, Integer anio);

    public boolean actividadTuristica(RenActividadComercial actl, RenTipoLocalComercial categoria);

    public RenPermisosFuncionamientoLocalComercial guadarPermisoFuncionamiento(RenPermisosFuncionamientoLocalComercial permisosFuncionamiento, CatEnte contribuyente, Long isUser, RenActivosLocalComercial activos, RenBalanceLocalComercial balance);

    public Boolean updateHtAndPermisoFunc(HistoricoTramites hts, RenPermisosFuncionamientoLocalComercial permisosFuncionamiento, Observaciones o);

    public RenRubrosLiquidacion getRubroById(Long idRubro);
    
    public RenTurismo guardarTurismo(RenTurismo turismo, Observaciones obs, List<HabitacionTurismo> detalleHabitaciones);

    public RenTurismoServicios guardarTurismoServicios(RenTurismoServicios servicios);

    public List<RenLiquidacion> guardarPermisosFuncionamiento(RenPermisosFuncionamientoLocalComercial permiso, LiquidacionesPermisosFuncionamiento actAnual, LiquidacionesPermisosFuncionamiento tasaHab, LiquidacionesPermisosFuncionamiento rotulos, LiquidacionesPermisosFuncionamiento turismo, LiquidacionesPermisosFuncionamiento patente, String usuarioCreador);

    public List<RenLocalUbicacion> getUbicaciones();

    public List<RenLocalCategoria> getCategorias();

    public List<RenTipoLocalComercial> getTipoLocals();

    public RenLocalCategoria getCategiriaByDescripcion(String descripcion);

    public RenActividadComercial guardarActividad(RenActividadComercial actividad);
    
    public Boolean existePermiso(RenLocalComercial local);

    /**
     * 
     * @param liquidacion
     * @return 1 por Pagar</br>
     * 2 Pagado </br>
     * 3 No existe.
     */
    public Integer existeLiquidacion(RenLiquidacion liquidacion);

    public boolean inactivarLiquidaciones(List<RenLiquidacion> liquidaciones);
    
    public FnSolicitudExoneracion registarDatoSolicitudExoneracion(FnSolicitudExoneracion solicitud, List<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionesExoneracion, List<FnSolicitudExoneracionPredios> prediosSolicitud);

    public boolean eliminarTitulo(RenTipoLiquidacion tipoLiq);

    public List<CatPredio> getPrediosByEnte(CatEnte solicitante);
    
     public BigDecimal interesCalculado(RenLiquidacion emision, Date hasta);
    
}