/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs.registro;

import com.origami.sgm.bpm.models.FichaIngreso;
import com.origami.sgm.database.Querys;
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
import com.origami.sgm.entities.RegFichaBien;
import com.origami.sgm.entities.RegFichaPropietarios;
import com.origami.sgm.entities.RegMovimiento;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import com.origami.sgm.entities.RegTipoBien;
import com.origami.sgm.entities.RegTipoFicha;
import com.origami.sgm.enums.ActividadesTransaccionales;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.SeqGenMan;
import com.origami.sgm.services.interfaces.auditoria.BitacoraServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import com.origami.sgm.services.interfaces.registro.InscripcionNuevaServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import util.EntityBeanCopy;
import util.HiberUtil;

/**
 * Metodos de para
 * la Ingreso de
 * Nueva Ficha estan
 * todos los metodos
 * de consulta para
 * las transaccines
 * de esta ficha
 *
 * @author Angel
 * Navarro
 */
@Singleton(name = "nuevaFicha")
@Lock(LockType.READ)
@Interceptors(value = {HibernateEjbInterceptor.class})
public class FichaIngresoNuevoEjb implements FichaIngresoNuevoServices {

    @javax.inject.Inject
    private Entitymanager manager;

    @javax.inject.Inject
    private Entitymanager serv;

    @javax.inject.Inject
    private SeqGenMan secuencias;

    @javax.inject.Inject
    private InscripcionNuevaServices inscripcionServices;

    @javax.inject.Inject
    private BitacoraServices bitacora;

    /**
     * Obtiene lista
     * de todos
     * tipos de
     * fichas de la
     * tabla
     * RegTipoFicha
     *
     * @return
     * Objeto
     * RegTipoFicha
     */
    @Override
    public List<RegTipoFicha> getRegTipoFichaList() {
        return serv.findAllEntCopy(RegTipoFicha.class);
    }

    /**
     * Buscar en la
     * tabla
     * RegTipoFicha
     * por id
     *
     * @param id
     * @return
     * RegTipoFicha
     */
    @Override
    public RegTipoFicha getRegTipoFichaById(Long id) {
        return (RegTipoFicha) serv.find(RegTipoFicha.class, id);
    }

    /**
     * Obtiene la
     * lista de
     * Parroquias
     * recibiendo
     * como
     * parametro el
     * cantos
     *
     * @param
     * idCanton
     * @return list
     * de Parroquias
     * filtradas por
     * el canton
     */
    @Override
    public List<CatParroquia> getCatPerroquiasListByCanton(Long idCanton) {
        if (idCanton == null) {
            return null;
        }
        return serv.findAll(Querys.getParroquiaByCanton, new String[]{"idCanton"}, new Object[]{idCanton});
    }

