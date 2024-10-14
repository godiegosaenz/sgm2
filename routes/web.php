<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\ConsultaPredioController;
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
use App\Http\Controllers\reportes\TitulosCoactivaController;
use App\Http\Controllers\enlinea\ConsultaLineaController;

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
    return view('reportes.reporteDePrueba');
});


Route::get('consultar/deudas', [ConsultaLineaController::class, 'index'])->name('deudas.consultar')->middleware('guest');
Route::post('consultar/deudas', [ConsultaLineaController::class, 'store'])->name('store.consultar')->middleware('guest');

Route::middleware(['auth', 'verified'])->group(function () {
    Route::view('home', 'home')->name('home');

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
    Route::post('usuario/datatables', [UsuarioController::class, 'datatables'])->name('datatable.usuario');
    Route::get('usuario/lista', [UsuarioController::class, 'index'])->name('lista.usuario');

    //Mostrar la interfaz para generar los titulos de creditos para coactiva
    Route::get('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'index'])->name('index.titulocredito');
    Route::post('tituloscoactiva/', [TituloCreditoCoactivaController::class, 'consulta'])->name('consulta.titulocredito');
    Route::post('tituloscoactiva/imprimir', [TituloCreditoCoactivaController::class, 'reporteTitulosCoactiva'])->name('reportecoactiva.titulos');
    Route::get('/pruebatitulo', function () {
        $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')->where('num_predio', '=', 545)->first();

        return $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
                                        ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                        ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
                                        ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos')
                                        ->where('predio','=',$predio_id->id)
                                        ->whereNot(function($query){
                                            $query->where('estado_liquidacion', 4)
                                            ->orWhere('estado_liquidacion', '=', 5);
                                        })
                                        ->orderBy('anio', 'desc')
                                        ->get();
    });
});

