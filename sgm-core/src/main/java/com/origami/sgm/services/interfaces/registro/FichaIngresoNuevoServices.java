/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.registro;

import com.origami.sgm.bpm.models.FichaIngreso;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatEscrituraRural;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.EnteCorreo;
import com.origami.sgm.entities.EnteTelefono;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoFicha;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Angel Navarro
 */
@Local
public interface FichaIngresoNuevoServices {

    public List<RegTipoFicha> getRegTipoFichaList();

    public List<CatParroquia> getCatPerroquiasListByCanton(Long idCanton);

    public List<RegMovimientoFicha> getRegMovimientoFichasList(Long id);

    public List<RegMovimientoFicha> getRegMovimientoFichasByMov(Long id);

    public List<RegMovimiento> getMovimientosByFicha(Long idFicha);

    public RegFicha getFichaByNumFichaByTipo(Long numFicha, Long tipo);

    public List<RegFicha> getFichasByRangoNumFichaByTipo(Long numFichaInicial, Long numFichaFinal, Long tipo);

    public CatPredio getPredioByNum(Long numPredio);

    public RegFicha getRegFichaNumPredio(Long numPredio);

    public RegFicha getRegFichaByEscrituraRural(Long idEscriRural);

    public List<CatCiudadela> getCiudadelasByParroquia(Long idParroquia);

    public CatEscritura getCatEscrituraByPredio(Long idPredio);

    public List<CatPredioPropietario> getPropietariosByPredio(Long idPredio);

    public CatEscrituraRural getCatEscrituraRural(Long regCatastral, Long identicadorPredial);

    public List<RegTipoBien> getTipoBienList(Boolean estado);

    public CatEnte getCatEnte(String ciRUC);

    public List<CatCiudadela> getCiudadelas();

    public List<String> getListNombresCdla();

    public RegFicha getRegFichaByPredio(Long id);

    public String guardarFichasDuplicadas(RegFicha matriz, Integer cantidad, String usuario, String linderos, List<RegMovimiento> movimientos);

    public Long saveRegFichaPredialUrbano(FichaIngreso ingreso, List<RegMovimiento> movimientos);

    public Long saveRegFichaPredialRural(FichaIngreso ingreso, List<RegMovimiento> movimientos);

    public CtlgItem getCtlgItemById(Long Id);

    public RegTipoFicha getRegTipoFichaById(Long id);

    public RegFicha guardarFichaBien(RegFicha ficha);

    public RegFicha guardarFicha(RegFicha ficha, List<RegMovimiento> movimientos);

    public Boolean guardarMovientos(RegFicha f, List<RegMovimiento> mov);

    public RegFicha guadarFichaYEscrituraRural(RegFicha f, CatEscrituraRural escRural);

    public RegFicha guardarFichaYEscritura(RegFicha f, CatEscritura escUrbana);

    public Boolean actualizarRegFicha(RegFicha regFichaHistorico);

    public CatEnte guardarCatEnte(CatEnte ente);

    public RegFicha getRegFichaByCodPredial(String numIdentificador, Long id);

    public CatEnte guardarTelefCorreosYContribuyente(CatEnte ente);

    public RegFicha guardarTelefCorreosYContribuyenteAndFicha(FichaIngreso ingreso, String nameUser);

    public RegEnteInterviniente guardaRegEnteInterviniente(RegEnteInterviniente inter);

    public RegEnteInterviniente buscaRegEnteInterv(String cedRuc, String nombre, String tipoInterv);

    public RegEnteInterviniente updateRegEnteInterviniente(RegEnteInterviniente inter);

    public RegFicha getRegFichaByIdFicha(Long idFicha);

    public void actualizarRegFichaBien(RegFicha ficha);

    public void actualizarCatEnteTelefEmails(CatEnte e, List<EnteCorreo> listCorreo, List<EnteTelefono> listTelefonos);

    public void actualizarRegFichaAndListMov(RegFicha ficha, List<RegMovimientoFicha> listMovFichElim);

    public void updateRegFicha(RegFicha ficha, CatEscritura escritura, List<RegMovimientoFicha> listMovFichElim, boolean permitido);

    public void updateCatEscritura(CatEscritura escritura);

    public void guardarRegMovimientoFicha(RegFicha ficha, List<RegMovimientoFicha> listNew);

    public void eliminarRegMovimientoFicha(List<RegMovimientoFicha> listMovFichElim);

    public List<RegFicha> getRegFichaByMovimientoId(Long id);

    public List<RegMovimientoCliente> getRegMovimientoClienteByMovimiento(Long id);

    public List<RegMovimientoRepresentante> getRegRegMovimientoRepresentanteByMovimiento(Long id);

    public List<RegMovimientoSocios> getRegMovimientoSociosByMovimiento(Long id);

    public List<RegMovimientoCapital> getRegMovimientoCapitalByMovimiento(Long id);

    public List<EnteCorreo> getEnteCorreoList(Long id);

    public CatEnte getCatEnteById(Long id);

    public void eliminarPropietarios(List<CatPredioPropietario> propietarios);

    public List<RegFicha> getFichasRegistralesByLinderos(String lindero, Long tipo);

    public CatEnte guardarCatEnteTelefEmails(CatEnte ente);

    public Boolean registrarImpresionFicha(RegFicha ficha, BigInteger anio, BigInteger numTramite);

    public List<CatEscritura> getCatEscrituraByPredioList(Long id);

    public boolean transferirPropietarios(List<RegMovimientoCliente> inter, CatPredio predio, List<CatPredioPropietario> propietarios, List<CatEnte> seleccionados, String usuario);

    public Boolean compruebaFichaTareaRegistro(Long liquidacion, Long tareaDinardap, Long numFicha);
    
    public List<CatPredioPropietario> getNewsPropietariosPredioByFicha(Long idFicha);
    
    public CatPredio getPredioByPredialant(String predialant);
    
}
