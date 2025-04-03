/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

import com.origami.sgm.entities.database.SchemasConfig;
import java.util.Date;
import util.Utils;

/**
 *
 * @author Equipo Origami :v
 */
public class Querys {

    /*CONSULTA PARA TRAER PARROQUIA, ZONA, SECTOR, MZ, SOLAR*/
    public static String getFichaCatastral = "select cp from CatPredio cp where cp.estado = 'A' ORDER BY cp.parroquia, cp.zona, cp.sector, cp.mz asc";
    // public static String getFichaCatastralFinal = "select cp from CatPredio cp GROUP BY cp.parroquia, cp.zona, cp.sector, cp.mz ORDER BY cp.parroquia, cp.zona, cp.sector, cp.mz asc";

    /* CONSULTAS PARA ACLSERVICE */
    public static String getUsuariobyUserPass = "select e from AclUser e where e.usuario = :user and e.pass = :pass and e.sisEnabled=true";
    public static String getAclUserByCiRucEnte = "select u from AclUser u where u.ente.ciRuc = :id";
    public static String getAclUserByUser = "select u from AclUser u where u.usuario = :user";
    public static String getEnteByUsername = "select u.ente from AclUser u where u.usuario = :user";
    public static String getAclUserByID = "select u from AclUser u where u.id = :id";
    public static String getAclUserDirectorByRol = "select n2 from AclUser n2"
            + "                join n2.aclRolCollection f2"
            + "                where f2.id = :idRol AND n2.userIsDirector = " + SchemasConfig.FILTER_ESTADO + " AND n2.sisEnabled = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getAclUserTecnicosByRol = "select n2 from AclUser n2"
            + "                join n2.aclRolCollection f2"
            + "                where f2.id = :idRol AND n2.userIsDirector = false AND n2.sisEnabled = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getDigitalizadorByTipoTramite = "select u from ParametrosDisparador u where u.tipoTramite = :tipoTramite and u.varResp = :var";
    public static String getMsgNotificacionByTipo = "select m from MsgFormatoNotificacion m where m.tipo.id = :tipo and m.estado=1";
    public static String getListEnteByCorreo = "select e.ente from EnteCorreo e where e.email = :email";
    public static String getListEnteByTelefono = "select e.ente from EnteTelefono e where e.telefono = :telefono";
    public static String getAllEntesByEstadoT = "select e from CatEnte e WHERE e.estado = 'T' ";
    public static String getEnteByIdent = "select e from CatEnte e where e.ciRuc = :ciRuc";
    public static String getPersonaByCi = "select e from CatEnte e where e.ciRuc = :ciRuc and e.esPersona = :persona";
    public static String getAclRolByEstado = "select a from AclRol a where a.estado = :estado";
    public static String getIdAclRolByDept = "select a.id from AclRol a where a.departamento = :dept AND a.estado= " + SchemasConfig.FILTER_ESTADO + "";
    public static String getIdAclRolByDepts = "select a from AclRol a where a.departamento = :dept AND a.estado= " + SchemasConfig.FILTER_ESTADO + "";
    public static String getCertExoLocalActByAnioAndLocal = "select a from CertificadoExoneracionLocalActivos a where a.estado = true AND a.anio = :anio AND a.localComercial = :local";

    public static String getAclRolByIdDirector = "select a from AclRol a where a.estado = true and a.id = :idRol and a.isDirector = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getAclRolByNombre = "select e from AclRol e where e.nombre=:nombre and e.estado = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getListAclRolByDep = "select e from AclRol e where e.departamento.id=:departamento and e.estado = " + SchemasConfig.FILTER_ESTADO + " ORDER BY e.nombre ASC";
    public static String getListAclUserByRol = "SELECT a FROM AclUser a INNER JOIN a.aclRolCollection r WHERE r.id = :idRol AND a.sisEnabled = TRUE ORDER BY a.usuario";
    public static String getListAclRolByDepNull = "select e from AclRol e where e.departamento IS NULL and e.estado = " + SchemasConfig.FILTER_ESTADO + " ORDER BY e.nombre ASC";
    public static String getPropietariosByCiOrRUC = "select e from CatPredioPropietario e where e.ente.ciRuc = :ciRuc";
    public static String getGeDepartamentoByEstado = "select a from GeDepartamento a where a.estado = :estado";
    public static String getRegCatPapelByPapel = "select pa from RegCatPapel pa where lower(pa.papel) like :papel ORDER BY pa.papel ASC";
    public static String getHistoricoTramiteById = "SELECT ht FROM HistoricoTramites ht WHERE ht.id = :id";
    public static String getEstadoHistoricoTramiteById = "SELECT ht.estado FROM HistoricoTramites ht WHERE ht.id = :id";
    public static String getHistoricoTramiteByIdNew = "SELECT ht FROM HistoricoTramites ht WHERE ht.idTramite = :id";
    public static String getObservacionesActivasHistoricoTramites = "select ob from Observaciones ob where ob.idTramite = :idTramite and ob.estado = :estado order by ob.fecCre";
    public static String getVuItemsByCatalogo = "SELECT v FROM VuItems v WHERE v.catalogo = :catalogo";
    public static String getVuItemsByNombre = "SELECT v FROM VuItems v WHERE v.catalogo.nombre = :nombre";
    public static String getVuItems = "SELECT v FROM VuItems v ORDER BY v.nombre ASC";
    public static String getCtlgItemList = "Select e from CtlgItem e";
    public static String getCantonList = "Select e from CatCanton e";
    public static String getCatCategoriasPropConstruccionList = "Select e from CatEdfCategProp e ORDER BY e.guiOrden ASC";
    public static String getCatCategoriasPropConstruccionbyCatagoria = "Select e from CatEdfCategProp e ORDER BY e.guiOrden ASC";
    public static String getTipoDominioList = "Select e from CatTiposDominio e";
    public static String getCtlgItemListByNombreDeCatalogo = "Select e from CtlgItem e Where e.catalogo.nombre=:catalogo ORDER BY e.valor ASC";
    public static String getCatCantonById = "SELECT c FROM CatCanton c WHERE c.id = :id";
    public static String getMatFormulaByTipoTramiteID = "SELECT c FROM MatFormulaTramite c WHERE c.tipoTramite = :idTipoTramite AND c.estado = true";
    public static String getCatEdfPropListByID = "SELECT c FROM CatEdfProp c WHERE c.id = :id";
    public static String getCatEntexEstado = "select e from CatEnte e where e.estado = :estado";
    public static String getUsersEntes = "select e from AclUser e where e.ente is not null";
    public static String getUsersEntesDirectores = "select e from AclUser e where e.ente is not null and e.userIsDirector = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getEntexCodSac = "select e from CatEnte e where e.codUsuario = :codUsuario";
    public static String getCatPredioEdificacionProp = "SELECT v FROM CatPredioEdificacionProp v WHERE v.edificacion = :edif AND v.prop = :prop";
    public static String getCatPredioEdificacionByPredioAndNumEdif = "SELECT v FROM CatPredioEdificacion v WHERE v.predio = :predio AND v.noEdificacion = :noEdif";
    public static String getCatPredioEdificacionByPredio = "SELECT v FROM CatPredioEdificacion v WHERE v.predio.id = :predio";
    /* CONSULTAS PARA VENTANILLAPUBLICA */

 /*CONSULTAS TRAMITES*/
    public static String getHistoricProceduresByProcId = "SELECT ht FROM HistoricoTramites ht Where ht.idProceso = :idprocess";
    public static String getHistoricProceduresByProcIdTemp = "SELECT ht FROM HistoricoTramites ht Where ht.idProcesoTemp = :idprocess";
    public static String getProcedureNumberById = "SELECT d.numTramiteRp FROM RegpLiquidacionDerechosAranceles d WHERE d.historicTramite.id = :tramite";
    public static String getGeTipoTramitesByState = "select e from GeTipoTramite e where e.estado = :estado";
    public static String getGeTipoTramiteById = "select u from GeTipoTramite u where u.tipoProceso.id = :id and u.estado = true order by u.descripcion asc";
    public static String getGeTipoTramitesByActKey_State = "select e from GeTipoTramite e where e.estado = :estado and e.activitykey = :key";
    public static String getGeTipoTramiteByTipoProc = "select e from GeTipoTramite e where e.tipoProceso.id = :tipo";
    public static String getGeTipoTramiteByReqWS = "select e from GeTipoTramite e where e.tipoReqWs = :tipoWS";
    public static String getPeFirmas = "select e from PeFirma e where e.estado = :estado";
    public static String getPeFirmaByID = "select e from PeFirma e where e.id = :id";
    public static String getHistoricoTramitesByEnte = "select e from HistoricoTramites e where e.solicitante = :persona ORDER BY e.id DESC";
    public static String getOtrosTramites = "select e from OtrosTramites e where e.estado = 'A' ORDER BY e.tipoTramite ASC";
    public static String getTipoTramitexAbreviatura = "select e from GeTipoTramite e where e.abreviatura = :abreviatura";

    /*CONSULTAS CATASTRO*/
    public static String getMaxManzana = "SELECT MAX(cp1.mz) FROM CatPredio cp1 WHERE cp1.zona = :zonap and cp1.sector=:sectorp  AND cp1.parroquia =:parroquia AND cp1.provincia =:provincia AND cp1.canton =:canton";
    public static String getMaxSolar = "SELECT MAX(cp1.solar) FROM CatPredio cp1 WHERE cp1.zona = :zonap and cp1.sector=:sectorp AND cp1.mz=:mzp  AND cp1.parroquia =:parroquia AND cp1.provincia =:provincia AND cp1.canton =:canton";
    public static String getMaxUnidad = "SELECT MAX(cp1.unidad) FROM CatPredio cp1 WHERE cp1.zona = :zonap and cp1.sector=:sectorp AND cp1.mz=:mzp  AND cp1.parroquia =:parroquia AND cp1.provincia =:provincia AND cp1.canton =:canton AND cp1.bloque=:bloquep AND cp1.piso=:pisop";

