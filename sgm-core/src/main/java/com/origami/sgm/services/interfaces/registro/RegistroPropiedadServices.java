/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.registro;

import com.origami.sgm.bpm.models.ActosPorLiquidaciones;
import com.origami.sgm.bpm.models.ConsultaMovimientoModel;
import com.origami.sgm.bpm.models.DatoMercantilContrato;
import com.origami.sgm.bpm.models.DatoMercantilSocietario;
import com.origami.sgm.bpm.models.DatoPublicoRegistroPropiedad;
import com.origami.sgm.bpm.models.DatosTramite;
import com.origami.sgm.bpm.models.ReporteTramitesRp;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CatTransferenciaDominio;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegCertificado;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoCertificado;
import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpIntervinientes;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.VuItems;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Anyelo
 */
@Local
public interface RegistroPropiedadServices {

    public RegpLiquidacionDerechosAranceles guardarLiquidacionRegistro(RegpLiquidacionDerechosAranceles liq, String user, Boolean financiero);

    public Boolean editarLiquidacionRegistro(RegpLiquidacionDerechosAranceles liq, String user, Boolean financiero);

    public RegpLiquidacionDerechosAranceles guardarLiquidacionAntigua(RegpLiquidacionDerechosAranceles liq, HistoricoTramites ht);
    
    public Boolean grabaLiquidacionNuevaSgm(RegpLiquidacionDerechosAranceles liq, List<ActosPorLiquidaciones> actosAgrupados, List<RegpIntervinientes> listInterv, RenTipoLiquidacion tipo, Integer anio);
    
    //METODO DEPRECADO
    @Deprecated
    public Boolean comprobarEstadoPago(RegpLiquidacionDerechosAranceles liq, String numLiquidacion);

    //METODO DEPRECADO
    @Deprecated
    public Integer getEstadoPagoByLiquidacion(Long idLiq);

    public Integer getEstadoPagoLiquidacionSgm(Long idLiq);
    
    public VuItems saveVuItmenRegistro(VuItems item);

    public List<VuItems> getUsosDocumentos();

    public AclUser getUsuarioSupervisor();

    public AclUser getUsuarioDirector();

    public AclUser getUsuarioDigitalizador();

    public String getLiquidadoresRegistro();

    public List<AclUser> getUsuariosByRolId(Long id);

    public String getTecnicoRegistro(Integer cantidad);

    public String getAbogadoRegistro(Integer cantidad);

    public String getTecnicoCatastroRegistro(Integer cantidad);

    public String getUrlByTarea(Long id);

    public HistoricoTramites getHistoricoTramiteById(Long id);

    public Boolean updateUserConTareas(String user, Integer cantidad);

    public void iniciarTramiteRegistro(RegpLiquidacionDerechosAranceles liq, String nameUser, String observacion);

    public Integer getCantidadTareasByIdLiquidacion(Long id);

    public List<RegpCertificadosInscripciones> getListaCertfByLiquidacion(Long id, String tipo);

    public List<RegpCertificadosInscripciones> getListaTareasDinardap(Long id, String tipo);

    public List<RegpCertificadosInscripciones> saveListCertfByLiq(Long id, Integer cant, String tipo);

    public List<RegpCertificadosInscripciones> getListTareasByLiquidacion(Long id);

    public List<RegpCertificadosInscripciones> getListTareasCertfByLiquidacion(Long id);

    public List<RegpCertificadosInscripciones> guardarTareasCatastroByLiquidacion(Long id);

    public Integer getCantidadTareasTransferenciaDominio(String taskId, Long numSeguimiento);

    public List<RegpCertificadosInscripciones> saveTareasCatastroTransferencia(Long numSeguimiento);
    
    public List<RegpCertificadosInscripciones> saveTareasFinalScann(Long numSeguimiento);

    //public List<DatoPublicoRegistroPropiedad> consultaDinardapAnexoUno(String fechaInicio, String fechaFin);
    public List<DatoPublicoRegistroPropiedad> consultaDinardapAnexoUno(String fechaInicio);

    public List<DatoMercantilContrato> consultaDinardapAnexoDos(String fechaInicio, String fechaFin);

    public List<DatoMercantilSocietario> consultaDinardapAnexoTres(String fechaInicio, String fechaFin);

    public List<RegFicha> getRegFichaByIdRegMov(Long id);

    public List<RegMovimientoReferencia> getRegMovRefByIdRegMov(Long id);

