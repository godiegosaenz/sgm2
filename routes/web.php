<?php
 
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\ConsultaPredioController;
use App\Http\Controllers\HomeController;
use App\Http\Controllers\TesoreriaController;
use App\Http\Controllers\tesoreria\TituloCreditoCoactivaController;
use App\Http\Controllers\tesoreria\TituloRuralController;
use App\Http\Controllers\RemisionInteresController;
use App\Http\Controllers\ExoneracionRuralController;
use App\Http\Controllers\paciente\ListarPacienteController;
use App\Http\Controllers\paciente\PacienteController;
use App\Http\Controllers\paciente\DetallarPacienteController;
use App\Http\Controllers\configuracion\CantonController;
use App\Http\Controllers\configuracion\UsuarioController;
use App\Http\Controllers\reportes\ExoneracionReporteController;
use App\Http\Controllers\reportes\LiquidacionReporteController;
use App\Http\Controllers\enlinea\ConsultaLineaController;
use App\Http\Controllers\rentas\CatastroContribuyente;
use App\Http\Controllers\rentas\PatenteController;
use App\Http\Controllers\psql\ente\EnteController;
use App\Http\Controllers\psql\actividad\ActividadComercialController;
use App\Http\Controllers\analitica\AnaliticaContribuyenteController;
use App\Http\Controllers\analitica\AnaliticaPredioController;
use App\Http\Controllers\configuracion\RolesController;
use App\Http\Controllers\transito\TransitoEnteController;
use App\Http\Controllers\transito\TransitoImpuestoController;
use App\Http\Controllers\transito\TransitoVehiculoController;
use App\Http\Controllers\PredioController;
use App\Http\Controllers\coactiva\CoactivaEmisionesController;
use App\Http\Controllers\coactiva\NotificacionesController;
use App\Http\Controllers\AreaController;
use App\Http\Controllers\FirmaElectronicaController;
use App\Http\Controllers\ParteDiarioController;
use App\Http\Controllers\CambiarContraseniaController;
use App\Http\Controllers\PermisosController;
use App\Http\Controllers\TitulosPredial\CobroTituloRuralController;

use App\Models\TransitoEnte;
use App\Models\TransitoTarifaAnual;
use App\Models\TransitoVehiculo;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

//  Route::get('sgm/login', function () {
//     return view('auth.login');
//  })->name('sgm/login')->middleware('guest');
// Route::redirect('/', '/sgm/login');

Route::get('/login', function () {
   return view('auth.login');
})->name('login')->middleware('guest');
Route::redirect('/', '/login');

//Route::get('/', [ConsultaPredioController::class, 'index'])->name('welcome');
Route::get('/consulta', [ConsultaPredioController::class, 'index'])->name('welcome');
Route::get('/consultapruebaame', function (){
    return DB::connection('sqlsrv')->select("SELECT * FROM TITULOS_PREDIO WHERE Pre_CodigoCatastral = '132250510101022000'");
});
Route::get('/consultapruebaame2', function (){
    $codigo = '132250510101022000';
    return DB::connection('sqlsrv')->select("SELECT * FROM TITULOS_PREDIO WHERE Pre_CodigoCatastral = ?", [$codigo]);
});
Route::get('/reporteprueba', function (){
    phpinfo();
});


Route::get('consultar/deudas', [ConsultaLineaController::class, 'index'])->name('deudas.consultar')->middleware('guest');
Route::post('consultar/deudas', [ConsultaLineaController::class, 'store'])->name('store.consultar')->middleware('guest');