    public static String getPredioByNum = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.numPredio = :numPredio AND cp1.estado='A'";
    public static String getPredioByNumPredio = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.numPredio = :numPredio";
    public static String getPredioByClaveCat = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.claveCat = :claveCat";
    public static String getPredioHijosByFatherID = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.predioRaiz = :numPredio AND cp1.estado='T'";
    public static String getPredioByCod = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.sector=:sectorp AND cp1.mz=:mzp AND cp1.cdla=:cdlap AND cp1.mzdiv=:mzdivp AND cp1.solar=:solarp AND cp1.div1=:div1p AND cp1.div2=:div2p AND cp1.div3=:div3p AND cp1.div4=:div4p AND cp1.div5=:div5p AND cp1.div6=:div6p AND cp1.div7=:div7p AND cp1.div8=:div8p AND cp1.div9=:div9p AND cp1.phh=:phhp AND cp1.phv=:phvp";
    public static String getPredioByCodCat = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.zona = :zonap and cp1.sector=:sectorp AND cp1.mz=:mzp AND cp1.solar=:solarp AND cp1.bloque=:bloquep AND cp1.piso=:pisop AND cp1.unidad=:unidadp AND cp1.provincia =:provincia AND cp1.canton =:canton AND cp1.parroquia =:parroquia";
    public static String getPredioByCodPredByEstado = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.sector=:sectorp AND cp1.mz=:mzp AND cp1.cdla=:cdlap AND cp1.mzdiv=:mzdivp AND cp1.solar=:solarp AND cp1.div1=:div1p AND cp1.div2=:div2p AND cp1.div3=:div3p AND cp1.div4=:div4p AND cp1.div5=:div5p AND cp1.div6=:div6p AND cp1.div7=:div7p AND cp1.div8=:div8p AND cp1.div9=:div9p AND cp1.phh=:phhp AND cp1.phv=:phvp AND cp1.estado = :estado";
    public static String getPredioByCodCatByEstado = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.provincia=:provincia AND cp1.canton=:canton AND cp1.parroquia=:parroquia AND cp1.zona=:zona AND cp1.sector=:sector AND cp1.mz=:mz AND cp1.solar=:solar AND cp1.bloque=:bloque AND cp1.piso=:piso AND cp1.unidad=:unidad AND cp1.estado = :estado";
    public static String getPrediosActivos = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.estado='A'";
    public static String getMaxCatPredio = "SELECT MAX(cp.numPredio) FROM CatPredio cp ";
    public static String getCatPropiedadItemByOrden = "select e from CatPropiedadItem e where e.orden = :orden";
    public static String gerMaxCertificados = "select max(e.numCert+1) from CatCertificadoAvaluo e ";
    public static String getParametrosValPredios = "select e from ParametrosValoracionPredio where e.prefijo = :prefijo and e.estado = true";
    public static String getIdPredioByNumPredio = "SELECT c.id FROM CatPredio c WHERE c.numPredio = :numPredio";
    public static String getPrediosByPropietario = "select p.predio from CatPredioPropietario p where p.ente = :idEnte";
    public static String getPrediosByNumCi = "Select e.predio from CatPredioPropietario e where e.ente.ciRuc = :ciRuc";
    public static String getHistoricoPrediobyNumPredio = "select e from Predio e where e.predio = :predio";

    public static String getValorCiudadela(int periodo) {
        return "select e from CatValoresCiudadela e where e.ciudadela.id = :ciudadela and " + periodo + " between e.anioDesde and e.anioHasta";
    }

    public static String getValorCategoriaConst(int periodo) {
        return "select e from CatPredioEdificacion e inner join e.categoria.catCategoriasConstruccionValoresCollection cc where e.predio.id = :predio and " + periodo + " between cc.anioDesde and cc.anioHasta";
    }

    /*ACL MENU DEL TEMPLATE*/
    public static String getMenuBar = "SELECT mb1 FROM PubGuiMenubar mb1 WHERE mb1.identificador = :ident";
    public static String getMenusOrdenadosPadre = "SELECT m1 FROM PubGuiMenu m1 WHERE m1.menuPadre = :menuPadre ORDER BY m1.numPosicion ASC";
    public static String getMenusOrdenadosBar = "SELECT m1 FROM PubGuiMenu m1 WHERE m1.menubar = :menuBar ORDER BY m1.numPosicion ASC ";

    // Otros tipos de tramites
    public static String getTipoOtrosTramitesByIdentificacion = "SELECT mb1 FROM TipoOtrosTramites mb1 WHERE mb1.identificacion = :ident";
    public static String getBaseCalculoOT = "SELECT e FROM BaseCalculoOtrosTramites e ORDER BY e.id asc";

    // Permiso Contruccion
    public static String getListRequisitosTipoTramitesByTipTra = "SELECT rq FROM GeRequisitosTipoTramite rq WHERE rq.tipoTramite.id = :tipo";
    public static String getCatEdfPropList = "SELECT cp1 FROM CatEdfProp cp1 WHERE cp1.categoria.id=:idCateg ORDER BY cp1.nombre ASC";
    public static String getCatEdfPropListOracle = "SELECT cp1 FROM CatEdfProp cp1 WHERE cp1.categoria.id=:idCateg AND cp1.extras.estado = 'A' ORDER BY cp1.nombre ASC";

    public static String getCatEdfPropListByNom = "SELECT cp1 FROM CatEdfProp cp1 WHERE cp1.categoria.nombre=:nombreCateg ORDER BY cp1.nombre ASC";
    public static String getCatEdfPropListByCategoria = "SELECT cp1 FROM CatEdfProp cp1 WHERE cp1.categoria=:categ ORDER BY cp1.nombre ASC";
    public static String getPeUnidadMedidaList = "Select tp from PeUnidadMedida tp";

    // PeTipoPermiso
    public static String getTipoPermisoDes = "SELECT tp FROM PeTipoPermiso tp WHERE tp.descripcion = :des";
    public static String getPeTipoPermisoCodigo = "SELECT tp1 FROM PeTipoPermiso tp1 where tp1.codigo in ('AR','CN','RN','RM','RP')";
    public static String getPeTipoPermisoCodigoAnt = "SELECT tp1 FROM PeTipoPermiso tp1 where tp1.codigo = :codigo";

    //PePermiso
    public static String getPePermisoByEstado = "Select permiso From PePermiso permiso Where permiso.estado = :estado AND permiso.idPredio != null";
    /**
     * SELECT MAX(p2.numReporte) FROM PePermiso p2 WHERE p2.anioPermiso =
     * :anioPermiso
     */
    public static String getNumerosReportes = "SELECT MAX(p2.numReporte) FROM PePermiso p2 WHERE p2.anioPermiso = :anioPermiso";
    public static String getCountNumerosReportes = "SELECT COUNT(p2.numReporte) FROM PePermiso p2 WHERE p2.anioPermiso = :anioPermiso";
    public static String getPePermisoByNumTra = "SELECT p FROM PePermiso p WHERE p.tramite = :numTramite";
    public static String getPePermisoAntiguoByEstado = "SELECT p.id FROM PePermiso p WHERE p.numReporte = :numReporte AND p.anioPermiso = :anio AND p.estado = :estado";
    public static String getPePermisoById = "SELECT p FROM PePermiso p WHERE p.id = :id";
    public static String getPeInspeccionFinalById = "SELECT p FROM PeInspeccionFinal p WHERE p.id = :id";
    public static String getPeInspeccionByPermisoID = "SELECT p FROM PeInspeccionFinal p WHERE p.numPermisoConstruc = :idPermiso";
    public static String getPeInspeccionList = "SELECT p FROM PeInspeccionFinal p ";
    public static String getPePermisoCabEdificacionByPePermisoID = "SELECT p FROM PePermisoCabEdificacion p WHERE p.idPermiso = :permisoId AND p.estado=TRUE";
    public static String getCatEdificacionesByPredio = "SELECT p FROM CatPredioEdificacion p WHERE p.predio = :predioId AND p.estado = 'A' order by p.noEdificacion asc ";
    public static String getNumeroEdificacionesByPredio = "SELECT COUNT(p) FROM CatPredioEdificacion p WHERE p.predio = :predioId AND p.estado = 'A'";
    public static String getCatEdificacionesByPredioSumAreaConst = "SELECT SUM(p.areaConsCenso) FROM CatPredioEdificacion p WHERE p.predio = :predioId AND p.estado = 'A'";
    public static String getPePermisosAdicionalesByTramiteID = "SELECT p FROM PePermisosAdicionales p WHERE p.numTramite = :tramiteId";
    public static String getValorOrdenanzaNew = "SELECT p FROM CatEdifInspeccionValores p WHERE p.id = :tipoId and :numPisos BETWEEN p.desdeNumPisos AND p.hastaNumPisos AND p.estado = true";

    //REGISTRO PROPIEDAD
    public static String getListRegpActosInscripByNom = "SELECT ac FROM RegpActosIngreso ac WHERE ac.tipoActo.id = :idTipo AND UPPER(ac.nombre) LIKE :nombre ORDER BY ac.nombre ASC";
    public static String getListRegpActosCertifByNom = "SELECT ac FROM RegpActosIngreso ac WHERE ac.tipoActo.id = :idTipo AND UPPER(ac.nombre) LIKE :nombre ORDER BY ac.nombre ASC";
    public static String getVuCatalogoByNombre = "SELECT v FROM VuCatalogo v WHERE v.nombre = :nombre";
    public static String getVuCatalogo = "SELECT v FROM VuCatalogo v ORDER BY v.nombre ASC";
    public static String getNumeroTramiteRegistro = "SELECT MAX(r.numTramiteRp) FROM RegpLiquidacionDerechosAranceles r where to_char(r.fecha, 'yyyy') = :anio";
    public static String getRegEnteIntervinienteByCedRuc = "select e from RegEnteInterviniente e where e.cedRuc=:cadena and e.secuencia=(select max(m.secuencia) from RegEnteInterviniente m where m.cedRuc=:variable)";
    public static String getRegEnteIntervinienteByCedRucByNombreByTipo = "select e from RegEnteInterviniente e where e.cedRuc=:cedula and e.nombre=:nombre and e.tipoInterv=:tipoInterv";
    public static String getCtlgItemaASC = "SELECT i1 FROM CtlgItem i1 WHERE i1.catalogo.nombre=:catalogo ORDER BY i1.orden ASC";

    public static String getCatCatonxCodNac = "SELECT i1 FROM CatCaton i1 WHERE i1.codNac=:codNac";
    public static String getCatProvinciaxCodNac = "SELECT i1 FROM CatProvincia i1 WHERE i1.codNac=:codNac";

