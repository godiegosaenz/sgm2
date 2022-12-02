<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\TesoreriaController;
use App\Http\Controllers\paciente\ListarPacienteController;
use App\Http\Controllers\paciente\PacienteController;
use App\Http\Controllers\paciente\DetallarPacienteController;
use App\Http\Controllers\configuracion\CantonController;
use App\Http\Controllers\configuracion\CategoriaController;
use App\Http\Controllers\citas\CitasController;
use App\Http\Controllers\consultas\ConsultaController;
use App\Http\Controllers\especialista\EspecialistaController;
use App\Http\Controllers\reportes\ExoneracionReporteController;
use App\Http\Controllers\enlinea\ConsultaLineaController;

use GuzzleHttp\Psr7\Request;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('auth.login');
})->middleware('guest');

Route::get('consultar/deudas', [ConsultaLineaController::class, 'index'])->name('deudas.consultar')->middleware('guest');;
Route::post('consultar/deudas', [ConsultaLineaController::class, 'store'])->name('store.consultar')->middleware('guest');;

Route::middleware(['auth', 'verified'])->group(function () {
    Route::view('home', 'home')->name('home');
});



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
Route::get('usuario/listar', [ListarUsuarioController::class, 'index'])->name('listar.usuario');
Route::get('usuario/crear', [CrearUsuarioController::class, 'index'])->name('crear.usuario');
Route::post('usuario/guardar',[CrearUsuarioController::class, 'guardar'])->name('guardar.usuario');
Route::post('usuario/verificar',[CrearUsuarioController::class, 'verificarUsuario'])->name('verificar.usuario');
Route::get('usuario/detallar/{idusuario}/persona/{idpersona}', [DetallarUsuarioController::class, 'index'])->name('detallar.usuario');

Route::get('tesoreria/terceraedad', [TesoreriaController::class, 'create'])->name('index.tesoreria');
Route::get('exoneracion/lista', [TesoreriaController::class, 'index'])->name('lista.exoneracion');
Route::post('exoneracion/datatables', [TesoreriaController::class, 'datatables'])->name('datatable.exoneracion');
Route::post('exoneracion/consulta', [TesoreriaController::class, 'consulta'])->name('consulta.exoneracion');
Route::post('tesoreria/exonerar', [TesoreriaController::class, 'store'])->name('store.tesoreria');
Route::get('exoneracion/detalle/{id}', [TesoreriaController::class, 'show'])->name('detalle.exoneracion');
Route::get('exoneracion/descargar/resolucion/{id}', [TesoreriaController::class, 'download'])->name('descargar.exoneracion');

Route::get('exoneracionreporte/imprimir/{id}', [ExoneracionReporteController::class, 'reporteExoneracion'])->name('imprimir.reporte.exoneracion');
