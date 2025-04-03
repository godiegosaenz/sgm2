/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.EnteSecuencia;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.GeneSecuencia;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.RecActasEspecies;
import com.origami.sgm.entities.RegEnteInterviniente;
import com.origami.sgm.entities.RegFicha;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegpCertificadosInscripciones;
import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import com.origami.sgm.entities.RenSecuenciaNumLiquidicacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import com.origami.sgm.entities.SecuenciaTramite;
import com.origami.sgm.entities.UserConTareas;
import com.origami.sgm.enums.ActividadesTransaccionales;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.auditoria.BitacoraServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import util.HiberUtil;
import utils.ejb.interfaces.DatabaseLocal;

/**
 *
 * @author CarlosLoorVargas
 */
@Singleton(name = "seqManager")
@Lock(LockType.READ)
@Interceptors(value = {HibernateEjbInterceptor.class})
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SeqGenManEjb implements SeqGenMan {

    @javax.inject.Inject
    private Entitymanager manager;
    @javax.inject.Inject
    private DatabaseLocal ds;
    @javax.inject.Inject
    private BitacoraServices bitacora;
    @javax.inject.Inject
    private Entitymanager services;

    /**
     * Obtiene la secuena de la tabla GeneSecuencia
     *
     * @param query Consulta
     * @param params Nombre del parametro
     * @param values Valor de los parametros
     * @return Número de la secuencia incrementada en uno.
     */
    @Override
    @Lock(LockType.WRITE)
    public BigInteger getSequence(String query, String[] params, Object[] values) {
        BigInteger sequence = BigInteger.ZERO;
        GeneSecuencia seq;
        try {
            seq = (GeneSecuencia) manager.find(query, params, values);
            if (seq != null) {
                sequence = seq.getSecuencia().add(BigInteger.ONE);
                seq.setSecuencia(sequence);
                manager.persist(seq);
            } else {
                seq = new GeneSecuencia();
                seq.setAnio(new BigInteger((new SimpleDateFormat("YYYY").format(new Date()))));
                sequence = BigInteger.ONE;
                seq.setSecuencia(sequence);
                if (manager.persist(seq) == null) {
                    sequence = BigInteger.ZERO;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return sequence;
    }

    @Override
    @Lock(LockType.WRITE)
    public Object getSequences(String query, String[] params, Object[] values) {
        Object sequence = null;
        try {
            sequence = manager.find(query, params, values);
            Long l;
            if (sequence == null) {
                l = 1L;
            } else {
                l = Long.parseLong(sequence.toString()) + 1L;
            }
            sequence = l;
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return sequence;
    }

    @Override
    @Lock(LockType.WRITE)
    public RegpLiquidacionDerechosAranceles saveRegpLiqDerAranc(RegpLiquidacionDerechosAranceles liq) {
        Session sess;
        HiberUtil.requireTransaction();
        sess = HiberUtil.getSession();
        Calendar c = Calendar.getInstance();
        try {
            BigInteger seq = (BigInteger) manager.find("SELECT MAX(r.numTramiteRp) FROM RegpLiquidacionDerechosAranceles r where to_char(r.fecha, 'yyyy') = '" + c.get(Calendar.YEAR) + "'");
            if (seq == null) {
                seq = BigInteger.ONE;
            } else {
                seq = seq.add(BigInteger.ONE);
            }
            liq.setNumTramiteRp(seq);
            liq.getHistoricTramite().setNumTramiteXDepartamento(seq);
            //liq = (RegpLiquidacionDerechosAranceles) manager.persist(liq);
            liq = (RegpLiquidacionDerechosAranceles) sess.merge(liq);
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return liq;
    }

    @Override
    @Lock(LockType.WRITE)
    public UserConTareas getUserConMenosTareas(Long rol, Integer cantidad) {
        UserConTareas u;
        try {
            u = (UserConTareas) manager.findUnique(Querys.getUserConMenosTareas, new String[]{"codigo", "codigorol"}, new Object[]{rol, rol});
            if (u != null) {
                BigInteger temp = u.getPeso().add(BigInteger.valueOf(cantidad));
                u.setPeso(temp);
                manager.update(u);
            }
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return u;
    }

    /**
     * Consulta el ultimo de número en RegFicha y lo incrementa en uno y envia a
     * guardar Reg Ficha
     *
     * @param ficha RegFicha
     * @return RegFicha
     */
    @Override
    @Lock(LockType.WRITE)
    public RegFicha savRegFichaPredialSecuencia(RegFicha ficha) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        Long numFicha;
        try {
            numFicha = this.getMaxNumeroFichaByTipo(1L);
            ficha.setNumFicha(numFicha + 1);
            //ficha = (RegFicha) manager.persist(ficha);
            ficha = (RegFicha) sess.merge(ficha);
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return ficha;
    }

    @Override
    @Lock(LockType.WRITE)
    public Long getMaxNumeroFichaByTipo(Long tipoFicha) {
        Long num;
        try {
            num = (Long) manager.find(Querys.getMaxNumFichaByTipo, new String[]{"tipo"}, new Object[]{tipoFicha});
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return num;
    }

    /**
     * Guarda una lista de tipo RegFicha, para mantener la secuencia de los
     * numeros de fichas guardados en todo el grupo de fichas
     *
     * @param list
     * @param movs
     * @return
     */
    @Override
    @Lock(LockType.WRITE)
    public Long saveListFichasPredial(List<RegFicha> list, List<RegMovimiento> movs) {
        BigInteger periodo = new BigInteger(Calendar.YEAR + "");
        List<RegMovimientoFicha> listMovFic;
        RegMovimientoFicha mf;
        Long numFicha;
        Session sess;
        HiberUtil.requireTransaction();
        sess = HiberUtil.getSession();
        try {
            numFicha = this.getMaxNumeroFichaByTipo(1L);
            Long secuencia = numFicha;
            for (RegFicha f : list) {
                secuencia = secuencia + 1;
                f.setNumFicha(secuencia);
                f = (RegFicha) sess.merge(f);
                bitacora.registrarFicha(f, ActividadesTransaccionales.GENERACION_FICHA, periodo, null);
                listMovFic = new ArrayList<>();
                for (RegMovimiento m : movs) {
                    mf = new RegMovimientoFicha();
                    mf.setFicha(f);
                    mf.setMovimiento(m);
                    listMovFic.add(mf);
                }
                bitacora.registrarFichaMovs(f, movs, ActividadesTransaccionales.AGREGAR_REFERENCIA, periodo);
                if (!listMovFic.isEmpty()) {
                    //manager.saveList(listMovFic);
                    for (Object entitie : listMovFic) {
                        sess.merge(entitie);
                    }
                }
            }
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return numFicha + 1;
    }

    @Override
    @Lock(LockType.WRITE)
    public RegEnteInterviniente getMaxInterviniente(RegEnteInterviniente interv) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        Integer sequence;
        try {
            if (interv.getCedRuc() != null) {
                sequence = (Integer) manager.find(Querys.getMaxRegEnteInterviniente, new String[]{"cedula", "tipointr"}, new Object[]{interv.getCedRuc(), interv.getTipoInterv()});
                if (sequence == null) {
                    sequence = 0;
                } else {
                    sequence = sequence + 1;
                }
            } else {
                sequence = 0;
            }
            interv.setSecuencia(sequence);
            //interv = (RegEnteInterviniente) manager.persist(interv);
            interv = (RegEnteInterviniente) sess.merge(interv);
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return interv;
    }

    @Override
    @Lock(LockType.WRITE)
    public RegMovimiento getMaxNumRepertRegMovimiento(RegMovimiento mov, int anio) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        Integer numRepertorio;
        try {
            numRepertorio = (Integer) manager.getNativeQuery("Select max(m.num_repertorio) FROM  " + SchemasConfig.APP1 + ".reg_movimiento m where m.is_mercantil = " + mov.getIsMercantil() + " and to_char(m.fecha_inscripcion,'YYYY')='" + anio + "'");
            if (numRepertorio == null) {
                numRepertorio = 0;
            }

            mov.setNumRepertorio(numRepertorio + 1);
            //mov = (RegMovimiento) manager.persist(mov);
            mov = (RegMovimiento) sess.merge(mov);
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return mov;
    }

    @Override
    @Lock(LockType.WRITE)
    public List<RegpCertificadosInscripciones> getRepertoriosPorAnio(Integer anio, Integer cantidad, Long idLiq) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        List<RegpCertificadosInscripciones> list = new ArrayList<>();
        RegpCertificadosInscripciones temp;
        Integer numRepertorio;
        try {
            numRepertorio = (Integer) manager.getNativeQuery("select max(repertorio) from sgm_secuencias.repertorio_registro where to_char(fecha, 'YYYY') = '" + anio + "'");
            if (numRepertorio == null) {
                numRepertorio = 0;
            }
            RegpLiquidacionDerechosAranceles liq = manager.find(RegpLiquidacionDerechosAranceles.class, idLiq);
            if (liq != null) {
                for (int i = 0; i < cantidad; i++) {
                    numRepertorio++;
                    temp = new RegpCertificadosInscripciones();
                    temp.setFecha(new Date());
                    temp.setLiquidacion(liq);
                    temp.setTipoTarea("I");
                    temp = (RegpCertificadosInscripciones) sess.merge(temp);
                    list.add(temp);

                    SQLQuery sql = sess.createSQLQuery("insert into sgm_secuencias.repertorio_registro(id, repertorio, fecha) values(DEFAULT, ?, ?)");
                    sql.setInteger(0, numRepertorio);
                    sql.setDate(1, new Date());
                    sql.executeUpdate();
                }
            }
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    @Override
    @Lock(LockType.WRITE)
    public RegMovimiento getNumInscripcionRegmovimientobyAnioLibro(int anio, Long idLibro, RegMovimiento movimiento) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        Integer numInscripcion;
        Integer numRepertorio;
        //Integer folioFinal;
        try {
            numInscripcion = (Integer) manager.getNativeQuery("Select max(m.num_inscripcion) FROM  " + SchemasConfig.APP1 + ".reg_movimiento  m where m.libro=" + idLibro + " and to_char(m.fecha_inscripcion,'YYYY')='" + anio + "'");
            if (numInscripcion == null) {
                numInscripcion = 0;
            }
            movimiento.setNumInscripcion(numInscripcion + 1);

            if (movimiento.getNumRepertorio() == null) {
                numRepertorio = (Integer) manager.getNativeQuery("select max(m.num_repertorio) from  " + SchemasConfig.APP1 + ".reg_movimiento m where to_char(m.fecha_inscripcion,'YYYY') = '" + anio + "'");
                if (numRepertorio == null) {
                    numRepertorio = 0;
                }
                movimiento.setNumRepertorio(numRepertorio + 1);
            }

            /*folioFinal = (Integer) manager.getNativeQuery("Select max(m.folio_fin) FROM  "+SchemasConfig.APP1+".reg_movimiento m where to_char(m.fecha_inscripcion,'YYYY')='" + anio + "' and m.libro=" + movimiento.getLibro().getId());
             if (folioFinal == null) {
             folioFinal = 0;
             }
             movimiento.setFolioAnterior(folioFinal);
             movimiento.setFolioInicio(folioFinal + 1);
             movimiento.setFolioFin(folioFinal + movimiento.getNumPaginasContabilizada());*/
            //return (RegMovimiento) manager.persist(movimiento);
            movimiento = (RegMovimiento) sess.merge(movimiento);
            sess.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return movimiento;
    }

    @Override
    @Lock(LockType.WRITE)
    public RecActasEspecies maxNumeroEspecie(RecActasEspecies acta) {
        HiberUtil.requireTransaction();
        Session ss = HiberUtil.getSession();
        Calendar cal = Calendar.getInstance();
        Integer numero;
        try {
            Object ob = manager.find(Querys.getMaxNumeroActa, new String[]{"valor"}, new Object[]{cal.get(Calendar.YEAR)});
            if (ob == null) {
                numero = 0;
            } else {
                numero = (Integer) ob;
            }
            acta.setNumActa(numero + 1);
            acta = (RecActasEspecies) ss.merge(acta);
            ss.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return acta;
    }

    @Override
    @Lock(LockType.WRITE)
    public Long getSecuenciasTram(String app) {
        Long x = 0L;
        try {
            Connection cx = ds.getDataSource().getConnection();
            if (cx != null) {
                try (PreparedStatement ps = cx.prepareStatement("insert into " + SchemasConfig.SECUENCIAS + ".secuencia(id, tramite, aplicacion) values(DEFAULT, DEFAULT, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, app);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        x = rs.getLong(2);
                    }
                    ps.getConnection().commit();
                }
                cx.close();
            }else{
                System.out.println("Coneccion para la tabla secuencia es nuloi");   
            }
        } catch (SQLException e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return x;

    }

    @Override
    @Lock(LockType.WRITE)
    public PePermiso getSequences(PePermiso permisoNuevo) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("anioPermiso", permisoNuevo.getAnioPermiso());
            map.put("tramite", permisoNuevo.getTramite());
            PePermiso temp = manager.findObjectByParameter(PePermiso.class, map);
            Long l = null;
            if (temp == null) {
                Object sequence = manager.find(Querys.getNumerosReportes, new String[]{"anioPermiso"}, new Object[]{permisoNuevo.getAnioPermiso()});
                //Object count = manager.find(Querys.getCountNumerosReportes, new String[]{"anioPermiso"}, new Object[]{permisoNuevo.getAnioPermiso()});
                if (sequence == null) {
                    l = 1L;
                } else {
                    l = Long.parseLong(sequence.toString()) + 1L;
                    //l=l+Long.parseLong(count.toString())-334;
                }
                permisoNuevo.setNumReporte(new BigInteger(l.toString()));
                return (PePermiso) manager.persist(permisoNuevo);
            } else {
                if (temp.getNumReporte() == null) {
                    Object sequence = manager.find(Querys.getNumerosReportes, new String[]{"anioPermiso"}, new Object[]{permisoNuevo.getAnioPermiso()});
                    Object count = manager.find(Querys.getCountNumerosReportes, new String[]{"anioPermiso"}, new Object[]{permisoNuevo.getAnioPermiso()});
                    if (sequence == null) {
                        l = 1L;
                    } else {
                        l = Long.parseLong(sequence.toString()) + 1L;
                        l = l + Long.parseLong(count.toString()) - 334;
                    }
                } else {
                    l = temp.getNumReporte().longValue();
                }
                permisoNuevo.setId(temp.getId());
                permisoNuevo.setNumReporte(new BigInteger(l.toString()));
                manager.persist(permisoNuevo);
                return permisoNuevo;
            }
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * Consulta el ultimo Número de predio generado en CatPredio y lo incrementa
     * en uno, se asigna el número de predio al campo numPredio y envia a
     * persistir la entiti para retornar la misma entiti persistida.
     *
     * @param predio Entiti CatPredio
     * @return CatPredio
     */
    @Override
    @Lock(LockType.WRITE)
    public CatPredio generarNumPredioAndGuardarCatPredio(CatPredio predio) {
        try {
            if (predio.getNumPredio() == null) {
                Object sequence = manager.find(Querys.getMaxCatPredio);
                Long l;
                if (sequence == null) {
                    l = 1L;
                } else {
                    l = Long.parseLong(sequence.toString()) + 1L;
                }
                CatPredio temp = (CatPredio) manager.find(Querys.getPredioByNumPredio, new String[]{"numPredio"}, new Object[]{new BigInteger(l.toString())});
                if (temp == null) {
                    predio.setNumPredio(new BigInteger(l.toString()));
                    if (predio.getId() == null) {
                        return (CatPredio) manager.persist(predio);
                    } else {
                        predio.setFecMod(new Date());
                        manager.persist(predio);
                        return predio;
                    }
                } else {
                    return null;
                }
            } else {
                if (predio.getId() == null) {
                    return (CatPredio) manager.persist(predio);
                } else {
                    manager.persist(predio);
                    return predio;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @Override
    @Lock(LockType.WRITE)
    public BigInteger getMaxSecuenciaTipoLiquidacion(Integer anio, Long idTipoLiquidacion) {
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        RenSecuenciaNumLiquidicacion secuenciaTipoLiquidacion;
        BigInteger secuencia;
        try {
            if (anio != null && idTipoLiquidacion != null) {
                secuenciaTipoLiquidacion = (RenSecuenciaNumLiquidicacion) manager.findNoProxy(Querys.MaxSecuenciaTipoLiquidacionSinAnio, new String[]{"idTipoLiquidacion"}, new Object[]{idTipoLiquidacion});
                if (secuenciaTipoLiquidacion == null) {
                    secuenciaTipoLiquidacion = new RenSecuenciaNumLiquidicacion();
                    secuencia = BigInteger.ONE;
                    secuenciaTipoLiquidacion.setAnio(anio);
                    secuenciaTipoLiquidacion.setTipoLiquidacion(new RenTipoLiquidacion(idTipoLiquidacion));
                } else {
                    secuencia = secuenciaTipoLiquidacion.getSecuencia().add(BigInteger.ONE);
                }
                secuenciaTipoLiquidacion.setSecuencia(secuencia);
                //manager.persist(secuenciaTipoLiquidacion);
                sess.merge(secuenciaTipoLiquidacion);
                sess.getTransaction().commit();
            } else {
                secuencia = BigInteger.ZERO;
            }
        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return secuencia;
    }

    @Override
    @Lock(LockType.WRITE)
    public Long getMaxSecuenciaTipoTramite(Integer anio, Long idTipoTramite) {
        SecuenciaTramite secuencia;
        Long sequence;
        try {
            if (anio != null && idTipoTramite != null) {
                secuencia = (SecuenciaTramite) manager.find(Querys.MaxSecuenciaTipoTramite, new String[]{"anio", "idTipoTramite"}, new Object[]{anio, idTipoTramite});
                if (secuencia == null) {
                    secuencia = new SecuenciaTramite();
                    sequence = 1L;
                    secuencia.setAnio(anio);
                    secuencia.setFechaActualizacion(new Date());
                    secuencia.setTramiteDepartamento(new GeTipoTramite(idTipoTramite));
                } else {
                    sequence = secuencia.getSecuencia() + 1L;
                }
                secuencia.setSecuencia(sequence);
                manager.persist(secuencia);
            } else {
                sequence = 0L;
            }

        } catch (Exception e) {
            Logger.getLogger(SeqGenManEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return sequence;
    }

    @Override
    @Lock(LockType.WRITE)
    public CatEnte guardarOActualizarEnte(CatEnte ente) {
        SQLQuery query;
        EnteSecuencia secuencia = new EnteSecuencia();
        Calendar c = Calendar.getInstance();
        Integer anio = c.get(Calendar.YEAR);
        secuencia.setAnio(new BigInteger(anio.toString()));

        if (ente.getCiRuc() == null || ente.getCiRuc().equals("")) {
            BigInteger secu = new BigInteger("0");
            //secu = (BigInteger) services.find(Querys.maxEnteSecuencia, new String[]{}, new Object[]{});
            query = services.getSession().createSQLQuery("SELECT max(es.secuencia) FROM " + SchemasConfig.SECUENCIAS + ".ente_secuencia es");
            secu = (BigInteger) query.uniqueResult();
            if (secu == null) {
                secu = new BigInteger("300000000000001");
            } else {
                secu = secu.add(BigInteger.ONE);
            }
            secuencia.setSecuencia(secu);
            secuencia = (EnteSecuencia) services.persist(secuencia);

            ente.setCiRuc(secu.toString());
        }
        if (ente.getId() == null) {
            return (CatEnte) manager.persist(ente);
        } else {
            manager.persist(ente);
            return ente;
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public Long getNumComprobante() {
        Long comprobante = null;
        try {
            /*RenSecuenciaNumComprobante secuenciaNumComprobante = (RenSecuenciaNumComprobante) manager.find(RenSecuenciaNumComprobante.class, 1L);
            if (secuenciaNumComprobante == null) {
                secuenciaNumComprobante = new RenSecuenciaNumComprobante();
                comprobante = 1L;
            } else {
                comprobante = secuenciaNumComprobante.getNumComprobante() + 1L;
            }
            secuenciaNumComprobante.setAnio(new Long(Utils.getDateValues("Y", new Date()).toString()));
            secuenciaNumComprobante.setNumComprobante(comprobante);
            manager.persist(secuenciaNumComprobante);*/
            comprobante = new Long(manager.getNativeQuery("select nextval('" + SchemasConfig.FINANCIERO + ".ren_pago_num_comprobante_seq')").toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return comprobante;
    }

}
