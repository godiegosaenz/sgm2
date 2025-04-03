/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful;

import com.origami.censocat.restful.JsonUtils;
import com.origami.sgm.entities.RenIntereses;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import util.Utils;
/**
 *
 * @author origami-idea
 */
@Named
@RequestScoped
@Path("interesesConsultaPagos")
@Produces({"application/Json", "text/xml"})
public class WSIntereses
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = Logger.getLogger(WSIntereses.class.getName());
  @Inject
  private RecaudacionesService recaudacionServices;
  @Inject
  private Entitymanager manager;
  private JsonUtils jsonUtils;
  
  public WSIntereses() {}
  
  @GET
  @Path("/valor/{valor}/fecha/{fecha}")
  public List<Double> getIntereses(@PathParam("valor") Double valor, @PathParam("fecha") Long fecha) {
    try {
      List<Double> interesDouble = new ArrayList();
      
      BigDecimal interes = generarInteres(new BigDecimal(valor.doubleValue()), Utils.getAnio(new Date(fecha.longValue())));
      if (interes == null) {
        interes = BigDecimal.ZERO;
      }
      interesDouble.add(Double.valueOf(interes.doubleValue()));
      return interesDouble;
    }
    catch (Exception e) {
      LOG.log(Level.SEVERE, "Copiar Ordenes.", e);
    }
    return null;
  }
  

  public BigDecimal generarInteres(BigDecimal valor, Integer anio)
  {
    System.out.println("anio " + anio);
    BigDecimal interesValor = new BigDecimal("0.00");
    try {
      RenIntereses interes = (RenIntereses)manager.find("SELECT i FROM RenIntereses i WHERE i.anio =:anio", new String[] { "anio" }, new Object[] { Integer.valueOf(anio.intValue() - 1) });
      if (interes != null) {
        interes.setPorcentaje(interes.getPorcentaje().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        interesValor = valor.multiply(interes.getPorcentaje()).setScale(2, RoundingMode.HALF_UP);
      }
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Copiar Ordenes.", e);
      return null; }
    RenIntereses interes;
    return interesValor;
  }
}