    public List<RegMovimientoCapital> getRegMovCapitalByIdMov(Long id);

    public List<RegMovimientoCapital> getRegMovCapitalByIdMovCopy(Long id);

    public List<RegMovimientoCliente> getRegMovClienteByIdMov(Long id);

    public List<RegMovimientoCliente> getRegMovClienteByIdMovCopy(Long id);

    public List<RegMovimientoRepresentante> getRegMovRepresentByIdMov(Long id);

    public List<RegMovimientoRepresentante> getRegMovRepresentByIdMovCopy(Long id);

    public List<RegMovimientoSocios> getRegMovSociosByIdMov(Long id);

    public List<RegMovimientoSocios> getRegMovSociosByIdMovCopy(Long id);

    public List<RegMovimientoFicha> getRegMovByIdFicha(Long id);

    public Collection getListIdMovsByInterv(Long id);
    
    public Collection getListIdMovsByCedRucInterv(String documento);

    public Collection getListIdFichasByInterv(Long id);
    
    public Collection getListIdFichasByDocInterv(String documento);

    public Boolean guardarActoLiquidacion(RegpActosIngreso a);

    public Boolean actualizarActoLiquidacion(RegpActosIngreso a, Boolean flag);

    public List<CatTiposDominio> getCatTiposDominioList();

    public void updateCatPredio(CatPredio predio);

//    public CatEscritura guardarCambioPropietario(CatEscritura escrituraNew, PropietariosPredioHist hist, List<PropietariosPredioDetalleHist> histProp, List<CatPredioPropietario> propietariosNew, List<CatPredioPropietario> propietarios);

//    public void guardarPropietariosPredioHist(PropietariosPredioHist hist, List<PropietariosPredioDetalleHist> histProp);

    public Integer getNumerosReporteImpresosByAnioEscritura(Long anioTramite);

    public void eliminarPropietarios(List<CatPredioPropietario> propietarios);

    public List<RegTipoBien> getRegTipoBienList();

    public RegTipoBien guardarRegTipoBienAndRegTipoBienCaracteristica(RegTipoBien tipoBien);

    public Boolean updateRegTipoBienAndRegTipoBienCaracteristica(RegTipoBien tipoBien);

    public List<RegCapital> getRegCapitalList();

    public RegCapital guardarRegCapital(RegCapital capital);

    public Boolean updateRegCapital(RegCapital capital);

    public RegpLiquidacionDerechosAranceles cargarLiquidacionAnterior(Long id, String user);

    public List<ActosPorLiquidaciones> getActosAgrupadosPorLiquidacionId(Long liquidacion);

    public ConsultaMovimientoModel getConsultaMovimiento(Long idMov);

    public List<RegTipoCertificado> getListTipoCertificado();

    public RegCertificado getCertificadoByIdTarea(Long id);

    public Integer getMaxNumeroIndiceCertificado(BigInteger numTramite);

    public Integer getMaxNumeroIndiceCertificadoSine(Integer anio);

    public DatosTramite getTramiteAnterior(Long numTramite);

    public String getPapelByMovimientoInterviniente(Long mov, Long inter);
    
    public String getPapelByMovAndDocumentoInterv(Long mov, String doc);

    public Date getFechaInscripcionMenor();

    public Date getFechaInscripcionMayor();

    public Observaciones guardarObservaciones(HistoricoTramites ht, String nameUser, String observaciones, String taskDefinitionKey);

    public List<CtlgItem> lisCtlgItems(String catalogo);

    public Boolean saveTransferenciaDominio(CatPredio predio, List<CatPredioPropietario> delete, List<CatPredioPropietario> news);

    public CatTransferenciaDominio registrarTransferencia(CatTransferenciaDominio td, List<CatPredioPropietario> list);

    public RegpLiquidacionDerechosAranceles getLiquidacionByNumYFecha(BigInteger numTramite, String fecha);

    public List<ReporteTramitesRp> tramitesAsignadosRegistro(String fecha, Integer opcion);

    public String getNombresByUser(String user);
    
     public Boolean restarTareasTransferenciaUser(String user, Integer cant);
     
     public CatEnte saveEnteSinCedRuc(CatEnte ente);
     
     public Integer getCantidadCertificadosRealizados(String desde, String hasta);
     
     public Integer getCantSolicitantes(Integer tipo, String desde, String hasta);
     
     public Integer getCantInscripcionesByTramite(Long tramite);
     
     public RegMovimiento getRegMovimientoById(Long id);

}