    public static String getCtlgItemaAvaluosASC = "SELECT i1 FROM CtlgItem i1 WHERE i1.catalogo.nombre=:catalogo AND referencia is null ORDER BY i1.valor ASC";
    public static String getCtlgItemabyPadreASC = "SELECT i1 FROM CtlgItem i1 WHERE i1.padre=:padre AND referencia =:referencia ORDER BY i1.orden ASC";
    public static String getCtlgItemaDESC = "SELECT i1 FROM CtlgItem i1 WHERE i1.catalogo.nombre=:catalogo ORDER BY i1.valor DESC";
    public static String getCtlgItemByCatalogoValor = "select e from CtlgItem e where e.catalogo.nombre = :catalogo and e.valor = :valor";
    public static String getCtlgItemByCatalogoReferencia = "select e from CtlgItem e where e.catalogo.nombre = :catalogo and e.referencia = :referencia";
    public static String getCtlgItemByCatalogoCodeName = "select e from CtlgItem e where e.catalogo.nombre = :catalogo and e.codename = :codename";
    public static String getCltgCargos = "Select c from CtlgCargo c WHERE c.estado=true";
    public static String getCltgItemIDByReferencia = "Select c from CtlgItem c WHERE c.referencia=:referencia AND c.catalogo.nombre = :nombre";
    public static String CtlgTipoParticipacionOrberByNombre = "SELECT tp FROM CtlgTipoParticipacion tp where tp.estado=true order by tp.nombre";
    public static String getRegCapital = "Select c from RegCapital c where c.estado=true";
    public static String getUserConMenosTareas = "Select u from UserConTareas u where u.estado='A' and u.rolUser=:codigo and u.peso in (select min(t.peso) from UserConTareas t where t.estado='A' and t.rolUser=:codigorol)";
    public static String getUserTareasByUser = "Select u from UserConTareas u where u.username = :user";
    public static String getRenIntervinienteByCedRucByNombreByTipoInt = "select e from RegEnteInterviniente e where e.cedRuc=:cedula and e.nombre=:nomb and e.tipoInterv=:tipointr";
    public static String getRegMovimientoReferenciaByIdMov = "select r from RegMovimientoReferencia r where r.movimiento=:idmov";
    public static String getRegLibroList = "Select lb from RegLibro lb where lb.estado=true ORDER BY lb.nombre ASC";
    public static String getRegActoList = "Select a from RegActo a where a.estado=true ORDER BY a.nombre ASC";
    public static String getMaxNumRepertRegMovimiento = "Select max(m.num_repertorio) FROM  sgm_app.reg_movimiento m where m.is_mercantil = ? and to_char(m.fecha_ingreso,'YYYY')= '?'";
    public static String getRegFichaByCodPredial = "Select f from RegFicha f where f.codigoPredial=:cadena and f.tipo.id=:tipo";
    public static String getRegMovimientosPorLibroAnio = "SELECT m FROM RegMovimiento m WHERE m.estado='AC' and m.libro.id=:libroid and to_char(m.fechaInscripcion, 'yyyy') =:anio ORDER BY m.folioFin";
    public static String getRegMovimientoById = "SELECT m FROM RegMovimiento m WHERE m.id=:movId";
    public static String getCantRegFichaByMovTramiteTaskID = "SELECT COUNT(mf) FROM RegMovimientoFicha mf WHERE mf.movimiento.numTramite = :numTramite and mf.movimiento.taskId = :taskId "
            + "and mf.movimiento.transferenciaDominio = true and mf.movimiento.estado = 'AC'";
    public static String getRegMovsFichaByMovTramiteTaskID = "SELECT mf FROM RegMovimientoFicha mf WHERE mf.movimiento.numTramite = :numTramite "
            + "and mf.movimiento.transferenciaDominio = true and mf.movimiento.estado = 'AC' ORDER BY mf.movimiento.fechaInscripcion";
    public static String getRegFichasByMovimientoId = "Select m.ficha from RegMovimientoFicha m WHERE m.movimiento.id=:idmov ORDER BY m.ficha.fechaApe DESC";
    public static String getRegMovFichasByMovimientoId = "Select m from RegMovimientoFicha m WHERE m.movimiento.id=:idmov";
    public static String getRegMovimientoClienteByMovimiento = "Select mc from RegMovimientoCliente mc where mc.movimiento.id=:movid";
    public static String getRegRegMovimientoRepresentanteByMovimiento = "Select mr from RegMovimientoRepresentante mr where mr.movimiento.id=:movid";
    public static String getRegMovimientoSociosByMovimiento = "Select ms from RegMovimientoSocios ms where ms.movimiento.id=:movid";
    public static String getRegMovimientoCapitalByMovimiento = "Select mc from RegMovimientoCapital mc where mc.movimiento.id=:movid";
    public static String getEnteCorreoByEnteId = "select ec from EnteCorreo ec where ec.ente.id = :enteId";
    public static String getListIdMovsByIdInterv = "select mc.movimiento.id from RegMovimientoCliente as mc where mc.enteInterv.id = :codigo";
    public static String getListIdMovsByCedRucInterv = "select mc.movimiento.id from RegMovimientoCliente as mc where mc.enteInterv.cedRuc = :codigo";
    public static String getParametroDisparadorByTipoTramite = "select pd from ParametrosDisparador pd where pd.tipoTramite.id = :tipoTramite";
    public static String getListaRegpCertificadosInscripcionesByLiquidacionAndTipoTarea = "SELECT C FROM RegpCertificadosInscripciones C WHERE C.liquidacion.id=:liquidacion AND C.tipoTarea=:tipoTarea";
    public static String getRegCatPapelCollectionByActo = "select pa from  sgm_app.reg_cat_papel pa left join  sgm_app.reg_acto_has_papel acpa on(acpa.papel = pa.id) left join  sgm_app.reg_acto ac on(ac.id = acpa.acto) where ac.id = ";
    public static String getListTipoCertificados = "select t from RegTipoCertificado t where t.estado = true";
    public static String getCertificadoByTarea = "select t from RegCertificado t where t.regpCertificadoInscripciones = :codigo";
    public static String getMaxNumCertificadoByTramite = "SELECT max(c.secuencia) FROM RegCertificado c WHERE c.numTramite = :tramite";
    public static String getMaxNumCertificadoSine = "SELECT max(c.secuencia) FROM RegCertificado c WHERE c.numTramite is null and to_char(c.fechaEmision, 'yyyy') = :anio";
    public static String getCtlgCargo = "SELECT c FROM CtlgCargo c ORDER BY c.nombre ASC";
    public static String getPapelByMovimientoInterviniente = "select mc.papel.papel from RegMovimientoCliente as mc where mc.movimiento.id= :mov and mc.enteInterv.id = :inter";
    public static String getPapelByMovYDocInterv = "select mc.papel.papel from RegMovimientoCliente as mc where mc.movimiento.id= :mov and mc.enteInterv.cedRuc = :doc";
    public static String getFechaInscripcionMenor = "SELECT MIN(m.fechaInscripcion) FROM RegMovimiento m";
    public static String getFechaInscripcionMayor = "SELECT MAX(m.fechaInscripcion) FROM RegMovimiento m";
    public static String getCantRegMovimientoByTramite = "select count(mov) from RegMovimiento mov where mov.estado = 'AC' and mov.numTramite = :tramite";
    public static String getRegMovimientoByTramite = "select mov from RegMovimiento mov where mov.estado = 'AC' and mov.numTramite = :tramite";
    public static String getRegMovimientoEspecifico = "SELECT m FROM RegMovimiento m WHERE m.libro.id=:libro AND m.numInscripcion=:inscripcion AND m.numRepertorio=:repertorio AND to_date(to_char(m.fechaInscripcion,'YYYY/MM/DD'),'YYYY/MM/DD')=:fecha AND m.indice=:secuencia";
    public static String getRegMovimientoEspecificoMaximo = "SELECT MAX(m.indice) FROM RegMovimiento m WHERE m.libro.id=:libro AND m.numInscripcion=:inscripcion AND m.numRepertorio=:repertorio AND to_date(to_char(m.fechaInscripcion,'YYYY/MM/DD'),'YYYY/MM/DD')=:fecha";
    public static String getRegEnteJudicialByAbrev = "select e from RegEnteJudiciales e where e.abreviatura ilike :abrev";
    public static String getTodasTareasByLiq = "Select r from RegpCertificadosInscripciones r where r.liquidacion = :idLiq and r.estado = true and r.realizado = true order by r.fechaFin";
    public static String getTareasByLiquidacion = "select c from RegpCertificadosInscripciones c where c.liquidacion = :idLiq and c.tipoTarea = :tipo and c.estado = true";
    public static String getTareasDinardap = "select c from RegpCertificadosInscripciones c where c.tareaDinardap = :idTarea and c.tipoTarea = :tipo and c.estado = true";
    public static String getListRegRegistrador = "select r from RegRegistrador r order by id";
    public static String getRegRegistrador = "select r from RegRegistrador r where r.actual = true";
    public static String getRegMantenimientoMenu = "select r from RegMantenimientoMenu r where r.estado = :estado and r.tipo = :tipo";
    public static String getRegpLiquidacionByFechaYTramite = "select r from RegpLiquidacionDerechosAranceles r where r.numTramiteRp = :numTramite and to_char(r.fecha, 'yyyy') = :fechaLiq";
    public static String getTotalPagarByNumComprebanteSAC = "SELECT TOTAL_PAGAR FROM dbo.RT_LIQUIDACION WHERE NUM_COMPROBANTE = ?";
    public static String getTipoCertificadoByTipoPlantilla = "select r from RegTipoCertificado r where r.tipoPlantilla = :tipo";
    public static String getExisteFichaTareaRegistro = "select c.id from RegpCertificadosInscripciones c where c.liquidacion = :idTarea and c.numFicha = :ficha";
    public static String getExisteFichaTareaDinardap = "select c.id from RegpCertificadosInscripciones c where c.tareaDinardap = :idTarea and c.numFicha = :ficha";
    public static String getHistoricoTramitesByTipoYFecha = "SELECT h FROM HistoricoTramites h WHERE h.tipoTramite.id = :tipo AND TO_CHAR(h.fecha, 'dd/MM/yyyy') = :fecha ORDER BY h.fecha";
    public static String getTramitesAsignadosCertificadosRp = "select ht.id_proceso, ht.nombre_propietario, liq.num_tramite_rp, ht.id, liq.fecha "
            + "from flow.regp_liquidacion_derechos_aranceles as liq left join flow.historico_tramites as ht on (ht.id_tramite = liq.historic_tramite) "
            + "where (liq.estado = 2 or liq.estado = 4) and to_char(liq.fecha, 'dd/MM/yyyy') = ? and liq.certificado = true and liq.inscripcion = false order by liq.fecha";
    public static String getTramitesAsignadosContratosRp = "select ht.id_proceso, ht.nombre_propietario, liq.num_tramite_rp, ht.id, liq.fecha "
            + "from flow.regp_liquidacion_derechos_aranceles as liq left join flow.historico_tramites as ht on (ht.id_tramite = liq.historic_tramite) "
            + "where (liq.estado = 2 or liq.estado = 4) and to_char(liq.fecha, 'dd/MM/yyyy') = ? and liq.inscripcion = true order by liq.fecha";
    public static String getRegPropietariosByFicha = "Select fp from RegFichaPropietarios fp where fp.ficha.id=:idficha and fp.estado = true";
    public static String getCatTransferenciaByTarea = "SELECT td FROM CatTransferenciaDominio td WHERE td.tareaRegistro = :idTarea";
    /*Esta es una consulta nativa y no se envian parametros de hibernate, sino que se concatena a la consulta,
     esto fue probado asi y funciona asi, favor de no molestar.*/
    public static String getListIdFichasByIdInterv = "select distinct mf.ficha from  sgm_app.reg_movimiento_ficha mf inner join  sgm_app.reg_movimiento_cliente mc on(mc.movimiento = mf.movimiento) where mc.ente_interv = ";
    public static String getRegInterviniente = "select e from RegEnteInterviniente e where e.cedRuc = :cedula and e.nombre = :nomb and e.tipoInterv = :tipointr";
    public static String getCantidadCertificadosRealizados = "select count(ce) from RegpCertificadosInscripciones ce where (ce.tipoTarea = 'CE' or ce.tipoTarea = 'DC' or ce.tipoTarea = 'DW') and ce.estado = true and ce.realizado = true and ce.fechaFin between "
            + "to_timestamp(:desde, 'dd-mm-yyyy') and to_timestamp(:hasta, 'dd-mm-yyyy')";
    public static String getCantSolicitantesCertLiq = "select count(distinct(ht.solicitante)) from flow.regp_liquidacion_derechos_aranceles as liq "
            + "left join flow.historico_tramites as ht on (ht.id_tramite = liq.historic_tramite) "
            + "where liq.certificado = true and liq.fecha between to_timestamp(?, 'dd-mm-yyyy') "
            + "and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolicitantesCertSine = "select count(distinct(td.solicitante)) from flow.regp_tareas_dinardap as td "
            + "left join flow.regp_certificados_inscripciones as ce on (td.id = ce.tarea_dinardap) "
            + "where (ce.tipo_tarea = 'DC' or ce.tipo_tarea = 'DW') and td.fecha between to_timestamp(?, 'dd-mm-yyyy') "
            + "and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscResolucion = "select count(distinct(ht.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_liquidacion_derechos_aranceles as lq on (lq.id = ce.liquidacion) "
            + "inner join flow.historico_tramites as ht on (ht.id_tramite = lq.historic_tramite) "
            + "where (m.acto = 1457 or m.acto = 1490) and m.estado = 'AC' "
            + "and lq.fecha between to_timestamp(?, 'dd-mm-yyyy') and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscResolucionSine = "select count(distinct(td.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_tareas_dinardap as td on (td.id = ce.tarea_dinardap) "
            + "where (m.acto = 1457 or m.acto = 1490) and m.estado = 'AC' "
            + "and td.fecha between to_timestamp(?, 'dd-mm-yyyy') and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscGravamen = "select count(distinct(ht.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_liquidacion_derechos_aranceles as lq on (lq.id = ce.liquidacion) "
            + "inner join flow.historico_tramites as ht on (ht.id_tramite = lq.historic_tramite) "
            + "where (m.acto <> 1457 and m.acto <> 1490) and m.escrit_juic_prov_resolucion is not null "
            + "and m.estado = 'AC' and lq.fecha between to_timestamp(?, 'dd-mm-yyyy') "
            + "and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscGravamenSine = "select count(distinct(td.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_tareas_dinardap as td on (td.id = ce.tarea_dinardap) "
            + "where (m.acto <> 1457 and m.acto <> 1490) and m.estado = 'AC' and m.escrit_juic_prov_resolucion is not null "
            + "and td.fecha between to_timestamp(?, 'dd-mm-yyyy') and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscPropiedad = "select count(distinct(ht.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_liquidacion_derechos_aranceles as lq on (lq.id = ce.liquidacion) "
            + "inner join flow.historico_tramites as ht on (ht.id_tramite = lq.historic_tramite) "
            + "where (m.acto <> 1457 and m.acto <> 1490) and m.escrit_juic_prov_resolucion is null "
            + "and m.estado = 'AC' and lq.fecha between to_timestamp(?, 'dd-mm-yyyy') "
            + "and to_timestamp(?, 'dd-mm-yyyy')";
    public static String getCantSolInscPropiedadSine = "select count(distinct(td.solicitante)) from  sgm_app.reg_movimiento as m "
            + "inner join flow.regp_certificados_inscripciones as ce on (ce.id = m.regp_certificados_inscripciones) "
            + "inner join flow.regp_tareas_dinardap as td on (td.id = ce.tarea_dinardap) "
            + "where (m.acto <> 1457 and m.acto <> 1490) and m.estado = 'AC' "
            + "and m.escrit_juic_prov_resolucion is null and td.fecha "
            + "between to_timestamp(?, 'dd-mm-yyyy') and to_timestamp(?, 'dd-mm-yyyy')";
    /*Agrupa los actos ingresados por liquidacion obetern la suma de cantidades y valores*/
    //public static String getActosIngresadosAgrupadosPorLiquidacion = "SELECT acto,codigo_rubro_sac codsac,SUM(cantidad) cantidad,SUM(valor_total) valor FROM flow.regp_liquidacion_detalles d INNER JOIN flow.regp_actos_ingreso a ON(d.acto=a.id) WHERE liquidacion=:liquidacion GROUP BY acto,codigo_rubro_sac";
    public static String getActosIngresadosAgrupadosPorLiquidacion = "SELECT acto, rubro_liquidacion as rubro, codigo_rubro_sac as codsac, SUM(cantidad) as cantidad, SUM(valor_total) as valor "
            + "FROM flow.regp_liquidacion_detalles d INNER JOIN flow.regp_actos_ingreso a ON(d.acto=a.id) "
            + "WHERE liquidacion = :liquidacion GROUP BY acto, codigo_rubro_sac, rubro_liquidacion";
    /**
     * Este grupo de querys se usan para traer liquidaciones de la base
     * postgresql anterior smbcatas y pasarlas a sgm
     */
    public static String getHistoricoTramiteAntiguo = "select per.ci, per.nombres, per.apellidos, ht.id_tramite, ht.fecha, ht.nombre_propietario, ht.correos, ht.telefonos, "
            + "ht.num_liquidacion, us.usuario from flow.historico_tramites ht left join  sgm_app.cat_ente_persona per on(per.ente=ht.solicitante) left join  sgm_app.acl_user us on(us.id=ht.user_creador) where ht.id = ?";
    public static String getLiquidacionBaseAnterior = "select liq.id, liq.tasa_catastro, liq.inf_adicional, liq.num_tramite_rp, liq.parentesco_solicitante, liq.cantidad_tasas_catastro, liq.total_pagar, "
            + "liq.is_exoneracion from flow.regp_liquidacion_derechos_aranceles liq  left join flow.historico_tramites ht on(ht.id_tramite=liq.historic_tramite) "
            + "where ht.id = ?";
    public static String getDetallesLiquidacionAnterior = "select tipo_acto, num_valor, valor_pagar, cuantia, avaluo, observacion from flow.regp_tramitesingresados_has_tipotramite where liquidacion_derec_aranceles = ?";
    public static String getIntervinientesLiquidacion = "select inte.papel, inte.es_beneficiario, ente.is_persona, "
            + "case ente.is_persona when true then per.ci else ente.ci_ruc end documento, "
            + "case ente.is_persona when true then per.nombres else ente.nombre end frist_name, "
            + "case ente.is_persona when true then per.apellidos else '' end second_name "
            + "from flow.regp_intervinientes inte left join  sgm_app.cat_ente ente on(ente.id=inte.ente) "
            + "left  join  sgm_app.cat_ente_persona per on(per.ente=ente.id) where inte.regp_liq_der_aranc_id = ?";

    public static String getHistoricoTramitexEstado = "select ht.id_tramite, (case when ce.is_persona = true then per.ci else ce.ci_ruc end) as ciruc, per.nombres, per.apellidos, ht.num_predio, ht.fecha, ht.nombre_propietario, ht.num_liquidacion, us.usuario, ce.nombre as razon_social, ce.is_persona, "
            + "(case when ce.is_persona = true then(select ec.email from  sgm_app.ente_correo ec where ec.ente = per.ente limit 1) else ce.email_main end) as correo, "
            + "(case when ce.is_persona = true then(select ec.telefono from  sgm_app.ente_telefono ec where ec.ente = per.ente limit 1) else ce.telefono_main end) as telefono, "
            + "ht.estado, ht.tipo_tramite "
            + "from flow.historico_tramites ht "
            + "left join  sgm_app.cat_ente_persona per on(per.ente=ht.solicitante) "
            + "left join  sgm_app.cat_ente ce on(ce.id = ht.solicitante) "
            + "left join  sgm_app.acl_user us on(us.id=ht.user_creador) "
            + "where ht.id = ? and ht.estado in ('inactivo','pendiente')";

    public static String getPeInspeccionFotos = "select foto.imagen_nombre, foto.imagen_file, foto.inspeccion from  sgm_app.pe_inspeccion_fotos as foto";
    public static String getPeInspeccionFotosByInspeccion = "select foto.imagen_nombre, foto.imagen_file, foto.inspeccion from  sgm_app.pe_inspeccion_fotos as foto where foto.inspeccion = ?";
    //public static String getTramitexPredio = "";

    //SECUENCIAS
    public static String getGenSecByPeriodo = "select e from GeneSecuencia e where e.anio = :periodo";
    public static String getMaxRegEnteInterviniente = "select max(e.secuencia) from RegEnteInterviniente e where e.cedRuc = :cedula and e.tipoInterv = :tipointr";
    public static String MaxSecuenciaTipoTramite = "SELECT s FROM SecuenciaTramite s WHERE s.anio = :anio and s.tramiteDepartamento.id = :idTipoTramite";
    public static String MaxSecuenciaTipoLiquidacion = "SELECT s FROM RenSecuenciaNumLiquidicacion s WHERE s.anio = :anio and s.tipoLiquidacion = :idTipoLiquidacion";
    public static String MaxSecuenciaTipoLiquidacionSinAnio = "SELECT s FROM RenSecuenciaNumLiquidicacion s WHERE s.tipoLiquidacion.id = :idTipoLiquidacion";

    //REPORTES
    public static String getReporteByNombreTarea = "select e from HistoricoReporteTramite e where e.nombreTarea = :nombreTarea and e.proceso = :idProceso and e.estado = :estado";
    public static String getReporteByNombreTareaSinEstado = "select e from HistoricoReporteTramite e where e.proceso = :idProceso";
    public static String getHistoricoReporteTramiteByEstadoAndTramiteID = "select e from HistoricoReporteTramite e where e.tramite = :idTramite AND e.estado = TRUE";

    // RegFicha                                 Select f from RegFicha f where f.numFicha = :numFicha and f.tipo.id = :tipo
    public static String getRegFichaNumFacha = "Select f from RegFicha f where f.numFicha = :numFicha and f.tipo.id = :tipo";
    public static String getRegMovimientoFichaByFicha = "SELECT m FROM RegMovimientoFicha m where m.ficha = :idFicha ORDER BY m.movimiento.fechaInscripcion DESC, m.ficha.numRepertorio DESC";
    public static String getMovimientosByIdFicha = "SELECT m.movimiento FROM RegMovimientoFicha m where m.ficha = :idFicha ORDER BY m.movimiento.fechaInscripcion DESC";
    public static String getRegFichaNumPredio = "SELECT reg1 FROM RegFicha reg1 WHERE reg1.predio.id = :numPredio";
    public static String getRegFichaByEscrituraRural = "SELECT reg1 FROM RegFicha reg1 WHERE reg1.catEscrituraRural.id = :rural";
    public static String getRegFichaByPredio = "SELECT reg1 FROM RegFicha reg1 WHERE reg1.predio.id=:predio";
    public static String getRegFichaByLindero = "SELECT rf FROM RegFicha rf WHERE rf.linderos ilike :linderos AND rf.tipo.id=:tipo";
    public static String getMaxNumFichaByTipo = "SELECT max(reg1.numFicha) FROM RegFicha reg1 WHERE reg1.tipo.id=:tipo";
    public static String getRegFichaRangoNumFicha = "Select f from RegFicha f where f.numFicha between :numFichaInicial and :numFichaFinal and f.tipo.id = :tipo ORDER BY f.numFicha ASC";
    // CatParroquia, CatCiudadela, CatCanton
    public static String getCiudadelasByParroquia = "SELECT cp1 FROM CatCiudadela cp1 where cp1.codParroquia = :parroquia ORDER BY cp1.nombre ASC";
    public static String getCiudadelasByTipoConjunto = "SELECT cp1 FROM CatCiudadela cp1 where cp1.codTipoConjunto = :conjunto ORDER BY cp1.nombre ASC";
    public static String getParroquiaByCanton = "SELECT p FROM CatParroquia p WHERE p.idCanton=:idCanton";
    public static String getCatParroquias = "SELECT p FROM CatParroquia p ORDER BY p.descripcion ASC";
    public static String getListNombresCdla = "SELECT cp1.nombre FROM CatCiudadela cp1 ORDER BY cp1.nombre ASC";
    public static String getCatCantonList = "SELECT cp1 FROM CatCanton cp1 ORDER BY cp1.nombre ASC";
    public static String getCatCiudadelaList = "Select e from CatCiudadela e ORDER BY e.nombre ASC";
    public static String getCatCiudadelaById = "SELECT c FROM CatCiudadela c WHERE c.id = :id";
    public static String getCatCiudadelaByCodigo = "SELECT c FROM CatCiudadela c WHERE c.codigo = :codigo";
    public static String getCatCiudadelaByCodigoParroquia = "SELECT c FROM CatCiudadela c INNER JOIN c.codParroquia p INNER JOIN p.idCanton cc WHERE cc.codigoNacional=:codigoNacional ORDER BY c.nombre ASC";
    public static String getCatCiudadelasByCanton = "Select e from CatCiudadela e ORDER BY e.nombre ASC";

    public static String getParroquiasByCanton = "SELECT c FROM CatCanton c INNER JOIN c.idProvincia p WHERE c.codigoNacional = :codigoNacional AND p.codNac=:codNac";
    // CatEscrituraRural
    public static String getCatEscrituraRural = "SELECT reg1 FROM CatEscrituraRural reg1 WHERE reg1.registroCatastral=:regCatastral and reg1.identificacionPredial=:idPredial";

    //CatEscritura
    public static String getCatEscrituraByPredio = "SELECT e FROM CatEscritura e WHERE e.predio.id = :id and e.estado = 'A'";
    public static String getCatEscriturasPendientes = "SELECT cpe FROM CatEscritura cpe WHERE cpe.predio.id = :id AND cpe.estado='P'";

    //CatPredioPropietario
    public static String getCatPropietariosByPredio = "SELECT p FROM CatPredioPropietario p WHERE p.predio.id=:id AND p.estado = 'A'";
    public static String getCatPropietariosByPredioEsAnterior = "SELECT p FROM CatPredioPropietario p WHERE p.predio.id=:id AND p.estado =:estado AND p.extras.esAnterior =:esAnterior";
    public static String getCatPropietariosByEnte = "SELECT p FROM CatPredioPropietario p WHERE p.ente=:enteId AND p.estado = 'A'";

    // RegTipoBien
    public static String getRegTipoBienList = "SELECT tb FROM RegTipoBien tb WHERE tb.estado=:estado";

    //RegLibro
    public static String getRegLibroById = "SELECT l FROM RegLibro l WHERE l.id=:idlibro";

    // Los Movimiento
    public static String getRegMovimientoReferencia = "SELECT r FROM RegMovimientoReferencia r WHERE r.movimiento=:idMov ORDER BY r.secuencia";
    public static String getRegMovimientoReferenciaByMovReffByIdMov = "SELECT mr FROM RegMovimientoReferencia mr WHERE mr.movimientoReff.id = :movReff AND mr.movimiento = :idMov";
    public static String getRegMovimientoBytaskId = "Select m FROM RegMovimiento m where m.taskId=:task";
    public static String getRegCatPapelByActo = "select l from RegActo l where l.id=:idacto";
    public static String getRegMovimientoByRegpCertificadoInscripcion = "Select m FROM RegMovimiento m where m.regpCertificadoInscripcion.id=:idTarea";

    // Actos
    public static String getActobyIdSencillo = "Select r from RegActo r where r.id=:id";
    public static String getActobyAbreviatura = "Select r from RegActo r where r.abreviatura ilike :abrev";

    /**
     * Normas Construccion
     */
    public static String getCatNormasConstruccionTipoByIsEspecial = "SELECT c FROM CatNormasConstruccionTipo c WHERE c.isEspecial = :isEspecial ORDER BY c.id ASC";
    public static String getCatNormasConstruccionByTipoNormaByCiudadela = "SELECT cn FROM CatNormasConstruccion cn WHERE cn.estado='A' AND cn.idCiudadela.id=:ciudadela AND cn.tipoNorma=:tipoNorma";
    public static String getCatNormasConstruccionByTipoNorma = "SELECT cn FROM CatNormasConstruccion cn WHERE cn.estado='A' AND cn.idCiudadela is null AND cn.tipoNorma=:tipoNorma";
    public static String getProcesoReporteByTramite = "SELECT p FROM ProcesoReporte p WHERE p.numTramite = :numTramite";
    public static String getHistoricoTramiteDetByTramite = "SELECT h FROM HistoricoTramiteDet h WHERE h.tramite.idTramite = :numTramite and h.estado=TRUE";
    public static String getHistoricoTramiteByProcessInstance = "SELECT h FROM HistoricoTramites h WHERE h.idProceso = :idP OR h.idProcesoTemp=:idP";

    //FINANCIERO
    public static String getTitulos = "SELECT r FROM FinRubros r WHERE r.titulo=:titulo AND r.prioridad=0 ORDER BY r.descripcion";
    public static String getRubros = "SELECT r FROM FinRubros r WHERE r.titulo=:titulo  AND r.prioridad!=0 ORDER BY r.descripcion";
    public static String getMaxNumeroActa = "SELECT MAX(ac.numActa) FROM RecActasEspecies ac WHERE ac.anio = :valor";

    // HistoricoArchivo
    public static String getHistoricoArchivosList = "SELECT r FROM HistoricoArchivo r WHERE r.tramite=:tramiteId AND r.carpetaContenedora=:carpeta";
    public static String getHistoricoArchivoByArchivoId = "SELECT ha from HistoricoArchivo ha WHERE ha.idArchivo = :idArchivo AND ha.carpetaContenedora = :carpeta and ha.estado=TRUE";
    public static String getRegistroProfesionalByCaTEnte = "SELECT r FROM RegistroProfesionalTecnico r WHERE r.ente=:id";
    public static String getSvSolicitudServicion = "SELECT s FROM SvSolicitudServicios s WHERE s.id IN (SELECT sd.solicitud.id FROM SvSolicitudDepartamento sd WHERE sd.departamento.id = :id)";
    public static String existeHistoricoTramite = "SELECT (1) FROM flow.historico_tramites h WHERE LOWER(h.urbmz) = ? AND LOWER(h.urbsolar) = ? AND CAST(h.urbanizacion AS bigint) = (SELECT c.id FROM  sgm_app.cat_ciudadela c WHERE c.id = ?) AND h.estado = ?";

    //AgendaTrabajo
    public static String getAgendaDetxInvolucrado = "select e from AgendaDet e where e.involucrado.id = :involucrado";
    public static String getAgendaPendiente = "select e from AgendaDet e where e.agenda.finalizado is null";

    public static String estadisticaDepartamento = "SELECT gd.nombre AS departamento, COUNT(HT.ID) AS numerotramites, cast(EXTRACT(YEAR FROM ht.fecha) as Integer) As anio FROM  sgm_app.ge_departamento gd LEFT JOIN  sgm_app.ge_tipo_procesos gp ON (gd.id = gp.departamento) LEFT JOIN  sgm_app.ge_tipo_tramite gt ON(gp.id = gt.tipo_proceso) LEFT JOIN flow.historico_tramites ht ON(ht.tipo_tramite = gt.id) WHERE EXTRACT(YEAR FROM ht.fecha) = :periodo GROUP BY gd.nombre, anio ORDER BY gd.nombre ASC;";
    public static String estadisticaDepartamentoAnio = "SELECT gd.nombre AS departamento, COUNT(HT.ID) AS numerotramites, cast(EXTRACT(YEAR FROM ht.fecha) as Integer) As anio FROM  sgm_app.ge_departamento gd LEFT JOIN  sgm_app.ge_tipo_procesos gp ON (gd.id = gp.departamento) LEFT JOIN  sgm_app.ge_tipo_tramite gt ON(gp.id = gt.tipo_proceso) LEFT JOIN flow.historico_tramites ht ON(ht.tipo_tramite = gt.id) WHERE EXTRACT(YEAR FROM ht.fecha) = :periodo AND gd.id = :dep GROUP BY gd.nombre, anio ORDER BY gd.nombre ASC;";
    public static String estadisticaDepartamentoEntreFecha = "SELECT gd.nombre AS departamento, COUNT(HT.ID) AS numerotramites, cast(EXTRACT(YEAR FROM ht.fecha) as Integer) As anio FROM  sgm_app.ge_departamento gd LEFT JOIN  sgm_app.ge_tipo_procesos gp ON (gd.id = gp.departamento) LEFT JOIN  sgm_app.ge_tipo_tramite gt ON(gp.id = gt.tipo_proceso) LEFT JOIN flow.historico_tramites ht ON(ht.tipo_tramite = gt.id) WHERE ht.fecha BETWEEN :periodo1 and :periodo2 GROUP BY gd.nombre, anio ORDER BY gd.nombre ASC;";
    public static String estadisticaDepartamentoEntreFechaDep = "SELECT gd.nombre AS departamento, COUNT(HT.ID) AS numerotramites, cast(EXTRACT(YEAR FROM ht.fecha) as Integer) As anio FROM  sgm_app.ge_departamento gd LEFT JOIN  sgm_app.ge_tipo_procesos gp ON (gd.id = gp.departamento) LEFT JOIN  sgm_app.ge_tipo_tramite gt ON(gp.id = gt.tipo_proceso) LEFT JOIN flow.historico_tramites ht ON(ht.tipo_tramite = gt.id) WHERE ht.fecha BETWEEN :periodo1 and :periodo2 AND gd.id = :dep GROUP BY gd.nombre, anio ORDER BY gd.nombre ASC;";
    public static String getCatPredioByCdlByMzBySolar = "SELECT c FROM CatPredio c WHERE c.mz = :mz AND c.solar = :solar AND c.ciudadela.id = :cdla AND c.estado = 'A'";
    public static String maxEnteSecuencia = "SELECT max(es.secuencia) FROM EnteSecuencia es";
    public static String CatEnteByRazonSocialList = "SELECT c FROM CatEnte c WHERE UPPER(c.razonSocial) LIKE UPPER(:razonSocial)";

    //Estadística
    public static String getTramitesByTipoAndEstado = "select e from HistoricoTramites e where e.tipoTramite = :tipo and lower(e.estado) =:estado";
    public static String getGeTipoTramiteListByEstado = "select e from GeTipoTramite e where e.estado=true and e.mostrarEstadistica = true";
    public static String getCountTramitesMuertosPorTipoTramite = "select count(*) from act_hi_taskinst as task where task.task_def_key_='revisionDocumentoGeneralRentas' and task.end_time_ IS NULL and lower(task.description_)=";
    public static String getPermisoAdicional = "SELECT p FROM PePermisosAdicionales p WHERE p.numTramite = :numTramite";

    //Correccion Predio
    public static String getFechaSolicitudMenor = "SELECT MIN(s.fechaSolicitud) FROM SolicitudCorreccionPredio s";
    public static String getFechaSolicitudMayor = "SELECT MAX(s.fechaSolicitud) FROM SolicitudCorreccionPredio s";

    /**
     * El query realiza una busqueda con 'LIKE' y los que tengan estado 'A'.
     * Necesita los parametros nombres, esPersona. Los oredna por el nombre.
     */
    public static String CatEnteByNombresList = "SELECT c FROM CatEnte c WHERE UPPER(c.nombres) LIKE UPPER(:nombres) AND c.esPersona = :esPersona AND c.estado = 'A' ORDER BY c.nombres ASC";
    /**
     * El query realiza una busqueda con 'LIKE' y los que tengan estado 'A'.
     * Necesita los parametros apellidos, esPersona. Los ordena por el apellido.
     */
    public static String CatEnteByApellidoslList = "SELECT c FROM CatEnte c WHERE UPPER(c.apellidos) LIKE UPPER(:apellidos) AND c.esPersona = :esPersona AND c.estado = 'A' ORDER BY c.apellidos ASC";

    /**
     * El query realiza una busqueda con 'LIKE' y los que tengan estado 'A', los
     * ordena por el campo razonSocial. Necesita los parametros apellidos,
     * esPersona.
     */
    public static String CatEnteByRazonSocialListAct = "SELECT c FROM CatEnte c WHERE UPPER(c.razonSocial) LIKE UPPER(:razonSocial) AND c.estado = 'A' ORDER BY c.razonSocial ASC";
    public static String getIdEnte = "SELECT c.id FROM CatEnte c WHERE c.ciRuc = :ciRuc";

    public static String getServicioExternoByIdent = "select e from ServicioExterno e where e.identificador = :identificador and e.estado = true";

    //RECAUDACIONES
    public static String getEspeciesActivas = "select es from RecEspecies es where es.tipo = 1 order by es.code";
    public static String getActasEspeciesActivas = "select ac from RecActasEspeciesDet ac where ac.estado = 'A' order by ac.acta.fechaIngreso";
    public static String getActaByEspecieUser = "select es from RecActasEspeciesDet es where es.especie = :idEspecie and es.acta.usuarioAsignado = :idUser and es.disponibles > 0 and es.estado = 'A' order by es.acta.fechaIngreso";
    public static String getDesripcionRubro = "SELECT r.descripcion FROM RenRubrosLiquidacion r WHERE r.id=:idRubro";
    public static String getParametrosInteresMulta = "SELECT p FROM RenParametrosInteresMulta p WHERE p.tipoLiquidacion=:tipoLiquidacion AND p.dia<=:dia AND p.mes<=:mes";

    //COACTIVA
    public static String getAbogadosJuicios = "Select c from CoaAbogado c where c.estado = true order by c.detalle";
    public static String getEstadosJuicios = "Select c from CoaEstadoJuicio c where c.estado = true order by c.secuencia";
    public static String getMimFechaJuicio = "SELECT MIM(cp.fechajuicio) FROM CoaJuicio c ";
    public static String getMimFechaIngreso = "SELECT MIN(cp.fechaIngreso) FROM CoaJuicio c";
    // SE CONSULTA POR ESTADO_COACTIVA = 1 POR MOTIVO DE QUE SOLO SE NECESITAN MOSTRAR EN PANTALLA
    // LAS EMISIONES PREDIALES QUE NO ESTAN EN COACTIVA
    public static String getLiquidacionNoPagadaByPredio = "select r from RenLiquidacion r where r.estadoCoactiva = 1 and r.tipoLiquidacion = :tipo and r.estadoLiquidacion = :estado and r.anio < :anio "
            + "and r.predio = :idPredio order by r.anio asc";
    public static String getLiquidacionesActivasByPredio = "select r from RenLiquidacion r where r.estadoCoactiva = 1 and (r.estadoLiquidacion = 1 or r.estadoLiquidacion = 2) and r.tipoLiquidacion = :tipo and r.anio < :anio "
            + "and r.predio = :idPredio order by r.anio asc";

    public static String getEmisionesEnCoactiva = "select r from RenLiquidacion r where r.tipoLiquidacion = 13 and (r.estadoLiquidacion = 1 or r.estadoLiquidacion = 2) and r.estadoCoactiva = 2 and r.predio = :idPredio and r.anio <= :anio";
    public static String getLiquidacionesCoactivaByJuicio = "select jp.liquidacion from CoaJuicioPredio jp where jp.juicio = :idJuicio";
    public static String getMinAnioImpuestoCoactiva = "select min(anio) from sgm_financiero.ren_liquidacion where tipo_liquidacion = 13 and estado_liquidacion = 2 and estado_coactiva = 2 and predio = ?";

    //EMISIONES PARA JUICIO ANTIGUOS
    public static String getLiquidacionParaJuiciosAntiguos = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=13 AND (e.estadoLiquidacion = 1 or e.estadoLiquidacion = 2) AND e.predio=:predio AND e.anio<:anio AND e NOT IN (SELECT jp.liquidacion FROM CoaJuicioPredio jp WHERE jp.liquidacion.predio=:predio AND jp.estado=TRUE) ORDER BY e.anio ASC";

    //EMISION EXISTENTE DADA DE BAJA SOLO VALIDO PARA SV - OBTIENE LOS RUBROS DEL CEM DE ANIOS ANTERIORES YA QUE NO SE SABE COMO SE LOS SACO :( 
    public static String emisionExistenteBaja = "SELECT det FROM RenDetLiquidacion det  INNER JOIN det.liquidacion l  WHERE l.predio.id=:idPredio AND l.anio=:anio AND l.tipoLiquidacion.id=13 AND l.estadoLiquidacion = 4 AND det.id =:idDetLiquidacion";
    public static String emisionExistenteBajaCEM = "SELECT det FROM RenDetLiquidacion det  INNER JOIN det.liquidacion l  WHERE l.predio.id=:idPredio AND l.anio=:anio AND l.tipoLiquidacion.id=13 AND l.estadoLiquidacion = 4 AND det.id =:idDetLiquidacion";

    //public static String emisionExistenteBaja  = "SELECT det.valor FROM sgm_financiero.ren_det_liquidacion det INNER JOIN sgm_financiero.ren_liquidacion liq ON liq.id = det.liquidacion WHERE liq.tipo_liquidacion = 13 AND liq.estado_liquidacion = 4 AND liq.predio = ? AND liq.anio = ? AND det.id = ?";
    //IMPUESTO PREDIAL
    public static String rangoAvaluo = "SELECT r FROM CatRangoAvaluos r WHERE (:anio BETWEEN r.anioDesde AND r.anioHasta) AND (:avaluoMunicipal BETWEEN r.valorDesde AND r.valorHasta)";
    public static String salario = "SELECT s FROM CtlgSalario s WHERE s.anio=:anio";
    public static String emisionExistente = "SELECT l FROM RenLiquidacion l WHERE l.predio.id=:idPredio AND l.anio=:anio AND l.tipoLiquidacion.id=13";
    public static String getLiquidacionByTramite = "SELECT l FROM RenLiquidacion l WHERE l.tramite = :idTramite and l.tipoLiquidacion = :tipoLiq";
    public static String getLiquidacionNoPagadaByTramite = "SELECT l FROM RenLiquidacion l WHERE l.tramite = :idTramite and l.tipoLiquidacion = :tipoLiq and l.estadoLiquidacion = 2"; // ESTADO 2 NO PAGADO
    public static String getLiquidacionByTramiteActivo = "SELECT l FROM RenLiquidacion l WHERE l.tramite = :idTramite and l.tipoLiquidacion = :tipoLiq and l.estadoLiquidacion = :estadoLiq";
    public static String descuentoFecha = "SELECT d FROM CtlgDescuentoEmision d WHERE d.numMes=:mes AND d.numQuincena=:quincena";
    public static String predios = "SELECT p FROM CatPredio p";
    public static String getPrediosById = "SELECT p FROM CatPredio p WHERE p.id =:predioID";
    public static String numPrediosActivos = "SELECT COUNT(p) FROM CatPredio p WHERE p.estado='A' AND p.esTributario=true";
    public static String numPredios = "SELECT COUNT(p) FROM CatPredio p";
    public static String numPrediosInactivos = "SELECT COUNT(p) FROM CatPredio p WHERE p.estado='I'";
    public static String numPrediosHistorico = "SELECT COUNT(p) FROM CatPredio p WHERE p.estado='H'";
    public static String totalAvaluosMunicipal = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A'";
    public static String totalAreaSolar = "SELECT SUM(s.areaSolar) FROM CatPredio s WHERE s.estado='A'";

    public static String totalAvaluosMunicipalByAll = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A'";

    public static String totalAvaluosMunicipalByParroquia = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS";
    public static String totalAvaluosMunicipalByZona = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS";
    public static String totalAvaluosMunicipalBySector = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS";
    public static String totalAvaluosMunicipalByMz = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS AND p.mz =:mzS";
    public static String totalAvaluosMunicipalBySolar = "SELECT SUM(p.avaluoMunicipal) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS AND p.mz =:mzS AND p.solar =:solarS";

    public static String totalAreaSolarByAll = "SELECT SUM(s.areaSolar) FROM CatPredio s WHERE s.estado='A'";

    public static String totalAreaSolarByParroquia = "SELECT SUM(p.areaSolar) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS";
    public static String totalAreaSolarByZona = "SELECT SUM(p.areaSolar) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS";
    public static String totalAreaSolarBySector = "SELECT SUM(p.areaSolar) FROM CatPredio p WHERE p.estado='A'  AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS";
    public static String totalAreaSolarByMz = "SELECT SUM(p.areaSolar) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS AND p.mz =:mzS";
    public static String totalAreaSolarBySolar = "SELECT SUM(p.areaSolar) FROM CatPredio p WHERE p.estado='A' AND p.parroquia =:parroquiaS AND p.zona =:zonaS AND p.sector =:sectorS AND p.mz =:mzS AND p.solar =:solarS";

    public static String totalEmisionByPredio = "SELECT SUM(e.saldo) FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=13 AND e.estadoLiquidacion.id=2 AND e.predio=:predio AND e.anio<=:anio ";
    public static String emisionByPredio = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=13 AND e.estadoLiquidacion.id=2 AND e.predio=:predio AND e.anio<=:anio ORDER BY e.anio ASC";
    public static String getEmisionesUrbanasEnCoactiva = "Select l from RenLiquidacion l where l.tipoLiquidacion = 13 and l.estadoLiquidacion = 2 and l.estadoCoactiva = 2 and l.predio =: idPredio and l.anio <= :anio";

    // IMPUESTO PREDIAL RURAL
    public static String parroquiasRurales = "SELECT p FROM CatParroquia p WHERE p.id IN (2,3) AND p.estado=true ORDER BY p.descripcion";
    public static String emisionByPredioRural = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=7 AND e.estadoLiquidacion.id=2 AND e.predioRustico=:predioRustico AND e.anio<=:anio ORDER BY e.anio ASC";
    public static String emisionByPredioRural2017 = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=7 AND e.estadoLiquidacion.id=2 AND e.ruralExcel=:ruralExcel AND e.anio<=:anio ORDER BY e.anio ASC";

    //IMPUESTO PREDIAL FOX
    public static String emisionByPredioFox2017 = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=13 AND e.estadoLiquidacion.id=2 AND e.ruralExcel=:ruralExcel AND e.anio<=:anio ORDER BY e.anio ASC";

    public static String emisionByPredioAME = "SELECT e FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=13 AND e.estadoLiquidacion.id=2 AND e.claveAME=:claveAME AND e.anio<=:anio ORDER BY e.anio ASC";

    //EMISIONES
    public static String emisionesByTipoAnio = "SELECT COUNT(e) FROM RenLiquidacion e WHERE e.tipoLiquidacion.id=:tipoLiquidacion AND e.anio=:anio";
    public static String cantidadObrasByAnio = "SELECT COUNT(o) FROM MejObra o WHERE o.anio=:anio";
    public static String cantidadSumasAnuales = "SELECT COUNT(s) FROM CatPredioSumasAnualesUbicacion s WHERE s.anio=:anio";
    public static String rangosAnualesUrb = "SELECT COUNT(*) FROM  sgm_app.cat_rango_avaluos  WHERE ? BETWEEN anio_desde AND anio_hasta";
    public static String rangosAnualesRur = "SELECT COUNT(*) FROM  sgm_app.cat_rango_avaluos_rusticos  WHERE ? BETWEEN anio_desde AND anio_hasta";
    public static String emisionesPrediales = "SELECT l FROM RenLiquidacion l WHERE l.tipoLiquidacion.id=13 AND l.predio=:predio";

    //COACTIVA
    public static String juiciosPorEmisiones = "SELECT DISTINCT c.juicio FROM CoaJuicioPredio c WHERE c.liquidacion IN (SELECT l FROM RenLiquidacion l WHERE l.tipoLiquidacion.id=13 AND l.predio=:predio) AND c.estado=TRUE";
    //PAGOS
    public static String bancos = "SELECT b FROM RenEntidadBancaria b WHERE b.estado = true and b.tipo = 1";
    public static String tarjetas = "SELECT b FROM RenEntidadBancaria b WHERE b.estado = true and b.tipo = 2";

    //FORMATO REPORTES
    public static String getFormatoxCodigo = "select e from FormatoReporte e where e.codigo = :codigo and e.estado = " + SchemasConfig.FILTER_ESTADO + "";

    //CERTIFICADO AVALUOS
    public static String getCertificadoAval = "select e from CatCertificadoAvaluo e where e.codigo = :codigo and e.estado = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getCertificadobyId = "select e from CatCertificadoAvaluo e where e.id = :id and e.estado = " + SchemasConfig.FILTER_ESTADO + "";

    //CAT_EXCEPCIONES
    public static String getCatExcParams = "select e from CatExcepcionesParam e where e.prefijo = :prefijo";

    public static String getFotosPredio = "select e from FotoPredio e where e.codPredio = :predio";

    public static String getFotosIdPredio = "select e from FotoPredio e where e.idPredio = :predio";
    public static String getCantidadFotosIdPredio = "select COUNT(e) from FotoPredio e where e.idPredio = :predio";
    public static String getFotosIdPredioBloque = "select e from FotoPredio e where e.bloque = :bloque";

    public static String getCtlgItemByParent = "select e from CtlgItem e where e.padre = :padre ORDER BY e.orden";

    //MEJORAS
    public static String getAniosObrasRegistradas = "SELECT DISTINCT o.anio FROM MejObra o ORDER BY 1";

    //CENSO CAT
    //ORDENES DE TRABAJO
    public static String getOrdenes(String cond) {
        return "select e from OrdenTrabajo e where " + cond;
    }

    public static String getDetOrdenes(String arg, String valor) {
        return "select e from OrdenDet e where e." + arg + " = " + valor + " and e.estadoDet = 'P' order by e.orden.numOrden asc";
    }
    public static String getDetOrdenUUID = "select e from OrdenDet e where e.orden.numOrden = :numOrden and e.uuid = :uuid";
    public static String getDetOrdenes = "select e from OrdenDet e where e.orden.numOrden = :numOrden";
    public static String getDetOrden = "select e from OrdenDet e where e.orden.numOrden = :numOrden and e.numPredio = :numPredio";
    public static String getPrediosPendientes = "select e from OrdenDet e where e.estadoDet = 'P' and e.datoAct is not null and e.predio is not null";
    public static String getPropietario = "select e from CatPredioPropietario e where e.ente.ciRuc = :identificacion and e.predio.numPredio = :numPredio";

    public static String getCatalogo = "select e from CtlgItem e where e.catalogo.nombre = :nombre and e.referencia = :referencia";

    /*
        *ESCRITURAS
     */
    public static String getEscriturasByPredio = "SELECT e FROM CatEscritura e WHERE e.predio = :predio ORDER BY e.fecCre DESC";

    public static String getDepreciacion(Integer vidautil, Integer panios, Long estado) {

        String e = estado.toString();
        String rango_depreciacion = "SELECT  "
                + " (CASE "
                + "when  " + estado + " = 86 then rd.nuevo "
                + "when  " + estado + " = 43 then rd.bueno "
                + "when  " + estado + " = 46 then rd.regular "
                + "when  " + estado + " = 45 then rd.malo "
                + "when  " + estado + " = 47 then rd.obsoleto else 0 end) as valor  "
                + "FROM sgm_app.rangos_depreciacion rd  "
                + "WHERE rd.anios = "
                + "(CASE WHEN " + panios + " = " + Utils.getAnio(new Date()) + "  THEN 1 "
                + "ELSE " + (Utils.getAnio(new Date()) - panios) + " END) "
                + "AND " + vidautil + " BETWEEN rd.vidautildesde AND rd.vidautilhasta";
        return rango_depreciacion;
    }

    /*FUSIONAR PREDIOS*/
    public static String getLisPrediosByManzana = "SELECT p FROM CatPredio p WHERE p.parroquia = :parroquia AND p.zona = :zona AND p.sector = :sector AND p.mz = :mz ORDER BY p.parroquia, p.zona, p.sector, p.mz, p.solar, p.bloque, p.piso, p.unidad";
    public static String getMaxCodeByManzana = "SELECT MAX(p.solar) FROM CatPredio p WHERE p.parroquia = :parroquia AND p.zona = :zona AND p.sector = :sector AND p.mz = :mz ";
    public static String getDetallesEdificacion = "SELECT c FROM CatPredioEdificacionProp c WHERE c.edificacion.id = :idEdificacion";

    public static String getPredioS4ByPredio = "SELECT c FROM CatPredioS4 c WHERE c.predio.id = :idPredio";
    public static String getPredioS6ByPredio = "SELECT c FROM CatPredioS6 c WHERE c.predio.id = :idPredio";
    public static String getFotosByPredio = "SELECT f FROM FotoPredio f WHERE f.idPredio = :idPredio";

    public static String getEdificacionesPropsByEdificacion = "SELECT c FROM CatPredioEdificacionProp c LEFT OUTER JOIN c.prop d WHERE c.edificacion.id = :idEdificacion";
    public static String getProps = "SELECT c FROM CatEdfProp c LEFT OUTER JOIN c.catPredioEdificacionPropCollection prop WHERE prop.edificacion.id = :idEdificacion AND prop.id = :idProp";
    public static String getCatEdfCategPropByEstado = "SELECT c FROM CatEdfCategProp c WHERE c.extras.estado = :estado ORDER BY c.guiOrden ASC ";
    public static String getCatEdfCategPropByEstadoId = "SELECT c FROM CatEdfCategProp c WHERE c.id = :id AND c.extras.estado = 'A' ORDER BY c.guiOrden ASC";
    public static String getCatEdfPropByEstado = "SELECT c FROM CatEdfProp c WHERE c.extras.estado ='A'";
    public static String getCatEdfPropByEstadoAndCategoria = "SELECT c FROM CatEdfProp c WHERE c.extras.estado = :estado AND c.categoria.id = :idCategoria";
    public static String edificacionesByPredio = "SELECT c FROM CatPredioEdificacion c INNER JOIN c.predio p WHERE p.id = :idPredio AND c.estado = 'A' ORDER BY c.noEdificacion ASC";
    public static String getEdificacionesPropsByEdificacionAndEstado = "SELECT c FROM CatPredioEdificacionProp c LEFT OUTER JOIN c.prop d WHERE c.edificacion.id = :idEdificacion AND c.estado = :estado";

    //GET HISTORICO DIVISION
    public static String getPredioRaizDivision = "SELECT pdf FROM CatPredioFusionDivision pdf WHERE pdf.predioResultante = :predioResultante";

    public static String getIdEscrituraActual = "SELECT max(e.idEscritura)  FROM CatEscritura e WHERE e.predio = :predio";
    public static String getIdEscrituraAnt = "select max(e.idEscritura)"
            + " from CatEscritura e"
            + " where e.predio = :predio"
            + " and e.idEscritura < "
            + " (select max(c.idEscritura) from CatEscritura c where c.predio = :predio1)";

    //PROPIEDAD HORIZONTAL
    public static String getPHsByMatriz = "SELECT p FROM CatPredio p WHERE p.predioRaiz = :predioRaiz AND p.estado = :estado ORDER BY p.parroquia, p.zona, p.sector, p.mz, p.solar, p.bloque, p.piso, p.unidad";
    public static String getEscriturasByPredioDesc = "SELECT e FROM CatEscritura e INNER JOIN e.predio p WHERE p.id = :idPredio ORDER BY e.fecCre DESC";

    public static String getEscriturasByPredioDescId = "SELECT e FROM CatEscritura e INNER JOIN e.predio p WHERE p.id = :idPredio ORDER BY e.idEscritura DESC";
    //GET AVALUOS COEFICIENTES:
    public static String getAvaluosCoeficientesSolar = "SELECT a FROM AvalCoeficientesSuelo a WHERE a.anioInicio =:anioInicio AND a.anioFin =:anioFin  AND a.categoriaSolar.id =:categoriaSolar";
    public static String getAvaluosCoeficientesConstruccion = "SELECT a FROM AvalCoeficientesSuelo a WHERE a.anioInicio =:anioInicio AND a.anioFin =:anioFin  AND a.categoriaConstruccion.id =:categoriaConstruccion";
    public static String getBandaImpositiva = "SELECT a FROM AvalBandaImpositiva a WHERE a.anioInicio =:anioInicio AND a.anioFin =:anioFin";
    public static String getAvalCoeficientesSueloByCategoriaSolar = "SELECT a FROM AvalCoeficientesSuelo a WHERE a.categoriaSolar.id =:categoriaSolar AND a.anioInicio =:anioInicio AND a.anioFin =:anioFin";
    public static String getAvalCoeficientesSueloByCategoriaConstruccion = "SELECT a FROM AvalCoeficientesSuelo a WHERE a.categoriaConstruccion.id =:categoriaConstruccion AND a.anioInicio =:anioInicio AND a.anioFin =:anioFin";

    public static String getEdifCategProp = "SELECT e FROM CatEdfCategProp WHERE isPorcentual = " + SchemasConfig.FILTER_ESTADO + "";
    public static String getPrediosXManzana = "select cp.parroquia, cp.zona, cp.sector, cp.mz, count(solar) cantidadsolar from sgm_app.cat_predio cp where cp.estado = :estado GROUP BY cp.parroquia, cp.zona, cp.sector, cp.mz ORDER BY cp.parroquia, cp.zona, cp.sector, cp.mz asc";
    public static String getZonaPredios = "select cp.parroquia, cp.zona from " + SchemasConfig.APP1 + ".cat_predio cp where cp.estado = :estado GROUP BY cp.parroquia, cp.zona ORDER BY cp.parroquia, cp.zona asc";
    public static String getSectoresPredios = "select cp.parroquia, cp.zona, cp.sector, CAST(count(distinct mz) AS smallint) mz, count(solar) cantidadsolar from " + SchemasConfig.APP1 + ".cat_predio cp where cp.estado = :estado GROUP BY cp.parroquia, cp.zona, cp.sector ORDER BY cp.parroquia, cp.zona asc";
    public static String getEdadesXZona = "SELECT acp FROM AvalEdadZonaConst acp WHERE acp.zona = :zon AND acp.parroquia = :parr";
    public static String getDepreciacion = "SELECT a FROM AvalDepreciacionSolar a WHERE a.anioInicio =:anioInicio AND a.anioFin =:anioFin";

    public static String getCtlgItemaByCultivos = "SELECT i1 FROM CtlgItem i1 WHERE i1.padre=:padre ORDER BY i1.orden ASC";
    public static String getCtlgItemaByCultivosHijos = "SELECT i1 FROM CtlgItem i1 WHERE i1.hijo=:hijo ORDER BY i1.orden ASC";
    public static String getFinanciamientoPredio = "SELECT f FROM FinanPrestamoPredio f WHERE f.predio = :predio";

    public static String getCiudadano(String ciuCedRuc) {
        return "SELECT * FROM " + SchemasConfig.APP1 + ".cat_ciudadano c WHERE c.ciu_ced_ruc = '" + ciuCedRuc + "'";
    }

    public static String getCountAvaluosHistorico = "SELECT COUNT(a) FROM CatPredioAvalHistorico a WHERE a.predio.id =:predioId AND a.anioInicio =:anioInicio AND a.anioFin =:anioFin";

    public static String getAvaluosHistoricoActualizados = "SELECT a FROM CatPredioAvalHistorico a WHERE a.predio.id =:predioId AND a.anioInicio =:anioInicio AND a.anioFin =:anioFin ORDER BY a.fechaActualizacion DESC";
    public static String getAllAvaluosHistoricoActualizados = "SELECT a FROM CatPredioAvalHistorico a WHERE a.anioInicio =:anioInicio AND a.anioFin =:anioFin";
    public static String getAllPrediosNotInListAvaluosHistorico = "SELECT cp.* FROM sgm_app.cat_predio cp WHERE cp.id not in (SELECT hist.predio FROM  sgm_app.cat_predio_aval_historico hist) AND cp.estado ='A'";

    public static String getBandaImpositivaActivas = "SELECT cp FROM AvalBandaImpositiva cp WHERE cp.estado = 'A'";
    public static String getAvalImpuestoPrediosActivo = "SELECT cp FROM AvalImpuestoPredios cp WHERE cp.estado = 'A' ORDER BY cp.parroquia ASC";

    public static String getAvaluosHistoricosPorPredios = "SELECT cpa FROM  CatPredioAvalHistorico cpa WHERE cpa.predio.id =:predio ORDER BY cpa.fechaActualizacion DESC";

    public static String getAvalValorSueloByKey = "SELECT c FROM AvalValorSuelo c WHERE c.parroquia =:parroquia AND c.zona =:zona "
            + "AND c.sector =:sector AND c.mz =:mz AND c.anioInicio =:anioInicio AND c.anioFin =:anioFin AND c.solar =:solar";

    public static String existenPropietarios = "SELECT c FROM CatPredioPropietario c INNER JOIN c.predio p WHERE p.id = :idPredio AND c.estado = :estado";
    public static String existePropietario = "SELECT c FROM CatPredioPropietario c INNER JOIN c.predio p INNER JOIN c.ente e WHERE p.id = :idPredio AND e.id = :idEnte AND c.estado = :estado";

    //CONTEO DE TOTALES DE PREDIOS
    public static String totalPredios = "SELECT count(c) FROM CatPredio c";
    //GET ALL ZONAS - SECTOR - MZ
    public static String getZonas = "SELECT DISTINCT p.zona FROM CatPredio p";
    public static String getSector = "SELECT DISTINCT p.sector FROM CatPredio p";
    public static String getMz = "SELECT DISTINCT p.mz FROM CatPredio p";

    /**
     * GET PREDIOS ID BY ENTES
     *
     */
    public static String getPredioIdByEntes = "SELECT p.predio.id FROM CatPredioPropietario p WHERE p.ente.id =:ente AND p.estado = 'A'";

    public static String getZonasByParroquia = "SELECT DISTINCT p.zona FROM CatPredio p WHERE p.parroquia =:parroquia AND p.estado = 'A'";
    public static String getSectorByZonas = "SELECT DISTINCT p.sector FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.estado = 'A'";
    public static String getMzBySector = "SELECT DISTINCT p.mz FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.sector =:sector AND p.estado = 'A'";
    public static String getLotesByMz = "SELECT  p.solar FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.sector =:sector AND p.mz =:mz AND p.estado = 'A'";

    /**
     * *
     * GET TOTALES DE AVALUOS SEGUN TODO
     */
    public static String getTotalAvaluosZonasByParroquia = "SELECT DISTINCT p.zona FROM CatPredio p WHERE p.parroquia =:parroquia AND p.estado = 'A'";
    public static String getTotalAvaluosSectorByZonas = "SELECT DISTINCT p.sector FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.estado = 'A'";
    public static String getTotalAvaluosMzBySector = "SELECT DISTINCT p.mz FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.sector =:sector AND p.estado = 'A'";
    public static String getTotalAvaluosLotesByMz = "SELECT  p.solar FROM CatPredio p WHERE p.parroquia =:parroquia AND p.zona =:zona AND p.sector =:sector AND p.mz =:mz AND p.estado = 'A'";

    public static String getDptoByName = "SELECT g FROM GeDepartamento g WHERE g.nombre = :nombre";

    //VALOR BASE DE M2 
    /*
    
        QUERY PARA SABER SI EL PREDIO QUE SE ESTA DIVIDIENDO TIENE UN VALO DEE METTRO CUADRADO ASIGNADO 
    
     */
    public static String getValorM2DivisionFusion = "SELECT cp1 FROM AvalValorSuelo cp1 WHERE cp1.zona = :zonap and cp1.sector=:sectorp "
            + "AND cp1.mz=:mzp AND cp1.solar=:solarp AND cp1.parroquia =:parroquia AND cp1.anioInicio =:anioInicio AND cp1.anioFin =:anioFin";

    public static String getAnioInicioMax = "SELECT MAX(a.anioInicio) FROM AvalValorSuelo a";
    public static String getAnioFinMax = "SELECT MAX(a.anioFin) FROM AvalValorSuelo a";

    ///SABER CCUSANTO HAY EN TOTALES DE AVALUO 
    public static String getTotalesPrediosPrivados = "SELECT count(a.id) FROM CatPredio a WHERE a.propiedad.nombre like :nombre";
    public static String getTotalesPrediosPublicos = "SELECT count(a.id) FROM CatPredio a WHERE a.propiedad.nombre like :nombre";
    public static String getTotalesAvaluosTerrenos = "SELECT sum(a.avaluoSolar) FROM CatPredio a WHERE a.estado = 'A'  AND a.esTributario=true";
    public static String getTotalesAvaluosConstruccion = "SELECT sum(a.avaluoConstruccion) FROM CatPredio a WHERE a.estado = 'A' AND a.esTributario=true";
    public static String getTotalesAvaluosPropiedad = "SELECT sum(a.avaluoMunicipal) FROM CatPredio a WHERE a.estado = 'A' AND a.esTributario=true";

    public static String getTotalesAvaluosPropiedadByParroquia = "SELECT sum(a.avaluoMunicipal) FROM CatPredio a WHERE a.estado = 'A' AND a.parroquia =: parro AND a.propiedadHorizontal=false AND a.fichaMadre=true";
    public static String getTotalesAvaluosPropiedadByParroquiaAndZona = "SELECT sum(a.avaluoMunicipal) FROM CatPredio a WHERE a.estado = 'A' AND a.parroquia =:parro AND a.zona =:zon AND a.propiedadHorizontal=false AND a.fichaMadre=true";
    public static String getTotalesAvaluosPropiedadByParroquiaAndZonaAndSector = "SELECT sum(a.avaluoMunicipal) FROM CatPredio a WHERE a.estado = 'A' AND a.parroquia =:parro AND a.zona =:zon AND a.sector =:sec AND a.propiedadHorizontal=false AND a.fichaMadre=true";
    public static String getTotalesAvaluosPropiedadByParroquiaAndZonaAndSectorAndMz = "SELECT sum(a.avaluoMunicipal) FROM CatPredio a WHERE a.estado = 'A' AND a.parroquia =:parro AND a.zona =:zon AND a.sector =:sec AND a.mz =:manzana AND a.propiedadHorizontal=false AND a.fichaMadre=true";

    public static String getJuicioDetallesByLiquidacion = "select jp from CoaJuicioPredio jp where jp.liquidacion=:liquidacion AND jp.estado=TRUE";

    public static String getDataEntes = "SELECT c FROM CedulaJama c WHERE c.estado = 'A'";

    public static String getPredioByPredialAnt = "SELECT cp1 FROM CatPredio cp1 WHERE cp1.predialant = :predialant AND cp1.estado='A'";

    //SOLICITUD DE SERVICIO
    public static String getSolicitudServicioByTramite = "SELECT cp1 FROM SvSolicitudServicios cp1 WHERE cp1.tramite.id = :idTramite";
    public static String getSolicitudServicioPredioBySolicitud = "SELECT cp1 FROM SvSolicitudServiciosPredios cp1 WHERE cp1.servicios.id = :idSolicitud";
    
    public static String getGeDocumentosIdPredio = "select e from GeDocumentos e where e.raiz = :predio AND e.estado = TRUE";
    
    public static String getLocalComercial = "SELECT r FROM RenLiquidacion r "
            + "WHERE r.mes = :mes AND r.anio = :anio "
            + "AND r.localComercial.id = :localComercial";
    

}