Route::middleware(['auth', 'verified'])->group(function () {
    Route::get('home', [HomeController::class, 'index'])->name('home');
    Route::get('/dashboard/data', [HomeController::class, 'getData']);
    Route::get('/dashboard/contribuyentes', [HomeController::class, 'getContribuyentesData']);
    Route::get('/dashboard/datadistribuibles', [HomeController::class, 'obtenerDatosDistribucion']);
    Route::get('/dashboard/discapacidad', [HomeController::class, 'obtenerDatosDiscapacidad']);
    Route::post('/consulta', [ConsultaPredioController::class, 'store'])->name('catpredio.consulta');

    Route::get('persona/mostrar', [ListarPacienteController::class, 'index'])->name('mostrar.persona');
    Route::post('paciente/listar', [ListarPacienteController::class, 'listar'])->name('listar.persona');
    Route::get('persona/ingresar', [PacienteController::class, 'index'])->name('ingresar.persona');
    Route::post('paciente/guardar', [PacienteController::class, 'store'])->name('guardar.persona');
    Route::patch('paciente/actualizar/{id}', [PacienteController::class, 'update'])->name('actualizar.paciente');
    Route::post('paciente/foto', [DetallarPacienteController::class, 'guardarFoto'])->name('foto.persona');
    Route::post('paciente/archivo', [DetallarPacienteController::class, 'guardarArchivo'])->name('guardar.archivo');
    Route::get('paciente/detallar/{id}',[DetallarPacienteController::class, 'index'])->name('detallar.persona');
    Route::get('paciente/editar/{id}',[PacienteController::class, 'edit'])->name('editar.paciente');
    Route::post('paciente/verificar', [PacienteController::class, 'verificarCedula'])->name('verificar.persona');
    Route::post('canton/obtener', [CantonController::class, 'obtener'])->name('canton.obtener');

    Route::get('tesoreria/terceraedad', [TesoreriaController::class, 'create'])->name('index.tesoreria');
    Route::get('exoneracion/lista', [TesoreriaController::class, 'index'])->name('lista.exoneracion');
    Route::post('exoneracion/datatables', [TesoreriaController::class, 'datatables'])->name('datatable.exoneracion');
    Route::post('exoneracion/consulta', [TesoreriaController::class, 'consulta'])->name('consulta.exoneracion');

    Route::post('tesoreria/exonerar', [TesoreriaController::class, 'store'])->name('store.tesoreria');
    Route::get('exoneracion/detalle/{id}', [TesoreriaController::class, 'show'])->name('detalle.exoneracion');
    Route::get('exoneracion/descargar/resolucion/{id}', [TesoreriaController::class, 'download'])->name('descargar.exoneracion');

    Route::get('exoneracionreporte/imprimir/{id}', [ExoneracionReporteController::class, 'reporteExoneracion'])->name('imprimir.reporte.exoneracion');

    Route::get('exoneracion/rural', [ExoneracionRuralController::class, 'create'])->name('rural.exoneracion');
    Route::post('exoneracion/rural/consulta', [ExoneracionRuralController::class, 'consulta'])->name('consulta.rural.exoneracion');

    Route::get('remision', [RemisionInteresController::class, 'create'])->name('create.remision');
    Route::post('remision', [RemisionInteresController::class, 'store'])->name('store.remision');
    Route::patch('remision/{id}', [RemisionInteresController::class, 'update'])->name('update.remision');
    Route::post('remision/datatables', [RemisionInteresController::class, 'datatables'])->name('datatables.remision');
    Route::get('remision/lista', [RemisionInteresController::class, 'index'])->name('index.remision');
    Route::get('remision/detalle/{id}', [RemisionInteresController::class, 'show'])->name('show.remision');
    Route::get('remision/consulta/liquidacion1', [RemisionInteresController::class, 'consultaLiquidacionConRemision'])->name('consulta.liquidacion.remision1');
    Route::post('remision/consulta/liquidacion1', [RemisionInteresController::class, 'storeConsultaLiquiadacionesConRemision'])->name('store.liquidacion.remision1');

    Route::get('remision/consulta/liquidacion', [RemisionInteresController::class, 'consultaLiquidacionConRemision'])->name('consulta.liquidacion.remision');
    Route::post('remision/consulta/liquidacion', [RemisionInteresController::class, 'storeConsultaLiquiadacionesConRemision'])->name('store.liquidacion.remision');
    Route::post('remision/consulta/liquidacion-reporte', [RemisionInteresController::class, 'reporteLiquidacion'])->name('reporteLiquidacion.remision');
    Route::get('remision/descargar/resolucion/{id}', [RemisionInteresController::class, 'download'])->name('descargar.remision');
    Route::post('remision/consulta', [RemisionInteresController::class, 'consulta'])->name('consulta.exoneracion.remision');

    Route::get('liquidacion/imprimir/{id}', [LiquidacionReporteController::class, 'reporteRemision'])->name('imprimir.reporte.liquidacion');

    Route::get('usuario', [UsuarioController::class, 'create'])->name('create.usuario');
    Route::post('usuario', [UsuarioController::class, 'store'])->name('store.usuario');
    Route::get('usuario/{id}/edit', [UsuarioController::class, 'edit'])->name('edit.usuario');
    Route::patch('usuario/{id}/edit', [UsuarioController::class, 'update'])->name('update.usuario');
    Route::post('usuario/datatables', [UsuarioController::class, 'datatables'])->name('datatable.usuario');
    Route::get('usuario/lista', [UsuarioController::class, 'index'])->name('lista.usuario');
    Route::get('usuario/resetear-clave/{id}', [UsuarioController::class, 'resetearPassword'])->name('resetearPassword.usuario');

    //actualizacion de rol a usuarios
    Route::post('rolusuario', [UsuarioController::class, 'rolusuario'])->name('rol.usuario');
    Route::post('permisousuario', [UsuarioController::class, 'Permisousuario'])->name('permisos.usuario');
    //Rutas de roles
    Route::get('roles', [RolesController::class, 'create'])->name('create.roles');
    Route::post('roles', [RolesController::class, 'store'])->name('store.roles');

    //rutas permisos de los roles
    Route::post('permisosroles', [RolesController::class, 'obtenerpermisos'])->name('permisos.roles');

    //Mostrar la interfaz para generar los titulos de creditos para coactiva (Urbano)
    Route::get('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'index'])->name('index.titulocredito');
    Route::post('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'consulta'])->name('consulta.titulocredito');
    Route::post('tituloscoactiva/imprimir', [TituloCreditoCoactivaController::class, 'reporteTitulosCoactiva'])->name('reportecoactiva.titulos');
    Route::get('tituloscoactiva/buscar-contribuyente/{idliquidacion}', [TituloCreditoCoactivaController::class, 'buscaContribuyente'])->name('buscaContribuyente.titulocredito');
    Route::post('tituloscoactiva/actualiza-contribuyente', [TituloCreditoCoactivaController::class, 'actualizaContribuyente'])->name('actualizaContribuyente.titulocredito');
    Route::get('buscarContribuyenteUrbano', [TituloCreditoCoactivaController::class, 'buscarContribuyenteUrbano'])->name('buscarContribuyenteUrbano.titulocredito');
    Route::get('buscarClaveCatastralUrbano', [TituloCreditoCoactivaController::class, 'buscarClaveCatastralUrbano'])->name('buscarClaveCatastralUrbano.titulocredito');

    //titulorural
    Route::get('titulorural/', [TituloRuralController::class, 'index'])->name('index.TitulosRural');
    Route::post('tituloscoactivarural', [TituloRuralController::class, 'consulta'])->name('consulta.TitulosRural');
    Route::get('test-reporte-rural/', [TituloRuralController::class, 'reportetest22'])->name('reportetest22.TitulosRural');
    Route::post('tituloscoactivarural/imprimir', [TituloRuralController::class, 'reportetest'])->name('reportetest.TitulosRural');
    Route::get('buscar-titulo-rural/{tipo}/{valor}', [TituloRuralController::class, 'consultaTitulos'])->name('consultaTitulos.TitulosRural');
    Route::get('descargar-reporte/{pdf}', [TituloRuralController::class, 'descargarPdf'])->name('descargarPdf.TitulosRural');
    Route::get('buscarContribuyenteRural', [TituloRuralController::class, 'buscarContribuyenteRural'])->name('buscarContribuyenteRural.TitulosRural');

    Route::get('/pruebatitulo', function () {
        $vehiculo = TransitoVehiculo::where('id',2)->first();
        return $vehiculo->Tipo;
        //$vehiculo = TransitoVehiculo::find(2);
        return $vehiculo->tipo_vehiculo;
    });

    Route::get('catastrocontribuyente/list', [CatastroContribuyente::class, 'index'])->name('index.catastro');
    Route::get('catastrocontribuyente/ver/{id}', [CatastroContribuyente::class, 'show'])->name('show.catastro');
    Route::post('catastrocontribuyente/obtener', [CatastroContribuyente::class, 'getCatastroContribuyente'])->name('get.catastro');
    Route::post('catastrocontribuyente/datatables', [CatastroContribuyente::class, 'datatable'])->name('datatable.catastro');
    Route::post('catastrocontribuyente/datatables2', [CatastroContribuyente::class, 'datatable2'])->name('datatable2.catastro');
    Route::get('catastrocontribuyente', [CatastroContribuyente::class, 'create'])->name('create.catastro');
    Route::post('catastrocontribuyente', [CatastroContribuyente::class, 'store'])->name('store.catastro');
    Route::post('catastrocontribuyente/canton', [CatastroContribuyente::class, 'getCanton'])->name('getcanton.catastro');
    Route::post('catastrocontribuyente/parroquia', [CatastroContribuyente::class, 'getParroquia'])->name('getparroquia.catastro');
    Route::get('catastrocontribuyente/buscarContribuyente', [CatastroContribuyente::class, 'buscarContribuyente'])->name('buscarContribuyente.catastro');

    Route::post('catastrocontribuyente/agregar-local', [CatastroContribuyente::class, 'guardaLocal'])->name('guardaLocal.catastro');
    Route::get('catastrocontribuyente/listado-locales/{id}', [CatastroContribuyente::class, 'listarLocales'])->name('listarLocales.catastro');
    Route::get('catastrocontribuyente/listado-actividades/{id}', [CatastroContribuyente::class, 'listarActividades'])->name('listarActividades.catastro');
    Route::get('catastrocontribuyente/buscarActividad', [CatastroContribuyente::class, 'buscarActividad'])->name('buscarActividad.catastro');
    Route::post('catastrocontribuyente/agregar-actividad', [CatastroContribuyente::class, 'guardaActividad'])->name('guardaActividad.catastro');
    Route::get('catastrocontribuyente/eliminar-activida-contr/{id}', [CatastroContribuyente::class, 'eliminarActividad'])->name('eliminarActividad.catastro');
    Route::get('catastrocontribuyente/buscarRucContribuyente', [CatastroContribuyente::class, 'buscarRucContribuyente'])->name('buscarRucContribuyente.catastro');
    Route::get('catastrocontribuyente/detalle-contr/{id}', [CatastroContribuyente::class, 'detalleContribuyente'])->name('detalleContribuyente.catastro');
    Route::get('catastrocontribuyente/pdf-contribuyente/{id}', [CatastroContribuyente::class, 'reporteContribuyente'])->name('reporteContribuyente.catastro');



    Route::get('patente', [PatenteController::class, 'create'])->name('create.patente');
    Route::post('patente', [PatenteController::class, 'store'])->name('store.patente');
    Route::get('patente/editar/{id}', [PatenteController::class, 'edit'])->name('edit.patente');
    Route::get('patente/previsualizar/{id}', [PatenteController::class, 'previsualizar'])->name('previsualizar.patente');
    Route::post('patente/editar/{id}', [PatenteController::class, 'update'])->name('update.patente');
    Route::get('patente/lista', [PatenteController::class, 'index'])->name('index.patente');
    Route::post('patente/datatables', [PatenteController::class, 'datatable'])->name('datatables.patente');
    Route::get('patente/calcular-impuesto/{valor}/{tipo}/{anio}/{tEdad}', [PatenteController::class, 'calcular'])->name('calcular.patente');
    Route::get('patente/reporte/{id}', [PatenteController::class, 'crearTitulo'])->name('crearTitulo.patente');
    Route::get('patente/documento/{ruta}', [PatenteController::class, 'verDocumento'])->name('verDocumento.patente');
    Route::get('patente/descargar-documento/{ruta}', [PatenteController::class, 'descargarArchivo'])->name('descargarArchivo.patente');
    Route::get('patente/guarda-liquidacion', [PatenteController::class, 'guardaLiquidacion'])->name('guardaLiquidacion.patente');
    Route::get('patente/declaracion-cobro', [PatenteController::class, 'pdfDeclaracionCobro'])->name('pdfDeclaracionCobro.patente');
    Route::get('patente/busca-data-contribuyente/{id}', [PatenteController::class, 'buscaInfoContribuyente'])->name('buscaInfoContribuyente.patente');
    Route::get('patente/ver-local/{id}', [PatenteController::class, 'verLocal'])->name('verLocal.patente');
    Route::get('patente/test-reporte/{id}', [PatenteController::class, 'testReporte'])->name('testReporte.patente');
    Route::get('patente/detalle-titulo-patente/{id}', [PatenteController::class, 'detalle'])->name('detalle.patente');
    Route::get('patente/registrar-cobro-patente/{id}', [PatenteController::class, 'realizarCobro'])->name('realizarCobro.patente');
    Route::get('patente/anular-cobro-patente/{id}', [PatenteController::class, 'anularCobro'])->name('anularCobro.patente');

    Route::post('patente-new-contribuyente', [PatenteController::class, 'guardaContribuyente'])->name('guardaContribuyente.patente');
    Route::post('catastrocontribuyente/actualiza-contribuyente', [PatenteController::class, 'actualizaContribuyente'])->name('actualizaContribuyente.patente');

    Route::get('patente/llenar-tabla-rango', [PatenteController::class, 'tablaRango'])->name('tablaRango.patente');
    Route::post('patente/guardar-rango', [PatenteController::class, 'guardarRango'])->name('guardarRango.patente');
    Route::put('patente/actualizar-rango/{id}', [PatenteController::class, 'actualizarRango'])->name('actualizarRango.patente');
    Route::post('patente/baja-titulo-patente', [PatenteController::class, 'bajaTituloPatente'])->name('bajaTituloPatente.patente');

    Route::get('patente/reportes', [PatenteController::class, 'vistaReportePatente'])->name('vistaReportePatente.patente');
    Route::post('patente/pago-consulta', [PatenteController::class, 'consultarPagos'])->name('consultarPagos.patente');
    Route::post('patente/reporte-diario', [PatenteController::class, 'ReporteTransito'])->name('ReporteTransito.patente');
    Route::post('patente/agregar-parroquia', [PatenteController::class, 'guardaParroquia'])->name('guardaParroquia.patente');
    // Route::get('transito/test-reportes', [AnaliticaContribuyenteController::class, 'testReporteTransito'])->name('testReporteTransito.transito');


    Route::post('ente/datatables', [EnteController::class, 'datatables'])->name('datatables.ente');
    Route::post('ente/datatables/listar', [EnteController::class, 'datatablesente'])->name('listar.ente');
    Route::post('ente/cedula', [EnteController::class, 'getEnteCedula'])->name('getentecedula.ente');
    Route::post('actividadcomercial/datatables', [ActividadComercialController::class, 'datatables'])->name('datatables.actividad');

    Route::get('ente/listar', [EnteController::class, 'index'])->name('index.ente');
    Route::get('ente', [EnteController::class, 'create'])->name('create.ente');
    Route::get('ente/editar/{id}', [EnteController::class, 'edit'])->name('edit.ente');
    Route::patch('ente/editar/{id}', [EnteController::class, 'update'])->name('update.ente');
    Route::get('ente/mostrar/{id}', [EnteController::class, 'show'])->name('show.ente');

    Route::post('ente', [EnteController::class, 'store'])->name('store.ente.sgmapp');

    //RUTAS PARA MODULO DE TRANSITO
    Route::get('transito', [TransitoImpuestoController::class, 'create'])->name('create.transito');
    Route::post('transito', [TransitoImpuestoController::class, 'store'])->name('store.transito');
    Route::get('transito/previsualizar/{id}', [TransitoImpuestoController::class, 'show'])->name('show.transito');
    Route::post('transito/imprimir/{id}', [TransitoImpuestoController::class, 'reportetituloimpuesto'])->name('reportetituloimpuesto.transito');
    Route::post('transito/datatables', [TransitoImpuestoController::class, 'datatable'])->name('datatables.impuesto');
    Route::get('transito/lista', [TransitoImpuestoController::class, 'index'])->name('index.transito');
    Route::post('calcular', [TransitoImpuestoController::class, 'calcular'])->name('calcular.transito');
    Route::post('transitoente', [TransitoEnteController::class, 'store'])->name('store.ente');
    Route::post('transitoente/cedula', [TransitoEnteController::class, 'getEnteCedula'])->name('get.cedula.transitoente');
    Route::post('transitovehiculo/placa', [TransitoVehiculoController::class, 'getVehiculoPlaca'])->name('get.placa.transitovehiculo');

    Route::post('vehiculos', [TransitoVehiculoController::class, 'store'])->name('store.vehiculo');
    Route::get('llenar-tabla-rango', [TransitoImpuestoController::class, 'tablaRango'])->name('tablaRango.transito');
    Route::post('guardar-rango', [TransitoImpuestoController::class, 'guardarRango'])->name('guardarRango.transito');
    Route::put('actualizar-rango/{id}', [TransitoImpuestoController::class, 'actualizarRango'])->name('guardarRango.transito');

    Route::get('llenar-tabla-marca', [TransitoImpuestoController::class, 'tablaMarca'])->name('tablaMarca.transito');
    Route::post('guardar-marca', [TransitoImpuestoController::class, 'guardarMarca'])->name('guardarMarca.transito');
    Route::put('actualizar-marca/{id}', [TransitoImpuestoController::class, 'actualizarMarca'])->name('actualizarMarca.transito');
    Route::get('eliminar-marca/{id}', [TransitoImpuestoController::class, 'eliminaMarca'])->name('eliminaMarca.transito');

    Route::get('llenar-tabla-tipo', [TransitoImpuestoController::class, 'tablaTipo'])->name('tablaTipo.transito');
    Route::post('guardar-tipo', [TransitoImpuestoController::class, 'guardarTipo'])->name('guardarTipo.transito');
    Route::put('actualizar-tipo/{id}', [TransitoImpuestoController::class, 'actualizarTipo'])->name('actualizarTipo.transito');
    Route::get('eliminar-tipo/{id}', [TransitoImpuestoController::class, 'eliminaTipo'])->name('eliminaTipo.transito');

    Route::get('llenar-tabla-clase-tipo', [TransitoImpuestoController::class, 'tablaClaseTipo'])->name('tablaClaseTipo.transito');
    Route::post('guardar-clase-tipo', [TransitoImpuestoController::class, 'guardarClaseTipo'])->name('guardarClaseTipo.transito');
    Route::put('actualizar-clase-tipo/{id}', [TransitoImpuestoController::class, 'actualizarClaseTipo'])->name('actualizarClaseTipo.transito');
    Route::get('eliminar-clase-tipo/{id}', [TransitoImpuestoController::class, 'eliminaClaseTipo'])->name('eliminaClaseTipo.transito');

    Route::get('llenar-tabla-concepto', [TransitoImpuestoController::class, 'tablaConcepto'])->name('tablaConcepto.transito');
    Route::post('guardar-concepto', [TransitoImpuestoController::class, 'guardarConcepto'])->name('guardarConcepto.transito');
    Route::put('actualizar-concepto/{id}', [TransitoImpuestoController::class, 'actualizarConcepto'])->name('actualizarConcepto.transito');
    // Route::get('eliminar-tipo/{id}', [TransitoImpuestoController::class, 'eliminaTipo'])->name('eliminaTipo.transito');

    Route::get('carga-info-persona/{ci}', [TransitoImpuestoController::class, 'infoPersona'])->name('infoPersona.transito');
    Route::get('carga-info-vehiculo/{ci}', [TransitoImpuestoController::class, 'infoVehiculo']);
    Route::get('transito-imprimir/{id}/{tipo}', [TransitoImpuestoController::class, 'pdfTransito'])->name('pdfTransito.transito');
    Route::post('baja-titulo-transito', [TransitoImpuestoController::class, 'bajaTituloTransito'])->name('bajaTituloTransito.transito');
    Route::get('detalle-titulo/{id}', [TransitoImpuestoController::class, 'detalleTitulo'])->name('detalleTitulo.transito');
    Route::get('registrar-cobro-transito/{id}', [TransitoImpuestoController::class, 'realizarCobro'])->name('realizarCobro.transito');
    Route::get('anular-cobro-transito/{id}', [TransitoImpuestoController::class, 'anularCobro'])->name('anularCobro.transito');

    Route::get('carga-combo-marca', [TransitoImpuestoController::class, 'comboMarca'])->name('comboMarca.transito');
    Route::get('carga-combo-tipo-vehiculo', [TransitoImpuestoController::class, 'comboTipoVehiculo'])->name('comboTipoVehiculo.transito');
    Route::get('carga-combo-clase-tipo-vehiculo', [TransitoImpuestoController::class, 'comboClaseTipoVehiculo'])->name('comboClaseTipoVehiculo.transito');
    Route::get('busca-tipo-vehiculo/{idclase}', [TransitoImpuestoController::class, 'buscaTipoVehiculo'])->name('buscaTipoVehiculo.transito');

    Route::get('firma-p12/{documento}/{pdoce}/{clave}/{prefijo}/{disco}', [TransitoImpuestoController::class, 'firmarDocumento2'])->name('firmarDocumento2.transito');



    Route::get('analitica/contribuyente', [AnaliticaContribuyenteController::class, 'index'])->name('analitica.contribuyente');
    Route::get('analitica/predios', [AnaliticaContribuyenteController::class, 'predios'])->name('analitica.predios');
    Route::post('analitica/carga-data', [AnaliticaContribuyenteController::class, 'cargaData'])->name('analitica.cargaData');
    Route::post('analitica/reporte-predio-rango', [AnaliticaContribuyenteController::class, 'pdfData'])->name('analitica.pdfData');
    Route::get('analitica/descargar-reporte/{pdf}', [AnaliticaContribuyenteController::class, 'descargarPdf'])->name('analitica.descargarPdf');

    Route::get('transito/reportes', [AnaliticaContribuyenteController::class, 'vistaReporteTransito'])->name('vistaReporteTransito.transito');
    Route::post('transito/pago-consulta-transito', [AnaliticaContribuyenteController::class, 'consultarPagos'])->name('consultarPagos.transito');
    Route::post('transito/reporte-diario', [AnaliticaContribuyenteController::class, 'ReporteTransito'])->name('ReporteTransito.transito');
    Route::get('transito/test-reportes', [AnaliticaContribuyenteController::class, 'testReporteTransito'])->name('testReporteTransito.transito');

    //predios exonerados
    Route::get('analitica/predios-exonerados', [AnaliticaPredioController::class, 'predios'])->name('analiticaExonerados.predios');
    Route::post('analitica/data-predio-exonerado', [AnaliticaPredioController::class, 'cargaData'])->name('analiticaExonerados.cargaData');
    Route::post('analitica/reporte-predio-exonerado', [AnaliticaPredioController::class, 'pdfData'])->name('analiticaExonerados.pdfData');


    //no deudor
    Route::get('nodeudor', [PredioController::class, 'index'])->name('index.nodeudor');
    Route::get('buscar-deudas/{cedula}', [PredioController::class, 'buscarDeudas'])->name('buscarDeudas.nodeudor');
    Route::get('buscar-detalle-deudas/{cedula}/{tipo}', [PredioController::class, 'buscarDetalleDeudas'])->name('buscarDetalleDeudas.nodeudor');
    Route::get('generar-nd/{cedula}/{tipo}', [PredioController::class, 'generarNoDeudor'])->name('generarNoDeudor.nodeudor');
    Route::post('nodeudor/guarda-contribuyente', [PredioController::class, 'guardaContribuyente'])->name('guardaContribuyente.nodeudor');
    Route::get('nodeudor/documento/{ruta}', [PredioController::class, 'verDocumento'])->name('verDocumento.nodeudor');


    //emisiones repetidas
    Route::get('emisiones-repetidas', [PredioController::class, 'vistaRepetidos'])->name('vistaRepetidos.emisiones');
    Route::get('buscar-repetidas/{estado}', [PredioController::class, 'buscarRepetidos'])->name('buscarRepetidos.emisiones');
    Route::get('quitar-repetidos/{estado}', [PredioController::class, 'quitarDuplicados'])->name('quitarDuplicados.emisiones');
    Route::get('descargar-txt/{txt}', [PredioController::class, 'descargarTxt'])->name('descargarTxt.emisiones');

    //notifciar coactiva emisiones
    Route::get('notificar-coactiva', [NotificacionesController::class, 'index'])->name('index.notificacion');
    Route::post('guardar-notificacion', [NotificacionesController::class, 'notificar'])->name('notificar.coativa');



    //coactivar emisiones
    Route::get('coactivar-emisiones', [CoactivaEmisionesController::class, 'vistaCoactivar'])->name('vistaCoactivar.coativa');
    Route::get('buscar-titulo-urbano/{tipo}/{valor}', [CoactivaEmisionesController::class, 'consultaTitulosUrbanos'])->name('consultaTitulosUrbanos.coativa');
    Route::get('buscar-notificados/{tipo}/{valor}', [CoactivaEmisionesController::class, 'buscarNotificados'])->name('consultaTitulosUrbanos.coativa');
    Route::get('ver-detalle-deudas/{id}', [CoactivaEmisionesController::class, 'detalleDeudas'])->name('detalleDeudas.coativa');
    Route::post('guardar-coactiva', [CoactivaEmisionesController::class, 'coactivar'])->name('coactivar.coativa');
    Route::post('guardar-coactiva-emi', [CoactivaEmisionesController::class, 'coactivarEmisiones'])->name('coactivarEmisiones.coativa');


    //jefe area
    Route::get('jefe-area', [AreaController::class, 'index'])->name('index.area');
    Route::post('jefe-area/datatables', [AreaController::class, 'datatable'])->name('datatables.area');
    Route::post('mantenimiento-jefe-area', [AreaController::class, 'mantenimiento'])->name('mantenimiento.area');
    Route::get('editar-jefe-area/{id}', [AreaController::class, 'editar'])->name('editar.area');
    Route::get('eliminar-jefe-area/{id}', [AreaController::class, 'eliminar'])->name('eliminar.area');

    //firma electronica
    Route::get('mi-firma-electronica', [FirmaElectronicaController::class, 'index'])->name('index.firma');
    Route::post('mi-firma-electronica/datatables', [FirmaElectronicaController::class, 'datatable'])->name('datatables.firma');
    Route::post('mantenimiento-firma', [FirmaElectronicaController::class, 'mantenimiento'])->name('mantenimiento.firma');
    Route::get('eliminar-firma/{id}', [FirmaElectronicaController::class, 'eliminar'])->name('eliminar.firma');

    // Route::get('firma-qr/{nombre}/{img}', [FirmaElectronicaController::class, 'generar_firma_qr']);
    Route::get('firma-qr/{nombre}/{img}', [TransitoImpuestoController::class, 'generar_firma_qr']);

    //contrasenia
    Route::get('cambiar-contrasenia', [CambiarContraseniaController::class, 'index'])->name('index.contrasenia');
    Route::post('guardar-contrasenia', [CambiarContraseniaController::class, 'cambiar'])->name('cambiar.contrasenia');


    //parte-diario
    Route::get('parte-diario', [ParteDiarioController::class, 'index'])->name('index.parte_diario');
    Route::get('generar-parte-diario/{fecha}', [ParteDiarioController::class, 'consultar'])->name('consultar.parte_diario');
    Route::get('descargar-parte/{pdf}', [ParteDiarioController::class, 'descargarPdf'])->name('descargarPdf.parte_diario');


    //permisos
    Route::get('permisos', [PermisosController::class, 'index'])->name('index.permisos');
    Route::get('listado-rol', [PermisosController::class, 'listar'])->name('listar.permisos');
    Route::get('/ver-permisos-roles/{id}', [PermisosController::class, 'verPermisos']);
    Route::get('/eliminar-permiso/{idpermiso}/{idrol}', [PermisosController::class, 'eliminarPermiso']);
    Route::post('/guardar-permiso', [PermisosController::class, 'guardarPermiso']);


    //cobro rural
    Route::get('cobro-titulo-rural', [CobroTituloRuralController::class, 'index'])->name('index.cobroTituloRural');

});

Route::get('/clear', function() {

    Artisan::call('cache:clear');
    Artisan::call('config:clear');
    Artisan::call('config:cache');
    Artisan::call('view:clear');

    return "Cleared!";

 });
