/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.catastro;

import com.origami.sgm.bpm.models.ListCollectionsReff;
import com.origami.sgm.bpm.models.ModelDepuracionEntes;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatBloqueObraEspecial;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatCertificadoAvaluo;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEdfCategProp;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEdificacionPisosDet;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatNacionalidad;
import com.origami.sgm.entities.CatPais;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioClasificRural;
import com.origami.sgm.entities.CatPredioCultivo;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioEdificacionProp;
import com.origami.sgm.entities.CatPredioLinderos;
import com.origami.sgm.entities.CatPredioObraInterna;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatProvincia;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FotoPredio;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.GeDocumentos;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenSecuenciaNumComprobante;
import com.origami.sgm.entities.predio.models.FichaModel;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 * Metodos de para la ficha de catastro.
 *
 * @author CarlosLoorVargas
 */
public interface CatastroServices {

    /**
     * Busca en la tabla de CatPredio por el id de la tabla.
     *
     * @param id Id de la tabla CatPredio
     * @return CatPredio si existe caso contrario retorna null.
     */
    public CatPredio getPredioId(Long id);

    /**
     * Busca en la tabla de CatPredio por el número de predio.
     *
     * @param numPredio Campo número de Predio.
     * @return CatPredio si existe caso contrario retorna null.
     */
    public CatPredio getPredioNumPredio(Long numPredio);

    /**
     * Busca en la tabla CatPredio por query de consulta y el arreglo de los
     * parametros de la consulta que se pasa como parametro.
     *
     * @param query Query de la consulta que se realizar.
     * @param params Arreglo de parametros que nesecita la consulta.
     * @param vals Arreglo de valores para cada parametro.
     * @return CatPredio si existe caso contrario retorna null.
     */
    public CatPredio getPredio(String query, String[] params, Object[] vals);

    /**
     * Realiza la busqueda de todos los predios que esten el estado activo.
     *
     * @return Lista de todos los predios que estan en estado activo.
     */
    public List<CatPredio> getPredios();

    /**
     * Recibe el query para CatPredio que se va a realizar, la lista de los
     * parametros de la consulta y la lista de los valores para cada uno de los
     * parametros.
     *
     * @param query Query de la consulta que se realizar.
     * @param params Arreglo de parametros que nesecita la consulta.
     * @param vals Arreglo de valores para cada parametro.
     * @return Lista de los predios si existen caso contrario retorna null.
     */
    public List<CatPredio> getPredios(String query, String[] params, Object[] vals);

    /**
     * Recibe el query de la consulta que se va a realizar, la lista de los
     * parametros de la consulta y la lista de los valores para cada uno de los
     * parametros.
     *
     * @param query Query de la consulta para CatEscritura que se realizar.
     * @param params Arreglo de parametros que nesecita la consulta.
     * @param vals Arreglo de valores para cada parametro.
     * @return Entity CatEscritura si existe caso contrario null.
     */
    public CatEscritura getEscritura(String query, String[] params, Object[] vals);

    /**
     * Envia a realizar la actualizacion de la entity CatPredio
     *
     * @param s1 Entity CatPredio persistida.
     * @param numPredio Número de Predio.
     * @return true si se realizo la actualización caso contrario false.
     */
    public Boolean editarPredio(CatPredio s1, BigInteger numPredio);

    /**
     * Realiza la actualización del registro en la base
     *
     * @param s4 Entity CatPredioS4 a actualizar en la base.
     * @return True si la transacción se realizo correctamente caso contario
     * false.
     */
    public Boolean editPredioS4(CatPredioS4 s4);

    /**
     * Genera en número de predio y envia a persistir la entity en la base de
     * datos.
     *
     * @param predio Entity CatPredio persistida o sin persistir.
     * @return Entity CatPredio persistida si no error en la transacciòn caso
     * contario null.
     */
    public CatPredio generarNumPredio(CatPredio predio);

    /**
     * Metodos del procesos Propiedad Horizontal.
     *
     * @return PropiedadHorizontalServices.
     */
    public PropiedadHorizontalServices propiedadHorizontal();

