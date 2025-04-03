/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.financiero.bancos;

import com.origami.sgm.database.QuerysFinanciero;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.bancos.BancaDiccionario;
import com.origami.sgm.entities.bancos.ConsolidacionBanco;
import com.origami.sgm.entities.bancos.FormatoBanca;
import com.origami.sgm.financiero.bancos.models.EnteModel;
import com.origami.sgm.financiero.bancos.models.FormatoUnificado;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.sgm.services.interfaces.financiero.bancos.ConsolidacionBancosServ;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class ConsolidacionBancosEjb implements ConsolidacionBancosServ {

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private RecaudacionesService recaudacion;

    private static final Logger logx = Logger.getLogger(ConsolidacionBancosEjb.class.getName());

    @Override
    public List<RenLiquidacion> getLiquidacionesPendientes(Long tipoLiquidacion, Long estadoLiquidacion, Short sector, Integer periodo) {
        List<RenLiquidacion> liqs = manager.findAll(QuerysFinanciero.getRenLiquidacionesByTipoLiquidacionEstadoPeriodo, new String[]{"tipoLiquidacion", "estadoLiquidacion", "sector", "anioAnt", "anioAct"}, new Object[]{tipoLiquidacion, estadoLiquidacion, sector, (periodo - 1), periodo});
        try {
            for (RenLiquidacion e : liqs) {
                e = recaudacion.realizarDescuentoRecargaInteresPredial(e, new Date());
                e.calcularPagoConCoactiva();
                //System.out.println("Predio " + e.getPredio().getNumPredio() + " - " + e.getPagoFinal());
            }
        } catch (Exception e) {
            logx.log(Level.SEVERE, null, e);
        }
        return liqs;
    }

    public FormatoUnificado getFormato(RenLiquidacion e) {
        FormatoUnificado fm = null;
        try {
            fm = new FormatoUnificado();
            fm.setLiquidacion(e.getId());
            if (fm.getComprobante() != null) {
                fm.setComprobante(e.getNumComprobante().longValue());
            } else {
                fm.setComprobante(e.getId());
            }
            fm.setPeriodo(e.getAnio().toString());
            fm.setEmision(e.getTotalPago().toString().replace(".", ""));
            BigDecimal adicionales = e.getRecargo().add(e.getInteres().add(e.getValorCoactiva()));
            if (e.getCoactiva()) {
                fm.setVvCoactiva("0");
            } else {
                fm.setVfCoactiva("1");
            }
            //System.out.println("valor adicional " + adicionales + " PF " + e.getPagoFinal());
            if (adicionales != null && adicionales.compareTo(BigDecimal.ZERO) > 0) {
                fm.setAdicional(adicionales.toString().replace(".", ""));
            } else {
                fm.setAdicional(BigDecimal.ZERO.toString());
            }
            if (e.getPagoFinal() != null) {
                fm.setTotal(e.getPagoFinal().toString().replace(".", ""));
            }
            if (e.getPredio() != null) {
                CatPredio cp = e.getPredio();
                fm.setNumPredio(cp.getNumPredio().toString());
                try {
                    fm.setClaveCatastral(cp.getCodigoPredialCompleto());
                    fm.setClaveFormatoG(cp.getCodigoPredialCompletoFormatoG());
                    fm.setClaveFormatoP(cp.getCodigoPredialCompletoFormatoP());
                    fm.setClaveFormatoSF(cp.getCodigoPredialCompletoSinFormato());
                    if (cp.getPredialant() != null) {
                        fm.setClaveAnterior(cp.getPredialant().replace("-", "."));
                    }
                    if (cp.getCiudadela() != null) {
                        fm.setCalle(cp.getCiudadela().getNombre().toUpperCase().trim());
                    }
                } catch (Exception ex) {
                    System.out.println("Sin predio " + ex.getMessage());
                }
                try {
                    if (e.getComprador() != null) {
                        EnteModel em = this.getDatosContribuyente(e.getComprador());
                        fm.setIdentificacion(em.getIdentificacion());
                        fm.setTipoPersona(em.getTipoPersona());
                        fm.setContribuyente(em.getDescripcion().trim());
                        fm.setDescPersona(em.getDescPersona());
                    } else {
                        fm.setTipoPersona("4");
                        fm.setIdentificacion("0000000000");
                        fm.setContribuyente(e.getNombreComprador().trim());
                        fm.setDescPersona("C");
                    }
                } catch (Exception ex) {
                    System.out.println("Propietarios " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logx.log(Level.SEVERE, null, ex);
        }
        return fm;
    }

    @Asynchronous
    @AccessTimeout(-1)
    @Override
    public Future<List<FormatoUnificado>> getPagosPrediales(List<RenLiquidacion> liq) {
        List<FormatoUnificado> resultado = new ArrayList<>();
        try {
            int x = 0;
            for (RenLiquidacion e : liq) {
//                e = recaudacion.realizarDescuentoRecargaInteresPredial(e, new Date());
//                e.calcularPagoConCoactiva();
                System.out.println("x " + x++);
                resultado.add(getFormato(e));
            }
        } catch (NumberFormatException e) {
            logx.log(Level.SEVERE, null, e);
        }
        return new AsyncResult<>(resultado);
    }

    @Override
    public List<String> getArchivo(List<FormatoUnificado> result, FormatoBanca formato) {
        List<String> ob = new ArrayList<>();
        StringBuilder sb;
        int x = 0, ref = 0;
        try {
            if (formato != null) {
                Collection<BancaDiccionario> dc = formato.getBancaDiccionarioCollection();
                ref = dc.size();
                for (FormatoUnificado r : result) {
                    Object obj = r;
                    Class cls = obj.getClass();
                    Field f;
                    sb = new StringBuilder();
                    for (BancaDiccionario d : dc) {
                        if (d.getReproceso()) {
                            continue;
                        }
                        x++;
                        f = getCampo(cls, d.getAtributo());
                        f.setAccessible(true);
                        if (d.getReqValRef()) {
                            if (d.getReqValRefAdic()) {
                                if (f.get(obj) != null) {
                                    if (f.get(obj).equals(d.getValorRef())) {
                                        f.set(obj, d.getValRefAdic());
                                    }
                                }
                            } else {
                                f.set(obj, d.getValorRef());
                            }
                        }
                        if (f.get(obj) != null) {
                            if (f.get(obj).toString().length() > d.getMaxContenido()) {
                                String vtemp = f.get(obj).toString().substring(0, d.getMaxContenido());
                                f.set(obj, vtemp);
                            } else if (f.get(obj).toString().length() < d.getMaxContenido()) {
                                if (d.getTipo().contains("Integer")) {
                                    if (f.getName().equalsIgnoreCase("adicional")) {
                                        BigInteger vadic = new BigInteger(f.get(obj).toString());
                                        String operador = "";
                                        if (vadic.compareTo(BigInteger.ZERO) > 0) {
                                            if (d.getReqOperador()) {
                                                operador = "+";
                                            }
                                        } else {
                                            if (d.getReqOperador()) {
                                                operador = "-";
                                            }
                                        }
                                        if (!d.getReqOperador()) {
                                            if (f.get(obj).toString().contains("+")) {
                                                f.set(obj, f.get(obj).toString().replace("+", ""));
                                            }
                                            if (f.get(obj).toString().contains("-")) {
                                                f.set(obj, f.get(obj).toString().replace("-", ""));
                                            }
                                        }
                                        f.set(obj, operador + String.format(d.getComplemento(), new BigInteger(f.get(obj).toString())));
                                    } else {
                                        if (d.getComplemento() != null) {
                                            f.set(obj, String.format(d.getComplemento(), new BigInteger(f.get(obj).toString())));
                                        }
                                    }
                                } else {
                                    StringBuilder esb = new StringBuilder();
                                    for (int i = f.get(obj).toString().length(); i < d.getMaxContenido(); i++) {
                                        esb.append(" ");
                                    }
                                    if (d.getSupVacios()) {
                                        f.set(obj, f.get(obj).toString() + esb.toString().trim());
                                    } else {
                                        f.set(obj, f.get(obj).toString() + esb.toString());
                                    }
                                }
                            }
                            if (x == ref) {
                                x = 0;
                                sb.append(f.get(obj).toString());
                            } else {
                                sb.append(f.get(obj)).append(formato.getDelimitador());
                            }
                        }
                    }
                    x = 0;
                    //System.out.println(sb.toString());
                    ob.add(sb.append("\r\n").toString());
                }
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            logx.log(Level.SEVERE, null, e);
        }
        return ob;
    }

    private static Field getCampo(Class clase, String campo) throws NoSuchFieldException {
        try {
            return clase.getDeclaredField(campo);
        } catch (NoSuchFieldException e) {
            Class superClass = clase.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getCampo(superClass, campo);
            }
        }
    }

    protected EnteModel getDatosContribuyente(CatEnte ce) {
        EnteModel m = new EnteModel();
        try {
            if (ce != null) {
                m.setIdentificacion(ce.getCiRuc());
                if (ce.getExcepcionales() != null && ce.getExcepcionales()) {
                    m.setTipoPersona("4");
                    m.setDescPersona("C");
                } else {
                    if (ce.getEsPersona()) {
                        m.setTipoPersona("1");
                        m.setDescPersona("C");
                    } else {
                        m.setTipoPersona("2");
                        m.setDescPersona("R");
                    }
                }
                if (m.getTipoPersona() != null) {
                    if (ce.getEsPersona()) {
                        m.setDescripcion(ce.getNombres().toUpperCase().trim() + " " + ce.getApellidos().toUpperCase().trim());
                    } else {
                        m.setDescripcion(ce.getRazonSocial().toUpperCase().trim());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("getDatosContribuyente " + e.getMessage());
        }
        return m;
    }

    @Override
    public List<ConsolidacionBanco> getProcesarPagos(File archivo, FormatoBanca formato) {
        List<ConsolidacionBanco> resuldata = null;
        try {
            if (formato != null) {
                FormatoUnificado fm;
                resuldata = new ArrayList<>();
                BufferedReader Flee = new BufferedReader(new FileReader(archivo));
                String Leerlinea;
                System.out.println("Leyendo Archivo");
                while ((Leerlinea = Flee.readLine()) != null) {
                    System.out.println(Leerlinea);
                    String[] valores = Leerlinea.split(formato.getDelimitador());
                    for (int i = 0; i < valores.length; i++) {
                        for (BancaDiccionario bd : formato.getBancaDiccionarioCollection()) {
                            if (bd.getReproceso()) {
                                if (bd.getReqValRef()) {
                                    String v = valores[Integer.parseInt(bd.getValorRef())];
                                }
                            }
                        }
                    }
                }
                System.out.println("Fin del Archivo Leido");

                Flee.close();
            }
        } catch (Exception e) {
        }
        return resuldata;
    }

}
