/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.financiero.bancos;

import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.bancos.ConsolidacionBanco;
import com.origami.sgm.entities.bancos.FormatoBanca;
import com.origami.sgm.financiero.bancos.models.FormatoUnificado;
import java.io.File;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.Local;

/**
 *
 * @author CarlosLoorVargas
 */
@Local
public interface ConsolidacionBancosServ {

    public List<RenLiquidacion> getLiquidacionesPendientes(Long tipoLiquidacion, Long estadoLiquidacion, Short sector, Integer periodo);

    public Future<List<FormatoUnificado>> getPagosPrediales(List<RenLiquidacion> liq);

    public List<String> getArchivo(List<FormatoUnificado> result, FormatoBanca formato);

    public List<ConsolidacionBanco> getProcesarPagos(File archivo, FormatoBanca formato);
}