    /**
     * Obtiene los
     * movientos que
     * ha tenido una
     * ficha
     *
     * @param id
     * @return lista
     * de Movientos
     * de ficha de
     * la tabla
     * RegMovimientoFicha
     */
    @Override
    public List<RegMovimientoFicha> getRegMovimientoFichasList(Long id) {
        List<RegMovimientoFicha> list;
        try {
            list = serv.findAll(Querys.getRegMovimientoFichaByFicha, new String[]{"idFicha"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una
     * lista de tipo
     * RegMovimientoFicha
     * que estan
     * asociadas a
     * un
     * RegMovimiento
     * al cual se
     * consulta por
     * el id
     *
     * @param id
     * @return
     */
    @Override
    public List<RegMovimientoFicha> getRegMovimientoFichasByMov(Long id) {
        List<RegMovimientoFicha> list;
        try {
            list = serv.findAll(Querys.getRegMovFichasByMovimientoId, new String[]{"idmov"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Retorna una
     * lista de tipo
     * RegMovimientos
     * que
     * representan
     * los
     * movimientos
     * que se
     * relacionan
     * con una ficha
     *
     * @param
     * idFicha
     * @return
     */
    @Override
    public List<RegMovimiento> getMovimientosByFicha(Long idFicha) {
        List<RegMovimiento> list;
        try {
            list = serv.findAll(Querys.getMovimientosByIdFicha, new String[]{"idFicha"}, new Object[]{idFicha});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return list;
    }

    /**
     * Obtiene la
     * Ficha a
     * partir del
     * número de
     * Ficha y el
     * tipo de ficha
     *
     * @param
     * numFicha
     * @param tipo
     * @return El
     * registro de
     * la Ficha si
     * existe
     */
    @Override
    public RegFicha getFichaByNumFichaByTipo(Long numFicha, Long tipo) {
        if (numFicha == null || tipo == null) {
            return null;
        }
        return (RegFicha) serv.find(Querys.getRegFichaNumFacha, new String[]{"numFicha", "tipo"}, new Object[]{numFicha, tipo});
    }

    /**
     * Obtine el
     * listado de
     * fichas a
     * partir del
     * rango de
     * numero de
     * fichas y tipo
     *
     * @param
     * numFichaInicial
     * @param
     * numFichaFinal
     * @param tipo
     * @return El
     * listado de
     * fichas dentro
     * del rango
     */
    @Override
    public List<RegFicha> getFichasByRangoNumFichaByTipo(Long numFichaInicial, Long numFichaFinal, Long tipo) {
        if (numFichaInicial != null && numFichaFinal != null && tipo != null) {
            return serv.findAll(Querys.getRegFichaRangoNumFicha, new String[]{"numFichaInicial", "numFichaFinal", "tipo"}, new Object[]{numFichaInicial, numFichaFinal, tipo});
        }
        return null;
    }

    /**
     * Retorna el
     * objeto
     * CatPredio el
     * cual se
     * consulta por
     * el numero de
     * predio
     *
     * @param
     * numPredio
     * @return El
     * registro de
     * la ficha
     */
    @Override
    public RegFicha getRegFichaNumPredio(Long numPredio) {
        if (numPredio == null) {
            return null;
        }
        return (RegFicha) serv.find(Querys.getRegFichaNumPredio, new String[]{"numPredio"}, new Object[]{numPredio});
    }

    /**
     * Retorna un
     * objeto de
     * tipo RegFicha
     * que esta
     * relacionado
     * con otro de
     * tipo
     * CatEscrituraRural
     * el cual se
     * consulta por
     * el id
     *
     * @param
     * idEscriRural
     * @return
     */
    @Override
    public RegFicha getRegFichaByEscrituraRural(Long idEscriRural) {
        RegFicha ficha;
        try {
            ficha = (RegFicha) serv.find(Querys.getRegFichaByEscrituraRural, new String[]{"rural"}, new Object[]{idEscriRural});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return ficha;
    }

    /**
     * Obtiene la
     * Ficha usando
     * como
     * parametro el
     * numero de
     * Predio
     *
     * @param id
     * @return la
     * ficha
     */
    @Override
    public RegFicha getRegFichaByPredio(Long id) {
        return (RegFicha) serv.find(Querys.getRegFichaByPredio, new String[]{"predio"}, new Object[]{id});
    }

    /**
     * Recibe El
     * número de
     * Predio
     *
     * @param
     * numPredio
     * @return El
     * registro del
     * predio si
     * existe
     */
    @Override
    public CatPredio getPredioByNum(Long numPredio) {
        if (numPredio == null) {
            return null;
        }
        return (CatPredio) serv.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{numPredio});
    }

    /**
     * Permite
     * Realizar la
     * busqueda en
     * la Tabla
     * CatCiudadela
     * recibiendo
     * como
     * parametro el
     * id de la
     * parroquia
     *
     * @param
     * idParroquia
     * @return lista
     * de Ciudelas
     * que
     * perteneces a
     * la parroquia
     */
    @Override
    public List<CatCiudadela> getCiudadelasByParroquia(Long idParroquia) {
        if (idParroquia == null) {
            return null;
        }
        return serv.findAllEntCopy(Querys.getCiudadelasByParroquia, new String[]{"parroquia"}, new Object[]{idParroquia});
    }

    /**
     * Obtiene la
     * consulta de
     * todas la
     * ciudadelas
     * que contiene
     * la tabla
     * ciudadelas
     *
     * @return Lista
     * CatCiudadelas
     */
    @Override
    public List<CatCiudadela> getCiudadelas() {
        return (List<CatCiudadela>) EntityBeanCopy.clone(manager.findAllObjectOrder(CatCiudadela.class, new String[]{"nombre"}, Boolean.TRUE));
    }

    /**
     * Realiza la
     * busqueda en
     * CatEscritura
     * por el id del
     * predio y que
     * se encuentre
     * en estado "A"
     *
     * @param
     * idPredio - id
     * del predio
     * @return El
     * registro de
     * la escritura
     */
    @Override
    public CatEscritura getCatEscrituraByPredio(Long idPredio) {
        if (idPredio != null) {
            return (CatEscritura) manager.findUnique(Querys.getCatEscrituraByPredio, new String[]{"id"}, new Object[]{idPredio});
        }
        return null;
    }

    /**
     * Retorna una
     * lista de
     * propietarios
     * que estan
     * relacionados
     * con un predio
     * y se consulta
     * el objeto
     * CatPredio por
     * el id
     *
     * @param
     * idPredio
     * @return
     */
    @Override
    public List<CatPredioPropietario> getPropietariosByPredio(Long idPredio) {
        try {
            return serv.findAll(Querys.getCatPropietariosByPredio, new String[]{"id"}, new Object[]{idPredio});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Busca en
     * CatEscrituraRural
     * recibiendo
     * como
     * parametro el
     * regCatastral
     * y el
     * IdentificadorPredial
     *
     * @param
     * regCatastral
     * @param
     * identicadorPredial
     * @return
     * Escritura
     * Rural
     */
    @Override
    public CatEscrituraRural getCatEscrituraRural(Long regCatastral, Long identicadorPredial) {
        CatEscrituraRural escritura;
        try {
            escritura = (CatEscrituraRural) serv.find(Querys.getCatEscrituraRural, new String[]{"regCatastral", "idPredial"}, new Object[]{regCatastral, identicadorPredial});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return escritura;
    }

    /**
     * Obtiene Lista
     * de la tabla
     * RegTipoBien
     * recibiendo
     * como pramatro
     * de el estado
     *
     * @param estado
     * @return List
     */
    @Override
    public List<RegTipoBien> getTipoBienList(Boolean estado) {
        return serv.findAll(Querys.getRegTipoBienList, new String[]{"estado"}, new Object[]{estado});
    }

    /**
     * Permite
     * obtener el
     * registro de
     * la tabla
     * CatEnte
     * mediante el
     * número de
     * Cédula o RUC
     *
     * @param ciRUC
     * - Cédula o
     * RUC de la
     * persona o
     * empresa
     * @return
     * CatEnte si
     * existe
     */
    @Override
    public CatEnte getCatEnte(String ciRUC) {
        return (CatEnte) serv.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{ciRUC});
    }

    /**
     * Obtiene Los
     * nombres de
     * Las
     * Ciudadelas
     *
     * @return Lista
     * String
     */
    @Override
    public List<String> getListNombresCdla() {
        return serv.findAll(Querys.getListNombresCdla, new String[]{}, new Object[]{});
    }

    /**
     * Devuelve un
     * objeto de
     * tipo CtlgItem
     * consultado
     * por el id
     *
     * @param Id -
     * id de la
     * tabla
     * CtlgItem
     * @return
     */
    @Override
    public CtlgItem getCtlgItemById(Long Id) {
        return (CtlgItem) serv.find(CtlgItem.class, Id);
    }

    /**
     * Realiza el
     * guardado de
     * una ficha la
     * veces que que
     * se le
     * especifique
     * en el
     * parametro
     * cantidad, La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param matriz
     * @param
     * cantidad -
     * Cantidad de
     * veces a
     * Duplicar la
     * ficha
     * @param
     * usuario -
     * Usuario que
     * inicio la
     * sessión
     * @param
     * linderos
     * @param
     * movimientos -
     * Lista de
     * Movimientos
     * @return
     * Mensaje con
     * el número de
     * fichas
     * creadas
     */
    @Override
    @Lock(LockType.WRITE)
    public String guardarFichasDuplicadas(RegFicha matriz, Integer cantidad, String usuario, String linderos, List<RegMovimiento> movimientos) {
        CtlgItem estado = this.getCtlgItemById(134L);
        List<RegFicha> listFichas = new ArrayList<>();
        CatCiudadela ciudadela = null;
        if (matriz.getCiudadela() != null) {
            ciudadela = matriz.getCiudadela();
        }
        RegFicha ficha;
        Long inicio, fin;
        String mensaje;
        try {
            for (int i = 0; i < cantidad; i++) {
                ficha = new RegFicha();
                ficha.setTipo(matriz.getTipo());
                ficha.setTipoPredio(matriz.getTipoPredio());
                ficha.setParroquia(matriz.getParroquia());
                ficha.setCiudadela(ciudadela);
                ficha.setCodigoPredial("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                ficha.setLinderos(linderos);
                ficha.setTipoDep(true);
                ficha.setDetFuncionario(usuario);
                ficha.setEstado(estado);
                ficha.setFechaApe(new Date());
                listFichas.add(ficha);
            }
            inicio = secuencias.saveListFichasPredial(listFichas, movimientos);
            fin = inicio + cantidad - 1;
            mensaje = "Fichas creadas desde la numero : " + inicio + ", hasta : " + fin + ".";
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return mensaje;
    }

    /**
     * La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param
     * ingreso
     * @param
     * movimientos
     * @return
     */
    @Override
    public Long saveRegFichaPredialUrbano(FichaIngreso ingreso, List<RegMovimiento> movimientos) {
        CtlgItem estado = this.getCtlgItemById(134L);
        CatEscritura escritura = ingreso.getEscritura();
        RegFicha ficha = ingreso.getFicha();
        RegFicha temp;
        try {
            ficha.setEstado(estado);
            ficha.setTipoPredio("U");
            ficha.setTipoDep(true);
            temp = secuencias.savRegFichaPredialSecuencia(ficha);
            bitacora.registrarFicha(temp, ActividadesTransaccionales.GENERACION_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
            this.guardarMovientos(temp, movimientos);
            if (escritura.getIdEscritura() != null) {
                manager.update(escritura);
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return temp.getNumFicha();
    }

    /**
     * La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param
     * ingreso
     * @param
     * movimientos
     * @return
     */
    @Override
    public Long saveRegFichaPredialRural(FichaIngreso ingreso, List<RegMovimiento> movimientos) {
        CtlgItem estado = this.getCtlgItemById(134L);
        CatEscrituraRural escritura = ingreso.getEscrituraRural();
        RegFicha ficha = ingreso.getFicha();
        RegFicha temp;
        try {
            ficha.setEstado(estado);
            ficha.setTipoPredio("R");
            ficha.setTipoDep(true);
            temp = secuencias.savRegFichaPredialSecuencia(ficha);
            bitacora.registrarFicha(temp, ActividadesTransaccionales.GENERACION_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
            this.guardarMovientos(temp, movimientos);
            if (escritura.getId() != null) {
                manager.update(escritura);
                temp.setCatEscrituraRural(escritura);
                temp.setCodigoPredial(escritura.getRegistroCatastral().toString() + "-" + escritura.getIdentificacionPredial());
                manager.update(temp);
            } else {
                if (escritura.getRegistroCatastral() != null && escritura.getIdentificacionPredial() != null) {
                    escritura.setPropiedadHorizontal(ingreso.getPropiedadHorz());
                    escritura = (CatEscrituraRural) manager.persist(escritura);
                    temp.setCatEscrituraRural(escritura);
                    temp.setCodigoPredial(escritura.getRegistroCatastral().toString() + "-" + escritura.getIdentificacionPredial());
                    manager.update(temp);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return temp.getNumFicha();
    }

    /**
     * Obtiene la
     * Collection
     * RegFichaBien
     * que se paso
     * de
     * anteriormente
     * y se envia a
     * guardar en
     * RegFicha para
     * obtener la
     * entidad
     * persistida y
     * luego enviar
     * a persistir
     * la lista de
     * RegFichaBien
     * que fue
     * obtenida con
     * anterioridad
     *
     *
     * @param ficha
     * Entity
     * RegFicha
     * @return
     * RegFicha
     */
    @Override
    public RegFicha guardarFichaBien(RegFicha ficha) {
        List<RegFichaBien> list = (List<RegFichaBien>) ficha.getRegFichaBienCollection();
        try {
            //ficha = secuencias.getNumFichaByTipo(ficha, ficha.getTipo().getId());
            if (list != null) {
                for (RegFichaBien fb : list) {
                    fb.setFicha(ficha);
                    //serv.persist(fb);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ficha;
    }

    /**
     * Guarda una
     * nueva ficha
     * en la tabla
     * RegFicha La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param ficha
     * Entity
     * RegFicha
     * @param
     * movimientos
     * @return
     * RegFicha
     */
    @Override
    public RegFicha guardarFicha(RegFicha ficha, List<RegMovimiento> movimientos) {
        ficha.setFechaApe(new Date());
        ficha.setTipoPredio("I");
        ficha.setTipoDep(true);
        ficha.setEstado(getCtlgItemById(134L));
        RegFicha f = (RegFicha) serv.persist(ficha);
        bitacora.registrarFicha(f, ActividadesTransaccionales.GENERACION_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
        if (guardarMovientos(f, movimientos)) {
            return f;
        }
        return null;
    }

    /**
     * Guardar lista
     * de Movientos
     * de la ficha
     * recibe como
     * parametros la
     * ficha y la
     * Lista de
     * movientos La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param f
     * @param mov
     * @return true
     * si los datos
     * fueron
     * guardados
     * caso
     * contrario
     * false
     */
    @Override
    public Boolean guardarMovientos(RegFicha f, List<RegMovimiento> mov) {
        RegMovimientoFicha mf;
        List<RegMovimientoFicha> listMov;
        try {
            if (!mov.isEmpty()) {
                listMov = new ArrayList<>();
                for (RegMovimiento m : mov) {
                    mf = new RegMovimientoFicha();
                    mf.setFicha(f);
                    mf.setMovimiento(m);
                    listMov.add(mf);
                }
                bitacora.registrarFichaMovs(f, mov, ActividadesTransaccionales.AGREGAR_REFERENCIA, new BigInteger(new Date().getYear() + 1900 + ""));
                return serv.saveList(listMov);
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return false;
    }

    /**
     * Guarda La
     * RegFicha Y la
     * CatEscrituraRural
     *
     * @param f
     * @param
     * escRural
     * @return
     * RegFicha
     */
    @Override
    public RegFicha guadarFichaYEscrituraRural(RegFicha f, CatEscrituraRural escRural) {
        RegFicha ficha = new RegFicha();
        CatEscrituraRural rural = (CatEscrituraRural) serv.persist(escRural);
        Hibernate.initialize(rural);
//        Long numFicha = secuencias.getNumFichaByTipo(1L);
//        f.setNumFicha(numFicha);
        f.setEstado(getCtlgItemById(134L));
        if (escRural != null) {
            f.setCatEscrituraRural(rural);
        }
        //ficha = secuencias.getNumFichaByTipo(f, 1L);
        return ficha;
    }

    /**
     * Guarda en
     * RegFicha Y en
     * CatEscritura
     *
     * @param f -
     * RegFicha
     * @param
     * escUrbana -
     * CatEscritura
     * @return
     * RegFicha
     */
    @Override
    public RegFicha guardarFichaYEscritura(RegFicha f, CatEscritura escUrbana) {
        RegFicha ficha = new RegFicha();
//        Long numficha = secuencias.getNumFichaByTipo(1L);
//        f.setNumFicha(numficha);
        f.setEstado(getCtlgItemById(134L));
        //ficha = secuencias.getNumFichaByTipo(f, 1L);
        if (escUrbana != null) {
            serv.persist(escUrbana);
        }
        return ficha;
    }

    /**
     * Actualiza la
     * Tabla
     * RegFicha La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param
     * regFichaHistorico
     * @return true
     * si se realizo
     * la
     * actualizacion
     * del registro
     * caso
     */
    @Override
    public Boolean actualizarRegFicha(RegFicha regFichaHistorico) {
        if (regFichaHistorico != null) {
            bitacora.registrarFicha(regFichaHistorico, ActividadesTransaccionales.MODIFICAR_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
            return serv.update(regFichaHistorico);
        }
        return false;
    }

    /**
     * Realiza un
     * merge en la
     * tabla CatEnte
     *
     * @param ente
     * @return
     */
    @Override
    public CatEnte guardarCatEnte(CatEnte ente) {
        return (CatEnte) serv.persist(ente);
    }

    /**
     * Realiza la
     * busqueda en
     * RegFicha si
     * existe
     * retorna la
     * entity caso
     * contrario
     * retorna null
     *
     * @param
     * numIdentificador
     * CodigoPredial
     * @param id Id
     * de
     * RegTipoFicha
     * @return
     * RegFicha
     */
    @Override
    public RegFicha getRegFichaByCodPredial(String numIdentificador, Long id) {
        return (RegFicha) serv.find(Querys.getRegFichaByCodPredial, new String[]{"cadena", "tipo"}, new Object[]{numIdentificador, id});
    }

    /**
     * Realiza la
     * persistencia
     * de catEnte y
     * despues envia
     * a persistir
     * EnteCorreo y
     * EnteTelefono
     *
     * @param ente
     * Entity
     * CatEnte
     * @return
     * CatEnte
     */
    @Override
    public CatEnte guardarTelefCorreosYContribuyente(CatEnte ente) {
        return serv.guardarEnteCorreosTlfns(ente);
    }

    /**
     * Si la CatEnte
     * en nulo lo
     * envia lo
     * envia a
     * guardar y la
     * lista de
     * correos y
     * telefonos por
     * igual caso
     * contrario
     * envia a
     * catEnte,
     * EnteCorreo,
     * EnteTelefono
     * a realizar
     * update y si
     * hay Correos y
     * Telefonos por
     * eliminar los
     * envia, para
     * despues
     * realizar en
     * guardado de
     * en la tabla
     * RegFicha.
     *
     * @param
     * ingreso
     * Modelo de
     * Datos que
     * contiene las
     * entity que
     * son
     * necesarias en
     * realizar el
     * Guardado de
     * la FichaBien
     * @param
     * nameUser
     * nombre del
     * usuario
     * logeado
     * @return
     * RegFicha
     */
    @Override
    public RegFicha guardarTelefCorreosYContribuyenteAndFicha(FichaIngreso ingreso, String nameUser) {
        ingreso.getEnte().setEsPersona(ingreso.getPersona());
        CatEnte en;
        if (ingreso.getEnte() != null) {
            ingreso.getEnte().setCiRuc(ingreso.getCiRUC());
            if (ingreso.getPersona()) {
                ingreso.getFicha().setLinderos(ingreso.getEnte().getNombres() + " " + ingreso.getEnte().getApellidos());
                ingreso.getFicha().setObservacion(ingreso.getEnte().getDireccion());
            } else {
                ingreso.getFicha().setLinderos(ingreso.getEnte().getRazonSocial());
                ingreso.getFicha().setObservacion(ingreso.getEnte().getDireccion());
            }
            ingreso.getEnte().setEnteCorreoCollection(ingreso.getListCorreo());
            ingreso.getEnte().setEnteTelefonoCollection(ingreso.getListTelefonos());
            ingreso.getEnte().setEstado("A");
            en = serv.guardarEnteCorreosTlfns(ingreso.getEnte());
        } else {
            if (ingreso.getPersona()) {
                ingreso.getFicha().setLinderos(ingreso.getEnte().getNombres() + " " + ingreso.getEnte().getApellidos());
                ingreso.getFicha().setObservacion(ingreso.getEnte().getDireccion());
            } else {
                ingreso.getFicha().setLinderos(ingreso.getEnte().getRazonSocial());
                ingreso.getFicha().setObservacion(ingreso.getEnte().getDireccion());
            }
            ingreso.getEnte().setEnteCorreoCollection(ingreso.getListCorreo());
            ingreso.getEnte().setEnteTelefonoCollection(ingreso.getListTelefonos());
            serv.editarEnteCorreosTlfns(ingreso.getEnte());
            en = ingreso.getEnte();
            if (ingreso.getListCorreoElim() != null) {
                for (EnteCorreo col : ingreso.getListCorreoElim()) {
                    serv.delete(col);
                }
            }
            if (ingreso.getListTelefonosElim() != null) {
                for (EnteTelefono col : ingreso.getListTelefonosElim()) {
                    serv.delete(col);
                }
            }
        }

        ingreso.getFicha().setDetFuncionario(nameUser);
        ingreso.getFicha().setTipo(ingreso.getTipoFicha());
        ingreso.getFicha().setCodigoPredial(ingreso.getNumIdentificador());
        ingreso.getFicha().setFechaApe(new Date());
        ingreso.getFicha().setTipoPredio("B");
        ingreso.getFicha().setTipoDep(true);
        ingreso.getFicha().setEstado(getCtlgItemById(134L));
        ingreso.getFicha().setEnte(en);
        return guardarFichaBien(ingreso.getFicha());
    }

    /**
     * Primero
     * obtiene el
     * ultimo número
     * de la
     * secuencia y
     * despues envia
     * guardar la
     * entity
     * RegEnteInterviniente.
     *
     * @param inter
     * Entity
     * @return
     * RegEnteInterviniente
     */
    @Override
    public RegEnteInterviniente guardaRegEnteInterviniente(RegEnteInterviniente inter) {
        return inscripcionServices.guardarRegEnteInterviniente(inter);
    }

    /**
     * Busca en la
     * tabla
     * RegEnteInterviniente
     * si existe
     * retorna la
     * entity caso
     * contario
     * retorna null
     *
     * @param cedRuc
     * número de
     * cédula o RUC
     * del
     * interviniente
     * @param nombre
     * Nombre del
     * Interviniente
     * @param
     * tipoInterv
     * tipo de
     * interviniente
     * si es natural
     * "N" o
     * juridica "J"
     * @return
     */
    @Override
    public RegEnteInterviniente buscaRegEnteInterv(String cedRuc, String nombre, String tipoInterv) {
        return (RegEnteInterviniente) serv.find(Querys.getRegEnteIntervinienteByCedRucByNombreByTipo, new String[]{"cedula", "nombre", "tipoInterv"}, new Object[]{cedRuc, nombre, tipoInterv});
    }

    /**
     * Envia a
     * realiza
     * Update a la
     * entity
     * RegEnteInterviniente
     *
     * @param inter
     * Entity
     * @return
     * RegEnteInterviniente
     */
    @Override
    public RegEnteInterviniente updateRegEnteInterviniente(RegEnteInterviniente inter) {
        return inscripcionServices.updateRegEnteInterviniente(inter);
    }

    /**
     * Consulta en
     * la RegFicha
     * por el Id
     *
     * @param
     * idFicha Id de
     * la RegFicha
     * @return
     * Entity
     * RegFicha
     */
    @Override
    public RegFicha getRegFichaByIdFicha(Long idFicha) {
        return (RegFicha) serv.find(RegFicha.class, idFicha);
    }

    /**
     * Actualiza la
     * tabla
     * Regficha y la
     * collection de
     * RegFichaBien
     * si no esta
     * vacio la
     * lista
     *
     * @param f
     * Entity
     * RegFicha
     */
    @Override
    public void actualizarRegFichaBien(RegFicha f) {
        List<RegFichaBien> list = (List<RegFichaBien>) f.getRegFichaBienCollection();
        f.setRegFichaBienCollection(null);
        try {
            serv.update(f);
            if (list != null) {
                for (RegFichaBien fb : list) {
                    serv.update(fb);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Primero
     * actualiza la
     * lista CatEnte
     * Y las
     * Collection de
     * EnteCorreo y
     * EnteTelefono
     * nuevos o
     * existentes y
     * despues
     * realiza la
     * eliminacion
     * de las lista
     * de EnteCorreo
     * y
     * EnteTelefono
     * si no estan
     * vacias la
     * listas
     *
     * @param e
     * Entity
     * CatEnte
     * @param
     * listCorreo
     * Lista
     * EnteCorreo a
     * eliminar
     * @param
     * listTelefonos
     * Lista
     * EnteTelefono
     * a eliminar
     */
    @Override
    public void actualizarCatEnteTelefEmails(CatEnte e, List<EnteCorreo> listCorreo, List<EnteTelefono> listTelefonos) {
        if (listTelefonos != null) {
            if (!listTelefonos.isEmpty()) {
                for (EnteTelefono listTelefono : listTelefonos) {
                    serv.delete(listTelefono);
                }
            }
        }
        if (listCorreo != null) {
            if (!listCorreo.isEmpty()) {
                for (EnteCorreo listCorreos : listCorreo) {
                    serv.delete(listCorreos);
                }
            }
        }
        serv.editarEnteCorreosTlfns(e);
    }

    /**
     * Primero
     * optiene la
     * lista de
     * RegMovimientoFicha
     * y despues
     * envia a
     * actualizar
     * RegFicha
     * despues envia
     * a guardar los
     * nuevos
     * movimientos
     * de la ficha,
     * y por ultimo
     * envia a
     * eliminar la
     * lista de
     * RegMovimientoFicha
     * si no esta
     * vacia la
     * lista La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param ficha
     * Entity
     * RegFicha
     * @param
     * listMovFichElim
     * Lista
     * RegMovimientoFicha
     * a eliminar
     */
    @Override
    public void actualizarRegFichaAndListMov(RegFicha ficha, List<RegMovimientoFicha> listMovFichElim) {
        List<RegMovimientoFicha> list = (List<RegMovimientoFicha>) ficha.getRegMovimientoFichaCollection();
        ficha.setRegMovimientoFichaCollection(null);
        try {
            bitacora.registrarFicha(ficha, ActividadesTransaccionales.MODIFICAR_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
            serv.persist(ficha);
            guardarRegMovimientoFicha(ficha, list);
            eliminarRegMovimientoFicha(listMovFichElim);
        } catch (Exception ex) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Optiene
     * CatEscrituraRural
     * y revisa si
     * no es nullo
     * para enviar a
     * guardar caso
     * contrario a
     * actualizar y
     * tambien
     * obtiene la
     * lista de
     * RegMovimientoFicha
     * para enviar a
     * guardar los
     * nuevos
     * movimientos
     * agregados
     * envia a
     * actualizar
     * RegFicha y
     * despues
     * CatEscritura
     * si no es
     * nulo, por
     * ultimo
     * elimina la
     * lista de los
     * movimientos
     * que contiene
     * la lista
     * listMovFichElim
     * La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param ficha
     * Entity
     * RegFicha a
     * actualizar
     * @param
     * escritura
     * Entity
     * CatEscritura
     * a guardar a
     * actualizar
     * @param
     * listMovFichElim
     * Lista de
     * RegMovimientoFicha
     * a eliminar
     * @param
     * permitido
     */
    @Override
    public void updateRegFicha(RegFicha ficha, CatEscritura escritura, List<RegMovimientoFicha> listMovFichElim, boolean permitido) {
        //CatEscrituraRural catEscrituraRural = ficha.getCatEscrituraRural();
        List<RegMovimientoFicha> listNew = (List) ficha.getRegMovimientoFichaCollection();
        ficha.setRegMovimientoFichaCollection(null);
        try {
            /*if (catEscrituraRural != null) {
             if (catEscrituraRural.getId() == null) {
             catEscrituraRural = (CatEscrituraRural) serv.persist(catEscrituraRural);
             } else {
             serv.update(ficha.getCatEscrituraRural());
             }
             ficha.setCatEscrituraRural(catEscrituraRural);
             }*/
            if (permitido) {
                bitacora.registrarFicha(ficha, ActividadesTransaccionales.MODIFICAR_FICHA, new BigInteger(new Date().getYear() + 1900 + ""), null);
            }
            manager.persist(ficha);
            /*if (escritura != null) {
             if (escritura.getIdEscritura() != null) {
             this.updateCatEscritura(escritura);
             } else {
             serv.persist(escritura);
             }
             }*/
            this.guardarRegMovimientoFicha(ficha, listNew);
            this.eliminarRegMovimientoFicha(listMovFichElim);
        } catch (Exception ex) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Actualiza la
     * CatEscritura
     *
     * @param
     * escritura
     * entity
     * CatEscritura
     */
    @Override
    public void updateCatEscritura(CatEscritura escritura) {
        serv.update(escritura);
    }

    /**
     * Verifica si
     * el Id de
     * RegMovimientoFicha
     * es nulo para
     * agregarle
     * RegFicha y
     * despues envia
     * a guardar el
     * RegMovimientoFicha
     * La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param ficha
     * Entity
     * RegFicha
     * @param
     * listNew Lista
     * RegMovimientoFicha
     */
    @Override
    public void guardarRegMovimientoFicha(RegFicha ficha, List<RegMovimientoFicha> listNew) {
        for (RegMovimientoFicha mf : listNew) {
            if (mf.getId() == null) {
                mf.setFicha(ficha);
                bitacora.registrarFichaMov(ficha, mf.getMovimiento(), ActividadesTransaccionales.AGREGAR_REFERENCIA, new BigInteger(new Date().getYear() + 1900 + ""));
                serv.persist(mf);
            }
        }
    }

    /**
     * Si la lista
     * RegMovimientoFicha
     * esta vacia no
     * realiza
     * ninguna
     * acción, caso
     * contario
     * realiza la
     * eliminacion
     * de loa
     * objetos que
     * tiene la
     * lista La
     * transaccion
     * debe
     * registrarse
     * en la
     * bitacora
     *
     * @param
     * listMovFichElim
     * Lista de
     * RegMovimientoFicha
     */
    @Override
    public void eliminarRegMovimientoFicha(List<RegMovimientoFicha> listMovFichElim) {
        if (!listMovFichElim.isEmpty()) {
            for (RegMovimientoFicha mf : listMovFichElim) {
                bitacora.registrarFichaMov(mf.getFicha(), mf.getMovimiento(), ActividadesTransaccionales.ELIMINAR_REFERENCIA, new BigInteger(new Date().getYear() + 1900 + ""));
                serv.delete(mf);
            }
        }
    }

    /**
     * Buscar en la
     * tabla
     * RegFicha por
     * el id de
     * RegMovimiento
     *
     * @param id del
     * RegMovimiento
     * @return Lista
     * de RegFicha
     */
    @Override
    public List<RegFicha> getRegFichaByMovimientoId(Long id) {
        List<RegFicha> regFichas;
        try {
            regFichas = serv.findAll(Querys.getRegFichasByMovimientoId, new String[]{"idmov"}, new Object[]{id});
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return regFichas;
    }

    /**
     * Busca en la
     * tabla
     * RegMovimientoCliente
     * por id del
     * movimiento
     *
     * @param id de
     * RegMovimiento
     * @return Lista
     * RegMovimientoCliente
     */
    @Override
    public List<RegMovimientoCliente> getRegMovimientoClienteByMovimiento(Long id) {
        List<RegMovimientoCliente> movimientoClientes;
        movimientoClientes = serv.findAll(Querys.getRegMovimientoClienteByMovimiento, new String[]{"movid"}, new Object[]{id});
        if (movimientoClientes != null && !movimientoClientes.isEmpty()) {
            movimientoClientes.size();
        }
        return movimientoClientes;
    }

    /**
     * Busca en la
     * tabla
     * RegMovimientoRepresentante
     * por id del
     * movimiento
     *
     * @param id Del
     * RegMovimiento
     * @return Lista
     * RegMovimientoRepresentante
     */
    @Override
    public List<RegMovimientoRepresentante> getRegRegMovimientoRepresentanteByMovimiento(Long id) {
        List<RegMovimientoRepresentante> movimientoRepresentante;
        movimientoRepresentante = serv.findAll(Querys.getRegRegMovimientoRepresentanteByMovimiento, new String[]{"movid"}, new Object[]{id});
        if (movimientoRepresentante != null && !movimientoRepresentante.isEmpty()) {
            movimientoRepresentante.size();
        }
        return movimientoRepresentante;
    }

    /**
     * Busca en la
     * tabla
     * RegMovimientoSocios
     * por id del
     * movimiento
     *
     * @param id Del
     * RegMovimiento
     * @return Lista
     * RegMovimientoSocios
     */
    @Override
    public List<RegMovimientoSocios> getRegMovimientoSociosByMovimiento(Long id) {
        List<RegMovimientoSocios> movimientoSocios;
        movimientoSocios = serv.findAll(Querys.getRegMovimientoSociosByMovimiento, new String[]{"movid"}, new Object[]{id});
        if (movimientoSocios != null && !movimientoSocios.isEmpty()) {
            movimientoSocios.size();
        }
        return movimientoSocios;
    }

    /**
     * Busca en la
     * tabla
     * RegMovimientoCapital
     * por id del
     * movimiento
     *
     * @param id Del
     * RegMovimiento
     * @return Lista
     * RegMovimientoCapital
     */
    @Override
    public List<RegMovimientoCapital> getRegMovimientoCapitalByMovimiento(Long id) {
        List<RegMovimientoCapital> movimientoCapital;
        movimientoCapital = serv.findAll(Querys.getRegMovimientoCapitalByMovimiento, new String[]{"movid"}, new Object[]{id});
        if (movimientoCapital != null && !movimientoCapital.isEmpty()) {
            movimientoCapital.size();
        }
        return movimientoCapital;
    }

    /**
     * Obtiene la
     * lista de
     * correos por
     * el Id del
     * CatEnte
     *
     * @param id Id
     * de CatEnte
     * @return Lista
     * de EnteCorreo
     */
    @Override
    public List<EnteCorreo> getEnteCorreoList(Long id) {
        List<EnteCorreo> list = serv.findMax(Querys.getEnteCorreoByEnteId, new String[]{"enteId"}, new Object[]{id}, 1);
        return (List<EnteCorreo>) EntityBeanCopy.clone(list);

    }

    /**
     * Busca en la
     * tabla CatEnte
     * por el Id del
     * CatEnte
     *
     * @param id Id
     * de CatEnte
     * @return
     * Enttiy
     * CatEnte
     */
    @Override
    public CatEnte getCatEnteById(Long id) {
        return (CatEnte) serv.find(CatEnte.class, id);
    }

    /**
     * Inactiva el
     * propietario
     * del predio.
     *
     * @param
     * propietarios
     * Lista de
     * CatPredioPropietario.
     */
    @Override
    public void eliminarPropietarios(List<CatPredioPropietario> propietarios) {
        if (!propietarios.isEmpty()) {
            for (CatPredioPropietario p : propietarios) {
                p.setEstado("I");
                serv.update(p);
            }
        }
    }

    /**
     * Filtra los
     * Registros de
     * la tabla
     * RegFicha por
     * los linderos
     * y Tipo de
     * ficha.
     *
     * @param
     * lindero
     * Lidero.
     * @param tipo
     * Tipo Ficha.
     * @return Lista
     * de RegFucha.
     */
    @Override
    public List<RegFicha> getFichasRegistralesByLinderos(String lindero, Long tipo) {
        return serv.findAll(Querys.getRegFichaByLindero, new String[]{"linderos", "tipo"}, new Object[]{lindero, tipo});
    }

    /**
     * Envia a
     * persistir
     * CatEnte y
     * despues
     * EnteTelefono
     * y EnteCorreos
     *
     * @param ente
     * Entity
     * CatEnte
     * @return
     * CatEnte.
     */
    @Override
    public CatEnte guardarCatEnteTelefEmails(CatEnte ente) {
        return serv.guardarEnteCorreosTlfns(ente);
    }

    /**
     * Guarda en la
     * tabla de
     * bitacora del
     * registro de
     * la propiedad
     * un registro
     * que
     * representa
     * una
     * generacion de
     * un documento
     * pdf que
     * contiene
     * datos de una
     * ficha
     *
     * @param ficha
     * @param anio
     * @param
     * numTramite
     * @return
     */
    @Override
    public Boolean registrarImpresionFicha(RegFicha ficha, BigInteger anio, BigInteger numTramite) {
        try {
            bitacora.registrarFicha(ficha, ActividadesTransaccionales.IMPRESION_FICHA, anio, numTramite);
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
        return true;
    }

    /**
     * Retorna una
     * lista de tipo
     * CatEscritura
     * que estan
     * relacionadas
     * a un Predio
     *
     * @param id
     * @return
     */
    @Override
    public List<CatEscritura> getCatEscrituraByPredioList(Long id) {
        try {
            Map paramt = new HashMap<>();
            paramt.put("idEscritura", id);
            return manager.findObjectByParameterList(CatEscritura.class, paramt);
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    public boolean transferirPropietarios(List<RegMovimientoCliente> inter, CatPredio predio, List<CatPredioPropietario> propietarios, List<CatEnte> seleccionados, String usuario) {
        boolean flag = false;
        HiberUtil.requireTransaction();
        Session sess = HiberUtil.getSession();
        try {
            CatPredioPropietario prop;
            if (predio != null && predio.getId() != null) {
                if (inter != null && !inter.isEmpty()) {
                    predio.setCatPredioPropietarioCollection(null);
                    if (propietarios != null && !propietarios.isEmpty()) {
                        for (CatPredioPropietario cpp : propietarios) {
                            sess.delete(cpp);
                        }
                        if (!inter.isEmpty()) {
                            for (RegMovimientoCliente i : inter) {
                                prop = new CatPredioPropietario();
                                prop.setPredio(predio);
                                if (i.getEnte() != null && i.getEnte().getId() != null) {
                                    prop.setEnte(i.getEnte());
                                    prop.setEstado("A");
                                    prop.setUsuario(usuario);
                                    prop.setFecha(new Date());
                                    sess.merge(prop);
                                } else {
                                    return false;
                                }
                            }
                        } else if (seleccionados != null) {
                            for (CatEnte e : seleccionados) {
                                prop = new CatPredioPropietario();
                                prop.setPredio(predio);
                                prop.setEnte(e);
                                prop.setEstado("A");
                                prop.setUsuario(usuario);
                                prop.setFecha(new Date());
                                sess.merge(prop);
                            }
                        }
                        if (predio.getInstCreacion() == null) {
                            predio.setInstCreacion(new Date());
                        }
                        sess.merge(predio);
                        flag = true;
                    } else {
                        predio.setCatPredioPropietarioCollection(null);
                        for (RegMovimientoCliente i : inter) {
                            prop = new CatPredioPropietario();
                            prop.setPredio(predio);
                            if (i.getEnte() != null && i.getEnte().getId() != null) {
                                prop.setEnte(i.getEnte());
                                prop.setEstado("A");
                                prop.setUsuario(usuario);
                                prop.setFecha(new Date());
                                sess.merge(prop);
                            } else {
                                return false;
                            }
                        }
                        if (predio.getInstCreacion() == null) {
                            predio.setInstCreacion(new Date());
                        }
                        sess.merge(predio);
                        flag = true;
                    }
                } else if (seleccionados != null && !seleccionados.isEmpty()) {
                    predio.setCatPredioPropietarioCollection(null);
                    if (propietarios != null && !propietarios.isEmpty()) {
                        for (CatPredioPropietario cpp : propietarios) {
                            sess.delete(cpp);
                        }
                    }
                    for (CatEnte i : seleccionados) {
                        prop = new CatPredioPropietario();
                        prop.setPredio(predio);
                        prop.setEnte(i);
                        prop.setEstado("A");
                        prop.setUsuario(usuario);
                        prop.setFecha(new Date());
                        sess.merge(prop);
                    }
                    if (predio.getInstCreacion() == null) {
                        predio.setInstCreacion(new Date());
                    }
                    sess.merge(predio);
                    flag = true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    @Override
    public Boolean compruebaFichaTareaRegistro(Long liquidacion, Long tareaDinardap, Long numFicha) {
        try {
            Object ob = null;
            if (liquidacion != null) {
                ob = manager.findUnique(Querys.getExisteFichaTareaRegistro, new String[]{"idTarea", "ficha"}, new Object[]{liquidacion, numFicha});
            }
            if (tareaDinardap != null) {
                ob = manager.findUnique(Querys.getExisteFichaTareaDinardap, new String[]{"idTarea", "ficha"}, new Object[]{tareaDinardap, numFicha});
            }
            return ob != null;
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    @Override
    public List<CatPredioPropietario> getNewsPropietariosPredioByFicha(Long idficha) {
        List<RegFichaPropietarios> listProps;
        List<CatPredioPropietario> list = new ArrayList<>();
        CatPredioPropietario prop;
        CtlgItem tipoPropietario;
        try {
            tipoPropietario = manager.find(CtlgItem.class, 56L); // TIPO PROPIETARIO POR DEFECTO
            listProps = manager.findAll(Querys.getRegPropietariosByFicha, new String[]{"idficha"}, new Object[]{idficha});
            if (listProps != null) {
                for (RegFichaPropietarios fp : listProps) {
                    if (fp.getEnte() != null) {
                        prop = new CatPredioPropietario();
                        prop.setEnte(fp.getEnte());
                        prop.setTipo(tipoPropietario);
                        prop.setEsResidente(Boolean.TRUE);
                        prop.setEstado("A");
                        list.add(prop);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(FichaIngresoNuevoEjb.class.getName()).log(Level.SEVERE, null, e);
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public CatPredio getPredioByPredialant(String predialant) {
        if (predialant == null) {
            return null;
        }
        return (CatPredio) serv.find(Querys.getPredioByPredialAnt, new String[]{"predialant"}, new Object[]{predialant});
    }

}