    /**
     * Envia a actualizar la entity CatEscritura en la base de datos
     *
     * @param escritura Entity CatEscritura
     * @return True si no hay ningun error en la transacción caso contrario
     * false.
     */
    public Boolean editarEscritura(CatEscritura escritura);

    /**
     * Envia a actualizar la entity CatPredioS6 en la base de datos
     *
     * @param catPredioS6 Entity CatPredioS6
     * @return True si no hay ningun error en la transacción caso contrario
     * false.
     */
    public Boolean editarPredioS6(CatPredioS6 catPredioS6);

    /**
     * Realiza la persistencia de los nuevos objectos de la entity
     * edificacionEliminar y los que existen los envia a actualizar, de la misma
     * form hace con los objecto de la entity propiedadesEliminar cambia el
     * campo estado a false de la lista propiedadesEliminar y los actualiza en
     * la base de datos.
     *
     * @param predio Entity CatPredio.
     * @param edificacionEliminar Entity CatPredioEdificacion.
     * @param propiedadesEliminar Entity CatPredioEdificacionProp.
     * @return True se no ocurrio ningun error en la transacción caso contrario
     * null.
     */
    public Boolean editarPrediosEdificaciones(CatPredio predio, List<CatPredioEdificacion> edificacionEliminar, List<CatPredioEdificacionProp> propiedadesEliminar);

    /**
     * Si la entity no esta persistida envia a realizar la persistencia en la
     * base de datos caso contrario envia a realizar la actualizacion de los
     * canpos.
     *
     * @param temp Entity CatPredioEdificacion
     * @param catPredioEdificacionProps Lista de CatPredioEdificacionProp
     * @return True se no ocurrio ningun error en la transacción caso contrario
     * null.
     */
    public Boolean guadarOEditarPredioEdifProp(CatPredioEdificacion temp, List<CatPredioEdificacionProp> catPredioEdificacionProps);

    /**
     * Envia a actualizar CatPredioS12 en la base de datos.
     *
     * @param catPredioS12 Entity CatPredioS12
     * @return True se no ocurrio ningun error en la transacción caso contrario
     * null.
     */
    public Boolean editPredioS12(CatPredioS12 catPredioS12);

    /**
     * Envia a persistir la entity CatPredioS4 en la base de datos.
     *
     * @param s4 Entity CatPredioS4
     * @return Entity CatPredioS4 si no hay error en la transacción caso
     * contrario null.
     */
    public CatPredioS4 guardarPredioS4(CatPredioS4 s4);

    /**
     * Obtiene el número de predio y lo envia a persistir si no hubo error el la
     * transacción retorna la entity persistida.
     *
     * @param predio Entity CatPredio a persistir.
     * @return Entity persitida si no hubo error en la transacción caso
     * contrario null.
     */
    public CatPredio guardarPredio(CatPredio predio);

    /**
     * Realiza la verificación que la suma de todos los frentes sean igual al
     * frenteTotal.
     *
     * @param catPredioS4 entity CatPredioS4.
     * @return True si la suma de los frentes es igual al frentetotal, caso
     * contario null.
     */
    public Boolean validarFrente(CatPredioS4 catPredioS4);

    /**
     * Realiza la suma de los frente 1 y 2, tambien de fondo 1 y 2 y despues de
     * obtiene los valores de las 2 sumas realiza el calculo del area Calculada.
     *
     * @param catPredioS4 Entity CatPredioS4
     * @return el valor del area calculada.
     */
    public BigDecimal areaCalculada(CatPredioS4 catPredioS4);

    /**
     * Realiza la busqueda en CatTipoConjunto por el id de la tabla.
     *
     * @param l Primary key de la CatTipoConjunto.
     * @return Entity CatTipoConjunto si existe caso contrario null.
     */
    public CatTipoConjunto getTipoConjunto(long l);

    /**
     * Recibe CatPredioS12 sin persistir.
     *
     * @param catPredioS12 Entity CatPredioS12 sin persistir.
     * @param usos
     * @return Entity CatPredioS12 persistida si no hubo ningun error en la
     * transacción caso contario null.
     */
    public CatPredioS12 guardarPredioS12(CatPredioS12 catPredioS12, List<CtlgItem> usos);

