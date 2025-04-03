/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.registro;

import com.origami.sgm.bpm.models.InscripcionNuevaModel;
import com.origami.sgm.bpm.models.MovimientoModel;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CtlgCargo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CtlgTipoParticipacion;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegCapital;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Angel Navarro
 */
@Local
public interface InscripcionNuevaServices {

    public RegMovimiento getRegMovimientoById(Long id);

    public RegMovimiento getRegMovimientoByRegpCertificadoInscripcion(Long idTarea);

    public List<RegLibro> getRegLibroList();

    public List<RegActo> getActosByLibro(Long idLibro);

    public List<RegMovimiento> getMovReferenciaByMov(Long idMov);

    public List<CatCanton> getCatCantonList();

    public RegCatPapel getRegCatPapelById(Long id);

    public Collection<RegCatPapel> getRegCatPapelByActo(Long idacto);

    public RegEnteInterviniente getInterviniente(String cedula);

    public List<CtlgItem> lisCtlgItems(String catalogo);

    public List<CtlgCargo> ctlgCargos();

    public List<CtlgTipoParticipacion> getListParticipante();

    public List<RegCapital> getListRegCapital();

    public RegEnteInterviniente bucarRegInterv(String cedRuc, String nombre, String tipoInterv);

    public Integer getMaxInterviniente(String cedula, String tipoInterv);

    public RegEnteInterviniente guardaRegEnteInterviniente(RegEnteInterviniente nuevoInterviniente);

    public Boolean creaEnteDeInterviniente(RegEnteInterviniente interv);

    public RegEnteInterviniente updateRegEnteInterviniente(RegEnteInterviniente interv);

    public Boolean guardarInscripcionAntigua(RegMovimiento movimiento, List<RegMovimiento> movimientoReferenciaList,
            List<RegFicha> movimientoFichas, List<RegMovimientoCliente> movimientoClientes);

    public List<RegMovimientoCliente> guardarMovsClientes(List<RegMovimientoCliente> list, RegMovimiento mov);

    public List<RegMovimientoFicha> guardarMovsFichas(List<RegMovimientoFicha> list, RegMovimiento mov);

    public List<RegMovimientoReferencia> guardarMovsReferencia( List<RegMovimientoReferencia> list, RegMovimiento mov);
    
    public void guadarEntesMovimientos(List<RegMovimientoCliente> movimientoClienteList, List<RegMovimientoRepresentante> movimientoRepresentanteList, List<RegMovimientoSocios> movimientoSocioList);

    public RegMovimiento guardarMovimientoEdidicion(RegMovimiento movimiento, InscripcionNuevaModel inscripcion, MovimientoModel movimientoModel);

    public void inactivaPropieatariosFichas(List<RegMovimientoFicha> movsFich);
    
    public void saveFichaPropietarios(List<RegMovimientoCliente> movsCli, List<RegMovimientoFicha> movsFich, RegMovimiento mov);
    
    public Boolean saveOrUpdateEnteOfMovimientosList(RegMovimiento mov, Collection<RegMovimientoCliente> movimientoClienteList,
            Collection<RegMovimientoRepresentante> movimientoRepresentanteList, Collection<RegMovimientoSocios> movimientoSocioList,
            Collection<RegMovimientoCapital> movCapList, Collection<RegMovimientoFicha> movFichaList, Collection<RegMovimientoReferencia> movRefList);

    public Boolean deleteListsOfMovimiento(List<RegMovimientoCliente> movCliList, List<RegMovimientoRepresentante> movRepList,
            List<RegMovimientoCapital> movCapList, List<RegMovimientoSocios> movSocioList, List<RegMovimientoFicha> movFichaList,
            List<RegMovimientoReferencia> movRefList);

    public List<RegMovimientoReferencia> listMovimientoReferenciaByMov(Long id);

    public void guardarMovimientoClientes(List<RegMovimientoCliente> clientesNew, RegMovimiento movimiento);

    public void guardarMovimientoFicha(List<RegMovimientoFicha> fichasNew, RegMovimiento movimiento);

    public void guardarMovimientoReferencia(List<RegMovimiento> listadoMovimientosRef, List<RegMovimientoReferencia> listRefOld, RegMovimiento movimiento);

    public void guardarMovimientoRepresentante(List<RegMovimientoRepresentante> representante, RegMovimiento movimiento);

    public void guardarMovimientosSocios(List<RegMovimientoSocios> sociosNew, RegMovimiento movimiento);

    public void guardarMovimientoCapital(List<RegMovimientoCapital> capitalNew, RegMovimiento movimiento);

    public RegMovimientoReferencia getMovimientoReferenciaByMovReff(Long idReff, Long idMov);

    public RegMovimiento asignarNumRepertorioByAnioYporTipoLibro(int anio, RegMovimiento movimiento);

    public RegEnteInterviniente guardarRegEnteInterviniente(RegEnteInterviniente interv);

    public RegMovimiento guardarMovimientoNuevo(RegMovimiento movimiento, List<RegMovimientoReferencia> movimientoReferenciaList);

    public List<RegMovimiento> getRegMovimientosPorLibroAnio(Integer anio, Long idLibro);

    public BigInteger cantidadMovimientosXanioYlibro(Integer anio, Long libroId);
    
    public RegActo getActoByAbrev(String abrev);

    public RegEnteJudiciales getRegEnteJudicialByAbrev(String abrev);
    
}
