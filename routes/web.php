<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\ConsultaPredioController;
use App\Http\Controllers\HomeController;
use App\Http\Controllers\TesoreriaController;
use App\Http\Controllers\tesoreria\TituloCreditoCoactivaController;
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

Route::get('/login', function () {
    return view('auth.login');
})->name('login')->middleware('guest');
Route::redirect('/', '/login');
//Route::get('/', [ConsultaPredioController::class, 'index'])->name('welcome');
Route::get('/consulta', [ConsultaPredioController::class, 'index'])->name('welcome');
Route::get('/consultapruebaame', function (){
    return DB::connection('sqlsrv')->select('select * from TITULOS_PREDIO where Pre_CodigoCatastral = "132250510101022000"');
});
Route::get('/reporteprueba', function (){
    return view('layouts.appv2');
    //return view('reportes.reporteDePrueba');
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

    Route::get('paciente/mostrar', [ListarPacienteController::class, 'index'])->name('mostrar.persona');
    Route::post('paciente/listar', [ListarPacienteController::class, 'listar'])->name('listar.persona');
    Route::get('paciente/ingresar', [PacienteController::class, 'index'])->name('ingresar.persona');
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
    Route::get('remision/consulta/liquidacion', [RemisionInteresController::class, 'consultaLiquidacionConRemision'])->name('consulta.liquidacion.remision');
    Route::post('remision/consulta/liquidacion', [RemisionInteresController::class, 'storeConsultaLiquiadacionesConRemision'])->name('store.liquidacion.remision');
    Route::get('remision/descargar/resolucion/{id}', [RemisionInteresController::class, 'download'])->name('descargar.remision');
    Route::post('remision/consulta', [RemisionInteresController::class, 'consulta'])->name('consulta.exoneracion.remision');

    Route::get('liquidacion/imprimir/{id}', [LiquidacionReporteController::class, 'reporteRemision'])->name('imprimir.reporte.liquidacion');

    Route::get('usuario', [UsuarioController::class, 'create'])->name('create.usuario');
    Route::post('usuario', [UsuarioController::class, 'store'])->name('store.usuario');
    Route::get('usuario/{id}/edit', [UsuarioController::class, 'edit'])->name('edit.usuario');
    Route::patch('usuario/{id}/edit', [UsuarioController::class, 'update'])->name('update.usuario');
    Route::post('usuario/datatables', [UsuarioController::class, 'datatables'])->name('datatable.usuario');
    Route::get('usuario/lista', [UsuarioController::class, 'index'])->name('lista.usuario');

    //Mostrar la interfaz para generar los titulos de creditos para coactiva
    Route::get('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'index'])->name('index.titulocredito');
    Route::post('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'consulta'])->name('consulta.titulocredito');
    Route::post('tituloscoactiva/imprimir', [TituloCreditoCoactivaController::class, 'reporteTitulosCoactiva'])->name('reportecoactiva.titulos');
    Route::get('/pruebatitulo', function () {
        try {
            return App\Models\PsqlEnte::all();
        } catch (\Exception $e) {
            return 'No se pudo conectar a la base de datos: ' . $e->getMessage();
        }
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
    Route::get('patente', [PatenteController::class, 'create'])->name('create.patente');

    Route::post('ente/datatables', [EnteController::class, 'datatables'])->name('datatables.ente');
    Route::post('ente/datatables/listar', [EnteController::class, 'datatablesente'])->name('listar.ente');
    Route::post('ente/cedula', [EnteController::class, 'getEnteCedula'])->name('getentecedula.ente');
    Route::post('actividadcomercial/datatables', [ActividadComercialController::class, 'datatables'])->name('datatables.actividad');

    Route::get('ente/listar', [EnteController::class, 'index'])->name('index.ente');
    Route::get('ente', [EnteController::class, 'create'])->name('create.ente');
    Route::get('ente/editar/{id}', [EnteController::class, 'edit'])->name('edit.ente');
    Route::patch('ente/editar/{id}', [EnteController::class, 'update'])->name('update.ente');
    Route::get('ente/mostrar/{id}', [EnteController::class, 'show'])->name('show.ente');


});