    /**
     * Envia a persistir en la base el registro que contiene la entity que se
     * pasa como parametro.
     *
     * @param catPredioS6 Entity sin persistir CatPredioS6
     * @param vias
     * @param instalacionesEspeciales
     * @return Entity catPredioS6 ya persistida si hubo ningun error caso
     * contrario null.
     */
    public CatPredioS6 guardarPredioS6(CatPredioS6 catPredioS6, List<CtlgItem> vias, List<CtlgItem> instalacionesEspeciales);

    /**
     * Realiza la busqueda en la tabla por el id.
     *
     * @param id Primary key de la tabla CatPredioS4.
     * @return CatPredioS4 sis existe caso contrario null.
     */
    public CatPredioS4 getCatPredioS4(Long id);

    /**
     * Realiza la busqueda en la tabla por el id.
     *
     * @param id Primary key de la tabla CatPredioS6.
     * @return CatPredioS6 sis existe caso contrario null.
     */
    public CatPredioS6 getCatPredioS6(Long id);

    /**
     * Realiza la busqueda en la tabla por el id.
     *
     * @param id Primary key de la tabla CatPredioS12.
     * @return CatPredioS12 sis existe caso contrario null.
     */
    public CatPredioS12 getCatPredioS12(Long id);

    /**
     * Realiza la busqueda por el id de la tabla CatCiudadela.
     *
     * @param id Primary key de la tabla CatCiudadela.
     * @return la CatCiudadela si existe caso contrario retorna null.
     */
    public CatCiudadela getCiudadelaById(Long id);

    /**
     * Busca todas la ciudadelas que esta asociadas al tipo de conjunto que se
     * pasa como parametro.
     *
     * @param id Id de la tabla CatTipoConjunto
     * @return Lista CatCiudadela si existen ciudadeles con el tipo de conjunto
     * que se pasa como parametos caso contarion retorna null.
     */
    public List<CatCiudadela> getCiudadelasByTipoC(Long id);

    

    /**
     * Recorre cada elemento de la lista {@link ModelDepuracionEntes} y envia a
     * actualizar todos los elementos de la lista que contiene
     * {@link ListCollectionsReff} y despues inactiva la lista de
     * {@link CatEnte}
     *
     * @param sel Lista de {@link CatEnte} que seran inactivados en la bade de
     * datos.
     * @param modificarLista Lista de elementos a ser actualizados en la base de
     * datos.
     * @param user
     * @return true si todos elementos se actualizaron correctamente.
     */
    public Boolean actualizarEnteAndCollection(List<CatEnte> sel, List<ModelDepuracionEntes> modificarLista, String user);

    public Boolean existePredio(CatPredio s1);

    public Integer getManzanaMaxPredio(CatPredio s1);
    
    public Integer getSolarMaxPredio(CatPredio s1);

    public Integer getUnidadMaxPredio(CatPredio s1);

    /**
     * Usa un 'LIKE' para realizar la busqueda en el campo rezonSocial de
     * {@link CatEnte}
     *
     * @param razonSocial Valor a buscar.
     * @return Lista de {@link CatEnte}.
     */
    public List<CatEnte> getEntesByRazonSocial(String razonSocial);

    /**
     * Realiza un update a la entity que recibe como parametro.
     *
     * @param valido {@link CatEnte}.
     * @return True si fue actualizado caso contrario false.
     */
    public Boolean actualizarEnte(CatEnte valido);

    /**
     * Envia a consultar en {@link CatEnte} usando 'LIKE' primero para el campo
     * apellidos y despues por el de nombres y retorna la lista con todos los
     * registros encontrados.
     *
     * @param entry Valor a consultar en la base de datos.
     * @param esPersona Si es persona natutal o juridica.
     * @return Lista de {@link CatEnte} si se encontraron coincidencia en los
     * nombres o apellidos.
     */
    public List<CatEnte> getCatEnteByNombresApellidos(String entry, Boolean esPersona);

