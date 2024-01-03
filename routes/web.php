<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\ConsultaPredioController;
use App\Http\Controllers\TesoreriaController;
use App\Http\Controllers\ExoneracionRuralController;
use App\Http\Controllers\paciente\ListarPacienteController;
use App\Http\Controllers\paciente\PacienteController;
use App\Http\Controllers\paciente\DetallarPacienteController;
use App\Http\Controllers\configuracion\CantonController;
use App\Http\Controllers\reportes\ExoneracionReporteController;
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
Route::get('/', [ConsultaPredioController::class, 'index'])->name('welcome');

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
    //Route::post('canton/obtener', [CantonController::class, 'obtener'])->name('canton.obtener');

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

});

