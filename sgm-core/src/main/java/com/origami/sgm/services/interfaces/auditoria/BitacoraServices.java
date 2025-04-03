/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.auditoria;

import com.origami.sgm.bpm.models.MovimientoModel;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.enums.ActividadesTransaccionales;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface BitacoraServices {

    public Object registrarFicha(RegFicha f, ActividadesTransaccionales actividadTransaccional, BigInteger periodo, BigInteger orden);

    public Object registrarMovimiento(MovimientoModel movimientoModel, RegMovimiento m, ActividadesTransaccionales actividadTransaccional, BigInteger periodo);

    public boolean registrarFichaMov(RegFicha f, RegMovimiento mov, ActividadesTransaccionales actividadTransaccional, BigInteger periodo);

    public boolean registrarFichaMovs(RegFicha f, List<RegMovimiento> movs, ActividadesTransaccionales actividadTransaccional, BigInteger periodo);

    public boolean registrarMovFichas(RegMovimiento m, List<RegFicha> fs, ActividadesTransaccionales actividadTransaccional, BigInteger periodo);

    public boolean registrarMovMovs(RegMovimiento m, List<RegMovimiento> movs, String actividad, BigInteger periodo);

    public String actividadTransaccion(ActividadesTransaccionales actividad, RegFicha ficha, MovimientoModel movimientoModel, RegMovimiento movimiento);

}