    /**
     *
     * @param p
     * @return
     */
    public Boolean guardarHistoricoPredio(com.origami.sgm.entities.historic.Predio p);

    public Boolean guardarHistoricoPredio(Long numPredio, String jsPredioAnt, String jsPredioAct, String usurario, String obs, String fedifAnt, String fedifAct, String fModelAnt, String fModelAct, GeDocumentos documento);

    public Long existeEnteByCiRuc(String[] param, Object[] values);

    public CatCertificadoAvaluo guardarCertificado(CatCertificadoAvaluo c);

    public RenSecuenciaNumComprobante saveRenSecuenciaNumComprobante(RenSecuenciaNumComprobante comprobante);

    public List getPropietarios(String ident, String nombres, String apellidos, String rsocial);

    public CatPredio getAvaluoPredio(CatPredio predio, Integer anio);

    public Boolean guardarHistoricoPredio(Long numPredio, String pant, String pact, String sac, String usurario, String obs);

    // public DatosValoracionPredio getValoracionPredios(Long id, Integer periodo, String user);
    public List<FichaModel> getDatosFicha(Long codPredio);

    public List<FichaModel> getClases();

    public CatPredioPropietario getPredioPropietarioById(Long valueOf);

    public boolean existePropietarioPredio(CatPredio predio, Long idEnte);

    public Boolean guardarEspecTexEdif(List<FichaModel> ficha, CatPredio ed, String user);

    public CatPredioEdificacion guardarEdificacion(CatPredioEdificacion edif, String usuario);

    public CatPredioPropietario guardarPropietario(CatPredioPropietario p, String usuario);

    public Boolean quitarEdificacionGeneral(CatPredioEdificacion ed);

    public Collection<CatPredioEdificacion> getEdificaciones(CatPredio predio);

    public Boolean quitarFoto(FotoPredio f);

    public Boolean guardarDetallePisos(List<CatEdificacionPisosDet> pisos);

    public List<CatNacionalidad> getNacionalidades();

    public List<CatPais> getPaises();

    public List<CtlgItem> getItemsByCatalogo(String catalogo);

    public List<CatPredio> registrarPHs(List<CatPredio> v, List<CatPredio> h);

    public CatPredioS6 guardarPredioS6(CatPredioS6 catPredioS6);

    public CatPredio saveUpdatePredio(CatPredio predio);

    public CatPredio sumarAreaEdificacion(CatPredio predio);

    /**
     * Devuelve el listado de predios de una manzana
     *
     * @param codParroquia
     * @param codZona
     * @param codSector
     * @param codManzana
     * @return
     */
    public List<CatPredio> findAllByManzana(Short codParroquia, Short codZona, Short codSector, Short codManzana);

    public CatPredioS4 getPredioS4ByPredio(CatPredio predio);

    public CatPredioS6 getPredioS6ByPredio(CatPredio predio);

    public List<AclUser> getUser(GeDepartamento geDepartamento);

    public List<CatEdfCategProp> getCategoriasModel(Collection<CatPredioEdificacion> catPredioEdificacionCollection);

    public List<CatPredioLinderos> guardarLinderos(List<CatPredioLinderos> linderos);

    public Boolean actualizarLindero(CatPredioLinderos c);

    public List<CatEdfCategProp> getCategoriasConst();

    public List<CatEdfCategProp> getCategoriasConstByEstado();

    public TreeNode getTreeNode(List<CatEdfCategProp> catProp);

    // Version Ibarra
    public CatPredioCultivo getPredioCultivoById(Long valueOf);

    public CatPredioCultivo guardarCultivo(CatPredioCultivo p, String usuario);

    public CatBloqueObraEspecial getBloqueObraEspecialById(Long valueOf);

    public CatBloqueObraEspecial guardarObraEspecial(CatBloqueObraEspecial p, String usuario);

    public void guardarEdificacion(CatPredioEdificacion edificacion);

    public boolean existePropietarioPredioCiudadano(CatPredio predio, String ciuCedRuc);

    public CtlgItem getDefaultItem(String catalogo);

    public CtlgCatalogo getCatalogoNombre(String argumento);

    public CatPredioObraInterna getPredioObraInternaById(Long valueOf);

    public CatPredioClasificRural getPredioClasificRuralById(Long valueOf);

    public CatPredioClasificRural guardarClasificacionSueloRural(CatPredioClasificRural p, String usuario);

    public CatPredioEdificacionProp getBloqueCaracteristicaById(Long valueOf);

    public CatPredioEdificacion getPredioBloqueById(Long valueOf);

    public CatPredioEdificacion guardarBloque(CatPredioEdificacion b, String usuario);

    public CtlgItem getCtlgitemById(Long valueOf);

    public CatPredioEdificacionProp guardarCaracteristica(CatPredioEdificacionProp c);

    public List<CatEdfProp> getPropiedadesConst(CatEdfCategProp categoriaConst);

    public List<CatEdfProp> getPropiedadesConstByEstado(CatEdfCategProp categoriaConst);

    public void guardarCaracteristica(CatEdificacionPisosDet caract);

    public Boolean actualizarFotos(FotoPredio fotoPredio);

    public List<CatProvincia> provincias();

    public Boolean actualizarPredio(CatPredio px);

    public CatPredioPropietario guardarPropietarioCiudadano(CatPredioPropietario p, String usuario);

    public CtlgItem getItemByCatalagoOrder(String prediobloquerevestpiso, BigInteger orden);

    public CatPredioObraInterna guardarObraInterna(CatPredioObraInterna p, String usuario);

    public Short obtenerNumEdificacion(CatPredio predio);

    public List<CatPredioEdificacionProp> getCaracteristicasEdificacion(CatPredioEdificacion edf);

    public List<CatPredioEdificacionProp> getCaracteristicasEdificacionByEstado(CatPredioEdificacion edf);

    public List<CatParroquia> getProrroquias(Long l);

    public CatCanton getCantonDefault();

    public List<CatCiudadela> getCiudadelasByTipoConjunto(CatTipoConjunto tipoConjunto);

    public CatPredio savePredio(CatPredio p);

    public CatPredio getPredioByClaveCat(String claveCat);

    public CatPredio getPredioByClaveCatAnt(String claveCatAnt);

    public List<CtlgItem> getListadoCultivos(Integer padreItem);

    public List<CtlgItem> getListadoItemsCultivos(Integer hijoItem);

    public List<FotoPredio> getFotosBloque(CatPredioEdificacion bloq);

    /**
     * Realiza la busqueda de los CatPredioPropietarios por numero de
     * identificacion
     *
     * @param cuiCiRuc
     * @return Lista de CatPredioPropietario
     */
    public List<CatPredioPropietario> getPropietarios(String cuiCiRuc);

    public CatParroquia getCatParroquia(Short parroquia);

    public GeDepartamento getDptoByName(String nombre);

    /**
     * Realiza la busqueda de los CatPredio por numero de identificacion del
     * propietario
     *
     * @param cuiCiRuc
     * @return Lista de CatPredio
     */
    public List<CatPredio> getPrediosByPropietarios(String cuiCiRuc);
    /**
     * Realiza la busqueda de los CatPredio por numero de identificacion del
     * propietario sin predios matrices
     *
     * @param cuiCiRuc
     * @return Lista de CatPredio
     */
    public List<CatPredio> getPrediosSinMatrices(String cuiCiRuc);

    public Boolean subirFotoPredio(ByteArrayInputStream byteArrayInputStream, String nombreFoto, String contentType, CatPredio predi);
    
    public Boolean subirFotoBloque(ByteArrayInputStream byteArrayInputStream, String nombreFoto, String contentType, CatPredioEdificacion bloque);
    /*
    *METODO PARA VVER SI EL PREDIO POSEE DEUDAS AL ANIO ACTUAL
    *    
    */
    public RenLiquidacion getDeudasPredioAnioActual(CatPredio predio, Integer anio);
    
    public RenLiquidacion getLiquidacionByPredio(CatPredio predio);
    
    public RenLiquidacion getPagadasPredioAnioActual(CatPredio predio);
    
    public GeDocumentos saveInactivarDocumentos(GeDocumentos documentos);
    
}
